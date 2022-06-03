package investwell.common.applock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.iw.acceleratordemo.R;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.common.applock.pinlock.PFFLockScreenConfiguration;
import investwell.common.applock.pinlock.PFLockScreenFragment;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomDialog;

import static investwell.common.applock.AppLockOptionActivity.CUSTOM_PIN_LOCK_CODE;
import static investwell.common.applock.AppLockOptionActivity.DEFAULT_LOCK_CODE;

public class PinLockActivity extends BaseActivity {
    private AppSession mSession;
    private String mType = "";

    @Override
    public void onBackPressed() {
        if (mType.equalsIgnoreCase("verify_lock")) {
            this.finishAffinity();
           // System.exit(0);
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_lock_activity);
        mSession = AppSession.getInstance(this);
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("type")) {
            mType = intent.getStringExtra("type");
        }
        showLockScreenFragment();
    }


    private PFLockScreenFragment.OnPFLockScreenCodeCreateListener mCodeCreateListener =
            new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
                @Override
                public void onCodeCreated(String encodedCode) {
                    mSession.setPIN(encodedCode);
                    redirectionMethod();

                }
            };

    private PFLockScreenFragment.OnPFLockScreenLoginListener mLoginListener =
            new PFLockScreenFragment.OnPFLockScreenLoginListener() {

                @Override
                public void onCodeInputSuccessful() {
                    redirectionMethod();
                }

                @Override
                public void onFingerprintSuccessful() {
                    redirectionMethod();
                }

                @Override
                public void onPinLoginFailed() {
                    // Toast.makeText(PinLockActivity.this, "Pin failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFingerprintLoginFailed() {
                    // Toast.makeText(PinLockActivity.this, "Fingerprint failed", Toast.LENGTH_SHORT).show();
                }
            };

    private void redirectionMethod(){
        if (mType.equalsIgnoreCase("verifyFromSetting")) {
            Intent intent = new Intent();
            setResult(DEFAULT_LOCK_CODE, intent);
            finish();
        } else if (mType.equalsIgnoreCase("change_pin")) {
            mSession.setHasAppLockEnable(true);
            Intent intent = new Intent();
            setResult(108, intent);
            finish();
        } else if (mType.equalsIgnoreCase("set_screen_lock")) {
            mSession.setHasAppLockEnable(true);

             if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                    || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                    || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(AppConstants.COME_FROM,mType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), BrokerActivity.class);
                intent.putExtra(AppConstants.COME_FROM,mType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);                        startActivity(intent);
                startActivity(intent);
                finish();
            }
        } else if (mType.equalsIgnoreCase("verify_lock")) {
            Intent intent = new Intent();
            setResult(CUSTOM_PIN_LOCK_CODE, intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void showLockScreenFragment() {
        //final boolean isPinExist = PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist();
        boolean isPinExist = false;
        if (mType.equalsIgnoreCase("change_pin")) {
            isPinExist = false;
        }else if (mType.equalsIgnoreCase("verifyFromSetting")) {
            isPinExist = true;
        }else if (mSession.getPIN().length()>0) {
            isPinExist = true;
        }else{
            isPinExist = false;
        }
        final PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration
                .Builder(this)
                .setTitle(isPinExist ? "Enter your 4-digit Pincode" : "Please set your 4-digit Pincode for quick access")
                .setCodeLength(4)
                .setLeftButton(getString(R.string.pin_pattern_forgot_txt), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCustomDailog();
                    }
                })
                .setUseFingerprint(true);
        PFLockScreenFragment fragment = new PFLockScreenFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", mType);
        fragment.setArguments(bundle);


        builder.setMode(isPinExist
                ? PFFLockScreenConfiguration.MODE_AUTH
                : PFFLockScreenConfiguration.MODE_CREATE);
        if (isPinExist) {
            fragment.setEncodedPinCode(mSession.getPIN());
            fragment.setLoginListener(mLoginListener);
        }

        fragment.setConfiguration(builder.build());
        fragment.setCodeCreateListener(mCodeCreateListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_view, fragment).commit();

    }

    private void showCustomDailog() {
        CustomDialog customDialog = new CustomDialog(new CustomDialog.DialogBtnCallBack() {
            @Override
            public void onDialogBtnClick(View view) {
                int id = view.getId();
                if (id == R.id.btDone) {
                    AppApplication appApplication = (AppApplication) getApplication();
                    appApplication.clearChacheSession();
                    mSession.setHasAppLockEnable(false);
                    mSession.setAppLockType("");
                    mSession.setPAN("");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.btCalcel) {
                }
            }
        });

        customDialog.showDialog(PinLockActivity.this, getString(R.string.alert_dialog_header_txt),
                getString(R.string.alert_dialog_pin_reset),
                getString(R.string.alert_dialog_yes_btn_txt),
                getString(R.string.alert_dialog_btn_no_txt),
                true, true);
    }


}

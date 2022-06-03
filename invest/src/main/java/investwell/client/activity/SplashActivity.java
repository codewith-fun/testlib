package investwell.client.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import investwell.broker.activity.BrokerActivity;
import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.common.applock.AppLockOptionActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class SplashActivity extends AppCompatActivity {
    private AppSession mSession;
    private AppApplication mApplication;
    int intValue = 10;
    Handler handler = new Handler();
    private String appLanguage = "";
    private String cameFromWhere = "";
    private String communicationMedium = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.my_progressBar);

        RelativeLayout relMainBody = findViewById(R.id.relMainBody);
        ImageView ivTop = findViewById(R.id.ivTop);
        ImageView ivCentre = findViewById(R.id.ivCentre);
        ImageView ivBottom = findViewById(R.id.ivBottom);

        relMainBody.setBackgroundResource(R.drawable.splash_background);
        ivTop.setBackgroundResource(R.drawable.splash_logo_top);
        ivCentre.setBackgroundResource(R.drawable.splash_logo_middle);
        ivBottom.setBackgroundResource(R.drawable.splash_logo_bottom);

        new Thread(() -> {
            // TODO Auto-generated method stub
            while (intValue < 100) {
                intValue++;
                handler.post(() -> {
                    progressBar.setProgress(intValue);
                });


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
     /*   if (Utils.checkRootMethod1() || Utils.checkRootMethod2() || Utils.checkRootMethod3()) {
            showAlertDialogAndExitApp("You are using a rooted device");
        } else {*/
        setInitializer();
        /*    }*/
        //Utils.generateFinalKey(AppConstants.VALUE_USERNAME,AppConstants.VALUE_PASSWORD);


    }


    public void setInitializer() {
        communicationMedium = "OnAir";
        mApplication = (AppApplication) getApplication();
        mSession = AppSession.getInstance(SplashActivity.this);
        FCMToken();
        refreshUI();

        if (!TextUtils.isEmpty(mSession.getSelectedAppLang())) {
            appLanguage = mSession.getSelectedAppLang();
        } else {
            appLanguage = "English";
        }
    }

    private void getDataFromBundle() {
        Intent i = getIntent();
        if (i != null) {
            if (!TextUtils.isEmpty(i.getStringExtra("comingFrom"))) {
                cameFromWhere = i.getStringExtra("comingFrom");
            }
        }
    }

    /**
     * Method setting up UI as per Session
     **/
    public void refreshUI() {
        getDataFromBundle();

        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(this::activitySelection,

                SPLASH_DISPLAY_LENGTH);

    }

    private void activitySelection() {
        if (mSession.getAppConfig().isEmpty()) {
            mSession.clear();
            if (communicationMedium.equalsIgnoreCase("OnAir"))
                callGeneratePassKeyApi();
            else
                callThroughTunnelMode();

        } else if (mSession.getPassKey().equals("")) {
            if (communicationMedium.equalsIgnoreCase("OnAir"))
                callGeneratePassKeyApi();
            else
                callThroughTunnelMode();
        } else if (!TextUtils.isEmpty(cameFromWhere)
                && (cameFromWhere.equalsIgnoreCase("LanguageChanged") ||
                cameFromWhere.equalsIgnoreCase("LanguageChangedFromSettings"))) {
            if (communicationMedium.equalsIgnoreCase("OnAir"))
                callGeneratePassKeyApi();
            else
                callThroughTunnelMode();
        } else {
            mApplication.getConfigData();
            if (mSession.getDefaultAppLang().isEmpty()) {
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Vernacular")) &&
                        Utils.getConfigData(mSession).optString("Vernacular").equalsIgnoreCase("Y")) {
                    Intent intent = new Intent(getApplicationContext(), LanguageSupportActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                 /*   final ProgressDialog mBar = ProgressDialog.show(SplashActivity.this, null, null, true, false);
                    mBar.setContentView(R.layout.progress_layout);*/
                    /*      mBar.dismiss();*/
                    new Handler().postDelayed(this::launchNextActivity, 4000);
                }
            } else {
                if (!((Activity) this).isFinishing()) {
                    final ProgressDialog mBar = ProgressDialog.show(SplashActivity.this, null, null, true, false);
                    mBar.setContentView(R.layout.progress_layout);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                    new Handler().postDelayed(() -> {
                        launchNextActivity();
                        if (mBar != null)
                            mBar.dismiss();
                    }, 4000);
                }

            }
        }

    }


    private void launchNextActivity() {
        intValue = 100;
        if (!((Activity) this).isFinishing()) {
            progressBar.setProgress(intValue);

        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("MobileVerificationFirstTime")) &&
                Utils.getConfigData(mSession).optString("MobileVerificationFirstTime").equalsIgnoreCase("Y")) {
            if (!mSession.getHasFirstTimeOTP()) {
                Intent intent = new Intent(getApplicationContext(), FirstTImePhoneVerify.class);
                startActivity(intent);
                finish();
            } else {
                if (!mSession.getHasFirstTimeAppIntroLaunched() && (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AppIntroScreen")) &&
                        Utils.getConfigData(mSession).optString("AppIntroScreen").equalsIgnoreCase("Y"))) {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
                        Intent intent = new Intent(getApplicationContext(), WelcomeOptionActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else if (mSession.getUserType().equals("") || !mSession.getHasFirstTimeCompleted()) {
                    Intent intent = new Intent(getApplicationContext(), UserTypesActivity.class);
                    startActivity(intent);
                    finish();


                } else if (mSession.getHasAppLockEnable()) {
                    Intent intent = new Intent(getApplicationContext(), AppLockOptionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("type", "verify_lock");
                    intent.putExtra("callFrom", "splash");
                    startActivity(intent);
                    finish();
                } else if (mSession.getHasLoging()) {
                    if (mSession.getAppLockType().equalsIgnoreCase("nothing")) {
                        if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                                || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                                || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") ||
                                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                                Intent intent = new Intent(getApplicationContext(), MainActivityTypeTwo.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(getApplicationContext(), BrokerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AppLockOptionActivity.class);
                        intent.putExtra("type", "set_screen_lock");
                        intent.putExtra("callFrom", "login");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")
                                    || Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                        Intent intent = new Intent(getApplicationContext(), MainActivityTypeTwo.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
                        Intent intent = new Intent(getApplicationContext(), WelcomeOptionActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        } else if (!mSession.getHasFirstTimeAppIntroLaunched() && (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AppIntroScreen")) &&
                Utils.getConfigData(mSession).optString("AppIntroScreen").equalsIgnoreCase("Y"))) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
                Intent intent = new Intent(getApplicationContext(), WelcomeOptionActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (mSession.getUserType().equals("") || !mSession.getHasFirstTimeCompleted()) {
            Intent intent = new Intent(getApplicationContext(), UserTypesActivity.class);
            startActivity(intent);
            finish();


        } else if (mSession.getHasAppLockEnable()) {
            Intent intent = new Intent(getApplicationContext(), AppLockOptionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("type", "verify_lock");
            intent.putExtra("callFrom", "splash");
            startActivity(intent);
            finish();
        } else if (mSession.getHasLoging()) {
            if (mSession.getAppLockType().equalsIgnoreCase("nothing")) {
                if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                        || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                        || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                        Intent intent = new Intent(getApplicationContext(), MainActivityTypeTwo.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), BrokerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), AppLockOptionActivity.class);
                intent.putExtra("type", "set_screen_lock");
                intent.putExtra("callFrom", "login");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")
                            || Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                Intent intent = new Intent(getApplicationContext(), MainActivityTypeTwo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
                Intent intent = new Intent(getApplicationContext(), WelcomeOptionActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    private void callThroughTunnelMode() {

    }

    private void callGeneratePassKeyApi() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_USERNAME, AppConstants.APP_USERNAME);
        params.put(AppConstants.KEY_USER_PASSWORD, AppConstants.APP_PASSWORD);
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        String macAddress = Settings.Secure.getString(this.getContentResolver(), "bluetooth_address");
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        params.put("IMEINO", android_id + "" + macAddress);
        params.put("Phonename", Build.BRAND);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.GET_GENERATE, new JSONObject(params), response -> {
            if (response.optBoolean("Status") && response.optString("Passkey").length() > 0) {
                mSession.setPassKey(response.optString("Passkey"));
                mSession.setAppType(response.optString("OnlineOption"));
                //mSession.setBid(AppConstants.VALUE_BROKER_ID);
                mSession.setAppConfig("");
                getConfigData(/*mBar*/);

            } else {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

        }, e -> {
//                mBar.dismiss();

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();

        });


        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void getConfigData() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.PASSKEY, mSession.getPassKey());
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        params.put(AppConstants.APP_LANGUAGE, appLanguage);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.APP_CONFIG, new JSONObject(params), response -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            }
            if (response.optBoolean("Status")) {
                mSession.setAppConfig(response.toString());
                cameFromWhere = "";
                activitySelection();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

        }, e -> {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();

        });


        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void FCMToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new Instance ID token
                    String token = Objects.requireNonNull(task.getResult()).getToken();
                    mSession.setFcmToken(token);
                });
    }
}

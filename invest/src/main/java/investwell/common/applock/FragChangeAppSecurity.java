package investwell.common.applock;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.iw.acceleratordemo.R;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static investwell.common.applock.AppLockOptionActivity.DEFAULT_LOCK_CODE;


public class FragChangeAppSecurity extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private RadioGroup mRadioGroup;
    private RadioButton mRadio1, mRadio2, mRadio3;
    private TextView mTvChangePinDetials;
    public ToolbarFragment fragToolBar;
    private BrokerActivity mBrokerActivity;
    private MainActivity mMainActivity;
    private AppApplication mApplication;
    private String mSelected = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());

        } else if (context instanceof MainActivity) {
            this.mMainActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mMainActivity);
            mApplication = (AppApplication) mMainActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());

        }

    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.pin_text_App_Security),
                    true, false, false, false,false,false,false,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_security, container, false);
        mSession = AppSession.getInstance(getActivity());
        if (mBrokerActivity != null) {
            mBrokerActivity.setMainVisibility(this, null);
        } else {
            mMainActivity.setMainVisibility(this, null);
        }


        setUpToolBar();
        mRadioGroup = view.findViewById(R.id.radioGroup1);
        mRadio1 = view.findViewById(R.id.radioButton);
        mRadio2 = view.findViewById(R.id.radioButton2);
        mRadio3 = view.findViewById(R.id.radioButton3);
        mTvChangePinDetials = view.findViewById(R.id.tvPIN);

        view.findViewById(R.id.btnContinue).setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int pos;
                pos = mRadioGroup.indexOfChild(mRadioGroup.findViewById(checkedId));

                switch (pos) {
                    case 0:
                        mSelected = "default";
                        break;

                    case 2:
                        mSelected = "pin";
                        break;

                    case 4:
                        mSelected = "nothing";
                        break;
                }
            }
        });

        updateView();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue) {
            if (mSelected.equalsIgnoreCase("default")) {
                KeyguardManager km = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
                if (km.isKeyguardSecure()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent i = km.createConfirmDeviceCredentialIntent("Use your Screen lock pattern to Login", "");
                        startActivityForResult(i, DEFAULT_LOCK_CODE);
                    }
                }
            } else if (mSelected.equalsIgnoreCase("pin")) {
                Intent intent2 = new Intent(getActivity(), PinLockActivity.class);
                intent2.putExtra("type", "change_pin");
                startActivityForResult(intent2, 108);
            } else if (mSelected.equalsIgnoreCase("nothing")) {
                mSession.setAppLockType(mSelected);
                if (mBrokerActivity != null) {
                    mBrokerActivity.getSupportFragmentManager().popBackStack();
                    mSession.setHasAppLockEnable(false);
                } else {
                    mMainActivity.getSupportFragmentManager().popBackStack();
                    mSession.setHasAppLockEnable(false);
                }

            }
        }
    }

    private void updateView() {
        if (mSession.getAppLockType().equalsIgnoreCase("default")) {
            mRadio1.setChecked(true);
        } else if (mSession.getAppLockType().equalsIgnoreCase("pin")) {
            mRadio2.setText(getString(R.string.pin_text_Change_PIN));
            mTvChangePinDetials.setText(getString(R.string.pin_text_Change_PIN_Details));
            mRadio2.setChecked(true);
        } else if (mSession.getAppLockType().equalsIgnoreCase("nothing")) {
            mRadio3.setChecked(true);
        } else {
            mRadio3.setChecked(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK && requestCode == DEFAULT_LOCK_CODE)) {
            mSession.setAppLockType(mSelected);
            mSession.setHasAppLockEnable(true);
            getActivity().getSupportFragmentManager().popBackStack();
        }else if ((resultCode == RESULT_CANCELED && requestCode == DEFAULT_LOCK_CODE)) {

        } else if ((resultCode == RESULT_CANCELED && requestCode == 108)) {

        } else if (requestCode == 108) {
            mSession.setAppLockType(mSelected);
            mSession.setHasAppLockEnable(true);
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }



}

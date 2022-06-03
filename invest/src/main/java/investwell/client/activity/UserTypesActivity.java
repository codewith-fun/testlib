package investwell.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.iw.acceleratordemo.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.DeviceUtils;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;


public class UserTypesActivity extends BaseActivity implements View.OnClickListener {
    private AppSession mSession;
    private RadioGroup mRadioGroup;
    private AppApplication mApplication;
    public ToolbarFragment fragToolBar;
    private ConstraintLayout cl_user_type_container;
    private CustomButton tvContinue;
    private ProgressBar pbUserType;
    private AVLoadingIndicatorView avlLoading;

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.app_name),
                    true, false, false, false, false, false, false, "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_types);
        mSession = AppSession.getInstance(this);
        setUpToolBar();
        tvContinue = findViewById(R.id.btnContinue);
        mRadioGroup = findViewById(R.id.radioGroup1);
        avlLoading = findViewById(R.id.avi_continue);
        tvContinue.setOnClickListener(this);
        mSession.setUserType("1");
        setUpUiVisibility();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int pos;
                pos = mRadioGroup.indexOfChild(findViewById(checkedId));

                switch (pos) {
                    case 0:
                        mSession.setUserType("1");
                        break;
                    case 2:
                        mSession.setUserType("2");
                        break;
                    case 4:
                        mSession.setUserType("3");
                        break;
                }
            }
        });
        cl_user_type_container = findViewById(R.id.cl_user_type_container);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            cl_user_type_container.setBackgroundColor(ContextCompat.getColor(this, R.color.darkWindowBackground));
        } else {
            cl_user_type_container.setBackground(ContextCompat.getDrawable(this, R.drawable.login_bg));

        }
        FCMToken();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue) {
            continueNext();
        }
    }

    private void setUpUiVisibility() {
        if (Utils.getConfigData(mSession).optString("RegistrationMandatory").equalsIgnoreCase("Y")) {
            findViewById(R.id.radioButton3).setVisibility(View.GONE);
            findViewById(R.id.radio_3_desc).setVisibility(View.GONE);
        } else {
            findViewById(R.id.radioButton3).setVisibility(View.VISIBLE);
            findViewById(R.id.radio_3_desc).setVisibility(View.VISIBLE);
        }
        if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {

            findViewById(R.id.radioButton2).setVisibility(View.VISIBLE);
            findViewById(R.id.radio_2_desc).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.radioButton2).setVisibility(View.GONE);
            findViewById(R.id.radio_2_desc).setVisibility(View.GONE);
        }
    }

    private void continueNext() {

        DoREgistered();
    }


    public void DoREgistered() {
        avlLoading.smoothToShow();
        tvContinue.setText("");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://notificationv2.investwell.in/public/user/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        avlLoading.smoothToHide();
                        tvContinue.setText(getString(R.string.continue_txt));

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("success")) {
                                mSession.setHasFirstTimeCompleted(true);

                                if (mSession.getUserType().equals("1")) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);

                                } else if (mSession.getUserType().equals("2")) {
                                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                                    startActivity(intent);
                                } else if (mSession.getUserType().equals("3")) {
                                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") || Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
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
                                }

                            } else {
                                mApplication.showSnackBar(mRadioGroup, getResources().getString(R.string.error_try_again));
                                avlLoading.smoothToHide();
                                tvContinue.setText(getString(R.string.continue_txt));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        avlLoading.smoothToHide();
                        tvContinue.setText(getString(R.string.continue_txt));

                        if (volleyError.getMessage() != null) {
                            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                try {
                                    JSONObject jsonObject = new JSONObject(error.getMessage());
                                    Toast.makeText(UserTypesActivity.this, jsonObject.optString("error"), Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (volleyError instanceof NoConnectionError) {

                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                TimeZone tz = TimeZone.getDefault();
                String Timezon_id = tz.getID();

                params.put("gcm_id", mSession.getFcmToken());
                params.put("device_name", DeviceUtils.getDeviceName());
                params.put("device_model", DeviceUtils.getDeviceModel());
                params.put("device_os", DeviceUtils.getDeviceOS());
                params.put("device_api", DeviceUtils.getDeviceAPILevel());
                params.put("last_lat", "");
                params.put("last_long", "");
                params.put("device_memory", DeviceUtils.getDeviceMemory(UserTypesActivity.this) + "");
                params.put("device_id", DeviceUtils.getDeviceId(UserTypesActivity.this) + "");
                params.put("pin_code", "");
                params.put("timezone", Timezon_id);
                params.put("email", "");
                params.put("app_type", "Android");
                params.put("app_name", getString(R.string.app_name));
                params.put("user_name", "");
                params.put("user_mobile_no", "");
                params.put("user_type", mSession.getUserType());
                params.put("iw_client_id", "");
                params.put("iw_bid", AppConstants.APP_BID);
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("fcmServerToken"))) {
                    params.put("iw_bid_api_key", Utils.getConfigData(mSession).optString("fcmServerToken"));
                }else{
                    params.put("iw_bid_api_key", "");
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(UserTypesActivity.this);
        requestQueue.add(stringRequest);
    }

    private void FCMToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            // Toast.makeText(getBaseContext(), "ERROR", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        mSession.setFcmToken(token);
                        //  Toast.makeText(getBaseContext(), ""+mSession.getFcmToken(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}

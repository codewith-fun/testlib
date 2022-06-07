package investwell.client.activity;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.iw.acceleratordemo.BuildConfig;
import com.iw.acceleratordemo.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import investwell.broker.activity.BrokerActivity;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.flavourTypeThree.LoginViaMobileFragment;
import investwell.client.flavourTypeThree.LoginViaUserNameFragment;
import investwell.common.applock.AppLockOptionActivity;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.CustomViewPager;
import investwell.utils.DeviceUtils;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomDialog;
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextInputLayout;


public class LoginActivity extends BaseActivity implements View.OnClickListener, CustomDialog.DialogBtnCallBack {
    public static final int REQUEST_CODE_SIGN_IN = 0;
    private TextInputEditText mETUserName, mETPassword;
    private AppSession mSession;
    private TextView tvForgotPwd, tvLogin;
    private CallbackManager mCallbackManager;
    private AppApplication mApplication;
    private CustomDialog customDialog;
    private LinearLayout ll_signup_section;
    private RelativeLayout rlDefaultLoginContainer;
    private LinearLayout ll_tab_login_container;
    public CustomViewPager mViewPager;
    public TabLayout mTabLayout;
    private FragViewPagerAdapter mPagerAdapter;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private AVLoadingIndicatorView avlLoading;
    private String alphaUser,alphaPass;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setInitializer();
        updateViewPagerView();
        setListener();
        setUpUiVisibility();
        Intent intent = getIntent();
        alphaUser = intent.getStringExtra("user");
        alphaPass = intent.getStringExtra("pass");
        if (alphaPass!= null || alphaUser!=null){
            callLoginApi();// when we login from another app,
        }


    }

    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }

    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }

    //Display Network Error Content
    private void displayNoInternetMessage() {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    /***************************************************
     Method contains all initializations of elements
     *****************************************************/
    public void setInitializer() {
        mApplication = (AppApplication) this.getApplication();
        customDialog = new CustomDialog(LoginActivity.this);
        rlDefaultLoginContainer = findViewById(R.id.rl_default_login_container);
        avlLoading = findViewById(R.id.avi_continue);
        ll_tab_login_container = findViewById(R.id.ll_tab_login_container);
        mViewPager = findViewById(R.id.login_viewpager);
        mTabLayout = findViewById(R.id.tabLayout);
        mSession = AppSession.getInstance(this);
        tvLogin = findViewById(R.id.btnLogin);
        tvForgotPwd = findViewById(R.id.tv_forgot);
        mETUserName = findViewById(R.id.et_login_username);
        mETPassword = findViewById(R.id.et_login_password);
        ll_signup_section = findViewById(R.id.ll_signup_section);
        InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            findViewById(R.id.rl_login_container).setBackground(null);
            findViewById(R.id.rl_login_container).setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlack));
        }else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
            ImageView ivLogo =  findViewById(R.id.iv_login_logo);
            TextView iv_dummy =  findViewById(R.id.iv_dummy);
            ivLogo.setVisibility(View.GONE);
            iv_dummy.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rl_login_container).setBackgroundColor(0);

            findViewById(R.id.rl_login_container).setBackground(ContextCompat.getDrawable(this, R.drawable.bg_login));
        }


        // Only for testing
        FCMToken();
        if (BuildConfig.DEBUG) {
           /* mETUserName.setText("girishpathak");
            mETPassword.setText("pathakgirish");*/

          /*  mETUserName.setText("rajkumar@investwellonline.com");
            mETPassword.setText("ZGFJL31N");*/


//            mETUserName.setText("investw252");
//            mETPassword.setText("Arn@75320");

           /*
            mETUserName.setText("nitin_srm");
            mETPassword.setText("Nitin@75320");*/

        /*    mETUserName.setText("binesh.kumar@investwellonline.com");
            mETPassword.setText("E4OHPNIR");*/
                 /*
investw252
Arn@75320
## Broker


## RM
nitin_srm
Nitin@75320

## SB
vallabh_sb
Sapient@123*/


        }
  /*      TypeWriter tvHeader= findViewById(R.id.tv_header);
       tvHeader.setText("");
       tvHeader.setCharacterDelay(150);
       tvHeader.animateText(getResources().getString(R.string.login_default_view_your_portfolio_txt));*/
    }


    /*******************************************
     * Method contains all listener events
     *******************************************/
    private void setListener() {
        tvLogin.setOnClickListener(this);
        tvForgotPwd.setOnClickListener(this);
        findViewById(R.id.tv_sign_up).setOnClickListener(this);
        findViewById(R.id.ivFacebook).setOnClickListener(this);
        findViewById(R.id.ivGoogle).setOnClickListener(this);
    }

    private void updateViewPagerView() {
        List<Fragment> fragList = new ArrayList<>();
        Bundle bundle = new Bundle();


        Fragment fragment1 = new LoginViaMobileFragment();

        fragList.add(fragment1);

        Fragment fragment2 = new LoginViaUserNameFragment();

        fragList.add(fragment2);

        mPagerAdapter = new FragViewPagerAdapter(getSupportFragmentManager(), fragList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mTabLayout.getTabAt(0)).setText("Via Mobile");
            Objects.requireNonNull(mTabLayout.getTabAt(1)).setText("Via Username");
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLogin) {
            validateCredentials();
        } else if (id == R.id.tv_forgot) {
            showForgotPwdDialog();
        } else if (id == R.id.tv_sign_up) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.ivFacebook) {
            getFacebookUserDetails();
        } else if (id == R.id.ivGoogle) {
            connectToGmail();
        }
    }

    private void setUpUiVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("SocialLogin")) &&
                Utils.getConfigData(mSession).optString("SocialLogin").equalsIgnoreCase("Y")) {
            findViewById(R.id.llSocialLogin).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.llSocialLogin).setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("LoginViaMobile")) &&
                Utils.getConfigData(mSession).optString("LoginViaMobile").equalsIgnoreCase("Y")) {
            ll_tab_login_container.setVisibility(View.VISIBLE);
            rlDefaultLoginContainer.setVisibility(View.GONE);
        } else {
            ll_tab_login_container.setVisibility(View.GONE);
            rlDefaultLoginContainer.setVisibility(View.VISIBLE);

        }
        if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
            ll_signup_section.setVisibility(View.VISIBLE);

        } else {
            ll_signup_section.setVisibility(View.GONE);

        }
    }


    /*************************************************
     * Method contains all elements validation operations & cases
     ***********************************************************/
    private void validateCredentials() {
        Utils.hideKeyboard(LoginActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.requireNonNull(mETUserName.getText()).toString().equals("")) {
                mApplication.showSnackBar(mETUserName, getString(R.string.login_erorr_empty_username));
            } else if (TextUtils.isEmpty(Objects.requireNonNull(mETPassword.getText()).toString())) {
                mApplication.showSnackBar(mETPassword, getString(R.string.login_erorr_empty_password));
            } else if (Objects.requireNonNull(mETPassword.getText()).toString().length() < 4) {
                mApplication.showSnackBar(mETPassword, getString(R.string.login_erorr_valid_password));
            } else {
                callLoginApi();
            }
        }
    }

    /*************************************************
     * Method contains calling LOGIN API operation
     ***********************************************************/
    private void callLoginApi() {
        avlLoading.smoothToShow();
        tvLogin.setText("");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());

            jsonObject.put(AppConstants.KEY_USERNAME, Objects.requireNonNull(alphaUser.trim()));
            jsonObject.put("Userpass", Objects.requireNonNull(alphaPass.trim()));


            String url = Config.LOGIN;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject objDa) {
                    if (objDa.optBoolean("Status")) {
                        mSession.setHasSocialLogin(false);
                        /*getConfigData(mBar);*/
                        mSession.set_login_detail(objDa.toString());
                        String userType = objDa.optString("LoginCategory");
                        mSession.setHasFirstTimeCompleted(true);
                        mSession.setEmail(objDa.optString("Email"));
                        mSession.setMobileNumber(objDa.optString("MobileNo"));
                        mSession.setUCC_CODE(objDa.optString("UCC"));
                        mSession.setPAN(objDa.optString("PAN"));
                        mSession.setDOB(objDa.optString("DOB"));
                        mSession.settaxStatus(objDa.optString("TaxStatus"));
                        mSession.setOccupation(objDa.optString("Occupation"));
                        mSession.setImageRawData(objDa.optString("ProfilePic"));
                        mSession.setTransactionPermission(objDa.optJSONArray("TransactionPermission").toString());
                        mSession.setLoginType(userType);
                        mSession.setHasLoging(true);
                        mSession.setUserType("1");
                        mSession.setAppType(objDa.optString("OnlineOption"));
                        mSession.setRiskName(objDa.optString("RiskName"));
                        mSession.setRiskCode(objDa.optString("RiskCode"));
                        mSession.setRiskDescription(objDa.optString("RiskDescription"));
                        mSession.setRiskImage(objDa.optString("RiskImage"));
                        mSession.setHasMendate(objDa.optString("MandateStatus").equals("Y"));
                        mSession.setHasFatca(objDa.optString("FATCAStatus").equals("Y"));
                        mSession.setHasCAFStatus(objDa.optString("CAFStatus").equals("Y"));
                        mSession.setHasSignature(objDa.optString("DocUploadStatus").equals("Y"));
                        mSession.setPANKYC(objDa.optString("PANKYCStatus").equals("Y"));
                        mSession.setRMmobile(objDa.optString("RMMobile"));
                        mSession.setRMemail(objDa.optString("RMEmail"));
                        mSession.setRM(objDa.optString("RM"));
                        mSession.setUname(mETUserName.getText().toString().trim());
                        mSession.setUPassword(mETPassword.getText().toString().trim());

                        if (userType.equals("RM") || userType.equals("SubBroker")) {
                            mSession.setSecondryCID(objDa.optString("CID"));
                        } else {
                            mSession.setCID(objDa.optString("CID"));
                            mSession.setSecondryCID(objDa.optString("CID"));
                        }

                        if ((mSession.getLoginType().equals("Broker")
                                || mSession.getLoginType().equals("SubBroker")
                                || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                                || mSession.getLoginType().equalsIgnoreCase("Region")
                                || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
                            mSession.setBrokerFullName(objDa.optString("Name"));
                        } else {
                            mSession.setFullName(objDa.optString("Name"));
                        }

                        callRegisterApi();


                    } else {
                        avlLoading.smoothToHide();
                        tvLogin.setText(getString(R.string.dashboard_login_btn_txt));
                        customDialog.showDialog(LoginActivity.this, getResources().getString(R.string.login_error_heading),
                                objDa.optString("ServiceMSG"),
                                getResources().getString(R.string.text_ok), "", true, false);

                    }
                }
            },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            avlLoading.smoothToHide();
                            tvLogin.setText(getString(R.string.dashboard_login_btn_txt));
                            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                try {
                                    JSONObject jsonObject = new JSONObject(error.getMessage());

                                    mApplication.showSnackBar(mETPassword, jsonObject.optString("error"));
                                    // Toast.makeText(LoginActivity.this, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (volleyError instanceof NoConnectionError)
                                mApplication.showSnackBar(mETPassword, getResources().getString(R.string.no_internet));
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*************************************************
     * Method contains calling REGISTRATION  API operations
     ***********************************************************/
    private void callRegisterApi() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://notificationv2.investwell.in/public/user/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        avlLoading.smoothToHide();
                        tvLogin.setText(getString(R.string.dashboard_login_btn_txt));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("success")) {
                                if (mSession.getHasAppLockEnable()) {
                                    if ((mSession.getLoginType().equals("Client"))
                                            || (mSession.getLoginType().equals("ClientG"))
                                            || (mSession.getLoginType().equals("Prospects"))) {
                                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                                (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") ||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                                mApplication.showSnackBar(mETPassword, getResources().getString(R.string.error_try_again));
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
                        tvLogin.setText(getString(R.string.dashboard_login_btn_txt));

                        if (volleyError.getMessage() != null) {
                            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                try {
                                    JSONObject jsonObject = new JSONObject(error.getMessage());
                                    mApplication.showSnackBar(mETPassword, jsonObject.optString("error"));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                TimeZone tz = TimeZone.getDefault();
                String Timezon_id = tz.getID();

                params.put("gcm_id", mSession.getFcmToken());
                params.put("device_name", DeviceUtils.getDeviceName());
                params.put("device_model", DeviceUtils.getDeviceModel());
                params.put("device_os", DeviceUtils.getDeviceOS());
                params.put("device_api", DeviceUtils.getDeviceAPILevel());
                params.put("last_lat", "");
                params.put("last_long", "");
                params.put("device_memory", DeviceUtils.getDeviceMemory(LoginActivity.this) + "");
                params.put("device_id", DeviceUtils.getDeviceId(LoginActivity.this) + "");
                params.put("pin_code", "");
                params.put("timezone", Timezon_id);
                params.put("email", mSession.getEmail());
                params.put("app_type", "Android");
                params.put("app_name", getString(R.string.app_name));
                params.put("user_name", mSession.getFullName());
                params.put("user_mobile_no", mSession.getMobileNumber());
                params.put("user_type", mSession.getUserType());
                params.put("iw_client_id", mSession.getCID());
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
            public void retry(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }


    /*************************************************
     * Method called when user Sign In with google & fb
     ***********************************************************/

    public void socialLogin(String email) {

        String url = Config.LOGIN_WITHOUT_PASSWORD;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Username", email.toUpperCase());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject objDa) {
                    if (objDa.optString("Status").equalsIgnoreCase("True")) {
                        String userType = objDa.optString("LoginCategory");
                        mSession.setHasSocialLogin(true);
                        mSession.setUname(objDa.optString("Email"));

                        mSession.set_login_detail(objDa.toString());
                        mSession.setHasFirstTimeCompleted(true);
                        mSession.setEmail(objDa.optString("Email"));
                        mSession.setUCC_CODE(objDa.optString("UCC"));
                        mSession.setPAN(objDa.optString("PAN"));
                        mSession.setDOB(objDa.optString("DOB"));
                        mSession.settaxStatus(objDa.optString("TaxStatus"));
                        mSession.setOccupation(objDa.optString("Occupation"));
                        mSession.setTransactionPermission(objDa.optJSONArray("TransactionPermission").toString());
                        mSession.setLoginType(userType);

                        mSession.setHasLoging(true);
                        mSession.setUserType("1");
                        mSession.setAppType(objDa.optString("OnlineOption"));

                        mSession.setHasMendate(objDa.optString("MandateStatus").equals("Y"));
                        mSession.setHasFatca(objDa.optString("FATCAStatus").equals("Y"));
                        mSession.setHasCAFStatus(objDa.optString("CAFStatus").equals("Y"));
                        mSession.setHasSignature(objDa.optString("DocUploadStatus").equals("Y"));
                        mSession.setPANKYC(objDa.optString("PANKYCStatus").equals("Y"));
                        mSession.setRiskName(objDa.optString("RiskName"));
                        mSession.setRiskCode(objDa.optString("RiskCode"));
                        mSession.setRiskDescription(objDa.optString("RiskDescription"));


                        mSession.setRMmobile(objDa.optString("RMMobile"));
                        mSession.setRMemail(objDa.optString("RMEmail"));
                        mSession.setRM(objDa.optString("RM"));

                        if (userType.equals("RM") || userType.equals("SubBroker")) {
                            mSession.setSecondryCID(objDa.optString("CID"));
                        } else {
                            mSession.setCID(objDa.optString("CID"));
                            mSession.setSecondryCID(objDa.optString("CID"));

                        }

                        if ((mSession.getLoginType().equals("Broker")
                                || mSession.getLoginType().equals("SubBroker")
                                || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                                || mSession.getLoginType().equalsIgnoreCase("Region")
                                || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
                            mSession.setBrokerFullName(objDa.optString("Name"));
                        } else {
                            mSession.setFullName(objDa.optString("Name"));
                        }

                        callRegisterApi();


                    } else {
                        // Toast.makeText(LoginActivity.this, objDa.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        mApplication.showSnackBar(mETPassword, objDa.optString("ServiceMSG"));

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connectToGmail() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
        if (account == null) {
            signIn();
        } else {
            String personEmail = account.getEmail();
            socialLogin(personEmail);
        }
    }

    private void signIn() {
        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(LoginActivity.this, signInOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // call back for facbook
            if (mCallbackManager != null)
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccou) {
                    Account account = googleSignInAccou.getAccount();
                    String personEmail = googleSignInAccou.getEmail();
                    socialLogin(personEmail);
                }
            });

        } else if (resultCode == 0) {
            mApplication.showSnackBar(mETPassword, getResources().getString(R.string.error_try_again));

        }


    }

    public void getFacebookUserDetails() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
            LoginManager.getInstance().registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResults) {
                            getFacebookUserDetails();
                        }

                        @Override
                        public void onCancel() {
                            mApplication.showSnackBar(mETPassword, getResources().getString(R.string.error_try_again));
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            mApplication.showSnackBar(mETPassword, getResources().getString(R.string.error_try_again));
                        }
                    });
        } else {
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,first_name,last_name,picture,gender");
            new GraphRequest(token, "/me", parameters, HttpMethod.GET, new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    try {
                        JSONObject json = response.getJSONObject();
                        if (json != null) {
                            String email = json.optString("email");
                            socialLogin(email);
                        }
                    } catch (Exception e) {

                    }

                }
            }
            ).executeAsync();
            parameters.putString("fields", "id,name,email,first_name,last_name,picture,gender");
        }
    }


    /*************************************************
     * Method contains calling FORGOT PASSWORD API
     ***********************************************************/
    public void onForgotPwdClickApi(final String email) {
        final ProgressDialog mBar = ProgressDialog.show(LoginActivity.this, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        String url = Config.FORGOT_PASSSWORD;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("UserName", email);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    mBar.hide();
                    try {

                        if (object.optString("Status").equals("True")) {
                            customDialog.showDialog(LoginActivity.this, getResources().getString(R.string.message_txt),
                                    object.optString("ServiceMSG"),
                                    getResources().getString(R.string.text_ok), "", true, false);

                        } else {
                            customDialog.showDialog(LoginActivity.this, getResources().getString(R.string.message_txt),
                                    object.optString("ServiceMSG"),
                                    getResources().getString(R.string.text_ok), "", true, false);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mBar.dismiss();
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(LoginActivity.this, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        mApplication.showSnackBar(mETPassword, getResources().getString(R.string.no_internet));

                }
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

            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void showForgotPwdDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_forgot_pwd, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        CustomButton btDone = dialogView.findViewById(R.id.btDone);
        CustomButton btCalcel = dialogView.findViewById(R.id.btCalcel);

        final CustomTextInputEditText editText = dialogView.findViewById(R.id.et_login_email);

        final CustomTextInputLayout tilForgotPwd = dialogView.findViewById(R.id.til_login_forgot_pwd);
        btDone.setVisibility(View.VISIBLE);
        btCalcel.setVisibility(View.VISIBLE);

        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
        tvTitle.setText(getResources().getString(R.string.Forget_Password));
        tvMessage.setText(getResources().getString(R.string.Please_enter_username));
        btDone.setText(getResources().getString(R.string.alert_dialog_btn_txt));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editText.getText())) {
                    mApplication.showSnackBar(tilForgotPwd, getResources().getString(R.string.Please_enter_valid_username));

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        onForgotPwdClickApi(Objects.requireNonNull(editText.getText()).toString().trim());
                    }
                }
                alertDialog.dismiss();

            }
        });
        btCalcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);

        alertDialog.show();
    }


    @Override
    public void onDialogBtnClick(View view) {

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

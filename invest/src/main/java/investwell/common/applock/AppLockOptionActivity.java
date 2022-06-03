package investwell.common.applock;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.SplashActivity;
import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;


public class AppLockOptionActivity extends BaseActivity implements View.OnClickListener {
    private AppSession mSession;
    static public int DEFAULT_LOCK_CODE = 99, CUSTOM_PIN_LOCK_CODE = 100;
    private RadioGroup mRadioGroup;
    public ToolbarFragment fragToolBar;
    private AppApplication mApplication;
    private String mType = "", mCallFrom = "";
    private String checkPinCondition = "";
    private ConstraintLayout clLockOptions;

    @Override
    public void onBackPressed() {
        if (mType.equalsIgnoreCase("set_screen_lock")) {
            mSession.setAppLockType("");
            mSession.setHasAppLockEnable(false);
        } else {
            this.finishAffinity();
        }
        super.onBackPressed();
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.pin_text_App_Security),
                    true, false, false, false, false, false, false, "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSession = AppSession.getInstance(AppLockOptionActivity.this);
        mApplication = (AppApplication) getApplication();
        setContentView(R.layout.activity_applock_options);
        mRadioGroup = findViewById(R.id.radioGroup1);
        clLockOptions=findViewById(R.id.cl_lock_option);


        Intent intent = getIntent();


        if (intent != null && intent.hasExtra("type")) {
            mType = intent.getStringExtra("type");
            mCallFrom = intent.getStringExtra("callFrom");

            if (mType.equalsIgnoreCase("set_screen_lock")) {
               // setContentView(R.layout.activity_applock_options);
                setUpToolBar();
                clLockOptions.setVisibility(View.VISIBLE);

                findViewById(R.id.btnContinue).setOnClickListener(this);
                mSession.setAppLockType("");
                mSession.setHasAppLockEnable(false);

                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                    clLockOptions.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkWindowBackground));
                }else {
                    clLockOptions.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.login_bg));

                }

            } else if (mType.equalsIgnoreCase("verify_lock")) {
                clLockOptions.setVisibility(View.GONE);
                verifyAppLock();
            }
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue) {
            verifyAppLock();
        }
    }

    private void verifyAppLock() {
        if (mType.equalsIgnoreCase("set_screen_lock")) {
            int radioButtonId = mRadioGroup.getCheckedRadioButtonId();
            if (radioButtonId == R.id.radioButton) {
                mSession.setAppLockType("default");
            } else if (radioButtonId == R.id.radioButton2) {
                mSession.setAppLockType("pin");
            } else if (radioButtonId == R.id.radioButton3) {
                mSession.setAppLockType("nothing");
            }
        }

        if (mSession.getAppLockType().equalsIgnoreCase("default")) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Intent i = km.createConfirmDeviceCredentialIntent("Use your Screen lock pattern to Login", "");
                    startActivityForResult(i, DEFAULT_LOCK_CODE);
                }
            } else {
                mSession.setHasAppLockEnable(false);
                mApplication.showCommonDailog(AppLockOptionActivity.this, this, false, getString(R.string.Error), getString(R.string.text_no_screen_lock), "message", false, true);
            }
        } else if (mSession.getAppLockType().equalsIgnoreCase("pin")) {
            Intent intent = new Intent(getApplicationContext(), PinLockActivity.class);
            intent.putExtra("type", mType);
            startActivityForResult(intent, CUSTOM_PIN_LOCK_CODE);
        } else if (mSession.getAppLockType().equalsIgnoreCase("nothing")) {
            mSession.setHasAppLockEnable(false);
            if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                    || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                    || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")||
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
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Intent i = km.createConfirmDeviceCredentialIntent("Use your Screen lock to Login", "");
                    startActivityForResult(i, DEFAULT_LOCK_CODE);
                }
            } else {
                mApplication.showCommonDailog(AppLockOptionActivity.this, this, false, getString(R.string.Error), getString(R.string.text_no_screen_lock), "message", false, true);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK && requestCode == DEFAULT_LOCK_CODE)) {
            mSession.setHasAppLockEnable(true);
            if (mType.equalsIgnoreCase("verify_lock")) {
                mApplication.mFirstTimeSkippedApplock = false;
                if (mCallFrom.equalsIgnoreCase("application")) {
                    if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                            || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                            || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")|| Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
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
                } else
                    loginValidation();
            } else {
                if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                        || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                        || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")|| Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
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
            }
        } else if ((resultCode == RESULT_CANCELED && requestCode == DEFAULT_LOCK_CODE)) {
            if (mType.equalsIgnoreCase("set_screen_lock")) {
                mSession.setAppLockType("");
                mSession.setHasAppLockEnable(false);
            } else {
                onBackPressed();
            }
        } else if ((resultCode == RESULT_CANCELED && requestCode == CUSTOM_PIN_LOCK_CODE)) {
            if (mType.equalsIgnoreCase("set_screen_lock")) {
                mSession.setAppLockType("");
                mSession.setHasAppLockEnable(false);
            } else {
                onBackPressed();
            }
        } else if (requestCode == CUSTOM_PIN_LOCK_CODE) {
            mSession.setHasAppLockEnable(true);
            if (mType.equalsIgnoreCase("verify_lock")) {
                mApplication.mFirstTimeSkippedApplock = false;
                if (mCallFrom.equalsIgnoreCase("application")) {
                    if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                            || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                            || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")|| Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
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
                    loginValidation();
                }
            } else {
                if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                        || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                        || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")|| Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
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
            }
        } else {
            mApplication.showCommonDailog(AppLockOptionActivity.this, this, false, getString(R.string.Error), getString(R.string.text_fail_screen_lock), "message", false, true);
        }
    }

    private void loginValidation() {
        if (mSession.getHasSocialLogin()) {
            if (AppConstants.APP_BID.isEmpty()
                    || mSession.getPassKey().isEmpty()
                    || mSession.getEmail().isEmpty()) {
                mSession.clear();
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                socialLogin(mSession.getEmail());
            }
        } else {
            if (AppConstants.APP_BID.isEmpty()
                    || mSession.getPassKey().isEmpty()
                    || mSession.getUname().isEmpty()
                    || mSession.getUPassword().isEmpty()) {
                mSession.clear();
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                callLoginApi();
            }
        }

    }

    /*************************************************
     * Method contains calling LOGIN API operation
     ***********************************************************/
    private void callLoginApi() {
        try {

            final ProgressDialog mBar = ProgressDialog.show(this, null, null, true, false);
            mBar.setContentView(R.layout.progress_piggy);
            mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_USERNAME, mSession.getUname());
            jsonObject.put("Userpass", mSession.getUPassword());

            String url = Config.LOGIN;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject objDa) {

try{
                    if (objDa.optBoolean("Status")) {
                        mSession.set_login_detail(objDa.toString());
                        String userType = objDa.optString("LoginCategory");
                        mSession.setHasFirstTimeCompleted(true);
                        mSession.setEmail(objDa.optString("Email"));
                        mSession.setLoginType(userType);
                        mSession.setFullName(objDa.optString("Name"));
                        mSession.setUCC_CODE(objDa.optString("UCC"));
                        mSession.setPAN(objDa.optString("PAN"));
                        mSession.setDOB(objDa.optString("DOB"));
                        mSession.settaxStatus(objDa.optString("TaxStatus"));
                        mSession.setOccupation(objDa.optString("Occupation"));
                        mSession.setTransactionPermission(objDa.optJSONArray("TransactionPermission").toString());

                        mSession.setHasLoging(true);
                        mSession.setUserType("1");
                        mSession.setAppType(objDa.optString("OnlineOption"));

                        mSession.setHasMendate(objDa.optString("MandateStatus").equals("Y"));
                        mSession.setHasFatca(objDa.optString("FATCAStatus").equals("Y"));
                        mSession.setHasCAFStatus(objDa.optString("CAFStatus").equals("Y"));
                        mSession.setHasSignature(objDa.optString("DocUploadStatus").equals("Y"));
                        mSession.setPANKYC(objDa.optString("PANKYCStatus").equals("Y"));

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


                        if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                                || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                                || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
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
                        } else {
                            Intent intent = new Intent(getApplicationContext(), BrokerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        showCustomDailog(objDa.optString("ServiceMSG"));
                    }

                }catch (Exception e){

                }finally {
                  mBar.dismiss();
                }

                }
            },

                    volleyError -> {
/*mBar.dismiss();*/
                        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            try {
                                JSONObject jsonObject1 = new JSONObject(error.getMessage());
                                Toast.makeText(AppLockOptionActivity.this, jsonObject1.optString("error"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (volleyError instanceof NoConnectionError)
                            Toast.makeText(AppLockOptionActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    });
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
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
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*************************************************
     * Method called when user Sign In with google & fb
     ***********************************************************/

    private void socialLogin(String email) {

        String url = Config.LOGIN_WITHOUT_PASSWORD;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Username", email);

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
                        mSession.setRiskImage(objDa.optString("RiskImage"));


                        mSession.setRMmobile(objDa.optString("RMMobile"));
                        mSession.setRMemail(objDa.optString("RMEmail"));
                        mSession.setRM(objDa.optString("RM"));

                        if (userType.equals("RM") || userType.equals("SubBroker")) {
                            mSession.setSecondryCID(objDa.optString("CID"));
                        } else {
                            mSession.setCID(objDa.optString("CID"));
                        }

                        if ((mSession.getLoginType().equals("Broker")
                                || mSession.getLoginType().equals("SubBroker")
                                || mSession.getLoginType().equals("RM") ||mSession.getLoginType().equalsIgnoreCase("Zone")
                                ||mSession.getLoginType().equalsIgnoreCase("Region")
                                ||mSession.getLoginType().equalsIgnoreCase("Branch"))) {
                            mSession.setBrokerFullName(objDa.optString("Name"));
                        } else {
                            mSession.setFullName(objDa.optString("Name"));
                        }

                        if ((mSession.getHasLoging() && mSession.getLoginType().equals("Client"))
                                || (mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                                || (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects"))) {
                            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")||
                                                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")|| Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
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
                        showCustomDailog(objDa.optString("ServiceMSG"));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(AppLockOptionActivity.this);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void showCustomDailog(String message) {
        CustomDialog customDialog = new CustomDialog(new CustomDialog.DialogBtnCallBack() {
            @Override
            public void onDialogBtnClick(View view) {
                int id = view.getId();
                if (id == R.id.btDone) {
                    mSession.clear();
                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.btCalcel) {
                }
            }
        });

        customDialog.showDialog(AppLockOptionActivity.this, getString(R.string.alert_dialog_header_txt),
                message,
                getString(R.string.alert_dialog_btn_txt),
                "",
                true, false);
    }
}

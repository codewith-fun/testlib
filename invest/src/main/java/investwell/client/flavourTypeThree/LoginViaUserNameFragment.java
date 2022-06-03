package investwell.client.flavourTypeThree;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.common.applock.AppLockOptionActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.DeviceUtils;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomDialog;
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginViaUserNameFragment extends Fragment implements View.OnClickListener,CustomDialog.DialogBtnCallBack  {

    private CustomDialog customDialog;
    private EditText mETUserName, mETPassword;
    private AppSession mSession;
    private AppApplication mApplication;
    private LoginActivity mActivity;
    private String mCallCommingFrom = "direct_from_login";
    public LoginViaUserNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for getActivity() fragment
        View view = inflater.inflate(R.layout.fragment_login_via_user_name, container, false);
        mActivity = (LoginActivity)getActivity();
        mApplication = (AppApplication) mActivity.getApplication();
        mSession = AppSession.getInstance(getActivity());
        mETUserName = view.findViewById(R.id.et_login_username);
        mETPassword = view.findViewById(R.id.et_login_password);
        customDialog = new CustomDialog(this);
        view.findViewById(R.id.btnLogin).setOnClickListener(this);
        view.findViewById(R.id.tv_forgot).setOnClickListener(this);
        view.findViewById(R.id.ivGoogle).setOnClickListener(this);
        view.findViewById(R.id.ivFacebook).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        if (id == R.id.btnLogin) {
            LoginValidations();
        } else if (id == R.id.tv_forgot) {
            showForgotPwdDialog();
        } else if (id == R.id.ivFacebook) {
            mActivity.getFacebookUserDetails();
        } else if (id == R.id.ivGoogle) {
            mActivity.connectToGmail();
        }
    }

    private void LoginValidations() {
        Utils.hideKeyboard(getActivity());

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
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());

            jsonObject.put(AppConstants.KEY_USERNAME, Objects.requireNonNull(mETUserName.getText()).toString().trim());
            jsonObject.put("Userpass", Objects.requireNonNull(mETPassword.getText()).toString().trim());


            String url = Config.LOGIN;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject objDa) {
                    mBar.dismiss();
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
                        customDialog.showDialog(getActivity(), getResources().getString(R.string.login_error_heading),
                                objDa.optString("ServiceMSG"),
                                getResources().getString(R.string.text_ok), "", true, false);

                    }
                }
            },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            mBar.dismiss();
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
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void callRegisterApi() {

        DialogsUtils.showProgressBar(getActivity(),false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://notificationv2.investwell.in/public/user/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogsUtils.hideProgressBar();
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
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    } else {
                                        Intent intent = new Intent(getActivity(), BrokerActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                } else {
                                    Intent intent = new Intent(getActivity(), AppLockOptionActivity.class);
                                    intent.putExtra("type", "set_screen_lock");
                                    intent.putExtra("callFrom", "login");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    getActivity().finish();
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
                        DialogsUtils.hideProgressBar();

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
                params.put("device_memory", DeviceUtils.getDeviceMemory(getActivity()) + "");
                params.put("device_id", DeviceUtils.getDeviceId(getActivity()) + "");
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


  


   

    private void showForgotPwdDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if (Build.VERSION.SDK_INT >= 21) {
            linerMain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dialog_background_inset));
            relSubMenu.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dialog_header_background));
            GradientDrawable drawable = (GradientDrawable) relSubMenu.getBackground();
            drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        } else {
            relSubMenu.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
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

    /*************************************************
     * Method contains calling FORGOT PASSWORD API
     ***********************************************************/
    public void onForgotPwdClickApi(final String email) {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
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
                            customDialog.showDialog(getActivity(), getResources().getString(R.string.message_txt),
                                    object.optString("ServiceMSG"),
                                    getResources().getString(R.string.text_ok), "", true, false);

                        } else {
                            customDialog.showDialog(getActivity(), getResources().getString(R.string.message_txt),
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
                            Toast.makeText(getActivity(), jsonObject.optString("error"), Toast.LENGTH_LONG).show();
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

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDialogBtnClick(View view) {
        if (view.getId() == R.id.btDone) {//Todo nothing
        }
    }
}

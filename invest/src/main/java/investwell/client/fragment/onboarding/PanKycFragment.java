package investwell.client.fragment.onboarding;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import investwell.client.activity.AccountConfActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.KycActivity;
import investwell.client.activity.MainActivity;
import investwell.client.activity.RequestVideoKycActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.DeviceUtils;
import investwell.utils.Utils;

public class PanKycFragment extends Fragment implements View.OnClickListener {
    private String mEmail = "";
    private String email = "", callBack = "";
    private boolean mIsPANVerifyed = false;
    private String mType = "", mAlreadyUser = "";
    private int count = 0;

    private MainActivity mActivity;
    private AppApplication mApplication;
    private Bundle mBundle, newBundle;
    private AppSession mSession;
    private View panKycView;
    private ToolbarFragment fragToolBar;
    private View contentSteppers, contentPan;
    private ProgressBar mReferCodeBar, mPanBar;
    //Stepper Views
    private TextView mTvStepOne, mTvStepTwo, mTvStepThree, mTvStepFour;
    private FloatingActionButton mFabStepOne, mFabStepTwo, mFabStepThree, mFabStepFour, mFabStepFive;

    //On boarding Views
    private EditText mEtName, mEtMail, mEtPhone, mEtReferCode, mEtPanCard;
    private TextView mTvPanVerify, mTvReferCodeVerify;
    private TextView mTvErrName, mTvErrMail, mTvErrPhone, mTvErrReferCode, mTvErrPan;
    private Button mBtnContinue;
    private CoordinatorLayout coordinatorLayoutRefer;
    private String mCid = "";
    private String kycMessage = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        panKycView = inflater.inflate(R.layout.fragment_pan_kyc, container, false);
        initializer();
        setUpToolBar();
        setUpVisibility();
        showStepper();
        checkBundle();
        initialChecks();
        FCMToken();
        setListener();
        return panKycView;
    }

    private TextWatcher mNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() >= 3) {
                mEtPanCard.setEnabled(true);
                clearErrorMessage();
                if(kycMessage.contains("Name & PAN Mismatched") ){
                    mEtPanCard.setText("");
                    mTvErrPan.setText("");
                }
            } else {
                mEtPanCard.setEnabled(false);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            clearErrorMessage();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher mReferTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mTvErrReferCode.setText(" ");
            if (mEtReferCode.getText().toString().length() >= 3) {
                mTvReferCodeVerify.setVisibility(View.VISIBLE);
            } else {
                mTvReferCodeVerify.setVisibility(View.GONE);
            }

            if (mEtReferCode.getText().toString().isEmpty()) {
                mBtnContinue.setVisibility(View.VISIBLE);
                count = 0;
                mBtnContinue.setEnabled(true);
                mBtnContinue.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.btnPrimaryBackgroundColor));
            }
        }

    };
    private TextWatcher mPanTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mTvErrPan.setText("");
            mTvErrPan.setVisibility(View.INVISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() > 9) {
                callVerifyPanApi(editable.toString(), "self");
            } else {
                mIsPANVerifyed = false;
            }
        }
    };

    private void initializer() {
        mBundle = new Bundle();
        newBundle = getArguments();
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        contentSteppers = panKycView.findViewById(R.id.content_onboarding_steppers);
        mFabStepOne = contentSteppers.findViewById(R.id.fab_step_1);
        mFabStepTwo = contentSteppers.findViewById(R.id.fab_step_2);
        mFabStepThree = contentSteppers.findViewById(R.id.fab_step_3);
        mFabStepFour = contentSteppers.findViewById(R.id.fab_step_4);
        mFabStepFive = contentSteppers.findViewById(R.id.fab_step_5);
        mTvStepOne = contentSteppers.findViewById(R.id.tv_horizontal_1);
        mTvStepTwo = contentSteppers.findViewById(R.id.tv_horizontal_2);
        mTvStepThree = contentSteppers.findViewById(R.id.tv_horizontal_3);
        mTvStepFour = contentSteppers.findViewById(R.id.tv_horizontal_4);
        contentPan = panKycView.findViewById(R.id.content_onboarding_pan_form);
        mEtName = contentPan.findViewById(R.id.et_name);
        mEtMail = contentPan.findViewById(R.id.et_mail);
        mEtPhone = contentPan.findViewById(R.id.et_mobile);
        mEtReferCode = contentPan.findViewById(R.id.et_refer);
        mEtPanCard = contentPan.findViewById(R.id.et_pan);
        coordinatorLayoutRefer = contentPan.findViewById(R.id.cod_refer_container);
        mTvPanVerify = contentPan.findViewById(R.id.tv_verify_pan);
        mTvReferCodeVerify = contentPan.findViewById(R.id.tv_verify_refer_code);
        mTvErrName = contentPan.findViewById(R.id.tv_error_name);
        mTvErrMail = contentPan.findViewById(R.id.tv_error_mail);
        mTvErrPhone = contentPan.findViewById(R.id.tv_error_mobile);
        mTvErrReferCode = contentPan.findViewById(R.id.tv_error_refer);
        mTvErrPan = contentPan.findViewById(R.id.tv_error_pan);
        mBtnContinue = contentPan.findViewById(R.id.btn_continue);
        mPanBar = contentPan.findViewById(R.id.pb_pan);
        mReferCodeBar = contentPan.findViewById(R.id.pb_refer_code);
    }

    private void setUpToolBar() {
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar("PAN KYC Check", true, false, false, false, false, false, false
                    , "");
        }
    }

    private void setUpVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Email"))) {
            email = Utils.getConfigData(mSession).optString("Email");

        } else {
            email = "";


        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CallBack"))) {
            callBack = Utils.getConfigData(mSession).optString("CallBack");

        } else {
            callBack = "";


        }
    }

    private void checkBundle() {
        if (mBundle.containsKey("type")) {
            mType = mBundle.getString("type");
        } else if (mBundle.containsKey("AlreadyUser")) {
            mAlreadyUser = mBundle.getString("AlreadyUser");
        }
        if (mBundle != null && mBundle.containsKey("cid")) {
            mCid = mBundle.getString("cid");
        } else if (mSession.getCID().isEmpty() || mSession.getCID().equalsIgnoreCase("NA")) {
            mCid = mEtPanCard.getText().toString();
        } else {
            mCid = mSession.getCID();
        }
    }

    private void initialChecks() {
        if (mBundle != null && mBundle.containsKey("coming_from")) {
            if (mBundle.getString("coming_from").equalsIgnoreCase("instruction")) {
                mEtName.setText(mSession.getFullName());
                mEtPanCard.setEnabled(true);
            } else {
                mEtPanCard.setEnabled(false);
            }

        } else if (newBundle != null && newBundle.containsKey("coming_from")) {
            mEtName.setText(mBundle.getString("name"));
            mEtMail.setText(mBundle.getString("email"));
            mEtPhone.setText(mBundle.getString("mobile"));
            mEtPanCard.setText(mBundle.getString("pan"));

        } else {
            mEtPanCard.setEnabled(false);
        }


        if (!mIsPANVerifyed && mEtPanCard.getText().toString().equals("")) {
            if (mSession.getHasLoging() && mSession.getEmail().length() > 0) {
                mEmail = mSession.getEmail();
                if (mAlreadyUser.equalsIgnoreCase("true")) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(Html.fromHtml("Kindly use Another PAN which is not registered earlier to avoid " + "<font><b>" + "Registration Failure" + "</b></font>" + "."))
                            .setTitle("Alert!")
                            .setCancelable(false).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                mEtMail.setText(mSession.getEmail());
                mEtPhone.setText(mSession.getMobileNumber());
            } else {
                mEtPanCard.setEnabled(false);

            }
        }

        if (mSession.getHasLoging()) {
            if (mBundle != null && mBundle.containsKey("Name")) {

                if ((!mBundle.getString("Name").isEmpty()) || (!mBundle.getString("Name").equals("_"))) {
                    mEtName.setText(mBundle.getString("Name"));
                    mEtPanCard.setEnabled(true);

                }

                if ((!mBundle.getString("MobileNO").isEmpty()) || (!mBundle.getString("MobileNO").equals("_"))) {
                    mEtPhone.setText(mBundle.getString("MobileNO"));
                }

                if ((!mBundle.getString("Email").isEmpty()) || (!mBundle.getString("Email").equals("_"))) {
                    mEtMail.setText(mBundle.getString("Email"));
                }


            } else {
                mEtMail.setText(mSession.getEmail());
                mEtPhone.setText(mSession.getMobileNumber());
                mEtName.setText(mSession.getFullName());
                mEtPanCard.setEnabled(true);

            }
        }
    }

    private void clearErrorMessage() {
        mTvErrMail.setText("");
        mTvErrMail.setVisibility(View.INVISIBLE);
        mTvErrPhone.setText("");
        mTvErrPhone.setVisibility(View.INVISIBLE);
        mTvErrReferCode.setText("");
        mTvErrReferCode.setVisibility(View.INVISIBLE);
        mTvErrMail.setText("");
        mTvErrMail.setVisibility(View.INVISIBLE);
        mTvErrName.setText("");
        mTvErrName.setVisibility(View.INVISIBLE);

    }

    private void setListener() {
        mBtnContinue.setOnClickListener(this);
        mTvReferCodeVerify.setOnClickListener(this);
        mEtName.addTextChangedListener(mNameTextWatcher);
        mEtMail.addTextChangedListener(mTextWatcher);
        mEtPhone.addTextChangedListener(mTextWatcher);
        mEtPanCard.addTextChangedListener(mPanTextWatcher);
        mEtReferCode.addTextChangedListener(mReferTextWatcher);
    }

    /*****************************************Views Validation Functions**************************/

    //Function validates all views entry one by one
    private boolean isValidViews() {
        if (!isValidName()) {
            return false;
        }else if (!isValidEmail()) {
            return false;
        } else if (!isValidMobile()) {
            return false;
        } else if (!isValidPan()) {
            return false;
        }
        return true;
    }

    //Function for PAN Validation
    private boolean isValidPan() {
        if (TextUtils.isEmpty(mEtPanCard.getText().toString())) {
            mTvErrPan.setText(getResources().getString(R.string.person_details_error_pan));
            mTvErrPan.setVisibility(View.VISIBLE);
            return false;
        } else if (mEtPanCard.getText().toString().length() < 10) {
            mTvErrPan.setText(getResources().getString(R.string.person_details_error_pan_invalid));
            mTvErrPan.setVisibility(View.VISIBLE);

            return false;
        }
        return true;
    }
    private boolean isValidEmail(){
        if (TextUtils.isEmpty(mEtMail.getText().toString())) {
            mTvErrMail.setText(getResources().getString(R.string.signup_error_email));
            mTvErrMail.setVisibility(View.VISIBLE);
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEtMail.getText().toString()).matches()) {
            mTvErrMail.setText(getResources().getString(R.string.signup_error_email_valid));
            mTvErrMail.setVisibility(View.VISIBLE);

            return false;

        }
        return true;
    }
    //Function for Name Validation
    private boolean isValidName() {
        if (TextUtils.isEmpty(mEtName.getText().toString())) {
            mTvErrName.setText(getResources().getString(R.string.person_details_error_name));
            mTvErrName.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }


    //Function for mobile validation
    private boolean isValidMobile() {
        if (TextUtils.isEmpty(mEtPhone.getText().toString())) {
            mTvErrPhone.setText(getResources().getString(R.string.person_details_error_mobile));
            mTvErrPhone.setVisibility(View.VISIBLE);
            return false;
        } else if (mEtPhone.getText().toString().length() < 10) {
            mTvErrPhone.setText(getResources().getString(R.string.person_details_error_mobile_invalid));
            mTvErrPhone.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    /************************************API Functions********************************************/

    private void callVerifyPanApi(String panNumber, final String type) {
        mTvErrPan.setText("");
        mPanBar.setVisibility(View.VISIBLE);
        if (mPanBar.isShown()) {
            mEtPanCard.setEnabled(false);
        } else {
            mEtPanCard.setEnabled(true);

        }
        String url = Config.PAN_VERIFICATION;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("PAN", panNumber);
            if (Config.COMMON_URL.equalsIgnoreCase("https://nativeapi.my-portfolio.in")) {
                jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            } else {
                jsonObject.put(AppConstants.PASSKEY, "jdfjdf7474jcfjh");
            }
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("ReferCode", mEtReferCode.getText().toString());
            jsonObject.put("Cid", mCid);
            jsonObject.put("applicant_name", mEtName.getText().toString());
            jsonObject.put("location_cord", "");
            jsonObject.put("location_details", "");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mPanBar.setVisibility(View.GONE);
                    mEtPanCard.setEnabled(true);
                    if (jsonObject.optString("KYCResponseMSG").equalsIgnoreCase("KYC Verified") ||
                            jsonObject.optString("KYCResponseMSG").equalsIgnoreCase("KYC Not Verified")) {
                        mBtnContinue.setEnabled(true);
                        mBtnContinue.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.btn_bg_primary));
                    } else {
                        mBtnContinue.setEnabled(false);
                        mBtnContinue.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorGrey_500));
                    }

                    if (jsonObject.optString("KYCResponseMSG").equals("KYC Verified")) {
                        // mEtName.setText(jsonObject.optString("Name"));
                        mTvErrPan.setText(jsonObject.optString("ServiceMSG"));
                        mTvErrPan.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
                        mTvErrPan.setVisibility(View.VISIBLE);

                    }

                    if (jsonObject.optString("KYCResponseMSG").contains("new credentials")) {

                        Intent intent = new Intent(getActivity(), AccountConfActivity.class);
                        intent.putExtra("message", jsonObject.optString("KYCResponseMSG"));
                        startActivity(intent);
                    }

                    if (jsonObject.optString("KYCResponseMSG").contains("Name & PAN Mismatched")) {
                        kycMessage = jsonObject.optString("KYCResponseMSG");
                    }

                    if (jsonObject.optString("Status").equals("True") && jsonObject.optString("KYCResponseMSG").contains("KYC Verified")) {
                        if (type.equals("minor")) {
                            //  tilGuardianPan.setError(jsonObject.optString("ServiceMSG"));
                            // cvGuardianName.setVisibility(View.VISIBLE);
                       /*     mEtGuardName.setText(jsonObject.optString("Name"));
                            mIsGardianPANVerifyed = true;*/

                        } else {

                            mIsPANVerifyed = true;

                            //Toast.makeText(mActivity, "Server error, Please try again later", Toast.LENGTH_SHORT).show();

                        }
                    } else if (jsonObject.optString("KYCResponseMSG").contains("IIN is ACTIVE")) {
                        String IIN = jsonObject.optString("IIN");
                        callIiNDialog(IIN, 100, jsonObject.optString("KYCResponseMSG"));


                    } else if (jsonObject.optString("KYCResponseMSG").contains("IIN is INACTIVE")) {
                        String IIN = jsonObject.optString("IIN");
                        callIiNDialog(IIN, 200, jsonObject.optString("KYCResponseMSG"));

                    } else {
                        if (type.equals("minor")) {
                           /* tilGuardianPan.setError(jsonObject.optString("ServiceMSG"));
                            cvGuardianName.setVisibility(View.GONE);
                            mIsGardianPANVerifyed = false;*/
                        } else {
                            mTvErrPan.setText(jsonObject.optString("ServiceMSG"));
                            mTvErrPan.setTextColor(ColorStateList.valueOf(Color.RED));
                            mTvErrPan.setVisibility(View.VISIBLE);
                            mIsPANVerifyed = false;
                        }


                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mPanBar.setVisibility(View.GONE);
                    mEtPanCard.setEnabled(true);
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            //  Toast.makeText(mActivity, mDataList.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError) {
                        // Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    } else {
                        if (type.equals("minor")) {
                            /*cvGuardianName.setVisibility(View.GONE);
                            mIsGardianPANVerifyed = false;*/
                        } else {
                            mIsPANVerifyed = false;
                        }
                    }
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

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            if (type.equals("minor")) {
              /*  cvGuardianName.setVisibility(View.GONE);
                mIsGardianPANVerifyed = false;*/
            } else {
                mIsPANVerifyed = false;
            }
        }


    }

    public void callCheckReferCodeApi() {
        mTvReferCodeVerify.setVisibility(View.GONE);
        mReferCodeBar.setVisibility(View.VISIBLE);
        String url = Config.Refrl_Code;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("SBCode", mEtReferCode.getText().toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mReferCodeBar.setVisibility(View.GONE);
                    try {
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {

                            mTvErrReferCode.setText("");
                            mTvErrReferCode.setVisibility(View.INVISIBLE);
                            mBtnContinue.setEnabled(true);
                            mBtnContinue.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.btn_bg_primary));

                        } else {
                            mTvErrReferCode.setText("(" + jsonObject.optString("ServiceMSG") + ")");
                            mTvErrReferCode.setVisibility(View.VISIBLE);
                            mBtnContinue.setEnabled(false);
                            mBtnContinue.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorGrey_500));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mReferCodeBar.setVisibility(View.GONE);
                    mTvErrReferCode.setText("(" + error.getLocalizedMessage() + ")");
                    mTvErrReferCode.setVisibility(View.VISIBLE);
                    mBtnContinue.setEnabled(false);
                    mBtnContinue.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorGrey_500));
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
            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callSaveProspectDetailsApi() {

        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.SAVE_BASIC_DETAILS;
        Map<String, String> params = new HashMap<String, String>();

        try {
            params.put("EmailID", mEmail);
            params.put("Bid", AppConstants.APP_BID);
            params.put("Mobile", mEtPhone.getText().toString());
            params.put("Name", mEtName.getText().toString());
            params.put("PAN", mEtPanCard.getText().toString());
            params.put(AppConstants.PASSKEY, mSession.getPassKey());
            params.put("KYCStatus", mIsPANVerifyed ? "Y" : "N");
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        mSession.setCID(object.optString("CID"));

                        if (mType.equals("fcm_registration")) {
                            mSession.setHasFirstTimeCompleted(true);
                            callRegisterUserApi();
                        } else {
                            setDataForBundle();
                        }

                    } else {

                        Toast.makeText(mActivity, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mActivity, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }

    public void callRegisterUserApi() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://notificationv2.investwell.in/public/user/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mBar.dismiss();
                        setDataForBundle();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mBar.dismiss();

                        if (volleyError.getMessage() != null) {
                            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                try {
                                    JSONObject jsonObject = new JSONObject(error.getMessage());
                                    Toast.makeText(mActivity, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
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
                final Map<String, String> params = new HashMap<String, String>();
                TimeZone tz = TimeZone.getDefault();
                String Timezon_id = tz.getID();

                params.put("gcm_id", mSession.getFcmToken());
                params.put("device_name", DeviceUtils.getDeviceName());
                params.put("device_model", DeviceUtils.getDeviceModel());
                params.put("device_os", DeviceUtils.getDeviceOS());
                params.put("device_api", DeviceUtils.getDeviceAPILevel());
                params.put("last_lat", "");
                params.put("last_long", "");
                params.put("device_memory", DeviceUtils.getDeviceMemory(mActivity) + "");
                params.put("device_id", DeviceUtils.getDeviceId(mActivity) + "");
                params.put("pin_code", "");
                params.put("timezone", Timezon_id);
                params.put("email", mEmail);
                params.put("app_type", "Android");
                params.put("app_name", getString(R.string.app_name));
                params.put("mEtName", mEtName.getText().toString());
                params.put("user_mobile_no", mEtPhone.getText().toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);
    }

    //Function for Video KYC
    private void callVideoKycApi() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int mSec = calendar.get(Calendar.MILLISECOND);
        String url = Config.Video_KYC;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Mobile", mEtPhone.getText().toString());
            jsonObject.put("Name", mEtName.getText().toString());
            jsonObject.put("Email", mEmail);
            jsonObject.put("PAN", mEtPanCard.getText().toString());
            jsonObject.put("KYCUserName", mEtName.getText().toString().replace(" ", "").toLowerCase() + mSec);
            jsonObject.put("ClientID", mSession.getCID());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    if (response.optString("Status").equalsIgnoreCase("True")) {

                        Intent intent = new Intent(mActivity, KycActivity.class);
                        intent.putExtra("title", "Video KYC");
                        intent.putExtra("video_kyc_url", response.optString("ServiceMSG"));
                        startActivity(intent);
                        mActivity.finish();
                    } else {
                        Intent intent = new Intent(mActivity, RequestVideoKycActivity.class);
                        intent.putExtra("title", "Video KYC");
                        intent.putExtra("messageKyc", response.optString("ServiceMSG"));
                        intent.putExtra("Email",mEtMail.getText().toString());
                        startActivity(intent);
                        mActivity.finish();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBar.dismiss();
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
            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**********************************Dialog Functions**************************************/

    public void callIiNDialog(final String value, final int code, String message) {

        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (code == 100) {

            Intent intent = new Intent(getActivity(), AccountConfActivity.class);
            intent.putExtra("message", message);
            startActivity(intent);
            // startActivity(new Intent(mActivity, AccountConfirmed.class));
        } else {

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("UCC", value);
                jsonObject.put("chequeRequired", Utils.getConfigData(mSession).optString("ChequeRequired"));
                jsonObject.put("ServiceMSG", message);
                if (Utils.getConfigData(mSession).optString("SignRequired").equalsIgnoreCase("Y")) {
                    mBundle.putString("AllData", jsonObject.toString());
                    mBundle.putString("comingFrom", "Form1");
                    mActivity.displayViewOther(95, mBundle);
                    mEtPanCard.setText("");
                } else {
                    startActivity(new Intent(getActivity(), AccountConfActivity.class));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void callSuccessDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.important_txt))
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.personal_details_success_registration_desc))
                .setPositiveButton(getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        mActivity.displayViewOther(6, mBundle);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void callKYCDialog() {

        final Dialog dialog = new Dialog(mActivity, R.style.Theme_AppCompat_Light);
        dialog.setContentView(R.layout.videokyc_dialog);
        TextView mTvEmail = dialog.findViewById(R.id.TvEmail);
        TextView mTvMobile = dialog.findViewById(R.id.TvMobile);
        TextView mInfo = dialog.findViewById(R.id.info);
        dialog.findViewById(R.id.later_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if (Utils.getConfigData(mSession).optString("AllowNonKYCRegistration").equalsIgnoreCase("Y")) {
                    mActivity.displayViewOther(6, mBundle);
                } else {
                    startActivity(new Intent(mActivity, MainActivity.class));
                    mActivity.finish();
                }
            }
        });

        dialog.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callVideoKycApi();
            }
        });
        mInfo.setText("You can also contact " + getString(R.string.app_name) + " for KYC at:");
        mTvEmail.setText(email);
        mTvMobile.setText(callBack);
        dialog.setCancelable(true);


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        dialog.show();
    }

    /**********************************Utility Functions**************************************/

    //Function to set data in bundles
    private void setDataForBundle() {

        mBundle.putString("radiobtn_value", "M");
        mBundle.putString("PAN_value", mEtPanCard.getText().toString());
        mBundle.putString("name_value", mEtName.getText().toString());
        // mBundle.putString("dateofbirth_value", mEtDateOfBirth.getText().toString());
        mBundle.putString("mobile_value", mEtPhone.getText().toString());
        mBundle.putString("email_value", mEtMail.getText().toString());
        //mBundle.putString("gaurdian_pan_value", mEtGuardianPan.getText().toString());
        //mBundle.putString("gaurdian_name_value", mEtGuardName.getText().toString());
        mBundle.putString("refrl_code", mEtReferCode.getText().toString());

        mBundle.putString("holding_nature_code", "SI");
        mBundle.putString("second_pan_value", "");
        mBundle.putString("second_name_value", "");
        // mBundle.putString("address_one_value", mEtHouseNo.getText().toString());
        //mBundle.putString("address_second_value", mEtStreet.getText().toString());
        // mBundle.putString("address_third_value", address_third_value);
        //mBundle.putString("city_value", mEtCity.getText().toString());
        //mBundle.putString("pin_value", mEtPin.getText().toString());
        mBundle.putString("second_pan_value", "");
        mBundle.putString("second_name_vlaue", "");
        mBundle.putString("foreign_address_value", "");
        mBundle.putString("foreign_country_value", "");
        mBundle.putString("foreign_state_value", "");
        mBundle.putString("foreign_city_value", "");
        mBundle.putString("foreign_pin_value", "");


        // showSucessDialog();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (!TextUtils.isEmpty(mTvErrPan.getText().toString())) {
                if (mTvErrPan.getText().toString().equalsIgnoreCase("KYC Verified")) {
                    if (mSession.getHasLoging()) {

                        mActivity.displayViewOther(6, mBundle);
                    } else {
                        callSuccessDialog();
                    }
                } else {
                    if (mTvErrPan.getText().toString().equalsIgnoreCase("KYC Not Verified") &&
                            Utils.getConfigData(mSession).optString("VideoKYCRequired").equalsIgnoreCase("Y")) {
                        callKYCDialog();
                    } else {
                        if (mSession.getHasLoging()) {
                            mActivity.displayViewOther(6, mBundle);
                        } else {
                            callSuccessDialog();
                        }
                    }
                }
            } else {
                //callKYCDialog();
                mTvErrPan.setText("PAN verification is in process,please wait");
                mTvErrPan.setVisibility(View.VISIBLE);
            }

        }

    }

    //Function to generate FCM Tokens
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

    //Show PAN KYC Stepper
    private void showStepper() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            mFabStepOne.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepOne.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv1 = contentSteppers.findViewById(R.id.tv_step_1);
            tv1.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepTwo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepTwo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv2 = contentSteppers.findViewById(R.id.tv_step_2);
            tv2.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepThree.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepThree.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv3 = contentSteppers.findViewById(R.id.tv_step_3);
            tv3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
        }else{
            mFabStepOne.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepOne.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv1 = contentSteppers.findViewById(R.id.tv_step_1);
            tv1.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepTwo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepTwo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv2 = contentSteppers.findViewById(R.id.tv_step_2);
            tv2.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepThree.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepThree.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv3 = contentSteppers.findViewById(R.id.tv_step_3);
            tv3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue) {
            if (mTvReferCodeVerify.getVisibility() == View.VISIBLE && count == 0) {
                mBtnContinue.setEnabled(false);
                mApplication.showSnackBar(contentPan, getResources().getString(R.string.personal_form_error_verification_code));
            } else {
                mBtnContinue.setEnabled(true);
                if (isValidViews()) {
                    mEmail = mEtMail.getText().toString();
                    if (Utils.isNetworkConnected(mActivity)) {
                        if (mSession.getHasLoging()) {
                            setDataForBundle();
                        } else {
                            callSaveProspectDetailsApi();
                        }
                    } else {
                        mApplication.showSnackBar(view, getResources().getString(R.string.no_internet));

                    }

                }

            }
        } else if (id == R.id.tv_verify_refer_code) {
            count = 1;
            mBtnContinue.setEnabled(true);
            callCheckReferCodeApi();
        }
    }
}

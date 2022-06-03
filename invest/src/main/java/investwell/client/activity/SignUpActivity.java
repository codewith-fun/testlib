package investwell.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.iw.acceleratordemo.R;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextInputLayout;
import investwell.utils.customView.CustomTextViewBold;
import investwell.utils.customView.CustomTextViewLight;
import investwell.utils.customView.CustomTextViewRegular;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {
    public static String sCallingFrom = "login";
    private ProgressDialog mBar;
    private CustomTextInputEditText mETFullName, mETEmail;
    private CustomTextInputLayout tilFullName, tilMobileNo, tilEmailAddress;
    private AppSession mSession;
    private CustomTextViewRegular txtByRegistering;
    private CustomTextViewBold tvSignIn;
    private CustomButton btnSignUp;
    private AppApplication appApplication;
    private String mLoginType = "normal";
    private LinearLayout llParent;
    public static final int REQUEST_CODE_SIGN_IN = 0;
    private CallbackManager mCallbackManager;
    private MainActivity mainActivity;
    private String toc = "", privacyPolicy = "";
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private AVLoadingIndicatorView avlLoading;
    private Spinner mSpCountryCode;
    private String mCountryCode = "+91";
    private String mComingFrom="", mPhoneCountrycode = "";
    private CountryCodePicker ccp;
    private EditText mEtMobile;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setInitializer();
        setListener();
        setUpUiVisibility();
        setUpFooterUi();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            clearErrorMessage();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setInitializer() {
        mainActivity = new MainActivity();
        avlLoading = findViewById(R.id.avi_continue);
        mETEmail = findViewById(R.id.et_signup_email);
        mETFullName = findViewById(R.id.et_signup_full_name);
        mEtMobile = findViewById(R.id.et_signup_phone);
        tilEmailAddress = findViewById(R.id.til_signup_email);
        tilFullName = findViewById(R.id.til_signup_full_name);
        tilMobileNo = findViewById(R.id.til_signup_phone);
        txtByRegistering = findViewById(R.id.tv_footer_content);
        tvSignIn = findViewById(R.id.tv_sign_in);
        btnSignUp = findViewById(R.id.btnSignUp);
        mSession = AppSession.getInstance(this);
        appApplication = (AppApplication) this.getApplication();
        llParent = findViewById(R.id.rl_parent_signup);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            llParent.setBackground(null);
            llParent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlack));
        } else {
            llParent.setBackgroundColor(0);

            llParent.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_login));
        }

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        mEtMobile = findViewById(R.id.phone_number_edt);
       ccp.registerPhoneNumberTextView(mEtMobile);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                mCountryCode = "+"+selectedCountry.getPhoneCode();
                //Toast.makeText(SignUpActivity.this, "Updated " + mCountryCode, Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void setListener() {
        btnSignUp.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
        mEtMobile.addTextChangedListener(textWatcher);
        mETEmail.addTextChangedListener(textWatcher);
        mETFullName.addTextChangedListener(textWatcher);
        findViewById(R.id.ivFacebook).setOnClickListener(this);
        findViewById(R.id.ivGoogle).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSignUp) {
            validateFormFields();
        } else if (id == R.id.tv_sign_in) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.ivFacebook) {
            getFacebookUserDetails();
        } else if (id == R.id.ivGoogle) {
            connectToGmail();
        }
    }


    private void setUpFooterUi() {
        SpannableString SpanString = new SpannableString(
                "By clicking Register, you agree to our Terms of Use  &  Privacy Policy");

        ClickableSpan teremsAndCondition = new ClickableSpan() {
            @Override
            public void onClick(View textView) {


                Intent mIntent = new Intent(SignUpActivity.this, WebViewActivity.class);
                mIntent.putExtra("title", "Terms of Use");
                mIntent.putExtra("url", toc);
                startActivity(mIntent);

            }
        };

        ClickableSpan privacy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {

                Intent mIntent = new Intent(SignUpActivity.this, WebViewActivity.class);
                mIntent.putExtra("title", "Privacy Policy");
                mIntent.putExtra("url", privacyPolicy);
                startActivity(mIntent);

            }
        };

        SpanString.setSpan(teremsAndCondition, 39, 52, 0);
        SpanString.setSpan(privacy, 56, 70, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 39, 52, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 56, 70, 0);
        SpanString.setSpan(new UnderlineSpan(), 39, 52, 0);
        SpanString.setSpan(new UnderlineSpan(), 56, 70, 0);

        txtByRegistering.setMovementMethod(LinkMovementMethod.getInstance());
        txtByRegistering.setText(SpanString, TextView.BufferType.SPANNABLE);
        txtByRegistering.setSelected(true);
    }

    private void validateFormFields() {
        if (TextUtils.isEmpty(mETFullName.getText().toString())) {
            tilFullName.setError(getResources().getString(R.string.signup_error_full_name));
        } else if (mETFullName.getText().length() < 3) {
            tilFullName.setError(getResources().getString(R.string.signup_error_full_name_length));
        } else if (TextUtils.isEmpty(mETEmail.getText().toString())) {
            tilEmailAddress.setError(getResources().getString(R.string.signup_error_email));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mETEmail.getText().toString()).matches()) {
            tilEmailAddress.setError(getResources().getString(R.string.signup_error_email_valid));
        } else if (TextUtils.isEmpty(mEtMobile.getText().toString())) {
            tilMobileNo.setError(getResources().getString(R.string.signup_error_mobile_no));
        } else if (mEtMobile.getText().length() < 10) {
            tilMobileNo.setError(getResources().getString(R.string.signup_error_mobile_no_length));
        } else {
            callVerifyEmailApi();
            mSession.setUserName(mETFullName.getText().toString());
        }
    }

    private void setUpUiVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("SocialLogin")) &&
                Utils.getConfigData(mSession).optString("SocialLogin").equalsIgnoreCase("Y")) {
            findViewById(R.id.llSocialLogin).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.llSocialLogin).setVisibility(View.GONE);


        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("TermsCondition"))) {
            toc = Utils.getConfigData(mSession).optString("TermsCondition");

        } else {
            toc = "";


        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("PrivacyPolicy"))) {
            privacyPolicy = Utils.getConfigData(mSession).optString("PrivacyPolicy");

        } else {
            privacyPolicy = "";


        }
    }

    private void callVerifyEmailApi() {
        avlLoading.smoothToShow();
        btnSignUp.setText("");
        String url = Config.EMAIL_VERIFICATION;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("EmailId", mETEmail.getText().toString().trim().replace(" ",""));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                     avlLoading.smoothToHide();
                        btnSignUp.setText(getString(R.string.signup_btn_txt));
                    try {

                        if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("EmailID Available")) {
                            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("MobileVerification")) &&
                                    Utils.getConfigData(mSession).optString("MobileVerification").equalsIgnoreCase("Y")) {
                                Intent intent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("coming_from", "instruction");
                                bundle.putString("fullname", mETFullName.getText().toString().trim());
                                bundle.putString("phone", mEtMobile.getText().toString().trim().replace(" ",""));
                                bundle.putString("phone_Country_code", mCountryCode);
                                bundle.putString("email", mETEmail.getText().toString().trim().replace(" ",""));
                                bundle.putString("loginType", mLoginType);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("EmailVerification")) &&
                                    Utils.getConfigData(mSession).optString("EmailVerification").equalsIgnoreCase("Y")) {
                                Intent intent = new Intent(getApplicationContext(), OTPVerificationEmailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("coming_from", "instruction");
                                bundle.putString("fullname", mETFullName.getText().toString().trim());
                                bundle.putString("phone", mEtMobile.getText().toString().trim().replace(" ",""));
                                bundle.putString("phone_Country_code", mCountryCode);
                                bundle.putString("email", mETEmail.getText().toString().trim());
                                bundle.putString("loginType", mLoginType);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString("coming_from", "instruction");
                                bundle.putString("fullname", mETFullName.getText().toString().trim());
                                bundle.putString("phone", mEtMobile.getText().toString().trim().replace(" ",""));
                                bundle.putString("phone_Country_code", mCountryCode);
                                bundle.putString("email", mETEmail.getText().toString().trim().replace(" ",""));
                                bundle.putString("loginType", mLoginType);


                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }
                        } else {

                            appApplication.showCommonDailog(SignUpActivity.this, SignUpActivity.this, true, getResources().getString(R.string.error),jsonObject.optString("ServiceMSG") , "Error", false, true);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                     avlLoading.smoothToHide();
                        btnSignUp.setText(getString(R.string.signup_btn_txt));
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(SignUpActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                            appApplication.showSnackBar(llParent, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

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


    private void clearErrorMessage() {
        tilMobileNo.setError("");
        tilFullName.setError("");
        tilEmailAddress.setError("");
    }

    private void connectToGmail() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SignUpActivity.this);
        if (account == null) {
            signIn();
        } else {
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            mETEmail.setText(personEmail);
            mETFullName.setText(personName);
            mLoginType = "gmail";
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
        return GoogleSignIn.getClient(SignUpActivity.this, signInOptions);
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

                    String personName = googleSignInAccou.getDisplayName();
                    String personEmail = googleSignInAccou.getEmail();

                    mETEmail.setText(personEmail);
                    mETFullName.setText(personName);
                    mLoginType = "gmail";

                    System.out.println();
                }
            });
        }
       /* if (requestCode == 1231 && resultCode == RESULT_OK) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            final String phoneNumber = credential.getId();
        }*/

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
                            Toast.makeText(SignUpActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onError(FacebookException exception) {
                            Toast.makeText(SignUpActivity.this, "Error!", Toast.LENGTH_SHORT).show();
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
                            mETEmail.setText(json.optString("email"));
                            mETFullName.setText(json.optString("name"));
                            mLoginType = "facebook";

                   /*     mSession.setfirstName(json.optString("first_name"));
                        mSession.setlastName(json.optString("last_name"));
                        mSession.setUserName(json.optString("first_name") + " " + json.optString("last_name"));
                        mSession.setEmail(json.optString("email"));
                        mSession.setInsertBy("facebook");
                        mSession.setSocialID(json.optString("id"));
                        doLogin();
*/
                        }
                    } catch (Exception e) {

                    }

                }
            }
            ).executeAsync();
        }
    }


}

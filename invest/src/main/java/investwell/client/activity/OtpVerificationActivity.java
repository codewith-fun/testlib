package investwell.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import investwell.broker.activity.BrokerActivity;
import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.common.applock.AppLockOptionActivity;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.PinEntryView;
import investwell.utils.Utils;

public class OtpVerificationActivity extends BaseActivity {

    private String mVerificationId;
    private PinEntryView editTextCode;
    private FirebaseAuth mAuth;
    private AppSession mSession;
    private ProgressDialog mBar;
    private TextView mtvSmsDescription;
    private String mPhoneNumber = "", mFullName = "", mEmailAddress = "";
    private Bundle mBundle;
    private AppApplication mApplication;
    private ImageView ivBack;
    private String mComingFrom="", mPhoneCountrycode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verify);
        initializer();
        getDataFromBundle();
        setUpUiVisibility();
        setListener();
        //if the automatic sms detection did not work, user can also enter the code manually


    }

    private void initializer() {
        mSession = AppSession.getInstance(OtpVerificationActivity.this);
        mApplication = (AppApplication) getApplication();
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);
        mtvSmsDescription = findViewById(R.id.tv_sms_description);
        ivBack = findViewById(R.id.imageView3);
    }

    private void getDataFromBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mBundle = intent.getExtras();
            if(mBundle!=null) {
                mPhoneNumber = mBundle.getString("phone");
                mPhoneCountrycode = mBundle.getString("phone_Country_code");
                mFullName = mBundle.getString("fullname");
                mEmailAddress = mBundle.getString("email");
                mComingFrom=mBundle.getString("coming_from");
                mSession.setEmail(mEmailAddress);
            }
        }
    }

    private void launchNextActivity() {
        if (!mSession.getHasFirstTimeAppIntroLaunched() && (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AppIntroScreen")) &&
                Utils.getConfigData(mSession).optString("AppIntroScreen").equalsIgnoreCase("Y"))) {

            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
                Intent intent = new Intent(getApplicationContext(), WelcomeOptionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }


        } else if (mSession.getUserType().equals("") || !mSession.getHasFirstTimeCompleted()) {
            Intent intent = new Intent(getApplicationContext(), UserTypesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }
    private void setListener() {
        //so adding a click listener to the button
        findViewById(R.id.btn_verify_sms).setOnClickListener(v -> {
            String code = editTextCode.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                mApplication.showSnackBar(editTextCode, "Enter OTP");

            } else {

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });


        findViewById(R.id.tv_sms_resend).setOnClickListener(view -> {

            sendVerificationCode(mPhoneNumber);
            mApplication.showSnackBar(editTextCode, getResources().getString(R.string.otp_verification_sent_msg));


            //verifying the code entered manually


        });
        findViewById(R.id.tv_change_mobile).setOnClickListener(view -> finish());

        ivBack.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void setUpUiVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("MobileVerification")) &&
                Utils.getConfigData(mSession).optString("MobileVerification").equalsIgnoreCase("Y")) {
            String primaryMsg = getResources().getString(R.string.otp_verification_sms_sent_desc)+"  ";
            String html=primaryMsg+"<b>"+mPhoneNumber+"</b>";
            sendVerificationCode(mPhoneNumber);
            mtvSmsDescription.setText(Html.fromHtml(html));
        } else {
            checkEmail();


        }
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mPhoneCountrycode+mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+91" + mobile,
//                60,
//                TimeUnit.SECONDS,
//                (Activity) TaskExecutors.MAIN_THREAD,
//                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            // super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        mBar = new ProgressDialog(OtpVerificationActivity.this, R.style.MyTheme);
        mBar.setCancelable(false);
        mBar.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mBar.show();

        try {

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mBar.dismiss();
                            verifyNum(mPhoneNumber);


                        } else {
                            mBar.dismiss();
                            //verification unsuccessful.. display an error message

                            String message = getResources().getString(R.string.otp_verification_phone_error);


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = getResources().getString(R.string.otp_verification_invalid_code);
                            }
                            mApplication.showSnackBar(editTextCode, message);


                        }
                    }
                });
    }

    private void verifyNum(String Mobile) {

        String url = Config.Num_Vrify;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Mobile", Mobile);
            jsonObject.put("Name", mFullName);
            jsonObject.put("EmailID", mEmailAddress);


        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response.optString("Status").equalsIgnoreCase("True")) {
                    if(mComingFrom.equalsIgnoreCase("FirstTimeActivity")){
                        mSession.setHasFirstTimeOTP(true);
                        launchNextActivity();
                    }else{
                        checkEmail();

                    }
                } else {
                    mApplication.showSnackBar(editTextCode, response.optString("ServiceMSG"));

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(OtpVerificationActivity.this);
        requestQueue.add(jsonObjectRequest);
    }


    private void checkEmail() {
        Intent intent = new Intent(OtpVerificationActivity.this, InitiateEmailVerify.class);
        intent.putExtras(mBundle);
        startActivity(intent);

    }


}

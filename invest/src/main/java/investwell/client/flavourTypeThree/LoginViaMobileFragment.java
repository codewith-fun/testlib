package investwell.client.flavourTypeThree;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.iw.acceleratordemo.R;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import investwell.client.activity.AppApplication;
import investwell.client.activity.LoginActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppSession;
import investwell.utils.PinEntryView;
import investwell.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginViaMobileFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE_SIGN_IN = 0;
    private PinEntryView mEtOTP;
    private EditText mLoginPhone;
    private LinearLayout mllOtpContainer;
    private TextView mTvSendOtp;
    private String mPhoneNumber, verificationId;
    private Button mbtnLogin;
    private AppSession mSession;
    private ProgressDialog mBar;
    private FirebaseAuth mAuth;
    private AppApplication mApplication;
    private LoginActivity loginActivity;
    private Spinner mSpCountryCode;
    private String mCountryCode = "+91";
    private CountryCodePicker ccp;
    private EditText mEtMobile;


    public LoginViaMobileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginActivity=(LoginActivity)context;
        mApplication=(AppApplication)loginActivity.getApplication();
        mSession=AppSession.getInstance(loginActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        final View view = inflater.inflate(R.layout.fragment_login_via_mobile, container, false);
        mAuth = FirebaseAuth.getInstance();
        mLoginPhone = view.findViewById(R.id.et_login_phone);
        mllOtpContainer = view.findViewById(R.id.ll_otp_container);
        mEtOTP = view.findViewById(R.id.etUserId);
        mTvSendOtp = view.findViewById(R.id.tvSendOTP);
        mbtnLogin = view.findViewById(R.id.btnLogin);

        mTvSendOtp.setOnClickListener(this);
        mbtnLogin.setOnClickListener(this);
        view.findViewById(R.id.ivGoogle).setOnClickListener(this);
        view.findViewById(R.id.ivFacebook).setOnClickListener(this);

        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        mEtMobile = view.findViewById(R.id.phone_number_edt);
        ccp.registerPhoneNumberTextView(mEtMobile);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                mCountryCode = "+"+selectedCountry.getPhoneCode();
                //Toast.makeText(SignUpActivity.this, "Updated " + mCountryCode, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.tvSendOTP) {
            mPhoneNumber = mEtMobile.getText().toString();
            mPhoneNumber = mPhoneNumber.replace(" ", "");
            mllOtpContainer.setVisibility(View.VISIBLE);
            mbtnLogin.setVisibility(View.VISIBLE);
            mTvSendOtp.setText("Resend OTP");
            sendVerificationCode(mCountryCode + mPhoneNumber);
        } else if (id == R.id.btnLogin) {
            String code = mEtOTP.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        } else if (id == R.id.ivFacebook) {
            loginActivity.getFacebookUserDetails();
        } else if (id == R.id.ivGoogle) {
            loginActivity.connectToGmail();
        }

    }


    private void setCountryCode() {
        String[] stringArray = Utils.countryCode;

        ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, stringArray);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCountryCode.setAdapter(spinner_value);
        mSpCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCountryCode = mSpCountryCode.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void verifyCode(String code) {
        mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (mBar!=null)
                            mBar.dismiss();
                        if (task.isSuccessful()) {
                            loginActivity.socialLogin(mPhoneNumber);
                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }




                });
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        Utils.hideKeyboard(getActivity());
        mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(loginActivity)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            if (mBar!=null)
                mBar.dismiss();
            verificationId = s;
            DialogsUtils.hideProgressBar();

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            if (mBar!=null)
                mBar.dismiss();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                mEtOTP.setText(code);
                verifyCode(code);
            } else {
                DialogsUtils.hideProgressBar();
                //DoREgistered();
                loginActivity.socialLogin(mPhoneNumber);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            if (mBar!=null)
                mBar.dismiss();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


}



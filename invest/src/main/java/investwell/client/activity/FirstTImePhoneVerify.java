package investwell.client.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.iw.acceleratordemo.R;

import investwell.utils.AppSession;

public class FirstTImePhoneVerify extends AppCompatActivity  {
    private final static int RESOLVE_HINT = 1011;
    String mobNumber;
    private TextInputEditText mEtPhone;
    private TextInputLayout tilPhone;
    private Button btn_verify_phn;
    private AppSession mSession;
    private AppApplication mApplication;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           tilPhone.setError("");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_t_ime_phone_verify);
        //requestUserPhoneNumber();
        initializer();
        setListener();
    }

    private void initializer() {
        mApplication = (AppApplication) getApplication();
        mSession = AppSession.getInstance(FirstTImePhoneVerify.this);
        mEtPhone = findViewById(R.id.et_first_phone);
        tilPhone = findViewById(R.id.til_first_phone);
        btn_verify_phn = findViewById(R.id.btn_verify_phn);
    }

    private void setListener() {
        mEtPhone.addTextChangedListener(textWatcher);
        btn_verify_phn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidatePhone()) {
                    Intent intent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("coming_from", "FirstTimeActivity");
                    bundle.putString("phone", mEtPhone.getText().toString().trim());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean isValidatePhone() {
        if (TextUtils.isEmpty(mEtPhone.getText())) {
            tilPhone.setError("Please enter your phone number");
            return false;
        } else if (mEtPhone.getText().length() < 10) {
            tilPhone.setError(getResources().getString(R.string.signup_error_mobile_no_length));
            return false;
        } else {
            return true;

        }
    }






}
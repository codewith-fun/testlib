package investwell.client.fragment.user_info;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextInputLayout;
import investwell.utils.edittext.MaskedEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class FragPersonalForm3 extends Fragment implements View.OnClickListener {
    CustomTextInputEditText mEtNomineeName, mEtNomineeRelation, mEtNomineeAddress, mEtNomineeCity;
    CustomTextInputLayout tilNomineeName, tilNomineeRelation, tilNomineeAddress, tilNomineeCity;
    MaskedEditText mEtNomineeDob;
    private Bundle mBundle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private boolean mIsRightDateFormate = false;
    private ToolbarFragment fragToolBar;
    private CardView cvNomineeAddress, cvNomineeCity, cvNomineeDob;
    private AppSession appSession;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_personal_form3, container, false);
        mBundle = getArguments();
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        appSession = AppSession.getInstance(mActivity);
        setUpToolBar();
        setInitializer(view);
        setListener(view);


        return view;
    }

    private TextWatcher allTextFieldTextWatcher = new TextWatcher() {
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
    private TextWatcher birthDateTextWatcher = new TextWatcher() {
        int beforeTextChangedLength;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int length = editable.length();
            // text is being removed

            if (beforeTextChangedLength > length) return;

            String str = editable.toString();


            if (str.length() == 10) {
                if (validateDateFormat(str)) {
                    mIsRightDateFormate = true;
                } else {
                    mIsRightDateFormate = false;

                    mEtNomineeDob.setError(getResources().getString(R.string.personal_form_error_invalid_date));
                    mEtNomineeDob.requestFocus();
                }
            } else {
                mIsRightDateFormate = false;
                //  mEtDateOfBirth.setError("Please enter date of birth in form DD-MM-YYYY");
            }

        }
    };

    private void clearErrorMessage() {
        tilNomineeCity.setError("");
        tilNomineeRelation.setError("");
        tilNomineeName.setError("");
        tilNomineeAddress.setError("");
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_nominee_details_form), true, false, false, false, false, false, false, "");
        }
    }

    private void setListener(View view) {
        view.findViewById(R.id.btn_continue_nse_fatca).setOnClickListener(this);
        view.findViewById(R.id.btn_previous_nse_fatca).setOnClickListener(this);
        //  mEtNomineeName.addTextChangedListener(allTextFieldTextWatcher);
        //  mEtNomineeRelation.addTextChangedListener(allTextFieldTextWatcher);
        //  mEtNomineeAddress.addTextChangedListener(allTextFieldTextWatcher);
        //   mEtNomineeCity.addTextChangedListener(allTextFieldTextWatcher);
        //  mEtNomineeAddress.addTextChangedListener(birthDateTextWatcher);
    }

    private void setInitializer(View view) {
        mEtNomineeName = view.findViewById(R.id.et_nominee_name);
        mEtNomineeRelation = view.findViewById(R.id.et_nominee_relation);
        mEtNomineeDob = view.findViewById(R.id.et_nominee_dob);
        mEtNomineeCity = view.findViewById(R.id.et_nominee_city);
        mEtNomineeAddress = view.findViewById(R.id.et_nominee_address);
        tilNomineeName = view.findViewById(R.id.til_nominee_name);
        tilNomineeRelation = view.findViewById(R.id.til_nominee_relation_name);
        tilNomineeAddress = view.findViewById(R.id.til_nominee_address);
        tilNomineeCity = view.findViewById(R.id.til_nominee_city);
        cvNomineeAddress = view.findViewById(R.id.cv_nominee_address);
        cvNomineeCity = view.findViewById(R.id.cv_nominee_city);
        cvNomineeDob = view.findViewById(R.id.cv_nominee_dob);


        if (appSession.getAppType().equals("N") || appSession.getAppType().equals("DN")) {

            cvNomineeAddress.setVisibility(View.VISIBLE);
            cvNomineeCity.setVisibility(View.VISIBLE);
            cvNomineeDob.setVisibility(View.VISIBLE);
        } else {
            cvNomineeAddress.setVisibility(View.GONE);
            cvNomineeCity.setVisibility(View.GONE);
            cvNomineeDob.setVisibility(View.GONE);

        }
    }

    public boolean validateDateFormat(String dateToValdate) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);
        Date parsedDate = null;
        boolean isValid = false;
        try {
            parsedDate = formatter.parse(dateToValdate);
            Date today = new Date();
            if (parsedDate.before(today)) {
                isValid = true;
            } else {
                isValid = false;
            }

        } catch (ParseException e) {
            //Handle exception
            isValid = false;
        }
        return isValid;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue_nse_fatca) {
            goToNext();
        } else if (id == R.id.btn_previous_nse_fatca) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

    private void goToNext() {
        if (appSession.getAppType().equals("N") || appSession.getAppType().equals("DN")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.requireNonNull(mEtNomineeName.getText()).toString().equals("")) {
                    tilNomineeName.setError(getResources().getString(R.string.nominnee_name_error));
                } else if (Objects.requireNonNull(mEtNomineeRelation.getText()).toString().equals("")) {
                    tilNomineeRelation.setError(getResources().getString(R.string.nominnee_rel_error));
                } else if (Objects.requireNonNull(mEtNomineeAddress.getText()).toString().equals("")) {
                    tilNomineeAddress.setError(getResources().getString(R.string.nominee_add_error));
                } else if (Objects.requireNonNull(mEtNomineeCity.getText()).toString().equals("")) {
                    tilNomineeCity.setError(getResources().getString(R.string.nominee_city_error));
                } else if (Objects.requireNonNull(mEtNomineeDob.getText().toString().equals(""))) {
                    mEtNomineeDob.requestFocus();
                    mEtNomineeDob.setError("");
                } else {
                    mBundle.putString("nominee_relation_value", mEtNomineeRelation.getText().toString());
                    mBundle.putString("nominee_name_value", mEtNomineeName.getText().toString());
                    mBundle.putString("NomineeDob", Objects.requireNonNull(mEtNomineeDob.getText()).toString());
                    mBundle.putString("NomineeAddress", mEtNomineeAddress.getText().toString());
                    mBundle.putString("NomineeCity", mEtNomineeCity.getText().toString());
                    mActivity.displayViewOther(8, mBundle);

                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.requireNonNull(mEtNomineeName.getText()).toString().equals("")) {
                    tilNomineeName.setError(getResources().getString(R.string.nominnee_name_error));
                } else if (Objects.requireNonNull(mEtNomineeRelation.getText()).toString().equals("")) {
                    tilNomineeRelation.setError(getResources().getString(R.string.nominnee_rel_error));
                } else {
                    mBundle.putString("nominee_relation_value", mEtNomineeRelation.getText().toString());
                    mBundle.putString("nominee_name_value", mEtNomineeName.getText().toString());
                    mBundle.putString("NomineeDob", Objects.requireNonNull(mEtNomineeDob.getText()).toString());
                    mBundle.putString("NomineeAddress", Objects.requireNonNull(mEtNomineeAddress.getText()).toString());
                    mBundle.putString("NomineeCity", Objects.requireNonNull(mEtNomineeCity.getText()).toString());
                    mActivity.displayViewOther(8, mBundle);

                }
            }
        }
    }
}

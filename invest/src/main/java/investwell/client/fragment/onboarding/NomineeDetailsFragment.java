package investwell.client.fragment.onboarding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iw.acceleratordemo.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.edittext.MaskedEditText;

public class NomineeDetailsFragment extends Fragment implements View.OnClickListener {
    private Bundle mBundle;
    private MainActivity mActivity;
    private AppSession mSession;
    private ToolbarFragment fragToolBar;
    private View nomineeDetailsView;
    private View contentSteppers;
    //Stepper Views
    private TextView mTvStepOne, mTvStepTwo, mTvStepThree, mTvStepFour;
    private FloatingActionButton mFabStepOne, mFabStepTwo, mFabStepThree, mFabStepFour, mFabStepFive;
    //On boarding Views
    private MaskedEditText mEtNomineeDob, dateofBirthMinor;
    private EditText mEtNomineeName, mEtNomineeRelation, mEtNomineeAddress, mEtNomineeCity, et_nominee_minor_gardain;
    private TextView mTvErrNomineeName, mTvErrNomineeRelation, mTvErrNomineeAddress, mTvErrNomineeCity, mTvErrDob, tv_err_minor_nom_gardian, tv_error_minor_dob;
    private Button mBtnContinue, mBtnPrevious;
    private AppCompatCheckBox cbIsMinor;
    private LinearLayoutCompat llMinorInside;
    boolean mIsMinorRightDateFormate = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        nomineeDetailsView = inflater.inflate(R.layout.fragment_nominee_details, container, false);
        initializer();
        setUpToolBar();
        showStepper();
        initialChecks(nomineeDetailsView);
        setListener(nomineeDetailsView);
        return nomineeDetailsView;
    }

    private void initializer() {
        mBundle = getArguments();
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        contentSteppers = nomineeDetailsView.findViewById(R.id.content_onboarding_steppers);
        mFabStepOne = contentSteppers.findViewById(R.id.fab_step_1);
        mFabStepTwo = contentSteppers.findViewById(R.id.fab_step_2);
        mFabStepThree = contentSteppers.findViewById(R.id.fab_step_3);
        mFabStepFour = contentSteppers.findViewById(R.id.fab_step_4);
        mFabStepFive = contentSteppers.findViewById(R.id.fab_step_5);
        mTvStepOne = contentSteppers.findViewById(R.id.tv_horizontal_1);
        mTvStepTwo = contentSteppers.findViewById(R.id.tv_horizontal_2);
        mTvStepThree = contentSteppers.findViewById(R.id.tv_horizontal_3);
        mTvStepFour = contentSteppers.findViewById(R.id.tv_horizontal_4);
        View contentNomDetails = nomineeDetailsView.findViewById(R.id.content_onboarding_nominee_details);
        mEtNomineeName = contentNomDetails.findViewById(R.id.et_nominee_name);
        mEtNomineeRelation = contentNomDetails.findViewById(R.id.et_nominee_relation);
        mEtNomineeAddress = contentNomDetails.findViewById(R.id.et_nominee_address);
        mEtNomineeCity = contentNomDetails.findViewById(R.id.et_nominee_city);
        mEtNomineeDob = contentNomDetails.findViewById(R.id.et_nominee_dob);
        mTvErrNomineeName = contentNomDetails.findViewById(R.id.tv_err_nominee_name);
        mTvErrNomineeRelation = contentNomDetails.findViewById(R.id.tv_err_nom_rel);
        mTvErrNomineeAddress = contentNomDetails.findViewById(R.id.tv_err_nom_address);
        mTvErrNomineeCity = contentNomDetails.findViewById(R.id.tv_err_nom_city);
        mTvErrDob = contentNomDetails.findViewById(R.id.tv_err_nominee_dob);
        mBtnContinue = contentNomDetails.findViewById(R.id.btn_continue_nse_fatca);
        mBtnPrevious = contentNomDetails.findViewById(R.id.btn_previous_nse_fatca);
        cbIsMinor = contentNomDetails.findViewById(R.id.cbIsMinor);
        llMinorInside = contentNomDetails.findViewById(R.id.llMinorInside);
        et_nominee_minor_gardain = contentNomDetails.findViewById(R.id.et_nominee_minor_gardain);
        tv_err_minor_nom_gardian = contentNomDetails.findViewById(R.id.tv_err_minor_nom_gardian);
        dateofBirthMinor = contentNomDetails.findViewById(R.id.dateofBirthMinor);
        tv_error_minor_dob = contentNomDetails.findViewById(R.id.tv_error_minor_dob);
    }

    private void setUpToolBar() {
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_nominee_details_form), true, false, false, false, false, false, false, "");
        }
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
            clearErrorMessage();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int length = editable.length();
            // text is being removed

            if (beforeTextChangedLength > length) return;

            String str = editable.toString();


            boolean mIsRightDateFormate = false;

                if (mEtNomineeDob.getText().toString().length() == 10) {

                        mIsRightDateFormate = true;




                } else {
                    mIsRightDateFormate = false;
                    mEtNomineeDob.setError(getResources().getString(R.string.personal_form_error_invalid_date));
                    mEtNomineeDob.requestFocus();
                    //  mEtDateOfBirth.setError("Please enter date of birth in form DD-MM-YYYY");
                }
            }

    };

    private TextWatcher MinorbirthDateTextWatcher = new TextWatcher() {
        int beforeTextChangedLength;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //clearErrorMessage();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int length = editable.length();
            // text is being removed

            if (beforeTextChangedLength > length) return;
            String str = editable.toString();

            if (str.length() == 10) {
                if (validateDateFormatMinor(str)) {
                    mIsMinorRightDateFormate = true;
                    tv_error_minor_dob.setText("");
                    tv_error_minor_dob.setVisibility(View.INVISIBLE);
                } else {
                    mIsMinorRightDateFormate = false;
                    tv_error_minor_dob.setText("Minor DOB should be less than 18 years");
                    tv_error_minor_dob.setVisibility(View.VISIBLE);
                }
            } else {
                mIsMinorRightDateFormate = false;
                tv_error_minor_dob.setText("Enter DOB in DD/MM/YYYY");
                tv_error_minor_dob.setVisibility(View.VISIBLE);
            }
        }
    };


    public boolean validateDateFormat(String dateToValdate) {
String finalDate=dateToValdate.replaceAll("-","/");
        DateFormat srcDf = new SimpleDateFormat("dd/MM/yyyy");
        boolean isValid = false;
        // parse the date string into Date object
        try {
            Date dateObj = srcDf.parse(finalDate);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            formatter.setLenient(false);
            finalDate=formatter.format(dateObj);
            Date parsedDate = null;

            parsedDate =formatter.parse(finalDate);
            Date today = new Date();
            if (parsedDate.before(today)) {
                isValid = true;
            } else {
                isValid = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }


    public boolean validateDateFormatMinor(String dateToValdate) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false);
        Date parsedDate = null;
        boolean isValid = false;
        try {
            parsedDate = formatter.parse(dateToValdate);
            Date today = new Date();
            if (parsedDate.before(today) && getAge(dateToValdate) <= 18) {
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

    private int getAge(String dobString) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    //Show Personal Details  Stepper
    private void showStepper() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            mFabStepOne.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepOne.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv1 = contentSteppers.findViewById(R.id.tv_step_1);
            tv1.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepTwo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepTwo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv2 = contentSteppers.findViewById(R.id.tv_step_2);
            tv2.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));


            mFabStepThree.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepThree.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv3 = contentSteppers.findViewById(R.id.tv_step_3);
            tv3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
        } else {
            mFabStepOne.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepOne.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv1 = contentSteppers.findViewById(R.id.tv_step_1);
            tv1.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepTwo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepTwo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv2 = contentSteppers.findViewById(R.id.tv_step_2);
            tv2.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));


            mFabStepThree.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepThree.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv3 = contentSteppers.findViewById(R.id.tv_step_3);
            tv3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
        }

    }

    private void clearErrorMessage() {
        mTvErrNomineeName.setText("");
        mTvErrNomineeRelation.setText("");
        mTvErrNomineeCity.setText("");
        mTvErrNomineeAddress.setText("");
        tv_err_minor_nom_gardian.setText("");
        tv_error_minor_dob.setText("");
        mTvErrNomineeName.setVisibility(View.INVISIBLE);
        mTvErrNomineeRelation.setVisibility(View.INVISIBLE);
        mTvErrNomineeCity.setVisibility(View.INVISIBLE);
        mTvErrNomineeAddress.setVisibility(View.INVISIBLE);
        tv_err_minor_nom_gardian.setVisibility(View.INVISIBLE);
        tv_error_minor_dob.setVisibility(View.INVISIBLE);
    }

    private void setListener(View view) {
        mBtnContinue.setOnClickListener(this);
        mBtnPrevious.setOnClickListener(this);
        cbIsMinor.setOnClickListener(this);
        mEtNomineeDob.addTextChangedListener(birthDateTextWatcher);
        dateofBirthMinor.addTextChangedListener(MinorbirthDateTextWatcher);
        mEtNomineeName.addTextChangedListener(allTextFieldTextWatcher);
        mEtNomineeRelation.addTextChangedListener(allTextFieldTextWatcher);
        mEtNomineeAddress.addTextChangedListener(allTextFieldTextWatcher);
        mEtNomineeCity.addTextChangedListener(allTextFieldTextWatcher);
        et_nominee_minor_gardain.addTextChangedListener(allTextFieldTextWatcher);

    }


    private void initialChecks(View view) {
        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {

            mEtNomineeAddress.setVisibility(View.VISIBLE);
            mEtNomineeCity.setVisibility(View.VISIBLE);
            mEtNomineeDob.setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_label_address).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_label_city).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_label_dob).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_dob).setVisibility(View.VISIBLE);
            view.findViewById(R.id.llMinor).setVisibility(View.GONE);
            mTvErrDob.setVisibility(View.INVISIBLE);
            mTvErrNomineeCity.setVisibility(View.INVISIBLE);
            mTvErrNomineeAddress.setVisibility(View.INVISIBLE);

        } else {
/*            mEtNomineeAddress.setVisibility(View.GONE);
            mEtNomineeCity.setVisibility(View.GONE);
            mEtNomineeDob.setVisibility(View.GONE);
            view.findViewById(R.id.tv_label_address).setVisibility(View.GONE);
            view.findViewById(R.id.tv_label_city).setVisibility(View.GONE);
            view.findViewById(R.id.tv_label_dob).setVisibility(View.GONE);
            view.findViewById(R.id.v_dob).setVisibility(View.GONE);
            view.findViewById(R.id.llMinor).setVisibility(View.VISIBLE);
            mTvErrDob.setVisibility(View.GONE);
            mTvErrNomineeCity.setVisibility(View.GONE);
            mTvErrNomineeAddress.setVisibility(View.GONE);*/
            mEtNomineeAddress.setVisibility(View.VISIBLE);
            mEtNomineeCity.setVisibility(View.VISIBLE);
            mEtNomineeDob.setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_label_address).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_label_city).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_label_dob).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_dob).setVisibility(View.VISIBLE);
            view.findViewById(R.id.llMinor).setVisibility(View.GONE);
            mTvErrDob.setVisibility(View.INVISIBLE);
            mTvErrNomineeCity.setVisibility(View.INVISIBLE);
            mTvErrNomineeAddress.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue_nse_fatca) {
            goToNext();
        } else if (id == R.id.btn_previous_nse_fatca) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.cbIsMinor) {
            if (cbIsMinor.isChecked()) {
                llMinorInside.setVisibility(View.VISIBLE);
            } else {
                llMinorInside.setVisibility(View.GONE);
            }
        }
    }

    private void setNameVisibility() {
        mTvErrNomineeName.setText(getResources().getString(R.string.nominnee_name_error));
        mTvErrNomineeName.setVisibility(View.VISIBLE);
        mTvErrNomineeRelation.setVisibility(View.INVISIBLE);
        mTvErrNomineeCity.setVisibility(View.INVISIBLE);
        mTvErrNomineeAddress.setVisibility(View.INVISIBLE);
        tv_err_minor_nom_gardian.setVisibility(View.INVISIBLE);
        tv_error_minor_dob.setVisibility(View.INVISIBLE);
    }

    private void setRelationVisibility() {
        mTvErrNomineeRelation.setText(getResources().getString(R.string.nominnee_rel_error));
        mTvErrNomineeRelation.setVisibility(View.VISIBLE);
        mTvErrNomineeName.setVisibility(View.INVISIBLE);
        mTvErrNomineeCity.setVisibility(View.INVISIBLE);
        mTvErrNomineeAddress.setVisibility(View.INVISIBLE);
        tv_err_minor_nom_gardian.setVisibility(View.INVISIBLE);
        tv_error_minor_dob.setVisibility(View.INVISIBLE);
    }

    private void setAddressVisibility() {
        mTvErrNomineeAddress.setText(getResources().getString(R.string.nominee_add_error));
        mTvErrNomineeAddress.setVisibility(View.VISIBLE);
        mTvErrNomineeName.setVisibility(View.INVISIBLE);
        mTvErrNomineeCity.setVisibility(View.INVISIBLE);
        mTvErrNomineeRelation.setVisibility(View.INVISIBLE);
        tv_err_minor_nom_gardian.setVisibility(View.INVISIBLE);
        tv_error_minor_dob.setVisibility(View.INVISIBLE);
    }

    private void setCityVisibility() {
        mTvErrNomineeCity.setText(getResources().getString(R.string.nominee_city_error));
        mTvErrNomineeCity.setVisibility(View.VISIBLE);
        mTvErrNomineeName.setVisibility(View.INVISIBLE);
        mTvErrNomineeAddress.setVisibility(View.INVISIBLE);
        mTvErrNomineeRelation.setVisibility(View.INVISIBLE);
        tv_err_minor_nom_gardian.setVisibility(View.INVISIBLE);
        tv_error_minor_dob.setVisibility(View.INVISIBLE);
    }

    private void setMinorGaurdianNameVisibility() {
        tv_err_minor_nom_gardian.setText(getResources().getString(R.string.nominee_minor_gurdian_name_error));
        tv_err_minor_nom_gardian.setVisibility(View.VISIBLE);
        mTvErrNomineeName.setVisibility(View.INVISIBLE);
        mTvErrNomineeAddress.setVisibility(View.INVISIBLE);
        mTvErrNomineeRelation.setVisibility(View.INVISIBLE);
        tv_error_minor_dob.setVisibility(View.INVISIBLE);
    }

    private void goToNext() {
        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.requireNonNull(mEtNomineeName.getText()).toString().equals("")) {
                    setNameVisibility();
                } else if (Objects.requireNonNull(mEtNomineeRelation.getText()).toString().equals("")) {
                    setRelationVisibility();
                } else if (Objects.requireNonNull(mEtNomineeAddress.getText()).toString().equals("")) {
                    setAddressVisibility();
                } else if (Objects.requireNonNull(mEtNomineeCity.getText()).toString().equals("")) {
                    setCityVisibility();
                } else if (Objects.requireNonNull(mEtNomineeDob.getText()).toString().equals("")) {
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
                String minorDateOfBirth = dateofBirthMinor.getText().toString();
                if (Objects.requireNonNull(mEtNomineeName.getText()).toString().equals("")) {
                    setNameVisibility();
                } else if (Objects.requireNonNull(mEtNomineeRelation.getText()).toString().equals("")) {
                    setRelationVisibility();
                } else if (cbIsMinor.isChecked() && Objects.requireNonNull(et_nominee_minor_gardain.getText()).toString().equals("")) {
                    setMinorGaurdianNameVisibility();
                } else if (cbIsMinor.isChecked() && (minorDateOfBirth.equals("") || minorDateOfBirth.equals("DD-MM-YYYY"))) {
                    tv_error_minor_dob.setVisibility(View.VISIBLE);
                    tv_error_minor_dob.setError(getResources().getString(R.string.personal_form_error_invalid_date));
                    tv_error_minor_dob.requestFocus();
                } else {

                    if (cbIsMinor.isChecked() && mIsMinorRightDateFormate) {
                        mBundle.putString("nominee_relation_value", mEtNomineeRelation.getText().toString());
                        mBundle.putString("nominee_name_value", mEtNomineeName.getText().toString());
                        mBundle.putString("NomineeDob", Objects.requireNonNull(mEtNomineeDob.getText()).toString());
                        mBundle.putString("NomineeAddress", Objects.requireNonNull(mEtNomineeAddress.getText()).toString());
                        mBundle.putString("NomineeCity", Objects.requireNonNull(mEtNomineeCity.getText()).toString());
                        mBundle.putString("isMinor", "true");
                        mBundle.putString("nominee_minor_gaurdain", et_nominee_minor_gardain.getText().toString());
                        mBundle.putString("nominee_minorBOD", minorDateOfBirth);

                        mActivity.displayViewOther(8, mBundle);
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
}

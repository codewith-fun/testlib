package investwell.client.fragment.onboarding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.edittext.MaskedEditText;

public class PersonalDetailsFragment extends Fragment implements View.OnClickListener {
    private boolean mIsRightDateFormate = false;
    private String[] code, particulars, StateCode, StateName;

    private MainActivity mActivity;
    private AppApplication mApplication;
    private Bundle mBundle, newBundle;
    private AppSession mSession;
    private View personalDetailsView;
    private ToolbarFragment fragToolBar;
    private View contentSteppers;
    //Stepper Views
    private TextView mTvStepOne, mTvStepTwo, mTvStepThree, mTvStepFour;
    private FloatingActionButton mFabStepOne, mFabStepTwo, mFabStepThree, mFabStepFour, mFabStepFive;
    //On boarding Views
    private EditText mEtAddress, mEtStreet, mEtCity, mEtPin;
    private MaskedEditText mEtDateOfBirth;
    private Spinner occupation_spinner, state_spinner;
    private TextView mTvErrAddress, mTvErrStreet, mTvErrCity, mTvErrPin, mTvErrDob;
    private Button mBtnContinue, mBtnPrevious;

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
        personalDetailsView = inflater.inflate(R.layout.fragment_personal_details, container, false);
        initializer();
        setUpToolBar();
        showStepper();
        setStateData();
        setOccupation();
        setListener();
        return personalDetailsView;
    }

    private void setListener() {
        state_spinner.setOnItemSelectedListener(mStateItemSelectedListener);
        occupation_spinner.setOnItemSelectedListener(mOccupationItemSelectedListener);
        mEtDateOfBirth.addTextChangedListener(dateTextWatcher);
        mBtnPrevious.setOnClickListener(this);
        mBtnContinue.setOnClickListener(this);
    }

    private void initializer() {
        mBundle = getArguments();
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        contentSteppers = personalDetailsView.findViewById(R.id.content_onboarding_steppers);
        mFabStepOne = contentSteppers.findViewById(R.id.fab_step_1);
        mFabStepTwo = contentSteppers.findViewById(R.id.fab_step_2);
        mFabStepThree = contentSteppers.findViewById(R.id.fab_step_3);
        mFabStepFour = contentSteppers.findViewById(R.id.fab_step_4);
        mFabStepFive = contentSteppers.findViewById(R.id.fab_step_5);
        mTvStepOne = contentSteppers.findViewById(R.id.tv_horizontal_1);
        mTvStepTwo = contentSteppers.findViewById(R.id.tv_horizontal_2);
        mTvStepThree = contentSteppers.findViewById(R.id.tv_horizontal_3);
        mTvStepFour = contentSteppers.findViewById(R.id.tv_horizontal_4);
        View contentPersonalDetails = personalDetailsView.findViewById(R.id.content_onboarding_personal_detail_form);
        mEtAddress = contentPersonalDetails.findViewById(R.id.et_address);
        mEtStreet = contentPersonalDetails.findViewById(R.id.et_street);
        mEtCity = contentPersonalDetails.findViewById(R.id.et_city);
        mEtPin = contentPersonalDetails.findViewById(R.id.et_pin);
        mEtDateOfBirth = contentPersonalDetails.findViewById(R.id.dateofBirth);
        occupation_spinner = contentPersonalDetails.findViewById(R.id.occupation_spinner);
        state_spinner = contentPersonalDetails.findViewById(R.id.state_spinner);
        mTvErrAddress = contentPersonalDetails.findViewById(R.id.tv_error_address);
        mTvErrStreet = contentPersonalDetails.findViewById(R.id.tv_error_street);
        mTvErrCity = contentPersonalDetails.findViewById(R.id.tv_error_city);
        mTvErrPin = contentPersonalDetails.findViewById(R.id.tv_error_pin);
        mTvErrDob = contentPersonalDetails.findViewById(R.id.tv_error_dob);
        mBtnContinue = contentPersonalDetails.findViewById(R.id.btn_continue_nse_fatca);
        mBtnPrevious = contentPersonalDetails.findViewById(R.id.btn_previous_nse_fatca);
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_personal_details_form), true, false, false, false, false, false, false, "");
        }
    }

    private TextWatcher dateTextWatcher = new TextWatcher() {
        int beforeTextChangedLength;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            beforeTextChangedLength = charSequence.length();
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

            if (mEtDateOfBirth.getText().toString().length() == 10 && validateDateFormat(mEtDateOfBirth.getText().toString().trim())) {
                mIsRightDateFormate = true;
                mTvErrDob.setText("");
                mTvErrDob.setVisibility(View.INVISIBLE);
            } else {
                mIsRightDateFormate = false;
                   mTvErrDob.setText("Enter DOB in DD-MM-YYYY");
                mTvErrDob.setVisibility(View.VISIBLE);
                //  mEtDateOfBirth.setError("Please enter date of birth in form DD-MM-YYYY");
            }
        }

    };
    private AdapterView.OnItemSelectedListener mStateItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String state_code = StateCode[i];
            mBundle.putString("state_code", state_code);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private AdapterView.OnItemSelectedListener mOccupationItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String occupation_code = code[i];
            mBundle.putString("occupation_code", occupation_code);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void clearErrorMessage() {
        mTvErrAddress.setText("");
        mTvErrCity.setText("");
        mTvErrPin.setText("");
        mTvErrDob.setText("");
        mTvErrStreet.setText("");
        mTvErrAddress.setVisibility(View.INVISIBLE);
        mTvErrCity.setVisibility(View.INVISIBLE);
        mTvErrPin.setVisibility(View.INVISIBLE);
        mTvErrDob.setVisibility(View.INVISIBLE);
        mTvErrStreet.setVisibility(View.INVISIBLE);
    }

    public boolean validateDateFormat(String dateToValdate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
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

            mFabStepTwo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepTwo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv2 = contentSteppers.findViewById(R.id.tv_step_2);
            tv2.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));


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

    //Function to set states Data
    private void setStateData() {

        try {
            JSONObject jsonObject = new JSONObject(Config.STATE);
            String BSEStateListResult = jsonObject.getString("BSEStateListResult");
            JSONArray jsonArray = new JSONArray(BSEStateListResult);

            StateCode = new String[jsonArray.length()];
            StateName = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject state_value = jsonArray.getJSONObject(i);
                StateCode[i] = state_value.getString("CODE");
                StateName[i] = state_value.getString("STATE");
            }

            ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, StateName);
            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            state_spinner.setAdapter(spinner_value);

            state_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Function to set Occupation Data
    private void setOccupation() {
        String url = Config.Occupation;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("OccupationDetail");
                        code = new String[jsonArray.length()];
                        particulars = new String[jsonArray.length()];
                        for (int i = 0; i <= jsonArray.length(); i++) {
                            JSONObject ocupation_value = jsonArray.getJSONObject(i);
                            code[i] = ocupation_value.getString("Code");
                            particulars[i] = ocupation_value.getString("Particulars");

                            ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, particulars);
                            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            occupation_spinner.setAdapter(spinner_value);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue_nse_fatca) {
            goToNext();
        } else if (id == R.id.btn_previous_nse_fatca) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

    private void onAddressValidate() {
        mTvErrAddress.setText(getResources().getString(R.string.personal_details_error_empty_address));
        mTvErrAddress.setVisibility(View.VISIBLE);
        mTvErrCity.setVisibility(View.INVISIBLE);
        mTvErrPin.setVisibility(View.INVISIBLE);
        mTvErrDob.setVisibility(View.INVISIBLE);
        mTvErrStreet.setVisibility(View.INVISIBLE);
    }

    private void onStreetValidate() {
        mTvErrStreet.setText(getResources().getString(R.string.personal_details_error_empty_street_name));
        mTvErrAddress.setVisibility(View.INVISIBLE);
        mTvErrCity.setVisibility(View.INVISIBLE);
        mTvErrPin.setVisibility(View.INVISIBLE);
        mTvErrDob.setVisibility(View.INVISIBLE);
        mTvErrStreet.setVisibility(View.VISIBLE);
    }

    private void onCityValidate() {
        mTvErrCity.setText(getResources().getString(R.string.personal_details_error_empty_city));
        mTvErrAddress.setVisibility(View.INVISIBLE);
        mTvErrCity.setVisibility(View.VISIBLE);
        mTvErrPin.setVisibility(View.INVISIBLE);
        mTvErrDob.setVisibility(View.INVISIBLE);
        mTvErrStreet.setVisibility(View.INVISIBLE);
    }

    private void onPinValidate() {
        mTvErrPin.setText(getResources().getString(R.string.personal_details_error_empty_pin));
        mTvErrAddress.setVisibility(View.INVISIBLE);
        mTvErrCity.setVisibility(View.INVISIBLE);
        mTvErrPin.setVisibility(View.VISIBLE);
        mTvErrDob.setVisibility(View.INVISIBLE);
        mTvErrStreet.setVisibility(View.INVISIBLE);
    }

    private void onInvalidPinValidate() {
        mTvErrPin.setText(getResources().getString(R.string.personal_details_error_invalid_pin));
        mTvErrAddress.setVisibility(View.INVISIBLE);
        mTvErrCity.setVisibility(View.INVISIBLE);
        mTvErrPin.setVisibility(View.VISIBLE);
        mTvErrDob.setVisibility(View.INVISIBLE);
        mTvErrStreet.setVisibility(View.INVISIBLE);
    }

    private void goToNext() {
        if (mEtAddress.getText().toString().equals("")) {
            onAddressValidate();
        } else if (mEtStreet.getText().toString().equals("")) {
            onStreetValidate();
        } else if (mEtCity.getText().toString().equals("")) {
            onCityValidate();
        } else if (mEtPin.getText().toString().equals("")) {
            onPinValidate();
        } else if (mEtPin.getText().toString().length() < 6) {
            onInvalidPinValidate();
        } else {
            if(mIsRightDateFormate) {
                mBundle.putString("address_one_value", mEtAddress.getText().toString());
                mBundle.putString("address_second_value", mEtStreet.getText().toString());
                mBundle.putString("city_value", mEtCity.getText().toString());
                mBundle.putString("pin_value", mEtPin.getText().toString());
                mBundle.putString("dateofbirth_value", mEtDateOfBirth.getText().toString());
                mActivity.displayViewOther(7, mBundle);
            }
        }
    }
}

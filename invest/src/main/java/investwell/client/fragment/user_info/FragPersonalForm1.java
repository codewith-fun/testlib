package investwell.client.fragment.user_info;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import investwell.client.activity.AccountConfActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.KycActivity;
import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.client.activity.UserTypesActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.DeviceUtils;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextInputLayout;


public class FragPersonalForm1 extends Fragment implements View.OnClickListener {
    private Spinner status_spinner, occupation_spinner, state_spinner, process_spinner;
    private String[] code, particulars, StateCode, StateName;
    private CardView panCard;
    private EditText mEtDateOfBirth;
    private CustomTextInputEditText mEtReferalCode, etPanNumber, mEtName, mEtMobile, mEtGuardianPan, mEtGuardName, mEtEmail;
    private CustomTextInputEditText mEtHouseNo, mEtStreet, mEtCity, mEtPin;
    ;
    private Bundle mBundle,newBundle;
    private AppSession mSession;
    TextView TvVerify;
    private String mEmail = "", mUserType = "01", radiobtn_value = "M", mStatusType = "";
    private MainActivity mActivity;
    private AppApplication mApplication;
    private boolean mIsPANVerifyed = false, mIsGardianPANVerifyed = false;
    private String mType = "", mAlreadyUser = "";
    private boolean mIsRightDateFormate = false;
    private CustomButton continuebtn;
    private int count = 0;
    private ProgressDialog mBar;
    private CustomTextInputLayout tilName, tilGuardianName, tilEmail, tilMobile, tilPan, tilReferal, tilGuardianPan, tilDob;
    private ToolbarFragment fragToolBar;
    String mPanErrorMessage = "";
    private String appAddress = "", appAddressGoogleLink = "", email = "", landLine = "", phnNo = "", aboutUs = "", callBack = "";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private CardView cvGuardianPan, cvGuardianName, cvProcess, cvEmail;
    private double mLat, mLong;
    GoogleApiClient googleApiClient;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;

        }
    }
    /*@Override
    public void onResume(){
        super.onResume();
        switchOnGPS();

    }*/

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_personal_form1, container, false);
        mSession = AppSession.getInstance(mActivity);

        //  switchOnGPS();
        mBundle = new Bundle();
        newBundle = getArguments();
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        setUpToolBar();
        setUpVisibility();
        checkBundle();
        setInitializer(view);
        setListener();
        initialChecks();
        setTaxStatus();
        setOccupation();
        FCMToken();

        occupation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String occupation_code = code[i];
                mBundle.putString("occupation_code", occupation_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() >= 3) {
                    etPanNumber.setEnabled(true);

                } else {
                    etPanNumber.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setStateData();

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String state_code = StateCode[i];
                mBundle.putString("state_code", state_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private TextWatcher pantextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() <= 1) {
                // tilPan.setError("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() > 9) {
                verifyPAN(editable.toString(), "self");
            } else {
                mIsPANVerifyed = false;
            }
        }
    };
    private TextWatcher guardPanTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            clearErrorMessage();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mEtGuardianPan.getText().toString().length() == 10) {
                verifyPAN(mEtGuardianPan.getText().toString(), "minor");
            } else {
                mIsGardianPANVerifyed = false;
                cvGuardianName.setVisibility(View.GONE);
            }
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
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
    private TextWatcher referaltextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            clearErrorMessage();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            tilReferal.setError(" ");
            if (mEtReferalCode.getText().toString().length() >= 3) {
                TvVerify.setVisibility(View.VISIBLE);
            } else {
                TvVerify.setVisibility(View.GONE);

            }

            if (mEtReferalCode.getText().toString().isEmpty()) {
                continuebtn.setVisibility(View.VISIBLE);
                count = 0;
                continuebtn.setEnabled(true);
            }
        }
    };
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

            if (str.length() == 10) {
                if (validateDateFormat(str)) {
                    mIsRightDateFormate = true;
                } else {
                    mIsRightDateFormate = false;

                    mEtDateOfBirth.setError("Enter DOB in DD-MM-YYYY");

                }
            } else {
                mIsRightDateFormate = false;
                //  mEtDateOfBirth.setError("Please enter date of birth in form DD-MM-YYYY");
            }
        }

    };

    private void setInitializer(View view) {

        continuebtn = view.findViewById(R.id.btn_continue);
        status_spinner = view.findViewById(R.id.et_spinner);
        state_spinner = view.findViewById(R.id.state_spinner);
        process_spinner = view.findViewById(R.id.et_process_spinner);
        tilPan = view.findViewById(R.id.til_pan);
        tilReferal = view.findViewById(R.id.til_referal);
        tilName = view.findViewById(R.id.til_name);
        tilGuardianPan = view.findViewById(R.id.til_guardian_pan);
        tilEmail = view.findViewById(R.id.til_mail);
        tilMobile = view.findViewById(R.id.til_mobile);
        tilGuardianName = view.findViewById(R.id.til_guardian_name);

        occupation_spinner = view.findViewById(R.id.occupation_spinner);

        cvGuardianPan = view.findViewById(R.id.cv_guardian_pan);
        cvGuardianName = view.findViewById(R.id.cv_guardian_name);
        cvProcess = view.findViewById(R.id.cv_process);
        cvEmail = view.findViewById(R.id.cv_email);
        mEtEmail = view.findViewById(R.id.et_mail);
        TvVerify = view.findViewById(R.id.tvverify);
        etPanNumber = view.findViewById(R.id.et_pan);

        mEtDateOfBirth = view.findViewById(R.id.dateofBirth);
        mEtMobile = view.findViewById(R.id.et_mobile);
        mEtGuardianPan = view.findViewById(R.id.et_guardian_pan);
        mEtName = view.findViewById(R.id.et_name);
        mEtGuardName = view.findViewById(R.id.et_guardian_name);
        mEtReferalCode = view.findViewById(R.id.et_referal);
        TextView mCondition = view.findViewById(R.id.condition);
        panCard = view.findViewById(R.id.panCard);

        mEtHouseNo = view.findViewById(R.id.et_address);
        mEtStreet = view.findViewById(R.id.et_street);
        mEtCity = view.findViewById(R.id.et_city);
        mEtPin = view.findViewById(R.id.et_pin);


        /*mEtDob = view.findViewById(R.id.et_dob);*/

        if (mSession.getUCC_CODE().isEmpty() || mSession.getUCC_CODE().equalsIgnoreCase("NA")) {
            mCondition.setText(getString(R.string.conditionNoUcc));
        } else {
            mCondition.setText(getString(R.string.conditionUcc));
        }
       // askLocationPermiSsion();
    }

    private void setListener() {
        continuebtn.setOnClickListener(this);
        TvVerify.setOnClickListener(this);
        mEtName.addTextChangedListener(textWatcher);
        mEtEmail.addTextChangedListener(textWatcher);
        mEtMobile.addTextChangedListener(textWatcher);
        etPanNumber.addTextChangedListener(pantextWatcher);
        mEtGuardianPan.addTextChangedListener(guardPanTextWatcher);
        mEtReferalCode.addTextChangedListener(referaltextWatcher);
        mEtDateOfBirth.addTextChangedListener(dateTextWatcher);

        /*mEtDob.addTextChangedListener(dobTextWatcher);*/
    }


    private void initialChecks() {
        if (mBundle != null && mBundle.containsKey("coming_from")) {
            if (mBundle.getString("coming_from").equalsIgnoreCase("instruction")) {
                mEtName.setText(mSession.getFullName());
                etPanNumber.setEnabled(true);
            } else {
                etPanNumber.setEnabled(false);
            }

        } else if(newBundle!=null && newBundle.containsKey("coming_from")){
            mEtName.setText(mBundle.getString("name"));
            mEtEmail.setText(mBundle.getString("email"));
            mEtMobile.setText(mBundle.getString("mobile"));
            etPanNumber.setText(mBundle.getString("pan"));

        }else {
            etPanNumber.setEnabled(false);
        }




        if (!mIsPANVerifyed && etPanNumber.getText().toString().equals("")) {
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
                } else {

                   /* etPanNumber.setText(mSession.getPAN());
                    mEtName.setText(mSession.getFullName());*/

                }
                cvEmail.setVisibility(View.VISIBLE);
                mEtEmail.setText(mSession.getEmail());
                // mEtName.setText(mSession.getFullName());
                mEtMobile.setText(mSession.getMobileNumber());


            } else {
                etPanNumber.setEnabled(false);

                //showDailog();
            }
        }
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_personal_details_form), true, false, false, false, false, false, false
                    , "");
        }
    }

    private void checkBundle() {
        if (mBundle.containsKey("type")) {
            mType = mBundle.getString("type");
        } else if (mBundle.containsKey("AlreadyUser")) {
            mAlreadyUser = mBundle.getString("AlreadyUser");
        }

    }


    private boolean isValid() {
        if (!isValidName()) {
            return false;
        }/* else if (!isValidGuardianName()) {
            return false;
        } else if (!isValidEmail()) {
            return false;
        } else if (!isValidEmail()) {
            return false;
        }*/ else if (!isValidMobile()) {
            return false;
        } else if (!isValidPan()) {
            return false;
        } /*else if (!isValidGuardianPan()) {
            return false;
        }*/ else if (!isValidDate()) {
            return false;
        } else {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(mActivity.getCurrentFocus()).getWindowToken(), 0);
                }
            }


        }
        return true;
    }

    private boolean isValidGuardianName() {
        if (cvGuardianName.getVisibility() == View.VISIBLE && TextUtils.isEmpty(mEtGuardName.getText().toString())) {
            tilGuardianName.setError(getResources().getString(R.string.person_details_error_guard_name));
            return false;
        }
        return true;
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

    private boolean isValidDate() {
        boolean isValidDate = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String toDate = mEtDateOfBirth.getText().toString();
            Date ageDate = sdf.parse(toDate);
            Date todayDate = new Date();

            long timeDifference = todayDate.getTime() - ageDate.getTime();
            long days = (timeDifference / (60 * 60 * 24 * 1000));
            //  mUserType = "02";
            if (mUserType.equals("02") && (days <= (18 * 365))) {
                isValidDate = true;
            } else if (mUserType.equals("01") || mUserType.equals("24") || mUserType.equals("04")) {
                if ((days > (18 * 365))) {
                    isValidDate = true;
                } else {
                    isValidDate = false;
                }
            } else {
                isValidDate = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            isValidDate = false;
        }
        if (mEtDateOfBirth.getText().toString().equals("")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_date));
            return false;
        } else if (mEtDateOfBirth.getText().toString().length() < 10) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_date));
            return false;
        } else if (!mIsRightDateFormate) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_date));
            return false;
        } else if (!isValidDate && mUserType.equals("02")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_minor_age));
            return false;
        } else if (!isValidDate && mUserType.equals("01")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_age));
            return false;
        } else if (!isValidDate && mUserType.equals("04")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_age));
            return false;
        } else if (!isValidDate && mUserType.equals("24")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_age));
            return false;
        }
        return true;
    }

    private boolean isValidName() {
        if (TextUtils.isEmpty(mEtName.getText().toString())) {
            tilName.setError(getResources().getString(R.string.person_details_error_name));
            return false;
        }
        return true;
    }

    private boolean isValidPan() {
        if (TextUtils.isEmpty(etPanNumber.getText().toString())) {
            tilPan.setError(getResources().getString(R.string.person_details_error_pan));
            return false;
        } else if (etPanNumber.getText().toString().length() < 10) {
            tilPan.setError(getResources().getString(R.string.person_details_error_pan_invalid));
            return false;
        }
        return true;
    }

    private boolean isValidGuardianPan() {
        if (cvGuardianPan.getVisibility() == View.VISIBLE && TextUtils.isEmpty(mEtGuardianPan.getText().toString())) {
            tilGuardianPan.setError(getResources().getString(R.string.person_details_error_guard_pan));
            return false;
        } else if (mEtGuardianPan.getText().toString().length() < 10) {
            tilGuardianPan.setError(getResources().getString(R.string.person_details_error_guard__pan_invalid));
            return false;
        }
        return true;
    }

    private boolean isValidEmail() {
        if (TextUtils.isEmpty(mEtEmail.getText().toString())) {
            tilEmail.setError(getResources().getString(R.string.person_details_error_email));
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText()).matches()) {
            tilEmail.setError(getResources().getString(R.string.person_details_error_email_invalid));
            return false;
        }
        return true;
    }

    private boolean isValidMobile() {
        if (TextUtils.isEmpty(mEtMobile.getText().toString())) {
            tilMobile.setError(getResources().getString(R.string.person_details_error_mobile));
            return false;
        } else if (mEtMobile.getText().toString().length() < 10) {
            tilMobile.setError(getResources().getString(R.string.person_details_error_mobile_invalid));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue) {/* if (cvEmail.getVisibility() == View.VISIBLE) {
                    if (mEtEmail.getText().toString().isEmpty()) {
                        tilEmail.setError("Vacant Field");
                    } else {

                    }
                }*/

            if (TvVerify.getVisibility() == View.VISIBLE && count == 0) {
                continuebtn.setEnabled(false);

                mApplication.showSnackBar(TvVerify, getResources().getString(R.string.personal_form_error_verification_code));
            } else if (mEtHouseNo.getText().toString().equals("")) {

                mApplication.showSnackBar(cvEmail, getResources().getString(R.string.personal_details_error_empty_address));
            } else if (mEtStreet.getText().toString().equals("")) {

                mApplication.showSnackBar(cvEmail, getResources().getString(R.string.personal_details_error_empty_street_name));
            } else if (mEtCity.getText().toString().equals("")) {

                mApplication.showSnackBar(cvEmail, getResources().getString(R.string.personal_details_error_empty_city));
            } else if (mEtPin.getText().toString().equals("")) {

                mApplication.showSnackBar(cvEmail, getResources().getString(R.string.personal_details_error_empty_pin));
            } else if (mEtPin.getText().toString().length() < 6) {

                mApplication.showSnackBar(cvEmail, getResources().getString(R.string.personal_details_error_invalid_pin));
            } else {
                continuebtn.setEnabled(true);
                if (isValid()) {
                    mEmail = mEtEmail.getText().toString();
                    if (Utils.isNetworkConnected(getActivity())) {
                        if (mSession.getHasLoging()) {
                            setData();
                        } else {
                            saveInformation();
                        }
                    } else {
                        mApplication.showSnackBar(view, getResources().getString(R.string.no_internet));

                    }

                }

            }
        } else if (id == R.id.tvverify) {
            count = 1;
            continuebtn.setEnabled(true);
            checkrfrlcode();
        }
    }

    public void checkrfrlcode() {

        mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.Refrl_Code;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("SBCode", mEtReferalCode.getText().toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            tilReferal.setError("");
                            continuebtn.setVisibility(View.VISIBLE);

                        } else {

                            continuebtn.setVisibility(View.INVISIBLE);
                        }
                        tilReferal.setError("(" + jsonObject.optString("ServiceMSG") + ")");

                    } catch (Exception e) {
                        e.printStackTrace();
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

    private void setTaxStatus() {
        String[] status = {"Individual"};
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, status);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(spinner_value);

        status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = status_spinner.getSelectedItem().toString();

                if (mUserType.equals("02")) {
                    mEtDateOfBirth.setText("");
                }

                cvGuardianPan.setVisibility(View.GONE);
                cvGuardianName.setVisibility(View.GONE);

                if (text.equalsIgnoreCase("Individual")) {
                    mUserType = "01";
                } else if (text.equalsIgnoreCase("On Behalf Of Minor")) {
                    cvGuardianPan.setVisibility(View.VISIBLE);
                    cvGuardianName.setVisibility(View.VISIBLE);
                    mUserType = "02";
                } else if (text.equalsIgnoreCase("NRI - NRO")) {
                    mUserType = "24";
                } else if (text.equalsIgnoreCase("Company")) {
                    mUserType = "04";
                }
                mBundle.putString("tax_status", String.valueOf(mUserType));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


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


    private void goToNext() {
        boolean isValidDate = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String toDate = mEtDateOfBirth.getText().toString();
            Date ageDate = sdf.parse(toDate);
            Date todayDate = new Date();

            long timeDifference = todayDate.getTime() - ageDate.getTime();
            long days = (timeDifference / (60 * 60 * 24 * 1000));
            //  mUserType = "02";
            if (mUserType.equals("02") && (days <= (18 * 365))) {
                isValidDate = true;
            } else if (mUserType.equals("01") || mUserType.equals("24") || mUserType.equals("04")) {
                if ((days > (18 * 365))) {
                    isValidDate = true;
                } else {
                    isValidDate = false;
                }
            } else {
                isValidDate = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            isValidDate = false;
        }

        if (etPanNumber.getText().toString().equals("")) {
            etPanNumber.requestFocus();
            etPanNumber.setError(getResources().getString(R.string.personal_details_error_pan_card));
        } else if (etPanNumber.length() < 10) {
            etPanNumber.requestFocus();
            etPanNumber.setError(getResources().getString(R.string.personal_details_error_invalid_pan_card));
        } else if (cvGuardianPan.getVisibility() == View.VISIBLE && (mEtGuardianPan.getText().toString().equals(""))) {
            tilGuardianPan.setError(getResources().getString(R.string.personal_details_error_guardian_pan));
            mEtGuardianPan.requestFocus();
        } else if (cvGuardianPan.getVisibility() == View.VISIBLE && mEtGuardianPan.getText().toString().length() < 10) {
            tilGuardianPan.setError(getResources().getString(R.string.personal_details_error_invalid_guardian_pan));
            mEtGuardianPan.requestFocus();
        } else if (mEtMobile.getText().toString().equals("")) {
            mEtMobile.setError(getResources().getString(R.string.personal_details_error_empty_mobile));
            mEtMobile.requestFocus();
        } else if (mEtMobile.getText().toString().length() < 10) {
            mEtMobile.setError(getResources().getString(R.string.personal_details_invalid_mobile));
            mEtMobile.requestFocus();
        } else if (mEtDateOfBirth.getText().toString().equals("")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_date));
            mEtDateOfBirth.requestFocus();
        } else if (mEtDateOfBirth.getText().toString().length() < 10) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_date));
            mEtDateOfBirth.requestFocus();
        } else if (!mIsRightDateFormate) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_date));
            mEtDateOfBirth.requestFocus();
        } else if (!isValidDate && mUserType.equals("02")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_minor_age));
            mEtDateOfBirth.requestFocus();
        } else if (!isValidDate && mUserType.equals("01")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_age));
            mEtDateOfBirth.requestFocus();
        } else if (!isValidDate && mUserType.equals("04")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_age));
            mEtDateOfBirth.requestFocus();
        } else if (!isValidDate && mUserType.equals("24")) {
            mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_age));
            mEtDateOfBirth.requestFocus();
        } else if (mEtName.getText().toString().equals("")) {
            tilName.setError(getResources().getString(R.string.personal_details_error_empty_username));
            mEtName.requestFocus();
        } else {

            if (mSession.getHasLoging()) {
                setData();
            } else {
                saveInformation();
            }

        }
    }


    private void saveInformation() {

        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.SAVE_BASIC_DETAILS;
        Map<String, String> params = new HashMap<String, String>();

        try {
            params.put("EmailID", mEmail);
            params.put("Bid", AppConstants.APP_BID);
            params.put("Mobile", mEtMobile.getText().toString());
            params.put("Name", mEtName.getText().toString());
            params.put("PAN", etPanNumber.getText().toString());
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
                            DoREgistered();
                        } else {
                            setData();
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

    public void DoREgistered() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://notificationv2.investwell.in/public/user/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mBar.dismiss();
                        setData();
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
                params.put("user_mobile_no", mEtMobile.getText().toString());
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


    private void setData() {

        mBundle.putString("radiobtn_value", "M");
        mBundle.putString("PAN_value", etPanNumber.getText().toString());
        mBundle.putString("name_value", mEtName.getText().toString());
        mBundle.putString("dateofbirth_value", mEtDateOfBirth.getText().toString());
        mBundle.putString("mobile_value", mEtMobile.getText().toString());
        mBundle.putString("email_value", mEmail);
        mBundle.putString("gaurdian_pan_value", mEtGuardianPan.getText().toString());
        mBundle.putString("gaurdian_name_value", mEtGuardName.getText().toString());
        mBundle.putString("refrl_code", mEtReferalCode.getText().toString());


        mBundle.putString("holding_nature_code", "SI");
        mBundle.putString("second_pan_value", "");
        mBundle.putString("second_name_value", "");
        mBundle.putString("address_one_value", mEtHouseNo.getText().toString());
        mBundle.putString("address_second_value", mEtStreet.getText().toString());
        // mBundle.putString("address_third_value", address_third_value);
        mBundle.putString("city_value", mEtCity.getText().toString());
        mBundle.putString("pin_value", mEtPin.getText().toString());
        mBundle.putString("second_pan_value", "");
        mBundle.putString("second_name_vlaue", "");

        mBundle.putString("foreign_address_value", "");
        mBundle.putString("foreign_country_value", "");
        mBundle.putString("foreign_state_value", "");
        mBundle.putString("foreign_city_value", "");
        mBundle.putString("foreign_pin_value", "");


        // showSucessDialog();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CharSequence panError = tilPan.getError();
            if (panError != null) {
                if (tilPan.getError().toString().equalsIgnoreCase("KYC Verified")) {
                    if (mSession.getHasLoging()) {

                       mActivity.displayViewOther(7, mBundle);
                    } else {
                        showSucessDialog();
                    }
                } else {
                    if (Utils.getConfigData(mSession).optString("VideoKYCRequired").equalsIgnoreCase("Y")) {
                        showKYCDialog();
                    } else {
                        if (mSession.getHasLoging()) {
                            mActivity.displayViewOther(7, mBundle);
                        } else {
                            showSucessDialog();
                        }
                    }
                }
            } else {
                showKYCDialog();
            }

        }

    }

    private void showSucessDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.important_txt))
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.personal_details_success_registration_desc))
                .setPositiveButton(getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        mActivity.displayViewOther(7, mBundle);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showKYCDialog() {

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
                    mActivity.displayViewOther(7, mBundle);
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
                VideoKyc();
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

    private void verifyPAN(String panNumber, final String type) {
        tilPan.setError("");
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.PAN_VERIFICATION;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("PAN", panNumber);
            if (Config.COMMON_URL.equalsIgnoreCase("https://nativeapi.my-portfolio.in")) {
                jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            }else{
                jsonObject.put(AppConstants.PASSKEY, "jdfjdf7474jcfjh");
            }
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("ReferCode", mEtReferalCode.getText().toString());
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("applicant_name", mEtName.getText().toString());
            jsonObject.put("location_cord", mLat + "," + mLong);
            jsonObject.put("location_details", mEtHouseNo.getText().toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();


                    if (jsonObject.optString("KYCResponseMSG").equals("KYC Verified")) {
                        // mEtName.setText(jsonObject.optString("Name"));
                        tilPan.setError(jsonObject.optString("ServiceMSG"));
                        tilPan.setErrorTextColor(ColorStateList.valueOf(Color.GREEN));

                    }

                    if (jsonObject.optString("KYCResponseMSG").contains("new credentials")) {

                        Intent intent = new Intent(getActivity(), AccountConfActivity.class);
                        intent.putExtra("message", jsonObject.optString("KYCResponseMSG"));
                        startActivity(intent);
                    }

                    if (jsonObject.optString("KYCResponseMSG").contains("Name & Email mismatched")) {
                        continuebtn.setEnabled(false);
                        continuebtn.setBackgroundResource(R.drawable.rounded_red);
                    } else {
                        continuebtn.setEnabled(true);
                        continuebtn.setBackgroundResource(R.drawable.btn_bg_primary);
                    }
                    if (jsonObject.optString("Status").equals("True") && jsonObject.optString("KYCResponseMSG").contains("KYC Verified")) {
                        if (type.equals("minor")) {
                            tilGuardianPan.setError(jsonObject.optString("ServiceMSG"));
                            cvGuardianName.setVisibility(View.VISIBLE);
                            mEtGuardName.setText(jsonObject.optString("Name"));
                            mIsGardianPANVerifyed = true;

                        } else {

                            mIsPANVerifyed = true;

                            //Toast.makeText(mActivity, "Server error, Please try again later", Toast.LENGTH_SHORT).show();

                        }
                    } else if (jsonObject.optString("KYCResponseMSG").contains("IIN is ACTIVE")) {
                        String IIN = jsonObject.optString("IIN");
                        alertdialog(IIN, 100, jsonObject.optString("KYCResponseMSG"));


                    } else if (jsonObject.optString("KYCResponseMSG").contains("IIN is INACTIVE")) {
                        String IIN = jsonObject.optString("IIN");
                        alertdialog(IIN, 200, jsonObject.optString("KYCResponseMSG"));

                    } else {
                        if (type.equals("minor")) {
                            tilGuardianPan.setError(jsonObject.optString("ServiceMSG"));
                            cvGuardianName.setVisibility(View.GONE);
                            mIsGardianPANVerifyed = false;
                        } else {
                            tilPan.setError(jsonObject.optString("ServiceMSG"));
                            tilPan.setErrorTextColor(ColorStateList.valueOf(Color.RED));

                            mIsPANVerifyed = false;
                        }


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
                            //  Toast.makeText(mActivity, mDataList.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError) {
                        // Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    } else {
                        if (type.equals("minor")) {
                            cvGuardianName.setVisibility(View.GONE);
                            mIsGardianPANVerifyed = false;
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
                cvGuardianName.setVisibility(View.GONE);
                mIsGardianPANVerifyed = false;
            } else {
                mIsPANVerifyed = false;
            }
        }


    }

    public void alertdialog(final String value, final int code, String message) {

        getActivity().getWindow().setSoftInputMode(
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
                    mBundle.putString("comingFrom","Form1");
                    mActivity.displayViewOther(95, mBundle);
                    etPanNumber.setText("");
                } else {
                    startActivity(new Intent(getActivity(), AccountConfActivity.class));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private void verifyEmail(String email, final Dialog dialog, final CoordinatorLayout coordinatorLayout) {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        String url = Config.EMAIL_VERIFICATION;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("EmailId", email);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {


                        if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("EmailID Available")) {
                            dialog.dismiss();
                        } else {

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.WHITE).setAction("Already registered. Please Login", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(mActivity, LoginActivity.class));
                                            //Toast.makeText(mActivity, "Go Losgin screen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    snackbar.setDuration(5000);
                                    snackbar.show();
                                }
                            });

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
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
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

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void showDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.email_layout, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        mSession.setCID("");

        final CoordinatorLayout coordinatorLayout = dialogView.findViewById(R.id.coordinatorLayout);
        final EditText email_edit = dialogView.findViewById(R.id.et_mail);
        investwell.utils.customView.CustomButton cancel_btn = dialogView.findViewById(R.id.cancel_btn);
        investwell.utils.customView.CustomButton verify_btn = dialogView.findViewById(R.id.verify_btn);
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_value = email_edit.getText().toString().trim();
                if (email_value.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                    Toast.makeText(mActivity, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                } else {
                    mEmail = email_value;
                    if (!mSession.getHasLoging()) {
                        verifyEmail(email_value, alertDialog, coordinatorLayout);
                    } else {
                        alertDialog.dismiss();
                    }
                }

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mType.equals("fcm_registration")) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(mActivity, UserTypesActivity.class);
                    startActivity(intent);
                    mActivity.finish();
                } else if (mType.equals("comming_from_login")) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    intent.putExtra(AppConstants.LOGIN_COME_FROM, "Dashboard");
                    startActivity(intent);
                    mActivity.finish();
                } else {
                    alertDialog.dismiss();
                    mActivity.removeAllStack();
                    mActivity.displayViewOther(1, null);
                }


            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

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

/*    public static class InputStreamToStringExample {

        public static void main(String[] args) throws IOException {

            // intilize an InputStream
            InputStream is =
                    new ByteArrayInputStream("file content..blah blah".getBytes());

            String result = getStringFromInputStream(is);

            System.out.println(result);
            System.out.println("Done");

        }

        // convert InputStream to String
        private static String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }

    }*/


    private void clearErrorMessage() {
        tilGuardianName.setError("");
        tilEmail.setError("");
        tilMobile.setError("");
        tilGuardianPan.setError("");
        tilName.setError("");
        tilReferal.setError("");
        /*tilDob.setError("");*/
        /*     mEtDateOfBirth.setError(null);*/
    }

    private void VideoKyc() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int mSec = calendar.get(Calendar.MILLISECOND);
        String url = Config.Video_KYC;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Mobile", mEtMobile.getText().toString());
            jsonObject.put("Name", mEtName.getText().toString());
            jsonObject.put("Email", mEmail);
            jsonObject.put("PAN", etPanNumber.getText().toString());
            jsonObject.put("KYCUserName", mEtName.getText().toString().replace(" ", "").toLowerCase() + mSec);
            jsonObject.put("ClientID", mSession.getCID());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        mBar.dismiss();
                        Intent intent = new Intent(mActivity, KycActivity.class);
                        intent.putExtra("title", "Video KYC");
                        intent.putExtra("video_kyc_url", response.optString("ServiceMSG"));
                        startActivity(intent);
                        mActivity.finish();
                    } else {
                        mBar.dismiss();
                        Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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





    private void askLocationPermiSsion() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            } else {
                getUserAddress();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserAddress();
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        }
    }

    private void getUserAddress() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Location currentLocation = location;
                    mLat = currentLocation.getLatitude();
                    mLong = currentLocation.getLongitude();
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        System.out.println("*****&&" + addresses);
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();

                        mEtHouseNo.setText(address);
                        mEtCity.setText(city);
                        mEtPin.setText(postalCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

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
}




package investwell.client.fragment.onboarding;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class FatcaDetailFormFragment extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack {
    private int mSelectedInComeSlab = 0;
    String[] mIncomeSlabArray = {"Below 1 Lakh", "> 1 <=5 Lacs", ">5 <=10 Lacs", ">10 <= 25 Lacs", "> 25 Lacs < = 1 Crore", "Above 1 Crore"};
    String[] mIncomeSlabCodeArray = {"31", "32", "33", "34", "35", "36"};

    String[] CountryCode;
    String[] particular;
    String[] address_type;
    String[] wealth_score;
    String[] political_exposed;
    String address_type_code, India_Value;
    String wealth_score_code;
    String fatca_dob, Email;
    String birth_country_spinner_code = "";
    String politically_exposed_code;
    String place_of_birth_value;
    private CustomDialog customDialog;
    private Bundle mBundle;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private View fatcaView;
    private View contentSteppers;
    //Stepper Views
    private TextView mTvStepOne, mTvStepTwo, mTvStepThree, mTvStepFour;
    private FloatingActionButton mFabStepOne, mFabStepTwo, mFabStepThree, mFabStepFour, mFabStepFive;
    private Spinner address_type_spinner, birth_country_spinner, wealth_source_spinner, mSpIncomeSlab, politically_exposed_spinner;
    private EditText place_of_birth;
    private Button mBtnContinue, mBtnPrevious;
    private ProgressBar pbAccountTypeBar;
    private TextView mTvPobErr;
    private ToolbarFragment fragToolBar;
    private ProgressDialog mBar;

    @Override
    public void onAttach(@NonNull Context context) {
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
        fatcaView = inflater.inflate(R.layout.fragment_fatca_details, container, false);
        initializer();
        setUpToolBar();
        showStepper();
        setFatcaStaticSpinners();
        getDataFromBundle();
        setAddress();
        setIncomeSlab();
        setWealthScore();
        setPoliticalData();
        getCountry();
        setListeners();
        return fatcaView;
    }

    private void setListeners() {
        place_of_birth.addTextChangedListener(textWatcher);
        mBtnContinue.setOnClickListener(this);
        mBtnPrevious.setOnClickListener(this);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mTvPobErr.setText("");
            mTvPobErr.setVisibility(View.INVISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void initializer() {
        mBundle = getArguments();
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        customDialog = new CustomDialog(this);
        contentSteppers = fatcaView.findViewById(R.id.content_onboarding_steppers);
        mFabStepOne = contentSteppers.findViewById(R.id.fab_step_1);
        mFabStepTwo = contentSteppers.findViewById(R.id.fab_step_2);
        mFabStepThree = contentSteppers.findViewById(R.id.fab_step_3);
        mFabStepFour = contentSteppers.findViewById(R.id.fab_step_4);
        mFabStepFive = contentSteppers.findViewById(R.id.fab_step_5);
        mTvStepOne = contentSteppers.findViewById(R.id.tv_horizontal_1);
        mTvStepTwo = contentSteppers.findViewById(R.id.tv_horizontal_2);
        mTvStepThree = contentSteppers.findViewById(R.id.tv_horizontal_3);
        mTvStepFour = contentSteppers.findViewById(R.id.tv_horizontal_4);
        View contentFatca = fatcaView.findViewById(R.id.content_onboarding_fatca_detail);
        address_type_spinner = contentFatca.findViewById(R.id.et_address_spinner);
        birth_country_spinner = contentFatca.findViewById(R.id.et_country_birth_spinner);
        wealth_source_spinner = contentFatca.findViewById(R.id.et_wealth_source_spinner);
        mSpIncomeSlab = contentFatca.findViewById(R.id.spIncomeSlab);
        politically_exposed_spinner = contentFatca.findViewById(R.id.et_politically_exposed_spinner);
        place_of_birth = contentFatca.findViewById(R.id.et_place_of_birth);
        mTvPobErr = contentFatca.findViewById(R.id.tv_err_pob);
        pbAccountTypeBar = contentFatca.findViewById(R.id.pb_cob);
        mBtnContinue = contentFatca.findViewById(R.id.btn_continue_nse_fatca);
        mBtnPrevious = contentFatca.findViewById(R.id.btn_previous_nse_fatca);

    }

    private void setFatcaStaticSpinners() {
        address_type = getResources().getStringArray(R.array.address_type);
        wealth_score = getResources().getStringArray(R.array.wealth_score);
        political_exposed = getResources().getStringArray(R.array.political_exposed);

        address_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        birth_country_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        wealth_source_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        politically_exposed_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);


        ArrayAdapter<? extends String> spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, address_type);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        address_type_spinner.setAdapter(spinner_value);

        ArrayAdapter<? extends String> wealth_source_spinner_spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, wealth_score);
        wealth_source_spinner_spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wealth_source_spinner.setAdapter(wealth_source_spinner_spinner_value);

        final ArrayAdapter<? extends String> political_exposed_spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, political_exposed);
        political_exposed_spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        politically_exposed_spinner.setAdapter(political_exposed_spinner_value);
    }

    private void setUpToolBar() {
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_fatca_details_form), true, false, false, false, false, false, true, "");
            fragToolBar.setCallback(this);
        }
    }

    private void getDataFromBundle() {
        if (mBundle != null) {
            if (mBundle.containsKey("email_value")) {

                Email = mBundle.getString("email_value");
            } else {
                Email = mSession.getEmail();
            }
        }
    }

    private void completeRegistration() {
        String dob_value = mBundle.getString("dateofbirth_value");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("dd/MM/yy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            Date oneWayTripDate = input.parse(dob_value);                 // parse input
            fatca_dob = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        saveInformation();
    }

    //Show Personal Details  Stepper
    private void showStepper() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString(
                        "APPType").equalsIgnoreCase("Type 1D")) {
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

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
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

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
        }

    }

    private void saveInformation() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                imm.hideSoftInputFromWindow(Objects.requireNonNull(mActivity.getCurrentFocus()).getWindowToken(), 0);
            }
        }
        mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Map<String, String> jsonParam = new HashMap<String, String>();

        try {
            String nominee_birth;

            String birth = mBundle.getString("dateofbirth_value").replace("-", "/");
            if (mBundle.getString("NomineeDob").equalsIgnoreCase("DD-MM-YYYY")) {
                nominee_birth = "";
            } else {
                nominee_birth = mBundle.getString("NomineeDob").replace("-", "/");
            }

            jsonParam.put("Holding", mBundle.getString("holding_nature_code"));
            //jsonParam.put("TaxStatus", mBundle.getString("tax_status"));
            jsonParam.put("TaxStatus", "01");
            jsonParam.put("Occupation", mBundle.getString("occupation_code"));
            jsonParam.put("Name", mBundle.getString("name_value"));
            jsonParam.put("DOB", birth);
            jsonParam.put("Gender", mBundle.getString("radiobtn_value"));
            jsonParam.put("PAN", mBundle.getString("PAN_value"));
            jsonParam.put("Address1", mBundle.getString("address_one_value"));
            jsonParam.put("Address2", mBundle.getString("address_second_value"));
            //jsonParam.put("Address3", mBundle.getString("address_third_value"));
            jsonParam.put("City", mBundle.getString("city_value"));
            jsonParam.put("State", mBundle.getString("state_code"));
            jsonParam.put("PinCode", mBundle.getString("pin_value"));
            jsonParam.put("Country", "IND");
            jsonParam.put("Email", Email);
            jsonParam.put("Mobile", mBundle.getString("mobile_value"));
            jsonParam.put("SecondApplicant", mBundle.getString("second_name_vlaue"));
            jsonParam.put("SecondApplicantPAN", mBundle.getString("second_pan_value"));
            jsonParam.put("ThirdApplicant", "");
            jsonParam.put("ThirdApplicantPAN", "");

            if (!mBundle.containsKey("gaurdian_name_value") || mBundle.getString("gaurdian_name_value").equals("null")) {
                jsonParam.put("GuardianName", "");
            } else {
                jsonParam.put("GuardianName", mBundle.getString("gaurdian_name_value"));
            }

            if (!mBundle.containsKey("gaurdian_pan_value") || mBundle.getString("gaurdian_pan_value").equals("null")) {
                jsonParam.put("GuardianPAN", "");
            } else {
                jsonParam.put("GuardianPAN", mBundle.getString("gaurdian_pan_value"));
            }

            jsonParam.put("NomineeName", mBundle.getString("nominee_name_value"));
            jsonParam.put("NomineeRelation", mBundle.getString("nominee_relation_value"));
            jsonParam.put("AcType", mBundle.getString("account_type_code"));
            jsonParam.put("AcNo", mBundle.getString("mEtAccNum"));
            if (mBundle.containsKey("micr"))
                jsonParam.put("MICRNo", mBundle.getString("micr"));
            else
                jsonParam.put("MICRNo", "");
            jsonParam.put("IFSCCode", mBundle.getString("IFSC"));
            jsonParam.put("BankName", mBundle.getString("bank"));
            jsonParam.put("BranchName", mBundle.getString("branch"));
            jsonParam.put("ForeignAddress", mBundle.getString("foreign_address_value"));
            jsonParam.put("ForeignCity", mBundle.getString("foreign_city_value"));
            jsonParam.put("ForeignPinCode", mBundle.getString("foreign_pin_value"));
            jsonParam.put("ForeignState", mBundle.getString("foreign_state_value"));
            jsonParam.put("ForeignCountry", mBundle.getString("foreign_country_value"));
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("FATCACountry", birth_country_spinner_code);
            // jsonParam.put("FATCABirthState", mBundle.getString("state_code"));
            jsonParam.put("FATCABirthState", place_of_birth_value);
            jsonParam.put("FATCAAddressType", address_type_code);
            jsonParam.put("FATCABirthplace", place_of_birth_value);
            jsonParam.put("FATCAWealthsource", wealth_score_code);
            jsonParam.put("FATCAPEP", politically_exposed_code);
            jsonParam.put("FATCADOB", birth);
            jsonParam.put("ClientID", mSession.getCID());
            jsonParam.put("OnlineOption", mSession.getAppType());
            jsonParam.put("ProcessMode", "P");
            jsonParam.put("BankBranchAddress", mBundle.getString("branch_address"));
            jsonParam.put("BankCity", "");
            jsonParam.put("SecondApplicantDOB", "");
            jsonParam.put("ThirdApplicantDOB", "");
            jsonParam.put("NomineeGuardianName1", "");
            jsonParam.put("NomineeGuardianPAN1", "");
            jsonParam.put("NomineeName2", "");
            jsonParam.put("NomineeRelation2", "");
            jsonParam.put("NomineeDOB2", "");
            jsonParam.put("NomineeAddress2", "");
            jsonParam.put("NomineePercentage2", "");
            jsonParam.put("NomineeCity2", "");
            jsonParam.put("NomineeGuardianName2", "");
            jsonParam.put("NomineeGuardianPAN2", "");
            jsonParam.put("NomineeName3", "");
            jsonParam.put("NomineeRelation3", "");
            jsonParam.put("NomineeDOB3", "");
            jsonParam.put("NomineeAddress3", "");
            jsonParam.put("NomineePercentage3", "");
            jsonParam.put("NomineeCity3", "");
            jsonParam.put("NomineeGuardianName3", "");
            jsonParam.put("NomineeGuardianPAN3", "");
            jsonParam.put("GuardianDOB", "");
            jsonParam.put("NumberOfNominee", "1");

            jsonParam.put("NomineeAddress1", mBundle.getString("NomineeAddress"));
            jsonParam.put("NomineeCity1", mBundle.getString("NomineeCity"));
            jsonParam.put("ReferCode", mBundle.getString("refrl_code"));

            // new changes 15 Jun 2021
            if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
                jsonParam.put("NomineePercentage1", "100");
                jsonParam.put("NomineeDOB1", nominee_birth);
            } else {
                jsonParam.put("NewUCCFormat", "Y");
                jsonParam.put("NomineePercentage1", "100");
                if (mBundle.containsKey("isMinor") && mBundle.getString("isMinor").equals("true")) {
                    jsonParam.put("NomineeMinorFlag", "Y");
                    jsonParam.put("NomineeDOB1", mBundle.getString("nominee_minorBOD"));
                    jsonParam.put("NomineeGuardianName1", mBundle.getString("nominee_minor_gaurdain"));
                } else {
                    jsonParam.put("NomineeMinorFlag", "N");
                }
            }

            jsonParam.put("FATCAIncomeSlab", mIncomeSlabCodeArray[mSelectedInComeSlab]);
            //---- adding for NSE Instant activation -----
            mBundle.putString("FATCACountry", birth_country_spinner_code);
            mBundle.putString("FATCABirthState", place_of_birth_value);
            mBundle.putString("FATCAAddressType", address_type_code);
            mBundle.putString("FATCABirthplace", place_of_birth_value);
            mBundle.putString("FATCAWealthsource", wealth_score_code);
            mBundle.putString("FATCAPEP", politically_exposed_code);
            mBundle.putString("FATCAIncomeSlab", mIncomeSlabCodeArray[mSelectedInComeSlab]);

        } catch (Exception e) {
            e.printStackTrace();
        }
      /*  if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            mBar.dismiss();
            mActivity.displayViewOther(120, mBundle);
        } else {*/

        String url = Config.COMPLETE_REGISTRATION;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParam), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        mActivity.removeAllStack();

                        if (mSession.getUCC_CODE().length() == 0 || mSession.getUCC_CODE().equalsIgnoreCase("NA")) {
                            mSession.setUCC_CODE(object.optString("UCC"));
                        }

                        mBundle.putString("AllData", object.toString());
                        mActivity.displayViewOther(95, mBundle);

                       // Toast.makeText(mActivity, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                       // Intent intent = new Intent(mActivity, SignatureActivity.class);
                       // intent.putExtra("ucc_code", object.optString("UCC"));
                       // intent.putExtra("chequeRequired", object.optString("ChequRequired"));
                       // startActivityForResult(intent, 100);

                    } else {
                        String message = object.optString("ServiceMSG");
                        if (message.contains(":")) {
                            String[] errorMessage = message.split(":", 2);
                            Toast.makeText(mActivity, errorMessage[1], Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        }
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
      //}
    }

    private void setFatcadata() {
        mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Country", birth_country_spinner_code);
            jsonObject.put("PAN", mSession.getPAN());
            jsonObject.put("OccupationCode", mSession.getOccupation());
            jsonObject.put("PANKYCRefNo", "");
            jsonObject.put("Name", mSession.getFullName());
            jsonObject.put("DOB", mSession.getDOB());
            jsonObject.put("FatherName", "");
            jsonObject.put("SpouseName", "");
            jsonObject.put("TaxStatus", mSession.gettaxStatus());
            jsonObject.put("AddressType", address_type_code);
            jsonObject.put("BirthPlace", place_of_birth_value);
            jsonObject.put("ResidenceCountry2", "");
            jsonObject.put("TaxPayerIdenType2", "");
            jsonObject.put("IdentityDocType2", "");
            jsonObject.put("ResidenceCountry3", "");
            jsonObject.put("TaxPayerIdenType3", "");
            jsonObject.put("IdentityDocType3", "");
            jsonObject.put("ResidenceCountry4", "");
            jsonObject.put("TaxPayerIdenType4", "");
            jsonObject.put("IdentityDocType4", "");
            jsonObject.put("IdentityDocType4", "");
            jsonObject.put("WealthSource", wealth_score_code);
            jsonObject.put("CorporateSector", "");
            jsonObject.put("IncomeSlab", mIncomeSlabCodeArray[mSelectedInComeSlab]);
            jsonObject.put("NetWorth", "");
            jsonObject.put("DateNetWorth", "");
            jsonObject.put("PolitocallyExposedPerser", politically_exposed_code);
            jsonObject.put("ExemptionCode", "");
            jsonObject.put("FFIDRNFE", "");
            jsonObject.put("GIINNumber", "");
            jsonObject.put("SponsoringEntity", "");
            jsonObject.put("GIIN", "");
            jsonObject.put("GIINExemptionCode", "");
            jsonObject.put("NonFinancialEntityCategory", "");
            jsonObject.put("NonFinancialEntitySubCategory", "");
            jsonObject.put("NatureBusiness", "");
            jsonObject.put("RelatedListedCompany", "");
            jsonObject.put("UBOApplicable", "N");
            jsonObject.put("UBOCount", "");
            jsonObject.put("UBOName", "");
            jsonObject.put("UBOPAN", "");
            jsonObject.put("UBONationality", "");
            jsonObject.put("UBOAddress1", "");
            jsonObject.put("UBOAddress2", "");
            jsonObject.put("UBOAddress3", "");
            jsonObject.put("UBOCity", "");
            jsonObject.put("UBOPIN", "");
            jsonObject.put("UBOStateCode", "");
            jsonObject.put("UBOCountry", "");
            jsonObject.put("UBOAddressType", "");
            jsonObject.put("UBOCountryTaxResidency", "");
            jsonObject.put("UBOTaxIdenNo", "");
            jsonObject.put("UBOIdenDocType", "");
            jsonObject.put("UBOBirthCountry", "");
            jsonObject.put("UBODOB", "");
            jsonObject.put("UBOGender", "");
            jsonObject.put("UBOFatherName", "");
            jsonObject.put("UBOOccupation", "");
            jsonObject.put("UBOOccupationType", "");
            jsonObject.put("UBOTelephone", "");
            jsonObject.put("UBOMobile", "");
            jsonObject.put("UBOCode", "");
            jsonObject.put("UBOHolding", "");
            jsonObject.put("SelfDeclarationFlag", "");
            jsonObject.put("UBODeclarationFlag", "");
            jsonObject.put("AaadharReportingPerson", "");
            jsonObject.put("LogName", "");
            jsonObject.put("ClientUCC", mSession.getUCC_CODE());
            jsonObject.put("Email", Email);


            String url = Config.Fatca_create;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        mActivity.getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mBar.dismiss();
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
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
        }


    }

    private void getCountry() {
        pbAccountTypeBar.setVisibility(View.VISIBLE);

        String url = Config.FATCA_COUNTYR_LIST;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    pbAccountTypeBar.setVisibility(View.GONE);
                    try {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("India");
                        if (jsonObject.optString("Status").equals("True")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("FATCACountryDetail");

                            CountryCode = new String[jsonArray.length()];
                            particular = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject values = jsonArray.getJSONObject(i);
                                CountryCode[i] = values.getString("Code");
                                particular[i] = values.getString("CountryName");
                                list.add(particular[i]);


                                if (particular[i].equalsIgnoreCase("India")) {

                                    India_Value = jsonArray.optJSONObject(i).optString("Code");
                                }

                                ArrayAdapter<? extends String> spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, list);
                                spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                birth_country_spinner.setAdapter(spinner_value);

                            }
                            setCountry(list);

                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbAccountTypeBar.setVisibility(View.GONE);
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

    private void setCountry(final ArrayList<String> list) {
        ArrayAdapter<? extends String> spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, list);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        birth_country_spinner.setAdapter(spinner_value);
        birth_country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (birth_country_spinner.getSelectedItem().toString().equalsIgnoreCase("India")) {

                    birth_country_spinner_code = India_Value;

                } else {
                    birth_country_spinner_code = CountryCode[i - 1];
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setAddress() {
        ArrayAdapter<? extends String> spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, address_type);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        address_type_spinner.setAdapter(spinner_value);

        address_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String address_value = address_type_spinner.getSelectedItem().toString();
                if (address_value.equalsIgnoreCase("Residential or Business")) {
                    address_type_code = "1";
                } else if (address_value.equalsIgnoreCase("Residential")) {
                    address_type_code = "2";
                } else if (address_value.equalsIgnoreCase("Business")) {
                    address_type_code = "3";
                } else if (address_value.equalsIgnoreCase("Registered Office")) {
                    address_type_code = "4";
                } else if (address_value.equalsIgnoreCase("Unspecified")) {
                    address_type_code = "5";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setIncomeSlab() {
        try {
            ArrayAdapter<? extends String> spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, mIncomeSlabArray);
            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpIncomeSlab.setAdapter(spinner_value);
            mSpIncomeSlab.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
            mSpIncomeSlab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mSelectedInComeSlab = mSpIncomeSlab.getSelectedItemPosition();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWealthScore() {
        ArrayAdapter<? extends String> wealth_source_spinner_spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, wealth_score);
        wealth_source_spinner_spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wealth_source_spinner.setAdapter(wealth_source_spinner_spinner_value);

        wealth_source_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String wealth_source_value = wealth_source_spinner.getSelectedItem().toString();
                if (wealth_source_value.equalsIgnoreCase("Salary")) {
                    wealth_score_code = "01";
                } else if (wealth_source_value.equalsIgnoreCase("Business Income")) {
                    wealth_score_code = "02";
                } else if (wealth_source_value.equalsIgnoreCase("Gift")) {
                    wealth_score_code = "03";
                } else if (wealth_source_value.equalsIgnoreCase("Ancestral Property")) {
                    wealth_score_code = "04";
                } else if (wealth_source_value.equalsIgnoreCase("Rental Income")) {
                    wealth_score_code = "05";
                } else if (wealth_source_value.equalsIgnoreCase("Prize Money")) {
                    wealth_score_code = "06";
                } else if (wealth_source_value.equalsIgnoreCase("Royalty")) {
                    wealth_score_code = "07";
                } else if (wealth_source_value.equalsIgnoreCase("Others")) {
                    wealth_score_code = "08";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setPoliticalData() {
        final ArrayAdapter<? extends String> political_exposed_spinner_value = new ArrayAdapter<>(mActivity, R.layout.spinner_item, political_exposed);
        political_exposed_spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        politically_exposed_spinner.setAdapter(political_exposed_spinner_value);

        politically_exposed_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String politically_exposed_value = politically_exposed_spinner.getSelectedItem().toString();
                if (politically_exposed_value.equalsIgnoreCase("Politically Not Exposed Person")) {
                    politically_exposed_code = "N";
                } else if (politically_exposed_value.equalsIgnoreCase("Politically Exposed Person")) {
                    politically_exposed_code = "Y";
                } else if (politically_exposed_value.equalsIgnoreCase("Relative of the Politically Exposed Person")) {
                    politically_exposed_code = "R";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue_nse_fatca) {
            place_of_birth_value = place_of_birth.getText().toString();
            if (place_of_birth_value.isEmpty()) {
                mTvPobErr.setText("Empty Field");
                mTvPobErr.setVisibility(View.VISIBLE);
                place_of_birth.requestFocus();
            } else {
                if (mBundle != null && mBundle.containsKey("comingFrom")) {
                    setFatcadata();
                } else {
                    completeRegistration();
                }
            }
        } else if (id == R.id.btn_previous_nse_fatca) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_fatca_info) {
            showFatcaInfoDialog();
        }
    }

    private void showFatcaInfoDialog() {
        customDialog.showDialog(mActivity, getResources().getString(R.string.fatca_instruction),
                getResources().getString(R.string.nse_fatca_header_desc),
                getResources().getString(R.string.text_ok), "", true, false);
    }

    @Override
    public void onDialogBtnClick(View view) {
        view.getId();//Todo nothing
    }
}

package investwell.client.fragment.user_info;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;

public class NseFatacaFragment extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack {
    String[] CountryCode, CountryPerticulars, line_value, state_code, state_name;
    Spinner mSpIncomeSlab, address_type_spinner, state_spinner, birth_country_spinner, wealth_source_spinner, politically_exposed_spinner, occupation_spinner;
    EditText place_of_birth, mEtDateOfBirth;
    String place_of_birth_value, occupation_code, India_Value;
    String[] address_type = {"Residential", "Residential or Business", "Business", "Registered Office", "Unspecified"};
    String[] wealth_score = {"Salary", "Business Income", "Gift", "Ancestral Property", "Rental Income", "Prize Money", "Royalty", "Others"};
    String[] political_exposed = {"Politically Not Exposed Person", "Politically Exposed Person", "Relative of the Politically Exposed Person"};
    String[] mIncomeSlabArray = {"Below 1 Lakh", "> 1 <=5 Lacs", ">5 <=10 Lacs", ">10 <= 25 Lacs", "> 25 Lacs < = 1 Crore", "Above 1 Crore"};
    String[] mIncomeSlabCodeArray = {"31", "32", "33", "34", "35", "36"};
    private int mSelectedInComeSlab = 0;


    String address_type_code, state_code_value;
    String wealth_score_code, dob_value;
    String fatca_dob;
    String birth_country_spinner_code = "", politically_exposed_code = "", line;
    ArrayList<String> arr = new ArrayList<String>();
    private String[] code, state, particulars, FATCA_Occupation_code, FATCA_particulars;
    private Bundle mBundle;
    private ProgressDialog mBar;
    private AppSession mSession;
    private MainActivity mActivity;
    private View view;
    private ToolbarFragment toolbarFragment;
    private boolean mIsRightDateFormate = false;
    private CustomDialog customDialog;
    private String mUcc, mPAN, mDOB, mName, mEmail;
    private AppApplication mApplication;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_nse_fatca, container, false);
        setInitializer();
        setUpToolBar();
        initialViewSetUp();
        setAddress();
        setWealthScore();
        setOccupation();
        setPoltical();
        setStateData();
        getCountry();
        setIncomeSlab();
        setListener();


        return view;
    }


    private void setInitializer() {

        mBundle = getArguments();

        address_type_spinner = view.findViewById(R.id.et_address_spinner);
        state_spinner = view.findViewById(R.id.state_spinner);
        birth_country_spinner = view.findViewById(R.id.et_birth_country_spinner);
        wealth_source_spinner = view.findViewById(R.id.et_wealth_source_spinner);
        occupation_spinner = view.findViewById(R.id.occupation_spinner);
        mSpIncomeSlab = view.findViewById(R.id.spIncomeSlab);
        place_of_birth = view.findViewById(R.id.place_of_birth);
        politically_exposed_spinner = view.findViewById(R.id.et_politically_exposed_spinner);
        mEtDateOfBirth = view.findViewById(R.id.dateofBirth);
        customDialog = new CustomDialog(this);
        address_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        birth_country_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        wealth_source_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        politically_exposed_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        mSpIncomeSlab.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);

        mEtDateOfBirth.addTextChangedListener(new TextWatcher() {
            int beforeTextChangedLength;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeTextChangedLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                // text is being removed
                if (beforeTextChangedLength > length)
                    return;

                String str = editable.toString();

                if (str.length() == 10) {
                    if (validateDateFormat(str)) {
                        mIsRightDateFormate = true;
                    } else {
                        mIsRightDateFormate = false;

                        mEtDateOfBirth.setError(getResources().getString(R.string.personal_form_error_invalid_date));
                        mEtDateOfBirth.requestFocus();
                    }
                } else {
                    mIsRightDateFormate = false;
                }
            }
        });

        if (mBundle != null && mBundle.containsKey("ucc_code")) {
            mUcc = mBundle.getString("ucc_code");

        } else {
            mUcc = mSession.getUCC_CODE();
        }

        if (mBundle != null && mBundle.containsKey("UserAllData")) {

            try {
                JSONObject jsonObject = new JSONObject(mBundle.getString("UserAllData"));
                mPAN = jsonObject.optString("PAN");
                mDOB = jsonObject.optString("DOB");
                mName = jsonObject.optString("APPNAME1");
                mEmail = jsonObject.optString("EMAIL");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mPAN = mSession.getPAN();
            mDOB = mSession.getDOB();
            mName = mSession.getFullName();
            mEmail = mSession.getEmail();
        }

        if (mDOB.contains("-")) {

            try {
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat inoutFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                mDOB = outputFormat.format(inoutFormat.parse(mDOB));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void setListener() {
        view.findViewById(R.id.btn_submit_nse_fatca).setOnClickListener(this);
    }


    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getResources().getString(R.string.main_nav_title_fatca_creation), true, false, false, false, false, false, true, "");
            toolbarFragment.setCallback(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit_nse_fatca) {
            checkValidation();
        }
    }


    private void checkValidation() {
        boolean isValidDate = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String toDate = mEtDateOfBirth.getText().toString();
            Date ageDate = sdf.parse(toDate);
            Date todayDate = new Date();

            long timeDifference = todayDate.getTime() - ageDate.getTime();
            long days = (timeDifference / (60 * 60 * 24 * 1000));
            if ((days > (18 * 365))) {
                isValidDate = true;
            } else {
                isValidDate = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            isValidDate = false;
        }

        if (mEtDateOfBirth.getText().toString().equals("")) {
            mEtDateOfBirth.setError("Enter DOB in DD/MM/YYYY");
            mEtDateOfBirth.requestFocus();
        } else if (mEtDateOfBirth.getText().toString().length() < 10) {
            mEtDateOfBirth.setError("Enter DOB in DD/MM/YYYY");
            mEtDateOfBirth.requestFocus();
        } else if (!mIsRightDateFormate) {
            mEtDateOfBirth.setError("Enter DOB in DD/MM/YYYY");
            mEtDateOfBirth.requestFocus();
        } else if (!isValidDate) {
            mEtDateOfBirth.setError("Age should be above 18 years");
            mEtDateOfBirth.requestFocus();
        } else if (place_of_birth.getText().toString().isEmpty()) {
            place_of_birth.setError("Empty Field");
            place_of_birth.requestFocus();
        } else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            setFatcadata();
        }

    }

    private void showFatcaInfoDialog() {
        customDialog.showDialog(mActivity, getResources().getString(R.string.fatca_instruction),
                getResources().getString(R.string.nse_fatca_header_desc),
                getResources().getString(R.string.text_ok), "", true, false);
    }

    private void setFatcadata() {

        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Country", birth_country_spinner_code);
            jsonObject.put("OccupationCode", occupation_code);
            jsonObject.put("PAN", mPAN);
            jsonObject.put("Name", mName);
            String dob = String.valueOf(mEtDateOfBirth.getText());
            jsonObject.put("DOB", dob);
            jsonObject.put("FatherName", "");
            jsonObject.put("SpouseName", "");
            jsonObject.put("TaxStatus", "01"); // Only for Individual
            jsonObject.put("AddressType", address_type_code);
            jsonObject.put("BirthPlace", place_of_birth.getText().toString());
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
            jsonObject.put("IncomeSlab", "" + mIncomeSlabCodeArray[mSelectedInComeSlab]);
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
            jsonObject.put("ClientUCC", mUcc);
            jsonObject.put("LogName", mName);
            jsonObject.put("Email", mEmail);


            String url = Config.Fatca_create;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getString(R.string.alert_dialog_order_placed_sucess_txt), response.optString("ServiceMSG"), "message", false, true);
                        mActivity.getSupportFragmentManager().popBackStack();
                    } else {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getString(R.string.alert_dialog_error_txt), response.optString("ServiceMSG"), "message", false, true);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBar.dismiss();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        Toast.makeText(mActivity, "Server Response", Toast.LENGTH_LONG).show();

                    } else
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

    private void initialViewSetUp() {
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, address_type);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        address_type_spinner.setAdapter(spinner_value);

        ArrayAdapter wealth_source_spinner_spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, wealth_score);
        wealth_source_spinner_spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wealth_source_spinner.setAdapter(wealth_source_spinner_spinner_value);

        final ArrayAdapter political_exposed_spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, political_exposed);
        political_exposed_spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        politically_exposed_spinner.setAdapter(political_exposed_spinner_value);

    }

    public boolean validateDateFormat(String dateToValdate) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
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

    public String convertDateFormatForFatca(String dateToValdate) {
        String formatedDate = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = formatter.parse(dateToValdate);
            formatedDate = formatter.format(date);
        } catch (Exception e) {

        }
        return formatedDate;
    }


    private void getCountry() {
        mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        String url = Config.FATCA_COUNTYR_LIST;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        ArrayList<String> list = new ArrayList<String>();
                        list.add("India");
                        if (jsonObject.optString("Status").equals("True")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("FATCACountryDetail");
                            CountryCode = new String[jsonArray.length()];
                            CountryPerticulars = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject values = jsonArray.getJSONObject(i);


                                CountryCode[i] = values.getString("Code");
                                CountryPerticulars[i] = values.getString("CountryName");
                                list.add(CountryPerticulars[i]);
                                if (CountryPerticulars[i].equalsIgnoreCase("India")) {

                                    India_Value = jsonArray.optJSONObject(i).optString("Code");
                                }

                            }
                            setCountry(list);


                        } else {
                            Toast.makeText(mActivity, "Error, Please try again later", Toast.LENGTH_SHORT).show();
                        }
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


    private void setCountry(final ArrayList<String> list) {
        ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, list);
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

                //  Toast.makeText(getActivity(), ""+birth_country_spinner_code, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setOccupation() {
        String url = Config.Occupation_Code;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("OccupationDetail");
                        FATCA_Occupation_code = new String[jsonArray.length()];
                        FATCA_particulars = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ocupation_value = jsonArray.getJSONObject(i);
                            FATCA_Occupation_code[i] = ocupation_value.getString("Code");
                            FATCA_particulars[i] = ocupation_value.getString("Particulars");

                        }

                        ArrayAdapter spinner_value_occupation = new ArrayAdapter(mActivity, R.layout.spinner_item, FATCA_particulars);
                        spinner_value_occupation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        occupation_spinner.setAdapter(spinner_value_occupation);

                        occupation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                occupation_code = FATCA_Occupation_code[i];

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


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


    private void setStateData() {

        try {
            JSONObject jsonObject = new JSONObject(Config.STATE);
            String BSEStateListResult = jsonObject.getString("BSEStateListResult");
            JSONArray jsonArray = new JSONArray(BSEStateListResult);

            code = new String[jsonArray.length()];
            state = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject state_value = jsonArray.getJSONObject(i);
                code[i] = state_value.getString("CODE");
                state[i] = state_value.getString("STATE");
            }

            ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, state);
            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            state_spinner.setAdapter(spinner_value);

            state_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
            state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    state_code_value = code[i];

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setIncomeSlab() {
        try {
            ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, mIncomeSlabArray);
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

    private void setAddress() {
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, address_type);
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

    private void setWealthScore() {
        ArrayAdapter wealth_source_spinner_spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, wealth_score);
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

    private void setPoltical() {
        final ArrayAdapter political_exposed_spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, political_exposed);
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

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_fatca_info) {
            showFatcaInfoDialog();
        }
    }

    @Override
    public void onDialogBtnClick(View view) {
        if (view.getId() == R.id.btDone) {//Todo nothing
        }
    }
}
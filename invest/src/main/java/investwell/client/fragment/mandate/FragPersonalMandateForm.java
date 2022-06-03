package investwell.client.fragment.mandate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragPersonalMandateForm extends Fragment implements View.OnClickListener {

    private Spinner mandate_type_spinner, account_type_spinner;
    private EditText amount, first_name, last_name, ifsc_code, account_number, to_date;
    private String amount_value, mandate_date_value, from_date, first_name_value, last_name_value, ifsc_code_value, account_number_value;
    private TextView bank_name, branch_name, mandate_date, etMicr;
    private String branch, bank, account_code, until_cancel_value = "Y";
    private String[] code, particulars;
    private String[] mandate_array;
    private String mandate_code;
    private CheckBox perpetusal_checkbox;
    private LinearLayout to_date_layout;
    private int notdone;
    private ProgressDialog mBar, mBar2;
    private AppSession mSession;
    private MainActivity mActivity;
    private boolean mIsFirstTime = true;
    private String mUCC_Code = "";
    private AppApplication mApplication;
    private ToolbarFragment fragmentToolBar;
    private static final String DATE_PATTERN =
            "(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)";
    private Pattern pattern;
    private Matcher matcher;
    private String bankName,accountNo,ifscCode;
    private boolean isNse = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_mended_form, container, false);
        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {

            mandate_array = getResources().getStringArray(R.array.NSE_mandate_array);
            view.findViewById(R.id.tvMessageDigital).setVisibility(View.GONE);
            isNse = true;
        } else {
            mandate_array = getResources().getStringArray(R.array.mandate_array);
            view.findViewById(R.id.tvMessageDigital).setVisibility(View.VISIBLE);
        }
        setUpToolBar();

        view.findViewById(R.id.continue_btn).setOnClickListener(this);
        amount = view.findViewById(R.id.amount);
        mandate_date = view.findViewById(R.id.mandate_date);
        first_name = view.findViewById(R.id.first_name);
        last_name = view.findViewById(R.id.last_name);
        ifsc_code = view.findViewById(R.id.et_ifsc_code);
        account_number = view.findViewById(R.id.et_account_number);
        bank_name = view.findViewById(R.id.bank_name);
        branch_name = view.findViewById(R.id.branch_name);
        perpetusal_checkbox = view.findViewById(R.id.perpetusal_checkbox);
        to_date = view.findViewById(R.id.to_date);
        etMicr = view.findViewById(R.id.etMicr);

        mandate_type_spinner = view.findViewById(R.id.mandate_type_spinner);
        account_type_spinner = view.findViewById(R.id.et_spinner);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
        }

        if (bundle != null && bundle.containsKey("IFSCCode")){
            bankName = bundle.getString("BankName");
            accountNo = bundle.getString("AccountNo");
            ifscCode = bundle.getString("IFSCCode");

        }else {
            mUCC_Code = mSession.getUCC_CODE();
        }

        mandate_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        account_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);

        to_date_layout = view.findViewById(R.id.to_date_layout);


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        final Date localDate = new Date();
        mandate_date.setText(formatter.format(localDate));
        mSession = AppSession.getInstance(mActivity);

        perpetusal_checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (perpetusal_checkbox.isChecked()) {
                to_date_layout.setVisibility(View.GONE);
            } else {
                to_date_layout.setVisibility(View.VISIBLE);
            }
        });

        ifsc_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (ifsc_code.getText().toString().length() == 11) {
                    getIFSCode();
                }
            }
        });

        to_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Toast.makeText(getActivity(), "done", Toast.LENGTH_SHORT).show();
                pattern = Pattern.compile(DATE_PATTERN);
                matcher = pattern.matcher(to_date.getText().toString());
                //  matcher = Pattern.compile(DATE_PATTERN).matcher(charSequence);

                if (charSequence.length() == 10 && !to_date.getText().toString().isEmpty()) {

                    String mndateDate = mandate_date.getText().toString().substring(mandate_date.getText().toString().length()-4);
                    String toDate = to_date.getText().toString().substring(to_date.getText().toString().length()-4);
                    if (matcher.matches()&&((Float.parseFloat(toDate))>(Float.parseFloat(mndateDate)))) {
                        view.findViewById(R.id.continue_btn).setEnabled(true);
                    } else{
                        to_date.setError("Invalid Date");
                        view.findViewById(R.id.continue_btn).setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        setMandateTypeSpinner();
            setNseDetails();

            getBseProfileDetail();



        return view;
    }




    private void setUpToolBar() {
        fragmentToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragmentToolBar != null) {
            fragmentToolBar.setUpToolBar(getResources().getString(R.string.mendate_list_btn_autopay_txt), true, false, false, false, false, false, false, "");
        }

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.continue_btn) {
            checkValidation();
            // mActivity.displayViewOther(8, mBundle);
        }
    }

    private void getNseProfileDetail() {
        String url = Config.BANK_DETAIL;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("IFSCCode", "ICIC0006744");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>()  {
                @Override
                public void onResponse(JSONObject response) {
//                    mBar.dismiss();

                    try {
                        if (response != null) {
                            String status = response.optString("Status");
                            Log.i("allbanks", "onResponse: NSE "+response.toString());
                            if (status.equalsIgnoreCase("True")) {

                                JSONObject jsonObject1 = response.getJSONObject("ProfileDetail");
                                ifsc_code.setText(jsonObject1.optString("IFSCCODE1"));
                                bank_name.setText(jsonObject1.optString("BANK1"));
                                String branch = jsonObject1.optString("BANKBRANCH1");
                                branch = URLEncoder.encode(branch, "UTF8");

                                String micr = jsonObject1.optString("MICRNO1");
                                if (micr.length()>0){
                                    etMicr.setText(micr);
                                }


                                branch_name.setText(branch);
                                account_number.setText(jsonObject1.optString("ACCNO1"));
                                first_name.setText(jsonObject1.optString("APPNAME1"));
                                last_name.setText(jsonObject1.optString("APPNAME2"));
                            } else {
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } finally {
                        getBankAccountType(mBar);
                    }

                }
            }, volleyError -> {
                mBar.dismiss();
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject12 = new JSONObject(error.getMessage());
                        Toast.makeText(getActivity(), jsonObject12.toString(), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

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
            if (getActivity() != null) {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(jsonObjectRequest);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNseDetails() {

        getBankAccountType(mBar);

    }

    private void getBseProfileDetail() {
        mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        String url = Config.BSE_PROFILE_DETAILS;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put("OnlineOption", mSession.getAppType());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();

                    try {
                        if (response != null) {
                            String status = response.optString("Status");
                            Log.i("allbanks", "onResponse:info "+response.toString());
                            if (status.equalsIgnoreCase("True")) {

                                JSONObject jsonObject1 = response.getJSONObject("ProfileDetail");
                                if (isNse){
                                    bank_name.setText(bankName);
                                    ifsc_code.setText(ifscCode);
                                    account_number.setText(accountNo);
                                    first_name.setText(jsonObject1.optString("APPNAME1"));
                                    last_name.setText(jsonObject1.optString("APPNAME2"));
                                }else {
                                    ifsc_code.setText(jsonObject1.optString("IFSCCODE1"));
                                    bank_name.setText(jsonObject1.optString("BANK1"));
                                    String branch = jsonObject1.optString("BANKBRANCH1");
                                    branch = URLEncoder.encode(branch, "UTF8");

                                    String micr = jsonObject1.optString("MICRNO1");
                                    if (micr.length()>0){
                                        etMicr.setText(micr);
                                    }


                                    branch_name.setText(branch);
                                    account_number.setText(jsonObject1.optString("ACCNO1"));
                                    first_name.setText(jsonObject1.optString("APPNAME1"));
                                    last_name.setText(jsonObject1.optString("APPNAME2"));
                                }

                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } finally {
                        getBankAccountType(mBar);
                    }

                }
            }, volleyError -> {
                mBar.dismiss();
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject12 = new JSONObject(error.getMessage());
                        Toast.makeText(getActivity(), jsonObject12.toString(), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

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
            if (getActivity() != null) {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(jsonObjectRequest);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void checkValidation() {
        account_number_value = account_number.getText().toString();
        ifsc_code_value = ifsc_code.getText().toString();
        boolean isValidDate = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String toDate = to_date.getText().toString();
            Date strDate = sdf.parse(toDate);


            if (new Date().after(strDate)) {
                isValidDate = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            isValidDate = false;
        }


        if (amount.getText().toString().equals("") || amount.getText().toString().equals("0")) {

            mApplication.showSnackBar(amount, getResources().getString(R.string.mandate_form_error_empty_amount));
        } else if (!perpetusal_checkbox.isChecked() && to_date.getText().toString().equals("")) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.mandate_form_error_empty_date));
        } else if (!perpetusal_checkbox.isChecked() && to_date.getText().toString().length() < 10) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.mandate_form_error_empty_curr_date));
        } else if (!perpetusal_checkbox.isChecked() && isValidDate) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.mandate_form_error_empty_curr_date));
        } else if (first_name.getText().toString().equals("")) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.mandate_form_error_empty_applicant_name));
        } else if (ifsc_code_value.equals("")) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.personal_details_error_empty_ifsc));
        } else if (ifsc_code_value.length() < 11) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.personal_details_error_invalid_ifsc));
        } else if (account_number_value.equals("")) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.error_empty_acc_no));
        } else if (account_number_value.length() < 10) {
            mApplication.showSnackBar(amount, getResources().getString(R.string.personal_details_error_invalid_acount_no));
        } else {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
            goToNext();
        }
    }


    private void goToNext() {
        amount_value = amount.getText().toString();
        first_name_value = first_name.getText().toString();
        last_name_value = last_name.getText().toString();

        if (perpetusal_checkbox.isChecked()) {
            mandate_date_value = "31/12/2099";
            until_cancel_value = "Y";
        } else {
            mandate_date_value = to_date.getText().toString();
            until_cancel_value = "N";
        }
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        from_date = dateFormat.format(cal.getTime()).toString();

        if (!(mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN"))&&
                mandate_code.equalsIgnoreCase("E")) {
            showMandateDailog();
        } else {
            saveOnServer();
        }


    }


    private void saveOnServer() {

        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.CREATE_MENDATE;
        JSONObject jsonParms = new JSONObject();

        try {
            String mandate_date = mandate_date_value.replace("-", "/");
            String from_date2 = from_date.replace("-", "/");

            jsonParms.put("ClientUCC", mUCC_Code);
            jsonParms.put("Bid", AppConstants.APP_BID);
            jsonParms.put("MandateType", mandate_code);
            jsonParms.put("MandateAmount", amount_value);
            jsonParms.put("AccountNo", account_number_value);
            jsonParms.put("Passkey", mSession.getPassKey());
            jsonParms.put("AccountType", account_code);
            jsonParms.put("IFSCCode", ifsc_code_value);
            jsonParms.put("MICRCode", etMicr.getText().toString());
            jsonParms.put("FromDate", from_date2);
            jsonParms.put("ToDate", mandate_date);
            jsonParms.put("FirstName", first_name_value);
            jsonParms.put("SecondName", last_name_value);
            jsonParms.put("ThirdName", "");
            jsonParms.put("BankName", bank_name.getText().toString());
            jsonParms.put("BankBranch", branch_name.getText().toString());
            jsonParms.put("OnlineOption", mSession.getAppType());
            jsonParms.put("UntilCancel", until_cancel_value);
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParms, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        mSession.setHasMendate(true);
                       /* if (notdone == 0) {
                            Bundle bundle = new Bundle();
                            String MandateID = object.optString("MandateID");
                            bundle.putString("MandateID", MandateID);
                            mActivity.displayViewOther(19, bundle);
                            Toast.makeText(mActivity, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(mActivity, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                            mActivity.getSupportFragmentManager().popBackStack();
                        }*/

                       // mApplication.showSnackBar(ifsc_code, object.optString("ServiceMSG"));

                        //mApplication.showCommonDailog(mActivity, getActivity(), false, getString(R.string.message_txt), object.optString("ServiceMSG"), "message", false, true);
                        //mActivity.getSupportFragmentManager().popBackStack();
                        showResultDialog(object.optString("ServiceMSG"));

                    } else {
                        if (object.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", object.optString("ServiceMSG"), "invalidPasskey", false, true);
                        } else {
                            mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", object.optString("ServiceMSG"), "message", false, true);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, volleyError -> {
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
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }


    private void getIFSCode() {
        if (!mIsFirstTime) {
            mBar2 = ProgressDialog.show(mActivity, null, null, true, false);
            mBar2.setContentView(R.layout.progress_piggy);
            mBar2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        mIsFirstTime = false;

        String ifsc_code_value = ifsc_code.getText().toString();
        String url = "https://ifsc.razorpay.com/" + ifsc_code_value;
        StringRequest createOrderRequest = new StringRequest(Request.Method.GET, url,
                result -> {
                    if (mBar2 != null)
                        mBar2.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        bank = jsonObject.getString("BANK");
                        branch = jsonObject.getString("BRANCH");
                        etMicr.setText(jsonObject.getString("MICR"));

                        bank_name.setText(bank);
                        branch_name.setText(branch);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                },
                volleyError -> {
                    if (mBar2 != null)
                        mBar2.dismiss();

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
                });

        createOrderRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(createOrderRequest);
    }


    private void setMandateTypeSpinner() {
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, mandate_array);
        spinner_value.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mandate_type_spinner.setAdapter(spinner_value);
        mandate_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String mandate_value = mandate_type_spinner.getSelectedItem().toString();
               /* if (mandate_value.equalsIgnoreCase("Aadhaar Mandate")) {
                    mandate_code = "E";
                    // showMandateDailog();

                } else */
            /*    if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                    if (mandate_value.equalsIgnoreCase("Paper Mandate")) {
                        notdone = 1;
                        mandate_code = "P";
                    } else if (mandate_value.equalsIgnoreCase("eMandate")){
                        notdone = 1;
                        mandate_code = "E";
                    }
                }else{
                    if (mandate_value.equalsIgnoreCase("Digital Mandate")) {
                        notdone = 1;
                        mandate_code = "I";
                    } else if (mandate_value.equalsIgnoreCase("E-NACH")){
                        notdone = 1;
                        mandate_code = "N";
                    }else {
                        notdone = 1;
                        mandate_code = "X";
                    }
                }*/
                if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                    if (mandate_value.equalsIgnoreCase("eMandate - NetBanking")) {
                        notdone = 1;
                        mandate_code = "Net";
                    } else if (mandate_value.equalsIgnoreCase("eMandate - Debit Card")){
                        notdone = 1;
                        mandate_code = "DC";
                    }else if (mandate_value.equalsIgnoreCase("Paper Mandate")){
                        notdone = 1;
                        mandate_code = "P";
                    }
                }else{
                    if (mandate_value.equalsIgnoreCase("Digital Mandate")) {
                        notdone = 1;
                        mandate_code = "I";
                    } else if (mandate_value.equalsIgnoreCase("E-NACH")){
                        notdone = 1;
                        mandate_code = "N";
                    }else {
                        notdone = 1;
                        mandate_code = "X";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showMandateDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_mandate, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        TextView aadhar_notes = dialogView.findViewById(R.id.aadhar_notes);
        Button readybtn = dialogView.findViewById(R.id.ready_btn);
        Button notreadybtn = dialogView.findViewById(R.id.notready_btn);
        aadhar_notes.setText(R.string.montade_notes);


        readybtn.setOnClickListener(v -> {
            notdone = 0;
            saveOnServer();
            alertDialog.dismiss();

        });

        notreadybtn.setOnClickListener(v -> {

            notdone = 1;
            alertDialog.dismiss();

        });

        alertDialog.setCancelable(false);
        alertDialog.show();

    }


    private void getBankAccountType(final ProgressDialog bar) {
        String url = Config.BANK_ACCOUNT_TYPE;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (bar != null)
                        bar.hide();
                    try {
                        if (jsonObject.optString("Status").equals("True")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("BankTypeDetail");
                            code = new String[jsonArray.length()];
                            particulars = new String[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject values = jsonArray.getJSONObject(i);
                                code[i] = values.getString("Code");
                                particulars[i] = values.getString("Particular");
                            }
                            setAccountType();
                        } else {
                            mApplication.showSnackBar(ifsc_code, getResources().getString(R.string.error_try_again));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, volleyError -> {
                if (bar != null)
                    bar.hide();
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject1 = new JSONObject(error.getMessage());
                        Toast.makeText(mActivity, jsonObject1.toString(), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAccountType() {
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, particulars);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_type_spinner.setAdapter(spinner_value);
        account_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                account_code = code[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void showResultDialog(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.do_later_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView editText = (TextView) dialogView.findViewById(R.id.textMsg);
        editText.setText(msg);
        AlertDialog alertDialog = dialogBuilder.create();
        TextView tvOk = (TextView) dialogView.findViewById(R.id.textOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                mActivity.getSupportFragmentManager().popBackStack();
            }
        });
        alertDialog.show();

    }
}

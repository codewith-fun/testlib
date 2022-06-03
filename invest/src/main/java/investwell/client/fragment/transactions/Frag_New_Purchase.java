package investwell.client.fragment.transactions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.PaymentForAdditionalPurchage;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class Frag_New_Purchase extends Fragment implements View.OnClickListener {

    private RadioGroup mCategoryRadio, mSchemetypeRadio, mRadioGroup1;
    private String[] SchemeName, SchemeCode;
    private String category_value = "E", scheme_type_value = "G", scheme_code_value, netbanking_option = "", payment_mode = "",mCid;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private Bundle bundle;
    private Timer timer = new Timer();
    private final long DELAY = 700;

    private TextView mFolioNumber, service_msg;
    private AppSession mSession;
    private MainActivity mActivity;
private AppApplication mApplication;
    private Spinner mSchemeSpinner, mSpPaymnetType, mspBank, mSpMandate;
    private List<String> new_scheme = new ArrayList<>();
    private List<JSONObject> mBankList, mMendateList;
    private JSONObject mSelectedBankObject, mSelectedMandateObject;
    private LinearLayout mLinerNetbankingView, mLinerRtgs, mLinerCheck, mLinerMandate;
    private EditText mEtRtgs, mEtMicr, mEtBank, mEtBranch, mEtUtr, mEtTransferDate, mEtAmount;
    private EditText mEtChequeAccount, mEtChequeNumber, mEtChequeBank, mEtChequeBranch, mEtChequeMICR, mEtChequeDate;
    private investwell.utils.customView.CustomButton mTransactbtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__new__purchase, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();
        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        mTransactbtn = view.findViewById(R.id.transact_btn);
        mEtAmount = view.findViewById(R.id.amount);
        mSpPaymnetType = view.findViewById(R.id.spPaymentType);
        mspBank = view.findViewById(R.id.spBank);
        mFolioNumber = view.findViewById(R.id.folio_number);
        service_msg = view.findViewById(R.id.service_msg);
        mRadioGroup1 = view.findViewById(R.id.radioGroup1);
        mEtRtgs = view.findViewById(R.id.etRtgs);
        mEtMicr = view.findViewById(R.id.etMicr);
        mEtBank = view.findViewById(R.id.etBank);
        mEtBranch = view.findViewById(R.id.etBranch);
        mEtUtr = view.findViewById(R.id.etUTR);

        mEtChequeAccount = view.findViewById(R.id.etCheckAcount);
        mEtChequeNumber = view.findViewById(R.id.etCheckNo);
        mEtChequeBank = view.findViewById(R.id.etCheckBank);
        mEtChequeBranch = view.findViewById(R.id.etCheckBranch);
        mEtChequeMICR = view.findViewById(R.id.etCheckMicr);
        mEtChequeDate = view.findViewById(R.id.etCheckDate);

        mEtTransferDate = view.findViewById(R.id.etTransferDate);
        mEtAmount.addTextChangedListener(new GenericTextWatcher(mEtAmount));


        mLinerNetbankingView = view.findViewById(R.id.linerNetbanking);
        mLinerRtgs = view.findViewById(R.id.linerRTGS);
        mLinerCheck = view.findViewById(R.id.linerCheque);
        mSpMandate = view.findViewById(R.id.spMandateList);
        mLinerMandate = view.findViewById(R.id.linerMandate);
        mCategoryRadio = view.findViewById(R.id.category);
        mSchemetypeRadio = view.findViewById(R.id.scheme_type);
        mSchemeSpinner = view.findViewById(R.id.scheme);
        bundle = new Bundle();
        bundle = getArguments();

        mFolioNumber.setText(bundle.getString("FolioNo"));


        mCategoryRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.equity) {
                    category_value = "E";
                    DialogsUtils.showProgressBar(getActivity(), false);
                    Scheme_List();
                } else if (checkedId == R.id.debt) {
                    category_value = "D";
                    DialogsUtils.showProgressBar(getActivity(), false);
                    Scheme_List();
                } else if (checkedId == R.id.fmp) {
                    category_value = "F";
                    DialogsUtils.showProgressBar(getActivity(), false);
                    Scheme_List();
                }
            }
        });

        mSchemetypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.growth) {
                    scheme_type_value = "G";
                    DialogsUtils.showProgressBar(getActivity(), false);
                    Scheme_List();
                } else if (checkedId == R.id.dividend_payout) {
                    scheme_type_value = "D";
                    DialogsUtils.showProgressBar(getActivity(), false);
                    Scheme_List();
                }
            }
        });

        mSchemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                scheme_code_value = SchemeCode[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mLinerNetbankingView.setVisibility(View.GONE);
        mLinerRtgs.setVisibility(View.GONE);
        mLinerCheck.setVisibility(View.GONE);
        mLinerMandate.setVisibility(View.GONE);
        mRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio1) {
                    netbanking_option = "Email";
                } else if (checkedId == R.id.radio2) {
                    netbanking_option = "Immediate";
                }
            }
        });

        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            view.findViewById(R.id.linerNSE).setVisibility(View.VISIBLE);
            setPaymentStatus();
            getBankDetail();
        } else {
            view.findViewById(R.id.linerNSE).setVisibility(View.GONE);
        }
        mTransactbtn.setOnClickListener(this);
        if(mSession.getUserType().equalsIgnoreCase("Broker")){
            mCid = "Broker";
        }else{
            mCid = mSession.getCID();
        }
        Scheme_List();
        return view;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_arrow) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (id == R.id.transact_btn) {
            if (mTransactbtn.getText().toString().equals(getString(R.string.new_purcahse__form_btn_footer_txt))) {


                if (mEtAmount.getText().toString().replaceAll(",", "").replaceAll(",", "").equals("") || mEtAmount.getText().toString().replaceAll(",", "").equals("0")) {
                    mEtAmount.requestFocus();
                    mEtAmount.setError(getString(R.string.new_purchase_error_empty_amnt));
                } else if ((Long.parseLong(mEtAmount.getText().toString().replaceAll(",", ""))) < 100) {
                    mEtAmount.requestFocus();
                    mEtAmount.setError(getResources().getString(R.string.new_purchase_error_invalid_amnt));
                } else if (payment_mode.equals("")) {
                    mApplication.showSnackBar(mEtAmount, getResources().getString(R.string.new_purchase_error_pay_mode_amnt));
                } else {
                    if (payment_mode.equals("OL")) {
                        if (mSelectedBankObject == null) {
                            mApplication.showSnackBar(mEtAmount, getResources().getString(R.string.new_purchase_error_bank_amnt));
                        } else {
                            newPurchase();
                        }

                    } else if (payment_mode.equals("TR")) {
                        validationForRTGS();
                    } else if (payment_mode.equals("M")) {
                        newPurchase();
                    } else if (payment_mode.equals("Q")) {
                        validationForCheck();
                    }
                }


            } else {
                Bundle bundle = new Bundle();
                bundle.putString("type", "pay_now");
                mActivity.displayViewOther(36, bundle);
            }
        }
    }

    private void validationForRTGS() {

        if (mEtRtgs.getText().toString().equals("")) {
            mEtRtgs.requestFocus();
            mEtRtgs.setError(getResources().getString(R.string.new_purchase_error_rtgs_empty));
        } else if (mEtMicr.getText().toString().equals("")) {
            mEtMicr.setError(getResources().getString(R.string.new_purchase_error_micr));
            mEtMicr.requestFocus();
        } else if (mEtBank.getText().toString().equals("")) {
            mEtBank.setError(getResources().getString(R.string.new_purchase_error_bank_empty));
            mEtBank.requestFocus();
        } else if (mEtBranch.getText().toString().equals("")) {
            mEtBranch.setError(getResources().getString(R.string.new_purchase_error_branch_empty));
            mEtBranch.requestFocus();
        }  else if (mEtTransferDate.getText().toString().equals("")) {
            mEtTransferDate.setError(getResources().getString(R.string.new_purchase_erroe_transfer_date));
            mEtTransferDate.requestFocus();
        } else {
            newPurchase();
        }
    }

    private void validationForCheck() {

        if (mEtChequeAccount.getText().toString().equals("")) {
            mEtChequeAccount.requestFocus();
            mEtChequeAccount.setError(getResources().getString(R.string.new_purchase_error_empty_acc_no));
        } else if (mEtChequeNumber.getText().toString().equals("")) {
            mEtChequeNumber.setError(getResources().getString(R.string.new_purchase_error_empty_cheque_no));
            mEtChequeNumber.requestFocus();
        } else if (mEtChequeBank.getText().toString().equals("")) {
            mEtChequeBank.setError(getResources().getString(R.string.new_purchase_error_bank_empty));
            mEtChequeBank.requestFocus();
        } else if (mEtChequeBranch.getText().toString().equals("")) {
            mEtChequeBranch.setError(getResources().getString(R.string.new_purchase_error_branch_empty));
            mEtChequeBranch.requestFocus();
        } else if (mEtChequeMICR.getText().toString().equals("")) {
            mEtChequeMICR.setError(getResources().getString(R.string.new_purchase_error_utr_empty));
            mEtChequeMICR.requestFocus();
        } else if (mEtChequeDate.getText().toString().equals("")) {
            mEtChequeDate.setError(getResources().getString(R.string.new_purchase_error_empty_cheque_date));
            mEtChequeDate.requestFocus();
        } else {

            newPurchase();

        }
    }


    private void setPaymentStatus() {
        String[] status = getResources().getStringArray(R.array.payment_status);
        ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, status);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpPaymnetType.setAdapter(spinner_value);
        mSpPaymnetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //   String text = mSpPaymnetType.getSelectedItem().toString();

                if (position == 0) {
                    mLinerNetbankingView.setVisibility(View.VISIBLE);
                    mLinerRtgs.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    mLinerMandate.setVisibility(View.GONE);
                    payment_mode = "OL";
                    netbanking_option = "Immediate";

                }else if (position == 1) {
                    mLinerNetbankingView.setVisibility(View.GONE);
                    mLinerRtgs.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    mLinerMandate.setVisibility(View.VISIBLE);
                    payment_mode = "M";
                    netbanking_option = "";
                    MandateListForNSE();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setBankName() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Bank");
        for (int i = 0; i < mBankList.size(); i++) {
            list.add(mBankList.get(i).optString("BankName"));
        }


        ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, list);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspBank.setAdapter(spinner_value);
        mspBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    int position = i - 1;
                    mSelectedBankObject = mBankList.get(position);
                    if (payment_mode.equals("Q")) {
                        mEtChequeAccount.setText(mSelectedBankObject.optString("AccountNo"));
                        mEtChequeBank.setText(mSelectedBankObject.optString("BankName"));
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setMandateListSpinner() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Mandate");
        mSpMandate.setAdapter(new MyAdapter(getActivity(), R.layout.custom_spinner_view, mMendateList));
        mSpMandate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedMandateObject = mMendateList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private class MyAdapter extends ArrayAdapter<JSONObject> {
        private List<JSONObject> items;
        private Context context;

        public MyAdapter(Context context, int textViewResourceId, List<JSONObject> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.context = context;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.custom_spinner_view, parent, false);
            TextView label = row.findViewById(R.id.tvTextView);
            ImageView icon = row.findViewById(R.id.image);
            JSONObject jsonObject = items.get(position);
            label.setText(jsonObject.optString("Mandate"));
            long mendateAmount = Long.parseLong(jsonObject.optString("Amount"));

            long requiredAmount = 0;

            String rawValue = mEtAmount.getText().toString().replaceAll(",", "");
            if (rawValue.equals("") || rawValue.equals("0")) {
                requiredAmount = 0;
            } else {
                requiredAmount = Long.parseLong(rawValue);
            }

            if (mendateAmount >= requiredAmount) {
                label.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
                icon.setImageResource(R.mipmap.green_checked);
            } else {
                label.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
                icon.setImageResource(R.mipmap.red_cross);
            }

            if(mSession.getUserType().equalsIgnoreCase("Broker")){
                mCid = "Broker";
            }else{
                mCid = mSession.getCID();
            }

            return row;
        }
    }


    private void getBankDetail() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.Bank_detail;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC",bundle.getString("UCC"));
            jsonObject.put("Bid",AppConstants.APP_BID);
            jsonObject.put("Passkey",mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        mBankList = new ArrayList();
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            JSONArray GetNSEBankList = jsonObject.optJSONArray("GetNSEBankList");
                            for (int i = 0; i < GetNSEBankList.length(); i++) {
                                JSONObject jsonObject1 = GetNSEBankList.optJSONObject(i);
                                mBankList.add(jsonObject1);
                            }
                            setBankName();

                        } else {
                               if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey",false,true);
                            }else{
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message",false,true);
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
                            Toast.makeText(getActivity(), jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

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

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void MandateListForNSE() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        String url = Config.GET_MENDATE_LIST;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey",mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC",bundle.getString("UCC"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        mMendateList = new ArrayList<>();

                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("SIPMandateDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mMendateList.add(object);
                            }
                            if (mMendateList.size() > 0)
                                setMandateListSpinner();
                            // showMedateListDailog();
                        } else {
                               if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey",false,true);
                            }else{
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message",false,true);
                            }
                        }


                    } catch (JSONException e) {
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
                            Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();


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

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void Scheme_List() {
        // DialogsUtils.showProgressBar(getActivity(), false);
        try {
            String url = Config.Scheme_List;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", bundle.getString("Fcode"));
            jsonObject.put("FolioNo", mFolioNumber);
            if (bundle.getString("FolioNo").contains("/")) {
                String[] folio = bundle.getString("FolioNo").split("/");
                jsonObject.put("FolioNo", folio[0]);
            } else {
                jsonObject.put("FolioNo", bundle.getString("FolioNo"));
            }

            jsonObject.put("SchemeType", category_value);
            jsonObject.put("Option", scheme_type_value);
            jsonObject.put("MyScheme", "N");
            jsonObject.put("Passkey", bundle.getString("Passkey"));
            jsonObject.put("OnlineOption", mSession.getAppType());
            System.out.println("jsonvalue" + jsonObject);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    //  DialogsUtils.hideProgressBar();

                    String Status = jsonObject.optString("Status");
                    if (Status.equalsIgnoreCase("True")) {
                        JSONArray SchemeListDetail = jsonObject.optJSONArray("SchemeListDetail");
                        SchemeName = new String[SchemeListDetail.length()];
                        SchemeCode = new String[SchemeListDetail.length()];
                        for (int i = 0; i < SchemeListDetail.length(); i++) {
                            JSONObject jsonObject1 = SchemeListDetail.optJSONObject(i);
                            SchemeName[i] = jsonObject1.optString("SchemeName");
                            SchemeCode[i] = jsonObject1.optString("SchemeCode");
                            new_scheme.add(SchemeName[i]);
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_dropdown, new_scheme);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSchemeSpinner.setAdapter(dataAdapter);


                    } else {
                           if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey",false,true);
                            }else{
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message",false,true);
                            }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // DialogsUtils.hideProgressBar();
                   if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message",false,true);
                    } else{
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message",false,true);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);

    }

    private void newPurchase() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        String url = Config.Additional_purchase;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", bundle.getString("Fcode"));
            jsonObject.put("Scode", scheme_code_value);
            if (bundle.getString("FolioNo").contains("/")) {
                String[] folio = bundle.getString("FolioNo").split("/");
                jsonObject.put("FolioNo", folio[0]);
            } else {
                jsonObject.put("FolioNo", bundle.getString("FolioNo"));
            }
            String rawValue = mEtAmount.getText().toString().replaceAll(",", "");
            jsonObject.put("Amount", rawValue);
            jsonObject.put("Passkey", bundle.getString("Passkey"));
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("BankName", mSelectedBankObject.optString("BankCode"));
            jsonObject.put("AccountNo", mSelectedBankObject.optString("AccountNo"));
            jsonObject.put("IFSCCode", mSelectedBankObject.optString("IFSCCode"));

            if (mSelectedMandateObject != null) {
                jsonObject.put("MandateID", mSelectedMandateObject.optString("MandateCode"));
            } else {
                jsonObject.put("MandateID", "");
            }

            jsonObject.put("PaymentMode", payment_mode);

            jsonObject.put("NetBankingOption", netbanking_option);
            jsonObject.put("InstrumentRTGSCode", mEtRtgs.getText().toString().trim());
            jsonObject.put("InstrumentBankMICR", mEtMicr.getText().toString().trim());
            jsonObject.put("InstrumentBank", mEtBank.getText().toString().trim());
            jsonObject.put("InstrumentBankBranch", mEtBranch.getText().toString().trim());
            jsonObject.put("RTGSUtrNo", mEtUtr.getText().toString().trim());
            jsonObject.put("RTGSDate", mEtTransferDate.getText().toString().trim());

            jsonObject.put("ChequeAccountNo", mEtChequeAccount.getText().toString().trim());
            jsonObject.put("ChequeNumber", mEtChequeNumber.getText().toString().trim());
            jsonObject.put("ChequeBank", mEtChequeBank.getText().toString().trim());
            jsonObject.put("ChequeBankBranch", mEtChequeBranch.getText().toString().trim());
            jsonObject.put("ChequeAmount", rawValue);
            jsonObject.put("ChequeDate", mEtChequeDate.getText().toString().trim());
            jsonObject.put("ChequeMICR", mEtChequeMICR.getText().toString().trim());
            jsonObject.put("DividendOption", "Z");
            jsonObject.put("IPAddress","");
            jsonObject.put("LoggedInUser",mCid);

        } catch (Exception e) {
            e.printStackTrace();
        }
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mBar.dismiss();
                String Status = response.optString("Status");
                String ServiceMSG = response.optString("ServiceMSG");
                if (Status.equalsIgnoreCase("True")) {
                    if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
                        if (payment_mode.equals("OL") && netbanking_option.equals("Immediate")) {
                            Intent intent = new Intent(getActivity(), PaymentForAdditionalPurchage.class);
                            intent.putExtra("url", response.optString("ServiceMSG"));
                            startActivity(intent);
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getActivity(), ServiceMSG, Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        if (ServiceMSG.contains("confirmed")) {
                            service_msg.setText(ServiceMSG);
                            service_msg.setVisibility(View.VISIBLE);
                            mTransactbtn.setText("Pay Now");
                            mTransactbtn.setText(getString(R.string.title_pay));
                        } else if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equals("DN")) {
                            mTransactbtn.setText("Pay Now");
                        } else {
                            mTransactbtn.setText(getString(R.string.new_purcahse__form_btn_footer_txt));
                            service_msg.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), ServiceMSG, Toast.LENGTH_SHORT).show();
                        }
                    }

                } else{
                    Toast.makeText(getActivity(), ServiceMSG, Toast.LENGTH_SHORT).show();

                }/*else {
                    service_msg.setText("");
                    service_msg.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), ServiceMSG, Toast.LENGTH_SHORT).show();
                }*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mBar.dismiss();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    Toast.makeText(getActivity(), "Server Response", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);


    }


    private void convertIntoCurrencyFormat(String currency, EditText editText) {
        try {
            Format format = NumberFormat.getNumberInstance(new Locale("en", "IN"));
            String str = format.format(Double.parseDouble(currency));
            editText.setText(str);
            editText.setSelection(editText.getText().toString().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void afterTextChanged(Editable editable) {
            if (getActivity().getCurrentFocus() == mEtAmount) {
                delayCall();
            }

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

    }

    private void delayCall() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String rawValue = mEtAmount.getText().toString().replaceAll(",", "");
                                    convertIntoCurrencyFormat(rawValue, mEtAmount);
                                    if ((mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) && payment_mode.equals("M")) {
                                        if (mMendateList != null && mMendateList.size() > 0)
                                            setMandateListSpinner();
                                    }

                                }
                            });
                        }

                    }
                },
                DELAY
        );
    }

}

package investwell.client.fragment.schemes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.PaymentForAdditionalPurchage;
import investwell.client.adapter.FragLumsumCartAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class FragGoForLumpsum extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback {
    private final long DELAY = 700; // milliseconds
    public TextView mTvTotalView;
    RecyclerView lumpsum_recycle;
    private ScrollView scrollView;
    private FragLumsumCartAdapter mAdapter;
    private CardView mCvPaymentView;
    private AppSession mSession;
    private ArrayList<JSONObject> mOrderList;
    private TextView mTvNothing;
    private String mUCC_Code = "", amount_value, netbanking_option = "", payment_mode = "";
    private Timer timer = new Timer();
    private Spinner mSpPaymnetType, mspBank, mSpMandate;
    private RadioGroup mRadioGroup1;
    private List<JSONObject> mBankList, mMendateList;
    private JSONObject mSelectedBankObject, mSelectedMandateObject;
    private LinearLayout mLinerNetbankingView, mLinerRtgs, mLinerCheck, mLinerMandate;
    private EditText mEtRtgs, mEtMicr, mEtBank, mEtBranch, mEtUtr, mEtTransferDate, mEtAmount;
    private EditText mEtChequeAccount, mEtChequeNumber, mEtChequeBank, mEtChequeBranch, mEtChequeMICR, mEtChequeDate;
    private RequestQueue requestQueue;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private View view;
    private ToolbarFragment toolbarFragment;
    String comingFrom = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_lumpsum, container, false);
        setInitializer();
        setUpToolBar(getResources().getString(R.string.text_lumpsum_title));
        getDataFormBundle();
        initialOperations();
        setRecylerViewAdapter();
        setListeners();
        getLumsumSchemes();
        disclaimerUi(view);

        return view;
    }

    private void disclaimerUi(View view) {
        TextView tvDisc = view.findViewById(R.id.tv_disc_title);
        String spanText = "You may need to confirm this transaction via link / OTP received on your Email / Mobile";
        tvDisc.setText(spanText);
        tvDisc.setSelected(true);
    }

    private void setUpToolBar(String title) {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(title, true, false, false, true, false, false, false, "home");
            toolbarFragment.setCallback(this);
        }
    }


    private void setInitializer() {
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        lumpsum_recycle = view.findViewById(R.id.lumpsum_recycle);
        mTvTotalView = view.findViewById(R.id.total_value);
        mTvNothing = view.findViewById(R.id.tvNothing);
        scrollView = view.findViewById(R.id.scrollView);
        mCvPaymentView = view.findViewById(R.id.paymentView);
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setVisibility(View.GONE);
        mTvNothing = view.findViewById(R.id.tvNothing);

        mSpPaymnetType = view.findViewById(R.id.spPaymentType);
        mspBank = view.findViewById(R.id.spBank);

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
        mLinerNetbankingView = view.findViewById(R.id.linerNetbanking);
        mLinerRtgs = view.findViewById(R.id.linerRTGS);
        mLinerCheck = view.findViewById(R.id.linerCheque);
        mSpMandate = view.findViewById(R.id.spMandateList);
        mLinerMandate = view.findViewById(R.id.linerMandate);


        /* mTvTotalView.setText(getString(R.string.Rs) + "0");*/
    }

    private void initialOperations() {
        mTvNothing.setText(getResources().getString(R.string.goal_summary_go_for_lumpsum_empty_txt));
        mLinerNetbankingView.setVisibility(View.GONE);
        mLinerRtgs.setVisibility(View.GONE);
        mLinerCheck.setVisibility(View.GONE);
        mLinerMandate.setVisibility(View.GONE);

        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            view.findViewById(R.id.linerNSE).setVisibility(View.VISIBLE);
            setPaymentStatus();
            getBankDetail();
        } else {
            view.findViewById(R.id.linerNSE).setVisibility(View.GONE);
        }

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

        System.out.println("@###@" + mSession.getAppType());
        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            view.findViewById(R.id.linerNSE).setVisibility(View.VISIBLE);
            setPaymentStatus();
            getBankDetail();
        } else {
            view.findViewById(R.id.linerNSE).setVisibility(View.GONE);
        }
    }

    private void setListeners() {
        view.findViewById(R.id.place_order).setOnClickListener(this);

        view.findViewById(R.id.add_scheme).setOnClickListener(this);
        mRadioGroup1.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio1) {
                netbanking_option = "Email";
            } else if (checkedId == R.id.radio2) {
                netbanking_option = "Immediate";
            }
        });

        view.findViewById(R.id.place_order).setOnClickListener(this);

        view.findViewById(R.id.add_scheme).setOnClickListener(this);

    }

    private void setRecylerViewAdapter() {
        lumpsum_recycle.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scrollView.setNestedScrollingEnabled(false);
        }

        mAdapter = new FragLumsumCartAdapter(mActivity, new ArrayList<JSONObject>(), mTvTotalView, mUCC_Code, mCvPaymentView, mTvNothing, toolbarFragment, new FragLumsumCartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("singleObject", jsonObject.toString());
            }
        });
        lumpsum_recycle.setAdapter(mAdapter);


    }

    private void getDataFormBundle() {


        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.containsKey("ucc_code")) {
                mUCC_Code = bundle.getString("ucc_code");
                if (bundle.containsKey("investType")) {
                    if (bundle.getString("investType").equalsIgnoreCase(AppConstants.INVEST_VIA_LUMPSUM)) {
                        if (bundle.containsKey("type")) {
                            comingFrom = bundle.getString("type");
                            if (comingFrom.equalsIgnoreCase("coming_from_dashborad")) {
                                callInvestNowApi(bundle.getString("investType"));
                            }
                        }

                    }
                }
            } else {
                mUCC_Code = mSession.getUCC_CODE();
            }
       /*     if (bundle.containsKey("type")) {
                comingFrom = bundle.getString("type");

                if (comingFrom != null && comingFrom.equalsIgnoreCase("coming_from_invest_confirm")) {
                    if (bundle.containsKey("investType")){
                        String investOption=bundle.getString("investType");
                        if(investOption!=null && investOption.equalsIgnoreCase(AppConstants.INVEST_VIA_LUMPSUM)){
                            callInvestNowApi(investOption);
                        }
                    }
                }

            }*/
        }


    }

    private void callInvestNowApi(final String type) {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = "";
        if (type.equals(AppConstants.INVEST_VIA_LUMPSUM)) {
            url = Config.INSERT_INTO_LUMPSUM;
        } else {
            url = Config.INSERT_INTO_SIP;
        }
        JSONObject jsonParam = new JSONObject();

        try {
            String FCode = "";
            String SCode = "";
            String FolioNo = "";
            String Reinvest = "";
            String Amount = "";
            String Installment = "";
            String amountData = "";
            if (Config.BASKET_SCHEMES_LIST.size() > 0) {
                for (int i = 0; i < Config.BASKET_SCHEMES_LIST.size(); i++) {
                    JSONObject jsonObject = Config.BASKET_SCHEMES_LIST.get(i);
//                   for (int j = 0; j < mApplication.amountList.size(); j++) {
//                        amountData = mApplication.getAmountList().get(i).getAmount();
//                    }
                    amountData = jsonObject.optString("amount");
                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                        FolioNo = "NEW";
                        Amount = amountData;
                        Installment = "0";
                        Reinvest = "Z";
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                        FolioNo = FolioNo + "|" + "NEW";
                        Amount = Amount + "|" + amountData;
                        Installment = Installment + "|" + "0";
                        Reinvest = Reinvest + "|" + "Z";
                    }
                }
                System.out.println(Config.BASKET_SCHEMES_LIST.size()+"--size  > 0 ---"+Config.BASKET_SCHEMES_LIST);
            } else {
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                        FolioNo = "NEW";
                        Amount = "0";
                        Installment = "0";
                        Reinvest = "Z";
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                        FolioNo = FolioNo + "|" + "NEW";
                        Amount = Amount + "|" + "0";
                        Installment = Installment + "|" + "0";
                        Reinvest = Reinvest + "|" + "Z";
                    }
                }
                System.out.println(Config.BASKET_SCHEMES_LIST.size()+"--size  else ---"+Config.BASKET_SCHEMES_LIST);
            }


            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonParam.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonParam.put("Fcode", FCode);
            jsonParam.put("Scode", SCode);
            jsonParam.put("FolioNo", FolioNo);
            jsonParam.put("Reinvest", Reinvest);
            jsonParam.put("Amount", Amount);
            jsonParam.put("Installment", Installment);
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            //jsonParam.put("BasketCode", mSession.getSelectedBasketCode());
            jsonParam.put("BasketCode", "00");
            jsonParam.put("OnlineOption", mSession.getAppType());

        } catch (Exception e) {

        }
        System.out.println("--jsonParam---"+jsonParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();

                try {
                    if (object.optString("Status").equals("True")) {
                        mSession.setAddToCartList("");
                        mSession.setAddToNFOCartList("");
                        // mSession.setSelectedBasketCode("");
                        Config.BASKET_SCHEMES_LIST.clear();

                       /* if (type.equals(AppConstants.INVEST_VIA_LUMPSUM)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUCC_Code);
                            mActivity.displayViewOther(14, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUCC_Code);
                            mActivity.displayViewOther(13, bundle);
                        }
*/
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

    private void validationForRTGS() {

        if (mEtRtgs.getText().toString().equals("")) {
            mEtRtgs.requestFocus();
            mEtRtgs.setError(getResources().getString(R.string.error_empty_rtgs_no));
        } else if (mEtMicr.getText().toString().equals("")) {
            mEtMicr.setError(getResources().getString(R.string.new_purchase_error_micr));
            mEtMicr.requestFocus();
        } else if (mEtBank.getText().toString().equals("")) {
            mEtBank.setError(getResources().getString(R.string.error_empty_bank_name));
            mEtBank.requestFocus();
        } else if (mEtBranch.getText().toString().equals("")) {
            mEtBranch.setError(getResources().getString(R.string.error_empty_branch_name));
            mEtBranch.requestFocus();
        } else if (mEtTransferDate.getText().toString().equals("")) {
            mEtTransferDate.setError(getResources().getString(R.string.error_empty_transfer_date));
            mEtTransferDate.requestFocus();
        } else {
            placeOrder();
        }
    }

    private void validationForCheck() {

        if (mEtChequeAccount.getText().toString().equals("")) {
            mEtChequeAccount.requestFocus();
            mEtChequeAccount.setError(getResources().getString(R.string.error_empty_acc_no));
        } else if (mEtChequeNumber.getText().toString().equals("")) {
            mEtChequeNumber.setError(getResources().getString(R.string.error_empty_cheque_no));
            mEtChequeNumber.requestFocus();
        } else if (mEtChequeBank.getText().toString().equals("")) {
            mEtChequeBank.setError(getResources().getString(R.string.error_empty_bank_name));
            mEtChequeBank.requestFocus();
        } else if (mEtChequeBranch.getText().toString().equals("")) {
            mEtChequeBranch.setError(getResources().getString(R.string.error_empty_branch_name));
            mEtChequeBranch.requestFocus();
        } else if (mEtChequeMICR.getText().toString().equals("")) {
            mEtChequeMICR.setError(getResources().getString(R.string.error_empty_utr_no));
            mEtChequeMICR.requestFocus();
        } else if (mEtChequeDate.getText().toString().equals("")) {
            mEtChequeDate.setError(getResources().getString(R.string.error_empty_cheque_date));
            mEtChequeDate.requestFocus();
        } else {

            placeOrder();

        }
    }

    private void setPaymentStatus() {
        String[] status = getResources().getStringArray(R.array.go_for_lumspum_status);
        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            List<String> list = new ArrayList<String>();
            list = Arrays.asList(status);
            ArrayList<String> arrayList = new ArrayList<String>(list);
            arrayList.add("UPI");
            status = arrayList.toArray(new String[list.size()]);
        }

        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, status);
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
                } else if (position == 1) {
                    mLinerNetbankingView.setVisibility(View.GONE);
                    mLinerRtgs.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    mLinerMandate.setVisibility(View.VISIBLE);
                    payment_mode = "M";
                    netbanking_option = "";
                    MandateListForNSE();

                } else if (position == 2) {
                    mLinerRtgs.setVisibility(View.VISIBLE);
                    mLinerNetbankingView.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    mLinerMandate.setVisibility(View.GONE);
                    payment_mode = "TR";
                    netbanking_option = "";
                } if (position == 3) {
                    mLinerNetbankingView.setVisibility(View.GONE);
                    mLinerRtgs.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    mLinerMandate.setVisibility(View.GONE);
                    payment_mode = "UPI";
                    netbanking_option = "Immediate";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void MandateListForNSE() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        String url = Config.GET_MENDATE_LIST;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("UCC", mUCC_Code);
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
                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
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
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();


                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setMandateListSpinner() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Mandate");
        mSpMandate.setAdapter(new MyAdapter(mActivity, R.layout.custom_spinner_view, mMendateList));
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

    private void getBankDetail() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.Bank_detail;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setBankName() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Bank");
        for (int i = 0; i < mBankList.size(); i++) {
            list.add(mBankList.get(i).optString("BankName") + " - " + mBankList.get(i).optString("AccountNo"));

        }


        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, list);
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


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.place_order) {
            boolean isAmountZero = false;
            for (int i = 0; i < mAdapter.mDataList.size(); i++) {
                JSONObject jsonObject = mAdapter.mDataList.get(i);
                int amount = jsonObject.optInt("Amount");
                if (amount == 0) {
                    isAmountZero = true;

                    mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_all_amount));
                    break;
                }
            }

            if (mAdapter.mDataList.size() == 0) {
                mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_no_items_in_cart));
            } else if (isAmountZero) {
                mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_all_amount));
            } else if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                if (payment_mode.equals("")) {

                    mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_select_pay_mode));
                } else {
                    if (payment_mode.equals("OL") || payment_mode.equals("UPI")) {
                        if (mSelectedBankObject == null) {
                            mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_select_bank));
                        } else {
                            placeOrder();
                        }

                    } else if (payment_mode.equals("TR")) {
                        validationForRTGS();
                    } else if (payment_mode.equals("M")) {
                        placeOrder();
                    } else if (payment_mode.equals("Q")) {
                        validationForCheck();
                    }
                }

            } else {
                placeOrder();

            }
        } else if (id == R.id.back_arrow) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.add_scheme) {
            MainActivity mainActivity = (MainActivity) mActivity;
            Bundle bundle = new Bundle();
            bundle.putString("type", "LS");
            bundle.putString("ucc_code", mUCC_Code);
            mainActivity.displayViewOther(21, bundle);
        }
    }

    private void placeOrder() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        String url = Config.LUMSUM_PLACE_ORDER;
        JSONObject jsonParam = new JSONObject();

        try {
            String FCode = "";
            String SCode = "";
            String SrNo = "";
            String Folio = "";
            String reinvest = "";
            String amount = "";

            for (int i = 0; i < mAdapter.mDataList.size(); i++) {
                JSONObject jsonObject = mAdapter.mDataList.get(i);
                View view = lumpsum_recycle.getChildAt(i);
                TextView folio = view.findViewById(R.id.tvFolioNo);
                if (folio.getText().toString().isEmpty()){
                    folio.setText("NEW");
                }else if (folio.getText().toString().equals("New")){
                    folio.setText("NEW");
                }
                if (i == 0) {
                    FCode = jsonObject.optString("Fcode");
                    SCode = jsonObject.optString("Scode");
                    SrNo = jsonObject.optString("srno");
                    amount = jsonObject.optString("Amount");
                    Folio = folio.getText().toString();
                    reinvest = "Z";
                } else {
                    FCode = FCode + "|" + jsonObject.optString("Fcode");
                    SCode = SCode + "|" + jsonObject.optString("Scode");
                    SrNo = SrNo + "|" + jsonObject.optString("srno");
                    Folio = Folio + "|" + folio.getText().toString();
                    amount = amount + "|" + jsonObject.optString("Amount");
                    reinvest = reinvest + "|" + "Z";

                }
            }
            String rawValue = mTvTotalView.getText().toString().replaceAll(",", "");
            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonParam.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonParam.put("Fcode", FCode);
            jsonParam.put("Scode", SCode);
            jsonParam.put("FolioNo", Folio);
            jsonParam.put("Reinvest", reinvest);
            jsonParam.put("Amount", amount);
            jsonParam.put("SRNO", SrNo);
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonParam.put("OnlineOption", mSession.getAppType());
            jsonParam.put("BankName", mSelectedBankObject.optString("BankCode"));
            jsonParam.put("AccountNo", mSelectedBankObject.optString("AccountNo"));
            jsonParam.put("IFSCCode", mSelectedBankObject.optString("IFSCCode"));

            if (mSelectedMandateObject != null) {
                jsonParam.put("MandateID", mSelectedMandateObject.optString("MandateCode"));
            } else {
                jsonParam.put("MandateID", "");
            }


            jsonParam.put("PaymentMode", payment_mode);

            jsonParam.put("NetBankingOption", netbanking_option);
            jsonParam.put("InstrumentRTGSCode", mEtRtgs.getText().toString().trim());
            jsonParam.put("InstrumentBankMICR", mEtMicr.getText().toString().trim());
            jsonParam.put("InstrumentBank", mEtBank.getText().toString().trim());
            jsonParam.put("InstrumentBankBranch", mEtBranch.getText().toString().trim());
            jsonParam.put("RTGSUtrNo", mEtUtr.getText().toString().trim());
            jsonParam.put("RTGSDate", mEtTransferDate.getText().toString().trim());

            jsonParam.put("ChequeAccountNo", mEtChequeAccount.getText().toString().trim());
            jsonParam.put("ChequeNumber", mEtChequeNumber.getText().toString().trim());
            jsonParam.put("ChequeBank", mEtChequeBank.getText().toString().trim());
            jsonParam.put("ChequeBankBranch", mEtChequeBranch.getText().toString().trim());
            jsonParam.put("ChequeAmount", rawValue);
            jsonParam.put("ChequeDate", mEtChequeDate.getText().toString().trim());
            jsonParam.put("ChequeMICR", mEtChequeMICR.getText().toString().trim());
            jsonParam.put("DividendOption", "Z");


        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                            if (netbanking_option.equals("Immediate")) {
                                Intent intent = new Intent(mActivity, PaymentForAdditionalPurchage.class);
                                intent.putExtra("url", object.optString("ServiceMSG"));
                                startActivityForResult(intent, 500);
                                // mActivity.getSupportFragmentManager().popBackStack();
                            } else {
                                showDailog(getString(R.string.alert_dialog_order_placed_sucess_txt), object.optString("ServiceMSG"));
                            }

                        } else {
                            List<JSONObject> list = new ArrayList<>();
                            JSONArray araArray = object.getJSONArray("ServiceMSG");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object2 = araArray.getJSONObject(i);
                                list.add(object2);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("data", list.toString());
                            bundle.putString("order_type", "lumsum");
                            bundle.putString("ucc_code", mUCC_Code);
                            MainActivity mainActivity = (MainActivity) mActivity;
                            mainActivity.displayViewOther(17, bundle);
                        }
                    } else {
                        if (object.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showCommonDailog(mActivity, mActivity, false, "Error", object.optString("ServiceMSG"), "invalidPasskey", false, true);
                        } else {
                            mApplication.showCommonDailog(mActivity, mActivity, false, "Error", object.optString("ServiceMSG"), "message", false, true);
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }

    private void showDailog(final String title, final String message) {
        CustomDialog customDialog = new CustomDialog(new CustomDialog.DialogBtnCallBack() {
            @Override
            public void onDialogBtnClick(View view) {
                if (view.getId() == R.id.btDone) {
                    mActivity.removeAllStack();
                }
            }
        });

        customDialog.showDialog(mActivity, title, message, getString(R.string.alert_dialog_btn_txt),
                "",
                true, false);
    }


    private void getLumsumSchemes() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        String url = Config.GET_LUMSUM_CART;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("UCC", mUCC_Code);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        mOrderList = new ArrayList<>();
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("LSCartDisplayDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mOrderList.add(object);
                            }
                            if (mOrderList.size() > 0) {
                                setUpToolBar(getResources().getString(R.string.text_lumpsum_title) + " (" + mOrderList.size() + ")");
                                mCvPaymentView.setVisibility(View.VISIBLE);
                                mAdapter.updateList(mOrderList);
                                mTvNothing.setVisibility(View.GONE);

                            } else {
                                mCvPaymentView.setVisibility(View.GONE);
                                mAdapter.updateList(new ArrayList<JSONObject>());
                                mTvNothing.setVisibility(View.VISIBLE);
                            }

                        } else {
                            mCvPaymentView.setVisibility(View.GONE);
                            mAdapter.updateList(new ArrayList<JSONObject>());
                            mTvNothing.setVisibility(View.VISIBLE);


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
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.btn_add_new) {
            mActivity.displayViewOther(0, null);
        }
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
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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

            String rawValue = mTvTotalView.getText().toString().replaceAll(",", "");
            if (rawValue.equals("") || rawValue.equals("0")) {
                requiredAmount = 0;
            } else {
                requiredAmount = Long.parseLong(rawValue);
            }

            if (mendateAmount >= requiredAmount) {
                label.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
                icon.setImageResource(R.mipmap.green_checked);
            } else {
                label.setTextColor(ContextCompat.getColor(mActivity, R.color.colorRed));
                icon.setImageResource(R.mipmap.red_cross);
            }

            return row;
        }
    }

}

package investwell.client.fragment.schemes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.PaymentForAdditionalPurchage;
import investwell.client.adapter.FragSIPCartAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomButton;

public class FragGoForSIP extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback {
    public TextView mTvTotalView;
    private RecyclerView mRecycleSIP;
    private ScrollView scrollView;
    private FragSIPCartAdapter mAdapter;
    private AppSession mSession;
    private ArrayList<JSONObject> mOrderList, mMendateList;
    private TextView mTvNothing;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private String mMendateCode = "", until_cancel_value = "Y";
    private CheckBox mCheckBox, until_cancel;
    private String mUCC_Code = "", netbanking_option = "", payment_mode = "";
    private Spinner mSpPaymnetType, mspBank;
    private RadioGroup mRadioGroup1;
    private List<JSONObject> mBankList;
    private JSONObject mSelectedBankObject, mSelectedMandateObject;
    private LinearLayout mLinerNetbankingView, mLinerRtgs, mLinerCheck;
    private EditText mEtRtgs, mEtMicr, mEtBank, mEtBranch, mEtUtr, mEtTransferDate, mEtAmount;
    private EditText mEtChequeAccount, mEtChequeNumber, mEtChequeBank, mEtChequeBranch, mEtChequeMICR, mEtChequeDate;
    private RequestQueue requestQueue;
    private View view;
    private ToolbarFragment toolbarFragment;
    private CardView mCvPaymentView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
            mActivity.setMainVisibility(this, null);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_lumpsum, container, false);
        setInitializer();
        setUpToolBar(getResources().getString(R.string.text_sip_title));
        getDataFormBundle();
        initialOperations();
        setRecylerViewAdapter();
        setListeners();
        getSIPSchemes();
        getSIPMendateList(false);
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
        mRecycleSIP = view.findViewById(R.id.lumpsum_recycle);
        mTvTotalView = view.findViewById(R.id.total_value);
        mTvNothing = view.findViewById(R.id.tvNothing);
        mCheckBox = view.findViewById(R.id.checkbox);
        until_cancel = view.findViewById(R.id.cb_until_cancel);
        mCvPaymentView = view.findViewById(R.id.paymentView);

        mSpPaymnetType = view.findViewById(R.id.spPaymentType);
        mspBank = view.findViewById(R.id.spBank);
        scrollView = view.findViewById(R.id.scrollView);
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
        view.findViewById(R.id.linerMandate).setVisibility(View.GONE);
        mMendateList = new ArrayList<>();
    }

    private void initialOperations() {
        mTvNothing.setText(getResources().getString(R.string.goal_summary_go_for_lumpsum_empty_txt));

        mLinerNetbankingView.setVisibility(View.GONE);
        mLinerRtgs.setVisibility(View.GONE);
        mLinerCheck.setVisibility(View.GONE);

        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            view.findViewById(R.id.linerNSE).setVisibility(View.VISIBLE);
            setPaymentStatus();
            getBankDetail();
        } else {
            view.findViewById(R.id.linerNSE).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.place_order) {
            SimpleDateFormat monthDisplay = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            boolean isAmountZero = false;
            boolean isInstallmentZero = false;
            boolean isDateWrong = false;
            for (int i = 0; i < mAdapter.mDataList.size(); i++) {
                JSONObject jsonObject = mAdapter.mDataList.get(i);
                int amount = jsonObject.optInt("Amount");
                int Installment = jsonObject.optInt("Installment");


                if (Installment == 0 && until_cancel_value.equalsIgnoreCase("N")) {

                    isInstallmentZero = true;
                    // Toast.makeText(mActivity, "Please enter Installment to all items", Toast.LENGTH_SHORT).show();
                    break;
                } else if (amount == 0) {
                    isAmountZero = true;
                    // Toast.makeText(mActivity, "Please enter amount in all funds", Toast.LENGTH_SHORT).show();
                    break;
                }

                try {

                    if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                        String selectedDate = jsonObject.optString("selectedDate");
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, 7);
                        String mDateAfter7days = simpleDateFormat.format(calendar.getTime());
                        Date dateAfter7Days = monthDisplay.parse(mDateAfter7days);
                        Date currentDate = monthDisplay.parse(selectedDate);

                        if (currentDate.getTime() < dateAfter7Days.getTime()) {
                            isDateWrong = true;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (mAdapter.mDataList.size() == 0) {
                mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_no_items_in_cart));
            } else if (isInstallmentZero) {
                mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_sip_error_installment_all_items));
            } else if (isAmountZero) {
                mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_sip_error_amnt_all_items));
            } else if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                if (isDateWrong) {
                    Toast.makeText(mActivity, getString(R.string.sip_date_will_be_7_days_ahead), Toast.LENGTH_SHORT).show();
                } else if (payment_mode.equals("")) {
                    mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_select_pay_mode));

                } else {
                    if (payment_mode.equals("OL") || payment_mode.equals("UPI")) {
                        if (mSelectedBankObject == null) {
                            mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_select_bank));
                        } else {
                            showMedateListDailog();
                        }
                    } else if (payment_mode.equals("TR")) {
                        if (mEtRtgs.getText().toString().isEmpty()) {
                            mApplication.showSnackBar(mCvPaymentView, getString(R.string.error_empty_rtgs_no));
                        } else if (mEtMicr.getText().toString().isEmpty()) {
                            mApplication.showSnackBar(mCvPaymentView, getString(R.string.error_empty_micr_no));
                        } else if (mEtBank.getText().toString().isEmpty()) {
                            mApplication.showSnackBar(mCvPaymentView, getString(R.string.error_empty_bank_name));
                        } else if (mEtBranch.getText().toString().isEmpty()) {
                            mApplication.showSnackBar(mCvPaymentView, getString(R.string.error_empty_branch_name));
                        } else if (mEtTransferDate.getText().toString().isEmpty()) {
                            mApplication.showSnackBar(mCvPaymentView, getString(R.string.error_empty_transfer_date));
                        } else {
                            showMedateListDailog();
                        }

                    } else {
                        showMedateListDailog();
                    }
                }

            } else {
                showMedateListDailog();
            }
        } else if (id == R.id.back_arrow) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.add_scheme) {
            MainActivity mainActivity = (MainActivity) mActivity;
            Bundle bundle = new Bundle();
            bundle.putString("type", "SIP");
            bundle.putString("ucc_code", mUCC_Code);
            mainActivity.displayViewOther(21, bundle);
        }
    }


    private void setListeners() {
        view.findViewById(R.id.place_order).setOnClickListener(this);

        view.findViewById(R.id.add_scheme).setOnClickListener(this);
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
    }

    private void setRecylerViewAdapter() {
        mRecycleSIP.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scrollView.setNestedScrollingEnabled(false);
        }

        mAdapter = new FragSIPCartAdapter(mActivity, new ArrayList<JSONObject>(), mTvTotalView, mCheckBox, mCvPaymentView, until_cancel, mTvNothing, mUCC_Code, toolbarFragment, new FragSIPCartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("singleObject", jsonObject.toString());
            }
        });
        mRecycleSIP.setAdapter(mAdapter);


    }

    private void getDataFormBundle() {

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
            if (bundle.containsKey("investType")) {
                if (bundle.getString("investType").equalsIgnoreCase(AppConstants.INVEST_VIA_SIP)) {
                    callInvestNowApi(bundle.getString("investType"));
                }
            }
        }
        until_cancel.setVisibility(View.GONE);
        until_cancel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    until_cancel_value = "Y";
                    mAdapter.update_List(until_cancel_value);
                } else {
                    until_cancel_value = "N";
                    mAdapter.update_List(until_cancel_value);
                }
            }
        });

    }

    private void callInvestNowApi(final String type) {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = "";
        if (type.equals("lumsum")) {
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
//                    for (int j = 0; j < mApplication.amountList.size(); j++) {
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
                        FolioNo = FolioNo + "|" + "NEW" +
                                "";
                        Amount = Amount + "|" + "0";
                        Installment = Installment + "|" + "0";
                        Reinvest = Reinvest + "|" + "Z";
                    }
                }
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

                      /*  if (type.equals("lumsum")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUCC_Code);
                            mActivity.displayViewOther(14, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUCC_Code);
                            mActivity.displayViewOther(13, bundle);
                        }*/


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

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setBankName() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < mBankList.size(); i++) {
            list.add(mBankList.get(i).optString("BankName"));
        }


        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, list);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspBank.setAdapter(spinner_value);
        mspBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mSelectedBankObject = mBankList.get(i);
                if (payment_mode.equals("Q")) {
                    mEtChequeAccount.setText(mSelectedBankObject.optString("AccountNo"));
                    mEtChequeBank.setText(mSelectedBankObject.optString("BankName"));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                    payment_mode = "OL";
                    netbanking_option = "Immediate";

                } else if (position == 1) {
                    mLinerNetbankingView.setVisibility(View.GONE);
                    mLinerRtgs.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    payment_mode = "M";
                    netbanking_option = "";


                } else if (position == 2) {
                    mLinerRtgs.setVisibility(View.VISIBLE);
                    mLinerNetbankingView.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    payment_mode = "TR";
                    netbanking_option = "";
                } else if (position == 3) {
                    mLinerNetbankingView.setVisibility(View.GONE);
                    mLinerRtgs.setVisibility(View.GONE);
                    mLinerCheck.setVisibility(View.GONE);
                    payment_mode = "UPI";
                    netbanking_option = "Immediate";

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void placeOrder() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        String url = Config.PLACE_SIP_ORDER;
        JSONObject jsonParam = new JSONObject();

        try {
            String FCode = "";
            String SCode = "";
            String SrNo = "";
            String Folio = "";
            String Frequency = "";
            String amount = "";
            String installment = "";
            String selectedDate = "";
            String reinvest = "";


            for (int i = 0; i < mAdapter.mDataList.size(); i++) {
                JSONObject jsonObject = mAdapter.mDataList.get(i);
                View view = mRecycleSIP.getChildAt(i);
                TextView folio = view.findViewById(R.id.tvFolioNo);
                if (folio.getText().toString().isEmpty()) {
                    folio.setText("NEW");
                } else if (folio.getText().toString().equals("New")) {
                    folio.setText("NEW");
                }

                if (i == 0) {
                    FCode = jsonObject.optString("Fcode");
                    SCode = jsonObject.optString("Scode");
                    SrNo = jsonObject.optString("srno");
                    Folio = folio.getText().toString();
                    amount = jsonObject.optString("Amount");
                    installment = jsonObject.optString("Installment");
                    Frequency = jsonObject.optString("Frequency");
                    selectedDate = jsonObject.optString("selectedDate");
                    reinvest = "Z";

                } else {
                    FCode = FCode + "|" + jsonObject.optString("Fcode");
                    SCode = SCode + "|" + jsonObject.optString("Scode");
                    SrNo = SrNo + "|" + jsonObject.optString("srno");
                    Folio = Folio + "|" + folio.getText().toString();
                    Frequency = Frequency + "|" + jsonObject.optString("Frequency");
                    amount = amount + "|" + jsonObject.optString("Amount");
                    installment = installment + "|" + jsonObject.optString("Installment");
                    selectedDate = selectedDate + "|" + jsonObject.optString("selectedDate");
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

            if (mCheckBox.isChecked())
                jsonParam.put("FirstOrderToday", "Y");
            else
                jsonParam.put("FirstOrderToday", "N");

            jsonParam.put("SRNO", SrNo);
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonParam.put("Installment", installment);
            jsonParam.put("SIPDate", selectedDate);
            jsonParam.put("MandateID", mMendateCode);


            jsonParam.put("OnlineOption", mSession.getAppType());
            jsonParam.put("UntilCancel", until_cancel_value);
            jsonParam.put("BankName", mSelectedBankObject.optString("BankCode"));
            jsonParam.put("AccountNo", mSelectedBankObject.optString("AccountNo"));
            jsonParam.put("IFSCCode", mSelectedBankObject.optString("IFSCCode"));
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
            jsonParam.put("Frequency", Frequency);


        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    String ServiceMSG = object.optString("ServiceMSG");
                    if (object.optString("Status").equals("True")) {
                        if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                            if (netbanking_option.equals("Immediate")) {
                                Intent intent = new Intent(mActivity, PaymentForAdditionalPurchage.class);
                                intent.putExtra("url", object.optString("ServiceMSG"));
                                startActivityForResult(intent, 500);
                                // mActivity.getSupportFragmentManager().popBackStack();
                            } else {
                                mApplication.showCommonDailog(mActivity, mActivity, false, getString(R.string.alert_dialog_order_placed_sucess_txt), ServiceMSG, "message", false, true);

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
                            bundle.putString("firstTimePayment", "" + mCheckBox.isChecked());
                            bundle.putString("ucc_code", mUCC_Code);
                            MainActivity mainActivity = (MainActivity) mActivity;
                            mainActivity.displayViewOther(17, bundle);
                        }


                    } else {
                        if (ServiceMSG.equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showCommonDailog(mActivity, mActivity, false, "Error", ServiceMSG, "invalidPasskey", false, true);
                        } else {
                            mApplication.showCommonDailog(mActivity, mActivity, false, "Error", ServiceMSG, "message", false, true);
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

    private void getSIPSchemes() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        }
        String url = Config.GET_SIP_CART;

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
                            JSONArray araArray = jsonObject.getJSONArray("SIPCartDisplayDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                object.put("untilCancel", true);
                                mOrderList.add(object);
                            }
                            if (mOrderList.size() > 0) {
                                setUpToolBar(getResources().getString(R.string.text_sip_title) + " (" + mOrderList.size() + ")");
                                mAdapter.updateList(mOrderList);
                                mTvNothing.setVisibility(View.GONE);
                                mCheckBox.setVisibility(View.VISIBLE);
                                mCvPaymentView.setVisibility(View.VISIBLE);
                            } else {
                                mAdapter.updateList(new ArrayList<JSONObject>());
                                mTvNothing.setVisibility(View.VISIBLE);
                                mCheckBox.setVisibility(View.GONE);
                                mCvPaymentView.setVisibility(View.GONE);
                            }

                        } else {
                            mAdapter.updateList(new ArrayList<JSONObject>());
                            mTvNothing.setVisibility(View.VISIBLE);
                            mCheckBox.setVisibility(View.GONE);
                            mCvPaymentView.setVisibility(View.GONE);


                        }
                        until_cancel.setVisibility(View.GONE);


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

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void getSIPMendateList(final boolean isShowLoader) {
        ProgressDialog mBar = null;
        if (isShowLoader) {
            mBar = ProgressDialog.show(mActivity, null, null, true, false);
            mBar.setContentView(R.layout.progress_piggy);
            mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        }
        final ProgressDialog finalMBar = mBar;
        String url = Config.GET_MENDATE_LIST;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put("FormatReq", "N");
            jsonObject.put("MandateStatus", "Higher");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (isShowLoader)
                        finalMBar.dismiss();
                    try {
                        mMendateList = new ArrayList<>();
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("SIPMandateDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mMendateList.add(object);
                            }
                            //showMedateListDailog();

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
            }, volleyError -> {
                if (isShowLoader)
                    finalMBar.dismiss();
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


    @SuppressLint("SetTextI18n")
    private void showMedateListDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dailog_sip_mendate_list, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        RelativeLayout relMain = dialogView.findViewById(R.id.relMain);
        LinearLayout relSubMenu = dialogView.findViewById(R.id.linerTop);
        TextView tvSorryText = dialogView.findViewById(R.id.tvSorryText);
        TextView amount = dialogView.findViewById(R.id.amount);
        CustomButton tvCancel = dialogView.findViewById(R.id.cancel);
        final CustomButton proceed = dialogView.findViewById(R.id.proceed);
        final RadioGroup rgp = dialogView.findViewById(R.id.radiogroup);
        RadioGroup.LayoutParams rprms;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (Build.VERSION.SDK_INT >= 21) {

            relSubMenu.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.dialog_header_background));
            GradientDrawable drawable = (GradientDrawable) relSubMenu.getBackground();
            drawable.setColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        } else {
            relSubMenu.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        }

        amount.setText(getString(R.string.Rs) + " " + mTvTotalView.getText().toString());
        boolean isRequiredNewCReate = true;

        for (int i = 0; i < mMendateList.size(); i++) {
            JSONObject jsonObject = mMendateList.get(i);
            RadioButton radioButton = new RadioButton(mActivity);
            rprms = new RadioGroup.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rprms.setMargins(5, 10, 5, 10);
            radioButton.setLayoutParams(rprms);

            int mendateAmount = Integer.parseInt(jsonObject.optString("Amount"));
            int requiredAmount = Integer.parseInt(mTvTotalView.getText().toString());

            if (mendateAmount >= requiredAmount) {
                isRequiredNewCReate = false;
                radioButton.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
            } else {
                radioButton.setTextColor(ContextCompat.getColor(mActivity, R.color.colorRed));
            }

            rgp.addView(radioButton, rprms);
            radioButton.setText(jsonObject.optString("Mandate"));

        }

        if (isRequiredNewCReate) {
            tvSorryText.setVisibility(View.VISIBLE);
            tvSorryText.setText("Sorry, You have not any mandate for required amount " + "" + mTvTotalView.getText().toString() + ", So please create another Mandate");
            proceed.setText("Create Mandate");
        } else {
            tvSorryText.setVisibility(View.GONE);
            proceed.setText("Proceed");
        }

        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if (btn.getId() == checkedId) {
                        JSONObject jsonObject1 = mMendateList.get(i);
                        int mendateAmount = Integer.parseInt(jsonObject1.optString("Amount"));
                        int requiredAmount = Integer.parseInt(mTvTotalView.getText().toString());

                        if (mendateAmount < requiredAmount) {
                            btn.setChecked(false);
                        }

                        mMendateCode = jsonObject1.optString("MandateCode");
                    }
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (proceed.getText().toString().equals("Proceed")) {
                    int idNumber = rgp.getCheckedRadioButtonId();

                    if (idNumber == -1) {
                        mApplication.showSnackBar(mCvPaymentView, getString(R.string.go_for_lumpsum_error_mandate));
                    } else {
                        alertDialog.dismiss();
                        placeOrder();
                    }
                } else {
                    alertDialog.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putString("ucc_code", mUCC_Code);
                    mActivity.displayViewOther(12, bundle);
                }
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
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

package investwell.client.fragment.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ChooserFragment;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

public class AdditionalRedeemTransactFragment extends Fragment implements View.OnClickListener, ChooserFragment.ChooserCallBack, ToolbarFragment.ToolbarCallback {
    private View view;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    private ChooserFragment chooserFragment;
    private AppSession mSession;
    private TextView tvInvestorName, tvUnitBalanceValue, tvFolioNumber, tvSchemeName, tvMarketValue;
    private EditText etAmount;
    private Spinner spinnerBank;
    private String amount_value, installments_value, selectedBtn, first_order_value = "Y", start_date_value, mCid, mUcc, mFolioNum, mFcode, mScode;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ArrayList<String> list;
    private ArrayList<JSONObject> jsonObjects;
    private String reedmption_type_value = "Amount", switch_type_value = "Amount", Bank_code, Account_no, IFSC;
    private Bundle bundle;
    private CheckBox cbFirstOrder;
    private SwipeButton swipeBtn;
    private String[] folio;
    private LinearLayout llMiddleViewHeader;
    private TextInputLayout tilAmount, tilInstallments;
    private LinearLayout llBankLayout, llEtContainer;
    private ShimmerFrameLayout mShimmerViewContainer;
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                JSONObject jsonObject = jsonObjects.get(position);
                Bank_code = jsonObject.optString("BankCode");
                Account_no = jsonObject.optString("AccountNo");
                IFSC = jsonObject.optString("IFSCCode");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            clearErrorMessages();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public AdditionalRedeemTransactFragment() {
        // Required empty public constructor
    }

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
    public void onStart() {

        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_additional_redeemption, container, false);
        setUpToolBar();
        setInitializer(view);
        getDatFromBundle();
        setUpChooserFragment();
        setListeners();
        disclaimerUi(view);
        return view;
    }



    private void disclaimerUi(View view) {
        TextView tvDisc = view.findViewById(R.id.tv_disc_title);
        String spanText="You may need to confirm this transaction via link / OTP received on your Email / Mobile";tvDisc.setText(spanText);
        tvDisc.setSelected(true);
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.btn_add_new) {
            mActivity.displayViewOther(0, null);
        }
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_redeem), true, false, false, true, false, false, false, "home");
            fragToolBar.setCallback(this);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity, R.color.colorPrimary));

            }
        }
    }

    private OnActiveListener btnTransactOnActiveListener = new OnActiveListener() {
        @Override
        public void onActive() {

            onTransactBtnClick();
            if (swipeBtn.isActive()) {
                swipeBtn.toggleState();
            }
        }
    };

    private void onTransactBtnClick() {

        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            tilAmount.setError(getResources().getString(R.string.error_empty_amount));
            etAmount.setFocusable(true);

        } else {
            callRedeemTransactApi();
        }
    }

    private void setInitializer(View view) {
        bundle = getArguments();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
    /*    mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();*/
        tvFolioNumber = view.findViewById(R.id.tv_folio_no);
        tvInvestorName = view.findViewById(R.id.tv_investor_name);
        tvSchemeName = view.findViewById(R.id.tv_scheme_name);
        tvUnitBalanceValue = view.findViewById(R.id.tv_unit_balance_value);
        tvMarketValue = view.findViewById(R.id.tv_market_value);
        etAmount = view.findViewById(R.id.et_amnt);
        spinnerBank = view.findViewById(R.id.spinner_bank);
        swipeBtn = view.findViewById(R.id.btn_swipe_transact);
        llBankLayout = view.findViewById(R.id.ll_bank_layout);
        llMiddleViewHeader = view.findViewById(R.id.ll_middle_view_data);
        llEtContainer = view.findViewById(R.id.ll_et_container);
        swipeBtn = view.findViewById(R.id.btn_swipe_transact);
        tilAmount = view.findViewById(R.id.til_amount);
    }

    private void clearErrorMessages() {

        tilAmount.setError("");
    }


    @SuppressLint("SetTextI18n")
    private void getDatFromBundle() {
        if (bundle != null) {
            tvInvestorName.setText(!TextUtils.isEmpty(bundle.getString("applicant_name")) ? bundle.getString("applicant_name") : "");
            tvSchemeName.setText(!TextUtils.isEmpty(bundle.getString("colorBlue")) ? bundle.getString("colorBlue") : "");
            tvUnitBalanceValue.setText((!TextUtils.isEmpty(bundle.getString("purchase_cost")) ? bundle.getString("purchase_cost") : ""));
            tvMarketValue.setText((!TextUtils.isEmpty(getString(R.string.rs) + bundle.getString("market_position")) ? getString(R.string.rs) + bundle.getString("market_position") : ""));
            if (!TextUtils.isEmpty(tvUnitBalanceValue.getText()) || !TextUtils.isEmpty(tvMarketValue.getText())) {
                llMiddleViewHeader.setVisibility(View.VISIBLE);
            } else {
                llMiddleViewHeader.setVisibility(View.GONE);
            }
            if (mSession.getUserType().equalsIgnoreCase("Broker")) {
                mCid = "Broker";
            } else {
                mCid = mSession.getCID();
            }
            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase(getResources().getString(R.string.apptype_dn))) {
                llBankLayout.setVisibility(View.VISIBLE);
                callBankDetailApi();
            } else {
                llBankLayout.setVisibility(View.GONE);
            }
            mUcc = bundle.getString("UCC");
            mFcode = bundle.getString("Fcode");
            mScode = bundle.getString("Scode");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.requireNonNull(bundle.getString("FolioNo")).contains("/")) {

                    folio = Objects.requireNonNull(bundle.getString("FolioNo")).split("/");
                    mFolioNum = folio[0];
                    tvFolioNumber.setText(getResources().getString(R.string.add_transac_folio_no) + mFolioNum);
                } else {
                    mFolioNum = bundle.getString("FolioNo");
                    tvFolioNumber.setText(getResources().getString(R.string.add_transac_folio_no) + mFolioNum);
                }
            }
        }
    }

    private void setUpChooserFragment() {
        chooserFragment = (ChooserFragment) getChildFragmentManager().findFragmentById(R.id.chooser_fragment);
        if (chooserFragment != null) {
            chooserFragment.setUpChooserElements(getResources().getString(R.string.additional_redeem_frequency_title),
                    getResources().getString(R.string.additional_switch_primary_frequency),
                    getResources().getString(R.string.additional_switch_secondary_frequency),
                    getResources().getString(R.string.additional_switch_tertiary_frequency), true, true, true, false);

            chooserFragment.setChooserCallback(this);
        }
    }

    private void setListeners() {
        spinnerBank.setOnItemSelectedListener(onItemSelectedListener);
        swipeBtn.setOnActiveListener(btnTransactOnActiveListener);
        etAmount.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            reedmption_type_value = "Amount";
            switch_type_value = "Amount";
            etAmount.setHint(getResources().getString(R.string.additional_redeem_enter_amount_txt));
            etAmount.setText("");
            etAmount.setEnabled(true);
            llEtContainer.setVisibility(View.VISIBLE);
        } else if (id == R.id.btn_secondary) {
            reedmption_type_value = "Unit";
            switch_type_value = "Unit";
            etAmount.setText("");
            etAmount.setEnabled(true);
            etAmount.setHint(getResources().getString(R.string.additional_redeem_enter_units_txt));
            llEtContainer.setVisibility(View.VISIBLE);
        } else if (id == R.id.btn_tertiary) {
            reedmption_type_value = "AllUnit";
            switch_type_value = "Unit";
            etAmount.setText(tvUnitBalanceValue.getText());
            etAmount.setEnabled(false);
            llEtContainer.setVisibility(View.GONE);
        }

    }


    /***************************API Calling**********************************/

    public void callRedeemTransactApi() {
        DialogsUtils.showProgressBar(mActivity, false);

        String url = Config.Additional_Redeem;
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUcc);
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("Scode", mScode);
            jsonObject.put("FolioNo", mFolioNum);
            jsonObject.put("Amount", etAmount.getText().toString());
            jsonObject.put("RedeemType", reedmption_type_value);
            jsonObject.put("Passkey", bundle.getString("Passkey"));
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("BankName", Bank_code);
            jsonObject.put("AccountNo", Account_no);
            jsonObject.put("IFSCCode", IFSC);
            jsonObject.put("IPAddress", "");
            jsonObject.put("LoggedInUser", mCid);


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("response" + jsonObject);

                    DialogsUtils.hideProgressBar();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        String ServiceMSG = response.optString("ServiceMSG");
                        mApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.dialog_title_message), ServiceMSG, "message", true, true);

                    } else {
                        String ServiceMSG = response.optString("ServiceMSG");
                        mApplication.showSnackBar(etAmount, ServiceMSG);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
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

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void callBankDetailApi() {

        String url = Config.Bank_detail;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    try {


                        list = new ArrayList();
                        jsonObjects = new ArrayList();

                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            JSONArray GetNSEBankList = jsonObject.optJSONArray("GetNSEBankList");
                            for (int i = 0; i < GetNSEBankList.length(); i++) {
                                JSONObject jsonObject1 = GetNSEBankList.optJSONObject(i);
                                list.add(jsonObject1.optString("BankName") + " - " + jsonObject1.optString("AccountNo"));
                                jsonObjects.add(jsonObject1);

                                /*if (jsonObject1.optString("DefaultBank").equalsIgnoreCase("N")) {

                                } else {
                                    list.add(jsonObject1.optString("BankName")+" - "+jsonObject1.optString("AccountNo"));
                                    jsonObjects.add(jsonObject1);

                                }*/

                            }
                            setBankName();

                        } else {
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
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
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());

                            mApplication.showSnackBar(etAmount, jsonObject.optString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        mApplication.showSnackBar(etAmount, getResources().getString(R.string.no_internet));
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
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, list);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(spinner_value);

    }
}

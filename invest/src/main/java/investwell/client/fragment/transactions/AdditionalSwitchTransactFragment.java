package investwell.client.fragment.transactions;


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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ChooserFragment;
import investwell.client.fragment.others.ChooserSchemeTypeFragment;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalSwitchTransactFragment extends Fragment implements View.OnClickListener, ChooserFragment.ChooserCallBack, ChooserSchemeTypeFragment.ChooserTypeCallback , ToolbarFragment.ToolbarCallback{
    private View view;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    private ChooserFragment chooserFragment;
    private ChooserSchemeTypeFragment chooserSchemeTypeFragment;
    private AppSession mSession;
    private TextView tvInvestorName, tvUnitBalanceValue, tvFolioNumber, tvSchemeName, tvMarketValue;
    private EditText etAmount;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private Bundle bundle;
    private Spinner spinnerMandate;
    private String[] folio;
    private SwipeButton btnTransact;
    private LinearLayout llEtAmounntContainer,llMiddleViewContainer;
    private String category_value = "D", scheme_type_value = "G", switch_type_value = "Amount";
    private String amountedtxt_value, to_scode_value, TValue, reedmption_type_value = "Amount", mCid, mUcc, mFolioNum, mFcode, mScode;
    private String[] SchemeName, SchemeCode;
    private TextInputLayout tilAmount, tilInstallments;
    private ShimmerFrameLayout mShimmerViewContainer, shimmerSchemeContainer;
    private LinearLayout mSchemeContainer;

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            to_scode_value = SchemeCode[position];
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

    public AdditionalSwitchTransactFragment() {
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
    public void onToolbarItemClick(View view) {
        if(view.getId()==R.id.btn_add_new){
            mActivity.displayViewOther(0,null);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_additional_switch, container, false);
        setUpToolBar();
        setInitializer(view);
        getDataFromBundle();
        setUpChooserFragment();
        setUpChooserSchemeType();
        setListeners();
        callSchemeListApi(category_value,scheme_type_value);
        disclaimerUi(view);
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_switch), true, false, false, true, false, false, false,"home");
            fragToolBar.setCallback(this);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
        }
    }
    private void disclaimerUi(View view){
        TextView tvDisc=view.findViewById(R.id.tv_disc_title);
        String spanText="You may need to confirm this transaction via link / OTP received on your Email / Mobile";tvDisc.setText(spanText);
        tvDisc.setSelected(true);
    }
    private void setInitializer(View view) {
        bundle = getArguments();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        shimmerSchemeContainer = view.findViewById(R.id.shimmer_scheme_container);
        mSchemeContainer = view.findViewById(R.id.ll_scheme_container);

        tvFolioNumber = view.findViewById(R.id.tv_folio_no);
        tvInvestorName = view.findViewById(R.id.tv_investor_name);
        tvSchemeName = view.findViewById(R.id.tv_scheme_name);
        tvUnitBalanceValue = view.findViewById(R.id.tv_unit_balance_value);
        tvMarketValue = view.findViewById(R.id.tv_market_value);
        etAmount = view.findViewById(R.id.et_amnt);
        spinnerMandate = view.findViewById(R.id.spinner_mandate);
        llMiddleViewContainer = view.findViewById(R.id.ll_middle_view_data);
        llEtAmounntContainer= view.findViewById(R.id.ll_et_container);
        btnTransact=view.findViewById(R.id.btn_swipe_transact);
        tilAmount = view.findViewById(R.id.til_amount);
    }

    private void setListeners() {
        spinnerMandate.setOnItemSelectedListener(onItemSelectedListener);
        btnTransact.setOnActiveListener(btnTransactOnActiveListener);
        etAmount.addTextChangedListener(textWatcher);
    }
    private void clearErrorMessages() {

        tilAmount.setError("");
    }
    private void getDataFromBundle() {
        bundle = getArguments();
        if (bundle != null) {
            tvInvestorName.setText(!TextUtils.isEmpty(bundle.getString("applicant_name")) ? bundle.getString("applicant_name") : "");
            tvSchemeName.setText(!TextUtils.isEmpty(bundle.getString("colorBlue")) ? bundle.getString("colorBlue") : "");
            tvUnitBalanceValue.setText((!TextUtils.isEmpty(bundle.getString("purchase_cost")) ? bundle.getString("purchase_cost") : ""));
            tvMarketValue.setText((!TextUtils.isEmpty(getString(R.string.rs) +bundle.getString("market_position")) ? getString(R.string.rs) +bundle.getString("market_position") : ""));
            if (!TextUtils.isEmpty(tvUnitBalanceValue.getText()) || !TextUtils.isEmpty(tvMarketValue.getText())) {
                llMiddleViewContainer.setVisibility(View.VISIBLE);
            } else {
                llMiddleViewContainer.setVisibility(View.GONE);
            }

            if (mSession.getUserType().equalsIgnoreCase("Broker")) {
                mCid = "Broker";
            } else {
                mCid = mSession.getCID();
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
    private OnActiveListener btnTransactOnActiveListener = new OnActiveListener() {
        @Override
        public void onActive() {
            onTransactBtnClick();
            if (btnTransact.isActive()) {
                btnTransact.toggleState();
            }
        }
    };
    private void setUpChooserFragment() {
        chooserFragment = (ChooserFragment) getChildFragmentManager().findFragmentById(R.id.chooser_fragment);
        if (chooserFragment != null) {
            chooserFragment.setUpChooserElements(getResources().getString(R.string.additional_switch_frequency_title),
                    getResources().getString(R.string.additional_switch_primary_frequency),
                    getResources().getString(R.string.additional_switch_secondary_frequency),
                    getResources().getString(R.string.additional_switch_tertiary_frequency), true, true, true, false);

            chooserFragment.setChooserCallback(this);
        }
    }

    private void setUpChooserSchemeType() {
        chooserSchemeTypeFragment = (ChooserSchemeTypeFragment) getChildFragmentManager().findFragmentById(R.id.chooser_scheme_type_fragment);
        if (chooserSchemeTypeFragment != null) {
            chooserSchemeTypeFragment.setUpChooserSchemeTypeElement(getResources().getString(R.string.additional_switch_scheme_type_title),
                    getResources().getString(R.string.additional_switch_scheme_growth_title),
                    getResources().getString(R.string.additional_switch_scheme_dividend_title),
                    true, true);
            chooserSchemeTypeFragment.setUpChooserCategoryElement(getResources().getString(R.string.additional_switch_category_title),
                    getResources().getString(R.string.category_chooser_equity),
                    getResources().getString(R.string.category_chooser_debt),
                    getResources().getString(R.string.category_chooser_fmp), true, true, true);

            chooserSchemeTypeFragment.setChooserTypeCallBack(this);
        }
    }

    @Override
    public void onClick(View view) {

    }
    private void onTransactBtnClick() {

        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            tilAmount.setError(getResources().getString(R.string.error_empty_amount));
            etAmount.setFocusable(true);
        }  else {
            callSwitchTransactionApi();
        }
    }
    @Override
    public void onItemChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            switch_type_value = "Amount";
            reedmption_type_value = "Unit";
            etAmount.setText("");
            etAmount.setEnabled(true);
            llEtAmounntContainer.setVisibility(View.VISIBLE);
        } else if (id == R.id.btn_secondary) {
            switch_type_value = "Unit";
            reedmption_type_value = "Unit";
            etAmount.setText("");
            etAmount.setEnabled(true);
            llEtAmounntContainer.setVisibility(View.VISIBLE);
        } else if (id == R.id.btn_tertiary) {
            switch_type_value = "AllUnit";
            reedmption_type_value = "AllUnit";
            etAmount.setText(tvUnitBalanceValue.getText());
            etAmount.setEnabled(false);
            llEtAmounntContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSchemeChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_growth) {
            scheme_type_value = "G";
            callSchemeListApi(category_value, scheme_type_value);
        } else if (id == R.id.btn_dividend) {
            scheme_type_value = "D";
            callSchemeListApi(category_value, scheme_type_value);
        }
    }


    @Override
    public void onSchemeCategoryChosen(int i) {
        category_value = mSession.getSchemeCategory();

        switch (i){
            case 0:
                callSchemeListApi(category_value,scheme_type_value);
                break;
            case 1:
                callSchemeListApi(category_value,scheme_type_value);
                break;
            case 2:

                callSchemeListApi(category_value,scheme_type_value);
                break;
            case 3:
                callSchemeListApi(category_value,scheme_type_value);
                break;

        }
    }


    /********************************Api Calling*******************************************/
    public void callSwitchTransactionApi() {

        DialogsUtils.showProgressBar(getActivity(), false);

        String url = Config.Additional_Switch;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUcc);
            jsonObject.put(AppConstants.KEY_BROKER_ID, bundle.getString(AppConstants.KEY_BROKER_ID));
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("FromScode", mScode);
            jsonObject.put("ToScode", to_scode_value);
            jsonObject.put("FolioNo", mFolioNum);
            jsonObject.put("Amount", etAmount.getText().toString());
            jsonObject.put("SwitchType", switch_type_value);
            jsonObject.put(AppConstants.PASSKEY, bundle.getString(AppConstants.PASSKEY));
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("DividendOption", "Z");
            jsonObject.put("IPAddress", "");
            jsonObject.put("LoggedInUser", mCid);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    String ServiceMSG = response.optString("ServiceMSG");
                    mApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.dialog_title_message), ServiceMSG, "message", true, true);


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
        } catch (Exception e) {
            e.printStackTrace();
        }

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


    }

    public void callSchemeListApi(String schemeCategory,String schemeTypValue) {
        shimmerSchemeContainer.setVisibility(View.VISIBLE);
        shimmerSchemeContainer.startShimmerAnimation();
        mSchemeContainer.setVisibility(View.GONE);

        try {

            String url = Config.Scheme_List;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put(AppConstants.KEY_BROKER_ID, bundle.getString(AppConstants.KEY_BROKER_ID));
            jsonObject.put("Fcode", bundle.getString("Fcode"));
            if (bundle != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.requireNonNull(bundle.getString("FolioNo")).contains("/")) {

                        String[] folio = bundle.getString("FolioNo").split("/");
                        jsonObject.put("FolioNo", folio[0]);
                    } else {
                        jsonObject.put("FolioNo", bundle.getString("FolioNo"));
                    }
                }
            }
            // mDataList.put("FolioNo", "1018409949");
            jsonObject.put("SchemeType", schemeCategory);
            jsonObject.put("Option", schemeTypValue);
            jsonObject.put("MyScheme", "N");
            jsonObject.put(AppConstants.PASSKEY, bundle.getString(AppConstants.PASSKEY));
            jsonObject.put("OnlineOption", mSession.getAppType());
            System.out.println("jsonvalue" + jsonObject);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    shimmerSchemeContainer.stopShimmerAnimation();
                    shimmerSchemeContainer.setVisibility(View.GONE);
                    mSchemeContainer.setVisibility(View.VISIBLE);
                    List<String> new_scheme = new ArrayList<>();
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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, new_scheme);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerMandate.setAdapter(dataAdapter);


                    } else {
                        if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                        } else {
                            mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    shimmerSchemeContainer.stopShimmerAnimation();
                    shimmerSchemeContainer.setVisibility(View.GONE);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);

    }
}

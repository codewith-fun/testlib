package investwell.client.fragment.transactions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
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
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ChooserFragment;
import investwell.client.fragment.others.ChooserSchemeTypeFragment;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class AdditonalStpTransactFragment extends Fragment implements View.OnClickListener, ChooserFragment.ChooserCallBack, ChooserSchemeTypeFragment.ChooserTypeCallback, ToolbarFragment.ToolbarCallback {
    private View view, vDivider;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    private ChooserFragment chooserFragment;
    private ChooserSchemeTypeFragment chooserSchemeTypeFragment;
    private AppSession mSession;
    private TextView tvInvestorName, tvUnitBalanceValue, tvFolioNumber, tvSchemeName, tvMarketValue;
    private EditText etAmount, etInstallment;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String[] MandateCode, Amount, Mandate, SchemeName, SchemeCode;
    private Bundle bundle;
    private Spinner spinnerMandate, spinnerScheme;
    private String[] folio;
    private ArrayList<String> list = new ArrayList<>();
    private Calendar c = Calendar.getInstance();
    private BottomSheetBehavior sheetBehavior;
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH) + 1;
    private ImageView ivEdit, ivCloseDialog;
    private CheckBox cbUntilCancel, cbFirstOrder;
    private CustomDialog customDialog;
    private ShimmerFrameLayout mShimmerViewContainer, shimmerSchemeContainer;
    private SwipeButton btnTransact;
    private LinearLayout llMiddleViewHeader;
    private String category_value = "D", scheme_type_value = "G", switch_type_value = "Monthly", scode, first_order_value = "Y", until_cancel_value = "Y";
    private String to_scode_value, TValue, reedmption_type_value = "Amount", mCid, mUcc, mFolioNum, mFcode, mScode;
    private ScrollView svStp;
    private TextInputLayout tilAmount, tilInstallments;
    private LinearLayout mSchemeContainer;

    private Spinner mSpDate, mSpMonth;
    private ArrayList<String> mDasyList;
    private String  mSeletedDate = "";
    TextView tvLevelDay, tvLevelMonthYear;
    private String mDateAfter7days = "";

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
    private AdapterView.OnItemSelectedListener onSchemeItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            to_scode_value = SchemeCode[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.btn_add_new) {
            mActivity.displayViewOther(0, null);
        }
    }

    private CompoundButton.OnCheckedChangeListener cbFirstOrderCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                first_order_value = "Y";
            } else {

                first_order_value = "N";
            }
        }
    };

    public AdditonalStpTransactFragment() {
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
        view = inflater.inflate(R.layout.fragment_additional_stp, container, false);
        setUpToolBar();
        setInitializer(view);
        getDataFromBundle();
        setUpChooserFragment();
        setUpChooserSchemeType();
        setListeners();
        callSchemeListApi(category_value, scheme_type_value);
        disclaimerUi(view);
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_stp), true, false, false, true, false, false, false, "home");
            fragToolBar.setCallback(this);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity, R.color.colorPrimary));

            }
        }
    }

    private void disclaimerUi(View view) {
        TextView tvDisc = view.findViewById(R.id.tv_disc_title);
        String spanText = "You may need to confirm this transaction via link / OTP received on your Email / Mobile";
        tvDisc.setText(spanText);
        tvDisc.setSelected(true);
    }

    private void setInitializer(View view) {
        bundle = getArguments();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        shimmerSchemeContainer = view.findViewById(R.id.shimmer_scheme_container);
        mSchemeContainer = view.findViewById(R.id.ll_scheme_container);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        tvFolioNumber = view.findViewById(R.id.tv_folio_no);
        tvInvestorName = view.findViewById(R.id.tv_investor_name);
        tvSchemeName = view.findViewById(R.id.tv_scheme_name);
        tvUnitBalanceValue = view.findViewById(R.id.tv_unit_balance_value);
        tvMarketValue = view.findViewById(R.id.tv_market_value);
        etAmount = view.findViewById(R.id.et_amnt);
        etInstallment = view.findViewById(R.id.et_installment);
        spinnerScheme = view.findViewById(R.id.spinner_scheme);
        llMiddleViewHeader = view.findViewById(R.id.ll_middle_view_data);
        chooserSchemeTypeFragment = (ChooserSchemeTypeFragment) getChildFragmentManager().findFragmentById(R.id.frag_chooser_scheme_type);
        cbFirstOrder = view.findViewById(R.id.cb_first_order);
        vDivider = view.findViewById(R.id.v_divider);
        btnTransact = view.findViewById(R.id.btn_swipe_transact);
        svStp = view.findViewById(R.id.sv_stp);
        tilAmount = view.findViewById(R.id.til_amount);
        tilInstallments = view.findViewById(R.id.til_installment);

        tvLevelDay = view.findViewById(R.id.tvLevelDay);
        tvLevelMonthYear = view.findViewById(R.id.tvLevelMonthYear);
        tvLevelMonthYear.setText(getString(R.string.stp_month_and_year));
        tvLevelDay.setText(getString(R.string.stp_day));
        mSpDate = view.findViewById(R.id.spDate);
        mSpMonth = view.findViewById(R.id.spMonth);

        mDasyList = new ArrayList<>();
    }

    private void setListeners() {
        spinnerScheme.setOnItemSelectedListener(onSchemeItemSelected);
        btnTransact.setOnActiveListener(btnTransactOnActiveListener);
        cbFirstOrder.setOnCheckedChangeListener(cbFirstOrderCheckedListener);
        etAmount.addTextChangedListener(textWatcher);
        etInstallment.addTextChangedListener(textWatcher);
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

    private void onTransactBtnClick() {
        Date currentDate = null;
        Date dateAfter7Days = null;

        String monthYear = mSpMonth.getSelectedItem().toString();
        String day = mSpDate.getSelectedItem().toString();
        String completeDate = day + " " + monthYear;
        SimpleDateFormat monthParse = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat monthDisplay = new SimpleDateFormat("dd/MM/yyyy");
        try {
            mSeletedDate = monthDisplay.format(monthParse.parse(completeDate));
            dateAfter7Days = monthDisplay.parse(mDateAfter7days);
            currentDate = monthDisplay.parse(mSeletedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            tilAmount.setError(getResources().getString(R.string.error_empty_amount));
            etAmount.setFocusable(true);
        } else if (TextUtils.isEmpty(etInstallment.getText().toString())) {

            tilInstallments.setError(getResources().getString(R.string.error_empty_installment));
            etInstallment.setFocusable(true);
        } else if (Integer.parseInt(etInstallment.getText().toString()) > 900) {
            mApplication.showSnackBar(view, getResources().getString(R.string.error_invalid_installment));

        } else {
            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                if (currentDate.getTime() < dateAfter7Days.getTime()) {
                    Toast.makeText(mActivity, getString(R.string.stp_date_will_be_7_days_ahead), Toast.LENGTH_SHORT).show();
                }else{
                    callStpTransactionApi();
                }
            }else{
                callStpTransactionApi();
            }
        }
    }

    private void getDataFromBundle() {
        bundle = getArguments();
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
        toggleCheckBoxes();

    }

    private void toggleCheckBoxes() {
        if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
            cbFirstOrder.setVisibility(View.GONE);
            first_order_value = "N";
        } else {
            cbFirstOrder.setVisibility(View.VISIBLE);
        }
    }

    private void setUpChooserFragment() {
        chooserFragment = (ChooserFragment) getChildFragmentManager().findFragmentById(R.id.chooser_fragment);
        chooserFragment = (ChooserFragment) getChildFragmentManager().findFragmentById(R.id.chooser_fragment);
        if (chooserFragment != null) {
            chooserFragment.setUpChooserElements(getResources().getString(R.string.additional_stp_dividend_txt),
                    getResources().getString(R.string.additional_swp_primary_frequency),
                    getResources().getString(R.string.additional_swp_secondary_frequency),
                    getResources().getString(R.string.additional_swp_tertiary_frequency), true, true, true, false);

            chooserFragment.setChooserCallback(this);
        }
    }

    private void setUpChooserSchemeType() {
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


    @Override
    public void onItemChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            switch_type_value = "MONTHLY";
        } else if (id == R.id.btn_secondary) {
            switch_type_value = "WEEKLY";
        } else if (id == R.id.btn_tertiary) {
            switch_type_value = "QUATERLY";
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

        switch (i) {
            case 0:

                callSchemeListApi(category_value, scheme_type_value);

                break;
            case 1:

                callSchemeListApi(category_value, scheme_type_value);

                break;
            case 2:

                callSchemeListApi(category_value, scheme_type_value);

                break;
            case 3:

                callSchemeListApi(category_value, scheme_type_value);

                break;

        }
    }

    private void clearErrorMessages() {
        tilInstallments.setError("");
        tilAmount.setError("");
    }

    /**********************************************API Calling**********************/
    public void callStpTransactionApi() {
        DialogsUtils.showProgressBar(mActivity, false);

        String url = Config.Additional_STP;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUcc);
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("FromScode", mScode);
            jsonObject.put("ToScode", to_scode_value);
            jsonObject.put("FolioNo", mFolioNum);
            jsonObject.put("Amount", etAmount.getText().toString());
            DecimalFormat mFormat = new DecimalFormat("00");
            String Dates = "";
            /*if (month < 10) {
                Dates = spinnerDate.getSelectedItem() + "/" + mFormat.format(Double.valueOf(month)) + "/" + year;
            } else {
                Dates = spinnerDate.getSelectedItem() + "/" + month + "/" + year;
            }*/
            jsonObject.put("StartDate", mSeletedDate);
            jsonObject.put("MonthOption", "Y");
            jsonObject.put("Frequency", switch_type_value);
            jsonObject.put("Installment", etInstallment.getText().toString());
            jsonObject.put("FirstOrder", first_order_value);
            jsonObject.put("Passkey", bundle.getString("Passkey"));
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("DividendOption", "Z");
            jsonObject.put("ToDate", "");
            jsonObject.put("IPAddress", "");
            jsonObject.put("LoggedInUser", mCid);


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    String Status = response.optString("Status");
                    if (Status.equalsIgnoreCase("True")) {
                        String ServiceMSG = response.optString("ServiceMSG");
                        mApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.dialog_title_message), ServiceMSG, "message", true, true);

                    } else {
                        if (response.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showSnackBar(etAmount, response.optString("ServiceMSG"));
                        } else {
                            mApplication.showSnackBar(etAmount, response.optString("ServiceMSG"));
                        }
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

    public void callSchemeListApi(String category_value, String scheme_type_value) {
        shimmerSchemeContainer.setVisibility(View.VISIBLE);
        shimmerSchemeContainer.startShimmerAnimation();
        mSchemeContainer.setVisibility(View.GONE);

        try {

            String url = Config.Scheme_List;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUcc);
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("FolioNo", mFolioNum);
            jsonObject.put("SchemeType", category_value);
            jsonObject.put("Option", scheme_type_value);
            jsonObject.put("MyScheme", "N");
            jsonObject.put("Passkey", bundle.getString("Passkey"));
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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, new_scheme);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerScheme.setAdapter(dataAdapter);


                    } else {
                        shimmerSchemeContainer.stopShimmerAnimation();
                        shimmerSchemeContainer.setVisibility(View.GONE);
                        String ServiceMSG = jsonObject.optString("ServiceMSG");

                        mApplication.showSnackBar(etAmount, ServiceMSG);
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

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            callSipDateApi();
        }
        requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);

    }

    public void callSipDateApi() {


        String url = Config.Sip_Dates;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", bundle.getString("Passkey"));
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Exlcode", bundle.getString("ExcelCode"));
            jsonObject.put("TranType", "STP");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    try {
                        String Status = jsonObject.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {
                            String Dates = jsonObject.optString("Dates");
                            List<String> DateList = Arrays.asList(Dates.split(","));

                            for (int i = 0; i < DateList.size(); i++) {
                                DecimalFormat formatter = new DecimalFormat("00");
                                int data = Integer.parseInt(DateList.get(i));
                                String formatDay = formatter.format(data);
                                mDasyList.add(formatDay);
                            }
                            if (mDasyList.size() == 0) {
                                DecimalFormat formatter = new DecimalFormat("00");
                                int data = 0;
                                for (int i = 1; i < 29; i++) {
                                    data = i;
                                }
                                String formatDay = formatter.format(data);
                                mDasyList.add(formatDay);
                            }
                            setDate();
                            setMonth();
                        } else {
                            String ServiceMSG = jsonObject.optString("ServiceMSG");
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mApplication.showSnackBar(etAmount, "" + ServiceMSG);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                    }
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


    private void setDate() {
        try {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item2, mDasyList);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown2);
            mSpDate.setAdapter(dataAdapter);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String[] date = null;
            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                calendar.add(Calendar.DATE, 7);
                mDateAfter7days = simpleDateFormat.format(calendar.getTime());
                date = mDateAfter7days.split("/");
            } else {
                String todayDate = simpleDateFormat.format(calendar.getTime());
                date = todayDate.split("/");
            }


            int spinnerPosition = 0;
            if (mDasyList.contains(date[0])) {
                spinnerPosition = dataAdapter.getPosition(date[0]);
                mSpDate.setSelection(spinnerPosition);
            } else {
                for (int i = 0; i < mDasyList.size(); i++) {
                    int valueDate = Integer.parseInt(date[0]);
                    if (Integer.parseInt(mDasyList.get(i)) >= valueDate) {
                        spinnerPosition = i;
                        mSpDate.setSelection(spinnerPosition);
                        break;
                    } else {
                        spinnerPosition = 0;
                        mSpDate.setSelection(spinnerPosition);
                    }
                }
            }


        } catch (Exception e) {
        }

    }

    private void setMonth() {
        try {
            List<String> monthyearList = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy");
            Calendar calendar = Calendar.getInstance();

            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                calendar.add(Calendar.DATE, 8);
                String firstMonth = simpleDateFormat.format(calendar.getTime());
                monthyearList.add(firstMonth);
            }else{
                String firstMonth = simpleDateFormat.format(calendar.getTime());
                monthyearList.add(firstMonth);
            }

            calendar.add(Calendar.MONTH, 1);
            String secondMonth = simpleDateFormat.format(calendar.getTime());
            monthyearList.add(secondMonth);

            calendar.add(Calendar.MONTH, 1);
            String thirdMonth = simpleDateFormat.format(calendar.getTime());
            monthyearList.add(thirdMonth);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item2, monthyearList);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown2);
            mSpMonth.setAdapter(dataAdapter);

        } catch (Exception e) {

        }
    }
}
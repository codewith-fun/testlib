package investwell.client.fragment.transactions;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
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
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class AdditionalSipTransactFragment extends Fragment implements View.OnClickListener, ChooserFragment.ChooserCallBack, ChooserSchemeTypeFragment.ChooserTypeCallback, CustomDialog.DialogBtnCallBack, ToolbarFragment.ToolbarCallback {
    private View view, vDivider;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    private ChooserFragment chooserFragment;
    private ChooserSchemeTypeFragment chooserSchemeTypeFragment;
    private AppSession mSession;
    private TextView tvInvestorName, tvUnitBalanceValue, tvFolioNumber, tvSchemeName, tvMarketValue;
    private TextInputEditText etAmount, etInstallment;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String[] MandateCode, Amount, Mandate, SchemeName, SchemeCode;
    private Bundle bundle;
    private Spinner spinnerMandate, spinnerScheme;
    private String[] folio;
    private ArrayList<String> list = new ArrayList<>();
    private Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH) + 1;
    private BottomSheetBehavior sheetBehavior;
    private ImageView ivEdit, ivCloseDialog;
    private CheckBox cbUntilCancel, cbFirstOrder;
    private CustomDialog customDialog;
    private SwipeButton btnTransact;
    private Button btnApplySchemeChange;
    private ShimmerFrameLayout mShimmerViewContainer, shimmerSchemeContainer;

    private LinearLayout llMiddleViewHeader, ll_et_container, lletInstallMentContainer;
    private RelativeLayout rlBottomsheetSchemeEdit;
    private String category_value = "D", Mandate_code_value, mScheme_name, scheme_type_value = "G", switch_type_value = "Monthly", scode, first_order_value = "Y", until_cancel_value = "Y";
    private String mCid, mUcc, mFolioNum, mFcode, mScode;
    private CardView cvHeaderCardData;
    private TextInputLayout tilAmount, tilInstallments;

    private Spinner mSpDate, mSpMonth;
    private ArrayList<String> mDasyList;
    private String  mSeletedDate = "";
    TextView tvLevelDay, tvLevelMonthYear;
    private String mDateAfter7days = "";


    private AdapterView.OnItemSelectedListener onSchemeItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mScode = SchemeCode[position];
            mScheme_name = SchemeName[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private AdapterView.OnItemSelectedListener onMandateItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Mandate_code_value = MandateCode[position];
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

    private CompoundButton.OnCheckedChangeListener cbUntilChangeCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {

                until_cancel_value = "Y";
                etInstallment.setText("0");
                lletInstallMentContainer.setVisibility(View.GONE);
            } else {
                until_cancel_value = "N";
                etInstallment.setEnabled(true);
                etInstallment.setText("");
                etInstallment.requestFocus();
                lletInstallMentContainer.setVisibility(View.VISIBLE);
            }
        }
    };

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
    private OnActiveListener btnTransactOnActiveListener = new OnActiveListener() {
        @Override
        public void onActive() {
            onTransactBtnClick();
            if (btnTransact.isActive()) {
                btnTransact.toggleState();
            }
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

    public AdditionalSipTransactFragment() {
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

    private LinearLayout mSchemeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_additional_sip, container, false);
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
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_sip), true, false, false, true, false, false, false, "home");
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
        spinnerMandate = view.findViewById(R.id.spinner_mandate);
        spinnerScheme = view.findViewById(R.id.spinner_scheme);
        llMiddleViewHeader = view.findViewById(R.id.ll_middle_view_data);
        rlBottomsheetSchemeEdit = view.findViewById(R.id.rl_bottomsheet_scheme_edit);
        rlBottomsheetSchemeEdit.setVisibility(View.GONE);
        ll_et_container = view.findViewById(R.id.ll_et_container);
        lletInstallMentContainer = view.findViewById(R.id.ll_et_installment_container);
        sheetBehavior = BottomSheetBehavior.from(rlBottomsheetSchemeEdit);
        chooserSchemeTypeFragment = (ChooserSchemeTypeFragment) getChildFragmentManager().findFragmentById(R.id.frag_chooser_scheme_type);
        btnApplySchemeChange = view.findViewById(R.id.btn_scheme_apply);
        ivEdit = view.findViewById(R.id.iv_scheme_edit);
        ivEdit.setVisibility(View.VISIBLE);
        cbFirstOrder = view.findViewById(R.id.cb_first_order);
        cbUntilCancel = view.findViewById(R.id.cb_until_cancel);
        ivCloseDialog = view.findViewById(R.id.iv_close_dialog);
        btnTransact = view.findViewById(R.id.btn_swipe_transact);
        vDivider = view.findViewById(R.id.v_divider);
        tilAmount = view.findViewById(R.id.til_amount);
        tilInstallments = view.findViewById(R.id.til_installment);
        cvHeaderCardData = view.findViewById(R.id.cv_add_transac_header);
        cvHeaderCardData.setVisibility(View.GONE);
        customDialog = new CustomDialog(this);
        tvFolioNumber.setVisibility(View.VISIBLE);
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            ivEdit.setEnabled(false);
            etAmount.setEnabled(false);
        } else {
            ivEdit.setEnabled(true);
            etAmount.setEnabled(true);
        }
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        ivEdit.setEnabled(false);
                        ivEdit.setOnClickListener(null);
                        etAmount.setEnabled(false);
                        etAmount.setOnClickListener(null);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        ivEdit.setEnabled(true);
                        ivEdit.setOnClickListener(AdditionalSipTransactFragment.this);
                        etAmount.setEnabled(true);
                        etAmount.setOnClickListener(null);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        tvLevelDay = view.findViewById(R.id.tvLevelDay);
        tvLevelMonthYear = view.findViewById(R.id.tvLevelMonthYear);
        tvLevelMonthYear.setText(getString(R.string.sip_month_and_year));
        tvLevelDay.setText(getString(R.string.sip_day));
        mSpDate = view.findViewById(R.id.spDate);
        mSpMonth = view.findViewById(R.id.spMonth);
        mDasyList = new ArrayList<>();
    }

    private void setListeners() {
        spinnerScheme.setOnItemSelectedListener(onSchemeItemSelected);
        spinnerMandate.setOnItemSelectedListener(onMandateItemSelected);
        ivEdit.setOnClickListener(this);
        ivCloseDialog.setOnClickListener(this);
        btnApplySchemeChange.setOnClickListener(this);
        cbUntilCancel.setOnCheckedChangeListener(cbUntilChangeCheckedListener);
        cbFirstOrder.setOnCheckedChangeListener(cbFirstOrderCheckedListener);
        btnTransact.setOnActiveListener(btnTransactOnActiveListener);
        etAmount.addTextChangedListener(textWatcher);
        etInstallment.addTextChangedListener(textWatcher);

    }

    private void clearErrorMessages() {
        tilInstallments.setError("");
        tilAmount.setError("");
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
            mScheme_name = bundle.getString("colorBlue");
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
        callMandateListApi();
    }

    private void toggleCheckBoxes() {
        if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
            cbFirstOrder.setVisibility(View.VISIBLE);
            cbUntilCancel.setVisibility(View.VISIBLE);
        } else {
            cbFirstOrder.setVisibility(View.VISIBLE);
            cbUntilCancel.setVisibility(View.VISIBLE);
        }
    }

    private void setUpChooserFragment() {
        chooserFragment = (ChooserFragment) getChildFragmentManager().findFragmentById(R.id.chooser_fragment);
        if (chooserFragment != null) {
            chooserFragment.setUpChooserElements(getResources().getString(R.string.additional_sip_frequency_title),
                    getResources().getString(R.string.additional_sip_primary_frequency),
                    getResources().getString(R.string.additional_sip_secondary_frequency),
                    "", true, true, false, false);

            chooserFragment.setChooserCallback(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    private void onSchemeChangeBtnClick() {
        scode = mScode;
        tvSchemeName.setText(mScheme_name);
        closeBottomSheetDialog();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_scheme_edit) {
            toggleBottomSheetDialog();
        } else if (id == R.id.iv_close_dialog) {
            closeBottomSheetDialog();
            ivEdit.setEnabled(true);
        } else if (id == R.id.btn_scheme_apply) {
            onSchemeChangeBtnClick();
        }
    }

    private void closeBottomSheetDialog() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            rlBottomsheetSchemeEdit.setVisibility(View.GONE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            rlBottomsheetSchemeEdit.setVisibility(View.VISIBLE);

        }
    }

    private void toggleBottomSheetDialog() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            ivCloseDialog.setVisibility(View.VISIBLE);
            rlBottomsheetSchemeEdit.setVisibility(View.VISIBLE);


        } else {
            ivCloseDialog.setVisibility(View.GONE);
            rlBottomsheetSchemeEdit.setVisibility(View.GONE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }

    @Override
    public void onItemChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            switch_type_value = "Monthly";
        } else if (id == R.id.btn_secondary) {
            switch_type_value = "Weekly";
        }
    }

    private void onTransactBtnClick() {
        checksBeforeApiCall();

    }

    private void checksBeforeApiCall() {


        if (until_cancel_value.equalsIgnoreCase("Y")) {
            validateWithoutInstallmentField();
        } else {
            validateWithInstallmentsField();
        }
    }

    private void validateWithoutInstallmentField() {
        Date currentDate = null;
        Date dateAfter7Days = null;
        Date todayDate = null;

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

        } else {
            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                if (currentDate.getTime() < dateAfter7Days.getTime()) {
                    Toast.makeText(mActivity, getString(R.string.sip_date_will_be_7_days_ahead), Toast.LENGTH_SHORT).show();
                }else{
                    callSipTransactionApi();
                }
            }else{
                callSipTransactionApi();
            }

        }
    }

    private void validateWithInstallmentsField() {
        Date currentDate = null;
        Date dateAfter7Days = null;
        Date todayDate = null;

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
            tilAmount.setFocusable(true);

        } else if (TextUtils.isEmpty(etInstallment.getText().toString())) {
            tilInstallments.setError(getResources().getString(R.string.error_empty_installment));
            tilInstallments.setFocusable(true);

        } else if (Integer.parseInt(etInstallment.getText().toString()) > 900) {
            tilInstallments.setError(getResources().getString(R.string.error_invalid_installment));
            tilInstallments.setFocusable(true);
        } else {
            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                if (currentDate.getTime() < dateAfter7Days.getTime()) {
                    Toast.makeText(mActivity, getString(R.string.sip_date_will_be_7_days_ahead), Toast.LENGTH_SHORT).show();
                }else{
                    callSipTransactionApi();
                }
            }else{
                callSipTransactionApi();
            }

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

    @Override
    public void onDialogBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btDone) {
            Bundle bundle = new Bundle();
            bundle.putString("ucc_code", mUcc);
            mActivity.displayViewOther(12, bundle);
        } else if (id == R.id.btCalcel) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

    /********************************Api Calling*******************************************/
    private void callSchemeListApi(String schemeCategory, String schemeType) {
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
            if (bundle.getString("FolioNo").contains("/")) {
                String[] folio = bundle.getString("FolioNo").split("/");
                jsonObject.put("FolioNo", folio[0]);
            } else {
                jsonObject.put("FolioNo", bundle.getString("FolioNo"));
            }
            jsonObject.put("SchemeType", schemeCategory);
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
                    String Status = jsonObject.optString("Status");
                    if (Status.equalsIgnoreCase("True")) {
                        JSONArray SchemeListDetail = jsonObject.optJSONArray("SchemeListDetail");
                        SchemeName = new String[SchemeListDetail.length()];
                        SchemeCode = new String[SchemeListDetail.length()];
                        for (int i = 0; i < SchemeListDetail.length(); i++) {
                            JSONObject jsonObject1 = SchemeListDetail.optJSONObject(i);
                            SchemeName[i] = jsonObject1.optString("SchemeName");
                            SchemeCode[i] = jsonObject1.optString("SchemeCode");

                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_dropdown, SchemeName);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerScheme.setAdapter(dataAdapter);


                    } else {
                        if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showSnackBar(etAmount, jsonObject.optString("ServiceMSG"));
                        } else {
                            mApplication.showSnackBar(etAmount, jsonObject.optString("ServiceMSG"));
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    shimmerSchemeContainer.stopShimmerAnimation();
                    shimmerSchemeContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showSnackBar(etAmount, error.getLocalizedMessage());

                    } else {
                        mApplication.showSnackBar(etAmount, getResources().getString(R.string.no_internet));

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
        requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }

    private void callSipDatesApi() {

        String url = Config.Sip_Dates;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", bundle.getString("Passkey"));
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Exlcode", bundle.getString("ExcelCode"));
            jsonObject.put("TranType", "SIP");
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

                            mApplication.showSnackBar(etAmount, ServiceMSG);
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
                        mApplication.showSnackBar(etAmount, error.getLocalizedMessage());
                    } else {
                        mApplication.showSnackBar(etAmount, getResources().getString(R.string.no_internet));
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


    private void callMandateListApi() {

        String url = Config.MAndate_List;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", bundle.getString("UCC"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {

                        String Status = jsonObject.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("SIPMandateDetail");
                            MandateCode = new String[jsonArray.length()];
                            Amount = new String[jsonArray.length()];
                            Mandate = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                MandateCode[i] = jsonObject1.optString("MandateCode");
                                Amount[i] = jsonObject1.optString("Amount");
                                Mandate[i] = jsonObject1.optString("Mandate");
                                list.add(0, Mandate[i]);
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, Mandate);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerMandate.setAdapter(dataAdapter);
                        } else {
                            //  String ServiceMSG = mDataList.optString("ServiceMSG");
                            //Toast.makeText(mActivity, "" + ServiceMSG, Toast.LENGTH_SHORT).show();
                            customDialog.showDialog(getActivity(), getResources().getString(R.string.dialog_no_mandate_header_txt),
                                    getResources().getString(R.string.dialog_no_mandate_mini_header_txt),
                                    getResources().getString(R.string.dialog_no_mandate_btn_proceed_txt),
                                    getResources().getString(R.string.dialog_no_mandate_btn_cancel_txt), true, true);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        callSipDatesApi();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showSnackBar(etAmount, error.getLocalizedMessage());
                    } else {
                        mApplication.showSnackBar(etAmount, getResources().getString(R.string.no_internet));

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

    public void callSipTransactionApi() {
        DialogsUtils.showProgressBar(mActivity, false);

        try {
            String month_length;
            month_length = String.valueOf(month);
            if (month_length.length() < 2) {
                month = Integer.parseInt("0") + month;
            }
            String url = Config.Additional_Sip;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUcc);
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("Scode", mScode);
            jsonObject.put("FolioNo", mFolioNum);
            if (!TextUtils.isEmpty(etAmount.getText())) {
                jsonObject.put("Amount", etAmount.getText());
            }
            jsonObject.put("StartDate", mSeletedDate);
            jsonObject.put("MonthOption", "Y");
            jsonObject.put("Frequency", switch_type_value);
            if (!TextUtils.isEmpty(etInstallment.getText().toString())) {
                jsonObject.put("Installment", etInstallment.getText());
            } else {
                jsonObject.put("Installment", 0);
            }
            jsonObject.put("FirstOrder", first_order_value);
            jsonObject.put("MandateID", Mandate_code_value);
            jsonObject.put("Passkey", bundle.getString("Passkey"));
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("DividendOption", "Z");
            jsonObject.put("UntilCancel", until_cancel_value);
            jsonObject.put("IPAddress", "");
            jsonObject.put("LoggedInUser", mCid);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    try {
                        if (response.optString("Status").equalsIgnoreCase("True")) {
                            String ServiceMSG = response.optString("ServiceMSG");
                            mApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.dialog_title_message), ServiceMSG, "message", true, true);

                        } else {
                            String ServiceMSG = response.optString("ServiceMSG");
                            mApplication.showSnackBar(etAmount, ServiceMSG);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showSnackBar(etAmount, error.getLocalizedMessage());

                    } else {
                        mApplication.showSnackBar(etAmount, getResources().getString(R.string.no_internet));

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

        requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
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

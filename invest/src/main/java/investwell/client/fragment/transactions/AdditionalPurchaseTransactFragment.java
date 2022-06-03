package investwell.client.fragment.transactions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
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
import com.iw.acceleratordemo.R;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.PaymentForAdditionalPurchage;
import investwell.client.fragment.others.ChooserFragment;
import investwell.client.fragment.others.ChooserSchemeTypeFragment;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class AdditionalPurchaseTransactFragment extends Fragment implements View.OnClickListener, ChooserFragment.ChooserCallBack, ChooserSchemeTypeFragment.ChooserTypeCallback, CustomDialog.DialogBtnCallBack, ToolbarFragment.ToolbarCallback {
    private final long DELAY = 700; // milliseconds
    private View view, vDivider;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    private ChooserFragment chooserFragment;
    private ChooserSchemeTypeFragment chooserSchemeTypeFragment;
    private AppSession mSession;
    private TextView tvInvestorName, tvUnitBalanceValue, tvFolioNumber, tvSchemeName, tvMarketValue;
    private EditText etAmount, etInstallment, etAccountNo, etChequeDate, etInstBankSecondary, etInstBranchSecondary, etMicrSecondary, etChequeNo;
    private EditText etRtgsCode, etMicrNo, etInstBankPrimary, etInstBrnachPrimary, etUtrNo, etTransferDate;
    private RequestQueue requestQueue;
    private JSONObject mSelectedBankObject, mSelectedMandateObject;
    private JsonObjectRequest jsonObjectRequest;
    private String[] MandateCode, Amount, Mandate, SchemeName, SchemeCode;
    private Bundle bundle;
    private Spinner spinnerMandate, spinnerScheme, spinnerBank;
    private String[] folio;
    private TextView tvServiceMessage;
    private BottomSheetBehavior sheetBehavior;
    private ImageView ivEdit, ivCloseDialog;
    private Button[] btn = new Button[2];
    private Button btn_unfocus, btnSendMeLinkOnMail, btnPayNow;
    private CardView cvHeaderCardData;
    private int[] btn_id = {R.id.btn_send_me_link_on_mail, R.id.btn_pay_now};
    private List<JSONObject> mBankList, mMendateList;
    private CustomDialog customDialog;
    private SwipeButton btnTransact;
    private ShimmerFrameLayout mShimmerViewContainer, shimmerSchemeContainer;
    private Button btnApplySchemeChange;
    private LinearLayout llMiddleViewHeader, ll_et_container, llBankBtnContainer, llFooterSectionOne, llFooterSectionTwo, llMandateSection, llNseContainer;
    private RelativeLayout rlBottomsheetSchemeEdit;
    private String category_value = "E", Mandate_code_value, mScheme_name, scheme_type_value = "G", switch_type_value = "Amount", scode, first_order_value = "Y", until_cancel_value = "Y";
    private String transact = "Transact Now", netbanking_option = "", payment_mode = "OL", to_scode_value, TValue, reedmption_type_value = "Amount", mCid, mUcc, mFolioNum, mFcode, mScode;
    private Timer timer = new Timer();
    private String schemeCategory = "D";
    private LinearLayout llBank;

    private TextInputLayout tilAmount, tilMicr, tilMicrSecondary, tilInstBank, tilInstBankSec, tilInstBrnch, tilInstBrnchSec, tilChequeDate, tilChequeNo, tilTransferDate, tilUtrNo, tilRtgs, tilAccNo;
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
    private LinearLayout mSchemeContainer;
    private CompoundButton.OnCheckedChangeListener cbUntilChangeCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {

                until_cancel_value = "Y";
                etInstallment.setText("0");
                vDivider.setVisibility(View.GONE);
                etInstallment.setVisibility(View.GONE);
            } else {
                until_cancel_value = "N";
                etInstallment.setEnabled(true);
                etInstallment.setText("");
                etInstallment.requestFocus();
                etInstallment.setVisibility(View.VISIBLE);
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
    private TextWatcher dateTextWatcher = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!charSequence.toString().equals(current)) {
                String clean = charSequence.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int j = 2; j <= cl && j < 6; j += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day = Integer.parseInt(clean.substring(0, 2));
                    int mon = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon - 1);
                    year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                    clean = String.format("%02d%02d%02d", day, mon, year);
                }

                clean = String.format("%s-%s-%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                etTransferDate.setText(current);
                etTransferDate.setSelection(sel < current.length() ? sel : current.length());
                etChequeDate.setText(current);
                etChequeDate.setSelection(sel < current.length() ? sel : current.length());
            }
            clearErrorMessages();
        }

        @Override
        public void afterTextChanged(Editable editable) {

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

    public AdditionalPurchaseTransactFragment() {
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
        view = inflater.inflate(R.layout.fragment_additional_purchase, container, false);
        setUpToolBar();
        setInitializer(view);
        getDataFromBundle();
        if (payment_mode.equalsIgnoreCase("OL")) {
            llBankBtnContainer.setVisibility(View.VISIBLE);
        } else {
            llBankBtnContainer.setVisibility(View.GONE);
        }
        setUpChooserFragment();
        setUpChooserSchemeType();
        setListeners();
        disclaimerUi(view);
        return view;
    }

    private void disclaimerUi(View view) {
        TextView tvDisc = view.findViewById(R.id.tv_disc_title);
        String spanText = "You may need to confirm this transaction via link / OTP received on your Email / Mobile";
        tvDisc.setText(spanText);
        tvDisc.setSelected(true);
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_add_purchase), true, false, false, true, false, false, false, "home");
            fragToolBar.setCallback(this);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity, R.color.colorPrimary));

            }

        }
    }

    private void setInitializer(View view) {
        bundle = getArguments();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        shimmerSchemeContainer = view.findViewById(R.id.shimmer_scheme_container);
        llBank = view.findViewById(R.id.llBank);
      /*  mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();*/
        tvFolioNumber = view.findViewById(R.id.tv_folio_no);
        tvInvestorName = view.findViewById(R.id.tv_investor_name);
        tvSchemeName = view.findViewById(R.id.tv_scheme_name);
        tvUnitBalanceValue = view.findViewById(R.id.tv_unit_balance_value);
        tvMarketValue = view.findViewById(R.id.tv_market_value);
        etAmount = view.findViewById(R.id.et_amnt);
        mSchemeContainer = view.findViewById(R.id.ll_scheme_container);
        etAccountNo = view.findViewById(R.id.et_account_no);
        etChequeNo = view.findViewById(R.id.et_cheque_no);
        etChequeDate = view.findViewById(R.id.et_cheque_date);
        etMicrNo = view.findViewById(R.id.et_micr);
        etMicrSecondary = view.findViewById(R.id.et_micr_no_secondary);
        etInstBankPrimary = view.findViewById(R.id.et_instrument_bank);
        etInstBankSecondary = view.findViewById(R.id.et_instrument_bank_secondary);
        etInstBrnachPrimary = view.findViewById(R.id.et_instrument_branch);
        etInstBranchSecondary = view.findViewById(R.id.et_instrument_branch_secondary);
        etRtgsCode = view.findViewById(R.id.et_rtgs);
        etTransferDate = view.findViewById(R.id.et_transfer_date);
        etUtrNo = view.findViewById(R.id.et_utr_no);
        spinnerMandate = view.findViewById(R.id.spinner_mandate);
        spinnerScheme = view.findViewById(R.id.spinner_scheme);
        spinnerBank = view.findViewById(R.id.spinner_bank);
        llMiddleViewHeader = view.findViewById(R.id.ll_middle_view_data);
        rlBottomsheetSchemeEdit = view.findViewById(R.id.rl_bottomsheet_scheme_edit);
        rlBottomsheetSchemeEdit.setVisibility(View.GONE);
        ll_et_container = view.findViewById(R.id.ll_et_container);
        sheetBehavior = BottomSheetBehavior.from(rlBottomsheetSchemeEdit);
        chooserSchemeTypeFragment = (ChooserSchemeTypeFragment) getChildFragmentManager().findFragmentById(R.id.frag_chooser_scheme_type);
        btnApplySchemeChange = view.findViewById(R.id.btn_scheme_apply);
        ivEdit = view.findViewById(R.id.iv_scheme_edit);
        ivEdit.setVisibility(View.VISIBLE);
        btnPayNow = view.findViewById(R.id.btn_pay_now);
        btnSendMeLinkOnMail = view.findViewById(R.id.btn_send_me_link_on_mail);
        btnTransact = view.findViewById(R.id.btn_swipe_transact);
        ivCloseDialog = view.findViewById(R.id.iv_close_dialog);
        vDivider = view.findViewById(R.id.v_divider);
        customDialog = new CustomDialog(this);
        llBankBtnContainer = view.findViewById(R.id.ll_bank_btn_container);
        llBankBtnContainer.setVisibility(View.GONE);
        cvHeaderCardData = view.findViewById(R.id.cv_add_transac_header);
        cvHeaderCardData.setVisibility(View.GONE);
        llFooterSectionOne = view.findViewById(R.id.ll_footer_section_one);
        llFooterSectionOne.setVisibility(View.GONE);
        llFooterSectionTwo = view.findViewById(R.id.ll_footer_section_two);
        llFooterSectionTwo.setVisibility(View.GONE);
        llMandateSection = view.findViewById(R.id.ll_mandate_section);
        llMandateSection.setVisibility(View.GONE);
        llNseContainer = view.findViewById(R.id.ll_nse_container);
        tvServiceMessage = view.findViewById(R.id.tv_service_msg);
        tilAmount = view.findViewById(R.id.til_amount);
        tilChequeDate = view.findViewById(R.id.til_cheque_date);
        tilChequeNo = view.findViewById(R.id.til_cheque_no);
        tilInstBank = view.findViewById(R.id.til_instrument_bank);
        tilInstBankSec = view.findViewById(R.id.til_instrument_bank_secondary);
        tilInstBrnch = view.findViewById(R.id.til_instrument_branch);
        tilInstBrnchSec = view.findViewById(R.id.til_instrument_branch_secondary);
        tilMicr = view.findViewById(R.id.til_micr);
        tilMicrSecondary = view.findViewById(R.id.til_micr_no_secondary);
        tilAccNo = view.findViewById(R.id.til_acc_no);
        tilUtrNo = view.findViewById(R.id.til_utr_no);
        tilRtgs = view.findViewById(R.id.til_rtgs);
        tilTransferDate = view.findViewById(R.id.til_transfer_date);
        bundle = getArguments();
    }

    private void clearErrorMessages() {
        tilChequeDate.setError("");
        tilChequeNo.setError("");
        tilInstBank.setError("");
        tilInstBankSec.setError("");
        tilInstBrnch.setError("");
        tilInstBrnchSec.setError("");
        tilMicr.setError("");
        tilMicrSecondary.setError("");
        tilAccNo.setError("");
        tilUtrNo.setError("");
        tilRtgs.setError("");
        tilTransferDate.setError("");
    }

    private void setListeners() {
        spinnerScheme.setOnItemSelectedListener(onSchemeItemSelected);
        spinnerMandate.setOnItemSelectedListener(onMandateItemSelected);
        ivEdit.setOnClickListener(this);
        ivCloseDialog.setOnClickListener(this);
        for (int i = 0; i < btn.length; i++) {
            btn[i] = view.findViewById(btn_id[i]);
            btn[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
            btn[i].setOnClickListener(this);
        }
        btn_unfocus = btn[0];
        btnApplySchemeChange.setOnClickListener(this);
        etAmount.addTextChangedListener(new GenericTextWatcher(etAmount));
        etChequeDate.addTextChangedListener(dateTextWatcher);
        etTransferDate.addTextChangedListener(dateTextWatcher);
        etAccountNo.addTextChangedListener(textWatcher);
        etAmount.addTextChangedListener(textWatcher);
        etInstBankPrimary.addTextChangedListener(textWatcher);
        etInstBranchSecondary.addTextChangedListener(textWatcher);
        etInstBankSecondary.addTextChangedListener(textWatcher);
        etInstBrnachPrimary.addTextChangedListener(textWatcher);
        etRtgsCode.addTextChangedListener(textWatcher);
        etUtrNo.addTextChangedListener(textWatcher);
        etMicrNo.addTextChangedListener(textWatcher);
        etMicrSecondary.addTextChangedListener(textWatcher);
        etChequeNo.addTextChangedListener(textWatcher);
        btnTransact.setOnActiveListener(btnTransactOnActiveListener);
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setTextColor(getResources().getColor(R.color.colorGrey_400));
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));
        } else {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
        }

        this.btn_unfocus = btn_focus;
    }

    private void onTransactBtnClick() {

        if (transact.equalsIgnoreCase("Transact Now")) {

            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_b)) || mSession.getAppType().equals(getResources().getString(R.string.apptype_db))) {
                if (etAmount.getText().toString().replaceAll(",", "").replaceAll(",", "").equals("") || etAmount.getText().toString().replaceAll(",", "").equals("0")) {

                    tilAmount.setError(getResources().getString(R.string.error_empty_amount));
                } else {
                    callAdditionalPurchaseApi();
                }
            } else {
                if (etAmount.getText().toString().replaceAll(",", "").replaceAll(",", "").equals("") || etAmount.getText().toString().replaceAll(",", "").equals("0")) {

                    tilAmount.setError(getResources().getString(R.string.error_empty_amount));
                } else if ((Long.parseLong(etAmount.getText().toString().replaceAll(",", ""))) < 100) {

                    tilAmount.setError(getString(R.string.error_invalid_amount));
                } else if (payment_mode.equals("")) {
                    mApplication.showSnackBar(etAmount, getResources().getString(R.string.add_pur_transact));
                } else {
                    if (payment_mode.equals("OL")) {
                        if (mSelectedBankObject == null) {

                            mApplication.showSnackBar(etAmount, getResources().getString(R.string.add_pur_transact_err_bank));
                        } else {
                            callAdditionalPurchaseApi();
                        }

                    } else if (payment_mode.equals("TR")) {
                        validationForRTGS();
                    } else if (payment_mode.equals("M")) {
                        callAdditionalPurchaseApi();
                    } else if (payment_mode.equals("Q")) {
                        validationForCheck();
                    }else if (payment_mode.equals("UPI")) {
                        callAdditionalPurchaseApi();
                    }
                }

            }
        } else {
          /*  Intent intent = new Intent(mActivity, PaymentActivity.class);
            intent.putExtra("ucc_code", bundle.getString("UCC"));
            intent.putExtra("type", "call_from_prospect");
            startActivityForResult(intent, 500);
*/

            Bundle bundle1 = new Bundle();
            bundle1.putString("ucc_code", bundle.getString("UCC"));
            bundle1.putString("type", "call_from_cleint");
            mActivity.displayViewOther(83, bundle1);

                   /* bundle.putString("type", "pay_now");
                     mActivity.displayViewOther(36, bundle);*/
        }
    }

    @SuppressLint("SetTextI18n")
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
            scode = mScode;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!TextUtils.isEmpty(bundle.getString("FolioNo"))) {
                    if (Objects.requireNonNull(bundle.getString("FolioNo")).contains("/")) {

                        folio = Objects.requireNonNull(bundle.getString("FolioNo")).split("/");
                        mFolioNum = folio[0];
                    } else {
                        mFolioNum = bundle.getString("FolioNo");
                    }
                    tvFolioNumber.setText(getResources().getString(R.string.add_transac_folio_no) + mFolioNum);
                    tvFolioNumber.setVisibility(View.VISIBLE);
                }
            } else {
                tvFolioNumber.setVisibility(View.GONE);
            }
        }

        toggleNseLayout();
    }

    private void toggleNseLayout() {
        if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
            view.findViewById(R.id.ll_nse_container).setVisibility(View.VISIBLE);
            callBankDetailsApi();
        } else {
            view.findViewById(R.id.ll_nse_container).setVisibility(View.GONE);
        }
    }


    private void setUpChooserFragment() {
        chooserFragment = (ChooserFragment) getChildFragmentManager().findFragmentById(R.id.chooser_fragment);
        if (chooserFragment != null) {
            chooserFragment.setUpChooserElements(getResources().getString(R.string.additional_purchase_frequency_title),
                    getResources().getString(R.string.additional_purchase_primary_frequency),
                    getResources().getString(R.string.additional_purchase_secondary_frequency),
                    getResources().getString(R.string.additional_purchase_tertiary_frequency), true, true, true, true);

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
        } else if (id == R.id.btn_scheme_apply) {
            onSchemeChangeBtnClick();
        } else if (id == R.id.btn_send_me_link_on_mail) {
            setFocus(btn_unfocus, btn[0]);
            netbanking_option = "Email";
        } else if (id == R.id.btn_pay_now) {
            setFocus(btn_unfocus, btn[1]);
            netbanking_option = "Immediate";
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
        callSchemeListApi(schemeCategory, scheme_type_value);
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

    private void validationForRTGS() {

        if (etRtgsCode.getText().toString().equals("")) {
            etRtgsCode.requestFocus();
            tilRtgs.setError(getResources().getString(R.string.error_empty_rtgs_no));
        } else if (etMicrNo.getText().toString().equals("")) {
            tilMicr.setError(getResources().getString(R.string.error_empty_micr_no));
            etMicrNo.requestFocus();
        } else if (etInstBankPrimary.getText().toString().equals("")) {
            tilInstBank.setError(getResources().getString(R.string.error_empty_bank_name));
            etInstBankPrimary.requestFocus();
        } else if (etInstBrnachPrimary.getText().toString().equals("")) {
            tilInstBrnch.setError(getResources().getString(R.string.error_empty_branch_name));
            etInstBrnachPrimary.requestFocus();
        } else if (etTransferDate.getText().toString().equals("")) {
            tilTransferDate.setError(getResources().getString(R.string.error_empty_transfer_date));
            etTransferDate.requestFocus();
        } else {
            callAdditionalPurchaseApi();
        }
    }

    private void validationForCheck() {

        if (etAccountNo.getText().toString().equals("")) {
            etAccountNo.requestFocus();
            tilAccNo.setError(getResources().getString(R.string.error_empty_acc_no));
        } else if (etChequeNo.getText().toString().equals("")) {
            tilChequeNo.setError(getResources().getString(R.string.error_empty_cheque_no));
            etChequeNo.requestFocus();
        } else if (etInstBankSecondary.getText().toString().equals("")) {
            tilInstBankSec.setError(getResources().getString(R.string.error_empty_bank_name));
            etInstBankSecondary.requestFocus();
        } else if (etInstBranchSecondary.getText().toString().equals("")) {
            tilInstBrnchSec.setError(getResources().getString(R.string.error_empty_branch_name));
            etInstBranchSecondary.requestFocus();
        } else if (etMicrSecondary.getText().toString().equals("")) {
            tilMicrSecondary.setError(getResources().getString(R.string.error_empty_utr_no));
            etMicrSecondary.requestFocus();
        } else if (etChequeDate.getText().toString().equals("")) {
            tilChequeDate.setError(getResources().getString(R.string.error_empty_cheque_date));
            etChequeDate.requestFocus();
        } else {
            callAdditionalPurchaseApi();

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            llBank.setVisibility(View.VISIBLE);
            llBankBtnContainer.setVisibility(View.VISIBLE);
            llFooterSectionOne.setVisibility(View.GONE);
            llFooterSectionTwo.setVisibility(View.GONE);
            llMandateSection.setVisibility(View.GONE);
            payment_mode = "OL";
            netbanking_option = "Immediate";
        } else if (id == R.id.btn_secondary) {
            llBank.setVisibility(View.VISIBLE);
            llBankBtnContainer.setVisibility(View.GONE);
            llFooterSectionOne.setVisibility(View.GONE);
            llFooterSectionTwo.setVisibility(View.GONE);
            llMandateSection.setVisibility(View.VISIBLE);
            payment_mode = "M";
            netbanking_option = "";
            callMandateListNseApi();
        } else if (id == R.id.btn_tertiary) {
            llBank.setVisibility(View.VISIBLE);
            llBankBtnContainer.setVisibility(View.GONE);
            llFooterSectionOne.setVisibility(View.VISIBLE);
            llFooterSectionTwo.setVisibility(View.GONE);
            llMandateSection.setVisibility(View.GONE);
            payment_mode = "TR";
            netbanking_option = "";
        } else if (id == R.id.btnUpi) {
            llBank.setVisibility(View.VISIBLE);
            llBankBtnContainer.setVisibility(View.GONE);
            llFooterSectionOne.setVisibility(View.GONE);
            llFooterSectionTwo.setVisibility(View.GONE);
            llMandateSection.setVisibility(View.GONE);
            payment_mode = "UPI";
            netbanking_option = "Immediate";
        }
    }

    @Override
    public void onSchemeChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_growth) {
            scheme_type_value = "G";
            callSchemeListApi(schemeCategory, scheme_type_value);
        } else if (id == R.id.btn_dividend) {
            scheme_type_value = "D";
            callSchemeListApi(schemeCategory, scheme_type_value);
        }
    }

    @Override
    public void onSchemeCategoryChosen(int i) {
        schemeCategory = mSession.getSchemeCategory();

        switch (i) {
            case 0:

                callSchemeListApi(schemeCategory, scheme_type_value);
                break;
            case 1:

                callSchemeListApi(schemeCategory, scheme_type_value);
                break;
            case 2:

                callSchemeListApi(schemeCategory, scheme_type_value);
                break;
            case 3:

                callSchemeListApi(schemeCategory, scheme_type_value);
                break;

        }
    }


    @Override
    public void onDialogBtnClick(View view) {

    }

    private void setBankName() {
        ArrayList<String> list = new ArrayList<String>();
        //   list.add("Select Bank");
        for (int i = 0; i < mBankList.size(); i++) {
            list.add(mBankList.get(i).optString("BankName") + " - " + mBankList.get(i).optString("AccountNo"));
        }


        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, list);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(spinner_value);
        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mSelectedBankObject = mBankList.get(i);
                if (payment_mode.equals("Q")) {
                    etAccountNo.setText(mSelectedBankObject.optString("AccountNo"));
                    etInstBankPrimary.setText(mSelectedBankObject.optString("BankName"));
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
        spinnerMandate.setAdapter(new MyAdapter(mActivity, R.layout.custom_spinner_view, mMendateList));
        spinnerMandate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedMandateObject = mMendateList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

    private void delayCall() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String rawValue = etAmount.getText().toString().replaceAll(",", "");
                                    convertIntoCurrencyFormat(rawValue, etAmount);
                                    if ((mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) && payment_mode.equals("M")) {
                                        // if (mMendateList != null && mMendateList.size() > 0)
                                        //setMandateListSpinner();
                                    }

                                }
                            });
                        }

                    }
                },
                DELAY
        );
    }

    /********************************API Callings***************************/
    private void callBankDetailsApi() {

        String url = Config.Bank_detail;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        mBankList = new ArrayList();
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            JSONArray GetNSEBankList = jsonObject.optJSONArray("GetNSEBankList");
                            for (int i = 0; i < GetNSEBankList.length(); i++) {
                                JSONObject jsonObject1 = GetNSEBankList.optJSONObject(i);
                                mBankList.add(jsonObject1);
                            }
                            setBankName();
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
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

    private void callSchemeListApi(String schemeCategory, String schemeType) {
        shimmerSchemeContainer.setVisibility(View.VISIBLE);
        shimmerSchemeContainer.startShimmerAnimation();
        mSchemeContainer.setVisibility(View.GONE);
        try {
            String url = Config.Scheme_List;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", bundle.getString("Fcode"));
            if (bundle.getString("FolioNo").contains("/")) {
                String[] folio = bundle.getString("FolioNo").split("/");
                jsonObject.put("FolioNo", folio[0]);
            } else {
                jsonObject.put("FolioNo", bundle.getString("FolioNo"));
            }
            jsonObject.put("SchemeType", schemeCategory);
            jsonObject.put("Option", schemeType);
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
                        shimmerSchemeContainer.stopShimmerAnimation();
                        shimmerSchemeContainer.setVisibility(View.GONE);
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
        requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }

    private void callMandateListNseApi() {
        String url = Config.GET_MENDATE_LIST;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", bundle.getString("UCC"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

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

    private void callAdditionalPurchaseApi() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }

        String url = Config.Additional_purchase;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode", bundle.getString("Fcode"));
            jsonObject.put("Scode", scode);
            if (bundle.getString("FolioNo").contains("/")) {
                String[] folio = bundle.getString("FolioNo").split("/");
                jsonObject.put("FolioNo", folio[0]);
            } else {
                jsonObject.put("FolioNo", bundle.getString("FolioNo"));
            }
            String rawValue = etAmount.getText().toString().replaceAll(",", "");
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
            jsonObject.put("InstrumentRTGSCode", etRtgsCode.getText().toString().trim());
            jsonObject.put("InstrumentBankMICR", etMicrNo.getText().toString().trim());
            jsonObject.put("InstrumentBank", etInstBankPrimary.getText().toString().trim());
            jsonObject.put("InstrumentBankBranch", etInstBrnachPrimary.getText().toString().trim());
            jsonObject.put("RTGSUtrNo", etUtrNo.getText().toString().trim());
            jsonObject.put("RTGSDate", etTransferDate.getText().toString().trim());

            jsonObject.put("ChequeAccountNo", etAccountNo.getText().toString().trim());
            jsonObject.put("ChequeNumber", etChequeNo.getText().toString().trim());
            jsonObject.put("ChequeBank", etInstBankSecondary.getText().toString().trim());
            jsonObject.put("ChequeBankBranch", etInstBranchSecondary.getText().toString().trim());
            jsonObject.put("ChequeAmount", rawValue);
            jsonObject.put("ChequeDate", "");
            jsonObject.put("ChequeMICR", etMicrSecondary.getText().toString().trim());
            jsonObject.put("DividendOption", "Z");
            jsonObject.put("IPAddress", "");
            jsonObject.put("LoggedInUser", mCid);


        } catch (Exception e) {
            e.printStackTrace();
        }
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
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
                            Intent intent = new Intent(mActivity, PaymentForAdditionalPurchage.class);
                            intent.putExtra("url", response.optString("ServiceMSG"));
                            startActivity(intent);
                            mActivity.getSupportFragmentManager().popBackStack();
                        }else if (payment_mode.equals("UPI") && netbanking_option.equals("Immediate")) {
                            Intent intent = new Intent(mActivity, PaymentForAdditionalPurchage.class);
                            intent.putExtra("url", response.optString("ServiceMSG"));
                            startActivity(intent);
                            mActivity.getSupportFragmentManager().popBackStack();
                        } else {

                            // mApplication.showSnackBar(etAmount, ServiceMSG);
                            mApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.dialog_title_message), ServiceMSG, "message", true, true);

                        }


                    } else {
                        if (ServiceMSG.contains("confirmed")) {
                            tvServiceMessage.setText(ServiceMSG);
                            etAmount.setEnabled(false);
                            tilAmount.setBackgroundColor(getResources().getColor(R.color.colorGrey_200));
                            tvServiceMessage.setVisibility(View.VISIBLE);
                            transact = "Pay Now";
                            btnTransact.setText(transact);
                            btnTransact.setText(getString(R.string.title_pay));
                        } else if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equals("DN")) {
                            transact = "Pay Now";
                            btnTransact.setText(transact);
                        } else {
                            btnTransact.setText(getString(R.string.new_purcahse__form_btn_footer_txt));
                            tvServiceMessage.setVisibility(View.GONE);

                            mApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.dialog_title_message), ServiceMSG, "message", true, true);

                        }
                    }

                } else {
                    tvServiceMessage.setText("");
                    tvServiceMessage.setVisibility(View.GONE);

                    mApplication.showSnackBar(etAmount, ServiceMSG);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mBar.dismiss();
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

            String rawValue = etAmount.getText().toString().replaceAll(",", "");
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

    private class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void afterTextChanged(Editable editable) {
            if (mActivity.getCurrentFocus() == etAmount) {
                delayCall();
            }

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            tilAmount.setError("");
        }

    }

}
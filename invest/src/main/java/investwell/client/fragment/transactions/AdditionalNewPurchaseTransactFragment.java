package investwell.client.fragment.transactions;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.PaymentForAdditionalPurchage;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ChooserFragment;
import investwell.client.fragment.others.ChooserSchemeTypeFragment;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalNewPurchaseTransactFragment extends Fragment implements View.OnClickListener, ChooserFragment.ChooserCallBack, ChooserSchemeTypeFragment.ChooserTypeCallback, CustomDialog.DialogBtnCallBack {
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
    private List<String> new_scheme = new ArrayList<>();
    private Button[] btn = new Button[2];
    private Button btn_unfocus, btnSendMeLinkOnMail, btnPayNow;
    private int[] btn_id = {R.id.btn_send_me_link_on_mail, R.id.btn_pay_now};
    private List<JSONObject> mBankList, mMendateList;
    private CustomDialog customDialog;
    private SwipeButton btnTransact;
    private Button btnApplySchemeChange;
    private LinearLayout llMiddleViewHeader, ll_et_container, llBankBtnContainer, llFooterSectionOne, llFooterSectionTwo, llMandateSection, llNseContainer;
    private RelativeLayout rlBottomsheetSchemeEdit;
    private String category_value = "E", Mandate_code_value, mScheme_name, scheme_type_value = "G", switch_type_value = "Amount", scode, first_order_value = "Y", until_cancel_value = "Y";
    private String transact = "Transact Now", netbanking_option = "", payment_mode = "", to_scode_value, TValue, reedmption_type_value = "Amount", mCid, mUcc, mFolioNum, mFcode, mScode;
    private Timer timer = new Timer();
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




    private OnActiveListener btnTransactOnActiveListener = new OnActiveListener() {
        @Override
        public void onActive() {
            onTransactBtnClick();
            if (btnTransact.isActive()) {
                btnTransact.toggleState();
            }
        }
    };

    public AdditionalNewPurchaseTransactFragment() {
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
        view = inflater.inflate(R.layout.fragment_additional_new_purchase, container, false);
        setUpToolBar();
        setInitializer(view);
        getDataFromBundle();
        setUpChooserFragment();
        setUpChooserSchemeType();
        setListeners();
        callSchemeListApi();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_add_purchase), true, false, false, false, false, false, false, "");
        }
    }

    private void setInitializer(View view) {
        bundle = getArguments();
        tvFolioNumber = view.findViewById(R.id.tv_folio_no);
        tvInvestorName = view.findViewById(R.id.tv_investor_name);
        tvSchemeName = view.findViewById(R.id.tv_scheme_name);
        tvUnitBalanceValue = view.findViewById(R.id.tv_unit_balance_value);
        tvMarketValue = view.findViewById(R.id.tv_market_value);
        etAmount = view.findViewById(R.id.et_amnt);
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

        btnPayNow = view.findViewById(R.id.btn_pay_now);
        btnSendMeLinkOnMail = view.findViewById(R.id.btn_send_me_link_on_mail);
        btnTransact = view.findViewById(R.id.btn_swipe_transact);

        vDivider = view.findViewById(R.id.v_divider);
        customDialog = new CustomDialog(this);
        llBankBtnContainer = view.findViewById(R.id.ll_bank_btn_container);
        llBankBtnContainer.setVisibility(View.GONE);
        llFooterSectionOne = view.findViewById(R.id.ll_footer_section_one);
        llFooterSectionOne.setVisibility(View.GONE);
        llFooterSectionTwo = view.findViewById(R.id.ll_footer_section_two);
        llFooterSectionTwo.setVisibility(View.GONE);
        llMandateSection = view.findViewById(R.id.ll_mandate_section);
        llMandateSection.setVisibility(View.GONE);
        llNseContainer = view.findViewById(R.id.ll_nse_container);
        tvServiceMessage = view.findViewById(R.id.tv_service_msg);

        bundle = getArguments();
    }

    private void setListeners() {
        spinnerScheme.setOnItemSelectedListener(onSchemeItemSelected);
        spinnerMandate.setOnItemSelectedListener(onMandateItemSelected);
        for (int i = 0; i < btn.length; i++) {
            btn[i] = (Button) view.findViewById(btn_id[i]);
            btn[i].setBackground(getResources().getDrawable(R.drawable.btn_inactive));
            btn[i].setOnClickListener(this);
        }
        btn_unfocus = btn[0];
        btnApplySchemeChange.setOnClickListener(this);
        etAmount.addTextChangedListener(new GenericTextWatcher(etAmount));
        btnTransact.setOnActiveListener(btnTransactOnActiveListener);
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setTextColor(getResources().getColor(R.color.btnSecondaryTextColor));
        btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_bg_secondary));
        btn_focus.setTextColor(getResources().getColor(R.color.btnPrimaryTextColor));
        btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
        this.btn_unfocus = btn_focus;
    }

    private void onTransactBtnClick() {

        if (transact.equalsIgnoreCase(getResources().getString(R.string.transact_now_txt))) {
            if (mSession.getAppType().equals(getString(R.string.apptype_b)) || mSession.getAppType().equals(getResources().getString(R.string.apptype_db))) {
                callAdditionalPurchaseApi();
            } else {
                if (etAmount.getText().toString().replaceAll(",", "").replaceAll(",", "").equals("") || etAmount.getText().toString().replaceAll(",", "").equals("0")) {

                    etAmount.setError(getResources().getString(R.string.error_empty_amount));
                } else if ((Long.parseLong(etAmount.getText().toString().replaceAll(",", ""))) < 100) {

                    etAmount.setError(getString(R.string.error_invalid_amount));
                } else if (payment_mode.equals("")) {
                    mApplication.showSnackBar(view, getResources().getString(R.string.error_empty_pay_mode));
                } else {
                    if (payment_mode.equals("OL")) {
                        if (mSelectedBankObject == null) {
                            mApplication.showSnackBar(view, getResources().getString(R.string.error_empty_bank));
                        } else {
                            callAdditionalPurchaseApi();
                        }

                    } else if (payment_mode.equals("TR")) {
                        validationForRTGS();
                    } else if (payment_mode.equals("M")) {
                        callAdditionalPurchaseApi();
                    } else if (payment_mode.equals("Q")) {
                        validationForCheck();
                    }
                }

            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("type", "pay_now");
            mActivity.displayViewOther(36, bundle);

        }
    }

    @SuppressLint("SetTextI18n")
    private void getDataFromBundle() {
        bundle = getArguments();
        if (bundle != null) {
            tvInvestorName.setText(!TextUtils.isEmpty(bundle.getString("applicant_name")) ? bundle.getString("applicant_name") : "");
            tvSchemeName.setText(!TextUtils.isEmpty(bundle.getString("colorBlue")) ? bundle.getString("colorBlue") : "");
            tvUnitBalanceValue.setText((!TextUtils.isEmpty(bundle.getString("purchase_cost")) ? bundle.getString("purchase_cost") : ""));
            tvMarketValue.setText((!TextUtils.isEmpty(getString(R.string.rs) +bundle.getString("market_position")) ? getString(R.string.rs) +bundle.getString("market_position") : ""));
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

        toggleNseLayout();
    }

    private void toggleNseLayout() {
        if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equals(getResources().getString(R.string.apptype_dn))) {
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
                   "", true, true, false, false);

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
        int id = view.getId();
        if (id == R.id.btn_send_me_link_on_mail) {
            setFocus(btn_unfocus, btn[0]);
            netbanking_option = "Email";
        } else if (id == R.id.btn_pay_now) {
            setFocus(btn_unfocus, btn[1]);
            netbanking_option = "Immediate";
        }
    }



    private void validationForRTGS() {

        if (etRtgsCode.getText().toString().equals("")) {
            etRtgsCode.requestFocus();
            etRtgsCode.setError(getResources().getString(R.string.error_empty_rtgs_no));
        } else if (etMicrNo.getText().toString().equals("")) {
            etMicrNo.setError(getResources().getString(R.string.error_empty_micr_no));
            etMicrNo.requestFocus();
        } else if (etInstBankPrimary.getText().toString().equals("")) {
            etInstBankPrimary.setError(getResources().getString(R.string.error_empty_bank_name));
            etInstBankPrimary.requestFocus();
        } else if (etInstBrnachPrimary.getText().toString().equals("")) {
            etInstBrnachPrimary.setError(getResources().getString(R.string.error_empty_branch_name));
            etInstBrnachPrimary.requestFocus();
        } else if (etTransferDate.getText().toString().equals("")) {
            etTransferDate.setError(getResources().getString(R.string.error_empty_transfer_date));
            etTransferDate.requestFocus();
        } else {
            callAdditionalPurchaseApi();
        }
    }

    private void validationForCheck() {

        if (etAccountNo.getText().toString().equals("")) {
            etAccountNo.requestFocus();
            etAccountNo.setError(getResources().getString(R.string.error_empty_acc_no));
        } else if (etChequeNo.getText().toString().equals("")) {
            etChequeNo.setError(getResources().getString(R.string.error_empty_cheque_no));
            etChequeNo.requestFocus();
        } else if (etInstBankSecondary.getText().toString().equals("")) {
            etInstBankSecondary.setError(getResources().getString(R.string.error_empty_bank_name));
            etInstBankSecondary.requestFocus();
        } else if (etInstBranchSecondary.getText().toString().equals("")) {
            etInstBranchSecondary.setError(getResources().getString(R.string.error_empty_branch_name));
            etInstBranchSecondary.requestFocus();
        } else if (etMicrSecondary.getText().toString().equals("")) {
            etMicrSecondary.setError(getResources().getString(R.string.error_empty_utr_no));
            etMicrSecondary.requestFocus();
        } else if (etChequeDate.getText().toString().equals("")) {
            etChequeDate.setError(getResources().getString(R.string.error_empty_cheque_date));
            etChequeDate.requestFocus();
        } else {

            callAdditionalPurchaseApi();
        }
    }

    @Override
    public void onItemChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            llBankBtnContainer.setVisibility(View.VISIBLE);
            llFooterSectionOne.setVisibility(View.GONE);
            llFooterSectionTwo.setVisibility(View.GONE);
            llMandateSection.setVisibility(View.GONE);
            payment_mode = "OL";
            netbanking_option = "Immediate";
        } else if (id == R.id.btn_secondary) {
            llBankBtnContainer.setVisibility(View.GONE);
            llFooterSectionOne.setVisibility(View.GONE);
            llFooterSectionTwo.setVisibility(View.GONE);
            llMandateSection.setVisibility(View.VISIBLE);
            payment_mode = "M";
            netbanking_option = "";
            callMandateListNseApi();
        } else if (id == R.id.btn_tertiary) {
            llBankBtnContainer.setVisibility(View.GONE);
            llFooterSectionOne.setVisibility(View.VISIBLE);
            llFooterSectionTwo.setVisibility(View.GONE);
            llMandateSection.setVisibility(View.GONE);
            payment_mode = "TR";
            netbanking_option = "";
        }
    }

    @Override
    public void onSchemeChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_growth) {
            scheme_type_value = "G";
            callSchemeListApi();
        } else if (id == R.id.btn_dividend) {
            scheme_type_value = "D";
            callSchemeListApi();
        } else if (id == R.id.btn_equity) {
            category_value = "E";
            callSchemeListApi();
        } else if (id == R.id.btn_debt) {
            category_value = "D";
            callSchemeListApi();
        } else if (id == R.id.btn_fmp) {
            category_value = "F";
            callSchemeListApi();
        }
    }

    @Override
    public void onSchemeCategoryChosen(int i) {

    }


    @Override
    public void onDialogBtnClick(View view) {

    }

    private void setBankName() {
        ArrayList<String> list = new ArrayList<String>();
        //   list.add("Select Bank");
        for (int i = 0; i < mBankList.size(); i++) {
            list.add(mBankList.get(i).optString("BankName")+" - "+mBankList.get(i).optString("AccountNo"));
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
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.Bank_detail;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", bundle.getString("UCC"));
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
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

                            mApplication.showSnackBar(etAmount,jsonObject.optString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(etAmount,getResources().getString(R.string.no_internet));
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

    private void callSchemeListApi() {
        DialogsUtils.showProgressBar(mActivity, false);
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
                    //  DialogsUtils.hideProgressBar();
                    DialogsUtils.hideProgressBar();

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


                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_dropdown, new_scheme);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerScheme.setAdapter(dataAdapter);


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
                    // DialogsUtils.hideProgressBar();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }

    private void callMandateListNseApi() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        String url = Config.GET_MENDATE_LIST;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", bundle.getString("UCC"));
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

                            mApplication.showSnackBar(etAmount,error.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(etAmount,getResources().getString(R.string.no_internet));

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
            jsonObject.put("UCC", mUcc);
            jsonObject.put("Bid", bundle.getString("Bid"));
            jsonObject.put("Fcode",mFcode);
            jsonObject.put("Scode", scode);
            jsonObject.put("FolioNo", mFolioNum);
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
            jsonObject.put("InstrumentRTGSCode", !TextUtils.isEmpty(etRtgsCode.getText().toString().trim()));
            jsonObject.put("InstrumentBankMICR", !TextUtils.isEmpty(etMicrNo.getText().toString().trim()));
            jsonObject.put("InstrumentBank", !TextUtils.isEmpty(etInstBankPrimary.getText().toString().trim()));
            jsonObject.put("InstrumentBankBranch", !TextUtils.isEmpty(etInstBrnachPrimary.getText().toString().trim()));
            jsonObject.put("RTGSUtrNo", !TextUtils.isEmpty(etUtrNo.getText().toString().trim()));
            jsonObject.put("RTGSDate", !TextUtils.isEmpty(etTransferDate.getText().toString().trim()));

            jsonObject.put("ChequeAccountNo", !TextUtils.isEmpty(etAccountNo.getText().toString().trim()));
            jsonObject.put("ChequeNumber", !TextUtils.isEmpty(etChequeNo.getText().toString().trim()));
            jsonObject.put("ChequeBank", !TextUtils.isEmpty(etInstBankSecondary.getText().toString().trim()));
            jsonObject.put("ChequeBankBranch", !TextUtils.isEmpty(etInstBranchSecondary.getText().toString().trim()));
            jsonObject.put("ChequeAmount", rawValue);
            jsonObject.put("ChequeDate", "");
            jsonObject.put("ChequeMICR", !TextUtils.isEmpty(etMicrSecondary.getText().toString().trim()));
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
                        } else {

                            mApplication.showSnackBar(etAmount,ServiceMSG);
                        }


                    } else {
                        if (ServiceMSG.contains("confirmed")) {
                            tvServiceMessage.setText(ServiceMSG);
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

                            mApplication.showSnackBar(etAmount,ServiceMSG);
                        }
                    }

                } else {
                    tvServiceMessage.setText("");
                    tvServiceMessage.setVisibility(View.GONE);

                    mApplication.showSnackBar(etAmount,ServiceMSG);
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
        }

    }

}
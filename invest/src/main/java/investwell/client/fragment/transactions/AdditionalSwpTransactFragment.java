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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.iw.acceleratordemo.R;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputLayout;

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
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalSwpTransactFragment extends Fragment implements View.OnClickListener, ChooserFragment.ChooserCallBack, ToolbarFragment.ToolbarCallback {
    private View view;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    private ChooserFragment chooserFragment;
    private AppSession mSession;
    private TextView tvInvestorName, tvUnitBalanceValue, tvFolioNumber, tvSchemeName, tvMarketValue;
    private EditText etAmount, etInstallment;
    private String amount_value, installments_value, selectedBtn = "Monthly", first_order_value = "Y", mCid, mUcc, mFolioNum, mFcode, mScode;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private Bundle bundle;
    private Calendar c = Calendar.getInstance();
    private CheckBox cbFirstOrder;
    private int year = c.get(Calendar.YEAR);
    private int month = c.get(Calendar.MONTH) + 1;
    private int day = c.get(Calendar.DAY_OF_MONTH);
    private SwipeButton swipeBtn;
    private CardView cvHeaderCardData;
    private String[] folio;
    private LinearLayout llMiddleViewHeader;
    private TextInputLayout tilAmount, tilInstallments;
    private ShimmerFrameLayout mShimmerViewContainer;
    private Spinner mSpDate, mSpMonth;
    private ArrayList<String> mDasyList;
    private String mSeletedDate = "";
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

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {

                first_order_value = "Y";
            } else {

                first_order_value = "N";
            }
        }
    };

    public AdditionalSwpTransactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.btn_add_new) {
            mActivity.displayViewOther(0, null);
        }
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
        view = inflater.inflate(R.layout.fragment_additional_swp, container, false);
        setUpToolBar();
        setInitializer(view);
        getDatFromBundle();
        setUpChooserFragment();
        setListeners();
        callSwpDatesApi();
        disclaimerUi(view);
        return view;
    }

    private void disclaimerUi(View view) {
        TextView tvDisc = view.findViewById(R.id.tv_disc_title);
        String spanText = "You may need to confirm this transaction via link / OTP received on your Email / Mobile";
        tvDisc.setText(spanText);

        tvDisc.setSelected(true);
    }

    private void clearErrorMessages() {
        tilInstallments.setError("");
        tilAmount.setError("");
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_swp), true, false, false, true, false, false, false, "home");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity, R.color.colorPrimary));

            }
        }
    }

    private void setInitializer(View view) {
        bundle = getArguments();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        tvFolioNumber = view.findViewById(R.id.tv_folio_no);
        tvInvestorName = view.findViewById(R.id.tv_investor_name);
        tvSchemeName = view.findViewById(R.id.tv_scheme_name);
        tvUnitBalanceValue = view.findViewById(R.id.tv_unit_balance_value);
        tvMarketValue = view.findViewById(R.id.tv_market_value);
        etAmount = view.findViewById(R.id.et_amnt);
        etInstallment = view.findViewById(R.id.et_installment);
        cbFirstOrder = view.findViewById(R.id.cb_first_order);
        cvHeaderCardData = view.findViewById(R.id.cv_add_transac_header);
        cvHeaderCardData.setVisibility(View.GONE);
        swipeBtn = view.findViewById(R.id.btn_swipe_transact);
        llMiddleViewHeader = view.findViewById(R.id.ll_middle_view_data);
        tilAmount = view.findViewById(R.id.til_amount);
        tilInstallments = view.findViewById(R.id.til_installment);

        tvLevelDay = view.findViewById(R.id.tvLevelDay);
        tvLevelMonthYear = view.findViewById(R.id.tvLevelMonthYear);
        tvLevelMonthYear.setText(getString(R.string.swp_month_and_year));
        tvLevelDay.setText(getString(R.string.swp_day));
        mSpDate = view.findViewById(R.id.spDate);
        mSpMonth = view.findViewById(R.id.spMonth);

        mDasyList = new ArrayList<>();
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

    private void onTransactBtnClick() {
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
            todayDate = Calendar.getInstance().getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            tilAmount.setError(getResources().getString(R.string.error_empty_amount));
            etAmount.setFocusable(true);

        } else if (TextUtils.isEmpty(etInstallment.getText().toString())) {

            tilInstallments.setError(getResources().getString(R.string.error_empty_installment));
            etInstallment.setFocusable(true);
            etInstallment.requestFocus();
        } else if (Integer.parseInt(etInstallment.getText().toString()) > 900) {
            mApplication.showSnackBar(view, getResources().getString(R.string.error_invalid_installment));

        } else {
            if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                if (currentDate.getTime() < dateAfter7Days.getTime()) {
                    Toast.makeText(mActivity, getString(R.string.swp_date_will_be_7_days_ahead), Toast.LENGTH_SHORT).show();
                }else{
                    callSwpTransactionApi();
                }
            }else{
                callSwpTransactionApi();
               /* if (currentDate.getTime() < todayDate.getTime()) {
                    Toast.makeText(mActivity, getString(R.string.swp_date_will_be_7_days_ahead), Toast.LENGTH_SHORT).show();
                }else{
                    callSwpTransactionApi();
                }*/

            }

        }
    }

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
            chooserFragment.setUpChooserElements(getResources().getString(R.string.additional_swp_frequency_title),
                    getResources().getString(R.string.additional_swp_primary_frequency),
                    getResources().getString(R.string.additional_swp_secondary_frequency),
                    getResources().getString(R.string.additional_swp_tertiary_frequency), true, true, true, false);

            chooserFragment.setChooserCallback(this);
        }
    }

    private void setListeners() {
        cbFirstOrder.setOnCheckedChangeListener(onCheckedChangeListener);
        swipeBtn.setOnActiveListener(btnTransactOnActiveListener);
        etAmount.addTextChangedListener(textWatcher);
        etInstallment.addTextChangedListener(textWatcher);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemChosen(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            selectedBtn = "MONTHLY";
        } else if (id == R.id.btn_secondary) {
            selectedBtn = "WEEKLY";
        } else if (id == R.id.btn_tertiary) {
            selectedBtn = "QUATERLY";
        }

    }


    /***************************API Calling**********************************/

    public void callSwpDatesApi() {

        String url = Config.Sip_Dates;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, bundle.getString(AppConstants.PASSKEY));
            jsonObject.put(AppConstants.KEY_BROKER_ID, bundle.getString(AppConstants.KEY_BROKER_ID));
            jsonObject.put("Exlcode", bundle.getString("ExcelCode"));
            jsonObject.put("TranType", "SWP");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    try {

                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
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
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mApplication.showSnackBar(etAmount, jsonObject.optString("ServiceMSG"));
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

    public void callSwpTransactionApi() {
        DialogsUtils.showProgressBar(mActivity, false);
        String url = Config.Additional_SWP;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUcc);
            jsonObject.put(AppConstants.KEY_BROKER_ID, bundle.getString(AppConstants.KEY_BROKER_ID));
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("Scode", mScode);
            jsonObject.put("FolioNo", mFolioNum);

            jsonObject.put("Amount", etAmount.getText().toString());
            DecimalFormat mFormat = new DecimalFormat("00");
           /* String Dates = "";
            if (month < 10) {
                Dates = start_date_value + "/" + mFormat.format(Double.valueOf(month)) + "/" + year;
            } else {
                Dates = start_date_value + "/" + month + "/" + year;
            }*/

            jsonObject.put("StartDate", mSeletedDate);
            jsonObject.put("Frequency", selectedBtn);
            jsonObject.put("Installment", etInstallment.getText().toString());
            jsonObject.put("FirstOrder", first_order_value);
            jsonObject.put("SWPType", "Amount");
            jsonObject.put(AppConstants.PASSKEY, bundle.getString(AppConstants.PASSKEY));
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("DividendOption", "Z");
            jsonObject.put("ToDate", "");
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

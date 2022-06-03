package investwell.client.fragment.others;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.Transaction_Adapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class MyTransactionNew extends Fragment implements View.OnClickListener {

    Bundle bundle;
    private RecyclerView recyclerView;
    private AppSession mSession;
    private String mCID = "";
    private Calendar mCalender;
    private String mStartingDate = "", mEndDate = "";
    private boolean mIsFirstTime = true;
    private int mMonthCount = 0;
    private Transaction_Adapter transaction_adapter;
    private RadioGroup mRdGroup;
    private int checkedId=-1;
    private SharedPreferences sharedPrefs;
    private Spinner mMemberSpinner, mFundSpinner, mTypeSpinner;
    private List<JSONObject> transactionlist, member_list;
    private List<JSONObject> colorBlue = new ArrayList<>();
    private String fund_value = "", type_value = "", whoseTransaction = "All";
    private FloatingActionButton mFilterIcon;
    private JSONObject mMember, mFund;
    private CoordinatorLayout coordinatorLayout;
    private int pos = 0, member_pos = 0, fund_pos = 0, type_pos = 0;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private TextInputEditText mEtFromDate, mEtToDate;
    private LinearLayout mLLCustom;
    private TextView mTvNothing;
    private ShimmerFrameLayout mShimmerViewContainer;
    private Date todayDate = new Date();
    private String dateCurrent = "";
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private ToolbarFragment fragToolBar;
    private TextWatcher fromDateTextWatcher = new TextWatcher() {
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
                mEtFromDate.setText(current);
                mEtFromDate.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher toDateTextWatcher = new TextWatcher() {
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
                mEtToDate.setText(current);
                mEtToDate.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Override
    public void onStart() {
        mActivity.setMainVisibility(this, null);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_my_transaction_new, container, false);
        transactionlist = new ArrayList<>();
        member_list = new ArrayList<>();
        errorContentInitializer(view);
        setUpToolBar(view);
        recyclerView = view.findViewById(R.id.transaction_recyclier);
        mFilterIcon = view.findViewById(R.id.filter_icon);

        view.findViewById(R.id.filter_icon).setOnClickListener(this);
        mTvNothing = view.findViewById(R.id.tvNothing);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        transaction_adapter = new Transaction_Adapter(mActivity, new ArrayList<JSONObject>());
        recyclerView.setAdapter(transaction_adapter);

        bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else {
            mCID = mSession.getCID();
        }

        // dateCalculation(true);
        setMemberSpinnerData();
        setFundSpinnerData();
        callMyTransactionApi();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFilterIcon.getVisibility() == View.VISIBLE) {
                    mFilterIcon.hide();
                } else if (dy < 0 && mFilterIcon.getVisibility() != View.VISIBLE) {
                    mFilterIcon.show();
                }
            }
        });
        mFilterIcon.show();
        return view;
    }

    private void setUpToolBar(View view) {
        if (
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")
        ) {
            view.findViewById(R.id.ll_toolBar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.ll_toolBar).setVisibility(View.GONE);
        }

        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_bottom_tab_menu_transactions), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity, R.color.colorPrimary));

            }


        }
    }

    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        ivErrorImage.setImageResource(R.drawable.bg_connection_timeout);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }

    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }

    //Display Network Error Content
    private void displayNoInternetMessage() {
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }

    public void onClick(final View view) {

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        if (view.getId() == R.id.filter_icon) {/* if(transactionlist.size()>0){*/
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.transaction_filter, null);
            dialogBuilder.setView(dialogView);
            final AlertDialog alertDialog = dialogBuilder.create();
            /*    final Dialog dialog = new Dialog(mActivity);
                dialog.setContentView(R.layout_gridview_type_two_a.transaction_filter);*/
            mRdGroup = dialogView.findViewById(R.id.rdGroup);
            mMemberSpinner = dialogView.findViewById(R.id.member_spinner);
            mFundSpinner = dialogView.findViewById(R.id.fund_spinner);
            mTypeSpinner = dialogView.findViewById(R.id.type_spinner);
            mEtFromDate = dialogView.findViewById(R.id.etFromDate);
            mEtToDate = dialogView.findViewById(R.id.etToDate);
            mLLCustom = dialogView.findViewById(R.id.llCustom);
            mEtFromDate.addTextChangedListener(fromDateTextWatcher);
            mEtToDate.addTextChangedListener(toDateTextWatcher);
            fund_value = "";
            type_value = "";


            if (checkedId >= 0) {
                mRdGroup.check(mRdGroup.getChildAt(checkedId).getId());
            }
            //  Toast.makeText(getActivity(), ""+checkedId+"/n"+pos, Toast.LENGTH_SHORT).show();

            String[] trans_type = getResources().getStringArray(R.array.trans_type);
            mRdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    int position = mRdGroup.indexOfChild(dialogView.findViewById(i));
                    //   Toast.makeText(getActivity(), ""+checkedId+"/n"+pos, Toast.LENGTH_SHORT).show();


                    switch (position) {
                        case 0:
                            pos = 0;
                            mMonthCount = 0;
                            dateCalculation(true);
                            mLLCustom.setVisibility(View.GONE);
                            editor.putInt("CheckedId", position);
                            editor.apply();
                            break;

                        case 1:
                            pos = 1;
                            mMonthCount = mMonthCount - 1;
                            dateCalculation(true);
                            mLLCustom.setVisibility(View.GONE);
                            editor.putInt("CheckedId", position);
                            editor.apply();
                            break;

                        case 2:
                            pos = 2;
                            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                            String next_year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1);
                            mStartingDate = year + "-04-01";
                            mEndDate = next_year + "-03-31";
                            mLLCustom.setVisibility(View.GONE);
                            editor.putInt("CheckedId", position);
                            editor.apply();
                            break;

                        case 3:
                            pos = 3;
                            if (mCalender == null) {
                                mCalender = Calendar.getInstance();
                            }
                            Date lastDateOfPreviousMonth = mCalender.getTime();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            mStartingDate = "1990-04-01";
                            mEndDate = formatter.format(lastDateOfPreviousMonth);
                            mLLCustom.setVisibility(View.GONE);
                            editor.putInt("CheckedId", position);
                            editor.apply();
                            break;

                        case 4:
                            pos = 4;
                            mLLCustom.setVisibility(View.VISIBLE);
                            editor.putInt("CheckedId", position);
                            editor.apply();
                            break;
                    }

                }


            });
            setMember();
            setFund();
            ArrayAdapter typeAdapter = new ArrayAdapter(mActivity, R.layout.spinner_dropdown, trans_type);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mTypeSpinner.setAdapter(typeAdapter);
            mMemberSpinner.setSelection(member_pos);
            mFundSpinner.setSelection(fund_pos);
            mTypeSpinner.setSelection(type_pos);


            ;
            if (checkedId == 4) {
                mLLCustom.setVisibility(View.VISIBLE);
            }

            dialogView.findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }


            });

            dialogView.findViewById(R.id.apply_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkedId = sharedPrefs.getInt("CheckedId", pos);
                    mMonthCount = 0;
                    if (mFundSpinner.getSelectedItem().equals("All")) {
                        fund_value = "";
                    }
                    if (mTypeSpinner.getSelectedItem().equals("ALl")) {
                        type_value = "";
                    }
                    if (mMemberSpinner.getSelectedItem().equals("All")) {
                        if (bundle != null && bundle.containsKey("cid")) {
                            mCID = bundle.getString("cid");

                        } else {
                            mCID = mSession.getCID();

                        }
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    Date GetDate = new Date();
                    dateCurrent = sdf2.format(GetDate);

                    if (mLLCustom.getVisibility() == View.VISIBLE) {
                        try {


                            if (!TextUtils.isEmpty(mEtFromDate.getText())) {
                                mStartingDate = sdf2.format(sdf.parse(mEtFromDate.getText().toString()));
                            }
                            if (!TextUtils.isEmpty(mEtToDate.getText())) {
                                mEndDate = sdf2.format(sdf.parse(mEtToDate.getText().toString()));
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(mStartingDate) && !TextUtils.isEmpty(mEndDate)) {
                            if (mStartingDate.compareTo(mEndDate) > 0) {
                                mApplication.showSnackBar(mEtFromDate, "Start Date should not be greater than End date");

                            } else {
                                setDateFilterValidation();
                                if (setDateFilterValidation()) {
                                    alertDialog.dismiss();
                                } else {
                                    //TODO Nothing
                                }
                            }

                        } else {
                            mApplication.showSnackBar(mEtToDate, "Please Fill Start Date and End Date");
                        }
                    } else {
                        callMyTransactionApi();
                        alertDialog.dismiss();
                    }


                }
            });
            mMemberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    member_pos = i;


                    if (i != 0) {
                        int position = i - 1;
                        mMember = member_list.get(position);
                        mCID = mMember.optString("Cid");
                    } else {
                        if (bundle != null && bundle.containsKey("cid")) {
                            mCID = bundle.getString("cid");
                        } else {
                            mCID = mSession.getCID();
                        }
                    }

                    if (adapterView.getSelectedItem().toString().equalsIgnoreCase("All")) {
                        whoseTransaction = "All";
                    } else {
                        whoseTransaction = mMember.optString("Cid");
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mFundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    fund_pos = i;
                    if (i != 0) {
                        int position = i - 1;
                        mFund = colorBlue.get(position);
                        fund_value = mFund.optString("Fcode");
                    } else {
                        fund_value = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    type_pos = i;

                    if (i == 0) {
                        type_value = "";
                    } else if (i == 1) {
                        type_value = "SIP";
                    } else if (i == 2) {
                        type_value = "STP";
                    } else if (i == 3) {
                        type_value = "SWP";
                    } else if (i == 4) {
                        type_value = "RED";
                    } else if (i == 5) {
                        type_value = "SWI";
                    } else if (i == 6) {
                        type_value = "SWO";
                    } else if (i == 7) {
                        type_value = "PUR";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            alertDialog.setCancelable(false);
            alertDialog.show();
                /*else {
                    mApplication.showSnackBar(mFilterIcon,"No data found to apply filter");

                }*/
        }

    }


    public void setMember() {


        ArrayList<String> list = new ArrayList<String>();
        list.add("All");
        for (int i = 0; i < member_list.size(); i++) {
            list.add(member_list.get(i).optString("Name"));
        }

        ArrayAdapter adapter = new ArrayAdapter(mActivity, R.layout.spinner_dropdown, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMemberSpinner.setAdapter(adapter);
    }

    public void setFund() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("All");
        for (int i = 0; i < colorBlue.size(); i++) {
            list.add(colorBlue.get(i).optString("FundName"));
        }

        ArrayAdapter dataAdapter = new ArrayAdapter(mActivity, R.layout.spinner_dropdown, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFundSpinner.setAdapter(dataAdapter);
    }


    private void dateCalculation(boolean isPreviousMonthClicked) {
        mCalender = Calendar.getInstance();

     /*   if (mIsFirstTime) {
            mIsFirstTime = false;
            mCalender.add(Calendar.MONTH, 0);
        } else*/ if (mMonthCount == 0) {
            mCalender.add(Calendar.MONTH, 0);
        } else {
            if (isPreviousMonthClicked)
                mCalender.add(Calendar.MONTH, mMonthCount);
            else
                mCalender.add(Calendar.MONTH, mMonthCount);
        }
        mCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDateOfPreviousMonth = mCalender.getTime();
        mCalender.set(Calendar.DATE, mCalender.getActualMaximum(Calendar.DATE)); // changed calendar to mCalender

        Date lastDateOfPreviousMonth = mCalender.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        mStartingDate = formatter.format(firstDateOfPreviousMonth);
        mEndDate = formatter.format(lastDateOfPreviousMonth);

    }


    public void callMyTransactionApi() {
        viewNoData.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.My_Transaction_url;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put("FromDate", mStartingDate);
            jsonObject.put("ToDate", mEndDate);
            jsonObject.put("Foliono", "");
            jsonObject.put("Fcode", fund_value);
            jsonObject.put("Scode", "");
            jsonObject.put("TranType", type_value);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");
            jsonObject.put("whoseTransaction", whoseTransaction);
            Log.e("TRANS REQ:", jsonObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    Log.e("RESP:", response.toString());

                    try {

                        transactionlist.clear();
                        String Status = response.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {

                            JSONArray jsonArray = response.optJSONArray("MyTransactionDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                transactionlist.add(jsonObject1);
                            }
                            transaction_adapter.updateList(transactionlist);
                            viewNoData.setVisibility(View.GONE);
                           /* mStartingDate = "";
                            mEndDate = "";*/
                        } else {
                            if (response.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
/*
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", response.optString("ServiceMSG"), "invalidPasskey",false,true);
*/
                                transactionlist.clear();
                                transaction_adapter.updateList(transactionlist);
                                viewNoData.setVisibility(View.VISIBLE);
                                displayServerMessage(response.optString("ServiceMSG"));
                                mStartingDate = "";
                                mEndDate = "";
                            } else {
                                displayServerMessage(response.optString("ServiceMSG"));
                                transactionlist.clear();
                                transaction_adapter.updateList(transactionlist);
                                viewNoData.setVisibility(View.VISIBLE);
                                mStartingDate = "";
                                mEndDate = "";
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        mStartingDate = "";
                        mEndDate = "";
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    mStartingDate = "";
                    mEndDate = "";
                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        transactionlist.clear();
                        transaction_adapter.updateList(transactionlist);
                        displayServerErrorMessage(error);
                        viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        transactionlist.clear();
                        transaction_adapter.updateList(transactionlist);
                        viewNoData.setVisibility(View.VISIBLE);
                        if (error instanceof TimeoutError)
                            displayConnectionTimeOut();
                        else if (error instanceof NoConnectionError)
                            displayNoInternetMessage();
                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean setDateFilterValidation() {
        if (mStartingDate.compareTo(dateCurrent) > 0 || mEndDate.compareTo(dateCurrent) > 0) {
            mApplication.showSnackBar(mEtFromDate, "Both Start & End date entered should not be greater than the today's date");
            return false;
        } else if (mStartingDate.compareTo(dateCurrent) < 0 && mEndDate.compareTo(dateCurrent) < 0) {
            callMyTransactionApi();
            return true;
        } else if (mStartingDate.compareTo(dateCurrent) == 0 || mEndDate.compareTo(dateCurrent) == 0) {
            callMyTransactionApi();
            return true;
        } else {
            mApplication.showSnackBar(mEtFromDate, "Please enter a valid date");
            return false;
        }

    }

    private void setMemberSpinnerData() {

        String url = Config.Member_List;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.toString());
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("MemberListDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                member_list.add(jsonObject1);

                            }


                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                //mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setFundSpinnerData() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.Fund_List;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        colorBlue = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response.toString());
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("InvestedFundListDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                colorBlue.add(jsonObject1);
                            }


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {


                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

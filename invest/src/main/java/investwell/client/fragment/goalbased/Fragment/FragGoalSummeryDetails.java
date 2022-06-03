package investwell.client.fragment.goalbased.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.goalbased.Adapter.FragGoalFolioAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.CustomProgressBar;
import investwell.utils.ProgressItem;
import investwell.utils.customView.CustomDialog;

public class FragGoalSummeryDetails extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private MainActivity mActivity;
    private LinearLayout mLl_Inflation, mLl_payment, mLinerShortFall, mLy_Projected, mLy_Completed;
    ;
    private ImageView mIvGoal;
    private TextView mTvGoalName, mTvGoalDis, mTvTargetTitle, mTvTargetAmt, mTvShortfalAmt, mTvCompletedVal,
            mTvProjectedVal, mTvShortfalVal, mTvInflatVal, mTvExpectedRetrn, mTvLump, mTvSIP, vTvShortTitle;

    private String mGoalID;
    private long mShortfalValue = 0;
    private CustomProgressBar mProgressBar;
    private FragGoalFolioAdapter mAdapter;
    private String[] sort;
    private int mTargetDue;
    private Bundle mBundle;
    private AppApplication mApplication;
    private ToolbarFragment toolbarFragment;
    private ShimmerFrameLayout mShimmerViewContainer;
    List<JSONObject> goalSummaryList;
    List<JSONObject> goalList;
    List<JSONObject> operationalList;
    private CheckBox cbAvail, cbUnAvail, cbMapped, cbAll;
    private boolean isAvail = true, isUnAvail = true, isMapped = true;
    private String isClicked = "all";
    private String isSelected = "";
    private String goalCategoryID;

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
        View view = inflater.inflate(R.layout.goal_summary_details, container, false);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_scheme_container);
        goalSummaryList = new ArrayList<>();
        goalList = new ArrayList<>();
        operationalList = new ArrayList<JSONObject>();
        cbAvail = view.findViewById(R.id.cb_available);
        cbUnAvail = view.findViewById(R.id.cb_unavilable);
        cbMapped = view.findViewById(R.id.cb_mapped);
        cbAll = view.findViewById(R.id.cb_all);
        mLl_Inflation = view.findViewById(R.id.ll_inflationreturn);
        mTvGoalName = view.findViewById(R.id.tvGoalName);
        mTvGoalDis = view.findViewById(R.id.tvGoalDis);
        mTvTargetTitle = view.findViewById(R.id.tvTargetTitle);
        mTvTargetAmt = view.findViewById(R.id.tv_targetamount);
        mTvShortfalAmt = view.findViewById(R.id.tv_shortfalamount);
        mTvCompletedVal = view.findViewById(R.id.tvComplete);
        mTvProjectedVal = view.findViewById(R.id.tvProjected);
        mTvShortfalVal = view.findViewById(R.id.tvShortFall);
        mTvInflatVal = view.findViewById(R.id.tv_inflationvalue);
        mTvExpectedRetrn = view.findViewById(R.id.tv_expectedreturn);
        mProgressBar = view.findViewById(R.id.seekBarGoal);
        mIvGoal = view.findViewById(R.id.ivImage);
        mTvLump = view.findViewById(R.id.tvLump);
        mTvSIP = view.findViewById(R.id.tvSip);
        vTvShortTitle = view.findViewById(R.id.tvShortTitle);
        mLl_payment = view.findViewById(R.id.ly_pay);
        mLy_Completed = view.findViewById(R.id.ll_completed);
        mLinerShortFall = view.findViewById(R.id.ll_ShortFall);
        mLy_Projected = view.findViewById(R.id.ll_Projected);
        setUpToolBar();
        sort = getResources().getStringArray(R.array.goal_summary_detail_sort);
        view.findViewById(R.id.cv_btnlump).setOnClickListener(this);
        view.findViewById(R.id.cv_btnsip).setOnClickListener(this);
        view.findViewById(R.id.iv_goalDel).setOnClickListener(this);
        view.findViewById(R.id.iv_goaledit).setOnClickListener(this);
        cbMapped.setOnCheckedChangeListener(onMappedListener);
        cbUnAvail.setOnCheckedChangeListener(onNotAvailListener);
        cbAvail.setOnCheckedChangeListener(onAvailListener);
        cbAll.setOnCheckedChangeListener(onAllCheckedListener);
        mBundle = getArguments();
        if (mBundle != null && mBundle.containsKey("data")) {
            sort[0] = mGoalID = mBundle.getString("goalid");
            setData(mBundle.getString("data"));
        }

        RecyclerView recycleView = view.findViewById(R.id.rv_goalfolio);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new FragGoalFolioAdapter(getActivity(), new ArrayList<JSONObject>(), mGoalID, FragGoalSummeryDetails.this, new FragGoalFolioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("data", jsonObject.toString());
//                mActivity.displayViewOther(71, bundle);
            }
        });
        recycleView.setAdapter(mAdapter);


        Calendar cal = Calendar.getInstance();

        cal.get(Calendar.YEAR);
        mTargetDue = mTargetDue - cal.get(Calendar.YEAR);
        getfoliolist();
        return view;
    }


    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getString(R.string.goal_summary_details_header_txt), true, false, false, false, false, false, false, "");
        }
    }

    private CompoundButton.OnCheckedChangeListener onAllCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (b) {
                isSelected = "all";
                cbAvail.setChecked(false);
                cbMapped.setChecked(false);
                cbUnAvail.setChecked(false);
                if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("avail") || isClicked.equalsIgnoreCase("not"))) {
                    operationalList.clear();
                    operationalList.addAll(goalSummaryList);
                    isClicked = "all";
                }
                mAdapter.updateList(operationalList);
            } else {
                if (isSelected.equalsIgnoreCase("all")) {
                    cbAll.setChecked(true);
                }
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener onMappedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (b) {
                isSelected = "map";
                cbAvail.setChecked(false);
                cbAll.setChecked(false);

                cbUnAvail.setChecked(false);
                goalList.clear();
                if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("avail") || isClicked.equalsIgnoreCase("not"))) {
                    operationalList.clear();
                    operationalList.addAll(goalSummaryList);
                    isClicked = "map";

                }
                for (int i = 0; i < operationalList.size(); i++) {
                    JSONArray newJson = operationalList.get(i).optJSONArray("ChildResponseData");
                    for (int j = 0; j < newJson.length(); j++) {
                        if (newJson.optJSONObject(j).optString("GoalID").contains(mGoalID)
                        ) {
                            goalList.add(operationalList.get(i));
                        }
                    }
                }
                mAdapter.updateList(goalList);
            } else {
                if (isSelected.equalsIgnoreCase("map")) {
                    cbMapped.setChecked(true);
                }
            }
        }

    };
    private CompoundButton.OnCheckedChangeListener onAvailListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                isSelected = "avail";
                cbMapped.setChecked(false);
                cbAll.setChecked(false);
                cbUnAvail.setChecked(false);
                goalList.clear();
                if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("map") || isClicked.equalsIgnoreCase("all") ||
                        isClicked.equalsIgnoreCase("not"))) {
                    operationalList.clear();
                    operationalList.addAll(goalSummaryList);
                    isClicked = "avail";


                }
                for (int i = 0; i < operationalList.size(); i++) {
                    JSONArray newJson = operationalList.get(i).optJSONArray("ChildResponseData");
                    for (int j = 0; j < newJson.length(); j++) {
                        if (newJson.optJSONObject(j).optString("GoalID").contains(mGoalID)
                        ) {
                            operationalList.remove(i);

                        }
                    }



                }
                for (int i = 0; i < operationalList.size(); i++) {
                    if (!operationalList.get(i).optString("UnAllocated").equalsIgnoreCase("0%")) {
                        goalList.add(operationalList.get(i));
                    }
                }

                mAdapter.updateList(goalList);
            } else {
                if (isSelected.equalsIgnoreCase("avail")) {
                    cbAvail.setChecked(true);
                }
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener onNotAvailListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                isSelected = "not";
                cbAvail.setChecked(false);
                cbAll.setChecked(false);
                cbMapped.setChecked(false);
                goalList.clear();
                if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("map") || isClicked.equalsIgnoreCase("all") ||
                        isClicked.equalsIgnoreCase("avail"))) {
                    operationalList.clear();
                    operationalList.addAll(goalSummaryList);
                    isClicked = "not";


                }
                for (int i = 0; i < operationalList.size(); i++) {
                    JSONArray newJson = operationalList.get(i).optJSONArray("ChildResponseData");
                    for (int j = 0; j < newJson.length(); j++) {
                        if (!newJson.optJSONObject(j).optString("GoalID").contains(mGoalID)
                                && operationalList.get(i).optString("UnAllocated").equalsIgnoreCase("0%")) {
                            goalList.add(operationalList.get(i));

                        }
                    }


                }
            /*    for (int i = 0; i < operationalList.size(); i++) {
                    if (operationalList.get(i).optString("UnAllocated").equalsIgnoreCase("0%")) {
                        goalList.add(operationalList.get(i));
                    }

                }*/
                mAdapter.updateList(goalList);
                isClicked = "not";

            } else {
                if (isSelected.equalsIgnoreCase("not")) {
                    cbUnAvail.setChecked(true);
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        final Bundle bundle = new Bundle();
        bundle.putInt("duration", mTargetDue);
        bundle.putString("ic_invest_route_goal", mTvGoalName.getText().toString());
        bundle.putString("type", "coming_from_goal");
        bundle.putString("RiskId", mSession.getRiskCode());
        bundle.putString("RiskProfile", mSession.getRiskName());
        int id = view.getId();
        if (id == R.id.cv_btnlump) {
            if (mSession.getRiskName().isEmpty()) {
                showDailogForNoRiskProfile();
            } else {
                bundle.putString("Amount", mTvLump.getText().toString());
                bundle.putString("investment_type", "Lumpsum");
                bundle.putString("GoalCategoryID", goalCategoryID);
                mActivity.displayViewOther(64, bundle);
            }
        } else if (id == R.id.cv_btnsip) {
            if (mSession.getRiskName().isEmpty()) {
                showDailogForNoRiskProfile();
            } else {
                bundle.putString("Amount", mTvSIP.getText().toString());
                bundle.putString("investment_type", "SIP");
                bundle.putString("GoalCategoryID", goalCategoryID);
                mActivity.displayViewOther(64, bundle);
            }
        } else if (id == R.id.iv_goalDel) {
            showDailog();
        } else if (id == R.id.iv_goaledit) {
            bundle.putString("category_data", mBundle.getString("data"));
            mActivity.displayViewOther(76, bundle);
        }
    }

    private void showDailogForNoRiskProfile() {
        CustomDialog customDialog = new CustomDialog(new CustomDialog.DialogBtnCallBack() {
            @Override
            public void onDialogBtnClick(View view) {
                int id = view.getId();
                if (id == R.id.btDone) {
                    mApplication.sStoreGoalBundle = mBundle;
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "create_goal_for_sip_lumpsum");
                    mActivity.displayViewOther(61, bundle);
                } else if (id == R.id.btCalcel) {
                }
            }
        });

        customDialog.showDialog(mActivity, getString(R.string.alert_dialog_confirmation_header_txt),
                getString(R.string.alert_dialog_no_risk_profile_txt2),
                getString(R.string.alert_dialog_continue_txt),
                getString(R.string.alert_dialog_later_txt),
                true, true);
    }

    public void getfoliolist() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        //  String url = Config.GOAL_FOLIO_LIST;
        String url = Config.GOAL_MAPPING_V4_2;
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            jsonParam.put("UCC", mSession.getUCC_CODE());
            jsonParam.put("Fcode", "All");
            jsonParam.put("OnlineOption", mSession.getAppType());
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mShimmerViewContainer.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmerAnimation();
                try {
                    if (jsonObject.optBoolean("Status")) {
                        JSONArray araArray = jsonObject.getJSONArray("ResponseData");
                        goalSummaryList.clear();
                        for (int s = 0; s < sort.length; s++) {
                            String goalid = sort[s];
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                if (s < sort.length - 1) {
                                    if (goalid.equalsIgnoreCase(object.optString("GoalID"))) {
                                        goalSummaryList.add(object);
                                    }
                                } else if (!object.optString("GoalID").equalsIgnoreCase(sort[0]) && !object.optString("GoalID").equalsIgnoreCase(sort[1])) {
                                    goalSummaryList.add(object);
                                }
                            }
                        }
                        operationalList.clear();
                        operationalList.addAll(goalSummaryList);
                        if(cbAll.isChecked()){
                            isSelected = "all";
                            cbAvail.setChecked(false);
                            cbMapped.setChecked(false);
                            cbUnAvail.setChecked(false);
                            if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("avail") || isClicked.equalsIgnoreCase("not"))) {
                                operationalList.clear();
                                operationalList.addAll(goalSummaryList);
                                isClicked = "all";
                            }
                            mAdapter.updateList(operationalList);
                        }else if(cbMapped.isChecked()){
                            isSelected = "map";
                            cbAvail.setChecked(false);
                            cbAll.setChecked(false);

                            cbUnAvail.setChecked(false);
                            goalList.clear();
                            if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("avail") || isClicked.equalsIgnoreCase("not"))) {
                                operationalList.clear();
                                operationalList.addAll(goalSummaryList);
                                isClicked = "map";

                            }
                            for (int i = 0; i < operationalList.size(); i++) {
                                JSONArray newJson = operationalList.get(i).optJSONArray("ChildResponseData");
                                for (int j = 0; j < newJson.length(); j++) {
                                    if (newJson.optJSONObject(j).optString("GoalID").contains(mGoalID)
                                    ) {
                                        goalList.add(operationalList.get(i));
                                    }
                                }
                            }
                            mAdapter.updateList(goalList);
                        }else if(cbAvail.isChecked()){
                            isSelected = "avail";
                            cbMapped.setChecked(false);
                            cbAll.setChecked(false);
                            cbUnAvail.setChecked(false);
                            goalList.clear();
                            if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("map") || isClicked.equalsIgnoreCase("all") ||
                                    isClicked.equalsIgnoreCase("not"))) {
                                operationalList.clear();
                                operationalList.addAll(goalSummaryList);
                                isClicked = "avail";


                            }
                            for (int i = 0; i < operationalList.size(); i++) {
                                JSONArray newJson = operationalList.get(i).optJSONArray("ChildResponseData");
                                for (int j = 0; j < newJson.length(); j++) {
                                    if (newJson.optJSONObject(j).optString("GoalID").contains(mGoalID)
                                    ) {
                                        operationalList.remove(i);

                                    }
                                }



                            }
                            for (int i = 0; i < operationalList.size(); i++) {
                                if (!operationalList.get(i).optString("UnAllocated").equalsIgnoreCase("0%")) {
                                    goalList.add(operationalList.get(i));
                                }
                            }

                            mAdapter.updateList(goalList);
                        }else if(cbUnAvail.isChecked()){
                            isSelected = "not";
                            cbAvail.setChecked(false);
                            cbAll.setChecked(false);
                            cbMapped.setChecked(false);
                            goalList.clear();
                            if ((!TextUtils.isEmpty(isClicked)) && (isClicked.equalsIgnoreCase("map") || isClicked.equalsIgnoreCase("all") ||
                                    isClicked.equalsIgnoreCase("avail"))) {
                                operationalList.clear();
                                operationalList.addAll(goalSummaryList);
                                isClicked = "not";


                            }
                            for (int i = 0; i < operationalList.size(); i++) {
                                JSONArray newJson = operationalList.get(i).optJSONArray("ChildResponseData");
                                for (int j = 0; j < newJson.length(); j++) {
                                    if (!newJson.optJSONObject(j).optString("GoalID").contains(mGoalID)
                                            && operationalList.get(i).optString("UnAllocated").equalsIgnoreCase("0%")) {
                                        goalList.add(operationalList.get(i));

                                    }
                                }


                            }
                            /*for (int i = 0; i < operationalList.size(); i++) {
                                if (operationalList.get(i).optString("UnAllocated").equalsIgnoreCase("0%")) {
                                    goalList.add(operationalList.get(i));
                                }

                            }*/
                            mAdapter.updateList(goalList);
                            isClicked = "not";
                        }else {
                            mAdapter.updateList(operationalList);

                        }


                    } else {
                        mApplication.showSnackBar(mShimmerViewContainer, jsonObject.optString("ServiceMSG"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mShimmerViewContainer.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmerAnimation();

                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        mApplication.showSnackBar(mShimmerViewContainer, jsonObject.optString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(mShimmerViewContainer, getResources().getString(R.string.no_internet));
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


    public void getGoalDetail() {
        String url = Config.GOAL_SUMMARY;
        final JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            jsonParam.put("GoalID", mGoalID);
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jobject) {
                try {
                    if (jobject.optBoolean("Status")) {
                        JSONArray araArray = jobject.getJSONArray("GoalDetailList");
                        JSONObject object = jobject.getJSONArray("GoalDetailList").getJSONObject(0);
                        setData(String.valueOf(object));

                    } else {
//                        Toast.makeText(getActivity(), jobject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
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
                        mApplication.showSnackBar(mShimmerViewContainer, jsonObject.optString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(mShimmerViewContainer, getResources().getString(R.string.no_internet));
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }


    private void setData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);


            String target[] = jsonObject.optString("TargetDate").split("-");
            goalCategoryID =jsonObject.optString("GoalCategoryID");
            mTargetDue = Integer.valueOf(target[2]);
            mTvGoalDis.setText(jsonObject.optString("GoalCategory") + " (Required in " + target[2] + ")");
            mTvGoalName.setText(jsonObject.optString("GoalName"));
            mGoalID = jsonObject.optString("GoalID");
            String shortfalll = jsonObject.optString("ShortFall").replace(",", "");
            if (!shortfalll.equalsIgnoreCase(""))
                mShortfalValue = Long.valueOf(shortfalll);
            if (mShortfalValue < 0) {
                vTvShortTitle.setText(getResources().getString(R.string.goal_summary_surplus));
                mTvShortfalAmt.setText(getString(R.string.rs) + jsonObject.optString("ShortFall").replace("-", ""));
                mTvShortfalAmt.setTextColor(getResources().getColor(R.color.colorGreen));
            } else {
                vTvShortTitle.setText(getResources().getString(R.string.goal_summary_shortfall));
                mTvShortfalAmt.setTextColor(getResources().getColor(R.color.colorRed));
                mTvShortfalAmt.setText(getString(R.string.rs) + jsonObject.optString("ShortFall"));
            }
            mTvTargetAmt.setText(getString(R.string.rs) + jsonObject.optString("ExpectedCorpus"));
            double inflat = jsonObject.optDouble("Inflation");
            if (inflat > 0) {
                mTvTargetTitle.setText(getResources().getString(R.string.goal_summary_inflation_target_amnt));
                mLl_Inflation.setVisibility(View.VISIBLE);
                mTvInflatVal.setText(" @" + String.valueOf(inflat) + "% |");
            } else {
                mTvTargetTitle.setText(getResources().getString(R.string.goal_summary_target_amnt));
                mLl_Inflation.setVisibility(View.GONE);
            }
            mTvExpectedRetrn.setText(" @" + jsonObject.optString("ExpectedReturn") + "%");
            String lump = jsonObject.optString("OneTimeInvestment");
            boolean isFound = lump.indexOf("-") != -1 ? true : false; //true
            if (isFound) {
                lump = lump.substring(0, 1) + getResources().getString(R.string.Rs) + " " + lump.substring(1, lump.length());
                mTvLump.setText(lump);
            } else {
                mTvLump.setText(getString(R.string.rs) + jsonObject.optString("OneTimeInvestment"));
            }


            String input = jsonObject.optString("SIPInvestment");
            boolean isFounds = input.indexOf("-") != -1 ? true : false; //true
            if (isFounds) {
                input = input.substring(0, 1) + getResources().getString(R.string.Rs) + " " + input.substring(1, input.length());
                mTvSIP.setText(input);
            } else {
                mTvSIP.setText(getResources().getString(R.string.rs) + jsonObject.optString("SIPInvestment"));
            }


            String path_goalpic = jsonObject.optString("GoalPic");
            if (!path_goalpic.equalsIgnoreCase(""))
                Picasso.get().load(path_goalpic).placeholder(R.mipmap.tranparent).into(mIvGoal);
            final Double completion = jsonObject.optDouble("CompletionPercentage");
            final Double projected = jsonObject.optDouble("ProjectedPercentage");
            final Double shortfal = jsonObject.optDouble("ShortfallPercentage");

            if (shortfal > 0) {
                mLl_payment.setVisibility(View.VISIBLE);
                mLinerShortFall.setVisibility(View.VISIBLE);
            } else {
                mLinerShortFall.setVisibility(View.GONE);
                mLl_payment.setVisibility(View.GONE);
//                mTvShortfalAmt.setText("-");
            }

            if (projected > 0)
                mLy_Projected.setVisibility(View.VISIBLE);
            else
                mLy_Projected.setVisibility(View.GONE);


            if (completion > 0)
                mLy_Completed.setVisibility(View.VISIBLE);
            else
                mLy_Completed.setVisibility(View.GONE);


            mProgressBar.getThumb().mutate().setAlpha(0);
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;
            mTvCompletedVal.setText("Completed\n(" + completion + "%)");
            mTvProjectedVal.setText("Projected\n(" + projected + "%)");
            mTvShortfalVal.setText("Shortfall\n(" + shortfal + "%)");


            progressItemList = new ArrayList<ProgressItem>();
            // colorRed span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = completion;
            mProgressItem.color = R.color.colorGreen;
            progressItemList.add(mProgressItem);

            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = projected - completion;
            mProgressItem.color = R.color.colorOrange;
            progressItemList.add(mProgressItem);

            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = shortfal;
            mProgressItem.color = R.color.colorRed;
            progressItemList.add(mProgressItem);


            mProgressBar.initData(progressItemList);
            mProgressBar.invalidate();


        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }


    public void delGoalDetail() {

        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.GOAL_DELETE;

        final JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            jsonParam.put("GoalID", mGoalID);
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jobject) {
                mShimmerViewContainer.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmerAnimation();
                mActivity.getSupportFragmentManager().popBackStack();
                try {
                    if (jobject.optBoolean("Status")) {
                        mApplication.showSnackBar(mShimmerViewContainer, jobject.optString("ServiceMSG"));
                    } else {
                        mApplication.showSnackBar(mShimmerViewContainer, jobject.optString("ServiceMSG"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mShimmerViewContainer.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmerAnimation();
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        mApplication.showSnackBar(mShimmerViewContainer, jsonObject.optString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(mShimmerViewContainer, getResources().getString(R.string.no_internet));
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }


    private void showDailog() {
        CustomDialog customDialog = new CustomDialog(new CustomDialog.DialogBtnCallBack() {
            @Override
            public void onDialogBtnClick(View view) {
                int id = view.getId();
                if (id == R.id.btDone) {
                    delGoalDetail();
                } else if (id == R.id.btCalcel) {
                }
            }
        });

        customDialog.showDialog(mActivity, getString(R.string.alert_dialog_confirmation_header_txt),
                getString(R.string.alert_dialog_delete_goal_txt),
                getString(R.string.alert_dialog_yes_btn_txt),
                getString(R.string.alert_dialog_btn_no_txt),
                true, true);
    }


}

package investwell.client.flavourtypefour.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.client.activity.SignUpActivity;
import investwell.client.adapter.DocumentAllAdapter;
import investwell.client.adapter.DocumentSwipeAdapter;
import investwell.client.adapter.FinancialToolAdapter;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.adapter.InvestmentRouteAdapter;
import investwell.client.flavourtypefour.adapter.SectionTypeFourPlanAdapter;
import investwell.client.flavourtypefour.adapter.SectionsTypeFourAdapter;
import investwell.client.fragment.allocation.ApplicantAllocation;
import investwell.client.fragment.allocation.AssetAllocation;
import investwell.client.fragment.allocation.CategoryAllocation;
import investwell.client.fragment.others.FundAllocation;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.model.FinancialTools;
import investwell.utils.model.InvestmentRoutes;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeClientFlavourFourFragment extends Fragment implements View.OnClickListener {
    private View view;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    public static String values;

    private RelativeLayout rlMainLayout, rlDashboardDefaultCard;

    private RequestQueue requestQueue;
    TextView tvMarketValue, tvDividendLabel, tvPurchaseCost, tvDaysChange, tvGain, tvDivident, tvReturnCagr;
    private ImageView ivDayArrow, tvGainArrow;
    private RelativeLayout rlPiewView;


    private TabLayout mTablayout, mTabLayout1a, tabLayoutFlavourFour;
    private ViewPager mPager, mPager1a, viewPagerFlavourFour;
    private String[] tabtext;

    private LinearLayout llSummarySection;
    private String url, PVCode = "";
    private JsonObjectRequest jsonObjectRequest;
    private InvestmentRouteAdapter adapter;
    private List<InvestmentRoutes> investmentRoutesList;
    private RecyclerView rvInvestRoute, rvInvestRoute1A, rvDashboardData, rvPlanData;
    private CardView cvInvestRoutes;
    private LinearLayout llInvestRouteOneA, llFinancialToolsOneA, llAllocationCharts1A;
    private FinancialToolAdapter financialToolAdapter;
    private List<FinancialTools> financialToolsList;
    private RecyclerView rvFinancialTools, rvFinancialTools1a;
    private CardView mCvNoData, mCvPaymentNow, mCvFinancialTools, mCvAllocationCharts;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ConstraintLayout llDashboardClientDataContainer;
    private LinearLayout mLinerDashboardContainer, mCvProfileNotCompleted,mLlDayChange, llDashboardFlavourFour, ll_netInv;
    private CustomButton btnPayNow;
    private TextView mTvUserName, mTvRealise, mTvXirr, tv_netInv_value;
    private ProgressDialog mBar;
    private RecyclerView rvQuickRead;
    private DocumentSwipeAdapter mDucSwipeAdapter;
    private RelativeLayout rlQuickRead;
    private LinearLayout llCurrentGain, llXirrGain;
    private SectionTypeFourPlanAdapter sectionTypeFourPlanAdapter;
    private SectionsTypeFourAdapter sectionsTypeFourAdapter;
    private JSONArray sectionJSONArray;
    private ArrayList<JSONObject> sectionJSONObjectList;
    private ArrayList<JSONObject> sectionJSONObjectList1;
    private ArrayList<JSONObject> sectionJSONObjectList2;
    private ArrayList<JSONObject> sectionJSONObjectList3;
    private ArrayList<JSONObject> sectionJSONObjectList4;
    private CardView cvFlavourFour;
    private int positionOfView = 0;
    private String nameOfTheView = "";
    private static HomeClientFlavourFourFragment instance = null;
    private DocumentAllAdapter mAdapter;
    ArrayList<JSONObject> payList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;


        }
        mActivity.updateCart();
    }

    public HomeClientFlavourFourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.updateCart();
        if (mSession.getCLicked()) {
            //Post it in a handler to make sure it gets called if coming back from a lifecycle method.
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    Intent intent = mActivity.getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    mActivity.overridePendingTransition(0, 0);
                    mActivity.finish();

                    mActivity.overridePendingTransition(0, 0);
                    startActivity(intent);
                    mSession.setCLicked(false);
                }
            });
        }
        initialUiCheck();
        setUpFlavourFourUi();
    }

    public static HomeClientFlavourFourFragment newInstance(int position, String sectionName) {
        HomeClientFlavourFourFragment fragment = new HomeClientFlavourFourFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("sectionName", sectionName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_home_client_flavour_four, container, false);

        setInitializer();
        initialUiCheck();
        setIncompleteCardAnimation();

        setUpQuickReadAdapter();

        if (getArguments() != null) {
            positionOfView = getArguments().getInt("position");
            nameOfTheView = getArguments().getString("sectionName");
        }
        if (mApplication.sGOAL_SUMMARY_ARRAY.length() == 0)
            getGoalSummary();

        if (mSession.getHasLoging()) {
            getXirrData();
        }

        setUpSectionsAdapter(view);


        setListener();
  /*      if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
            mCvProfileNotCompleted.setVisibility(View.VISIBLE);

        } else {
            mCvProfileNotCompleted.setVisibility(View.GONE);

        }*/
setCustomTabFont();
        return view;
    }
    private void setCustomTabFont(){

        for (int i = 0; i < mTablayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            TextView tv = (TextView)LayoutInflater.from(mActivity).inflate(R.layout.custom_tab,null);
            Typeface typeface = ResourcesCompat.getFont(mActivity,R.font.lato_bold);
            tv.setTypeface(typeface);
            if(mTablayout.getTabAt(i)!=null) {
                mTablayout.getTabAt(i).setCustomView(tv);
                mTabLayout1a.getTabAt(i).setCustomView(tv);
            }
        }
    }
    private void setUpFlavourFourUi() {

        if ((positionOfView == 0) && (nameOfTheView.equalsIgnoreCase("Summary"))) {
            llSummarySection.setVisibility(View.VISIBLE);
        } else {
            llSummarySection.setVisibility(View.GONE);
        }

        if ((positionOfView == 0 || positionOfView == 1) && (nameOfTheView.equalsIgnoreCase("Invest"))) {
            rvDashboardData.setVisibility(View.VISIBLE);
            view.findViewById(R.id.fl_invest).setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))
            {
                view.findViewById(R.id.iv_invest_bg).setVisibility(View.VISIBLE);
               /* FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                buttonLayoutParams.setMargins(0, 400, 0, 0);
                view.findViewById(R.id.ll_invest).setLayoutParams(buttonLayoutParams);*/
            }else{
                view.findViewById(R.id.iv_invest_bg).setVisibility(View.GONE);
               /* FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                buttonLayoutParams.setMargins(0, 0, 0, 0);
                view.findViewById(R.id.ll_invest).setLayoutParams(buttonLayoutParams);*/
            }
        } else {
            rvDashboardData.setVisibility(View.GONE);
            view.findViewById(R.id.fl_invest).setVisibility(View.GONE);
        }

        if ((positionOfView == 1 || positionOfView == 2) && (nameOfTheView.equalsIgnoreCase("Plan"))) {
            rvPlanData.setVisibility(View.VISIBLE);
            view.findViewById(R.id.fl_plan).setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))
            {
                view.findViewById(R.id.iv_plan_bg).setVisibility(View.VISIBLE);
       /*         FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                buttonLayoutParams.setMargins(0, 400, 0, 0);
                view.findViewById(R.id.ll_plan).setLayoutParams(buttonLayoutParams);*/
            }else{
                view.findViewById(R.id.iv_plan_bg).setVisibility(View.GONE);
          /*      FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                buttonLayoutParams.setMargins(0, 0, 0, 0);
                view.findViewById(R.id.ll_plan).setLayoutParams(buttonLayoutParams);*/
            }
        } else {
            rvPlanData.setVisibility(View.GONE);
            view.findViewById(R.id.fl_plan).setVisibility(View.GONE);
        }
        if ((positionOfView == 2 || positionOfView == 3) && (nameOfTheView.equalsIgnoreCase("Learn"))) {
            rlQuickRead.setVisibility(View.VISIBLE);
        } else {
            rlQuickRead.setVisibility(View.GONE);

        }


    }


    /***************************************************
     Method contains all initializations of elements
     *****************************************************/

    private void setInitializer() {
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        mSession = AppSession.getInstance(mActivity);
        sectionJSONArray = new JSONArray();
        sectionJSONObjectList = new ArrayList<>();
        sectionJSONObjectList1 = new ArrayList<>();
        sectionJSONObjectList2 = new ArrayList<>();
        sectionJSONObjectList3 = new ArrayList<>();
        sectionJSONObjectList4 = new ArrayList<>();
        /*rlMainLayout = view.findViewById(R.id.rl_dashboard_parent_container);*/
        rlDashboardDefaultCard = view.findViewById(R.id.rl_dashboard_card_container);
        mCvNoData = view.findViewById(R.id.cv_dashboard_noData);
        /*mLlDayChange = view.findViewById(R.id.llDayChange);*/
        rvQuickRead = view.findViewById(R.id.rv_quick_read);
        llDashboardClientDataContainer = view.findViewById(R.id.ll_dashboard_client_data_container);
        mLinerDashboardContainer = view.findViewById(R.id.ll_dashboard_container);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mCvProfileNotCompleted = view.findViewById(R.id.cv_profile_not_complete);
        mCvPaymentNow = view.findViewById(R.id.cv_pay_now);
        rvDashboardData = view.findViewById(R.id.rv_invest_data);
        rvPlanData = view.findViewById(R.id.rv_plan_data);
        llSummarySection = view.findViewById(R.id.ll_summary_section);
/*
       tvDividendLabel = view.findViewById(R.id.tv_divident_label);
*/

        tvMarketValue = view.findViewById(R.id.tv_market_value);
        tvPurchaseCost = view.findViewById(R.id.purchase_cost);
        tvDaysChange = view.findViewById(R.id.days_change);
        tvGain = view.findViewById(R.id.gain);
        rlPiewView = view.findViewById(R.id.rl_animatedPieView);
/*
       tvDivident = view.findViewById(R.id.divident);
*/
        tvReturnCagr = view.findViewById(R.id.return_cagr);
        ivDayArrow = view.findViewById(R.id.day_arrow);
        tvGainArrow = view.findViewById(R.id.gain_arrow);
        investmentRoutesList = new ArrayList<>();
        financialToolsList = new ArrayList<>();
        rlQuickRead = view.findViewById(R.id.rl_quick_read);
      /*  rvInvestRoute = view.findViewById(R.id.rv_investment_routes);
        rvInvestRoute1A = view.findViewById(R.id.rv_investment_routes_type_a);*/
        cvInvestRoutes = view.findViewById(R.id.cv_dashboard_investment);
        mCvFinancialTools = view.findViewById(R.id.cv_financial_tools);
        mCvAllocationCharts = view.findViewById(R.id.cv_allocation_charts);
        mCvAllocationCharts.setVisibility(View.VISIBLE);
        /*  llInvestRouteOneA = view.findViewById(R.id.ll_investment_route_one_a);*/
        llFinancialToolsOneA = view.findViewById(R.id.ll_financial_tools_1a);
        llAllocationCharts1A = view.findViewById(R.id.ll_allocation_charts);
        rvFinancialTools = view.findViewById(R.id.rv_financial_tools);
        rvFinancialTools1a = view.findViewById(R.id.rv_financial_tools_1a);
        mTablayout = view.findViewById(R.id.tab_dashboard_allocation_charts);
        mTabLayout1a = view.findViewById(R.id.tab_dashboard_allocation_charts_1a);

        mPager = view.findViewById(R.id.viewpager_charts);

        mPager.setOffscreenPageLimit(mPager.getChildCount());
        mPager.setSaveFromParentEnabled(false);
        mPager1a = view.findViewById(R.id.viewpager_charts_1a);
        mPager1a.setOffscreenPageLimit(mPager1a.getChildCount());
        mPager1a.setSaveFromParentEnabled(false);
        btnPayNow = view.findViewById(R.id.btn_pay);
        llDashboardFlavourFour = view.findViewById(R.id.ll_dashboard_flavour_four);
        cvFlavourFour = view.findViewById(R.id.cv_flavour_four);
        tabtext = getResources().getStringArray(R.array.allocation_charts_tab_txt);
        llXirrGain = view.findViewById(R.id.ll_all_time_gain);

      /* if (Utils.getConfigData(mSession).optString("DayChangeRequired").equalsIgnoreCase("Y")) {
           mLlDayChange.setVisibility(View.VISIBLE);
       } else {
           mLlDayChange.setVisibility(View.GONE);
       }
*/
        mTvRealise = view.findViewById(R.id.tvrealised);
        mTvXirr = view.findViewById(R.id.tvxirr);

        ll_netInv = view.findViewById(R.id.ll_netInv);
        tv_netInv_value = view.findViewById(R.id.tv_netInv_value);
    }

    @SuppressLint("WrongConstant")
    private void setIncompleteCardAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofInt(mCvProfileNotCompleted, "backgroundColor", Color.WHITE, Color.RED,
                Color.WHITE);
        anim.setDuration(3000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();

    }

    private void setUpUiVisibility(View view) {

        if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.signup_btn).setVisibility(View.VISIBLE);

        } else {
            view.findViewById(R.id.signup_btn).setVisibility(View.GONE);

        }


        if (Utils.getConfigData(mSession).optJSONArray("SectionList").length() > 0) {
            sectionJSONArray = Utils.getConfigData(mSession).optJSONArray("SectionList");
            for (int i = 0; i < sectionJSONArray.length(); i++) {
                JSONObject object = null;
                try {
                    object = sectionJSONArray.getJSONObject(i);
                    sectionJSONObjectList.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            /**************************List Partion for TYPE 4***********************************************/
            for (int i = 0; i < sectionJSONObjectList.size(); i++) {
                JSONObject object = null;
                object = sectionJSONObjectList.get(i);
                String sectionName = object.optString("SectionName");
                if (i == 0 && sectionName.equalsIgnoreCase("Invest")) {
                    sectionJSONObjectList1.add(object);
                } else if (i == 1 && sectionName.equalsIgnoreCase("Invest")) {
                    sectionJSONObjectList1.add(object);

                } else if (i == 1 && sectionName.equalsIgnoreCase("Plan")) {
                    sectionJSONObjectList3.add(object);

                } else if (i == 2 && sectionName.equalsIgnoreCase("Plan")) {
                    sectionJSONObjectList3.add(object);

                } else if (i == 3) {
                    sectionJSONObjectList4.add(object);

                }
            }
         /*   Log.e("LIST 1", "" + sectionJSONObjectList1);
            Log.e("LIST 2", "" + sectionJSONObjectList2);
            Log.e("LIST 3", "" + sectionJSONObjectList3);
            Log.e("LIST 4", "" + sectionJSONObjectList4);*/
            sectionsTypeFourAdapter.updateSectionsList(sectionJSONObjectList1);
            sectionTypeFourPlanAdapter.updateSectionsList(sectionJSONObjectList3);

        } else {
            sectionJSONObjectList.clear();
            sectionsTypeFourAdapter.updateSectionsList(new ArrayList<JSONObject>());
            sectionTypeFourPlanAdapter.updateSectionsList(new ArrayList<JSONObject>());
        }

    }


    private void setUpSectionsAdapter(View view) {
        sectionsTypeFourAdapter = new SectionsTypeFourAdapter(mActivity, new ArrayList<JSONObject>());
        rvDashboardData.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rvDashboardData.setItemAnimator(new DefaultItemAnimator());
        rvDashboardData.setNestedScrollingEnabled(false);
        rvDashboardData.setAdapter(sectionsTypeFourAdapter);
        sectionTypeFourPlanAdapter = new SectionTypeFourPlanAdapter(mActivity, new ArrayList<JSONObject>());
        rvPlanData.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rvPlanData.setItemAnimator(new DefaultItemAnimator());
        rvPlanData.setNestedScrollingEnabled(false);
        rvPlanData.setAdapter(sectionTypeFourPlanAdapter);
        setUpUiVisibility(view);
    }

    private void initialUiCheck() {
        if (mSession.getLoginType().isEmpty() || !mSession.getHasLoging()) {
            /*rlMainLayout.setVisibility(View.VISIBLE);*/
            mCvNoData.setVisibility(View.GONE);
            llDashboardClientDataContainer.setVisibility(View.GONE);
            rlDashboardDefaultCard.setVisibility(View.VISIBLE);
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);

            mLinerDashboardContainer.setVisibility(View.VISIBLE);

            mCvPaymentNow.setVisibility(View.GONE);

        } else {
            mActivity.updateCart();
            callDashboardApi();
            getProfileList();
            if (AppApplication.sClientDashboard.isEmpty()) {

                getProfileList();
            } else {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

                mLinerDashboardContainer.setVisibility(View.VISIBLE);

                setUpUiData();
            }
            if (payList.size() > 0) {
                mCvPaymentNow.setVisibility(View.VISIBLE);
            } else {
                mCvPaymentNow.setVisibility(View.GONE);
            }


        }

    }

    /*******************************************
     * Method contains all listener events
     *******************************************/
    private void setListener() {
        /*setUpFlavourFourUi();*/
        view.findViewById(R.id.login_btn).setOnClickListener(this);
        view.findViewById(R.id.signup_btn).setOnClickListener(this);
        view.findViewById(R.id.iv_dashboard_referesh).setOnClickListener(this);
        /*view.findViewById(R.id.tv_demo).setOnClickListener(this);*/
        llDashboardClientDataContainer.setOnClickListener(this);
        view.findViewById(R.id.cv_profile_not_complete).setOnClickListener(this);
        btnPayNow.setOnClickListener(this);
        view.findViewById(R.id.iv_dismiss).setOnClickListener(this);
        view.findViewById(R.id.tvViewAll).setOnClickListener(this);
        view.findViewById(R.id.journey_card).setOnClickListener(this);
    }


    /*******************************************
     * Method used to set XIRR data
     *******************************************/


    private void getXirrData() {
        String url = Config.My_Journey;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        showXirrData(response.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void showXirrData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (Utils.getConfigData(mSession).optString("XIRRMainCard").equalsIgnoreCase("Y")) {
                JSONArray jsonArray = jsonObject.optJSONArray("MyJourneyDetail");
                JSONObject jsonObject1 = jsonArray.optJSONObject(0);

                mTvRealise.setText(getString(R.string.Rs) + jsonObject1.optString("TotalGain"));
                String xirrData = jsonObject1.optString("XIIR");
                boolean isFound2 = xirrData.indexOf("-") != -1 ? true : false; //true
                if (isFound2) {
/*
                    xirrData = xirrData.substring(0, 1) + getString(R.string.Rs) + " " + xirrData.substring(1, xirrData.length());
*/
                    mTvXirr.setText("(" + xirrData + "%" + ")");
                } else {
                    mTvXirr.setText("(" + jsonObject1.optString("XIIR") + "%" + ")");
                }

                if (jsonObject1.optString("XIIR").contains("-")) {
                    mTvXirr.setTextColor(getResources().getColor(R.color.colorNegativeValues));

                } else {
                    mTvXirr.setTextColor(getResources().getColor(R.color.colorPositiveValues));

                }
                llXirrGain.setVisibility(View.VISIBLE);

            } else if (Utils.getConfigData(mSession).optString("XIRRReport").equalsIgnoreCase("Y")) {
                JSONArray jsonArray = jsonObject.optJSONArray("MyJourneyDetail");
                JSONObject jsonObject1 = jsonArray.optJSONObject(0);

                mTvRealise.setText(getString(R.string.Rs) + jsonObject1.optString("TotalGain"));
                String xirrData = jsonObject1.optString("XIIR");
                boolean isFound2 = xirrData.indexOf("-") != -1 ? true : false; //true
                if (isFound2) {
                    xirrData = xirrData.substring(0, 1) + getString(R.string.Rs) + " " + xirrData.substring(1, xirrData.length());
                    mTvXirr.setText("(" + xirrData + "%" + ")");
                } else {
                    mTvXirr.setText("(" + jsonObject1.optString("XIIR") + "%" + ")");
                }

                if (jsonObject1.optString("XIIR").contains("-")) {
                    mTvXirr.setTextColor(getResources().getColor(R.color.colorNegativeValues));

                } else {
                    mTvXirr.setTextColor(getResources().getColor(R.color.colorPositiveValues));

                }
                if (jsonObject1.optString("TotalGain").contains("-")) {
                    mTvRealise.setTextColor(getResources().getColor(R.color.colorNegativeValues));

                } else {
                    mTvRealise.setTextColor(getResources().getColor(R.color.colorPositiveValues));

                }
                view.findViewById(R.id.journey_card).setVisibility(View.VISIBLE);
            } else {
                llXirrGain.setVisibility(View.GONE);
                view.findViewById(R.id.journey_card).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_btn) {
            onLoginBtnClick();
        } else if (id == R.id.signup_btn) {// mActivity.displayViewOther(5, null);
            onSignUpBtnClick();
        } else if (id == R.id.iv_dashboard_referesh) {
            onRefresh();
                /*    case R.id.tv_demo:
                mActivity.displayViewOther(222, null);
                break;*/
        } else if (id == R.id.btn_pay) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "pay_now");
            mActivity.displayViewOther(36, bundle);
        } else if (id == R.id.iv_dismiss) {
            mCvPaymentNow.setVisibility(View.GONE);
        } else if (id == R.id.cv_profile_not_complete) {
            mActivity.displayViewOther(16, null);
        } else if (id == R.id.ll_dashboard_client_data_container) {
            mActivity.onPortFolioClick();
        } else if (id == R.id.tvViewAll) {
            if (mDucSwipeAdapter.mDataListRycleView.size() > 0)
                mActivity.displayViewOther(100, null);
            else
                mApplication.showSnackBar(rvFinancialTools, "No documents");
        } else if (id == R.id.journey_card) {
            mActivity.displayViewOther(24, null);
        }


    }


    private void onRefresh() {
        if (AppApplication.sClientDashboard.isEmpty()) {
            callDashboardApi();
            getProfileList();
        } else {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);

            mLinerDashboardContainer.setVisibility(View.VISIBLE);
            System.out.println();
            setUpUiData();
        }
    }

    private void showDialog() {
        if (mActivity != null) {
            final Dialog dialog = new Dialog(mActivity);
            dialog.setContentView(R.layout.dailog_investnow);
            investwell.utils.customView.CustomButton rdybtn = dialog.findViewById(R.id.ready_btn);
            investwell.utils.customView.CustomButton notrdybtn = dialog.findViewById(R.id.notready_btn);
            TextView notes = dialog.findViewById(R.id.notes);
            notes.setText(R.string.notes);

            rdybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mSession.getUCC_CODE().isEmpty() && !mSession.getUCC_CODE().equalsIgnoreCase("NA")) {
                        mActivity.displayViewOther(52, null);

                    } else if (mSession.getHasLoging() && mSession.getUCC_CODE().isEmpty()) {

                        mActivity.displayViewOther(36, null);
                    } else {
                        if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                            startActivity(new Intent(getActivity(), SignUpActivity.class));
                        }
                    }

                    dialog.dismiss();
                }
            });

            notrdybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void setUpQuickReadAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvQuickRead.setLayoutManager(layoutManager);
        mAdapter = new DocumentAllAdapter(getActivity(), new ArrayList<JSONObject>());
        rvQuickRead.setAdapter(mAdapter);
        updateDocumentdata();
    }

    //this method is checking Client Profile has completed or not
    //this method is checking Client Profile has completed or not
    private void checkProfileStatus(JSONObject mJsonObject) {


        if (mSession.getHasLoging()) {

            if (((!mSession.getLoginType().equals("Broker")) || (!mSession.getLoginType().equals("SubBroker")) || (!mSession.getLoginType().equals("RM"))
                    || (!mSession.getLoginType().equalsIgnoreCase("Zone"))
                    || (!mSession.getLoginType().equalsIgnoreCase("Region"))
                    || (!mSession.getLoginType().equalsIgnoreCase("Branch")))) {
                try {
                    String UserData = "";
                    String CAFStatus = mJsonObject.optString("CAFStatus");
                    String DocUploadStatus = mJsonObject.optString("DocUploadStatus");
                    String FatcaStatus = mJsonObject.optString("FatcaStatus");
                    if (llDashboardClientDataContainer.getVisibility() == View.GONE) {
                        UserData = "";
                    } else {
                        UserData = "HasData";
                    }
                    if (mSession.getUCC_CODE().isEmpty()) {
                        boolean b=(CAFStatus.equals("N") || DocUploadStatus.equals("N") || FatcaStatus.equals("N"));
                        if ( b && UserData.isEmpty()) {
                            if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
                                mCvProfileNotCompleted.setVisibility(View.VISIBLE);
                            }else{
                                mCvProfileNotCompleted.setVisibility(View.GONE);
                            }
                        } else if (b && !UserData.isEmpty()) {
                            mCvProfileNotCompleted.setVisibility(View.GONE);
                        } else if ((CAFStatus.equals("Y") && DocUploadStatus.equals("Y") && FatcaStatus.equals("Y"))) {
                            mCvProfileNotCompleted.setVisibility(View.GONE);
                        }

                    } else if (!mSession.getUCC_CODE().isEmpty()) {

                        if ((CAFStatus.equals("Y") && DocUploadStatus.equals("Y") && FatcaStatus.equals("Y"))) {
                            mCvProfileNotCompleted.setVisibility(View.GONE);
                        } else if ((CAFStatus.equals("N") || DocUploadStatus.equals("N") || FatcaStatus.equals("N")) && UserData.isEmpty()) {
                            if (!PVCode.equals("300")) {
                                if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
                                    mCvProfileNotCompleted.setVisibility(View.VISIBLE);
                                }else{
                                    mCvProfileNotCompleted.setVisibility(View.GONE);
                                }
                            } else {
                                mCvProfileNotCompleted.setVisibility(View.GONE);
                            }
                        } else {
                            mCvProfileNotCompleted.setVisibility(View.GONE);
                        }
                    } else {
                        mCvProfileNotCompleted.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mCvProfileNotCompleted.setVisibility(View.GONE);
            }


        }

    }


    private void updateViewPager() {
        List<Fragment> fragList = new ArrayList<>();
        Fragment asset_allocation = new AssetAllocation();
        Fragment category_allocation = new CategoryAllocation();
        Fragment applicant_allocation = new ApplicantAllocation();
        Fragment fund_allocation = new FundAllocation();
        if (Utils.getConfigData(mSession).optString("ChartAsset").equalsIgnoreCase("Y")) {
            fragList.add(asset_allocation);
        }
        if (Utils.getConfigData(mSession).optString("ChartCategory").equalsIgnoreCase("Y")) {
            fragList.add(category_allocation);
        }
        if (Utils.getConfigData(mSession).optString("ChartApplicant").equalsIgnoreCase("Y")) {
            fragList.add(applicant_allocation);
        }
        if (Utils.getConfigData(mSession).optString("ChartAMC").equalsIgnoreCase("Y")) {
            fragList.add(fund_allocation);
        }

        FragViewPagerAdapter mPagerAdapter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            FragmentManager fragmentManager = getChildFragmentManager();
            if (fragmentManager!=null){
                mPagerAdapter = new FragViewPagerAdapter(fragmentManager, fragList);
            }
        }
        mPager.setAdapter(mPagerAdapter);

        mPager1a.setAdapter(mPagerAdapter);
        mTablayout.setupWithViewPager(mPager);
        mTabLayout1a.setupWithViewPager(mPager1a);
        if(!fragList.isEmpty()) {
            setupTabIcons(fragList.size());
            rlPiewView.setVisibility(View.VISIBLE);
        }else{
            rlPiewView.setVisibility(View.GONE);
        }

    }

    private void setupTabIcons(int item_no) {
        String[] tabstext;
        tabstext = tabtext;

        for (int i = 0; i < item_no; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mTablayout.getTabAt(i)).setText(tabstext[i]);
            }
        }

        for (int i = 0; i < item_no; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mTabLayout1a.getTabAt(i)).setText(tabstext[i]);
            }
        }
    }

    private void onLoginBtnClick() {
     /*  if (mSession.getHasAppLockEnable()) {
           Intent intent = new Intent(mActivity, AppLockOptionActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.putExtra("type", "verify_lock");
           intent.putExtra("callFrom", "sClientDashboard");
           startActivity(intent);
       } else {
           Intent intent = new Intent(mActivity, LoginActivity.class);
           intent.putExtra(AppConstants.LOGIN_COME_FROM, "Dashboard");
           startActivity(intent);
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
               Objects.requireNonNull(mActivity).finish();
           }
       }*/

        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.putExtra(AppConstants.LOGIN_COME_FROM, "Dashboard");
        startActivity(intent);

    }

    private void onSignUpBtnClick() {
        Intent intent = new Intent(mActivity, SignUpActivity.class);
        intent.putExtra(AppConstants.LOGIN_COME_FROM, "Dashboard");
        startActivity(intent);

    }

    /****************************************Frag
     * Converting dp to pixel
     ****************************************/
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @SuppressLint("SetTextI18n")
    private void setUpUiData() {

        try {
            JSONObject jsonObject = new JSONObject(AppApplication.sClientDashboard);
            if (jsonObject.optBoolean("Status")) {
                mCvNoData.setVisibility(View.GONE);

                rlDashboardDefaultCard.setVisibility(View.GONE);

                llDashboardClientDataContainer.setVisibility(View.VISIBLE);

                if (!AppApplication.sClientDashboard.isEmpty()
                        && !AppApplication.category_allocation.isEmpty()
                        && !AppApplication.applicant_allocation.isEmpty()
                        && !AppApplication.fund_allocation.isEmpty()) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

                    updateViewPager();
                } else {
                    if (Utils.isNetworkConnected(mActivity)) {
                        callAllocationAssetsApi(AppConstants.APP_BID, mSession.getPassKey(), mSession.getCID());
                        callAllocationCategoryApi(AppConstants.APP_BID, mSession.getPassKey(), mSession.getCID());
                        callAllocationApplicationApi(AppConstants.APP_BID, mSession.getPassKey(), mSession.getCID());
                        callAllocationFundApi(AppConstants.APP_BID, mSession.getPassKey(), mSession.getCID());
                    } else {
                        mApplication.showSnackBar(rvFinancialTools, getResources().getString(R.string.no_internet));
                    }
                }

                JSONArray jsonArray = jsonObject.optJSONArray("DBSnapshotDetail");
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                String initialValue = jsonObject1.optString("InitialValue");
                String currentValue = jsonObject1.optString("CurrentValue");
                String gain1 = jsonObject1.optString("Gain");
                String dividend = jsonObject1.optString("DividendFragment");
                String CAGR = jsonObject1.optString("CAGR");
                String oneDayChange = jsonObject1.optString("OneDayChange");
                String netInvestment = jsonObject1.optString("NetInvestment");

              /* if (gain1.contains("-")) {
                   tvGainArrow.setBackgroundResource(R.drawable.menu_down);
               } else {
                   tvGainArrow.setBackgroundResource(R.drawable.menu_up);
               }*/
                String gainData = gain1;
                boolean isFound2 = gainData.indexOf("-") != -1 ? true : false; //true
                if (isFound2) {
                    gainData = gainData.substring(0, 1) + getString(R.string.Rs) + " " + gainData.substring(1, gainData.length());
                    tvGain.setText(gainData);

                } else {
                    tvGain.setText(getString(R.string.Rs) + " " + gain1);
                }
                if (!TextUtils.isEmpty(initialValue) && !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("GainMainCard")) &&
                        Utils.getConfigData(mSession).optString("GainMainCard").equalsIgnoreCase("Y")) {
                    view.findViewById(R.id.grp_gain).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.grp_gain).setVisibility(View.GONE);
                }
                if (gain1.contains("-")) {

                    tvReturnCagr.setTextColor(getResources().getColor(R.color.colorNegativeValues));
                } else {

                    tvReturnCagr.setTextColor(getResources().getColor(R.color.colorPositiveValues));
                }
                tvMarketValue.setText(getString(R.string.Rs) + " " + currentValue);

                if (!TextUtils.isEmpty(initialValue) && !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("PurchaseCostMainCard")) &&
                        Utils.getConfigData(mSession).optString("PurchaseCostMainCard").equalsIgnoreCase("Y")) {
                    tvPurchaseCost.setText(getString(R.string.Rs) + " " + initialValue);
                    tvPurchaseCost.setVisibility(View.VISIBLE);
                } else {
                    tvPurchaseCost.setVisibility(View.GONE);
                }

                if( Utils.getConfigData(mSession).optString("NetInvestment").equalsIgnoreCase("Y")) {
                    ll_netInv.setVisibility(View.VISIBLE);
                    tv_netInv_value.setText( getString(R.string.Rs) + " " + netInvestment);
                }else{
                    ll_netInv.setVisibility(View.GONE);
                }

                String input = oneDayChange;
                boolean isFound = input.indexOf("-") != -1 ? true : false; //true
                if (isFound) {
                    input = input.substring(0, 0) + getString(R.string.Rs) + " " + input.substring(1, input.length());

                    tvDaysChange.setText("(" + input + ")");
                } else {
                    tvDaysChange.setText("(" + getString(R.string.Rs) + " " + oneDayChange + ")");
                }
                if (oneDayChange.contains("-")) {
                    tvDaysChange.setTextColor(getResources().getColor(R.color.colorNegativeValues));
                } else {
                    tvDaysChange.setTextColor(getResources().getColor(R.color.colorPositiveValues));

                }
                if ((!TextUtils.isEmpty(input) || !TextUtils.isEmpty(oneDayChange)) && !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("DayChangeRequired")) &&
                        Utils.getConfigData(mSession).optString("DayChangeRequired").equalsIgnoreCase("Y")) {

                    tvDaysChange.setVisibility(View.VISIBLE);
                } else {
                    tvDaysChange.setVisibility(View.GONE);
                }
              /* if (jsonObject1.optString("Amount").contains("-")) {
                   //viewHolder.tvTransacAmount.getText().toString().replace("-","");
                  tvDaysChange.setTextColor(getResources().getColor(R.color.colorNegativeValues));
               } else {
                  tvDaysChange.setTextColor(getResources().getColor(R.color.colorPositiveValues));

               }*/

                if (!TextUtils.isEmpty(CAGR) && !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CAGRMainCard")) &&
                        Utils.getConfigData(mSession).optString("CAGRMainCard").equalsIgnoreCase("Y")) {
                    tvReturnCagr.setText("(" + CAGR + "%" + ")");
                    tvReturnCagr.setVisibility(View.VISIBLE);
                } else {
                    tvReturnCagr.setVisibility(View.GONE);
                }
                values = currentValue + "|" + initialValue + "|" + gain1 + "|" + CAGR;


            } else {
                if (!jsonObject.has("PVCode") && !jsonObject.optString("PVCode").equalsIgnoreCase("300")) {
                    // checkProfileStatus();

                    if (mSession.getLoginType().equals("Prospects")) {
                        mCvNoData.setVisibility(View.GONE);
                    } else {
                        mCvNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    PVCode = jsonObject.optString("PVCode");
                    mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), jsonObject.optString("ServiceMSG"), "message", false, true);
                }


                rlDashboardDefaultCard.setVisibility(View.GONE);
                llDashboardClientDataContainer.setVisibility(View.GONE);
                rlPiewView.setVisibility(View.GONE);

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

                mLinerDashboardContainer.setVisibility(View.VISIBLE);

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ((mSession.getHasLoging() && (!mSession.getLoginType().equals("Broker") &&
                    !mSession.getLoginType().equals("SubBroker") && !mSession.getLoginType().equals("RM"))) &&
                    (!mSession.getHasFatca() || !mSession.getHasCAFStatus() || !mSession.getHasSignature())) {
                if (mSession.getUname().isEmpty()) {
                    LoginWithOutPassword(mSession.getEmail());
                } else {
                    LoginWithOutPassword(mSession.getUname());
                }


            }
        }


    }

    /*************************************************
     * Method called when user clicks ic_invest_route_goal card
     ***********************************************************/
    private void onGoalClick() {

    /*   if ((mSession.getRiskCode().equalsIgnoreCase("NA") || mSession.getRiskCode().isEmpty()) && mApplication.sGOAL_SUMMARY_ARRAY.length() == 0)
           mActivity.displayViewOther(59, null);
       else
           mActivity.displayViewOther(70, null);*/

        mActivity.displayViewOther(70, null);
    }


    /*************************************************
     * Method contains calling DASHBOARD API operation
     ***********************************************************/

    public void callDashboardApi() {
        mLinerDashboardContainer.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();

        String url = Config.Dashboard;
        try {
            JSONObject jsonObject = new JSONObject();
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")) {
                jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
                jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
                jsonObject.put(AppConstants.CUSTOMER_ID, mSession.getCID());
                jsonObject.put("OnlyMF", "Y");
                jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");
            } else {
                jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
                jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
                jsonObject.put(AppConstants.CUSTOMER_ID, mSession.getCID());
                jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AppApplication.sClientDashboard = response.toString();
                    setUpUiData();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
            }
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void getGoalSummary() {
        String url = Config.GOAL_SUMMARY;
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            jsonParam.put("GoalID", "");
        } catch (Exception e) {

        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.optBoolean("Status")) {
                        mApplication.sGOAL_SUMMARY_ARRAY = jsonObject.getJSONArray("GoalDetailList");

                    } else {
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
                        mApplication.showSnackBar(rvFinancialTools, jsonObject.optString("error"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(rvFinancialTools, getResources().getString(R.string.no_internet));
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }


    /*************************************************************
     Method to check User Deatil with login without password
     * **********************************************************/

    private void LoginWithOutPassword(final String email) {
        /* DialogsUtils.showProgressBar(getActivity(), false);*/
        String url = Config.LOGIN_WITHOUT_PASSWORD;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Username", email);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject objDa) {
                    if (objDa.optString("Status").equalsIgnoreCase("True")) {
/*
                       DialogsUtils.hideProgressBar();
*/
                        String userType = objDa.optString("LoginCategory");

                        //if (objDa.optString("ActiveStatus").equalsIgnoreCase("Yes")){
                        mSession.set_login_detail(objDa.toString());
                        mSession.setHasFirstTimeCompleted(true);
                        mSession.setEmail(objDa.optString("Email"));
                        mSession.setLoginType(userType);
                        mSession.setFullName(objDa.optString("Name"));
                        mSession.setUCC_CODE(objDa.optString("UCC"));
                        mSession.setMobileNumber(objDa.optString("MobileNo"));
                        mSession.setPAN(objDa.optString("PAN"));
                        mSession.setDOB(objDa.optString("DOB"));
                        mSession.settaxStatus(objDa.optString("TaxStatus"));
                        mSession.setOccupation(objDa.optString("Occupation"));
                        mSession.setTransactionPermission(objDa.optJSONArray("TransactionPermission").toString());
                        mSession.setLoginType(userType);

                        mSession.setHasLoging(true);
                        mSession.setUserType("1");
                        mSession.setAppType(objDa.optString("OnlineOption"));

                        mSession.setHasMendate(objDa.optString("MandateStatus").equals("Y"));
                        mSession.setHasFatca(objDa.optString("FATCAStatus").equals("Y"));
                        mSession.setHasCAFStatus(objDa.optString("CAFStatus").equals("Y"));
                        mSession.setHasSignature(objDa.optString("DocUploadStatus").equals("Y"));
                        mSession.setPANKYC(objDa.optString("PANKYCStatus").equals("Y"));

                        mSession.setRMmobile(objDa.optString("RMMobile"));
                        mSession.setRMemail(objDa.optString("RMEmail"));
                        mSession.setRM(objDa.optString("RM"));
                        mSession.setUname(email);

                        mSession.setRiskName(objDa.optString("RiskName"));
                        mSession.setRiskCode(objDa.optString("RiskCode"));
                        mSession.setRiskDescription(objDa.optString("RiskDescription"));

                        mSession.setTransactionPermission(objDa.optJSONArray("TransactionPermission").toString());

                        mSession.setHasLoging(true);
                        mSession.setUserType("1");
                        mSession.setAppType(objDa.optString("OnlineOption"));


                        if (userType.equals("RM") || userType.equals("SubBroker")) {
                            mSession.setSecondryCID(objDa.optString("CID"));
                        } else {
                            mSession.setCID(objDa.optString("CID"));
                            mSession.setSecondryCID(objDa.optString("CID"));
                        }

                        if ((mSession.getLoginType().equals("Broker")
                                || mSession.getLoginType().equals("SubBroker")
                                || mSession.getLoginType().equals("RM"))) {
                            mSession.setBrokerFullName(objDa.optString("Name"));
                        } else {
                            mSession.setFullName(objDa.optString("Name"));
                        }

                        checkProfileStatus(objDa);
                    } else {
                        //  Toast.makeText(getActivity(), objDa.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    /*DialogsUtils.hideProgressBar();*/
                }
            });

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {

        }
    }


    /*************************************************
     * Method contains calling ASSET ALLOCATION API operation
     ***********************************************************/

    private void callAllocationAssetsApi(String bid, String passkey, String cid) {
        url = Config.PAllocation;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, bid);
            jsonObject.put(AppConstants.PASSKEY, passkey);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

                    AppApplication.asset_allocation = response.toString();
                    updateViewPager();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
            }
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void updateDocumentdata() {
       /* try {
            List<JSONObject> documentList = new ArrayList<>();
            if (!TextUtils.isEmpty(mSession.getDocumentData())) {
                JSONObject jsonObject = new JSONObject(mSession.getDocumentData());
                JSONArray jsonArray = jsonObject.optJSONArray("DocumentList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (!jsonObject1.toString().contains(".pdf"))
                        documentList.add(jsonObject1);
                }
                mDucSwipeAdapter.updateList(documentList);
                rlQuickRead.setVisibility(View.VISIBLE);
            } else {
                rlQuickRead.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        List<JSONObject> documentList = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(mSession.getDocumentData())) {
                JSONObject jsonObject = new JSONObject(mSession.getDocumentData());
                JSONArray jsonArray = jsonObject.optJSONArray("DocumentList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (!jsonObject1.toString().contains(".pdf"))
                        documentList.add(jsonObject1);
                }
                mAdapter.updateList(documentList);
                rlQuickRead.setVisibility(View.VISIBLE);

            } else {
                rlQuickRead.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*************************************************
     * Method contains calling CATEGORY ALLOCATION API operation
     ***********************************************************/

    private void callAllocationCategoryApi(String mBid, String mPasskey, String cid) {
        url = Config.Allocation_Category;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConstants.PASSKEY, mPasskey);
            jsonObject.put(AppConstants.KEY_BROKER_ID, mBid);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

                    AppApplication.category_allocation = response.toString();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
            }
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*************************************************
     * Method contains calling APPLICATION ALLOCATION API operation
     ***********************************************************/

    private void callAllocationApplicationApi(String mBid, String mPasskey, String cid) {

        String url = Config.Allocation_Applicant;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mPasskey);
            jsonObject.put("Bid", mBid);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

                    AppApplication.applicant_allocation = response.toString();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
            }
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*************************************************
     * Method contains calling FUND ALLOCATION API operation
     ***********************************************************/

    private void callAllocationFundApi(String bid, String passkey, String cid) {
        url = Config.Allocation_Fund;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, bid);
            jsonObject.put(AppConstants.PASSKEY, passkey);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

                    AppApplication.fund_allocation = response.toString();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLinerDashboardContainer.setVisibility(View.VISIBLE);

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
            }
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfileList() {
        String url = Config.PROFILE_LIST;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        if (response.optBoolean("Status")) {
                            JSONArray araArray = response.getJSONArray("ProfileListDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);

                                String activeOrderStatus = object.optString("ActiveOrder");
                                if (!activeOrderStatus.equals("") && !activeOrderStatus.equals("0")) {
                                    payList.add(object);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (payList.size() > 0) {
                            mCvPaymentNow.setVisibility(View.VISIBLE);
                        } else {
                            mCvPaymentNow.setVisibility(View.GONE);
                        }

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
/*
                           mApplication.showSnackBar(rvFinancialTools,jsonObject.toString());
*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } /*else if (volleyError instanceof NoConnectionError)
                       mApplication.showSnackBar(rvFinancialTools,getResources().getString(R.string.no_internet));
*/

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
                requestQueue.add(jsonObjectRequest);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

package investwell.client.fragment.home;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

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
import investwell.client.activity.WebViewActivity;
import investwell.client.adapter.DocumentSwipeAdapter;
import investwell.client.adapter.FinancialToolAdapter;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.adapter.InvestmentRouteAdapter;
import investwell.client.adapter.SectionTypeOneAdapter;
import investwell.client.flavourtypefour.fragment.HomeClientFlavourFourFragment;
import investwell.client.fragment.allocation.ApplicantAllocation;
import investwell.client.fragment.allocation.AssetAllocation;
import investwell.client.fragment.allocation.CategoryAllocation;
import investwell.client.fragment.others.FundAllocation;
import investwell.client.fragment.requestservice.ServiceRequest;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.FirebaseAnalyticsHelper;
import investwell.utils.GridSpacingItemDecoration;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.model.FinancialTools;


public class FragHomeClient extends Fragment implements View.OnClickListener,
        InvestmentRouteAdapter.InvestmentRouteListener, FinancialToolAdapter.FinancialToolListener {
    public static String values;
    private View view;
    private RelativeLayout rlMainLayout, rlDashboardDefaultCard, mrlSpecialMessage;
    private AppSession mSession;
    private RequestQueue requestQueue;
    private TextView tvMarketValue, tvPurchaseCost, tvDaysChange, tvGain, tvReturnCReturn, mTvMessage;
    private RelativeLayout mRlPieView;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private TabLayout mTabLayout, mTabLayout1a, tabLayoutFlavourFour;
    private ViewPager mPager, mPager1a, viewPagerFlavourFour;
    private String[] mTabText;
    private String url, PVCode = "";
    private JsonObjectRequest jsonObjectRequest;
    private RecyclerView rvDashboardData;
    private LinearLayout llAllocationCharts1A, ll_netInv;
    private FinancialToolAdapter financialToolAdapter;
    private List<FinancialTools> financialToolsList;
    private RecyclerView rvFinancialTools, rvFinancialTools1a;
    private CardView mCvNoData, mCvPaymentNow, mCvAllocationCharts;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ConstraintLayout llDashboardClientDataContainer;
    private LinearLayout mLinerDashboardContainer, mCvProfileNotCompleted, llDashboardFlavourFour;
    private CustomButton btnPayNow;
    private TextView mTvRealise, mTvXReturn, tv_netInv_value;
    private RecyclerView rvQuickRead;
    private DocumentSwipeAdapter mDucSwipeAdapter;
    private RelativeLayout rlQuickRead;
    private LinearLayout  mLlXReturnGain;
    private SectionTypeOneAdapter sectionTypeOneAdapter;
    private JSONArray sectionJSONArray;
    private ArrayList<JSONObject> sectionJSONObjectList;
    private String secTitle = "";
    private FirebaseAnalytics mFirebaseAnalytics;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);

        }
        mActivity.updateCart();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home_client, container, false);
        setInitializer();
        initialUiCheck();
        setIncompleteCardAnimation();
        setUpQuickReadAdapter();
        if (AppApplication.sGOAL_SUMMARY_ARRAY.length() == 0)
            getGoalSummary();
        if (mSession.getHasLoging()) {
            callXReturnApi();
        }
        setUpSectionsAdapter(view);
        callGoalSummaryV1();
        setListener();
        //Calling a helper class method
        FirebaseAnalyticsHelper.callFireBaseAnalytics(mFirebaseAnalytics, mActivity);
setCustomTabFont();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                initialUiCheck();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });
        return view;
    }
private void setCustomTabFont(){

    for (int i = 0; i < mTabLayout.getTabCount(); i++) {
        //noinspection ConstantConditions
        TextView tv = (TextView)LayoutInflater.from(mActivity).inflate(R.layout.custom_tab,null);
        Typeface typeface = ResourcesCompat.getFont(mActivity,R.font.lato_bold);
        tv.setTypeface(typeface);
        if(mTabLayout.getTabAt(i)!=null) {
            mTabLayout.getTabAt(i).setCustomView(tv);
            mTabLayout1a.getTabAt(i).setCustomView(tv);
        }
    }
}
    /***************************************************
     Method contains all initializations of elements
     *****************************************************/

    private void setInitializer() {
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        mSession = AppSession.getInstance(mActivity);
        // Obtain the FirebaseAnalytics instance.
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mActivity);
        sectionJSONArray = new JSONArray();
        sectionJSONObjectList = new ArrayList<>();
        rlMainLayout = view.findViewById(R.id.rl_dashboard_parent_container);
        rlDashboardDefaultCard = view.findViewById(R.id.rl_dashboard_card_container);
        mrlSpecialMessage = view.findViewById(R.id.rlSpecialMessage);
        mTvMessage = view.findViewById(R.id.tvMessage);
        mCvNoData = view.findViewById(R.id.cv_dashboard_noData);
        rvQuickRead = view.findViewById(R.id.rv_quick_read);
        llDashboardClientDataContainer = view.findViewById(R.id.ll_dashboard_client_data_container);
        mLinerDashboardContainer = view.findViewById(R.id.ll_dashboard_container);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mCvProfileNotCompleted = view.findViewById(R.id.cv_profile_not_complete);
        mCvPaymentNow = view.findViewById(R.id.cv_pay_now);
        rvDashboardData = view.findViewById(R.id.rv_invest_data);
        tvMarketValue = view.findViewById(R.id.tv_market_value);
        tvPurchaseCost = view.findViewById(R.id.purchase_cost);
        tvDaysChange = view.findViewById(R.id.days_change);
        tvGain = view.findViewById(R.id.gain);
        mRlPieView = view.findViewById(R.id.rl_animatedPieView);
        tvReturnCReturn = view.findViewById(R.id.return_cagr);
        financialToolsList = new ArrayList<>();
        rlQuickRead = view.findViewById(R.id.rl_quick_read);
        mCvAllocationCharts = view.findViewById(R.id.cv_allocation_charts);
        llAllocationCharts1A = view.findViewById(R.id.ll_allocation_charts);
        rvFinancialTools = view.findViewById(R.id.rv_financial_tools);
        rvFinancialTools1a = view.findViewById(R.id.rv_financial_tools_1a);
        mTabLayout = view.findViewById(R.id.tab_dashboard_allocation_charts);
        tabLayoutFlavourFour = view.findViewById(R.id.tabLayout_flavour_four);
        mTabLayout1a = view.findViewById(R.id.tab_dashboard_allocation_charts_1a);
        tabLayoutFlavourFour = view.findViewById(R.id.tabLayout_flavour_four);
        mPager = view.findViewById(R.id.viewpager_charts);
        viewPagerFlavourFour = view.findViewById(R.id.viewpager_dashboard_flavour_four);
        mPager.setOffscreenPageLimit(mPager.getChildCount());
        mPager.setSaveFromParentEnabled(false);
        mPager1a = view.findViewById(R.id.viewpager_charts_1a);
        mPager1a.setOffscreenPageLimit(mPager1a.getChildCount());
        mPager1a.setSaveFromParentEnabled(false);
        btnPayNow = view.findViewById(R.id.btn_pay);
        llDashboardFlavourFour = view.findViewById(R.id.ll_dashboard_flavour_four);
        mTabText = getResources().getStringArray(R.array.allocation_charts_tab_txt);
        mLlXReturnGain = view.findViewById(R.id.ll_all_time_gain);

        mTvRealise = view.findViewById(R.id.tvrealised);
        mTvXReturn = view.findViewById(R.id.tvxirr);

        ll_netInv = view.findViewById(R.id.ll_netInv);
        tv_netInv_value = view.findViewById(R.id.tv_netInv_value);

        handleSpecialMessage();


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
      /* if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CalculatorRequired")) &&
               Utils.getConfigData(mSession).optString("CalculatorRequired").equalsIgnoreCase("Y")) {
           view.findViewById(R.id.rl_financial_tools_container).setVisibility(View.VISIBLE);

       } else {
           view.findViewById(R.id.rl_financial_tools_container).setVisibility(View.GONE);

       }
*/
        if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.signup_btn).setVisibility(View.VISIBLE);

        } else {
            view.findViewById(R.id.signup_btn).setVisibility(View.GONE);

        }


        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
            llAllocationCharts1A.setVisibility(View.VISIBLE);
            mCvAllocationCharts.setVisibility(View.GONE);

        } else {
            llAllocationCharts1A.setVisibility(View.GONE);
            mCvAllocationCharts.setVisibility(View.VISIBLE);

        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
            mLinerDashboardContainer.setVisibility(View.GONE);
            llDashboardFlavourFour.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") || Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
            llDashboardFlavourFour.setVisibility(View.GONE);
            mLinerDashboardContainer.setVisibility(View.VISIBLE);
            rvDashboardData.setVisibility(View.GONE);
            rlQuickRead.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
            llDashboardFlavourFour.setVisibility(View.GONE);
            mLinerDashboardContainer.setVisibility(View.VISIBLE);
            rvDashboardData.setVisibility(View.GONE);
            rlQuickRead.setVisibility(View.GONE);
        } else {
            llDashboardFlavourFour.setVisibility(View.GONE);
            mLinerDashboardContainer.setVisibility(View.VISIBLE);
            rvDashboardData.setVisibility(View.VISIBLE);
            rlQuickRead.setVisibility(View.VISIBLE);
        }
        if (Utils.getConfigData(mSession).has("SectionList") && Utils.getConfigData(mSession).optJSONArray("SectionList").length() > 0) {
            sectionJSONArray = Utils.getConfigData(mSession).optJSONArray("SectionList");
            for (int i = 0; i < sectionJSONArray.length(); i++) {
                JSONObject object;
                try {
                    object = sectionJSONArray.getJSONObject(i);
                    if (!object.optString("Priority").equals("99")) {
                        sectionJSONObjectList.add(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            for (int i = 0; i < sectionJSONObjectList.size(); i++) {

                secTitle = sectionJSONObjectList.get(i).optString("Title");
            }
            sectionTypeOneAdapter.updateSectionsList(sectionJSONObjectList, secTitle);

        } else {
            sectionJSONObjectList.clear();
            sectionTypeOneAdapter.updateSectionsList(new ArrayList<JSONObject>(), secTitle);

        }
        setUpDashboardFlavourFourData(sectionJSONObjectList);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("QuickRead")) &&
                Utils.getConfigData(mSession).optString("QuickRead").equalsIgnoreCase("Y")) {
            updateDocumentdata();

        } else {
            rlQuickRead.setVisibility(View.GONE);

        }
    }

    private void callGoalSummaryV1() {
        String url = Config.GOAL_SUMMARY_ONE;
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (jsonObject.optBoolean("Status")) {
                        mSession.setGoalData(String.valueOf(jsonObject));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Log.e("ERROR GOAL V1", jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpDashboardFlavourFourData(ArrayList<JSONObject> sectionJSONObjectFlavourFourList) {
        ArrayList<HomeClientFlavourFourFragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < sectionJSONObjectFlavourFourList.size(); i++) {

            tabLayoutFlavourFour.addTab(tabLayoutFlavourFour.newTab().setText("" + sectionJSONObjectFlavourFourList.get(i).optString("Title")));

            fragmentList.add(HomeClientFlavourFourFragment.newInstance(i, sectionJSONObjectFlavourFourList.get(i).optString("Title")));

        }

        DynamicPagerAdapter adapter = new DynamicPagerAdapter(fragmentList, getChildFragmentManager(), tabLayoutFlavourFour.getTabCount(), sectionJSONObjectFlavourFourList);
        viewPagerFlavourFour.setAdapter(adapter);

        viewPagerFlavourFour.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutFlavourFour));
        tabLayoutFlavourFour.setupWithViewPager(viewPagerFlavourFour);

/*        If  tab  has more than 2 tabs
        then tab will scroll other wise they will take whole width of the screen*/

        if (tabLayoutFlavourFour.getTabCount() <= 4) {
            tabLayoutFlavourFour.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayoutFlavourFour.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    public static class DynamicPagerAdapter extends FragmentStatePagerAdapter {
        private final ArrayList<HomeClientFlavourFourFragment> fragmentList;
        private final ArrayList<JSONObject> sectionJSONObjectFlavourFourList;
        int mNumOfTabs;

        public DynamicPagerAdapter(ArrayList<HomeClientFlavourFourFragment> fragmentList, FragmentManager fm, int NumOfTabs, ArrayList<JSONObject> sectionJSONObjectFlavourFourList) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            this.fragmentList = fragmentList;
            this.sectionJSONObjectFlavourFourList = sectionJSONObjectFlavourFourList;
        }

        @Override
        public Fragment getItem(int position) {

            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return sectionJSONObjectFlavourFourList.get(position).optString("Title");
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    private void setUpSectionsAdapter(View view) {
        sectionTypeOneAdapter = new SectionTypeOneAdapter(mActivity, new ArrayList<JSONObject>());
        rvDashboardData.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rvDashboardData.setItemAnimator(new DefaultItemAnimator());
        rvDashboardData.setNestedScrollingEnabled(false);
        rvDashboardData.setAdapter(sectionTypeOneAdapter);
        setUpUiVisibility(view);
    }

    private void initialUiCheck() {
        if (mSession.getLoginType().isEmpty() || !mSession.getHasLoging()) {
            rlMainLayout.setVisibility(View.VISIBLE);
            mCvNoData.setVisibility(View.GONE);
            llDashboardClientDataContainer.setVisibility(View.GONE);
            rlDashboardDefaultCard.setVisibility(View.VISIBLE);
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                mLinerDashboardContainer.setVisibility(View.GONE);
            } else {
                mLinerDashboardContainer.setVisibility(View.VISIBLE);
            }
            mCvPaymentNow.setVisibility(View.GONE);
        } else {
            mActivity.updateCart();
            callDashboardApi();

            if (AppApplication.sClientDashboard.isEmpty()) {

                getProfileList();
            } else {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                        (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                    mLinerDashboardContainer.setVisibility(View.GONE);
                } else {
                    mLinerDashboardContainer.setVisibility(View.VISIBLE);
                }
                getProfileList();
                setUpUiData();
            }

        }

    }

    /*******************************************
     * Method contains all listener events
     *******************************************/
    private void setListener() {
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
        view.findViewById(R.id.ivCross).setOnClickListener(this);
        view.findViewById(R.id.tvReadMore).setOnClickListener(this);
    }


    /*******************************************
     * Method used to set XIRR data
     *******************************************/


    private void callXReturnApi() {
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


    @SuppressLint("SetTextI18n")
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

                    mTvXReturn.setText("(" + xirrData + "%" + ")");
                } else {
                    mTvXReturn.setText("(" + jsonObject1.optString("XIIR") + "%" + ")");
                }

                if (jsonObject1.optString("XIIR").contains("-")) {
                    mTvXReturn.setTextColor(getResources().getColor(R.color.colorNegativeValues));

                } else {
                    mTvXReturn.setTextColor(getResources().getColor(R.color.colorPositiveValues));

                }
                mLlXReturnGain.setVisibility(View.VISIBLE);

            } else if (Utils.getConfigData(mSession).optString("XIRRReport").equalsIgnoreCase("Y")) {
                JSONArray jsonArray = jsonObject.optJSONArray("MyJourneyDetail");
                JSONObject jsonObject1 = jsonArray.optJSONObject(0);

                mTvRealise.setText(getString(R.string.Rs) + jsonObject1.optString("TotalGain"));
                String xirrData = jsonObject1.optString("XIIR");
                boolean isFound2 = xirrData.indexOf("-") != -1 ? true : false; //true
                if (isFound2) {
                    xirrData = xirrData.substring(0, 1) + getString(R.string.Rs) + " " + xirrData.substring(1, xirrData.length());
                    mTvXReturn.setText("(" + xirrData + "%" + ")");
                } else {
                    mTvXReturn.setText("(" + jsonObject1.optString("XIIR") + "%" + ")");
                }

                if (jsonObject1.optString("XIIR").contains("-")) {
                    mTvXReturn.setTextColor(getResources().getColor(R.color.colorNegativeValues));

                } else {
                    mTvXReturn.setTextColor(getResources().getColor(R.color.colorPositiveValues));

                }
                if (jsonObject1.optString("TotalGain").contains("-")) {
                    mTvRealise.setTextColor(getResources().getColor(R.color.colorNegativeValues));

                } else {
                    mTvRealise.setTextColor(getResources().getColor(R.color.colorPositiveValues));

                }
                view.findViewById(R.id.journey_card).setVisibility(View.VISIBLE);
            } else {
                mLlXReturnGain.setVisibility(View.GONE);
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
                mApplication.showSnackBar(rlQuickRead, "No documents");
        } else if (id == R.id.journey_card) {
            mActivity.displayViewOther(24, null);
        } else if (id == R.id.ivCross) {
            mrlSpecialMessage.setVisibility(View.GONE);
        } else if (id == R.id.tvReadMore) {
            Intent intent = new Intent(mActivity, WebViewActivity.class);
            intent.putExtra("title", "Information");
            intent.putExtra("url", Utils.getConfigData(mSession).optString("SpecialMessageURL"));
            startActivity(intent);
        }


    }

    @Override
    public void onRoutesClick(int position) {
        switch (position) {
            case 0:
                onGoalClick();
                break;
            case 1:
                mActivity.displayViewOther(1, null);
                break;
            case 2:
                mActivity.displayViewOther(49, null);
                break;
            case 3:
                Bundle bundle1 = new Bundle();
                mActivity.displayViewOther(41, bundle1);
                break;
            case 4:
                mActivity.displayViewOther(66, null);
                break;

            case 5:
                mActivity.displayViewOther(90, null);
                break;
            case 6:
                mActivity.displayViewOther(74, null);
                break;
            case 7:
                if (mSession.getSave().isEmpty()) {
                    mActivity.displayViewOther(96, null);
                } else {
                    mActivity.displayViewOther(97, null);
                }
                break;
            case 8:
                mActivity.displayViewOther(79, null);
                break;
            case 9:
                Intent i = new Intent(mActivity, ServiceRequest.class);
                startActivity(i);
                break;
            case 10:
                mActivity.displayViewOther(101, null);
                break;
            case 11:
                if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                    showDialog();
                } else {
                    mActivity.displayViewOther(52, null);
                }
                break;
        }
    }

    @Override
    public void onToolsClick(int position) {
        switch (position) {
            case 0:
                mActivity.displayFinancialCalculators(10, null);
                break;
            case 1:
                mActivity.displayFinancialCalculators(3, null);

                break;
            case 2:
                mActivity.displayFinancialCalculators(5, null);
                break;
            case 3:
                mActivity.displayFinancialCalculators(7, null);

                break;
            case 4:
                mActivity.displayFinancialCalculators(8, null);
                break;
            case 5:
                mActivity.displayFinancialCalculators(6, null);

                break;


        }
    }


    private void onRefresh() {
        if (AppApplication.sClientDashboard.isEmpty()) {
            callDashboardApi();
            getProfileList();
        } else {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                mLinerDashboardContainer.setVisibility(View.GONE);
            } else {
                mLinerDashboardContainer.setVisibility(View.VISIBLE);
            }
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
                            startActivity(new Intent(mActivity, SignUpActivity.class));
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvQuickRead.setLayoutManager(layoutManager);
        mDucSwipeAdapter = new DocumentSwipeAdapter(mActivity, new ArrayList<JSONObject>());
        rvQuickRead.setAdapter(mDucSwipeAdapter);
    }

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


    /*******************************************
     * Method used to set dynamic financial items
     *******************************************/

    private void setFinancialToolAdapter() {

        financialToolAdapter = new FinancialToolAdapter(mActivity, financialToolsList, this);
        rvFinancialTools.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rvFinancialTools.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        rvFinancialTools.setItemAnimator(new DefaultItemAnimator());
        rvFinancialTools.setNestedScrollingEnabled(false);
        rvFinancialTools.setAdapter(financialToolAdapter);
        rvFinancialTools1a.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rvFinancialTools1a.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        rvFinancialTools1a.setItemAnimator(new DefaultItemAnimator());
        rvFinancialTools1a.setNestedScrollingEnabled(false);
        rvFinancialTools1a.setAdapter(financialToolAdapter);
        prepareFinancialTools();
    }


    /*******************************************
     * Method contains data for investment route items
     *******************************************/
    private void prepareFinancialTools() {
        int[] covers;
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
            covers = new int[]{
                    R.mipmap.ic_calculator_lumpsum_1a,
                    R.mipmap.ic_calculator_sip_1a,
                    R.mipmap.ic_calculator_cost_delay_sip_1a,
                    R.mipmap.ic_calculator_education_1a,
                    R.mipmap.ic_calculator_marriage_1a,
                    R.mipmap.ic_calculator_retirement_1a};
        } else {
            covers = new int[]{
                    R.mipmap.ic_calculator_lumpsum,
                    R.mipmap.ic_calculator_sip,
                    R.mipmap.ic_calculator_cost_delay_sip,
                    R.mipmap.ic_calculator_education,
                    R.mipmap.ic_calculator_marriage,
                    R.mipmap.ic_calculator_retirement};
        }

        FinancialTools a = new FinancialTools(covers[0], getResources().getString(R.string.dashboard_finance_tool_lumpsum));
        financialToolsList.add(a);

        a = new FinancialTools(covers[1], getResources().getString(R.string.dashboard_finance_tool_sip_cal));
        financialToolsList.add(a);

        a = new FinancialTools(covers[2], getResources().getString(R.string.dashboard_finance_tool_cost_sip));
        financialToolsList.add(a);

        a = new FinancialTools(covers[3], getResources().getString(R.string.dashboard_finance_tool_education_cal));
        financialToolsList.add(a);

        a = new FinancialTools(covers[4], getResources().getString(R.string.dashboard_finance_tool_marriage_cal));
        financialToolsList.add(a);

        a = new FinancialTools(covers[5], getResources().getString(R.string.dashboard_finance_tool_retirement_cal));
        financialToolsList.add(a);


        financialToolAdapter.notifyDataSetChanged();
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
        if(!fragList.isEmpty()){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mPagerAdapter = new FragViewPagerAdapter(Objects.requireNonNull(mActivity).getSupportFragmentManager(), fragList);
            }}
        mPager.setAdapter(mPagerAdapter);
        mPager1a.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mPager);
        mTabLayout1a.setupWithViewPager(mPager1a);
        if(!fragList.isEmpty()) {
            setupTabIcons(fragList.size());
            mRlPieView.setVisibility(View.VISIBLE);
        }else{
            mRlPieView.setVisibility(View.GONE);
        }

    }

    private void setupTabIcons(int item_no) {
        String[] tabstext;
        tabstext = mTabText;

        for (int i = 0; i < item_no; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mTabLayout.getTabAt(i)).setText(tabstext[i]);
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
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    }
                    updateViewPager();
                } else {
                    if (Utils.isNetworkConnected(mActivity)) {
                        callAllocationAssetsApi(AppConstants.APP_BID, mSession.getPassKey(), mSession.getCID());
                        callAllocationCategoryApi(AppConstants.APP_BID, mSession.getPassKey(), mSession.getCID());
                        callAllocationApplicationApi(AppConstants.APP_BID, mSession.getPassKey(), mSession.getCID());
                        callAllocationFundApi(mSession.getPassKey(), mSession.getCID());
                    } else {
                        mApplication.showSnackBar(llDashboardClientDataContainer, getResources().getString(R.string.no_internet));
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

                    tvReturnCReturn.setTextColor(getResources().getColor(R.color.colorNegativeValues));
                } else {

                    tvReturnCReturn.setTextColor(getResources().getColor(R.color.colorPositiveValues));
                }
                tvMarketValue.setText(getString(R.string.Rs) + " " + currentValue);

                if (!TextUtils.isEmpty(initialValue) && !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("PurchaseCostMainCard")) &&
                        Utils.getConfigData(mSession).optString("PurchaseCostMainCard").equalsIgnoreCase("Y")) {
                    tvPurchaseCost.setText( getString(R.string.Rs) + " " + initialValue);
                    tvPurchaseCost.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_invest_value_label).setVisibility(View.VISIBLE);
                } else {
                    tvPurchaseCost.setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.tv_invest_value_label).setVisibility(View.INVISIBLE);
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
                    tvReturnCReturn.setText("(" + CAGR + "%" + ")");
                    tvReturnCReturn.setVisibility(View.VISIBLE);
                } else {
                    tvReturnCReturn.setVisibility(View.GONE);
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
                mRlPieView.setVisibility(View.GONE);

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                        (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                    mLinerDashboardContainer.setVisibility(View.GONE);
                } else {
                    mLinerDashboardContainer.setVisibility(View.VISIBLE);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ((mSession.getHasLoging() && (!mSession.getLoginType().equals("Broker") &&
                    !mSession.getLoginType().equals("SubBroker") && !mSession.getLoginType().equals("RM")
                    && !mSession.getLoginType().equals("Zone")
                    && !mSession.getLoginType().equals("Region")
                    && !mSession.getLoginType().equals("Branch"))) &&
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
                    createTimer();
                    setUpUiData();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    }
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }


    /*************************************************************
     Method to check User Deatil with login without password
     * **********************************************************/

    private void LoginWithOutPassword(final String email) {
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

                        String userType = objDa.optString("LoginCategory");
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
                        mSession.setRiskImage(objDa.optString("RiskImage"));

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
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
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
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    }
                    AppApplication.asset_allocation = response.toString();
                    updateViewPager();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
            }
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void updateDocumentdata() {
        try {
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
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    }
                    AppApplication.category_allocation = response.toString();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    }
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
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
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    }
                    AppApplication.applicant_allocation = response.toString();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
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

    private void callAllocationFundApi(String passkey, String cid) {
        url = Config.Allocation_Fund;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, passkey);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    }
                    AppApplication.fund_allocation = response.toString();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4A"))) {
                        mLinerDashboardContainer.setVisibility(View.GONE);
                    } else {
                        mLinerDashboardContainer.setVisibility(View.VISIBLE);
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
                    ArrayList<JSONObject> list = new ArrayList<>();

                    try {

                        if (response.optBoolean("Status")) {
                            JSONArray araArray = response.getJSONArray("ProfileListDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);

                                String activeOrderStatus = object.optString("ActiveOrder");
                                if (!activeOrderStatus.equals("") && !activeOrderStatus.equals("0")) {
                                    list.add(object);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (list.size() > 0) {
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
                            Log.e("PROFILE LIST ERROR :",jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mActivity));
                requestQueue.add(jsonObjectRequest);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void handleSpecialMessage() {

        if (!Utils.getConfigData(mSession).optString("SpecialMessage").isEmpty()) {
            mrlSpecialMessage.setVisibility(View.VISIBLE);
            mTvMessage.setText(Utils.getConfigData(mSession).optString("SpecialMessage"));
        } else {
            mrlSpecialMessage.setVisibility(View.GONE);
        }

        if (!Utils.getConfigData(mSession).optString("SpecialMessageURL").isEmpty()) {
            view.findViewById(R.id.tvReadMore).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.tvReadMore).setVisibility(View.GONE);
        }

        if (!Utils.getConfigData(mSession).optString("PopupMessage").isEmpty() && mSession.getShowDialog()) {
            showPopUpDialog();
        }
    }

    private void showPopUpDialog() {
        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.special_message_popup);
        ImageView mIvBanner = dialog.findViewById(R.id.ivBanner);
        ImageView mIvClose = dialog.findViewById(R.id.ivClose);
        TextView mTvMessage = dialog.findViewById(R.id.tvMessage);
        CustomButton mBtnKnowMore = dialog.findViewById(R.id.btnKnowMore);
        CheckBox mCheckBox = dialog.findViewById(R.id.checkBox);
        if (!Utils.getConfigData(mSession).optString("PopupMessageLinkURL").isEmpty()) {
            mBtnKnowMore.setVisibility(View.VISIBLE);
        } else {
            mBtnKnowMore.setVisibility(View.GONE);
        }
        mTvMessage.setText(Utils.getConfigData(mSession).optString("PopupMessage"));
        mBtnKnowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("title", "Information");
                intent.putExtra("url", Utils.getConfigData(mSession).optString("PopupMessageLinkURL"));
                startActivity(intent);
                dialog.dismiss();
            }
        });

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mSession.setShowDialog(false);
                    // dialog.dismiss();
                }
            }
        });

        Picasso.get().load(Utils.getConfigData(mSession).optString("PopupMessageImagePath")).into(mIvBanner);


        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogTheme; //style id
        }
        dialog.show();


    }

    public void createTimer() {
        long currentTime = System.currentTimeMillis();
        mSession.set_current_time(currentTime);
    }

}


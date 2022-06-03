package investwell.client.flavourtypetwo.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.client.activity.SignUpActivity;
import investwell.client.adapter.DocumentSwipeAdapter;
import investwell.client.flavourtypetwo.adapter.DashboardOtherServiceAdapter;
import investwell.client.flavourtypetwo.adapter.DashboardTwoEquityItemAdapter;
import investwell.client.flavourtypetwo.adapter.InvestRouteAdapterTypeTwo;
import investwell.client.flavourtypetwo.adapter.SectionsTypeTwoAdapter;
import investwell.client.flavourtypetwo.model.InvestRouteTypeTwo;
import investwell.client.fragment.requestservice.ServiceRequest;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomTextViewBold;


public class MainActivityTypeTwo extends BaseActivity implements View.OnClickListener, InvestRouteAdapterTypeTwo.InvestRouteListenerTypeTwo,
        DashboardTwoEquityItemAdapter.EquityItemListenerTypeTwo, DashboardOtherServiceAdapter.OtherServicesClickListener {
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout llDashboardFooter, ll_dashboard_card_default, ll_dashboard_client_data_container;
    private ConstraintLayout ll_consortium_header_container;
    private Button btn_dashboard_type_two_login, btn_dashboard_type_two_enquiry, btn_dashboard_type_two_my_assets;
    private ImageView iv_dashboard_cross_btn, ivContactBtn, ivConA;
    private CardView cv_dashboard_noData;
    private TextView tvMyAssetValue, tvMyAssetConsortium;
    private AppApplication mApplication;
    private AppSession mSession;
    private double total = 0;
    private InvestRouteAdapterTypeTwo adapter;
    private DashboardTwoEquityItemAdapter dashboardTwoEquityItemAdapter;
    private DashboardOtherServiceAdapter dashboardOtherServiceAdapter;
    private List<InvestRouteTypeTwo> investmentRoutesList;
    private List<InvestRouteTypeTwo> equityItemList;
    private RecyclerView rvInvestRoute, rvEquityItems, rvOtherServices, rvSections;
    private SectionsTypeTwoAdapter sectionsTypeTwoAdapter;
    private Intent intent;
    private NestedScrollView nestedScrollView;
    private JSONArray sectionJSONArray;
    private ArrayList<JSONObject> sectionJSONObjectList;
    private RelativeLayout rlQuickRead;
    private DocumentSwipeAdapter mDucSwipeAdapter;
    private RecyclerView rvQuickRead;
    private FrameLayout flCart, flCartA, fl_notifications, fl_notifications_a;
    private CustomTextViewBold tvCartBadge, tv_notification_badge, tvCartBadgeA, tv_notification_badge_a;
    private String appType = "";
    private AppBarLayout primaryAppBar, transparentAppBar;
    private CoordinatorLayout clTypeTwo;
    private ImageView ivDefaultCard, ivClientCard;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_type_two);
        initializer();
        initialUiChecks();
        callQuickLearnApi();
        setUpInvestmentRouteAdapter();
        setUpOtherServicesAdapter();
        setUpQuickReadAdapter();
        setUpSectionsAdapter();
        notificationCount();
        setListeners();
    }

    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
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
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }

    private void initializer() {
        mSession = AppSession.getInstance(MainActivityTypeTwo.this);
        mApplication = (AppApplication) this.getApplication();
        intent = new Intent();
        investmentRoutesList = new ArrayList<>();
        primaryAppBar = findViewById(R.id.appbar);
        transparentAppBar = findViewById(R.id.appbar_transparent);
        clTypeTwo = findViewById(R.id.cl_type_two);
        equityItemList = new ArrayList<>();
        sectionJSONArray = new JSONArray();
        sectionJSONObjectList = new ArrayList<>();
        rlQuickRead = findViewById(R.id.rl_quick_read);
        rvQuickRead = findViewById(R.id.rv_quick_read);
        flCart = findViewById(R.id.fl_cart);
        flCartA = findViewById(R.id.fl_cart_a);
        ivDefaultCard = findViewById(R.id.iv_default_card);
        ivClientCard = findViewById(R.id.iv_client_card);
        fl_notifications = findViewById(R.id.fl_notifications);
        fl_notifications_a = findViewById(R.id.fl_notifications_a);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        tvCartBadgeA = findViewById(R.id.tv_cart_badge_a);
        tv_notification_badge = findViewById(R.id.tv_notification_badge);
        tv_notification_badge_a = findViewById(R.id.tv_notification_badge_a);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        llDashboardFooter = findViewById(R.id.dashboard_type_two_footer_layout);
        ivContactBtn = findViewById(R.id.ivContactUs);
        ivConA = findViewById(R.id.ivContactUs_a);
        btn_dashboard_type_two_my_assets = findViewById(R.id.btn_dashboard_type_two_my_assets);
        ll_dashboard_card_default = findViewById(R.id.ll_dashboard_card_default);
        ll_dashboard_client_data_container = findViewById(R.id.ll_dashboard_client_data_container);
        ll_consortium_header_container = findViewById(R.id.ll_consortium_header_container);
        btn_dashboard_type_two_login = findViewById(R.id.btn_dashboard_type_two_login);
        btn_dashboard_type_two_enquiry = findViewById(R.id.btn_dashboard_type_two_enquiry);
        iv_dashboard_cross_btn = findViewById(R.id.iv_dashboard_cross_btn);
        cv_dashboard_noData = findViewById(R.id.cv_dashboard_noData);
        tvMyAssetValue = findViewById(R.id.tv_my_assets_value);
        tvMyAssetConsortium = findViewById(R.id.tv_my_assets_value_consortium);
        rvInvestRoute = findViewById(R.id.rv_investment_route_type_2b);
        rvEquityItems = findViewById(R.id.rv_equity_market);
        rvOtherServices = findViewById(R.id.rv_other_services);
        nestedScrollView = findViewById(R.id.nsv_main_2b);
        rvSections = findViewById(R.id.rv_sections);

    }

    private void setListeners() {
        btn_dashboard_type_two_login.setOnClickListener(this);
        btn_dashboard_type_two_enquiry.setOnClickListener(this);
        iv_dashboard_cross_btn.setOnClickListener(this);
        ll_dashboard_client_data_container.setOnClickListener(this);
        ll_consortium_header_container.setOnClickListener(this);
        ivContactBtn.setOnClickListener(this);
        ivConA.setOnClickListener(this);
        findViewById(R.id.tvViewAll).setOnClickListener(this);
        fl_notifications.setOnClickListener(this);
        flCart.setOnClickListener(this);
        fl_notifications_a.setOnClickListener(this);
        flCartA.setOnClickListener(this);
        btn_dashboard_type_two_my_assets.setOnClickListener(this);
    }

    private void setUpQuickReadAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivityTypeTwo.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvQuickRead.setLayoutManager(layoutManager);
        mDucSwipeAdapter = new DocumentSwipeAdapter(MainActivityTypeTwo.this, new ArrayList<JSONObject>());
        rvQuickRead.setAdapter(mDucSwipeAdapter);
    }

    private void setUpInvestmentRouteAdapter() {
        adapter = new InvestRouteAdapterTypeTwo(MainActivityTypeTwo.this, investmentRoutesList, this);
        rvInvestRoute.setLayoutManager(new LinearLayoutManager(MainActivityTypeTwo.this, LinearLayoutManager.VERTICAL, false));
        rvInvestRoute.setItemAnimator(new DefaultItemAnimator());
        rvInvestRoute.setNestedScrollingEnabled(false);
        rvInvestRoute.setAdapter(adapter);
        prepareInvestRouteData();

    }


    private void setUpOtherServicesAdapter() {
        dashboardOtherServiceAdapter = new DashboardOtherServiceAdapter(MainActivityTypeTwo.this, new ArrayList<JSONObject>(), this);
        rvOtherServices.setLayoutManager(new LinearLayoutManager(MainActivityTypeTwo.this, LinearLayoutManager.HORIZONTAL, false));
        rvOtherServices.setItemAnimator(new DefaultItemAnimator());
        rvOtherServices.setNestedScrollingEnabled(false);
        rvOtherServices.setAdapter(dashboardOtherServiceAdapter);

    }

    private void callQuickLearnApi() {
        String url = Config.GET_DOCUMENTs;
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Passkey", mSession.getPassKey());
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                try {

                    String status = object.optString("Status");
                    if (status.equalsIgnoreCase("True")) {
                        mSession.setDocumentData(object.toString());
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("QuickRead")) &&
                                Utils.getConfigData(mSession).optString("QuickRead").equalsIgnoreCase("Y")) {
                            updateDocumentdata();

                        } else {
                            rlQuickRead.setVisibility(View.GONE);

                        }


                    } else {
                        //  mApplication.showSnackBar(rvEquityItems, object.optString("ServiceMSG"));
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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivityTypeTwo.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void setUpSectionsAdapter() {
        sectionsTypeTwoAdapter = new SectionsTypeTwoAdapter(MainActivityTypeTwo.this, new ArrayList<JSONObject>());
        rvSections.setLayoutManager(new LinearLayoutManager(MainActivityTypeTwo.this, LinearLayoutManager.VERTICAL, false));
        rvSections.setItemAnimator(new DefaultItemAnimator());
        rvSections.setNestedScrollingEnabled(false);
        rvSections.setAdapter(sectionsTypeTwoAdapter);
        setUpUiVisibility();
    }

    private void setUpUiVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType"))) {
            appType = Utils.getConfigData(mSession).optString("APPType");
        }
        if (Utils.getConfigData(mSession).optJSONArray("SectionList").length() > 0) {
            sectionJSONArray = Utils.getConfigData(mSession).optJSONArray("SectionList");
            for (int i = 0; i < sectionJSONArray.length(); i++) {
                JSONObject object = null;
                try {
                    object = sectionJSONArray.getJSONObject(i);
                    if (!object.optString("Priority").equals("99")) {
                        sectionJSONObjectList.add(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            sectionsTypeTwoAdapter.updateSectionsList(sectionJSONObjectList, appType);
        } else {
            sectionJSONObjectList.clear();
            sectionsTypeTwoAdapter.updateSectionsList(new ArrayList<JSONObject>(), appType);
        }
    }

    private void prepareEquityItemData() {
        int[] routeIcons = new int[]{
                R.mipmap.app_icon_r,
                R.mipmap.app_icon_r
        };
        InvestRouteTypeTwo a;
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("GoalModuleV2")) &&
                Utils.getConfigData(mSession).optString("GoalModuleV2").equalsIgnoreCase("Y")) {
            a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_model_port_tv_txt) + " " +
                    "",

                    getResources().getString(R.string.dashboard_model_port_desc_txt), routeIcons[0]);
            equityItemList.add(a);
        }
        a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_trading_online_tv_txt) + " " +
                "",
                getResources().getString(R.string.dashboard_trading_online_l_desc_txt), routeIcons[1]);
        equityItemList.add(a);
        dashboardTwoEquityItemAdapter.notifyDataSetChanged();
    }

    private void initialUiChecks() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType"))) {
            appType = Utils.getConfigData(mSession).optString("APPType");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A")))) {
                btn_dashboard_type_two_enquiry.setText(R.string.sign_up_login_txt);
                btn_dashboard_type_two_my_assets.setText(R.string.dashboard_type_two_my_portfolio_btn);

            } else {
                btn_dashboard_type_two_enquiry.setText(R.string.help_enquiry);
                btn_dashboard_type_two_my_assets.setText(R.string.dashboard_type_two_my_assets_btn);

            }
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")))) {
                primaryAppBar.setVisibility(View.VISIBLE);
                transparentAppBar.setVisibility(View.GONE);
                ivClientCard.setImageResource(R.mipmap.dashboard_top_layout_type_two_child);
                ivDefaultCard.setImageResource(R.mipmap.dashboard_top_layout_type_two_child);
                clTypeTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
            } else {
                primaryAppBar.setVisibility(View.GONE);
                transparentAppBar.setVisibility(View.VISIBLE);
                clTypeTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.home_background_bg));
                ivClientCard.setImageResource(0);
                ivDefaultCard.setImageResource(0);
            }
        }
        if (Utils.getConfigData(mSession).optString("MainAddToCart").equalsIgnoreCase("Y")) {
            flCart.setVisibility(View.VISIBLE);
        } else {
            flCart.setVisibility(View.GONE);

        }

        if (Utils.getConfigData(mSession).optString("Notification").equalsIgnoreCase("Y")) {
            fl_notifications.setVisibility(View.VISIBLE);
        } else {
            fl_notifications.setVisibility(View.GONE);

        }
        if (mSession.getLoginType().isEmpty() || !mSession.getHasLoging()) {
            llDashboardFooter.setVisibility(View.VISIBLE);
            cv_dashboard_noData.setVisibility(View.GONE);
            ll_dashboard_client_data_container.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")) {
                ll_dashboard_card_default.setVisibility(View.GONE);
            } else {
                ll_dashboard_card_default.setVisibility(View.VISIBLE);
            }
            ll_consortium_header_container.setVisibility(View.GONE);

        } else {
            updateCart();
            callMyAssetApi();


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
     * Method called to calculate notification count
     ***********************************************************/
    private void notificationCount() {
        try {
            if (!mSession.getNotification().isEmpty()) {
                JSONArray jsonArray = new JSONArray(mSession.getNotification());
                tv_notification_badge.setVisibility(View.VISIBLE);
                tv_notification_badge_a.setVisibility(View.VISIBLE);
                tv_notification_badge.setText("" + jsonArray.length());
                tv_notification_badge_a.setText("" + jsonArray.length());
            } else {
                tv_notification_badge.setVisibility(View.GONE);
                tv_notification_badge_a.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**********************************************************
     BroadCastReceiver for get notification Check
     ***********************************************************/

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getStringExtra("action");
            if (action.isEmpty()) {

                tv_notification_badge.setVisibility(View.INVISIBLE);
                tv_notification_badge_a.setVisibility(View.INVISIBLE);

            } else {
                tv_notification_badge.setVisibility(View.VISIBLE);
                tv_notification_badge_a.setVisibility(View.VISIBLE);
            }
        }
    };

    /****************************************************
     * Method called when cart update operation is going
     ****************************************************/
    @SuppressLint("SetTextI18n")
    public void updateCart() {
        try {
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                tvCartBadge.setVisibility(View.GONE);
                tvCartBadgeA.setVisibility(View.GONE);

            } else {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadgeA.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                tvCartBadge.setText("" + jsonArray.length());
                tvCartBadgeA.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*************************************API Callings*********************************************/
    private void callMyAssetApi() {

        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        String url = Config.MY_ASSETS_API;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mSession.getCID());


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    llDashboardFooter.setVisibility(View.GONE);
                    cv_dashboard_noData.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")) {
                        ll_consortium_header_container.setVisibility(View.VISIBLE);

                    } else {
                        ll_dashboard_card_default.setVisibility(View.GONE);
                        ll_dashboard_client_data_container.setVisibility(View.VISIBLE);
                    }


                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();
                        DecimalFormat myFormatter = new DecimalFormat("##,##,###.##");
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("AllAssetAUMDetail");
                            total = 0;
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);

                                if (!object.optString("AUM").isEmpty() && !object.optString("Product").contains("Insurance")) {

                                    total += Integer.parseInt(object.optString("AUM").replace(",", ""));
                                }

                            }

                            tvMyAssetValue.setText(String.valueOf(myFormatter.format(total)));
                            tvMyAssetConsortium.setText(String.valueOf(myFormatter.format(total)));
                        } else {
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            cv_dashboard_noData.setVisibility(View.VISIBLE);
                            llDashboardFooter.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")) {
                                ll_consortium_header_container.setVisibility(View.GONE);
                            } else {
                                ll_dashboard_card_default.setVisibility(View.GONE);

                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    cv_dashboard_noData.setVisibility(View.VISIBLE);
                    llDashboardFooter.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")) {
                        ll_consortium_header_container.setVisibility(View.GONE);
                    } else {
                        ll_dashboard_card_default.setVisibility(View.GONE);

                    }
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            mApplication.showSnackBar(ll_dashboard_card_default, jsonObject.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        mApplication.showSnackBar(ll_dashboard_card_default, getResources().getString(R.string.no_internet));

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
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivityTypeTwo.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*******************************************
     * Method contains data for investment route items
     *******************************************/
    private void prepareInvestRouteData() {
        int[] routeIcons = new int[]{
                R.mipmap.ic_invest_route_goal,
                R.mipmap.ic_invest_route_expertise,
                R.mipmap.ic_invest_route_fund_pick,
                R.mipmap.ic_invest_route_top_performer,
                R.mipmap.ic_invest_route_amc,
                R.mipmap.ic_invest_nfo,
                R.mipmap.ic_invest_route_flavour,
                R.mipmap.ic_invest_route_just_save,
                R.mipmap.ic_invest_route_simply_save,
                R.mipmap.ic_invest_route_service_req,
                R.mipmap.ic_invest_route_track_old,
                R.mipmap.ic_invest_route_transfer_holding
        };
        InvestRouteTypeTwo a;
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("GoalModuleV2")) &&
                Utils.getConfigData(mSession).optString("GoalModuleV2").equalsIgnoreCase("Y")) {
            a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_goal_tv_txt) + " " +
                    getResources().getString(R.string.dashboard_goal_header_txt),

                    getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[0]);
            investmentRoutesList.add(a);
        }
        a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_believe_tv_txt) + " " +
                getResources().getString(R.string.dashboard_believe_header_txt),

                getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[1]);
        investmentRoutesList.add(a);

        a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_fund_tv_txt) + " " +
                getResources().getString(R.string.dashboard_fund_header_txt),

                getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[2]);
        investmentRoutesList.add(a);

        a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_top_perfromer_tv_txt) + " " +
                getResources().getString(R.string.dashboard_top_performer_header_txt),

                getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[3]);
        investmentRoutesList.add(a);

        a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_amc_tv_txt) + " " +
                getResources().getString(R.string.dashboard_amc_header_txt),

                getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[4]);
        investmentRoutesList.add(a);

        a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_nfo_tv_txt) + " " +
                getResources().getString(R.string.dashboard_nfo_header_txt),

                getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[5]);
        investmentRoutesList.add(a);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("FlavourOfMonth")) &&
                Utils.getConfigData(mSession).optString("FlavourOfMonth").equalsIgnoreCase("Y")) {
            a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_flavour_of_month_txt) + " " +
                    getResources().getString(R.string.dashboard_flavour_of_month_header_txt),

                    getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[6]);
            investmentRoutesList.add(a);
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("JustSaveReq")) &&
                Utils.getConfigData(mSession).optString("JustSaveReq").equalsIgnoreCase("Y")) {
            a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_just_save_tv_txt) + " " +
                    getResources().getString(R.string.dashboard_just_save_header_txt),

                    getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[7]);
            investmentRoutesList.add(a);
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("SimplySave")) &&
                Utils.getConfigData(mSession).optString("SimplySave").equalsIgnoreCase("Y")) {
            a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_simply_save_tv_txt) + " " +
                    getResources().getString(R.string.dashboard_simply_save_header_txt),

                    getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[8]);
            investmentRoutesList.add(a);
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("ServiceRequest")) &&
                Utils.getConfigData(mSession).optString("ServiceRequest").equalsIgnoreCase("Y")) {
            a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_service_request_txt) + " " +
                    getResources().getString(R.string.dashboard_service_req_header_txt),

                    getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[9]);
            investmentRoutesList.add(a);
        }
        a = new InvestRouteTypeTwo(getResources().getString(R.string.dashboard_track_txt) + " " +
                getResources().getString(R.string.dashboard_track_old_invest_header_txt),

                getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[10]);
        investmentRoutesList.add(a);
        a = new InvestRouteTypeTwo(getResources().getString(R.string.toolBar_title_transfer_holding) + " " +
                getResources().getString(R.string.dashboard_track_old_invest_header_txt),

                getResources().getString(R.string.dashboard_goal_desc_txt), routeIcons[11]);
        investmentRoutesList.add(a);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_dashboard_type_two_my_assets) {
            onLoginBtnClick();
        } else if (id == R.id.ll_dashboard_client_data_container) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("comingFromActivity", "MyAssetType2B");
                startActivity(intent);
            } else {
                intent = new Intent(MainActivityTypeTwo.this, DashboardMyAssetsActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.ll_consortium_header_container) {
            intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
            intent.putExtra("comingFromActivity", "MyAssetType2B");
            startActivity(intent);
        } else if (id == R.id.btn_dashboard_type_two_login) {
            onLoginBtnClick();
        } else if (id == R.id.btn_dashboard_type_two_enquiry) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A")))) {
                onSignUpBtnClick();

            } else {
                onEnquiryBtnClick();
            }
        } else if (id == R.id.fl_cart || id == R.id.fl_cart_a) {
            onCartIconClick();
        } else if (id == R.id.fl_notifications || id == R.id.fl_notifications_a) {
            intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
            intent.putExtra("Flavour", "TYPE 2");
            intent.putExtra("position", "85");
            startActivity(intent);
        } else if (id == R.id.tvViewAll) {
            if (mDucSwipeAdapter.mDataListRycleView.size() > 0) {
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "100");
                startActivity(intent);
            } else {
                mApplication.showSnackBar(rvEquityItems, "No documents");
            }
        } else if (id == R.id.ivContactUs || id == R.id.ivContactUs_a) {
            if (mSession.getHasLoging() && (mSession.getLoginType().equals("Broker")
                    || mSession.getLoginType().equals("SubBroker")
                    || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                    || mSession.getLoginType().equalsIgnoreCase("Region")
                    || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
                finish();
            } else {

                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "57");
                startActivity(intent);
            }
        } else if (id == R.id.iv_dashboard_cross_btn) {
            llDashboardFooter.setVisibility(View.GONE);
        }
    }

    /*************************************************
     * Method called when user clicks on cart icon
     ***********************************************************/
    private void onCartIconClick() {
        try {
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {

                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "39");
                startActivity(intent);
            } else {

                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "4");
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLoginBtnClick() {

        Intent intent = new Intent(MainActivityTypeTwo.this, LoginActivity.class);
        intent.putExtra(AppConstants.LOGIN_COME_FROM, "Dashboard");
        startActivity(intent);

    }

    private void onSignUpBtnClick() {
        Intent intent = new Intent(MainActivityTypeTwo.this, SignUpActivity.class);
        intent.putExtra(AppConstants.LOGIN_COME_FROM, "Dashboard");
        startActivity(intent);

    }

    private void onEnquiryBtnClick() {
        intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
        intent.putExtra("Flavour", "TYPE 2");
        intent.putExtra("position", "99");
        startActivity(intent);
    }

    @Override
    public void onRoutesClick(int position) {
        switch (position) {
            case 0:
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "70");
                startActivity(intent);
                break;
            case 1:

                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "1");
                startActivity(intent);
                break;
            case 2:

                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "49");
                startActivity(intent);
                break;
            case 3:

                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "41");
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "66");
                startActivity(intent);
                break;

            case 5:
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "90");
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "74");
                startActivity(intent);
                break;
            case 7:
                if (mSession.getSave().isEmpty()) {
                    intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                    intent.putExtra("Flavour", "TYPE 2");
                    intent.putExtra("position", "96");
                    startActivity(intent);
                } else {

                    intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                    intent.putExtra("Flavour", "TYPE 2");
                    intent.putExtra("position", "97");
                    startActivity(intent);
                }
                break;
            case 8:
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "79");
                startActivity(intent);
                break;
            case 9:
                Intent i = new Intent(MainActivityTypeTwo.this, ServiceRequest.class);
                startActivity(i);
                break;
            case 10:
                intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "101");
                startActivity(intent);
                break;
            case 11:
                if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                    /*showDialog();*/
                } else {
                    intent = new Intent(MainActivityTypeTwo.this, MainActivity.class);
                    intent.putExtra("Flavour", "TYPE 2");
                    intent.putExtra("position", "52");
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onEquityItemClick(int position) {

    }

    @Override
    public void onOtherServicesClick(int position) {

    }


}

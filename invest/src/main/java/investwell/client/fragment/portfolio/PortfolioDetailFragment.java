package investwell.client.fragment.portfolio;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import investwell.broker.fragment.FragHomeBroker;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.IndividualPortfolioAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.MyViewPagerAdapter;


public class PortfolioDetailFragment extends Fragment implements View.OnClickListener {
    Bundle bundle;
    TextView applicant_name, purchase_cost, market_value, gain, cagr, tvfilterName;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    String[] ApplicantName, Gain, CAGR, cid, grpLeader;
    RecyclerView sub_client_recycle;
    private String mCID = "";
    ImageView gain_arrow, cagr_arrow;
    private JSONObject jsonObject;
    private View viewTopPortfolio, viewFilterPortfolio;
    AdapterPortfolioDetail portfolio_detail_adapter;
    private IndividualPortfolioAdapter individualPortfolioAdapter;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private ToolbarFragment fragToolBar;
    private View view;
    private ShimmerFrameLayout mShimmerViewContainer;
    private boolean isOpen = false;
    private Button btnNameAsc, btnCatRaise;
    private Button[] btn = new Button[12];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btn_cat_raise, R.id.btn_cat_fall, R.id.btn_asc_name, R.id.btn_desc_name, R.id.btn_cv_filter_growth, R.id.btn_cv_filter_declined,
            R.id.btn_invest_raise, R.id.btn_invest_fall, R.id.btn_gain_raise, R.id.btn_gain_fall, R.id.btn_cagr_raise, R.id.btn_cagr_fall
    };
    private FloatingActionButton fab, fabApplyFilter;
    private String sortBy = "catAsc";
    ArrayList<JSONObject> list;
    boolean flag = true;
    private String[] mTabTittle = {"Equity", "Debt", "Hybrid"};
    private View vContentSchemeCategoryContainer;
    private TabLayout mTabList;
    private ViewPager mViewPager;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private JSONArray sectionJSONArray;
    private JSONArray mJsonArray;
    private ArrayList<JSONObject> sectionJSONObjectList;
    private JSONObject mObject;
    private MyViewPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_portfolio__detail, container, false);
        setUpToolBar();
        setInitializer();
        errorContentInitializer(view);
        getDataFromBundle();
        Portfolio_Data();
        setRecyclerAdapter();
        setListener();
        return view;
    }

    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }



    @Override
    public void onResume() {
        super.onResume();


    }

    private void setListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFilterPortfolio();


            }
        });
        fabApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFilterPortfolio();
                runLayoutAnimation(sub_client_recycle);
            }
        });

        for (int i = 0; i < btn.length; i++) {
            btn[i] = viewFilterPortfolio.findViewById(btn_id[i]);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                btn[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
            } else {
                btn[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_secondary));

            }
            btn[i].setOnClickListener(this);
        }
        btn_unfocus = btn[0];

    }

    private void getDataFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else {
            mCID = mSession.getCID();
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        }
        recyclerView.scheduleLayoutAnimation();
    }

    private void viewFilterPortfolio() {

        if (!isOpen) {

            int x = viewFilterPortfolio.getRight();
            int y = viewFilterPortfolio.getBottom();

            int startRadius = 0;
            int endRadius = (int) Math.hypot(viewFilterPortfolio.getWidth(), viewFilterPortfolio.getHeight());

            Animator anim = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(viewFilterPortfolio, x, y, startRadius, endRadius);
            }

            viewFilterPortfolio.setVisibility(View.VISIBLE);
            if (anim != null) {
                anim.start();
            }

            isOpen = true;

        } else {

            int x = viewFilterPortfolio.getRight();
            int y = viewFilterPortfolio.getBottom();

            int startRadius = Math.max(viewFilterPortfolio.getWidth(), viewFilterPortfolio.getHeight());
            int endRadius = 0;


            Animator anim = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(viewFilterPortfolio, x, y, startRadius, endRadius);
            }
            if (anim != null) {
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        viewFilterPortfolio.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
            if (anim != null) {
                anim.start();
            }

            isOpen = false;
        }
    }

    private void setInitializer() {
        bundle = getArguments();
        sectionJSONArray = new JSONArray();
        list = new ArrayList<>();
        sectionJSONObjectList = new ArrayList<>();
        applicant_name = view.findViewById(R.id.applicant_name);
        viewTopPortfolio = view.findViewById(R.id.content_top_layout_portfolio);
        viewTopPortfolio.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.bg_portfolio_top_card));
        viewFilterPortfolio = view.findViewById(R.id.content_filter_portfolio);
        vContentSchemeCategoryContainer = view.findViewById(R.id.content_portfolio_detail_list);
        mTabList = vContentSchemeCategoryContainer.findViewById(R.id.tab_portfolio_detail);
        mViewPager = vContentSchemeCategoryContainer.findViewById(R.id.vp_portfolio_detail);
        tvfilterName = viewFilterPortfolio.findViewById(R.id.tv_filter_name);
        tvfilterName.setText("Scheme Name");
        fabApplyFilter = viewFilterPortfolio.findViewById(R.id.fab_apply_filter);
        btnNameAsc = viewFilterPortfolio.findViewById(R.id.btn_asc_name);
        btnCatRaise = viewFilterPortfolio.findViewById(R.id.btn_cat_raise);
        purchase_cost = viewTopPortfolio.findViewById(R.id.purchase_cost);
        market_value = viewTopPortfolio.findViewById(R.id.tv_market_value);
        fab = view.findViewById(R.id.fab_filter_portfolio);
        gain = viewTopPortfolio.findViewById(R.id.gain);
        cagr = viewTopPortfolio.findViewById(R.id.cagr);
        gain_arrow = viewTopPortfolio.findViewById(R.id.gain_arrow);
        cagr_arrow = viewTopPortfolio.findViewById(R.id.cagr_arrow);
        sub_client_recycle = view.findViewById(R.id.sub_client_recycle);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);

    }

    private void setUpToolBar() {
        if (
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B") &&
                        !(mSession.getLoginType().equalsIgnoreCase("ClientG") || FragHomeBroker.comming_from.equalsIgnoreCase("Broker_group"))
        ) {
            view.findViewById(R.id.frag_toolBar).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.frag_toolBar).setVisibility(View.VISIBLE);
        }

        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_portfolio_details), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity, R.color.colorPrimary));

            }


        }
    }

    private void setRecyclerAdapter() {
        sub_client_recycle.setHasFixedSize(true);
        sub_client_recycle.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        portfolio_detail_adapter = new AdapterPortfolioDetail(mActivity, new ArrayList<JSONObject>(), mCID);
        individualPortfolioAdapter = new IndividualPortfolioAdapter(mActivity, new ArrayList<JSONObject>(), mCID);
        sub_client_recycle.setNestedScrollingEnabled(false);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
            sub_client_recycle.setAdapter(individualPortfolioAdapter);
        } else {
            sub_client_recycle.setAdapter(portfolio_detail_adapter);
        }


    }

    public void callPortfolioCatWiseApi() {
        String url = Config.PORT_CAT_WISE;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put("OnlineOption",mSession.getAppType());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    String Status = response.optString("Status");
                    if (Status.equalsIgnoreCase("True")) {
                        sectionJSONArray = response.optJSONArray("ResponseData");
                        String portfolioDetailData = sectionJSONArray.toString();
                        setUpCategoryWiseData(portfolioDetailData);
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

    private void setUpCategoryWiseData(String portfolioDetail) {
        List<Fragment> fragList = new ArrayList<>();
        try {
            mJsonArray = new JSONArray(portfolioDetail);
            if (mJsonArray.length() > 0) {

                for (int i = 0; i < mJsonArray.length(); i++) {
                    mObject = mJsonArray.getJSONObject(i);
                    Bundle bundle = new Bundle();
                    bundle.putString("portfolioDetailData", String.valueOf(mObject));
                    bundle.putString("cid",mCID);
                    bundle.putString("ApplicantName",applicant_name.getText().toString());
                    Fragment fragment = new PortfolioDetailCategoryWise();
                    fragment.setArguments(bundle);
                    fragList.add(fragment);
                }
                if (!isAdded()) return;

                mAdapter = new MyViewPagerAdapter(getChildFragmentManager(), fragList);
                mViewPager.setAdapter(mAdapter);
                mTabList.setupWithViewPager(mViewPager);
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject object = mJsonArray.getJSONObject(i);
                    mTabList.getTabAt(i).setText(object.optString("Category"));

                }
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                viewTopPortfolio.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void Portfolio_Detail_Data() {


        String url = Config.Portfolio_Client_Detailed;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    AppApplication.portfolio_detail_data = response.toString();


                    setData();

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

    public void Portfolio_Data() {

        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

        String url = Config.Portfolio_Return;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    AppApplication.porfolio_detail_data2 = response.toString();
                    setPortfolio_Data();

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

    private void setPortfolio_Data() {


        try {
            JSONObject jsonObject = new JSONObject(AppApplication.porfolio_detail_data2);

            String Status = jsonObject.optString("Status");
            if (Status.equalsIgnoreCase("True")) {
                JSONArray jsonArray = jsonObject.optJSONArray("PortfolioReturnDetail");
                ApplicantName = new String[jsonArray.length()];
                String[] initialval = new String[jsonArray.length()];
                String[] currentval = new String[jsonArray.length()];
                Gain = new String[jsonArray.length()];
                CAGR = new String[jsonArray.length()];
                cid = new String[jsonArray.length()];
                grpLeader = new String[jsonArray.length()];


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    ApplicantName[i] = jsonObject1.getString("ApplicantName");
                    initialval[i] = jsonObject1.getString("InitialVal");
                    currentval[i] = jsonObject1.getString("CurrentVal");
                    Gain[i] = jsonObject1.getString("Gain");
                    CAGR[i] = jsonObject1.getString("CAGR");
                    cid[i] = jsonObject1.getString(AppConstants.CUSTOMER_ID);
                    grpLeader[i] = jsonObject1.getString("GroupLeader");
                    currentval[i] = jsonObject1.getString("CurrentVal");
                    if (cid[i].equalsIgnoreCase(mCID)) {
                        applicant_name.setText(ApplicantName[i]);
                        mApplication.applicantName = ApplicantName[i];
                        purchase_cost.setText(getString(R.string.Rs) + initialval[i]);
                        market_value.setText(getString(R.string.Rs) + currentval[i]);
                        gain.setText(getString(R.string.Rs) + Gain[i]);
                        cagr.setText(CAGR[i] + "%");

                    }
                }


                if (gain.getText().toString().contains("-")) {
                    gain_arrow.setBackgroundResource(R.drawable.menu_down);
                } else {
                    gain_arrow.setBackgroundResource(R.drawable.menu_up);
                }
                if (cagr.getText().toString().contains("-")) {
                    cagr_arrow.setBackgroundResource(R.drawable.menu_down);
                } else {
                    cagr_arrow.setBackgroundResource(R.drawable.menu_up);
                }


            } else {

                String ServiceMSG = jsonObject.optString("ServiceMSG");
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                mApplication.showSnackBar(mShimmerViewContainer, ServiceMSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

     /*   if (AppApplication.portfolio_detail_data.isEmpty()) {

            Portfolio_Detail_Data();
        } else {
            setData();
        }*/
        callPortfolioCatWiseApi();
    }


    private void setData() {


        try {

            jsonObject = new JSONObject(AppApplication.portfolio_detail_data);
            String Status = jsonObject.optString("Status");
            if (Status.equalsIgnoreCase("True")) {
                JSONArray jsonArray = jsonObject.optJSONArray("MFTransactionDetail");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    list.add(jsonObject1);

                }
                mApplication.setPortfolioSchemeList(list);
               /* Collections.sort(list, new Comparator<JSONObject>() {

                    public int compare(JSONObject a, JSONObject b) {
                        String valA = new String();
                        String valB = new String();

                        try {
                            valA = (String) a.get("Objective");
                            valB = (String) b.get("Objective");
                        } catch (JSONException e) {
//do something
                        }

                        return valA.compareTo(valB);
                    }
                });*/
                portfolio_detail_adapter.updateList(list, applicant_name.getText().toString());
                individualPortfolioAdapter.updateList(list, applicant_name.getText().toString());

            } else {
                String ServiceMSG = jsonObject.optString("ServiceMSG");
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                mApplication.showSnackBar(sub_client_recycle, ServiceMSG);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }


    private void setSortData(String sortBy) {
        if (sortBy.equalsIgnoreCase("nameAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get("SchemeName");
                        valB = (String) b.get("SchemeName");
                    } catch (JSONException e) {
//do something
                    }

                    return valA.compareTo(valB);
                }
            });
        } else if (sortBy.equalsIgnoreCase("nameDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get("SchemeName");
                        valB = (String) b.get("SchemeName");
                    } catch (JSONException e) {
//do something
                    }

                    return valB.compareTo(valA);
                }
            });
        } else if (sortBy.equalsIgnoreCase("catAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get("Objective");
                        valB = (String) b.get("Objective");
                    } catch (JSONException e) {
//do something
                    }

                    return valA.compareTo(valB);
                }
            });
        } else if (sortBy.equalsIgnoreCase("catDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get("Objective");
                        valB = (String) b.get("Objective");
                    } catch (JSONException e) {
//do something
                    }

                    return valB.compareTo(valA);
                }
            });
        } else if (sortBy.equalsIgnoreCase("cvAsc")) {


            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("CurrentValue").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("CurrentValue").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valB.compareTo(valA);


                }

            });
        } else if (sortBy.equalsIgnoreCase("cvDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {
                String s = "";

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("CurrentValue").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("CurrentValue").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valA.compareTo(valB);


                }

            });
        } else if (sortBy.equalsIgnoreCase("investAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("InitialValue").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("InitialValue").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valB.compareTo(valA);

                }

            });
        } else if (sortBy.equalsIgnoreCase("investDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("InitialValue").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("InitialValue").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valA.compareTo(valB);


                }

            });

        } else if (sortBy.equalsIgnoreCase("gainAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("Gain").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("Gain").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valB.compareTo(valA);

                }

            });
        } else if (sortBy.equalsIgnoreCase("gainDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("Gain").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("Gain").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valA.compareTo(valB);


                }

            });

        } else if (sortBy.equalsIgnoreCase("cagrAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Double valA = 0.0;
                    Double valB = 0.0;

                    try {
                        valA = Double.parseDouble(a.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));
                        valB = Double.parseDouble(b.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return Double.compare(valB, valA);

                }

            });
        } else if (sortBy.equalsIgnoreCase("cagrDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Double valA = 0.0;
                    Double valB = 0.0;

                    try {
                        valA = Double.parseDouble(a.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));
                        valB = Double.parseDouble(b.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return Double.compare(valA, valB);

                }

            });

        }
        mApplication.setPortfolioSchemeList(list);
        portfolio_detail_adapter.updateList(list, applicant_name.getText().toString());
        individualPortfolioAdapter.updateList(list, applicant_name.getText().toString());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_asc_name) {
            sortBy = "nameAsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[2]);
        } else if (id == R.id.btn_desc_name) {
            sortBy = "nameDsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[3]);
        } else if (id == R.id.btn_cv_filter_growth) {
            sortBy = "cvAsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[4]);
        } else if (id == R.id.btn_cv_filter_declined) {
            sortBy = "cvDsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[5]);
        } else if (id == R.id.btn_invest_raise) {
            sortBy = "investAsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[6]);
        } else if (id == R.id.btn_invest_fall) {
            sortBy = "investDsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[7]);
        } else if (id == R.id.btn_gain_raise) {
            sortBy = "gainAsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[8]);
        } else if (id == R.id.btn_gain_fall) {
            sortBy = "gainDsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[9]);
        } else if (id == R.id.btn_cagr_raise) {
            sortBy = "cagrAsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[10]);
        } else if (id == R.id.btn_cagr_fall) {
            sortBy = "cagrDsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[11]);
        } else if (id == R.id.btn_cat_fall) {
            sortBy = "catDsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[1]);
        } else if (id == R.id.btn_cat_raise) {
            sortBy = "catAsc";
            setSortData(sortBy);
            setFocus(btn_unfocus, btn[0]);
        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setTextColor(getResources().getColor(R.color.darkSecondaryBtnTextColor));
        } else {
            btn_unfocus.setTextColor(getResources().getColor(R.color.lightSecondaryBtnTextColor));
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));
        } else {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_bg_secondary));
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


}
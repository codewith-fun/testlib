package investwell.broker.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FinancialToolAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.model.FinancialTools;


public class FragHomeBroker extends Fragment implements FinancialToolAdapter.FinancialToolListener, View.OnClickListener {

    public static String comming_from = "";
    private AppSession mSession;
    private String cid = "";
    private BrokerActivity mBrokerActivity;
    private AppApplication mApplication;
    private TextView tvAUM, tvInvestor, tvSip, tvSipPerMonth, mTvUserName,mTvChnageinSip,mTvChnageinClient;
    private String mSearchType = "Group";
    private FinancialToolAdapter financialToolAdapter;
    private List<FinancialTools> financialToolsList;
    private RecyclerView rvFinancialTools, rvFinancialTools1a;
    private View view;

    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout mLinerDashboardContainer, llFinancialToolsOneA;
    private CardView cvFlavour, mCvFinancialTools;
    private MainActivity mainActivity;
    private LinearLayout mllDetail,mllMain;
    private ImageView mIvArrow;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());

        } else if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mainActivity);
            mApplication = (AppApplication) mainActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home_broker, container, false);

        setInitializer();
        getDataFromBundle();
        setUpUiVisibility(view);
        setListeners();
        setUpAppUi();
        return view;
    }

    private void getDataFromBundle() {
        if (mSession.getLoginType().equalsIgnoreCase("Broker")) {
            cid = "NA";
        } else {
            if (mSession.getSecondryCID().length() > 0)
                cid = mSession.getSecondryCID();
            else
                cid = mSession.getCID();
        }
        setFinancialToolAdapter();
        if (mApplication.sOBJ_DASHBOARD_BROKER != null) {
            BrokerDashboard();
        } else {
            setSummary();
        }
    }

    /*******************************************
     * Method used to setup UI as per the app layout_gridview_type_two_a type
     *******************************************/
    private void setUpAppUi() {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {

            llFinancialToolsOneA.setVisibility(View.VISIBLE);
            mCvFinancialTools.setVisibility(View.GONE);

        } else {

            llFinancialToolsOneA.setVisibility(View.GONE);


            mCvFinancialTools.setVisibility(View.VISIBLE);

        }

    }

    private void setInitializer() {
        mBrokerActivity.setMainVisibility(this, null);
        mllMain = view.findViewById(R.id.llMain);
        mIvArrow = view.findViewById(R.id.ivArrow);
        mllDetail = view.findViewById(R.id.ll_Detail);
        tvAUM = view.findViewById(R.id.tv_market_value);
        tvInvestor = view.findViewById(R.id.purchase_cost);
        tvSipPerMonth = view.findViewById(R.id.days_change);
        tvSip = view.findViewById(R.id.gain);
        mTvUserName = view.findViewById(R.id.tvName);
        rvFinancialTools = view.findViewById(R.id.rv_financial_tools);
        financialToolsList = new ArrayList<>();
        rvFinancialTools = view.findViewById(R.id.rv_financial_tools);
        cvFlavour = view.findViewById(R.id.cv_flavour);
        llFinancialToolsOneA = view.findViewById(R.id.ll_financial_tools_1a);
        rvFinancialTools1a = view.findViewById(R.id.rv_financial_tools_1a);
        mCvFinancialTools = view.findViewById(R.id.cv_financial_tools);
        mLinerDashboardContainer = view.findViewById(R.id.ll_dashboard_container);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mTvChnageinSip = view.findViewById(R.id.tvChnageinSip);
        mTvChnageinClient = view.findViewById(R.id.tvChangeinClient);
        mIvArrow.setOnClickListener(this);




    }

    private void setUpUiVisibility() {

    }

    private void setListeners() {
        cvFlavour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainActivity != null) {
                    mainActivity.displayViewOther(73, null);
                } else {
                    mBrokerActivity.displayViewOther(73, null);
                }

            }
        });
    }

    private void setUpUiVisibility(View view) {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CalculatorRequired")) &&
                Utils.getConfigData(mSession).optString("CalculatorRequired").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.rl_financial_tools_container).setVisibility(View.VISIBLE);

        } else {
            view.findViewById(R.id.rl_financial_tools_container).setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("FlavourOfMonth")) &&
                Utils.getConfigData(mSession).optString("FlavourOfMonth").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.cv_flavour).setVisibility(View.VISIBLE);

        } else {
            view.findViewById(R.id.cv_flavour).setVisibility(View.GONE);

        }
    }

    private void setSummary() {
        JSONObject jsonObject = mApplication.sOBJ_DASHBOARD_BROKER;
        String Status = jsonObject.optString("Status");
        if (Status.equalsIgnoreCase("True")) {
            JSONArray jsonArray = jsonObject.optJSONArray("BrokerDashBoardDetail");
            JSONObject jsonObject1 = jsonArray.optJSONObject(0);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(0);

            double currentValue = jsonObject1.optDouble("AUM");
            String strCurrentAmount = format.format(currentValue);
            tvAUM.setText(strCurrentAmount);

            double amountOfSIPPerMonth = jsonObject1.optDouble("TotalSIPAMT");
            String strCamountOfSIPPerMonth = format.format(amountOfSIPPerMonth);
            tvSipPerMonth.setText(strCamountOfSIPPerMonth);

            tvInvestor.setText(jsonObject1.optString("TotalClient"));
            tvSip.setText(jsonObject1.optString("TotalSIP"));
            mTvChnageinSip.setText(jsonObject1.optString("ChangeinSIP"));
            mTvChnageinClient.setText(jsonObject1.optString("ChangeinClient"));
            mTvUserName.setText(mSession.getBrokerFullName());


        } else {
            mApplication.showCommonDailog(mBrokerActivity, mBrokerActivity, false, getResources().getString(R.string.Server_Error), jsonObject.optString("ServiceMSG"), "message", false, true);
        }
    }


    public void BrokerDashboard() {
        mLinerDashboardContainer.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.Broker_Dashboard;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put("LoginCategory", mSession.getLoginType());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    mApplication.sOBJ_DASHBOARD_BROKER = response;
                    setSummary();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mLinerDashboardContainer.setVisibility(View.VISIBLE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        mApplication.showCommonDailog(mBrokerActivity, mBrokerActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mBrokerActivity, mBrokerActivity, false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
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

            RequestQueue requestQueue = Volley.newRequestQueue(mBrokerActivity);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*******************************************
     * Method used to set dynamic financial items
     *******************************************/

    private void setFinancialToolAdapter() {

        financialToolAdapter = new FinancialToolAdapter(getActivity(), financialToolsList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mBrokerActivity, 3);
        rvFinancialTools.setLayoutManager(gridLayoutManager);
        rvFinancialTools.addItemDecoration(new DividerItemDecoration(mBrokerActivity,
                DividerItemDecoration.HORIZONTAL));
        rvFinancialTools.addItemDecoration(new DividerItemDecoration(mBrokerActivity,
                DividerItemDecoration.VERTICAL));
        rvFinancialTools.setItemAnimator(new DefaultItemAnimator());
        rvFinancialTools.setNestedScrollingEnabled(false);
        rvFinancialTools.setAdapter(financialToolAdapter);
        GridLayoutManager grid = new GridLayoutManager(mBrokerActivity, 3);
        rvFinancialTools1a.setLayoutManager(grid);
        rvFinancialTools1a.addItemDecoration(new DividerItemDecoration(mBrokerActivity,
                DividerItemDecoration.HORIZONTAL));
        rvFinancialTools1a.addItemDecoration(new DividerItemDecoration(mBrokerActivity,
                DividerItemDecoration.VERTICAL));
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

    @Override
    public void onToolsClick(int position) {
        if (mBrokerActivity != null) {
            switch (position) {
                case 0:
                    mBrokerActivity.displayFinancialCalculators(10, null);
                    // mBrokerActivity.displayViewOther(6,null);
                    break;
                case 1:
                    mBrokerActivity.displayFinancialCalculators(3, null);

                    break;
                case 2:
                    mBrokerActivity.displayFinancialCalculators(5, null);
                    break;
                case 3:
                    mBrokerActivity.displayFinancialCalculators(7, null);
                    break;
                case 4:
                    mBrokerActivity.displayFinancialCalculators(8, null);
                    break;
                case 5:
                    mBrokerActivity.displayFinancialCalculators(6, null);
                    ///   mApplication.showSnackBar(view, getResources().getString(R.string.work_under_development));
                    break;


            }
        } else {
            switch (position) {
                case 0:
                    mainActivity.displayFinancialCalculators(10, null);
                    break;
                case 1:
                    mainActivity.displayFinancialCalculators(3, null);

                    break;
                case 2:
                    mainActivity.displayFinancialCalculators(5, null);
                    break;
                case 3:
                    mainActivity.displayFinancialCalculators(7, null);
                    break;
                case 4:
                    mainActivity.displayFinancialCalculators(8, null);
                    break;
                case 5:
                    mainActivity.displayFinancialCalculators(6, null);
                    mApplication.showSnackBar(view, getResources().getString(R.string.work_under_development));
                    break;


            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.ivArrow) {
            if (mllDetail.getVisibility() == View.VISIBLE) {
                mIvArrow.setBackgroundResource(R.drawable.down_arrow);
                mllDetail.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                    mllMain.setBackgroundResource(R.color.darkDialogBackground);

                } else {
                    mllMain.setBackgroundResource(R.color.colorWhite);

                }

            } else {
                mIvArrow.setBackgroundResource(R.drawable.up_arrow);
                mllDetail.setVisibility(View.VISIBLE);
                mllMain.setBackgroundResource(R.mipmap.blank_card);
            }
        }


    }
}

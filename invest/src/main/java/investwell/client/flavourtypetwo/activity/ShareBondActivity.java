package investwell.client.flavourtypetwo.activity;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.flavourtypetwo.adapter.ShareBondAdapter;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class ShareBondActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rvShareBond;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private Bundle bundle;
    private String mCid;
    private ShareBondAdapter shareNBondAdapter;
    private AppApplication mApplication;
    private ImageView ivToolBarBackIcon;
    private ShimmerFrameLayout shimmerFrameLayout;
    private CardView cv_dashboard_noData;
    private TextView mtvMarketValue, mtvPurchaseCost, mtvAbsoluteReturn, mtvWeightedDays, mtvDayChnage, mtvGain, mtvCagr;
    private LinearLayout llShareBond;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bond);
        initializer();
        getDataFromBundle();
        setShareBondAdapter();
        callShareBondPortfolioApi();
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
        mApplication = (AppApplication) this.getApplication();
        mSession = AppSession.getInstance(ShareBondActivity.this);
        bundle = getIntent().getExtras();
        ivToolBarBackIcon = findViewById(R.id.iv_back_my_assets);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        cv_dashboard_noData = findViewById(R.id.cv_dashboard_noData);
        llShareBond = findViewById(R.id.ll_share_bond_container);
        llShareBond.setVisibility(View.GONE);
        rvShareBond = findViewById(R.id.rv_share_bond);
        mtvMarketValue = findViewById(R.id.tv_market_value);
        mtvPurchaseCost = findViewById(R.id.tv_purchase_cost_value);
        mtvDayChnage = findViewById(R.id.tv_days_change_value);
        mtvAbsoluteReturn = findViewById(R.id.tv_absolute_return_value);
        mtvGain = findViewById(R.id.tv_gain_value);
        mtvCagr = findViewById(R.id.tv_cagr_value);
    }

    private void setShareBondAdapter() {
        rvShareBond.setHasFixedSize(true);
        rvShareBond.setLayoutManager(new LinearLayoutManager(ShareBondActivity.this, LinearLayoutManager.VERTICAL, false));
        shareNBondAdapter = new ShareBondAdapter(ShareBondActivity.this, new ArrayList<JSONObject>());
        rvShareBond.setAdapter(shareNBondAdapter);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("cid")) {

            mCid = bundle.getString("cid");
        } else {
            mCid = mSession.getCID();
        }
    }

    private void setListeners() {
        ivToolBarBackIcon.setOnClickListener(this);
    }

    /********************************************API Calling**************************************/
    private void callShareBondPortfolioApi() {
        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        String url = Config.Share_Bond;
        final JSONObject jsonObject = new JSONObject();
        final ArrayList<JSONObject> list = new ArrayList<>();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCid);
            jsonObject.put("FormatReq", "Y");
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        cv_dashboard_noData.setVisibility(View.GONE);
                        llShareBond.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = response.optJSONArray("EquityInvestmentsDetail");
                        JSONObject Obj = jsonArray.optJSONObject(0);
                        mtvMarketValue.setText(Obj.optString("TotalCurrentValue"));
                        mtvPurchaseCost.setText(Obj.optString("TotalInitialValue"));
                        mtvAbsoluteReturn.setText(Obj.optString("TotalAbsoluteReturn"));
                        /*mtvWeightedDays.setText(Obj.optString("TotalWeightedDays"));*/
                        mtvDayChnage.setText(Obj.optString("TotalOneDayChange"));
                        mtvGain.setText(Obj.optString("TotalGain"));
                        mtvCagr.setText(Obj.optString("TotalCAGR"));

                        JSONArray jsonArray1 = Obj.optJSONArray("EquityApplicantDetail");

                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject1 = jsonArray1.optJSONObject(i);

                            list.add(jsonObject1);
                        }
                        shareNBondAdapter.updateList(list);
                    } else {
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        cv_dashboard_noData.setVisibility(View.VISIBLE);
                        llShareBond.setVisibility(View.GONE);
                        mApplication.showSnackBar(rvShareBond, response.optString("SetviceMSG"));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    cv_dashboard_noData.setVisibility(View.VISIBLE);
                    llShareBond.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showSnackBar(rvShareBond, getResources().getString(R.string.error_try_again));
                    } else {

                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue = Volley.newRequestQueue(ShareBondActivity.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back_my_assets) {
            onBackPressed();
        }
    }
}

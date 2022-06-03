package investwell.client.flavourtypetwo.activity;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
import investwell.client.flavourtypetwo.adapter.FixedDepositAdapter;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FixedDepositActivity extends BaseActivity implements View.OnClickListener {
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private RecyclerView rvFixedDeposit;
    private AppSession mSession;
    private Bundle bundle;
    private String mCid;
    private FixedDepositAdapter fixedDepositAdapter;
    private AppApplication mApplication;
    private ImageView ivToolBarBackIcon;
    private ShimmerFrameLayout shimmerFrameLayout;
    private CardView cv_dashboard_noData;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_deposit);
        initializer();
        getDataFromBundle();
        setFixedDepositAdapter();
        callFixedDepositApi();
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
        mSession = AppSession.getInstance(FixedDepositActivity.this);
        bundle = getIntent().getExtras();
        ivToolBarBackIcon = findViewById(R.id.iv_back_my_assets);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        cv_dashboard_noData = findViewById(R.id.cv_dashboard_noData);
        rvFixedDeposit = findViewById(R.id.rv_fixed_deposit);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back_my_assets) {
            onBackPressed();
        }
    }

    private void setFixedDepositAdapter() {

        rvFixedDeposit.setHasFixedSize(true);

        rvFixedDeposit.setLayoutManager(new LinearLayoutManager(FixedDepositActivity.this,LinearLayoutManager.VERTICAL,false));
        fixedDepositAdapter = new FixedDepositAdapter(FixedDepositActivity.this, new ArrayList<JSONObject>());
        rvFixedDeposit.setAdapter(fixedDepositAdapter);

    }

    private void callFixedDepositApi() {
        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        String url = Config.Fixed_Deposit;
        JSONObject jsonObject = new JSONObject();
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
                        JSONArray jsonArray = response.optJSONArray("FDInvestmentsDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);

                        }
                        fixedDepositAdapter.updateList(list);


                    } else {
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        cv_dashboard_noData.setVisibility(View.VISIBLE);
                        mApplication.showSnackBar(rvFixedDeposit, response.optString("ServiceMSG"));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    cv_dashboard_noData.setVisibility(View.VISIBLE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showSnackBar(rvFixedDeposit, getResources().getString(R.string.error_try_again));
                    } else {
                        mApplication.showSnackBar(rvFixedDeposit, getResources().getString(R.string.no_internet));
                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue = Volley.newRequestQueue(FixedDepositActivity.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

package investwell.client.flavourtypetwo.activity;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.flavourtypetwo.adapter.LifeInsuranceAdapter;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class LifeInsuranceActivity extends BaseActivity implements View.OnClickListener {
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private Bundle bundle;
    private AppSession mSession;
    private String mCID = "";
    private LifeInsuranceAdapter my_insurance_adapter;
    private RecyclerView rvLifeInsurance;
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
        setContentView(R.layout.activity_life_insurance);
        initializer();
        getDataFromBundle();
        setLifeInsuranceAdapter();
        callLifeInsuranceApi();
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
        mSession = AppSession.getInstance(LifeInsuranceActivity.this);
        bundle = getIntent().getExtras();

        rvLifeInsurance = findViewById(R.id.rv_life_insurance);
        ivToolBarBackIcon = findViewById(R.id.iv_back_my_assets);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        cv_dashboard_noData = findViewById(R.id.cv_dashboard_noData);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("cid")) {

            mCID = bundle.getString("cid");
        } else {
            mCID = mSession.getCID();
        }
    }

    private void setLifeInsuranceAdapter() {
        rvLifeInsurance.setHasFixedSize(true);
        rvLifeInsurance.setLayoutManager(new LinearLayoutManager(LifeInsuranceActivity.this, LinearLayoutManager.VERTICAL, false));
        my_insurance_adapter = new LifeInsuranceAdapter(LifeInsuranceActivity.this, new ArrayList<JSONObject>());
        rvLifeInsurance.setAdapter(my_insurance_adapter);
    }

    /*******************************API Calling****************************************************/
    private void callLifeInsuranceApi() {
        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        String url = Config.My_Insurance;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCID);
            jsonObject.put("FormatReq", "Y");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    ArrayList<JSONObject> list = new ArrayList<>();
                    try {

                        String Status = response.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            cv_dashboard_noData.setVisibility(View.GONE);
                            JSONArray jsonArray = response.optJSONArray("MyInsuranceDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                if (jsonObject1.optString("InsType").equalsIgnoreCase("Life")) {
                                    list.add(jsonObject1);
                                }

                            }
                            my_insurance_adapter.updateList(list);
                        } else {
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            cv_dashboard_noData.setVisibility(View.VISIBLE);
                            mApplication.showSnackBar(rvLifeInsurance, response.optString("ServiceMSG"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    cv_dashboard_noData.setVisibility(View.VISIBLE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showSnackBar(rvLifeInsurance, getResources().getString(R.string.error_try_again));

                    } else {
                        mApplication.showSnackBar(rvLifeInsurance, getString(R.string.no_internet));
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

            requestQueue = Volley.newRequestQueue(LifeInsuranceActivity.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
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
}

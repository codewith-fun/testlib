package investwell.client.flavourtypetwo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.flavourtypetwo.adapter.MyAssetTypeTwoAdapter;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class DashboardMyAssetsActivity extends BaseActivity implements View.OnClickListener, MyAssetTypeTwoAdapter.MyAssetsClickListener {
    private Toolbar toolbar;
    private TextView tvToolBarTitle;
    private ImageView ivToolBarBackIcon;
    private RecyclerView rvMyAssets;
    private MyAssetTypeTwoAdapter myAssetTypeTwoAdapter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private AppApplication mApplication;
    private AppSession mSession;
    private Intent intent;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_my_assets);
        initializer();
        setListeners();
        setMyAssetAdapter();
        callMyAssetsApi();
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
        mSession = AppSession.getInstance(DashboardMyAssetsActivity.this);
        mApplication = (AppApplication) this.getApplication();
        intent = new Intent();
        toolbar = findViewById(R.id.toolbar_type_two);
        tvToolBarTitle = findViewById(R.id.tv_toolbar_title);
        ivToolBarBackIcon = findViewById(R.id.iv_back_my_assets);
        rvMyAssets = findViewById(R.id.rv_my_assets);
        myAssetTypeTwoAdapter = new MyAssetTypeTwoAdapter(DashboardMyAssetsActivity.this, new ArrayList<JSONObject>(), this);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
    }

    private void setListeners() {
        ivToolBarBackIcon.setOnClickListener(this);
    }

    private void setMyAssetAdapter() {
        rvMyAssets.setLayoutManager(new LinearLayoutManager(DashboardMyAssetsActivity.this, LinearLayoutManager.VERTICAL, false));
        rvMyAssets.setItemAnimator(new DefaultItemAnimator());
        rvMyAssets.setNestedScrollingEnabled(false);
        rvMyAssets.setAdapter(myAssetTypeTwoAdapter);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back_my_assets) {
            onBackPressed();
        }
    }

    private void callMyAssetsApi() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.MY_ASSETS_API;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("FormatReq", "Y");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("AllAssetAUMDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                if (object.toString().contains("Mutual")) {
                                    list.add(0, object);
                                } else {
                                    list.add(object);
                                }
                            }

                            if (list.size() > 0) {
                                myAssetTypeTwoAdapter.updateList(list);
                            } else {
//                                mTvNothing.setVisibility(View.VISIBLE);
                            }

                        } else {
                            if (list.size() > 0) {
                                myAssetTypeTwoAdapter.updateList(list);

                            } else {

                            }
                            //Toast.makeText(getActivity(), jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
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
                            mApplication.showSnackBar(rvMyAssets, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError) {

                        mApplication.showSnackBar(rvMyAssets, getResources().getString(R.string.no_internet));
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
            RequestQueue requestQueue = Volley.newRequestQueue(DashboardMyAssetsActivity.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMyAssetsClick(int position) {
        switch (position) {
            case 0:
                intent = new Intent(DashboardMyAssetsActivity.this, MainActivity.class);
                intent.putExtra("comingFromActivity", "MyAssetType2B");
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(DashboardMyAssetsActivity.this, ShareBondActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(DashboardMyAssetsActivity.this, FixedDepositActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(DashboardMyAssetsActivity.this, GeneralInsuranceActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(DashboardMyAssetsActivity.this, LifeInsuranceActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                startActivity(intent);
                break;
            case 5:
                break;
        }
    }
}

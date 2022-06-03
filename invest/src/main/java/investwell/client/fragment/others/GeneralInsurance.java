package investwell.client.fragment.others;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
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
import investwell.client.activity.MainActivity;
import investwell.client.adapter.My_Insurance_Adapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class GeneralInsurance extends Fragment {
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private Bundle bundle;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private String mCID = "";
    private My_Insurance_Adapter my_insurance_adapter;
    private RecyclerView insurance_recycle;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {

            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_general_insurance, container, false);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        errorContentInitializer(view);
        mApplication = (AppApplication) mActivity.getApplication();
        bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else {
            mCID = mSession.getCID();
        }
        insurance_recycle = view.findViewById(R.id.insurance_recycle);

        insurance_recycle.setHasFixedSize(true);
        insurance_recycle.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        my_insurance_adapter = new My_Insurance_Adapter(mActivity, new ArrayList<JSONObject>());
        insurance_recycle.setAdapter(my_insurance_adapter);


        getData();

        return view;
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
        ivErrorImage.setImageResource(R.drawable.bg_connection_timeout);
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
    private void getData() {
        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        String url = Config.My_Insurance;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    ArrayList<JSONObject> list = new ArrayList<>();
                    try {

                        String Status = response.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {
                            JSONArray jsonArray = response.optJSONArray("MyInsuranceDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                if (jsonObject1.optString("InsType").equalsIgnoreCase("General")) {
                                    list.add(jsonObject1);
                                }
                            }

                        } else {
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            viewNoData.setVisibility(View.VISIBLE);
                            displayServerMessage(response.optString("ServiceMSG"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        if (list.size() > 0) {
                            my_insurance_adapter.updateList(list);
                           viewNoData.setVisibility(View.GONE);
                        } else {
                            my_insurance_adapter.updateList(list);
                            viewNoData.setVisibility(View.VISIBLE);
                            displayServerMessage(response.optString("ServiceMSG"));
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        if (error instanceof TimeoutError)
                            displayConnectionTimeOut();
                        else if (error instanceof NoConnectionError)
                            displayNoInternetMessage();     }
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
}

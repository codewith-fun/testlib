package investwell.client.fragment.goalbased.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.goalbased.Adapter.SchemeAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

import static investwell.utils.Config.Goal_Based_Scheme_Recommendation;

public class FragScheme extends Fragment {
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private MainActivity mActivity;
    private String investment_amount, amount;
    Bundle bundle;
    private RecyclerView scheme_recycle;
    private SchemeAdapter schemeAdapter;
    private String elss_value, mRiskID;
    private TextView mTvNothing;
    private ShimmerFrameLayout mShimmerViewContainer;
    private AppApplication mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_scheme, container, false);
        bundle = getArguments();
        mActivity = (MainActivity) getActivity();
        mSession = AppSession.getInstance(getActivity());
        mApplication = (AppApplication) getActivity().getApplication();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_scheme_container);
        if (bundle != null && bundle.containsKey("ELSS")) {

            elss_value = bundle.getString("ELSS");
        } else {
            elss_value = "N";
        }
        if (bundle != null) {
            investment_amount = (bundle.getString("Amount"));
            amount = investment_amount.replace(",", "").replace(getString(R.string.rs), "");
            mRiskID = bundle.getString("RiskId");
            getData();
        }


        mTvNothing = view.findViewById(R.id.tvNothing);
        scheme_recycle = view.findViewById(R.id.scheme_recycle);
        scheme_recycle.setHasFixedSize(true);
        scheme_recycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        schemeAdapter = new SchemeAdapter(getActivity(), new ArrayList<JSONObject>());
        scheme_recycle.setAdapter(schemeAdapter);


        return view;

    }

    private void getData() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Goal_Based_Scheme_Recommendation;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Period", bundle.getInt("duration"));
            jsonObject.put("RiskCode", mRiskID);
            jsonObject.put("Amount", amount);
            jsonObject.put(AppConstants.CUSTOMER_ID,mSession.getCID());
            jsonObject.put("ELSS", elss_value);
            jsonObject.put("GoalCategoryID",bundle.getString("GoalCategoryID"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    Config.GoalBaseScheme = jsonObject;
                    ArrayList<JSONObject> list = new ArrayList<>();
                    if (jsonObject.optBoolean("Status")) {
                        JSONArray jsonArray = jsonObject.optJSONArray("GoalBasedSchemeDetail");



                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);
                        }

                    } else {
                        //  Toast.makeText(mActivity, jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

                    PersonalInvestmentFrag fragment = (PersonalInvestmentFrag) getParentFragment();
                    if (list.size() > 0) {
                        if (fragment != null && fragment instanceof PersonalInvestmentFrag) {
                            fragment.mBtOrder.setClickable(true);
                            fragment.mBtOrder.setBackgroundResource(R.drawable.blue_border_btn);

                        }
                        mSession.setCLicked(true);
                        schemeAdapter.updateList(list);
                        mTvNothing.setVisibility(View.GONE);
                    } else {
                        if (fragment != null && fragment instanceof PersonalInvestmentFrag) {
                            fragment.mBtOrder.setClickable(false);
                            fragment.mBtOrder.setBackgroundResource(R.drawable.blue_border_btn);
                        }
                        mTvNothing.setVisibility(View.VISIBLE);
                        mSession.setCLicked(false);
                        schemeAdapter.updateList(new ArrayList<JSONObject>());
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
                            mApplication.showSnackBar(mShimmerViewContainer,jsonObject.optString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        mApplication.showSnackBar(mShimmerViewContainer,getResources().getString(R.string.no_internet));


                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

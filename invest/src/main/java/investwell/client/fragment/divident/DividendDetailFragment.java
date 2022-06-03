package investwell.client.fragment.divident;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class DividendDetailFragment extends Fragment {
    StringRequest stringRequest;
    RequestQueue requestQueue;
    private RecyclerView detail_recycle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private Bundle bundle;
    private AdapterDividendDetail dividend_detail_adapter;
    private TextView applicant_name, colorBlue;
    private CardView cardview;

    private ToolbarFragment toolbarFragment;
    private ShimmerFrameLayout mShimmerViewContainer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dividend__detail, container, false);

        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();
        mSession = AppSession.getInstance(getActivity());
        setUpToolBar();
        cardview = view.findViewById(R.id.cv_dashboard_default);
        applicant_name = view.findViewById(R.id.applicant_name);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        colorBlue = view.findViewById(R.id.colorBlue);
        detail_recycle = view.findViewById(R.id.detail_recycle);
        detail_recycle.setHasFixedSize(true);
        detail_recycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        detail_recycle.addItemDecoration(new DividerItemDecoration(detail_recycle.getContext(), DividerItemDecoration.VERTICAL));

        dividend_detail_adapter = new AdapterDividendDetail(getActivity(), new ArrayList<JSONObject>());
        detail_recycle.setAdapter(dividend_detail_adapter);
        bundle = getArguments();
        if (bundle != null) {
            applicant_name.setText(bundle.getString("applicant_name"));
            colorBlue.setText(bundle.getString("colorBlue"));
        }
        callDividendDetailApi();


        return view;
    }

    private void setUpToolBar() {
        toolbarFragment
                = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment
                != null) {
            toolbarFragment
                    .setUpToolBar(getResources().getString(R.string.main_nav_title_dividend_detail), true, false, false, false, false, false, false, "");
        }
    }

    private void callDividendDetailApi() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();

        String url = Config.Dividend_Detail;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, bundle.getString("cid"));
            jsonObject.put("FromYear", bundle.getString("year"));
            jsonObject.put("ToYear", bundle.getString("next_year"));
            jsonObject.put("Fcode", bundle.getString("fcode"));
            jsonObject.put("Scode", bundle.getString("scode"));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String status = jsonObject.optString("Status");
                        if (status.equalsIgnoreCase("True")) {
                            cardview.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = jsonObject.optJSONArray("DividendDetails");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                list.add(jsonObject1);

                            }
                            dividend_detail_adapter.updateList(list);


                        } else {

                            mApplication.showSnackBar(detail_recycle,jsonObject.optString("ServiceMSG"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
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
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

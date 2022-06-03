package investwell.client.fragment.goalbased.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
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
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.goalbased.Adapter.GoalCategoryTypeOneAAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.GridSpacingItemDecoration;

public class GoalCategoryTypeOneAFragment extends Fragment implements View.OnClickListener {
    private MainActivity mActivity;
    private AppSession mSession;
    private GoalCategoryTypeOneAAdapter mGoalCategoryTypeOneAAdapter;
    private ToolbarFragment toolbarFragment;
    private ShimmerFrameLayout mShimmerViewContainer;
    private TextView mTvNothing;
    private RecyclerView rvGoalCategoryTypeOneA;
    private AppApplication mApplication;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_category_type_one_a, container, false);
        setInitializer(view);
        setUpToolBar();
        setRvGoalCategoryTypeOneAAdapter();
        callGoalCategoryApi();
        return view;
    }

    private void setInitializer(View view) {
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(mActivity);
        rvGoalCategoryTypeOneA = view.findViewById(R.id.rv_goal_category_type_one_a);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mTvNothing = view.findViewById(R.id.tvNothing);
    }

    private void setUpToolBar() {
        toolbarFragment
                = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment
                != null) {
            toolbarFragment
                    .setUpToolBar(getResources().getString(R.string.main_nav_title_choose_your_goal), true, false, false, false, false, false, false, "");
        }
    }

    private void setRvGoalCategoryTypeOneAAdapter() {
        mGoalCategoryTypeOneAAdapter = new GoalCategoryTypeOneAAdapter(mActivity, new ArrayList<JSONObject>());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
        rvGoalCategoryTypeOneA.setLayoutManager(gridLayoutManager);
        rvGoalCategoryTypeOneA.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        rvGoalCategoryTypeOneA.setItemAnimator(new DefaultItemAnimator());
        rvGoalCategoryTypeOneA.setNestedScrollingEnabled(false);
        rvGoalCategoryTypeOneA.setAdapter(mGoalCategoryTypeOneAAdapter);
    }

    /****************************************
     * Converting dp to pixel
     ****************************************/
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void callGoalCategoryApi() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.GOAL_Category;
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ArrayList<JSONObject> list = new ArrayList<>();

                try {
                    if (jsonObject.optBoolean("Status")) {
                        JSONArray araArray = jsonObject.getJSONArray("GoalCategoryList");
                        for (int i = 0; i < araArray.length(); i++) {
                            JSONObject object = araArray.getJSONObject(i);
                            list.add(object);
                        }


                    } else {
                        mApplication.showSnackBar(rvGoalCategoryTypeOneA,getResources().getString(R.string.error_try_again));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (list.size() > 0) {
                        mTvNothing.setVisibility(View.GONE);
                        mGoalCategoryTypeOneAAdapter.updateList(list);
                    } else {
                        mTvNothing.setVisibility(View.VISIBLE);
                        mGoalCategoryTypeOneAAdapter.updateList(new ArrayList<JSONObject>());
                    }


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        mApplication.showSnackBar(rvGoalCategoryTypeOneA,jsonObject.optString("error"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(rvGoalCategoryTypeOneA,getResources().getString(R.string.no_internet));


            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View v) {

    }


}

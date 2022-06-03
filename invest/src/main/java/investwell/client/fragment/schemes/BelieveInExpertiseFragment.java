package investwell.client.fragment.schemes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import investwell.client.adapter.RecycleGoalAdapter;
import investwell.client.adapter.RecycleSchemesAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

public class BelieveInExpertiseFragment extends Fragment {

    private ProgressDialog mBar;
    private RecycleGoalAdapter mAdapter;
    private RecycleSchemesAdapter mAdapter2;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppApplication mAppApplication;
    private ToolbarFragment fragToolBar;
    private ShimmerFrameLayout mShimmerViewContainer;
    private TextView tvGoalBased, tvRecommended;
    private LinearLayout llRecommendedCartContainer,llGoalBasedContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_believe_in_expertise, container, false);
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        mAppApplication = (AppApplication) mActivity.getApplication();
        LinearLayout lldesc = view.findViewById(R.id.ll_description);
        TextView tvDesc = view.findViewById(R.id.tv_description);
        if (!TextUtils.isEmpty(mSession.getAcceleratorDesc())) {
            tvDesc.setText(mSession.getAcceleratorDesc());
            lldesc.setVisibility(View.VISIBLE);
        } else {
            lldesc.setVisibility(View.GONE);
        }
        mShimmerViewContainer = view.findViewById(R.id.shimmer_scheme_container);
        llRecommendedCartContainer = view.findViewById(R.id.ll_recommended_container);
        llGoalBasedContainer=view.findViewById(R.id.ll_goal_based_basket);
        setUpToolBar();
        tvGoalBased = view.findViewById(R.id.tv_goal_based);
        tvRecommended = view.findViewById(R.id.tv_recommended);
        RecyclerView recycleGoal = view.findViewById(R.id.rv_goal_scheme);
        RecyclerView recycleScheme = view.findViewById(R.id.rv_recommended);

        recycleGoal.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        recycleScheme.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

        mAdapter = new RecycleGoalAdapter(mActivity, new ArrayList<JSONObject>(), new RecycleGoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    JSONObject jsonObject = mAdapter.mDataList.get(position);
                    mSession.setSelectedBasketCode(jsonObject.optString("BasketCode"));
                    jsonObject.put("isSelected", true);
                    Bundle bundle = new Bundle();
                    bundle.putString("AllData", mAdapter.mDataList.toString());
                    bundle.putString("selected_position", "" + position);
                    mActivity.displayViewOther(2, bundle);
                    /*    mApplication.showSnackBar(view,getResources().getString(R.string.work_under_development));*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mAdapter2 = new RecycleSchemesAdapter(mActivity, new ArrayList<JSONObject>(), new RecycleSchemesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter2.mDataList.get(position);
                mSession.setSelectedBasketCode(jsonObject.optString("BasketCode"));
                Bundle bundle = new Bundle();
                bundle.putString("singleObject", jsonObject.toString());
                mActivity.displayViewOther(3, bundle);
                /*  mApplication.showSnackBar(view,getResources().getString(R.string.work_under_development));*/
            }
        });


        recycleGoal.setAdapter(mAdapter);
        recycleScheme.setAdapter(mAdapter2);

        tvGoalBased.setText(getResources().getString(R.string.believe_scheme_header_goal_based_txt));
        tvRecommended.setText(getResources().getString(R.string.believe_scheme_header_recommended_based_txt));

        if (mAppApplication.getRecomendetSchemeList().size() > 0 && mAppApplication.getGoalSchemeList().size() > 0) {
            updateList();
        } else {
            getSchemes();
        }
        setUpUiVisibility(view);
        setUpAppUi(view);
        return view;
    }

    /*******************************************
     * Method used to setup UI as per the app layout_gridview_type_two_a type
     *******************************************/
    private void setUpAppUi(View view) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
            view.findViewById(R.id.tv_goal_based).setBackground(getResources().getDrawable(R.mipmap.heading_bg));
            tvGoalBased.setTextColor(getResources().getColor(R.color.colorWhite));
            view.findViewById(R.id.tv_recommended).setBackground(getResources().getDrawable(R.mipmap.heading_bg));
            tvRecommended.setTextColor(getResources().getColor(R.color.colorWhite));
            llRecommendedCartContainer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            llGoalBasedContainer.setBackgroundColor(getResources().getColor(R.color.colorGrey_100));
            tvGoalBased.setPadding(16,16,16,16);
            tvRecommended.setPadding(16,16,16,16);
            llGoalBasedContainer.setPadding(8,8,8,8);
            llRecommendedCartContainer.setPadding(8,8,8,8);
        } else {
            view.findViewById(R.id.tv_goal_based).setBackground(null);
            tvGoalBased.setTextColor(getResources().getColor(R.color.colorBlack));
            view.findViewById(R.id.tv_recommended).setBackground(null);
            tvRecommended.setTextColor(getResources().getColor(R.color.colorBlack));
            tvGoalBased.setPadding(4,4,4,4);
            tvRecommended.setPadding(4,4,4,4);
            llGoalBasedContainer.setPadding(0,0,0,0);
            llRecommendedCartContainer.setPadding(0,0,0,0);
            llRecommendedCartContainer.setBackgroundColor(0);
            llGoalBasedContainer.setBackgroundColor(0);
        }

    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(mSession.getAccelerator(),
                    true, false, false, false, false, false, false, "");
        }
    }

    private void setUpUiVisibility(View view) {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AccelatorHorizontal")) &&
                Utils.getConfigData(mSession).optString("AccelatorHorizontal").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.tv_goal_based).setVisibility(View.VISIBLE);
            view.findViewById(R.id.rv_goal_scheme).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.tv_goal_based).setVisibility(View.GONE);
            view.findViewById(R.id.rv_goal_scheme).setVisibility(View.GONE);

        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AccelatorVertical")) &&
                Utils.getConfigData(mSession).optString("AccelatorVertical").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.tv_recommended).setVisibility(View.VISIBLE);
            view.findViewById(R.id.rv_recommended).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.tv_recommended).setVisibility(View.GONE);
            view.findViewById(R.id.rv_recommended).setVisibility(View.GONE);

        }
    }

    private void getSchemes() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.GET_BASKET;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();
                        ArrayList<JSONObject> list2 = new ArrayList<>();
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("BasketList");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                if (object.optString("MobileView").equals("Y")) {
                                    list.add(object);
                                } else {
                                    list2.add(object);
                                }

                            }

                            mAppApplication.setRecomendetSchemeList(list);
                            mAppApplication.setGoalSchemeList(list2);
                            updateList();
                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                            }
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
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

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

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void updateList() {
        mAdapter.updateList(mAppApplication.recomendetSchemeList);
        mAdapter2.updateList(mAppApplication.goalBasedSchemeList);
    }

}

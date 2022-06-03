package investwell.client.fragment.goalbased.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.goalbased.Adapter.FragGoalSummeryAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class FragGoalList extends Fragment implements ToolbarFragment.ToolbarCallback {
    private ProgressDialog mBar;
    private AppApplication mApplication;
    private FragGoalSummeryAdapter mAdapter;
    private AppSession mSession;
    private MainActivity mActivity;
    private String mType = "";
    private TextView mTvNothing;
    private String mUCC_Code = "";
    private RecyclerView mRecyclerView;
    private ToolbarFragment toolbarFragment;
    private ShimmerFrameLayout mShimmerViewContainer;
    private View viewErrorGoal;
    private Button btnCreateGoal;
    private int totalGoalTargetPrice = 0;
    private int totalCurrentInvest = 0;
    private TextView mTotalGoalTargetPrice, mTotalCurrentInvest;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_list, container, false);
        setUpToolBar();
        viewErrorGoal = view.findViewById(R.id.content_error_with_action);
        btnCreateGoal = viewErrorGoal.findViewById(R.id.btn_create_goal);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_scheme_container);
        mRecyclerView = view.findViewById(R.id.rv_goal_list);
        mTotalCurrentInvest = view.findViewById(R.id.tv_total_invest_value);
        mTotalGoalTargetPrice = view.findViewById(R.id.tv_total_goal_value);
        setAdapter();
        if(mSession.getHasLoging()) {
            getGoalSummary(view);
            viewErrorGoal.setVisibility(View.GONE);
        }else {
            viewErrorGoal.setVisibility(View.VISIBLE);
        }
        setListener();
        return view;
    }

    private void setListener() {
        btnCreateGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSession.getHasLoging()) {
                    if (mSession.getRiskName().isEmpty()) {
                        showDailog();
                    } else {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                            mActivity.displayViewOther(102, null);
                        } else {
                            mActivity.displayViewOther(77, null);
                        }

                    }


                } else {
                    mApplication.showCommonDailog(mActivity, mActivity, false, "Goal Management", "Login required for Goal Management", "message", false, true);
                }
            }
        });
    }

    private void initializer(View view) {

    }

    private void hideTopCardData(View view) {
        view.findViewById(R.id.iv_card_header).setVisibility(View.GONE);
        view.findViewById(R.id.tv_total_goal_value).setVisibility(View.GONE);
        view.findViewById(R.id.textView19).setVisibility(View.GONE);
        view.findViewById(R.id.tv_total_invest_value).setVisibility(View.GONE);
        view.findViewById(R.id.textView18).setVisibility(View.GONE);
        view.findViewById(R.id.v_divider).setVisibility(View.GONE);
    }

    private void showTopCardData(View view) {
        view.findViewById(R.id.iv_card_header).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_total_goal_value).setVisibility(View.VISIBLE);
        view.findViewById(R.id.textView19).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_total_invest_value).setVisibility(View.VISIBLE);
        view.findViewById(R.id.textView18).setVisibility(View.VISIBLE);
        view.findViewById(R.id.v_divider).setVisibility(View.VISIBLE);
    }

    private void setAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mAdapter = new FragGoalSummeryAdapter(mActivity, new ArrayList<JSONObject>(), new FragGoalSummeryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("data", jsonObject.toString());
                bundle.putString("goalid", jsonObject.optString("GoalID"));
                mActivity.displayViewOther(71, bundle);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(mSession.getGoal(), true, false, false, true, false, false, false, getResources().getString(R.string.goal_summary_btn_txt));
            toolbarFragment.setCallback(this);
        }
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.btn_add_new) {
            if (mSession.getHasLoging()) {
                if (mSession.getRiskName().isEmpty()) {
                    showDailog();
                } else {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                        mActivity.displayViewOther(102, null);
                    } else {
                        mActivity.displayViewOther(77, null);
                    }

                }


            } else {
                mApplication.showCommonDailog(mActivity, mActivity, false, "Goal Management", "Login required for Goal Management", "message", false, true);
            }
        }
    }

    private void showDailog() {
        CustomDialog customDialog = new CustomDialog(new CustomDialog.DialogBtnCallBack() {
            @Override
            public void onDialogBtnClick(View view) {
                int id = view.getId();
                if (id == R.id.btDone) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "create_goal");
                    mActivity.displayViewOther(61, bundle);
                } else if (id == R.id.btCalcel) {
                }
            }
        });

        customDialog.showDialog(mActivity, getString(R.string.alert_dialog_confirmation_header_txt),
                getString(R.string.alert_dialog_no_risk_profile_txt),
                getString(R.string.alert_dialog_continue_txt),
                getString(R.string.alert_dialog_later_txt),
                true, true);
    }

    private void getGoalSummary(final View view) {
        hideTopCardData(view);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.GOAL_SUMMARY;
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            jsonParam.put("GoalID", "");
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                try {
                    if (jsonObject.optBoolean("Status")) {
                        JSONArray araArray = jsonObject.getJSONArray("GoalDetailList");
                        // JSONArray araArray = jsonObject.getJSONArray("GoalReportDetail");
                        if (araArray.length() > 0) {
                            viewErrorGoal.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            viewErrorGoal.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                        List<JSONObject> goalSummaryList = new ArrayList<>();
                        for (int i = 0; i < araArray.length(); i++) {
                            JSONObject object = araArray.getJSONObject(i);
                            goalSummaryList.add(object);
                        }

                        mAdapter.updateList(goalSummaryList);
                        totalCurrentInvest=0;
                        totalGoalTargetPrice=0;
                        for (int i = 0; i < goalSummaryList.size(); i++) {
                            String num = String.valueOf(NumberFormat.getNumberInstance(java.util.Locale.US).parse(goalSummaryList.get(i).optString("ExpectedCorpus")));
                            totalGoalTargetPrice += Integer.parseInt(num);
                            String currVal = !TextUtils.isEmpty(goalSummaryList.get(i).optString("CurrentValue")) ? goalSummaryList.get(i).optString("CurrentValue") : "0";
                            totalCurrentInvest += Integer.parseInt(String.valueOf(NumberFormat.getNumberInstance(java.util.Locale.US).parse(currVal)));
                        }
                        Log.e("TOTAL GOAL", String.valueOf(totalGoalTargetPrice));
                        Log.e("Invested GOAL", String.valueOf(totalCurrentInvest));
                        showTopCardData(view);
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        String strAmount = format.format(totalCurrentInvest);
                        String[] resultAmount = strAmount.split("\\.", 0);
                        mTotalCurrentInvest.setText(resultAmount[0]);
                        Format f2 = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        String goalTotal = f2.format(totalGoalTargetPrice);
                        String[] goalTargetAmount = goalTotal.split("\\.", 0);
                        mTotalGoalTargetPrice.setText(goalTargetAmount[0]);

                    } else {
                        viewErrorGoal.setVisibility(View.VISIBLE);
                        hideTopCardData(view);
                        mRecyclerView.setVisibility(View.GONE);
//                        Toast.makeText(mActivity, mDataList.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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
                        mApplication.showSnackBar(mShimmerViewContainer, jsonObject.optString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(mShimmerViewContainer, getResources().getString(R.string.no_internet));

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
    }


}

package investwell.client.fragment.divident;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.Calendar;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;


public class DividendFragment extends Fragment implements ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack {

    private RecyclerView dividend_recyclier;
    private AdapterDividend dividend_adapter;
    private TextView Previous_Year, Next_Year;
    private ImageView Previous_Year_img, Next_Year_img;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private Calendar c = Calendar.getInstance();
    private int year;
    private int next_year;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private Bundle bundle;
    private String mCID = "";
    private CardView cardview;
    private ToolbarFragment fragToolBar;
    private CustomDialog customDialog;
    private TextView mTvNothing;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_dividend, container, false);
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        setUpToolBar();
        mApplication = (AppApplication) mActivity.getApplication();
        cardview = view.findViewById(R.id.cv_dashboard_default);
        Previous_Year_img = view.findViewById(R.id.previous_year);
        Next_Year_img = view.findViewById(R.id.next_year);
        mTvNothing = view.findViewById(R.id.tvNothing);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        customDialog = new CustomDialog(this);
        dividend_recyclier = view.findViewById(R.id.dividend_recyclier);
        dividend_recyclier.setHasFixedSize(true);
        dividend_recyclier.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        dividend_adapter = new AdapterDividend(mActivity, new ArrayList<JSONObject>());
        dividend_recyclier.setAdapter(dividend_adapter);
        bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else
            mCID = mSession.getCID();

        Previous_Year = view.findViewById(R.id.previous_year_txt);
        Next_Year = view.findViewById(R.id.next_year_txt);
        year = c.get(Calendar.YEAR);
        next_year = year - 1;

        if (c.get(Calendar.MONTH) > 2) {
            next_year = next_year + 1;
            year = year + 1;

        }
        Previous_Year.setText("" + next_year);
        Next_Year.setText("" + year);


        Previous_Year_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = year - 1;
                next_year = next_year - 1;
                Previous_Year.setText("" + next_year);
                Next_Year.setText("" + year);
                callDividendApi();
            }
        });

        Next_Year_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = year + 1;
                next_year = next_year + 1;
                Previous_Year.setText("" + next_year);
                Next_Year.setText("" + year);
                callDividendApi();
            }
        });


        callDividendApi();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_dividend), true, false, false, false, false, false, true, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
            fragToolBar.setCallback(this);
        }

    }

    private void callDividendApi() {

        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        if (c.get(Calendar.YEAR) == Integer.parseInt(Previous_Year.getText().toString())) {

            Next_Year_img.setVisibility(View.INVISIBLE);
        } else {

            Next_Year_img.setVisibility(View.VISIBLE);
        }

        String url = Config.Dividend_Summary;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put("FromYear", next_year);
            jsonObject.put("ToYear", year);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    ArrayList<JSONObject> list = new ArrayList<>();

                    try {
                        String Status = response.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {
                            cardview.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = response.getJSONArray("DividendSummaryClientList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                list.add(jsonObject1);

                            }
                        } else {
                            cardview.setVisibility(View.INVISIBLE);
                            String ServiceMsg = response.optString("ServiceMSG");
                            mApplication.showSnackBar(cardview, ServiceMsg);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (list.size() > 0) {
                            dividend_adapter.updateList(list, next_year, year);
                            mTvNothing.setVisibility(View.GONE);
                        } else {
                            dividend_adapter.updateList(list, next_year, year);
                            mTvNothing.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
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

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showWatchListInfoDialog() {
        customDialog.showDialog(getActivity(), getResources().getString(R.string.fatca_instruction),
                getResources().getString(R.string.dividend_header_txt),
                getResources().getString(R.string.text_ok), "", true, false);
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_fatca_info) {
            showWatchListInfoDialog();
        }
    }

    @Override
    public void onDialogBtnClick(View view) {
        if (view.getId() == R.id.btDone) {
            //Todo nothing
        }
    }
}

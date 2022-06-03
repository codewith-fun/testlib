package investwell.client.fragment.myOrderCeaseSIP;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragOrderSIPAll extends Fragment implements View.OnClickListener {
    private MainActivity mActivty;
    private AppSession mSession;
    private AppApplication mApplication;
    private String whoseTransaction = "All";
    private AdapterOrderSIP mAdapterOrderSIP;
    private int i;
    private CheckBox cbOnlyMyData;
    private TextView mTvNoData;
    private String mType = "SIP";
    private ShimmerFrameLayout mShimmerViewContainer;
    private boolean isShowingSnackbar;
    private Bundle bundle;
    private String url, mOnlineOption, mLastDay, mTransType, mTransStatus, mOrderType, mFromDate;
    private TextView mTvMonth;
    private ImageView mIvPriviousMonth, mIvNextMonth;
    private Calendar mCalendar, CurrentCalendar;
    private int Month_value, year;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivty = (MainActivity) context;
            mActivty = (MainActivity) getActivity();
            mSession = AppSession.getInstance(mActivty);
            mActivty.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivty.getApplication();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_my_order_sip_cease, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();
        mType = bundle.getString("type");
        mOnlineOption = bundle.getString("OnlineOption");
        if(mType.equals("All") || mType.equals("SIP")) {
            mLastDay = bundle.getString("LastDay");
            mTransType = bundle.getString("TranType");
            mTransStatus = bundle.getString("TranStatus");
            mOrderType = bundle.getString("OrderType");
        } else {
            mFromDate = bundle.getString("mFromDate");
        }
        initializer(view);
        callFetchSIPDataApi();
    }

    private void initializer(View view) {
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mTvNoData = view.findViewById(R.id.tvLoading);
        cbOnlyMyData = view.findViewById(R.id.cb_only_my_data);

        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapterOrderSIP = new AdapterOrderSIP(getActivity(), new ArrayList<JSONObject>(), new AdapterOrderSIP.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
        recyclerView.setAdapter(mAdapterOrderSIP);
        //callFetchSIPDataApi();
    }

    @Override
    public void onClick(View view) {
    }

    /*********************************Fetch My Order SIP and ALL Data**********************/
    private void callFetchSIPDataApi() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("OnlineOption", mSession.getAppType());
            if(mType.equals("All") || mType.equals("SIP")) {
                if(mType.equals("All"))
                    jsonObject.put("OrderType", "ALL"); // ALLSIP, STP, SWP
                else
                    jsonObject.put("OrderType", "ALLSIP");
                jsonObject.put("LastDay", mLastDay);
                jsonObject.put("TranType", mTransType);
                jsonObject.put("TranStatus", mTransStatus);
            }else{
                jsonObject.put("SystematicType", "SIP");
                jsonObject.put("FromDate", mFromDate);
            }

            if (mType.equals("All"))
                url = Config.Live_My_Orders_All;
            else if (mType.equals("SIP"))
                url = Config.Live_My_Orders_SIP;
            else
                url = Config.Systematic_Reports;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ArrayList<JSONObject> list = new ArrayList<>();
                    try {
                        if (response.optBoolean("Status")) {
                            JSONArray jsonArray = response.optJSONArray("ResponseData");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject();
                                JSONArray jsonArray1 = new JSONArray();
                                if (mType.equals("All")) { // For ALl Orders
                                    if (jsonArray.optJSONObject(i).optJSONArray("LiveOrderDataList").length() > 0)
                                        jsonArray1 = jsonArray.optJSONObject(i).optJSONArray("LiveOrderDataList");
                                } else if (mType.equals("SIP")) {
                                     // For SIP Orders
                                    if (jsonArray.optJSONObject(i).optJSONArray("LiveSystematicOrderDataList").length() > 0)
                                        jsonArray1 = jsonArray.optJSONObject(i).optJSONArray("LiveSystematicOrderDataList");
                                } else {
                                    // For SIP CEASE
                                    if (jsonArray.optJSONObject(i).optJSONArray("LiveSystematicReportList").length() > 0)
                                        jsonArray1 = jsonArray.optJSONObject(i).optJSONArray("LiveSystematicReportList");
                                }
                                for (int j = 0; jsonArray1 != null && j < jsonArray1.length(); j++) {
                                    jsonObject = jsonArray1.optJSONObject(j);
                                    if (mType.equals("Running") && jsonObject.optString("TrxnStatus").equals("SIP-Processed")) {
                                        list.add(jsonObject);
                                    } else if (mType.equals("Matured") && jsonObject.optString("TrxnStatus").equals("SIP-Matured")) {
                                        list.add(jsonObject);
                                    } else if (mType.equals("CEASED") && jsonObject.optString("TrxnStatus").equals("SIP-Ceased")) {
                                        list.add(jsonObject);
                                    } else if (mType.equals("All") || mType.equals("SIP")) {
                                        list.add(jsonObject);
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        if (list.size() > 0) {
                            mAdapterOrderSIP.updateList(list, mType);
                            mTvNoData.setVisibility(View.GONE);
                        } else {
                            mAdapterOrderSIP.updateList(new ArrayList<JSONObject>(), mType);
                            mTvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivty, mActivty, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mTvNoData.setVisibility(View.VISIBLE);
                        mTvNoData.setText(getResources().getString(R.string.no_internet));
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

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        }


    }

}

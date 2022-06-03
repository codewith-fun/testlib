package investwell.client.fragment.mysip;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragSipStpSwp extends Fragment implements View.OnClickListener {
    private MainActivity mActivty;
    private AppSession mSession;
    private AppApplication mApplication;
    private String whoseTransaction = "All";
    private AdapterSipStpSwp mAdapterSipStpSwp;
    private int i;
    private CheckBox cbOnlyMyData;
    private TextView mTvNoData;
    private String mType = "SIP";
    private ShimmerFrameLayout mShimmerViewContainer;
    private boolean isShowingSnackbar;
    private Bundle bundle;
    private String url;
    private TextView mTvMonth;
    private ImageView mIvPriviousMonth, mIvNextMonth;
    private Calendar mCalendar, CurrentCalendar;
    private int Month_value,year;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivty = (MainActivity) context;
            mActivty = (MainActivity) getActivity();
            mSession = AppSession.getInstance(mActivty);
            mActivty.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivty.getApplication();


            mCalendar = Calendar.getInstance();
            CurrentCalendar = Calendar.getInstance();
            mCalendar.add(Calendar.MONTH, -1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ////frag_sip_stp_swp

        return inflater.inflate(R.layout.frag_sip_stp_swp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();
        getDataFromBundle();
        initializer(view);
    }

    private void initializer(View view) {

      /*  mCalendar.add(Calendar.YEAR,-1);*/
        Month_value = mCalendar.get(Calendar.MONTH);
        year = mCalendar.get(Calendar.YEAR);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mTvNoData = view.findViewById(R.id.tvLoading);
        cbOnlyMyData = view.findViewById(R.id.cb_only_my_data);
        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapterSipStpSwp = new AdapterSipStpSwp(getActivity(), new ArrayList<JSONObject>(), new AdapterSipStpSwp.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    JSONObject jsonObject = mAdapterSipStpSwp.mDataList.get(position);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("data", jsonObject.toString());
                    bundle1.putString("type", mType);
                    bundle1.putString("comingFrom", bundle.getString("comingFrom"));
                    mActivty.displayViewOther(35, bundle1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        recyclerView.setAdapter(mAdapterSipStpSwp);
/*
        year = Calendar.getInstance().get(Calendar.YEAR);
*/

        mTvMonth = view.findViewById(R.id.tvMonth);
        mIvPriviousMonth = view.findViewById(R.id.ivPreviousMonth);
        mIvNextMonth = view.findViewById(R.id.ivNextMonth);

        mIvPriviousMonth.setOnClickListener(this);
        mIvNextMonth.setOnClickListener(this);
        mTvMonth.setText(mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));


        if (bundle.getString("comingFrom").equalsIgnoreCase("mySip")) {
           view.findViewById(R.id.RlMonth).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.RlMonth).setVisibility(View.VISIBLE);
        }
        getMonth(Month_value);

        //callFetchSIPDataApi();
    }

    private void getDataFromBundle() {

        if (bundle != null)
            mType = bundle.getString("type");

        if (mType != null) {
            if (mType.equalsIgnoreCase("SIP")) {
            } else if (mType.equalsIgnoreCase("STP")) {

            } else if (mType.equalsIgnoreCase("SWP")) {

            }

        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.ivPreviousMonth) {
            mCalendar.add(Calendar.MONTH, -1);
            Month_value = mCalendar.get(Calendar.MONTH);
            year = mCalendar.get(Calendar.YEAR);
            System.out.println("Updated Date front= " + Month_value + " " + year);
            getMonth(Month_value);
        } else if (id == R.id.ivNextMonth) {
            mCalendar.add(Calendar.MONTH, 1);
            Month_value = mCalendar.get(Calendar.MONTH);
            year = mCalendar.get(Calendar.YEAR);
            System.out.println("Updated Date front= " + Month_value + " " + year);
            getMonth(Month_value);
        }

    }

    private void getMonth(int num) {

        String month = "";
        if ((Month_value == CurrentCalendar.get(Calendar.MONTH) - 1) && year == CurrentCalendar.get((Calendar.YEAR)-1)) {
            mIvNextMonth.setVisibility(View.INVISIBLE);
        } else {
            mIvNextMonth.setVisibility(View.VISIBLE);
        }

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }

        mTvMonth.setText(month.substring(0, 3) + " - " + year);
        callFetchSIPDataApi();


    }


    private void setListener() {
        cbOnlyMyData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbOnlyMyData.isChecked()) {
                    whoseTransaction = "Cid";
                    callFetchSIPDataApi();
                } else {
                    whoseTransaction = "All";
                    callFetchSIPDataApi();

                }

            }
        });
    }

    /*********************************Fetch SIP Data**********************/
    private void callFetchSIPDataApi() {
        try {

            JSONObject jsonObject = new JSONObject();
            if (bundle != null && bundle.containsKey("comingFrom")) {
                jsonObject.put("Passkey", mSession.getPassKey());
                jsonObject.put("Bid", AppConstants.APP_BID);
                jsonObject.put("Cid", mSession.getCID());
                jsonObject.put("TranType", mType);
                jsonObject.put("whoseTransaction", whoseTransaction);
                jsonObject.put("FormatReq", "N");
                if (bundle.getString("comingFrom").equalsIgnoreCase("mySip")) {
                    url = Config.Systematic_investment;
                } else {

                    url = Config.Systemetic_Transaction;
                    jsonObject.put("TranMonth", Month_value+1);
                    jsonObject.put("TranYear", year);
                }

            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ArrayList<JSONObject> list = new ArrayList<>();

                    try {
                        if (response.optBoolean("Status")) {
                            JSONArray jsonArray;
                            if (bundle.getString("comingFrom").equalsIgnoreCase("mySip")) {
                                jsonArray = response.optJSONArray("MySystematicDetail");
                            } else {
                                jsonArray = response.optJSONArray("MyLastSystematicDetail");
                            }
                            for (i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                list.add(jsonObject1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        if (list.size() > 0) {
                            mAdapterSipStpSwp.updateList(list, mType);
                            mTvNoData.setVisibility(View.GONE);
                            cbOnlyMyData.setVisibility(View.GONE);
                        } else {
                            mAdapterSipStpSwp.updateList(new ArrayList<JSONObject>(), mType);
                            mTvNoData.setVisibility(View.VISIBLE);
                            cbOnlyMyData.setVisibility(View.GONE);
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
                       /* mApplication.showCommonDailog(mActivty, mActivty, false, getResources().getString(R.string.Server_Error), getResources().getString(R.string.no_internet), "message", false, true);

                        mApplication.showSnackBar(cbOnlyMyData, getResources().getString(R.string.no_internet));*/
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

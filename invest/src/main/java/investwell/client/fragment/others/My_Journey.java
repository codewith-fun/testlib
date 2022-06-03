package investwell.client.fragment.others;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class My_Journey extends Fragment {
    RequestQueue requestQueue;
    StringRequest stringRequest;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private String mCID = "";
    private SwipeRefreshLayout swipetorefresh;
    private TextView mPurchase, mSwitchin, mSwitchout, mSell, mDiv_amount, mNet_investment, mCurrent_value, mTotal_gain, mXiir;
    private ToolbarFragment fragToolBar;
    private CardView mCardViewMain;

    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_my__journey, container, false);


        mActivity.setMainVisibility(this, null);
        setUpToolBar();
        errorContentInitializer(view);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else
            mCID = mSession.getCID();

        swipetorefresh = view.findViewById(R.id.swipetorefresh);
        mPurchase = view.findViewById(R.id.purchase);
        mSwitchin = view.findViewById(R.id.switchin);
        mSwitchout = view.findViewById(R.id.switchout);
        mSell = view.findViewById(R.id.sell);
        mDiv_amount = view.findViewById(R.id.dev_amount);
        mNet_investment = view.findViewById(R.id.net_investment);
        mCurrent_value = view.findViewById(R.id.current_val);
        mTotal_gain = view.findViewById(R.id.total_gain);
        mXiir = view.findViewById(R.id.xiir);
        mCardViewMain = view.findViewById(R.id.cardView);


        String logintype = mSession.getLoginType();
        MyJourneyData();
      /*  if (logintype.equalsIgnoreCase("RM") || logintype.equalsIgnoreCase("Broker") || logintype.equalsIgnoreCase("SubBroker")) {
            MyJourneyData();
        } else {
            if (mSession.get_my_journey().isEmpty()) {

                MyJourneyData();

            } else {
                setData();
            }
        }*/
        swipetorefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MyJourneyData();
                swipetorefresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_my_journey_so_far), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
        }
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
    private void MyJourneyData() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.My_Journey;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    mSession.set_my_journey(response.toString());
                    setData();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
viewNoData.setVisibility(View.VISIBLE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        displayServerErrorMessage(error);
                    } else {
                        displayNoInternetMessage();
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


    private void setData() {
        try {

            JSONObject jsonObject = new JSONObject(mSession.get_my_journey());
            String Status = jsonObject.optString("Status");
            if (Status.equalsIgnoreCase("True")) {
                mCardViewMain.setVisibility(View.VISIBLE);
                viewNoData.setVisibility(View.GONE);

                JSONArray jsonArray = jsonObject.optJSONArray("MyJourneyDetail");
                JSONObject jsonObject1 = jsonArray.optJSONObject(0);

                String Purchase = jsonObject1.optString("Purchase");
                String SwitchIn = jsonObject1.optString("SwitchIn");
                String Switchout = jsonObject1.optString("Switchout");
                String Sell = jsonObject1.optString("Sell");
                String DivAmt = jsonObject1.optString("DivAmt");
                String NetInvestment = jsonObject1.optString("NetInvestment");
                String CurrentVal = jsonObject1.optString("CurrentVal");
                String TotalGain = jsonObject1.optString("TotalGain");
                String XIIR = jsonObject1.optString("XIIR");


                mPurchase.setText(getString(R.string.Rs) + Purchase);
                mSwitchin.setText(getString(R.string.Rs) + SwitchIn);
                mSwitchout.setText(getString(R.string.Rs) + Switchout);
                mSell.setText(getString(R.string.Rs) + Sell);
                mDiv_amount.setText(getString(R.string.Rs) + DivAmt);
                mNet_investment.setText(getString(R.string.Rs) + NetInvestment);
                mCurrent_value.setText(getString(R.string.Rs) + CurrentVal);
                mTotal_gain.setText(getString(R.string.Rs) + TotalGain);
                mXiir.setText(XIIR + "%");


            } else {
                mCardViewMain.setVisibility(View.GONE);
                viewNoData.setVisibility(View.VISIBLE);
displayServerMessage("No Data Found");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

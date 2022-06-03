package investwell.client.fragment.others;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;


public class SendMailFragment extends Fragment implements View.OnClickListener, CustomDialog.DialogBtnCallBack {


    LinearLayout current_portfolio, portfolio_summary, login_detail,capitalgain_current,capitalgain_privious;
    ImageView bac_arrow;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    String EmailType;
    private MainActivity mActivity;
    private BrokerActivity mBrokerActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private String mCID = "";
    private ToolbarFragment fragToolBar;
    private CustomDialog customDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
            mActivity.setMainVisibility(this, null);

        }else if(context instanceof BrokerActivity){
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mBrokerActivity.setMainVisibility(this, null);

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
        View view = inflater.inflate(R.layout.fragment_send__mail, container, false);

        current_portfolio = view.findViewById(R.id.current_portfolio);
        portfolio_summary = view.findViewById(R.id.portfolio_summary);
        login_detail = view.findViewById(R.id.login_detail);
        capitalgain_current=view.findViewById(R.id.capital_gain_current);
        capitalgain_privious=view.findViewById(R.id.capital_gain_privious);


        current_portfolio.setOnClickListener(this);
        portfolio_summary.setOnClickListener(this);
        login_detail.setOnClickListener(this);
        capitalgain_current.setOnClickListener(this);
        capitalgain_privious.setOnClickListener(this);
        setUpToolBar();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else
            mCID = mSession.getCID();

        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_send_mail), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
        }
    }

    @Override
    public void onClick(View view) {

       /* if (view.getId() == R.id.bac_arrow) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else*/
        if (view.getId() == R.id.current_portfolio) {
            EmailType = "PortfolioReturn";
            SendEmail();

        } else if (view.getId() == R.id.portfolio_summary) {
            EmailType = "PortfolioSummary";
            SendEmail();


        } else if (view.getId() == R.id.login_detail) {
            EmailType = "LoginDetail";
            SendEmail();

        } else if (view.getId() == R.id.capital_gain_current){
            EmailType = "CapitalGainCurrentFY";
            SendEmail();

        } else if (view.getId() == R.id.capital_gain_privious) {
            EmailType = "CapitalGainPreviousFY";
            SendEmail();
        }



    }

    public void SendEmail() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.Send_Mail;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put("EmailType", EmailType);
            jsonObject.put("Passkey", mSession.getPassKey());


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    String message = response.optString("ServiceMSG");
                    if (response.optString("Status").equals("True")) {

                        mApplication.showSnackBar(current_portfolio,message);

                    } else {
                        mApplication.showSnackBar(current_portfolio,message);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showSnackBar(current_portfolio,getResources().getString(R.string.Server_Error));

                    } else {
                        mApplication.showSnackBar(current_portfolio,getResources().getString(R.string.no_internet));
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

    @Override
    public void onDialogBtnClick(View view) {

    }
}
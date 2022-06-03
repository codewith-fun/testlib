package investwell.client.fragment.payment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.CustomViewPager;


public class FragPrePaymentHome extends Fragment implements ToolbarFragment.ToolbarCallback {
    private AppSession mSession;
    public CustomViewPager mViewPager;
    public FragViewPagerAdapter mPagerAdapter;
    public TabLayout mTabLayout;
    private MainActivity mActivity;
    private View view;
    private ToolbarFragment toolbarFragment;
    private AppApplication mApplication;
    private Bundle bundle;
    private String mUcc;
    public static JSONObject resonsePrePaymentData = new JSONObject();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_pre_payment_home, container, false);

        mSession = AppSession.getInstance(getActivity());

        mViewPager = view.findViewById(R.id.pager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        bundle = getArguments();

        setUpToolBar();
        getDataFromBundle();
        getTransactOrderList();
        return view;
    }

    private void setUpToolBar() {
        toolbarFragment
                = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment
                != null) {
            toolbarFragment
                    .setUpToolBar(getResources().getString(R.string.payment_toolbar_title), true, false, false, false, true, false, false, "");
        }

        toolbarFragment.setCallback(this);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUcc = bundle.getString("ucc_code");
        } else {
            mUcc = mSession.getUCC_CODE();
        }
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_refresh) {
            getTransactOrderList();
        }
    }

    public void updateViewPagerView() {
        List<Fragment> fragList = new ArrayList<>();
        Bundle bundle;
        Fragment fragment = new FragPrePayment();
        bundle = new Bundle();
        bundle.putString("type", "Pending");
        bundle.putString("ucc_code", mUcc);
        fragment.setArguments(bundle);

        Fragment fragment2 = new FragPrePayment();
        bundle = new Bundle();
        bundle.putString("type", "Success");
        bundle.putString("ucc_code", mUcc);
        fragment2.setArguments(bundle);


        fragList.add(fragment);
        fragList.add(fragment2);
        mPagerAdapter = new FragViewPagerAdapter(getChildFragmentManager(), fragList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("Pending");
        mTabLayout.getTabAt(1).setText("Success");

    }


    private void getTransactOrderList() {
        DialogsUtils.showProgressBar(mActivity, false);
        String url = Config.Transact_Order_List;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UCC", mUcc);
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("PaymentStatus", "Y");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                try {
                    DialogsUtils.hideProgressBar();
                    resonsePrePaymentData = response;
                    updateViewPagerView();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }, error -> DialogsUtils.hideProgressBar());

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

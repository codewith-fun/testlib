package investwell.client.fragment.fundpicks.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.CustomViewPager;
import investwell.utils.Utils;

public class FundPicksFragment extends Fragment implements View.OnClickListener {

    private CustomViewPager mViewPager;
    private TabLayout mTabLayout, mFilterTabLayout;
    public List<JSONObject> mSelectedCartsList;
    private FragViewPagerAdapter mPagerAdapter;
    private List<JSONObject> mAllComingList;
    private int mSelected_Position = 0;
    private AppSession mSession;
    private MainActivity mActivity;
    private ProgressDialog mBar;
    private JSONArray mJsonArray;
    private JSONObject mObject;
    public TextView mTvCart;
    public static int mCatFunds = 5;
    private boolean again = true;
    private FragViewPagerAdapter mAdapter;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private String mTabShort[] = {"7D", "15D", "30D", "3M", "6M", "1Y", "2Y", "3Y", "5Y", "10Y"};
    //    private String mTabLong[] = {"7 Days", "15 Days", "30 Days", "3 Months", "6 Months", "1 Year", "2 Years", "3 Years", "5 Years", "10 Years"};
    public ToolbarFragment fragToolBar;
    private ShimmerFrameLayout mShimmerViewContainer;

    private AppApplication mApplication;

    @Override
    public void onResume() {
        super.onResume();
        updateCart();
    /*    if (!mSession.get_fundpick().isEmpty()) {
            updateFundscheme();
        } else {

        }*/
        callFundSchemeApi();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();

            mActivity.setMainVisibility(this, null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_fund_picks, container, false);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_scheme_container);
        errorContentInitializer(view);

        LinearLayout lldesc = view.findViewById(R.id.ll_description);
        TextView tvDesc = view.findViewById(R.id.tv_description);
        if (!TextUtils.isEmpty(mSession.getFundPicksDesc())) {
            tvDesc.setText(mSession.getFundPicksDesc());
            lldesc.setVisibility(View.VISIBLE);
        } else {
            lldesc.setVisibility(View.GONE);

        }
        setUpToolBar();
        mTabShort = getResources().getStringArray(R.array.fund_picks_duration);

        mViewPager = view.findViewById(R.id.funds_viewpager);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout = view.findViewById(R.id.funds_tabs);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        mFilterTabLayout = view.findViewById(R.id.filter_tablayout);
        mFilterTabLayout.setVisibility(View.GONE);
        mViewPager.setSaveFromParentEnabled(false);
        mAllComingList = new ArrayList<>();
        mSelectedCartsList = new ArrayList<>();


        for (int i = 0; i < mTabShort.length; i++) {
            mFilterTabLayout.addTab(mFilterTabLayout.newTab().setText(mTabShort[i]));
        }
       /* mFilterTabLayout.setScrollX(mFilterTabLayout.getWidth());
        mFilterTabLayout.getTabAt(5).select();*/

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mFilterTabLayout.getTabAt(5).select();
                    }
                }, 100);


        return view;
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

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("FundPicksAddToCart")) &&
                Utils.getConfigData(mSession).optString("FundPicksAddToCart").equalsIgnoreCase("Y")) {
            if (fragToolBar != null) {
                fragToolBar.setUpToolBar(mSession.getFundPicks(), true, false, true, false, false, true, false, "");

            }
        } else {
            if (fragToolBar != null) {
                fragToolBar.setUpToolBar(mSession.getFundPicks(), true, false, true, false, false, false, false, "");

            }
        }
    }

    public void updateCart() {
        try {
            mTvCart.setText("0");
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                mTvCart.setVisibility(View.GONE);
            } else {
                mTvCart.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                mTvCart.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callFundSchemeApi() {
        if(mSession.getAddToCartList()!=null && !TextUtils.isEmpty(mSession.getAddToCartList())){
            try {
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                mTvCart.setText("" + jsonArray.length());
                fragToolBar.updateCart(true);
                for (int i = 0; i < jsonArray.length(); i++) {
                    mSelectedCartsList.add(jsonArray.getJSONObject(i));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }}
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        final String url = Config.GET_FUNDPICKS;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    try {

                        if (object.optBoolean("Status")) {
                            JSONArray array = object.getJSONArray("FundPickBroadCategory");
                            viewNoData.setVisibility(View.GONE);
                            String jsonArrray = array.toString();
                            updateFundscheme(jsonArrray);
                        } else {
                            viewNoData.setVisibility(View.VISIBLE);
                            displayServerMessage(object.optString("ServiceMSG"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        displayServerErrorMessage(error);
                    } else {
                        if (error instanceof TimeoutError)
                            displayConnectionTimeOut();
                        else if (error instanceof NoConnectionError)
                            displayNoInternetMessage();
                    }
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void updateFundscheme(String json) {
        try {

            List<Fragment> fragList = new ArrayList<>();
            mJsonArray = new JSONArray(json);
            if (mJsonArray.length()>0) {

                for (int i = 0; i < mJsonArray.length(); i++) {
                    mObject = mJsonArray.getJSONObject(i);
                    mObject.put("defaultvalue", mCatFunds);
                    Bundle bundle = new Bundle();
                    bundle.putString("data", String.valueOf(mObject));
                    Fragment fragment = new FragFundScheme();
                    fragment.setArguments(bundle);
                    fragList.add(fragment);
                }
                if (!isAdded()) return;

                mAdapter = new FragViewPagerAdapter(getChildFragmentManager(), fragList);
                mViewPager.setAdapter(mAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject object = mJsonArray.getJSONObject(i);

                    mTabLayout.getTabAt(i).setText(object.optString("BroadCategory"));


                }
                mTabLayout.setVisibility(View.VISIBLE);
                mFilterTabLayout.setVisibility(View.VISIBLE);
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                mFilterTabLayout.getTabAt(5).select();
                            }
                        }, 100);

            } else {
                viewNoData.setVisibility(View.VISIBLE);
                displayServerMessage("No Data Found");
                mTabLayout.setVisibility(View.GONE);
                again = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        ----------------------------------------
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mFilterTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
//                mFilterTabLayout.getTabAt(position).setText(mTabLong[position]);

                try {

                    mCatFunds = tab.getPosition();
                    mViewPager.getAdapter().notifyDataSetChanged();
                    mTabLayout.setupWithViewPager(mViewPager);
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject object = mJsonArray.getJSONObject(i);
                        mTabLayout.getTabAt(i).setText(object.optString("BroadCategory"));
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                int i = tab.getPosition();
                mFilterTabLayout.getTabAt(i).setText(mTabShort[i]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


//        mTabLayout.notify();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivLeft) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.fl_cart) {
            try {
                if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                    mActivity.displayViewOther(39, null);
                } else {
                    mSession.setGetSchemeData(mAllComingList.toString());
                    mActivity.displayViewOther(4, null);
                }

            } catch (Exception e) {

            }
        }
    }
}

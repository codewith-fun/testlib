package investwell.client.fragment.nfo.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
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
import investwell.utils.Utils;


public class FragNewFundOffers extends Fragment implements View.OnClickListener {

    public List<JSONObject> mSelectedCartsList;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragViewPagerAdapter mAdapter;
    private MainActivity mActivity;
    private AppSession mSession;
    public ToolbarFragment fragToolBar;
    private ProgressDialog mBar;
    private boolean again = true;
    private ImageView cart;
    public TextView mTvCart;
    private ShimmerFrameLayout mShimmerViewContainer;
    private AppApplication mApplication;

    @Override
    public void onResume() {
        super.onResume();
        updateCart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_new_fund_offers, container, false);
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(getActivity());
        mTabLayout = view.findViewById(R.id.nfo_tabs);
        mViewPager = view.findViewById(R.id.nfo_viewpager);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        cart = view.findViewById(R.id.cart_icon);
        view.findViewById(R.id.fl_cart).setOnClickListener(this);


        setUpToolBar();
        mSelectedCartsList = new ArrayList<>();
        getNFOBasket();
        fragToolBar.updateNFOCart(true);
        fragToolBar.updateCart(false);
        return view;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.fl_cart) {
            if (Utils.isNetworkConnected(getActivity())) {

                onCartIconClick();

            } else {
                mApplication.showSnackBar(view, getResources().getString(R.string.no_internet));
            }
        }
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("NFOAddToCart")) &&
                investwell.utils.Utils.getConfigData(mSession).optString("NFOAddToCart").equalsIgnoreCase("Y")) {

            if (fragToolBar != null) {
                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_nfo), true, false, false, false, false, true, false, "");
                cart.setBackgroundResource(R.mipmap.nfo);

            }
        } else {
            if (fragToolBar != null) {
                fragToolBar.setUpToolBar(mSession.getNfo(), true, false, false, false, false, false, false, "");
                cart.setBackgroundResource(R.mipmap.nfo);

            }
        }
    }

    private void setUpTabs(String allData) {
        List<Fragment> fragList = new ArrayList<>();
        ArrayList<JSONObject> openList = new ArrayList<>();

        String[] tabName = getResources().getStringArray(R.array.fund_offers_tab);
        try {
            JSONArray jsonArray = new JSONArray(allData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                openList.add(jsonObject);
            }

            for (int i = 0; i < tabName.length; i++) {
                Fragment fragOpen = new FragOpen();
                Bundle dataBundle = new Bundle();

                if (i == 0) {
                    dataBundle.putString("AllData", openList.toString());
                    dataBundle.putString("Type", "Open Now");
                    fragOpen.setArguments(dataBundle);
                    fragList.add(fragOpen);
                } else {
                    dataBundle.putString("AllData", openList.toString());
                    dataBundle.putString("Type", "Close");
                    fragOpen.setArguments(dataBundle);
                    fragList.add(fragOpen);
                }

            }


            mAdapter = new FragViewPagerAdapter(getChildFragmentManager(), fragList);
            mViewPager.setAdapter(mAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
            for (int i = 0; i < fragList.size(); i++) {

                mTabLayout.getTabAt(i).setText(tabName[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getNFOBasket() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        try {
            String url = Config.NFO_Basket;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("OnlineOption", mSession.getAppType());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("NFOBasketDetail");
                        setUpTabs(jsonArray.toString());
                        again = true;
                    } else {

                        mApplication.showSnackBar(mTabLayout, response.optString("ServiceMSG"));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showSnackBar(mViewPager, getResources().getString(R.string.no_internet));
                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCart() {

        cart.setImageResource(R.mipmap.nfo);
        try {
            mTvCart.setText("0");
            if (mSession.getAddToNFOCartList().isEmpty() || mSession.getAddToNFOCartList().length() == 2) {
                mTvCart.setVisibility(View.GONE);
            } else {
                mTvCart.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToNFOCartList());
                for (int i = 0; i < jsonArray.length(); i++) {
                    mSelectedCartsList.add(jsonArray.getJSONObject(i));
                }
                mTvCart.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onCartIconClick() {
        try {
            if (mSession.getAddToNFOCartList().isEmpty() || mSession.getAddToNFOCartList().length() == 2) {

                mApplication.showSnackBar(mTabLayout, "Add Scheme");
            } else {
                mActivity.displayViewOther(91, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

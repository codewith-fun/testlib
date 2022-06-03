package investwell.client.fragment.schemes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.iw.acceleratordemo.R;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.CustomViewPager;
import investwell.utils.Utils;
import investwell.utils.customView.CustomTextViewRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragRecomendedAddToCart extends Fragment implements View.OnClickListener {
    public CustomViewPager mViewPager;
    public TabLayout mTabLayout;
    public CustomTextViewRegular mTvCart;
    public List<JSONObject> mSelectedCartsList;
    private FragViewPagerAdapter mPagerAdapter;
    private AppSession mSession;
    private List<JSONObject> mAllComingList;
    private int mSekected_Position = 0;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;

    @Override
    public void onResume() {
        super.onResume();
        updateCart();
        updateViewPagerView();
    }

    private BrokerActivity mBrokerActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mBrokerActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mBrokerActivity.getApplication();


        } else if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);

            mApplication = (AppApplication) mActivity.getApplication();

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_basket_add_cart, container, false);
        mSession = AppSession.getInstance(getActivity());
        setUpToolBar();
        mViewPager = view.findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout = view.findViewById(R.id.tabs);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        mViewPager.setSaveFromParentEnabled(false);
        mAllComingList = new ArrayList<>();
        mSelectedCartsList = new ArrayList<>();

        Bundle bundle = getArguments();


        if (bundle != null && bundle.containsKey("matchData")) {
            String allData = bundle.getString("matchData");
            if (bundle.containsKey("selected_position"))
                mSekected_Position = Integer.parseInt(bundle.getString("selected_position"));
            try {
                mSekected_Position = 0;
                JSONObject jsonObject = new JSONObject(allData);
                mAllComingList.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (bundle.containsKey("AllData")) {
            String allData = bundle.getString("AllData");
            if (bundle.containsKey("selected_position"))
                mSekected_Position = Integer.parseInt(bundle.getString("selected_position"));
            try {
                JSONArray jsonArray = new JSONArray(allData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    mAllComingList.add(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
            view.findViewById(R.id.ivRiskProfile).setVisibility(View.VISIBLE);
        }else {
            view.findViewById(R.id.ivRiskProfile).setVisibility(View.GONE);

        }

        view.findViewById(R.id.ivRiskProfile).setOnClickListener(this);
        return view;


    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_recomended_schemes),
                    true, false, false, false, false, true, false, "");
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ivLeft) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (id == R.id.fl_cart) {
            try {
                if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                    mActivity.displayViewOther(39, null);
                } else {
                    // mSession.setGetSchemeData(mAllComingList.toString());
                    mActivity.displayViewOther(4, null);
                }

            } catch (Exception e) {

            }
        } else if (id == R.id.ivRiskProfile) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "showRiskProfile");
            mActivity.displayViewOther(60, bundle);
        }
    }

    private void updateViewPagerView() {
        try {
            JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
            mTvCart.setText("" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                mSelectedCartsList.add(jsonArray.getJSONObject(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<Fragment> fragList = new ArrayList<>();

        for (int i = 0; i < mAllComingList.size(); i++) {
            JSONObject jsonObject = mAllComingList.get(i);
            Fragment fragment = new FragBucket();
            Bundle bundle = new Bundle();
            bundle.putString("type", "" + i);
            bundle.putString("dataObject", jsonObject.toString());
            bundle.putString("selected_position", "" + mSekected_Position);
            fragment.setArguments(bundle);
            fragList.add(fragment);
        }

        mPagerAdapter = new FragViewPagerAdapter(getChildFragmentManager(), fragList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mAllComingList.size(); i++) {
            JSONObject jsonObject = mAllComingList.get(i);
            mTabLayout.getTabAt(i).setText(jsonObject.optString("BasketName"));
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {

                    tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                try {
                    int currentPosition = tab.getPosition();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });
        mViewPager.setCurrentItem(mSekected_Position);
    }


}



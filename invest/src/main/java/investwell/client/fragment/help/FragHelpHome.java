package investwell.client.fragment.help;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.adapter.SocialMediaAdapter;
import investwell.client.flavourtypetwo.fragment.HelpServiceFragment;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.CustomViewPager;
import investwell.utils.Utils;


public class FragHelpHome extends Fragment  {
    private AppSession mSession;
    public CustomViewPager mViewPager;
    public FragViewPagerAdapter mPagerAdapter;
    public TabLayout mTabLayout;
    public MainActivity mActivity;

    public ToolbarFragment fragToolBar;
    private LinearLayout rlSocialMedia;
    private SocialMediaAdapter socialMediaAdapter;
    private RecyclerView rvSocialMedia;
    private JSONArray socialJSONArray;
    private BrokerActivity mBrokerActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
        } else if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mBrokerActivity.setMainVisibility(this, null);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_help_home, container, false);
        socialJSONArray = new JSONArray();
        mViewPager = view.findViewById(R.id.pager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        rlSocialMedia = view.findViewById(R.id.footer_layout);
        rvSocialMedia = view.findViewById(R.id.rv_social_media);
        updateViewPagerView();
        setUpToolBar();
        setUpUiVisibility();
        setLanguageAdapter();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.help_main_screen_title),
                    true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
        }
    }

    private void setUpUiVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("SocialLink")) &&
                Utils.getConfigData(mSession).optString("SocialLink").equalsIgnoreCase("Y")) {
            rlSocialMedia.setVisibility(View.VISIBLE);
        } else {
            rlSocialMedia.setVisibility(View.GONE);


        }
    }

    private void setUpSocialMediaIconVisibility() {

        if (Objects.requireNonNull(Utils.getConfigData(mSession).optJSONArray("SocialLinkList")).length() > 0) {
            socialJSONArray = Utils.getConfigData(mSession).optJSONArray("SocialLinkList");
            ArrayList<JSONObject> langJSONObject = new ArrayList<>();

            for (int i = 0; i < socialJSONArray.length(); i++) {
                JSONObject object = null;
                try {
                    object = socialJSONArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                langJSONObject.add(object);
            }
            socialMediaAdapter.upDateLangList(langJSONObject);
        } else {
            socialMediaAdapter.upDateLangList(new ArrayList<>());
        }
    }

    /*******************************************
     * Method used to set dynamic language items
     *******************************************/

    private void setLanguageAdapter() {


        if (getActivity() == mActivity) {
            socialMediaAdapter = new SocialMediaAdapter(mActivity, new ArrayList<>());
            rvSocialMedia.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
            rvSocialMedia.setItemAnimator(new DefaultItemAnimator());
            rvSocialMedia.setNestedScrollingEnabled(false);
            rvSocialMedia.setAdapter(socialMediaAdapter);
            setUpSocialMediaIconVisibility();
        } else if (getActivity() == mBrokerActivity) {
            socialMediaAdapter = new SocialMediaAdapter(mBrokerActivity, new ArrayList<>());
            rvSocialMedia.setLayoutManager(new LinearLayoutManager(mBrokerActivity, LinearLayoutManager.HORIZONTAL, false));
            rvSocialMedia.setItemAnimator(new DefaultItemAnimator());
            rvSocialMedia.setNestedScrollingEnabled(false);
            rvSocialMedia.setAdapter(socialMediaAdapter);
            setUpSocialMediaIconVisibility();
        }
    }



    public void updateViewPagerView() {
        List<Fragment> fragList = new ArrayList<>();
        Fragment fragment = new FragHelpContactus();
        Fragment fragment2 = new FragHelpFAQs();
        Fragment fragment3 = new FragHelpMore();
        Fragment frag4 = new HelpServiceFragment();

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")) {
            fragList.add(fragment);
            fragList.add(frag4);
            fragList.add(fragment2);
            fragList.add(fragment3);
        }else{
            fragList.add(fragment);
            fragList.add(fragment2);
            fragList.add(fragment3);
        }

        mPagerAdapter = new FragViewPagerAdapter(getChildFragmentManager(), fragList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")) {

            Objects.requireNonNull(mTabLayout.getTabAt(0)).setText(getString(R.string.help_contact_us_title));
            Objects.requireNonNull(mTabLayout.getTabAt(1)).setText(getString(R.string.help_service_title));
            Objects.requireNonNull(mTabLayout.getTabAt(2)).setText(getString(R.string.help_faq_title));
            Objects.requireNonNull(mTabLayout.getTabAt(3)).setText(getString(R.string.help_more_title));
        }else{
            Objects.requireNonNull(mTabLayout.getTabAt(0)).setText(getString(R.string.help_contact_us_title));
            Objects.requireNonNull(mTabLayout.getTabAt(1)).setText(getString(R.string.help_faq_title));
            Objects.requireNonNull(mTabLayout.getTabAt(2)).setText(getString(R.string.help_more_title));

        }

    }


}

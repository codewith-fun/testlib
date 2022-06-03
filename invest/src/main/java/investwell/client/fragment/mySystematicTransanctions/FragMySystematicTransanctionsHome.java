package investwell.client.fragment.mySystematicTransanctions;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.iw.acceleratordemo.R;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.mysip.FragSipStpSwp;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.CustomViewPager;


public class FragMySystematicTransanctionsHome extends Fragment {
    private View view;
    private AppSession mSession;
    private MainActivity mActivity;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayout;
    private ToolbarFragment fragToolBar;
    private RelativeLayout mRlMonth;

    private AppApplication mApplication;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity = (MainActivity) getActivity();
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);



        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_systemetic_transaction, container, false);
        setUpToolBar();
        mViewPager = view.findViewById(R.id.csv_sip_viewpager);
        mTabLayout = view.findViewById(R.id.tl_sip_tabs);
        mRlMonth = view.findViewById(R.id.RlMonth);


        updateViewPagerView();
        return view;
    }


    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.systemetic_investment_txt), true, false, false, false, false, false, false, "");
        }
    }


    public void updateViewPagerView() {
        List<Fragment> fragList = new ArrayList<>();
        Bundle bundle;
        Fragment fragment = new FragSipStpSwp();
        bundle = new Bundle();
        bundle.putString("comingFrom", "systemeticTransaction");
        bundle.putString("type", "SIP");
       /* bundle.putString("month", "" + Month_value);
        bundle.putString("year", "" + year);*/
        fragment.setArguments(bundle);

        Fragment fragment2 = new FragSipStpSwp();
        bundle = new Bundle();
        bundle.putString("comingFrom", "systemeticTransaction");
        bundle.putString("type", "STP");

        fragment2.setArguments(bundle);

        Fragment fragment3 = new FragSipStpSwp();
        bundle = new Bundle();
        bundle.putString("comingFrom", "systemeticTransaction");
        bundle.putString("type", "SWP");
        fragment3.setArguments(bundle);

        fragList.add(fragment);
        fragList.add(fragment2);
        fragList.add(fragment3);
        FragViewPagerAdapter pagerAdapter = new FragViewPagerAdapter(getChildFragmentManager(), fragList);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("SIP");
        mTabLayout.getTabAt(1).setText("STP");
        mTabLayout.getTabAt(2).setText("SWP");

        // mTabLayout.getTabAt(0).setCustomView(R.layout.red);

        mViewPager.addOnPageChangeListener(onViewPageChange);


    }


    ViewPager.OnPageChangeListener onViewPageChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            if (mViewPager.getCurrentItem() == 2) {
                mViewPager.setCurrentItem(2);
            }
        }

        @Override
        public void onPageSelected(final int position) {
            //  mSession.setMySipType(getResources().getString(R.string.my_sip_type_sip_detail));
            switch (position) {
                case 0:
                    // mTvTitle.setText("Mutual Fund");
                    mSession.setMySipType("");
                    mSession.setMySipType(getResources().getString(R.string.my_sip_type_sip_detail));

                    break;

                case 1:
                    mSession.setMySipType("");
                    mSession.setMySipType(getResources().getString(R.string.my_sip_type_stp_detail));

                    break;

                case 2:
                    mSession.setMySipType("");
                    mSession.setMySipType(getResources().getString(R.string.my_sip_type_swp_detail));

                    break;

                case 3:
                    //mTvTitle.setText("Others Asserts");
                    break;

                default:
                    mSession.setMySipType("");
                    mSession.setMySipType(getResources().getString(R.string.my_sip_type_sip_detail));
                    break;
            }
        }


        @Override
        public void onPageScrollStateChanged(int state) {


        }
    };



    public class FragViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> myFragments;

        public FragViewPagerAdapter(FragmentManager fm, List<Fragment> myFrags) {
            super(fm);
            myFragments = myFrags;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return myFragments.get(position);
        }

        @Override
        public int getCount() {
            return myFragments.size();
        }


    }
}
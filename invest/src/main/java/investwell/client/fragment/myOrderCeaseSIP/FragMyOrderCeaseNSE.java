package investwell.client.fragment.myOrderCeaseSIP;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.mysip.FragSipStpSwp;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.CustomViewPager;
import investwell.utils.Utils;


public class FragMyOrderCeaseNSE extends Fragment {
    private View view;
    private AppSession mSession;
    private MainActivity mActivity;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayout;
    private ToolbarFragment fragToolBar;
    private TextView tvYearText, tvYear;
    private ImageView Previous_Year_img, Next_Year_img;
    private String year, month, day;
    private int selected_year, changedYear=5,countYear = 5;
    private Calendar c = Calendar.getInstance();
    RelativeLayout yearChangeLL;
    private String selected_Date;
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
        final View view = inflater.inflate(R.layout.fragment_my_sip_cease_, container, false);
        setUpToolBar();
        mViewPager = view.findViewById(R.id.csv_sip_viewpager);
        mTabLayout = view.findViewById(R.id.tl_sip_tabs);

        yearChangeLL = view.findViewById(R.id.yearChangeLL);
        yearChangeLL.setVisibility(View.VISIBLE);

        Previous_Year_img = view.findViewById(R.id.previous_year);
        Next_Year_img = view.findViewById(R.id.next_year);

        tvYearText = view.findViewById(R.id.tvYearText);

        year = String.valueOf(c.get(Calendar.YEAR));
        month = String.valueOf(c.get(Calendar.MONTH));
        day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

        if(Integer.parseInt(month)<10)
            month = "0"+month;
        if(Integer.parseInt(day)<10)
            month = "0"+day;

        selected_year = Integer.parseInt(year);
        selected_year = selected_year - countYear;

        Previous_Year_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countYear = countYear+changedYear;
                selected_year = selected_year-5;
                updateViewPagerView();
            }
        });

        Next_Year_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countYear = countYear-changedYear;
                selected_year = selected_year+5;
                updateViewPagerView();
            }
        });

        updateViewPagerView();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_title_sip_cease), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
        }
    }

    public void updateViewPagerView() {

        if(countYear == 5)
            Next_Year_img.setVisibility(View.INVISIBLE);
        else
            Next_Year_img.setVisibility(View.VISIBLE);

        selected_Date = day+"/"+month+"/"+selected_year;
        tvYearText.setText("Last " + countYear + " Years");

        List<Fragment> fragList = new ArrayList<>();
        Bundle bundle;
        Fragment fragment = new FragOrderSIPAll();
        bundle = new Bundle();
        bundle.putString("comingFrom","mySipCease");
        bundle.putString("type", "Running");
        bundle.putString("mFromDate", ""+selected_Date);
        fragment.setArguments(bundle);

        Fragment fragment2 = new FragOrderSIPAll();
        bundle = new Bundle();
        bundle.putString("comingFrom","mySipCease");
        bundle.putString("type", "Matured");
        bundle.putString("mFromDate", selected_Date);
        fragment2.setArguments(bundle);

        Fragment fragment3 = new FragOrderSIPAll();
        bundle = new Bundle();
        bundle.putString("comingFrom","mySipCease");
        bundle.putString("type", "CEASED");
        bundle.putString("mFromDate", selected_Date);
        fragment3.setArguments(bundle);

        fragList.add(fragment);
        fragList.add(fragment2);
        fragList.add(fragment3);
        investwell.client.fragment.myOrderCeaseSIP.FragMyOrderCeaseNSE.FragViewPagerAdapter pagerAdapter = new investwell.client.fragment.myOrderCeaseSIP.FragMyOrderCeaseNSE.FragViewPagerAdapter(getChildFragmentManager(), fragList);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("Running");
        mTabLayout.getTabAt(1).setText("Matured");
        mTabLayout.getTabAt(2).setText("CEASED");
        mViewPager.addOnPageChangeListener(onViewPageChange);

    }

    ViewPager.OnPageChangeListener onViewPageChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
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

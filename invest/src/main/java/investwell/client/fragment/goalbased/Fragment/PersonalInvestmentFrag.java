package investwell.client.fragment.goalbased.Fragment;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;


public class PersonalInvestmentFrag extends Fragment {
    public LinearLayout mBtOrder,mBtSkip;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private TextView investment_type, investment_amount, risk_txt, goal_txt;
    private Bundle bundle;
    private TabLayout tabs;
    private ViewPager pager;
    private String mUCC_Code = "";
    private String mCommingFrom = "";
    private ToolbarFragment toolbarFragment;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_investment, container, false);
        tabs = view.findViewById(R.id.tabs);
        pager = view.findViewById(R.id.viewpager_allocation_charts);
        setUpToolBar();
        bundle = getArguments();
        investment_type = view.findViewById(R.id.investment_type);
        investment_amount = view.findViewById(R.id.investment_amount);
        mBtOrder = view.findViewById(R.id.order_now);
        mBtSkip = view.findViewById(R.id.skip);
        risk_txt = view.findViewById(R.id.risk_txt);
        goal_txt = view.findViewById(R.id.goal_txt);

        if (bundle != null) {
            investment_type.setText(bundle.getString("investment_type") + " Amount");
            investment_amount.setText(bundle.getString("Amount"));
            risk_txt.setText(bundle.getString("RiskProfile"));
            goal_txt.setText(bundle.getString("ic_invest_route_goal"));
        }


        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getActivity(), getChildFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
        mUCC_Code = mSession.getUCC_CODE();
        if (mSession.getCLicked()) {
            mBtOrder.setVisibility(View.VISIBLE);
            mBtSkip.setVisibility(View.VISIBLE);
        } else {
            mBtOrder.setVisibility(View.VISIBLE);
            mBtSkip.setVisibility(View.VISIBLE);
        }
        mBtOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bundle.getString("type").equals("coming_from_goal")) {
                    mActivity.displayViewOther(36, bundle);
                } else if (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "coming_from_dashborad");
                    mActivity.displayViewOther(11, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "coming_from_dashborad");
                    mActivity.displayViewOther(36, bundle);
                }


            }
        });

        mBtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bundle.getString("type").equals("coming_from_goal")) {
                    mActivity.displayViewOther(36, null);
                } else if (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "coming_from_dashborad");
                    mActivity.displayViewOther(11, null);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "coming_from_dashborad");
                    mActivity.displayViewOther(36, null);
                }
            }
        });

        try {
            if (Utils.getConfigData(mSession).has("ChooseDIYGoal") && Utils.getConfigData(mSession).optString("ChooseDIYGoal").equals("Y")) {
                mBtSkip.setVisibility(View.VISIBLE);
            }else{
                mBtSkip.setVisibility(View.GONE);
            }
        }catch(Exception e){ }

        return view;
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getString(R.string.goal_summary_personal_investment_plan), true, false, false, false, false, false, false, "");
        }
    }


    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {

                FragScheme schemeFrag = new FragScheme();
                schemeFrag.setArguments(bundle);
                return schemeFrag;

            } else if (position == 1) {

                FragAllocation allocationFrag = new FragAllocation();
                allocationFrag.setArguments(bundle);
                return allocationFrag;
            }
            return null;
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return 2;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:

                    return mContext.getString(R.string.Scheme);
                case 1:
                    return mContext.getString(R.string.Allocation);

                default:
                    return null;
            }
        }

    }

}

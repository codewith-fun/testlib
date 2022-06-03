package investwell.client.fragment.myOrderCeaseSIP;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.CustomViewPager;
import investwell.utils.Utils;

public class FragMyOrderCeaseSIP extends Fragment {
    private View view;
    private AppSession mSession;
    private MainActivity mActivity;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayout;
    private ToolbarFragment fragToolBar;
    private FloatingActionButton mFilterIcon ;
    private Button mCeaseBtn;
    private Spinner mLastDaySpinner, mTransTypeSpinner, mTransStatusSpinner, mOrderTypeSpinner;
    private String[] LastDayValue = {"1 Day" ,"3 Days" , "7 Days"},TransTypeValue, TransStatusValue, OrderTypeValue= {"ALLSIP" ,"STP" , "SWP"};
    private String mLastDay = "7D", mTransType, mTransStatus = "ALL", mUcc, mDaysValue=" (Last 7 Days)", mOrderType = "ALLSIP";
    ArrayList<JSONObject> list = new ArrayList<>();

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
        final View view = inflater.inflate(R.layout.fragment_my_order_cease, container, false);
        setUpToolBar();
        mViewPager = view.findViewById(R.id.csv_sip_viewpager);
        mTabLayout = view.findViewById(R.id.tl_sip_tabs);
        mFilterIcon = view.findViewById(R.id.filter_icon);
        mCeaseBtn = view.findViewById(R.id.cease_btn);
        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
            mCeaseBtn.setVisibility(View.VISIBLE);
            mTransType = "A";
            TransTypeValue = new String[]{"All", "Purchase", "Redemption", "Switch"};
            TransStatusValue = new String[]{"All", "Processed", "Authorized", "Pending", "Rejected"};
        } else {
            mCeaseBtn.setVisibility(View.GONE);
            mTransType = "P";
            TransTypeValue = new String[]{"Purchase", "Sell"};
            TransStatusValue = new String[]{"All", "Valid", "Invalid"};
        }
        OrderTypeValue = new String[]{"SIP", "STP", "SWP"};  // ALLSIP, STP, SWP
        updateViewPagerView();

        mFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialog = inflater.inflate(R.layout.my_order_cease_sip_dialog, null);

                dialogBuilder.setView(dialog);
                final AlertDialog alertDialog = dialogBuilder.create();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                mLastDaySpinner = dialog.findViewById(R.id.lastDay_spinner);
                mTransTypeSpinner = dialog.findViewById(R.id.type_spinner);
                mTransStatusSpinner = dialog.findViewById(R.id.status_spinner);
                mOrderTypeSpinner = dialog.findViewById(R.id.order_spinner);
                setSpinner(list);

                dialog.findViewById(R.id.apply_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateViewPagerView();
                        alertDialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        mCeaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("passkey", AppSession.getInstance(getActivity()).getPassKey());
                mActivity.displayViewOther(122, bundle);
            }
           });
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_my_orders), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));
            }
        }
    }

    public void updateViewPagerView() {
        List<Fragment> fragList = new ArrayList<>();
        Bundle bundle;
        Fragment fragment = new FragOrderSIPAll();
        bundle = new Bundle();
        bundle.putString("comingFrom","myOder");
        bundle.putString("type", "SIP");
        bundle.putString("OnlineOption", mSession.getAppType());
        bundle.putString("LastDay", mLastDay);
        bundle.putString("TranType", mTransType);
        bundle.putString("TranStatus", mTransStatus);
        bundle.putString("OrderType", mOrderType);
        fragment.setArguments(bundle);

        Fragment fragment2 = new FragOrderSIPAll();
        bundle = new Bundle();
        bundle.putString("comingFrom","myOder");
        bundle.putString("type", "All");
        bundle.putString("OnlineOption", mSession.getAppType());
        bundle.putString("LastDay", mLastDay);
        bundle.putString("TranType", mTransType);
        bundle.putString("TranStatus", mTransStatus);
        bundle.putString("OrderType", mOrderType);
        fragment2.setArguments(bundle);

        fragList.add(fragment);
        fragList.add(fragment2);
        FragViewPagerAdapter pagerAdapter = new FragViewPagerAdapter(getChildFragmentManager(), fragList);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("SIP Orders");
        mTabLayout.getTabAt(1).setText("All Orders");
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
    private void setSpinner(ArrayList<JSONObject> mlist) {
        ArrayList<String> member = new ArrayList<>();
        for (int i = 0; i < mlist.size(); i++) {
            member.add(mlist.get(i).optString("InvestorName"));
        }

        ArrayAdapter typeAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, TransTypeValue);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTransTypeSpinner.setAdapter(typeAdapter);


        ArrayAdapter statusAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, TransStatusValue);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTransStatusSpinner.setAdapter(statusAdapter);

        ArrayAdapter orderTypeAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, OrderTypeValue);
        orderTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOrderTypeSpinner.setAdapter(orderTypeAdapter);

        ArrayAdapter lastDayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, LastDayValue);
        lastDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLastDaySpinner.setAdapter(lastDayAdapter);


        mTransTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {

                    case 0:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransType = "A";
                        } else {
                            mTransType = "P";
                        }

                        break;


                    case 1:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransType = "P";
                        } else {
                            mTransType = "R";
                        }
                        break;

                    case 2:
                        mTransType = "R";
                        break;

                    case 3:
                        mTransType = "S";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mTransStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {

                    case 0:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransStatus = "ALL";
                        } else {
                            mTransStatus = "ALL";
                        }
                        break;

                    case 1:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransStatus = "A";
                        } else {
                            mTransStatus = "Valid";
                        }
                        break;

                    case 2:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransStatus = "C";
                        } else {
                            mTransStatus = "Invalid";
                        }
                        break;

                    case 3:
                        mTransStatus = "P";
                        break;

                    case 4:
                        mTransStatus = "R";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mLastDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDaysValue = " (Last "+adapterView.getSelectedItem().toString()+")";
                switch (i){
                    case 0:
                        System.out.println("---1D---");
                        mLastDay = "1D";
                        break;

                    case 1:
                        System.out.println("---3D---");
                        mLastDay = "3D";
                        break;

                    case 2:
                        System.out.println("---7D---");
                        mLastDay = "7D";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mOrderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //mDaysValue = " (Last "+adapterView.getSelectedItem().toString()+")";
                switch (i){
                    case 0:
                        mOrderType = "ALLSIP";
                        break;

                    case 1:
                        mOrderType = "STP";
                        break;

                    case 2:
                        mOrderType = "SWP";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }
}

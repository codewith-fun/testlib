package investwell.client.fragment.others;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.utils.customView.CustomDialog;


public class MyInsuranceFragment extends Fragment implements ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack{

    private TabLayout mTabLayout;
    private ViewPager mPager;
    private String[] tabtext ={"General Insurance","Life Insurance"} ;
    private MainActivity mActivty;
    private View view;
    private ToolbarFragment fragToolBar;
    private CustomDialog customDialog;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivty = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_my_insurance, container, false);
        setInitializer();
        setUpToolBar();
        updateViewPagerView(0);
        return view;
    }

    private void setInitializer() {
        mActivty.setMainVisibility(this, null);
        mTabLayout = view.findViewById(R.id.tabLayout);
        mPager = view.findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(mPager.getChildCount());
        mPager.setSaveFromParentEnabled(false);
        tabtext=getResources().getStringArray(R.array.my_insurance_tab);
        customDialog = new CustomDialog(this);
    }
    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_my_insurance),true, false, false, false,false,false,true,"");
            fragToolBar.setCallback(this);
        }

    }
    private void updateViewPagerView(int showScreenNo) {
        List<Fragment> fragList = new ArrayList<>();

        switch (showScreenNo) {
            case 0:

                Fragment generalinsurance = new GeneralInsurance();
                fragList.add(generalinsurance);

                Fragment lifeinsurance = new LifeInsurance();
                fragList.add(lifeinsurance);
                break;


        }


        FragViewPagerAdapter mPagerAdapter = new FragViewPagerAdapter(mActivty.getSupportFragmentManager(), fragList);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mPager);
        setupTabIcons(fragList.size());

    }

    private void setupTabIcons(int item_no) {
        String[] tabstext = null;
        tabstext = tabtext;

        for (int i = 0; i < item_no; i++) {

            mTabLayout.getTabAt(i).setText(tabstext[i]);


/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mTabLayout.getTabAt(i)).setText(tabstext[i]);
            }*/

        }

    }

    private void showMyInsuranceInfo() {
        customDialog.showDialog(getActivity(), getResources().getString(R.string.fatca_instruction),
                getResources().getString(R.string.my_insurance_header_txt),
                getResources().getString(R.string.text_ok), "", true, false);
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_fatca_info) {
            showMyInsuranceInfo();
        }
    }

    @Override
    public void onDialogBtnClick(View view) {
        if (view.getId() == R.id.btDone) {
            //Todo nothing
        }
    }
}

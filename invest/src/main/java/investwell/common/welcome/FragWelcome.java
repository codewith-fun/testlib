package investwell.common.welcome;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;

import investwell.client.activity.LoginActivity;
import investwell.client.activity.UserTypesActivity;
import investwell.client.activity.WelcomeActivity;
import investwell.utils.AppSession;
import investwell.utils.customView.CircleIndicator;
import investwell.utils.Utils;


public class FragWelcome extends Fragment implements View.OnClickListener {
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private AppSession mSession;
    private JSONArray jsonArray;
    private WelcomeActivity welcomeActivity;
    private TextView tvSkip;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.welcomeActivity = (WelcomeActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_welcome, container, false);
        mSession = AppSession.getInstance(welcomeActivity);
        mViewPager = view.findViewById(R.id.viewpager);
        final CircleIndicator indicator = view.findViewById(R.id.indicator);
        final TextView btstarted = view.findViewById(R.id.btn_get_started);
        final TextView btViewPortfolio = view.findViewById(R.id.btn_view_portfolio);
        jsonArray = Utils.getConfigData(mSession).optJSONArray("SliderList");
        mAdapter = new ViewPagerAdapter(welcomeActivity.getSupportFragmentManager(), mViewPager);
        mViewPager.setAdapter(mAdapter);
        indicator.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(jsonArray.length());
        Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.down_from_top);
        view.findViewById(R.id.iv_app_logo).startAnimation(slideDown);
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.item_animation_fall_down);
        btstarted.startAnimation(slideUp);
        Animation animationLeftToRight = AnimationUtils.loadAnimation(getActivity(), R.anim.item_animation_fall_down);
        btViewPortfolio.startAnimation(animationLeftToRight);
        btstarted.setOnClickListener(this);
        btViewPortfolio.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        if (id == R.id.btn_view_portfolio) {
            intent = new Intent(welcomeActivity, LoginActivity.class);
            mSession.setHasFirstTimeAppIntroLaunched(true);
            startActivity(intent);
        } else if (id == R.id.btn_get_started) {
            intent = new Intent(welcomeActivity, UserTypesActivity.class);
            mSession.setHasFirstTimeAppIntroLaunched(true);
            startActivity(intent);
        }
    }
}


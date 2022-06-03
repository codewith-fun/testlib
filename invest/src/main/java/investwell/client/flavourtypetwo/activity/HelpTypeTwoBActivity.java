package investwell.client.flavourtypetwo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.adapter.FragViewPagerAdapter;
import investwell.client.adapter.SocialMediaAdapter;
import investwell.client.fragment.help.FragHelpContactus;
import investwell.client.fragment.help.FragHelpFAQs;
import investwell.client.fragment.help.FragHelpMore;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;
import investwell.utils.CustomViewPager;
import investwell.utils.Utils;

public class HelpTypeTwoBActivity extends BaseActivity implements View.OnClickListener {
    private AppSession mSession;
    public CustomViewPager mViewPager;
    public FragViewPagerAdapter mPagerAdapter;
    public TabLayout mTabLayout;
    private ImageView ivToolBarBack;
    private AppApplication mApplication;
    private RelativeLayout rlSocialMedia;
    private SocialMediaAdapter socialMediaAdapter;
    private RecyclerView rvSocialMedia;
    private JSONArray socialJSONArray;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_type_two_b);
        initializer();
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
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
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

    private void initializer() {
        mSession = AppSession.getInstance(this);
        mApplication = (AppApplication) this.getApplication();
        socialJSONArray = new JSONArray();
        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tabLayout);
        rlSocialMedia = findViewById(R.id.footer_layout);
        rvSocialMedia = findViewById(R.id.rv_social_media);
        ivToolBarBack = findViewById(R.id.iv_back_my_assets);
        findViewById(R.id.tvWhatsApp).setOnClickListener(this);
        findViewById(R.id.tvWitter).setOnClickListener(this);
        findViewById(R.id.tvFacebook).setOnClickListener(this);
        findViewById(R.id.linkedin).setOnClickListener(this);
        findViewById(R.id.tvYoutube).setOnClickListener(this);
        updateViewPagerView();
        setUpUiVisibility();
        setLanguageAdapter();
        ivToolBarBack.setOnClickListener(this);

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
        if (Utils.getConfigData(mSession).optJSONArray("SocialLinkList").length() > 0) {
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
            socialMediaAdapter.upDateLangList(new ArrayList<JSONObject>());
        }
    }

    /*******************************************
     * Method used to set dynamic language items
     *******************************************/

    private void setLanguageAdapter() {

        socialMediaAdapter = new SocialMediaAdapter(HelpTypeTwoBActivity.this, new ArrayList<JSONObject>());
        rvSocialMedia.setLayoutManager(new LinearLayoutManager(HelpTypeTwoBActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvSocialMedia.setItemAnimator(new DefaultItemAnimator());
        rvSocialMedia.setNestedScrollingEnabled(false);
        rvSocialMedia.setAdapter(socialMediaAdapter);
        setUpSocialMediaIconVisibility();

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.iv_back_my_assets) {
            super.onBackPressed();
        }
    }

    public void updateViewPagerView() {
        List<Fragment> fragList = new ArrayList<>();
        Fragment fragment = new FragHelpContactus();
        Fragment fragment2 = new FragHelpFAQs();
        Fragment fragment3 = new FragHelpMore();

        fragList.add(fragment);
        fragList.add(fragment2);
        fragList.add(fragment3);
        mPagerAdapter = new FragViewPagerAdapter(getSupportFragmentManager(), fragList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mTabLayout.getTabAt(0)).setText(getString(R.string.help_contact_us_title));
            Objects.requireNonNull(mTabLayout.getTabAt(1)).setText(getString(R.string.help_faq_title));
            Objects.requireNonNull(mTabLayout.getTabAt(2)).setText(getString(R.string.help_more_title));
        }


    }


}

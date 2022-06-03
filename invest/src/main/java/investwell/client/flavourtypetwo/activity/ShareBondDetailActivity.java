package investwell.client.flavourtypetwo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.flavourtypetwo.adapter.ShareBondDetailAdapter;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;

public class ShareBondDetailActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rvShareBondDetail;
    private Bundle bundle;
    private ShareBondDetailAdapter shareBondDetailAdapter;
    private AppApplication mApplication;
    private ImageView ivToolBarBackIcon;
    private ShimmerFrameLayout shimmerFrameLayout;
    private CardView cv_dashboard_noData;
    private TextView mApplicantName, mtvMarketValue, mtvPurchaseCost, mtvDayChnage, mtvGain, mtvCagr;
    private AppSession mSession;
    private LinearLayout llShareBonDetail;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bond_detail);
        initializer();
        setShareBondRecycleAdapter();
        getDataFromBundle();
        setListeners();
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
        mApplication = (AppApplication) this.getApplication();
        mSession = AppSession.getInstance(ShareBondDetailActivity.this);
        bundle = getIntent().getExtras();
        ivToolBarBackIcon = findViewById(R.id.iv_back_my_assets);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        cv_dashboard_noData = findViewById(R.id.cv_dashboard_noData);
        mApplicantName = findViewById(R.id.tv_user_portfolio_name);
        mtvMarketValue = findViewById(R.id.tv_market_value);
        mtvPurchaseCost = findViewById(R.id.tv_purchase_cost_value);
        mtvGain = findViewById(R.id.tv_gain_value);
        mtvCagr = findViewById(R.id.tv_cagr_value);
        rvShareBondDetail = findViewById(R.id.rv_share_bond_detail);
        llShareBonDetail = findViewById(R.id.ll_share_bond_detail_container);
        llShareBonDetail.setVisibility(View.GONE);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("JsonData")) {
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("JsonData"));
                mApplicantName.setText(jsonObject.optString("ApplicantName"));
                mtvMarketValue.setText(jsonObject.optString("CurrentValue"));
                mtvPurchaseCost.setText(jsonObject.optString("InitialValue"));
                /* mtvDayChnage.setText(jsonObject.optString("WeightedDays"));*/
                mtvGain.setText(jsonObject.optString("Gain"));
                mtvCagr.setText(jsonObject.optString("CAGR"));
                JSONArray jsonArray = jsonObject.optJSONArray("ShareDetail");
                setShareBondDetailRecyclerData(jsonArray);
                llShareBonDetail.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            llShareBonDetail.setVisibility(View.GONE);
        }
    }

    private void setShareBondDetailRecyclerData(JSONArray ShareDetail) {


        if (ShareDetail.length() < 0) {
            cv_dashboard_noData.setVisibility(View.VISIBLE);

        } else {
            cv_dashboard_noData.setVisibility(View.GONE);
            ArrayList<JSONObject> list = new ArrayList<>();
            try {
                for (int i = 0; i < ShareDetail.length(); i++) {
                    JSONObject jsonObject = ShareDetail.optJSONObject(i);
                    list.add(jsonObject);
                }
                shareBondDetailAdapter.updateList(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setShareBondRecycleAdapter() {
        rvShareBondDetail.setHasFixedSize(true);
        rvShareBondDetail.setLayoutManager(new LinearLayoutManager(ShareBondDetailActivity.this, LinearLayoutManager.VERTICAL, false));
        shareBondDetailAdapter = new ShareBondDetailAdapter(ShareBondDetailActivity.this, new ArrayList<JSONObject>());
        rvShareBondDetail.setNestedScrollingEnabled(false);
        rvShareBondDetail.setAdapter(shareBondDetailAdapter);
    }

    private void setListeners() {
        ivToolBarBackIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back_my_assets) {
            onBackPressed();
        }
    }
}

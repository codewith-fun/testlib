package investwell.client.fragment.portfolio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.MainActivity;

public class PortfolioDetailCategoryWise extends Fragment implements View.OnClickListener {

    private JSONObject mObject;
    private IndicatorAdapter indicatorAdapter;
    private View view;
    private View mViewTopCard;
    private TextView mTvCurrentVal, mTvInvestVal, mTvGain, mTvReturn, mTvAbs, mTvDiv,textView28;
    private ImageView gain_arrow, cagr_arrow;
    private RecyclerView rvData;
    private String mCid = "", applicantName = "";
    private MainActivity mActivity;
private String mCat="";
    public static PortfolioDetailCategoryWise newInstance() {
        return new PortfolioDetailCategoryWise();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the  for this fragment
        view = inflater.inflate(R.layout.fragment_portfolio_detail_categorywise, container, false);
        initializer();
        getDataFromBundle();
        setTopCardData();
        setIndicatorAdapter();
        getPortfolioSchemeList();
        textView28.setText(mCat);
        return view;
    }

    private void getDataFromBundle() {
        Bundle bundle = getArguments();
        try {
            if (bundle != null) {
                mObject = new JSONObject(bundle.getString("portfolioDetailData"));
                mCid = bundle.getString("cid");
                applicantName = bundle.getString("ApplicantName");
                mCat=mObject.optString("Category");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializer() {
        mViewTopCard = view.findViewById(R.id.content_portfolio_top_tabs);
        mTvDiv = mViewTopCard.findViewById(R.id.textView37);
        mTvAbs = mViewTopCard.findViewById(R.id.textView40);
        mTvReturn = mViewTopCard.findViewById(R.id.cagr);
        mTvGain = mViewTopCard.findViewById(R.id.gain);
        mTvInvestVal = mViewTopCard.findViewById(R.id.purchase_cost);
        mTvCurrentVal = mViewTopCard.findViewById(R.id.tv_market_value);
        gain_arrow = mViewTopCard.findViewById(R.id.gain_arrow);
        cagr_arrow = mViewTopCard.findViewById(R.id.cagr_arrow);
        rvData = view.findViewById(R.id.rv_schemes_portfolio);
        textView28=mViewTopCard.findViewById(R.id.textView28);
    }

    private void setIndicatorAdapter() {
        indicatorAdapter = new IndicatorAdapter(mActivity, new ArrayList<JSONObject>(), mCid);
        rvData.setAdapter(indicatorAdapter);
        rvData.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvData.setNestedScrollingEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    private void setTopCardData() {
        mTvCurrentVal.setText(getString(R.string.Rs) +mObject.optString("CurrentValue") );
        mTvInvestVal.setText(getString(R.string.Rs) + mObject.optString("PurchaseCost"));
        String input = mObject.optString("Gain");
        boolean isFound = input.indexOf("-") != -1 ? true : false; //true
        if (!TextUtils.isEmpty(input)) {
            mTvGain.setVisibility(View.VISIBLE);
            mViewTopCard.findViewById(R.id.tv_label_gain).setVisibility(View.VISIBLE);
        } else {
            mTvGain.setVisibility(View.GONE);
            mViewTopCard.findViewById(R.id.tv_label_gain).setVisibility(View.GONE);
        }
        if (isFound) {
            input = input.substring(0, 1) + mActivity.getString(R.string.Rs) + " " + input.substring(1, input.length());
            mTvGain.setText(input);
        } else {

            mTvGain.setText(getString(R.string.Rs) + mObject.optString("Gain"));
        }

        mTvReturn.setText(mObject.optString("CAGR") + "%");
        mTvAbs.setText(mObject.optString("AbsReturn") + "%");
        if (!TextUtils.isEmpty(mObject.optString("Dividend"))) {
            mTvDiv.setText(getString(R.string.Rs) + mObject.optString("Dividend"));
            mTvDiv.setVisibility(View.VISIBLE);
            mViewTopCard.findViewById(R.id.tv_label_dividend).setVisibility(View.VISIBLE);
        } else {
            mTvDiv.setText(getString(R.string.Rs) + mObject.optString("Dividend"));
            mTvDiv.setVisibility(View.GONE);
            mViewTopCard.findViewById(R.id.tv_label_dividend).setVisibility(View.GONE);
        }
        if (mTvGain.getText().toString().contains("-")) {
            mTvGain.setTextColor(ContextCompat.getColor(mActivity, R.color.colorRed));
            gain_arrow.setBackgroundResource(R.drawable.menu_down);
        } else {
            mTvGain.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
            gain_arrow.setBackgroundResource(R.drawable.menu_up);
        }
        if (mTvReturn.getText().toString().contains("-")) {
            cagr_arrow.setBackgroundResource(R.drawable.menu_down);
            mTvReturn.setTextColor(ContextCompat.getColor(mActivity, R.color.colorRed));
        } else {
            cagr_arrow.setBackgroundResource(R.drawable.menu_up);
            mTvReturn.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
        }
        if (mTvAbs.getText().toString().contains("-")) {

            mTvAbs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorRed));
        } else {

            mTvAbs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
        }

    }

    private void getPortfolioSchemeList() {
        ArrayList<JSONObject> mAdapterItemData = new ArrayList<>();
        try {
            JSONArray mSchemeCatArray = mObject.getJSONArray("SchemeDetails");
            for (int i = 0; i < mSchemeCatArray.length(); i++) {
                JSONObject object = mSchemeCatArray.getJSONObject(i);
                mAdapterItemData.add(object);
            }

            indicatorAdapter.updateList(mAdapterItemData, applicantName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {

    }
}

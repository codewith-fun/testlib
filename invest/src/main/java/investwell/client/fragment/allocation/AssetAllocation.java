package investwell.client.fragment.allocation;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.AssetAllocationAdapter;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

import static com.github.mikephil.charting.animation.Easing.EasingOption.EaseInOutQuad;


public class AssetAllocation extends Fragment {

    private PieChart mAssetChart;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private Bundle bundle;
    private String url, bid, cid, passkey;
    private String[] Asset, Amount, Percentage;
    private AppSession mSession;
    private RecyclerView mAssetRecycle;
    private AssetAllocationAdapter assetAllocationAdapter;
    private MainActivity mActivity;
    private AppApplication mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_asset_allocation, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();
        bundle = getArguments();
        mAssetChart = view.findViewById(R.id.asset_chart);
        mAssetRecycle = view.findViewById(R.id.asset_recycle);
        mAssetRecycle.setHasFixedSize(true);
        mAssetRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            assetAllocationAdapter = new AssetAllocationAdapter(getActivity(), new ArrayList<JSONObject>());
        mAssetRecycle.setAdapter(assetAllocationAdapter);


        bid = AppConstants.APP_BID;
        passkey = mSession.getPassKey();

        if (bundle != null && bundle.containsKey("cid")) {
            cid = bundle.getString("cid");
        } else {
            cid = mSession.getCID();
        }
        setchartfeature();
        if (AppApplication.asset_allocation.isEmpty()) {
            AllocationbyAsset(bid, passkey, cid);
        } else {
            setData();
        }
        return view;
    }

    private void setchartfeature() {

        mAssetChart.setUsePercentValues(true);
        mAssetChart.getDescription().setEnabled(false);
        mAssetChart.setExtraOffsets(5, 10, 5, 5);

        mAssetChart.setDragDecelerationFrictionCoef(0.95f);
        mAssetChart.setCenterText(generateCenterSpannableText());
        mAssetChart.setDrawHoleEnabled(true);
        mAssetChart.setHoleColor(Color.WHITE);
        mAssetChart.setTransparentCircleColor(Color.WHITE);
        mAssetChart.setTransparentCircleAlpha(110);
        mAssetChart.setHoleRadius(65f);
        mAssetChart.setTransparentCircleRadius(60f);
        mAssetChart.setDrawCenterText(true);
        mAssetChart.setRotationAngle(0);
        mAssetChart.setRotationEnabled(true);
        mAssetChart.setHighlightPerTapEnabled(true);
        mAssetChart.getLegend().setWordWrapEnabled(true);


        Legend l = mAssetChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            l.setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor));
        } else {
            l.setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor));
        }


        l.setYEntrySpace(0f);
        l.setYOffset(8f);


        mAssetChart.setEntryLabelColor(Color.TRANSPARENT);
        mAssetChart.getLegend().setWordWrapEnabled(true);
        mAssetChart.setEntryLabelTextSize(12f);
    }

    private void AllocationbyAsset(String bid, String passkey, String cid) {
        DialogsUtils.showProgressBar(getActivity(), false);
        url = Config.PAllocation;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", bid);
            jsonObject.put("Passkey", passkey);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    AppApplication.asset_allocation = response.toString();
                    setData();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                    }
                }
            });

             jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setData() {

        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(AppApplication.asset_allocation);
            if (response.optString("Status").equalsIgnoreCase("True")) {
                JSONArray jsonArray = response.optJSONArray("PortfolioAllocationDetail");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    list.add(jsonObject1);
                }
                DrawAssetAllocation(list);

            } else {
                Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void DrawAssetAllocation(ArrayList<JSONObject> list) {
        int mTotalPrice = 0;
        float mAllocation = 0.0f;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        Asset = new String[list.size()];
        Amount = new String[list.size()];
        Percentage = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = list.get(i);
            Asset[i] = jsonObject.optString("Asset");
            Amount[i] = jsonObject.optString("Amount");
            Percentage[i] = jsonObject.optString("HoldingPercentage");
            entries.add(new PieEntry((float) ((Double.parseDouble(Amount[i].replace(",", ""))) + Double.parseDouble(Amount[i].replace(",", "")) / 5), Asset[i]));
        }
        for (int i = 0; i < list.size(); i++) {
            mTotalPrice += Integer.parseInt(list.get(i).optString("Amount").replaceAll(",", ""));
            mAllocation += Float.parseFloat(list.get(i).optString("HoldingPercentage").replaceAll(",", ""));

        }
        JSONObject jsonObjectTotal = new JSONObject();
        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

        try {

            jsonObjectTotal.put("Asset", "Total");

            String num = NumberFormat.getNumberInstance(Locale.US).format(mTotalPrice).split("//.")[0];
            jsonObjectTotal.put("Amount","Rs."+num);
            jsonObjectTotal.put("HoldingPercentage", NumberFormat.getNumberInstance(Locale.US).format(mAllocation));



            list.add(jsonObjectTotal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assetAllocationAdapter.updateList(list);
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        //  data.setValueTypeface(mTfLight);


        mAssetChart.setData(data);
        mAssetChart.setRotationEnabled(false);
        mAssetChart.animateY(1400, EaseInOutQuad);
        // undo all highlights
        mAssetChart.highlightValues(null);

        mAssetChart.invalidate();


    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString(" Asset Allocation ");
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 17, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 17, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 17, 0);
       /* s.setSpan(new RelativeSizeSpan(.8f), 16, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 18, s.length(), 0);
        */
        return s;
    }
}

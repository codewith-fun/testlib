package investwell.client.fragment.allocation;

import android.content.Context;
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
import android.widget.LinearLayout;
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

import org.apmem.tools.layouts.FlowLayout;
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
import investwell.client.adapter.CategoryAllocationAdapter;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.piechart.others.AnimatedPieView;

import static com.github.mikephil.charting.animation.Easing.EasingOption.EaseInOutQuad;


public class CategoryAllocation extends Fragment {

    private AppSession mSession;
    private Bundle bundle;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String url, bid, cid, passkey;
    private AnimatedPieView mAnimatedPieView;
    private FlowLayout mViewGroup;
    private String[] amount, Objective;
    private PieChart mCategoryChart;
    private RecyclerView mCategoryRecycle;
    private CategoryAllocationAdapter categoryAllocationAdapter;
    private MainActivity mActivity;
    private AppApplication mApplication;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_graphs, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();

        mAnimatedPieView = view.findViewById(R.id.animatedPieView);
        mCategoryChart = view.findViewById(R.id.category_chart);

        mViewGroup = view.findViewById(R.id.ll_legends);
        mCategoryRecycle = view.findViewById(R.id.category_recycle);
        mCategoryRecycle.setHasFixedSize(true);


        mCategoryRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mCategoryRecycle.addItemDecoration(new DividerItemDecoration(mCategoryRecycle.getContext(),LinearLayoutManager.VERTICAL));
        categoryAllocationAdapter = new CategoryAllocationAdapter(getActivity(), new ArrayList<JSONObject>());
        mCategoryRecycle.setAdapter(categoryAllocationAdapter);

        bundle = getArguments();
        bid = AppConstants.APP_BID;
        passkey = mSession.getPassKey();

        if (bundle != null && bundle.containsKey("cid")) {
            cid = bundle.getString("cid");
        } else {
            cid = mSession.getCID();
        }


        setchartfeature();
        if (AppApplication.category_allocation.isEmpty()) {
            AllocationbyCategory(bid, passkey, cid);
        } else {
            setData();
        }
        return view;
    }

    private void setchartfeature() {

        mCategoryChart.setUsePercentValues(true);
        mCategoryChart.getDescription().setEnabled(false);
        mCategoryChart.setExtraOffsets(5, 10, 5, 5);

        mCategoryChart.setDragDecelerationFrictionCoef(0.95f);
        mCategoryChart.setCenterText(generateCenterSpannableText());
        mCategoryChart.setDrawHoleEnabled(true);
        mCategoryChart.setHoleColor(Color.WHITE);
        mCategoryChart.setTransparentCircleColor(Color.WHITE);
        mCategoryChart.setTransparentCircleAlpha(110);
        mCategoryChart.setHoleRadius(65f);
        mCategoryChart.setTransparentCircleRadius(60f);
        mCategoryChart.setDrawCenterText(true);
        mCategoryChart.setRotationAngle(0);
        mCategoryChart.setRotationEnabled(true);
        mCategoryChart.setHighlightPerTapEnabled(true);

        Legend l = mCategoryChart.getLegend();
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


        mCategoryChart.setEntryLabelColor(Color.TRANSPARENT);
        mCategoryChart.getLegend().setWordWrapEnabled(true);
        mCategoryChart.setEntryLabelTextSize(12f);
    }

    private void AllocationbyCategory(String mBid, String mPasskey, String mCid) {
        DialogsUtils.showProgressBar(getActivity(), false);
        url = Config.Allocation_Category;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mPasskey);
            jsonObject.put("Bid", mBid);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    AppApplication.category_allocation = response.toString();
                    setData();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message",false,true);
                    } else{
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message",false,true);
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

    private void DrawCategoryChart(ArrayList<JSONObject> list) {
        int mTotalPrice = 0;
        float mAllocation = 0.0f;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        amount = new String[list.size()];
        Objective = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = list.get(i);
            amount[i] = jsonObject.optString("Amount");
            Objective[i] = jsonObject.optString("Objective");
            entries.add(new PieEntry((float) (( Double.parseDouble(amount[i].replace(",", ""))) + Double.parseDouble(amount[i].replace(",", "")) / 5), Objective[i]));
        }
        for (int i = 0; i < list.size(); i++) {
            mTotalPrice += Integer.parseInt(list.get(i).optString("Amount").replaceAll(",", ""));
            mAllocation += Float.parseFloat(list.get(i).optString("HoldingPercentage").replaceAll(",", ""));

        }
        JSONObject jsonObjectTotal = new JSONObject();
        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

        try {

            String num = NumberFormat.getNumberInstance(Locale.US).format(mTotalPrice).split("//.")[0];
            jsonObjectTotal.put("Objective", "Total");
            jsonObjectTotal.put("Amount", "Rs."+num);
            jsonObjectTotal.put("HoldingPercentage", NumberFormat.getNumberInstance(Locale.US).format(mAllocation));
            list.add(jsonObjectTotal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        categoryAllocationAdapter.updateList(list);
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


        if (list.size() <= 3) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCategoryChart.getLayoutParams();
            layoutParams.height = dpToPx(300, getActivity());
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mCategoryChart.setLayoutParams(layoutParams);
        }


        mCategoryChart.setData(data);
        mCategoryChart.setRotationEnabled(false);
        // undo all highlights
        mCategoryChart.animateY(1400, EaseInOutQuad);
        mCategoryChart.highlightValues(null);

        mCategoryChart.invalidate();
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Category Allocation");
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 19, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 19, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 19, 0);
        /*s.setSpan(new RelativeSizeSpan(.8f), 19, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 18, s.length(), 0);
        */
        return s;
    }

    private void setData() {
        int mTotalPrice = 0;
        float mAllocation = 0.0f;
        try {
            JSONObject response = new JSONObject(AppApplication.category_allocation);

            ArrayList<JSONObject> list = new ArrayList<>();
            if (response.optString("Status").equalsIgnoreCase("True")) {
                JSONArray jsonArray = response.optJSONArray("AllocationCategoryDetail");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        list.add(jsonObject1);

                    }
                }
                DrawCategoryChart(list);

            } else {
                Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

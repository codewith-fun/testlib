package investwell.client.fragment.others;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import investwell.client.adapter.FundAllocationAdapter;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class FundAllocation extends Fragment implements OnChartValueSelectedListener {
    private BarChart mFundChart;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private Bundle bundle;
    private AppSession mSession;
    private String bid, cid, passkey, url;
    private String[] FundName, SchemeName, CurrentValue, Fcode;
    XAxis xAxis;
    YAxis leftAxis;
    private int count = 0;
    private ImageView mFundBtn;
    private RecyclerView mFundRecycle;
    private FundAllocationAdapter fundAllocationAdapter;
    private TextView mName;
    private MainActivity mActivity;
    private AppApplication mApplication;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_fund_allocation, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();

        bundle = getArguments();
        mFundChart = view.findViewById(R.id.fund_chart);
        mFundBtn = view.findViewById(R.id.fund_btn);
        mName = view.findViewById(R.id.name);
        mFundRecycle = view.findViewById(R.id.fund_recycle);
        mFundRecycle.setHasFixedSize(true);
        mFundRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        fundAllocationAdapter = new FundAllocationAdapter(getActivity(), new ArrayList<>());
        mFundRecycle.setAdapter(fundAllocationAdapter);
        bid = AppConstants.APP_BID;
        passkey = mSession.getPassKey();
        if (bundle != null && bundle.containsKey("cid")) {
            cid = bundle.getString("cid");
        } else {
            cid = mSession.getCID();
        }
        setChartFeature();
        if (AppApplication.fund_allocation.isEmpty()) {
            getFundAllocation(bid, passkey, cid);
        } else {
            setData();
        }

        mFundBtn.setOnClickListener(view1 -> {
            mFundChart.clear();
            getFundAllocation(bid, passkey, cid);
            mFundBtn.setVisibility(View.INVISIBLE);
        });


        return view;
    }

    private void setChartFeature() {
        mFundChart.setOnChartValueSelectedListener(this);
        mFundChart.setDrawBarShadow(false);
        mFundChart.setDrawValueAboveBar(true);
        mFundChart.getDescription().setEnabled(false);
        mFundChart.setPinchZoom(false);
        mFundChart.setDrawGridBackground(false);
        mFundChart.getLegend().setEnabled(false);
        mFundChart.setDrawValueAboveBar(true);



       /* mFundChart.setFitBars(true);
        mFundChart.setMaxVisibleValueCount(60);*/
        xAxis = mFundChart.getXAxis();
        // xAxis.setLabelRotationAngle(-90);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(6f);
        xAxis.setEnabled(true);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            mFundChart.getAxisLeft().setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor)); // left y-axis
            mFundChart.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor)); // left y-axis
            mFundChart.getLegend().setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor)); // left y-axis
        } else {
            mFundChart.getAxisLeft().setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor)); // left y-axis
            mFundChart.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor)); // left y-axis
            mFundChart.getLegend().setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor)); // left y-axis
        }
        leftAxis = mFundChart.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        YAxis rightAxis = mFundChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(5);
        rightAxis.setEnabled(false);
        rightAxis.setSpaceTop(15f);
        // this replaces setStartAtZero(true)

        // Set the marker to the chart
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int position = (int) e.getX();
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex() + "fcode: " + Fcode[position]);


        if (count == 1) {
            mFundChart.clear();
            mFundBtn.setVisibility(View.VISIBLE);
            getSchemeList(Fcode[position]);
        }

    }
    @Override
    public void onNothingSelected() {
    }

    private void getFundAllocation(String bid, String passkey, String cid) {
        count = 0;
        DialogsUtils.showProgressBar(getActivity(), false);
        url = Config.Allocation_Fund;

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
                    AppApplication.fund_allocation = response.toString();
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

    private void DrawFundAllocation(final ArrayList<JSONObject> list) {
        count = count + 1;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> list2 = new ArrayList<>();
        FundName = new String[list.size()];
        SchemeName = new String[list.size()];
        CurrentValue = new String[list.size()];
        Fcode = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {

            JSONObject jsonObject = list.get(i);
            FundName[i] = jsonObject.optString("FundName");
            SchemeName[i] = jsonObject.optString("SchemeName");
            CurrentValue[i] = jsonObject.optString("CurrentValue");
            Fcode[i] = jsonObject.optString("Fcode");

            if (FundName[i].length() > 5) {

                list2.add(FundName[i].substring(0, 6) + ".");
            } else {
                list2.add(FundName[i] + ".");
            }

            float Yaxis = Float.parseFloat(CurrentValue[i].replace(",", ""));
            float Yvalue = (Yaxis);
            yVals1.add(new BarEntry(i, Yvalue));

        }
        // xAxis.setLabelCount(list.size(), true);
        YAxis leftAxis = mFundChart.getAxisLeft();
        leftAxis.setEnabled(true);
        GraphData(list2, yVals1);


    }


    private void getSchemeList(String mFcode) {

        DialogsUtils.showProgressBar(getActivity(), false);
        count = 0;
        url = Config.Allocation_Scheme;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", passkey);
            jsonObject.put("Bid", bid);
            jsonObject.put(AppConstants.CUSTOMER_ID, cid);
            jsonObject.put("Fcode", mFcode);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //setChartFeature();
                    DialogsUtils.hideProgressBar();

                    ArrayList<JSONObject> list = new ArrayList<>();

                    if (response.optString("Status").equalsIgnoreCase("True")) {

                        JSONArray jsonArray = response.optJSONArray("AllocationBySchemeDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                            list.add(jsonObject1);
                        }
                        int mTotalPrice = 0;
                        float mAllocation = 0.0f;
                        mName.setText("Scheme");
                        DrawSchemeGraph(list);
                        for (int i = 0; i < list.size(); i++) {
                            mTotalPrice += Integer.parseInt(list.get(i).optString("CurrentValue").replaceAll(",", ""));
                            mAllocation += Float.parseFloat(list.get(i).optString("HoldingPercentage").replaceAll(",", ""));

                        }
                        JSONObject jsonObjectTotal = new JSONObject();
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                        try {

                            jsonObjectTotal.put("SchemeName", "Total");
                            String num = NumberFormat.getNumberInstance(Locale.US).format(mTotalPrice).split("//.")[0];

                            jsonObjectTotal.put("CurrentValue",  "Rs."+num);
                            jsonObjectTotal.put("HoldingPercentage", NumberFormat.getNumberInstance(Locale.US).format(mAllocation));
                            list.add(jsonObjectTotal);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        fundAllocationAdapter.updateList(list);


                    } else {

                        Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

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

    private void DrawSchemeGraph(ArrayList<JSONObject> list) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> list2 = new ArrayList<>();
        SchemeName = new String[list.size()];
        CurrentValue = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = list.get(i);
            SchemeName[i] = jsonObject.optString("SchemeName");
            CurrentValue[i] = jsonObject.optString("CurrentValue");
            list2.add(SchemeName[i]);
            float Yaxis = Float.parseFloat(CurrentValue[i].replace(",", ""));
            float Yvalue = (Yaxis);
            yVals1.add(new BarEntry(i, Yvalue));

        }
        mFundChart.clear();
        YAxis leftAxis = mFundChart.getAxisLeft();
        leftAxis.setEnabled(false);
        GraphData(list2, yVals1);

        // xAxis.setLabelCount(list.size(), true);
    }

    private void GraphData(ArrayList<String> list, ArrayList<BarEntry> yVal) {

        mFundChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(list));
        xAxis.setLabelCount(list.size());


        BarDataSet set1;

        set1 = new BarDataSet(yVal, "");


        set1.setDrawIcons(false);
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

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
        set1.setColors(colors);
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        //  data.setValueFormatter(new IndexValueFormatter(SchemeName));
        data.setValueTextSize(9f);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            data.setValueTextColor(ContextCompat.getColor(mActivity,R.color.darkPrimaryTextColor));

        } else {
            data.setValueTextColor(ContextCompat.getColor(mActivity,R.color.lightPrimaryTextColor));

        }
        data.setBarWidth(0.6f);
        mFundChart.animateXY(3000, 3000);
        mFundChart.setHighlightPerDragEnabled(false);
mFundChart.getAxisLeft().setDrawLabels(false);
        mFundChart.setData(data);

    }


    public class IndexValueFormatter implements IValueFormatter {

        private final String[] values;

        public IndexValueFormatter(String[] values) {
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // int index = Math.round(value);

            String index = String.valueOf(value);

            for (int i = 0; i < values.length; i++) {

                return values[i];
            }


            return index;
        }
    }

    private void setData() {
        count = 0;
        int mTotalPrice = 0;
        float mAllocation = 0.0f;
        try {
            JSONObject response = new JSONObject(AppApplication.fund_allocation);
            ArrayList<JSONObject> list = new ArrayList<>();
            if (response.optString("Status").equalsIgnoreCase("True")) {
                JSONArray jsonArray = response.optJSONArray("AllocationByFundDetail");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        list.add(jsonObject1);
                    }
                }
                mName.setText("Fund");
                DrawFundAllocation(list);
                for (int i = 0; i < list.size(); i++) {
                    mTotalPrice += Integer.parseInt(list.get(i).optString("CurrentValue").replaceAll(",", ""));
                    mAllocation += Float.parseFloat(list.get(i).optString("HoldingPercentage").replaceAll(",", ""));

                }
                JSONObject jsonObjectTotal = new JSONObject();

                try {

                    jsonObjectTotal.put("SchemeName", "Total");
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                    String num = NumberFormat.getNumberInstance(Locale.US).format(mTotalPrice).split("//.")[0];

                   // String num=format.format(new BigDecimal(String.valueOf(mTotalPrice)));
                    jsonObjectTotal.put("CurrentValue","Rs."+num);
                    jsonObjectTotal.put("HoldingPercentage", NumberFormat.getNumberInstance(Locale.US).format(mAllocation));
                    list.add(jsonObjectTotal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                fundAllocationAdapter.updateList(list);

            } else {
                Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}



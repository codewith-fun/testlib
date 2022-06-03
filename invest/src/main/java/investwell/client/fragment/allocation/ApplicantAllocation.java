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
import investwell.client.adapter.ApplicantAllocationAdapter;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

import static com.github.mikephil.charting.animation.Easing.EasingOption.EaseInOutQuad;


public class ApplicantAllocation extends Fragment {

    private PieChart mApplicantChart;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private Bundle bundle;
    private String bid, passkey, cid;
    private AppSession mSession;
    private String[] name, marketvalue;
    private RecyclerView mApplicantRecycle;
    private ApplicantAllocationAdapter applicantAllocationAdapter;
    private MainActivity mActivity;
    private AppApplication mApplication;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_applicant_allocation, container, false);
        bundle = getArguments();
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();

        mApplicantChart = view.findViewById(R.id.applicant_chart);
        mApplicantRecycle = view.findViewById(R.id.applicant_recycle);
        mApplicantRecycle.setHasFixedSize(true);
        mApplicantRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
           applicantAllocationAdapter = new ApplicantAllocationAdapter(getActivity(), new ArrayList<JSONObject>());
        mApplicantRecycle.setAdapter(applicantAllocationAdapter);
        bundle = getArguments();
        bid = AppConstants.APP_BID;
        passkey = mSession.getPassKey();

        if (bundle != null && bundle.containsKey("cid")) {
            cid = bundle.getString("cid");
        } else {
            cid = mSession.getCID();
        }
        setchartfeature();
        if (AppApplication.applicant_allocation.isEmpty()){
            getApplicantAllocation(bid, passkey, cid);
        }else{
            setData();
        }

        return view;
    }

    public void setchartfeature() {
        mApplicantChart.setUsePercentValues(true);
        mApplicantChart.getDescription().setEnabled(false);
        mApplicantChart.setExtraOffsets(5, 10, 5, 5);

        mApplicantChart.setDragDecelerationFrictionCoef(0.95f);
        mApplicantChart.setCenterText(generateCenterSpannableText());
        mApplicantChart.setDrawHoleEnabled(true);
        mApplicantChart.setHoleColor(Color.WHITE);
        mApplicantChart.setTransparentCircleColor(Color.WHITE);
        mApplicantChart.setTransparentCircleAlpha(110);
        mApplicantChart.setHoleRadius(65f);
        mApplicantChart.setTransparentCircleRadius(60f);
        mApplicantChart.setDrawCenterText(true);
        mApplicantChart.setRotationAngle(0);
        mApplicantChart.setRotationEnabled(true);
        mApplicantChart.setHighlightPerTapEnabled(true);

        Legend l = mApplicantChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            l.setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor));
        } else {
            l.setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor));
        }

        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(8f);
        mApplicantChart.setEntryLabelColor(Color.TRANSPARENT);
        mApplicantChart.getLegend().setWordWrapEnabled(true);
        mApplicantChart.setEntryLabelTextSize(12f);
    }

    private void getApplicantAllocation(String mBid, String mPasskey, String mCid) {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.Allocation_Applicant;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConstants.PASSKEY, mPasskey);
            jsonObject.put(AppConstants.KEY_BROKER_ID, mBid);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCid);
            jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    AppApplication.applicant_allocation = response.toString();
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

    private void DrawApplicantChart(ArrayList<JSONObject> list) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        name = new String[list.size()];
        marketvalue = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = list.get(i);
            name[i] = jsonObject.optString("Name");
            marketvalue[i] = jsonObject.optString("MarketValue");
            entries.add(new PieEntry((float) (( Double.parseDouble(marketvalue[i].replace(",", ""))) + Double.parseDouble(marketvalue[i].replace(",", "")) / 5), name[i]));
        }

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


        mApplicantChart.setData(data);
        mApplicantChart.setRotationEnabled(false);
        mApplicantChart.animateY(1400, EaseInOutQuad);
        mApplicantChart.highlightValues(null);
        mApplicantChart.invalidate();


    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Applicant Allocation ");
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 20, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL),  0, 20, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY),  0, 20, 0);
       /* s.setSpan(new RelativeSizeSpan(.8f), 20, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 18, s.length(), 0);
       */ return s;
    }

    private void setData(){
        int mTotalPrice = 0;
        float mAllocation = 0.0f;
        try{
            ArrayList<JSONObject> list = new ArrayList<>();
            JSONObject response = new JSONObject(AppApplication.applicant_allocation);
            if (response.optString("Status").equalsIgnoreCase("True")) {
                JSONArray jsonArray = response.optJSONArray("AllocationApplicantDetail");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        list.add(jsonObject1);
                    }
                }
                DrawApplicantChart(list);
                for (int i = 0; i < list.size(); i++) {
                   mTotalPrice += Integer.parseInt(list.get(i).optString("MarketValue").replaceAll(",", ""));
                    mAllocation += Float.parseFloat(list.get(i).optString("HoldingPercentage").replaceAll(",", ""));

                }
                JSONObject jsonObjectTotal = new JSONObject();
                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                try {

                    jsonObjectTotal.put("Name", "Total");
                    String num = NumberFormat.getNumberInstance(Locale.US).format(mTotalPrice).split("//.")[0];

                    jsonObjectTotal.put("MarketValue","Rs."+num);
                    jsonObjectTotal.put("HoldingPercentage", NumberFormat.getNumberInstance(Locale.getDefault()).format(mAllocation));
                    list.add(jsonObjectTotal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                applicantAllocationAdapter.updateList(list);
            } else {
                Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

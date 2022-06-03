package investwell.client.fragment.factsheet.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class OverViewFragment extends Fragment {
    RequestQueue requestQueue;
    StringRequest stringRequest;
    LineChart mChart;
    String x, y;
    Float[] a;

    ArrayList<String> arr = new ArrayList<>();
    JSONArray FactsheetCumulativePerformanceDetail;
    String bid, passkey, excl_code;
    TextView description;


    //-----------------------SUMMARY PARAMETER-------------------------------------//
    String Corpus, LaunchDate, Exitload, Benchmark, Equity, Debt, Other, FundRate, Weekhigh, Weeklow,
            Category, Schemetype, SchemeDesc, Sname, MinimunIvestment, CloseDate, EntryLoad, ExpenseR,
            SNAV, BroadBenchmark, ChangeNAV, NAVDt, FCMonth, FCYear, AMCLogo;


    TextView colorBlue, benchmark, nav, cagr, exit_load, corpus;
    RatingBar ratingBar;
    TextView radio1, radio2;

    ImageView scheme_icon;

    //---------------------PERFORMANCE PARAMETER----------------------------------//
    String SchemeName, Exlcode, Month1Return, Month3Return, Month6Return, Year1Return, Year2Return, Year3Return, Year5Return,
            Year10Return, SinceInception;
    TextView month_one, month_two, month_three, month_four, year_one, year_two, year_three, year_four, year_five;
    TextView month_one2, month_two2, month_three2, month_four2, year_one2, year_two2,
            year_three2, year_four2, year_five2;

    String[] DateSeries, SchemeAmount, BroadBenchmarkAmount, SchemeBenchmarkAmount;
    private FloatingActionButton mFloatingButtom;
    private AppSession mSession;
    private JSONObject mJsonObject;
    private boolean mIsAlreadyAdded = false;
    private List<JSONObject> mAddToCartList;
    private Bundle bundle;
    private MainActivity mainActivity;
private AppApplication mApplication;
    @Override
    public void onResume() {
        super.onResume();
        setmFloatingButtom();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
            mApplication=(AppApplication)mainActivity.getApplication();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_over_view, container, false);
        DialogsUtils.showProgressBar(mainActivity, false);
        bundle = getArguments();
        bid = getArguments().getString("bid");
        passkey = getArguments().getString("passkey");
        excl_code = getArguments().getString("excl_code");
        mSession = AppSession.getInstance(mainActivity);

        colorBlue = view.findViewById(R.id.colorBlue);
        benchmark = view.findViewById(R.id.benchmark);
        scheme_icon = view.findViewById(R.id.scheme_icon);
        radio1 = view.findViewById(R.id.radio1);
        radio2 = view.findViewById(R.id.radio2);
        ratingBar = view.findViewById(R.id.ratingBar);
        nav = view.findViewById(R.id.nav);
        cagr = view.findViewById(R.id.cagr);
        exit_load = view.findViewById(R.id.exit_load);
        corpus = view.findViewById(R.id.corpus);

        month_one = view.findViewById(R.id.month_one);
        month_two = view.findViewById(R.id.month_two);
        month_three = view.findViewById(R.id.month_three);
        month_four = view.findViewById(R.id.month_four);
        year_one = view.findViewById(R.id.year_one);
        year_two = view.findViewById(R.id.year_two);
        year_three = view.findViewById(R.id.year_three);
        year_four = view.findViewById(R.id.year_four);
        year_five = view.findViewById(R.id.year_five);

        month_one2 = view.findViewById(R.id.month_one2);
        month_two2 = view.findViewById(R.id.month_two2);
        month_three2 = view.findViewById(R.id.month_three2);
        month_four2 = view.findViewById(R.id.month_four2);
        year_one2 = view.findViewById(R.id.year_one2);
        year_two2 = view.findViewById(R.id.year_two2);
        year_three2 = view.findViewById(R.id.year_three2);
        year_four2 = view.findViewById(R.id.year_four2);
        year_five2 = view.findViewById(R.id.year_five2);

        mFloatingButtom = view.findViewById(R.id.cart_add);

        colorBlue.setText(getArguments().getString("colorBlue"));

        try {

            if (bundle != null) {
                if (bundle.containsKey("hide_icon")) {
                    mFloatingButtom.setVisibility(View.INVISIBLE);
                } else {
                    mJsonObject = new JSONObject(bundle.getString("object"));
                    setmFloatingButtom();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mFloatingButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAlreadyAdded) {
                    mAddToCartList.add(mJsonObject);

                    mSession.setAddToCartList(mAddToCartList.toString());
                    mFloatingButtom.setImageResource(R.mipmap.ic_check);
                    mFloatingButtom.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainActivity, R.color.colorGreen)));
                    mIsAlreadyAdded = true;
                    FragFactsheetHome parentFrag = ((FragFactsheetHome) OverViewFragment.this.getParentFragment());
                    if (parentFrag != null) {
                        parentFrag.toolbarFragment.updateCart(true);
                    }
                    // Toast.makeText(mainActivity, "Successfully added to Cart", Toast.LENGTH_SHORT).show();
                } else {

                    mAddToCartList.remove(mJsonObject);
                    mSession.setAddToCartList(mAddToCartList.toString());
                    mFloatingButtom.setImageResource(R.mipmap.ic_add);
                    mFloatingButtom.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainActivity, R.color.colorPrimary)));
                    mIsAlreadyAdded = false;
                    FragFactsheetHome parentFrag = ((FragFactsheetHome) OverViewFragment.this.getParentFragment());
                    if (parentFrag != null) {
                        parentFrag.toolbarFragment.updateCart(true);
                    }
                    // Toast.makeText(mainActivity, "Scheme already added to Cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SummaryDetails();
        mChart = view.findViewById(R.id.linechart);
        description = view.findViewById(R.id.description);
        CumulativePerfornamnce();
setData(12);
        return view;
    }

    private void setmFloatingButtom() {
        mAddToCartList = new ArrayList<>();

        try {

            if (!mSession.getAddToCartList().equals("[]")) {
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mAddToCartList.add(jsonObject);
                    if (mJsonObject != null) {
                        if (mJsonObject.optString("Exlcode").equals(jsonObject.optString("Exlcode"))) {
                            mFloatingButtom.setImageResource(R.mipmap.ic_check);
                            mFloatingButtom.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainActivity, R.color.colorGreen)));
                            mIsAlreadyAdded = true;
                            break;
                        } else {
                            mFloatingButtom.setImageResource(R.mipmap.ic_add);
                            mFloatingButtom.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainActivity, R.color.colorAccent)));
                            mIsAlreadyAdded = false;
                        }
                    }
                }
            } else {
                mFloatingButtom.setImageResource(R.mipmap.ic_add);
                mFloatingButtom.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainActivity, R.color.colorAccent)));
                mIsAlreadyAdded = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public void SummaryDetails() {
        DialogsUtils.showProgressBar(mainActivity, false);

        String url = Config.Summary_Details;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", passkey);
            jsonObject.put("Exlcode", excl_code);
            jsonObject.put("FormatReq", "Y");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        String Status = jsonObject.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {

                            DialogsUtils.hideProgressBar();

                            JSONArray jsonArray = jsonObject.getJSONArray("FactsheetSummaryDetail");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                            Corpus = jsonObject1.optString("Corpus");
                            LaunchDate = jsonObject1.optString("LaunchDate");
                            Exitload = jsonObject1.optString("Exitload");
                            Benchmark = jsonObject1.optString("Benchmark");
                            Equity = jsonObject1.optString("Equity");
                            Debt = jsonObject1.optString("Debt");
                            Other = jsonObject1.optString("Other");
                            FundRate = jsonObject1.optString("FundRate");
                            Weekhigh = jsonObject1.optString("Weekhigh");
                            Weeklow = jsonObject1.optString("Weeklow");
                            Category = jsonObject1.optString("Category");
                            Schemetype = jsonObject1.optString("Schemetype");
                            SchemeDesc = jsonObject1.optString("SchemeDesc");
                            Sname = jsonObject1.optString("SchemeName");
                            MinimunIvestment = jsonObject1.optString("MinimunIvestment");
                            CloseDate = jsonObject1.optString("CloseDate");
                            EntryLoad = jsonObject1.optString("EntryLoad");
                            ExpenseR = jsonObject1.optString("ExpenseR");
                            SNAV = jsonObject1.optString("SchemeNAV");
                            BroadBenchmark = jsonObject1.optString("BroadBenchmark");
                            ChangeNAV = jsonObject1.optString("ChangeNAV");
                            NAVDt = jsonObject1.optString("NAVDt");
                            FCMonth = jsonObject1.optString("FCMonth");
                            FCYear = jsonObject1.optString("FCYear");
                            AMCLogo = jsonObject1.optString("AMCLogo");


                            Config.mGraphValue.clear();
                            if (!Equity.equals("0")) {
                                Config.mGraphValue.put("Equity", Equity);
                            }

                            if (!Debt.equals("0")) {
                                Config.mGraphValue.put("Debt", Debt);
                            }

                            if (!Other.equals("0")) {
                                Config.mGraphValue.put("Other", Other);
                            }

                            description.setText(SchemeDesc);

                            benchmark.setText(Benchmark);
                           /* ratingBar.setRating(Float.parseFloat(FundRate));*/
                            radio1.setText(Category);
                            radio2.setText(Schemetype);


                            Picasso.get().load(AMCLogo).placeholder(R.mipmap.tranparent)
                                    .error(R.mipmap.tranparent).into(scheme_icon);
                            if (ChangeNAV.contains("-")) {
                                nav.setTextColor(Color.parseColor("#FFE53115"));
                            }
                            nav.setText(SNAV + " (" + ChangeNAV + "%)");

                            if (Exitload.contains("0")) {
                                exit_load.setText("Nil");
                            } else {
                                exit_load.setText(Exitload);
                            }
                            corpus.setText(Corpus + " Cr.");
                            // CumulativePerfornamnce();
                        } else {
                            DialogsUtils.hideProgressBar();
                            String ServiceMSG = jsonObject.optString("ServiceMSG");
                            Toast.makeText(mainActivity, ServiceMSG, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                    } finally {
                        PerformanceAnalysis();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
mApplication.showSnackBar(description,getResources().getString(R.string.error_try_again));
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mainActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void PerformanceAnalysis() {
        DialogsUtils.showProgressBar(mainActivity, false);
        String url = Config.Performance_Analysis;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", passkey);
            jsonObject.put("Exlcode", excl_code);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    DialogsUtils.hideProgressBar();
                    try {

                        String status = jsonObject.optString("Status");
                        if (status.equalsIgnoreCase("True")) {

                            //DataSet();

                            JSONArray jsonArray = jsonObject.getJSONArray("FactsheetSchemePerformanceDetail");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            JSONObject jsonObject2 = jsonArray.getJSONObject(2);
                            SchemeName = jsonObject1.optString("SchemeName");
                            Exlcode = jsonObject1.optString("Exlcode");

                            Month1Return = jsonObject1.optString("Month1Return");
                            Month3Return = jsonObject1.optString("Month3Return");
                            Month6Return = jsonObject1.optString("Month6Return");
                            Year1Return = jsonObject1.optString("Year1Return");
                            Year2Return = jsonObject1.optString("Year2Return");
                            Year3Return = jsonObject1.optString("Year3Return");
                            Year5Return = jsonObject1.optString("Year5Return");
                            Year10Return = jsonObject1.optString("Year10Return");


                            SinceInception = jsonObject1.optString("SinceInception");
                            if (Year5Return.contains("-")) {
                                cagr.setText("- " + Year5Return + "%");
                                cagr.setTextColor(Color.parseColor("#FFE53115"));

                            } else {

                                cagr.setText("+ " + Year5Return + "%");
                                cagr.setTextColor(Color.parseColor("#FF51B53F"));
                            }


                            if (Month1Return.contains("0.00")) {

                                month_one.setText("Nil");
                            } else {
                                month_one.setText(Month1Return + "%");

                            }
                            if (Month3Return.contains("0.00")) {

                                month_two.setText("Nil");
                            } else {

                                month_two.setText(Month3Return + "%");
                            }
                            if (Month6Return.contains("0.00")) {

                                month_three.setText("Nil");

                            } else {
                                month_three.setText(Month6Return + "%");
                            }
                            if (Year1Return.contains("0.00")) {
                                month_four.setText("Nil");

                            } else {

                                month_four.setText(Year1Return + "%");
                            }
                            if (Year2Return.contains("0.00")) {

                                year_one.setText("Nil");

                            } else {

                                year_one.setText(Year2Return + "%");
                            }
                            if (Year3Return.contains("0.00")) {

                                year_two.setText("Nil");
                            } else {

                                year_two.setText(Year3Return + "%");
                            }
                            if (Year5Return.contains("0.00")) {

                                year_three.setText("Nil");

                            } else {

                                year_three.setText(Year5Return + "%");
                            }
                            if (Year10Return.contains("0.00")) {
                                year_four.setText("Nil");
                            } else {
                                year_four.setText(Year10Return + "%");
                            }

                            String Month1Return2 = jsonObject2.optString("Month1Return");
                            String Month3Return2 = jsonObject2.optString("Month3Return");
                            String Month6Return2 = jsonObject2.optString("Month6Return");
                            String Year1Return2 = jsonObject2.optString("Year1Return");
                            String Year2Return2 = jsonObject2.optString("Year2Return");
                            String Year3Return2 = jsonObject2.optString("Year3Return");
                            String Year5Return2 = jsonObject2.optString("Year5Return");
                            String Year10Return2 = jsonObject2.optString("Year10Return");


                            if (Month1Return2.contains("0.00")) {
                                month_one2.setText("Nil");
                            } else {
                                month_one2.setText(Month1Return2 + "%");

                            }
                            if (Month3Return2.contains("0.00")) {

                                month_two2.setText("Nil");
                            } else {

                                month_two2.setText(Month3Return2 + "%");
                            }
                            if (Month6Return2.contains("0.00")) {

                                month_three2.setText("Nil");

                            } else {
                                month_three2.setText(Month6Return2 + "%");
                            }
                            if (Year1Return2.contains("0.00")) {
                                month_four2.setText("Nil");

                            } else {

                                month_four2.setText(Year1Return2 + "%");
                            }
                            if (Year2Return2.contains("0.00")) {

                                year_one2.setText("Nil");

                            } else {

                                year_one2.setText(Year2Return2 + "%");
                            }
                            if (Year3Return2.contains("0.00")) {

                                year_two2.setText("Nil");
                            } else {

                                year_two2.setText(Year3Return2 + "%");
                            }
                            if (Year5Return2.contains("0.00")) {

                                year_three2.setText("Nil");

                            } else {

                                year_three2.setText(Year5Return2 + "%");
                            }
                            if (Year10Return2.contains("0.00")) {
                                year_four2.setText("Nil");
                            } else {
                                year_four2.setText(Year10Return2 + "%");
                            }

                        } else {

                            String ServiceMSG = jsonObject.optString("ServiceMSG");
                            Toast.makeText(mainActivity, ServiceMSG, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    DialogsUtils.hideProgressBar();
                    mApplication.showSnackBar(description,getResources().getString(R.string.error_try_again));

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mainActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void CumulativePerfornamnce() {


        String url = Config.Cumulative_Performance + passkey + "/" + AppConstants.APP_BID + "/" + excl_code;



        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject mDataList = new JSONObject(response);
                    String Status = mDataList.optString("Status");

                    ArrayList<Float> arr = new ArrayList<>();


                    if (Status.equalsIgnoreCase("True")) {

                        PerformanceAnalysis();
                        FactsheetCumulativePerformanceDetail = mDataList.optJSONArray("FactsheetCumulativePerformanceDetail");
                        DateSeries = new String[FactsheetCumulativePerformanceDetail.length()];
                        SchemeAmount = new String[FactsheetCumulativePerformanceDetail.length()];
                        BroadBenchmarkAmount = new String[FactsheetCumulativePerformanceDetail.length()];
                        SchemeBenchmarkAmount = new String[FactsheetCumulativePerformanceDetail.length()];


                        for (int i = 0; i < FactsheetCumulativePerformanceDetail.length(); i++) {

                            JSONObject jsonObject1 = FactsheetCumulativePerformanceDetail.optJSONObject(i);
                            DateSeries[i] = jsonObject1.optString("DateSeries");
                            SchemeAmount[i] = jsonObject1.optString("SchemeAmount");
                            BroadBenchmarkAmount[i] = jsonObject1.optString("BroadBenchmarkAmount");
                            SchemeBenchmarkAmount[i] = jsonObject1.optString("SchemeBenchmarkAmount");
                        }


                    } else {

                        DialogsUtils.hideProgressBar();
                        String ServiceMSG = mDataList.optString("ServiceMSG");
                        Toast.makeText(mainActivity, ServiceMSG, Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println();

                DialogsUtils.hideProgressBar();
            }
        });

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue = Volley.newRequestQueue(mainActivity);
        requestQueue.add(stringRequest);
    }

    private void setData(int count) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = Float.parseFloat(String.valueOf(SchemeAmount[i]));
            yVals1.add(new Entry(i, val));
        }

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float val = Float.parseFloat(String.valueOf(BroadBenchmarkAmount[i]));
            yVals2.add(new Entry(i, val));

        }

        ArrayList<Entry> yVals3 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = Float.parseFloat(String.valueOf(SchemeBenchmarkAmount[i]));
            yVals3.add(new Entry(i, val));
        }

        LineDataSet set1, set2, set3;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            set3.setValues(yVals3);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {

            set1 = new LineDataSet(yVals1, "Scheme");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            // set1.setCircleColor(Color.WHITE);
            set1.setDrawCircles(false);
            set1.setDrawCircleHole(false);
            set1.setLineWidth(2f);
            set1.setCircleRadius(1f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);


            set2 = new LineDataSet(yVals2, "Broad Benchmark");
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setColor(Color.RED);
            // set2.setCircleColor(Color.WHITE);
            set2.setDrawCircles(false);
            set2.setDrawCircleHole(false);
            set2.setLineWidth(2f);
            set2.setCircleRadius(1f);
            set2.setFillAlpha(65);
            set2.setFillColor(Color.RED);
            set2.setDrawCircleHole(false);
            set2.setHighLightColor(Color.rgb(244, 117, 117));


            set3 = new LineDataSet(yVals3, "Scheme Benchmark");
            set3.setAxisDependency(YAxis.AxisDependency.LEFT);
            set3.setColor(Color.GREEN);
            // set3.setCircleColor(Color.WHITE);
            set3.setDrawCircles(false);
            set3.setDrawCircleHole(false);
            set3.setLineWidth(2f);
            set3.setCircleRadius(1f);
            set3.setFillAlpha(65);
            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.GREEN, 200));
            set3.setDrawCircleHole(false);
            set3.setHighLightColor(Color.rgb(244, 117, 117));


            LineData data = new LineData(set1, set2, set3);
            data.setValueTextColor(Color.WHITE);
            data.setDrawValues(false);

            data.setValueTextSize(9f);
            mChart.setData(data);
        }
    }


}


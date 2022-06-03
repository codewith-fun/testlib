package investwell.client.fragment.factsheet.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.iw.acceleratordemo.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.TopHoldingAdapter;
import investwell.client.fragment.factsheet.Adapter.SchemeComparisonAdapter;
import investwell.client.fragment.factsheet.Adapter.ScoreAnalysisAdapter;
import investwell.client.fragment.factsheet.Utils.ComparisonSchemeModel;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.piechart.callback.OnPieLegendBindListener;
import investwell.utils.piechart.callback.OnPieSelectListener;
import investwell.utils.piechart.data.IPieInfo;
import investwell.utils.piechart.data.MyMarkerView;
import investwell.utils.piechart.data.SimplePieInfo;
import investwell.utils.piechart.others.AnimatedPieView;
import investwell.utils.piechart.others.AnimatedPieViewConfig;
import investwell.utils.piechart.others.BasePieLegendsView;
import investwell.utils.piechart.others.DefaultPieLegendsView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FactSheetFragment extends Fragment implements ToolbarFragment.ToolbarCallback, SchemeComparisonAdapter.SchemeComparisonListener, View.OnClickListener {
    public ToolbarFragment toolbarFragment;
    RequestQueue requestQueue;
    String bid, passkey, excl_code;
    //-----------------------SUMMARY PARAMETER-------------------------------------//
    String Corpus, LaunchDate, Exitload, Benchmark, Equity, Debt, Other, FundRate, Weekhigh, Weeklow,
            Category, Schemetype, SchemeDesc, Sname, MinimunIvestment, CloseDate, EntryLoad, ExpenseR,
            SNAV, BroadBenchmark, ChangeNAV, NAVDt, FCMonth, FCYear, AMCLogo, bId, passKey, exclCode;
    //---------------------PERFORMANCE PARAMETER----------------------------------//
    String SchemeName, Exlcode, Month1Return, Month3Return, Month6Return, Year1Return, Year2Return, Year3Return, Year5Return,
            Year10Return, SinceInception;
    String[] DateSeries, SchemeAmount, BroadBenchmarkAmount, SchemeBenchmarkAmount;
    RecyclerView rvSectorAnalysis, rvHoldingAnalysis, rvSchemeComparisonList;
    ScoreAnalysisAdapter scoreAnalysisAdapter;
    private View view;
    private Bundle bundle_new = new Bundle();
    private Bundle bundle;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ScrollView svFactSheetContainer;
    private LinearLayout mLinerFooter;
    private TextView mTvCart;
    private FrameLayout cart;
    private JSONArray mJSONArraySixMonthScheme = new JSONArray();
    private JSONArray mJSONArrayOneYearMonthScheme = new JSONArray();
    private JSONArray mJSONArrayThreeYearMonthScheme = new JSONArray();
    private JSONArray mJSONArrayFiveYearMonthScheme = new JSONArray();
    private JSONObject mJsonObjectSchemeComp = new JSONObject();
    private JSONObject mJsonObject;
    private ImageView ivShare;
    private List<JSONObject> mAddToCartList;
    private LinearLayout llYrpContent;
    private TextView tvOneMonth, tvThreeMonth, tvSixMonth, tvYearOne, tvYearTwo, tvYearThree, tvYearFive, tvYearTen;
    private TextView tvBenchmarkOneMonth, tvBenchmarkThreeMonth, tvBenchmarkSixMonth, tvBenchmarkYearOne, tvBenchmarkYearTwo, tvBenchmarkYearThree,
            tvBenchmarkYearFive, tvBenchmarkYearTen;
    private TextView tvSchemeName, tvBenchmark, tvNav, tvNavDate, tvExitLoad, tvNavPriceValue, tvDesc, tvSchemeCompFooterDate;
    private TextView tvShortTermBenchMarkTitle, tvLongTermBenchMarkTitle, tvRiskoMeterGraphInstructions, tvSchemeCompHeader, tvCurrentSchemeValue, tvSchemeCompValue;
    private TextView tvSchemeObjective, tvFundManagerValue, tvRiskProfileValue, tvFundSize, tvFundAge, tvFundCategory, tvExpenseRatio;
    private ImageView ivScheme, ivRiskometer;
    private AnimatedPieView mAnimatedPieView;
    private FlowLayout mViewGroup;
    private SchemeComparisonAdapter schemeComparisonAdapter;
    private List<ComparisonSchemeModel> comparisonSchemeList;
    private String[] schemeAgeValue = {"6M", "1Y", "3Y", "5Y"};
    private boolean mIsAlreadyAdded = false;
    private String selectedScheme = "1Y", schemeSixMonth, schemeOneYear, schemeThreeYear, schemeFiveYear, benchMarkSixMonth, benchMarkOneYr, benchMarkThreeYr, benchMarkFiveYr;
    private LineChart lcSchemeComparisonChart;
    private MyMarkerView mMarkerView;
    private ImageView ivCart , ivVideoPlay;
    private CardView cvRiskMeter;
    private TextView tvNoChartData,tv_launch_date;
    private RelativeLayout rlTransact;
    private TextView btnShowMoreSector, btnShowMoreHolding;
    private ArrayList<JSONObject> dataList, mDataListSector;
    private TopHoldingAdapter topHoldingAdapter;
    private String sixMonthApiStatus = "", oneYearApiStatus = "", threeYearApiStatus = "", fiveYearStatus = "";
    private String path = "";
    Bitmap b;
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb;

    private CardView videoCard;
    private TextView titleText,descText;
    private String mVideoUrl = "";

    //String videoUrl = " ";

    public FactSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        updateCart();
    }

    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
            mActivity.setMainVisibility(this, null);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_app_factsheet, container, false);
        setUpToolBar();
        cvRiskMeter = view.findViewById(R.id.cv_risk);
        setInitializer(view);
        setUpUiItemVisibility();
        getDataFromBundle();
        setUpAdapter();
        setUpComparisonSchemeAdapter();
        callFactSheetDetailsApi();
        callSectorAnalysisApi();
        callTopHoldingApi();
        setListener();
        setDisclaimerData(view);
        return view;
    }

    private void setVideo(String videoUrl) {
        videoCard.setVisibility(View.VISIBLE);
       /* String videoID = videoUrl.replace("https://www.youtube.com/embed/","");
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoID));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }*/
       /* youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                Log.e("12345",videoUrl);
                String videoID=videoUrl.replace("https://youtu.be/","");
               Log.e("12345",videoID);

               youTubePlayer.loadVideo(videoID,0);
            }
        });*/

    }

    private   void watchYoutubeVideo( String videoUrl){
        String videoID = videoUrl.replace("https://www.youtube.com/embed/","");
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoID));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setDisclaimerData(View view){
        TextView mDiscDesc=view.findViewById(R.id.tv_disc);

        mDiscDesc.setText(getResources().getString(R.string.disc_content_top_scheme)+""+getResources().getString(R.string.top_scheme_desc_list));
    }
    private void setUpUiItemVisibility() {
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("FundFactsheetAddToCart")) &&
                investwell.utils.Utils.getConfigData(mSession).optString("FundFactsheetAddToCart").equalsIgnoreCase("Y")) {
            ivCart.setVisibility(View.VISIBLE);
        } else {
            ivCart.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("BrokerName"))) {
            tvBrokerName.setText(investwell.utils.Utils.getConfigData(mSession).optString("BrokerName"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Address"))) {
            tvAddress.setText(investwell.utils.Utils.getConfigData(mSession).optString("Address"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Email"))) {
            tvEmail.setText(investwell.utils.Utils.getConfigData(mSession).optString("Email"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Mobile"))) {
            tvPhone.setText(investwell.utils.Utils.getConfigData(mSession).optString("Mobile"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Website"))) {
            tvWeb.setText(investwell.utils.Utils.getConfigData(mSession).optString("Website"));
        }

    }

    private void setUpComparisonSchemeAdapter() {
        schemeComparisonAdapter = new SchemeComparisonAdapter(mActivity, schemeAgeValue, this);
        rvSchemeComparisonList.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvSchemeComparisonList.setItemAnimator(new DefaultItemAnimator());
        rvSchemeComparisonList.setNestedScrollingEnabled(false);
        rvSchemeComparisonList.setAdapter(schemeComparisonAdapter);
    }

    private void setListener() {
        ivCart.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        rlTransact.setOnClickListener(this);
        btnShowMoreSector.setOnClickListener(this);
        btnShowMoreHolding.setOnClickListener(this);
        ivVideoPlay.setOnClickListener(this);
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);

        if (toolbarFragment != null) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
              //  toolbarFragment.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
            if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot")) &&
                    investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot").equalsIgnoreCase("Y")) {

                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_factsheet), true, true, false, false, false, true, false, "");
                toolbarFragment.setCallback(this);
                toolbarFragment.updateCart(true);
            } else {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_factsheet), true, false, false, false, false, true, false, "");
                toolbarFragment.setCallback(this);
                toolbarFragment.updateCart(true);
            }
            if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("FundFactsheetAddToCart")) &&
                    investwell.utils.Utils.getConfigData(mSession).optString("FundFactsheetAddToCart").equalsIgnoreCase("Y")) {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_factsheet), true, true, false, false, false, true, false, "");
                toolbarFragment.setCallback(this);
                toolbarFragment.updateCart(true);
            } else {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_factsheet), true, false, false, false, false, false, false, "");
                toolbarFragment.setCallback(this);
                toolbarFragment.updateCart(true);
            }
        }
    }


    private void onCartBtnClick() {
        if (!mIsAlreadyAdded) {
            mAddToCartList.add(mJsonObject);

            mSession.setAddToCartList(mAddToCartList.toString());
            ivCart.setImageResource(R.mipmap.ic_factsheet_cart_done);
            mIsAlreadyAdded = true;
            toolbarFragment.updateCart(true);

            // Toast.makeText(mainActivity, "Successfully added to Cart", Toast.LENGTH_SHORT).show();
        } else {

            mAddToCartList.remove(mJsonObject);
            mSession.setAddToCartList(mAddToCartList.toString());
            ivCart.setImageResource(R.mipmap.ic_factsheet_add_cart);
            mIsAlreadyAdded = false;
            toolbarFragment.updateCart(true);

            // Toast.makeText(mainActivity, "Scheme already added to Cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void setInitializer(View view) {



        bundle = getArguments();
        comparisonSchemeList = new ArrayList<>();
        mLinerFooter = view.findViewById(R.id.linerFooter);
        mLinerFooter.setVisibility(View.GONE);
        svFactSheetContainer = view.findViewById(R.id.sv_factsheet_container);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        cart = view.findViewById(R.id.fl_cart);
        tvOneMonth = view.findViewById(R.id.tv_row_scheme_element_one_month);
        tvThreeMonth = view.findViewById(R.id.tv_row_scheme_element_three_month);
        tvSixMonth = view.findViewById(R.id.tv_row_scheme_element_six_month);
        tvYearOne = view.findViewById(R.id.tv_row_scheme_element_one_year);
        tvYearTwo = view.findViewById(R.id.tv_long_row_scheme_element_two_year);
        tvYearThree = view.findViewById(R.id.tv_long_row_scheme_element_three_year);
        tvYearFive = view.findViewById(R.id.tv_long_row_scheme_element_six_year);
        tvYearTen = view.findViewById(R.id.tv_long_row_scheme_element_ten_year);
        tv_launch_date= view.findViewById(R.id.tv_launch_date);
        tvBenchmarkOneMonth = view.findViewById(R.id.tv_row_benchmark_element_one_month);
        tvBenchmarkThreeMonth = view.findViewById(R.id.tv_row_benchmark_element_three_month);
        tvBenchmarkSixMonth = view.findViewById(R.id.tv_row_benchmark_element_six_month);
        tvBenchmarkYearOne = view.findViewById(R.id.tv_row_benchmark_element_one_year);
        tvBenchmarkYearTwo = view.findViewById(R.id.tv_long_row_benchmark_element_two_year);
        tvBenchmarkYearThree = view.findViewById(R.id.tv_long_row_benchmark_element_three_year);
        tvBenchmarkYearFive = view.findViewById(R.id.tv_long_row_benchmark_element_six_year);
        tvBenchmarkYearTen = view.findViewById(R.id.tv_long_row_benchmark_element_ten_year);
        tvSchemeName = view.findViewById(R.id.tv_scheme_name);
        tvSchemeName.setSelected(true);
        tvSchemeObjective = view.findViewById(R.id.tv_scheme_obj_value);
        ivShare = view.findViewById(R.id.iv_share);
        tvFundManagerValue = view.findViewById(R.id.tv_fund_manager_value);
        tvRiskProfileValue = view.findViewById(R.id.tv_risk_profile_value);
        rlTransact = view.findViewById(R.id.rl_transact);
        tvNavPriceValue = view.findViewById(R.id.tv_scheme_price);
        tvExitLoad = view.findViewById(R.id.tv_exit_load);
        tvNav = view.findViewById(R.id.tv_scheme_growth);
        tvFundAge = view.findViewById(R.id.tv_fund_age);
        tvFundCategory = view.findViewById(R.id.tv_fund_category);
        tvExpenseRatio = view.findViewById(R.id.tv_fund_expense);
        tvFundSize = view.findViewById(R.id.tv_fund_size);
        tvNavDate = view.findViewById(R.id.tv_scheme_date);
        tvAddress = view.findViewById(R.id.tv_address);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        tvSchemeCompFooterDate = view.findViewById(R.id.tv_scheme_footer_date);
        ivScheme = view.findViewById(R.id.iv_scheme);
        ivRiskometer = view.findViewById(R.id.iv_riskometer_graph);
        tvShortTermBenchMarkTitle = view.findViewById(R.id.tv_row_benchmark_element_title);
        tvLongTermBenchMarkTitle = view.findViewById(R.id.tv_long_row_header_element_benchmark_header_title);
        tvBenchmark = view.findViewById(R.id.tv_benchmark_factsheet);
        tvRiskoMeterGraphInstructions = view.findViewById(R.id.tv_riskometer_graph_instruction);
        rvSectorAnalysis = view.findViewById(R.id.rv_sector_analysis);
        rvHoldingAnalysis = view.findViewById(R.id.rv_holding);
        rvSchemeComparisonList = view.findViewById(R.id.rv_scheme_comparison_data_elements);
        mAnimatedPieView = view.findViewById(R.id.animatedPieView);
        mViewGroup = view.findViewById(R.id.ll_legends);
        tvDesc = view.findViewById(R.id.tv_desc);
        tvSchemeCompHeader = view.findViewById(R.id.tv_comp_scheme_header);
        tvSchemeCompValue = view.findViewById(R.id.tv_comp_scheme_value);
        tvCurrentSchemeValue = view.findViewById(R.id.tv_current_scheme_value);
        lcSchemeComparisonChart = view.findViewById(R.id.lc_scheme_comparison);
        llYrpContent = view.findViewById(R.id.ll_yrp_content);
        if (mSession.getRiskName() != null && !TextUtils.isEmpty(mSession.getRiskName())) {
            tvRiskProfileValue.setText(mSession.getRiskName());
            llYrpContent.setVisibility(View.GONE);
        } else {
            llYrpContent.setVisibility(View.GONE);
        }

        ivCart = view.findViewById(R.id.iv_cart);
        ivVideoPlay = view.findViewById(R.id.ivVideoPlay);
        tvNoChartData = view.findViewById(R.id.tv_no_cart_data);
        btnShowMoreHolding = view.findViewById(R.id.btn_show_more_top_holding);
        btnShowMoreSector = view.findViewById(R.id.btn_show_more_sector);
        btnShowMoreHolding.setText("Show More");
        btnShowMoreSector.setText("Show More");
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();

        videoCard= view.findViewById(R.id.video_card);
        videoCard.setVisibility(View.GONE);
      //  getLifecycle().addObserver(youTubePlayerView);

        titleText = view.findViewById(R.id.factsheet_video_title);
        descText = view.findViewById(R.id.factsheet_video_desc);


        
    }



    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission() {
        Dexter.withActivity(mActivity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            mApplication.showSnackBar(tvAddress, "All permissions are granted!");
                            createImage();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void getDataFromBundle() {
        if (bundle != null) {

            if (bundle.containsKey("comming_from")) {
                passKey = !TextUtils.isEmpty(bundle.getString("passkey")) ? bundle.getString("passkey") : "";
                exclCode = !TextUtils.isEmpty(bundle.getString("excl_code")) ? bundle.getString("excl_code") : "";
                bId = !TextUtils.isEmpty(bundle.getString("bid")) ? bundle.getString("bid") : "";
                tvSchemeName.setText(!TextUtils.isEmpty(bundle.getString("colorBlue")) ? bundle.getString("colorBlue") : "");
                bundle.putString("hide_icon", "Y");
                bundle_new.putString("object", bundle.getString("object"));
                ivCart.setVisibility(View.GONE);
                rlTransact.setVisibility(View.VISIBLE);
            } else {
                passKey = !TextUtils.isEmpty(bundle.getString("passkey")) ? bundle.getString("passkey") : "";
                exclCode = !TextUtils.isEmpty(bundle.getString("excl_code")) ? bundle.getString("excl_code") : "";
                bId = !TextUtils.isEmpty(bundle.getString("bid")) ? bundle.getString("bid") : "";
                tvSchemeName.setText(!TextUtils.isEmpty(bundle.getString("scheme")) ? bundle.getString("scheme") : "");
                bundle_new.putString("object", bundle.getString("object"));
                try {
                    mJsonObject = new JSONObject(bundle_new.getString("object"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUpCartIcon();
                ivCart.setVisibility(View.VISIBLE);

                rlTransact.setVisibility(View.GONE);

                //  heading.setText(bundle.getString("scheme"));
            }
            callCommulativePerformanceFiveYearApi("3Y");
        }
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_share) {
            onShareOptionClick();
        }
    }

    public void updateCart() {
        try {
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                mTvCart.setVisibility(View.GONE);
            } else {
                mTvCart.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                mTvCart.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setUpAdapter() {
        rvSectorAnalysis.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvSectorAnalysis.setNestedScrollingEnabled(false);
        rvHoldingAnalysis.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvHoldingAnalysis.setNestedScrollingEnabled(false);

        scoreAnalysisAdapter = new ScoreAnalysisAdapter(getActivity(), new ArrayList<JSONObject>());
        rvSectorAnalysis.setAdapter(scoreAnalysisAdapter);
        topHoldingAdapter = new TopHoldingAdapter(getActivity(), new ArrayList<JSONObject>());
        rvHoldingAnalysis.setAdapter(topHoldingAdapter);

    }

    private void setPieChart() {
        String[] colors = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            colors = requireActivity().getResources().getStringArray(R.array.colors);
        }
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        for (int i = 0; i < Config.mGraphValue.size(); i++) {
            Object firstKey = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                firstKey = Objects.requireNonNull(Config.mGraphValue.keySet().toArray())[i];
            }
            String value = Config.mGraphValue.get(firstKey);
            if (value != null) {
                config.addData(new SimplePieInfo(Float.parseFloat(value), Color.parseColor(colors[i]), ""), true);
                tvNoChartData.setVisibility(View.GONE);
            } else {
                tvNoChartData.setVisibility(View.VISIBLE);
            }
        }
        config.startAngle(0.9224089f)
                .selectListener(new OnPieSelectListener() {
                    @Override
                    public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                     /*   tvDesc.setText(String.format(Locale.getDefault(),
                                "  value = %s\n  desc = %s", pieInfo.getValue(), pieInfo.getDesc()));*/
                    }
                })
                .drawText(true)
                .duration(500)
                .textSize(26)
                .focusAlphaType(AnimatedPieViewConfig.FOCUS_WITHOUT_ALPHA)
                .textGravity(AnimatedPieViewConfig.ABOVE)
                .interpolator(new DecelerateInterpolator())
                .legendsWith(mViewGroup, new OnPieLegendBindListener<BasePieLegendsView>() {
                    @Override
                    public BasePieLegendsView onCreateLegendView(int position, IPieInfo info) {
                        return position % 2 == 0 ?
                                DefaultPieLegendsView.newInstance(getActivity())
                                : DefaultPieLegendsView.newInstance(getActivity());
                    }

                    @Override
                    public boolean onAddView(ViewGroup parent, BasePieLegendsView view) {
                        return false;
                    }
                });
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();
    }

    /***********************************************Share option Click operation********************************************/
    private void onShareOptionClick() {
        mLinerFooter.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          requestStoragePermission();
                                      }
                                  },

                100);
    }

    private void createImage() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Factsheet/");

        if (!folder.exists()) {
            folder.mkdir();
        }

        path = folder.getAbsolutePath();
        String schemeName="";
        if (Sname.contains("/")) {
            schemeName = Sname.replace("/", "by");
        }else{
            schemeName=Sname;
        }
        path = path + "/" + "Factsheet-" + schemeName + System.currentTimeMillis() + ".pdf";// path where pdf will be stored


        int totalHeight = svFactSheetContainer.getChildAt(0).getHeight();
        int totalWidth = svFactSheetContainer.getChildAt(0).getWidth();

        //Save bitmap to  below path
        String extr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Factsheet/";
        File file = new File(extr);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileName = "sheet_img_" + ".jpg";
        File myPath = new File(extr, fileName);
        String imagesUri = myPath.getPath();
        FileOutputStream fos = null;
        b = getBitmapFromView(svFactSheetContainer, totalHeight, totalWidth);

        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        createPdf();


    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }


    private void createPdf() {

        PdfDocument document = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            document = new PdfDocument();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(b.getWidth(), b.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();


            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);


            paint.setColor(Color.BLUE);
            canvas.drawBitmap(b, 0, 0, null);
            document.finishPage(page);
            File filePath = new File(path);
            try {
                document.writeTo(new FileOutputStream(filePath));
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("application/pdf");
                Uri uri = FileProvider.getUriForFile(mActivity, mActivity.getApplicationContext().getPackageName() + ".provider", filePath);
                email.putExtra(Intent.EXTRA_STREAM, uri);
                mActivity.startActivity(email);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
            } finally {
                mLinerFooter.setVisibility(View.GONE);
                svFactSheetContainer.destroyDrawingCache();
            }

            // close the document
            document.close();

        }
    }


    /***********************************************************RiskMeter Set Up*************************************************/
    private void setUpRiskMeterGraph(String riskValue) {
        if(!TextUtils.isEmpty(riskValue)){
            cvRiskMeter.setVisibility(View.VISIBLE);
            if (riskValue.equalsIgnoreCase("Below Average")) {
                ivRiskometer.setImageResource(R.mipmap.ic_moderately_low);
                setUpRiskMeterGraphBelowAvgInstructions();
            } else if (riskValue.equalsIgnoreCase("Above Average")) {
                ivRiskometer.setImageResource(R.mipmap.ic_moderately_high);
                setUpRiskMeterGraphAboveAvgInstructions();
            } else if (riskValue.equalsIgnoreCase("Average")) {
                ivRiskometer.setImageResource(R.mipmap.ic_moderate);
                setUpRiskMeterGraphAvgInstructions();
            } else if (riskValue.equalsIgnoreCase("Low")) {
                ivRiskometer.setImageResource(R.mipmap.ic_low);
                setUpRiskMeterGraphLowInstructions();
            } else if (riskValue.equalsIgnoreCase("High")) {
                ivRiskometer.setImageResource(R.mipmap.ic_high);
                setUpRiskMeterGraphHighInstructions();
            }else if (riskValue.equalsIgnoreCase("Very High")) {
                ivRiskometer.setImageResource(R.mipmap.ic_very_high);
                setUpRiskMeterGraphHighInstructions();
            }
        }else{
            cvRiskMeter.setVisibility(View.GONE);
        }

    }

    private void setUpRiskMeterGraphBelowAvgInstructions() {
        SpannableString SpanString = new SpannableString(
                "Investors understand that there principal will be at Below Average risk");
        SpanString.setSpan("Below Average", 53, 66, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBelowAvgRisk)), 53, 66, 0);
        tvRiskoMeterGraphInstructions.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvRiskoMeterGraphInstructions.setSelected(true);
    }

    private void setUpRiskMeterGraphAboveAvgInstructions() {
        SpannableString SpanString = new SpannableString(
                "Investors understand that there principal will be at Above Average risk");
        SpanString.setSpan("Above Average", 53, 66, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAboveAvgRisk)), 53, 66, 0);
        tvRiskoMeterGraphInstructions.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvRiskoMeterGraphInstructions.setSelected(true);
    }

    private void setUpRiskMeterGraphAvgInstructions() {
        SpannableString SpanString = new SpannableString(
                "Investors understand that there principal will be at  Average risk");
        SpanString.setSpan("Average", 53, 62, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAvgRisk)), 53, 62, 0);
        tvRiskoMeterGraphInstructions.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvRiskoMeterGraphInstructions.setSelected(true);
    }

    private void setUpRiskMeterGraphLowInstructions() {
        SpannableString SpanString = new SpannableString(
                "Investors understand that there principal will be at Low risk");
        SpanString.setSpan("Low", 53, 56, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLowRisk)), 53, 56, 0);
        tvRiskoMeterGraphInstructions.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvRiskoMeterGraphInstructions.setSelected(true);
    }

    private void setUpRiskMeterGraphHighInstructions() {
        SpannableString SpanString = new SpannableString(
                "Investors understand that there principal will be at High risk");
        SpanString.setSpan("High", 53, 57, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorHighRisk)), 53, 57, 0);
        tvRiskoMeterGraphInstructions.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvRiskoMeterGraphInstructions.setSelected(true);
    }


    /***************************API Calling**********************/
    public void callFactSheetDetailsApi() {


        String url = Config.Summary_Details;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", passKey);
            jsonObject.put("Exlcode", exclCode);
            jsonObject.put("FormatReq", "Y");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        String Status = jsonObject.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {


                            JSONArray jsonArray = jsonObject.getJSONArray("FactsheetSummaryDetail");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                            tvNavPriceValue.setText(!TextUtils.isEmpty(getResources().getString(R.string.Rs) + " " + jsonObject1.optString("SchemeNAV")) ? getResources().getString(R.string.Rs) + " " + jsonObject1.optString("SchemeNAV") : "");
                            tvFundAge.setText(!TextUtils.isEmpty(jsonObject1.optString("FundAge")) ? jsonObject1.optString("FundAge") : "");
                            tvFundCategory.setText(!TextUtils.isEmpty(jsonObject1.optString("Category")) ? jsonObject1.optString("Category") : "");
                            tvExpenseRatio.setText(!TextUtils.isEmpty(jsonObject1.optString("ExpenseR") + " %") ? jsonObject1.optString("ExpenseR") + " %" : "");
                            tvExitLoad.setText(!TextUtils.isEmpty(jsonObject1.optString("Exitload")) ? jsonObject1.optString("Exitload") : "");
                            tvNavDate.setText(!TextUtils.isEmpty("|  NAV as on " + jsonObject1.optString("NAVDate")) ? "| NAV as on " + jsonObject1.optString("NAVDate") : "");
                            String str = !TextUtils.isEmpty(jsonObject1.optString("FundManager").trim()) ? jsonObject1.optString("FundManager").trim() : "";
                          /*  str = str.replaceAll("[^a-zA-Z0-9]", " ");*/
                            tvFundManagerValue.setText(str);
                            tv_launch_date.setText(!TextUtils.isEmpty(jsonObject1.optString("LaunchDate")) ? jsonObject1.optString("LaunchDate")  : "");
                            setUpRiskMeterGraph(jsonObject1.optString("RsikGrade"));
                            tvFundSize.setText(!TextUtils.isEmpty(jsonObject1.optString("Corpus") + " Cr.") ? jsonObject1.optString("Corpus") + " Cr" : "");

                            tvLongTermBenchMarkTitle.setText(!TextUtils.isEmpty(jsonObject1.optString("BroadBenchmark")) ? jsonObject1.optString("BroadBenchmark") : "");
                            tvShortTermBenchMarkTitle.setText(!TextUtils.isEmpty(jsonObject1.optString("BroadBenchmark")) ? jsonObject1.optString("BroadBenchmark") : "");
                            tvBenchmark.setText(!TextUtils.isEmpty(jsonObject1.optString("Benchmark")) ? jsonObject1.optString("Benchmark") : "NA");
                            tvSchemeCompHeader.setText(!TextUtils.isEmpty(jsonObject1.optString("BroadBenchmark")) ? jsonObject1.optString("BroadBenchmark") : "NA");
                            Equity = jsonObject1.optString("Equity");
                            Debt = jsonObject1.optString("Debt");
                            Other = jsonObject1.optString("Other");
                            FundRate = jsonObject1.optString("FundRate");
                            Weekhigh = jsonObject1.optString("Weekhigh");
                            Weeklow = jsonObject1.optString("Weeklow");
                            Category = jsonObject1.optString("Category");
                            Schemetype = jsonObject1.optString("Schemetype");
                            tvSchemeObjective.setText(!TextUtils.isEmpty(jsonObject1.optString("SchemeDesc")) ? jsonObject1.optString("SchemeDesc") : "");
                            Sname = jsonObject1.optString("SchemeName");
                            MinimunIvestment = jsonObject1.optString("MinimunIvestment");
                            CloseDate = jsonObject1.optString("CloseDate");
                            EntryLoad = jsonObject1.optString("EntryLoad");
                            ExpenseR = jsonObject1.optString("ExpenseR");
                            SNAV = jsonObject1.optString("SchemeNAV");
                            ChangeNAV = jsonObject1.optString("ChangeNAV");
                            NAVDt = jsonObject1.optString("NAVDt");
                            FCMonth = jsonObject1.optString("FCMonth");
                            FCYear = jsonObject1.optString("FCYear");
                            /*  AMCLogo = jsonObject1.optString("AMCLogo");*/

                            if(jsonObject1.has("VideoLink") && !jsonObject1.optString("VideoLink").equals("")){
                               // setVideo(jsonObject1.optString("VideoLink"));
                                videoCard.setVisibility(View.VISIBLE);
                                titleText.setText(jsonObject1.optString("VideoTitle"));
                                descText.setText(jsonObject1.optString("VideoDescription"));
                                mVideoUrl = jsonObject1.optString("VideoLink");
                                //watchYoutubeVideo(jsonObject1.optString("VideoLink"));
                            }


                           // titleText.setText(!TextUtils.isEmpty(jsonObject1.optString("VideoTitle")) ? jsonObject1.optString("VideoTitle") : "");
                           // descText.setText(!TextUtils.isEmpty(jsonObject1.optString("VideoDescription")) ? jsonObject1.optString("VideoDescription") : "");


                            Picasso.get().load(!TextUtils.isEmpty(jsonObject1.optString("AMCLogo")) ? jsonObject1.optString("AMCLogo") : "").placeholder(R.mipmap.tranparent)
                                    .error(R.mipmap.tranparent).into(ivScheme);
                            if (ChangeNAV.contains("-")) {
                                tvNav.setTextColor(Color.parseColor("#FFE53115"));
                            }
                            tvNav.setText(ChangeNAV + "%");
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


                        } else {

                            String ServiceMSG = jsonObject.optString("ServiceMSG");
                            Toast.makeText(mActivity, ServiceMSG, Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                    } finally {

                        callPerformanceAnalysisApi();

                        setPieChart();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    mApplication.showSnackBar(tvBenchmark, getResources().getString(R.string.error_try_again));

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void callPerformanceAnalysisApi() {

        String url = Config.Performance_Analysis;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", passKey);
            jsonObject.put("Exlcode", exclCode);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {

                        String status = jsonObject.optString("Status");
                        if (status.equalsIgnoreCase("True")) {

                            //DataSet();

                            JSONArray jsonArray = jsonObject.getJSONArray("FactsheetSchemePerformanceDetail");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            JSONObject jsonObject2 = jsonArray.getJSONObject(1);
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

                            schemeSixMonth = !TextUtils.isEmpty(Month6Return) ? Month6Return : "" + " %";
                            schemeOneYear = !TextUtils.isEmpty(Year1Return) ? Year1Return : "" + " %";
                            schemeThreeYear = !TextUtils.isEmpty(Year3Return) ? Year3Return : "" + " %";
                            schemeFiveYear = !TextUtils.isEmpty(Year5Return) ? Year5Return : "" + " %";

                            SinceInception = jsonObject1.optString("SinceInception");
                           /* if (Year5Return.contains("-")) {
                                cagr.setText("- " + Year5Return + "%");
                                cagr.setTextColor(Color.parseColor("#FFE53115"));

                            } else {

                                cagr.setText("+ " + Year5Return + "%");
                                cagr.setTextColor(Color.parseColor("#FF51B53F"));
                            }
*/

                            if (Month1Return.contains("0.00")) {

                                tvOneMonth.setText("-");
                            } else {
                                tvOneMonth.setText(Month1Return + "%");

                            }
                            if (Month3Return.contains("0.00")) {

                                tvThreeMonth.setText("-");
                            } else {

                                tvThreeMonth.setText(Month3Return + "%");
                            }
                            if (Month6Return.contains("0.00")) {

                                tvSixMonth.setText("-");


                            } else {
                                tvSixMonth.setText(Month6Return + "%");
                            }
                            if (Year1Return.contains("0.00")) {
                                tvYearOne.setText("-");

                            } else {

                                tvYearOne.setText(Year1Return + "%");
                            }
                            if (Year2Return.contains("0.00")) {

                                tvYearTwo.setText("-");

                            } else {

                                tvYearTwo.setText(Year2Return + "%");
                            }
                            if (Year3Return.contains("0.00")) {

                                tvYearThree.setText("-");
                            } else {

                                tvYearThree.setText(Year3Return + "%");
                            }
                            if (Year5Return.contains("0.00")) {

                                tvYearFive.setText("-");

                            } else {

                                tvYearFive.setText(Year5Return + "%");
                            }
                            if (Year10Return.contains("0.00")) {
                                tvYearTen.setText("-");
                            } else {
                                tvYearTen.setText(Year10Return + "%");
                            }

                            String Month1Return2 = jsonObject2.optString("Month1Return");
                            String Month3Return2 = jsonObject2.optString("Month3Return");
                            String Month6Return2 = jsonObject2.optString("Month6Return");
                            String Year1Return2 = jsonObject2.optString("Year1Return");
                            String Year2Return2 = jsonObject2.optString("Year2Return");
                            String Year3Return2 = jsonObject2.optString("Year3Return");
                            String Year5Return2 = jsonObject2.optString("Year5Return");
                            String Year10Return2 = jsonObject2.optString("Year10Return");

                            benchMarkSixMonth = !TextUtils.isEmpty(Month6Return2) ? Month6Return2 : "" + " %";
                            benchMarkOneYr = !TextUtils.isEmpty(Year1Return2) ? Year1Return2 : "" + " %";
                            benchMarkThreeYr = !TextUtils.isEmpty(Year3Return2) ? Year3Return2 : "" + " %";
                            benchMarkFiveYr = !TextUtils.isEmpty(Year5Return2) ? Year5Return2 : "" + " %";
                            setUpComparisonSchemeAdapter();
                            if (Month1Return2.contains("0.00")) {
                                tvBenchmarkOneMonth.setText("-");
                            } else {
                                tvBenchmarkOneMonth.setText(Month1Return2 + "%");

                            }
                            if (Month3Return2.contains("0.00")) {

                                tvBenchmarkThreeMonth.setText("-");
                            } else {

                                tvBenchmarkThreeMonth.setText(Month3Return2 + "%");
                            }
                            if (Month6Return2.contains("0.00")) {

                                tvBenchmarkSixMonth.setText("-");

                            } else {
                                tvBenchmarkSixMonth.setText(Month6Return2 + "%");
                            }
                            if (Year1Return2.contains("0.00")) {
                                tvBenchmarkYearOne.setText("-");

                            } else {

                                tvBenchmarkYearOne.setText(Year1Return2 + "%");
                            }
                            if (Year2Return2.contains("0.00")) {

                                tvBenchmarkYearTwo.setText("-");

                            } else {

                                tvBenchmarkYearTwo.setText(Year2Return2 + "%");
                            }
                            if (Year3Return2.contains("0.00")) {

                                tvBenchmarkYearThree.setText("-");
                            } else {

                                tvBenchmarkYearThree.setText(Year3Return2 + "%");
                            }
                            if (Year5Return2.contains("0.00")) {

                                tvBenchmarkYearFive.setText("-");

                            } else {

                                tvBenchmarkYearFive.setText(Year5Return2 + "%");
                            }
                            if (Year10Return2.contains("0.00")) {
                                tvBenchmarkYearTen.setText("-");
                            } else {
                                tvBenchmarkYearTen.setText(Year10Return2 + "%");
                            }

                        } else {

                            String ServiceMSG = jsonObject.optString("ServiceMSG");
                            Toast.makeText(mActivity, ServiceMSG, Toast.LENGTH_SHORT).show();
                        }
                        tvSchemeCompValue.setText(!TextUtils.isEmpty(benchMarkOneYr + " %") ? benchMarkOneYr + " %" : "");
                        tvCurrentSchemeValue.setText(!TextUtils.isEmpty(schemeOneYear + " %") ? schemeOneYear + " %" : "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    mApplication.showSnackBar(tvBenchmark, getResources().getString(R.string.error_try_again));


                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }


    }


    public void callSectorAnalysisApi() {

        String url = Config.Sector_Analysis;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, passKey);
            jsonObject.put("Exlcode", exclCode);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    mDataListSector = new ArrayList<>();
                    try {

                        String Status = jsonObject.optString("Status");

                        if (Status.equalsIgnoreCase("True")) {
                            JSONArray FactsheetSectorAnalysisDetail = jsonObject.getJSONArray("FactsheetSectorAnalysisDetail");
                            if (FactsheetSectorAnalysisDetail.length() >= 6) {
                                btnShowMoreSector.setVisibility(View.VISIBLE);
                            } else {
                                btnShowMoreSector.setVisibility(View.GONE);
                            }


                            for (int i = 0; i < FactsheetSectorAnalysisDetail.length(); i++) {
                                JSONObject jsonObject1 = FactsheetSectorAnalysisDetail.getJSONObject(i);
                                mDataListSector.add(jsonObject1);
                            }
                            updateTopHolding(false, "sector");


                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.alert);

                        dialog.findViewById(R.id.okay).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialog.dismiss();
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                    } else if (error instanceof NoConnectionError) {

                        Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void callTopHoldingApi() {
        String url = Config.Top_Holdings;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, passKey);
            jsonObject.put("Exlcode", exclCode);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        String status = jsonObject.optString("Status");
                        if (status.equalsIgnoreCase("True")) {

                            JSONArray FactsheetTopHoldingsDetail = jsonObject.optJSONArray("FactsheetTopHoldingsDetail");
                            if (FactsheetTopHoldingsDetail.length() >= 6) {
                                btnShowMoreHolding.setVisibility(View.VISIBLE);
                            } else {
                                btnShowMoreHolding.setVisibility(View.GONE);
                            }
                            dataList = new ArrayList<>();
                            for (int i = 0; i < FactsheetTopHoldingsDetail.length(); i++) {
                                JSONObject jsonObject1 = FactsheetTopHoldingsDetail.optJSONObject(i);
                                dataList.add(jsonObject1);
                            }
                            updateTopHolding(false, "topHolding");
                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void callCommulativePerformanceFiveYearApi(final String performancePeriod) {

        String url = Config.FACTSHEET_COMPARISON_MINIFY;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, passKey);
            jsonObject.put("Exlcode", exclCode);
            jsonObject.put("PerformancePeriod", performancePeriod);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        fiveYearStatus = jsonObject.optString("Status");
                        if (fiveYearStatus.equalsIgnoreCase("True")) {

                            mJSONArrayFiveYearMonthScheme = jsonObject.optJSONArray("FSCumulativePerformanceMinifyDetail");
                            getDataByDuration(36);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***************************Graph Data Setup**********************************/
    private void setUpSchemeComparisonHeaderData() {
        if (selectedScheme.equalsIgnoreCase("6M")) {
            tvSchemeCompValue.setText(benchMarkSixMonth + " %");
            tvCurrentSchemeValue.setText(schemeSixMonth + " %");
        } else if (selectedScheme.equalsIgnoreCase("1Y")) {
            tvSchemeCompValue.setText(benchMarkOneYr + " %");
            tvCurrentSchemeValue.setText(schemeOneYear + " %");
        } else if (selectedScheme.equalsIgnoreCase("3Y")) {
            tvSchemeCompValue.setText(benchMarkThreeYr + " %");
            tvCurrentSchemeValue.setText(schemeThreeYear + " %");
        } else if (selectedScheme.equalsIgnoreCase("5Y")) {
            tvSchemeCompValue.setText(benchMarkFiveYr + " %");
            tvCurrentSchemeValue.setText(schemeFiveYear + " %");
        } else {
            selectedScheme = "1Y";
            tvSchemeCompValue.setText(benchMarkOneYr + " %");
            tvCurrentSchemeValue.setText(schemeOneYear + " %");
        }
    }

    public void setLineGraph(List<JSONObject> graphDatalist) {
        lcSchemeComparisonChart.getDescription().setEnabled(false);
        lcSchemeComparisonChart.setDrawGridBackground(false);
        lcSchemeComparisonChart.getAxisLeft().setDrawGridLines(true);
        lcSchemeComparisonChart.getXAxis().setDrawGridLines(true);

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            lcSchemeComparisonChart.getAxisLeft().setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor)); // left y-axis
            lcSchemeComparisonChart.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor)); // left y-axis
            lcSchemeComparisonChart.getLegend().setTextColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryTextColor)); // left y-axis
        } else {
            lcSchemeComparisonChart.getAxisLeft().setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor)); // left y-axis
            lcSchemeComparisonChart.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor)); // left y-axis
            lcSchemeComparisonChart.getLegend().setTextColor(ContextCompat.getColor(mActivity, R.color.lightPrimaryTextColor)); // left y-axis
        }
        lcSchemeComparisonChart.setPinchZoom(true);
        lcSchemeComparisonChart.setDoubleTapToZoomEnabled(true);
        lcSchemeComparisonChart.setScaleEnabled(true);
        lcSchemeComparisonChart.getLegend().setWordWrapEnabled(true);
        Legend l = lcSchemeComparisonChart.getLegend();
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
        if (getActivity() != null) {
            mMarkerView = new MyMarkerView(getContext(), R.layout.custom_marker_view, "currentFY");
            mMarkerView.setChartView(lcSchemeComparisonChart);
            lcSchemeComparisonChart.setMarker(mMarkerView);
        }

        XAxis xAxis = lcSchemeComparisonChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String xAxisName = "";

                try {

                    float[] entryValue = axis.mEntries;
                    int totalCount = axis.mEntryCount;
                    if (entryValue[0] == value) {
                        JSONObject jsonObject = AppApplication.dataList.get(0);
                        xAxisName = jsonObject.optString("DateSeries");

                    } else if (totalCount <= 6) {
                        if (entryValue[(totalCount - 1)] == value) {
                            JSONObject jsonObject = AppApplication.dataList.get(AppApplication.dataList.size() - 1);
                            xAxisName = jsonObject.optString("DateSeries");

                        }
                    } else if (totalCount > 6) {
                        if (entryValue[(totalCount - 2)] == value) {
                            JSONObject jsonObject = AppApplication.dataList.get(AppApplication.dataList.size() - 1);
                            xAxisName = jsonObject.optString("DateSeries");

                        }
                    } else {
                        if (entryValue[(totalCount - 1)] == value) {
                            JSONObject jsonObject = AppApplication.dataList.get(AppApplication.dataList.size() - 1);
                            xAxisName = jsonObject.optString("DateSeries");

                        }
                    }

                } catch (Exception e) {
                    System.out.println();
                }

                return xAxisName;
            }
        });

        YAxis leftAxis = lcSchemeComparisonChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f);
        // this replaces setStartAtZero(true)
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axisBase) {
                String yLeftAxisName = "";

                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                format.setMinimumFractionDigits(0);
                format.setMaximumFractionDigits(0);

                yLeftAxisName = format.format(value);

                return yLeftAxisName;
            }
        });


        YAxis rightAxis = lcSchemeComparisonChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setEnabled(false);

        // set data
        lcSchemeComparisonChart.setData((generateDataLine(graphDatalist)));

        lcSchemeComparisonChart.animateX(1000);
        lcSchemeComparisonChart.setVisibility(View.VISIBLE);
    }

    private LineData generateDataLine(List<JSONObject> graphDatalist) {
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        ArrayList<Float> list = new ArrayList<>();

        if (getActivity() != null) {
            try {

                ArrayList<Entry> values1 = new ArrayList<>();
                ArrayList<Entry> values2 = new ArrayList<>();
                ArrayList<JSONObject> dataList = new ArrayList<>();
                if (graphDatalist.size() > 0) {
                    for (int i = 0; i < graphDatalist.size(); i++) {
                        JSONObject jsonObject1 = graphDatalist.get(i);
                        dataList.add(jsonObject1);

                        String value1 = jsonObject1.optString("SchemeAmount");
                        values1.add(new Entry(i, Float.parseFloat(value1)));

                        String value2 = jsonObject1.optString("SchemeBenchmarkAmount");
                        float floatValue1 = Float.parseFloat(value1);
                        float floatValue2 = Float.parseFloat(value2);
                        list.add(floatValue1);
                        list.add(floatValue2);

                        if (i == (graphDatalist.size() - 1)) {
                            Float minValue = Collections.min(list);
                            YAxis leftAxis = lcSchemeComparisonChart.getAxisLeft();
                            minValue = (minValue - (minValue * 10 / 100));
                            leftAxis.setAxisMinimum(minValue);
                        }

                        if (!jsonObject1.optString("SchemeBenchmarkAmount").equalsIgnoreCase("null")) {
                            values2.add(new Entry(i, Float.parseFloat(value2)));
                        }


                    }
                    AppApplication.GInvestmentList.clear();
                    AppApplication.GCurrentValueList.clear();
                    AppApplication.dataList.clear();

                    AppApplication.GInvestmentList.addAll(values1);
                    AppApplication.GCurrentValueList.addAll(values2);
                    AppApplication.dataList.addAll(dataList);

                    LineDataSet d1 = new LineDataSet(values1, "Scheme");
                    d1.setLineWidth(1.5f);
                    d1.setCircleRadius(1.5f);
                    d1.setColor(ContextCompat.getColor(getActivity(), R.color.colorPurple));
                    d1.setHighLightColor(ContextCompat.getColor(getActivity(), R.color.colorPurple));
                    d1.setCircleColor(ContextCompat.getColor(getActivity(), R.color.colorTransparent));
                    d1.setCircleColorHole(ContextCompat.getColor(getActivity(), R.color.colorTransparent));
                    d1.setDrawValues(false);
                    sets.add(d1);

                    LineDataSet d2 = new LineDataSet(values2, "Benchmark");
                    d2.setLineWidth(1.5f);
                    d2.setCircleRadius(1.5f);
                    d2.setColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
                    d2.setHighLightColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
                    d2.setCircleColor(ContextCompat.getColor(getActivity(), R.color.colorTransparent));
                    d2.setCircleColorHole(ContextCompat.getColor(getActivity(), R.color.colorTransparent));
                    d2.setDrawValues(false);

                    sets.add(d2);
                    mJsonObjectSchemeComp = graphDatalist.get(0);
                    tvSchemeCompFooterDate.setText(mJsonObjectSchemeComp.optString("DateSeries"));
                } else {
                    //mLinerLineChart.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return new LineData(sets);
    }


    @Override
    public void onSchemeAgeClick(int position) {
        switch (position) {
            case 0:

                selectedScheme = "6M";
                getDataByDuration(6);

               /* if (mJSONArraySixMonthScheme.length() > 0) {
                    setLineGraph(mJSONArraySixMonthScheme);
                }*/
                setUpSchemeComparisonHeaderData();

                break;
            case 1:
                selectedScheme = "1Y";
                getDataByDuration(12);

               /* if (mJSONArrayOneYearMonthScheme.length() > 0) {
                    setLineGraph(mJSONArrayOneYearMonthScheme);
                }*/
                setUpSchemeComparisonHeaderData();

                break;
            case 2:
                selectedScheme = "3Y";
                getDataByDuration(36);
                /*   if (mJSONArrayThreeYearMonthScheme.length() > 0) {
                 *//*    if(threeYearApiStatus.equalsIgnoreCase("True"))*//*
                    setLineGraph(mJSONArrayThreeYearMonthScheme);
                }*/
                setUpSchemeComparisonHeaderData();

                break;
            case 3:
                selectedScheme = "5Y";
                getDataByDuration(60);
                /*  if (mJSONArrayFiveYearMonthScheme.length() > 0) {
                 *//*  if(fiveYearStatus.equalsIgnoreCase("True"))*//*
                    setLineGraph(mJSONArrayFiveYearMonthScheme);
                }*/
                setUpSchemeComparisonHeaderData();

                break;

        }
    }

    private void getDataByDuration(int month) {

        try {
            List<JSONObject> list = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -month);
            Date exactDate = calendar.getTime();
            String strdate = formatter.format(exactDate);
            System.out.println(strdate);

            long exactTimeInMilliSecond = exactDate.getTime();

            for (int i = 0; i < mJSONArrayFiveYearMonthScheme.length(); i++) {
                JSONObject jsonObject = mJSONArrayFiveYearMonthScheme.optJSONObject(i);
                String objDate = jsonObject.optString("DateSeries");
                Date date = formatter.parse(objDate);
                long objTimeInMilliSecond = date.getTime();
                if (objTimeInMilliSecond >= exactTimeInMilliSecond) {
                    list.add(jsonObject);
                }

            }

            setLineGraph(list);
        } catch (Exception e) {

        }

    }

    private void setUpCartIcon() {
        mAddToCartList = new ArrayList<>();

        try {

            if (!mSession.getAddToCartList().equals("[]")) {
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mAddToCartList.add(jsonObject);
                    if (mJsonObject != null) {
                        if (mJsonObject.optString("Exlcode").equals(jsonObject.optString("Exlcode"))) {

                            ivCart.setImageResource(R.mipmap.ic_factsheet_cart_done);
                            mIsAlreadyAdded = true;
                            break;
                        } else {

                            ivCart.setImageResource(R.mipmap.ic_factsheet_add_cart);
                            mIsAlreadyAdded = false;
                        }
                    }
                }
            } else {
                ivCart.setImageResource(R.mipmap.ic_factsheet_add_cart);
                mIsAlreadyAdded = false;
            }
            ivCart.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_cart) {
            onCartBtnClick();
        } else if (view.getId() == R.id.iv_share) {
            onShareOptionClick();
        } else if (view.getId() == R.id.rl_transact) {
            mActivity.displayViewOther(47, bundle);
        } else if (view.getId() == R.id.ivVideoPlay) {
            watchYoutubeVideo(mVideoUrl);

        } else if (view.getId() == R.id.btn_show_more_sector) {
            if (btnShowMoreSector.getText().toString().equalsIgnoreCase("Show More")) {
                updateTopHolding(true, "sector");
                btnShowMoreSector.setText("Show Less");
            } else {
                updateTopHolding(false, "sector");
                btnShowMoreSector.setText("Show More");
            }
        } else if (view.getId() == R.id.btn_show_more_top_holding) {
            if (btnShowMoreHolding.getText().toString().equalsIgnoreCase("Show More")) {
                updateTopHolding(true, "topHolding");
                btnShowMoreHolding.setText("Show Less");
            } else {
                updateTopHolding(false, "topHolding");
                btnShowMoreHolding.setText("Show More");
            }
        }
    }

    private void updateTopHolding(Boolean hasShowAll, String dataType) {
        if (dataType.equalsIgnoreCase("sector")) {
            if (hasShowAll) {
                scoreAnalysisAdapter.updateList(mDataListSector, dataType);
            } else if (mDataListSector.size() <= 6) {
                scoreAnalysisAdapter.updateList(mDataListSector, dataType);
            } else {
                ArrayList<JSONObject> newList = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    JSONObject jsonObject = mDataListSector.get(i);
                    newList.add(jsonObject);
                }
                scoreAnalysisAdapter.updateList(newList, dataType);
            }
        } else {
            if (hasShowAll) {
                topHoldingAdapter.updateList(dataList, dataType);
            } else if (dataList.size() <= 6) {
                topHoldingAdapter.updateList(dataList, dataType);
            } else {
                ArrayList<JSONObject> newList = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    JSONObject jsonObject = dataList.get(i);
                    newList.add(jsonObject);
                }
                topHoldingAdapter.updateList(newList, dataType);
            }
        }


    }
}

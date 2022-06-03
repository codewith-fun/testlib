package investwell.broker.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.iw.acceleratordemo.BuildConfig;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import investwell.broker.fragment.FragClientSearch;
import investwell.broker.fragment.FragHomeBroker;
import investwell.client.activity.AppApplication;
import investwell.client.activity.SplashActivity;
import investwell.client.fragment.flavour.SetFlavourFrag;
import investwell.client.fragment.goalbased.Fragment.FragQuestions;
import investwell.client.fragment.help.FragHelpHome;
import investwell.client.fragment.home.FragHomeClient;
import investwell.client.fragment.home.FragNotification;
import investwell.client.fragment.language.LanguageChangeFragment;
import investwell.client.fragment.others.BseProfileDetailFrag;
import investwell.client.fragment.others.OutstandingUnitFrag;
import investwell.client.fragment.others.SendMailFragment;
import investwell.client.fragment.profile.FragBrokerProfile;
import investwell.client.fragment.profile.FragProfileIncomplete;
import investwell.client.fragment.profile.FragProfileList;
import investwell.client.fragment.profile.FragProfileSettings;
import investwell.client.fragment.user_info.ChangePasswordFragment;
import investwell.common.applock.FragChangeAppSecurity;
import investwell.common.basic.BaseActivity;
import investwell.common.calculator.fragment.FragEducationCalculator;
import investwell.common.calculator.fragment.FragEmiCalculator;
import investwell.common.calculator.fragment.FragMarriegeCalculator;
import investwell.common.calculator.fragment.FragRetirmentCalculator;
import investwell.common.calculator.fragment.FragSip;
import investwell.common.calculator.fragment.FragSipDelayCost;
import investwell.common.calculator.fragment.FragSipStepup;
import investwell.common.calculator.fragment.FragSpareMoneyCal;
import investwell.common.calculator.fragment.FragTaxInvestmentCal;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextViewBold;


public class BrokerActivity extends BaseActivity implements View.OnClickListener {
    // tags used to attach the fragments
    private static final String TAG_HOME = "ic_bottombar_home_inactive";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_PROFILE = "profile";
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    public List<JSONObject> mSelectedCartsList;
    int REQUEST_APP_UPDATE = 17362;
    private AppSession mSession;
    private AppUpdateManager appUpdateManager;
    private Toolbar toolbar;
    TextView tvDashboard, tvProfile, tvSearch;
    private CustomTextViewBold tvToolBarTitle;
    private ImageView ivToolbarLogo, ivDashBoard, ivProfile, ivSearch;
    private AppBarLayout appbar;
    private CustomTextViewBold tvCartBadge, tv_notification_badge;
    private String[] activityTitles;
    private LinearLayout llDashBoard, llProfile, llSearch;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private View vDashboard, vProfile, vSearch;
    private LinearLayout ll_bottomBar_main_container;
    private AppApplication mApplication;
    private ImageView ivClientSearch;
    private ImageView ivNotification;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

 /*   @Override
    public void onBackPressed() {

        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            return;
        }
        super.onBackPressed();
    }*/

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();



        if ((fragment instanceof FragHomeClient) && (mSession.getLoginType().equals("Broker")
                || mSession.getLoginType().equals("SubBroker")
                || mSession.getLoginType().equals("RM") ||mSession.getLoginType().equalsIgnoreCase("Zone")
                ||mSession.getLoginType().equalsIgnoreCase("Region")
                ||mSession.getLoginType().equalsIgnoreCase("Branch"))) {
            finish();

        } else if ((fragment instanceof FragHomeBroker) || (fragment instanceof FragClientSearch) || (fragment instanceof FragProfileSettings)) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                //finish();
            } else if (navItemIndex == 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                this.finishAffinity();
            }
        } else if (fragment instanceof FragNotification) {

            AppApplication.notification = "";
            Intent myIntent = new Intent("FBR-IMAGE");
            myIntent.putExtra("action", AppApplication.notification);
            this.sendBroadcast(myIntent);
            super.onBackPressed();

        } else if (fragmentCount > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    public <T> void setMainVisibility(Fragment fragment, T object) {
        if (fragment instanceof FragHomeBroker
                || fragment instanceof FragProfileSettings || fragment instanceof FragClientSearch) {
            appbar.setVisibility(View.VISIBLE);

            if (mSession.getHasLoging()) {
                ll_bottomBar_main_container.setVisibility(View.VISIBLE);
            } else {
                ll_bottomBar_main_container.setVisibility(View.GONE);
            }
        } else {
            appbar.setVisibility(View.GONE);
            ll_bottomBar_main_container.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_broker);

        setInitializer();
        setUpInitialUi(savedInstanceState);
        setListeners();
        showSelectedDashboard();
        notificationCount();
        callLastNAVupdateApi();
        if (!mSession.getHasNotificationEnable()) {
            addAutoStartup();
        }

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
    private void setInitializer(){
        if (!BuildConfig.DEBUG) {
            checkUpdate();
        }
        mApplication = (AppApplication) getApplication();
        mSelectedCartsList = new ArrayList<>();
        mSession = AppSession.getInstance(this);

        toolbar = findViewById(R.id.toolbar_main);
        appbar = findViewById(R.id.appbar);
        tvToolBarTitle = findViewById(R.id.tv_toolbar_title);
        ivToolbarLogo = findViewById(R.id.iv_app_logo);
        llDashBoard = findViewById(R.id.ll_home);
        llSearch = findViewById(R.id.ll_search);
        llProfile = findViewById(R.id.ll_profile);
        mHandler = new Handler();
        ivNotification = findViewById(R.id.iv_notification);
        // load toolbar titles from string resources
        /*activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);*/
        vDashboard = findViewById(R.id.divider_home);
        vSearch = findViewById(R.id.divider_search);
        tv_notification_badge = findViewById(R.id.tv_notification_badge);
        vProfile = findViewById(R.id.divider_profile);
        tvDashboard = findViewById(R.id.tv_home);
        tvSearch = findViewById(R.id.tv_search);
        tvProfile = findViewById(R.id.tv_profile);
        ivDashBoard = findViewById(R.id.iv_home);
        ivSearch = findViewById(R.id.iv_search);
        ivProfile = findViewById(R.id.iv_profile);
        ll_bottomBar_main_container = findViewById(R.id.ll_bottomBar_main_container);
    }


    private void setListeners() {
        llProfile.setOnClickListener(this);
        llDashBoard.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        findViewById(R.id.ivContactUs).setOnClickListener(this);
        ivNotification.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_home) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            showSelectedDashboard();
            loadHomeFragment();
        } else if (id == R.id.ll_search) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_SEARCH;
            showSelectedSearch();
            loadHomeFragment();
        } else if (id == R.id.ll_profile) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_PROFILE;
            showSelectedProfile();
            loadHomeFragment();
        } else if (id == R.id.iv_notification) {
            displayViewOther(85, null);
        } else if (id == R.id.ivContactUs) {
            displayViewOther(57, null);
        }

    }


    /***************************************************************
     * This method sets Up Initial UI as per the check Operation
     * ****************************************************************/
    private void setUpInitialUi(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            showBrokerUi();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null &&bundle.containsKey("comming_from") && getIntent().getStringExtra("comming_from").equalsIgnoreCase("home")){
                navItemIndex = 1;
                CURRENT_TAG = TAG_SEARCH;
                showSelectedSearch();
                loadHomeFragment();
            }else {
                loadHomeFragment();
            }
        }
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
/*
        setToolbarTitle();
*/

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = clientHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.fl_main_container, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment clientHomeFragment() {
        Fragment fragment = null;

        switch (navItemIndex) {
            case 0:
                showSelectedDashboard();
                fragment = new FragHomeBroker();
                return fragment;

            case 1:
                showSelectedSearch();
                fragment = new FragClientSearch();
                return fragment;

            case 2:
                showSelectedProfile();
                fragment = new FragProfileSettings();
                return fragment;


            default:
                return new FragHomeBroker();
        }
    }

   /* private void setToolbarTitle() {
        tvToolBarTitle.setText(activityTitles[navItemIndex]);
    }*/


    /************
     * Method displays various fragment as per the conditions/cases satisfied
     * @param position specify the position
     * @param bundle provide the bundle object
     */
    public void displayViewOther(int position, Bundle bundle) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FragHomeBroker();
                fragment.setArguments(bundle);
                showBrokerUi();
                break;

            case 16:
                fragment = new FragProfileIncomplete();
                fragment.setArguments(bundle);
                break;

            case 23:
                fragment = new SendMailFragment();
                fragment.setArguments(bundle);
                break;

            case 36:
                fragment = new FragProfileList();
                fragment.setArguments(bundle);
                break;


            case 57:
                fragment = new FragHelpHome();
                fragment.setArguments(bundle);
                break;


            case 38:
                fragment = new ChangePasswordFragment();
                fragment.setArguments(bundle);
                break;




            case 56:
                fragment = new FragChangeAppSecurity();
                fragment.setArguments(bundle);
                break;

            case 62:
                fragment = new FragQuestions();
                fragment.setArguments(bundle);
                break;

            case 73:
                fragment = new SetFlavourFrag();
                fragment.setArguments(bundle);
                break;

            case 80:
                fragment = new BseProfileDetailFrag();
                fragment.setArguments(bundle);
                break;

            case 81:
                fragment = new OutstandingUnitFrag();
                fragment.setArguments(bundle);
                break;
            case 85:
                fragment = new FragNotification();
                fragment.setArguments(bundle);
                break;
            case 92:
                fragment=new LanguageChangeFragment();
                fragment.setArguments(bundle);
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (fragment != null) {
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.fl_main_container, fragment, "" + count).commit();
        }
    }


    /*************************************************
     * Method called to calculate notification count
     ***********************************************************/
    public void notificationCount() {
        try {
            if (!mSession.getNotification().isEmpty()) {
                JSONArray jsonArray = new JSONArray(mSession.getNotification());
                tv_notification_badge.setVisibility(View.VISIBLE);
                tv_notification_badge.setText("" + jsonArray.length());
            } else {
                tv_notification_badge.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**************************************************************************
     * Method Displays various Financial Tools/calculators used as per cases satisfied
     * @param position specify the position
     * @param bundle provide the bundle object
     **********************************************************************/
    public void displayFinancialCalculators(int position, Bundle bundle) {
        Fragment fragment = null;
        switch (position) {
            case 0:

                break;

            case 1:
                fragment = new FragSip();
                fragment.setArguments(bundle);
                break;


            case 3:
                fragment = new FragSipStepup();
                fragment.setArguments(bundle);
                break;

            case 4:
                fragment = new FragEmiCalculator();
                fragment.setArguments(bundle);
                break;

            case 5:
                fragment = new FragSipDelayCost();
                fragment.setArguments(bundle);
                break;

            case 6:
                fragment = new FragRetirmentCalculator();
                fragment.setArguments(bundle);
                break;

            case 7:
                fragment = new FragEducationCalculator();
                fragment.setArguments(bundle);
                break;

            case 8:
                fragment = new FragMarriegeCalculator();
                fragment.setArguments(bundle);
                break;
            case 9:
                fragment = new FragTaxInvestmentCal();
                fragment.setArguments(bundle);
                break;
            case 10:
                fragment = new FragSpareMoneyCal();
                fragment.setArguments(bundle);
                break;


        }
        if (position == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out).replace(R.id.fl_main_container, fragment).commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragment != null) {
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out).addToBackStack(null).replace(R.id.fl_main_container, fragment).commit();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);

        if (requestCode == REQUEST_APP_UPDATE) {
            if (resultCode == RESULT_OK) {
                // checkUpdate();
                startActivity(new Intent(getBaseContext(), SplashActivity.class));

            }
        }
        if (requestCode == 300) {
            if (fragment instanceof FragBrokerProfile || fragment instanceof FragProfileSettings) {
                fragment.onActivityResult(requestCode, resultCode, result);
            }
        }


    }

    /****************************************************
     * Method shows a common dialog throughout the app
     * @param context provide the context
     * @param title  specify the title
     * @param message specify the message
     *********************************************************/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showCommonDialog(Context context, String title, String message) {
        if (getWindow() != null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.comom_dialog, null);
            dialogBuilder.setView(dialogView);
            final AlertDialog alertDialog = dialogBuilder.create();
            if (alertDialog != null && alertDialog.isShowing()) return;

            LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);

            tvTitle.setText(title);
            tvMessage.setText(message);

            CustomButton tvOk = dialogView.findViewById(R.id.tvOk);
            RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (alertDialog != null) {
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }

            if (Build.VERSION.SDK_INT >= 21) {
                linerMain.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background_inset));
                relSubMenu.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_header_background));
                GradientDrawable drawable = (GradientDrawable) relSubMenu.getBackground();
                drawable.setColor(ContextCompat.getColor(context, R.color.colorGrey_300));
            } else {
                relSubMenu.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey_300));
            }


            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
            });
            if (alertDialog != null) {
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                });
            }

            if (alertDialog != null) {
                alertDialog.setCancelable(false);
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                } else {
                    alertDialog.show();
                }
            }


        }
    }

    /********************************************************************
     * Method converts currency to specified currency format
     * @param currency provide the currency
     * @param editText  provide the editext element
     **************************************************************************/
    public void convertIntoCurrencyFormat(String currency, EditText editText) {
        try {
            Format format = NumberFormat.getNumberInstance(new Locale("en", "in"));
            String str = format.format(Double.parseDouble(currency));
            editText.setText(str);
            editText.setSelection(editText.getText().toString().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /********************************************************************
     * Method converts currency to specified currency format
     * @param currency provide the currency
     * @param editText  provide the editext element
     **************************************************************************/
    public void convertIntoIndianCurrencyFormat(int currency, EditText editText) {
        try {
            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            String strAmount = format.format(currency);
            String[] resultAmount = strAmount.split("\\.", 0);
            editText.setText(resultAmount[0]);

            editText.setSelection(editText.getText().toString().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showBrokerUi() {
        llDashBoard.setVisibility(View.VISIBLE);
        llSearch.setVisibility(View.VISIBLE);
        llProfile.setVisibility(View.VISIBLE);

    }


    private void showSelectedDashboard() {
        vDashboard.setVisibility(View.VISIBLE);
        vProfile.setVisibility(View.GONE);
        vSearch.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            tvDashboard.setTextColor(getResources().getColor(R.color.darkColorAccent));
            ivDashBoard.setColorFilter(getResources().getColor(R.color.darkColorAccent));
        } else {
            tvDashboard.setTextColor(getResources().getColor(R.color.colorAccent));
            ivDashBoard.setColorFilter(getResources().getColor(R.color.colorAccent));
        }
        tvProfile.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvSearch.setTextColor(getResources().getColor(R.color.colorGrey_400));
        ivProfile.setColorFilter(getResources().getColor(R.color.colorGrey_400));
        ivSearch.setColorFilter(getResources().getColor(R.color.colorGrey_400));
    }

    private void showSelectedProfile() {
        vDashboard.setVisibility(View.GONE);
        vProfile.setVisibility(View.VISIBLE);
        vSearch.setVisibility(View.GONE);
        tvDashboard.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvSearch.setTextColor(getResources().getColor(R.color.colorGrey_400));
        ivDashBoard.setColorFilter(getResources().getColor(R.color.colorGrey_400));
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            tvProfile.setTextColor(getResources().getColor(R.color.darkColorAccent));
            ivProfile.setColorFilter(getResources().getColor(R.color.darkColorAccent));
        } else {
            tvProfile.setTextColor(getResources().getColor(R.color.colorAccent));
            ivProfile.setColorFilter(getResources().getColor(R.color.colorAccent));
        }
        ivSearch.setColorFilter(getResources().getColor(R.color.colorGrey_400));
    }

    private void showSelectedSearch() {
        vDashboard.setVisibility(View.GONE);
        vProfile.setVisibility(View.GONE);
        vSearch.setVisibility(View.VISIBLE);
        tvDashboard.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvProfile.setTextColor(getResources().getColor(R.color.colorGrey_400));
        ivDashBoard.setColorFilter(getResources().getColor(R.color.colorGrey_400));
        ivProfile.setColorFilter(getResources().getColor(R.color.colorGrey_400));
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            tvSearch.setTextColor(getResources().getColor(R.color.darkColorAccent));
            ivSearch.setColorFilter(getResources().getColor(R.color.darkColorAccent));
        } else {
            tvSearch.setTextColor(getResources().getColor(R.color.colorAccent));
            ivSearch.setColorFilter(getResources().getColor(R.color.colorAccent));
        }
    }


    /**********************************************************************************
     * Method Shows a kind of alert dialog which asks user to enable/disable notifications
     * @param intent provide the intent object
     **********************************************************/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void dialogNotification(final Intent intent) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BrokerActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_notification, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);


        CustomButton yes_btn = dialogView.findViewById(R.id.yes_btn);
        CustomButton no_btn = dialogView.findViewById(R.id.no_btn);

        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            linerMain.setBackground(ContextCompat.getDrawable(this, R.drawable.dialog_background_inset));
            relSubMenu.setBackground(ContextCompat.getDrawable(this, R.drawable.dialog_header_background));
            GradientDrawable drawable = (GradientDrawable) relSubMenu.getBackground();
            drawable.setColor(ContextCompat.getColor(this, R.color.colorGrey_300));
        } else {
            relSubMenu.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrey_300));
        }


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(intent);
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();


    }

    public void showLogOutAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_common_application, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        CustomButton btDone = dialogView.findViewById(R.id.btDone);
        CustomButton btCalcel = dialogView.findViewById(R.id.btCalcel);

        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);

        tvTitle.setText(getResources().getString(R.string.settings_logout_txt));
        tvMessage.setText(getResources().getString(R.string.setting_logout_desc_txt));
        btDone.setText(getResources().getString(R.string.alert_dialog_btn_txt));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }




        btDone.setVisibility(View.VISIBLE);
        btCalcel.setVisibility(View.VISIBLE);


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                mApplication.clearChacheSession();
                displayViewOther(0, null);

            }
        });
        btCalcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /***********************************************
     * This method checks for the App Updates**********/
    private void checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                // Checks that the platform will allow the specified type of update.
                if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                BrokerActivity.this, REQUEST_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /*********************************************************************************
     * This method requests user to enable notification for the below devices mentioned:
     * Xiaomi
     * Oppo
     * Vivo
     * Letv
     * Honor
     ******************************************************************************** *****/
    private void addAutoStartup() {
        try {
            Intent intent = new Intent();
            String manufacturer = Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                mSession.setHasNotificationEnable(true);
                dialogNotification(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    private void callLastNAVupdateApi() {
        String url = Config.Last_NAV_Update;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("LastNAVDateDetail");
                        JSONObject jsonObject1 = jsonArray.optJSONObject(0);
                        AppApplication.last_transaction_update = jsonObject1.optString("TransactionDate");
                        AppApplication.last_nav_update = jsonObject1.optString("LastNAVDate");

                    } else {

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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
            RequestQueue requestQueue = Volley.newRequestQueue(BrokerActivity.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

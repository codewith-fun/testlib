package investwell.client.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.multidex.MultiDex;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.github.mikephil.charting.data.Entry;
import com.iw.acceleratordemo.R;
import com.yariksoffice.lingver.Lingver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import investwell.broker.fragment.FragHomeBroker;
import investwell.common.applock.AppLockOptionActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

import investwell.utils.customView.CustomButton;
import investwell.utils.model.Amount;


public class AppApplication extends Application implements LifecycleObserver {
    private AppSession mSession;
    public boolean mFirstTimeSkippedApplock = false, isAppForeground = false;
    private Handler mHandler;
    private boolean mIsFirstTime = false;

    public static JSONObject sOBJ_DASHBOARD_BROKER = new JSONObject();
    public static JSONObject sOBJ_PROSPECT_STATUS = new JSONObject();
    private String appLanguage = "";
    public static int mDashPagerLoc;
    public static JSONArray sGOAL_SUMMARY_ARRAY = new JSONArray();
    public String aServiceName = "";
    public String applicantName = "";
    public static String sClientDashboard = "", graph = "", portfolio_data = "", portfolio_detail_data = "", porfolio_detail_data2 = "", my_transaction = "",
            asset_allocation = "", category_allocation = "", applicant_allocation = "", watch_list = "", fund_allocation = "", last_transaction_update = "", last_nav_update = "", notification = "";
    public ArrayList<JSONObject> schemeDetailsList = new ArrayList<>();
    public ArrayList<JSONObject> stpSchemeDetailsList = new ArrayList<>();
    public ArrayList<JSONObject> swpSchemeDetailsList = new ArrayList<>();
    public List<JSONObject> recomendetSchemeList = new ArrayList<>();
    public List<JSONObject> goalBasedSchemeList = new ArrayList<>();
    public List<JSONObject> topSchemeList = new ArrayList<>();
    public List<String> topSchemeCategory = new ArrayList<>();
    public List<JSONObject> portFolioList = new ArrayList<>();

    public List<JSONObject> getLangList() {
        return langList;
    }

    public void setLangList(List<JSONObject> langList) {
        this.langList = langList;
    }

    public List<JSONObject> langList = new ArrayList<>();
    public static ArrayList<Entry> GInvestmentList = new ArrayList<>();
    public static ArrayList<Entry> GCurrentValueList = new ArrayList<>();
    public static ArrayList<JSONObject> dataList = new ArrayList<>();
    public List<Amount> amountList = new ArrayList<>();

    public List<Amount> getAmountList() {
        return amountList;
    }

    public void setAmountList(List<Amount> amountList) {
        this.amountList = amountList;
    }

    public List<JSONObject> getRecomendetSchemeList() {
        return recomendetSchemeList;
    }

    public void setRecomendetSchemeList(List<JSONObject> recomendetSchemeList) {
        this.recomendetSchemeList = recomendetSchemeList;
    }

    public List<JSONObject> getPortfolioSchemeList() {
        return portFolioList;
    }

    public void setPortfolioSchemeList(List<JSONObject> portFolioList) {
        this.portFolioList = portFolioList;
    }

    public List<JSONObject> getGoalSchemeList() {
        return goalBasedSchemeList;
    }

    public void setGoalSchemeList(List<JSONObject> goalBasedSchemeList) {
        this.goalBasedSchemeList = goalBasedSchemeList;
    }

    public List<JSONObject> getTopSchemeList() {
        return topSchemeList;
    }

    public void setTopSchemeList(List<JSONObject> topSchemeList) {
        this.topSchemeList = topSchemeList;
    }

    public List<String> getTopSchemeCaregoryList() {
        return topSchemeCategory;
    }

    public void setTopSchemeCaregoryList(List<String> topSchemeCategory) {
        this.topSchemeCategory = topSchemeCategory;
    }

    public static Bundle sStoreGoalBundle = new Bundle();


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mSession = AppSession.getInstance(getApplicationContext());
        mHandler = new Handler();
        // setMarketValueDuraton();
        checkTime();
        Lingver.init(this, "en");

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        if (!TextUtils.isEmpty(mSession.getSelectedAppLang())) {
            appLanguage = mSession.getSelectedAppLang();
        } else {
            appLanguage = "English";
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onAppBackgrounded() {
        if (mFirstTimeSkippedApplock && mSession.getHasAppLockEnable()) {
            mFirstTimeSkippedApplock = false;
            Intent intent = new Intent(getApplicationContext(), AppLockOptionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("type", "verify_lock");
            intent.putExtra("callFrom", "application");
            startActivity(intent);
        }

        if (mSession.getHasLoging() == true) {
            checkTime();
        }
    }

    public void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    // First time its automatic called so i have cheched mFirstTimeSkipped Applock
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void appInPauseState() {
        new CountDownTimer(10 * 1000, 1) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                isAppForeground = isAppOnForeground();
                if (!isAppForeground) {
                    new CountDownTimer(1000 * 60 * 10, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            mFirstTimeSkippedApplock = true;
                        }
                    }.start();
                }
            }
        }.start();


    }

    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    public void checkTime() {

        long currentTime = System.currentTimeMillis();
        if (!mSession.get_current_time().equals(0) || mSession.get_current_time() != null) {
            if (currentTime - mSession.get_current_time() >= 1800000) {
                sClientDashboard = "";
                mSession.set_current_time(currentTime);
            }

        }
    }

    public void showCommonDailog(AppCompatActivity activity, Context context, boolean isPopToBackEnable, String title, String message, final String type, boolean isShowingCancelBtn, boolean isShowingDoneBtn) {
        if (type.equals("invalidPasskey")) {
            goToLogin(type);
        } else if (type.equals("prospect")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                showDailog(activity, context, isPopToBackEnable, title, message, type, isShowingCancelBtn, isShowingDoneBtn);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                showDailog(activity, context, isPopToBackEnable, title, message, type, isShowingCancelBtn, isShowingDoneBtn);
            }
        }

    }

    public void goToLogin(String type) {
        clearChacheSession();
        Intent intent = null;
        if (type.equals("invalidPasskey")) {
            mSession.setPassKey("");
            intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.putExtra("type", type);
        } else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }

    public void clearChacheSession() {
        mSession = AppSession.getInstance(getApplicationContext());


        recomendetSchemeList.clear();
        goalBasedSchemeList.clear();
        topSchemeList.clear();
        sClientDashboard = "";
        graph = "";
        portfolio_data = "";
        portfolio_detail_data = "";
        my_transaction = "";
        asset_allocation = "";
        category_allocation = "";
        applicant_allocation = "";
        fund_allocation = "";
        FragHomeBroker.comming_from = "";

        boolean isNotificationOn = mSession.getHasNotificationEnable();
        boolean isFirstTime = mSession.getHasFirstTimeCompleted();
        boolean intro = mSession.getHasFirstTimeDashBoard();
        String userType = mSession.getUserType();
        String appType = mSession.getAppType();
        String passKey = mSession.getPassKey();
        String marketValue = mSession.getMarketValue();
        String fcmToken = mSession.getFcmToken();
        String Bid = AppConstants.APP_BID;
        String flavour = mSession.getFlavour();
        boolean appIntroFirstTime = mSession.getHasFirstTimeAppIntroLaunched();
        // Boolean loged_in = mSession.getHasLoging();
        String pin = mSession.getPIN();
        String uName = mSession.getUname();
        String uPass = mSession.getUPassword();
        String uAppLockType = mSession.getAppLockType();
        boolean appLockEnable = mSession.getHasAppLockEnable();
        String login_type = mSession.getLoginType();
        String language = mSession.getDefaultAppLang();
        String appConfig = mSession.getAppConfig();

        boolean hasSocialLogin = mSession.getHasSocialLogin();


        mSession.clear();


        mSession.setUname(uName);
        mSession.setUPassword(uPass);
        mSession.setAppLockType(uAppLockType);
        mSession.setHasAppLockEnable(appLockEnable);
        mSession.setHasFirstTimeAppIntroLaunched(appIntroFirstTime);
        mSession.setLoginType(login_type);
        mSession.setHasNotificationEnable(isNotificationOn);
        mSession.setHasFirstTimeCompleted(isFirstTime);
        mSession.setHasFirstTimeDashBoard(intro);
        mSession.setUserType(userType);
        mSession.setAppType(appType);
        mSession.setPassKey(passKey);
        mSession.setMarketValue(marketValue);
        mSession.setFcmToken(fcmToken);
        /*   mSession.setBid(Bid);*/
        mSession.setFlavour(flavour);
        mSession.setDefaultAppLang(language);
        mSession.setAppConfig(appConfig);
        // mSession.setHasLoging(loged_in);
        mSession.setPAN(pin);
        mSession.setPIN(pin);
        mSession.setHasSocialLogin(hasSocialLogin);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showDailog(final AppCompatActivity activity, final Context context, final boolean isPopToBackEnable, String title, String message, final String type, boolean isShowingCancelBtn, boolean isShowingDoneBtn) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_common_application, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        CustomButton btDone = dialogView.findViewById(R.id.btDone);
        CustomButton btCalcel = dialogView.findViewById(R.id.btCalcel);
        if (isShowingDoneBtn) {
            btDone.setVisibility(View.VISIBLE);
        } else {
            btDone.setVisibility(View.GONE);
        }

        if (isShowingCancelBtn) {

            btCalcel.setVisibility(View.VISIBLE);
        } else {
            btCalcel.setVisibility(View.GONE);
        }

        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
        if (title.equals("Failed") || title.equals("Error")) {
            tvMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        } else {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                tvMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkPrimaryTextColor));

            } else {
                tvMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.lightPrimaryTextColor));

            }
        }
        tvTitle.setText(title);
        tvMessage.setText(message);
        btDone.setText(getResources().getString(R.string.alert_dialog_btn_txt));


        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (type.equals("invalidPasskey")) {
                    goToLogin(type);
                } else if (type.equals("prospect")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (type.equals("message")) {
                    if (isPopToBackEnable)
                        activity.getSupportFragmentManager().popBackStack();

                }
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

    public void getConfigData() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.PASSKEY, mSession.getPassKey());
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        params.put(AppConstants.APP_LANGUAGE, appLanguage);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.APP_CONFIG, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response.optBoolean("Status")) {
                    /*ArrayList<JSONObject> list = new ArrayList<>();
                    JSONArray appConfig = response.optJSONArray("APPConfigDetail");
                    JSONObject jsonObject1 = appConfig.optJSONObject(0);*/
                    mSession.setAppConfig("");
                    mSession.setAppConfig(response.toString());

                } else {
                    /*  activitySelection(); // only for testing*/


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {

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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}

package investwell.client.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import investwell.broker.fragment.FragHomeBroker;
import investwell.client.flavourtypefour.fragment.HomeClientFlavourFourFragment;
import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.client.flavourtypetwo.fragment.FinancialToolFragment;
import investwell.client.fragment.CASUpload.FragPreCASUpload;
import investwell.client.fragment.DocUpload.FragDocumentDetailsList;
import investwell.client.fragment.DocUpload.FragPreCheque;
import investwell.client.fragment.DocUpload.FragPreDoc;
import investwell.client.fragment.DocUpload.FragPreSignature;
import investwell.client.fragment.InvestInExistingSchemes.Fragment.ExistingInvestmentSchemeFragment;
import investwell.client.fragment.InvestInExistingSchemes.Fragment.FragExistingHome;
import investwell.client.fragment.MyOrderReport.FragMyOrderReport;
import investwell.client.fragment.UploadExistingInvestmentModule.FragUploadModule;
import investwell.client.fragment.bank.FragAddBank;
import investwell.client.fragment.bank.FragAllBankListIndia;
import investwell.client.fragment.bank.FragClientBankList;
import investwell.client.fragment.divident.DividendDetailFragment;
import investwell.client.fragment.divident.DividendFragment;
import investwell.client.fragment.documentViewer.FragClientDocView;
import investwell.client.fragment.documentViewer.FragDocViewer;
import investwell.client.fragment.factsheet.Fragment.FactSheetFragment;
import investwell.client.fragment.flavour.GetFlavourFrag;
import investwell.client.fragment.flavour.SetFlavourFrag;
import investwell.client.fragment.folio.FragFolioList;
import investwell.client.fragment.foliolookup.FolioLookupFragment;
import investwell.client.fragment.foliolookup.FragFolioLookupDetails;
import investwell.client.fragment.fundpicks.fragments.FundPicksFragment;
import investwell.client.fragment.goalbased.Fragment.FeedbackFragment;
import investwell.client.fragment.goalbased.Fragment.FragConfirmRiskProfile;
import investwell.client.fragment.goalbased.Fragment.FragCreateGoal;
import investwell.client.fragment.goalbased.Fragment.FragGoalCategory;
import investwell.client.fragment.goalbased.Fragment.FragGoalList;
import investwell.client.fragment.goalbased.Fragment.FragGoalSummeryDetails;
import investwell.client.fragment.goalbased.Fragment.FragQuestions;
import investwell.client.fragment.goalbased.Fragment.GoalCategoryTypeOneAFragment;
import investwell.client.fragment.goalbased.Fragment.PersonalInvestmentFrag;
import investwell.client.fragment.goalbased.Fragment.ServiceRequestFragment;
import investwell.client.fragment.goalbased.Fragment.StartAssessmentFrag;
import investwell.client.fragment.goalmodulev1.GoalSummaryDetailsFragment;
import investwell.client.fragment.goalmodulev1.GoalVersonOneFragment;
import investwell.client.fragment.help.FragHelpHome;
import investwell.client.fragment.home.FragAllDocuments;
import investwell.client.fragment.home.FragHomeClient;
import investwell.client.fragment.home.FragNotification;
import investwell.client.fragment.justsave.GetJustSaveFrag;
import investwell.client.fragment.justsave.JustSaveFrag;
import investwell.client.fragment.language.LanguageChangeFragment;
import investwell.client.fragment.mandate.FragMandateUpload;
import investwell.client.fragment.mandate.FragMendateList;
import investwell.client.fragment.mandate.FragPersonalMandateForm;
import investwell.client.fragment.myOrderCeaseSIP.FragMyOrderCeaseNSE;
import investwell.client.fragment.myOrderCeaseSIP.FragMyOrderCeaseSIP;
import investwell.client.fragment.mySystematicTransanctions.FragMySystematicTransanctionsHome;
import investwell.client.fragment.mysip.FragMySipHome;
import investwell.client.fragment.mysip.FragSipStpSwpDetails;
import investwell.client.fragment.nav.fragment.FragNavScheme;
import investwell.client.fragment.nav.fragment.SearchAmcFragment;
import investwell.client.fragment.nfo.Fragments.FragNFOCart;
import investwell.client.fragment.nfo.Fragments.FragNewFundOffers;
import investwell.client.fragment.nfo.Fragments.FragSchemeVideo;
import investwell.client.fragment.onboarding.BankDetailFragment;
import investwell.client.fragment.onboarding.FatcaDetailFormFragment;
import investwell.client.fragment.onboarding.InstantNSEAcivationFragment;
import investwell.client.fragment.onboarding.NomineeDetailsFragment;
import investwell.client.fragment.onboarding.PanKycFragment;
import investwell.client.fragment.onboarding.PersonalDetailsFragment;
import investwell.client.fragment.others.BottomSheetFragment;
import investwell.client.fragment.others.BseProfileDetailFrag;
import investwell.client.fragment.others.FragmentPaymentMethod;
import investwell.client.fragment.others.MyInsuranceFragment;
import investwell.client.fragment.others.MyTransactionNew;
import investwell.client.fragment.others.My_Journey;
import investwell.client.fragment.others.OutstandingUnitFrag;
import investwell.client.fragment.others.SchemeDetailFragment;
import investwell.client.fragment.others.SendMailFragment;
import investwell.client.fragment.others.SimplySaveFrag;
import investwell.client.fragment.others.WatchListFragment;
import investwell.client.fragment.payment.FragPrePaymentHome;
import investwell.client.fragment.portfolio.PortfolioDetailFragment;
import investwell.client.fragment.portfolio.PortfolioFragment;
import investwell.client.fragment.profile.FragProfileDetails;
import investwell.client.fragment.profile.FragProfileIncomplete;
import investwell.client.fragment.profile.FragProfileList;
import investwell.client.fragment.profile.FragProfileSettings;
import investwell.client.fragment.schemes.BelieveInExpertiseFragment;
import investwell.client.fragment.schemes.FragAddSchemesSingle;
import investwell.client.fragment.schemes.FragAddToCartList;
import investwell.client.fragment.schemes.FragEmptyCart;
import investwell.client.fragment.schemes.FragGoForLumpsum;
import investwell.client.fragment.schemes.FragGoForSIP;
import investwell.client.fragment.schemes.FragGoalBaseAddToCart;
import investwell.client.fragment.schemes.FragInvestNow;
import investwell.client.fragment.schemes.FragLumpsumOrderStatus;
import investwell.client.fragment.schemes.FragMyOrder;
import investwell.client.fragment.schemes.FragOrderHistory;
import investwell.client.fragment.schemes.FragRecomendedAddToCart;
import investwell.client.fragment.schemes.FragSearchSchemes;
import investwell.client.fragment.schemes.InvestmentConfirmFragment;
import investwell.client.fragment.taxsaver.FragTaxSavingDetail;
import investwell.client.fragment.taxsaver.TaxSavingFragment;
import investwell.client.fragment.topScheme.TopSchemeFragment;
import investwell.client.fragment.topSipScheme.TopSIPSchemeFragment;
import investwell.client.fragment.transactions.AdditionalPurchaseTransactFragment;
import investwell.client.fragment.transactions.AdditionalRedeemTransactFragment;
import investwell.client.fragment.transactions.AdditionalSipTransactFragment;
import investwell.client.fragment.transactions.AdditionalSwitchTransactFragment;
import investwell.client.fragment.transactions.AdditionalSwpTransactFragment;
import investwell.client.fragment.transactions.AdditonalStpTransactFragment;
import investwell.client.fragment.transactions.Frag_New_Purchase;
import investwell.client.fragment.transferholding.FragTransferHolding;
import investwell.client.fragment.transferholding.FragTransferHoldingIntro;
import investwell.client.fragment.user_info.ActiveStatus;
import investwell.client.fragment.user_info.ChangePasswordFragment;
import investwell.client.fragment.user_info.ChequeUpload;
import investwell.client.fragment.user_info.NseFatacaFragment;
import investwell.client.fragment.videoKyc.frag_kyc_Form;
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
import investwell.utils.crop.CropImage;
import investwell.utils.crop.CropImageView;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomDialog;
import investwell.utils.customView.CustomTextViewBold;


public class MainActivity extends BaseActivity implements View.OnClickListener, CustomDialog.DialogBtnCallBack {
    // tags used to attach the fragments
    private static final String TAG_HOME = "ic_bottombar_home_inactive";
    private static final String TAG_PORTFOLIO = "photos";
    private static final String TAG_TRANSACTIONS = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    public List<JSONObject> mSelectedCartsList;
    int REQUEST_APP_UPDATE = 17362;
    private AppSession mSession;
    private String mCallCommingFrom = "";
    private AppUpdateManager appUpdateManager;
    private Toolbar toolbar;
    private TextView tvToolBarTitle, tvDashboard, tvPortfolio, tvProfile, tvTransaction, tvMore;
    private ImageView ivContactUs, ivCont3B, ivToolbarLogo, ivDashBoard, ivPortfolio, ivProfile, ivTransaction, ivMore, ivSearch;
    private AppBarLayout appbar;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private LinearLayout llDashBoard, llPortfolio, llProfile, llTransaction, llMore;
    // flag to load ic_bottombar_home_inactive fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private View vDashboard, vPortfolio, vProfile, vTransaction, vMore;
    private LinearLayout ll_bottomBar_main_container;
    private BottomSheetFragment bottomSheetFragment;
    private CustomTextViewBold tvCartBadge, tv_notification_badge, tvNotficationBadge3B, tvCartBadge3b;
    private AppApplication mApplication;
    private FrameLayout flCart, fl_notifications, flCart3B, flNotify3B;
    private int count = 0;
    private int pos = 2;
    private LinearLayout llMiddleViewLogo;
    private ImageView ivNotification, ivNotification3B;
    private CustomDialog customDialog;
    private String showLastSelectedTab = "Dashboard";
    private String appFlavour = "", flavourPosition = "", comingFromActivity = "";
    private String addToBackStack = "";
    private String appLanguage = "";
    private LinearLayout llToolBarPrimary, llToolBarSecondary;
    private static final String ACTION_QUICKSHARE = "com.sipbajaar.QUICKSHARING";
    private String shareLink = "";
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private TextView tvDashTitle;
    public JSONObject mBankObjectOnBankList = null;

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();

        if ((fragment instanceof FragHomeClient) && (mSession.getLoginType().equals("Broker")
                || mSession.getLoginType().equals("SubBroker")
                || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                || mSession.getLoginType().equalsIgnoreCase("Region")
                || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
            finish();

        } else if ((fragment instanceof MyTransactionNew) && Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
            super.onBackPressed();
        } else if ((fragment instanceof FragHomeClient) || (fragment instanceof PortfolioFragment) || (fragment instanceof MyTransactionNew)) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                //finish();
            } else if (navItemIndex == 0 && comingFromActivity.equalsIgnoreCase("MyAssetType2B")) {
                super.onBackPressed();

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
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") ||
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")
                            || Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                startActivity(new Intent(this, MainActivityTypeTwo.class));
            } else {
                super.onBackPressed();
            }


        } else if (fragment instanceof StartAssessmentFrag) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")) {
                finish();
            } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                getSupportFragmentManager().popBackStack();
            } else {
                displayViewOther(0, null);
            }

        } else if (fragment instanceof FragConfirmRiskProfile) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")) {
                finish();
            } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                getSupportFragmentManager().popBackStack();
            } else {
                displayViewOther(0, null);
            }

        } else if (fragment instanceof FragProfileList && (mSession.getLoginType().equals("Broker")
                || mSession.getLoginType().equals("SubBroker")
                || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                || mSession.getLoginType().equalsIgnoreCase("Region")
                || mSession.getLoginType().equalsIgnoreCase("Branch")) &&
                FragProfileList.mType.equalsIgnoreCase("brokerActivitySearch")) {
            removeAllStack();
            super.onBackPressed();

        } else if (fragmentCount > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    public <T> void setMainVisibility(Fragment fragment, T object) {

        if (fragment instanceof FragHomeClient
                || fragment instanceof FragHomeBroker
                || fragment instanceof PortfolioFragment
                || fragment instanceof MyTransactionNew
                || fragment instanceof PortfolioDetailFragment
                || fragment instanceof HomeClientFlavourFourFragment
                || fragment instanceof BottomSheetFragment) {
            if (appbar != null)
                appbar.setVisibility(View.VISIBLE);
            if (mSession.getHasLoging()) {
                ll_bottomBar_main_container.setVisibility(View.VISIBLE);
            } else {
                ll_bottomBar_main_container.setVisibility(View.GONE);
            }

            if (fragment instanceof FragHomeClient) {
                showSelectedDashboard();
                showLastSelectedTab = "Dashboard";
            } else if (fragment instanceof PortfolioFragment) {
                showSelectedPortFolio();
                showLastSelectedTab = "Portfolio";

            } else if (fragment instanceof PortfolioDetailFragment) {
                if ((!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) &&
                        !(mSession.getLoginType().equalsIgnoreCase("ClientG") || FragHomeBroker.comming_from.equalsIgnoreCase("Broker_group"))) {
                    showSelectedPortFolio();
                    showLastSelectedTab = "Portfolio";
                    if (appbar != null)
                        appbar.setVisibility(View.VISIBLE);
                    if (mSession.getHasLoging()) {
                        ll_bottomBar_main_container.setVisibility(View.VISIBLE);
                    } else {
                        ll_bottomBar_main_container.setVisibility(View.GONE);
                    }
                } else {
                    if (appbar != null)
                        appbar.setVisibility(View.GONE);
                    ll_bottomBar_main_container.setVisibility(View.GONE);

                }
            } else if (fragment instanceof MyTransactionNew) {
                showSelectedTransaction();
                showLastSelectedTab = "Transaction";
                if ((!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
                    showSelectedTransaction();
                    showLastSelectedTab = "Transaction";
                    if (appbar != null)
                        appbar.setVisibility(View.GONE);
                    ll_bottomBar_main_container.setVisibility(View.GONE);
                    if (bottomSheetFragment.isVisible()) {
                        bottomSheetFragment.dismiss();
                    }

                } else {
                    if (appbar != null)
                        appbar.setVisibility(View.VISIBLE);
                    if (mSession.getHasLoging()) {
                        ll_bottomBar_main_container.setVisibility(View.VISIBLE);
                    } else {
                        ll_bottomBar_main_container.setVisibility(View.GONE);
                    }

                }
            } else if (fragment instanceof BottomSheetFragment) {
                showSelectedMore();
                if (showLastSelectedTab.equalsIgnoreCase("Dashboard")) {
                    showSelectedDashboard();
                } else if (showLastSelectedTab.equalsIgnoreCase("Portfolio")) {
                    showSelectedPortFolio();
                } else if (showLastSelectedTab.equalsIgnoreCase("Transaction")) {
                    showSelectedTransaction();
                }
            } else {
                showSelectedDashboard();
            }
        } else {
            if (appbar != null)
                appbar.setVisibility(View.GONE);
            ll_bottomBar_main_container.setVisibility(View.GONE);
            if (bottomSheetFragment.isVisible()) {
                bottomSheetFragment.dismiss();
            }
        }
    }

    private void setUpShareMessage() {
        String primaryShareMsg = Utils.getConfigData(mSession).optString("ShareTextMessage");
        String androidLink = "";
        String iosLink = "";
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AndroidLink"))) {
            androidLink = "Android:  " + Utils.getConfigData(mSession).optString("AndroidLink");

        } else {
            androidLink = "";
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("IOSLink"))) {
            iosLink = "iOS:  " + Utils.getConfigData(mSession).optString("IOSLink");

        } else {
            iosLink = "";
        }

        if (!TextUtils.isEmpty(androidLink) && !TextUtils.isEmpty(iosLink)) {
            shareLink = primaryShareMsg + "\n\n" + androidLink + "\n\n" + iosLink;
        } else if (TextUtils.isEmpty(androidLink) && !TextUtils.isEmpty(iosLink)) {
            shareLink = primaryShareMsg + "\n\n" + iosLink;
        } else if (!TextUtils.isEmpty(androidLink) && TextUtils.isEmpty(iosLink)) {
            shareLink = primaryShareMsg + "\n\n" + androidLink;
        } else {
            shareLink = primaryShareMsg;
        }
    }

    private void onShare() {
        Intent intnt = new Intent(Intent.ACTION_SEND);
        intnt.setType("text/plain");
        if (intnt != null) {
            intnt.putExtra(Intent.EXTRA_TEXT, shareLink);
            startActivity(intnt);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_client);
        FirebaseMessaging.getInstance().subscribeToTopic(getApplicationContext().getString(R.string.TokenKey) + AppConstants.APP_BID);
        setInitializer();
        setUpShareMessage();

        getDataFromBundle(savedInstanceState);
        if (ACTION_QUICKSHARE.equals(getIntent().getAction())) {
            onShare();
        }
        whenToShowReviewDailog();

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

    private void getDataFromBundle(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            if (!TextUtils.isEmpty(intent.getStringExtra("Flavour"))) {
                appFlavour = intent.getStringExtra("Flavour");
            } else {
                appFlavour = "";
            }
            if (!TextUtils.isEmpty(intent.getStringExtra("comingFromActivity"))) {
                comingFromActivity = intent.getStringExtra("comingFromActivity");
            } else {
                comingFromActivity = "";
            }
            if (!TextUtils.isEmpty(intent.getStringExtra("comingFromActivity")) && (intent.getStringExtra("comingFromActivity").equalsIgnoreCase("BrokerActivity"))) {
                Bundle bundle = new Bundle();
                bundle.putString("cid", intent.getStringExtra("cid"));
                displayViewOther(16, bundle);
            }

            if (!TextUtils.isEmpty(intent.getStringExtra("position"))) {
                flavourPosition = intent.getStringExtra("position");
            } else {
                flavourPosition = "";
            }
            if (appFlavour.equalsIgnoreCase("TYPE 2")) {
                addToBackStack = "YES";
                getActivityTypeCheck(appFlavour, flavourPosition);
            } else if (!TextUtils.isEmpty(comingFromActivity) && comingFromActivity.equalsIgnoreCase("MyAssetType2B")) {
                addToBackStack = "YES";
                setUpInitialUi(savedInstanceState);
                setListeners();
                showSelectedDashboard();
                notificationCount();
                callLastNAVupdateApi();
                if (!mSession.getHasNotificationEnable()) {
                    addAutoStartup();
                }
                setUpUiVisibility();
                getDocument();
            } else if (comingFromActivity.equalsIgnoreCase("profile_list_cheque") || comingFromActivity.equalsIgnoreCase("profile_list_signature")) {
                setListeners();
                Bundle bundle = new Bundle();
                bundle.putString("type", "show_only_profiles");
                displayViewOther(0, null);
                displayViewOther(36, bundle);
            } else if (!TextUtils.isEmpty(comingFromActivity) && (comingFromActivity.equalsIgnoreCase("brokerActivitySearch"))) {
                Bundle bundle = new Bundle();
                bundle.putString("cid", intent.getStringExtra("cid"));
                bundle.putString("type", "brokerActivitySearch");
                bundle.putString("ProfilePic", intent.getStringExtra("ProfilePic"));
                bundle.putString("Name", intent.getStringExtra("Name"));
                bundle.putString("MobileNO", intent.getStringExtra("MobileNO"));
                bundle.putString("Email", intent.getStringExtra("Email"));
                displayViewOther(36, bundle);
            } else if (!TextUtils.isEmpty(comingFromActivity) && (comingFromActivity.equalsIgnoreCase("RiskProfile"))) {
                // displayViewOther(61, null);
                if (mSession.getRiskName().isEmpty()) {
                    displayViewOther(61, null);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "showRiskProfile");
                    displayViewOther(60, bundle);
                }
            } else {
                setUpInitialUi(savedInstanceState);
                setListeners();
                showSelectedDashboard();
                notificationCount();
                callLastNAVupdateApi();
                if (!mSession.getHasNotificationEnable()) {
                    addAutoStartup();
                }
                setUpUiVisibility();

                getDocument();
            }
        }
        if (CURRENT_TAG == TAG_HOME) {
            tvDashTitle.setText("My Dashboard");
        } else if (CURRENT_TAG == TAG_PORTFOLIO) {
            tvDashTitle.setText("Portfolio");

        }
    }

    private void setInitializer() {
        if (!BuildConfig.DEBUG) {
            checkUpdate();
        }
        /**/
        mApplication = (AppApplication) getApplication();
        mSelectedCartsList = new ArrayList<>();
        mSession = AppSession.getInstance(this);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(AppConstants.COME_FROM)) {
            mCallCommingFrom = intent.getStringExtra(AppConstants.COME_FROM);
        }
        toolbar = findViewById(R.id.toolbar_main);
        appbar = findViewById(R.id.appbar);
        flCart = findViewById(R.id.fl_cart);
        flNotify3B = findViewById(R.id.fl_notifications_3_b);
        tvDashTitle = findViewById(R.id.tv_dash_title);
        flCart3B = findViewById(R.id.fl_cart_3_b);
        fl_notifications = findViewById(R.id.fl_notifications);
        tvToolBarTitle = findViewById(R.id.tv_toolbar_title);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        tvCartBadge3b = findViewById(R.id.tv_cart_badge_3b);
        tv_notification_badge = findViewById(R.id.tv_notification_badge);
        tvNotficationBadge3B = findViewById(R.id.tv_notification_badge_3_b);
        ivToolbarLogo = findViewById(R.id.iv_app_logo);
        llDashBoard = findViewById(R.id.ll_home);
        llMore = findViewById(R.id.ll_more);
        llPortfolio = findViewById(R.id.ll_portfolio);
        ivNotification = findViewById(R.id.iv_notification);
        /*llProfile = findViewById(R.id.ll_profile);*/
        llTransaction = findViewById(R.id.ll_transaction);
        mHandler = new Handler();
        // load toolbar titles from string resources
        llToolBarPrimary = findViewById(R.id.ll_primary_toolbar);
        llToolBarSecondary = findViewById(R.id.ll_secondary_toolBar);
        vDashboard = findViewById(R.id.divider_home);
        vMore = findViewById(R.id.divider_more);
        vPortfolio = findViewById(R.id.divider_portfolio);
        llMiddleViewLogo = findViewById(R.id.ll_middle_view_logo);
        vTransaction = findViewById(R.id.divider_transaction);
        tvDashboard = findViewById(R.id.tv_home);
        tvPortfolio = findViewById(R.id.tv_portfolio);
        customDialog = new CustomDialog(MainActivity.this);
        tvTransaction = findViewById(R.id.tv_transaction);
        tvMore = findViewById(R.id.tv_more);
        ivDashBoard = findViewById(R.id.iv_home);
        ivPortfolio = findViewById(R.id.iv_portfolio);
        ivSearch = findViewById(R.id.iv_search);
        ivContactUs = findViewById(R.id.ivContactUs);
        ivCont3B = findViewById(R.id.iv_contact_3_b);
        ivTransaction = findViewById(R.id.iv_transaction);
        ivMore = findViewById(R.id.iv_more);
        ll_bottomBar_main_container = findViewById(R.id.ll_bottomBar_main_container);
        bottomSheetFragment = new BottomSheetFragment();
        if (!TextUtils.isEmpty(mSession.getSelectedAppLang())) {
            appLanguage = mSession.getSelectedAppLang();
        } else {
            appLanguage = "English";
        }
    }


    private void setUpUiVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Menu")) &&
                Utils.getConfigData(mSession).optString("Menu").equalsIgnoreCase("Y")) {
            llMore.setVisibility(View.VISIBLE);
        } else {
            llMore.setVisibility(View.GONE);


        }
        if (Utils.getConfigData(mSession).optString("MainAddToCart").equalsIgnoreCase("Y")) {
            flCart.setVisibility(View.VISIBLE);
            flCart3B.setVisibility(View.VISIBLE);
        } else {
            flCart.setVisibility(View.GONE);
            flCart3B.setVisibility(View.GONE);
        }

        if (Utils.getConfigData(mSession).optString("Notification").equalsIgnoreCase("Y")) {
            fl_notifications.setVisibility(View.VISIBLE);
            flNotify3B.setVisibility(View.VISIBLE);
        } else {
            fl_notifications.setVisibility(View.GONE);
            flNotify3B.setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
            llToolBarSecondary.setVisibility(View.VISIBLE);
            llToolBarPrimary.setVisibility(View.GONE);
            flavourThreeBottomBar();
        } else {
            llToolBarPrimary.setVisibility(View.VISIBLE);
            llToolBarSecondary.setVisibility(View.GONE);
            defaultBottomBar();
        }
    }

    private void defaultBottomBar() {
        ivDashBoard.setImageResource(R.mipmap.ic_bottombar_home_inactive);
        ivPortfolio.setImageResource(R.mipmap.ic_bottombar_portfolio_inactive);
        ivTransaction.setImageResource(R.mipmap.ic_bottombar_tranc_inactive);
        ivMore.setImageResource(R.mipmap.ic_bottombar_more_inactive);
        tvDashboard.setText(R.string.main_bottom_tab_menu_dashboard);
        tvPortfolio.setText(R.string.main_bottom_tab_menu_portfolio);
        tvTransaction.setText(R.string.main_bottom_tab_menu_transactions);
        tvMore.setText(R.string.main_bottom_tab_menu_more);
    }

    private void flavourThreeBottomBar() {
        ivDashBoard.setImageResource(R.mipmap.ic_bottombar_home_inactive);
        ivPortfolio.setImageResource(R.mipmap.ic_bottombar_portfolio_inactive);
        ivTransaction.setImageResource(R.mipmap.ic_bottombar_tranc_inactive);
        ivMore.setImageResource(R.mipmap.ic_bottombar_more_inactive);
        ivTransaction.setImageResource(R.mipmap.ic_bottombar_invest_now_3b);
        ivTransaction.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvDashboard.setText(R.string.main_bottom_tab_menu_dashboard);
        tvPortfolio.setText(R.string.main_bottom_tab_menu_portfolio);
        tvTransaction.setText(R.string.main_bottom_tab_menu_invest_now);
        tvMore.setText(R.string.main_bottom_tab_menu_more);
    }

    private void setListeners() {
        llTransaction.setOnClickListener(this);
        llPortfolio.setOnClickListener(this);
        llDashBoard.setOnClickListener(this);
        llMore.setOnClickListener(this);
        findViewById(R.id.fl_cart).setOnClickListener(this);
        flCart3B.setOnClickListener(this);
        ivContactUs.setOnClickListener(this);
        ivCont3B.setOnClickListener(this);
        llMiddleViewLogo.setOnClickListener(this);
        ivNotification.setOnClickListener(this);
        flNotify3B.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_home) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            showSelectedDashboard();
            tvDashTitle.setText("My Dashboard");
            loadHomeFragment();
        } else if (id == R.id.ll_portfolio) {
            tvDashTitle.setText("Portfolio");

            onPortFolioClick();
        } else if (id == R.id.ll_transaction) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                navItemIndex = 2;
                CURRENT_TAG = TAG_TRANSACTIONS;
                displayViewOther(75, null);
                   /* if (bottomSheetFragment.isAdded()) {
                        return;
                    } else {
                        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                        showSelectedMore();
                    }*/
            } else {
                navItemIndex = 2;
                CURRENT_TAG = TAG_TRANSACTIONS;
                showSelectedTransaction();
                loadHomeFragment();
            }
        } else if (id == R.id.ll_more) {/*   if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                    displayViewOther(75, null);
                } else {*/
            if (bottomSheetFragment.isAdded()) {
                return;
            } else {
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                showSelectedMore();
            }
            /*  }*/
        } else if (id == R.id.fl_cart || id == R.id.fl_cart_3_b) {
            onCartIconClick();
        } else if (id == R.id.ivContactUs || id == R.id.iv_contact_3_b) {
            if (mSession.getHasLoging() && (mSession.getLoginType().equals("Broker")
                    || mSession.getLoginType().equals("SubBroker")
                    || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                    || mSession.getLoginType().equalsIgnoreCase("Region")
                    || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
                finish();
            } else if (!TextUtils.isEmpty(comingFromActivity) && comingFromActivity.equalsIgnoreCase("MyAssetType2B")) {
                super.onBackPressed();
                   /* Intent intent = new Intent(MainActivity.this, HelpTypeTwoBActivity.class);

                    startActivity(intent);*/
            } else {
                displayViewOther(57, null);

            }
        } else if (id == R.id.ll_middle_view_logo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                onHomeLogoIconClick();
            }
        } else if (id == R.id.iv_notification || id == R.id.fl_notifications_3_b) {
            displayViewOther(85, null);
        }

    }

    public void onPortFolioClick() {
        navItemIndex = 1;
        CURRENT_TAG = TAG_PORTFOLIO;
        showSelectedPortFolio();
        loadHomeFragment();
    }

    /*************************************************
     * Method called when user clicks on cart icon
     ***********************************************************/
    private void onCartIconClick() {
        try {
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                displayViewOther(39, null);
            } else {
                displayViewOther(4, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                tvNotficationBadge3B.setVisibility(View.VISIBLE);
                tv_notification_badge.setText("" + jsonArray.length());
                tvNotficationBadge3B.setText("" + jsonArray.length());
            } else {
                tv_notification_badge.setVisibility(View.GONE);
                tvNotficationBadge3B.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**********************************************************
     BroadCastReceiver for get notification Check
     ***********************************************************/

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getStringExtra("action");
            if (action.isEmpty()) {

                tv_notification_badge.setVisibility(View.INVISIBLE);
                tvNotficationBadge3B.setVisibility(View.INVISIBLE);
            } else {
                tv_notification_badge.setVisibility(View.VISIBLE);
                tvNotficationBadge3B.setVisibility(View.VISIBLE);
                notificationCount();
            }
        }
    };
    //keytool -list -v -keystore /home/dev-server/sapient.dat


    /***************************************************************
     * This method sets Up Initial UI as per the check Operation
     * ****************************************************************/
    private void setUpInitialUi(Bundle savedInstanceState) {
        if (mSession.getHasLoging()) {
            if ((mSession.getLoginType().equals("Broker")
                    || mSession.getLoginType().equals("SubBroker")
                    || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                    || mSession.getLoginType().equalsIgnoreCase("Region")
                    || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
                fl_notifications.setVisibility(View.GONE);
                flNotify3B.setVisibility(View.GONE);
                ivContactUs.setImageResource(R.drawable.back_arrow);
                ivContactUs.setVisibility(View.VISIBLE);
            } else if (comingFromActivity.equalsIgnoreCase("MyAssetType2B")) {
                ivContactUs.setImageResource(R.drawable.back_arrow);
                ivContactUs.setVisibility(View.VISIBLE);
            } else {
                if (Utils.getConfigData(mSession).optString("Notification").equalsIgnoreCase("Y")) {
                    fl_notifications.setVisibility(View.VISIBLE);
                    flNotify3B.setVisibility(View.VISIBLE);
                } else {
                    fl_notifications.setVisibility(View.GONE);
                    flNotify3B.setVisibility(View.GONE);

                }

                ivContactUs.setImageResource(R.mipmap.ic_dashboard_contact);
            }
        } else {
            ivContactUs.setVisibility(View.VISIBLE);
        }


        if (savedInstanceState == null) {
            if (mCallCommingFrom.equalsIgnoreCase("instruction")) {
                Bundle bundle = new Bundle();
                bundle.putString("coming_from", mCallCommingFrom);
                displayViewOther(0, bundle);
                displayViewOther(5, bundle);
            } else if (mCallCommingFrom.equalsIgnoreCase("notification")) {
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") ||
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")
                                || Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                    displayViewOther(85, null);
                } else {
                    displayViewOther(0, null);
                    displayViewOther(85, null);
                }
            } else if (mSession.getHasLoging()) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                showClientUi();
                loadHomeFragment();

            } else if (mCallCommingFrom.equals(AppConstants.LOGIN_FOR_REGISTRATION)) {
                displayViewOther(0, null);
                Bundle bundle = new Bundle();
                bundle.putString("type", "comming_from_login");
                displayViewOther(5, bundle);

            } /*else if (mSession.getUserType().equals("2") && !mSession.getHasFirstTimeCompleted()) {
                displayViewOther(0, null);
                Bundle bundle = new Bundle();
                bundle.putString("type", "fcm_registration");
                displayViewOther(5, bundle);
            } else if (mSession.getUserType().equals("3") && !mSession.getHasFirstTimeCompleted()) {
                mSession.setHasFirstTimeCompleted(true);
                displayViewOther(0, null);
                displayViewOther(5, null);
            } */ else {
                displayViewOther(0, null);
            }
        }

    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        /*setToolbarTitle();*/

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

    private void getActivityTypeCheck(String type, String position) {
        if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("49")) {
            displayViewOther(49, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("1")) {
            displayViewOther(1, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("41")) {
            displayViewOther(41, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("66")) {
            displayViewOther(66, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("90")) {
            displayViewOther(90, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("74")) {
            displayViewOther(74, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("96")) {
            displayViewOther(96, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("97")) {
            displayViewOther(97, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("79")) {
            displayViewOther(79, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("101")) {
            displayViewOther(101, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("52")) {
            displayViewOther(52, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("70")) {
            displayViewOther(70, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("50")) {
            displayViewOther(50, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("57")) {
            displayViewOther(57, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("99")) {
            displayViewOther(99, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("58")) {
            displayViewOther(58, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("3")) {
            displayFinancialCalculators(3, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("5")) {
            displayFinancialCalculators(5, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("7")) {
            displayFinancialCalculators(7, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("8")) {
            displayFinancialCalculators(8, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("10")) {
            displayFinancialCalculators(10, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("6")) {
            displayFinancialCalculators(6, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("100")) {
            displayViewOther(100, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("39")) {
            displayViewOther(39, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("4")) {
            displayViewOther(4, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("85")) {
            displayViewOther(85, null);
        } else if (type.equalsIgnoreCase("TYPE 2") && position.equalsIgnoreCase("105")) {
            displayViewOther(105, null);
        }
    }

    private Fragment clientHomeFragment() {
        Fragment fragment = null;

        switch (navItemIndex) {
            case 0:

                showSelectedDashboard();
                fragment = new FragHomeClient();

            /*  // only short time testing
                Bundle bundle = new Bundle();
                bundle.putString("ucc_code", "5011070342");
                fragment = new FragGoForLumpsum();
                //fragment = new FragGoForSIP();
                fragment.setArguments(bundle);
                //displayViewOther(14, bundle);*/
                return fragment;

            case 1:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType"))) {
                    if (
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B") &&
                                    !(mSession.getLoginType().equalsIgnoreCase("ClientG") || FragHomeBroker.comming_from.equalsIgnoreCase("Broker_group"))
                    ) {
                        fragment = new PortfolioDetailFragment();
                    } else {
                        fragment = new PortfolioFragment();
                    }
                    showSelectedPortFolio();
                }

                return fragment;
            case 2:
                fragment = new MyTransactionNew();
                showSelectedTransaction();
                return fragment;


            default:
                return new FragHomeClient();
        }
    }


    /************
     * Method displays various fragment as per the conditions/cases satisfied
     * @param position specify the position
     * @param bundle provide the bundle object
     */
    public void displayViewOther(int position, Bundle bundle) {
        Fragment fragment = null;
        switch (position) {
            case 0:
             /*   if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 4")) {
                    fragment = new HomeClientFlavourFourFragment();
                    fragment.setArguments(bundle);
                    showClientUi();
                    showSelectedDashboard();
                }else{*/
                fragment = new FragHomeClient();
                fragment.setArguments(bundle);
                showClientUi();
                showSelectedDashboard();
                /* }*/
                break;

            case 1:
                fragment = new BelieveInExpertiseFragment();
                fragment.setArguments(bundle);
                break;

            case 2:
                fragment = new FragRecomendedAddToCart();
                fragment.setArguments(bundle);
                break;

            case 3:
                fragment = new FragGoalBaseAddToCart();
                fragment.setArguments(bundle);
                break;

            case 4:
                fragment = new FragAddToCartList();
                fragment.setArguments(bundle);
                break;

            case 5:
                fragment = new PanKycFragment();
                fragment.setArguments(bundle);
                break;

            case 6:
                fragment = new PersonalDetailsFragment();
                fragment.setArguments(bundle);
                break;

            case 7:
                fragment = new NomineeDetailsFragment();
                fragment.setArguments(bundle);
                /*fragment = new FragPersonalForm3();
                fragment.setArguments(bundle);*/
                break;

            case 8:
                fragment = new BankDetailFragment();
                fragment.setArguments(bundle);
               /* fragment = new FragPersonalForm4();
                fragment.setArguments(bundle);*/
                break;

            case 9:
                fragment = new FatcaDetailFormFragment();
                fragment.setArguments(bundle);
               /* fragment = new FragPersonalForm5();
                fragment.setArguments(bundle);*/
                break;


            case 11:
                fragment = new FragInvestNow();
                fragment.setArguments(bundle);
                break;

            case 12:
                fragment = new FragPersonalMandateForm();
                fragment.setArguments(bundle);
                break;

            case 13:
                fragment = new FragGoForSIP();
                fragment.setArguments(bundle);
                break;

            case 14:
                fragment = new FragGoForLumpsum();
                fragment.setArguments(bundle);
                break;

            case 15:
            /*    //  ucc single using
                fragment = new FragProsoectHome(); // ic_bottombar_home_inactive screen for prospect
                fragment.setArguments(bundle);*/
                break;

            case 16:
                fragment = new FragProfileIncomplete();
                fragment.setArguments(bundle);
                break;

            case 17:

                fragment = new FragLumpsumOrderStatus();
                fragment.setArguments(bundle);
                break;

            case 18:
                fragment = new FragOrderHistory();
                fragment.setArguments(bundle);
                break;

            case 19:
                fragment = new FragMyOrder();
                fragment.setArguments(bundle);
                break;

            case 20:
//                fragment = new FragLocateUs();
//                fragment.setArguments(bundle);
                break;
//
            case 21:
                fragment = new FragAddSchemesSingle();
                fragment.setArguments(bundle);
                break;


            case 22:
                fragment = new FragHomeClient();
                fragment.setArguments(bundle);
                break;


            case 23:
                fragment = new SendMailFragment();
                fragment.setArguments(bundle);
                break;

            case 24:
                fragment = new My_Journey();
                fragment.setArguments(bundle);
                break;


            case 25:
                fragment = new WatchListFragment();
                fragment.setArguments(bundle);
                break;

            case 26:
                fragment = new FragHomeBroker();
                fragment.setArguments(bundle);
                break;

            case 27:

               /* fragment = new Broker_Detail_Frag();
                fragment.setArguments(bundle);*/
                break;

            case 28:
                fragment = new AdditionalPurchaseTransactFragment();
                fragment.setArguments(bundle);
                break;


            case 29:
                fragment = new AdditionalSipTransactFragment();
                fragment.setArguments(bundle);
                break;

            case 30:
                fragment = new AdditionalSwitchTransactFragment();
                fragment.setArguments(bundle);
                break;

            case 31:
                fragment = new AdditionalSwpTransactFragment();
                fragment.setArguments(bundle);
                break;

            case 32:
                fragment = new AdditonalStpTransactFragment();
                fragment.setArguments(bundle);
                break;

            case 33:
                fragment = new AdditionalRedeemTransactFragment();
                fragment.setArguments(bundle);
                break;

            case 34:
                fragment = new FragMySipHome();
                fragment.setArguments(bundle);
                break;

            case 35:
                fragment = new FragSipStpSwpDetails();
                fragment.setArguments(bundle);
                break;

            case 36:
                fragment = new FragProfileList();
                fragment.setArguments(bundle);
                break;

            case 37:
                fragment = new FragMendateList();
                fragment.setArguments(bundle);
                break;

            case 38:
                fragment = new ChangePasswordFragment();
                fragment.setArguments(bundle);
                break;

            case 39:
                fragment = new FragEmptyCart();
                fragment.setArguments(bundle);
                break;

            case 40:
                fragment = new FragFolioList();
                fragment.setArguments(bundle);
                break;

            case 41:
                fragment = new TopSchemeFragment();
                fragment.setArguments(bundle);
                break;

            case 42:
                fragment = new FactSheetFragment();
                fragment.setArguments(bundle);
                break;

            case 43:
                fragment = new DividendFragment();
                fragment.setArguments(bundle);
                break;

            case 44:
                fragment = new MyInsuranceFragment();
                fragment.setArguments(bundle);
                break;

            case 45:
                fragment = new DividendDetailFragment();
                fragment.setArguments(bundle);
                break;

            case 46:
                AppApplication.portfolio_detail_data = "";
                AppApplication.porfolio_detail_data2 = "";
                fragment = new PortfolioDetailFragment();
                fragment.setArguments(bundle);
                break;

            case 47:
                fragment = new SchemeDetailFragment();
                fragment.setArguments(bundle);
                break;


            case 48:

                fragment = new TaxSavingFragment();
                fragment.setArguments(bundle);
                break;

            case 49:
                fragment = new FundPicksFragment();
                fragment.setArguments(bundle);


            case 50:
                fragment = new FundPicksFragment();
                fragment.setArguments(bundle);
                break;

            case 51:
                fragment = new FragTransferHolding();
                fragment.setArguments(bundle);
                break;

            case 52:
                fragment = new FragTransferHoldingIntro();
                fragment.setArguments(bundle);
                break;

            case 53:
                fragment = new Frag_New_Purchase();
                fragment.setArguments(bundle);
                break;

            case 54:
                fragment = new ChequeUpload();
                fragment.setArguments(bundle);
                break;

            case 55:

                break;

            case 56:
                fragment = new FragChangeAppSecurity();
                fragment.setArguments(bundle);
                break;

            case 57:
                fragment = new FragHelpHome();
                fragment.setArguments(bundle);
                break;

            case 58:
                fragment = new FinancialToolFragment();
                fragment.setArguments(bundle);
                break;

            case 59:

                break;

            case 60:
                fragment = new FragConfirmRiskProfile();
                fragment.setArguments(bundle);
                break;

            case 61:
                fragment = new StartAssessmentFrag();
                fragment.setArguments(bundle);
                break;

            case 62:
                fragment = new FragQuestions();
                fragment.setArguments(bundle);
                break;

            case 63:
              /*  fragment = new InvestmentFrag();
                fragment.setArguments(bundle);*/
                break;

            case 64:
                fragment = new PersonalInvestmentFrag();
                fragment.setArguments(bundle);
                break;

            case 65:
                fragment = new ActiveStatus();
                fragment.setArguments(bundle);
                break;

            case 66:
                fragment = new SearchAmcFragment();
                fragment.setArguments(bundle);
                break;

            case 67:
                fragment = new FragNavScheme();
                fragment.setArguments(bundle);
                break;

            case 68:
                fragment = new FragTaxSavingDetail();
                fragment.setArguments(bundle);
                break;

            case 69:

                break;

            case 70:
                fragment = new FragGoalList();
                fragment.setArguments(bundle);
                break;

            case 71:
                fragment = new FragGoalSummeryDetails();
                fragment.setArguments(bundle);
                break;

            case 72:
                fragment = new BseProfileDetailFrag();
                fragment.setArguments(bundle);
                break;

            case 73:
                fragment = new SetFlavourFrag();
                fragment.setArguments(bundle);
                break;

            case 74:
                fragment = new GetFlavourFrag();
                fragment.setArguments(bundle);
                break;

            case 75:

                fragment = new FragSearchSchemes();
                fragment.setArguments(bundle);

                break;

            case 76:
                fragment = new FragCreateGoal();
                fragment.setArguments(bundle);
                break;

            case 77:
                fragment = new FragGoalCategory();
                fragment.setArguments(bundle);
                break;

            case 78:
                fragment = new FolioLookupFragment();
                fragment.setArguments(bundle);
                break;

            case 79:
                fragment = new SimplySaveFrag();
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
            case 82:
                fragment = new FragProfileDetails();
                fragment.setArguments(bundle);
                break;

            case 83:
                fragment = new FragmentPaymentMethod();
                fragment.setArguments(bundle);
                break;

            case 84:
                //fragment = new FragPrePayment();
                fragment = new FragPrePaymentHome();
                fragment.setArguments(bundle);
                break;

            case 85:
                fragment = new FragNotification();
                fragment.setArguments(bundle);
                break;

            case 86:
                fragment = new NseFatacaFragment();
                fragment.setArguments(bundle);
                break;
            case 87:
                fragment = new FragFolioLookupDetails();
                fragment.setArguments(bundle);
                break;
            case 89:
                fragment = new FragProfileSettings();
                fragment.setArguments(bundle);
                break;

            case 90:
                fragment = new FragNewFundOffers();
                fragment.setArguments(bundle);
                break;

            case 91:
                fragment = new FragNFOCart();
                fragment.setArguments(bundle);
                break;

            case 92:
                fragment = new LanguageChangeFragment();
                fragment.setArguments(bundle);
                break;

            case 93:
                fragment = new FragPreSignature();
                fragment.setArguments(bundle);
                break;

            case 94:
                fragment = new FragPreCheque();
                fragment.setArguments(bundle);
                break;

            case 95:
                fragment = new FragPreDoc();
                fragment.setArguments(bundle);
                break;

            case 96:
                fragment = new JustSaveFrag();
                fragment.setArguments(bundle);
                break;
            case 97:
                fragment = new GetJustSaveFrag();
                fragment.setArguments(bundle);
                break;
            case 98:
                fragment = new ServiceRequestFragment();
                fragment.setArguments(bundle);
                break;
            case 99:
                fragment = new FeedbackFragment();
                fragment.setArguments(bundle);
                break;
            case 100:
                fragment = new FragAllDocuments();
                fragment.setArguments(bundle);
                break;

            case 101:
                fragment = new FragPreCASUpload();
                fragment.setArguments(bundle);
                break;


            case 102:
                fragment = new GoalCategoryTypeOneAFragment();
                fragment.setArguments(bundle);
                break;

            case 103:
                fragment = new FragUploadModule();
                fragment.setArguments(bundle);
                break;

            case 104:
                fragment = new frag_kyc_Form();
                fragment.setArguments(bundle);
                break;

            case 106:
                fragment = new FragExistingHome();
                fragment.setArguments(bundle);
                break;

            case 105:
                fragment = new ExistingInvestmentSchemeFragment();
                fragment.setArguments(bundle);
                break;

            case 107:
                fragment = new GoalVersonOneFragment();
                fragment.setArguments(bundle);
                break;
            case 108:
                fragment = new GoalSummaryDetailsFragment();
                fragment.setArguments(bundle);
                break;
            case 109:
                fragment = new FragMySystematicTransanctionsHome();
                fragment.setArguments(bundle);
                break;

            case 110:
                fragment = new FragMyOrderReport();
                fragment.setArguments(bundle);
                break;

            case 111:
                fragment = new TopSIPSchemeFragment();
                fragment.setArguments(bundle);
                break;

            case 112:
                fragment = new MyTransactionNew();
                fragment.setArguments(bundle);
                break;

            case 113:
                fragment = new InvestmentConfirmFragment();
                fragment.setArguments(bundle);
                break;
            case 114:
                fragment = new FragClientDocView();
                fragment.setArguments(bundle);
                break;
            case 115:
                fragment = new FragDocViewer();
                fragment.setArguments(bundle);
                break;
            case 116:
                fragment = new FragMandateUpload();
                fragment.setArguments(bundle);
                break;
            case 117:
                fragment = new FragClientBankList();
                fragment.setArguments(bundle);
                break;
            case 118:
                fragment = new FragAllBankListIndia();
                fragment.setArguments(bundle);
                break;
            case 119:
                fragment = new FragAddBank();
                fragment.setArguments(bundle);
                break;
            case 120:
                fragment = new InstantNSEAcivationFragment();
                fragment.setArguments(bundle);
                break;
            case 121:
                fragment = new FragMyOrderCeaseSIP();
                fragment.setArguments(bundle);
                break;
            case 122:
                fragment = new FragMyOrderCeaseNSE();
                fragment.setArguments(bundle);
                break;

            case 123:
                fragment = new FragSchemeVideo();
                fragment.setArguments(bundle);
                break;

            case 124:
                fragment = new FragDocumentDetailsList();
                fragment.setArguments(bundle);
                break;
        }
        if (addToBackStack.equalsIgnoreCase("YES")) {
            if (fragment instanceof FundPicksFragment ||
                    fragment instanceof TopSchemeFragment || fragment instanceof TopSIPSchemeFragment ||
                    fragment instanceof BelieveInExpertiseFragment ||
                    fragment instanceof FragGoalList ||
                    fragment instanceof SearchAmcFragment ||
                    fragment instanceof FragNewFundOffers ||
                    fragment instanceof GetFlavourFrag ||
                    fragment instanceof SimplySaveFrag ||
                    fragment instanceof FragTransferHoldingIntro ||
                    fragment instanceof JustSaveFrag ||
                    fragment instanceof FragPreCASUpload ||
                    fragment instanceof FragHelpHome ||
                    fragment instanceof FeedbackFragment ||
                    fragment instanceof FragAllDocuments ||
                    fragment instanceof FragEmptyCart ||
                    fragment instanceof FragNotification ||
                    fragment instanceof FragAddToCartList ||
                    fragment instanceof FragExistingHome ||
                    fragment instanceof FragSearchSchemes ||
                    fragment instanceof FinancialToolFragment
            ) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out).replace(R.id.fl_main_container, fragment).commit();
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (fragment != null) {
                    fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out).addToBackStack(null).replace(R.id.fl_main_container, fragment, "" + count).commit();
                }
            }
        } else if (addToBackStack.equalsIgnoreCase("YES") &&
                comingFromActivity.equalsIgnoreCase("MyAssetType2B")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out).replace(R.id.fl_main_container, fragment).commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (fragment != null) {
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out).addToBackStack(null).replace(R.id.fl_main_container, fragment, "" + count).commit();
            }
        }
    }

    /****************************************************
     * Method called when cart update operation is going
     ****************************************************/
    @SuppressLint("SetTextI18n")
    public void updateCart() {
        try {
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                tvCartBadge.setVisibility(View.GONE);
                tvCartBadge3b.setVisibility(View.GONE);
            } else {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge3b.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                tvCartBadge.setText("" + jsonArray.length());
                tvCartBadge3b.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /******************************************************
     * ***Method removes fragments from the BackStack of the app
     ********************************************************/
    public void removeAllStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++)
            fragmentManager.popBackStack();
    }

    /******************************************************
     * ***Removing a Fragment from the back stack
     ********************************************************/
    public void removesFragmentsFromBackStack(int count) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(
                fragmentManager.getBackStackEntryCount() - count).getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateCart();
        registerReceiver(myReceiver, new IntentFilter("FBR-IMAGE"));
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
        /*if (position == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out).replace(R.id.fl_main_container, fragment).commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragment != null) {
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out).addToBackStack(null).replace(R.id.fl_main_container, fragment).commit();
            }
        }*/

        if (addToBackStack.equalsIgnoreCase("YES")) {
            if (fragment instanceof FragSip ||
                    fragment instanceof FragSipStepup ||
                    fragment instanceof FragEmiCalculator ||
                    fragment instanceof FragSipDelayCost ||
                    fragment instanceof FragRetirmentCalculator ||
                    fragment instanceof FragEducationCalculator ||
                    fragment instanceof FragMarriegeCalculator ||
                    fragment instanceof FragSpareMoneyCal ||
                    fragment instanceof FragTaxInvestmentCal


            ) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out).replace(R.id.fl_main_container, fragment).commit();
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (fragment != null) {
                    fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out).addToBackStack(null).replace(R.id.fl_main_container, fragment, "" + count).commit();
                }
            }
        } else if (addToBackStack.equalsIgnoreCase("YES") &&
                comingFromActivity.equalsIgnoreCase("MyAssetType2B")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out).replace(R.id.fl_main_container, fragment).commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (fragment != null) {
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out).addToBackStack(null).replace(R.id.fl_main_container, fragment, "" + count).commit();
            }
        }
    }

    public void pickPhoto() {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(MainActivity.this, 108);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);

        if (requestCode == REQUEST_APP_UPDATE) {
            if (resultCode == RESULT_OK) {

                startActivity(new Intent(getBaseContext(), SplashActivity.class));


            }
        }

        if (resultCode == 100) {
            String isSingUploaded = result.getStringExtra("signUploaded");
            if (isSingUploaded.equals("yes")) {
                Bundle bundle = result.getExtras();
               /* bundle.putString("ucc_code", result.getStringExtra("ucc_code"));
                bundle.putString("file1", result.getStringExtra("File1"));
                bundle.putString("coming_from", result.getStringExtra("coming_from"));
*/
                if (result.getStringExtra("chequeRequired").equalsIgnoreCase("N")) {
                    Intent intent = new Intent(getBaseContext(), AccountConfActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                   // displayViewOther(94, bundle);
                    displayViewOther(124, bundle);
                }

            }
        } else if (resultCode == 500) {//ClientG Client
            if ((mSession.getHasLoging() && mSession.getLoginType().equals("ClientG"))
                    || mSession.getHasLoging() && mSession.getLoginType().equals("Client")) {
                removeAllStack();
                displayViewOther(0, null);
                // displayViewOther(15, null);
            } else if ((mSession.getHasLoging() && mSession.getLoginType().equals("SubBroker"))
                    || mSession.getHasLoging() && mSession.getLoginType().equals("RM")
                    || mSession.getHasLoging() && mSession.getLoginType().equals("Broker") || mSession.getLoginType().equalsIgnoreCase("Zone")
                    || mSession.getLoginType().equalsIgnoreCase("Region")
                    || mSession.getLoginType().equalsIgnoreCase("Branch")) {
                removeAllStack();
                displayViewOther(0, null);
                displayViewOther(26, null);

            } else {
                removeAllStack();
                displayViewOther(0, null);
                displayViewOther(15, null);
            }
        } else if (requestCode == 203) {
            if (fragment instanceof ChequeUpload) {
                fragment.onActivityResult(requestCode, resultCode, result);
            }


        } else if (requestCode == 300) {
            if (fragment instanceof FragProfileSettings) {
                fragment.onActivityResult(requestCode, resultCode, result);
            }


        } else if (requestCode == 600) {
            if (fragment instanceof FragMandateUpload) {
                fragment.onActivityResult(requestCode, resultCode, result);
            }
        } else if (requestCode == 108) {
            if (fragment instanceof FragClientBankList) {
                fragment.onActivityResult(requestCode, resultCode, result);
            }
        } else if (requestCode == 109) {
            if (fragment instanceof FragAddBank) {
                fragment.onActivityResult(requestCode, resultCode, result);
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            callErrorDialog(this, getResources().getString(R.string.error), getResources().getString(R.string.main_activity_crop_error)
                    , getResources().getString(R.string.text_ok), "", true, false);

        }
    }



    public void whenToShowReviewDailog() {
        int count = mSession.getCountOfScreen();
        count = count + 1;
        mSession.setCountOfScreen(count);
        int noOfAppOpen = mSession.getCountOfAppOpen();

        if (mSession.getHasFirstTimeReviewShown()) {
            if (mSession.getAskMeLater()) {
                Date lastDateOfShowwn = Utils.getTodayDate(mSession.getShownDateOfReview());
                long miliSecond = lastDateOfShowwn.getTime();
                Date today = new Date();
                long miliSecondToday = today.getTime();

                long diff = miliSecondToday - miliSecond;
                long days = (diff / (60 * 60 * 24 * 1000));
                if (days >= 5 && noOfAppOpen > 5) {
                    mSession.setCountOfAppOpen(0);
                    dailogForRateUs();
                }
            }

        } else {
            if (count >= 10 && noOfAppOpen >= 3) {
                mSession.setCountOfAppOpen(0);
                dailogForRateUs();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void dailogForRateUs() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dailog_review, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);
        Button btNoThanks = dialogView.findViewById(R.id.btNoThanks);
        Button btReminder = dialogView.findViewById(R.id.btReminder);
        Button btRateNow = dialogView.findViewById(R.id.btRateNow);

        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= 21) {
            linerMain.setBackground(ContextCompat.getDrawable(this, R.drawable.dialog_background_inset));
            relSubMenu.setBackground(ContextCompat.getDrawable(this, R.drawable.dialog_header_background));
            GradientDrawable drawable = (GradientDrawable) relSubMenu.getBackground();
            drawable.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            relSubMenu.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        btNoThanks.setOnClickListener(v -> {
            alertDialog.dismiss();
            mSession.setAskMeLater(false);
        });

        btReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                mSession.setAskMeLater(true);

            }
        });

        btRateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                mSession.setAskMeLater(false);
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        mSession.setHasFirstTimeReviewShown(true);
        mSession.setShownDateOfReview(Utils.getTodayDateString());
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void callErrorDialog(Context context, String title, String msg, String btnOne, String btnTwo, boolean isShowingCancelBtn, boolean isShowingOkBtn) {
        customDialog.showDialog(context, title,
                msg,
                btnOne, btnTwo, isShowingCancelBtn, isShowingOkBtn);

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

            TextView tvOk = dialogView.findViewById(R.id.tvOk);
            RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                    alertDialog.dismiss();
                }
            });
            if (alertDialog != null) {
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                });
            }

            alertDialog.setCancelable(false);
            alertDialog.show();

        }
    }

    /********************************************************************
     * Method converts currency to specified currency format
     * @param currency provide the currency
     * @param editText  provide the editext element
     **************************************************************************/
    public void convertIntoCurrencyFormat(String currency, EditText editText) {
        try {
            Format format = NumberFormat.getNumberInstance(new Locale("en", "IN"));
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

    private void getDocument() {
        String url = Config.GET_DOCUMENTs;
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Passkey", mSession.getPassKey());
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                try {

                    String status = object.optString("Status");
                    if (status.equalsIgnoreCase("True")) {
                        mSession.setDocumentData(object.toString());
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                        if (fragment instanceof FragHomeClient) {
                            ((FragHomeClient) fragment).updateDocumentdata();
                        }
                    } else {
                        /*mApplication.showSnackBar(toolbar, object.optString("ServiceMSG"));*/
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void showClientUi() {
        llDashBoard.setVisibility(View.VISIBLE);
        llMore.setVisibility(View.VISIBLE);
        llPortfolio.setVisibility(View.VISIBLE);
        llTransaction.setVisibility(View.VISIBLE);


    }

    private void  showSelectedDashboard() {
        vDashboard.setVisibility(View.VISIBLE);
        vTransaction.setVisibility(View.GONE);
        vMore.setVisibility(View.GONE);
        vPortfolio.setVisibility(View.GONE);

        tvMore.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvTransaction.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvPortfolio.setTextColor(getResources().getColor(R.color.colorGrey_400));


        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
            ivTransaction.setColorFilter(null);
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            tvDashboard.setTextColor(getResources().getColor(R.color.darkColorAccent));

        } else {
            tvDashboard.setTextColor(getResources().getColor(R.color.colorAccent));

        }

    }

    private void showSelectedPortFolio() {
        vDashboard.setVisibility(View.GONE);
        vTransaction.setVisibility(View.GONE);

        vMore.setVisibility(View.GONE);
        vPortfolio.setVisibility(View.VISIBLE);
        tvDashboard.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvMore.setTextColor(getResources().getColor(R.color.colorGrey_400));
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
            ivTransaction.setColorFilter(null);
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {


            tvPortfolio.setTextColor(getResources().getColor(R.color.darkColorAccent));
        } else {

            tvPortfolio.setTextColor(getResources().getColor(R.color.colorAccent));
        }


    }


    private void showSelectedTransaction() {
        vDashboard.setVisibility(View.GONE);
        vTransaction.setVisibility(View.VISIBLE);

        vMore.setVisibility(View.GONE);
        vPortfolio.setVisibility(View.GONE);
        tvDashboard.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvMore.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvPortfolio.setTextColor(getResources().getColor(R.color.colorGrey_400));


        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {


            tvTransaction.setTextColor(getResources().getColor(R.color.darkColorAccent));
        } else {

            tvTransaction.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void showSelectedMore() {
        vDashboard.setVisibility(View.GONE);
        vTransaction.setVisibility(View.GONE);

        vMore.setVisibility(View.VISIBLE);
        vPortfolio.setVisibility(View.GONE);
        tvDashboard.setTextColor(getResources().getColor(R.color.colorGrey_400));

        tvTransaction.setTextColor(getResources().getColor(R.color.colorGrey_400));
        tvPortfolio.setTextColor(getResources().getColor(R.color.colorGrey_400));


        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            tvMore.setTextColor(getResources().getColor(R.color.darkColorAccent));
        } else {
            tvMore.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }


    /**********************************************************************************
     * Method Shows a kind of alert dialog which asks user to enable/disable notifications
     * @param intent provide the intent object
     **********************************************************/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void dialogNotification(final Intent intent) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_notification, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);


        investwell.utils.customView.CustomButton yes_btn = dialogView.findViewById(R.id.yes_btn);
        investwell.utils.customView.CustomButton no_btn = dialogView.findViewById(R.id.no_btn);
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
        btDone.setText("Log Out");
        CustomButton btCalcel = dialogView.findViewById(R.id.btCalcel);
        btCalcel.setText("Cancel");
        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);

        tvTitle.setText("Logout From " + getResources().getString(R.string.app_name));
        tvMessage.setText(getString(R.string.alert_dialog_logout_desc_txt));


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
                notificationCount();
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") || Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                    Intent intent = new Intent(getApplicationContext(), MainActivityTypeTwo.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
                    Intent intent = new Intent(getApplicationContext(), WelcomeOptionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    displayViewOther(0, null);
                }
                if (bottomSheetFragment.isVisible()) {
                    bottomSheetFragment.dismiss();
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
                                MainActivity.this, REQUEST_APP_UPDATE);
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

    /*************************************************
     * Method called when user clicks on ic_bottombar_home_inactive logo
     ***********************************************************/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onHomeLogoIconClick() {
        count = count + 1;
        //mSession.setBid(AppConstants.VALUE_BROKER_ID);
     /*   mSession.setPassword(AppConstants.VALUE_PASSWORD);
        mSession.setUserName(AppConstants.VALUE_USERNAME);*/
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (count == 5) {

            final Dialog dialog = new Dialog(Objects.requireNonNull(this));
            dialog.setContentView(R.layout.testing_view);
            final RadioGroup radiogroup = dialog.findViewById(R.id.radiogroup);
            int checkedId = sharedPrefs.getInt("CheckedId", pos);
            radiogroup.check(radiogroup.getChildAt(checkedId).getId());
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    int position = radiogroup.indexOfChild(dialog.findViewById(i));

                    switch (position) {

                        case 0:
                            pos = 0;
                            mSession.setBid("30497");
                            mSession.setUserName("IwellDemo3");
                            mSession.setPassword("kksj8375TYW^1*fT");

                            break;

                        case 1:
                            pos = 1;
                            mSession.setBid("20000");
                            mSession.setUserName("IwellDemo2");
                            mSession.setPassword("jdh837TERU*@j$k56^");


                            break;

                        case 2:
                            pos = 2;
                            //mSession.setBid(AppConstants.VALUE_BROKER_ID);
                          /*  mSession.setPassword(AppConstants.VALUE_PASSWORD);
                            mSession.setUserName(AppConstants.VALUE_USERNAME);*/
                            break;


                    }

                }
            });

            dialog.findViewById(R.id.check_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPassKey();
                    dialog.dismiss();
                }
            });

            dialog.setCancelable(false);
            dialog.show();
            count = 0;
        }
    }

    /***************************************************
     * Method containing code the gets PassKey from the API
     ******************************************************/
    @SuppressLint("HardwareIds")
    private void getPassKey() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_USERNAME, AppConstants.APP_USERNAME);
        params.put(AppConstants.KEY_USER_PASSWORD, AppConstants.APP_PASSWORD);
        params.put("Bid", AppConstants.APP_BID);
        String macAddress = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            macAddress = Settings.Secure.getString(MainActivity.this.getContentResolver(), "bluetooth_address");
        }
        @SuppressLint("HardwareIds") String android_id = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            android_id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        params.put("IMEINO", android_id + "" + macAddress);
        params.put("Phonename", Build.BRAND);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.GET_GENERATE, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optBoolean("Status")) {
                    mSession.setPassKey(response.optString("Passkey"));
                    mSession.setAppType(response.optString("OnlineOption"));
                } else {
                    // Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                    mApplication.showSnackBar(tvCartBadge, getResources().getString(R.string.error_try_again));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                mApplication.showSnackBar(tvCartBadge, getResources().getString(R.string.error_try_again));
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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);
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
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogBtnClick(View view) {

    }


}

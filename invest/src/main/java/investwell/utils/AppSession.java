package investwell.utils;

import android.content.Context;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Created by SK-Accocoate on 09/26/2015.
 */
public class AppSession {

    public static int value;
    private static AppSession session;
    private SharedPreferences prefs;

    public AppSession(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);

    }

    public static AppSession getInstance(Context cntx) {
        if (session == null)
            session = new AppSession(cntx);

        return session;
    }


    public void setDevicetokn(String devicetokn) {
        prefs.edit().putString("devicetokn", devicetokn).apply();
    }

    public void setAppType(String appType) {
        prefs.edit().putString("appType", appType).apply();
    }

    public String getAppType() {
        String appType = prefs.getString("appType", "");
        return appType;
    }

    public void setAudioPermission(String audioPermssion) {
        prefs.edit().putString("audioPermssion", audioPermssion).apply();
    }

    public void setShownDateOfReview(String shownDateOfReview) {
        prefs.edit().putString("shownDateOfReview", shownDateOfReview).apply();
    }

    public String getShownDateOfReview() {
        String shownDateOfReview = prefs.getString("shownDateOfReview", "");
        return shownDateOfReview;
    }

    public String getAudioPermission() {
        String audioPermssion = prefs.getString("audioPermssion", "");
        return audioPermssion;
    }

    // for Review
    public boolean getHasFirstTimeReviewShown() {
        return prefs.getBoolean("hasFirstTimeReviewShown", false);
    }

    public void setHasFirstTimeReviewShown(boolean hasFirstTimeReviewShown) {
        prefs.edit().putBoolean("hasFirstTimeReviewShown", hasFirstTimeReviewShown).apply();
    }

    public boolean getAskMeLater() {
        return prefs.getBoolean("askMeLater", false);
    }

    public void setAskMeLater(boolean askMeLater) {
        prefs.edit().putBoolean("askMeLater", askMeLater).apply();
    }

    public void setMySipType(String sipType) {
        prefs.edit().putString("sipType", sipType).apply();
    }

    public void setCountOfAppOpen(int countOfAppOpen) {
        prefs.edit().putInt("countOfAppOpen", countOfAppOpen).apply();
    }

    public int getCountOfAppOpen() {
        int countOfAppOpen = prefs.getInt("countOfAppOpen", 0);
        return countOfAppOpen;
    }

    public String getSipType() {
        String sipType = prefs.getString("sipType", "");
        return sipType;
    }

    public String getSelectedBasketCode() {
        String basketCode = prefs.getString("basketCode", "");
        return basketCode;
    }

    public void setCountOfScreen(int countOfScreen) {
        prefs.edit().putInt("countOfScreen", countOfScreen).apply();
    }

    public int getCountOfScreen() {
        int countOfScreen = prefs.getInt("countOfScreen", 0);
        return countOfScreen;
    }

    public void setSelectedBasketCode(String basketCode) {
        prefs.edit().putString("basketCode", basketCode).apply();
    }

    public void set_login_detail(String value) {
        prefs.edit().putString("login_detail", value).apply();

    }


    public String get_login_detail() {

        String value = prefs.getString("login_detail", "");
        return value;
    }

    public void set_coming(String value) {
        prefs.edit().putString("coming", value).apply();

    }

    public String get_coming() {

        String value = prefs.getString("coming", "");
        return value;
    }

    //Accelerator
    public void setAccelerator(String value) {
        prefs.edit().putString("accelerator", value).apply();

    }

    public String getAccelerator() {

        String value = prefs.getString("accelerator", "");
        return value;
    }

    public void setAcceleratorDesc(String value) {
        prefs.edit().putString("accDesc", value).apply();

    }

    public String getAcceleratorDesc() {

        String value = prefs.getString("accDesc", "");
        return value;
    }

    //FundPicks
    public void setFundPicks(String value) {
        prefs.edit().putString("fund", value).apply();

    }

    public String getFundPicks() {

        String value = prefs.getString("fund", "");
        return value;
    }

    public void setFundPicksDesc(String value) {
        prefs.edit().putString("fundDesc", value).apply();

    }

    public String getFundPicksDesc() {

        String value = prefs.getString("fundDesc", "");
        return value;
    }
    //Invest Now
    public void setInvestNow(String value) {
        prefs.edit().putString("investNow", value).apply();

    }

    public String getInvestNow() {

        String value = prefs.getString("investNow", "");
        return value;
    }

    public void setInvestNowDesc(String value) {
        prefs.edit().putString("investNowDesc", value).apply();

    }

    public String getInvestNowDesc() {

        return prefs.getString("investNowDesc", "");
    }
    //NFO
    public void setNfo(String value) {
        prefs.edit().putString("nfoTitle", value).apply();

    }

    public String getNfo() {

        String value = prefs.getString("nfoTitle", "");
        return value;
    }

    public void setNfoDesc(String value) {
        prefs.edit().putString("nfoTitleDesc", value).apply();

    }

    public String getNfoDesc() {

        String value = prefs.getString("nfoTitleDesc", "");
        return value;
    }
    //CAS
    public void setCas(String value) {
        prefs.edit().putString("casTitle", value).apply();

    }

    public String getCas() {

        String value = prefs.getString("casTitle", "");
        return value;
    }

    public void setCasDesc(String value) {
        prefs.edit().putString("casTitleDesc", value).apply();

    }

    public String getCasDesc() {

        String value = prefs.getString("casTitleDesc", "");
        return value;
    }
//FOM
    public void setFom(String value) {
        prefs.edit().putString("fomTitle", value).apply();

    }

    public String getFom() {

        String value = prefs.getString("fomTitle", "");
        return value;
    }

    public void setFomDesc(String value) {
        prefs.edit().putString("fomTitleDesc", value).apply();

    }

    public String getFomDesc() {

        String value = prefs.getString("fomTitleDesc", "");
        return value;
    }

    //holding
    public void setTHolding(String value) {
        prefs.edit().putString("holdingTitle", value).apply();

    }

    public String getTHolding() {

        String value = prefs.getString("holdingTitle", "");
        return value;
    }

    public void setTHoldingDesc(String value) {
        prefs.edit().putString("holdingTitleDesc", value).apply();

    }

    public String getTHoldingDesc() {

        String value = prefs.getString("holdingTitleDesc", "");
        return value;
    }
    //Simply Save
    public void setSimplySave(String value) {
        prefs.edit().putString("SimplyTitle", value).apply();

    }

    public String getSimplySave() {

        String value = prefs.getString("SimplyTitle", "");
        return value;
    }

    public void setSimplySaveDesc(String value) {
        prefs.edit().putString("SimplyTitleDesc", value).apply();

    }

    public String getSimplySaveDesc() {

        String value = prefs.getString("SimplyTitleDesc", "");
        return value;
    }
    //Just Save
    public void setJustSave(String value) {
        prefs.edit().putString("JustTitle", value).apply();

    }

    public String getJustSave() {

        String value = prefs.getString("JustTitle", "");
        return value;
    }

    public void setJustSaveDesc(String value) {
        prefs.edit().putString("JustTitleDesc", value).apply();

    }

    public String getJustSaveDesc() {

        String value = prefs.getString("JustTitleDesc", "");
        return value;
    }
    //Existing Scheme
    public void setExistingScheme(String value) {
        prefs.edit().putString("existingScheme", value).apply();

    }

    public String getExistingScheme() {

        String value = prefs.getString("existingScheme", "");
        return value;
    }

    public void setExistingSchemeDesc(String value) {
        prefs.edit().putString("existingSchemeDesc", value).apply();

    }

    public String getExistingSchemeDesc() {

        String value = prefs.getString("existingSchemeDesc", "");
        return value;
    }

    //Service Request
    public void setServiceReq(String value) {
        prefs.edit().putString("serviceReq", value).apply();

    }

    public String getServiceReq() {

        String value = prefs.getString("serviceReq", "");
        return value;
    }

    public void setServiceReqDesc(String value) {
        prefs.edit().putString("serviceReqDesc", value).apply();

    }

    public String getServiceReqDesc() {

        String value = prefs.getString("serviceReqDesc", "");
        return value;
    }

    //Performer
    public void setTopPerformer(String value) {
        prefs.edit().putString("performer", value).apply();

    }

    public String getTopPerformer() {

        String value = prefs.getString("performer", "");
        return value;
    }

    public void setTopPerformerDesc(String value) {
        prefs.edit().putString("performerDesc", value).apply();

    }

    public String getTopPerformerDesc() {

        String value = prefs.getString("performerDesc", "");
        return value;
    }

    //AMC
    public void setAmc(String value) {
        prefs.edit().putString("amc", value).apply();

    }

    public String getAmc() {

        String value = prefs.getString("amc", "");
        return value;
    }

    //Goal
    public void setGoal(String value) {
        prefs.edit().putString("goal", value).apply();

    }

    public String getGoal() {

        String value = prefs.getString("goal", "");
        return value;
    }
  public void setGoalDesc(String value) {
        prefs.edit().putString("goalDesc", value).apply();

    }

    public String getGoalDesc() {

        String value = prefs.getString("goalDesc", "");
        return value;
    }

    public void setAmcDesc(String value) {
        prefs.edit().putString("amcDesc", value).apply();

    }

    public String getAmcDesc() {

        String value = prefs.getString("amcDesc", "");
        return value;
    }

    public void set_my_journey(String value) {
        prefs.edit().putString("my_journey", value).apply();

    }

    public String get_my_journey() {

        String value = prefs.getString("my_journey", "");
        return value;
    }

    public void setServiceTitle(String value) {
        prefs.edit().putString("service_title", value).apply();

    }

    public String getServiceTitle() {

        String value = prefs.getString("service_title", "");
        return value;
    }

    public void setViewType(String viewType) {
        prefs.edit().putString("viewType", viewType).apply();

    }

    public String getViewType() {

        String value = prefs.getString("viewType", "");
        return value;
    }

    public String getDeviceToken() {
        String devicetokn = prefs.getString("devicetokn", "");
        return devicetokn;
    }

    public void setSocialMediaLink(String socialMediaLink) {
        prefs.edit().putString("social_media", socialMediaLink).apply();
    }

    public String getSocialMediaLink() {
        String social_media = prefs.getString("social_media", "");
        return social_media;
    }

    public void setImageRawData(String imageRawData) {
        prefs.edit().putString("imageRawData", imageRawData).apply();
    }

    public void setSectorAnanlysisCount(int count) {
        prefs.edit().putInt("countSector", count).apply();

    }

    public int getSectorAnalysis() {
        int countSector = prefs.getInt("countSector", 0);
        return countSector;
    }

    public void setTopHoldingCount(int count) {
        prefs.edit().putInt("countHolding", count).apply();

    }

    public int getTopHolding() {
        int countHolding = prefs.getInt("countHolding", 0);
        return countHolding;
    }


    public void setTopHoldingClicked(String topHolding) {
        prefs.edit().putString("topHolding", topHolding).apply();
    }

    public String getTopHoldingClicked() {
        String topHolding = prefs.getString("topHolding", "");
        return topHolding;
    }

    public void setNotifyIcon(String notifyIcon) {
        prefs.edit().putString("notifyIcon", notifyIcon).apply();
    }

    public String getNotifyIcon() {
        String notifyIcon = prefs.getString("notifyIcon", "");
        return notifyIcon;
    }

    public String getImageRawData() {
        String imageRawData = prefs.getString("imageRawData", "");
        return imageRawData;
    }

    public void setClientImage(String clientimage) {
        prefs.edit().putString("clientimage", clientimage).apply();
    }

    public String getClientImage() {
        String clientimage = prefs.getString("clientimage", "");
        return clientimage;
    }

    public void setDefaultAppLang(String defaultAppLang) {
        prefs.edit().putString("defaultLang", defaultAppLang).apply();
    }

    public String getDefaultAppLang() {
        String defaultAppLang = prefs.getString("defaultLang", "");
        return defaultAppLang;
    }

    public void setSelectedAppLang(String selectedAppLang) {
        prefs.edit().putString("selectedLang", selectedAppLang).apply();
    }

    public String getSelectedAppLang() {
        String selectedAppLang = prefs.getString("selectedLang", "");
        return selectedAppLang;
    }

    public void setPattern(String pattern) {
        prefs.edit().putString("pattern", pattern).apply();
    }

    public String getPattern() {
        String pattern = prefs.getString("pattern", "");
        return pattern;
    }

    public void setPIN(String pin) {
        prefs.edit().putString("pin", pin).apply();
    }

    public void setSavedLangPos(int i) {
        prefs.edit().putInt("langPos", i).apply();
    }

    public int getSavedLangPos() {
        int i = prefs.getInt("langPos", 0);
        return i;
    }

    public String getPIN() {
        String pin = prefs.getString("pin", "");
        return pin;
    }

    public boolean getHasFirstTimeDashBoard() {
        boolean firstTimedashboard = prefs.getBoolean("firstTimeCompleteddashboard", false);
        return firstTimedashboard;
    }

    public void setHasFirstTimeDashBoard(boolean firstTimedashboard) {
        prefs.edit().putBoolean("firstTimeCompleteddashboard", firstTimedashboard).apply();
    }

    public boolean getHasFirstTimeOTP() {
        boolean firstOtp = prefs.getBoolean("firstOtp", false);
        return firstOtp;
    }

    public void setHasFirstTimeOTP(boolean firstOtp) {
        prefs.edit().putBoolean("firstOtp", firstOtp).apply();
    }

    public boolean getCLicked() {
        boolean cLicked = prefs.getBoolean("clicked", false);
        return cLicked;
    }

    public void setCLicked(boolean cLicked) {
        prefs.edit().putBoolean("clicked", cLicked).apply();
    }

    public String getAddToCartList() {
        String addToCartList = prefs.getString("addToCartList", "");
        return addToCartList;
    }

    public void setAddToCartList(String addToCartList) {
        prefs.edit().putString("addToCartList", addToCartList).apply();
    }


    public String getAddToNFOCartList() {
        String addToNFOCartList = prefs.getString("addToNFOCartList", "");
        return addToNFOCartList;
    }

    public void setAddToNFOCartList(String addToNFOCartList) {
        prefs.edit().putString("addToNFOCartList", addToNFOCartList).apply();
    }

    public boolean getHasSocialLogin() {
        boolean hasSocialLogin = prefs.getBoolean("hasSocialLogin", false);
        return hasSocialLogin;
    }

    public void setHasSocialLogin(boolean hasSocialLogin) {
        prefs.edit().putBoolean("hasSocialLogin", hasSocialLogin).apply();
    }


    public String getSave() {
        String save = prefs.getString("save", "");
        return save;
    }

    public void setSave(String save) {
        prefs.edit().putString("save", save).apply();
    }


    public boolean getHasNotificationEnable() {
        boolean hasNotificationEnable = prefs.getBoolean("hasNotificationEnable", false);
        return hasNotificationEnable;
    }

    public void setHasNotificationEnable(boolean hasNotificationEnable) {
        prefs.edit().putBoolean("hasNotificationEnable", hasNotificationEnable).apply();
    }


    public String getPassKey() {
        String passKey = prefs.getString("passKey", "");
        return passKey;
    }

    public void setPassKey(String passKey) {
        prefs.edit().putString("passKey", passKey).apply();
    }

    public String getNotification() {
        String Notification = prefs.getString("Notification", "");
        return Notification;
    }

    public void setNotification(String Notification) {
        prefs.edit().putString("Notification", Notification).apply();
    }

/*

    public boolean getHasUserSetFirst() {
        return prefs.getBoolean("hasUserSetFirstTime", false);
    }

    public void setHasUserSetFirst(boolean hasUserSetFirstTime) {
        prefs.edit().putBoolean("hasUserSetFirstTime", hasUserSetFirstTime).apply();
    }
*/


    public boolean getHasAppLockEnable() {
        return prefs.getBoolean("hasAppLockEnable", false);
    }

    public void setHasAppLockEnable(boolean hasAppLockEnable) {
        prefs.edit().putBoolean("hasAppLockEnable", hasAppLockEnable).apply();
    }

    public String getAppLockType() {
        String appLockType = prefs.getString("appLockType", "");
        return appLockType;
    }

    public void setAppLockType(String appLockType) {
        prefs.edit().putString("appLockType", appLockType).apply();
    }


    public String getPAN() {
        String pan = prefs.getString("pan", "");
        return pan;
    }

    public void setPAN(String pan) {
        prefs.edit().putString("pan", pan).apply();
    }

    public String getUCC_CODE() {
        String uccCode = prefs.getString("uccCode", "");
        return uccCode;
    }

    public void setUCC_CODE(String uccCode) {
        prefs.edit().putString("uccCode", uccCode).apply();
    }

    public String getCID() {
        String code = prefs.getString("code", "");
        return code;
    }

    public void setCID(String code) {
        prefs.edit().putString("code", code).apply();
    }

    public String getSecondryCID() {
        String secondryCID = prefs.getString("secondryCID", "");
        return secondryCID;
    }

    public void setSecondryCID(String secondryCID) {
        prefs.edit().putString("secondryCID", secondryCID).apply();
    }


    public String getEmail() {
        String username = prefs.getString("email", "");
        return username;
    }

    public void setEmail(String email) {
        prefs.edit().putString("email", email).apply();
    }


    public String getMarketValue() {
        String marketValue = prefs.getString("marketValue", "");
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        prefs.edit().putString("marketValue", marketValue).apply();
    }


    public String getDOB() {
        String dob = prefs.getString("dob", "");
        return dob;
    }

    public void setDOB(String dob) {
        prefs.edit().putString("dob", dob).apply();
    }


    public String gettaxStatus() {
        String status = prefs.getString("taxstatus", "");
        return status;
    }

    public void settaxStatus(String status) {
        prefs.edit().putString("taxstatus", status).apply();
    }


    public String getOccupation() {
        String occupation = prefs.getString("occupation", "");
        return occupation;
    }

    public void setOccupation(String occupation) {
        prefs.edit().putString("occupation", occupation).apply();
    }


    public String getTransactionPermission() {
        String TransactionPermission = prefs.getString("transactionPermission", "");
        return TransactionPermission;
    }

    public void setTransactionPermission(String setTransactionPermission) {
        prefs.edit().putString("transactionPermission", setTransactionPermission).apply();
    }

    public boolean getHasFatca() {
        boolean hasFatca = prefs.getBoolean("hasFatca", false);
        return hasFatca;
    }

    public void setHasFatca(boolean hasFatca) {
        prefs.edit().putBoolean("hasFatca", hasFatca).apply();
    }

    public boolean getHasCAFStatus() {
        boolean hasCAFStatus = prefs.getBoolean("hasCAFStatus", false);
        return hasCAFStatus;
    }

    public void setHasCAFStatus(boolean hasCAFStatus) {
        prefs.edit().putBoolean("hasCAFStatus", hasCAFStatus).apply();
    }

    public boolean getHasSignature() {
        boolean hasSignature = prefs.getBoolean("hasSignature", false);
        return hasSignature;
    }

    public void setHasSignature(boolean hasSignature) {
        prefs.edit().putBoolean("hasSignature", hasSignature).apply();
    }

    public boolean getPANKYC() {
        boolean PANKYC = prefs.getBoolean("PANKYC", false);
        return PANKYC;
    }

    public void setPANKYC(boolean PANKYC) {
        prefs.edit().putBoolean("PANKYC", PANKYC).apply();
    }

    public boolean getHasMendate() {
        boolean hasMendate = prefs.getBoolean("hasMendate", false);
        return hasMendate;
    }

    public void setHasMendate(boolean hasMendate) {
        prefs.edit().putBoolean("hasMendate", hasMendate).apply();
    }


    ///////////////////////////////////////////////////////////

    public String getFcmToken() {
        String devicetokn = prefs.getString("device_token", "");
        return devicetokn;
    }

    public void setFcmToken(String devicetokn) {
        prefs.edit().putString("device_token", devicetokn).apply();
    }

    public String getUserType() {
        String userType = prefs.getString("userType", "");
        return userType;
    }

    public void setUserType(String userType) {
        prefs.edit().putString("userType", userType).apply();
    }


    public String getLoginType() {
        String loginType = prefs.getString("loginType", "");
        return loginType;
    }

    public void setLoginType(String loginType) {
        prefs.edit().putString("loginType", loginType).apply();
    }

    public boolean getHasFirstTimeCompleted() {
        boolean firstTimeCompleted = prefs.getBoolean("firstTimeCompleted", false);
        return firstTimeCompleted;
    }

    public void setHasFirstTimeCompleted(boolean firstTimeCompleted) {
        prefs.edit().putBoolean("firstTimeCompleted", firstTimeCompleted).apply();
    }

    public boolean getHasFirstTimeAppIntroLaunched() {
        boolean firstTimeAppIntroLaunched = prefs.getBoolean("firstTimeAppIntroLaunched", false);
        return firstTimeAppIntroLaunched;
    }

    public void setHasFirstTimeAppIntroLaunched(boolean firstTimeAppIntroLaunched) {
        prefs.edit().putBoolean("firstTimeAppIntroLaunched", firstTimeAppIntroLaunched).apply();
    }

    public String getMobileNumber() {
        String mibile = prefs.getString("mobile", "");
        return mibile;
    }

    public void setMobileNumber(String mibile) {
        prefs.edit().putString("mobile", mibile).apply();
    }

    public String getBrokerFullName() {
        String brokerFullName = prefs.getString("brokerFullName", "");
        return brokerFullName;
    }

    public void setBrokerFullName(String brokerFullName) {
        prefs.edit().putString("brokerFullName", brokerFullName).apply();
    }

    public String getFullName() {
        String fullName = prefs.getString("fullName", "");
        return fullName;
    }

    public void setFullName(String fullName) {
        prefs.edit().putString("fullName", fullName).apply();
    }

    public void clear() {
        prefs.edit().clear().apply();

    }

    public boolean getHasLoging() {
        boolean hasLoging = prefs.getBoolean("hasLoging", false);
        return hasLoging;
    }

    public void setHasLoging(boolean value) {
        prefs.edit().putBoolean("hasLoging", value).apply();
    }

    public void set_current_time(Long time) {
        prefs.edit().putLong("current_time", time).apply();
    }

    public Long get_current_time() {
        Long time = prefs.getLong("current_time", 0);
        return time;
    }

    public String getFlavour() {
        String flavour = prefs.getString("flavour", "");
        return flavour;
    }

    public void setFlavour(String flavour) {
        prefs.edit().putString("flavour", flavour).apply();
    }


    public void set_fundpick(String value) {
        prefs.edit().putString("Fund_Picks", value).apply();
    }

    public String get_fundpick() {
        String value = prefs.getString("Fund_Picks", "");
        return value;
    }

    public void setRiskCode(String value) {
        prefs.edit().putString("my_risk_code", value).apply();
    }

    public String getSchemeData() {
        String schemeData = prefs.getString("schemeData", "");
        return schemeData;
    }

    public void setGetSchemeData(String schemeData) {
        prefs.edit().putString("schemeData", schemeData).apply();
    }

    public void setGoalData(String documentData) {
        prefs.edit().putString("setgoaldata", documentData).apply();
    }

    public String getGoalData() {
        String goalData = prefs.getString("setgoaldata", " ");
        return goalData;
    }

    public String getRiskCode() {
        String value = prefs.getString("my_risk_code", "");
        return value;
    }

    public void setDocumentData(String documentData) {
        prefs.edit().putString("documentData", documentData).apply();
    }

    public String getDocumentData() {
        String documentData = prefs.getString("documentData", "");
        return documentData;
    }

    public void setRiskName(String value) {
        prefs.edit().putString("my_risk_profile", value).apply();
    }

    public String getRiskName() {
        String value = prefs.getString("my_risk_profile", "");
        return value;
    }

    public void setRiskDescription(String riskDescription) {
        prefs.edit().putString("riskDescription", riskDescription).apply();
    }

    public String getRiskDescription() {
        String riskDescription = prefs.getString("riskDescription", "");
        return riskDescription;
    }


    public void setRiskImage(String riskImage) {
        prefs.edit().putString("riskImage", riskImage).apply();
    }

    public String getRiskImage() {
        String riskImage = prefs.getString("riskImage", "");
        return riskImage;
    }


    public void setBid(String value) {
        prefs.edit().putString("bid", value).apply();

    }

    public String getBid() {

        String value = prefs.getString("bid", "");
        return value;
    }

    public void setUserName(String value) {
        prefs.edit().putString("userName", value).apply();

    }

    public String getUserName() {

        String value = prefs.getString("userName", "");
        return value;
    }

    public void setPassword(String value) {
        prefs.edit().putString("password", value).apply();

    }

    public String getPassword() {

        String value = prefs.getString("password", "");
        return value;
    }


    public void setUname(String uname) {
        prefs.edit().putString("uname", uname).apply();
    }

    public String getUname() {
        String uname = prefs.getString("uname", "");
        return uname;
    }

    public void setUPassword(String uPassword) {
        prefs.edit().putString("uPassword", uPassword).apply();
    }

    public String getUPassword() {
        String uPassword = prefs.getString("uPassword", "");
        return uPassword;
    }


    public void setRMmobile(String RMmobil) {
        prefs.edit().putString("RMmobil", RMmobil).apply();
    }

    public void setRM(String rm) {
        prefs.edit().putString("RM", rm).apply();
    }

    public String getRM() {
        String rm = prefs.getString("RM", "");
        return rm;
    }

    public String getRMmobil() {
        String RMmobil = prefs.getString("RMmobil", "");
        return RMmobil;
    }


    public void setRMemail(String RMemail) {
        prefs.edit().putString("RMemail", RMemail).apply();
    }

    public String getRMemail() {
        String RMemail = prefs.getString("RMemail", "");
        return RMemail;
    }

    public void setSchemeCategory(String schemeCat) {
        prefs.edit().putString("SchemeCategory", schemeCat).apply();
    }

    public String getSchemeCategory() {
        String schemeCat = prefs.getString("SchemeCategory", "D");
        return schemeCat;
    }

    public void setAppLayoutType(String appLayoutType) {
        prefs.edit().putString("AppLayoutType", appLayoutType).apply();
    }

    public String getAppLayoutType() {
        String appLayoutType = prefs.getString("AppLayoutType", "");
        return appLayoutType;
    }


    public boolean getShowDialog() {
        boolean showdialog = prefs.getBoolean("showdialog", true);
        return showdialog;
    }

    public void setShowDialog(boolean showdialog) {
        prefs.edit().putBoolean("showdialog", showdialog).apply();
    }

    /**************************App Config*************************/
    public void setAppConfig(String appConfig) {
        prefs.edit().putString("appConfig", appConfig).apply();
    }

    public String getAppConfig() {
        String appConfig = prefs.getString("appConfig", "");
        return appConfig;
    }



    //THE END//
}
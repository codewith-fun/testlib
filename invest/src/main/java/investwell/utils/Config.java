package investwell.utils;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    public static int investCount = 0;

    public static HashMap<String, String> mGraphValue = new HashMap<>();

    public static boolean mIsPANVerifyed = false;

    //public static String COMMON_PATH = "https://nativeapi.my-portfolio.in/";


    ///
    public static String COMMON_URL = "https://nativeapi.my-portfolio.in";

    public static  String BANK_DETAIL = COMMON_URL+"/Investwell.svc/IFSCBankDetailsV4";


    public static String Summary_Details = COMMON_URL + "/Investwell.svc/FFSummaryDetailsV4";

    public static String Performance_Analysis = COMMON_URL + "/Investwell.svc/FFPerformanceAnalysisV4";

    public static String Sector_Analysis = COMMON_URL + "/Investwell.svc/FFSectorAnalysisV4";

    public static String Top_Holdings = COMMON_URL + "/Investwell.svc/FFTopHoldingsV4";

    public static String FATCA_COUNTYR_LIST = COMMON_URL + "/BSEStarMF.svc/BSEFATCACountryListV4";

    public static String Video_KYC = COMMON_URL + "/Investwell.svc/VideoKYCV4";

    public static String GET_NAV = COMMON_URL + "/Investwell.svc/LatestNAVV4";

    public static String GOAL_Category = COMMON_URL + "/BSEStarMF.svc/GoalCategoryV4";

    public static String GOAL_Create = COMMON_URL + "/BSEStarMF.svc/ClientGoalCreateV4";

    public static String GOAL_SUMMARY = COMMON_URL + "/BSEStarMF.svc/GoalDetailsV4";
    public static String GOAL_SUMMARY_ONE = "https://nativeapi.my-portfolio.in/Portfolio.svc/GoalSummaryV4";
    public static String GOAL_DELETE = COMMON_URL + "/BSEStarMF.svc/GoalDeleteV4";

    public static String GOAL_FOLIO_LIST = COMMON_URL + "/BSEStarMF.svc/FolioforGoalV4";

    public static String GOAL_MAPPING_V4_2 = COMMON_URL + "/BSEStarMF.svc/FolioforGoalV4_2";

    public static String GOAL_FOLIO_Maping = COMMON_URL + "/BSEStarMF.svc/GoalFolioMappingV4";
    public static String MY_ASSETS_API = COMMON_URL + "/Portfolio.svc/AllAssetAUMV4";
    public static String Cumulative_Performance = COMMON_URL + "/Investwell.svc/FFCumulativePerformanceV4";

    public static String Cheque_Upload = COMMON_URL + "/BSESignatureUpload.svc/NSEChequeUpload";

    public static String SIGNATURE_UPLOAD_1 = COMMON_URL + "/BSESignatureUpload.svc/BSEUploadSignaturePart1";

    public static String SIGNATURE_UPLOAD_2 = COMMON_URL + "/BSESignatureUpload.svc/BSEUploadSignaturePart2";

    public static String Last_NAV_Update = COMMON_URL + "/Investwell.svc/LastNAvUpdateV4";

    public static String Get_Broker_Details = COMMON_URL + "/Investwell.svc/BrokerDetails";


    public static String Dashboard = COMMON_URL + "/Portfolio.svc/dbSnapshotV4";

    public static String PAllocation = COMMON_URL + "/Portfolio.svc/PAllocationV4";

    public static String Portfolio_Return = COMMON_URL + "/Portfolio.svc/PortfolioReturnV4";


    //public static String GOAL_Category = COMMON_PATH + "BSEStarMF.svc/GoalCategoryV4";

    public static String Systematic_investment = COMMON_URL + "/Portfolio.svc/MyRunningSystematicTransactionsV4";

    public static String Systemetic_Transaction = COMMON_URL + "/Portfolio.svc/LastMonthSystematicV4";

    public static String BSE_PROFILE_DETAILS = COMMON_URL + "/BSEStarMF.svc/BSEProfileDetailsV4";

    public static String Profile_Detail = COMMON_URL + "/BSEStarMF.svc/BSEProfileDetailsV4";

    public static String My_Transaction_url = COMMON_URL + "/Portfolio.svc/MyTransactionV4";

    public static String Send_Mail = COMMON_URL + "/Investwell.svc/SendMailV2";

    public static String My_Journey = COMMON_URL + "/Portfolio.svc/MyJourneyV4";

    public static String Watch_List = COMMON_URL + "/Portfolio.svc/WatchListV4";

    public static String Portfolio_Client_Detailed = COMMON_URL + "/Portfolio.svc/MFTransactionV4";

    public static String Broker_Dashboard = COMMON_URL + "/Portfolio.svc/BrokerDashBoardV4";

    public static String Broker_Dashbord_Search = COMMON_URL + "/Portfolio.svc/BrokerDBClientSearchV4";

    public static String Additional_purchase = COMMON_URL + "/BSEStarMF.svc/AdditionalPurchaseV2";

    public static String New_purchase = COMMON_URL + "/BSEStarMF.svc/NewPurchaseV4";

    public static String Bank_detail = COMMON_URL + "/BSEStarMF.svc/NSEClientBankListV4";

    public static String Bank_LIST_India = COMMON_URL + "/BSEStarMF.svc/NSEBankListV4";

    public static String Additional_Sip = COMMON_URL + "/BSEStarMF.svc/SIPTransactionV2";

    public static String Num_Vrify = COMMON_URL + "/Investwell.svc/VerifyMobileV4";

    public static String MAndate_List = COMMON_URL + "/BSEStarMF.svc/BSEMandateListV4";

    //  public static String MAndate_List_NSE = COMMON_URL + "/BSEStarMF.svc/NSEClientMandateListV2/";

    public static String Sip_Dates = COMMON_URL + "/BSEStarMF.svc/TranDatesV4";

    public static String Additional_Switch = COMMON_URL + "/BSEStarMF.svc/SwitchTransactionV2";

    public static String Set_Just_Save = COMMON_URL + "/BSEStarMF.svc/SetJustSaveV4";

    public static String Folio_Outstanding_Transaction = COMMON_URL + "/Portfolio.svc/FolioOutstandingTransactionV4";

    public static String Get_Just_Save = COMMON_URL + "/BSEStarMF.svc/GetJustSaveV4";

    public static String Scheme_List = COMMON_URL + "/BSEStarMF.svc/SchemeListV2";

    public static String Amount_Unit_Detail = COMMON_URL + "/BSEStarMF.svc/AmountUnitDetailV2/";

    public static String Simply_Save = COMMON_URL + "/BSEStarMF.svc/SimplySaveFolioV4";

    public static String Additional_SWP = COMMON_URL + "/BSEStarMF.svc/SWPTransactionV2";

    public static String EMAIL_OTP = COMMON_URL + "/Investwell.svc/EmailOTPVerificationV4";

    public static String Additional_STP = COMMON_URL + "/BSEStarMF.svc/STPTransactionV2";

    public static String Additional_Redeem = COMMON_URL + "/BSEStarMF.svc/RedemptionTransactionV2";

    public static String Dividend_Summary = COMMON_URL + "/Portfolio.svc/DividendSummaryV4";

    public static String LOGIN_WITHOUT_PASSWORD = COMMON_URL + "/Investwell.svc/LoginAuthenticationWithoutPassV4";

    public static String Fixed_Deposit = COMMON_URL+"/Portfolio.svc/FixedDepositInvestmentsV4";

    public static String Dividend_Detail = COMMON_URL + "/Portfolio.svc/DividendDetailsV4";

    public static String My_Insurance = COMMON_URL + "/Portfolio.svc/InsuranceReportV4";

    public static String GENERAL_Insurance = COMMON_URL + "/Portfolio.svc/GeneralInsuranceInvestmentsV4";
    public static String Share_Bond = COMMON_URL+"/Portfolio.svc/EquityInvestmentsV4";
    public static String LIFE_Insurance = COMMON_URL + "/Portfolio.svc/LifeInsuranceInvestmentsV4";

    public static String Tax_Saving = COMMON_URL + "/Portfolio.svc/ELSSReportV4";

    public static String Folio = COMMON_URL + "/Portfolio.svc/FolioQueryV4";

    public static String Folio_Lookup = COMMON_URL + "/Portfolio.svc/FolioLookUpV4";

    public static String Folio_Detail = COMMON_URL + "/Portfolio.svc/FolioDetailsV4";
    public static String FACTSHEET_COMPARISON_MINIFY = COMMON_URL + "/Investwell.svc/FFCumulativePerformanceMinifyV4";

    public static String GET_FUNDPICKS = COMMON_URL + "/Investwell.svc/FundPicksV4";


    public static String GET_BASKET = COMMON_URL + "/BSEStarMF.svc/getOBBasketV4";

    public static String GET_GENERATE = COMMON_URL + "/Investwell.svc/GeneratePasskeyV2";

    public static String RECOMENDED_SCHEMES = COMMON_URL + "/BSEStarMF.svc/getRecommendedSchemeV4";

    public static String GET_DOCUMENTs = COMMON_URL + "/Investwell.svc/DocumentDisplayV4";


    public static String Top_scheme = COMMON_URL + "/Investwell.svc/TopSchemesV4";

    public static String Search_list = COMMON_URL + "/Investwell.svc/SchemeSearchV4";

    public static String SERVICE_REQ = "http://nativeapi.my-portfolio.in/Investwell.svc/PostQueryV4";

    public static String EMAIL_VERIFICATION = COMMON_URL + "/Investwell.svc/emailStatusV4";

    public static String PAN_VERIFICATION = COMMON_URL + "/BSEStarMF.svc/PANKYCCheckV4";

    public static String SAVE_BASIC_DETAILS = COMMON_URL + "/Investwell.svc/ProspectsRegistrationV2";

    public static String BSE_Gateway = COMMON_URL + "/BSEStarMF.svc/GoToBSECartV4";

    public static String BANK_ACCOUNT_TYPE = COMMON_URL + "/BSEStarMF.svc/BSEBankAccountTypeV4";

    public static String COMPLETE_REGISTRATION = COMMON_URL + "/BSEStarMF.svc/BSEUCCCreationV2";

    // public static String GET_COUNTRY = COMMON_URL + "/BSEStarMF.svc/BSEFATCACountryListV2/";

    public static String INSERT_INTO_LUMPSUM = COMMON_URL + "/BSEStarMF.svc/InsertLumpsumCartV2";

    public static String INSERT_INTO_SIP = COMMON_URL + "/BSEStarMF.svc/InsertSIPCartV2";

    public static String GET_LUMSUM_CART = COMMON_URL + "/BSEStarMF.svc/LSCartDisplayV4";

    public static String GET_SIP_CART = COMMON_URL + "/BSEStarMF.svc/SIPCartDisplayV4";

    public static String DELETE_LUMSUM = COMMON_URL + "/BSEStarMF.svc/RemoveSchemeLSCartV3";

    public static String Delete_Pending_Order = COMMON_URL + "/BSEStarMF.svc/DeleteTransactOrderV4";

    public static String Transact_Order_List = COMMON_URL + "/BSEStarMF.svc/TransactOrderListV4";

    public static String LUMSUM_PLACE_ORDER = COMMON_URL + "/BSEStarMF.svc/LSOrderPlaceV2";

    public static String Get_Scheme_Video = COMMON_URL + "/Investwell.svc/VideoListV4";

    private static String RISK_UPDATE_PROFILE = COMMON_URL + "/Investwell.svc/ClientRiskProfileV4";

    public static String UPDATE_RISK_PROFILE = COMMON_URL + "/Investwell.svc/ClientRiskProfileV4";

    public static String SAVE_IMAGE_ONLY = "http://beta-m.investwell.in/mapi/uploadsign";

    public static String PROSPECT_ACCOUNT_STATUS = COMMON_URL + "/BSEStarMF.svc/ProspectAccountStatusv4";

    public static String ORDER_HISTORY = COMMON_URL + "/BSEStarMF.svc/ProspectMyOrderV4";

    public static String FORGOT_PASSSWORD = COMMON_URL + "/Investwell.svc/ForgotPasswordV4";

    public static String LOGIN = COMMON_URL + "/Investwell.svc/LoginAuthenticationV4";

    public static String BANK_DETAILS = COMMON_URL + "/BSEStarMF.svc/BankDetailsV4";

    public static String CREATE_MENDATE = COMMON_URL + "/BSEStarMF.svc/BSEMandateCreationV2";

    public static String DELETE_SIP = COMMON_URL + "/BSEStarMF.svc/RemoveSchemeSIPCartV3";

    public static String PLACE_SIP_ORDER = COMMON_URL + "/BSEStarMF.svc/SIPOrderPlaceV2";

    public static String GET_MENDATE_LIST = COMMON_URL + "/BSEStarMF.svc/BSEMandateListV4";

    public static String GET_MENDATE_WEBVIEW_LIST = COMMON_URL + "/BSEStarMF.svc/AadharMandateLinkV2/";

    public static String GET_SCHMES_LIST = COMMON_URL + "/BSEStarMF.svc/SchemeListV2";

    public static String GET_ALL_AMC = COMMON_URL + "/Investwell.svc/AMCListV4";

    public static String ADD_SCHEMES_SIP = COMMON_URL + "/BSEStarMF.svc/AddSchemeCartV2";

    public static String SIGNATURE_UPLOAD = COMMON_URL + "/BSESignatureUpload.svc/BSEUploadSignature";

    //public static String GET_FOLIO_LIST = COMMON_URL + "/BSEStarMF.svc/ExistingFolioListV2/";
    public static String Occupation_Code = COMMON_URL + "/BSEStarMF.svc/BSEOccupationV4";

    public static String NSE_Fatca = COMMON_URL + "/BSEStarMF.svc/NSEFATCAV4";
    public static String GET_MARKET_DETAILS = COMMON_URL + "/Investwell.svc/MarketIndexV4";

    public static String Category_List = COMMON_URL + "/Investwell.svc/CategoryListV4";

    public static String PROFILE_LIST = COMMON_URL + "/BSEStarMF.svc/ProfileListV4";

    public static String CHANGE_PASSWORD = COMMON_URL + "/Investwell.svc/ChangePasswordV2";

    public static String Folio_LIST = COMMON_URL + "/BSEStarMF.svc/ExistingFolioListV4";

    public static String Exceptional_Switch = COMMON_URL + "/BSEStarMF.svc/SwitchExceptionTransactionV2";

    public static String START_PAYMENT = COMMON_URL + "/BSEStarMF.svc/BSEDirectPaymentV2";

    public static String Member_List = COMMON_URL + "/Portfolio.svc/ListofMembersV4";

    public static String Fund_List = COMMON_URL + "/Portfolio.svc/ListFundInvDoneV4";

    public static String Refrl_Code = COMMON_URL + "/Investwell.svc/SubBrokerCodeV4";

    public static String Fatca_create = COMMON_URL + "/BSEStarMF.svc/BSEFATCAUpdateV2";

    public static String Allocation_Category = COMMON_URL + "/Portfolio.svc/AllocationByCategoryV4";

    public static String Allocation_Applicant = COMMON_URL + "/Portfolio.svc/AllocationByApplicantV4";

    public static String Allocation_Fund = COMMON_URL + "/Portfolio.svc/AllocationByFundV4";

    public static String Allocation_Scheme = COMMON_URL + "/Portfolio.svc/AllocationBySchemeV4";

    public static String Folio_Query = COMMON_URL + "/Portfolio.svc/FolioQueryV4";

    public static String Set_Flavour = COMMON_URL + "/BSEStarMF.svc/SetFlavourMonthV4";

    public static String Get_Flavour = COMMON_URL + "/BSEStarMF.svc/GetFlavourMonthV4";

    public static String NFO_Basket = COMMON_URL + "/BSEStarMF.svc/NFOBasketV4";

    public static String UPLOAD_PROFILE_PIC = COMMON_URL + "/BSESignatureUpload.svc/PhotoUpload";

    public static String APP_CONFIG = COMMON_URL + "/Investwell.svc/APPConfigV4";

    public static String UPLOAD_DOCUMENT = COMMON_URL+"/BSESignatureUpload.svc/UploadFile?";

    public static String PAN_KYC_VERIFY =    COMMON_URL+"/Investwell.svc/PANKYCCheckV2/";
    /******************************TOP SIP SCHEME************************************************/
    public static final String TOP_SIP_SCHEME_API =COMMON_URL+"/Investwell.svc/TopSIPSchemesV4";

    public static String Occupation = COMMON_URL + "/BSEStarMF.svc/BSEOccupationV4";

    public static String STATE = "{\"BSEStateListResult\":\"[{\\\"CODE\\\":\\\"AN\\\",\\\"STATE\\\":\\\"ANDAMAN \\\\u0026 NICOBAR\\\"},{\\\"CODE\\\":\\\"AP\\\",\\\"STATE\\\":\\\"ANDHRA PRADESH\\\"},{\\\"CODE\\\":\\\"AR\\\",\\\"STATE\\\":\\\"ARUNACHAL PRADESH\\\"},{\\\"CODE\\\":\\\"AS\\\",\\\"STATE\\\":\\\"ASSAM\\\"},{\\\"CODE\\\":\\\"BH\\\",\\\"STATE\\\":\\\"BIHAR\\\"},{\\\"CODE\\\":\\\"CH\\\",\\\"STATE\\\":\\\"CHANDIGARH\\\"},{\\\"CODE\\\":\\\"CG\\\",\\\"STATE\\\":\\\"CHHATTISGARH\\\"},{\\\"CODE\\\":\\\"DN\\\",\\\"STATE\\\":\\\"DADRA AND NAGAR HAVELI\\\"},{\\\"CODE\\\":\\\"DD\\\",\\\"STATE\\\":\\\"DAMAN AND DIU\\\"},{\\\"CODE\\\":\\\"DL\\\",\\\"STATE\\\":\\\"DELHI\\\"},{\\\"CODE\\\":\\\"GO\\\",\\\"STATE\\\":\\\"GOA\\\"},{\\\"CODE\\\":\\\"GU\\\",\\\"STATE\\\":\\\"GUJARAT\\\"},{\\\"CODE\\\":\\\"HA\\\",\\\"STATE\\\":\\\"HARYANA\\\"},{\\\"CODE\\\":\\\"HP\\\",\\\"STATE\\\":\\\"HIMACHAL PRADESH\\\"},{\\\"CODE\\\":\\\"JM\\\",\\\"STATE\\\":\\\"JAMMU \\\\u0026 KASHMIR\\\"},{\\\"CODE\\\":\\\"JK\\\",\\\"STATE\\\":\\\"JHARKHAND\\\"},{\\\"CODE\\\":\\\"KA\\\",\\\"STATE\\\":\\\"KARNATAKA\\\"},{\\\"CODE\\\":\\\"KE\\\",\\\"STATE\\\":\\\"KERALA\\\"},{\\\"CODE\\\":\\\"MP\\\",\\\"STATE\\\":\\\"MADHYA PRADESH\\\"},{\\\"CODE\\\":\\\"MA\\\",\\\"STATE\\\":\\\"MAHARASHTRA\\\"},{\\\"CODE\\\":\\\"MN\\\",\\\"STATE\\\":\\\"MANIPUR\\\"},{\\\"CODE\\\":\\\"ME\\\",\\\"STATE\\\":\\\"MEGHALAYA\\\"},{\\\"CODE\\\":\\\"MI\\\",\\\"STATE\\\":\\\"MIZORAM\\\"},{\\\"CODE\\\":\\\"NA\\\",\\\"STATE\\\":\\\"NAGALAND\\\"},{\\\"CODE\\\":\\\"ND\\\",\\\"STATE\\\":\\\"NEW DELHI\\\"},{\\\"CODE\\\":\\\"OR\\\",\\\"STATE\\\":\\\"ORISSA\\\"},{\\\"CODE\\\":\\\"PO\\\",\\\"STATE\\\":\\\"PONDICHERRY\\\"},{\\\"CODE\\\":\\\"PU\\\",\\\"STATE\\\":\\\"PUNJAB\\\"},{\\\"CODE\\\":\\\"RA\\\",\\\"STATE\\\":\\\"RAJASTHAN\\\"},{\\\"CODE\\\":\\\"SI\\\",\\\"STATE\\\":\\\"SIKKIM\\\"},{\\\"CODE\\\":\\\"TN\\\",\\\"STATE\\\":\\\"TAMIL NADU\\\"},{\\\"CODE\\\":\\\"TG\\\",\\\"STATE\\\":\\\"TELENGANA\\\"},{\\\"CODE\\\":\\\"TR\\\",\\\"STATE\\\":\\\"TRIPURA\\\"},{\\\"CODE\\\":\\\"UP\\\",\\\"STATE\\\":\\\"UTTAR PRADESH\\\"},{\\\"CODE\\\":\\\"UC\\\",\\\"STATE\\\":\\\"UTTARANCHAL\\\"},{\\\"CODE\\\":\\\"WB\\\",\\\"STATE\\\":\\\"WEST BENGAL\\\"}]\"}";

    public static String Country = COMMON_URL + "/BSEStarMF.svc/BSECountryListV4";

    public static String Download_Insurance = COMMON_URL+"/Portfolio.svc/InsuranceDocDisplayV4";

    public static String My_Fund_Picks = COMMON_URL+"/Portfolio.svc/MyFundPickV4";

    public static String My_Order_Report = COMMON_URL+"/BSEStarMF.svc/NSEBSEOrderV4";

    public static String IIN_Check = COMMON_URL+"/BSEStarMF.svc/IINActivationCheckV4";

    public static String ENach = COMMON_URL+"/BSEStarMF.svc/ENACHMandateV4";

    public static String Live_My_Orders_SIP = COMMON_URL+"/BSEStarMF.svc/LiveSystematicOrdersNSEBSEV4";

    public static String Live_My_Orders_All = COMMON_URL+"/BSEStarMF.svc/LiveTransactionOrdersNSEBSEV4";

    public static String Systematic_Reports = COMMON_URL+"/BSEStarMF.svc/SystematicReportsV4";

    public static String Cease_SIP = COMMON_URL+"/BSEStarMF.svc/CEASESIPV4";

    //--------------------RISK PROFILE API'S------------------------------//

    public static String Risk_Profile = COMMON_URL + "/BSEStarMF.svc/RiskListV4";

    public static String Question = COMMON_URL + "/BSEStarMF.svc/RiskQuestionnaireV4";

    public static String Risk_Result = COMMON_URL + "/BSEStarMF.svc/RiskResultV4";

    public static String Goal_Based_Scheme_Recommendation = COMMON_URL + "/BSEStarMF.svc/GoalBasedRecommendedSchemesV4";

    public static String Goal_Based_Scheme_Allocation = COMMON_URL + "/BSEStarMF.svc/GoalBasedSchemeAllocationV4";

    public static ArrayList<JSONObject> BASKET_SCHEMES_LIST = new ArrayList<>();

    public static JSONObject GoalBaseScheme = new JSONObject();
    //----------------------Video KYC Limit---------------------------------//
    public static String VIDEO_KYC_LIMIT = COMMON_URL + "/Investwell.svc/VideoKYCLinkMailV4";

    public static String PORT_CAT_WISE = COMMON_URL + "/Portfolio.svc/PortfolioDetailCategoryWiseV4";
    //------------------------ Document Viewer ------------------------------------------
    public static String CLIENT_DOC_LIST = COMMON_URL + "/Investwell.svc/ClientDocDisplyV4";

    public static String CLIENT_DOC_DOWNLOAD = COMMON_URL + "/Investwell.svc/ClientDocDownloadV4";

    public static String Mandate_UPLOAD_PART1 = COMMON_URL + "/BSESignatureUpload.svc/MandateUploadPart1";

    public static String Mandate_UPLOAD_PART2 = COMMON_URL + "/BSESignatureUpload.svc/MandateUploadPart2";

    public static String ADD_NSE_BANK = COMMON_URL + "/BSEStarMF.svc/NSEAddBankV4";

    public static String UPLOAD_FILE_NSE_BANK = COMMON_URL + "/BSESignatureUpload.svc/AdditionalBankDocUpload";

    public static String Document_Upload_Detials = COMMON_URL + "/BSEStarMF.svc/ProfileDocumentUploadDetails";
  ///  http://nativeapi.my-portfolio.in/BSEStarMF.svc/ProfileDocumentUploadDetails

}

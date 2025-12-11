/*
 * Copyright (c) 2018. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 29/12/18 11:26 AM
 */

package in.gov.mahapocra.mahavistaarai.sma;

//8470807282
public interface APIServices {

//// *************** For PROD *************
//   String BASE_API = AppEnv.PROD.instance();
//   String SSO = AppEnv.SSO.instance();
//String BASE_URL_FORCE_UPDATE = "https://mahapocra.gov.in/";

   //*************** For UAT *************
    String BASE_API = AppEnv.UAT.instance();
    String SSO = AppEnv.UAT_SSO.instance();
    String BASE_URL_FORCE_UPDATE = "https://mahapocra.gov.in/";

    String SSO_KEY = APIKeys.SSO_PROD.key();

//    String kOAuth ="authService/sso_login"; // new SSO final
    String kOAuth ="authService/sso_login_farmer_app"; // new for SSO-SMA final
    String kOAuthRefreshToken = "authService/refresh-token"; // new refresh token final
    String kTaluka = SSO + "masterService/get-villages-by-taluka-id/";
    String kForgotPass = "authService/forgot-password-send-otp";
    String kResendOtp = "authService/resend-otp";
    String kResetPass = "authService/reset-password";
    String kAppVersion = "appVersionService/build_version";
    String GET_NOTIFICATION_UNREAD_COUNT = "fcmService/get-Unread-notifications-count";

    //String CHECK_USER_ACTIVE_DEACTIVE = "authService/checkActivateDeactivateUser";
    String CHECK_USER_ACTIVE_DEACTIVE = "authService/checkAppVersionLoggedDetails";
    String kFirstAttemptPassChange = "authService/first-login-password-change";
    String userdetails = "authService/userdetail";
    String kUpdateProfile = "authService/update-profile";

    String kUpdateProfilePic = "authService/update-profile-picture";
    String kFacilitatorList = "userService/facilitators";

    String kDistrictMaster = BASE_API + "masterService/districts";
    String kTalukaMasters = BASE_API + "masterService/talukas/";
    String kVillageMasters = BASE_API + "masterService/villages/";
    String kSearchVillageCensusCode = "masterService/get-census-code-by-village-name";
    String kSERVER_TIMESTAMP = BASE_API + "attendanceService/ca-checkout-current-timestamp";
    String kSubDivisionMaster = "facilitatorService/subdivision";
    String kTalukaBySubDivision = "facilitatorService/talukas-by-subdivision-facilitator";

    String BASE_API_CA = BASE_API;//AppEnv.CA_PROD.instance();
    String CA_Village = "activityService/villages";

    String KT_FileUpload = "ktservice/upload-image";
    String KT_FarmerGroupList = "ktservice/farmer-group-list";
    String KT_FarmerDiscussionList = "ktservice/farmer-discussion-list";
    String KT_submitDetails = "ktservice/kt-details-submit";
    String KT_dbtFarmerList = "ktservice/farmer-list";
    String KT_CATEGORY_LIST = "masterService/get-category-list-kt";
    String KT_SUB_CATEGORY_LIST = "masterService/get-subcategory-list-kt";
    String KT_REPORT_CALENDAR = "ktservice/kt-calender-view";
    String KT_FEEDBACK = "ktservice/add-kt-feedback";
    String KT_GET_WORK_DETAILS = "ktservice/get-kt-details";
    String CA_CLUSTER_CODE = "attendanceService/offline-ca-cluster-data";
    String CA_ABSENT_REASON = "attendanceService/absent-reason";

}
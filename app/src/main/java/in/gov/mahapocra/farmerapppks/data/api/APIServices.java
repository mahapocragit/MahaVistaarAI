package in.gov.mahapocra.farmerapppks.data.api;

public interface APIServices {

    String DBT = AppEnv.DBT.instance();           // for production
    String WOTR = AppEnv.WOTR.instance();
    String GIS = AppEnv.GIS.instance();
    String SSO = AppEnv.SSO.instance();
    String TMS = AppEnv.TMS.instance();
    String SSO_KEY = APIKeys.SSO_PROD.key();
    String FFS_BASE_URL = "http://api-ffs.mahapocra.gov.in/v23/";

//    String DBT = AppEnv.UAT_DBT.instance();          // for UAT
//    String WOTR = AppEnv.WOTR.instance();
//    String SSO = AppEnv.UAT_SSO.instance();
//    String TMS = AppEnv.UAT_TMS.instance();
//    String SSO_KEY = APIKeys.SSO_DEV.key();
//    String FFS_BASE_URL = "https://ilab-ffs-api.mahapocra.gov.in/v23/";

    String SS0_Temp = AppEnv.SSO.instance();

    String SDAO_BASE_URL = "http://api-sma.mahapocra.gov.in/v22/";

    //firebase
    String GET_NOTIFICATION_UNREAD_COUNT = SDAO_BASE_URL + "fcmService/get-Unread-notifications-count";
    String USER_NOTIFICATION_LIST = SDAO_BASE_URL + "fcmService/get-user-activities-on-notification";
    String READ_NOTIFICATION_MESSAGE = SDAO_BASE_URL + "fcmService/mark-notification-as-read-overview";
    String USER_News_LIST = "Shareddbtapi/OtherApp/GetNews";

    String kOTPRequest = "Shareddbtapi/OtherApp/SendOTP";

    String kRegistrationRequest = "Shareddbtapi/OtherApp/AddEditRegistration";

    String kGetDistrictData = "Shareddbtapi/OtherApp/GetDistrict";

    String kGetTalukaData = "Shareddbtapi/OtherApp/GetTaluka";
    String kMyProfileVillageDetails = "masterService/village-profile-pdf";

    String kGetVillageData = "Shareddbtapi/OtherApp/GetVillage";

    String kUserLogin = "Shareddbtapi/OtherApp/LoginCheck";
    String kRefreshTokenLogin = "Shareddbtapi/OtherApp/refreshtoken";

    String kDbtActivitiesDetails = "Shareddbtapi/PocraWebsite/GetActivityCategory";

    String kDbtActivitiesGrpDetails = "Shareddbtapi/PocraWebsite/GetActivityDetailsGroupWise";
    String kGetTokenFromWotr = "Api_GoM/login";
    String kGetFertilizerCalculatedDataWotr = "Api_GoM/getNutrient_calculator";

    String kDbtActivitiesGrpDocDetails = "Shareddbtapi/PocraWebsite/ActivityCategoryDetails";

    String kWareHouseDetails = "warehouseService/warehouse-details";

    String kWeatherDetails = "superficialServices/imd-forcast-previous-weather-details";

    String kGetRegistration = "Shareddbtapi/OtherApp/GetRegistration";
    String kGeFarmersSelectedCrop = "farmerService/get-farmers-selected-crop";
    String kdeleteFarmerSelectedCrop = "farmerService/delete-farmers-selected-crop";
    String kGetmarketsList = "masterService/get-markets-by-taluka";

    String CHECK_USER_ACTIVE_DEACTIVE = "authService/checkAppVersionLoggedDetails";

    String kGetmarketsPriceDetails ="masterService/msamb-data";
    String kResetPassword = "Shareddbtapi/OtherApp/ResetPassword";
    String fetchFarmerListForSHC = "webservices//fetch_farmer_shc_list";
    String kGetDistrictList = "masterService/get-all-district";
    String kGetCropCategorywise = "masterService/get-crop-categorywise";

    String kGetTalukaList = "masterService/get-taluka-on-district";
    String kGetVillageList ="masterService/get-village-on-taluka";
    String kSaveFarmerSelectedCrop ="farmerService/save-farmers-selected-crop";
    String kGetMarketAndMarketName ="masterService/get-markets-by-districts";

    String kGetCropList = "farmerService/get-crop-details";

    String kSubmitGrievance = "masterService/reg_grievance";
    String kSubmitSuggetion = "masterService/farmer-app-feedback";
    String kGrievanceCategory ="masterService/grievance_category";
    String kGetMarketData ="masterService/get-markets";

    String kBandhavarPostStory ="masterService/bandhavar-succsess-stories";
    String KSaveFertilizerFormula ="farmerService/save-fertilizer-formula-of-user";
    String kGetFertilizerSavedFormula="farmerService/get-fertilizer-formula-of-user";
    String kBandhavarViewStory ="masterService/bandhavar-succsess-storie/";
    String kCropAdvisory ="farmService/get-all-crops";
    String kCropAdvisoryNew ="farmService/get-all-crops-by-taluka-advisory/";

    String kClimateResilientTechnology ="Shareddbtapi/PocraWebsite/GetCRAGroups";
    String kClimateResilientTechnologyNewUpdates ="Shareddbtapi/PocraWebsite/GetCRAGroupKharif";

    String kSubdivisionUpcommingEvent ="buildup/subdivision-wise-upcomming-event-list";
    String GET_PS_SCHEDULE_DETAIL_URL ="buildup/get-schedule-details";
    String kAutoCropAdvisory ="masterService/get-auto-crop-advisory";
    String kGetPestDiseaseDetails ="farmerService/get-pest-disease-details";
    String kGetCropStages ="farmerService/get-crop-stages";
    String kGetCropStagesAndAdvisory ="farmerService/get-crop-stages-and-advisory";
    String DELETE_FERTILIZER_FROM_SAVED ="farmerService/delete-fertilizer-formula-of-user";
    String kAutoAdvisory ="farmerService/get-auto-advisory";
    String advisory_feedback ="farmerService/advisory-feedback";



        //    String kgetPocraDistrict = "http://api-ffs.mahapocra.gov.in/v23/masterService/get-all-district";
        //    String kgetPocraTaluka = "http://api-ffs.mahapocra.gov.in/v23/masterService/get-taluka-on-district/";
        //    String kgetPocraVillage = "http://api-ffs.mahapocra.gov.in/v23/masterService/get-village-on-taluka/";
}

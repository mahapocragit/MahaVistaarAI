package in.gov.mahapocra.mahavistaarai.data.api;

public interface APIServices {

    String DBT = AppEnvironment.DBT.getBaseUrl();           // for production
    String FARMER = AppEnvironment.FARMER.getBaseUrl();           // for production
    String WOTR = AppEnvironment.WOTR.getBaseUrl();
    String GIS = AppEnvironment.GIS.getBaseUrl();
    String SSO = AppEnvironment.SSO.getBaseUrl();
    String SSO_KEY = in.gov.mahapocra.mahavistaarai.data.api.APIKeys.SSO_PROD;
    String USER_News_LIST = "Shareddbtapi/OtherApp/GetNews";
    String kOTPRequest = "authService/SendOTP";
    String kOTPRegisterRequest = "authService/SendOTPRegistration";
    String kRegistrationRequest = "authService/AddEditRegistration";
    String kUserLogin = "authService/LoginCheck";
    String kRefreshTokenLogin = "authService/refreshtoken";
    String getNearestCHCenters = "chcService/get-nearest-chc-centers";
    String getCodeFromCoordinates = "webservices/fetch_admin_info_for_location";
    String kRevampedDBTSchemes = "FarmerApp/ActivitiesData";
    String kRevampedMahaDBTSchemes = "FarmerApp/MahaDBTSchemesData";
    String kGetTokenFromWotr = "Api_GoM/login";
    String kGetFertilizerCalculatedDataWotr = "Api_GoM/getNutrient_calculator";
    String kWareHouseDetails = "warehouseService/warehouse-details";
    String kWeatherDetails = "imdService/imd-forcast-previous-weather-details";
    String kGetRegistration = "authService/getUserdetails";
    String kGetSOPByList = "sopServices/get_sop_by_crop";
    String kSoilHealthCardDetailsFromSHCNumber = "webservices/fetch_soil_health_card_json";
    String kRequestForChatBot = "jwt-token-url.php";
    String kAuthenticationForNews = "api/auth/login";
    String kEventsForNews = "api/events/";
    String kGeFarmersSelectedCrop = "farmerService/get-farmers-selected-crop";
    String kGetVideosCategories = "videoService/get-video-category-details";
    String kdeleteFarmerSelectedCrop = "farmerService/delete-farmers-selected-crop";
    String kGetmarketsList = "masterService/get-markets-by-taluka";
    String kGetmarketsPriceDetails = "msambService/msamb-data";
    String kResetPassword = "authService/resetPassword";
    String fetchFarmerListForSHC = "webservices//fetch_farmer_shc_list";
    String kGetDistrictList = "masterService/get-all-district";
    String kGetCropCategorywise = "masterService/get-crop-categorywise";
    String kGetTalukaList = "masterService/get-taluka-on-district";
    String kGetVillageList = "masterService/get-village-on-taluka";
    String kSaveFarmerSelectedCrop = "farmerService/save-farmers-selected-crop";
    String kGetMarketAndMarketName = "msambService/get-markets-by-districts";
    String kGetMarketData = "msambService/get-markets";
    String KSaveFertilizerFormula = "farmerService/save-fertilizer-formula-of-user";
    String kGetFertilizerSavedFormula = "farmerService/get-fertilizer-formula-of-user";
    String kClimateResilientTechnology = "craService/get_cra_groups";
    String kGetPestDiseaseDetails = "farmerService/get-pest-disease-details";
    String kGetCropStages = "farmerService/get-crop-stages";
    String kGetCropStagesAndAdvisory = "farmerService/get-crop-stages-and-advisory";
    String DELETE_FERTILIZER_FROM_SAVED = "farmerService/delete-fertilizer-formula-of-user";
    String advisory_feedback = "farmerService/advisory-feedback";
}

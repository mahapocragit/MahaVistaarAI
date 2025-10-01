package `in`.gov.mahapocra.mahavistaarai.data.api

object ApiConstants {
    val SSO_KEY: String = APIKeys.SSO_PROD
    const val clarityKey: String = APIKeys.CLARITY_PROD
    val KEY_LAST_SHOWN_DATE = "last_shown_date"
//    const val clarityKey: String = APIKeys.CLARITY_STAGE
    const val USER_News_LIST: String = "Shareddbtapi/OtherApp/GetNews"
    const val kOTPRequest: String = "authService/SendOTP"
    const val kOTPRegisterRequest: String = "authService/SendOTPRegistration"
    const val kRegistrationRequest: String = "authService/AddEditRegistration"
    const val kUserLogin: String = "authService/LoginCheck"
    const val kRefreshTokenLogin: String = "authService/refreshtoken"
    const val getNearestCHCenters: String = "chcService/get-nearest-chc-centers"
    const val getCodeFromCoordinates: String = "webservices/fetch_admin_info_for_location"
    const val kRevampedDBTSchemes: String = "FarmerApp/ActivitiesData"
    const val kRevampedMahaDBTSchemes: String = "FarmerApp/MahaDBTSchemesData"
    const val kGetDigitalShetishalaSchedule: String =
        "masterServices/get-digital-shetishala-urls"
    const val kGetTokenFromWotr: String = "Api_GoM/login"
    const val kGetFertilizerCalculatedDataWotr: String = "Api_GoM/getNutrient_calculator"
    const val kWareHouseDetails: String = "warehouseService/warehouse-details"
    const val kWeatherDetails: String = "imdService/imd-forcast-previous-weather-details"
    const val kGetRegistration: String = "authService/getUserdetails"
    const val kGetSOPByList: String = "sopServices/get_sop_by_crop"
    const val kSoilHealthCardDetailsFromSHCNumber: String =
        "webservices/fetch_soil_health_card_json"
    const val kRequestForChatBot: String = "jwt-token-url.php"
    const val kAuthenticationForNews: String = "api/auth/login"
    const val kEventsForNews: String = "api/events/"
    const val kGeFarmersSelectedCrop: String = "farmerService/get-farmers-selected-crop"
    const val kGetVideosCategories: String = "videoService/get-video-category-details"
    const val kGetShetishalaVideos: String = "videoService/get-digital-shetishala-details"
    const val kdeleteFarmerSelectedCrop: String = "farmerService/delete-farmers-selected-crop"
    const val kLoginViaFarmerId: String = "authService/farmeridotp"
    const val kGetmarketsPriceDetails: String = "msambService/msamb-data"
    const val kResetPassword: String = "authService/resetPassword"
    const val kCompareOtp: String = "authService/compareOTP"
    const val kCompareOtpReg: String = "authService/compareOTPReg"
    const val kSearchFarmerRequest: String = "AgriStackToPoCRA/SearchFarmerRequest"
    const val kRetrieveFarmerData: String = "AgriStackToPoCRA/FetchFarmerData"
    const val fetchFarmerListForSHC: String = "webservices//fetch_farmer_shc_list"
    const val kGetDistrictList: String = "masterService/get-all-district"
    const val kGetCropCategorywise: String = "masterService/get-crop-categorywise"
    const val kGetTalukaList: String = "masterService/get-taluka-on-district"
    const val kGetVillageList: String = "masterService/get-village-on-taluka"
    const val kSaveFarmerSelectedCrop: String = "farmerService/save-farmers-selected-crop"
    const val kGetMarketAndMarketName: String = "msambService/get-markets-by-districts"
    const val kGetMarketData: String = "msambService/get-markets"
    const val KSaveFertilizerFormula: String = "farmerService/save-fertilizer-formula-of-user"
    const val kGetFertilizerSavedFormula: String =
        "farmerService/get-fertilizer-formula-of-user"
    const val kClimateResilientTechnology: String = "craService/get_cra_groups"
    const val kGetPestDiseaseDetails: String = "farmerService/get-pest-disease-details"
    const val kGetCropStages: String = "farmerService/get-crop-stages"
    const val kGetCropStagesAndAdvisory: String = "farmerService/get-crop-stages-and-advisory"
    const val DELETE_FERTILIZER_FROM_SAVED: String =
        "farmerService/delete-fertilizer-formula-of-user"
    const val GetCropSapAdvisory: String = "farmerService/getCropSapAdvisory"

}

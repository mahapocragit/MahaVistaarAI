package `in`.gov.mahapocra.mahavistaarai.data.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST(ApiConstants.kGeFarmersSelectedCrop)
    suspend fun getFarmersSelectedCrop(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kSaveFarmerSelectedCrop)
    suspend fun kSaveFarmerSelectedCrop(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kdeleteFarmerSelectedCrop)
    suspend fun deleteSelectedCrop(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kLoginViaFarmerId)
    suspend fun farmerLoginBasedOnID(
        @Header("FarmerID") farmerId: String,
        @Body params: RequestBody
    ): JsonObject

    @POST(ApiConstants.kGetCropCategorywise)
    suspend fun getCropCategoryWise(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetTalukaList)
    suspend fun getTalukaList(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kWeatherDetails)
    suspend fun getWeatherDetails(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetRegistration)
    suspend fun getGetRegistration(
        @Header("FAAPRegistrationID") registrationId: Int,
        @Body params: RequestBody
    ): JsonObject

    @POST(ApiConstants.kGetSOPByList)
    suspend fun getSOPByList(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kSoilHealthCardDetailsFromSHCNumber)
    suspend fun getSoilHealthCardDetailsFromSHCNumber(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kRequestForChatBot)
    suspend fun requestForChatBotURL(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kAuthenticationForNews)
    suspend fun authenticationForNews(@Body params: RequestBody): JsonObject

    @GET("api/category")
    suspend fun fetchNewsCategories(@Header("Authorization") bearerToken: String): JsonObject

    @GET("api/category/sub_category")
    suspend fun fetchNewsSubCategories(
        @Header("Authorization") bearerToken: String,
        @Query("category") category: String
    ): JsonObject

    @POST(ApiConstants.getNearestCHCenters)
    suspend fun getCHCInformation(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.getCodeFromCoordinates)
    suspend fun getCodeFromCoordinates(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetMarketData)
    suspend fun getMarketList(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kCompareOtp)
    suspend fun compareOtp(
        @Header("MobileNo") mobileNo: String,
        @Body params: RequestBody
    ): JsonObject

    @POST(ApiConstants.kCompareOtpReg)
    suspend fun compareOtpReg(
        @Header("MobileNo") mobileNo: String,
        @Body params: RequestBody
    ): JsonObject

    @GET(ApiConstants.kRevampedDBTSchemes)
    suspend fun getDBTSchemes(): JsonObject

    @GET(ApiConstants.kRevampedMahaDBTSchemes)
    suspend fun getMahaDBTSchemes(): JsonObject

    @POST(ApiConstants.kSearchFarmerRequest)
    suspend fun retrieveFarmerToken(@Body params: RequestBody): JsonObject

    @GET(ApiConstants.kGetDigitalShetishalaSchedule)
    suspend fun getDigitalShetishalaSchedule(): JsonObject

    @GET(ApiConstants.kEventsForNews)
    suspend fun eventsForNews(
        @Header("Authorization") authHeader: String,
        @Query("state") state: String,
        @Query("offset") offset: Int,
        @Query("from_date") dateSevenDaysAgo: String,
        @Query("to_date") currentDateTime: String,
        @Query("is_rejected") isRejected: Boolean,
        @Query("category") category: String,
        @Query("sub_category") subCategory: String
    ): JsonObject

    @GET(ApiConstants.kGetVideosCategories)
    suspend fun getFarmersVideosJson(): JsonObject

    @GET(ApiConstants.kGetShetishalaVideos)
    suspend fun getShetishalaVideos(): JsonObject

    @POST(ApiConstants.kRetrieveFarmerData)
    suspend fun retrieveFarmerData(requestBody: RequestBody): JsonObject

    @POST(ApiConstants.kClimateResilientTechnology)
    suspend fun getClimateResilientList(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetCropStagesAndAdvisory)
    suspend fun getCropStagesAndAdvisory(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetCropStages)
    suspend fun getCropStages(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetPestDiseaseDetails)
    suspend fun getPestDiseaseDetails(@Body params: RequestBody): ResponseBody

    @POST(ApiConstants.GetCropSapAdvisory)
    suspend fun getCropSapAdvisory(@Body params: RequestBody): JsonObject

    @POST("notificationServices/fetch-notifications")
    suspend fun getNotificationList(@Header("userid") userId: Int): JsonObject

    @POST("notificationServices/fetch-notifications-indetail")
    suspend fun getNotificationDetails(@Body params: RequestBody): JsonObject

    @POST("notificationServices/update-notification-read-status")
    suspend fun updateNotificationStatus(@Body params: RequestBody): JsonObject

    @POST("authService/updateFcmToken")
    suspend fun updateFCMToken(
        @Header("userid") farmerId: Int,
        @Header("fcmtoken") fcmToken: String
    ): JsonObject

    @POST("authService/checkFcmToken")
    suspend fun checkFcmToken(@Header("userid") farmerId: Int): JsonObject

    @POST(ApiConstants.kOTPRequest)
    fun getOTPRequest(
        @Header("MobileNo") mobileNo: String,
        @Body params: RequestBody
    ): Call<JsonObject>

    @POST(ApiConstants.kOTPRegisterRequest)
    fun getOTPRegisterRequest(
        @Header("MobileNo") mobileNo: String,
        @Body params: RequestBody
    ): Call<JsonObject>

    @POST(ApiConstants.kRegistrationRequest)
    fun getRegistrationRequest(
        @Header("MobileNo") mobileNo: String,
        @Header("NewMobileNo") updatedMobileNo: String,
        @Body params: RequestBody
    ): Call<JsonObject>

    @POST(ApiConstants.kUserLogin)
    fun getUserLoginOTP(
        @Header("MobileNo") mobileNo: String,
        @Header("otp") enteredOTP: String,
        @Body params: RequestBody
    ): Call<JsonObject>

    @POST(ApiConstants.kUserLogin)
    fun getUserLoginPassword(
        @Header("MobileNo") mobileNo: String,
        @Header("Password") password: String,
        @Body params: RequestBody
    ): Call<JsonObject>

    @POST(ApiConstants.kRefreshTokenLogin)
    fun getRefreshTokenLoginViaOTP(
        @Header("MobileNo") mobileNo: String,
        @Header("otp") enteredOTP: String,
        @Header("fcmToken") firebaseToken: String,
        @Body params: RequestBody?
    ): Call<JsonObject>

    @POST(ApiConstants.kRefreshTokenLogin)
    fun getRefreshTokenLoginViaPassword(
        @Header("MobileNo") mobileNo: String,
        @Header("Password") password: String,
        @Header("fcmToken") firebaseToken: String,
        @Body params: RequestBody
    ): Call<JsonObject>

    @POST(ApiConstants.kWareHouseDetails)
    suspend fun getWareHouseDetails(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetDistrictList)
    suspend fun getDistrictList(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.kGetMarketAndMarketName)
    fun getMarketAndMarketName(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetVillageList)
    fun kGetVillageList(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.USER_News_LIST)
    fun getNewsList(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetmarketsPriceDetails)
    suspend fun getMarketPriceDetails(@Body params: RequestBody): JsonObject

    @POST(ApiConstants.DELETE_FERTILIZER_FROM_SAVED)
    fun deleteFertilizerFromSavedList(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.KSaveFertilizerFormula)
    fun saveFertilizerFormula(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetFertilizerSavedFormula)
    fun getFertilizerSavedFormula(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kResetPassword)
    fun getNewPassword(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetTokenFromWotr)
    fun getTokenFromWotr(
        @Query("MobileNo") securityKey: String,
        @Query("Password") dataRequired: String
    ): Call<JsonObject>

    @POST(ApiConstants.kGetFertilizerCalculatedDataWotr)
    fun getFertilizerCalculatedData(
        @Query("CropID") cropID: String?,
        @Query("SowingDate") sowingDate: String,
        @Query("IsNPK") isNPK: String,
        @Query("SoilTestN") soilTestN: String,
        @Query("SoilTestP") soilTestP: String,
        @Query("SoilTestK") soilTestK: String,
        @Query("VillageCode") villageCode: String,
        @Query("FYM") requiredFYM: String,
        @Query("TargetYield") targetYield: String,
        @Query("PlotSize") plotSize: String,
        @Query("PlotUnit") plotUnit: String,
        @Query("Token") token: String
    ): Call<JsonObject>

    @POST(ApiConstants.fetchFarmerListForSHC)
    fun fetchFarmerListForSHC(@Body params: RequestBody): Call<JsonObject>

    //Expert Corner
    @Multipart
    @POST("expertscornerServices/upload-article")
    suspend fun uploadArticle(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part("subcategory") subcategory: RequestBody,
        @Part("user") user: RequestBody
    ): JsonObject

    @GET("expertscornerServices/get-categories")
    suspend fun getCategoriesForExpertCorner(): JsonObject

    @POST("expertscornerServices/get-subcategories")
    suspend fun getSubCategoriesForExpertCorner(@Body params: RequestBody): JsonObject

    @POST("expertscornerServices/get-users-articles")
    suspend fun getUserArticles(@Body params: RequestBody): JsonObject


    @POST("expertscornerServices/articles-list")
    suspend fun getAllArticles(@Body params: RequestBody): JsonObject

    @POST("shcServices/fetch_soil_health_card_single")
    suspend fun fetchSoilHealthCard(@Body params: RequestBody): JsonObject

    @GET("/costofcultivationServices/get-expense-categories")
    suspend fun fetchExpenseCategories(): JsonObject

    @POST("/costofcultivationServices/add-crop")
    suspend fun addCropForCropCalculation(@Body params: RequestBody): JsonObject


    @POST("/costofcultivationServices/get-crops-profits")
    suspend fun getTotalCostTransactions(@Body params: RequestBody): JsonObject

    @POST("/costofcultivationServices/get-transactions")
    suspend fun getCropCostTransactions(@Body params: RequestBody): JsonObject

    @POST("/costofcultivationServices/add-transactions")
    suspend fun addCropCostTransactions(@Body params: RequestBody): JsonObject

    @POST("/costofcultivationServices/delete-farmer-crop")
    suspend fun deleteCrop(@Body params: RequestBody): JsonObject

    @POST("/authService/provideConsent")
    suspend fun updateConsent(@Header("userid") farmerId: Int, @Header("consent") consentValue: Boolean) : JsonObject

}
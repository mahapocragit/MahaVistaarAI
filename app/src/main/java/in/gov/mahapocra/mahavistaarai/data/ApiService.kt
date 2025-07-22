package `in`.gov.mahapocra.mahavistaarai.data

import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST(APIServices.kGeFarmersSelectedCrop)
    suspend fun getFarmersSelectedCrop(@Body params: RequestBody): JsonObject

    @POST(APIServices.kSaveFarmerSelectedCrop)
    suspend fun kSaveFarmerSelectedCrop(@Body params: RequestBody): JsonObject

    @POST(APIServices.kdeleteFarmerSelectedCrop)
    suspend fun deleteSelectedCrop(@Body params: RequestBody): JsonObject

    @POST(APIServices.kLoginViaFarmerId)
    suspend fun farmerLoginBasedOnID(
        @Header("FarmerID") farmerId: String,
        @Body params: RequestBody): JsonObject

    @POST(APIServices.kGetCropCategorywise)
    suspend fun getCropCategoryWise(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetTalukaList)
    suspend fun getTalukaList(@Body params: RequestBody): JsonObject

    @POST(APIServices.kWeatherDetails)
    suspend fun getWeatherDetails(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetRegistration)
    suspend fun getGetRegistration(
        @Header("FAAPRegistrationID") registrationId: Int,
        @Body params: RequestBody
    ): JsonObject

    @POST(APIServices.kGetSOPByList)
    suspend fun getSOPByList(@Body params: RequestBody): JsonObject

    @POST(APIServices.kSoilHealthCardDetailsFromSHCNumber)
    suspend fun getSoilHealthCardDetailsFromSHCNumber(@Body params: RequestBody): JsonObject

    @POST(APIServices.kRequestForChatBot)
    suspend fun requestForChatBotURL(@Body params: RequestBody): JsonObject

    @POST(APIServices.kAuthenticationForNews)
    suspend fun authenticationForNews(@Body params: RequestBody): JsonObject

    @POST(APIServices.getNearestCHCenters)
    suspend fun getCHCInformation(@Body params: RequestBody): JsonObject

    @POST(APIServices.getCodeFromCoordinates)
    suspend fun getCodeFromCoordinates(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetMarketData)
    suspend fun getMarketList(@Body params: RequestBody): JsonObject

    @POST(APIServices.kCompareOtp)
    suspend fun compareOtp(
        @Header("MobileNo") mobileNo: String,
        @Body params: RequestBody): JsonObject

    @POST(APIServices.kCompareOtpReg)
    suspend fun compareOtpReg(
        @Header("MobileNo") mobileNo: String,
        @Body params: RequestBody): JsonObject

    @GET(APIServices.kRevampedDBTSchemes)
    suspend fun getDBTSchemes(): JsonObject

    @GET(APIServices.kRevampedMahaDBTSchemes)
    suspend fun getMahaDBTSchemes(): JsonObject

    @POST(APIServices.kSearchFarmerRequest)
    suspend fun retrieveFarmerToken(@Body params: RequestBody): JsonObject

    @GET(APIServices.kGetDigitalShetishalaSchedule)
    suspend fun getDigitalShetishalaSchedule(): JsonObject

    @GET(APIServices.kEventsForNews)
    suspend fun eventsForNews(
        @Header("Authorization") authHeader: String,
        @Query("state") state: String,
        @Query("is_rejected") isRejected: Boolean
    ): JsonObject

    @GET(APIServices.kGetVideosCategories)
    suspend fun getFarmersVideosJson(): JsonObject

    @GET(APIServices.kGetShetishalaVideos)
    suspend fun getShetishalaVideos(): JsonObject

    @POST(APIServices.kRetrieveFarmerData)
    suspend fun retrieveFarmerData(requestBody: RequestBody): JsonObject

    @POST(APIServices.kClimateResilientTechnology)
    suspend fun getClimateResilientList(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetCropStagesAndAdvisory)
    suspend fun getCropStagesAndAdvisory(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetCropStages)
    suspend fun getCropStages(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetPestDiseaseDetails)
    suspend fun getPestDiseaseDetails(@Body params: RequestBody): ResponseBody

    @POST(APIServices.GetCropSapAdvisory)
    suspend fun getCropSapAdvisory(@Body params: RequestBody): JsonObject

    @POST("notificationServices/fetch-notifications")
    suspend fun getNotificationList(@Header("userid") userId: Int): JsonObject

    @POST("notificationServices/fetch-notifications-indetail")
    suspend fun getNotificationDetails(@Body params: RequestBody): JsonObject
    @POST("notificationServices/update-notification-read-status")
    suspend fun updateNotificationStatus(@Body params: RequestBody): JsonObject

}
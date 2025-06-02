package `in`.gov.mahapocra.mahavistaarai.data

import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.SchemeDataModel
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
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

    @POST(APIServices.kGetCropCategorywise)
    suspend fun getCropCategoryWise(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetTalukaList)
    suspend fun getTalukaList(@Body params: RequestBody): JsonObject

    @POST(APIServices.kWeatherDetails)
    suspend fun getWeatherDetails(@Body params: RequestBody): JsonObject

    @POST(APIServices.kGetRegistration)
    suspend fun getGetRegistration(@Body params: RequestBody): JsonObject

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
    suspend fun compareOtp(@Body params: RequestBody): JsonObject

    @POST(APIServices.kCompareOtpReg)
    suspend fun compareOtpReg(@Body params: RequestBody): JsonObject

    @GET(APIServices.kRevampedDBTSchemes)
    suspend fun getDBTSchemes(): JsonObject

    @GET(APIServices.kRevampedMahaDBTSchemes)
    suspend fun getMahaDBTSchemes(): JsonObject

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
}
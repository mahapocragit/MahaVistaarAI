package `in`.gov.mahapocra.mahavistaarai.data

import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import okhttp3.RequestBody
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

    @GET(APIServices.kEventsForNews)
    suspend fun eventsForNews(
        @Header("Authorization") authHeader: String,
        @Query("state") state: String
    ): JsonObject

    @GET(APIServices.kGetVideosCategories)
    suspend fun getFarmersVideosJson(): JsonObject
}
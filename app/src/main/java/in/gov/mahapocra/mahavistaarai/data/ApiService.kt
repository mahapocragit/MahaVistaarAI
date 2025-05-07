package `in`.gov.mahapocra.mahavistaarai.data

import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

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
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.data.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/api/v1/predict")
    suspend fun predictPest(
        @Part("crop_id") cropId: RequestBody,
        @Part("crop_type") cropType: RequestBody,
        @Part("sowing_date") sowingDate: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<JsonObject>
    @Multipart
    @POST("/pestdetectionServices/store-feedback")
    suspend fun submitFeedback(
        @Part("id") predictResponseId: RequestBody,
        @Part("feedback") feedbackStr: RequestBody,
    ): Response<JsonObject>

    @Multipart
    @POST("pestdetectionServices/crop_pd_advisory")
    suspend fun getPestAdvisory(
        @Header("Authorization") bearerToken: String,
        @Part pdId: MultipartBody.Part
    ): Response<JsonObject>

    @GET("/pestdetectionServices/get-crops-for-pest-detection")
    suspend fun fetchCropList( @Header("Authorization") bearerToken: String ): Response<JsonObject>

    @Multipart
    @POST("pestdetectionServices/store-response-against-crop-image")
    suspend fun storeResponseAgainstCropImage(
        @Header("Authorization") bearerToken: String,
        @Part image: MultipartBody.Part,
        @Part("user_id") usrId: RequestBody,
        @Part("crop_id") cropId: RequestBody,
        @Part("sowing_date") sowingDate: RequestBody,
        @Part("is_success") isSuccess: RequestBody,
        @Part("response") response: RequestBody,
        @Part("pd_id")diseaseId: RequestBody
    ): Response<JsonObject>
}
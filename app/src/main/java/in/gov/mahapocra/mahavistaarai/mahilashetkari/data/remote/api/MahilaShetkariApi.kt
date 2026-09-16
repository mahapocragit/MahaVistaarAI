package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api

import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/** Endpoints from API_README.md. Base URL is the bare host
 *  (see NetworkModule) — every path here starts with the
 *  "mahila-shetkari-service/api/" prefix documented for UAT. */
interface MahilaShetkariApi {

    @GET("mahila-shetkari-service/api/districts/")
    suspend fun getDistricts(
        @Query("division") divisionId: Int? = null
    ): Response<ApiResponseDto<List<DistrictDto>>>

    @GET("mahila-shetkari-service/api/talukas/")
    suspend fun getTalukas(
        @Query("district") districtId: Int
    ): Response<ApiResponseDto<List<TalukaDto>>>

    @GET("mahila-shetkari-service/api/villages/")
    suspend fun getVillages(
        @Query("taluka") talukaId: Int
    ): Response<ApiResponseDto<List<VillageDto>>>

    @GET("mahila-shetkari-service/api/work-types/")
    suspend fun getWorkTypes(): Response<ApiResponseDto<List<WorkTypeDto>>>

    @GET("mahila-shetkari-service/api/caste-categories/")
    suspend fun getCasteCategories(): Response<ApiResponseDto<List<CasteCategoryDto>>>

    @POST("mahila-shetkari-service/api/aadhaar/send-otp/")
    suspend fun sendAadhaarOtp(
        @Body body: SendOtpRequest
    ): Response<ApiResponseDto<SendOtpData>>

    @POST("mahila-shetkari-service/api/aadhaar/verify-otp/")
    suspend fun verifyAadhaarOtp(
        @Body body: VerifyOtpRequest
    ): Response<ApiResponseDto<AadhaarVerifyData>>

    @POST("mahila-shetkari-service/api/farmer/verify/")
    suspend fun verifyFarmerId(
        @Body body: FarmerVerifyRequest
    ): Response<ApiResponseDto<FarmerVerifyData>>

    @POST("mahila-shetkari-service/api/applications/")
    suspend fun submitApplication(
        @Body body: ApplicationRequest
    ): Response<ApiResponseDto<SubmitData>>

    @GET("mahila-shetkari-service/api/applications/status/")
    suspend fun getStatusByAck(
        @Query("ack_no") ackNo: String
    ): Response<ApiResponseDto<ApplicationStatusDto>>

    @GET("mahila-shetkari-service/api/applications/status/")
    suspend fun getStatusByNameVillage(
        @Query("name") name: String,
        @Query("village") villageId: Int
    ): Response<ApiResponseDto<ApplicationStatusDto>>
}

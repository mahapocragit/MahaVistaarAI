package `in`.gov.mahapocra.mahavistaarai.data.api

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface APIRequest {
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
    fun getWareHouseDetails(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetDistrictList)
    fun getDistrictList(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetMarketAndMarketName)
    fun getMarketAndMarketName(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetVillageList)
    fun kGetVillageList(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.USER_News_LIST)
    fun getNewsList(@Body params: RequestBody): Call<JsonObject>

    @POST(ApiConstants.kGetmarketsPriceDetails)
    fun getmarketPriceDetails(@Body params: RequestBody): Call<JsonObject>

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
}

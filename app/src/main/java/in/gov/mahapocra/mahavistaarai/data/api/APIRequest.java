package in.gov.mahapocra.mahavistaarai.data.api;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIRequest {

    @POST(ApiConstants.kOTPRequest)
    Call<JsonObject> getOTPRequest(
            @Header("MobileNo") String mobileNo,
            @Body RequestBody params);

    @POST(ApiConstants.kOTPRegisterRequest)
    Call<JsonObject> getOTPRegisterRequest(
            @Header("MobileNo") String mobileNo,
            @Body RequestBody params);

    @POST(ApiConstants.kRegistrationRequest)
    Call<JsonObject> getRegistrationRequest(
            @Header("MobileNo") String mobileNo,
            @Header("NewMobileNo") String updatedMobileNo,
            @Body RequestBody params);

    @POST(ApiConstants.kUserLogin)
    Call<JsonObject> getUserLoginOTP(
            @Header("MobileNo") String mobileNo,
            @Header("otp") String enteredOTP,
            @Body RequestBody params);

    @POST(ApiConstants.kUserLogin)
    Call<JsonObject> getUserLoginPassword(
            @Header("MobileNo") String mobileNo,
            @Header("Password") String password,
            @Body RequestBody params);

    @POST(ApiConstants.kRefreshTokenLogin)
    Call<JsonObject> getRefreshTokenLoginViaOTP(
            @Header("MobileNo") String mobileNo,
            @Header("otp") String enteredOTP,
            @Header("fcmToken") String firebaseToken,
            @Body RequestBody params);

    @POST(ApiConstants.kRefreshTokenLogin)
    Call<JsonObject> getRefreshTokenLoginViaPassword(
            @Header("MobileNo") String mobileNo,
            @Header("Password") String password,
            @Header("fcmToken") String firebaseToken,
            @Body RequestBody params);

    @POST(ApiConstants.kWareHouseDetails)
    Call<JsonObject> getWareHouseDetails(@Body RequestBody params);

    @POST(ApiConstants.kGetDistrictList)
    Call<JsonObject> getDistrictList(@Body RequestBody params);

    @POST(ApiConstants.kGetMarketAndMarketName)
    Call<JsonObject> getMarketAndMarketName(@Body RequestBody params);

    @POST(ApiConstants.kGetVillageList)
    Call<JsonObject> kGetVillageList(@Body RequestBody params);

    @POST(ApiConstants.USER_News_LIST)
    Call<JsonObject> getNewsList(@Body RequestBody params);

    @POST(ApiConstants.kGetmarketsPriceDetails)
    Call<JsonObject> getmarketPriceDetails(@Body RequestBody params);

    @POST(ApiConstants.DELETE_FERTILIZER_FROM_SAVED)
    Call<JsonObject> deleteFertilizerFromSavedList(@Body RequestBody params);

    @POST(ApiConstants.KSaveFertilizerFormula)
    Call<JsonObject> saveFertilizerFormula(@Body RequestBody params);

    @POST(ApiConstants.kGetFertilizerSavedFormula)
    Call<JsonObject> getFertilizerSavedFormula(@Body RequestBody params);

    @POST(ApiConstants.kResetPassword)
    Call<JsonObject> getNewPassword(@Body RequestBody params);

    @POST(ApiConstants.kGetTokenFromWotr)
    Call<JsonObject> getTokenFromWotr(@Query("MobileNo") String securityKey,
                                      @Query("Password") String dataRequired
    );

    @POST(ApiConstants.kGetFertilizerCalculatedDataWotr)
    Call<JsonObject> getFertilizerCalculatedData(@Query("CropID") String cropID,
                                                 @Query("SowingDate") String sowingDate,
                                                 @Query("IsNPK") String isNPK,
                                                 @Query("SoilTestN") String soilTestN,
                                                 @Query("SoilTestP") String soilTestP,
                                                 @Query("SoilTestK") String soilTestK,
                                                 @Query("VillageCode") String villageCode,
                                                 @Query("FYM") String requiredFYM,
                                                 @Query("TargetYield") String targetYield,
                                                 @Query("PlotSize") String plotSize,
                                                 @Query("PlotUnit") String plotUnit,
                                                 @Query("Token") String token
    );

    @POST(ApiConstants.fetchFarmerListForSHC)
    Call<JsonObject> fetchFarmerListForSHC(@Body RequestBody params);
}

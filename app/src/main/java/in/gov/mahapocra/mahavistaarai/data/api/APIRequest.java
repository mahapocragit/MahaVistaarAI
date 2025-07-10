package in.gov.mahapocra.mahavistaarai.data.api;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIRequest {

    @POST(APIServices.kOTPRequest)
    Call<JsonObject> getOTPRequest(
            @Header("MobileNo") String mobileNo,
            @Body RequestBody params);

    @POST(APIServices.kOTPRegisterRequest)
    Call<JsonObject> getOTPRegisterRequest(
            @Header("MobileNo") String mobileNo,
            @Body RequestBody params);

    @POST(APIServices.kRegistrationRequest)
    Call<JsonObject> getRegistrationRequest(
            @Header("MobileNo") String mobileNo,
            @Header("NewMobileNo") String updatedMobileNo,
            @Body RequestBody params);

    @POST(APIServices.kUserLogin)
    Call<JsonObject> getUserLogin(
            @Header("MobileNo") String mobileNo,
            @Header("Password") String password,
            @Header("otp") String enteredOTP,
            @Body RequestBody params);

    @POST(APIServices.kRefreshTokenLogin)
    Call<JsonObject> getRefreshTokenLogin(
            @Header("MobileNo") String mobileNo,
            @Header("Password") String password,
            @Header("otp") String enteredOTP,
            @Header("fcmToken") String firebaseToken,
            @Body RequestBody params);

    @POST(APIServices.kWareHouseDetails)
    Call<JsonObject> getWareHouseDetails(@Body RequestBody params);

    @POST(APIServices.kGetDistrictList)
    Call<JsonObject> getDistrictList(@Body RequestBody params);

    @POST(APIServices.kGetMarketAndMarketName)
    Call<JsonObject> getMarketAndMarketName(@Body RequestBody params);

    @POST(APIServices.kGetVillageList)
    Call<JsonObject> kGetVillageList(@Body RequestBody params);

    @POST(APIServices.USER_News_LIST)
    Call<JsonObject> getNewsList(@Body RequestBody params);

    @POST(APIServices.kGetmarketsPriceDetails)
    Call<JsonObject> getmarketPriceDetails(@Body RequestBody params);

    @POST(APIServices.kClimateResilientTechnology)
    Call<JsonObject> getClimateResilientList(@Body RequestBody params);

    @POST(APIServices.kGetPestDiseaseDetails)
    Call<JsonObject> getPestDiseaseDetails(@Body RequestBody params);

    @POST(APIServices.kGetCropStages)
    Call<JsonObject> getCropStages(@Body RequestBody params);

    @POST(APIServices.DELETE_FERTILIZER_FROM_SAVED)
    Call<JsonObject> deleteFertilizerFromSavedList(@Body RequestBody params);

    @POST(APIServices.KSaveFertilizerFormula)
    Call<JsonObject> saveFertilizerFormula(@Body RequestBody params);

    @POST(APIServices.kGetFertilizerSavedFormula)
    Call<JsonObject> getFertilizerSavedFormula(@Body RequestBody params);

    @POST(APIServices.kResetPassword)
    Call<JsonObject> getNewPassword(@Body RequestBody params);

    @POST(APIServices.kGetTokenFromWotr)
    Call<JsonObject> getTokenFromWotr(@Query("MobileNo") String securityKey,
                                      @Query("Password") String dataRequired
    );

    @POST(APIServices.kGetFertilizerCalculatedDataWotr)
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

    @POST(APIServices.fetchFarmerListForSHC)
    Call<JsonObject> fetchFarmerListForSHC(@Body RequestBody params);
}

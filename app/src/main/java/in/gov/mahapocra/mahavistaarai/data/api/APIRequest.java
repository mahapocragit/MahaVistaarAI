package in.gov.mahapocra.mahavistaarai.data.api;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface APIRequest {

    @POST(APIServices.kOTPRequest)
    Call<JsonObject> getOTPRequest(@Body RequestBody params);
    @POST(APIServices.kOTPRegisterRequest)
    Call<JsonObject> getOTPRegisterRequest(@Body RequestBody params);

    @POST(APIServices.kRegistrationRequest)
    Call<JsonObject> getRegistrationRequest(@Body RequestBody params);

    @POST(APIServices.kUserLogin)
    Call<JsonObject> getUserLogin(@Body RequestBody params);

    @POST(APIServices.kRefreshTokenLogin)
    Call<JsonObject> getRefreshTokenLogin(@Body RequestBody params);

    @POST(APIServices.getNearestCHCenters)
    Call<JsonObject> getCHCInformation(@Body RequestBody params);

    @POST(APIServices.kWareHouseDetails)
    Call<JsonObject> getWareHouseDetails(@Body RequestBody params);

    @GET(APIServices.kGetVideosCategories)
    Call<JsonObject> getFarmersVideosJson();

    @POST(APIServices.kGetmarketsList)
    Call<JsonObject> getmarketList(@Body RequestBody params);

    @POST(APIServices.kGetDistrictList)
    Call<JsonObject> getDistrictList(@Body RequestBody params);
    @POST(APIServices.kGetMarketData)
    Call<JsonObject> getMarketList(@Body RequestBody params);

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

    @POST(APIServices.kGetCropStagesAndAdvisory)
    Call<JsonObject> getCropStagesAndAdvisory(@Body RequestBody params);

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

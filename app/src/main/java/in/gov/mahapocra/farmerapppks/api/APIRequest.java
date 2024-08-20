package in.gov.mahapocra.farmerapppks.api;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface APIRequest {

    @POST(APIServices.kOTPRequest)
    Call<JsonObject> getOTPRequest(@Body RequestBody params);

    @POST(APIServices.kRegistrationRequest)
    Call<JsonObject> getRegistrationRequest(@Body RequestBody params);

    @POST(APIServices.kGetDistrictData)
    Call<JsonObject> getDistrictData(@Body RequestBody params);

    @POST(APIServices.kGetTalukaData)
    Call<JsonObject> getTalukaData(@Body RequestBody params);

    @POST(APIServices.kMyProfileVillageDetails)
    Call<JsonObject> getMyVillageProfileDetails(@Body RequestBody params);

    @POST(APIServices.kGetVillageData)
    Call<JsonObject> getVillageData(@Body RequestBody params);

    @POST(APIServices.kUserLogin)
    Call<JsonObject> getUserLogin(@Body RequestBody params);

    @POST(APIServices.kRefreshTokenLogin)
    Call<JsonObject> getRefreshTokenLogin(@Body RequestBody params);

    @POST(APIServices.kWareHouseDetails)
    Call<JsonObject> getWareHouseDetails(@Body RequestBody params);

    @POST(APIServices.kWeatherDetails)
    Call<JsonObject> getWeatherDetails(@Body RequestBody params);

    @POST(APIServices.kGetRegistration)
    Call<JsonObject> getGetRegistration(@Body RequestBody params);

    @POST(APIServices.kGeFarmersSelectedCrop)
    Call<JsonObject> getFarmersSelectedCrop(@Body RequestBody params);

    @POST(APIServices.kdeleteFarmerSelectedCrop)
    Call<JsonObject> deleteSelectedCrop(@Body RequestBody params);

    @POST(APIServices.CHECK_USER_ACTIVE_DEACTIVE)
    Call<JsonObject> checkActivateDeactivateUser(@Body RequestBody params);

    @POST(APIServices.kGetmarketsList)
    Call<JsonObject> getmarketList(@Body RequestBody params);

    @POST(APIServices.kGetDistrictList)
    Call<JsonObject> getDistrictList(@Body RequestBody params);

    @POST(APIServices.kGetCropCategorywise)
    Call<JsonObject> getCropCategorywise(@Body RequestBody params);

    @POST(APIServices.kGetTalukaList)
    Call<JsonObject> getTalukaList(@Body RequestBody params);

    @POST(APIServices.kGetCropList)
    Call<JsonObject> getCropList();

    @POST(APIServices.kGetMarketAndMarketName)
    Call<JsonObject> getMarketAndMarketName(@Body RequestBody params);

    @POST(APIServices.kGetVillageList)
    Call<JsonObject> kGetVillageList(@Body RequestBody params);

    @POST(APIServices.kSaveFarmerSelectedCrop)
    Call<JsonObject> kSaveFarmerSelectedCrop(@Body RequestBody params);

    // Get Firebase Notification List
    @POST(APIServices.USER_NOTIFICATION_LIST)
    Call<JsonObject> getfirebaseNotificationList(@Body RequestBody params);

    // Get News List
    @POST(APIServices.USER_News_LIST)
    Call<JsonObject> getNewsList(@Body RequestBody params);

    //READ NOTIFICATION MESSAGE
    @POST(APIServices.READ_NOTIFICATION_MESSAGE)
    Call<JsonObject> readfirebaseNotification(@Body RequestBody params);

    @POST(APIServices.kGetmarketsPriceDetails)
    Call<JsonObject> getmarketPriceDetails(@Body RequestBody params);

    @POST(APIServices.kClimateResilientTechnology)
    Call<JsonObject> getClimateResilientList(@Body RequestBody params);

    @POST(APIServices.kClimateResilientTechnologyNewUpdates)
    Call<JsonObject> getClimateResilientListNewUpdate(@Body RequestBody params);

    @POST(APIServices.kAutoCropAdvisory)
    Call<JsonObject> getAutoCropAdvisory(@Body RequestBody params);

    @POST(APIServices.kGetPestDiseaseDetails)
    Call<JsonObject> getPestDiseaseDetails(@Body RequestBody params);

    @POST(APIServices.kGetCropStages)
    Call<JsonObject> getCropStages(@Body RequestBody params);

    @POST(APIServices.kGetCropStagesAndAdvisory)
    Call<JsonObject> getCropStagesAndAdvisory(@Body RequestBody params);



    @POST(APIServices.kAutoAdvisory)
    Call<JsonObject> getAutoAdvisory(@Body RequestBody params);

    @POST(APIServices.advisory_feedback)
    Call<JsonObject> submitAdvisoryFeedback(@Body RequestBody params);

    @POST(APIServices.kSubdivisionUpcommingEvent)
    Call<JsonObject> getSubdivisionUpcomingList(@Body RequestBody params);

    @POST(APIServices.GET_PS_SCHEDULE_DETAIL_URL)
    Call<JsonObject> psGetEventDetailRequest(@Body RequestBody params);

    @Multipart
    @POST(APIServices.kSubmitGrievance)
    Call<JsonObject> submitGrievance(@Part MultipartBody.Part image,
                                     @PartMap Map<String, RequestBody> params);

    @POST(APIServices.kBandhavarPostStory)
    Call<JsonObject> postStoryBandhavar(@Body RequestBody params);

    @POST(APIServices.KSaveFertilizerFormula)
    Call<JsonObject> saveFertilizerFormula(@Body RequestBody params);

    @POST(APIServices.kGetFertilizerSavedFormula)
    Call<JsonObject> getFertilizerSavedFormula(@Body RequestBody params);

    @Multipart
    @POST(APIServices.kSubmitSuggetion)
    Call<JsonObject> submitSuggestion(@Part MultipartBody.Part image,
                                      @PartMap Map<String, RequestBody> params);

    @POST(APIServices.kDbtActivitiesDetails)
    Call<JsonObject> getDbtActivitiesDetails(@Query("SecurityKey") String securityKey,
                                             @Query("Lang") String dataRequired,
                                             @Query("DataRequired") String DataRequired);
    @POST(APIServices.kResetPassword)
    Call<JsonObject> getNewPassword(@Body RequestBody params);


    @POST(APIServices.kDbtActivitiesGrpDetails)
    Call<JsonObject> getDbtActivitiesGrpDetails(@Query("SecurityKey") String securityKey,
                                                @Query("Lang") String dataRequired,
                                                @Query("DataRequired") String DataRequired,
                                                @Query("ActivityGroupID") String farmerID
                                                );

    @POST(APIServices.kDbtActivitiesGrpDocDetails)
    Call<JsonObject> getDbtActivitiesGrpDocDetails(@Query("SecurityKey") String securityKey,
                                                   @Query("Lang") String dataRequired,
                                                   @Query("DataRequired") String DataRequired,
                                                   @Query("ActivityGroupID") String farmerID
    );

    @POST(APIServices.kGetTokenFromWotr)
    Call<JsonObject> getTokenFromWotr(@Query("MobileNo") String securityKey,
                                                @Query("Password") String dataRequired
    );

    @POST(APIServices.kGetFertilizerCalculatedDataWotr)
    Call<JsonObject> getFertilzerCalculatedData(@Query("CropID") String cropID,
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
}

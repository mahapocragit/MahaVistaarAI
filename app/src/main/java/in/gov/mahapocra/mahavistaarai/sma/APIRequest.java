/*
 * Copyright (c) 2018. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 29/12/18 11:26 AM
 */

package in.gov.mahapocra.mahavistaarai.sma;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface APIRequest {
//

    /****** COMMON API ******/
    // Login
//    @POST(APIServices.kOAuth)
//    Call<JsonObject> oauthLoginRequest(@Body RequestBody params);
    @POST("authService/sso_login_farmer_app")
    Call<JsonObject> oauthLoginRequest(
            @Header("username") String username,
            @Header("secret") String secret,
            @Body RequestBody body
    );

    @POST(APIServices.kOAuthRefreshToken)
    Call<JsonObject> oauthRefreshToken(@Body RequestBody params);

    //Change Password
    @POST(APIServices.kFirstAttemptPassChange)
    Call<JsonObject> changePasswordRequest(@Body RequestBody params);

    @POST(APIServices.kAppVersion)
    Call<JsonObject> appVersion(@Body RequestBody params);

    @POST("scripts/get_apk_file.php?app=SMA")
    Call<JsonObject> forceUpdateRequest();
    @POST(APIServices.CHECK_USER_ACTIVE_DEACTIVE)
    Call<JsonObject> checkActivateDeactivateUser(@Body RequestBody params);

    @POST(APIServices.GET_NOTIFICATION_UNREAD_COUNT)
    Call<JsonObject> getFirebaseUnreadNotificationCount(@Body RequestBody params);

    @POST(APIServices.userdetails)
    Call<JsonObject> getroledata(@Body RequestBody params);

    //FORGOT PASS
    @POST(APIServices.kForgotPass)
    Call<JsonObject> forgotPasswordRequest(@Body RequestBody params);

    @POST(APIServices.kResendOtp)
    Call<JsonObject> resendOTPRequest(@Body RequestBody params);

    @POST(APIServices.kResetPass)
    Call<JsonObject> resetPasswordRequest(@Body RequestBody params);



    //--Profile
    @POST(APIServices.kUpdateProfile)
    Call<JsonObject> updateProfileRequest(@Body RequestBody params);

    @Multipart
    @POST(APIServices.kUpdateProfilePic)
    Call<JsonObject> uploadProfileImagesRequest(@Part MultipartBody.Part image,
                                                @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST(APIServices.KT_FileUpload)
    Call<JsonObject> uploadImagesKTActivity(@Part MultipartBody.Part image,
                                              @PartMap Map<String, RequestBody> params);

    @POST(APIServices.KT_FarmerGroupList)
    Call<JsonObject> farmerGroupListKTRequest(@Body RequestBody params);

    @POST(APIServices.KT_FarmerDiscussionList)
    Call<JsonObject> farmerDiscussionListKTRequest(@Body RequestBody params);
    @POST(APIServices.KT_submitDetails)
    Call<JsonObject> submitDetailsKTRequest(@Body RequestBody params);

    @POST(APIServices.KT_CATEGORY_LIST)
    Call<JsonObject> categoryListKTRequest(@Body RequestBody params);

    @POST(APIServices.KT_dbtFarmerList)
    Call<JsonObject> dbtFarmerListKTRequest(@Body RequestBody params);

    @POST(APIServices.KT_SUB_CATEGORY_LIST)
    Call<JsonObject> subCategoryListKTRequest(@Body RequestBody params);
    @POST(APIServices.KT_REPORT_CALENDAR)
    Call<JsonObject> fetchCalendarMonthReportKT(@Body RequestBody params);

    @POST(APIServices.KT_FEEDBACK)
    Call<JsonObject> addKTFeedBackRequest(@Body RequestBody params);

    @POST(APIServices.KT_GET_WORK_DETAILS)
    Call<JsonObject> fetchWorkDetailsKT(@Body RequestBody params);



    //FARMERS
    @POST(APIServices.kSubDivisionMaster)
    Call<JsonObject> fetchSubDivisionRequest(@Body RequestBody params);

    @POST(APIServices.kTalukaBySubDivision)
    Call<JsonObject> fetchTalukaBySubDivisionRequest(@Body RequestBody params);

    //KVK
    @POST(APIServices.kFacilitatorList)
    Call<JsonObject> fetchFacilitator(@Body RequestBody params);

    @POST(APIServices.kFacilitatorList)
    Call<JsonObject> fetchFacilitatorSchedules(@Body RequestBody params);


    @POST(APIServices.kSearchVillageCensusCode)
    Call<JsonObject> fetchVillageCensusCodeRequest(@Body RequestBody params);




    //CA
    @POST(APIServices.CA_Village)
    Call<JsonObject> fetchCAVillageListRequest(@Body RequestBody params);


//    @POST(APIServices.CA_SUB_ACT)
//    Call<JsonObject> fetchCASubActivityRequest(@Body RequestBody params);


    @POST(APIServices.CA_CLUSTER_CODE)
    Call<JsonObject> clusterCodeSyncDownRequest(@Body RequestBody params);

    @POST(APIServices.CA_ABSENT_REASON)
    Call<JsonObject> absentReasonSyncDownRequest(@Body RequestBody params);

//    @Multipart
//    @POST(APIServices.CA_OFFLINE_IMG_UPLOAD)
//    Call<JsonObject> uploadCAImagesRequest(@Part MultipartBody.Part image,
//                                           @PartMap Map<String, RequestBody> params);


    @POST(APIServices.kAllDistrict)
    Call<JsonObject> fetchDistrict(@Body RequestBody params);
    @POST(APIServices.kAllTaluka)
    Call<JsonObject> fetchTaluka(@Body RequestBody params);
    @POST(APIServices.kAllVillage)
    Call<JsonObject> fetchVillage(@Body RequestBody params);
    @POST(APIServices.kAllCrops)
    Call<JsonObject> fetchCropsForCA(@Body RequestBody params);
    @POST(APIServices.kCropsPest)
    Call<JsonObject> fetchCropsPestForCA(@Body RequestBody params);
    @POST(APIServices.kImageSection)
    Call<JsonObject> fetchkImageSectionForCA(@Body RequestBody params);
    @POST(APIServices.kCropsDiease)
    Call<JsonObject> fetchCropsDieaseForCA(@Body RequestBody params);
    @POST(APIServices.kCHMS_Farmer_CropListTC)
    Call<JsonObject> fetchkCHMSFarmerCropListForTC(@Body RequestBody params);

    @POST(APIServices.kUpdatefarmerDataByTC)
    Call<JsonObject> fetchUpdatefarmerStatusForTC(@Body RequestBody params);
    @Multipart
    @POST(APIServices.kFileUploadImgCGMByTC)
    Call<JsonObject> uploadImagesRequestImgCropTC(@Part MultipartBody.Part image,
                                                  @PartMap Map<String, RequestBody> params);


}




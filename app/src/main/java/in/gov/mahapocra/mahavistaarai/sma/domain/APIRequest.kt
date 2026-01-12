package in.gov.mahapocra.mahavistaarai.sma.domain;

import com.google.gson.JsonObject;

import java.util.Map;

import in.gov.mahapocra.mahavistaarai.sma.data.constants.APIServices;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface APIRequest {

    @POST("authService/sso_login_farmer_app")
    Call<JsonObject> oauthLoginRequest(
            @Header("username") String username,
            @Header("secret") String secret,
            @Body RequestBody body
    );

    @POST(APIServices.kOAuthRefreshToken)
    Call<JsonObject> oauthRefreshToken(@Body RequestBody params);

    @POST("scripts/get_apk_file.php?app=SMA")
    Call<JsonObject> forceUpdateRequest();

    @POST(APIServices.CHECK_USER_ACTIVE_DEACTIVE)
    Call<JsonObject> checkActivateDeactivateUser(@Body RequestBody params);

    @POST(APIServices.GET_NOTIFICATION_UNREAD_COUNT)
    Call<JsonObject> getFirebaseUnreadNotificationCount(@Body RequestBody params);

    @POST(APIServices.userdetails)
    Call<JsonObject> getroledata(@Body RequestBody params);

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

    @POST(APIServices.CA_ABSENT_REASON)
    Call<JsonObject> absentReasonSyncDownRequest(@Body RequestBody params);

}




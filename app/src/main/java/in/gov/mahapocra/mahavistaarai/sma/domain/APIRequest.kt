package `in`.gov.mahapocra.mahavistaarai.sma.domain

import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.sma.data.constants.APIServices
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface APIRequest {
    @POST("authService/sso_login_farmer_app")
    fun oauthLoginRequest(
        @Header("username") username: String?,
        @Header("secret") secret: String?,
        @Body body: RequestBody?
    ): Call<JsonObject?>?

    @POST(APIServices.kOAuthRefreshToken)
    fun oauthRefreshToken(@Body params: RequestBody?): Call<JsonObject?>?

    @POST("scripts/get_apk_file.php?app=SMA")
    fun forceUpdateRequest(): Call<JsonObject?>?

    @POST(APIServices.CHECK_USER_ACTIVE_DEACTIVE)
    fun checkActivateDeactivateUser(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.GET_NOTIFICATION_UNREAD_COUNT)
    fun getFirebaseUnreadNotificationCount(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.userdetails)
    fun getroledata(@Body params: RequestBody?): Call<JsonObject?>?

    @Multipart
    @POST(APIServices.KT_FileUpload)
    fun uploadImagesKTActivity(
        @Part image: MultipartBody.Part?,
        @PartMap params: MutableMap<String?, RequestBody?>?
    ): Call<JsonObject?>?

    @POST(APIServices.KT_FarmerGroupList)
    fun farmerGroupListKTRequest(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_FarmerDiscussionList)
    fun farmerDiscussionListKTRequest(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_submitDetails)
    fun submitDetailsKTRequest(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_CATEGORY_LIST)
    fun categoryListKTRequest(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_dbtFarmerList)
    fun dbtFarmerListKTRequest(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_SUB_CATEGORY_LIST)
    fun subCategoryListKTRequest(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_REPORT_CALENDAR)
    fun fetchCalendarMonthReportKT(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_FEEDBACK)
    fun addKTFeedBackRequest(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.KT_GET_WORK_DETAILS)
    fun fetchWorkDetailsKT(@Body params: RequestBody?): Call<JsonObject?>?

    @POST(APIServices.CA_ABSENT_REASON)
    fun absentReasonSyncDownRequest(@Body params: RequestBody?): Call<JsonObject?>?
}




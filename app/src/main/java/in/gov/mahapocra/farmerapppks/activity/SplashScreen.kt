package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppSession
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import `in`.gov.mahapocra.farmerapppks.models.response.UserDetailsModel
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.*

class SplashScreen : AppCompatActivity(), ApiCallbackCode {

    lateinit var session: AppSession
    lateinit var userDetailsModel: UserDetailsModel
    var sharedpreferences: SharedPreferences? = null
    val mypreference = "user_registration_details"
    var userRegistration = "userRegistration"
    var farmerId : Int ? = 0
    var languageToLoad: String? = null
    private var userId: Int = 0
    private var loginType = ""
    var username: String? = null
    var versionName:kotlin.String? = null
    var token:kotlin.String? = null
    var machineId:kotlin.String? = null
    var appID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@SplashScreen).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        setContentView(R.layout.activity_splash_screen)
        var pinfo: PackageInfo? = null
        try {
            pinfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val versionNumber = pinfo!!.versionCode
        versionName = pinfo!!.versionName
        token = FirebaseInstanceId.getInstance().token
        Log.d("token12345", "" + token)
        session = AppSession(this)
       // userDetailsModel = session.userDetailsModel
         farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        //          String  languageToLoad = "hi";
//        languageToLoad = "hi"
//        if (AppSettings.getLanguage(this@SplashScreen).equals("1", ignoreCase = true))
//        {
//            languageToLoad = "en"
//        }
        if (farmerId!! > 0) {
            getUserDetails()
        } else
        {
            Handler().postDelayed({
                val intent = Intent(this, LoginScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }, 2000)
        }



    }

    private fun getUserDetails()
    {
        val jsonObject = JSONObject()
        //var farmerId: Int = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        Log.d("farmerId11111", farmerId.toString());
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("FAAPRegistrationID", farmerId)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppinventorApi(this, APIServices.DBT, "", AppString(this).getkMSG_WAIT(), true)
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getGetRegistration(requestBody)
                DebugLog.getInstance().d("param1=" + responseCall.request().toString())
                DebugLog.getInstance().d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                api.postRequest(responseCall, this, 1)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                //editor.putString(userRegistration, "UserRegistration")
                Log.d("User_Registration===",userRegistration )

            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
    }
    private fun manageSession(model:UserDetailsModel) {
//        val session = AppSession(this)
//       // DebugLog.getInstance().d("cropId=$cropId")
//        AppSettings.getInstance().setValue(this, AppConstants.uName, model.name)
//        AppSettings.getInstance().setValue(this, AppConstants.uMobileNo, model.mobileNo)
//        AppSettings.getInstance().setValue(this, AppConstants.uEmail, model.emailId)
//        //AppSettings.getInstance().setValue(this, AppConstants.fREGISTER_ID, model.faapRegistrationID)
//        AppSettings.getInstance().setValue(this, AppConstants.uDIST, model.districtName)
//        AppSettings.getInstance().setValue(this, AppConstants.uDISTId, model.districtID)
//        AppSettings.getInstance().setValue(this, AppConstants.uTALUKA, model.talukaName)
//        AppSettings.getInstance().setValue(this, AppConstants.uTALUKAID, model.talukaID)
//        Log.d("SplashScreen::","manageSession"+model.name);
//        Log.d("SplashScreen::","manageSession"+model.mobileNo);
//        Log.d("SplashScreen::",model.emailId);
//        Log.d("SplashScreen::",model.faapRegistrationID);
//        Log.d("SplashScreen::",model.districtName);
//        Log.d("SplashScreen::",model.districtID);
//        Log.d("SplashScreen::",model.talukaName);
//        Log.d("SplashScreen::",model.talukaID);
    }
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                Log.d("jSONObject2323232", jSONObject.toString())
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    username =  jSONObject.getString("Name")
                    Log.d("strName1111", username.toString())
                    var strMobNo: String =  jSONObject.getString("MobileNo")
                    var strEmailId: String =  jSONObject.getString("EmailId")
                    userId =  jSONObject.getInt("FAAPRegistrationID")
                    var strDistName: String =  jSONObject.getString("DistrictName")
                    Log.d("strDistName1212121", strDistName.toString())
                    var strDistId: Int =  jSONObject.getInt("DistrictID")
                    var strTalukaName: String =  jSONObject.getString("TalukaName")
                    Log.d("strTalukaName121212", strTalukaName.toString())
                    var strTalukaId: Int =  jSONObject.getInt("TalukaID")
                    val strVillageId : Int= jSONObject.getInt("VillageID")
                    val strVillageName: String = jSONObject.getString("VillageName")
                   // Toast.makeText(this,strName, Toast.LENGTH_LONG).show();
                    //UIToastMessage.showShortDuration(this, notifiCountValue)

                    //FarmerRegstredID = "3"
                    AppSettings.getInstance().setValue(this, AppConstants.uName, username)
                    AppSettings.getInstance().setValue(this, AppConstants.uMobileNo, strMobNo)
                    AppSettings.getInstance().setValue(this, AppConstants.uEmail, strEmailId)
                    AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, userId)
                    AppSettings.getInstance().setValue(this, AppConstants.uDIST, strDistName)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, strDistId)
                    AppSettings.getInstance().setValue(this, AppConstants.uTALUKA, strTalukaName)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, strTalukaId)
                    Log.d("SplashScreen::","onResponse"+response)

                  var districtName: String  =  AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
                    var taluka: String  =  AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
                    Log.d("districtName::taluka",districtName+" ::"+taluka)

                   // saveTokenNcheckActiveDeactive()

                    Handler().postDelayed({
                        val intent = Intent(this, DashboardScreen::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
        }

        if (i == 2) {
            if (jSONObject != null) {
                val intent = Intent(this, DashboardScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                if (jSONObject.getString("status").equals("200", ignoreCase = true)) {
                    val responseModel = ResponseModel(jSONObject)
                    if (responseModel.getStatus()) {
                        //Handler().postDelayed({
                            val intent = Intent(this, DashboardScreen::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                       // }, 2000)
                    } else {
                        UIToastMessage.show(this, "Users deactive")
                    }
                }
            }
        }
    }

    private fun saveTokenNcheckActiveDeactive() {
            try {

                appID = 8
                machineId = getMachineId()
                Log.d(
                    "appVersionLoggedDetails",
                    " username=$username  app_id=$appID  versionName=$versionName token=$token user_id=$userId device_ID=$machineId"
                )


                    try {
                        val jsonObject = JSONObject()
                        jsonObject.put("username", username)
                        jsonObject.put("app_id", appID)
                        jsonObject.put("version_number", versionName)
                        jsonObject.put("fcm_token", token)
                        jsonObject.put("user_id", userId.toInt())
                        jsonObject.put("device_id", machineId)
                        val requestBody =
                            AppUtility.getInstance().getRequestBody(jsonObject.toString())
                        val api = AppinventorApi(this, APIServices.SSO, "", "Please Wait...", true)
                        val retrofit = api.retrofitInstance
                        val apiRequest = retrofit.create(APIRequest::class.java)
                        val responseCall: Call<JsonObject> =
                            apiRequest.checkActivateDeactivateUser(requestBody)
                        DebugLog.getInstance()
                            .d("Splash_param=" + responseCall.request().toString())
                        DebugLog.getInstance().d(
                            "Splash_param=" + AppUtility.getInstance()
                                .bodyToString(responseCall.request())
                        )
                        api.postRequest(responseCall, this, 2)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

    }

    @JvmName("getMachineId1")
    fun getMachineId(): String? {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj")
    }
}
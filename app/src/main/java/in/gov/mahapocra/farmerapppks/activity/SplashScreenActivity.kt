package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppSession
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.ResponseModel
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.*

class SplashScreenActivity : AppCompatActivity(), ApiCallbackCode {

    private lateinit var session: AppSession
    var farmerId: Int? = 0
    var languageToLoad: String? = null
    private var userId: Int = 0
    private var username: String? = null
    private var versionName: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@SplashScreenActivity).equals("1", ignoreCase = true)) {
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
        var pinfo: PackageInfo? = null
        try {
            pinfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        versionName = pinfo!!.versionName
        token = FirebaseMessaging.getInstance().token.toString()
        Log.d("token12345", "" + token)
        session = AppSession(this)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (farmerId!! > 0) {
            getUserDetails()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

    private fun getUserDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("FAAPRegistrationID", farmerId)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                APIServices.DBT,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getGetRegistration(requestBody)
                api.postRequest(responseCall, this@SplashScreenActivity, 1)
            }
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                val response =
                    ResponseModel(jSONObject)
                if (response.getStatus()) {
                    username = jSONObject.getString("Name")
                    val strMobNo: String = jSONObject.getString("MobileNo")
                    val strEmailId: String = jSONObject.getString("EmailId")
                    userId = jSONObject.getInt("FAAPRegistrationID")
                    val strDistName: String = jSONObject.getString("DistrictName")
                    val strDistId: Int = jSONObject.getInt("DistrictID")
                    val strTalukaName: String = jSONObject.getString("TalukaName")
                    val strTalukaId: Int = jSONObject.getInt("TalukaID")

                    AppSettings.getInstance().setValue(this, AppConstants.uName, username)
                    AppSettings.getInstance().setValue(this, AppConstants.uMobileNo, strMobNo)
                    AppSettings.getInstance().setValue(this, AppConstants.uEmail, strEmailId)
                    AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, userId)
                    AppSettings.getInstance().setValue(this, AppConstants.uDIST, strDistName)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, strDistId)
                    AppSettings.getInstance().setValue(this, AppConstants.uTALUKA, strTalukaName)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, strTalukaId)

                    Handler(Looper.getMainLooper()).postDelayed({
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
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj")
    }
}
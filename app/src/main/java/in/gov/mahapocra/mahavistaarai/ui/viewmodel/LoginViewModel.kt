package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.sql.Timestamp

class LoginViewModel : ViewModel() {

    private val _sendOtpToMobileResponse = MutableLiveData<JsonObject>()
    val sendOtpToMobileResponse: LiveData<JsonObject> = _sendOtpToMobileResponse

    private val _sendOtpForFarmerIdResponse = MutableLiveData<JsonObject>()
    val sendOtpForFarmerIdResponse: LiveData<JsonObject> = _sendOtpForFarmerIdResponse

    private val _sendOtpToFarmerIdResponse = MutableLiveData<JsonObject>()
    val sendOtpToFarmerIdResponse: LiveData<JsonObject> = _sendOtpToFarmerIdResponse

    private val _compareOtpToFarmerIdResponse = MutableLiveData<JsonObject>()
    val compareOtpToFarmerIdResponse: LiveData<JsonObject> = _compareOtpToFarmerIdResponse

    private val _compareOtpToFarmerIdRegistrationResponse = MutableLiveData<JsonObject>()
    val compareOtpToFarmerIdRegistrationResponse: LiveData<JsonObject> =
        _compareOtpToFarmerIdRegistrationResponse

    private val _updateFarmerDetailsByIdResponse = MutableLiveData<JsonObject>()
    val updateFarmerDetailsByIdResponse: LiveData<JsonObject> = _updateFarmerDetailsByIdResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun sendOtpToFarmerId(context: Context, farmerId: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val userId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.sendOtpToFarmerId(farmerId, userId, requestBody)
                ProgressHelper.disableProgressDialog()
                _sendOtpToFarmerIdResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun sendOtpToMobile(context: Context, mobile: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)

                val response = api.sendOtpToMobile(mobile, requestBody)

                ProgressHelper.disableProgressDialog()

                if (response.isSuccessful) {
                    _sendOtpToMobileResponse.value = response.body()
                } else {
                    val errorBodyString = response.errorBody()?.string()

                    val json = JSONObject(errorBodyString ?: "{}")
                    val serverMessage =
                        json.optString("response", "Too many attempts. Please try later.")

                    _error.value = serverMessage
                }

            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun compareOtpToFarmerId(context: Context, farmerId: String, otp: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val userId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.compareOtpToFarmerId(farmerId, userId, otp, requestBody)
                ProgressHelper.disableProgressDialog()
                _compareOtpToFarmerIdResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun compareOtpToFarmerIdRegistration(
        context: Context,
        farmerId: String,
        otp: String,
        timestamp: Long
    ) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            val deviceId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val versionNumber = LocalCustom.getVersionName(context)
            try {
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.compareOtpToFarmerIdRegistration(
                    farmerId,
                    otp,
                    timestamp,
                    versionNumber,
                    deviceId,
                    requestBody
                )
                ProgressHelper.disableProgressDialog()
                _compareOtpToFarmerIdRegistrationResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun updateFarmerDetailsById(
        context: Context,
        farmerId: String,
        name: String,
        mobile: String,
        villageCode: String
    ) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val userId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.updateFarmerDetailsById(
                    farmerId,
                    userId,
                    name,
                    mobile,
                    villageCode,
                    requestBody
                )
                ProgressHelper.disableProgressDialog()
                _updateFarmerDetailsByIdResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}
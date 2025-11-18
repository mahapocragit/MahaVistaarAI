package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.content.Context
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
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class LoginViewModel : ViewModel(){

    private val _getOTPRequestResponse = MutableLiveData<JsonObject>()
    val getOTPRequestResponse: LiveData<JsonObject> = _getOTPRequestResponse

    private val _sendOtpToFarmerIdResponse = MutableLiveData<JsonObject>()
    val sendOtpToFarmerIdResponse: LiveData<JsonObject> = _sendOtpToFarmerIdResponse

    private val _compareOtpToFarmerIdResponse = MutableLiveData<JsonObject>()
    val compareOtpToFarmerIdResponse: LiveData<JsonObject> = _compareOtpToFarmerIdResponse

    private val _getUserLoginOTPResponse = MutableLiveData<JsonObject>()
    val getUserLoginOTPResponse: LiveData<JsonObject> = _getUserLoginOTPResponse

    private val _getUserLoginPasswordResponse = MutableLiveData<JsonObject>()
    val getUserLoginPasswordResponse: LiveData<JsonObject> = _getUserLoginPasswordResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getOTPRequest(context: Context, mobileNo: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().put("SecurityKey", ApiConstants.SSO_KEY)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.getOTPRequest(mobileNo, requestBody)
                ProgressHelper.disableProgressDialog()
//                _getOTPRequestResponse.value = response
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

    fun getUserLoginOTP(context: Context, mobileNo: String, otp: String, refreshToken: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                    put("refresh_token", refreshToken)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.getUserLoginOTP(mobileNo, otp, requestBody)
                ProgressHelper.disableProgressDialog()
//                _getUserLoginOTPResponse.value = response
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

    fun getUserLoginPassword(context: Context, mobileNo: String, password: String, refreshToken: String = "") {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                    if (refreshToken!="") {
                        put("refresh_token", refreshToken)
                    }
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.getUserLoginPassword(mobileNo, password, requestBody)
                ProgressHelper.disableProgressDialog()
//                _getUserLoginPasswordResponse.value = response
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

    fun sendOtpToFarmerId(context: Context, farmerId: String){
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val userId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.sendOtpToFarmerId(farmerId, userId, requestBody)
                _sendOtpToFarmerIdResponse.value = response
            }catch (e: Exception) {
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

    fun compareOtpToFarmerId(context: Context, farmerId: String, otp: String){
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val userId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.compareOtpToFarmerId(farmerId, userId, otp, requestBody)
                _compareOtpToFarmerIdResponse.value = response
            }catch (e: Exception) {
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
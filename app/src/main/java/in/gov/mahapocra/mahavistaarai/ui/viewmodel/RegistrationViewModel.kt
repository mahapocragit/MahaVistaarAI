package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class RegistrationViewModel : ViewModel() {


    private val _getOTPRegisterResponse = MutableLiveData<JsonObject>()
    val getOTPRegisterResponse: LiveData<JsonObject> = _getOTPRegisterResponse

    private val _getRegistrationResponse = MutableLiveData<JsonObject>()
    val getRegistrationResponse: LiveData<JsonObject> = _getRegistrationResponse

    private val _getVillageListResponse = MutableLiveData<JsonObject>()
    val getVillageListResponse: LiveData<JsonObject> = _getVillageListResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getOTPRegisterRequest(context: Context, mobile: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response: JsonObject = apiRequest.getOTPRegisterRequest(mobile, requestBody)
                ProgressHelper.disableProgressDialog()
                _getOTPRegisterResponse.value = response
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

    fun getRegistrationRequest(
        context: Context,
        registerMob: String,
        updatedMobile: String,
        data: JSONObject
    ) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val requestBody = AppUtility.getInstance().getRequestBody(data.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response: JsonObject = apiRequest.getRegistrationRequest(
                    registerMob,
                    updatedMobile,
                    requestBody
                )
//                Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show()
                ProgressHelper.disableProgressDialog()
                _getRegistrationResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getVillageList(context: Context, languageToLoad: String, talukaCode: Int) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lang", languageToLoad)
                jsonObject.put("taluka_code", talukaCode)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getVillageList(requestBody)
                ProgressHelper.disableProgressDialog()
                _getVillageListResponse.value = response
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
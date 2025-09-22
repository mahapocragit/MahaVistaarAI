package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
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

class MarketPriceViewModel : ViewModel() {


    private val _getMarketPriceDetailsResponse = MutableLiveData<JsonObject>()
    val getMarketPriceDetailsResponse: LiveData<JsonObject> = _getMarketPriceDetailsResponse

    private val _responseMarketList = MutableLiveData<JsonObject>()
    val responseMarketList: LiveData<JsonObject> = _responseMarketList

    private val _getMarketAndMarketNameResponse = MutableLiveData<JsonObject>()
    val getMarketAndMarketNameResponse: LiveData<JsonObject> = _getMarketAndMarketNameResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getMarketAndMarketName(context: Context, districtID: Int, language: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("api_key", APIKeys.SSO_PROD)
                    put("district_code", districtID)
                    put("lang", language)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.getMarketAndMarketName(requestBody)
                ProgressHelper.disableProgressDialog()
                _getMarketAndMarketNameResponse.value = response
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

    fun fetchMarketList(context: Context, languageToLoad: String, districtCode: Int) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lang", languageToLoad)
                jsonObject.put("api_key", APIKeys.SSO_PROD)
                jsonObject.put("district_code", districtCode)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getMarketList(requestBody)
                ProgressHelper.disableProgressDialog()
                _responseMarketList.value = response
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

    fun getMarketPriceDetails(context: Context, mandiId: Int, language: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("api_key", APIKeys.SSO_PROD)
                    put("apmc_id", mandiId)
                    put("lang", language)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.getMarketPriceDetails(requestBody)
                ProgressHelper.disableProgressDialog()
                _getMarketPriceDetailsResponse.value = response
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
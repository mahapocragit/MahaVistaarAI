package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class LeaderboardViewModel : ViewModel() {
    private var _responseLeaderboardForAll = MutableLiveData<JsonObject>()
    val responseLeaderboardForAll: MutableLiveData<JsonObject> = _responseLeaderboardForAll
    private var _responseUpdateUserPoints = MutableLiveData<JsonObject>()
    val responseUpdateUserPoints: MutableLiveData<JsonObject> = _responseUpdateUserPoints

    private var _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    fun getLeaderboardForAll(context: Context, token: String) {
        viewModelScope.launch {
            try {
                ProgressHelper.showProgressDialog(context)
                val farmerId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getLeaderboardForAll(
                    farmerId,
                    token
                )
                ProgressHelper.disableProgressDialog()
                _responseLeaderboardForAll.value = response
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

    fun updateUserPoints(context: Context, screen: Int) {
        viewModelScope.launch {
            try {
                val farmerId = AppSettings.getInstance()
                    .getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("user_id", farmerId)
                    put("page_id", screen)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.updateUserPoints(requestBody)
                _responseUpdateUserPoints.value = response
            } catch (e: Exception) {
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
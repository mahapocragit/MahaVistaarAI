package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.content.Context
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
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class LeaderboardViewModel : ViewModel() {
    private var _getLeaderboardDataResponse = MutableLiveData<JsonObject>()
    val getLeaderboardDataResponse: MutableLiveData<JsonObject> = _getLeaderboardDataResponse

    private var _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    fun getLeaderboardData(context: Context, selectedValue: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {

                val talukaId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.uTALUKAID, 0)
                val districtId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.uDISTId, 0)
                val jsonObject = JSONObject()
                when (selectedValue) {
                    "taluka" -> {
                        jsonObject.apply {
                            put("level", "taluka")
                            put("code", talukaId)//4201
                        }
                    }

                    "district" -> {
                        jsonObject.apply {
                            put("level", "district")
                            put("code", districtId)//522
                        }
                    }

                    else -> {
                        jsonObject.apply {
                            put("level", "state")
                            put("code", 0)
                        }
                    }
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getLeaderboardData(requestBody)
                ProgressHelper.disableProgressDialog()
                _getLeaderboardDataResponse.value = response
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
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
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

class LeaderboardViewModel : ViewModel() {
    private var _getLeaderboardDataResponse = MutableLiveData<JsonObject>()
    val getLeaderboardDataResponse: MutableLiveData<JsonObject> = _getLeaderboardDataResponse

    private var _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    fun getLeaderboardData(context: Context, talukaCode: Int = 0, districtCode: Int = 0) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                if (talukaCode!=0){
                    jsonObject.apply {
                        put("level", "taluka")
                        put("code", talukaCode)//4201
                    }
                }else if (districtCode!=0){
                    jsonObject.apply {
                        put("level", "district")
                        put("code", districtCode)//522
                    }
                }else{
                    jsonObject.apply {
                        put("level", "state")
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
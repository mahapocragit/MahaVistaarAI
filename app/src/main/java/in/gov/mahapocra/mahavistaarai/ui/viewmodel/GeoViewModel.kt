package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

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
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class GeoViewModel : ViewModel() {

    private val _districtIdResponse = MutableLiveData<JsonObject>()
    val districtIdResponse: LiveData<JsonObject> = _districtIdResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getDistrictData(context: Context, languageToLoad: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lang", languageToLoad)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getDistrictList(requestBody)
                ProgressHelper.disableProgressDialog()
                _districtIdResponse.value = response
            } catch (e: JSONException) {
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
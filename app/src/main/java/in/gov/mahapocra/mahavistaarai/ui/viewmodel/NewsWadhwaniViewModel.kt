package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.gov.mahapocra.mahavistaarai.data.ApiService
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.launch
import org.json.JSONObject

class NewsWadhwaniViewModel : ViewModel() {

    private val _responseAuthToken = MutableLiveData<JsonObject>()
    val responseAuthToken: LiveData<JsonObject> = _responseAuthToken

    private val _responseNewsWadhwani = MutableLiveData<JsonObject>()
    val responseNewsWadhwani: LiveData<JsonObject> = _responseNewsWadhwani

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getAuthenticationForNews(context: Context) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("email", "gis.pmu@mahapocra.gov.in")
                    put("password", "eD4WWvglRwRi")
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    "https://ianm-preprod.wadhwaniai.org/",
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                val response = apiRequest.authenticationForNews(requestBody)
                _responseAuthToken.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }

    fun getNewsWadhwani(context: Context, bearerToken: String) {
        viewModelScope.launch {
            try {
                val api = AppInventorApi(
                    context,
                    "https://ianm-preprod.wadhwaniai.org/",
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                val response = apiRequest.eventsForNews("Bearer $bearerToken", "Maharashtra", false)
                _responseNewsWadhwani.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }
}
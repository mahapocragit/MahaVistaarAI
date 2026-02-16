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
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject

class GisViewModel : ViewModel() {

    private val _shcInformationResponse = MutableLiveData<JsonObject>()
    val shcInformationResponse: LiveData<JsonObject> = _shcInformationResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchSoilHealthCardDetailsFromSHCNumber(context: Context, shcID: String, language: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val lang = if (language =="mr") language else "eng"
                val jsonObject = JSONObject().apply {
                        put("language", lang)
                        put("shc_no", shcID)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.GIS.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getSoilHealthCardDetailsFromSHCNumber(requestBody)
                ProgressHelper.disableProgressDialog()
                _shcInformationResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
                e.printStackTrace()
                _error.value = "JSON Error: ${e.localizedMessage}"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}
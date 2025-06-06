package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.gov.mahapocra.mahavistaarai.data.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class GisViewModel : ViewModel() {


    private val _shcInformationResponse = MutableLiveData<JsonObject>()
    val shcInformationResponse: LiveData<JsonObject> = _shcInformationResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchSoilHealthCardDetailsFromSHCNumber(context: Context, shcID: String, language: String) {
        viewModelScope.launch {
            try {
                val lang = if (language =="mr") language else "eng"
                val jsonObject = JSONObject().apply {
                        put("language", lang)
                        put("shc_no", shcID)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.GIS.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getSoilHealthCardDetailsFromSHCNumber(requestBody)
                _shcInformationResponse.value = response
            } catch (e: JSONException) {
                e.printStackTrace()
                _error.value = "JSON Error: ${e.localizedMessage}"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

}
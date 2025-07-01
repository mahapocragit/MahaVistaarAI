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
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit

class DbtSchemesViewModel : ViewModel() {

    private val _responseUrlDbtSchemes = MutableLiveData<JsonObject>()
    val responseUrlDbtSchemes: LiveData<JsonObject> = _responseUrlDbtSchemes

    private val _responseUrlMahaDbtSchemes = MutableLiveData<JsonObject>()
    val responseUrlMahaDbtSchemes: LiveData<JsonObject> = _responseUrlMahaDbtSchemes

    private val _retrieveFarmerToken = MutableLiveData<JsonObject>()
    val retrieveFarmerToken: LiveData<JsonObject> = _retrieveFarmerToken

    private val _retrieveFarmerData = MutableLiveData<JsonObject>()
    val retrieveFarmerData: LiveData<JsonObject> = _retrieveFarmerData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getDBTSchemes(context: Context) {
        viewModelScope.launch {
            try {
                val api =
                    AppInventorApi(
                        context,
                        AppEnvironment.UAT_DBT.baseUrl,
                        "",
                        AppString(context).getkMSG_WAIT(),
                        false
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                // Retrofit suspend call
                val response = apiRequest.getDBTSchemes()
                _responseUrlDbtSchemes.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getMahaDBTSchemes(context: Context) {
        viewModelScope.launch {
            try {
                val api =
                    AppInventorApi(
                        context,
                        AppEnvironment.UAT_DBT.baseUrl,
                        "",
                        AppString(context).getkMSG_WAIT(),
                        false
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                // Retrofit suspend call
                val response = apiRequest.getMahaDBTSchemes()
                _responseUrlMahaDbtSchemes.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun retrieveFarmerTokenFromID(context: Context, farmerId:String) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("FarmerID", farmerId)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        context,
                        AppEnvironment.UAT_DBT.baseUrl,
                        "",
                        AppString(context).getkMSG_WAIT(),
                        false
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                // Retrofit suspend call
                val response = apiRequest.retrieveFarmerToken(requestBody)
                _retrieveFarmerToken.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun retrieveFarmerDataFromToken(context: Context, correlationId:String) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("CorrelationId", correlationId)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        context,
                        AppEnvironment.UAT_DBT.baseUrl,
                        "",
                        AppString(context).getkMSG_WAIT(),
                        false
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                // Retrofit suspend call
                val response = apiRequest.retrieveFarmerData(requestBody)
                _retrieveFarmerData.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}
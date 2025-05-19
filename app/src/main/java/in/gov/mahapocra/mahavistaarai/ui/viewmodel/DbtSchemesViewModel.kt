package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.model.SchemeDataModel
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class DbtSchemesViewModel : ViewModel(){

    private val _responseUrlDbtSchemes = MutableLiveData<JsonObject>()
    val responseUrlDbtSchemes: LiveData<JsonObject> = _responseUrlDbtSchemes

    private val _responseUrlMahaDbtSchemes = MutableLiveData<List<SchemeDataModel>>()
    val responseUrlMahaDbtSchemes: LiveData<List<SchemeDataModel>> = _responseUrlMahaDbtSchemes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getDBTSchemes(context: Context) {
        viewModelScope.launch {
            try {
                val api =
                    AppInventorApi(
                        context,
                        "https://uat-dbt.mahapocra.gov.in:8026/",
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
            }
        }
    }

    fun getMahaDBTSchemes(context: Context) {
        viewModelScope.launch {
            try {
                val api =
                    AppInventorApi(
                        context,
                        "https://uat-dbt.mahapocra.gov.in:8026/",
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
            }
        }
    }
}
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
import `in`.gov.mahapocra.mahavistaarai.data.model.Category
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject

class NewsWadhwaniViewModel : ViewModel() {

    private val _responseAuthToken = MutableLiveData<JsonObject>()
    val responseAuthToken: LiveData<JsonObject> = _responseAuthToken

    private val _responseNewsWadhwani = MutableLiveData<JsonObject>()
    val responseNewsWadhwani: LiveData<JsonObject> = _responseNewsWadhwani


    private val _getNewsCategoriesResponse = MutableLiveData<JsonObject>()
    val getNewsCategoriesResponse: LiveData<JsonObject> = _getNewsCategoriesResponse

    private val _getNewsSubCategoriesResponse = MutableLiveData<JsonObject>()
    val getNewsSubCategoriesResponse: LiveData<JsonObject> = _getNewsSubCategoriesResponse

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
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.PANI_FOUNDATION.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                val response = apiRequest.authenticationForNews(requestBody)
                _responseAuthToken.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getNewsWadhwani(bearerToken: String, offset: Int, dateSevenDaysAgo: String, currentDateTime: String, category: String, subCategory: String) {
        viewModelScope.launch {
            try {
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.PANI_FOUNDATION.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                val response =
                    apiRequest.eventsForNews("Bearer $bearerToken", "Maharashtra", offset,dateSevenDaysAgo, currentDateTime,  false, category, subCategory)
                _responseNewsWadhwani.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getNewsCategories(bearerToken: String){
        viewModelScope.launch {
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.PANI_FOUNDATION.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.fetchNewsCategories("Bearer $bearerToken")
                _getNewsCategoriesResponse.value = response
            }catch (e: Exception){
                _error.value = e.localizedMessage ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getNewsSubCategories(bearerToken: String, category: String){
        viewModelScope.launch {
            val jsonObject = JSONObject().apply {
                put("category", category)
            }
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.PANI_FOUNDATION.baseUrl)
            val apiRequest = retrofit.create(ApiService::class.java)
            val response = apiRequest.fetchNewsSubCategories(bearerToken, category)
            _getNewsSubCategoriesResponse.value = response
        }
    }
}
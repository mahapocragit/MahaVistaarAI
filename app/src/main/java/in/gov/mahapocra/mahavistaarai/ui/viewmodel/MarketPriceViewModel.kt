package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class MarketPriceViewModel : ViewModel() {
    val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
    val apiRequest = retrofit.create(ApiService::class.java)

    private val _getMarketAndMarketNameResponse = MutableLiveData< UiState<JsonObject>>()
    val getMarketAndMarketNameResponse: LiveData<UiState<JsonObject>> = _getMarketAndMarketNameResponse

    private val _getMarketListResponse = MutableLiveData<UiState<JsonObject>>()
    val getMarketListResponse: LiveData<UiState<JsonObject>> = _getMarketListResponse

    private val _getMarketPriceDetailsResponse = MutableLiveData<UiState<JsonObject>>()
    val getMarketPriceDetailsResponse: LiveData<UiState<JsonObject>> = _getMarketPriceDetailsResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getMarketAndMarketName(districtID: Int, language: String) {
        viewModelScope.launch {
            _getMarketAndMarketNameResponse.value = UiState.Loading
            try {
                val response = apiRequest.getMarketAndMarketName(districtID, language)
                _getMarketAndMarketNameResponse.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _getMarketAndMarketNameResponse.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun fetchMarketList(languageToLoad: String, districtCode: Int) {
        viewModelScope.launch {
            _getMarketListResponse.value = UiState.Loading
            try {
                val response = apiRequest.getMarketList(languageToLoad, districtCode)
                _getMarketListResponse.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _getMarketListResponse.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getMarketPriceDetails(mandiId: Int, language: String) {
        viewModelScope.launch {
            _getMarketPriceDetailsResponse.value = UiState.Loading
            try {
                val response = apiRequest.getMarketPriceDetails(language, mandiId)
                _getMarketPriceDetailsResponse.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _getMarketPriceDetailsResponse.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

}
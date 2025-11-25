package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class DbtSchemesViewModel : ViewModel() {

    private val _responseUrlDbtSchemes = MutableLiveData<JsonObject>()
    val responseUrlDbtSchemes: LiveData<JsonObject> = _responseUrlDbtSchemes

    private val _responseUrlMahaDbtSchemes = MutableLiveData<JsonObject>()
    val responseUrlMahaDbtSchemes: LiveData<JsonObject> = _responseUrlMahaDbtSchemes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getDBTSchemes(context: Context) {
        viewModelScope.launch {
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.DBT_BASE_URL.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                // Retrofit suspend call
                val response = apiRequest.getDBTSchemes()
                _responseUrlDbtSchemes.value = response

            } catch (e: Exception) {
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

    fun getMahaDBTSchemes(context: Context) {
        viewModelScope.launch {
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.DBT_BASE_URL.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                // Retrofit suspend call
                val response = apiRequest.getMahaDBTSchemes()
                _responseUrlMahaDbtSchemes.value = response

            } catch (e: Exception) {
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
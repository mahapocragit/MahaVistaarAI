package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import com.microsoft.clarity.Clarity
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class MahavistaarViewModel : ViewModel(){

    private val _responseUrlForChatBot = MutableLiveData< UiState<JsonObject>>()
    val responseUrlForChatBot: LiveData<UiState<JsonObject>> = _responseUrlForChatBot

    fun requestUrlForChatBot(username: String, mobileNumber: String) {

        val username = CryptoHelper.decryptField(username)
        val mobileNumber = CryptoHelper.decryptField(mobileNumber)

        viewModelScope.launch {
            _responseUrlForChatBot.value = UiState.Loading
            try {
                val jsonObject = JSONObject().apply {
                    put("name", username)
                    put("mobile", mobileNumber)
                    put("role", "public")
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.VISTAAR.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                Clarity.sendCustomEvent("JWT_SESSION_START")
                val response = apiRequest.requestForChatBotURL(requestBody)
                _responseUrlForChatBot.value = UiState.Success(response)

            }catch (e: Exception) {
                Clarity.sendCustomEvent("JWT_SESSION_STOPPED")
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _responseUrlForChatBot.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}
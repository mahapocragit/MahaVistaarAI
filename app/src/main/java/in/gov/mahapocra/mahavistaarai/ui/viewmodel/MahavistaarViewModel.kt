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
import `in`.gov.mahapocra.mahavistaarai.data.ApiService
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.launch
import org.json.JSONObject

class MahavistaarViewModel : ViewModel(){

    private val _responseUrlForChatBot = MutableLiveData<JsonObject>()
    val responseUrlForChatBot: LiveData<JsonObject> = _responseUrlForChatBot

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun requestUrlForChatBot(context: Context) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("mobile", "8104131734")
                    put("name", "Siddhesh Bhatkar")
                    put("role", "public")
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    "https://vistaar.maharashtra.gov.in/",
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                val response = apiRequest.requestForChatBotURL(requestBody)
                _responseUrlForChatBot.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }
}
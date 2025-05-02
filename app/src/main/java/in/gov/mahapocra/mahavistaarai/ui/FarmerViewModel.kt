package `in`.gov.mahapocra.mahavistaarai.ui

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class FarmerViewModel : ViewModel() {

    private val _saveFarmerSelectedCrop = MutableLiveData<JsonObject>()
    val saveFarmerSelectedCrop: LiveData<JsonObject> = _saveFarmerSelectedCrop

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun saveFarmerSelectedCrop(context: Context, sowingDate: String, cropId: Int) {
        val jsonObject = JSONObject()
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        val isGuest = AppSettings.getInstance().getBooleanValue(context, AppConstants.IS_USER_GUEST, false)
        val userValue = if (isGuest) 1 else 0
        try {
            jsonObject.put("api_key", APIKeys.SSO_PROD.key())
            jsonObject.put("farmer_id", farmerId)
            jsonObject.put("sowing_date", sowingDate)
            jsonObject.put("crop_id", cropId)
            jsonObject.put("is_guest", userValue)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(context, APIServices.FARMER, "", AppString(context).getkMSG_WAIT(), false)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)

            apiRequest.kSaveFarmerSelectedCrop(requestBody)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            _saveFarmerSelectedCrop.value = response.body()
                        } else {
                            _error.value = "API Error: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        _error.value = t.localizedMessage
                    }
                })

        } catch (e: JSONException) {
            _error.value = "JSON Error: ${e.message}"
        }
    }
}

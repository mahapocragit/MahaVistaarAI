package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import android.os.Handler
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
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class FarmerViewModel : ViewModel() {

    private val _saveFarmerSelectedCrop = MutableLiveData<JsonObject>()
    val saveFarmerSelectedCrop: LiveData<JsonObject> = _saveFarmerSelectedCrop

    private val _getFarmerSelectedCrop = MutableLiveData<JsonObject>()
    val getFarmerSelectedCrop: LiveData<JsonObject> = _getFarmerSelectedCrop

    private val _deleteFarmerSelectedCrop = MutableLiveData<JsonObject>()
    val deleteFarmerSelectedCrop: LiveData<JsonObject> = _deleteFarmerSelectedCrop

    private val _cropCategoryResponse = MutableLiveData<JsonObject>()
    val cropCategoryResponse: LiveData<JsonObject> = _cropCategoryResponse

    private val _talukaList = MutableLiveData<JsonObject>()
    val talukaList: LiveData<JsonObject> = _talukaList

    private val _weatherResponse = MutableLiveData<JsonObject>()
    val weatherResponse: LiveData<JsonObject> = _weatherResponse

    private val _userDetailsResponse = MutableLiveData<JsonObject>()
    val userDetailsResponse: LiveData<JsonObject> = _userDetailsResponse

    private val _videosResponse = MutableLiveData<JsonObject>()
    val videosResponse: LiveData<JsonObject> = _videosResponse

    private val _sopResponse = MutableLiveData<JsonObject>()
    val sopResponse: LiveData<JsonObject> = _sopResponse

    private val _chcCentersResponse = MutableLiveData<JsonObject>()
    val chcCentersResponse: LiveData<JsonObject> = _chcCentersResponse

    private val _fetchLocationDataFromCoordinates = MutableLiveData<JsonObject>()
    val fetchLocationDataFromCoordinates: LiveData<JsonObject> = _fetchLocationDataFromCoordinates

    private val _responseMarkerList = MutableLiveData<JsonObject>()
    val responseMarkerList: LiveData<JsonObject> = _responseMarkerList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun saveFarmerSelectedCrop(context: Context, sowingDate: String, cropId: Int) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("api_key", APIKeys.SSO_PROD)
                    put(
                        "farmer_id",
                        AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                    )
                    put("sowing_date", sowingDate)
                    put("crop_id", cropId)
                    put(
                        "is_guest",
                        if (AppSettings.getInstance()
                                .getBooleanValue(context, AppConstants.IS_USER_GUEST, false)
                        ) 1 else 0
                    )
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                val response = apiRequest.kSaveFarmerSelectedCrop(requestBody)
                _saveFarmerSelectedCrop.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }

    fun getFarmerSelectedCrop(context: Context, language: String?) {
        viewModelScope.launch {
            val farmerId =
                AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
            val jsonObject = JSONObject()
            try {
                jsonObject.put("api_key", APIKeys.SSO_PROD)
                jsonObject.put("lang", language)
                jsonObject.put("farmer_id", farmerId)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                try {
                    val response = withContext(Dispatchers.IO) {
                        apiRequest.getFarmersSelectedCrop(requestBody)
                    }
                    _getFarmerSelectedCrop.value = response
                } catch (e: Exception) {
                    _error.value = "Error: ${e.localizedMessage}"
                }

            } catch (e: JSONException) {
                e.printStackTrace()
                _error.value = "JSON Error: ${e.localizedMessage}"
            }
        }
    }

    fun deleteFarmerSelectedCrop(context: Context, cropId: Int) {
        viewModelScope.launch {
            try {
                val farmerId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("api_key", APIKeys.SSO_PROD)
                    put("crop_id", cropId)
                    put("farmer_id", farmerId)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.deleteSelectedCrop(requestBody)

                // You can handle the result however you want, for example:
                _deleteFarmerSelectedCrop.value =
                    response // or create a separate LiveData if needed

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error occurred"
            }
        }
    }

    fun getCropCategoriesAndCropDetails(context: Context, languageToLoad: String) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("api_key", APIKeys.SSO_PROD)
                    put("lang", languageToLoad)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                // Suspend call
                val response = apiRequest.getCropCategoryWise(requestBody)

                // Handle the response (success case)
                _cropCategoryResponse.value = response // ← use appropriate LiveData

            } catch (e: Exception) {
                // Handle error case
                _error.value = e.localizedMessage ?: "Unknown error occurred"
            }
        }
    }

    fun fetchTalukaMasterData(context: Context, languageToLoad: String) {
        viewModelScope.launch {
            try {
                val districtID =
                    AppSettings.getInstance().getIntValue(context, AppConstants.uDISTId, 0)

                val jsonObject = JSONObject().apply {
                    put("lang", languageToLoad)
                    put("district_code", districtID)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )
                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                // This is the suspend function call
                val response = apiRequest.getTalukaList(requestBody)

                // Handle success
                _talukaList.value = response // <- your LiveData for the UI

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Something went wrong"
            }
        }
    }

    fun fetchWeatherDetails(context: Context, talukaID: Int, languageToLoad: String) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("taluka_code", talukaID)
                    put("lang", languageToLoad)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )

                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                // Suspend API call
                val response = apiRequest.getWeatherDetails(requestBody)

                // Post the response to LiveData
                _weatherResponse.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Weather fetch failed"
            }
        }
    }

    fun fetchUserInformation(context: Context) {
        viewModelScope.launch {
            try {
                val farmerId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)

                val jsonObject = JSONObject().apply {
                    put("SecurityKey", APIServices.SSO_KEY)
                    put("FAAPRegistrationID", farmerId)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )

                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.getGetRegistration(requestBody)

                // Handle success
                _userDetailsResponse.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to fetch user details"
            }
        }
    }

    fun getVideosForFarmer(context: Context) {
        viewModelScope.launch {
            try {
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )

                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getFarmersVideosJson()
                _videosResponse.value = response
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to fetch videos"
            }
        }
    }

    fun fetchSOPDate(context: Context, cropId: Int) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("api_key", APIServices.SSO_KEY)
                    put("crop_id", cropId)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    context,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(context).getkMSG_WAIT(),
                    false
                )

                val retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.getSOPByList(requestBody)

                // Handle success
                _sopResponse.value = response

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to fetch user details"
            }
        }
    }

    fun fetchDataForCHC(context: Context, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("api_key", APIServices.SSO_KEY)
                jsonObject.put("lat", latitude)
                jsonObject.put("lon", longitude)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        context,
                        AppEnvironment.FARMER.baseUrl,
                        "",
                        AppString(context).getkMSG_WAIT(),
                        false
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCHCInformation(requestBody)
                _chcCentersResponse.value = response
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to fetch user details"
            }
        }
    }

    fun fetchLocationDataFromCoordinates(context: Context, latitude: Double, longitude: Double){
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lat", latitude)
                jsonObject.put("lon", longitude)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        context,
                        AppEnvironment.FARMER.baseUrl,
                        "",
                        AppString(context).getkMSG_WAIT(),
                        false
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCodeFromCoordinates(requestBody)
                _fetchLocationDataFromCoordinates.value = response
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to fetch user details"
            }
        }
    }

    fun fetchMarketList(context: Context, languageToLoad: String, districtCode:Int){
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lang", languageToLoad)
                jsonObject.put("api_key", APIKeys.SSO_PROD)
                jsonObject.put("district_code", districtCode)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        context,
                        AppEnvironment.FARMER.baseUrl,
                        "",
                        AppString(context).getkMSG_WAIT(),
                        false
                    )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getMarketList(requestBody)
                _responseMarkerList.value = response
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to fetch user details"
            }
        }
    }
}

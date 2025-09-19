package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

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

    private val _getDigitalShetishalaScheduleResponse = MutableLiveData<JsonObject>()
    val getDigitalShetishalaScheduleResponse: LiveData<JsonObject> =
        _getDigitalShetishalaScheduleResponse

    private val _sopResponse = MutableLiveData<JsonObject>()
    val sopResponse: LiveData<JsonObject> = _sopResponse

    private val _chcCentersResponse = MutableLiveData<JsonObject>()
    val chcCentersResponse: LiveData<JsonObject> = _chcCentersResponse

    private val _fetchLocationDataFromCoordinates = MutableLiveData<JsonObject>()
    val fetchLocationDataFromCoordinates: LiveData<JsonObject> = _fetchLocationDataFromCoordinates

    private val _responseMarkerList = MutableLiveData<JsonObject>()
    val responseMarkerList: LiveData<JsonObject> = _responseMarkerList

    private val _compareOtpResponse = MutableLiveData<JsonObject>()
    val compareOtpResponse: LiveData<JsonObject> = _compareOtpResponse

    private val _compareOtpResponseReg = MutableLiveData<JsonObject>()
    val compareOtpResponseReg: LiveData<JsonObject> = _compareOtpResponseReg

    private val _shetishalaVideosResponse = MutableLiveData<JsonObject>()
    val shetishalaVideosResponse: LiveData<JsonObject> = _shetishalaVideosResponse

    private val _agristackLoginResponse = MutableLiveData<JsonObject>()
    val agristackLoginResponse: LiveData<JsonObject> = _agristackLoginResponse

    private val _getCropStagesAndAdvisoryResponse = MutableLiveData<JsonObject>()
    val getCropStagesAndAdvisoryResponse: LiveData<JsonObject> = _getCropStagesAndAdvisoryResponse

    private val _getClimateResilientListResponse = MutableLiveData<JsonObject>()
    val getClimateResilientListResponse: LiveData<JsonObject> = _getClimateResilientListResponse

    private val _getCropStagesResponse = MutableLiveData<JsonObject>()
    val getCropStagesResponse: LiveData<JsonObject> = _getCropStagesResponse
    private val _getPestDiseaseDetailsResponse = MutableLiveData<JSONObject>()
    val getPestDiseaseDetailsResponse: LiveData<JSONObject> = _getPestDiseaseDetailsResponse
    private val _getCropSapAdvisoryResponse = MutableLiveData<JsonObject>()
    val getCropSapAdvisoryResponse: LiveData<JsonObject> = _getCropSapAdvisoryResponse

    private val _getNotificationResponse = MutableLiveData<JsonObject>()
    val getNotificationResponse: LiveData<JsonObject> = _getNotificationResponse
    private val _getNotificationDetailedResponse = MutableLiveData<JsonObject>()
    val getNotificationDetailedResponse: LiveData<JsonObject> = _getNotificationDetailedResponse
    private val _updateNotificationStatusResponse = MutableLiveData<JsonObject>()
    val updateNotificationStatusResponse: LiveData<JsonObject> = _updateNotificationStatusResponse
    private val _updateFCMTokenResponse = MutableLiveData<JsonObject>()
    val updateFCMTokenResponse: LiveData<JsonObject> = _updateFCMTokenResponse
    private val _checkFCMTokenResponse = MutableLiveData<JsonObject>()
    val checkFCMTokenResponse: LiveData<JsonObject> = _checkFCMTokenResponse

    private val _warehouseDetailsResponse = MutableLiveData<JsonObject>()
    val warehouseDetailsResponse: LiveData<JsonObject> = _warehouseDetailsResponse
    private val _districtIdResponse = MutableLiveData<JsonObject>()
    val districtIdResponse: LiveData<JsonObject> = _districtIdResponse
    private val _consentResponse = MutableLiveData<JsonObject>()
    val consentResponse: LiveData<JsonObject> = _consentResponse

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
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // Retrofit suspend call
                val response = apiRequest.kSaveFarmerSelectedCrop(requestBody)
                _saveFarmerSelectedCrop.value = response

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
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                try {
                    val response = withContext(Dispatchers.IO) {
                        apiRequest.getFarmersSelectedCrop(requestBody)
                    }
                    _getFarmerSelectedCrop.value = response
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
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.deleteSelectedCrop(requestBody)

                // You can handle the result however you want, for example:
                _deleteFarmerSelectedCrop.value =
                    response // or create a separate LiveData if needed

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

    fun getCropCategoriesAndCropDetails(context: Context, languageToLoad: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject().apply {
                    put("api_key", APIKeys.SSO_PROD)
                    put("lang", languageToLoad)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // Suspend call
                val response = apiRequest.getCropCategoryWise(requestBody)
                ProgressHelper.disableProgressDialog()
                // Handle the response (success case)
                _cropCategoryResponse.value = response // ← use appropriate LiveData

            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
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
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // This is the suspend function call
                val response = apiRequest.getTalukaList(requestBody)

                // Handle success
                _talukaList.value = response // <- your LiveData for the UI

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

    fun fetchWeatherDetails(context: Context, talukaID: Int, languageToLoad: String) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("taluka_code", talukaID)
                    put("lang", languageToLoad)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                // Suspend API call
                val response = apiRequest.getWeatherDetails(requestBody)

                // Post the response to LiveData
                _weatherResponse.value = response

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

    fun fetchUserInformation(context: Context, farmerRegistrationID: Int) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", ApiConstants.SSO_KEY)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.getGetRegistration(farmerRegistrationID, requestBody)

                // Handle success
                _userDetailsResponse.value = response

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

    fun getVideosForFarmer(context: Context) {
        viewModelScope.launch {
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getFarmersVideosJson()
                _videosResponse.value = response
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

    fun getDigitalShetishalaSchedule(context: Context) {
        viewModelScope.launch {
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getDigitalShetishalaSchedule()
                _getDigitalShetishalaScheduleResponse.value = response
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

    fun fetchSOPDate(context: Context, cropId: Int) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("api_key", ApiConstants.SSO_KEY)
                    put("crop_id", cropId)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.getSOPByList(requestBody)

                // Handle success
                _sopResponse.value = response

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

    fun fetchDataForCHC(context: Context, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("api_key", ApiConstants.SSO_KEY)
                jsonObject.put("lat", latitude)
                jsonObject.put("lon", longitude)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCHCInformation(requestBody)
                _chcCentersResponse.value = response
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

    fun fetchLocationDataFromCoordinates(context: Context, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lat", latitude)
                jsonObject.put("lon", longitude)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCodeFromCoordinates(requestBody)
                _fetchLocationDataFromCoordinates.value = response
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

    fun fetchMarketList(context: Context, languageToLoad: String, districtCode: Int) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lang", languageToLoad)
                jsonObject.put("api_key", APIKeys.SSO_PROD)
                jsonObject.put("district_code", districtCode)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getMarketList(requestBody)
                ProgressHelper.disableProgressDialog()
                _responseMarkerList.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
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

    fun compareOtp(context: Context, mobile: String, enteredOTP: String) {
        viewModelScope.launch {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                jsonObject.put("otp", enteredOTP)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.compareOtp(mobile.trim { it <= ' ' }, requestBody)
                _compareOtpResponse.value = response
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

    fun compareOtpReg(context: Context, mobile: String, enteredOTP: String) {
        viewModelScope.launch {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                jsonObject.put("otp", enteredOTP)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.compareOtpReg(mobile.trim { it <= ' ' }, requestBody)
                _compareOtpResponseReg.value = response
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

    fun getShetishalaVideos(context: Context) {
        viewModelScope.launch {
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getShetishalaVideos()
                _shetishalaVideosResponse.value = response
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

    fun farmerIdBasedLogin(context: Context, agristackID: String) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("SecurityKey", APIKeys.SSO_PROD)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.farmerLoginBasedOnID(agristackID, requestBody)

                // You can handle the result however you want, for example:
                _agristackLoginResponse.value = response

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

    fun getCropStagesAndAdvisory(
        context: Context,
        cropId: Int?,
        sowingDate: String,
        language: String
    ) {
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("crop_id", cropId)
                jsonObject.put("farmer_id", farmerId)
                jsonObject.put("sowing_date", sowingDate)
                jsonObject.put("lang", language)
                Log.d("TAGGER", "getCropStagesAndAdvisory jsonObject: $jsonObject")
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                try {
                    val response = apiRequest.getCropStagesAndAdvisory(requestBody)
                    _getCropStagesAndAdvisoryResponse.value = response
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

            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _error.value = message
                FirebaseCrashlytics.getInstance().recordException(e)
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

    fun climateResilientGroupList(context: Context, languageToLoad: String) {
        viewModelScope.launch {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("api_key", ApiConstants.SSO_KEY)
                jsonObject.put("lang", languageToLoad)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getClimateResilientList(requestBody)
                _getClimateResilientListResponse.value = response
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

    fun getCropStages(context: Context, cropId: Int?, language: String) {
        viewModelScope.launch {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("crop_id", cropId)
                jsonObject.put("lang", language)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCropStages(requestBody)
                _getCropStagesResponse.value = response
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

    fun showPestDiseaseDetails(context: Context, pestId: Int) {
        viewModelScope.launch {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("pdid", pestId)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val rawResponse = apiRequest.getPestDiseaseDetails(requestBody)
                val jsonString = rawResponse.string()
                val jsonObject =
                    JSONObject(jsonString) // OR Gson().fromJson(jsonString, JsonObject::class.java)
                _getPestDiseaseDetailsResponse.value = jsonObject
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

    fun getCropSapAdvisory(context: Context, villageCode: Int) {
        viewModelScope.launch {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("village_code", villageCode.toString())
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCropSapAdvisory(requestBody)
                _getCropSapAdvisoryResponse.value = response
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

    fun getNotificationList(context: Context) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            val farmerId =
                AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getNotificationList(farmerId)
                ProgressHelper.disableProgressDialog()
                _getNotificationResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
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

    fun getNotificationDetails(context: Context, notificationID: Long) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            val jsonObject = JSONObject().apply {
                put("notification_id", notificationID)
            }
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getNotificationDetails(requestBody)
                ProgressHelper.disableProgressDialog()
                _getNotificationDetailedResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
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

    fun updateNotificationStatus(context: Context, notificationID: Long) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            val jsonObject = JSONObject().apply {
                put("notification_id", notificationID)
            }
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.updateNotificationStatus(requestBody)
                ProgressHelper.disableProgressDialog()
                _updateNotificationStatusResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
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

    fun updateFCMToken(context: Context, fcmToken: String) {
        ProgressHelper.showProgressDialog(context)
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.updateFCMToken(farmerId, fcmToken)
                ProgressHelper.disableProgressDialog()
                val jsonString = """{"status": 200,"response": "FCM Cleared"}""".trimIndent()
                val jsonObject: JsonObject = JsonParser.parseString(jsonString).asJsonObject
                if (fcmToken == "NA") {
                    _updateFCMTokenResponse.value = jsonObject
                } else {
                    _updateFCMTokenResponse.value = response
                }
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
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

    fun validateFCMToken(context: Context) {
        ProgressHelper.showProgressDialog(context)
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.checkFcmToken(farmerId)
                ProgressHelper.disableProgressDialog()
                _checkFCMTokenResponse.value = response
            } catch (e: Exception) {
                ProgressHelper.disableProgressDialog()
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

    fun fetchWarehouseData(context: Context, districtID: Int, languageToLoad: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("api_key", APIKeys.SSO_PROD)
                jsonObject.put("district_code", districtID)
                jsonObject.put("lang", languageToLoad)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getWareHouseDetails(requestBody)
                ProgressHelper.disableProgressDialog()
                _warehouseDetailsResponse.value = response
            } catch (e: JSONException) {
                ProgressHelper.disableProgressDialog()
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

    fun getDistrictData(context: Context, languageToLoad: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("lang", languageToLoad)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getDistrictList(requestBody)
                ProgressHelper.disableProgressDialog()
                _districtIdResponse.value = response
            } catch (e: JSONException) {
                ProgressHelper.disableProgressDialog()
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

    fun updateConsent(context: Context, consentValue: Boolean) {
        ProgressHelper.showProgressDialog(context)
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.updateConsent(farmerId, consentValue)
                ProgressHelper.disableProgressDialog()
                _consentResponse.value = response
            } catch (e: JSONException) {
                ProgressHelper.disableProgressDialog()
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

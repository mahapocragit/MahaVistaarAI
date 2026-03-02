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
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class FarmerViewModel : ViewModel() {

    private val _saveFarmerSelectedCrop = MutableLiveData<JsonObject>()
    val saveFarmerSelectedCrop: LiveData<JsonObject> = _saveFarmerSelectedCrop

    private val _getFarmerSelectedCrop = MutableLiveData<UiState<JsonObject>>()
    val getFarmerSelectedCrop: LiveData<UiState<JsonObject>> = _getFarmerSelectedCrop

    private val _deleteFarmerSelectedCrop = MutableLiveData<UiState<JsonObject>>()
    val deleteFarmerSelectedCrop: LiveData<UiState<JsonObject>> = _deleteFarmerSelectedCrop

    private val _cropCategoryResponse = MutableLiveData<JsonObject>()
    val cropCategoryResponse: LiveData<JsonObject> = _cropCategoryResponse

    private val _talukaList = MutableLiveData<JsonObject>()
    val talukaList: LiveData<JsonObject> = _talukaList

    private val _weatherResponse = MutableLiveData<UiState<JsonObject>>()
    val weatherResponse: LiveData<UiState<JsonObject>> = _weatherResponse

    private val _videosResponse = MutableLiveData<JsonObject>()
    val videosResponse: LiveData<JsonObject> = _videosResponse

    private val _getDigitalShetishalaScheduleResponse = MutableLiveData<JsonObject>()
    val getDigitalShetishalaScheduleResponse: LiveData<JsonObject> =
        _getDigitalShetishalaScheduleResponse

    private val _sopResponse = MutableLiveData<JsonObject>()
    val sopResponse: LiveData<JsonObject> = _sopResponse

    private val _chcCentersResponse = MutableLiveData<JsonObject>()
    val chcCentersResponse: LiveData<JsonObject> = _chcCentersResponse

    private val _shetishalaVideosResponse = MutableLiveData<JsonObject>()
    val shetishalaVideosResponse: LiveData<JsonObject> = _shetishalaVideosResponse

    private val _getCropStagesAndAdvisoryResponse = MutableLiveData<JsonObject>()
    val getCropStagesAndAdvisoryResponse: LiveData<JsonObject> = _getCropStagesAndAdvisoryResponse

    private val _getClimateResilientListResponse = MutableLiveData<JsonObject>()
    val getClimateResilientListResponse: LiveData<JsonObject> = _getClimateResilientListResponse

    private val _getCropStagesResponse = MutableLiveData<JsonObject>()
    val getCropStagesResponse: LiveData<JsonObject> = _getCropStagesResponse

    private val _getPestDiseaseDetailsResponse = MutableLiveData<JsonObject>()
    val getPestDiseaseDetailsResponse: LiveData<JsonObject> = _getPestDiseaseDetailsResponse

    private val _getCropSapAdvisoryResponse = MutableLiveData<UiState<JsonObject>>()
    val getCropSapAdvisoryResponse: LiveData<UiState<JsonObject>> = _getCropSapAdvisoryResponse

    private val _getNotificationResponse = MutableLiveData<UiState<JsonObject>>()
    val getNotificationResponse: LiveData<UiState<JsonObject>> = _getNotificationResponse

    private val _getNotificationDetailedResponse = MutableLiveData<JsonObject>()
    val getNotificationDetailedResponse: LiveData<JsonObject> = _getNotificationDetailedResponse

    private val _updateNotificationStatusResponse = MutableLiveData<JsonObject>()
    val updateNotificationStatusResponse: LiveData<JsonObject> = _updateNotificationStatusResponse

    private val _addNotificationFeedbackResponse = MutableLiveData<JsonObject>()
    val addNotificationFeedbackResponse: LiveData<JsonObject> = _addNotificationFeedbackResponse

    private val _updateFCMTokenResponse = MutableLiveData< UiState<JsonObject>>()
    val updateFCMTokenResponse: LiveData<UiState<JsonObject>> = _updateFCMTokenResponse

    private val _checkFCMTokenResponse = MutableLiveData<UiState<JsonObject>>()
    val checkFCMTokenResponse: LiveData<UiState<JsonObject>> = _checkFCMTokenResponse

    private val _warehouseDetailsResponse = MutableLiveData<JsonObject>()
    val warehouseDetailsResponse: LiveData<JsonObject> = _warehouseDetailsResponse

    private val _districtIdResponse = MutableLiveData<JsonObject>()
    val districtIdResponse: LiveData<JsonObject> = _districtIdResponse

    private val _consentResponse = MutableLiveData<UiState<JsonObject>>()
    val consentResponse: LiveData<UiState<JsonObject>> = _consentResponse

    private val _saveSubscribedTopicResponse = MutableLiveData<JsonObject>()
    val saveSubscribedTopicResponse: LiveData<JsonObject> = _saveSubscribedTopicResponse

    private val _deleteSubscribedTopicResponse = MutableLiveData<JsonObject>()
    val deleteSubscribedTopicResponse: LiveData<JsonObject> = _deleteSubscribedTopicResponse


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun saveFarmerSelectedCrop(context: Context, sowingDate: String, cropId: Int) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
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
                ProgressHelper.disableProgressDialog()
                _saveFarmerSelectedCrop.value = response

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

    fun getFarmerSelectedCrop(farmerId: Int, language: String?) {
        viewModelScope.launch {
            _getFarmerSelectedCrop.value = UiState.Loading
            try {
                val jsonObject = JSONObject()
                jsonObject.put("api_key", APIKeys.SSO_PROD)
                jsonObject.put("lang", language)
                jsonObject.put("farmer_id", farmerId)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getFarmersSelectedCrop(requestBody)
                _getFarmerSelectedCrop.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _getFarmerSelectedCrop.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun deleteFarmerSelectedCrop(farmerId: Int, cropId: Int) {
        viewModelScope.launch {
            try {
                _deleteFarmerSelectedCrop.value = UiState.Loading
                val jsonObject = JSONObject().apply {
                    put("api_key", APIKeys.SSO_PROD)
                    put("crop_id", cropId)
                    put("farmer_id", farmerId)
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)

                val response = apiRequest.deleteSelectedCrop(requestBody)
                _deleteFarmerSelectedCrop.value = UiState.Success(response)

            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _deleteFarmerSelectedCrop.value = UiState.Error(message)
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

    fun fetchWeatherDetails(talukaID: Int, languageToLoad: String) {
        viewModelScope.launch {
            _weatherResponse.value = UiState.Loading
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
                _weatherResponse.value = UiState.Success(response)

            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _weatherResponse.value = UiState.Error(message)
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
            ProgressHelper.showProgressDialog(context)
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getDigitalShetishalaSchedule()
                ProgressHelper.disableProgressDialog()
                _getDigitalShetishalaScheduleResponse.value = response
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

    fun getShetishalaVideos(context: Context) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getShetishalaVideos()
                ProgressHelper.disableProgressDialog()
                _shetishalaVideosResponse.value = response
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

    fun getCropStagesAndAdvisory(
        context: Context,
        cropId: Int?,
        sowingDate: String,
        language: String
    ) {
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("crop_id", cropId)
                jsonObject.put("farmer_id", farmerId)
                jsonObject.put("sowing_date", sowingDate)
                jsonObject.put("lang", language)
                Log.d(TAG, "getCropStagesAndAdvisory jsonObject: $jsonObject")
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCropStagesAndAdvisory(requestBody)
                ProgressHelper.disableProgressDialog()
                _getCropStagesAndAdvisoryResponse.value = response
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

    fun climateResilientGroupList(context: Context, languageToLoad: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("api_key", ApiConstants.SSO_KEY)
                jsonObject.put("lang", languageToLoad)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getClimateResilientList(requestBody)
                ProgressHelper.disableProgressDialog()
                _getClimateResilientListResponse.value = response
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

    fun getCropStages(context: Context, cropId: Int?, language: String) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("crop_id", cropId)
                jsonObject.put("lang", language)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCropStages(requestBody)
                ProgressHelper.disableProgressDialog()
                _getCropStagesResponse.value = response
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

    fun showPestDiseaseDetails(context: Context, pestId: Int) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject().apply { put("pdid", pestId) }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getPestDiseaseDetails(requestBody)
                ProgressHelper.disableProgressDialog()
                _getPestDiseaseDetailsResponse.value = response
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

    fun getCropSapAdvisory(villageCode: Int) {
        viewModelScope.launch {
            try {
                _getCropSapAdvisoryResponse.value = UiState.Loading
                val jsonObject = JSONObject()
                jsonObject.put("village_code", villageCode.toString())
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCropSapAdvisory(requestBody)
                _getCropSapAdvisoryResponse.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _getCropSapAdvisoryResponse.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getNotificationList(farmerId:Int) {
        viewModelScope.launch {
            try {
                _getNotificationResponse.value = UiState.Loading
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getNotificationList(farmerId)
                _getNotificationResponse.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _getNotificationResponse.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun getNotificationDetails(context: Context, notificationID: Long, notificationType: String?) {
        ProgressHelper.showProgressDialog(context)
        val userId =
            AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            val jsonObject = JSONObject().apply {
                put("notification_id", notificationID)
                put("user_id", userId)
                put("type", notificationType)
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

    fun updateNotificationStatus(context: Context, notificationID: Long, notificationType: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            val userId =
                AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
            val jsonObject = JSONObject().apply {
                put("notification_id", notificationID)
                put("type", notificationType)
                put("user_id", userId)
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

    fun updateNotificationStatusForChatbot(context: Context, notificationID: Long) {
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
                val response = apiRequest.updateNotificationStatusForChatbot(requestBody)
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

    fun addNotificationFeedback(
        context: Context,
        customId: String,
        type: String,
        feedbackArray: JSONArray
    ) {
        ProgressHelper.showProgressDialog(context)
        val userId =
            AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            val jsonObject = JSONObject().apply {
                put("user_id", userId)
                put("notification_id", customId)
                put("type", type)
                put("feedback", feedbackArray)
            }
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.addNotificationFeedback(requestBody)
                ProgressHelper.disableProgressDialog()
                _addNotificationFeedbackResponse.value = response
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

    fun updateFCMToken(farmerId: Int, fcmToken: String) = viewModelScope.launch {
        _updateFCMTokenResponse.value = UiState.Loading
        try {
            val api = RetrofitHelper
                .createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                .create(ApiService::class.java)
            val response = if (fcmToken == "NA") {
                JsonParser.parseString(
                    """{"status":200,"response":"FCM Cleared"}"""
                ).asJsonObject
            } else {
                api.updateFCMToken(farmerId, fcmToken)
            }
            _updateFCMTokenResponse.value = UiState.Success(response)
        } catch (e: Exception) {
            _updateFCMTokenResponse.value = UiState.Error(
                when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
            )
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun validateFCMToken(farmerId:Int) {
        viewModelScope.launch {
            _checkFCMTokenResponse.value = UiState.Loading
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.checkFcmToken(farmerId)
                _checkFCMTokenResponse.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _checkFCMTokenResponse.value = UiState.Error(message)
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

    fun updateConsent(farmerId:Int, consentValue: Boolean) {
        viewModelScope.launch {
            try {
                _consentResponse.value = UiState.Loading
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.updateConsent(farmerId, consentValue)
                _consentResponse.value = UiState.Success(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is SocketTimeoutException -> "Request timed out. Please try again."
                    is SocketException -> "Connection lost. Please check your internet."
                    is IOException -> "Network error occurred."
                    else -> e.localizedMessage ?: "Unknown error"
                }
                _consentResponse.value = UiState.Error(message)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun saveSubscribedTopic(context: Context, topic: String) {
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("user_id", farmerId)
                jsonObject.put("topic", topic)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.saveSubscribedTopic(requestBody)
                _saveSubscribedTopicResponse.value = response
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

    fun deleteSubscribedTopic(context: Context, topic: String) {
        val farmerId = AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("user_id", farmerId)
                jsonObject.put("topic", topic)
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val api = retrofit.create(ApiService::class.java)
                val response = api.deleteSubscribedTopic(requestBody)
                ProgressHelper.disableProgressDialog()
                _deleteSubscribedTopicResponse.value = response
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
}

package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.data.repository.PredictRepository
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class MainViewModel(private val repo: PredictRepository) : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result

    private val _getCropListResponse = MutableLiveData<String>()
    val getCropListResponse: LiveData<String> get() = _getCropListResponse

    private val _getStoreDataResponse = MutableLiveData<String>()
    val getStoreDataResponse: LiveData<String> get() = _getStoreDataResponse

    private val _feedbackResponse = MutableLiveData<String>()
    val feedbackResponse: LiveData<String> get() = _feedbackResponse

    private val _pestAdvisoryResponse = MutableLiveData<String>()
    val pestAdvisoryResponse: LiveData<String> get() = _pestAdvisoryResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun submit(context: Context, cropId: Int, crop: String, date: String, uri: Uri) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val response = repo.predict(cropId, crop, date, uri)
                if (response.isSuccessful) {
                    _result.value =
                        response.body()?.toString() ?: """{"success":true,"message":"Success"}"""
                } else {
                    val errorJson = response.errorBody()?.string()
                    _result.value = errorJson ?: """{"success":false,"message":"Unknown error"}"""
                }
            } catch (e: Exception) {
                _result.value = """{"success":false,"message":"${e.message}"}"""
            }
        }
    }

    fun submitFeedback(context: Context, responseId: Int, feedbackStr: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val response = repo.submitFeedback(responseId, feedbackStr)
                if (response.isSuccessful) {
                    ProgressHelper.disableProgressDialog()
                    _feedbackResponse.value =
                        response.body()?.toString() ?: """{"success":true,"message":"Success"}"""
                } else {
                    ProgressHelper.disableProgressDialog()
                    val errorJson = response.errorBody()?.string()
                    _feedbackResponse.value =
                        errorJson ?: """{"success":false,"message":"Unknown error"}"""
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

    fun fetchCropList() {
        viewModelScope.launch {
            try {
                val response = repo.fetchCropList()
                if (response.isSuccessful) {
                    _getCropListResponse.value =
                        response.body()?.toString() ?: """{"success":true,"message":"Success"}"""
                } else {
                    val errorJson = response.errorBody()?.string()
                    _getCropListResponse.value =
                        errorJson ?: """{"success":false,"message":"Unknown error"}"""
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

    fun storeResponse(
        farmerId: Int,
        cropId: String,
        sowingDate: String,
        isSuccess: Boolean,
        responseString: String,
        imageUri: Uri,
        diseaseId: String
    ) {
        viewModelScope.launch {
            try {
                val response = repo.storeResponse(
                    farmerId,
                    cropId,
                    sowingDate,
                    isSuccess,
                    responseString,
                    imageUri,
                    diseaseId
                )
                Log.d("TAGGER", "storeResponse: $response")
                if (response.isSuccessful) {
                    _getStoreDataResponse.value =
                        response.body()?.toString() ?: """{"success":true,"message":"Success"}"""
                } else {
                    val errorJson = response.errorBody()?.string()
                    _getStoreDataResponse.value =
                        errorJson ?: """{"success":false,"message":"Unknown error"}"""
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

    fun getPestAdvisory(context: Context, pestId: String) {
        ProgressHelper.showProgressDialog(context)
        viewModelScope.launch {
            try {
                val response = repo.getPestAdvisory(pestId)
                if (response.isSuccessful) {
                    ProgressHelper.disableProgressDialog()
                    _pestAdvisoryResponse.value =
                        response.body()?.toString() ?: """{"success":true,"message":"Success"}"""
                } else {
                    ProgressHelper.disableProgressDialog()
                    val errorJson = response.errorBody()?.string()
                    _pestAdvisoryResponse.value =
                        errorJson ?: """{"success":false,"message":"Unknown error"}"""
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
}
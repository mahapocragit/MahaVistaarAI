package `in`.gov.mahapocra.mahavistaarai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class ExpertsViewModel : ViewModel() {

    private val _uploadArticleResponse = MutableLiveData<JsonObject>()
    val uploadArticleResponse: LiveData<JsonObject> = _uploadArticleResponse
    private val _getCategoriesForExpertCorner = MutableLiveData<JsonObject>()
    val getCategoriesForExpertCorner: LiveData<JsonObject> = _getCategoriesForExpertCorner
    private val _getSubCategoriesForExpertCorner = MutableLiveData<JsonObject>()
    val getSubCategoriesForExpertCorner: LiveData<JsonObject> = _getSubCategoriesForExpertCorner
    private val _getUserArticlesResponse = MutableLiveData<JsonObject>()
    val getUserArticlesResponse: LiveData<JsonObject> = _getUserArticlesResponse
    private val _getAllArticlesResponse = MutableLiveData<JsonObject>()
    val getAllArticlesResponse: LiveData<JsonObject> = _getAllArticlesResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun uploadArticle(
        context: Context,
        file: MultipartBody.Part,
        title: String,
        description: String,
        category: String,
        subcategory: String
    ) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val farmerId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val titlePart = LocalCustom.createPartFromString(title)
                val descPart = LocalCustom.createPartFromString(description)
                val categoryPart = LocalCustom.createPartFromString(category)
                val subcategoryPart = LocalCustom.createPartFromString(subcategory)
                val userPart = LocalCustom.createPartFromString(farmerId.toString())
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.Companion.FARMER.baseUrl)
                val apiService = retrofit.create(ApiService::class.java)
                val response = apiService.uploadArticle(
                    file,
                    titlePart,
                    descPart,
                    categoryPart,
                    subcategoryPart,
                    userPart
                )
                ProgressHelper.disableProgressDialog()
                _uploadArticleResponse.value = response
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

    fun getCategories(context: Context) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.Companion.FARMER.baseUrl)
                val apiService = retrofit.create(ApiService::class.java)
                val response = apiService.getCategoriesForExpertCorner()
                ProgressHelper.disableProgressDialog()
                _getCategoriesForExpertCorner.value = response
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

    fun getSubCategories(selectedCategoryId: Int) {
        viewModelScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("category", selectedCategoryId)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.Companion.FARMER.baseUrl)
                val apiService = retrofit.create(ApiService::class.java)
                val response = apiService.getSubCategoriesForExpertCorner(requestBody)
                _getSubCategoriesForExpertCorner.value = response
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

    fun getSubCategories(selectedCategoryIdList: List<Int>) {
        viewModelScope.launch {
            try {
                val jsonArray = JSONArray(selectedCategoryIdList)
                val jsonObject = JSONObject().apply {
                    put("category", jsonArray)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.Companion.FARMER.baseUrl)
                val apiService = retrofit.create(ApiService::class.java)
                val response = apiService.getSubCategoriesForExpertCorner(requestBody)
                _getSubCategoriesForExpertCorner.value = response
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

    fun getUserArticles(context: Context) {
        viewModelScope.launch {
            try {
                val farmerId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("user_id", farmerId)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.Companion.FARMER.baseUrl)
                val apiService = retrofit.create(ApiService::class.java)
                val response = apiService.getUserArticles(requestBody)
                _getUserArticlesResponse.value = response
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

    fun getArticlesForFarmers(
        context: Context,
        categoryIds: List<Int> = emptyList(),
        subCategoryIds: List<Int> = emptyList(),
        sortBy: String = "expert_name"
    ) {
        viewModelScope.launch {
            ProgressHelper.showProgressDialog(context)
            try {
                val jsonObject = JSONObject()

                if (categoryIds.isNotEmpty()) {
                    val categoryArray = JSONArray()
                    categoryIds.forEach { categoryArray.put(it) }
                    jsonObject.put("category", categoryArray)
                }

                if (subCategoryIds.isNotEmpty()) {
                    val subCategoryArray = JSONArray()
                    subCategoryIds.forEach { subCategoryArray.put(it) }
                    jsonObject.put("subcategory", subCategoryArray)
                }

                jsonObject.put("sortby", sortBy)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiService = retrofit.create(ApiService::class.java)
                val response = apiService.getAllArticles(requestBody)
                ProgressHelper.disableProgressDialog()
                _getAllArticlesResponse.value = response
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
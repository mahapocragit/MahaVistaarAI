package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.viewmodels

import android.content.Context
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
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class CostCalculatorViewModel : ViewModel() {

    private var _expenseCategoryResponse = MutableLiveData<JsonObject>()
    val expenseCategoryResponse: MutableLiveData<JsonObject> = _expenseCategoryResponse

    private var _addCropForCalculationResponse = MutableLiveData<JsonObject>()
    val addCropForCalculationResponse: MutableLiveData<JsonObject> = _addCropForCalculationResponse

    private var _getTotalCostTransactionsResponse = MutableLiveData<JsonObject>()
    val getTotalCostTransactionsResponse: MutableLiveData<JsonObject> =
        _getTotalCostTransactionsResponse

    private var _getCropCostTransactionsResponse = MutableLiveData<JsonObject>()
    val getCropCostTransactionsResponse: MutableLiveData<JsonObject> =
        _getCropCostTransactionsResponse

    private var _addCropSpecificTransactionsResponse = MutableLiveData<JsonObject>()
    val addCropSpecificTransactionsResponse: MutableLiveData<JsonObject> =
        _addCropSpecificTransactionsResponse

    private var _deleteCropResponse = MutableLiveData<JsonObject>()
    val deleteCropResponse: MutableLiveData<JsonObject> =
        _deleteCropResponse

    private var _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    fun getExpenseCategory() {
        viewModelScope.launch {
            try {
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.fetchExpenseCategories()
                _expenseCategoryResponse.value = response
            } catch (e: JSONException) {
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

    fun addCropForCropCalculation(
        context: Context,
        cropId: Int,
        season: Int = 1,
        year: Int = 2025
    ) {
        viewModelScope.launch {
            try {
                val farmerId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("farmer_id", farmerId)
                    put("crop_id", cropId)
                    put("season", season)
                    put("year", year)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.addCropForCropCalculation(requestBody)
                _addCropForCalculationResponse.value = response
            } catch (e: JSONException) {
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

    fun getTotalCostTransactions(context: Context, season: Int = 1, year: Int = 2025) {
        viewModelScope.launch {
            try {
                val farmerId =
                    AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
                val jsonObject = JSONObject().apply {
                    put("farmer_id", farmerId)
                    put("season", season)
                    put("year", year)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getTotalCostTransactions(requestBody)
                _getTotalCostTransactionsResponse.value = response
            } catch (e: JSONException) {
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

    fun getCropSpecificTransactions(
        context: Context,
        cropId: Int,
        season: Int = 1,
        year: Int = 2025
    ) {
        viewModelScope.launch {
            try {
                ProgressHelper.showProgressDialog(context)
                val jsonObject = JSONObject().apply {
                    put("crop_id", cropId)
                    put("season", season)
                    put("year", year)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.getCropCostTransactions(requestBody)
                ProgressHelper.disableProgressDialog()
                _getCropCostTransactionsResponse.value = response
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

    fun addCropSpecificTransactions(
        context: Context,
        cropId: Int,
        date: String,
        type: String,
        categoryId: Int = 1,
        transactionName: String,
        priceTotal: Int,
        yield: Int = 0,
        pricePerUnit: Int = 0,
        unit:String = "kg"
    ) {
        viewModelScope.launch {
            try {
                ProgressHelper.showProgressDialog(context)
                val jsonObject = JSONObject()
                if (type == "income") {
                    jsonObject.apply {
                        put("crop_id", cropId)
                        put("date", date)
                        put("type", type)
                        put("transaction_name", transactionName)
                        put("price", priceTotal)
                        put("yield", yield)
                        put("price_per_unit", pricePerUnit)
                        put("unit", unit)
                    }
                } else {
                    jsonObject.apply {
                        put("crop_id", cropId)
                        put("date", date)
                        put("type", type)
                        put("category", categoryId)
                        put("transaction_name", transactionName)
                        put("price", priceTotal)
                    }
                }

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.addCropCostTransactions(requestBody)
                ProgressHelper.disableProgressDialog()
                _addCropSpecificTransactionsResponse.value = response
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

    fun deleteCrop(context: Context, cropId: Int){

        viewModelScope.launch {
            try {
                ProgressHelper.showProgressDialog(context)
                val jsonObject = JSONObject().apply {
                    put("crop_id", cropId)
                }
                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val retrofit: Retrofit =
                    RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                val response = apiRequest.deleteCrop(requestBody)
                ProgressHelper.disableProgressDialog()
                _deleteCropResponse.value = response
            }catch (e: JSONException) {
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
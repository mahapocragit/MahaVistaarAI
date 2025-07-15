package `in`.gov.mahapocra.mahavistaarai.graph_ql

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.app_util.AppConstants

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val _graphQLResponse = MutableLiveData<JsonObject?>()
    val graphQLResponse: LiveData<JsonObject?> = _graphQLResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchAccessToken() {
        val token = AppConstants.JWT_TOKEN
        val refreshToken = AppConstants.REFRESH_TOKEN
        repository.getAccessToken("Bearer $token", refreshToken) { graphQlResponse, errorMsg ->
            if (graphQlResponse!=null){
                _graphQLResponse.value = graphQlResponse
            } else {
                _error.postValue(errorMsg)
            }
        }
    }
}
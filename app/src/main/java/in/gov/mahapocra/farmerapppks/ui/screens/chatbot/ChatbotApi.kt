package `in`.gov.mahapocra.farmerapppks.ui.screens.chatbot

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ChatbotApi {
    @Headers("Content-Type: application/json")
    @POST("/query/")
    fun postQuery(@Body queryRequest: QueryRequest): Call<JsonObject>
}

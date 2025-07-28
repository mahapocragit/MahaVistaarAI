package `in`.gov.mahapocra.mahavistaarai.graph_ql

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface GraphQLApi {
    @POST("/")
    fun generateAccessToken(
        @Header("Authorization") authToken: String,
        @Body request: GraphQLRequest
    ): Call<JsonObject>

    @POST("/")
    fun getTestForAuthUser(
        @Header("Authorization") token: String,
        @Body request: GQLRequest
    ): Call<JsonObject>
}
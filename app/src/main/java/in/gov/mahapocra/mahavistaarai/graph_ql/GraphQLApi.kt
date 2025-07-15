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

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer", // You can append token if available
        "Cookie: ADRUM_BT=R:0|i:471885|g:e07108b5-490b-44d8-b5a9-abe1742ec2e721612255|e:1777|n:moafw-pov_64ad3129-73a7-4dd2-a21f-a0a2b120ed62"
    )
    @POST("/")
    fun getTestForPortal(@Body request: GQLRequest): Call<JsonObject>
}
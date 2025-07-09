package `in`.gov.mahapocra.mahavistaarai.graph_ql

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GraphQLApi {
    @POST("/")
    fun generateAccessToken(
        @Header("Authorization") authToken: String,
        @Body request: GraphQLRequest
    ): Call<GraphQLResponse>
}
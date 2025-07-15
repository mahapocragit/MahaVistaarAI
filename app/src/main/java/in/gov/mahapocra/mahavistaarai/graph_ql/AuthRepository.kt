package `in`.gov.mahapocra.mahavistaarai.graph_ql
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository {

    private val api: GraphQLApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://soilhealth4.dac.gov.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(GraphQLApi::class.java)
    }

    fun getAccessToken(
        authHeader: String,
        refreshToken: String,
        callback: (JsonObject?, String?) -> Unit
    ) {
        val query = """
    query Query(${'$'}refreshToken: String!) {
        generateAccessToken(refreshToken: ${'$'}refreshToken)
    }
""".trimIndent()

        val request = GraphQLRequest(query, mapOf("refreshToken" to refreshToken))

        api.generateAccessToken(authHeader, request).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "API Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback(null, "Network Error: ${t.localizedMessage}")
            }
        })
    }
}

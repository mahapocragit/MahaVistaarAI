package `in`.gov.mahapocra.mahavistaarai.graph_ql

import android.util.Log
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

    fun fetchResponse(phoneNumber:String, bearerToken: String, callback: (JsonObject?) -> Unit) {
        val query = """
    query GetTestForPortal(${'$'}state: String, ${'$'}district: String, ${'$'}village: String,
    ${'$'}phone: String, ${'$'}farmername: String, ${'$'}locale: String) {
        getTestForPortal(state: ${'$'}state,
        district: ${'$'}district, village: ${'$'}village, phone: ${'$'}phone, farmername: ${'$'}farmername) {
            id
            computedID
            cycle
            scheme
            status
            village
            createdAt
            sampleDate
            crop
            results
            plot {
                address
            }
            farmer {
                state
                district
                name
                phone
            }
            html(locale: ${'$'}locale)
        }
    }
""".trimIndent()
        val variables = Variables(phone = phoneNumber, locale = "hi")
        val request = GQLRequest(query, variables)

        api.getTestForPortal(bearerToken, request)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        callback(result)
                    } else {
                        Log.e("API", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("API", "Failure: ${t.message}")
                }
            })
    }
}

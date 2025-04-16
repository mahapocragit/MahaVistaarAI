package `in`.gov.mahapocra.farmerapp.ui.screens.chatbot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import `in`.gov.mahapocra.farmerapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatbotFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        // Call the API when the fragment view is created
        fetchChatbotResponse()

        return view
    }

    private fun fetchChatbotResponse() {
        val apiService = ChatbotRetrofitClient.instance

        // Create request body
        val variables = JsonObject() // Empty JSON object as per your request
        val queryRequest = QueryRequest("What is the best fertilizer for wheat?", variables)

        // Call the API
        apiService.postQuery(queryRequest).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    Log.d("API Response", jsonResponse.toString())
                } else {
                    Log.e("API Error", "Response Code: ${response.code()}, Message: ${response.message()}")
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API Error Body", errorBody ?: "No error body")
                    } catch (e: Exception) {
                        Log.e("API Error Body", "Error parsing error body", e)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("API Failure", t.message ?: "Unknown error")
            }
        })
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.chatbot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatbotRetrofitClient {
    private const val BASE_URL = "https://70ed-2405-201-2008-59f3-99b3-e52e-74e-989b.ngrok-free.app/"

    val instance: ChatbotApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatbotApi::class.java)
    }
}
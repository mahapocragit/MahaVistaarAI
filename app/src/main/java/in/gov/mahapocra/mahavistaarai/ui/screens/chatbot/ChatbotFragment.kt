package `in`.gov.mahapocra.mahavistaarai.ui.screens.chatbot

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatbotFragment : Fragment() {
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)
        webView = view.findViewById(R.id.webView)
        loadWebView()
        return view
    }

    private fun loadWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                callback?.invoke(origin, true, false)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed() // ⚠️ Use with caution - for dev only
            }
        }

        webView.loadUrl("https://vistaar.kenpath.ai/")
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
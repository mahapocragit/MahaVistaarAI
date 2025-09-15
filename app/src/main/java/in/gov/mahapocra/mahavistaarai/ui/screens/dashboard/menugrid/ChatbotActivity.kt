package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Context
import android.content.pm.PackageManager
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.microsoft.clarity.Clarity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChatbotBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MahavistaarViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import org.json.JSONObject

class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding
    private val mahavistaarViewModel: MahavistaarViewModel by viewModels()
    private lateinit var languageToLoad: String
    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ChatbotActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        askForLocationAndMicrophonePermission()
        binding.toolbar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.toolbar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            Clarity.sendCustomEvent("WEBVIEW_CLOSED")
        }
        binding.toolbar.textViewHeaderTitle.text = ""

        ProgressHelper.showProgressDialog(this)
        mahavistaarViewModel.requestUrlForChatBot(this)
        mahavistaarViewModel.responseUrlForChatBot.observe(this) { response->
            if (response!=null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optString("status")
                if (status == "success") {
                    Clarity.sendCustomEvent("WEBVIEW_OPENED")
                    val jwtToken = jSONObject.optString("token").trim()
                    val chatBotUrl = "${AppEnvironment.BOT_URL.baseUrl}$jwtToken"
                    binding.webView.visibility = View.VISIBLE
                    binding.noInternetAvailableLayout.visibility = View.GONE
                    loadWebView(chatBotUrl)
                } else {
                    ProgressHelper.disableProgressDialog()
                    Clarity.sendCustomEvent("WEBVIEW_STOPPED")
                    binding.webView.visibility = View.GONE
                    binding.noInternetAvailableLayout.visibility = View.VISIBLE
                }
            }else{
                ProgressHelper.disableProgressDialog()
                Clarity.sendCustomEvent("WEBVIEW_STOPPED")
                binding.webView.visibility = View.GONE
                binding.noInternetAvailableLayout.visibility = View.VISIBLE
            }
        }
        mahavistaarViewModel.error.observe(this) {
            Clarity.sendCustomEvent("WEBVIEW_STOPPED")
            binding.webView.visibility = View.GONE
            binding.noInternetAvailableLayout.visibility = View.VISIBLE
            ProgressHelper.disableProgressDialog()
        }

        binding.tryAgainTextView.setOnClickListener {
            askForLocationAndMicrophonePermission()
            mahavistaarViewModel.requestUrlForChatBot(this)
            ProgressHelper.showProgressDialog(this)
        }
    }

    private fun askForLocationAndMicrophonePermission() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun loadWebView(chatBotUrl: String) {
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true)

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: android.webkit.PermissionRequest) {
                runOnUiThread {
                    request.grant(request.resources)
                }
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                Clarity.sendCustomEvent("WEBVIEW_STOPPED")
                handler?.cancel()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            runOnUiThread {
                ProgressHelper.disableProgressDialog()
                binding.webView.loadUrl(chatBotUrl)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Clarity.sendCustomEvent("WEBVIEW_RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Clarity.sendCustomEvent("WEBVIEW_PAUSED")
    }

    override fun onDestroy() {
        super.onDestroy()
        Clarity.sendCustomEvent("WEBVIEW_CLOSED")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for ((index, permission) in permissions.withIndex()) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "$permission granted")
                } else {
                    Log.d("Permissions", "$permission denied")
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}
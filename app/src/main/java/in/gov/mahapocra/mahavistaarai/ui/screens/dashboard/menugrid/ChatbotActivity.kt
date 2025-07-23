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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChatbotBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MahavistaarViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper

class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding
    private lateinit var mahavistaarViewModel: MahavistaarViewModel
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
        uiResponsive(binding.root, window)

        askForLocationAndMicrophonePermission()
        mahavistaarViewModel = ViewModelProvider(this)[MahavistaarViewModel::class.java]
        binding.toolbar.imageViewHeaderBack.setVisibility(View.VISIBLE)
        binding.toolbar.imageViewHeaderBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.textViewHeaderTitle.text = ""

        ProgressHelper.showProgressDialog(this)
        mahavistaarViewModel.requestUrlForChatBot(this)
        mahavistaarViewModel.responseUrlForChatBot.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null && this@ChatbotActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                binding.webView.visibility = View.VISIBLE
                binding.noInternetAvailableLayout.visibility = View.GONE
                val url = it.get("url")?.asString.orEmpty()
                loadWebView(url)
            }else{
                binding.webView.visibility = View.GONE
                binding.noInternetAvailableLayout.visibility = View.VISIBLE
            }
        }
        mahavistaarViewModel.error.observe(this) {
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
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
                handler?.cancel()
            }
        }
        binding.webView.loadUrl(chatBotUrl)
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
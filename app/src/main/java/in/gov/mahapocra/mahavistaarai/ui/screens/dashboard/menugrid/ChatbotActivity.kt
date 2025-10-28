package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.microsoft.clarity.Clarity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.util.NetworkUtils.isNetworkAvailable
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChatbotBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MahavistaarViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatbotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding
    private val mahavistaarViewModel: MahavistaarViewModel by viewModels()
    private val farmerViewModel: FarmerViewModel by viewModels()

    private var languageToLoad = "mr"
    private val PERMISSION_REQUEST_CODE = 1001
    private var isWebViewLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Language setup ---
        languageToLoad = if (AppSettings.getLanguage(this).equals("1", ignoreCase = true)) "en" else "mr"
        switchLanguage(this, languageToLoad)

        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        // --- Toolbar setup ---
        binding.toolbar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.toolbar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            Clarity.sendCustomEvent("WEBVIEW_CLOSED")
        }
        binding.toolbar.textViewHeaderTitle.text = ""

        // --- Load WebView faster ---
        setupWebView()

        // --- Show progress dialog only if load takes time ---
        CoroutineScope(Dispatchers.Main).launch {
            delay(300)
            if (!isWebViewLoaded) ProgressHelper.showProgressDialog(this@ChatbotActivity)
        }

        // --- Run permission & network in parallel ---
        CoroutineScope(Dispatchers.Main).launch {
            val permissionJob = async { askForLocationAndMicrophonePermission() }
            val urlJob = async { mahavistaarViewModel.requestUrlForChatBot(this@ChatbotActivity) }
            permissionJob.await()
            urlJob.await()
        }

        // --- Observe API response ---
        mahavistaarViewModel.responseUrlForChatBot.observe(this) { response ->
            handleChatbotResponse(response)
        }

        mahavistaarViewModel.error.observe(this) {
            onChatbotError()
        }

        // --- Retry button ---
        binding.tryAgainTextView.setOnClickListener {
            mahavistaarViewModel.requestUrlForChatBot(this)
            ProgressHelper.showProgressDialog(this)
        }

        // --- Update notification status (non-blocking) ---
        val notificationId = intent.getLongExtra("id", 0L)
        if (notificationId != 0L) {
            farmerViewModel.updateNotificationStatusForChatbot(this, notificationId)
        }
    }

    private fun askForLocationAndMicrophonePermission() {
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun setupWebView() {
        val webSettings = binding.webView.settings
        webSettings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            // ✅ Modern caching
            cacheMode = WebSettings.LOAD_DEFAULT
            if (!isNetworkAvailable(this@ChatbotActivity)) {
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            }

            userAgentString =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(binding.webView, true)
        }

        binding.webView.apply {
            visibility = View.INVISIBLE

            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    runOnUiThread { request.grant(request.resources) }
                }

                override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                    callback?.invoke(origin, true, false)
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    handler?.cancel()
                    Clarity.sendCustomEvent("WEBVIEW_STOPPED")
                    onChatbotError()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isWebViewLoaded = true
                    ProgressHelper.disableProgressDialog()
                    binding.noInternetAvailableLayout.visibility = View.GONE
                    binding.webView.visibility = View.VISIBLE
                    binding.webView.animate().alpha(1f).setDuration(250).start()
                }
            }
        }
    }

    private fun loadChatbot(chatBotUrl: String) {
        Clarity.sendCustomEvent("WEBVIEW_OPENED")
        binding.webView.loadUrl(chatBotUrl)
    }

    private fun handleChatbotResponse(response: Any?) {
        if (response == null) {
            onChatbotError()
            return
        }

        try {
            val json = JSONObject(response.toString())
            val status = json.optString("status")
            if (status.equals("success", ignoreCase = true)) {
                val jwtToken = json.optString("token").trim()
                val chatBotUrl = "${AppEnvironment.BOT_URL.baseUrl}$jwtToken"
                loadChatbot(chatBotUrl)
            } else {
                onChatbotError()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onChatbotError()
        }
    }

    private fun onChatbotError() {
        ProgressHelper.disableProgressDialog()
        Clarity.sendCustomEvent("WEBVIEW_STOPPED")
        binding.webView.visibility = View.GONE
        binding.noInternetAvailableLayout.visibility = View.VISIBLE
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                Log.d("Permissions", "$permission -> ${if (grantResults[index] == PackageManager.PERMISSION_GRANTED) "granted" else "denied"}")
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) "en" else "mr"
        val updatedContext = configureLocale(newBase, languageToLoad)
        super.attachBaseContext(updatedContext)
    }
}
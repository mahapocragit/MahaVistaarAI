package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Context
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityTempDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MahavistaarViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import org.json.JSONObject

class TempDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTempDashboardBinding
    private lateinit var mahavistaarViewModel: MahavistaarViewModel
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@TempDashboardActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityTempDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mahavistaarViewModel = ViewModelProvider(this)[MahavistaarViewModel::class.java]
        binding.toolbar.imageViewHeaderBack.setVisibility(View.VISIBLE)
        binding.toolbar.imageViewHeaderBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.textViewHeaderTitle.text = ""

        ProgressHelper.showProgressDialog(this)
        mahavistaarViewModel.requestUrlForChatBot(this)
        mahavistaarViewModel.responseUrlForChatBot.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null && this@TempDashboardActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                val url = it.get("url")?.asString.orEmpty()
                loadWebView(url)
            }
        }
        mahavistaarViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
            Toast.makeText(this, "Token generation failed!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadWebView(chatBotUrl: String) {
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true)

        binding.webView.webChromeClient = object : WebChromeClient() {
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
                handler?.cancel() // ⚠️ Use with caution - for dev only
            }
        }

        binding.webView.loadUrl(chatBotUrl)
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
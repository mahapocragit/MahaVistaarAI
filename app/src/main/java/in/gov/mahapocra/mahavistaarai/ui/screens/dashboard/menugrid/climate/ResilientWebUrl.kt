package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityResilientWebUrlBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper

class ResilientWebUrl : AppCompatActivity() {

    private lateinit var binding: ActivityResilientWebUrlBinding
    lateinit var languageToLoad: String
    private lateinit var climateWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = if (AppSettings.getLanguage(this).equals("2", ignoreCase = true)) {
            "mr"
        } else {
            "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityResilientWebUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.layoutToolbar.textViewHeaderTitle.text =
            getString(R.string.climate_resilient_technology)
        binding.layoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.layoutToolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
        climateWebView = findViewById(R.id.climateWebView)
        intent.getStringExtra("webUrl")?.let { openWebView(it) }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun openWebView(url: String) {

        climateWebView.settings.javaScriptEnabled = true
        climateWebView.settings.domStorageEnabled = true
        climateWebView.settings.mediaPlaybackRequiresUserGesture = false // auto-play support
        climateWebView.settings.useWideViewPort = true
        climateWebView.settings.loadWithOverviewMode = true
        climateWebView.settings.allowFileAccess = true

        // Enable hardware acceleration (usually default, but ensure it's on)
        climateWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        climateWebView.webChromeClient = object : WebChromeClient() {
            // Optional: handle fullscreen video playback here if needed
        }

        climateWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (!this@ResilientWebUrl.isFinishing && !this@ResilientWebUrl.isDestroyed) {
                    ProgressHelper.showProgressDialog(this@ResilientWebUrl)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                ProgressHelper.disableProgressDialog()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false // Let WebView load URLs itself
            }
        }

        climateWebView.loadUrl(url)
    }

    override fun onDestroy() {
        ProgressHelper.disableProgressDialog()
        super.onDestroy()
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
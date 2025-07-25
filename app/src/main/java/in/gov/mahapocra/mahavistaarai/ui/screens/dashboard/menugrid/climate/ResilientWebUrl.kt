package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityResilientWebUrlBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive

class ResilientWebUrl : AppCompatActivity() {

    private lateinit var climateWebView: WebView
    private lateinit var progressDialog: ProgressDialog
    lateinit var languageToLoad: String
    private lateinit var binding: ActivityResilientWebUrlBinding

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

        climateWebView = findViewById(R.id.climateWebView)
        intent.getStringExtra("webUrl")?.let { openWebView(it) }
    }

    private fun openWebView(url: String) {
        progressDialog = ProgressDialog(this).apply {
            setMessage(getString(R.string.please_wait))
            setCancelable(true)
            show()
        }

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
                    progressDialog.show()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
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
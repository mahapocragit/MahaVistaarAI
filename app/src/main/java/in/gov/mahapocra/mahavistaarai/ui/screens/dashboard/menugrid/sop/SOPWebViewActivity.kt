package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.sop

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityResilientWebUrlBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.SOP_POINT
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FarmerHelper.containsFarmerId
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONObject

class SOPWebViewActivity : AppCompatActivity() {

    private lateinit var climateWebView: WebView
    private lateinit var binding: ActivityResilientWebUrlBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load correct language
        languageToLoad =
            if (AppSettings.getLanguage(this).equals("2", ignoreCase = true)) "mr" else "en"
        switchLanguage(this, languageToLoad)

        // Inflate layout
        binding = ActivityResilientWebUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        // Toolbar setup
        binding.layoutToolbar.textViewHeaderTitle.text = getString(R.string.sop_title)
        binding.layoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.layoutToolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        observeResponse()
        // WebView setup
        climateWebView = binding.climateWebView
        setupWebView()

        // Load URL
        intent.getStringExtra("webUrl")?.let { openWebView(it) }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (climateWebView.canGoBack()) {
                    climateWebView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun observeResponse() {
        leaderboardViewModel.responseUpdateUserPoints.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status == 200) {
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }
    }

    private fun setupWebView() {
        binding.webViewProgressBar.visibility = View.VISIBLE
        with(climateWebView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            allowFileAccess = true
        }

        climateWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        climateWebView.webChromeClient = object : WebChromeClient() {}

        climateWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (!isFinishing && !isDestroyed) {
                    binding.webViewProgressBar.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (!isFinishing && !isDestroyed) {
                    binding.webViewProgressBar.visibility = View.GONE
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }
        if (containsFarmerId(this)) {
            leaderboardViewModel.updateUserPoints(this, SOP_POINT)
        }
    }

    private fun openWebView(url: String) {
        climateWebView.loadUrl(url)
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
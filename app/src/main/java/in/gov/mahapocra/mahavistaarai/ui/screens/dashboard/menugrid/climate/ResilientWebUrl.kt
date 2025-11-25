package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityResilientWebUrlBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.CLIMATE_RESILIENT_POINT
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONObject

class ResilientWebUrl : AppCompatActivity() {

    private lateinit var binding: ActivityResilientWebUrlBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLanguage()

        binding = ActivityResilientWebUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        setupToolbar()
        observeResponse()
        setupWebView()
        setupBackPressed()

        intent.getStringExtra("webUrl")?.let {
            binding.climateWebView.loadUrl(it)
        }
    }

    private fun observeResponse() {
        leaderboardViewModel.responseUpdateUserPoints.observe(this){ response->
            if (response!=null){
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status==200){
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }
    }

    private fun setupLanguage() {
        languageToLoad = if (AppSettings.getLanguage(this).equals("2", true)) "mr" else "en"
        switchLanguage(this, languageToLoad)
    }

    private fun setupToolbar() {
        binding.layoutToolbar.textViewHeaderTitle.text =
            getString(R.string.climate_resilient_technology)

        binding.layoutToolbar.imgBackArrow.apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }
    }

    private fun setupWebView() {
        with(binding.climateWebView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            allowFileAccess = true
        }

        binding.climateWebView.webChromeClient = WebChromeClient()

        binding.climateWebView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (!isFinishing && !isDestroyed) {
                    ProgressHelper.showProgressDialog(this@ResilientWebUrl)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                ProgressHelper.disableProgressDialog()
                leaderboardViewModel.updateUserPoints(this@ResilientWebUrl, CLIMATE_RESILIENT_POINT)
            }
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onDestroy() {
        binding.climateWebView.apply {
            stopLoading()
            clearHistory()
            clearCache(true)
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }
        ProgressHelper.disableProgressDialog()
        super.onDestroy()
    }

    override fun attachBaseContext(newBase: Context) {
        val language = if (AppSettings.getLanguage(newBase).equals("1", true)) "en" else "mr"
        super.attachBaseContext(configureLocale(newBase, language))
    }
}
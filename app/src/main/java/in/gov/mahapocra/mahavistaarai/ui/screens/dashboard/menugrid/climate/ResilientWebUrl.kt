package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.app.ProgressDialog
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityResilientWebUrlBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import java.util.*

class ResilientWebUrl : AppCompatActivity() {

    private lateinit var climateWebView: WebView
    private lateinit var progressDialog: ProgressDialog
    lateinit var languageToLoad: String
    private lateinit var binding:ActivityResilientWebUrlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageToLoad = if (AppSettings.getLanguage(this) == "1") "en" else "mr"
        LocalCustom.configureLocale(baseContext, languageToLoad)
        binding = ActivityResilientWebUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutToolbar.textViewHeaderTitle.text = getString(R.string.climate_resilient_technology)
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
        // Configure WebView
        climateWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressDialog.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressDialog.dismiss()
            }
        }

        intent.getStringExtra("webUrl")?.let { climateWebView.loadUrl(url) }
    }

    override fun onBackPressed() {
        finish()
    }
}
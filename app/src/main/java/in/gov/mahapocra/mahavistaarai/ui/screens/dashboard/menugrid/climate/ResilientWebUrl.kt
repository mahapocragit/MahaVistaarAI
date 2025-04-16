package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.app.ProgressDialog
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.*

class ResilientWebUrl : AppCompatActivity() {

    private lateinit var climateWebView: WebView
    private lateinit var progressDialog: ProgressDialog
    lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resilient_web_url)

        val languageToLoad = if (AppSettings.getLanguage(this) == "1") "en" else "mr"
        Locale.setDefault(Locale(languageToLoad))

        val config = Configuration()
        Configuration().setLocale(Locale(languageToLoad))
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )

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
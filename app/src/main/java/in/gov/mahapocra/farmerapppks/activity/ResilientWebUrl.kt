package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import java.util.*

class ResilientWebUrl : AppCompatActivity() {

    private var climateWebView: WebView? = null
    lateinit var languageToLoad: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resilient_web_url)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@ResilientWebUrl).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        init()
        val b = intent.extras
        val url = b?.getString("webUrl")
        Log.d("urlResilientWebUrl", url.toString())
       // climateWebView?.settings!!.javaScriptEnabled = true

            openWebview(url.toString())

    }

    fun init() {
        climateWebView = findViewById<View>(R.id.climateWebView) as WebView
    }

    fun openWebview(url:String)
    {
        climateWebView?.loadUrl(url);
    }

    override fun onBackPressed() {
        finish()
    }
}
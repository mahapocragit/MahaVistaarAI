package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ProgressBar

class HealthCardActivity : AppCompatActivity() {

    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@HealthCardActivity).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_health_card)
        initComponents()
        setConfiguration()

    }

    private fun initComponents() {
        webView = findViewById(R.id.webView)
        // progressBar = findViewById<ProgressBar>(R.id.progressBar)
    }

    private fun setConfiguration() {
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        webView!!.setOnLongClickListener { true }
        webView!!.isLongClickable = false
       // webView!!.loadUrl("http://gis.mahapocra.gov.in/controlpanel/soilcard.html")
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://gis.mahapocra.gov.in/controlpanel/soilcard.html"))
        startActivity(browserIntent)
    }
}
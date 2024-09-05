package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityGisBinding

class GisActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGisBinding
    var languageToLoad: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@GisActivity).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }

        //Loading URL in webView
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.progressBar.visibility = View.VISIBLE
        val webSettings = binding.webView1.settings
        webSettings.cacheMode
        webSettings.javaScriptEnabled = true
        binding.webView1.loadUrl("http://gis.mahapocra.gov.in/controlpanel")
        if (binding.webView1.progress == 10){
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}
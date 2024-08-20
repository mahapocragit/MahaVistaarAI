package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.os.Bundle
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class Gis : AppCompatActivity() {

    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@Gis).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_gis)
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
        webView!!.loadUrl("http://gis.mahapocra.gov.in/controlpanel")

//        webView!!.settings.javaScriptEnabled = true //enable javascript
//
//        webView!!.webViewClient = object : WebViewClient() {
//            override fun onReceivedError(
//                view: WebView,
//                errorCode: Int,
//                description: String,
//                failingUrl: String
//            ) {
//                webView!!.visibility = View.VISIBLE
//                view.loadUrl("about:blank")
//            }
//
//            @TargetApi(Build.VERSION_CODES.M)
//            override fun onReceivedError(
//                view: WebView,
//                req: WebResourceRequest,
//                rerr: WebResourceError
//            ) {
//                // Redirect to deprecated method, so you can use it in all SDK versions
//                onReceivedError(
//                    view,
//                    rerr.errorCode,
//                    rerr.description.toString(),
//                    req.url.toString()
//                )
//                webView!!.visibility = View.VISIBLE
//                view.loadUrl("about:blank")
//            }
//
//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                super.onPageStarted(view, url, favicon)
//                progressBar!!.visibility = View.VISIBLE
//                progressBar!!.isIndeterminate = true
//            }
//
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//                progressBar!!.visibility = View.GONE
//                webView!!.visibility = View.VISIBLE
//                progressBar!!.isIndeterminate = false
//            }
//        }
//
//        webView!!.loadUrl("http://gis.mahapocra.gov.in/controlpanel/soilcard.html")
    }
}
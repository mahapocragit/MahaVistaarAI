package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.sidenavigation

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapp.R
import `in`.gov.mahapocra.farmerapp.util.app_util.AppConstants
import android.annotation.TargetApi
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.menugrid.DashboardScreen
import java.util.*

class DbtStatus : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var imageMenushow: ImageView? = null
    private var textViewHeaderTitle: TextView? = null
    private lateinit var phoneNumber: String
    var languageToLoad: String? = "mr"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dbt_status)
        Log.d("languageToLoad:",languageToLoad +"");
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DbtStatus).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        initComponents()
        setConfiguration()
    }
    private fun initComponents() {
         webView = findViewById(R.id.webView)
         progressBar = findViewById<ProgressBar>(R.id.progressBar)
        imageMenushow=findViewById(R.id.imageMenushow)
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle)
    }
    private fun setConfiguration() {
        if (getIntent() != null) {
            phoneNumber = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
           // phoneNumber = intent.getStringExtra("USER_MOBILE").toString()
            Log.d("DbtStatus:","phoneNumber:= "+phoneNumber);
        }
        textViewHeaderTitle?.setText(R.string.pocra_dbt_status)
        imageMenushow?.setVisibility(View.VISIBLE)
        imageMenushow?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        })
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
       // webView.settings.javaScriptEnabled = true //enable javascript
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                webView.visibility = View.VISIBLE
                view.loadUrl("about:blank")
            }
            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView,
                req: WebResourceRequest,
                rerr: WebResourceError
            ) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(
                    view,
                    rerr.errorCode,
                    rerr.description.toString(),
                    req.url.toString()
                )
                webView.visibility = View.VISIBLE
                view.loadUrl("about:blank")
            }
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                progressBar.isIndeterminate = true
            }
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
                progressBar.isIndeterminate = false
            }
        }
        var strUrl : String
        strUrl ="https://dbt.mahapocra.gov.in/Office/GetFarmerApp.aspx?Mob="+phoneNumber;
        webView.loadUrl(strUrl)
        Log.d("param_DbtStatus:","strUrl:= "+strUrl);
    }
}


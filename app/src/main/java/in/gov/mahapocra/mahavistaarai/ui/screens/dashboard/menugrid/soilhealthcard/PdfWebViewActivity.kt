package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPdfViewBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import java.util.Locale

class PdfWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var languageToLoad: String
    private val googleDriveView: String = "https://mozilla.github.io/pdf.js/web/viewer.html?file="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PdfWebViewActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        LocalCustom.configureLocale(baseContext, languageToLoad)
        binding= ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.soil_health_card)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val url = intent.getStringExtra("pdf_url")
        progressBar = findViewById(R.id.progressBar)
        val settings: WebSettings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        binding.floatingActionButton.setOnClickListener {
            url?.let { it1 -> LocalCustom.downloadPdf(this, it1) }
        }

        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(googleDriveView + url)
    }


}
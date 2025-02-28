package `in`.gov.mahapocra.farmerapppks.ui.screens

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.farmerapppks.R

class PdfWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var headerTitle: TextView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        val url = intent.getStringExtra("pdf_url")
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        headerTitle = findViewById(R.id.textViewHeaderTitle)
        headerTitle.text = "Soil Health Card"
        backButton = findViewById(R.id.imageViewHeaderBack)

        // Show ProgressBar while loading
        progressBar.visibility = View.VISIBLE
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Enable JavaScript and other settings
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE // Hide ProgressBar once fully loaded
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE // Hide ProgressBar when the page is done loading
            }
        }

        // Load the PDF using Google Docs Viewer (Recommended)
        val pdfViewerUrl = "https://docs.google.com/gview?embedded=true&url=$url"
        webView.loadUrl(pdfViewerUrl)
    }
}
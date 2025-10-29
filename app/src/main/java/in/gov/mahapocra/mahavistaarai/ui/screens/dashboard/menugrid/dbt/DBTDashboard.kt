package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDbtdashboardBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.encodeToBase64
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.getLocationUsingLocationManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.getMobileOrWifiIp
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants

class DBTDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDbtdashboardBinding
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val results = WebChromeClient.FileChooserParams.parseResult(result.resultCode, data)
                fileChooserCallback?.onReceiveValue(results)
            } else {
                fileChooserCallback?.onReceiveValue(null)
            }
            fileChooserCallback = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDbtdashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpViews()

        val agristackId = AppSettings.getInstance().getValue(this, AppConstants.AGRISTACKID, "")
        Log.d("TAGGER", "loadWebView: $agristackId")

        if (!agristackId.isNullOrEmpty() && agristackId != "null") {
            loadWebView(encodeToBase64(agristackId))
        } else {
            loadWebView("")
        }

        // ✅ Handle back navigation properly
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    startActivity(Intent(this@DBTDashboard, DBTActivity::class.java))
                    finish()
                }
            }
        })
    }

    private fun setUpViews() {
        binding.layoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.layoutToolbar.imgBackArrow.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                startActivity(Intent(this@DBTDashboard, DBTActivity::class.java))
                finish()
            }
        }
        binding.layoutToolbar.textViewHeaderTitle.text = ""
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(encryptedFarmerId: String) {
        val dbtUrl = "https://dbt-ndksp.mahapocra.gov.in"
        val location = getLocationUsingLocationManager(this)
        val lat = location?.latitude
        val long = location?.longitude

        val urlForLoadWeb = if (encryptedFarmerId.isEmpty()) {
            "$dbtUrl/MahavistaarLoginAuth"
        } else {
            "$dbtUrl/MahavistaarLoginAuth" +
                    "?farmerid=$encryptedFarmerId" +
                    "&details=Chrome_Windows10,latitude=$lat,longitude=$long"
        }

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.allowFileAccess = false           // 🔒 disable local file access
        webSettings.allowContentAccess = true         // ✅ needed for image/pdf uploads
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        // ✅ Safe URL handling
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                return if (url.startsWith("https://dbt-ndksp.mahapocra.gov.in")) {
                    false
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    true
                }
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                AlertDialog.Builder(view?.context)
                    .setTitle("SSL Certificate Error")
                    .setMessage("The connection is not secure. Do you want to continue?")
                    .setPositiveButton("Continue") { _, _ -> handler?.proceed() }
                    .setNegativeButton("Cancel") { _, _ -> handler?.cancel() }
                    .show()
            }
        }

        // ✅ Restrict file uploads to only images and PDFs
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf"))
                }

                val chooserIntent = Intent.createChooser(intent, "Select Image or PDF")
                fileChooserLauncher.launch(chooserIntent)
                return true
            }
        }

        binding.webView.loadUrl(urlForLoadWeb)
    }
}
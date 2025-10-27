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
        val dbtUrl = "https://dbt-ndksp.mahapocra.gov.in" // or UAT if needed
        val location = getLocationUsingLocationManager(this)
        val lat = location?.latitude
        val long = location?.longitude

        val urlForLoadWeb = if (encryptedFarmerId.isEmpty()) {
            "$dbtUrl/MahavistaarLoginAuth"
        } else {
            "$dbtUrl/MahavistaarLoginAuth" +
                    "?farmerid=$encryptedFarmerId" +
                    "&ip=${getMobileOrWifiIp()}" +
                    "&details=Chrome_Windows10, latitude=$lat, longitude=$long"
        }

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // ✅ Handle redirects, SSL, and errors
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                // ⚠️ In production, prompt the user instead of always proceeding
                handler?.proceed()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page Loaded: $url")
            }
        }

        // ✅ Handle JS popups, confirms, new windows, file uploads
        binding.webView.webChromeClient = object : WebChromeClient() {

            // --- File chooser for <input type="file"> ---
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = filePathCallback

                val intent = try {
                    fileChooserParams?.createIntent()
                } catch (e: Exception) {
                    fileChooserCallback = null
                    return false
                }
                intent?.let { fileChooserLauncher.launch(it) }
                return true
            }

            // --- JavaScript alerts ---
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                AlertDialog.Builder(view?.context)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
                    .setCancelable(false)
                    .create()
                    .show()
                return true
            }

            // --- JavaScript confirms ---
            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                AlertDialog.Builder(view?.context)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> result?.cancel() }
                    .create()
                    .show()
                return true
            }

            // --- Handle new windows / popups (like OTP dialogs) ---
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(view!!.context)
                val dialog = Dialog(view.context)
                dialog.setContentView(newWebView)
                dialog.show()

                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.domStorageEnabled = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    newWebView.webViewClient = view.webViewClient
                }
                newWebView.webChromeClient = this

                (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                resultMsg.sendToTarget()

                return true
            }
        }

        // ✅ Load the webpage
        binding.webView.loadUrl(urlForLoadWeb)
    }
}
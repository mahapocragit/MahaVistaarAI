package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.microsoft.clarity.Clarity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.util.NetworkUtils.isNetworkAvailable
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChatbotBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MahavistaarViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.CHATBOT_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FarmerHelper.containsFarmerId
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatbotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding

    private val mahavistaarViewModel: MahavistaarViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var name = ""
    private var mobile = ""
    private var languageToLoad = "mr"

    companion object {
        private const val TAG = "ChatbotActivity"
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private lateinit var fileChooserLauncher: ActivityResultLauncher<Intent>

    private var cameraImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad =
            if (AppSettings.getLanguage(this).equals("1", ignoreCase = true))
                "en"
            else
                "mr"

        switchLanguage(this, languageToLoad)

        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiResponsive(binding.root)

        setupToolbar()
        registerFileChooser()
        setupWebView()
        observeResponse()
        setUpListeners()

        askForPermissions()
        name = AppPreferenceManager(this).getString(AppConstants.USER_NAME).toString()
        mobile = AppPreferenceManager(this).getString(AppConstants.USER_MOBILE).toString()
        // Request chatbot URL
        ProgressHelper.showProgressDialog(this)
        mahavistaarViewModel.requestUrlForChatBot(name, mobile)

        // Update notification status
        val notificationId = intent.getLongExtra("id", 0L)
        if (notificationId != 0L) {
            farmerViewModel.updateNotificationStatusForChatbot(this, notificationId)
        }
    }

    private fun registerFileChooser() {

        fileChooserLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                val callback = filePathCallback ?: return@registerForActivityResult
                filePathCallback = null

                val uris = when {

                    result.resultCode != Activity.RESULT_OK -> null

                    result.data?.clipData != null -> {

                        val clipData = result.data!!.clipData!!

                        Array(clipData.itemCount) {
                            clipData.getItemAt(it).uri
                        }
                    }

                    result.data?.data != null -> {

                        arrayOf(result.data!!.data!!)
                    }

                    cameraImageUri != null -> {

                        arrayOf(cameraImageUri!!)
                    }

                    else -> null
                }

                callback.onReceiveValue(uris)

                cameraImageUri = null
            }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {

        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "IMG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }


    private fun setupToolbar() {
        binding.toolbar.imageViewHeaderBack.visibility = View.VISIBLE

        binding.toolbar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            Clarity.sendCustomEvent("WEBVIEW_CLOSED")
        }

        binding.toolbar.textViewHeaderTitle.text = ""
    }

    private fun setUpListeners() {

        binding.tryAgainTextView.setOnClickListener {

            binding.noInternetAvailableLayout.visibility = View.GONE

            ProgressHelper.showProgressDialog(this)

            mahavistaarViewModel.requestUrlForChatBot(name, mobile)
        }
    }

    private fun askForPermissions() {

        val permissionsNeeded = mutableListOf<String>()

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {

            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setupWebView() {

        WebView.setWebContentsDebuggingEnabled(true)

        val webSettings = binding.webView.settings

        webSettings.apply {

            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true

            allowFileAccess = true
            allowContentAccess = true

            javaScriptCanOpenWindowsAutomatically = true

            loadsImagesAutomatically = true
            mediaPlaybackRequiresUserGesture = false

            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            cacheMode =
                if (isNetworkAvailable(this@ChatbotActivity)) {
                    WebSettings.LOAD_DEFAULT
                } else {
                    WebSettings.LOAD_CACHE_ELSE_NETWORK
                }

            userAgentString =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36"
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(binding.webView, true)
        }

        binding.webView.apply {

            alpha = 0f
            visibility = View.INVISIBLE

            webChromeClient = object : WebChromeClient() {

                override fun onPermissionRequest(request: PermissionRequest) {
                    Log.e("WEBVIEW", "PermissionRequest: ${request.resources.joinToString()}")
                    runOnUiThread {

                        try {
                            request.grant(request.resources)
                        } catch (e: Exception) {
                            Log.e(TAG, "Permission request error", e)
                        }
                    }
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {

                    this@ChatbotActivity.filePathCallback?.onReceiveValue(null)
                    this@ChatbotActivity.filePathCallback = filePathCallback

                    val imageFile = createImageFile()

                    cameraImageUri = FileProvider.getUriForFile(
                        this@ChatbotActivity,
                        "${packageName}.android.fileprovider",
                        imageFile
                    )

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }

                    val galleryIntent = Intent(fileChooserParams?.createIntent()).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, fileChooserParams?.mode == FileChooserParams.MODE_OPEN_MULTIPLE)
                    }

                    if (fileChooserParams?.isCaptureEnabled == true) {

                        if (ContextCompat.checkSelfPermission(
                                this@ChatbotActivity,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {

                            ActivityCompat.requestPermissions(
                                this@ChatbotActivity,
                                arrayOf(Manifest.permission.CAMERA),
                                PERMISSION_REQUEST_CODE
                            )

                            this@ChatbotActivity.filePathCallback?.onReceiveValue(null)
                            this@ChatbotActivity.filePathCallback = null

                            return true
                        }

                        if (cameraIntent.resolveActivity(packageManager) != null) {
                            fileChooserLauncher.launch(cameraIntent)
                        } else {
                            this@ChatbotActivity.filePathCallback?.onReceiveValue(null)
                            this@ChatbotActivity.filePathCallback = null
                        }

                    } else {

                        val chooser = Intent(Intent.ACTION_CHOOSER).apply {
                            putExtra(Intent.EXTRA_INTENT, galleryIntent)
                            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                        }

                        fileChooserLauncher.launch(chooser)
                    }

                    return true
                }

                override fun onGeolocationPermissionsShowPrompt(
                    origin: String?,
                    callback: GeolocationPermissions.Callback?
                ) {

                    callback?.invoke(origin, true, false)
                }
            }

            webViewClient = object : WebViewClient() {

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: android.graphics.Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    ProgressHelper.disableProgressDialog()
                    binding.noInternetAvailableLayout.visibility = View.GONE
                    binding.webView.visibility = View.VISIBLE
                    binding.webView.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()

                    if (containsFarmerId(this@ChatbotActivity)) {
                        leaderboardViewModel.updateUserPoints(
                            this@ChatbotActivity,
                            CHATBOT_POINT
                        )
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)

                    Log.e(
                        TAG,
                        "WebView Error: ${error?.description}"
                    )

                    onChatbotError()
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {

                    Log.e(TAG, "SSL Error: $error")

                    // DO NOT PROCEED IN PRODUCTION
                    handler?.cancel()

                    onChatbotError()
                }
            }
        }
    }

    private fun observeResponse() {
        mahavistaarViewModel.responseUrlForChatBot.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    val response = JSONObject(state.data.toString())
                    handleChatbotResponse(response)
                    ProgressHelper.disableProgressDialog()
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    onChatbotError()
                }
            }
        }
    }

    private fun handleChatbotResponse(response: Any?) {

        try {

            if (response == null) {
                onChatbotError()
                return
            }
            val jsonObject = JSONObject(response.toString())
            val status = jsonObject.optString("status")
            if (status.equals("success", ignoreCase = true)) {
                val jwtToken = jsonObject.optString("token").trim()
                if (jwtToken.isEmpty()) {
                    onChatbotError()
                    return
                }
                val chatBotUrl =
                    "${AppEnvironment.BOT_URL.baseUrl}$jwtToken"
                loadChatbot(chatBotUrl)
            } else {
                onChatbotError()
            }

        } catch (_: Exception) {
            onChatbotError()
        }
    }

    private fun loadChatbot(chatBotUrl: String) {
        try {
            Clarity.sendCustomEvent("WEBVIEW_OPENED")
            binding.webView.loadUrl(chatBotUrl)
        } catch (_: Exception) {
            onChatbotError()
        }
    }

    private fun onChatbotError() {
        ProgressHelper.disableProgressDialog()
        Clarity.sendCustomEvent("WEBVIEW_STOPPED")
        binding.webView.visibility = View.GONE
        binding.noInternetAvailableLayout.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        Clarity.sendCustomEvent("WEBVIEW_RESUMED")
    }

    override fun onPause() {
        super.onPause()
        Clarity.sendCustomEvent("WEBVIEW_PAUSED")
    }

    override fun onDestroy() {
        filePathCallback = null
        cameraImageUri = null

        binding.webView.apply {
            stopLoading()
            clearHistory()
            clearCache(true)
            destroy()
        }
        Clarity.sendCustomEvent("WEBVIEW_CLOSED")
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { _, _ ->
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad =
            if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true))
                "en"
            else
                "mr"
        val updatedContext = configureLocale(newBase, languageToLoad)
        super.attachBaseContext(updatedContext)
    }
}
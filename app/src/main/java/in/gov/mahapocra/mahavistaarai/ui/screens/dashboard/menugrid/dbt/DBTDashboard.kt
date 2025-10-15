package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationProvider
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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
        binding= ActivityDbtdashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setUpViews()
        val agristackId = AppSettings.getInstance().getValue(this, AppConstants.AGRISTACKID, "")
        Log.d("TAGGER", "loadWebView: $agristackId")
        if (agristackId!="" || agristackId !=null || agristackId != "null") {
            loadWebView(encodeToBase64(agristackId))
        }else{
            loadWebView("")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()){
                    binding.webView.goBack()
                }else{
                    startActivity(Intent(this@DBTDashboard, DBTActivity::class.java))
                }
            }
        })
    }

    private fun setUpViews() {
        binding.layoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.layoutToolbar.imgBackArrow.setOnClickListener {
            if (binding.webView.canGoBack()){
                binding.webView.goBack()
            }else{
                startActivity(Intent(this@DBTDashboard, DBTActivity::class.java))
            }
        }
        binding.layoutToolbar.textViewHeaderTitle.text = ""
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadWebView(encrypterFarmerId: String) {
        val location = getLocationUsingLocationManager(this)
        val lat = location?.latitude
        val long = location?.longitude
        val urlForLoadWeb = if (encrypterFarmerId.isEmpty()){
            "https://uat-dbt.mahapocra.gov.in:8006/MahavistaarLoginAuth"
        }else{
            "https://uat-dbt.mahapocra.gov.in:8006/MahavistaarLoginAuth" +
                    "?farmerid=$encrypterFarmerId" +
                    "&ip=${getMobileOrWifiIp()}" +
                    "&details=Chrome_Windows10, latitude=$lat, longitude=$long"
        }

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true

        binding.webView.webViewClient = WebViewClient()

        binding.webView.webChromeClient = object : WebChromeClient() {
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
        }

        binding.webView.loadUrl(urlForLoadWeb)
    }


}
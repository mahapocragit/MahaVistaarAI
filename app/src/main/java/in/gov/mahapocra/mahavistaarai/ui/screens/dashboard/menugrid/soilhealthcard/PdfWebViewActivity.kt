package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPdfViewBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.GisViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONObject
import java.util.Locale

class PdfWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private lateinit var gisViewModel: GisViewModel
    private lateinit var soilTestResultAdapter: SoilTestResultAdapter
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

        gisViewModel = ViewModelProvider(this)[GisViewModel::class.java]
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
        val shcNumber = intent.getStringExtra("shcNumber")
        Log.d("TAGGER", "onCreate: $shcNumber")
        shcNumber?.let { gisViewModel.fetchSoilHealthCardDetailsFromSHCNumber(this, it) }
        observeResponse()
    }

    private fun observeResponse() {
        gisViewModel.shcInformationResponse.observe(this){
            if (it!=null){
                val jsonObject = JSONObject(it.toString())
                val basicInfo = jsonObject.getJSONArray("basic_info")[0] as JSONObject
                val soilTestResultJson = jsonObject.getJSONArray("soil_test_result")
                val soilTestResultAdapter = SoilTestResultAdapter(soilTestResultJson)
                binding.soilHealthCardLayout.farmerName.text = basicInfo.optString("farmer_name")
                binding.soilHealthCardLayout.shcNo.text = basicInfo.optString("shc_no")
                binding.soilHealthCardLayout.villageTextView.text = basicInfo.optString("village")
                binding.soilHealthCardLayout.talukaTextView.text = basicInfo.optString("taluka")
                binding.soilHealthCardLayout.districtTextView.text = basicInfo.optString("district")
                binding.soilHealthCardLayout.soilTestResultRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.soilHealthCardLayout.soilTestResultRecyclerView.adapter = soilTestResultAdapter
                soilTestResultAdapter.notifyDataSetChanged()
            }
        }
    }


}
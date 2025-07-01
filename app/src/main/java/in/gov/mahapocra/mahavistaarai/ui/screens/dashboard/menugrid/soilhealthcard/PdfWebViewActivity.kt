package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPdfViewBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.GisViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import org.json.JSONObject

class PdfWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private lateinit var gisViewModel: GisViewModel
    private lateinit var soilTestResultAdapter: SoilTestResultAdapter
    private lateinit var fertilizerRecommendationAdapter: FertilizerRecommendationAdapter
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PdfWebViewActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gisViewModel = ViewModelProvider(this)[GisViewModel::class.java]
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.soil_health_card)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val url = intent.getStringExtra("pdf_url")
        binding.floatingActionButton.setOnClickListener {
            url?.let { it1 -> LocalCustom.downloadPdf(this, it1) }
        }
        val shcNumber = intent.getStringExtra("shcNumber")
        shcNumber?.let {
            gisViewModel.fetchSoilHealthCardDetailsFromSHCNumber(
                this,
                it,
                languageToLoad
            )
        }
        ProgressHelper.showProgressDialog(this)
        observeResponse()
    }

    private fun observeResponse() {
        gisViewModel.shcInformationResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val basicInfo = jsonObject.getJSONArray("basic_info")[0] as JSONObject
                binding.soilHealthCardLayout.farmerName.text = basicInfo.optString("farmer_name")
                binding.soilHealthCardLayout.shcNo.text = basicInfo.optString("shc_no")
                binding.soilHealthCardLayout.villageTextView.text = basicInfo.optString("village")
                binding.soilHealthCardLayout.talukaTextView.text = basicInfo.optString("taluka")
                binding.soilHealthCardLayout.districtTextView.text = basicInfo.optString("district")

                val soilTestResultJson = jsonObject.getJSONArray("soil_test_result")
                soilTestResultAdapter = SoilTestResultAdapter(soilTestResultJson)
                binding.soilHealthCardLayout.soilTestResultRecyclerView.layoutManager =
                    LinearLayoutManager(this)
                binding.soilHealthCardLayout.soilTestResultRecyclerView.adapter =
                    soilTestResultAdapter
                soilTestResultAdapter.notifyDataSetChanged()

                val fertilizerRecommendationJson =
                    jsonObject.getJSONArray("fertilizer_recommendation")
                fertilizerRecommendationAdapter =
                    FertilizerRecommendationAdapter(fertilizerRecommendationJson)
                binding.soilHealthCardLayout.fertilizerRecommendationRecyclerView.layoutManager =
                    LinearLayoutManager(this)
                binding.soilHealthCardLayout.fertilizerRecommendationRecyclerView.adapter =
                    fertilizerRecommendationAdapter
                fertilizerRecommendationAdapter.notifyDataSetChanged()
            }
        }

        gisViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPdfViewBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.FertilizerRecommendationAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.SoilTestResultAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.GisViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.SOIL_HEALTH_CARD_POINT
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FarmerHelper.containsFarmerId
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONArray
import org.json.JSONObject

class DetailedSoilHealthCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private val gisViewModel: GisViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var soilTestResultAdapter: SoilTestResultAdapter
    private lateinit var fertilizerRecommendationAdapter: FertilizerRecommendationAdapter
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DetailedSoilHealthCardActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

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

        leaderboardViewModel.responseUpdateUserPoints.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status == 200) {
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }

        gisViewModel.shcInformationResponse.observe(this) { response ->
            ProgressHelper.disableProgressDialog()
            response ?: return@observe
            try {
                val jsonObject = JSONObject(response.toString())

                val basicInfoArray = jsonObject.optJSONArray("basic_info")
                val basicInfo = basicInfoArray?.optJSONObject(0)

                basicInfo?.let {
                    binding.soilHealthCardLayout.apply {
                        farmerName.text = it.optString("farmer_name")
                        shcNo.text = it.optString("shc_no")
                        villageTextView.text = it.optString("village")
                        talukaTextView.text = it.optString("taluka")
                        districtTextView.text = it.optString("district")
                    }
                }

                val soilTestResultJson = jsonObject.optJSONArray("soil_test_result") ?: JSONArray()
                soilTestResultAdapter = SoilTestResultAdapter(soilTestResultJson)
                binding.soilHealthCardLayout.soilTestResultRecyclerView.adapter =
                    soilTestResultAdapter

                val fertilizerRecommendationJson =
                    jsonObject.optJSONArray("fertilizer_recommendation") ?: JSONArray()
                fertilizerRecommendationAdapter =
                    FertilizerRecommendationAdapter(fertilizerRecommendationJson)
                binding.soilHealthCardLayout.fertilizerRecommendationRecyclerView.adapter =
                    fertilizerRecommendationAdapter
                if (containsFarmerId(this)) {
                    leaderboardViewModel.updateUserPoints(this, SOIL_HEALTH_CARD_POINT)
                }

            } catch (e: Exception) {
                e.printStackTrace()
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
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.mahadbt

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMahaDbtSchemesDetailsBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.FarmerMahaDbtSchemeDetailsRecyclerAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.DBT_SCHEME_POINT
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONArray
import org.json.JSONObject
import kotlin.getValue

class MahaDbtSchemesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMahaDbtSchemesDetailsBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private var languageToLoad: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Language setup
        languageToLoad = if (AppSettings.getLanguage(this) == "2") "mr" else "en"
        switchLanguage(this, languageToLoad)
        binding = ActivityMahaDbtSchemesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        // Header setup
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.dbtschema)
        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        observeResponse()
        // Get and parse JSON data
        val data = intent.getStringExtra("FARMERDBTRESPONSE")
        val jsonData = JSONObject(data ?: "{}")

        // Fallback to empty arrays to avoid crashes
        var mainComponent = jsonData.optJSONArray("MainComponent") ?: JSONArray()
        var importantDocuments = jsonData.optJSONArray("RequiredDocuments") ?: JSONArray()
        var eligibilityCriteria = jsonData.optJSONArray("EligibilityCriteria") ?: JSONArray()

        // Use Marathi fields if selected
        if (languageToLoad == "mr") {
            mainComponent = jsonData.optJSONArray("MainComponentMarathi") ?: JSONArray()
            importantDocuments = jsonData.optJSONArray("RequiredDocumentMarathi") ?: JSONArray()
            eligibilityCriteria = jsonData.optJSONArray("EligibilityCriteriaMarathi") ?: JSONArray()
        }

        // Setup RecyclerViews
        binding.mainComponentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainComponentRecyclerView.adapter =
            FarmerMahaDbtSchemeDetailsRecyclerAdapter(mainComponent)
        binding.mainComponentRecyclerView.visibility = View.GONE

        binding.importantDocumentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.importantDocumentsRecyclerView.adapter =
            FarmerMahaDbtSchemeDetailsRecyclerAdapter(importantDocuments)
        binding.importantDocumentsRecyclerView.visibility = View.GONE

        binding.eligibilityCriteriaRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.eligibilityCriteriaRecyclerView.adapter =
            FarmerMahaDbtSchemeDetailsRecyclerAdapter(eligibilityCriteria)
        binding.eligibilityCriteriaRecyclerView.visibility = View.GONE

        // Toggle on header click
        binding.mainComponentCardTV.setOnClickListener {
            toggleVisibility(
                binding.mainComponentRecyclerView,
                binding.mainComponentToggleImageView
            )
        }
        binding.mainComponentToggleImageView.setOnClickListener {
            toggleVisibility(
                binding.mainComponentRecyclerView,
                binding.mainComponentToggleImageView
            )
        }

        binding.importantDocumentsCardTV.setOnClickListener {
            toggleVisibility(
                binding.importantDocumentsRecyclerView,
                binding.requiredDocumentToggleImageView
            )
        }
        binding.requiredDocumentToggleImageView.setOnClickListener {
            toggleVisibility(
                binding.importantDocumentsRecyclerView,
                binding.requiredDocumentToggleImageView
            )
        }

        binding.eligibilityCriteriaCardTV.setOnClickListener {
            toggleVisibility(
                binding.eligibilityCriteriaRecyclerView,
                binding.eligibilityCriteriaToggleImageView
            )
        }
        binding.eligibilityCriteriaToggleImageView.setOnClickListener {
            toggleVisibility(
                binding.eligibilityCriteriaRecyclerView,
                binding.eligibilityCriteriaToggleImageView
            )
        }
        leaderboardViewModel.updateUserPoints(this, DBT_SCHEME_POINT)
    }

    private fun observeResponse() {
        leaderboardViewModel.responseUpdateUserPoints.observe(this){ response->
            if (response!=null){
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status==200){
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }
    }

    private fun toggleVisibility(view: View, imageView: ImageView) {
        view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        imageView.setImageResource(
            if (view.visibility == View.VISIBLE)
                R.drawable.ic_baseline_arrow_drop_up_24
            else
                R.drawable.ic_baseline_arrow_drop_down_24
        )
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase) == "1") "en" else "mr"
        val updatedContext = configureLocale(newBase, languageToLoad)
        super.attachBaseContext(updatedContext)
    }
}
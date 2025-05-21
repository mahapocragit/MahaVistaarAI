package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.mahadbt

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMahaDbtSchemesDetailsBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONArray
import org.json.JSONObject

class MahaDbtSchemesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMahaDbtSchemesDetailsBinding
    private var languageToLoad: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Language setup
        languageToLoad = if (AppSettings.getLanguage(this) == "2") "mr" else "en"
        switchLanguage(this, languageToLoad)
        binding = ActivityMahaDbtSchemesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Header setup
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.dbtschema)
        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Get and parse JSON data
        val data = intent.getStringExtra("FARMERDBTRESPONSE")
        val jsonData = JSONObject(data ?: "{}")
        Log.d("TAGGER", "onCreate: $jsonData")

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
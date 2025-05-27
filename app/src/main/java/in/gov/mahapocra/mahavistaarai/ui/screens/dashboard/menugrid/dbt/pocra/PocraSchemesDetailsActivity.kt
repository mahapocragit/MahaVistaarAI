package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.pocra

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDbtSchemesDetailsBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONObject

class PocraSchemesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDbtSchemesDetailsBinding
    private var languageToLoad: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = if (AppSettings.getLanguage(this@PocraSchemesDetailsActivity)
                .equals("2", ignoreCase = true)) { "mr" } else { "en" }
        switchLanguage(this, languageToLoad)
        binding = ActivityDbtSchemesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.dbtschema)
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val data = intent.getStringExtra("FARMERDBTRESPONSE")
        val jsonData = JSONObject(data);
        var importantDocuments = jsonData.optString("importantDocuments")
        var eligibilityCriteria = jsonData.optString("eligibilityCriteria")

        if (languageToLoad == "mr") {
            importantDocuments = jsonData.optString("importantDocumentsMr")
            eligibilityCriteria = jsonData.optString("eligibilityCriteriaMr")
        }

        val importantDocumentsArray = importantDocuments.split(";")
        val eligibilityCriteriaArray = eligibilityCriteria.split(";")

        binding.importantDocumentsCardTV.setOnClickListener {
            openRecyclerView(binding.importantDocumentsRecyclerView)
        }

        binding.importantDocImageView.setOnClickListener {
            openRecyclerView(binding.importantDocumentsRecyclerView)
        }

        binding.eligibilityCriteriaCardTV.setOnClickListener {
            openRecyclerView(binding.eligibilityCriteriaRecyclerView)
        }

        binding.eligibilityCriteriaCardIV.setOnClickListener {
            openRecyclerView(binding.eligibilityCriteriaRecyclerView)
        }

        binding.importantDocumentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.importantDocumentsRecyclerView.adapter =
            PocraSchemeDetailsRecyclerAdapter(importantDocumentsArray)

        binding.eligibilityCriteriaRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.eligibilityCriteriaRecyclerView.adapter =
            PocraSchemeDetailsRecyclerAdapter(eligibilityCriteriaArray)
    }

    private fun openRecyclerView(recyclerView: RecyclerView) {
        if (recyclerView.visibility == View.VISIBLE) {
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
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

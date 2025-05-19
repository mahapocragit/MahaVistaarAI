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
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONArray
import org.json.JSONObject

class MahaDbtSchemesDetailsActivity : AppCompatActivity() {

    private lateinit var textViewHeaderTitle: TextView
    private lateinit var imageMenushow: ImageView
    private lateinit var mainComponentRecyclerView: RecyclerView
    private lateinit var importantDocumentsRecyclerView: RecyclerView
    private lateinit var eligibilityCriteriaRecyclerView: RecyclerView
    private lateinit var mainComponentCardTV: TextView
    private lateinit var importantDocumentsCardTV: TextView
    private lateinit var eligibilityCriteriaCardTV: TextView
    private var languageToLoad: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Language setup
        languageToLoad = if (AppSettings.getLanguage(this) == "2") "mr" else "en"
        switchLanguage(this, languageToLoad)
        setContentView(R.layout.activity_maha_dbt_schemes_details)
        // Bind views
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
        mainComponentRecyclerView = findViewById(R.id.mainComponentRecyclerView)
        importantDocumentsRecyclerView = findViewById(R.id.importantDocumentsRecyclerView)
        eligibilityCriteriaRecyclerView = findViewById(R.id.eligibilityCriteriaRecyclerView)
        mainComponentCardTV = findViewById(R.id.mainComponentCardTV)
        importantDocumentsCardTV = findViewById(R.id.importantDocumentsCardTV)
        eligibilityCriteriaCardTV = findViewById(R.id.eligibilityCriteriaCardTV)

        // Header setup
        textViewHeaderTitle.setText(R.string.dbtschema)
        imageMenushow.visibility = View.VISIBLE
        imageMenushow.setOnClickListener {
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
        mainComponentRecyclerView.layoutManager = LinearLayoutManager(this)
        mainComponentRecyclerView.adapter = FarmerMahaDbtSchemeDetailsRecyclerAdapter(mainComponent)
        mainComponentRecyclerView.visibility = View.GONE

        importantDocumentsRecyclerView.layoutManager = LinearLayoutManager(this)
        importantDocumentsRecyclerView.adapter = FarmerMahaDbtSchemeDetailsRecyclerAdapter(importantDocuments)
        importantDocumentsRecyclerView.visibility = View.GONE

        eligibilityCriteriaRecyclerView.layoutManager = LinearLayoutManager(this)
        eligibilityCriteriaRecyclerView.adapter = FarmerMahaDbtSchemeDetailsRecyclerAdapter(eligibilityCriteria)
        eligibilityCriteriaRecyclerView.visibility = View.GONE

        // Toggle on header click
        mainComponentCardTV.setOnClickListener {
            toggleVisibility(mainComponentRecyclerView)
        }

        importantDocumentsCardTV.setOnClickListener {
            toggleVisibility(importantDocumentsRecyclerView)
        }

        eligibilityCriteriaCardTV.setOnClickListener {
            toggleVisibility(eligibilityCriteriaRecyclerView)
        }
    }

    private fun toggleVisibility(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase) == "1") "en" else "mr"
        val updatedContext = configureLocale(newBase, languageToLoad)
        super.attachBaseContext(updatedContext)
    }
}
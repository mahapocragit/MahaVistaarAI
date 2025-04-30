package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONObject

class DbtSchemesDetailsActivity : AppCompatActivity() {

    private lateinit var textViewHeaderTitle: TextView
    private lateinit var imageMenushow: ImageView
    private lateinit var importantDocumentsRecyclerView: RecyclerView
    private lateinit var eligibilityCriteriaRecyclerView: RecyclerView
    private lateinit var importantDocumentsCardTV: TextView
    private lateinit var eligibilityCriteriaCardTV: TextView
    var languageToLoad: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dbt_schemes_details)

        if (AppSettings.getLanguage(this@DbtSchemesDetailsActivity).equals("2", ignoreCase = true)) {
            languageToLoad = "mr"
        } else {
            languageToLoad = "en"
        }
        LocalCustom.configureLocale(baseContext, languageToLoad)

        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
        importantDocumentsRecyclerView = findViewById(R.id.importantDocumentsRecyclerView)
        eligibilityCriteriaRecyclerView = findViewById(R.id.eligibilityCriteriaRecyclerView)
        importantDocumentsCardTV = findViewById(R.id.importantDocumentsCardTV)
        eligibilityCriteriaCardTV = findViewById(R.id.eligibilityCriteriaCardTV)

        imageMenushow.visibility = View.VISIBLE
        textViewHeaderTitle.setText(R.string.dbtschema)
        imageMenushow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val data = intent.getStringExtra("FARMERDBTRESPONSE")
        val jsonData = JSONObject(data);
        var importantDocuments = jsonData.optString("importantDocuments")
        var eligibilityCriteria = jsonData.optString("eligibilityCriteria")

        if (languageToLoad=="mr"){
            importantDocuments = jsonData.optString("importantDocumentsMr")
            eligibilityCriteria = jsonData.optString("eligibilityCriteriaMr")
        }

        val importantDocumentsArray = importantDocuments.split(";")
        val eligibilityCriteriaArray = eligibilityCriteria.split(";")

        importantDocumentsCardTV.setOnClickListener {
            openRecyclerView(importantDocumentsRecyclerView)
        }

        eligibilityCriteriaCardTV.setOnClickListener {
            openRecyclerView(eligibilityCriteriaRecyclerView)
        }

        importantDocumentsRecyclerView.layoutManager = LinearLayoutManager(this)
        importantDocumentsRecyclerView.adapter = FarmerDbtSchemeDetailsRecyclerAdapter(importantDocumentsArray)

        eligibilityCriteriaRecyclerView.layoutManager = LinearLayoutManager(this)
        eligibilityCriteriaRecyclerView.adapter = FarmerDbtSchemeDetailsRecyclerAdapter(eligibilityCriteriaArray)
    }

    private fun openRecyclerView(recyclerView: RecyclerView) {
        if (recyclerView.visibility==View.VISIBLE) {
            recyclerView.visibility = View.GONE
        }else{
            recyclerView.visibility = View.VISIBLE
        }
    }
}
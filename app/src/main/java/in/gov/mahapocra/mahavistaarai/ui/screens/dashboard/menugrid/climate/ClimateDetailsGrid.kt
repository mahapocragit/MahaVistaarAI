package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ClimateGridModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityClimateDetailsGridBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ClimateGridAdapter
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive

class ClimateDetailsGrid : AppCompatActivity() {

    private lateinit var binding: ActivityClimateDetailsGridBinding
    private val climateModelArrayList = ArrayList<ClimateGridModel>()
    private var languageToLoad = "mr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLanguage()

        binding = ActivityClimateDetailsGridBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiResponsive(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.climate_resilient_technology)

        setupGridData()
        setupClicks()
        setupBackPressed()
    }

    private fun setupLanguage() {
        languageToLoad = if (AppSettings.getLanguage(this).equals("1", true)) "en" else "mr"
        switchLanguage(this, languageToLoad)
    }

    private fun setupGridData() {
        val bundle = intent.extras ?: return

        val groupNames = bundle.getStringArrayList("GroupName") ?: arrayListOf()
        val imagePaths = bundle.getStringArrayList("GroupImagePath") ?: arrayListOf()
        val webUrls = bundle.getStringArrayList("WebUrl") ?: arrayListOf()

        climateModelArrayList.clear()

        groupNames.indices.forEach { i ->
            climateModelArrayList.add(
                ClimateGridModel(
                    groupNames[i],
                    imagePaths.getOrNull(i) ?: "",
                    webUrls.getOrNull(i) ?: ""
                )
            )
        }

        val adapter = ClimateGridAdapter(this, climateModelArrayList, "ClimateDetailsGrid")
        binding.gridViewJobs.adapter = adapter
    }

    private fun setupClicks() {
        binding.gridViewJobs.setOnItemClickListener { _, _, position, _ ->
            val url = climateModelArrayList[position].webUrl
            startActivity(
                Intent(this, ResilientWebUrl::class.java).apply {
                    putExtra("webUrl", url)
                }
            )
        }

        binding.relativeLayoutTopBar.imgBackArrow.apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun attachBaseContext(newBase: Context) {
        val language = if (AppSettings.getLanguage(newBase).equals("1", true)) "en" else "mr"
        super.attachBaseContext(configureLocale(newBase, language))
    }
}
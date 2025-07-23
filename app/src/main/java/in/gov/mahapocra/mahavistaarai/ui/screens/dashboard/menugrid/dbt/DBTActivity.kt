package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDbtactivityBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.mahadbt.MahaDbtSchemesActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.pocra.PocraDbtSchemes
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive

class DBTActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDbtactivityBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DBTActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityDbtactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiResponsive(binding.root, window)
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.dbtschema)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.pocraDBTCardView.setOnClickListener {
            startActivity(Intent(this, PocraDbtSchemes::class.java).apply {
                putExtra("dbtFromDashboard", "pocraDBTCardView")
            })
        }

        binding.mahaDBTCardView.setOnClickListener {
            startActivity(Intent(this, MahaDbtSchemesActivity::class.java).apply {
                putExtra("dbtFromDashboard", "mahaDBTCardView")
            })
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
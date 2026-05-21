package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.magazine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDashboardMagazineBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper

class DashboardMagazineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardMagazineBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityDashboardMagazineBinding.inflate(layoutInflater)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DashboardMagazineActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.textViewHeaderTitle.text = getString(R.string.dashboard_farm_magazine)
        binding.toolbar.textViewHeaderTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            16f
        )
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            AppHelper(this).redirectToPage(2)
            finish()
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                AppHelper(this@DashboardMagazineActivity).redirectToPage(2)
                finish()
            }
        })

        binding.farmMagazineCard.setOnClickListener {
            startActivity(Intent(this, FarmMagazineDashboardActivity::class.java))
        }
        binding.animalHusbandryCard.setOnClickListener {
            startActivity(Intent(this, MagazineDashboardActivity::class.java))
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
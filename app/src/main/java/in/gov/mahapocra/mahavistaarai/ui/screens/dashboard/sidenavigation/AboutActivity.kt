package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.SettingItem
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAboutBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.GridAdapter
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AboutActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        if (languageToLoad == "en") {
            binding.aboutAppIcon.setImageResource(R.drawable.about_logo_en)
        }
        binding.toolbarLayout.imgBackArrow.visibility = View.VISIBLE
        binding.toolbarLayout.imgBackArrow.setOnClickListener {
            AppHelper(this@AboutActivity).redirectToHome()
        }
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                AppHelper(this@AboutActivity).redirectToHome()
            }
        })
        binding.toolbarLayout.textViewHeaderTitle.text = getString(R.string.about)

        val items = listOf(
            SettingItem(R.drawable.mpkv_logo, "MPKV Rahuri"),
            SettingItem(R.drawable.pdkv_akola_ic, "PDKV Akola"),
            SettingItem(R.drawable.cnmkv_ic, "VNMKV Parbhani"),

            SettingItem(R.drawable.bskv_ic, "BSKKV Dapoli"),
            SettingItem(R.drawable.pocra_latest_logo, "POCRA"),
            SettingItem(R.drawable.ek_step_ic, "EkStep Foundation"),

            SettingItem(R.drawable.fide_ic, "FIDE"),
            SettingItem(R.drawable.coss_ic, "COSS"),
            SettingItem(R.drawable.ic_imd, "IMD"),

            SettingItem(R.drawable.mahavedh_ic, "Mahavedh"),
            SettingItem(R.drawable.ic_msamb, "MSAMB"),
            SettingItem(R.drawable.ic_mswc, "MSWC"),

            SettingItem(R.drawable.wotr_icon, "WOTR"),
            SettingItem(R.drawable.pani_foundation_logo, "Pani Foundation"),
            SettingItem(
                R.drawable.animal_husbandry_logo,
                "Department of Animal Husbandry and Dairying"
            ),

            SettingItem(R.drawable.matsyavyavasay_icon, "Department of Fisheries"),
            SettingItem(R.drawable.ic_world_bank, "The World Bank")
        )

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = GridAdapter(items)
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
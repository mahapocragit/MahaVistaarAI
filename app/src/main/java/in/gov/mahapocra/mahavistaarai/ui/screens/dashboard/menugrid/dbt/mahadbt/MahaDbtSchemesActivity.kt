package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.mahadbt

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMahaDbtSchemesBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.MahadbtSchemesAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.DbtSchemesViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONObject

class MahaDbtSchemesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMahaDbtSchemesBinding
    private lateinit var languageToLoad: String
    private val dbtSchemesViewModel: DbtSchemesViewModel by viewModels()
    private lateinit var mahadbtSchemesAdapter: MahadbtSchemesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MahaDbtSchemesActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityMahaDbtSchemesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.layoutToolbar.textViewHeaderTitle.text = getString(R.string.maha_dbt_name)
        binding.layoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.layoutToolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.applyForMahaDBTLinearLayout.setOnClickListener {
            val url = "https://mahadbt.maharashtra.gov.in/farmer/login/login"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")

        dbtSchemesViewModel.getMahaDBTSchemes(this)
        ProgressHelper.showProgressDialog(this)
        dbtSchemesViewModel.responseUrlMahaDbtSchemes.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val schemesJSONArray = jsonObject.optJSONArray("data")
                mahadbtSchemesAdapter = MahadbtSchemesAdapter(schemesJSONArray, languageToLoad)
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = mahadbtSchemesAdapter
            }
        }
        dbtSchemesViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
            Log.d("TAGGER", "error: $it")
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
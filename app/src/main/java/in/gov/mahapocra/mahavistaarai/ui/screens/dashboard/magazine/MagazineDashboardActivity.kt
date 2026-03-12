package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.magazine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMagazineDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject

class MagazineDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMagazineDashboardBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@MagazineDashboardActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityMagazineDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.dashboard_magazine)
        binding.toolbar.textViewHeaderTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            16f
        )
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        binding.magazineRecyclerView.layoutManager = GridLayoutManager(this, 2)
        farmerViewModel.getMagazineDetailsResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val responseObject = JSONObject(state.data.toString())
                    val dataArray = responseObject.optJSONArray("data")
                    binding.magazineRecyclerView.adapter = MagazineRecyclerAdapter(dataArray, languageToLoad)
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        farmerViewModel.getMagazineDetails()
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
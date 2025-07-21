package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.etl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAgriStackAdvisoryBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CropRecyclerSapAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONObject

class AgriStackAdvisoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgriStackAdvisoryBinding
    private lateinit var farmerViewModel: FarmerViewModel
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AgriStackAdvisoryActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityAgriStackAdvisoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]

        binding.relativeLayoutTopBar.textViewHeaderTitle.text =
            getString(R.string.etl_crop_advisory)
        binding.relativeLayoutTopBar.textViewHeaderTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            18f
        )
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        observeResponse()
        val villageCode = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        if (villageCode!=0) {
            farmerViewModel.getCropSapAdvisory(this, villageCode) //TODO: static villageCode code 537820
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardScreen::class.java))
    }

    private fun observeResponse() {
        farmerViewModel.getCropSapAdvisoryResponse.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val jsonArray = jsonObject.optJSONArray("advisory")
                if (jsonArray?.length() != 0) {
                    binding.agriStackRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(this@AgriStackAdvisoryActivity)
                        adapter = CropRecyclerSapAdapter(jsonArray)
                    }
                }
            }
        }
        farmerViewModel.error.observe(this) {
            Log.d("TAGGER", "onCreate: $it")
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
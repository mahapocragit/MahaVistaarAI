package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityFarmDetailsBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.LandAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONObject

class FarmDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmDetailsBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var languageToLoad: String = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@FarmDetailsActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        binding = ActivityFarmDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeResponse()
        init()
    }

    private fun init() {
        binding.relativeLayoutTopBar.textViewHeaderTitle.text =
            getString(R.string.farm_lands_crops)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            AppHelper(this).redirectToHome()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AppHelper(this@FarmDetailsActivity).redirectToHome()
            }
        })
        binding.farmDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        farmerViewModel.getFarmDetails()
    }

    private fun observeResponse() {
        farmerViewModel.getFarmDetailsResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val dataObject = jsonObject.optJSONObject("data")
                    val farmsArray = dataObject?.optJSONArray("farm_details")
                    binding.farmDetailsRecyclerView.adapter = farmsArray?.let {
                        LandAdapter(
                            it,
                            languageToLoad
                        )
                    }
                    Log.d(TAG, "observeResponse: $farmsArray")
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
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
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDetailedFarmBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONObject

class DetailedFarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailedFarmBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var farmId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailedFarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        observeResponse()
        init()
    }

    private fun observeResponse() {
        farmerViewModel.saveFarmCropDCSResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }
        farmerViewModel.getFarmCropDCSResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    Log.d(TAG, "observeResponse  getFarmCropDCSResponse: ${state.data}")
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }
    }

    private fun init() {

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "Farm lands & crops"
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this@DetailedFarmActivity, FarmDetailsActivity::class.java))
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@DetailedFarmActivity, FarmDetailsActivity::class.java))
            }
        })

        binding.addCropForFarmLayout.setOnClickListener {
            farmerViewModel.saveFarmCropDCS(
                cropId = CryptoHelper.encryptField("68").toString(),
                sowingDate = CryptoHelper.encryptField("2026-04-30").toString(),
                farmId = CryptoHelper.encryptField(farmId).toString()
            )
        }

        val farmData = intent.getStringExtra("FARM_DETAIL_DATA")
        if (farmData != null) {
            val jsonObject = JSONObject(farmData)
            farmId = jsonObject.optString("farm_id")
            val ownerName = jsonObject.optString("owner_name")
            val surveyNumber = jsonObject.optString("survey_no")
            val villageName = jsonObject.optString("village_name")
            val totalArea = jsonObject.optDouble("total_plot_area")
            binding.nameTextView.text = buildString {
                append("Name")
                append(" $ownerName")
            }
            binding.surveyNumberTextView.text = buildString {
                append("Survey/LPM/Compartment No.")
                append(" $surveyNumber")
            }
            binding.totalAreaTextView.text = buildString {
                append(totalArea)
                append(" acre")
            }
            binding.villageNameTextView.text = villageName
        }

        farmerViewModel.getFarmCropDCS(CryptoHelper.encryptField(farmId).toString())
    }
}
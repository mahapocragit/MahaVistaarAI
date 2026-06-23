package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.newshc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivitySoilHealthCardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import org.json.JSONObject

class HealthCardActivity : AppCompatActivity() {

    private lateinit var languageToLoad: String
    private lateinit var binding: ActivitySoilHealthCardBinding
    private val farmerViewModel: FarmerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@HealthCardActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivitySoilHealthCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.relativeLayoutToolbar.textViewHeaderTitle.text =
            getString(R.string.soil_health_card)
        binding.relativeLayoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutToolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        observeResponse()

//        binding.mobileNoEditText.setText(mobileNo)
//        if (mobileNo != null && mobileNo != "USER_MOBILE") {
//            farmerViewModel.fetchSoilHealthCardDetails(this, mobileNo)
//        } else {
//            binding.noDataFoundImageView.visibility = View.VISIBLE
//            binding.noDataFoundText.visibility = View.VISIBLEA
//            binding.healthCardRecyclerView.visibility = View.GONE
//        }

        binding.submitButton.setOnClickListener {
            val mobileNo =
                binding.mobileNoEditText.text.toString()
            if (mobileNo != null && mobileNo != "USER_MOBILE") {
                farmerViewModel.fetchSoilHealthCardDetails(
                    this,
                    binding.mobileNoEditText.text.toString()
                )
            } else {
                binding.noDataFoundImageView.visibility = View.VISIBLE
                binding.noDataFoundText.visibility = View.VISIBLE
                binding.healthCardRecyclerView.visibility = View.GONE
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            }
        }

        val isGuest =
            AppSettings.getInstance().getBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        binding.chatbotIcon.setOnClickListener {
            if (!isGuest) {
                startActivity(Intent(this, ChatbotActivity::class.java))
            } else {
                AlertDialog.Builder(this)
                    .setMessage(R.string.bot_chat_login_redirect_mesage)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        // Handle login action here
                        startActivity(Intent(this, LoginScreen::class.java).apply {
                            putExtra("from", "dashboard")
                        })
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun observeResponse() {
        farmerViewModel.soilHealthCardDetailsResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val status = jSONObject.optInt("status")
                if (status == 200) {
                    binding.noDataFoundImageView.visibility = View.GONE
                    binding.noDataFoundText.visibility = View.GONE
                    binding.healthCardRecyclerView.visibility = View.VISIBLE
                    val soilHealthCardArray = jSONObject.optJSONArray("shc")
                    binding.healthCardRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.healthCardRecyclerView.adapter =
                        SoilHealthCardAdapter(soilHealthCardArray)
                } else {
                    binding.noDataFoundImageView.visibility = View.VISIBLE
                    binding.noDataFoundText.visibility = View.VISIBLE
                    binding.healthCardRecyclerView.visibility = View.GONE
                }
            } else {
                binding.noDataFoundImageView.visibility = View.VISIBLE
                binding.noDataFoundText.visibility = View.VISIBLE
                binding.healthCardRecyclerView.visibility = View.GONE
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
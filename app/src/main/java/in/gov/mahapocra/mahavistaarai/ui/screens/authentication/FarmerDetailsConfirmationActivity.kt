package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.mahavistaarai.data.helpers.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityFarmerDetailsConfirmationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import org.json.JSONObject

class FarmerDetailsConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerDetailsConfirmationBinding
    private var token = ""
    private lateinit var appPreferenceManager: AppPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFarmerDetailsConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        appPreferenceManager = AppPreferenceManager(this)
        FirebaseHelper(this).getFCMToken { token = it }

        observeResponse()
        setUpListeners()
    }

    private fun setUpListeners() {

        val farmerFetchedData = intent.getStringExtra("farmerFetchedData")

        val farmerData = JSONObject(farmerFetchedData)
        Log.d("TAGGER", "setUpListeners: $farmerData")

        val userName = farmerData.optString("user_name")
        val mobile = farmerData.optString("mobile")
        val districtName = farmerData.optString("district_name")
        val districtCode = farmerData.optString("district_code")
        val talukaName = farmerData.optString("taluka_name")
        val talukaCode = farmerData.optString("taluka_code")
        val villageName = farmerData.optString("village_name")
        val villageCode = farmerData.optString("village_code")

        binding.usernameET.setText(userName)
        binding.mobileET.setText(mobile)
        binding.districtET.setText(districtName)
        binding.talukaET.setText(talukaName)
        binding.villageET.setText(villageName)

        binding.updateProfileDataButton.setOnClickListener {
            appPreferenceManager.saveBoolean("AGRISTACK_LOGIN_DIALOG", true)
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        binding.backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeResponse() {
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.mahavistaarai.data.helpers.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityFarmerDetailsConfirmationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LoginViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import org.json.JSONObject

class FarmerDetailsConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerDetailsConfirmationBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var appPreferenceManager: AppPreferenceManager
    private var token = ""

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

        val farmerFetchedData = intent?.getStringExtra("farmerFetchedData")
        val farmerId = intent?.getStringExtra("farmerId").toString()

        val farmerData = JSONObject(farmerFetchedData)
        Log.d("TAGGER", "setUpListeners: $farmerData")

        val userName = farmerData.optString("user_name")
        val mobile = farmerData.optString("mobile")
        val districtName = farmerData.optString("district_name")
        val talukaName = farmerData.optString("taluka_name")
        val villageName = farmerData.optString("village_name")
        val villageCode = farmerData.optString("village_code")

        binding.usernameET.setText(userName)
        binding.mobileET.setText(mobile)
        binding.districtET.setText(districtName)
        binding.talukaET.setText(talukaName)
        binding.villageET.setText(villageName)

        binding.updateProfileDataButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Overwrite Data?")
                .setMessage("Your data will be overwritten with the data fetched from your Farmer ID. Do you want to continue?")
                .setCancelable(false)
                .setPositiveButton("Confirm") { _, _ ->
                    // Hit API
                    loginViewModel.updateFarmerDetailsById(
                        this,
                        farmerId,
                        userName,
                        mobile,
                        villageCode
                    )
                }
                .create()

            dialog.show()
        }

        binding.backPressIcon.setOnClickListener {
            startActivity(Intent(this, AuthenticateFarmerIdActivity::class.java))
        }

        OnBackPressedDispatcher().addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(
                    Intent(
                        this@FarmerDetailsConfirmationActivity,
                        AuthenticateFarmerIdActivity::class.java
                    )
                )
            }
        })
    }

    private fun observeResponse() {
        loginViewModel.updateFarmerDetailsByIdResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                val message = jSONObject.optString("response")
                if (status == 200) {
                    // Save preference
                    appPreferenceManager.saveBoolean("AGRISTACK_LOGIN_DIALOG", true)
                    startActivity(Intent(this, DashboardScreen::class.java))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(this, AuthenticateFarmerIdActivity::class.java))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
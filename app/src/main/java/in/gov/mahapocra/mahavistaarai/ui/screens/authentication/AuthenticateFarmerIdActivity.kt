package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAuthenticateFarmerIdBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive

class AuthenticateFarmerIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticateFarmerIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityAuthenticateFarmerIdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        observeResponse()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.sendFarmerIdOTPButton.setOnClickListener {
            startActivity(Intent(this, FarmerDetailsConfirmationActivity::class.java))
        }
        binding.backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeResponse() {
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityFarmerDetailsConfirmationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive

class FarmerDetailsConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerDetailsConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFarmerDetailsConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        observeResponse()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.updateProfileDataButton.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        binding.backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeResponse() {
    }
}
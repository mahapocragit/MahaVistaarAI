package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCostCalculatorDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive

class CostCalculatorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCostCalculatorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCostCalculatorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.toolbarLayout.imgBackArrow.visibility = View.VISIBLE
        binding.toolbarLayout.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbarLayout.textViewHeaderTitle.text = getString(R.string.cost_calculator)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(
                    Intent(
                        this@CostCalculatorDashboardActivity,
                        DashboardScreen::class.java
                    )
                )
            }
        })
    }
}
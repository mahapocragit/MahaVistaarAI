package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCropCostCalculationBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom

class CropCostCalculationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropCostCalculationBinding
    private var isIncomeSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCropCostCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)

        binding.toolbarLayout.imgBackArrow.visibility = View.VISIBLE
        binding.toolbarLayout.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbarLayout.textViewHeaderTitle.text = "Kharif Maize Expenses"

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(
                    Intent(
                        this@CropCostCalculationActivity,
                        CostCalculatorDashboardActivity::class.java
                    )
                )
            }
        })

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.addExpenseButton.setOnClickListener {
            // Inflate your custom view
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_expense_layout, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val incomeToggle = dialogView.findViewById<TextView>(R.id.incomeToggleButton)
            val expenseToggle = dialogView.findViewById<TextView>(R.id.expenseToggleButton)
            val cancelText = dialogView.findViewById<TextView>(R.id.cancelText)

            val incomeLayout = dialogView.findViewById<LinearLayout>(R.id.incomeLinearLayout)
            val expenseLayout = dialogView.findViewById<LinearLayout>(R.id.expenseLinearLayout)

            incomeToggle.setOnClickListener {
                isIncomeSelected = true
                incomeLayout.visibility = View.VISIBLE
                expenseLayout.visibility = View.GONE
                incomeToggle.apply {
                    background = ContextCompat.getDrawable(
                        this@CropCostCalculationActivity,
                        R.drawable.shape_left
                    )
                    setTextColor(Color.WHITE)
                }
                expenseToggle.apply {
                    background = ContextCompat.getDrawable(
                        this@CropCostCalculationActivity,
                        R.drawable.shape_right
                    )
                    setTextColor(Color.BLACK)
                }
            }

            expenseToggle.setOnClickListener {
                isIncomeSelected = false
                incomeLayout.visibility = View.GONE
                expenseLayout.visibility = View.VISIBLE
                expenseToggle.apply {
                    background = ContextCompat.getDrawable(
                        this@CropCostCalculationActivity,
                        R.drawable.shape_right_green
                    )
                    setTextColor(Color.WHITE)
                }
                incomeToggle.apply {
                    background = ContextCompat.getDrawable(
                        this@CropCostCalculationActivity,
                        R.drawable.shape_left_white
                    )
                    setTextColor(Color.BLACK)
                }
            }

            cancelText.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
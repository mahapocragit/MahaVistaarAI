package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import CostCalculatorAdapter
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCostCalculatorDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.viewmodels.CostCalculatorViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONObject

class CostCalculatorDashboardActivity : AppCompatActivity(), OnDeleteClick {

    private lateinit var binding: ActivityCostCalculatorDashboardBinding
    private val costCalculatorViewModel: CostCalculatorViewModel by viewModels()
    private var season = 1
    private var currentYear = 0
    private var currentYearForTransaction = 0
    private var currentSeasonForTransaction = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCostCalculatorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)

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

        currentYear = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar.getInstance().get(Calendar.YEAR)
        } else {
            2025
        }
        setUpObservers()

        currentYearForTransaction = AppPreferenceManager(this).getInt("CURRENT_YEAR_FOR_TRANSACTION")
        if (currentYearForTransaction!=0){
            binding.yearTextView.text = "Year: $currentYearForTransaction"
            costCalculatorViewModel.getTotalCostTransactions(this, season, currentYearForTransaction)
        }else{
            costCalculatorViewModel.getTotalCostTransactions(this, season, currentYear)
        }

        setUpListeners()
        binding.seasonText.text = if (currentSeasonForTransaction==1) "Season: Kharif" else "Season: Rabbi"

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.seasonLayout.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.seasonLayout)
            popupMenu.menu.add("Rabbi")
            popupMenu.menu.add("Kharif")

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item) {
                    popupMenu.menu[0] -> {
                        binding.seasonText.text = "Season: Rabbi"
                        season = 2
                        AppPreferenceManager(this).saveInt("CURRENT_SEASON_FOR_TRANSACTION", 2)
                        costCalculatorViewModel.getTotalCostTransactions(this, season, currentYear)
                    }

                    popupMenu.menu[1] -> {
                        binding.seasonText.text = "Season: Kharif"
                        season = 1
                        AppPreferenceManager(this).saveInt("CURRENT_SEASON_FOR_TRANSACTION", 1)
                        costCalculatorViewModel.getTotalCostTransactions(this, season, currentYear)
                    }
                }
                true
            }

            popupMenu.show()
        }
        binding.yearLayout.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.yearLayout)
            popupMenu.menu.add("${currentYear - 1}")
            popupMenu.menu.add("$currentYear")
            popupMenu.menu.add("${currentYear + 1}")

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item) {
                    popupMenu.menu[0] -> {
                        binding.yearTextView.text = "Year: $item"
                        AppPreferenceManager(this).saveInt("CURRENT_YEAR_FOR_TRANSACTION", item.toString().toInt())
                        costCalculatorViewModel.getTotalCostTransactions(
                            this,
                            season,
                            item.toString().toInt()
                        )
                    }

                    popupMenu.menu[1] -> {
                        binding.yearTextView.text = "Year: $item"
                        AppPreferenceManager(this).saveInt("CURRENT_YEAR_FOR_TRANSACTION", item.toString().toInt())
                        costCalculatorViewModel.getTotalCostTransactions(
                            this,
                            season,
                            item.toString().toInt()
                        )
                    }

                    popupMenu.menu[2] -> {
                        binding.yearTextView.text = "Year: $item"
                        AppPreferenceManager(this).saveInt("CURRENT_YEAR_FOR_TRANSACTION", item.toString().toInt())
                        costCalculatorViewModel.getTotalCostTransactions(
                            this,
                            season,
                            item.toString().toInt()
                        )
                    }
                }
                true
            }

            popupMenu.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentYearForTransaction!=0){
            binding.yearTextView.text = "Year: $currentYearForTransaction"
            costCalculatorViewModel.getTotalCostTransactions(this, season, currentYearForTransaction)
        }else{
            costCalculatorViewModel.getTotalCostTransactions(this, season, currentYear)
        }
    }

    private fun setUpObservers() {
        costCalculatorViewModel.addCropForCalculationResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    Toast.makeText(this, "Crop Added successfully", Toast.LENGTH_SHORT).show()
                    if (currentYearForTransaction!=0){
                        binding.yearTextView.text = "Year: $currentYearForTransaction"
                        costCalculatorViewModel.getTotalCostTransactions(this, season, currentYearForTransaction)
                    }else{
                        costCalculatorViewModel.getTotalCostTransactions(this, season, currentYear)
                    }
                } else {
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        costCalculatorViewModel.getTotalCostTransactionsResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    val total = jSONObject.optInt("total")
                    binding.cropTotalProfitTextView.text = "₹$total"
                    val jsonArray = jSONObject.optJSONArray("data")
                    binding.recyclerView.adapter = CostCalculatorAdapter(jsonArray, this)
                } else {
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        costCalculatorViewModel.deleteCropResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    Toast.makeText(this, "Crop Deleted successfully", Toast.LENGTH_SHORT).show()
                    if (currentYearForTransaction!=0){
                        binding.yearTextView.text = "Year: $currentYearForTransaction"
                        costCalculatorViewModel.getTotalCostTransactions(this, season, currentYearForTransaction)
                    }else{
                        costCalculatorViewModel.getTotalCostTransactions(this, season, currentYear)
                    }
                } else {
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setUpListeners() {
        try {
            val cropId = intent.getIntExtra("id", 0)
            val currentYearForTransaction = AppPreferenceManager(this).getInt("CURRENT_YEAR_FOR_TRANSACTION")
            currentSeasonForTransaction = AppPreferenceManager(this).getInt("CURRENT_SEASON_FOR_TRANSACTION")
            if (cropId != 0) {
                Log.d("TAGGER", "setUpListeners: currentSeasonForTransaction $currentSeasonForTransaction")
                if (currentYearForTransaction!=0) {
                    costCalculatorViewModel.addCropForCropCalculation(
                        this,
                        cropId = cropId,
                        season = currentSeasonForTransaction,
                        year = currentYearForTransaction
                    )
                }else{
                    costCalculatorViewModel.addCropForCropCalculation(
                        this,
                        cropId = cropId,
                        season = currentSeasonForTransaction
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("TAGGER", "setUpListeners: ${e.message}")
        }

        binding.addCropCardView.setOnClickListener {
            startActivity(
                Intent(this, AddCropActivity::class.java).putExtra(
                    "callerActivity",
                    "costCalculator"
                )
            )
        }
    }

    override fun onDeleteClick(cropId: Int) {
        costCalculatorViewModel.deleteCrop(this, cropId)
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCostCalculatorDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CostCalculatorAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.CropSelectionAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.CostCalculatorViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject

class CostCalculatorDashboardActivity : AppCompatActivity(), OnDeleteClick {

    private lateinit var binding: ActivityCostCalculatorDashboardBinding
    private val costCalculatorViewModel: CostCalculatorViewModel by viewModels()
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var cropsJsonArray = JSONArray()
    private lateinit var languageToLoad: String
    private var season = 1
    private var currentYear = 0
    private var currentYearForTransaction = 0
    private var currentSeasonForTransaction = 1
    private var isDeleteEnabled: Boolean = false
    private var costCalculatorAdapter = CostCalculatorAdapter(JSONArray(), "en", this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CostCalculatorDashboardActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityCostCalculatorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)
        AppPreferenceManager(this).saveBoolean("COST_CALCULATOR_REDIRECT", true)

        setupToolbar()
        setupBackPress()

        // Initialize class-level year/season from preferences
        currentYear = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar.getInstance().get(Calendar.YEAR)
        } else {
            2025
        }

        currentYearForTransaction = AppPreferenceManager(this)
            .getInt("CURRENT_YEAR_FOR_TRANSACTION", currentYear)

        currentSeasonForTransaction = AppPreferenceManager(this)
            .getInt("CURRENT_SEASON_FOR_TRANSACTION", 1).takeIf { it != 0 } ?: 1

        binding.seasonText.text =
            if (currentSeasonForTransaction == 1) getString(R.string.season_kharif) else getString(R.string.season_rabbi)
        binding.yearTextView.text = buildString {
            append(getString(R.string.year_text))
            append(" ")
            append(currentYearForTransaction)
        }

        setUpObservers()
        setUpListeners()

        // Hit API once on Activity start
        getCurrentYearAllTransactions(currentSeasonForTransaction, currentYearForTransaction)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.toggleDeleteImageView.setOnClickListener {
            isDeleteEnabled = !isDeleteEnabled
            if (isDeleteEnabled) {
                binding.toggleDeleteImageView.setImageResource(R.drawable.ic_cross)
                costCalculatorAdapter.setDeleteEnabled(true)
            } else {
                binding.toggleDeleteImageView.setImageResource(R.drawable.delete_icon)
                costCalculatorAdapter.setDeleteEnabled(false)
            }
        }
    }

    private fun setUpObservers() {
        costCalculatorViewModel.addCropForCalculationResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    Toast.makeText(this, "Crop Added successfully", Toast.LENGTH_SHORT).show()
                    // refresh transactions after adding crop
                    getCurrentYearAllTransactions(
                        currentSeasonForTransaction,
                        currentYearForTransaction
                    )
                } else {
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        costCalculatorViewModel.deleteCostCalculatorCropResponse.observe(this) { response ->
            if (response == null) return@observe

            val jsonObject = JSONObject(response.toString())
            val status = jsonObject.optInt("status")
            val message = jsonObject.optString("response")

            if (status == 200) {

                Toast.makeText(this, "Crop Deleted successfully", Toast.LENGTH_SHORT).show()

                // Toggle delete UI state
                isDeleteEnabled = !isDeleteEnabled
                if (isDeleteEnabled) {
                    binding.toggleDeleteImageView.setImageResource(R.drawable.ic_cross)
                    costCalculatorAdapter.setDeleteEnabled(true)
                } else {
                    binding.toggleDeleteImageView.setImageResource(R.drawable.delete_icon)
                    costCalculatorAdapter.setDeleteEnabled(false)
                }

                // Refresh transactions
                if (currentYearForTransaction != 0) {
                    binding.yearTextView.text = buildString {
                        append(getString(R.string.year_text))
                        append(" ")
                        append(currentYearForTransaction)
                    }

                    getCurrentYearAllTransactions(season, currentYearForTransaction)
                } else {
                    getCurrentYearAllTransactions(season, currentYear)
                }

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        costCalculatorViewModel.getTotalCostTransactionsResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    val total = jSONObject.optInt("total")
                    binding.cropTotalProfitTextView.text =
                        if (total < 0) "-₹${-total}" else "₹$total"
                    val jsonArray = jSONObject.optJSONArray("data")
                    if (jsonArray.length() == 0){
                        binding.notificationNotFoundLayout.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.toggleDeleteImageView.visibility = View.GONE
                    }else{
                        binding.notificationNotFoundLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.toggleDeleteImageView.visibility = View.VISIBLE
                    }
                    AppPreferenceManager(this).saveString("CostCalculatorArrayData", jsonArray?.toString())
                    costCalculatorAdapter = CostCalculatorAdapter(jsonArray, languageToLoad, this)
                    binding.recyclerView.adapter = costCalculatorAdapter
                } else {
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        farmerViewModel.fetchCropsForDCSResponse.observe(this) { state ->
            cropsJsonArray = JSONArray()
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val dataObject = jsonObject.optJSONArray("data")
                    cropsJsonArray = dataObject ?: JSONArray()
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbarLayout.imgBackArrow.visibility = View.VISIBLE
        binding.toolbarLayout.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbarLayout.textViewHeaderTitle.text = getString(R.string.cost_calculator)
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AppHelper(this@CostCalculatorDashboardActivity).redirectToHome()
            }
        })
    }

    private fun setUpListeners() {

        binding.addCropCardView.setOnClickListener {
            showCropSelectionDialog(cropsJsonArray) { selectedCrop ->
                val cropId = selectedCrop.optInt("id")
                if (cropId != 0) {
                    costCalculatorViewModel.addCropForCropCalculation(
                        this,
                        cropId = cropId,
                        season = currentSeasonForTransaction,
                        year = currentYearForTransaction
                    )
                }
            }
        }

        binding.yearLayout.setOnClickListener { showYearPopup() }
        binding.seasonLayout.setOnClickListener { showSeasonPopup() }
        farmerViewModel.fetchCropsForDCS()
    }

    private fun showCropSelectionDialog(
        jsonArray: JSONArray,
        onItemSelected: (JSONObject) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_crop_selection, null)

        val etSearch = dialogView.findViewById<EditText>(R.id.etSearch)
        val ivClose = dialogView.findViewById<ImageView>(R.id.ivClose)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)

        val cropList = mutableListOf<JSONObject>()

        for (i in 0 until jsonArray.length()) {
            cropList.add(jsonArray.getJSONObject(i))
        }


        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val adapter = CropSelectionAdapter(cropList.toMutableList()) { selectedCrop ->
            onItemSelected(selectedCrop)
            dialog.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                val filteredList = cropList.filter {
                    it.optString("name").lowercase().contains(query) ||
                            it.optString("name_mr").lowercase().contains(query)
                }

                adapter.updateList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        dialog.show()
    }

    fun getCurrentYearAllTransactions(selectedSeason: Int, selectedYear: Int) {
        costCalculatorViewModel.getTotalCostTransactions(
            this,
            selectedSeason,
            selectedYear
        )
    }

    private fun showYearPopup() {
        val popupMenu = PopupMenu(this, binding.yearLayout)
        popupMenu.menu.add("${currentYear - 1}")
        popupMenu.menu.add("$currentYear")
        popupMenu.menu.add("${currentYear + 1}")

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            val selectedYear = item.title.toString().toInt()
            binding.yearTextView.text = buildString {
                append(getString(R.string.year_text))
                append(" ")
                append(selectedYear)
            }
            currentYearForTransaction = selectedYear
            AppPreferenceManager(this).saveInt("CURRENT_YEAR_FOR_TRANSACTION", selectedYear)
            getCurrentYearAllTransactions(currentSeasonForTransaction, selectedYear)
            true
        }
        popupMenu.show()
    }

    private fun showSeasonPopup() {
        val popupMenu = PopupMenu(this, binding.seasonLayout)
        popupMenu.menu.add(getString(R.string.rabbi))
        popupMenu.menu.add(getString(R.string.kharif))

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.title.toString()) {
                getString(R.string.rabbi) -> {
                    currentSeasonForTransaction = 2
                    binding.seasonText.text = buildString {
                        append(getString(R.string.season_text))
                        append(" ")
                        append(getString(R.string.rabbi))
                    }
                }

                getString(R.string.kharif) -> {
                    currentSeasonForTransaction = 1
                    binding.seasonText.text = buildString {
                        append(getString(R.string.season_text))
                        append(" ")
                        append(getString(R.string.kharif))
                    }
                }
            }
            AppPreferenceManager(this).saveInt(
                "CURRENT_SEASON_FOR_TRANSACTION",
                currentSeasonForTransaction
            )
            getCurrentYearAllTransactions(currentSeasonForTransaction, currentYearForTransaction)
            true
        }
        popupMenu.show()
    }

    override fun onDeleteClick(cropId: Int, data: JSONObject) {
        if (cropId != 0) {
            costCalculatorViewModel.deleteCostCalculatorCrop(this, cropId)
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
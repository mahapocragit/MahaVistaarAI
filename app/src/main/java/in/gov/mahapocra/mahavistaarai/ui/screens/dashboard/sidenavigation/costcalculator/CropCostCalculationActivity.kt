package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCropCostCalculationBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.DialogAddExpenseLayoutBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.DialogAddIncomeLayoutBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.EditExpenseLayoutBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CropTransactionAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.CostCalculatorViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DateHelper.convertDate
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DateHelper.convertDateFormat
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DateHelper.getTodayDate
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CropCostCalculationActivity : AppCompatActivity(), OnDeleteClick {
    private lateinit var binding: ActivityCropCostCalculationBinding
    private var isIncomeSelected: Boolean = true
    private lateinit var languageToLoad: String
    private val costCalculatorViewModel: CostCalculatorViewModel by viewModels()
    private var jsonArray = JSONArray()
    private var categoryId = 0
    private var cropId = 0
    private var yieldAmount = 0
    private var totalAmount = 0
    private var pricePerUnit = 0
    private var unitMultiplier = 1
    private var calendarDateForUpdate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CropCostCalculationActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityCropCostCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)

        binding.toolbarLayout.imgBackArrow.visibility = View.VISIBLE
        binding.toolbarLayout.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbarLayout.textViewHeaderTitle.text =
            getString(R.string.expense)

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

        binding.cropTransactionRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cropTransactionRecyclerView.hasFixedSize()
        setUpObservers()
        setUpListeners()
    }

    private fun setUpObservers() {
        costCalculatorViewModel.expenseCategoryResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val categoryArray = jSONObject.optJSONArray("data")
                if (categoryArray != null && categoryArray.length() > 0) {
                    jsonArray = categoryArray
                }
            }
        }

        costCalculatorViewModel.getCropCostTransactionsResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val dataObject = jSONObject.getJSONObject("data")
                val income = dataObject.getInt("income")
                val expense = dataObject.getInt("expense")
                val total = dataObject.getInt("total")
                val cropTransactionArray = dataObject.optJSONArray("data")
                binding.totalProfitTextView.text = if (total < 0) "-₹${-total}" else "₹$total"
                binding.incomeTextView.text = if (income < 0) "-₹${-income}" else "₹$income"
                binding.expenseTextView.text = if (expense < 0) "-₹${-expense}" else "₹$expense"
                if (cropTransactionArray != null && cropTransactionArray.length() > 0) {
                    binding.notificationNotFoundLayout.visibility = View.GONE
                    binding.cropTransactionRecyclerView.visibility = View.VISIBLE
                    binding.cropTransactionRecyclerView.adapter =
                        CropTransactionAdapter(cropTransactionArray, languageToLoad, this)
                } else {
                    binding.notificationNotFoundLayout.visibility = View.VISIBLE
                    binding.cropTransactionRecyclerView.visibility = View.GONE
                }
            }
        }

        costCalculatorViewModel.addCropSpecificTransactionsResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                Log.d("TAGGER", "setUpObservers: $jSONObject")

                val currentSelectedYear =
                    AppPreferenceManager(this).getInt("CURRENT_YEAR_FOR_TRANSACTION", 2025)
                val currentSelectedSeason =
                    AppPreferenceManager(this).getInt("CURRENT_SEASON_FOR_TRANSACTION", 1)
                costCalculatorViewModel.getCropSpecificTransactions(
                    this,
                    cropId,
                    season = currentSelectedSeason,
                    year = currentSelectedYear
                )
            }
        }

        costCalculatorViewModel.deleteCropTransactionResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                Log.d("TAGGER", "setUpObservers: $jSONObject")
                val currentSelectedYear =
                    AppPreferenceManager(this).getInt("CURRENT_YEAR_FOR_TRANSACTION", 2025)
                val currentSelectedSeason =
                    AppPreferenceManager(this).getInt("CURRENT_SEASON_FOR_TRANSACTION", 1)
                costCalculatorViewModel.getCropSpecificTransactions(
                    this,
                    cropId,
                    season = currentSelectedSeason,
                    year = currentSelectedYear
                )
            }
        }

        costCalculatorViewModel.updateCropTransactionResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                Log.d("TAGGER", "setUpObservers: $jSONObject")
                val currentSelectedYear =
                    AppPreferenceManager(this).getInt("CURRENT_YEAR_FOR_TRANSACTION", 2025)
                val currentSelectedSeason =
                    AppPreferenceManager(this).getInt("CURRENT_SEASON_FOR_TRANSACTION", 1)
                costCalculatorViewModel.getCropSpecificTransactions(
                    this,
                    cropId,
                    season = currentSelectedSeason,
                    year = currentSelectedYear
                )
            }
        }
    }

    private fun setUpListeners() {

        try {
            cropId = intent.getIntExtra("crop_id", 0)
            val name = intent.getStringExtra("crop_name")
            binding.cropNameTextView.text = name
            binding.cropNameProfitTitle.text = buildString {
                append(name)
                append(" ")
                append(getString(R.string.crops_profit))
            }
            val currentSelectedYear =
                AppPreferenceManager(this).getInt("CURRENT_YEAR_FOR_TRANSACTION", 2025)
            val currentSelectedSeason =
                AppPreferenceManager(this).getInt("CURRENT_SEASON_FOR_TRANSACTION", 1)
            costCalculatorViewModel.getCropSpecificTransactions(
                this,
                cropId,
                season = currentSelectedSeason,
                year = currentSelectedYear
            )
        } catch (e: Exception) {
            Log.d("TAGGER", "setUpListeners: ${e.message}")
        }

        binding.addExpenseButton.setOnClickListener {
            // Inflate your custom view
            DialogAddExpenseLayoutBinding.inflate(layoutInflater)
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_expense_layout, null)
            unitMultiplier = 1
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val incomeToggle = dialogView.findViewById<TextView>(R.id.incomeToggleButton)
            val unitText = dialogView.findViewById<TextView>(R.id.unitText)
            val expenseToggle = dialogView.findViewById<TextView>(R.id.expenseToggleButton)
            val submitText = dialogView.findViewById<TextView>(R.id.submitText)
            val cancelText = dialogView.findViewById<TextView>(R.id.cancelText)
            val totalPriceTextView = dialogView.findViewById<TextView>(R.id.totalPriceTextView)
            val categoryNameTextView = dialogView.findViewById<TextView>(R.id.categoryNameTextView)
            val incomeCalendarDateTextView =
                dialogView.findViewById<TextView>(R.id.incomeCalendarDateTextView)
            val expenseCalendarDateTextView =
                dialogView.findViewById<TextView>(R.id.expenseCalendarDateTextView)
            val incomeNameEditText = dialogView.findViewById<EditText>(R.id.incomeNameEditText)
            val expenseNameEditText = dialogView.findViewById<EditText>(R.id.expenseNameEditText)
            val priceEditText = dialogView.findViewById<EditText>(R.id.priceEditText)
            val yieldText = dialogView.findViewById<EditText>(R.id.yieldText)
            val pricePerUnitText = dialogView.findViewById<EditText>(R.id.pricePerUnitText)
            val incomeDateLinearLayout =
                dialogView.findViewById<LinearLayout>(R.id.incomeDateLinearLayout)
            val expenseDateLinearLayout =
                dialogView.findViewById<LinearLayout>(R.id.expenseDateLinearLayout)
            val unitSelectionLayout =
                dialogView.findViewById<LinearLayout>(R.id.unitSelectionLayout)

            yieldText.addTextChangedListener { editable ->
                yieldAmount = editable?.toString()?.toIntOrNull() ?: 0
                calculateTotal(yieldAmount, pricePerUnit, unitMultiplier, totalPriceTextView)
            }

            unitSelectionLayout.setOnClickListener {
                val popupMenu = PopupMenu(this, unitSelectionLayout)
                popupMenu.menu.add("Quintal")
                popupMenu.menu.add("Kilogram")
                popupMenu.menu.add("Ton")

                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item) {
                        popupMenu.menu[0] -> {
                            unitText.text = "q"
                            unitMultiplier = 100
                            calculateTotal(
                                yieldAmount,
                                pricePerUnit,
                                unitMultiplier,
                                totalPriceTextView
                            )
                        }

                        popupMenu.menu[1] -> {
                            unitText.text = "kg"
                            unitMultiplier = 1
                            calculateTotal(
                                yieldAmount,
                                pricePerUnit,
                                unitMultiplier,
                                totalPriceTextView
                            )
                        }

                        popupMenu.menu[2] -> {
                            unitText.text = "t"
                            unitMultiplier = 1000
                            calculateTotal(
                                yieldAmount,
                                pricePerUnit,
                                unitMultiplier,
                                totalPriceTextView
                            )
                        }
                    }
                    true
                }

                popupMenu.show()
            }

            incomeCalendarDateTextView.text = getTodayDate()
            expenseCalendarDateTextView.text = getTodayDate()
            incomeDateLinearLayout.setOnClickListener { view ->
                showDatePicker(incomeCalendarDateTextView)
            }
            expenseDateLinearLayout.setOnClickListener { view ->
                showDatePicker(expenseCalendarDateTextView)
            }

            pricePerUnitText.addTextChangedListener { editable ->
                pricePerUnit = editable?.toString()?.toIntOrNull() ?: 0
                calculateTotal(
                    yieldAmount,
                    pricePerUnit,
                    unitMultiplier,
                    totalPriceTextView
                )
            }

            val incomeLayout = dialogView.findViewById<LinearLayout>(R.id.incomeLinearLayout)
            val expenseLayout = dialogView.findViewById<LinearLayout>(R.id.expenseLinearLayout)
            val categoryExpenseLayout =
                dialogView.findViewById<LinearLayout>(R.id.categoryExpenseLayout)

            submitText.setOnClickListener {
                val transactionType = if (isIncomeSelected) "income" else "expense"
                if (transactionType == "income") {
                    var transactionName = incomeNameEditText.text.toString()
                    if (transactionName.isEmpty()) {
                        transactionName = "Income"
                    }
                    if (yieldAmount == 0 || pricePerUnit == 0) {
                        Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    } else {
                        costCalculatorViewModel.addCropSpecificTransactions(
                            this,
                            cropId,
                            convertDate(incomeCalendarDateTextView.text.toString()),
                            transactionType,
                            categoryId,
                            transactionName,
                            totalAmount,
                            yieldAmount,
                            pricePerUnit,
                            unitText.text.toString()
                        )
                    }
                } else {
                    val transactionName = expenseNameEditText.text.toString()
                    val price =
                        if (priceEditText.text.toString() == "null" || priceEditText.text.toString() == "") "0" else priceEditText.text.toString()
                    if (categoryId == 0 || price.toInt() == 0) {
                        Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    } else {
                        costCalculatorViewModel.addCropSpecificTransactions(
                            this,
                            cropId,
                            convertDate(expenseCalendarDateTextView.text.toString()),
                            transactionType,
                            categoryId,
                            transactionName,
                            price.toInt()
                        )
                    }
                }

                dialog.dismiss()
            }

            categoryExpenseLayout.setOnClickListener {
                val items = Array(jsonArray.length()) { i ->
                    if (languageToLoad == "en") {
                        jsonArray.getJSONObject(i).getString("name")
                    } else {
                        jsonArray.getJSONObject(i).getString("name_mr")
                    }
                }

                // Create ListView programmatically
                val listView = ListView(this).apply {
                    adapter =
                        ArrayAdapter(
                            this@CropCostCalculationActivity,
                            android.R.layout.simple_list_item_1,
                            items
                        )
                    dividerHeight = 1
                }

                // Build dialog with custom ListView
                val dialog = AlertDialog.Builder(this)
                    .setTitle(R.string.select_option)
                    .setView(listView)
                    .setNegativeButton(R.string.cancel, null)
                    .create()

                // Handle click
                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedObj = jsonArray.getJSONObject(position)
                    categoryId = selectedObj.getInt("id")
                    val name =
                        if (languageToLoad == "en") selectedObj.getString("name") else selectedObj.getString(
                            "name_mr"
                        )
                    categoryNameTextView.text = name
                    dialog.dismiss()
                }

                dialog.show()

                // Limit dialog height (shorter + scrollable)
                dialog.window?.setLayout(
                    (resources.displayMetrics.widthPixels * 0.9).toInt(),
                    (resources.displayMetrics.heightPixels * 0.5).toInt() // 50% of screen height
                )
            }

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
                costCalculatorViewModel.getExpenseCategory()
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

        binding.cropInfoCardView.setOnClickListener {
            val dataArrayStr = AppPreferenceManager(this).getString("CostCalculatorArrayData")
            val dataJsonArray = JSONArray(dataArrayStr)
            val items = Array(dataJsonArray.length()) { i ->
                if (languageToLoad == "en")
                    dataJsonArray.getJSONObject(i).getString("name")
                else
                    dataJsonArray.getJSONObject(i).getString("name_mr")
            }

            val adapter = ArrayAdapter(
                this@CropCostCalculationActivity,
                android.R.layout.simple_list_item_1,
                items
            )

            // Create AlertDialog
            val alertDialog = AlertDialog.Builder(this)
                .setTitle(R.string.select_crop)
                .setAdapter(adapter) { dialog, position ->
                    val selectedObj = dataJsonArray.getJSONObject(position)
                    val selectedCropId = selectedObj.optInt("crop_id")
                    cropId = selectedCropId

                    val selectedCropName = if (languageToLoad == "en")
                        selectedObj.optString("name")
                    else
                        selectedObj.optString("name_mr")

                    binding.cropNameProfitTitle.text = buildString {
                        append(selectedCropName)
                        append(" ")
                        append(getString(R.string.crops_profit))
                    }
                    binding.cropNameTextView.text = selectedCropName
                    val currentSelectedYear =
                        AppPreferenceManager(this).getInt("CURRENT_YEAR_FOR_TRANSACTION", 2025)
                    val currentSelectedSeason =
                        AppPreferenceManager(this).getInt("CURRENT_SEASON_FOR_TRANSACTION", 1)
                    costCalculatorViewModel.getCropSpecificTransactions(
                        this,
                        selectedCropId,
                        season = currentSelectedSeason,
                        year = currentSelectedYear
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            alertDialog.setOnShowListener {
                val listView = alertDialog.listView

                // Add padding
                val padding = (16 * resources.displayMetrics.density).toInt() // 16dp
                listView.setPadding(padding, 0, padding, 0)
                listView.clipToPadding = false

                // Divider
                listView.divider = ContextCompat.getDrawable(this, R.color.font_color_figma)
                listView.dividerHeight = 2

                // Restrict height if content is too large
                val maxHeight = (200 * resources.displayMetrics.density).toInt() // 200dp
                listView.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        resources.displayMetrics.widthPixels, View.MeasureSpec.AT_MOST
                    ),
                    View.MeasureSpec.UNSPECIFIED
                )

                val height =
                    if (listView.measuredHeight > maxHeight) maxHeight else ViewGroup.LayoutParams.WRAP_CONTENT
                alertDialog.window?.setLayout(
                    (resources.displayMetrics.widthPixels * 0.9).toInt(),
                    height
                )
            }

            alertDialog.show()
        }

    }

    private fun calculateTotal(
        yieldAmount: Int,
        pricePerUnit: Int,
        unitMultiplier: Int,
        totalPriceTextView: TextView
    ) {
        val total = yieldAmount * pricePerUnit * unitMultiplier
        totalAmount = total
        totalPriceTextView.text = buildString {
            append("${getString(R.string.totalPrice)}: ₹")
            append(total)
        }
    }

    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this@CropCostCalculationActivity,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Set chosen date to calendar
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Format as dd/MM/yyyy and day name
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val formattedDate = sdf.format(calendar.time)
                calendarDateForUpdate = formattedDate
                textView.text = formattedDate
            },
            year, month, day
        )
        datePickerDialog.show()
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

    override fun onDeleteClick(cropId: Int, data: JSONObject) {
        if (cropId == 0) {
            unitMultiplier = 1
            totalAmount = 0
            costCalculatorViewModel.getExpenseCategory()
            val transactionId = data.optInt("id") // optInt returns 0 if not present
            val transactionType = data.optString("type", "")
            val transactionCropId = data.optInt("crop_id")
            val transactionCategory = data.optString("category", "")
            val transactionCategoryMr = data.optString("category_mr", "")
            val transactionCategoryId = data.optString("category_id", "0").toIntOrNull() ?: 0
            val transactionDate = data.optString("date", "")
            val transactionAmount = data.optString("price", "0")
            val transactionName = data.optString("name", "")
            val transactionYield = data.optString("yield", "0")
            val transactionPricePerUnit = data.optString("price_per_unit", "0")
            val transactionPriceUnit = data.optString("unit", "")

            // safely convert to Int
            totalAmount = transactionAmount.toIntOrNull() ?: 0
            var transactionYieldAmount: Int = transactionYield.toIntOrNull() ?: 0
            var transactionPPUAmount: Int = transactionPricePerUnit.toIntOrNull() ?: 0

            if (transactionType == "income") {
                // INCOME dialog
                val dialogAddIncomeLayoutBinding =
                    DialogAddIncomeLayoutBinding.inflate(LayoutInflater.from(this))

                val dialog = AlertDialog.Builder(this)
                    .setView(dialogAddIncomeLayoutBinding.root) // ✅ correct
                    .create()

                dialogAddIncomeLayoutBinding.cancelText.setOnClickListener {
                    dialog.dismiss()
                }
                dialogAddIncomeLayoutBinding.deleteText.setOnClickListener {
                    costCalculatorViewModel.deleteCropTransaction(this, transactionId)
                    dialog.dismiss()
                }

                dialogAddIncomeLayoutBinding.yieldText.setText(transactionYield)
                dialogAddIncomeLayoutBinding.pricePerUnitText.setText(transactionPricePerUnit)
                dialogAddIncomeLayoutBinding.totalPriceTextView.text = buildString {
                    append(getString(R.string.totalPrice))
                    append(": ₹")
                    append(transactionAmount)
                }
                dialogAddIncomeLayoutBinding.incomeDateLinearLayout.setOnClickListener {
                    showDatePicker(dialogAddIncomeLayoutBinding.incomeCalendarDateTextView)
                }

                dialogAddIncomeLayoutBinding.unitSelectionLayout.setOnClickListener {
                    val popupMenu =
                        PopupMenu(this, dialogAddIncomeLayoutBinding.unitSelectionLayout)
                    popupMenu.menu.add("Quintal")
                    popupMenu.menu.add("Kilogram")
                    popupMenu.menu.add("Ton")

                    popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                        when (item) {
                            popupMenu.menu[0] -> {
                                dialogAddIncomeLayoutBinding.unitText.text = "q"
                                unitMultiplier = 100
                                calculateTotal(
                                    transactionYieldAmount,
                                    transactionPPUAmount,
                                    unitMultiplier,
                                    dialogAddIncomeLayoutBinding.totalPriceTextView
                                )
                            }

                            popupMenu.menu[1] -> {
                                dialogAddIncomeLayoutBinding.unitText.text = "kg"
                                unitMultiplier = 1
                                calculateTotal(
                                    transactionYieldAmount,
                                    transactionPPUAmount,
                                    unitMultiplier,
                                    dialogAddIncomeLayoutBinding.totalPriceTextView
                                )
                            }

                            popupMenu.menu[2] -> {
                                dialogAddIncomeLayoutBinding.unitText.text = "t"
                                unitMultiplier = 1000
                                calculateTotal(
                                    transactionYieldAmount,
                                    transactionPPUAmount,
                                    unitMultiplier,
                                    dialogAddIncomeLayoutBinding.totalPriceTextView
                                )
                            }
                        }
                        true
                    }

                    popupMenu.show()
                }

                dialogAddIncomeLayoutBinding.yieldText.addTextChangedListener { editable ->
                    transactionYieldAmount = editable?.toString()?.toIntOrNull() ?: 0
                    calculateTotal(
                        transactionYieldAmount,
                        transactionPPUAmount,
                        unitMultiplier,
                        dialogAddIncomeLayoutBinding.totalPriceTextView
                    )
                }

                dialogAddIncomeLayoutBinding.pricePerUnitText.addTextChangedListener { editable ->
                    transactionPPUAmount = editable?.toString()?.toIntOrNull() ?: 0
                    calculateTotal(
                        transactionYieldAmount,
                        transactionPPUAmount,
                        unitMultiplier,
                        dialogAddIncomeLayoutBinding.totalPriceTextView
                    )
                }

                dialogAddIncomeLayoutBinding.incomeCalendarDateTextView.text =
                    convertDateFormat(transactionDate)
                dialogAddIncomeLayoutBinding.incomeNameEditText.setText(transactionName)

                dialogAddIncomeLayoutBinding.submitText.setOnClickListener {
                    val dialogTransactionName =
                        dialogAddIncomeLayoutBinding.incomeNameEditText.text.toString()

                    val dialogDateText =
                        dialogAddIncomeLayoutBinding.incomeCalendarDateTextView.text.toString()
                    if (totalAmount == 0) {
                        Toast.makeText(this, "Price field shouldn't be Empty", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    costCalculatorViewModel.updateCropTransaction(
                        this,
                        type = transactionType,
                        cropId = transactionCropId,
                        date = convertDate(dialogDateText),
                        transactionName = dialogTransactionName,
                        priceTotal = totalAmount,
                        yield = transactionYieldAmount,
                        pricePerUnit = transactionPPUAmount,
                        unit = transactionPriceUnit,
                        transactionId = transactionId
                    )
                    dialog.dismiss()
                }
                dialog.show()

            } else {
                // EXPENSE dialog
                val editExpenseLayoutBinding =
                    EditExpenseLayoutBinding.inflate(LayoutInflater.from(this))

                val dialog = AlertDialog.Builder(this)
                    .setView(editExpenseLayoutBinding.root) // ✅ correct
                    .create()

                editExpenseLayoutBinding.cancelText.setOnClickListener {
                    dialog.dismiss()
                }
                editExpenseLayoutBinding.categoryNameTextView.text =
                    if (languageToLoad == "en") transactionCategory else transactionCategoryMr
                editExpenseLayoutBinding.priceEditText2.setText(transactionAmount)
                editExpenseLayoutBinding.expenseNameEditText2.setText(transactionName)
                editExpenseLayoutBinding.expenseCalendarDateTextView.setOnClickListener {
                    showDatePicker(editExpenseLayoutBinding.expenseCalendarDateTextView)
                }
                editExpenseLayoutBinding.expenseCalendarDateTextView.text =
                    convertDateFormat(transactionDate)
                editExpenseLayoutBinding.deleteText.setOnClickListener {
                    costCalculatorViewModel.deleteCropTransaction(this, transactionId)
                    dialog.dismiss()
                }
                editExpenseLayoutBinding.categoryExpenseLayout.setOnClickListener {
                    val items = Array(jsonArray.length()) { i ->
                        if (languageToLoad == "en") {
                            jsonArray.getJSONObject(i).getString("name")
                        } else {
                            jsonArray.getJSONObject(i).getString("name_mr")
                        }
                    }

                    // Create ListView programmatically
                    val listView = ListView(this).apply {
                        adapter =
                            ArrayAdapter(
                                this@CropCostCalculationActivity,
                                android.R.layout.simple_list_item_1,
                                items
                            )
                        dividerHeight = 1
                    }

                    // Build dialog with custom ListView
                    val dialog = AlertDialog.Builder(this)
                        .setTitle(R.string.select_option)
                        .setView(listView)
                        .setNegativeButton(R.string.cancel, null)
                        .create()

                    // Handle click
                    listView.setOnItemClickListener { _, _, position, _ ->
                        val selectedObj = jsonArray.getJSONObject(position)
                        categoryId = selectedObj.getInt("id")
                        val name =
                            if (languageToLoad == "en") selectedObj.getString("name") else selectedObj.getString(
                                "name_mr"
                            )
                        editExpenseLayoutBinding.categoryNameTextView.text = name
                        dialog.dismiss()
                    }

                    dialog.show()

                    // Limit dialog height (shorter + scrollable)
                    dialog.window?.setLayout(
                        (resources.displayMetrics.widthPixels * 0.9).toInt(),
                        (resources.displayMetrics.heightPixels * 0.5).toInt() // 50% of screen height
                    )
                }

                editExpenseLayoutBinding.submitText.setOnClickListener {
                    val dialogTransactionName =
                        editExpenseLayoutBinding.expenseNameEditText2.text.toString()
                    val totalAmount =
                        editExpenseLayoutBinding.priceEditText2.text.toString()
                    val dialogDateText =
                        editExpenseLayoutBinding.expenseCalendarDateTextView.text.toString()
                    if (totalAmount.isEmpty() || totalAmount.isEmpty() || totalAmount.toInt() == 0) {
                        Toast.makeText(this, "Price field shouldn't be Empty", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    val transactionCatId =
                        if (categoryId == 0) transactionCategoryId else categoryId
                    costCalculatorViewModel.updateCropTransaction(
                        this,
                        type = transactionType,
                        cropId = transactionCropId,
                        date = convertDate(dialogDateText),
                        categoryId = transactionCatId,
                        transactionName = dialogTransactionName,
                        priceTotal = totalAmount.toInt(),
                        transactionId = transactionId
                    )
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

}
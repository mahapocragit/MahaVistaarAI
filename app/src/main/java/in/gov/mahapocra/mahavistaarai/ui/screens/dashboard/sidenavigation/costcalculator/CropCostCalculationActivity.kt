package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import CropTransactionAdapter
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCropCostCalculationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.viewmodels.CostCalculatorViewModel
import `in`.gov.mahapocra.mahavistaarai.util.DateHelper.convertDate
import `in`.gov.mahapocra.mahavistaarai.util.DateHelper.getTodayDate
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CropCostCalculationActivity : AppCompatActivity() {
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
                binding.totalProfitTextView.text = "₹$total"
                binding.incomeTextView.text = "₹$income"
                binding.expenseTextView.text = "₹$expense"
                binding.cropTransactionRecyclerView.adapter =
                    CropTransactionAdapter(cropTransactionArray)
            }
        }

        costCalculatorViewModel.addCropSpecificTransactionsResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                Log.d("TAGGER", "setUpObservers: $jSONObject")
                costCalculatorViewModel.getCropSpecificTransactions(this, cropId)
            }
        }
    }

    private fun setUpListeners() {

        try {
            cropId = intent.getIntExtra("crop_id", 0)
            val name = intent.getStringExtra("crop_name")
            val imageUrl = intent.getStringExtra("crop_image")
            binding.cropNameTextView.text = name
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery) // fallback image
                .into(binding.cropImageView)
            costCalculatorViewModel.getCropSpecificTransactions(this, cropId)
        } catch (e: Exception) {
            Log.d("TAGGER", "setUpListeners: ${e.message}")
        }

        binding.addExpenseButton.setOnClickListener {
            // Inflate your custom view
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_expense_layout, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val incomeToggle = dialogView.findViewById<TextView>(R.id.incomeToggleButton)
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

            yieldText.addTextChangedListener { editable ->
                yieldAmount = editable?.toString()?.toIntOrNull() ?: 0
                totalAmount = yieldAmount * pricePerUnit
                Log.d("TAGGER", "setUpListeners: $yieldAmount")
                totalPriceTextView.text = buildString {
                    append("Total Price: ₹")
                    append(totalAmount)
                }
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
                Log.d("TAGGER", "setUpListeners: $pricePerUnit")
                totalAmount = yieldAmount * pricePerUnit
                totalPriceTextView.text = buildString {
                    append("Total Price: ₹")
                    append(totalAmount)
                }
            }

            val incomeLayout = dialogView.findViewById<LinearLayout>(R.id.incomeLinearLayout)
            val expenseLayout = dialogView.findViewById<LinearLayout>(R.id.expenseLinearLayout)
            val categoryExpenseLayout =
                dialogView.findViewById<LinearLayout>(R.id.categoryExpenseLayout)

            submitText.setOnClickListener {
                val transactionType = if (isIncomeSelected) "income" else "expense"
                if (transactionType == "income") {
                    val transactionName = incomeNameEditText.text.toString()
                    if (transactionName == "" || yieldAmount == 0 || pricePerUnit == 0) {
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
                            pricePerUnit
                        )
                    }
                } else {
                    val transactionName = expenseNameEditText.text.toString()
                    val price = priceEditText.text.toString()
                    if (categoryId == 0 || transactionName == "" || price.toInt() == 0) {
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
                    jsonArray.getJSONObject(i).getString("name")
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
                    .setTitle("Select Option")
                    .setView(listView)
                    .setNegativeButton("Cancel", null)
                    .create()

                // Handle click
                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedObj = jsonArray.getJSONObject(position)
                    categoryId = selectedObj.getInt("id")
                    val name = selectedObj.getString("name")
                    categoryNameTextView.text = name
                    Toast.makeText(this, "ID: $categoryId, Name: $name", Toast.LENGTH_SHORT).show()
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
}
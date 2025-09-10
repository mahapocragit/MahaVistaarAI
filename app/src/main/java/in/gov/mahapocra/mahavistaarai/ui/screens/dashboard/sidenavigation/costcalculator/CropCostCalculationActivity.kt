package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCropCostCalculationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.viewmodels.CostCalculatorViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONArray
import org.json.JSONObject

class CropCostCalculationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropCostCalculationBinding
    private var isIncomeSelected: Boolean = false
    private val costCalculatorViewModel: CostCalculatorViewModel by viewModels()
    private var jsonArray = JSONArray()
    private var categoryId = 0
    private var cropId = 0
    private var yieldAmount = 0
    private var pricePerUnit = 1


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
                Log.d("TAGGER", "setUpObservers: $jSONObject")
                binding.totalProfitTextView.text = "₹$total"
                binding.incomeTextView.text = "₹$income"
                binding.expenseTextView.text = "₹$expense"
            }
        }

        costCalculatorViewModel.addCropSpecificTransactionsResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                Log.d("TAGGER", "setUpObservers: $jSONObject")
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
            val yieldText = dialogView.findViewById<EditText>(R.id.yieldText)
            val pricePerUnitText = dialogView.findViewById<EditText>(R.id.pricePerUnitText)

            yieldText.addTextChangedListener { editable ->
                yieldAmount = editable?.toString()?.toIntOrNull() ?: 0
                Log.d("TAGGER", "setUpListeners: $yieldAmount")
                totalPriceTextView.text = "Total Price: ₹${yieldAmount * pricePerUnit}"
            }

            pricePerUnitText.addTextChangedListener { editable ->
                pricePerUnit = editable?.toString()?.toIntOrNull() ?: 1
                if (pricePerUnit == 0) pricePerUnit = 1
                Log.d("TAGGER", "setUpListeners: $pricePerUnit")
                totalPriceTextView.text = "Total Price: ₹${yieldAmount * pricePerUnit}"
            }

            val incomeLayout = dialogView.findViewById<LinearLayout>(R.id.incomeLinearLayout)
            val expenseLayout = dialogView.findViewById<LinearLayout>(R.id.expenseLinearLayout)
            val categoryExpenseLayout =
                dialogView.findViewById<LinearLayout>(R.id.categoryExpenseLayout)

            submitText.setOnClickListener {
                val transactionType = if (isIncomeSelected) "income" else "expense"
                costCalculatorViewModel.addCropSpecificTransactions(
                    this,
                    cropId,
                    transactionType,
                    categoryId
                )
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
}
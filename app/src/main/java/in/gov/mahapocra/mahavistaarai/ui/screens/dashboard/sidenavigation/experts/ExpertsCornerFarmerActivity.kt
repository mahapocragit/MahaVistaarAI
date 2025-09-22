package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.Category
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityExpertsCornerFarmerBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.ExpertsViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONArray
import org.json.JSONObject
import kotlin.getValue

class ExpertsCornerFarmerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpertsCornerFarmerBinding
    private val expertsViewModel: ExpertsViewModel by viewModels()
    private var selectedCategoryId = 0
    private lateinit var selectedCategories: List<Int>
    private lateinit var selectedSubCategories: List<Int>
    private var apiSortCategories: List<Category> = listOf(
        Category(0, "Date (Ascending)"),
        Category(1, "Date (Descending)"),
        Category(2, "Expert Name")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExpertsCornerFarmerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = "Experts Corner"
        binding.toolbar.textViewHeaderTitle.setOnClickListener {
            startActivity(Intent(this, ExpertsCornerFilterActivity::class.java))
        }
        Log.d("TAGGER", "onCreate: $apiSortCategories")

        selectedCategories = intent.getIntegerArrayListExtra("selectedCategories") ?: emptyList()
        selectedSubCategories = intent.getIntegerArrayListExtra("selectedSubCategories") ?: emptyList()

        Log.d("TAGGER", "Received Categories: $selectedCategories")
        Log.d("TAGGER", "Received SubCategories: $selectedSubCategories")

        setUpObserver()
        expertsViewModel.getArticlesForFarmers(
            context = this,
            categoryIds = selectedCategories,
            subCategoryIds = selectedSubCategories
        )
        expertsViewModel.getCategories()
        expertsViewModel.getSubCategories(selectedCategoryId)
        setUpUI()
    }

    private fun setUpUI() {
        binding.sortLinearLayout.setOnClickListener {
            if (apiSortCategories.isNotEmpty()) {
                val items = apiSortCategories.map { it.name }.toTypedArray()
                AlertDialog.Builder(this)
                    .setTitle("Select Sub Category")
                    .setItems(items) { dialog, which ->
                        val selected = apiSortCategories[which]   // The actual Category object
                        var sort = "expert_name"
                        when (selected.id){
                            0 -> {
                                sort = "date_asc"
                            }
                            1 -> {
                                sort = "date_desc"
                            }
                            2 -> {
                                sort = "expert_name"
                            }
                        }
                        binding.sortTextView.text = selected.name  // Show name in TextView
                        expertsViewModel.getArticlesForFarmers(this, categoryIds = selectedCategories, subCategoryIds = selectedSubCategories, sortBy = sort)
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(this, "No categories loaded", Toast.LENGTH_SHORT).show()
            }
        }
        binding.createArticleButton.setOnClickListener {
            startActivity(Intent(this, ExpertsCornerAdminActivity::class.java))
        }
    }

    private fun setUpObserver() {
        expertsViewModel.getAllArticlesResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val jsonArray = jSONObject.optJSONArray("data")
                val adapter = jsonArray?.let { jArray -> ExpertsCornerFarmerAdapter(jArray) }
                binding.expertsCornerFarmerRecycler.layoutManager = LinearLayoutManager(this)
                binding.expertsCornerFarmerRecycler.adapter = adapter
            }
        }
    }
}
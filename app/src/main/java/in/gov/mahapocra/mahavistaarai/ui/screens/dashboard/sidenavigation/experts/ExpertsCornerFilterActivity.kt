package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.Category
import `in`.gov.mahapocra.mahavistaarai.data.model.SubCategory
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityExpertsCornerFilterBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.ExpertsViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONArray
import org.json.JSONObject

class ExpertsCornerFilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpertsCornerFilterBinding
    private val expertsViewModel: ExpertsViewModel by viewModels()
    private var apiCategories: List<Category> = emptyList()
    private var apiSubCategories: List<SubCategory> = emptyList()
    private var selectedCategories: List<Int> = emptyList()
    private var selectedSubCategories: List<Int> = emptyList()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpertsCornerFilterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = "Categories Filter"

        setUpObserver()
        setUpUI()
        expertsViewModel.getCategories(this)
    }

    private fun setUpObserver() {
        expertsViewModel.getCategoriesForExpertCorner.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataArray = jsonObject.optJSONArray("data")
                apiCategories = jsonArrayToNameList(dataArray)
                if (::categoryAdapter.isInitialized) {
                    categoryAdapter.notifyDataSetChanged()
                } else {
                    categoryAdapter = CategoryAdapter(apiCategories) { selected ->
                        selectedCategories = selected
                    }
                    binding.categoryRecyclerView.adapter = categoryAdapter
                }
            }
        }

        expertsViewModel.getSubCategoriesForExpertCorner.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataArray = jsonObject.optJSONArray("data")
                apiSubCategories = jsonArrayToNameListForSubCategory(dataArray)
                if (::subCategoryAdapter.isInitialized) {
                    subCategoryAdapter.notifyDataSetChanged()
                } else {
                    subCategoryAdapter = SubCategoryAdapter(apiSubCategories) { selected ->
                        selectedSubCategories = selected
                    }
                    binding.subCategoryRecyclerView.adapter = subCategoryAdapter
                }
            }
        }
    }

    private fun setUpUI() {
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.subCategoryRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.categoryTextView.setOnClickListener {
            binding.categoryRecyclerView.visibility = View.VISIBLE
            binding.subCategoryRecyclerView.visibility = View.GONE
            binding.subCategoryTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.categoryTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_green))
        }

        binding.subCategoryTextView.setOnClickListener {
            binding.subCategoryRecyclerView.visibility = View.VISIBLE
            binding.categoryRecyclerView.visibility = View.GONE
            if (selectedCategories.isNotEmpty()) {
                binding.subCategoryTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_green))
                binding.categoryTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                expertsViewModel.getSubCategories(selectedCategories)
            }else{
                Toast.makeText(this, "Please select a category first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.applyButton.setOnClickListener {
            val intent = Intent(this, ExpertsCornerFarmerActivity::class.java).apply {
                putIntegerArrayListExtra(
                    "selectedCategories",
                    ArrayList(selectedCategories)
                )
                putIntegerArrayListExtra(
                    "selectedSubCategories",
                    ArrayList(selectedSubCategories)
                )
            }
            startActivity(intent)
        }
    }

    fun jsonArrayToNameList(jsonArray: JSONArray?): List<Category> {
        val list = mutableListOf<Category>()
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i)
                val id: Int = obj?.optInt("id") ?: 0
                val name: String = obj?.optString("name") ?: ""
                list.add(Category(id, name))
            }
        }
        return list
    }

    fun jsonArrayToNameListForSubCategory(jsonArray: JSONArray?): List<SubCategory> {
        val list = mutableListOf<SubCategory>()
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i)
                val id: Int = obj?.optInt("id") ?: 0
                val name: String = obj?.optString("name") ?: ""
                val category: Int = obj?.optInt("category") ?: 0
                list.add(SubCategory(id, name, category))
            }
        }
        return list
    }
}
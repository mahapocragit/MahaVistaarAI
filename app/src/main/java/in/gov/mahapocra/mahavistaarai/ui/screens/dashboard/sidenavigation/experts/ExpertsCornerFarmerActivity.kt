package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts

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
    private var selectedSubCategoryId = 0
    private var apiCategories: List<Category> = emptyList()
    private var apiSubCategories: List<Category> = emptyList()
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
        Log.d("TAGGER", "onCreate: $apiSortCategories")
        setUpObserver()
        expertsViewModel.getArticlesForFarmers(this)
        expertsViewModel.getCategories()
        expertsViewModel.getSubCategories(selectedCategoryId)
        setUpUI()
    }

    private fun setUpUI() {
        binding.categoryLayout.setOnClickListener {
            if (apiCategories.isNotEmpty()) {
                val items = apiCategories.map { it.name }.toTypedArray()
                AlertDialog.Builder(this)
                    .setTitle("Select Category")
                    .setItems(items) { dialog, which ->
                        val selected = apiCategories[which]   // The actual Category object
                        binding.spinnerCategory.text = selected.name  // Show name in TextView
                        selectedCategoryId = selected.id          // You can store ID for API
                        expertsViewModel.getSubCategories(selectedCategoryId)
                        expertsViewModel.getArticlesForFarmers(this, categoryId = selectedCategoryId)
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(this, "No categories loaded", Toast.LENGTH_SHORT).show()
            }
        }
        binding.subCategoryLayout.setOnClickListener {
            if (apiSubCategories.isNotEmpty()) {
                val items = apiSubCategories.map { it.name }.toTypedArray()
                AlertDialog.Builder(this)
                    .setTitle("Select Sub Category")
                    .setItems(items) { dialog, which ->
                        val selected = apiSubCategories[which]   // The actual Category object
                        binding.subCategoryTextView.text = selected.name  // Show name in TextView
                        selectedSubCategoryId = selected.id          // You can store ID for API
                        expertsViewModel.getArticlesForFarmers(this, categoryId = selectedCategoryId, subCategoryId = selectedSubCategoryId)
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(this, "No sub categories loaded", Toast.LENGTH_SHORT).show()
            }
        }
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
                        expertsViewModel.getArticlesForFarmers(this, categoryId = selectedCategoryId, subCategoryId = selectedSubCategoryId, sortBy = sort)
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(this, "No categories loaded", Toast.LENGTH_SHORT).show()
            }
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

        expertsViewModel.getCategoriesForExpertCorner.observe(this) { response ->
            Log.d("TAGGER", "observeResponse cat: $response")
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataArray = jsonObject.optJSONArray("data")
                apiCategories = jsonArrayToNameList(dataArray)
            }
        }
        expertsViewModel.getSubCategoriesForExpertCorner.observe(this) { response ->
            Log.d("TAGGER", "observeResponse sub cat: $response")
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataArray = jsonObject.optJSONArray("data")
                apiSubCategories = jsonArrayToNameList(dataArray)
            }
        }
    }

    fun jsonArrayToNameList(jsonArray: JSONArray?): List<Category> {
        val list = mutableListOf<Category>()
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i)
                val id: Int = (obj?.optInt("id") ?: 0)
                val name = obj?.optString("name") ?: ""
                list.add(Category(id, name))
            }
        }
        return list
    }

    private fun showSubCategoryDialog(subcategories: List<Category>) {
        val items = subcategories.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Subcategory")
            .setItems(items) { dialog, which ->
                val selected = subcategories[which]
                selectedSubCategoryId = selected.id
                Log.d("SelectedID", "SUB_ID = $selectedSubCategoryId")
                dialog.dismiss()
            }
            .show()
    }

}
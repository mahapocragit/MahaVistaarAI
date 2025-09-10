package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.Category
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityExpertsCornerAdminBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.ExpertsViewModel
import `in`.gov.mahapocra.mahavistaarai.util.UriFileHelper
import org.json.JSONArray
import org.json.JSONObject

class ExpertsCornerAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpertsCornerAdminBinding
    private val expertsViewModel: ExpertsViewModel by viewModels()
    private var selectedFileUri: Uri? = null
    private var selectedCategoryId = 0
    private var selectedSubCategoryId = 0
    private var apiCategories: List<Category> = emptyList()
    private var apiSubcategories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExpertsCornerAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = "Experts Corner"
        observeResponse()
        expertsViewModel.getCategories()
        expertsViewModel.getUserArticles(this)
        binding.expertsCornerAdminRecycler.layoutManager = LinearLayoutManager(this)
        binding.spinnerCategory.setOnClickListener {
            if (apiCategories.isNotEmpty()) {
                // Show only the names in the dialog
                val items = apiCategories.map { it.name }.toTypedArray()

                AlertDialog.Builder(this)
                    .setTitle("Select Category")
                    .setItems(items) { dialog, which ->
                        val selected = apiCategories[which]   // The actual Category object
                        binding.spinnerCategory.text = selected.name  // Show name in TextView
                        selectedCategoryId = selected.id          // You can store ID for API
                        Log.d("SelectedID", "ID = $selectedCategoryId")
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(this, "No categories loaded", Toast.LENGTH_SHORT).show()
            }
        }
        binding.spinnerSubcategories.setOnClickListener {
            if (selectedCategoryId == 0) {
                Toast.makeText(this, "Please select a category first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            expertsViewModel.getSubCategories(selectedCategoryId)
        }

        binding.addPostToggleButton.apply {
            background =
                ContextCompat.getDrawable(
                    this@ExpertsCornerAdminActivity,
                    R.drawable.shape_right_green
                )
            setTextColor(Color.WHITE)
        }
        binding.myPostToggleButton.apply {
            background =
                ContextCompat.getDrawable(
                    this@ExpertsCornerAdminActivity,
                    R.drawable.shape_left_white
                )
            setTextColor(Color.BLACK)
        }
        binding.addPostToggleButton.setOnClickListener { toggleView(true) }
        binding.myPostToggleButton.setOnClickListener { toggleView(false) }
        binding.submitButton.setOnClickListener {
            val titleText = binding.titleEditText.text.toString()
            val descriptionText = binding.descriptionTextView.text.toString()
            if (titleText.isNotEmpty()) {
                if (selectedCategoryId != 0) {
                    if (selectedSubCategoryId != 0) {
                        if (descriptionText.isNotEmpty()) {
                            if (selectedFileUri != null) {
                                val file = selectedFileUri?.let {
                                    UriFileHelper.uriToFilePart(
                                        it,
                                        "file",
                                        contentResolver
                                    )
                                }
                                selectedFileUri?.let {
                                    expertsViewModel.uploadArticle(
                                        this,
                                        file!!,
                                        titleText,
                                        descriptionText,
                                        selectedCategoryId.toString(),
                                        selectedSubCategoryId.toString()
                                    )
                                }
                            } else {
                                Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "Please select Sub Category", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Please select Category", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
            }
        }
        binding.uploadFileButton.setOnClickListener {
            pickFileLauncher.launch(
                arrayOf(
                    "application/pdf",
                    "application/msword", // .doc
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
                )
            )
        }
        binding.deleteImageView.setOnClickListener {
            selectedFileUri = null
            Toast.makeText(this, "File Deleted", Toast.LENGTH_SHORT).show()
            binding.selectedFileNameTextView.text = "No File Selected"
            binding.deleteImageView.visibility = View.GONE
        }
    }

    private fun observeResponse() {
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
                apiSubcategories = jsonArrayToNameList(dataArray)
                if (apiSubcategories.isNotEmpty()) {
                    showSubCategoryDialog(apiSubcategories)
                }
            }

        }
        expertsViewModel.getUserArticlesResponse.observe(this) { response ->
            Log.d("TAGGER", "observeResponse getUserArticles: $response")
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val jsonArray = jSONObject.optJSONArray("data") ?: JSONArray()
                val adapter = ExpertsCornerAdminAdapter(jsonArray)
                binding.expertsCornerAdminRecycler.adapter = adapter
            }
        }
        expertsViewModel.uploadArticleResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                val responseMessage = jSONObject.optString("response")
                if (status == 200) {
                    Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        expertsViewModel.error.observe(this) {
            Log.d("TAGGER", "observeResponse fail: $it")
            Toast.makeText(this, "$it Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleView(showAddView: Boolean) {
        if (showAddView) {
            binding.addPostLayout.visibility = View.VISIBLE
            binding.myPostLayout.visibility = View.GONE
            binding.addPostToggleButton.apply {
                background =
                    ContextCompat.getDrawable(
                        this@ExpertsCornerAdminActivity,
                        R.drawable.shape_right_green
                    )
                setTextColor(Color.WHITE)
            }
            binding.myPostToggleButton.apply {
                background =
                    ContextCompat.getDrawable(
                        this@ExpertsCornerAdminActivity,
                        R.drawable.shape_left_white
                    )
                setTextColor(Color.BLACK)
            }
        } else {
            expertsViewModel.getUserArticles(this)
            binding.addPostLayout.visibility = View.GONE
            binding.myPostLayout.visibility = View.VISIBLE
            binding.addPostToggleButton.apply {
                background =
                    ContextCompat.getDrawable(
                        this@ExpertsCornerAdminActivity,
                        R.drawable.shape_right
                    )
                setTextColor(Color.BLACK)
            }
            binding.myPostToggleButton.apply {
                background = ContextCompat.getDrawable(
                    this@ExpertsCornerAdminActivity,
                    R.drawable.shape_left
                )
                setTextColor(Color.WHITE)
            }
        }
    }

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                binding.deleteImageView.visibility = View.VISIBLE
                selectedFileUri = uri
                val fileName = getFileName(uri)
                binding.selectedFileNameTextView.text = fileName
                Toast.makeText(this, "Selected: $fileName", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getFileName(uri: Uri): String {
        var name = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                cursor.moveToFirst()
                name = cursor.getString(nameIndex)
            }
        }
        return name
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
                binding.spinnerSubcategories.text = selected.name
                selectedSubCategoryId = selected.id
                Log.d("SelectedID", "SUB_ID = $selectedSubCategoryId")
                dialog.dismiss()
            }
            .show()
    }

}
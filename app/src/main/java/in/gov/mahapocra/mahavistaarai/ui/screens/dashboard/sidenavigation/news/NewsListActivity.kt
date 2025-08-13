package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNotificationListBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.NewsWadhwaniViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationListBinding
    private val newsWadhwaniViewModel: NewsWadhwaniViewModel by viewModels()
    private lateinit var languageToLoad: String
    private var apiCategories: List<String> = emptyList()
    private var apiSubcategories: List<String> = emptyList()
    private var bearerToken = ""
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@NewsListActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityNotificationListBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        uiResponsive(binding.root)
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.news)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        ProgressHelper.showProgressDialog(this)
        observeResponseArrivingFromAPI()
        newsWadhwaniViewModel.getAuthenticationForNews(this)
        binding.filterImageView.setOnClickListener {
            showFilterDialog(apiCategories)
        }
    }

    private fun observeResponseArrivingFromAPI() {
        newsWadhwaniViewModel.responseAuthToken.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataJsonObject = jsonObject.optJSONObject("data")
                bearerToken = dataJsonObject.optString("token")
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -7) // Go back 7 days
                val dateSevenDaysAgo =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(calendar.time)
                val currentDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(
                    Date()
                )
                newsWadhwaniViewModel.getNewsWadhwani(
                    bearerToken,
                    offset,
                    dateSevenDaysAgo,
                    currentDateTime
                )
                newsWadhwaniViewModel.getNewsCategories(bearerToken)
                binding.nextButton.setOnClickListener {
                    offset += 10
                    binding.countText.text = offset.toString()
                    newsWadhwaniViewModel.getNewsWadhwani(
                        bearerToken,
                        offset,
                        dateSevenDaysAgo,
                        currentDateTime
                    )
                }
                binding.prevButton.setOnClickListener {
                    offset -= 10
                    newsWadhwaniViewModel.getNewsWadhwani(
                        bearerToken,
                        offset,
                        dateSevenDaysAgo,
                        currentDateTime
                    )
                }
            }
        }

        newsWadhwaniViewModel.responseNewsWadhwani.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataJsonObject = jsonObject.optJSONObject("data")
                val eventJsonArray = dataJsonObject?.optJSONArray("events")
                if (eventJsonArray?.length() != 0) {
                    binding.newsRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.newsRecyclerView.adapter =
                        eventJsonArray?.let { it1 -> NewsAdapter(it1, languageToLoad) }
                } else {
                    offset -= 10
                    UIToastMessage.show(this, "This is the last page")
                }
            }
        }

        newsWadhwaniViewModel.getNewsCategoriesResponse.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataObject = jsonObject.optJSONObject("data")
                val categoriesArray = dataObject?.optJSONArray("category")
                apiCategories = jsonArrayToList(categoriesArray)
            }
        }

        newsWadhwaniViewModel.getNewsSubCategoriesResponse.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataObject = jsonObject.optJSONObject("data")
                val subCategoriesArray = dataObject?.optJSONArray("sub_category")
                apiSubcategories = jsonArrayToList(subCategoriesArray)
            }
        }
    }

    fun jsonArrayToList(jsonArray: JSONArray?): List<String> {
        val list = mutableListOf<String>()
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.optString(i))
            }
        }
        return list
    }

    fun showFilterDialog(categories: List<String>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerSubcategories = dialogView.findViewById<MultiSelectSpinner>(R.id.spinnerSubcategories)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        val prefs = getSharedPreferences("PREFS_NAME", MODE_PRIVATE)
        var selectedCategory = prefs.getString("KEY_CATEGORY", categories.firstOrNull() ?: "") ?: ""
        var selectedSubcategories = prefs.getStringSet("KEY_SUBCATEGORIES", emptySet())?.toMutableSet() ?: mutableSetOf()

        // Category Spinner setup
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = spinnerAdapter
        spinnerCategory.setSelection(categories.indexOf(selectedCategory))

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val cat = categories[position]
                Log.d("TAGGER", "Category selected: $cat")
                newsWadhwaniViewModel.getNewsSubCategories(bearerToken, cat)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Observe API result and update subcategories dynamically
        newsWadhwaniViewModel.getNewsSubCategoriesResponse.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataObject = jsonObject.optJSONObject("data")
                val subCategoriesArray = dataObject?.optJSONArray("sub_category")
                val apiSubcategories = jsonArrayToList(subCategoriesArray)

                spinnerSubcategories.setItems(apiSubcategories, selectedSubcategories)
                spinnerSubcategories.setOnSelectionListener { list ->
                    selectedSubcategories.clear()
                    selectedSubcategories.addAll(list)
                }
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSubmit.setOnClickListener {
            selectedCategory = spinnerCategory.selectedItem.toString()
            prefs.edit()
                .putString("KEY_CATEGORY", selectedCategory)
                .putStringSet("KEY_SUBCATEGORIES", selectedSubcategories)
                .apply()
            dialog.dismiss()
        }

        dialog.show()
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


package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news

import android.content.Context
import android.os.Bundle
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
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.NewsAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.NewsWadhwaniViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
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

    // Pagination
    private var offset = 0
    private val pageSize = 10
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad =
            if (AppSettings.getLanguage(this@NewsListActivity).equals("1", ignoreCase = true)) {
                "en"
            } else {
                "mr"
            }
        switchLanguage(this, languageToLoad)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
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
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val dateSevenDaysAgo =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(calendar.time)
                val currentDateTime =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(Date())

                // First load
                getNews(dateSevenDaysAgo, currentDateTime)

                // Pagination buttons
                binding.nextButton.setOnClickListener {
                    if (!isLastPage) {
                        offset += pageSize
                        getNews(dateSevenDaysAgo, currentDateTime)
                    } else {
                        UIToastMessage.show(this, "This is the last page")
                    }
                }
                binding.prevButton.setOnClickListener {
                    if (offset > 0) {
                        offset -= pageSize
                        getNews(dateSevenDaysAgo, currentDateTime)
                    } else {
                        UIToastMessage.show(this, "This is the first page")
                    }
                }

                // Fetch categories
                newsWadhwaniViewModel.getNewsCategories(bearerToken)
            }
        }

        newsWadhwaniViewModel.responseNewsWadhwani.observe(this) { itData ->
            ProgressHelper.disableProgressDialog()
            if (itData != null) {
                val jsonObject = JSONObject(itData.toString())
                val dataJsonObject = jsonObject.optJSONObject("data")
                val eventJsonArray = dataJsonObject?.optJSONArray("events")

                if (eventJsonArray == null || eventJsonArray.length() == 0) {
                    // Clear old data from RecyclerView
                    binding.newsRecyclerView.adapter = NewsAdapter(JSONArray(), languageToLoad)

                    if (offset >= pageSize) {
                        // If we were paging forward, go back to last valid page
                        offset -= pageSize
                        isLastPage = true
                        UIToastMessage.show(this, "This is the last page")
                    } else {
                        // If on first page, just show "No data"
                        UIToastMessage.show(this, "No news available")
                        isLastPage = true
                    }
                } else {
                    // Check if this is the last page
                    isLastPage = eventJsonArray.length() < pageSize

                    // Update page number
                    binding.countText.text = ((offset / pageSize) + 1).toString()

                    // Update RecyclerView
                    binding.newsRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.newsRecyclerView.adapter = NewsAdapter(eventJsonArray, languageToLoad)
                }
            } else {
                // If response itself is null, clear list
                binding.newsRecyclerView.adapter = NewsAdapter(JSONArray(), languageToLoad)
                UIToastMessage.show(this, "No data available")
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

    private fun getNews(dateSevenDaysAgo: String, currentDateTime: String) {
        // Read from SharedPreferences
        val prefs = getSharedPreferences("PREFS_NAME", MODE_PRIVATE)
        val category = prefs.getString("KEY_CATEGORY", "") ?: ""

        // Convert Set<String> of subcategories to comma-separated String
        val subcategoriesSet = prefs.getStringSet("KEY_SUBCATEGORIES", emptySet()) ?: emptySet()
        val subcategoriesString = subcategoriesSet.joinToString(",")

        ProgressHelper.showProgressDialog(this)
        newsWadhwaniViewModel.getNewsWadhwani(
            bearerToken,
            offset,
            dateSevenDaysAgo,
            currentDateTime,
            category,
            subcategoriesString
        )
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
        val spinnerSubcategories =
            dialogView.findViewById<MultiSelectSpinner>(R.id.spinnerSubcategories)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        val prefs = getSharedPreferences("PREFS_NAME", MODE_PRIVATE)
        var selectedCategory = prefs.getString("KEY_CATEGORY", categories.firstOrNull() ?: "") ?: ""
        var selectedSubcategories =
            prefs.getStringSet("KEY_SUBCATEGORIES", emptySet())?.toMutableSet() ?: mutableSetOf()

        // Category Spinner
        val spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = spinnerAdapter
        spinnerCategory.setSelection(categories.indexOf(selectedCategory))

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val cat = categories[position]
                newsWadhwaniViewModel.getNewsSubCategories(bearerToken, cat)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Observe and update subcategories
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

            // Reset pagination
            offset = 0
            binding.countText.text = "1"  // ✅ Reset page number in UI

            // Prepare dates for API call
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val dateSevenDaysAgo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(calendar.time)
            val currentDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(Date())

            // Call API with filters applied
            getNews(dateSevenDaysAgo, currentDateTime)

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
        val updatedContext = configureLocale(newBase, languageToLoad)
        super.attachBaseContext(updatedContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        val prefs = getSharedPreferences("PREFS_NAME", MODE_PRIVATE)
        prefs.edit()
            .remove("KEY_CATEGORY")
            .remove("KEY_SUBCATEGORIES")
            .apply()
    }
}



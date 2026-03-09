package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAddCropBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CropCategoriesAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FirebaseTopicHelper.unSubscribeToTopic
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar


class AddCropActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener,
    DatePickerRequestListener {

    private lateinit var binding: ActivityAddCropBinding
    private lateinit var textViewHeaderTitle: TextView
    private lateinit var imageMenuShow: ImageView
    private lateinit var imgBackArrow: ImageView
    private lateinit var receivedJson: JSONObject
    private var activityStarted = false
    private var farmerId = 0
    private val viewModel: FarmerViewModel by viewModels()
    private var languageToLoad: String = "mr"
    private var cropId: Int = 0
    private var cropToken = ""
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Language setup ---
        languageToLoad =
            if (AppSettings.getLanguage(this).equals("1", ignoreCase = true)) "en" else "mr"
        switchLanguage(this, languageToLoad)

        binding = ActivityAddCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        setupToolbar()
        setupRecyclerView()

        // --- Load data in background ---
        uiScope.launch {
            ProgressHelper.showProgressDialog(this@AddCropActivity)
            withContext(Dispatchers.IO) {
                viewModel.getCropCategoriesAndCropDetails(languageToLoad)
            }
        }

        observeResponse()
    }

    // -----------------------------
    // 🧭 Toolbar Setup
    // -----------------------------
    private fun setupToolbar() {
        imgBackArrow = findViewById(R.id.imgBackArrow)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenuShow = findViewById(R.id.imageMenushow)

        textViewHeaderTitle.setText(R.string.select_crop)
        imgBackArrow.visibility = View.VISIBLE

        imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        imageMenuShow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
    }

    // -----------------------------
    // ♻️ RecyclerView Setup
    // -----------------------------
    private fun setupRecyclerView() {
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.setHasFixedSize(true)
    }

    // -----------------------------
    // 🌾 Observe Crop Data
    // -----------------------------
    private fun observeResponse() {
        viewModel.cropCategoryResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val jsonDataArray = jsonObject.getJSONArray("data")
                    val callerActivityString =
                        if (intent.getStringExtra("callerActivity") != null) {
                            "costCalculator"
                        } else {
                            "TitleVideosDetailsAdpter"
                        }

                    if (callerActivityString != null) {
                        uiScope.launch(Dispatchers.Default) {
                            val adapter = CropCategoriesAdapter(
                                this@AddCropActivity,
                                jsonDataArray,
                                callerActivityString,
                                this@AddCropActivity
                            )
                            withContext(Dispatchers.Main) {
                                binding.mainRecyclerView.adapter = adapter
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deleteSubscribedTopicResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    safeStartActivity()
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }

        }

        viewModel.saveFarmerSelectedCrop.observe(this) { response ->

            ProgressHelper.disableProgressDialog()

            val jsonObject = JSONObject(response.toString())

            if (jsonObject.optString("status") == "200") {

                val prefManager = AppPreferenceManager(this)
                val jsonStr = prefManager.getString("topic_saved_fcm")
                val jsonArray = JSONArray(jsonStr ?: "[]")

                var cropTopicFound = false

                for (i in 0 until jsonArray.length()) {
                    val topic = jsonArray.optString(i)
                    try {
                        val topicHead = topic.substringBefore("_")
                        if (topicHead == "crop") {
                            cropTopicFound = true
                            if (topic == cropToken) {
                                safeStartActivity()
                                return@observe
                            }
                            unSubscribeToTopic(topic) { unsubscribed ->
                                if (unsubscribed) {
                                    viewModel.deleteSubscribedTopics(
                                        farmerId = farmerId,
                                        topics = listOf(topic)
                                    )
                                    val updatedArray = JSONArray()
                                    for (j in 0 until jsonArray.length()) {
                                        val savedTopic = jsonArray.optString(j)
                                        if (savedTopic != topic) {
                                            updatedArray.put(savedTopic)
                                        }
                                    }

                                    prefManager.saveString(
                                        "topic_saved_fcm",
                                        updatedArray.toString()
                                    )
                                }
                                safeStartActivity()
                            }
                            return@observe
                        }
                    } catch (e: Exception) {
                        safeStartActivity()
                        return@observe
                    }
                }

                // If no crop topic found
                if (!cropTopicFound) {
                    safeStartActivity()
                }
            } else {
                Toast.makeText(this, R.string.error_saving_crop, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
            Toast.makeText(this, R.string.failed_to_load, Toast.LENGTH_SHORT).show()
        }
    }

    // -----------------------------
    // 🧩 Item Clicks
    // -----------------------------
    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1 && obj is JSONObject) {
            receivedJson = obj

            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_sowing_date))
                .setTheme(R.style.CustomMaterialDatePickerTheme) // 🎨 Apply your theme
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)

            val datePicker = builder.build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val calendar = Calendar.getInstance().apply { timeInMillis = selection }
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                onDateSelected(1, day, month, year)
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }

    // -----------------------------
    // 📅 Date Picker Callback
    // -----------------------------
    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            val sowingDate = "$day-${month + 1}-$year"
            cropId = receivedJson.optInt("id")
            cropToken = "crop_$cropId"
            ProgressHelper.showProgressDialog(this)
            viewModel.saveFarmerSelectedCrop(this, sowingDate, cropId)
        }
    }

    fun safeStartActivity() {
        if (!activityStarted) {
            activityStarted = true
            Toast.makeText(this, R.string.selected_crop_saved, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DashboardScreen::class.java).apply {
                putExtra("savedCropResponse", "200")
            })
        }
    }

    // -----------------------------
    // 🌍 Locale
    // -----------------------------
    override fun attachBaseContext(newBase: Context) {
        languageToLoad =
            if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) "en" else "mr"
        val updatedContext = configureLocale(newBase, languageToLoad)
        super.attachBaseContext(updatedContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel() // Cancel background coroutines to prevent leaks
    }
}
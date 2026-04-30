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
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.NewDashboardMainActivity
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
                    val callerActivityString = intent.getStringExtra("callerActivity")
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

        viewModel.saveFarmerSelectedCrop.observe(this) { state ->

            when(state){
                is UiState.Loading->{
                    ProgressHelper.showProgressDialog(this)
                }
                is UiState.Success->{
                    ProgressHelper.disableProgressDialog()
                    val dataObject = JSONObject(state.data.toString())
                    Log.d(TAG, "observeResponse: $dataObject")
                    val status = dataObject.optInt("status")
                    if (status == 200) {
                        safeStartActivity()
                    }
                }
                is UiState.Error->{
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
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
            viewModel.saveFarmerSelectedCrop(farmerId, sowingDate, cropId)
        }
    }

    fun safeStartActivity() {
        if (!activityStarted) {
            activityStarted = true
            Toast.makeText(this, R.string.selected_crop_saved, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, NewDashboardMainActivity::class.java).apply {
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
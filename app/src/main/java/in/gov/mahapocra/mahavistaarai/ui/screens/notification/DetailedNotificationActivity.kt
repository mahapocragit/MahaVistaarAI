package `in`.gov.mahapocra.mahavistaarai.ui.screens.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDetailedNotificationBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.DialogFeedbackNotificationsBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.QuestionsAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.etl.AgriStackAdvisoryActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.Warehouse
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate.ClimateResilientTechnology
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.DBTActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice.MarketPrice
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.SoilHealthCardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.sop.SOPActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala.ShetishalaActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.WeatherActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import org.json.JSONArray
import org.json.JSONObject


class DetailedNotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedNotificationBinding
    private lateinit var languageToLoad: String
    private val farmerViewModel: FarmerViewModel by viewModels()
    val flatCropsJsonArray = JSONArray()
    var cropId: Int? = 0
    private var cropName: String? = null
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DetailedNotificationActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityDetailedNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        
        val notificationObject = intent.getStringExtra("notificationObject")
        Log.d(TAG, "onCreate: $notificationObject")
        if (notificationObject != null) {
            val jsonObject = JSONObject(notificationObject)
            val id = jsonObject.optLong("id")
            val flatCropId = jsonObject.optInt("crop")
            val type = jsonObject.optString("type")
            farmerViewModel.getNotificationDetails(this, id, type)
            farmerViewModel.getNotificationDetailedResponse.observe(this) {
                if (it != null) {
                    Log.d(TAG, "onCreate............: $it")
                    val jsonObject = JSONObject(it.toString())
                    val notificationObject = jsonObject.optJSONObject("notifications")
                    setUpPageContent(notificationObject, id)
                    val questionsJson = notificationObject?.optJSONArray("questions")
                    if (questionsJson?.length() == 0) {
                        binding.feedbackFAB.visibility = View.GONE
                    } else {
                        binding.feedbackFAB.visibility = View.VISIBLE
                        binding.feedbackFAB.setOnClickListener {
                            val dialogBinding =
                                DialogFeedbackNotificationsBinding.inflate(layoutInflater)
                            // 🔥 Create JSON once and share with adapter
                            dialogBinding.questionRecyclerView.apply {
                                layoutManager =
                                    LinearLayoutManager(this@DetailedNotificationActivity)
                                setHasFixedSize(false)
                                adapter = QuestionsAdapter(questionsJson)
                            }

                            val dialog = AlertDialog.Builder(this@DetailedNotificationActivity)
                                .setView(dialogBinding.root)
                                .setCancelable(true)
                                .create()

                            dialogBinding.button.setOnClickListener {
                                questionsJson?.let { jsonArray ->
                                    if (!isAllAnswered(jsonArray)) {
                                        Toast.makeText(
                                            this@DetailedNotificationActivity,
                                            "Please answer all questions",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@setOnClickListener
                                    }
                                }

                                // ✅ FINAL OUTPUT JSON
                                questionsJson?.let {
                                    farmerViewModel.addNotificationFeedback(
                                        this,
                                        id.toString(),
                                        type,
                                        questionsJson
                                    )
                                }

                                farmerViewModel.addNotificationFeedbackResponse.observe(this){
                                    Log.d(TAG, "onCreate: $it")
                                    farmerViewModel.getNotificationDetails(this, id, type)
                                }
                                Log.d("FEEDBACK_JSON", questionsJson.toString())
                                dialog.dismiss()
                            }
                            dialog.show()
                        }
                    }
                }
            }
            fetchCropList(flatCropId)
        } else {
            val id = intent.getLongExtra("id", 0L)
            val type = intent.getStringExtra("type")
            farmerViewModel.getNotificationDetails(this, id, type)
            farmerViewModel.getNotificationDetailedResponse.observe(this) {
                if (it != null) {
                    val jsonObject = JSONObject(it.toString())
                    Log.d(TAG, "getNotificationDetails: $jsonObject")
                    val notificationObject = jsonObject.optJSONObject("notifications")
                    val flatCropId = notificationObject?.optInt("crop")
                    Log.d(TAG, "onCreate: $flatCropId")
                    setUpPageContent(notificationObject, id)
                    val questionsJson = notificationObject?.optJSONArray("questions")
                    if (questionsJson?.length() == 0) {
                        binding.feedbackFAB.visibility = View.GONE
                    } else {
                        binding.feedbackFAB.visibility = View.VISIBLE
                        binding.feedbackFAB.setOnClickListener {
                            val dialogBinding =
                                DialogFeedbackNotificationsBinding.inflate(layoutInflater)
                            // 🔥 Create JSON once and share with adapter
                            dialogBinding.questionRecyclerView.apply {
                                layoutManager =
                                    LinearLayoutManager(this@DetailedNotificationActivity)
                                setHasFixedSize(false)
                                adapter = QuestionsAdapter(questionsJson)
                            }

                            val dialog = AlertDialog.Builder(this@DetailedNotificationActivity)
                                .setView(dialogBinding.root)
                                .setCancelable(true)
                                .create()

                            dialogBinding.button.setOnClickListener {
                                questionsJson?.let { jsonArray ->
                                    if (!isAllAnswered(jsonArray)) {
                                        Toast.makeText(
                                            this@DetailedNotificationActivity,
                                            "Please answer all questions",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@setOnClickListener
                                    }
                                }

                                // ✅ FINAL OUTPUT JSON
                                questionsJson?.let {
                                    farmerViewModel.addNotificationFeedback(
                                        this,
                                        id.toString(),
                                        type.toString(),
                                        questionsJson
                                    )
                                }

                                farmerViewModel.addNotificationFeedbackResponse.observe(this){
                                    Log.d(TAG, "onCreate: $it")
                                    farmerViewModel.getNotificationDetails(this, id, type)
                                }
                                Log.d("FEEDBACK_JSON", questionsJson.toString())
                                dialog.dismiss()
                            }
                            dialog.show()
                        }
                    }
                    fetchCropList(flatCropId)
                }
            }
        }

        binding.relativeLayoutTopBar.textViewHeaderTitle.text =
            getString(R.string.detailed_notifications)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this@DetailedNotificationActivity, DashboardScreen::class.java))
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(
                    Intent(
                        this@DetailedNotificationActivity,
                        DashboardScreen::class.java
                    )
                )
            }
        })
    }

    private fun isAllAnswered(jsonArray: JSONArray): Boolean {
        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).isNull("answer")) {
                return false
            }
        }
        return true
    }

    private fun fetchCropList(flatCropId: Int?) {
        farmerViewModel.getCropCategoriesAndCropDetails(this, languageToLoad)
        farmerViewModel.cropCategoryResponse.observe(this) { response ->
            if (response != null) {
                try {
                    val jsonObject = JSONObject(response.toString())
                    val dataArray = jsonObject.optJSONArray("data")

                    if (dataArray != null) {
                        for (i in 0 until dataArray.length()) {
                            val categoryObject = dataArray.optJSONObject(i)
                            val crops = categoryObject.optJSONArray("crops")

                            if (crops != null) {
                                for (j in 0 until crops.length()) {
                                    val crop = crops.optJSONObject(j)
                                    flatCropsJsonArray.put(crop)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            var foundMatch = false
            if (flatCropsJsonArray.length() > 0 && flatCropId != null) {
                for (i in 0 until flatCropsJsonArray.length()) {
                    val jsonObject = flatCropsJsonArray.getJSONObject(i)
                    val id = jsonObject.optInt("id")
                    if (id == flatCropId) {
                        Log.d(TAG, "checkCropIdAndFetchJson: $jsonObject")
                        cropId = jsonObject.optInt("id")
                        cropName = jsonObject.optString("name")
                        sowingDate = jsonObject.optString("sowing_date")
                        wotrCropId = jsonObject.optString("wotr_crop_id")
                        mUrl = jsonObject.optString("mUrl")
                        foundMatch = true
                        break
                    }
                }
            }

            // 🔁 Fallback to saved preferences if no matching crop found
            if (!foundMatch) {
                val prefs = AppPreferenceManager(this)
                cropId = prefs.getInt("CROP_ID_SAVED")
                if (cropId != 0) {
                    cropName = prefs.getString("CROP_NAME_SAVED")
                    mUrl = prefs.getString("CROP_IMAGE_SAVED")
                    sowingDate = prefs.getString("CROP_SOWING_DATE_SAVED") ?: ""
                    wotrCropId = prefs.getString("CROP_WOTR_ID_SAVED")
                } else {
                    cropId = 25
                    cropName = if (languageToLoad == "en") "Cotton" else "कापूस"
                    mUrl =
                        "https://s3.object.webwerksvmx.com/ffsauditlogs/ffs-api/ffs-api/uploads/crop_image/25_Cotton_1697091770.png"
                    sowingDate = "22/06"
                    wotrCropId = "1"
                }
            }
        }
    }

    private fun setUpPageContent(jsonObject: JSONObject, notificationId: Long) {
        binding.notificationInfoLayout.visibility = View.VISIBLE
        val page = jsonObject.optString("page")
        val type = jsonObject.optString("type")
        var title = jsonObject.optString("title")
        val description = jsonObject.optString("description")
        if (type == "etl") {
            title = jsonObject.optString("crop")
        }
        val shortDescription = jsonObject.optString("body")
        val url = jsonObject.optString("url") ?: ""
        Log.d(TAG, "setUpPageContent: $url")
        val notificationDate =
            LocalCustom.convertDateFormat(jsonObject.optString("notification_date"))
        val redirectionText = jsonObject.optString("redirection_text")
        if (redirectionText.isEmpty()) {
            binding.redirectTextView.visibility = View.GONE
        }
        binding.titleTextView.text = title
        binding.shortDescriptionTextView.text = shortDescription
        if (url.isEmpty()) {
            binding.descriptionTextView.text = description
            binding.webView.visibility = View.GONE
            binding.descriptionTextView.visibility = View.VISIBLE
        } else {
            binding.webView.visibility = View.VISIBLE
            binding.descriptionTextView.visibility = View.GONE
            binding.webView.loadUrl(url)
        }
        binding.dateTextView.text = notificationDate
        val content = SpannableString(redirectionText ?: "अधिक माहितीसाठी येथे क्लिक करा.")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.redirectTextView.text = content
        binding.redirectTextView.setOnClickListener { redirectToScreen(page) }
        farmerViewModel.updateNotificationStatus(this, notificationId, type)
        farmerViewModel.updateNotificationStatusResponse.observe(this) {
        }
    }

    private fun redirectToScreen(testValue: String) {
        val targetIntent = when (testValue.lowercase()) {
            "advisory" -> checkAndRedirect(AdvisoryCropActivity::class.java)
            "sop" -> checkAndRedirect(SOPActivity::class.java)
            "fertilizer" -> checkAndRedirect(FertilizerCalculatorActivity::class.java)
            "pestdisease" -> checkAndRedirect(PestsAndDiseasesStages::class.java)
            "weather" -> Intent(this, WeatherActivity::class.java)
            "soilcard" -> Intent(this, SoilHealthCardActivity::class.java)
            "climatetech" -> Intent(this, ClimateResilientTechnology::class.java)
            "marketprice" -> Intent(this, MarketPrice::class.java)
            "shetishala" -> Intent(this, ShetishalaActivity::class.java)
            "warehouse" -> Intent(this, Warehouse::class.java)
            "customhire" -> Intent(this, CHCenterActivity::class.java)
            "videos" -> Intent(this, VideosActivity::class.java)
            "dbtschemes" -> Intent(this, DBTActivity::class.java)
            "dashboard" -> Intent(this, DashboardScreen::class.java)
            "etl_page" -> Intent(this, AgriStackAdvisoryActivity::class.java)
            else -> Intent(this, DashboardScreen::class.java)
        }
        startActivity(targetIntent)
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

    private fun checkAndRedirect(targetClass: Class<*>): Intent {
        Log.d(TAG, "checkAndRedirect: $cropId")
        val sowingDateFormat = if (
            (targetClass == FertilizerCalculatorActivity::class.java || targetClass == AdvisoryCropActivity::class.java)
            && isShortDateFormat(sowingDate)
        ) {
            LocalCustom.getSowingDateInDayMonthYearFormat(sowingDate)
        } else {
            sowingDate
        }
        return Intent(this, targetClass).apply {
            putExtra("id", cropId)
            putExtra("wotr_crop_id", wotrCropId)
            putExtra("mUrl", mUrl)
            putExtra("sowingDate", sowingDateFormat)
            putExtra("mName", cropName)
        }
    }

    private fun isShortDateFormat(date: String?): Boolean {
        if (date.isNullOrEmpty()) return false
        // Simple check: pattern matches "dd/MM" only
        val regex = Regex("""\d{2}/\d{2}""")
        return regex.matches(date.trim())
    }
}
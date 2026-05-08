package `in`.gov.mahapocra.mahavistaarai.ui.screens.notification

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDetailedNotificationBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.NotificationFeedbackDialogBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.etl.AgriStackAdvisoryActivity
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
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.NewDashboardMainActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject


class DetailedNotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedNotificationBinding
    private lateinit var languageToLoad: String
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val flatCropsJsonArray = JSONArray()
    private var cropId: Int? = 0
    private var farmerId: Int = 0
    private var cropName: String? = null
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String = ""
    private var notificationId = ""
    private var notificationType = ""
    private var questionJsonObject: JSONObject? = null

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
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        val notificationObject = intent.getStringExtra("notificationObject")
        observeResponse()
        if (notificationObject != null) {
            val jsonObject = JSONObject(notificationObject)
            notificationId = jsonObject.optLong("id").toString()
            val flatCropId = jsonObject.optInt("crop")
            notificationType = jsonObject.optString("type")
            farmerViewModel.getNotificationDetails(notificationId, notificationType)
            farmerViewModel.getNotificationDetailedResponse.observe(this) { state ->
                when (state) {
                    is UiState.Loading -> {
                        ProgressHelper.showProgressDialog(this)
                    }

                    is UiState.Success -> {
                        ProgressHelper.disableProgressDialog()
                        val jsonObject = JSONObject(state.data.toString())
                        val notificationObject = jsonObject.optJSONObject("notifications")
                        setUpPageContent(notificationObject, notificationId)
                        val questionsJsonArray = notificationObject?.optJSONArray("questions")
                        if (questionsJsonArray?.length() == 0) {
                            binding.feedbackFAB.visibility = View.GONE
                        } else {
                            questionJsonObject = questionsJsonArray?.get(0) as JSONObject
                        }
                    }

                    is UiState.Error -> {
                        ProgressHelper.disableProgressDialog()
                        Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            fetchCropList(flatCropId)
        } else {
            notificationId = intent.getLongExtra("id", 0L).toString()
            notificationType = intent.getStringExtra("type").toString()
            farmerViewModel.getNotificationDetails(notificationId, notificationType)
            farmerViewModel.getNotificationDetailedResponse.observe(this) { state ->
                when (state) {
                    is UiState.Loading -> {
                        ProgressHelper.showProgressDialog(this)
                    }

                    is UiState.Success -> {
                        ProgressHelper.disableProgressDialog()
                        val jsonObject = JSONObject(state.data.toString())
                        val notificationObject = jsonObject.optJSONObject("notifications")
                        val flatCropId = notificationObject?.optInt("crop")
                        Log.d(TAG, "onCreate: $flatCropId")
                        setUpPageContent(notificationObject, notificationId)
                        val questionsJsonArray = notificationObject?.optJSONArray("questions")
                        if (questionsJsonArray?.length() == 0) {
                            binding.feedbackFAB.visibility = View.GONE
                        } else {
                            questionJsonObject = questionsJsonArray?.get(0) as JSONObject
                        }
                        fetchCropList(flatCropId)
                    }

                    is UiState.Error -> {
                        ProgressHelper.disableProgressDialog()
                        Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.relativeLayoutTopBar.textViewHeaderTitle.text =
            getString(R.string.detailed_notifications)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            Log.d(TAG, "onCreate: hello")
            safelyNavigateToPreviousScreen()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "onCreate: hello1")
                safelyNavigateToPreviousScreen()
            }
        })
    }

    private fun observeResponse() {
        farmerViewModel.addNotificationFeedbackResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val responseObject = JSONObject(state.data.toString())
                    Log.d(TAG, "observeResponse: $responseObject")
                    startActivity(
                        Intent(
                            this@DetailedNotificationActivity,
                            NotificationActivity::class.java
                        )
                    )
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    startActivity(
                        Intent(
                            this@DetailedNotificationActivity,
                            NotificationActivity::class.java
                        )
                    )
                }
            }
        }
    }

    fun setUpFeedbackDialog(
        notificationType: String,
        notificationId: String,
        questionObject: JSONObject
    ) {

        Log.d(TAG, "setUpFeedbackDialog: $questionObject")
        val feedbackId = questionObject.optInt("id")
        val question = questionObject.optString("question")
        val questionFeedback = questionObject.optInt("answer")
        val remarkFeedback = questionObject.optString("remarks").toString()

        var questionFeedbackAns = 0
        var remarkFeedbackAns = ""

        val dialogBinding =
            NotificationFeedbackDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.questionTitleTV.text = question
        if (questionFeedback == 1) {
            showPositiveSelected(true, dialogBinding)
        } else if (questionFeedback == 2) {
            showPositiveSelected(false, dialogBinding)
        }
        if (remarkFeedback.isNotEmpty() && remarkFeedback != "null") {
            dialogBinding.remarkEditText.setText(remarkFeedback)
        }
        dialogBinding.ivClose.setOnClickListener {
            farmerViewModel.addNotificationFeedback(
                "",
                notificationType,
                notificationId,
                feedbackId.toString(),
                "0"
            )
            dialog.dismiss()
        }
        dialogBinding.btnSubmit.setOnClickListener {
            remarkFeedbackAns = dialogBinding.remarkEditText.text.toString()
            if (questionFeedbackAns != 0) {
                farmerViewModel.addNotificationFeedback(
                    remarkFeedbackAns,
                    notificationType,
                    notificationId,
                    feedbackId.toString(),
                    questionFeedbackAns.toString()
                )
            } else {
                Toast.makeText(this, "Please Select any Response", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.cardNotHelpful.setOnClickListener {
            questionFeedbackAns = 2
            showPositiveSelected(false, dialogBinding)
        }
        dialogBinding.cardHelpful.setOnClickListener {
            questionFeedbackAns = 1
            showPositiveSelected(true, dialogBinding)
        }

        dialog.show()
    }

    private fun showPositiveSelected(
        isPositiveSelected: Boolean, dialogBinding:
        NotificationFeedbackDialogBinding
    ) {
        if (isPositiveSelected) {
            dialogBinding.cardHelpful.strokeColor =
                ContextCompat.getColor(this, R.color.positive_feedback_color)
            dialogBinding.cardNotHelpful.strokeColor =
                ContextCompat.getColor(this, R.color.negative_feedback_color)

            ImageViewCompat.setImageTintList(
                dialogBinding.iconHelpful,
                ContextCompat.getColorStateList(this, R.color.positive_feedback_color)
            )
            ImageViewCompat.setImageTintList(
                dialogBinding.iconNotHelpful,
                ContextCompat.getColorStateList(this, R.color.button_feedback_tint)
            )
        } else {
            dialogBinding.cardNotHelpful.strokeColor =
                ContextCompat.getColor(this, R.color.positive_feedback_color)
            dialogBinding.cardHelpful.strokeColor =
                ContextCompat.getColor(this, R.color.negative_feedback_color)
            ImageViewCompat.setImageTintList(
                dialogBinding.iconHelpful,
                ContextCompat.getColorStateList(this, R.color.button_feedback_tint)
            )
            ImageViewCompat.setImageTintList(
                dialogBinding.iconNotHelpful,
                ContextCompat.getColorStateList(this, R.color.positive_feedback_color)
            )
        }
    }

    private fun fetchCropList(flatCropId: Int?) {
        farmerViewModel.cropCategoryResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
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

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        farmerViewModel.getCropCategoriesAndCropDetails(languageToLoad)
    }

    private fun setUpPageContent(jsonObject: JSONObject, notificationId: String) {
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
        binding.redirectTextView.setOnClickListener {
            if (questionJsonObject != null) {
                val isAnswered = questionJsonObject?.optBoolean("is_answered", false)
                if (isAnswered == false) {
                    try {
                        setUpFeedbackDialog(notificationType, notificationId, questionJsonObject!!)
                    } catch (e: Exception) {
                        redirectToScreen(page)
                    }
                } else {
                    redirectToScreen(page)
                }
            } else {
                redirectToScreen(page)
            }
        }
        farmerViewModel.updateNotificationStatus(userId = farmerId, notificationId, type)
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
            "dashboard" -> Intent(this, NewDashboardMainActivity::class.java)
            "etl_page" -> Intent(this, AgriStackAdvisoryActivity::class.java)
            else -> Intent(this, NewDashboardMainActivity::class.java)
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

    private fun safelyNavigateToPreviousScreen() {
        if (questionJsonObject != null) {
            val isAnswered = questionJsonObject?.optBoolean("is_answered", false)
            if (isAnswered == false) {
                Log.d(TAG, "safelyNavigateToPreviousScreen: $isAnswered")
                try {
                    setUpFeedbackDialog(
                        notificationType,
                        notificationId,
                        questionJsonObject!!
                    )
                } catch (e: Exception) {
                    startActivity(
                        Intent(
                            this@DetailedNotificationActivity,
                            NotificationActivity::class.java
                        )
                    )
                }
            } else {
                startActivity(
                    Intent(
                        this@DetailedNotificationActivity,
                        NotificationActivity::class.java
                    )
                )
            }
        } else {
            startActivity(
                Intent(
                    this@DetailedNotificationActivity,
                    NotificationActivity::class.java
                )
            )
        }
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.clarity.Clarity
import com.squareup.picasso.Picasso
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.helpers.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDashboardScreenBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CropRecyclerSapAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DashboardAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DrawerMenuAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.AuthenticateFarmerIdActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.ProfileScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.etl.AgriStackAdvisoryActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate.ClimateResilientTechnology
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.DBTActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice.MarketPrice
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.HealthCardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.sop.SOPActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala.ShetishalaActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.AboutActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.CreditsActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.CostCalculatorDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts.ExpertsCornerFarmerActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard.LeaderboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news.NewsListActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.WeatherActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.notification.NotificationActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.splash.SplashScreenActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper.shrinkToCenter
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.getLatestAdvisoriesAsJsonArray
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.app_util.ApUtil
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.SideNavMenuHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class DashboardScreen : AppCompatActivity(), OnItemClickListener, OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivityDashboardScreenBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var navUserName: TextView
    private var consentMessage: String? = null
    private var districtCode: Int = 0
    private var villageCode: Int = 0
    private lateinit var navUserPhone: TextView
    private lateinit var languageToLoad: String
    private var farmerId = 0
    private var cropId = 0
    private var savedCropName = ""
    private var savedCropId = 0
    private var savedCropSowingDate: String? = null
    private var savedCropWoTRId: String? = null
    private var savedCropImageUrl: String? = null
    private lateinit var appPreferenceManager: AppPreferenceManager
    private var jsonArray: JSONArray? = null
    private var showToast = true
    private var etlAdvisoryJsonArray: JSONArray = JSONArray()
    private var selectedCropList: ArrayList<CropsCategName>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DashboardScreen).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        showToast = true
        binding = ActivityDashboardScreenBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        askForPermissions()
        observeResponse()
        setUpListeners()
        FirebaseHelper(this)

        binding.appBarMain.dashboardScreen.progressBar.visibility = View.VISIBLE
        binding.appBarMain.dashboardScreen.temperatureTextView.visibility = View.GONE
        appPreferenceManager = AppPreferenceManager(this)

        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.validateFCMToken(this)
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }

        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.fetchWeatherDetails(this, languageToLoad)
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }
        binding.appBarMain.dashboardScreen.greetingsTextView.text = greetingMessage
        binding.appBarMain.dashboardScreen.timestampTextView.text = formattedTimestamp

        this.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        setSupportActionBar(binding.appBarMain.toolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayShowTitleEnabled(false)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout1)


        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerSlideAnimationEnabled = true

        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                // 👉 Drawer is fully opened
                Clarity.sendCustomEvent("SIDEBAR_BUTTON_CLICKED")
            }

            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        // Set Data
        val userName: String =
            AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName)
        val userNumber: String =
            AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
        val hView = binding.navView.getHeaderView(0)
        navUserName = hView.findViewById(R.id.tv_farmerName)
        navUserPhone = hView.findViewById(R.id.tv_famerPhoneNumber)
        if (userName != "USER_NAME") {
            val capitalizeStrName: String = ApUtil.getCamelCaseStreing(userName)
            navUserName.text = capitalizeStrName.ifEmpty { userName }
            navUserPhone.text = userNumber
        }

        binding.appBarMain.dashboardScreen.gridViewDashboard.columnWidth =
            GridView.STRETCH_COLUMN_WIDTH
        if (languageToLoad.equals("en", ignoreCase = true)) {
            binding.appBarMain.dashboardScreen.gridViewDashboard.adapter = DashboardAdapter(
                this, arrayCategory, arrayCategoryImg, "single_item_grid"
            )
        } else if (languageToLoad.equals("mr", ignoreCase = true)) {
            binding.appBarMain.dashboardScreen.gridViewDashboard.adapter = DashboardAdapter(
                this, arrayCategoryMarathi, arrayCategoryImg, "single_item_grid"
            )
        }
        setVersion()
        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.getFarmerSelectedCrop(this, languageToLoad)
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }

        val animation = AnimationUtils.loadAnimation(this, R.anim.blink)
        binding.appBarMain.dashboardScreen.etlWarningLayout.animation = animation

        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.getNotificationList(this)
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun shakeAnimationChatbot() {
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        binding.appBarMain.dashboardScreen.chatbotIcon.startAnimation(shakeAnimation)

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            binding.appBarMain.dashboardScreen.chatbotIcon.clearAnimation()
        }
    }

    private fun bubbleAnimationChatbot() {
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.appBarMain.dashboardScreen.chatBubbleImageView.animate()
                .alpha(0f)
                .setDuration(500) // animation duration in ms
                .withEndAction {
                    binding.appBarMain.dashboardScreen.chatBubbleImageView.visibility = View.GONE
                    binding.appBarMain.dashboardScreen.chatBubbleImageView.alpha =
                        1f // reset alpha in case you show it again
                }
                .start()
        }
        shrinkToCenter(binding.appBarMain.dashboardScreen.chatBubbleImageView)
    }

    private fun setUpListeners() {

        init()
        setUpDrawerMenu()
        dashboardGridItemsLayoutSetup()
        shakeAnimationChatbot()
        bubbleAnimationChatbot()

        binding.appBarMain.imgLangChange.setOnClickListener { openChangeLangPopup() }
        binding.appBarMain.imgNotification.setOnClickListener {
            val intent = Intent(
                this@DashboardScreen,
                NotificationActivity::class.java
            )
            startActivity(intent)
        }

        binding.appBarMain.dashboardScreen.changeCropText.setOnClickListener {
            val intent = Intent(
                this@DashboardScreen,
                AddCropActivity::class.java
            )
            appPreferenceManager.clearPreference(AppConstants.ACTION_FROM_DASHBOARD)
            startActivity(intent)
        }

        binding.appBarMain.dashboardScreen.addCropCardView.setOnClickListener {
            val intent = Intent(
                this@DashboardScreen,
                AddCropActivity::class.java
            )
            appPreferenceManager.clearPreference(AppConstants.ACTION_FROM_DASHBOARD)
            startActivity(intent)
        }

        binding.appBarMain.dashboardScreen.temperatureLayout.setOnClickListener {
            val tempTXT = binding.appBarMain.dashboardScreen.temperatureTextView.text
            if (tempTXT == "22°C") {
                Toast.makeText(this, "Weather isn't updated Currently", Toast.LENGTH_SHORT).show()
            } else {
                val weather = Intent(
                    this@DashboardScreen,
                    WeatherActivity::class.java
                )
                startActivity(weather)
            }
        }

        binding.appBarMain.dashboardScreen.customNavBottom.navHome.setOnClickListener {
            Clarity.sendCustomEvent("HOME_BUTTON_CLICKED")
            startActivity(
                Intent(
                    this@DashboardScreen,
                    DashboardScreen::class.java
                )
            )
        }

        binding.appBarMain.dashboardScreen.chatbotIcon.setOnClickListener {
            Clarity.sendCustomEvent("VISTAAR_AI_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(Intent(this, ChatbotActivity::class.java))
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.appBarMain.dashboardScreen.customNavBottom.navChc.setOnClickListener {
            Clarity.sendCustomEvent("CHC_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(
                    Intent(
                        this@DashboardScreen,
                        CHCenterActivity::class.java
                    )
                )
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.appBarMain.dashboardScreen.customNavBottom.navVideos.setOnClickListener {
            Clarity.sendCustomEvent("VIDEOS_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(
                    Intent(
                        this@DashboardScreen,
                        VideosActivity::class.java
                    )
                )
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.appBarMain.dashboardScreen.customNavBottom.navShetishala.setOnClickListener {
            Clarity.sendCustomEvent("DBT_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(
                    Intent(
                        this@DashboardScreen,
                        ShetishalaActivity::class.java
                    )
                )
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.appBarMain.dashboardScreen.deleteCropImageView.setOnClickListener {
            cropId = savedCropId
            deleteDialog()
        }

        binding.appBarMain.dashboardScreen.etlWarningLayout.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.etl_crossed_dialog, null)
            val cropSapRecyclerView =
                dialogView.findViewById<RecyclerView>(R.id.cropSapRecyclerView)
            val redirectToETLAdvisoryTextView =
                dialogView.findViewById<TextView>(R.id.redirectToETLAdvisoryTextView)
            val cropRecyclerSapAdapter =
                CropRecyclerSapAdapter(getLatestAdvisoriesAsJsonArray(etlAdvisoryJsonArray))
            cropSapRecyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(this@DashboardScreen)
                Log.d("TAGGER", "onCreate: ${getLatestAdvisoriesAsJsonArray(etlAdvisoryJsonArray)}")
                adapter = cropRecyclerSapAdapter
            }

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val closeIcon = dialogView.findViewById<ImageView>(R.id.closeIcon)
            redirectToETLAdvisoryTextView.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(this, AgriStackAdvisoryActivity::class.java))
            }

            cropSapRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    cropSapRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val itemCount = cropRecyclerSapAdapter.itemCount
                    val visibleItems = minOf(itemCount, 3) // Show up to 3 items
                    val itemView = cropSapRecyclerView.findViewHolderForAdapterPosition(0)?.itemView

                    if (itemView != null) {
                        val itemHeight = itemView.measuredHeight
                        val maxHeight = itemHeight * visibleItems
                        cropSapRecyclerView.layoutParams.height = maxHeight
                        cropSapRecyclerView.requestLayout()
                    }
                }
            })

            dialog.show()
            closeIcon.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun observeResponse() {

        farmerViewModel.error.observe(this) {
            LocalCustom.createSnackbar(binding.root, it)
            Log.d("TAGGER", "onCreate: $it")
        }

        farmerViewModel.checkFCMTokenResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val status = jSONObject.optInt("status")
                if (status != 200) {
                    FirebaseHelper(this).getFCMToken { fcmToken ->
                        farmerViewModel.updateFCMToken(this, fcmToken)
                    }
                } else {
                    AppPreferenceManager(this).saveBoolean("FCM_VALIDATED", true)
                }
            }
        }

        farmerViewModel.getNotificationResponse.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val notificationJsonArray = jsonObject.getJSONArray("notifications")

                var unreadCount = 0
                for (i in 0 until notificationJsonArray.length()) {
                    val notification = notificationJsonArray.getJSONObject(i)
                    if (notification.optInt("is_read") == 0) {
                        unreadCount++
                    }
                }
                // Optionally update your badge view
                updateNotificationCount(unreadCount)
            }
        }

        farmerViewModel.getCropSapAdvisoryResponse.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val jsonArray = jsonObject.optJSONArray("advisory")
                if (jsonArray.length() != 0) {
                    binding.appBarMain.dashboardScreen.etlWarningLayout.visibility = View.VISIBLE
                    etlAdvisoryJsonArray = jsonArray
                }
            }
        }

        farmerViewModel.getFarmerSelectedCrop.observe(this) { response ->
            response ?: return@observe

            if (isFinishing || isDestroyed) return@observe

            try {
                val jsonObject = JSONObject(response.toString())
                val cropResponse = ResponseModel(jsonObject)

                if (cropResponse.getStatus()) {
                    val selectedCrops = cropResponse.getDataArrays()
                    if (selectedCrops.length() > 0) {
                        selectedCropList = ArrayList()

                        for (j in 0 until selectedCrops.length()) {
                            val selectedCrop = selectedCrops.getJSONObject(j)

                            savedCropId = selectedCrop.getInt("crop_id")
                            savedCropName = selectedCrop.getString("name")
                            savedCropImageUrl = selectedCrop.getString("image")
                            savedCropSowingDate = selectedCrop.getString("sowing_date")
                            savedCropWoTRId = selectedCrop.getString("wotr_crop_id")

                            AppPreferenceManager(this).saveInt("CROP_ID_SAVED", savedCropId)
                            AppPreferenceManager(this).saveString("CROP_NAME_SAVED", savedCropName)
                            AppPreferenceManager(this).saveString(
                                "CROP_IMAGE_SAVED",
                                savedCropImageUrl
                            )
                            AppPreferenceManager(this).saveString(
                                "CROP_SOWING_DATE_SAVED",
                                savedCropSowingDate
                            )
                            AppPreferenceManager(this).saveString(
                                "CROP_WOTR_ID_SAVED",
                                savedCropWoTRId
                            )

                            binding.appBarMain.dashboardScreen.apply {
                                addChangeCropTV.setText(R.string.change_Crop)
                                addChangeCropIV.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@DashboardScreen,
                                        R.drawable.ic_swap
                                    )
                                )
                                savedCropNameCardView.visibility = View.VISIBLE
                                savedCropNameTextView.text = savedCropName
                                yourCropTv.visibility = View.GONE
                                addCropCardView.visibility = View.GONE
                            }

                            if (!savedCropImageUrl.isNullOrEmpty() && !isFinishing && !isDestroyed) {
                                Picasso.get()
                                    .load(savedCropImageUrl)
                                    .fit()
                                    .centerCrop()
                                    .error(R.drawable.ic_no_data_found) // fallback image
                                    .into(binding.appBarMain.dashboardScreen.savedCropNameImageView)
                            }

                            appPreferenceManager.saveInt("saved_crop_id", savedCropId)

                            selectedCropList?.add(
                                CropsCategName(
                                    savedCropId,
                                    savedCropName,
                                    savedCropImageUrl,
                                    savedCropWoTRId
                                )
                            )
                        }

                        AppSettings.getInstance().setList(
                            this,
                            AppConstants.kFarmerCrop,
                            mutableListOf<Any>(*selectedCropList!!.toTypedArray())
                        )

                    } else {
                        binding.appBarMain.dashboardScreen.apply {
                            savedCropNameCardView.visibility = View.GONE
                            yourCropTv.visibility = View.VISIBLE
                            yourCropTv.setText(R.string.no_crops_added)
                            addCropCardView.visibility = View.VISIBLE
                            addChangeCropTV.setText(R.string.add_Crop)
                            addChangeCropIV.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@DashboardScreen,
                                    R.drawable.baseline_add_24
                                )
                            )
                        }
                    }
                } else {
                    UIToastMessage.show(this, cropResponse.getResponse())
                }
            } catch (e: Exception) {
                Log.e("fetchReceivingData", "Exception: ${e.localizedMessage}")
            }
        }

        // Observe deletion of selected crop
        farmerViewModel.deleteFarmerSelectedCrop.observe(this) { response ->
            try {
                val jsonObject = JSONObject(response.toString())
                val deleteResponse = ResponseModel(jsonObject)

                if (deleteResponse.getStatus()) {
                    if (showToast) {
                        UIToastMessage.show(this, getString(R.string.selected_crop_deleted))
                    }

                    AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null)
                    selectedCropList?.clear()
                    farmerViewModel.getFarmerSelectedCrop(this, languageToLoad)
                    savedCropName = ""
                    AppPreferenceManager(this).saveInt("CROP_ID_SAVED", 0)
                    AppPreferenceManager(this).clearPreference("CROP_NAME_SAVED")
                    AppPreferenceManager(this).clearPreference("CROP_IMAGE_SAVED")
                    AppPreferenceManager(this).clearPreference("CROP_SOWING_DATE_SAVED")
                    AppPreferenceManager(this).clearPreference("CROP_WOTR_ID_SAVED")
                } else {
                    UIToastMessage.show(this, deleteResponse.getResponse())
                }
            } catch (e: Exception) {
                Log.e("deleteSelectedCrop", "Exception: ${e.localizedMessage}")
            }
        }

        // Observe weather details
        farmerViewModel.weatherResponse.observe(this) { response ->
            response ?: return@observe

            try {
                binding.appBarMain.dashboardScreen.apply {
                    progressBar.visibility = View.GONE
                    temperatureTextView.visibility = View.VISIBLE
                    progressBar.progress = 0
                }

                val jsonObject = JSONObject(response.toString())
                appPreferenceManager.saveString(
                    AppConstants.WEATHER_RESPONSE,
                    jsonObject.toString()
                )

                val weatherResponse = ResponseModel(jsonObject)
                if (weatherResponse.getStatus()) {
                    binding.appBarMain.dashboardScreen.temperatureTextView.text =
                        getTemperatureFromJSON(jsonObject)
                }

            } catch (e: Exception) {
                Log.e("weatherResponse", "Exception: ${e.localizedMessage}")
            }
        }

        // Observe user details
        farmerViewModel.userDetailsResponse.observe(this) { response ->
            response ?: return@observe

            try {
                val jsonObject = JSONObject(response.toString())
                Log.d("TAGGER", "observeResponse: $jsonObject")
                if (jsonObject.optInt("status") == 200) {
                    val data = jsonObject.optJSONObject("data") ?: return@observe

                    val name = data.optString("Name", "")
                    val mobNo = data.optString("MobileNo", "")
                    val emailId = data.optString("EmailId", "")
                    val ffaReg = data.optInt("FAAPRegistrationID", -1)
                    val distName = data.optString("DistrictName", "")
                    val distNameMr = data.optString("DistrictNameMr", "")
                    val distId = data.optInt("DistrictCode", 0)
                    val talukaName = data.optString("TalukaName", "")
                    val talukaNameMr = data.optString("TalukaNameMr", "")
                    val talukaId = data.optInt("TalukaCode", 0)
                    val villageId = data.optInt("VillageCode", 0)
                    val villageName = data.optString("VillageName", "")
                    val villageNameMr = data.optString("VillageNameMr", "")
                    val agristack_id = data.optString("farmer_id", "")
                    val consent = data.optBoolean("consent")
                    farmerViewModel.getCropSapAdvisory(
                        this,
                        villageId
                    ) //TODO: static village code 537820
                    districtCode = distId
                    villageCode = talukaId
                    binding.appBarMain.dashboardScreen.weatherTalukaTV.text =
                        if (languageToLoad == "mr") talukaNameMr else talukaName
                    AppSettings.getInstance().apply {
                        setValue(this@DashboardScreen, AppConstants.uName, name)
                        setValue(this@DashboardScreen, AppConstants.uMobileNo, mobNo)
                        setValue(this@DashboardScreen, AppConstants.uEmail, emailId)
                        if (ffaReg != -1) setIntValue(
                            this@DashboardScreen,
                            AppConstants.fREGISTER_ID,
                            ffaReg
                        )
                        setValue(this@DashboardScreen, AppConstants.uDIST, distName)
                        setValue(this@DashboardScreen, AppConstants.uDISTMR, distNameMr)
                        setIntValue(this@DashboardScreen, AppConstants.uDISTId, distId)
                        setValue(this@DashboardScreen, AppConstants.uTALUKA, talukaName)
                        setValue(this@DashboardScreen, AppConstants.uTALUKAMR, talukaNameMr)
                        setIntValue(this@DashboardScreen, AppConstants.uTALUKAID, talukaId)
                        setValue(this@DashboardScreen, AppConstants.uVILLAGE, villageName)
                        setValue(this@DashboardScreen, AppConstants.uVILLAGEMR, villageNameMr)
                        setIntValue(this@DashboardScreen, AppConstants.uVILLAGEID, villageId)
                        setBooleanValue(this@DashboardScreen, AppConstants.userDataSaved, true)
                        setValue(this@DashboardScreen, AppConstants.AGRISTACKID, agristack_id)
                    }

                    binding.appBarMain.dashboardScreen.userFullNameTextView.text = name
                    val userName = AppSettings.getInstance().getValue(this, AppConstants.uName, "")
                    val userNumber =
                        AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, "")
                    val headerView = binding.navView.getHeaderView(0)
                    navUserName = headerView.findViewById(R.id.tv_farmerName)
                    navUserPhone = headerView.findViewById(R.id.tv_famerPhoneNumber)

                    navUserName.text = ApUtil.getCamelCaseStreing(userName).ifEmpty { userName }
                    navUserPhone.text = userNumber
                    farmerViewModel.fetchWeatherDetails(this, languageToLoad)
                    if (agristack_id != "null" && farmerId != null) {
                        if (!consent) {
                            showDialogForConsent()
                        } else {
                            Log.d("TAGGER", "observeResponse: consent is given")
                        }
                    } else {
                        val showDialog = appPreferenceManager.getBoolean("AGRISTACK_LOGIN_DIALOG")
                        if (!showDialog) {
                            showAgristackLinkingDialog()
                        }
                        Log.d("TAGGER", "observeResponse: $consent farmer id null")
                    }
                }
            } catch (e: Exception) {
                Log.e("userDetailsObserver", "Exception: ${e.localizedMessage}")
            }
        }


        farmerViewModel.updateFCMTokenResponse.observe(this) {
            Log.d("TAGGER", "logoutFromApp: $it")
            if (it != null) {
                AppPreferenceManager(this).saveBoolean("FCM_VALIDATED", true)
                val jsonObject = JSONObject(it.toString())
                val response = jsonObject.optString("response")
                if (response == "FCM Cleared") {
                    AppSettings.getInstance().setValue(this, AppConstants.uName, AppConstants.uName)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uEmail, AppConstants.uEmail)
                    AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, 0)
                    AppSettings.getInstance().setValue(this, AppConstants.uDIST, AppConstants.uDIST)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, 0)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, 0)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uVILLAGEID, 0)
                    AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null)
                    AppUtility.getInstance().clearAppSharedPrefData(this, AppConstants.kSHARED_PREF)
                    AppSettings.getInstance()
                        .setBooleanValue(this, AppConstants.userDataSaved, false)
                    val intent = Intent(this@DashboardScreen, SplashScreenActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("TAGGER", "logoutFromApp: $response")
                }
            }
        }

        farmerViewModel.consentResponse.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val status = jsonObject.optInt("status")
                if (status == 200) {
                    Toast.makeText(this, consentMessage ?: "Consent Submitted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val responseText = jsonObject.optString("response")
                    Toast.makeText(this, responseText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDialogForConsent() {

        val dialogLayout = LayoutInflater.from(this)
            .inflate(R.layout.dialog_consent_layout, null)

        val consentDialog = AlertDialog.Builder(this)
            .setView(dialogLayout)
            .setCancelable(false)
            .create()

        // ✅ get the view from the dialog layout, not the activity
        val acceptText = dialogLayout.findViewById<TextView>(R.id.acceptText)
        val declineText = dialogLayout.findViewById<TextView>(R.id.declineText)
        acceptText.setOnClickListener {
            consentMessage = ContextCompat.getString(this, R.string.consent_accepted)
            farmerViewModel.updateConsent(this, true)
            consentDialog.dismiss() // optionally close the dialog
        }
        declineText.setOnClickListener {
            val confirmationDialog =
                AlertDialog.Builder(this).setTitle(R.string.withdraw_consent)
                    .setMessage(R.string.withdraw_consent_desc_decline)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        consentMessage = ContextCompat.getString(this, R.string.consent_declined)
                        farmerViewModel.updateConsent(this, false)
                        logoutFromApp()
                        dialog.dismiss()
                        consentDialog.dismiss()
                    }.setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
            confirmationDialog.show() // optionally close the dialog
        }
        consentDialog.show()
    }

    private fun updateNotificationCount(unreadNotificationsCount: Int) {
        if (unreadNotificationsCount > 0) {
            binding.appBarMain.notificationBadge.text = unreadNotificationsCount.toString()
            binding.appBarMain.notificationBadge.visibility = View.VISIBLE
        } else {
            binding.appBarMain.notificationBadge.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        shakeAnimationChatbot()
        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.getNotificationList(this)
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }
    }

    private fun askForPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Only check POST_NOTIFICATIONS for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for ((index, permission) in permissions.withIndex()) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    UIToastMessage.show(
                        this@DashboardScreen,
                        "Access Permission Granted"
                    )
                    // Perform the related action (e.g., accessing the camera) if needed
                } else {
                    UIToastMessage.show(
                        this@DashboardScreen,
                        "Access Permission Denied"
                    )
                    // Optionally handle specific denied permission cases here
                }
            }
        }
    }

    private val greetingMessage: String
        get() {
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]

            return when (hour) {
                in 5..11 -> {
                    getString(R.string.good_morning)
                }

                in 12..16 -> {
                    getString(R.string.good_afternoon)
                }

                in 17..20 -> {
                    getString(R.string.good_evening)
                }

                else -> {
                    getString(R.string.good_night)
                }
            }
        }

    private fun dashboardGridItemsLayoutSetup() {
        binding.appBarMain.dashboardScreen.gridViewDashboard.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                when (position) {
                    0 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            if (savedCropName.isEmpty()) {
                                val sharing =
                                    Intent(this@DashboardScreen, AddCropActivity::class.java)
                                appPreferenceManager.saveString(
                                    AppConstants.ACTION_FROM_DASHBOARD,
                                    AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD
                                )
                                startActivity(sharing)
                            } else {
                                val intent = Intent(this, AdvisoryCropActivity::class.java)
                                intent.putExtra("id", savedCropId)
                                intent.putExtra("wotr_crop_id", savedCropWoTRId)
                                intent.putExtra("mUrl", savedCropImageUrl)
                                intent.putExtra("sowingDate", savedCropSowingDate)
                                intent.putExtra("mName", savedCropName)
                                startActivity(intent)
                            }
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }

                    }

                    1 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            if (savedCropName.isEmpty()) {
                                val sharing =
                                    Intent(this@DashboardScreen, AddCropActivity::class.java)
                                appPreferenceManager.saveString(
                                    AppConstants.ACTION_FROM_DASHBOARD,
                                    AppConstants.SOP_FROM_DASHBOARD
                                )
                                startActivity(sharing)
                            } else {
                                val intent = Intent(this, SOPActivity::class.java)
                                intent.putExtra("id", savedCropId)
                                intent.putExtra("wotr_crop_id", savedCropWoTRId)
                                intent.putExtra("mUrl", savedCropImageUrl)
                                intent.putExtra("mName", savedCropName)
                                startActivity(intent)
                            }
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }
                    }

                    2 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            val healthIntent = Intent(
                                this@DashboardScreen,
                                HealthCardActivity::class.java
                            )
                            startActivity(healthIntent)
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }
                    }

                    3 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            if (savedCropName.isEmpty()) {
                                val comingSoonIntent = Intent(
                                    this@DashboardScreen,
                                    AddCropActivity::class.java
                                )
                                appPreferenceManager.saveString(
                                    AppConstants.ACTION_FROM_DASHBOARD,
                                    AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD
                                )
                                startActivity(comingSoonIntent)
                            } else {
                                val intent = Intent(this, FertilizerCalculatorActivity::class.java)
                                intent.putExtra("id", savedCropId)
                                intent.putExtra("wotr_crop_id", savedCropWoTRId?.toInt())
                                intent.putExtra("mUrl", savedCropImageUrl)
                                intent.putExtra("mName", savedCropName)
                                intent.putExtra("sowingDate", savedCropSowingDate)
                                startActivity(intent)
                            }
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }
                    }

                    4 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            val addPeople =
                                Intent(this@DashboardScreen, ClimateResilientTechnology::class.java)
                            startActivity(addPeople)
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }

                    }

                    5 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            if (savedCropName.isEmpty()) {
                                val sharing =
                                    Intent(this@DashboardScreen, AddCropActivity::class.java)
                                appPreferenceManager.saveString(
                                    AppConstants.ACTION_FROM_DASHBOARD,
                                    AppConstants.PEST_AND_DISEASES_STAGES
                                )
                                startActivity(sharing)
                            } else {
                                val intent = Intent(this, PestsAndDiseasesStages::class.java)
                                intent.putExtra("id", savedCropId)
                                intent.putExtra("wotr_crop_id", savedCropWoTRId)
                                intent.putExtra("sowingDate", savedCropSowingDate)
                                intent.putExtra("mUrl", savedCropImageUrl)
                                intent.putExtra("mName", savedCropName)
                                startActivity(intent)
                            }
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }
                    }

                    6 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            val marketIntent = Intent(this@DashboardScreen, MarketPrice::class.java)
                            startActivity(marketIntent)
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }
                    }

                    7 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            startActivity(
                                Intent(
                                    this@DashboardScreen,
                                    DBTActivity::class.java
                                )
                            )
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }
                    }

                    8 -> {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            val warehouseIntent =
                                Intent(this@DashboardScreen, Warehouse::class.java)
                            startActivity(warehouseIntent)
                        } else {
                            LocalCustom.createSnackbar(binding.root, "Internet not available!")
                        }
                    }
                }
            }
    }

    private fun init() {
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (farmerId > 0) {
            if (NetworkUtils.isInternetAvailable(this)) {
                farmerViewModel.fetchUserInformation(this, farmerId)
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }
    }

    private fun setUpDrawerMenu() {
        try {
            jsonArray = if (languageToLoad.equals("en", ignoreCase = true)) {
                SideNavMenuHelper.instance.menuOption
            } else {
                SideNavMenuHelper.instance.menuOptionMarathi
            }
            val menuAdapter = jsonArray?.let { DrawerMenuAdapter(this, it) }
            binding.menuListView.adapter = menuAdapter
            binding.menuListView.onItemClickListener = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setVersion() {
        val versionName = LocalCustom.getVersionName(this)
        binding.appVerTextView.text = buildString {
            append(getString(R.string.app_version))
            append(" ")
            append(versionName)
        }
        AppSettings.getInstance()
            .setValue(this@DashboardScreen, AppConstants.kAPP_BUILD_VERSION, versionName)
    }

    private fun openChangeLangPopup() {
        val dialog = Dialog(this@DashboardScreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_language_selector)

        val tvEnglish = dialog.findViewById<TextView>(R.id.tv_eng)
        val tvMarathi = dialog.findViewById<TextView>(R.id.tv_mar)

        tvEnglish.setOnClickListener {
            val languageToLoad = "en"
            configureLocale(baseContext, languageToLoad)
            AppSettings.setLanguage(this@DashboardScreen, "1")

            finish()
            startActivity(intent)

            dialog.dismiss()
            farmerViewModel.getFarmerSelectedCrop(this, languageToLoad)
        }

        tvMarathi.setOnClickListener {
            val languageToLoad = "mr"
            configureLocale(baseContext, languageToLoad)
            AppSettings.setLanguage(this@DashboardScreen, "2")

            finish()
            startActivity(intent)

            dialog.dismiss()
            farmerViewModel.getFarmerSelectedCrop(this, languageToLoad)
        }

        dialog.show()
    }

    private fun deleteDialog() {
        val dialog: Dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.delete_crop_title)
            .setMessage(R.string.delete_crop_message)
            .setPositiveButton(
                R.string.delete_crop_yes
            ) { _: DialogInterface?, _: Int ->
                farmerViewModel.deleteFarmerSelectedCrop(this, cropId)
            }
            .setNegativeButton(
                R.string.delete_crop_no
            ) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, l: Long) {
        try {
            val jsonObject = jsonArray!!.getJSONObject(position)
            val id = jsonObject.getInt("id")
            when (id) {
                0 -> {
                    val intent = Intent(this@DashboardScreen, ProfileScreen::class.java)
                    intent.putExtra("FAAPRegistrationID", farmerId)
                    startActivity(intent)
                }

                1 -> if (farmerId > 0) {
                    startActivity(
                        Intent(
                            this@DashboardScreen,
                            AboutActivity::class.java
                        )
                    )
                } else {
                    UIToastMessage.show(this@DashboardScreen, "Please Login First...")
                }

                2 -> {
                    val notificationIntent = Intent(
                        this@DashboardScreen,
                        ExpertsCornerFarmerActivity::class.java
                    )
                    startActivity(notificationIntent)
                }

                3 -> {
                    val notificationIntent = Intent(
                        this@DashboardScreen,
                        CreditsActivity::class.java
                    )
                    startActivity(notificationIntent)
                }

                4 -> {
                    val notificationIntent = Intent(
                        this@DashboardScreen,
                        NewsListActivity::class.java
                    )
                    startActivity(notificationIntent)
                }

                5 -> {
                    val costCalculatorIntent = Intent(
                        this@DashboardScreen,
                        CostCalculatorDashboardActivity::class.java
                    )
                    startActivity(costCalculatorIntent)
                }

                6 -> {
                    startActivity(
                        Intent(
                            this@DashboardScreen,
                            LoginScreen::class.java
                        ).apply {
                            putExtra("from", "dashboard")
                        }
                    )
                }

                7 -> logoutFromApp()

                8 -> {
                    startActivity(
                        Intent(
                            this@DashboardScreen,
                            LeaderboardActivity::class.java
                        )
                    )
                }
            }
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout1)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun logoutFromApp() {
        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.updateFCMToken(this, "NA")
            appPreferenceManager.clearAll()
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, id: Any) {
        if (i == 2) {
            cropId = (id as Int)
            farmerViewModel.deleteFarmerSelectedCrop(this, cropId)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private val arrayCategory = arrayOf(
            "Crop Advisory",
            "S.O.P.",
            "Soil Health Card",
            "Fertilizer Calculator",
            "Climate Resilent Technology",
            "Pest and Diseases",
            "Market Price",
            "D.B.T.",
            "Warehouse"
        )

        private val arrayCategoryMarathi = arrayOf(
            "पीक सल्ला",
            "एस.ओ.पी.",
            "मृदा आरोग्य पत्रिका",
            "खत मात्रा गणक",
            "हवामानास अनुकूल शेती पद्धती",
            "कीड व रोग",
            "बाजारभाव",
            "डी.बी.टी.",
            "गोदाम"
        )

        var arrayCategoryImg: IntArray = intArrayOf(
            R.drawable.ic_cropadvisory,
            R.drawable.ic_sop,
            R.drawable.ic_soilhealthcard,
            R.drawable.ic_fertilizercalculator,
            R.drawable.ic_climateresilenttechnology,
            R.drawable.ic_pestsanddiseases,
            R.drawable.ic_marketprice,
            R.drawable.icon_dbt,
            R.drawable.ic_warehouse
        )

        val formattedTimestamp: String
            get() {
                val dateFormat = SimpleDateFormat(
                    "dd MMMM yyyy | HH:mm",
                    Locale.getDefault()
                )
                return dateFormat.format(Date())
            }
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

    private fun getTemperatureFromJSON(jsonObject: JSONObject): String {
        val temperatureObject = checkNotNull(jsonObject.optJSONObject("Temperature"))
        val tempMin = temperatureObject.optString("min")
        val tempMax = temperatureObject.optString("max")
        val temperature = "$tempMin°C / $tempMax°C"
        return temperature
    }

    private fun showAgristackLinkingDialog() {
        val context = ContextThemeWrapper(this, R.style.Theme_FarmerApp)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.famer_id_login_dialog, null)
        val agristackLoginDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        confirmButton.setOnClickListener {
            appPreferenceManager.saveBoolean("AGRISTACK_LOGIN_DIALOG", true)
            startActivity(Intent(this, AuthenticateFarmerIdActivity::class.java))
            agristackLoginDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            agristackLoginDialog.dismiss()
        }

        agristackLoginDialog.show()
    }
}
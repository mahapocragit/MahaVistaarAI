package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDashboardScreenBinding
import `in`.gov.mahapocra.mahavistaarai.ui.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DashboardAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DrawerMenuAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.VideosImageDetailsAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.Registration
import `in`.gov.mahapocra.mahavistaarai.ui.screens.chatbot.TempDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.forum.ForumActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate.ClimateResilientTechnology
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.DbtSchemes
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.HealthCardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.AboutActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.DbtStatus
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.Grievances
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.MyVillageProfilePdf
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news.NewsListActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.training.TrainingLocationSelection
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.notification.ComingSoonActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.splash.SplashScreenActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.WeatherActivity
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.ForceUpdateChecker
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.app_util.ApUtil
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.Executors

class DashboardScreen : AppCompatActivity(), ApiCallbackCode,
    OnItemClickListener, ForceUpdateChecker.OnUpdateNeededListener,
    OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivityDashboardScreenBinding
    private lateinit var navUserName: TextView
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
    private var selectedCropList: ArrayList<CropsCategName>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DashboardScreen).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        configureLocale(baseContext, languageToLoad)
        showToast = true
        binding = ActivityDashboardScreenBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        appPreferenceManager = AppPreferenceManager(this)
        init()
        FirebaseHelper(this)
        binding.appBarMain.dashboardScreen.deleteCropImageView.setOnClickListener {
            cropId = savedCropId
            deleteDialog()
        }

        binding.appBarMain.dashboardScreen.greetingsTextView.text = greetingMessage
        binding.appBarMain.dashboardScreen.timestampTextView.text = formattedTimestamp
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

        firebaseTokenFromServer
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check()
        setConfiguration()
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

        // Set Data
        val userName: String =
            AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName)
        val userNumber: String =
            AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
        binding.appBarMain.dashboardScreen.userFullNameTextView.text = userName
        val hView = binding.navView.getHeaderView(0)
        navUserName = hView.findViewById(R.id.tv_farmerName)
        navUserPhone = hView.findViewById(R.id.tv_famerPhoneNumber)
        if (userName != "USER_NAME") {
            val capitalizeStrName: String = ApUtil.getCamelCaseStreing(userName)
            navUserName.text = capitalizeStrName.ifEmpty { userName }
            navUserPhone.text = userNumber
        }

        // navigationView.setNavigationItemSelectedListener(this);
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

        binding.appBarMain.dashboardScreen.chatFab.setOnClickListener {
            startActivity(Intent(this, TempDashboardActivity::class.java))
        }

        appPreferenceManager.clearAll()
        dashboardGridItemsLayoutSetup()
        binding.appBarMain.imgLangChange.setOnClickListener { openChangeLangPopup() }
        binding.appBarMain.imgNotification.setOnClickListener {
            val intent = Intent(
                this@DashboardScreen,
                ComingSoonActivity::class.java
            )
            startActivity(intent)
        }
        binding.appBarMain.imgCallIcon.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callingFun()
            }
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
        setVersion()
        getFarmerSelectedCrop(languageToLoad)
        requestingPermissions()
        binding.appBarMain.dashboardScreen.bottomNavigation.itemIconTintList = null
        binding.appBarMain.dashboardScreen.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(
                    Intent(
                        this@DashboardScreen,
                        DashboardScreen::class.java
                    )
                )

                R.id.nav_about -> startActivity(
                    Intent(
                        this@DashboardScreen,
                        CHCenterActivity::class.java
                    )
                )

                R.id.nav_chat -> startActivity(
                    Intent(
                        this@DashboardScreen,
                        TempDashboardActivity::class.java
                    )
                )

                R.id.nav_help -> startActivity(
                    Intent(
                        this@DashboardScreen,
                        ForumActivity::class.java
                    )
                )

                R.id.nav_videos -> startActivity(
                    Intent(
                        this@DashboardScreen,
                        VideosActivity::class.java
                    )
                )
            }
            false
        }
    }

    private fun fetchTalukaMasterData() {
        val districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("district_code", districtID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.FARMER,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getTalukaList(requestBody)
                api.postRequest(responseCall, this@DashboardScreen, 4)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
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
                    0 -> if (savedCropName.isEmpty()) {
                        val sharing = Intent(this@DashboardScreen, AddCropActivity::class.java)
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

                    1 -> {
                        if (savedCropName.isEmpty()) {
                            val sharing = Intent(this@DashboardScreen, AddCropActivity::class.java)
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
                    }

                    2 -> {
                        val healthIntent = Intent(
                            this@DashboardScreen,
                            HealthCardActivity::class.java
                        )
                        startActivity(healthIntent)
                    }

                    3 -> if (savedCropName.isEmpty()) {
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
                        intent.putExtra("wotr_crop_id", savedCropWoTRId)
                        intent.putExtra("mUrl", savedCropImageUrl)
                        intent.putExtra("mName", savedCropName)
                        intent.putExtra("sowingDate", savedCropSowingDate)
                        startActivity(intent)
                    }

                    4 -> {
                        val addPeople =
                            Intent(this@DashboardScreen, ClimateResilientTechnology::class.java)
                        startActivity(addPeople)
                    }

                    5 -> if (savedCropName.isEmpty()) {
                        val sharing = Intent(this@DashboardScreen, AddCropActivity::class.java)
                        appPreferenceManager.saveString(
                            AppConstants.ACTION_FROM_DASHBOARD,
                            AppConstants.PEST_AND_DISEASES_STAGES
                        )
                        startActivity(sharing)
                    } else {
                        val intent = Intent(this, PestsAndDiseasesStages::class.java)
                        intent.putExtra("cropId", savedCropId)
                        intent.putExtra("wotr_crop_id", savedCropWoTRId)
                        intent.putExtra("sowingDate", savedCropSowingDate)
                        intent.putExtra("mUrl", savedCropImageUrl)
                        intent.putExtra("mName", savedCropName)
                        startActivity(intent)
                    }

                    6 -> {
                        val marketIntent = Intent(this@DashboardScreen, MarketPrice::class.java)
                        startActivity(marketIntent)
                    }

                    7 -> {
                        val warehouseIntent = Intent(this@DashboardScreen, Warehouse::class.java)
                        startActivity(warehouseIntent)
                    }

                    8 -> {
                        val dbtIntent = Intent(this@DashboardScreen, DbtSchemes::class.java)
                        startActivity(dbtIntent)
                    }
                }
            }
    }

    private val firebaseTokenFromServer: Unit
        get() {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String?> ->
                    if (!task.isSuccessful) {
                        Objects.requireNonNull(task.exception)
                            ?.printStackTrace()
                    }
                    val token = task.result
                    appPreferenceManager.saveString("FCM_TOKEN", token)
                }
        }

    private fun requestingPermissions() {
        var permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        // Check which permissions are not granted
        val permissionsToRequest: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        // Request the permissions that are not granted
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray<String>(),
                PERMISSION_REQUEST_CODE
            )
        }
    }


    fun init() {
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (farmerId > 0) {
            userDetails
        }
    }

    private fun setConfiguration() {
        try {
            if (languageToLoad.equals("en", ignoreCase = true)) {
                jsonArray = AppHelper.getInstance().getMenuOption()
            } else if (languageToLoad.equals("mr", ignoreCase = true)) {
                jsonArray = AppHelper.getInstance().getMenuOptionMarathi()
            }
            val menuAdapter = DrawerMenuAdapter(this, jsonArray, farmerId)
            binding.menuListView.adapter = menuAdapter
            binding.menuListView.onItemClickListener = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setVersion() {
        var pinfo: PackageInfo? = null
        try {
            pinfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val versionName = pinfo!!.versionName
        if (APIServices.DBT.equals("https://ilab-sso.mahapocra.gov.in/", ignoreCase = true)) {
            binding.appVerTextView.text = "${getString(R.string.app_version)} $versionName S"
        } else {
            binding.appVerTextView.text = "${getString(R.string.app_version)} $versionName"
        }
        AppSettings.getInstance()
            .setValue(this@DashboardScreen, AppConstants.kAPP_BUILD_VERSION, versionName)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun callingFun() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:02222163352"))
        startActivity(intent)
    }


    private val userDetails: Unit
        get() {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("FAAPRegistrationID", farmerId)

                val requestBody: RequestBody =
                    AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    this,
                    APIServices.FARMER,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )

                val handler = Handler()
                val runnable = Runnable {
                    val retrofit: Retrofit = api.getRetrofitInstance()
                    val apiRequest: APIRequest =
                        retrofit.create(APIRequest::class.java)
                    val responseCall: Call<JsonObject> =
                        apiRequest.getGetRegistration(requestBody)
                    api.postRequest(responseCall, this, 1)
                }
                handler.post(runnable)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    private fun getFarmerSelectedCrop(language: String?) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api_key", "67840097657891")
            jsonObject.put("lang", language)
            jsonObject.put("farmer_id", farmerId)

            val requestBody: RequestBody =
                AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(this, APIServices.FARMER, "", AppString(this).getkMSG_WAIT(), true)

            val handler = Handler()
            val runnable = Runnable {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest: APIRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getFarmersSelectedCrop(requestBody)
                api.postRequest(responseCall, this, 2)
            }
            handler.post(runnable)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun deleteFarmerSelectedCrop() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api_key", "67840097657891")
            jsonObject.put("crop_id", cropId)
            jsonObject.put("farmer_id", farmerId)

            val requestBody: RequestBody =
                AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(this, APIServices.FARMER, "", AppString(this).getkMSG_WAIT(), true)

            val handler = Handler()
            val runnable = Runnable {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest: APIRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.deleteSelectedCrop(requestBody)
                api.postRequest(responseCall, this, 3)
            }
            handler.post(runnable)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )

            AppSettings.setLanguage(this@DashboardScreen, "1")

            finish()
            startActivity(intent)

            dialog.dismiss()
            getFarmerSelectedCrop(languageToLoad)
        }

        tvMarathi.setOnClickListener {
            val languageToLoad = "mr"
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )

            AppSettings.setLanguage(this@DashboardScreen, "2")

            finish()
            startActivity(intent)

            dialog.dismiss()
            getFarmerSelectedCrop(languageToLoad)
        }

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    // Perform the related action (e.g., accessing the camera)
                    UIToastMessage.show(this@DashboardScreen, "Access Permission granted")
                } else {
                    // Permission was denied
                    // Notify the user and handle the situation gracefully
                    UIToastMessage.show(this@DashboardScreen, "Access Permission denied")
                }
            }
        }
    }

    override fun onFailure(obj: Any, th: Throwable, i: Int) {
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (jSONObject != null) {
            when (i) {
                1 -> {
                    try {
                        if (jSONObject.optInt("status") == 200) {
                            try {
                                val data = jSONObject.optJSONObject("data")
                                val strName = data?.getString("Name")
                                val strMobNo = data?.getString("MobileNo")
                                val strEmailId = data?.getString("EmailId")
                                val strFFAReg = data?.getInt("FAAPRegistrationID")
                                val strDistName = data?.getString("DistrictName")
                                val strDistId = data?.getInt("DistrictCode")
                                val strTalukaName = data?.getString("TalukaName")
                                val strTalukaId = data?.getInt("TalukaCode")
                                val strVillageId = data?.getInt("VillageCode")
                                val strVillageName = data?.getString("VillageName")
                                if (strDistId != null) {
                                    districtCode = strDistId
                                }
                                if (strTalukaId != null) {
                                    villageCode = strTalukaId
                                }
                                AppSettings.getInstance()
                                    .setValue(this, AppConstants.uName, strName)
                                binding.appBarMain.dashboardScreen.userFullNameTextView.text =
                                    strName
                                AppSettings.getInstance()
                                    .setValue(this, AppConstants.uMobileNo, strMobNo)
                                AppSettings.getInstance()
                                    .setValue(this, AppConstants.uEmail, strEmailId)
                                strFFAReg?.let {
                                    AppSettings.getInstance()
                                        .setIntValue(this, AppConstants.fREGISTER_ID, it)
                                }
                                AppSettings.getInstance()
                                    .setValue(this, AppConstants.uDIST, strDistName)
                                strDistId?.let {
                                    AppSettings.getInstance()
                                        .setIntValue(this, AppConstants.uDISTId, it)
                                }
                                AppSettings.getInstance()
                                    .setValue(this, AppConstants.uTALUKA, strTalukaName)
                                Log.d("TAGGER", "onResponse ->: $strTalukaId")
                                strTalukaId?.let {
                                    AppSettings.getInstance()
                                        .setIntValue(this, AppConstants.uTALUKAID, it)
                                }
                                AppSettings.getInstance()
                                    .setValue(this, AppConstants.uVILLAGE, strVillageName)
                                strVillageId?.let {
                                    AppSettings.getInstance()
                                        .setIntValue(this, AppConstants.uVILLAGEID, it)
                                }
                                AppSettings.getInstance()
                                    .setBooleanValue(this, AppConstants.userDataSaved, true)

                                val userName: String = AppSettings.getInstance()
                                    .getValue(this, AppConstants.uName, AppConstants.uName)
                                val userNumber: String = AppSettings.getInstance()
                                    .getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
                                val hView = binding.navView.getHeaderView(0)
                                navUserName = hView.findViewById(R.id.tv_farmerName)
                                navUserPhone = hView.findViewById(R.id.tv_famerPhoneNumber)
                                if (userName != "USER_NAME") {
                                    try {
                                        val capitalizeStrName: String =
                                            ApUtil.getCamelCaseStreing(userName)
                                        navUserName.text = capitalizeStrName.ifEmpty { userName }
                                        navUserPhone.text = userNumber
                                    } catch (e: StringIndexOutOfBoundsException) {
                                        e.printStackTrace()
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        fetchTalukaMasterData()
                    } catch (e: Exception) {
                        Log.d("TAGGER", "onResponse: $e")
                    }
                }

                2 -> {
                    try {
                        val farmersSelectedCropResponse = ResponseModel(jSONObject)
                        if (farmersSelectedCropResponse.getStatus()) {
                            val selectedCrops: JSONArray =
                                farmersSelectedCropResponse.getDataArrays()
                            if (selectedCrops.length() > 0) {
                                selectedCropList = ArrayList()
                                var j = 0
                                while (j < selectedCrops.length()) {
                                    try {
                                        val selectedCrop = selectedCrops.getJSONObject(j)
                                        savedCropId = selectedCrop.getInt("crop_id")
                                        savedCropName = selectedCrop.getString("name")
                                        savedCropImageUrl = selectedCrop.getString("image")
                                        savedCropSowingDate = selectedCrop.getString("sowing_date")
                                        Log.d("TAGGER", "onResponse: $savedCropSowingDate")
                                        savedCropWoTRId = selectedCrop.getString("wotr_crop_id")
                                        binding.appBarMain.dashboardScreen.addChangeCropTV.setText(R.string.change_Crop)
                                        binding.appBarMain.dashboardScreen.addChangeCropIV.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                this, R.drawable.ic_swap
                                            )
                                        )
                                        binding.appBarMain.dashboardScreen.savedCropNameCardView.visibility =
                                            View.VISIBLE
                                        binding.appBarMain.dashboardScreen.savedCropNameTextView.text =
                                            savedCropName
                                        Picasso.get().load(savedCropImageUrl).fit().centerCrop()
                                            .into(
                                                binding.appBarMain.dashboardScreen.savedCropNameImageView
                                            )
                                        binding.appBarMain.dashboardScreen.yourCropTv.visibility =
                                            View.GONE
                                        binding.appBarMain.dashboardScreen.addCropCardView.visibility =
                                            View.GONE
                                        selectedCropList!!.add(
                                            CropsCategName(
                                                selectedCrop.getInt("crop_id"),
                                                selectedCrop.getString("name"),
                                                selectedCrop.getString("image"),
                                                selectedCrop.getString("wotr_crop_id")
                                            )
                                        )
                                    } catch (e: JSONException) {
                                        throw RuntimeException(e)
                                    }
                                    j++
                                }
                                AppSettings.getInstance().setList(
                                    this, AppConstants.kFarmerCrop,
                                    mutableListOf<Any>(*selectedCropList!!.toTypedArray())
                                )
                            } else {
                                binding.appBarMain.dashboardScreen.savedCropNameCardView.visibility =
                                    View.GONE
                                binding.appBarMain.dashboardScreen.yourCropTv.visibility =
                                    View.VISIBLE
                                binding.appBarMain.dashboardScreen.yourCropTv.setText(R.string.no_crops_added)
                                binding.appBarMain.dashboardScreen.addCropCardView.visibility =
                                    View.VISIBLE
                                binding.appBarMain.dashboardScreen.addChangeCropTV.setText(R.string.add_Crop)
                                binding.appBarMain.dashboardScreen.addChangeCropIV.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this, R.drawable.baseline_add_24
                                    )
                                )
                            }
                            if (selectedCropList != null) {
                                showCropList(selectedCropList!!)
                            } else {
                                binding.appBarMain.dashboardScreen.selectedCropRecyclerView.visibility =
                                    View.GONE
                            }
                        } else {
                            UIToastMessage.show(this, farmersSelectedCropResponse.getResponse())
                        }
                        updateSavedCropDetails()
                    } catch (e: Exception) {
                        Log.d("TAGGER", "onResponse: $e")
                    }
                }

                3 -> {
                    try {
                        val deleteSelectedCropResponse = ResponseModel(jSONObject)
                        if (deleteSelectedCropResponse.getStatus()) {
                            if (showToast) {
                                UIToastMessage.show(this, deleteSelectedCropResponse.getResponse())
                            }
                            AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null)
                            selectedCropList?.clear()
                            getFarmerSelectedCrop(languageToLoad)
                        } else {
                            UIToastMessage.show(this, deleteSelectedCropResponse.getResponse())
                        }
                    } catch (e: Exception) {
                        Log.d("TAGGER", "onResponse: $e")
                    }
                }

                4 -> {
                    if (jSONObject != null) {
                        try {
                            val talukaID: Int = AppSettings.getInstance()
                                .getIntValue(this, AppConstants.uTALUKAID, 0)
                            val talukaArray = jSONObject.optJSONArray("data")
                            for (i in 0 until talukaArray!!.length()) {
                                val talukaIDJson = talukaArray.getJSONObject(i)
                                if (talukaID == talukaIDJson.optInt("code")) {
                                    binding.appBarMain.dashboardScreen.weatherTalukaTV.text =
                                        talukaIDJson.optString("name")
                                }
                            }
                            callForWeatherApi(villageCode)
                        } catch (e: Exception) {
                            Log.d("TAGGER", "onResponse: $e")
                        }
                    }
                }

                5 -> if (jSONObject != null) {
                    try {
                        appPreferenceManager.saveString(
                            AppConstants.WEATHER_RESPONSE,
                            jSONObject.toString()
                        )
                        val response = ResponseModel(jSONObject)
                        if (response.getStatus()) {
                            val temperatureObject =
                                checkNotNull(jSONObject.optJSONObject("Temperature"))
                            val tempMin = temperatureObject.optString("min")
                            val tempMax = temperatureObject.optString("max")
                            val temperature = "$tempMin°C / $tempMax°C"
                            binding.appBarMain.dashboardScreen.temperatureTextView.text =
                                temperature
                        }
                    } catch (e: Exception) {
                        Log.d("TAGGER", "onResponse: $e")
                    }
                }
            }
        }
    }

    private fun callForWeatherApi(talukaID: Int) {
        val jsonObject = JSONObject()
        Executors.newSingleThreadExecutor().execute {
            try {
                jsonObject.put("taluka_code", talukaID)
                jsonObject.put("lang", languageToLoad)

                val requestBody: RequestBody =
                    AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    this,
                    APIServices.FARMER,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    false
                )

                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest: APIRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> =
                    apiRequest.getWeatherDetails(requestBody)

                api.postRequest(responseCall, this, 5)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateSavedCropDetails() {
        val newCropID = intent.getIntExtra("helloCrop", 0)
        if (selectedCropList != null) {
            if (selectedCropList!!.size > 1) {
                for (i in selectedCropList!!.indices) {
                    if (newCropID != 0) {
                        if (selectedCropList!![i].id != newCropID) {
                            cropId = selectedCropList!![i].id
                            showToast = false
                            deleteFarmerSelectedCrop()
                        }
                    }
                    Log.d(
                        "TAGGER",
                        "updateSavedCropDetails: saved ${selectedCropList!![i].id} and received $newCropID"
                    )
                }
            }
        }
    }

    private fun showCropList(selectedCropList: ArrayList<CropsCategName>) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.layoutManager = layoutManager
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.setHasFixedSize(true)
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.itemAnimator =
            DefaultItemAnimator()
        val adapter = VideosImageDetailsAdapter(
            this, selectedCropList,
            this, "dashboardScreen"
        )
        binding.appBarMain.dashboardScreen.selectedCropRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun deleteDialog() {
        val dialog: Dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.delete_crop_title)
            .setMessage(R.string.delete_crop_message)
            .setPositiveButton(
                R.string.delete_crop_yes
            ) { _: DialogInterface?, _: Int -> deleteFarmerSelectedCrop() }
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
                    val intent = Intent(this@DashboardScreen, Registration::class.java)
                    intent.putExtra("FAAPRegistrationID", farmerId)
                    startActivity(intent)
                }

                1 -> if (farmerId > 0) {
                    val sharing = Intent(
                        this@DashboardScreen,
                        AboutActivity::class.java
                    )
                    startActivity(sharing)
                } else {
                    UIToastMessage.show(this@DashboardScreen, "Please Login First...")
                }

                2 -> {
                    val dbtStatusIntent = Intent(
                        this@DashboardScreen,
                        DbtStatus::class.java
                    )
                    dbtStatusIntent.putExtra("userPhoneNumber", navUserPhone.toString())
                    startActivity(dbtStatusIntent)
                }

                3 -> {
                    val notificationIntent = Intent(
                        this@DashboardScreen,
                        NewsListActivity::class.java
                    )
                    startActivity(notificationIntent)
                }

                5 -> {
                    val trainingLocationIntent = Intent(
                        this@DashboardScreen,
                        TrainingLocationSelection::class.java
                    )
                    startActivity(trainingLocationIntent)
                }

                6 -> {
                    val loginIntent = Intent(
                        this@DashboardScreen,
                        LoginScreen::class.java
                    )
                    startActivity(loginIntent)
                }

                7 -> logoutFromApp()
                11 -> startActivity(Intent(this@DashboardScreen, MyVillageProfilePdf::class.java))
                13 -> if (farmerId > 0) {
                    val grievanceIntent = Intent(
                        this@DashboardScreen,
                        Grievances::class.java
                    )
                    grievanceIntent.putExtra("FAAPRegistrationID", farmerId)
                    startActivity(grievanceIntent)
                } else {
                    UIToastMessage.show(this@DashboardScreen, "Please Login First...")
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
        AppSettings.getInstance().setValue(this, AppConstants.uName, AppConstants.uName)
        AppSettings.getInstance().setValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
        AppSettings.getInstance().setValue(this, AppConstants.uEmail, AppConstants.uEmail)
        AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, 0)
        AppSettings.getInstance().setValue(this, AppConstants.uDIST, AppConstants.uDIST)
        AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, 0)
        AppSettings.getInstance().setValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
        AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, 0)
        AppSettings.getInstance().setValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)
        AppSettings.getInstance().setIntValue(this, AppConstants.uVILLAGEID, 0)
        AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null)
        AppUtility.getInstance().clearAppSharedPrefData(this, AppConstants.kSHARED_PREF)
        AppSettings.getInstance().setBooleanValue(this, AppConstants.userDataSaved, false)
        val intent = Intent(this@DashboardScreen, SplashScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onUpdateNeeded(updateUrl: String) {
        val dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("New version available")
            .setMessage("Please, update app to new version to continue reposting.")
            .setPositiveButton(
                "Update"
            ) { _: DialogInterface?, _: Int -> redirectStore(updateUrl) }
            .setNegativeButton(
                "No, thanks"
            ) { dialog: DialogInterface?, _: Int ->
                dialog?.dismiss()
                finish()
            }.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun redirectStore(updateUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onMultiRecyclerViewItemClick(i: Int, id: Any) {
        if (i == 2) {
            cropId = (id as Int)
            deleteFarmerSelectedCrop()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
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
            "Warehouse Availabilities",
            "DBT Schemes"
        )

        private val arrayCategoryMarathi = arrayOf(
            "पीक सल्ला",
            "एस.ओ.पी.(SOP)",
            "मृदा आरोग्य पत्रिका",
            "खत मात्रा गणक (कॅलक्यूलेटर)",
            "बदलत्या हवामानास अनुकूल शेती पद्धती",
            "कीड व रोग",
            "बाजारभाव",
            "गोदाम उपलब्धता",
            "थेट लाभ हस्तांतरण योजना"
        )

        var arrayCategoryImg: IntArray = intArrayOf(
            R.drawable.ecology,
            R.drawable.ic_sop,
            R.drawable.soil,
            R.drawable.fertilizer,
            R.drawable.climate_change,
            R.drawable.ladybug,
            R.drawable.commodity,
            R.drawable.warehouse,
            R.drawable.ic_dbt
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
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.microsoft.clarity.Clarity
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.helpers.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.PocraRole
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNewDashboardMainBinding
import `in`.gov.mahapocra.mahavistaarai.sma.ui.screens.KTDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DrawerMenuAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.ProfileScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala.ShetishalaActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.AboutActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.CreditsActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.CostCalculatorDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts.ExpertsCornerFarmerActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard.LeaderboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news.NewsListActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.notification.NotificationActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.ConfirmationDialog
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.TokenSessionManager
import `in`.gov.mahapocra.mahavistaarai.util.app_util.SideNavMenuHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper.shrinkToCenter
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FirebaseTopicHelper.subscribeToTopic
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FirebaseTopicHelper.unSubscribeToTopic
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NewDashboardMainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityNewDashboardMainBinding
    private lateinit var appPreferenceManager: AppPreferenceManager
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var doubleBackToExitPressedOnce = false
    private var languageToLoad: String = "en"
    private lateinit var navUserName: TextView
    private lateinit var navUserPhone: TextView
    private var jsonArray: JSONArray? = null
    private var topicsArray = JSONArray()
    private var isPromoFetched = false

    private var savedCropId = 0
    private var savedCropName = ""
    private var savedCropSowingDate: String? = null
    private var savedCropWoTRId: String? = null
    private var savedCropImageUrl: String? = null
    private var selectedCropList: ArrayList<CropsCategName>? = null
    private var farmerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@NewDashboardMainActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityNewDashboardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.mainLayout)
        uiResponsive(binding.navView, false)
        askForPermissions()
        init()
        FirebaseHelper(this)
    }

    private fun init() {

        appPreferenceManager = AppPreferenceManager(this)
        val hView = binding.navView.getHeaderView(0)
        navUserName = hView.findViewById(R.id.tv_farmerName)
        navUserPhone = hView.findViewById(R.id.tv_famerPhoneNumber)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (NetworkUtils.isInternetAvailable(this)) {
            authViewModel.fetchUserInformation()
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }


        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        setUpDrawerMenu()
        bubbleAnimationChatbot()
        setVersion()
        observeResponse()
        val name = AppPreferenceManager(this).getString(AppConstants.USER_NAME, "")
        binding.nameTextView.text = buildString {
            append("${getString(R.string.hello)} ")
            append(CryptoHelper.decryptField(name)?.split(" ")[0] ?: "")
        }
        farmerViewModel.getNotificationList(farmerId)
        val toggle = ActionBarDrawerToggle(
            this@NewDashboardMainActivity,
            drawerLayout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = adapter
        val titles = listOf(
            getString(R.string.my_dashboard),
            getString(R.string.agri_services),
            getString(R.string.smart_farming)
        )
        val icons = listOf(
            R.drawable.ic_dashboard_md,
            R.drawable.ic_agri_services_md,
            R.drawable.ic_smart_farming_md
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                appPreferenceManager.saveInt(
                    AppConstants.REDIRECT_TO_TAB,
                    tab.position
                )

                val view = tab.customView!!
                view.isSelected = true

                val text = view.findViewById<TextView>(R.id.tabText)
                text.setTextColor(getColor(R.color.off_black))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val view = tab.customView!!
                view.isSelected = false

                val text = view.findViewById<TextView>(R.id.tabText)
                text.setTextColor(getColor(R.color.off_white))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            val view = LayoutInflater.from(this)
                .inflate(R.layout.tab_item, null)

            val icon = view.findViewById<ImageView>(R.id.tabIcon)
            val text = view.findViewById<TextView>(R.id.tabText)

            icon.setImageResource(icons[position])
            text.text = titles[position]

            tab.customView = view

        }.attach()


        val selectedTabFromIntent = intent.getIntExtra("selected_tab", -1)

        if (selectedTabFromIntent != -1) {

            // TEMPORARY NAVIGATION CASE
            binding.viewPager.setCurrentItem(selectedTabFromIntent, false)

        } else {

            // NORMAL APP OPEN CASE
            val savedTab = appPreferenceManager.getInt(
                AppConstants.REDIRECT_TO_TAB,
                0
            )

            binding.viewPager.setCurrentItem(savedTab, false)
        }

        binding.toolbar.inflateMenu(R.menu.toolbar_menu)

        binding.chatbotIcon.setOnClickListener {
            Clarity.sendCustomEvent("VISTAAR_AI_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(Intent(this, ChatbotActivity::class.java))
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.customNavBottom.navChc.setOnClickListener {
            Clarity.sendCustomEvent("CHC_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(
                    Intent(
                        this@NewDashboardMainActivity,
                        CHCenterActivity::class.java
                    )
                )
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.customNavBottom.navVideos.setOnClickListener {
            Clarity.sendCustomEvent("VIDEOS_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(
                    Intent(
                        this@NewDashboardMainActivity,
                        VideosActivity::class.java
                    )
                )
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.customNavBottom.navShetishala.setOnClickListener {
            Clarity.sendCustomEvent("DBT_BUTTON_CLICKED")
            if (NetworkUtils.isInternetAvailable(this)) {
                startActivity(
                    Intent(
                        this@NewDashboardMainActivity,
                        ShetishalaActivity::class.java
                    )
                )
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        binding.toolbar.menu.get(0).icon
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_language -> {
                    openChangeLangPopup()
                    true
                }

                R.id.action_notification -> {
                    startActivity(
                        Intent(
                            this@NewDashboardMainActivity,
                            NotificationActivity::class.java
                        )
                    )
                    true
                }

                R.id.action_call -> {
                    startActivity(Intent(Intent.ACTION_DIAL).apply {
                        data = "tel:155313".toUri()
                    })
                    true
                }

                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    if (doubleBackToExitPressedOnce) {
                        finishAffinity()
                        return
                    }

                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@NewDashboardMainActivity,
                        "Swipe again to exit",
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        )

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    appPreferenceManager.saveInt(
                        AppConstants.REDIRECT_TO_TAB,
                        position
                    )
                }
            }
        )

        binding.krishiTaiButton.setOnClickListener {

            val rolesJson = AppSettings.getInstance()
                .getValue(this, AppConstants.pocraRoles, "[]")

            val pocraRoles = mutableListOf<PocraRole>()

            try {
                val arr = JSONArray(rolesJson)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    pocraRoles.add(
                        PocraRole(
                            obj.getInt("role_id"),
                            obj.getString("username"),
                            obj.getString("role"),
                            obj.getString("short_name")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Check only Krishi Tai roles
            val ktRoles = pocraRoles.filter { it.role_id == 45 }

            if (ktRoles.isEmpty()) {
                Toast.makeText(this, "You are not authorized for SMA module", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // If only one username → direct login
            if (ktRoles.size == 1) {
                val userName = ktRoles[0].username
                AppSettings.getInstance().setValue(this, AppConstants.smaUsername, userName)
                val intent = Intent(this, KTDashboardActivity::class.java)
                intent.putExtra("selected_username", userName)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            // If multiple usernames → show dialog
            else {
                showRoleSelectionDialog(ktRoles)
            }
        }

        farmerViewModel.getFarmerSelectedCrop(languageToLoad)
    }

    private fun showRoleSelectionDialog(roles: List<PocraRole>) {
        val usernames = roles.map { it.username }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Username")
            .setItems(usernames) { dialog, which ->
                val selectedUser = usernames[which]
                AppSettings.getInstance().setValue(this, AppConstants.smaUsername, selectedUser)
                val intent = Intent(this, KTDashboardActivity::class.java)
                intent.putExtra("selected_username", selectedUser)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
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
            .setValue(this@NewDashboardMainActivity, AppConstants.kAPP_BUILD_VERSION, versionName)
    }

    private fun openChangeLangPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_language_selector)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvEnglish = dialog.findViewById<TextView>(R.id.tv_eng)
        val tvMarathi = dialog.findViewById<TextView>(R.id.tv_mar)
        val ivEnglish = dialog.findViewById<ImageView>(R.id.check_eng)
        val ivMarathi = dialog.findViewById<ImageView>(R.id.check_mar)

        if (languageToLoad == "en") {
            ivEnglish.visibility = View.VISIBLE
            ivMarathi.visibility = View.GONE
        } else {
            ivEnglish.visibility = View.GONE
            ivMarathi.visibility = View.VISIBLE
        }

        tvEnglish.setOnClickListener {
            val languageToLoad = "en"
            configureLocale(baseContext, languageToLoad)
            AppSettings.setLanguage(this@NewDashboardMainActivity, "1")

            finish()
            startActivity(intent)

            dialog.dismiss()
            farmerViewModel.getFarmerSelectedCrop(languageToLoad)
        }

        tvMarathi.setOnClickListener {
            val languageToLoad = "mr"
            configureLocale(baseContext, languageToLoad)
            AppSettings.setLanguage(this@NewDashboardMainActivity, "2")

            finish()
            startActivity(intent)

            dialog.dismiss()
            farmerViewModel.getFarmerSelectedCrop(languageToLoad)
        }

        dialog.show()

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_language -> {
                // open language selector
                true
            }

            R.id.action_notification -> {
                // open notifications screen
                true
            }

            R.id.action_call -> {
                // initiate call
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, l: Long) {
        try {
            val jsonObject = jsonArray!!.getJSONObject(position)
            val id = jsonObject.getInt("id")
            when (id) {
                0 -> {
                    val intent = Intent(this@NewDashboardMainActivity, ProfileScreen::class.java)
                    intent.putExtra("FAAPRegistrationID", farmerId)
                    startActivity(intent)
                }

                1 ->
                    startActivity(
                        Intent(
                            this@NewDashboardMainActivity,
                            AboutActivity::class.java
                        )
                    )

                2 -> {
                    val notificationIntent = Intent(
                        this@NewDashboardMainActivity,
                        ExpertsCornerFarmerActivity::class.java
                    )
                    startActivity(notificationIntent)
                }

                3 -> {
                    val notificationIntent = Intent(
                        this@NewDashboardMainActivity,
                        CreditsActivity::class.java
                    )
                    startActivity(notificationIntent)
                }

                4 -> {
                    val notificationIntent = Intent(
                        this@NewDashboardMainActivity,
                        NewsListActivity::class.java
                    )
                    startActivity(notificationIntent)
                }

                5 -> {
                    val costCalculatorIntent = Intent(
                        this@NewDashboardMainActivity,
                        CostCalculatorDashboardActivity::class.java
                    )
                    startActivity(costCalculatorIntent)
                }

                6 -> {
                    startActivity(
                        Intent(
                            this@NewDashboardMainActivity,
                            LoginScreen::class.java
                        ).apply {
                            putExtra("from", "dashboard")
                        }
                    )
                }

                7 -> {
                    ConfirmationDialog(
                        context = this,
                        title = getString(R.string.logout_title),
                        message = getString(R.string.logout_alert_mr),
                        positiveText = getString(R.string.yes),
                        negativeText = getString(R.string.no),
                        onPositiveClick = {
                            logoutFromApp()
                        }
                    ).show()
                }

                8 -> {
                    startActivity(
                        Intent(
                            this@NewDashboardMainActivity,
                            LeaderboardActivity::class.java
                        )
                    )
                }
            }
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun logoutFromApp() {

        if (!NetworkUtils.isInternetAvailable(this)) {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
            return
        }

        ProgressHelper.showProgressDialog(this)

        if (topicsArray.length() == 0) {
            completeLogout()
            return
        }

        val topicList = mutableListOf<String>()
        var completedCount = 0
        val totalTopics = topicsArray.length()

        for (i in 0 until totalTopics) {

            val topic = topicsArray.optString(i)

            unSubscribeToTopic(topic) { unsubscribed ->
                completedCount++

                if (unsubscribed) {
                    topicList.add(topic)
                }

                if (completedCount == totalTopics) {
                    farmerViewModel.deleteSubscribedTopics(topicList)
                    completeLogout()
                }
            }
        }
    }

    private fun completeLogout() {
        ProgressHelper.disableProgressDialog()
        farmerViewModel.updateFCMToken(farmerId, "NA")
        appPreferenceManager.clearAll()
    }

    private fun observeResponse() {

        farmerViewModel.getAppVersionResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    val appHelper = AppHelper(this@NewDashboardMainActivity)
                    val jsonResponse = JSONObject(state.data.toString())
                    val remoteAppVersion = jsonResponse.optInt("version_code")
                    val currentAppVersion = appHelper.getCurrentAppVersion()
                    if (remoteAppVersion > currentAppVersion) {
                        appHelper.showUpdateDialog()
                    }
                }

                is UiState.Error -> {}
            }
        }

        authViewModel.userDetailsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonResponse = JSONObject(state.data.toString())
                    val dataObject = jsonResponse.optJSONObject("data")
                    val name = dataObject?.optString("Name")
                    val mobile = dataObject?.optString("MobileNo")
                    val agristackId = dataObject?.optString("farmer_id")
                    val villageCode = dataObject?.optString("VillageCode")
                    val villageName = dataObject?.optString("VillageName")
                    val talukaCode = dataObject?.optString("TalukaCode")
                    val talukaName = dataObject?.optString("TalukaName")
                    val districtCode = dataObject?.optString("DistrictCode")
                    val districtName = dataObject?.optString("DistrictName")
                    val farmerRegId = dataObject?.optString("FAAPRegistrationID")
                    val rolesArray = dataObject?.optJSONArray("pocra_roles")
                    val pocraRoles = mutableListOf<PocraRole>()
                    val topicJsonArray = dataObject?.optJSONArray("topics") ?: JSONArray()
                    val topicsToSubArray =
                        dataObject?.optJSONArray("topics_to_subscribe") ?: JSONArray()
                    val topicsToDeleteArray =
                        dataObject?.optJSONArray("topics_to_delete") ?: JSONArray()
                    AppPreferenceManager(this).saveString(AppConstants.USER_NAME, name)
                    AppPreferenceManager(this).saveString(AppConstants.USER_MOBILE, mobile)
                    AppPreferenceManager(this).saveString(AppConstants.VILLAGE_CODE, villageCode)
                    AppPreferenceManager(this).saveString(AppConstants.VILLAGE_NAME, villageName)
                    AppPreferenceManager(this).saveString(AppConstants.TALUKA_CODE, talukaCode)
                    AppPreferenceManager(this).saveString(AppConstants.TALUKA_NAME, talukaName)
                    AppPreferenceManager(this).saveString(AppConstants.DISTRICT_CODE, districtCode)
                    AppPreferenceManager(this).saveString(AppConstants.DISTRICT_NAME, districtName)
                    AppPreferenceManager(this).saveString(AppConstants.AGRISTACKID, agristackId)
                    appPreferenceManager.saveString("FARMER_POPUP_ID", agristackId)
                    AppPreferenceManager(this).saveString(AppConstants.FARMER_REG_ID, farmerRegId)

                    val userRoleId = -1
                    var hasKrishiTaiRole = false   // FLAG
                    if (rolesArray != null && rolesArray.length() > 0) {

                        // roles exist → parse them
                        for (i in 0 until rolesArray.length()) {
                            val roleObj = rolesArray.optJSONObject(i) ?: continue
                            val roleId = roleObj.optInt("role_id", -1)
                            val username = roleObj.optString("username", "")
                            val role = roleObj.optString("role", "")
                            val shortName = roleObj.optString("short_name", "")
                            pocraRoles.add(PocraRole(roleId, username, role, shortName))
                            // ✅ CHECK ROLE 45
                            if (roleId == 45) {
                                hasKrishiTaiRole = true
                            }
                        }
                    }
                    binding.krishiTaiButton.visibility =
                        if (hasKrishiTaiRole) View.VISIBLE else View.GONE
                    val rolesJsonString = convertRolesToJson(pocraRoles)
                    AppSettings.getInstance().setValue(
                        this@NewDashboardMainActivity,
                        AppConstants.pocraRoles,
                        rolesJsonString
                    )
                    AppSettings.getInstance()
                        .setIntValue(this@NewDashboardMainActivity, AppConstants.uRole, userRoleId)


                    navUserName.text = CryptoHelper.decryptField(name)?.split(" ")[0] ?: ""
                    navUserPhone.text = CryptoHelper.decryptField(mobile)
                    binding.nameTextView.text = buildString {
                        append("${getString(R.string.hello)} ")
                        append(CryptoHelper.decryptField(name)?.split(" ")[0] ?: "")
                    }
                    farmerViewModel.getCropSapAdvisory(
                        CryptoHelper.decryptField(villageCode).toString().toInt()
                    )
                    topicsOperations(topicJsonArray, topicsToSubArray, topicsToDeleteArray)
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.updateFCMTokenResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    AppPreferenceManager(this).saveBoolean("FCM_VALIDATED", true)
                    val jsonObject = JSONObject(state.data.toString())
                    val response = jsonObject.optString("response")
                    if (response == "FCM Cleared") {
                        AppPreferenceManager(this).clearAll()
                        AppSettings.getInstance()
                            .setValue(this, AppConstants.uName, AppConstants.uName)
                        AppSettings.getInstance()
                            .setValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
                        AppSettings.getInstance()
                            .setValue(this, AppConstants.uEmail, AppConstants.uEmail)
                        AppSettings.getInstance()
                            .setIntValue(this, AppConstants.fREGISTER_ID, 0)
                        AppSettings.getInstance()
                            .setValue(this, AppConstants.uDIST, AppConstants.uDIST)
                        AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, 0)
                        AppSettings.getInstance()
                            .setValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
                        AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, 0)
                        AppSettings.getInstance()
                            .setValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)
                        AppSettings.getInstance().setIntValue(this, AppConstants.uVILLAGEID, 0)
                        AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null)
                        AppUtility.getInstance()
                            .clearAppSharedPrefData(this, AppConstants.kSHARED_PREF)
                        AppSettings.getInstance()
                            .setBooleanValue(this, AppConstants.userDataSaved, false)
                        TokenSessionManager.saveTokens("", "")
                        TokenSessionManager.clear()
                        val intent = Intent(
                            this@NewDashboardMainActivity,
                            LoginScreen::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.getCropSapAdvisoryResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val jsonArray = jsonObject.optJSONArray("advisory")
                    if (jsonArray?.length() != 0) {
                        appPreferenceManager.saveString(
                            AppConstants.ETL_ADVISORY_ARRAY,
                            jsonArray?.toString()
                        )
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.getNotificationResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val notificationJsonArray = jsonObject.optJSONArray("notifications")
                    var unreadCount = 0
                    if (notificationJsonArray != null) {
                        for (i in 0 until notificationJsonArray.length()) {
                            val notification = notificationJsonArray.getJSONObject(i)
                            if (notification.optInt("is_read", 1) == 0) {
                                unreadCount++
                            }
                        }
                    }
                    updateNotificationCount(unreadCount)
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.getFarmerSelectedCrop.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()

                    val jsonObject = JSONObject(state.data.toString())
                    val selectedCrops = jsonObject.optJSONArray("Data")

                    if (selectedCrops != null && selectedCrops.length() > 0) {
                        selectedCropList = ArrayList()
                        for (i in 0 until selectedCrops.length()) {
                            val selectedCrop = selectedCrops.getJSONObject(i)
                            savedCropId = selectedCrop.getInt("crop_id")
                            savedCropName = selectedCrop.getString("name")
                            savedCropImageUrl = selectedCrop.getString("image")
                            savedCropSowingDate = selectedCrop.getString("sowing_date")
                            savedCropWoTRId = selectedCrop.getString("wotr_crop_id")
                        }
                        appPreferenceManager.saveInt("CROP_ID_SAVED", savedCropId)
                        appPreferenceManager.saveString("CROP_NAME_SAVED", savedCropName)
                        appPreferenceManager.saveString("CROP_IMAGE_SAVED", savedCropImageUrl)
                        appPreferenceManager.saveString(
                            "CROP_SOWING_DATE_SAVED",
                            savedCropSowingDate
                        )
                        appPreferenceManager.saveString("CROP_WOTR_ID_SAVED", savedCropWoTRId)
                        selectedCropList?.add(
                            CropsCategName(
                                savedCropId,
                                savedCropName,
                                savedCropImageUrl,
                                savedCropWoTRId
                            )
                        )
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateNotificationCount(unreadNotificationsCount: Int) {

        val menuItem = binding.toolbar.menu.findItem(R.id.action_notification)

        if (unreadNotificationsCount > 0) {
            // Change icon when unread notifications exist
            menuItem.setIcon(R.drawable.ic_notification_active)
        } else {
            // Default notification icon
            menuItem.setIcon(R.drawable.ic_notification)

        }
    }

    private fun topicsOperations(
        currentTopics: JSONArray,
        topicsToSubArray: JSONArray,
        topicsToDeleteArray: JSONArray
    ) {

        lifecycleScope.launch {

            val finalTopics = mutableSetOf<String>()

            // Existing topics
            for (i in 0 until currentTopics.length()) {
                finalTopics.add(currentTopics.optString(i))
            }

            // =========================
            // SUBSCRIBE NEW TOPICS
            // =========================

            for (i in 0 until topicsToSubArray.length()) {

                val topic = topicsToSubArray.optString(i)

                kotlinx.coroutines.suspendCancellableCoroutine<Unit> { cont ->

                    subscribeToTopic(topic) { subscribed ->

                        if (subscribed) {

                            finalTopics.add(topic)

                            farmerViewModel.saveSubscribedTopic(
                                topic
                            )
                        }

                        cont.resume(Unit) {}
                    }
                }
            }

            // =========================
            // DELETE TOPICS
            // =========================

            val deletedTopics = mutableListOf<String>()

            for (i in 0 until topicsToDeleteArray.length()) {

                val topic = topicsToDeleteArray.optString(i)

                kotlinx.coroutines.suspendCancellableCoroutine<Unit> { cont ->

                    unSubscribeToTopic(topic) { unsubscribed ->

                        if (unsubscribed) {

                            finalTopics.remove(topic)

                            deletedTopics.add(topic)
                        }

                        cont.resume(Unit) {}
                    }
                }
            }

            // =========================
            // DELETE FROM SERVER
            // =========================

            if (deletedTopics.isNotEmpty()) {

                farmerViewModel.deleteSubscribedTopics(
                    topics = deletedTopics
                )
            }

            // =========================
            // SAVE FINAL TOPICS
            // =========================

            val finalJsonArray = JSONArray()

            finalTopics.forEach {
                finalJsonArray.put(it)
            }

            topicsArray = finalJsonArray

            appPreferenceManager.saveString(
                "topic_saved_fcm",
                finalJsonArray.toString()
            )
        }
    }

    private fun bubbleAnimationChatbot() {
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.chatBubbleImageView.animate()
                .alpha(0f)
                .setDuration(500) // animation duration in ms
                .withEndAction {
                    binding.chatBubbleImageView.visibility = View.GONE
                    binding.chatBubbleImageView.alpha =
                        1f // reset alpha in case you show it again
                }
                .start()
        }
        shrinkToCenter(binding.chatBubbleImageView)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onResume() {
        super.onResume()
        farmerViewModel.getNotificationList(farmerId)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for ((index, _) in permissions.withIndex()) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    UIToastMessage.show(
                        this@NewDashboardMainActivity,
                        "Access Permission Granted"
                    )
                    // Perform the related action (e.g., accessing the camera) if needed
                } else {
                    UIToastMessage.show(
                        this@NewDashboardMainActivity,
                        "Access Permission Denied"
                    )
                    // Optionally handle specific denied permission cases here
                }
            }
        }
    }

    fun convertRolesToJson(roles: List<PocraRole>): String {
        val jsonArray = JSONArray()
        for (role in roles) {
            val obj = JSONObject()
            obj.put("role_id", role.role_id)
            obj.put("username", role.username)
            obj.put("role", role.role)
            obj.put("short_name", role.short_name)
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tab = intent.getIntExtra("selected_tab", 0) ?: 0
        binding.viewPager.currentItem = tab
    }

}
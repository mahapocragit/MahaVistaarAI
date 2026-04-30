package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.mhvui.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNewDashboardMainBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.DrawerMenuAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.ProfileScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.AboutActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.CreditsActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.CostCalculatorDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts.ExpertsCornerFarmerActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard.LeaderboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news.NewsListActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.notification.NotificationActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.splash.SplashScreenActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.app_util.SideNavMenuHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FirebaseTopicHelper.unSubscribeToTopic
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
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
    private var farmerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@NewDashboardMainActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        binding = ActivityNewDashboardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            view.updateLayoutParams<ViewGroup.LayoutParams> {
                height = resources.getDimensionPixelSize(
                    R.dimen.m3_appbar_size_compact
                ) + topInset
            }

            view.setPadding(
                view.paddingLeft,
                topInset,
                view.paddingRight,
                view.paddingBottom
            )

            insets
        }

        init()
    }

    private fun init() {

        appPreferenceManager = AppPreferenceManager(this)
        val hView = binding.navView.getHeaderView(0)
        navUserName = hView.findViewById(R.id.tv_farmerName)
        navUserPhone = hView.findViewById(R.id.tv_famerPhoneNumber)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        val accessToken = AppPreferenceManager(this).getString(AppConstants.ACCESS_TOKEN) ?: ""
        Log.d(TAG, "init: $accessToken")
        if (accessToken.isNotEmpty()) {
            if (NetworkUtils.isInternetAvailable(this)) {
                authViewModel.fetchUserInformation(accessToken)
            } else {
                LocalCustom.createSnackbar(binding.root, "Internet not available!")
            }
        }

        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        setUpDrawerMenu()
        setVersion()
        observeResponse()

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
        binding.viewPager.adapter = adapter
        val titles = listOf("My Dashboard", "Agri Services", "Smart Farming")
        val icons = listOf(
            R.drawable.ic_dashboard_md,
            R.drawable.ic_agri_services_md,
            R.drawable.ic_smart_farming_md
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
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

        binding.toolbar.inflateMenu(R.menu.toolbar_menu)

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
            farmerViewModel.getFarmerSelectedCrop(farmerId, languageToLoad)
        }

        tvMarathi.setOnClickListener {
            val languageToLoad = "mr"
            configureLocale(baseContext, languageToLoad)
            AppSettings.setLanguage(this@NewDashboardMainActivity, "2")

            finish()
            startActivity(intent)

            dialog.dismiss()
            farmerViewModel.getFarmerSelectedCrop(farmerId, languageToLoad)
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

                1 -> if (farmerId > 0) {
                    startActivity(
                        Intent(
                            this@NewDashboardMainActivity,
                            AboutActivity::class.java
                        )
                    )
                } else {
                    UIToastMessage.show(this@NewDashboardMainActivity, "Please Login First...")
                }

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

                7 -> logoutFromApp()

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

        if (topicsArray == null || topicsArray.length() == 0) {
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
                    farmerViewModel.deleteSubscribedTopics(farmerId, topicList)
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

        authViewModel.userDetailsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonResponse = JSONObject(state.data.toString())
                    val dataObject = jsonResponse.optJSONObject("data")
                    val name = CryptoHelper.decryptField(dataObject?.optString("Name"))
                    val mobile = CryptoHelper.decryptField(dataObject?.optString("MobileNo"))
                    val firstName = name?.split(" ")[0] ?: ""
                    navUserName.text = name
                    navUserPhone.text = mobile
                    binding.nameTextView.text = "Hello, $firstName"
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
                        val intent = Intent(
                            this@NewDashboardMainActivity,
                            SplashScreenActivity::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d(TAG, "logoutFromApp: $response")
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
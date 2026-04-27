package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mhvui.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
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
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.app_util.SideNavMenuHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FirebaseTopicHelper.unSubscribeToTopic
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONException
import kotlin.getValue

class NewDashboardMainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityNewDashboardMainBinding
    private lateinit var appPreferenceManager: AppPreferenceManager
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var languageToLoad: String = "en"
    private lateinit var navUserName: TextView
    private lateinit var navUserPhone: TextView
    private var jsonArray: JSONArray? = null
    private var topicsArray = JSONArray()
    private var farmerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        val accessToken = AppPreferenceManager(this).getString(AppConstants.ACCESS_TOKEN)?:""
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
                text.setTextColor(Color.BLACK)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val view = tab.customView!!
                view.isSelected = false

                val text = view.findViewById<TextView>(R.id.tabText)
                text.setTextColor(Color.WHITE)
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
                R.id.action_language -> { true }
                R.id.action_notification -> { true }
                R.id.action_call -> { true }
                else -> false
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
            .setValue(this@NewDashboardMainActivity, AppConstants.kAPP_BUILD_VERSION, versionName)
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
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout1)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
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
}
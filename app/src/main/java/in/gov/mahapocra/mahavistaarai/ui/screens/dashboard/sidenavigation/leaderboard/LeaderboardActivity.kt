package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityLeaderboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    var languageToLoad = "mr"
    private var selectedValue = "taluka"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@LeaderboardActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        LocalCustom.switchLanguage(this, languageToLoad)

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalCustom.uiResponsive(binding.root)

        observeViewModel()
        setUpViews()
    }

    private fun observeViewModel() {
        leaderboardViewModel.getLeaderboardDataResponse.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val dataObject = jsonObject.getJSONObject("data")
                //First Rank Holder
                val firstRankObject = dataObject.getJSONObject("first")
                val firstRankName = firstRankObject.optString("user_name")
                val firstRankTaluka = firstRankObject.optString("taluka")
                binding.firstRankNameTextView.text = formatName(firstRankName)
                binding.firstRankTalukaTextView.text = formatName(firstRankTaluka)
                //Second Rank Holder
                val secondRankObject = dataObject.getJSONObject("second")
                val secondRankName = secondRankObject.optString("user_name")
                val secondRankTaluka = firstRankObject.optString("taluka")
                binding.secondRankNameTextView.text = formatName(secondRankName)
                binding.secondRankTalukaTextView.text = formatName(secondRankTaluka)
                //Third Rank Holder
                val thirdRankObject = dataObject.getJSONObject("third")
                val thirdRankName = thirdRankObject.optString("user_name")
                val thirdRankTaluka = firstRankObject.optString("taluka")
                binding.thirdRankNameTextView.text = formatName(thirdRankName)
                binding.thirdRankTalukaTextView.text = formatName(thirdRankTaluka)
                //Toppers List
                val toppersList = dataObject.optJSONArray("list")
                Log.d("TAGGER", "observeViewModel: $toppersList")
                binding.leaderboardRecyclerView.adapter =
                    toppersList?.let { LeaderboardAdapter(toppersList, selectedValue) }
            }
        }
    }

    fun formatName(str:String):String{
        val stringArr = str.split(" ")
        return stringArr[0]
    }

    private fun setUpViews() {
        binding.leaderboardRecyclerView.hasFixedSize()
        binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)

        highlightUi(taluka = 1)
        binding.talukaTextView.setOnClickListener { highlightUi(taluka = 1) }
        binding.districtTextView.setOnClickListener { highlightUi(district = 1) }
        binding.stateTextView.setOnClickListener { highlightUi(state = 1) }

        binding.toolbarLayout.textViewHeaderTitle.text =
            getString(R.string.leaderboard)
        binding.toolbarLayout.imgBackArrow.visibility = View.VISIBLE
        binding.toolbarLayout.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@LeaderboardActivity, DashboardScreen::class.java))
            }
        })
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext =
            LocalCustom.configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }

    fun highlightUi(taluka: Int = 0, district: Int = 0, state: Int = 0) {
        val normalColor = ContextCompat.getColor(this, R.color.font_color_figma)
        val highlightColor = ContextCompat.getColor(this, R.color.actionbar_color_figma)
        val customFont = ResourcesCompat.getFont(this, R.font.poppins_regular)
        // Reset all to normal (color + style)
        listOf(binding.talukaTextView, binding.districtTextView, binding.stateTextView).forEach {
            it.setTextColor(normalColor)
            it.setTypeface(customFont, Typeface.NORMAL)
        }

        // Apply highlight (color + bold style)
        when {
            taluka == 1 -> {
                binding.talukaTextView.setTextColor(highlightColor)
                binding.talukaTextView.setTypeface(customFont, Typeface.BOLD)
                selectedValue = "taluka"
                leaderboardViewModel.getLeaderboardData(context = this, selectedValue)
            }

            district == 1 -> {
                binding.districtTextView.setTextColor(highlightColor)
                binding.districtTextView.setTypeface(customFont, Typeface.BOLD)
                selectedValue = "district"
                leaderboardViewModel.getLeaderboardData(context = this, selectedValue)
            }

            state == 1 -> {
                binding.stateTextView.setTextColor(highlightColor)
                binding.stateTextView.setTypeface(customFont, Typeface.BOLD)
                selectedValue = "state"
                leaderboardViewModel.getLeaderboardData(context = this, selectedValue)
            }
        }
    }

}
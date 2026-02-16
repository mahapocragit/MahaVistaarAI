package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityLeaderboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.LeaderboardNewAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MahavistaarViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private val mahavistaarViewModel: MahavistaarViewModel by viewModels()
    private var talukaJsonArray = JSONArray()
    private var districtJsonArray = JSONArray()
    private var stateJsonArray = JSONArray()
    var languageToLoad = "mr"
    private var selectedValue = "taluka"
    private var token: String = ""
    private var leaderboardInfoText = ""

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
        ProgressHelper.showProgressDialog(this)
        observeViewModel()
        mahavistaarViewModel.requestUrlForChatBot(this)
        setUpViews()
    }

    private fun observeViewModel() {
        mahavistaarViewModel.responseUrlForChatBot.observe(this) { response ->
            if (response != null) {
                val json = JSONObject(response.toString())
                val status = json.optString("status")
                if (status.equals("success", ignoreCase = true)) {
                    token = json.optString("token").trim()
                    leaderboardViewModel.getLeaderboardForAll(this, token)
                }
            }
        }

        leaderboardViewModel.responseLeaderboardForAll.observe(this) { response ->
            if (response != null) {
                val json = JSONObject(response.toString())
                leaderboardInfoText = if (languageToLoad=="en")json.optString("leaderInfoText") else json.optString("leaderInfoTextMr")
                val dataJson = json.optJSONObject("data")
                talukaJsonArray = dataJson.optJSONArray("talukaList")
                districtJsonArray = dataJson.optJSONArray("districtList")
                stateJsonArray = dataJson.optJSONArray("stateList")
                enableDisableViews(talukaJsonArray)
            }
        }
    }

    private fun enableDisableViews(dataJsonArray: JSONArray) {
        if (dataJsonArray.length() > 0) {
            formatToppers(dataJsonArray)
            binding.rankNameLocationTextView.visibility = View.VISIBLE
            binding.leaderboardRecyclerView.visibility = View.VISIBLE
            binding.notificationNotFoundLayout.visibility = View.GONE
            binding.leaderboardRecyclerView.adapter =
                LeaderboardNewAdapter(dataJsonArray, selectedValue)
        } else {
            binding.notificationNotFoundLayout.visibility = View.VISIBLE
            binding.rankNameLocationTextView.visibility = View.GONE
            binding.leaderboardRecyclerView.visibility = View.GONE
        }
    }

    fun formatToppers(dataJsonArray: JSONArray) {
        val rankBindings = listOf(
            Pair(binding.firstRankNameTextView, binding.firstRankTalukaTextView),
            Pair(binding.secondRankNameTextView, binding.secondRankTalukaTextView),
            Pair(binding.thirdRankNameTextView, binding.thirdRankTalukaTextView)
        )

        for (i in 0 until minOf(3, dataJsonArray.length())) {
            val topperJson = dataJsonArray[i] as JSONObject
            val username = topperJson.optString("username").trim().split(" ")[0]
            val taluka = topperJson.optString("taluka")
            val district = topperJson.optString("district")

            val (nameView, talukaView) = rankBindings[i]
            nameView.text = formatName(username)
            talukaView.text = formatLocation(
                when (selectedValue) {
                    "taluka", "district" -> taluka
                    else -> district
                }
            )
        }
    }

    fun formatName(str: String): String {
        val firstWord = str.split(" ")[0]   // take first word
        return if (firstWord.length > 6) {
            firstWord.take(6) + ".."
        } else {
            firstWord
        }
    }

    fun formatLocation(str: String): String {
        return if (str.length > 6) {
            str.take(6) + ".."
        } else {
            str
        }
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

        binding.toolbarLayout.imageViewFilterMenu.visibility = View.VISIBLE
        binding.toolbarLayout.imageViewFilterMenu.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.info_ic
            )
        )
        binding.toolbarLayout.imageViewFilterMenu.setOnClickListener { view ->
            val popupView = layoutInflater.inflate(R.layout.popup_info, null)

            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true // focusable, allows dismiss on outside touch
            )

            val popupText = popupView.findViewById<TextView>(R.id.popupText)
            popupText.text = leaderboardInfoText

            popupWindow.elevation = 10f
            popupWindow.isOutsideTouchable = true

            // Show popup just below the info icon
            popupWindow.showAsDropDown(view, 0, 0)
        }
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
                enableDisableViews(talukaJsonArray)
            }

            district == 1 -> {
                binding.districtTextView.setTextColor(highlightColor)
                binding.districtTextView.setTypeface(customFont, Typeface.BOLD)
                selectedValue = "district"
                enableDisableViews(districtJsonArray)
            }

            state == 1 -> {
                binding.stateTextView.setTextColor(highlightColor)
                binding.stateTextView.setTypeface(customFont, Typeface.BOLD)
                selectedValue = "state"
                enableDisableViews(stateJsonArray)
            }
        }
    }

}
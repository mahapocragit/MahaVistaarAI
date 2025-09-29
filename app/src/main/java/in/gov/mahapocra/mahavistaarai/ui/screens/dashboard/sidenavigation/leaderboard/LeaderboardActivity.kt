package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityLeaderboardBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    var languageToLoad = "mr"

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
            if (response!=null){
                val jsonObject = JSONObject(response.toString())
                Log.d("TAGGER", "observeViewModel: $jsonObject")
            }
        }
    }

    private fun setUpViews() {
        val jsonArray = JSONArray(
            "[\n" +
                    "  { \"name\": \"Alice\", \"score\": 100 },\n" +
                    "  { \"name\": \"Bob\", \"score\": 85 },\n" +
                    "  { \"name\": \"Charlie\", \"score\": 95 },\n" +
                    "  { \"name\": \"Diana\", \"score\": 78 },\n" +
                    "  { \"name\": \"Ethan\", \"score\": 88 },\n" +
                    "  { \"name\": \"Fiona\", \"score\": 92 },\n" +
                    "  { \"name\": \"George\", \"score\": 81 }\n" +
                    "]"
        )
        binding.leaderboardRecyclerView.hasFixedSize()
        binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.leaderboardRecyclerView.adapter = LeaderboardAdapter(jsonArray)

        binding.talukaTextView.setOnClickListener {
            leaderboardViewModel.getLeaderboardData()
        }

        binding.districtTextView.setOnClickListener {
            leaderboardViewModel.getLeaderboardData()
        }

        binding.stateTextView.setOnClickListener {
            leaderboardViewModel.getLeaderboardData()
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
}
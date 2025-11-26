package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosDetailedBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.VideosAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.VIDEOS_POINT
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONObject

class VideosDetailedActivity : AppCompatActivity(), RecyclerItemClickListener {

    private lateinit var binding: ActivityVideosDetailedBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@VideosDetailedActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityVideosDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        observeResponse()
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.videos_bottom)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val videosJsonObject = intent.getStringExtra("videosJsonObject")
        if (videosJsonObject != null) {
            val jsonObject = JSONObject(videosJsonObject)
            val videosJsonArray = jsonObject.optJSONArray("links")
            binding.videosRecyclerView.layoutManager = GridLayoutManager(this, 2)
            binding.videosRecyclerView.hasFixedSize()
            binding.videosRecyclerView.adapter = videosJsonArray?.let {
                VideosAdapter(
                    it, this
                )
            }
        }
    }

    private fun observeResponse(){
        leaderboardViewModel.responseUpdateUserPoints.observe(this){ response->
            if (response!=null){
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status==200){
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
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

    override fun onRecyclerItemClick(i: Int, obj: Any) {
        if (i==1){
            leaderboardViewModel.updateUserPoints(this, VIDEOS_POINT)
        }
    }
}
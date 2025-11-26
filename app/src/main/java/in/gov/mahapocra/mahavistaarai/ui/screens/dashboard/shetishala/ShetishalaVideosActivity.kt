package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityShetishalaVideosBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ShetishalaVideosAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.SHETISHALA_VIDEO_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONObject

class ShetishalaVideosActivity : AppCompatActivity(), RecyclerItemClickListener {

    private lateinit var binding: ActivityShetishalaVideosBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ShetishalaVideosActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityShetishalaVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        observeResponse()
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.videos_bottom)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        farmerViewModel.getShetishalaVideos(this)
    }

    private fun observeResponse() {

        leaderboardViewModel.responseUpdateUserPoints.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status == 200) {
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }

        farmerViewModel.shetishalaVideosResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val videosJsonArray = jsonObject.optJSONArray("data")
                binding.videosRecyclerView.layoutManager = GridLayoutManager(this, 2)
                binding.videosRecyclerView.hasFixedSize()
                binding.videosRecyclerView.adapter = videosJsonArray?.let { video ->
                    ShetishalaVideosAdapter(
                        video, languageToLoad, this
                    )
                }
            }
        }

        farmerViewModel.error.observe(this) {
            Log.d(TAG, "observeResponse: $it")
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
        if (i == 2) {
            Log.d(TAG, "onRecyclerItemClick: $obj")
            leaderboardViewModel.updateUserPoints(this, SHETISHALA_VIDEO_POINT)
        }
    }
}
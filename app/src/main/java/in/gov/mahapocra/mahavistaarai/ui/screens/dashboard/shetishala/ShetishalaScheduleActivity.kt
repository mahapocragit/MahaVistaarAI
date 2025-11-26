package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityShetishalaScheduleBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ShetishalaScheduleAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.CROP_ADVISORY_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.SHETISHALA_MEETING_URL_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.SHETISHALA_YOUTUBE_URL_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import org.json.JSONObject

class ShetishalaScheduleActivity : AppCompatActivity(), RecyclerItemClickListener {

    private lateinit var binding: ActivityShetishalaScheduleBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ShetishalaScheduleActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityShetishalaScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        setUpToolbar()
        observeResponse()
        binding.useYoutubeLayout.setOnClickListener {
            leaderboardViewModel.updateUserPoints(this, SHETISHALA_YOUTUBE_URL_POINT)
            loadYoutubeUrl("https://www.youtube.com/@PaaniFoundation/live")
        }
        //setRecyclerView
        binding.shetishalaRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.shetishalaRecyclerView.setHasFixedSize(true)
        farmerViewModel.getDigitalShetishalaSchedule(this)
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

        farmerViewModel.getDigitalShetishalaScheduleResponse.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataObject = jsonObject.optJSONObject("data")
                val scheduleArray = dataObject.optJSONArray("schedule")
                scheduleArray?.let { array ->
                    binding.shetishalaRecyclerView.adapter =
                        ShetishalaScheduleAdapter(array, this)
                }
            }
        }
        farmerViewModel.error.observe(this) {
            Log.d(TAG, "observeResponse: $it")
        }
    }

    private fun loadYoutubeUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.setPackage("com.google.android.youtube")
        try {
            startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(webIntent)
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.shetishala_schedule)
        binding.toolbar.textViewHeaderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
            leaderboardViewModel.updateUserPoints(this, SHETISHALA_MEETING_URL_POINT)
        }
    }
}
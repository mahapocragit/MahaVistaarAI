package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.VideosCategoryAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.abs

class VideosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var languageToLoad: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@VideosActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.videos_bottom)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(this@VideosActivity, DashboardScreen::class.java))
            }
        })
        ProgressHelper.showProgressDialog(this)
        farmerViewModel.getVideosForFarmer(this)
        getFarmerSelectedCrop()
        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.bubbleIconImageView.animate()
                .alpha(0f)
                .setDuration(500) // animation duration in ms
                .withEndAction {
                    binding.bubbleIconImageView.visibility = View.GONE
                    binding.bubbleIconImageView.alpha = 1f // reset alpha in case you show it again
                }
                .start()
        }

        binding.chatbotIcon.setOnTouchListener(DraggableTouchListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        })
    }

    private fun getFarmerSelectedCrop() {
        farmerViewModel.videosResponse.observe(this){
            ProgressHelper.disableProgressDialog()
            if (it!=null){
                val jSONObject = JSONObject(it.toString())
                if (jSONObject.optInt("status") == 200) {
                    val videosCategoryJson = jSONObject.optJSONArray("data")
                    binding.videosCategoriesRecyclerView.layoutManager = GridLayoutManager(this, 2)
                    binding.videosCategoriesRecyclerView.hasFixedSize()
                    binding.videosCategoriesRecyclerView.adapter = videosCategoryJson?.let { it1 ->
                        VideosCategoryAdapter(
                            it1, languageToLoad
                        )
                    }
                }
            }
        }
        farmerViewModel.error.observe(this){
            ProgressHelper.disableProgressDialog()
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
}
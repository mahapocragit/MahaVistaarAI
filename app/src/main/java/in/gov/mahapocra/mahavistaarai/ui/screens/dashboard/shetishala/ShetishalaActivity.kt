package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityShetishalaBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosDetailedActivity
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class ShetishalaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShetishalaBinding
    private lateinit var languageToLoad: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ShetishalaActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityShetishalaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.shetishala)
        binding.toolbar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ShetishalaActivity, DashboardScreen::class.java))
            }
        })
        binding.scheduleCardView.setOnClickListener {
            startActivity(
                Intent(
                    this@ShetishalaActivity,
                    ShetishalaScheduleActivity::class.java
                )
            )
        }

        binding.shetishalaCardView.setOnClickListener {
            startActivity(
                Intent(
                    this@ShetishalaActivity,
                    VideosDetailedActivity::class.java
                )
            )
        }
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
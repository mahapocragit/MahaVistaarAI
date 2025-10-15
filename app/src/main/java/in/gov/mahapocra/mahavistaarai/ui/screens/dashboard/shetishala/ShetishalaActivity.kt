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
import `in`.gov.mahapocra.mahavistaarai.util.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
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
        val isGuest =
            AppSettings.getInstance().getBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        binding.chatbotIcon.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f
            private var startX = 0f
            private var startY = 0f
            private val CLICK_THRESHOLD = 20 // px movement allowed

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        startX = event.rawX
                        startY = event.rawY
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val parent = v.parent as View
                        val newX = event.rawX + dX
                        val newY = event.rawY + dY

                        // calculate boundaries (you can adjust margin if needed)
                        val margin = 32 // px margin from edges
                        val maxX = parent.width - v.width - margin
                        val maxY = parent.height - v.height - margin
                        val minX = margin
                        val minY = margin

                        // constrain movement inside screen
                        val boundedX = newX.coerceIn(minX.toFloat(), maxX.toFloat())
                        val boundedY = newY.coerceIn(minY.toFloat(), maxY.toFloat())

                        v.animate()
                            .x(boundedX)
                            .y(boundedY)
                            .setDuration(0)
                            .start()
                    }

                    MotionEvent.ACTION_UP -> {
                        val diffX = abs(event.rawX - startX)
                        val diffY = abs(event.rawY - startY)

                        if (diffX < CLICK_THRESHOLD && diffY < CLICK_THRESHOLD) {
                            if (!isGuest) {
                                startActivity(
                                    Intent(
                                        this@ShetishalaActivity,
                                        ChatbotActivity::class.java
                                    )
                                )
                            } else {
                                AlertDialog.Builder(this@ShetishalaActivity)
                                    .setMessage(R.string.bot_chat_login_redirect_mesage)
                                    .setPositiveButton(R.string.yes) { dialog, _ ->
                                        startActivity(
                                            Intent(
                                                this@ShetishalaActivity,
                                                LoginScreen::class.java
                                            ).apply {
                                                putExtra("from", "dashboard")
                                            })
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                        }
                    }
                }
                return true
            }
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
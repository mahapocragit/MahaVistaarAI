package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDbtactivityBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.mahadbt.MahaDbtSchemesActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.pocra.PocraDbtSchemes
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DBTActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDbtactivityBinding
    private lateinit var languageToLoad: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DBTActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityDbtactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiResponsive(binding.root)
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.dbtschema)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(this@DBTActivity, DashboardScreen::class.java))
            }
        })

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
        binding.pocraDBTCardView.setOnClickListener {
            startActivity(Intent(this, PocraDbtSchemes::class.java).apply {
                putExtra("dbtFromDashboard", "pocraDBTCardView")
            })
        }

        binding.mahaDBTCardView.setOnClickListener {
            startActivity(Intent(this, MahaDbtSchemesActivity::class.java).apply {
                putExtra("dbtFromDashboard", "mahaDBTCardView")
            })
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
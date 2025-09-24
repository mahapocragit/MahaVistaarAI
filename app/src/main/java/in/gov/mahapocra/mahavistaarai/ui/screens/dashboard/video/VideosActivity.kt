package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONObject

class VideosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var languageToLoad: String

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
            onBackPressedDispatcher.onBackPressed()
        }
        ProgressHelper.showProgressDialog(this)
        farmerViewModel.getVideosForFarmer(this)
        getFarmerSelectedCrop()
        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)

        val isGuest = AppSettings.getInstance().getBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        binding.chatbotIcon.setOnClickListener {
            if (!isGuest) {
                startActivity(Intent(this, ChatbotActivity::class.java))
            } else {
                AlertDialog.Builder(this)
                    .setMessage(R.string.bot_chat_login_redirect_mesage)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        // Handle login action here
                        startActivity(Intent(this, LoginScreen::class.java).apply {
                            putExtra("from", "dashboard")
                        })
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
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
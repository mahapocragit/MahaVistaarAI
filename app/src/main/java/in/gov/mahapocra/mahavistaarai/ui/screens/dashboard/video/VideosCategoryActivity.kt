package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosCategoryBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.VideosSubCategoryAdapter
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONObject

class VideosCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosCategoryBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@VideosCategoryActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityVideosCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.textViewHeaderTitle.text = getString(R.string.videos_bottom)
        binding.toolbar.imgBackArrow.setOnClickListener {
            finish()
        }
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    OnBackPressedDispatcher().onBackPressed()
                }
            })
            finish()
        }
        val videosJsonString = intent.getStringExtra("videosJsonObject")
        Log.d(TAG, "onCreate: $videosJsonString")

        if (videosJsonString != null) {
            val jSONObject = JSONObject(videosJsonString.toString())
            Log.d(TAG, "onCreate: $jSONObject")
            val videosCategoryJson = jSONObject.optJSONArray("folders")
            binding.videosCategoriesRecyclerView.layoutManager = GridLayoutManager(this, 2)
            binding.videosCategoriesRecyclerView.hasFixedSize()
            binding.videosCategoriesRecyclerView.adapter = videosCategoryJson?.let { it1 ->
                VideosSubCategoryAdapter(
                    it1, languageToLoad
                )
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
}
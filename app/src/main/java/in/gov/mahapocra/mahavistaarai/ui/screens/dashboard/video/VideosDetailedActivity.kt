package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosDetailedBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONObject

class VideosDetailedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosDetailedBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@VideosDetailedActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        LocalCustom.configureLocale(baseContext, languageToLoad)
        binding= ActivityVideosDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textViewHeaderTitle.text = getString(R.string.videos_bottom)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val videosJsonObject  = intent.getStringExtra("videosJsonObject")
        val jsonObject = JSONObject(videosJsonObject)
        val videosJsonArray = jsonObject.optJSONArray("links")

        binding.videosRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.videosRecyclerView.hasFixedSize()
        binding.videosRecyclerView.adapter = videosJsonArray?.let {
            VideosAdapter(
                it
            )
        }
    }
}
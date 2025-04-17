package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.helper.JsonObject
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosDetailedBinding
import org.json.JSONObject

class VideosDetailedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosDetailedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding.videosRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.videosRecyclerView.hasFixedSize()
        binding.videosRecyclerView.adapter = videosJsonArray?.let {
            VideosAdapter(
                it
            )
        }
    }
}
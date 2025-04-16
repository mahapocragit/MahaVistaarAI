package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.forum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.farmerapp.databinding.ActivityForumPostDetailsBinding

class ForumPostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForumPostDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ForumViewPostAdapter()
        binding.postRepliesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.postRepliesRecyclerView.adapter = adapter
    }
}
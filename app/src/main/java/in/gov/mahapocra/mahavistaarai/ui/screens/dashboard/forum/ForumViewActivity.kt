package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.forum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityForumViewBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ForumAllAdapter

class ForumViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForumViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ForumAllAdapter()
        binding.trendingOpenRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trendingOpenRecyclerView.adapter = adapter

    }
}
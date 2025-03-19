package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.forum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityForumViewBinding

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
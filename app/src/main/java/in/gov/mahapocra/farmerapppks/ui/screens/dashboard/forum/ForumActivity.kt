package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.forum

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityForumBinding

class ForumActivity : AppCompatActivity() {

    lateinit var binding: ActivityForumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "Forum"
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Trending"
                1 -> "All"
                else -> "Tab One"
            }
        }.attach()
    }
}
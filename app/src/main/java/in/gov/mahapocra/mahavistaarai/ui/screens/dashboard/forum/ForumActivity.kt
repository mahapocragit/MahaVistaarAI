package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.forum

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityForumBinding

class ForumActivity : AppCompatActivity() {

    lateinit var binding: ActivityForumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.forum_bottom)
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        showAlertDialog()

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Trending"
                1 -> "All"
                else -> "Tab One"
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> binding.textLabelForTabs.text = "Trending"
                    1 -> binding.textLabelForTabs.text = "All"
                    else -> binding.textLabelForTabs.text = "Tab One"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Optional: Handle if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Optional: Handle if needed
            }
        })
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.rules_and_regulations))
            .setMessage(
                getString(R.string.rules_and_regulations_desc)
            )
            .setCancelable(true) // Allows user to dismiss by tapping outside
            .setPositiveButton(R.string.i_agree) { dialog, _ ->
                // Handle OK click
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
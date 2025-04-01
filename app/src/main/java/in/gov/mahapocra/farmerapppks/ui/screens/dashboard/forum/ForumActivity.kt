package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.forum

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
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
        builder.setTitle("Rules & Regulations")
            .setMessage(
                "1. Follow the Rules – Ensure you adhere to all guidelines to maintain a safe and enjoyable environment.\n\n" +
                        "2. Be Respectful – Treat everyone with kindness. Avoid hate speech, harassment, or offensive language.\n\n" +
                        "3. No Spamming – Do not flood the chat with repetitive messages, links, or promotional content.\n\n" +
                        "4. Stay On Topic – Keep discussions relevant to the chat's purpose. Off-topic conversations may be redirected or removed.\n\n" +
                        "5. No Personal Attacks – Debate ideas, not people. Disagreements are okay, but personal attacks are not tolerated.\n\n" +
                        "6. No Sharing of Personal Information – For privacy and security, do not share personal details like phone numbers or addresses.\n\n" +
                        "7. Follow Moderator Instructions – Respect the decisions of moderators. They are here to ensure a fair and enjoyable experience for everyone."
            )
            .setCancelable(true) // Allows user to dismiss by tapping outside
            .setPositiveButton("I agree") { dialog, _ ->
                // Handle OK click
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
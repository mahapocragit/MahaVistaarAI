package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.forum

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityForumBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage

class ForumActivity : AppCompatActivity() {

    lateinit var binding: ActivityForumBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ForumActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
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
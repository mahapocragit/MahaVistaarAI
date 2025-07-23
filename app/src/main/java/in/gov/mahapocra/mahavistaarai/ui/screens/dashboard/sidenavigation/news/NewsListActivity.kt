package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNotificationListBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.NewsWadhwaniViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import org.json.JSONObject

class NewsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationListBinding
    private val newsWadhwaniViewModel: NewsWadhwaniViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@NewsListActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityNotificationListBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.news)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        ProgressHelper.showProgressDialog(this)
        observeResponseArrivingFromAPI()
        newsWadhwaniViewModel.getAuthenticationForNews(this)
    }

    private fun observeResponseArrivingFromAPI() {
        newsWadhwaniViewModel.responseAuthToken.observe(this) {
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataJsonObject = jsonObject.optJSONObject("data")
                val bearerToken = dataJsonObject.optString("token")
                newsWadhwaniViewModel.getNewsWadhwani(this, bearerToken)
            }
        }
        newsWadhwaniViewModel.responseNewsWadhwani.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataJsonObject = jsonObject.optJSONObject("data")
                val eventJsonArray = dataJsonObject?.optJSONArray("events")
                binding.newsRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.newsRecyclerView.adapter =
                    eventJsonArray?.let { it1 -> NewsAdapter(it1, languageToLoad) }
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


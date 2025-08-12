package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNotificationListBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.NewsWadhwaniViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationListBinding
    private val newsWadhwaniViewModel: NewsWadhwaniViewModel by viewModels()
    private lateinit var languageToLoad: String
    private var offset = 0

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
        uiResponsive(binding.root)
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
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -7) // Go back 7 days
                val dateSevenDaysAgo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(calendar.time)
                val currentDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(
                    Date()
                )
                newsWadhwaniViewModel.getNewsWadhwani(bearerToken, offset, dateSevenDaysAgo, currentDateTime)
                binding.nextButton.setOnClickListener {
                    offset +=10
                    binding.countText.text = offset.toString()
                    newsWadhwaniViewModel.getNewsWadhwani(bearerToken, offset, dateSevenDaysAgo, currentDateTime)
                }
                binding.prevButton.setOnClickListener {
                    offset -=10
                    newsWadhwaniViewModel.getNewsWadhwani(bearerToken, offset, dateSevenDaysAgo, currentDateTime)
                }
            }
        }
        newsWadhwaniViewModel.responseNewsWadhwani.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataJsonObject = jsonObject.optJSONObject("data")
                val eventJsonArray = dataJsonObject?.optJSONArray("events")
                if (eventJsonArray?.length() != 0) {
                    binding.newsRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.newsRecyclerView.adapter =
                        eventJsonArray?.let { it1 -> NewsAdapter(it1, languageToLoad) }
                }else{
                    offset -=10
                    UIToastMessage.show(this, "This is the last page")
                }
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


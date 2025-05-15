package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNotificationListBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.NewsWadhwaniViewModel
import org.json.JSONObject

class NewsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var newsWadhwaniViewModel: NewsWadhwaniViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationListBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        newsWadhwaniViewModel = ViewModelProvider(this)[NewsWadhwaniViewModel::class.java]
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.news)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

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
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataJsonArray = jsonObject.optJSONArray("data")
            }
        }
    }
}


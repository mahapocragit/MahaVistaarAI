package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityShetishalaScheduleBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.ScoreBubbleHelper
import org.json.JSONObject

class ShetishalaScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShetishalaScheduleBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ShetishalaScheduleActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityShetishalaScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        setUpToolbar()
        binding.useYoutubeLayout.setOnClickListener {
            loadYoutubeUrl("https://www.youtube.com/@PaaniFoundation/live")
        }
        //setRecyclerView
        binding.shetishalaRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.shetishalaRecyclerView.setHasFixedSize(true)
        farmerViewModel.getDigitalShetishalaSchedule(this)
        ProgressHelper.showProgressDialog(this)
        farmerViewModel.getDigitalShetishalaScheduleResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataObject = jsonObject.optJSONObject("data")
                val scheduleArray = dataObject.optJSONArray("schedule")
                scheduleArray?.let { array ->
                    binding.shetishalaRecyclerView.adapter =
                        ShetishalaScheduleAdapter(array)
                }
            }
        }
        farmerViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
        }
        ScoreBubbleHelper.showSnackbar(binding.root, "10 Points Added")
    }

    private fun loadYoutubeUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.setPackage("com.google.android.youtube")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to browser if YouTube app isn't installed
            val webIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(webIntent)
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.shetishala_schedule)
        binding.toolbar.textViewHeaderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class VideosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosBinding
    private lateinit var farmerViewModel: FarmerViewModel
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@VideosActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.videos_bottom)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        ProgressHelper.showProgressDialog(this)
        farmerViewModel.getVideosForFarmer(this)
        getFarmerSelectedCrop()
    }

    private fun getFarmerSelectedCrop() {
        farmerViewModel.videosResponse.observe(this){
            ProgressHelper.disableProgressDialog()
            if (it!=null){
                val jSONObject = JSONObject(it.toString())
                if (jSONObject.optInt("status") == 200) {
                    val videosCategoryJson = jSONObject.optJSONArray("data")
                    binding.videosCategoriesRecyclerView.layoutManager = GridLayoutManager(this, 2)
                    binding.videosCategoriesRecyclerView.hasFixedSize()
                    binding.videosCategoriesRecyclerView.adapter = videosCategoryJson?.let { it1 ->
                        VideosCategoryAdapter(
                            it1, languageToLoad
                        )
                    }
                }
            }
        }
        farmerViewModel.error.observe(this){
            ProgressHelper.disableProgressDialog()
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
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityVideosBinding
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class VideosActivity : AppCompatActivity(), ApiCallbackCode {

    private lateinit var binding: ActivityVideosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textViewHeaderTitle.text = getString(R.string.videos_bottom)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        getFarmerSelectedCrop()
    }

    private fun getFarmerSelectedCrop() {
        try {
            val api =
                AppInventorApi(this, APIServices.FARMER, "", AppString(this).getkMSG_WAIT(), true)

            val handler = Handler()
            val runnable = Runnable {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest: APIRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getFarmersVideosJson()
                api.postRequest(responseCall, this, 1)
            }
            handler.post(runnable)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    val videosCategoryJson = jSONObject.optJSONArray("data")
                    binding.videosCategoriesRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.videosCategoriesRecyclerView.hasFixedSize()
                    binding.videosCategoriesRecyclerView.adapter = videosCategoryJson?.let {
                        VideosCategoryAdapter(
                            it
                        )
                    }
                }
            }
        }
    }
}
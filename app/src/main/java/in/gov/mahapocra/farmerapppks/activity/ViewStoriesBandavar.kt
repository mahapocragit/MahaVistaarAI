package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorIncAPI
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.FarmerStoryAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class ViewStoriesBandavar : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback, OnMultiRecyclerItemClickListener {
    lateinit var textViewHeaderTitle: TextView
    lateinit var imageMenushow: ImageView
    private var recyclerViewFarmerSuccessStory: RecyclerView? = null
    private lateinit var userMobileNo: String
    private var farmerStoryJSONArray: JSONArray? = null
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stories_bandavar)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@ViewStoriesBandavar).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }

        init()
        onClick()
        imageMenushow.visibility = View.VISIBLE
        textViewHeaderTitle.setText(R.string.success_farmer)
        userMobileNo = AppSettings.getInstance().getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
        getFarmerStoryList()
    }
    private fun init()
    {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
        recyclerViewFarmerSuccessStory = findViewById(R.id.recyclerViewFarmerStory)
    }
    private fun onClick()
    {
        imageMenushow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }
    }

    private fun getFarmerStoryList() {
        val url: String = APIServices.kBandhavarViewStory+userMobileNo
        val api = AppinventorIncAPI(this, APIServices.SSO, APIServices.SSO_KEY, "", true)
        api.getRequestData(url, this, 1)
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                farmerStoryJSONArray = response.getResponseArray()
                val adaptorDbtActivityGrp =
                    FarmerStoryAdapter(
                        this,
                        this,
                        farmerStoryJSONArray
                    )
                recyclerViewFarmerSuccessStory?.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
                recyclerViewFarmerSuccessStory?.setAdapter(adaptorDbtActivityGrp)
                adaptorDbtActivityGrp.notifyDataSetChanged()
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }

    override fun onFailure(th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }
}
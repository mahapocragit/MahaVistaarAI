package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.sidenavigation.news

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.util.Utility
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityFarmNewsBinding
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class FarmNewsActivity : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {
    
    private lateinit var binding: ActivityFarmNewsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        try {
            setConfiguration()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun initComponents() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.setLayoutManager(layoutManager)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemAnimator(DefaultItemAnimator())
    }

    @Throws(JSONException::class)
    private fun setConfiguration() {
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.news)
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            val intent = Intent(this@FarmNewsActivity, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


        if (Utility.checkConnection(this)) {
            getNotificationList()
        } else {
            UIToastMessage.show(this@FarmNewsActivity, "No internet connection")
        }
    }

    override fun onResume() {
        super.onResume()
        if (Utility.checkConnection(this)) {
            try {
                getNotificationList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            UIToastMessage.show(this@FarmNewsActivity, "No internet connection")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, o: Any) {
        val jsonObject = o as JSONObject
        val intent = Intent(this, NewsReadActivity::class.java)
        intent.putExtra("noticationData", jsonObject.toString())
        startActivity(intent)
    }

    @Synchronized
    fun getNotificationList() {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            val requestBody: RequestBody =
                AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(this, APIServices.DBT, "", AppConstants.kMSG, true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest: APIRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getNewsList(requestBody)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    override fun onResponse(jsonObject: JSONObject?, i: Int) {
        if (jsonObject != null) {
            val responseModel = ResponseModel(jsonObject)
            if (i == 1) {
                if (responseModel.getStatus()) {
                    val jsonArray: JSONArray = responseModel.getNewsArray()
                    if (jsonArray.length() > 0) {
                        val adapter = NewsMessageListAdapter(this, this, jsonArray)
                        binding.recyclerView.setAdapter(adapter)
                    }
                } else {
                    UIToastMessage.show(this, responseModel.getMsg())
                }
            }
        }
    }

    override fun onFailure(o: Any?, throwable: Throwable?, i: Int) {
    }
}
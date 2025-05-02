package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ClimateResilientTechnologyAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class ClimateResilientTechnology : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode,
    OnMultiRecyclerItemClickListener {

    private var climateResilientGroup: RecyclerView? = null
    private var textViewHeaderTitle: TextView? = null
    private var imageMenushow: ImageView? = null
    private lateinit var languageToLoad: String
    private var resilientGrpWiseDetailsJSONArray: JSONArray? = null
    private lateinit var resilientCRAGroupJSONArray: JSONArray

    private var groupName: ArrayList<String> = ArrayList()
    private var groupImagePath: ArrayList<String> = ArrayList()
    private var webUrl: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this).equals("2", ignoreCase = true)) {
            languageToLoad = "mr"
        } else {
            languageToLoad = "en"
        }
        LocalCustom.configureLocale(baseContext, languageToLoad)
        setContentView(R.layout.activity_climate_resilint_technology)
        init()
        textViewHeaderTitle?.setText(R.string.climateTechnology)
        imageMenushow?.visibility = View.VISIBLE
        imageMenushow?.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        climateResilientGroupList()
    }

    private fun init() {
        climateResilientGroup = findViewById(R.id.climateResilint)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
    }

    private fun climateResilientGroupList() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api_key", APIServices.SSO_KEY)
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                APIServices.FARMER,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getClimateResilientList(requestBody)
            api.postRequest(responseCall, this, 2)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onFailure(th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        th?.printStackTrace()
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 2) {
            if (jSONObject != null) {
                Log.d("TAGGER", "onResponse: $jSONObject")
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    resilientGrpWiseDetailsJSONArray = response.getResilientyGrpArray()
                    val adaptorResilientTechnologyGrp =
                        ClimateResilientTechnologyAdapter(
                            this,
                            this,
                            resilientGrpWiseDetailsJSONArray
                        )
                    climateResilientGroup?.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    )
                    climateResilientGroup?.setAdapter(adaptorResilientTechnologyGrp)
                    adaptorResilientTechnologyGrp.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        val jsonObject = obj as JSONObject
        resilientCRAGroupJSONArray = jsonObject.getJSONArray("CRAGroups")
        val craGroppLength: Int = resilientCRAGroupJSONArray.length()
        for (i in 0 until craGroppLength) {
            val userDetail = resilientCRAGroupJSONArray.getJSONObject(i)
            groupName.add(userDetail.getString("GroupName"))
            groupImagePath.add(userDetail.getString("groupimagepath"))
            webUrl.add(userDetail.getString("WbUrl"))
        }
        val intent = Intent(this, ClimateDetailsGrid::class.java)
        val b = Bundle()
        b.putSerializable("craGroppLength", craGroppLength)
        b.putSerializable("GroupName", groupName)
        b.putSerializable("GroupImagePath", groupImagePath)
        b.putSerializable("WebUrl", webUrl)
        intent.putExtras(b)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
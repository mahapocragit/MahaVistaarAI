package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.ClimateResilientTechnology
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
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
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

    class NewUpdateKharip : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode,
        OnMultiRecyclerItemClickListener {

        private var climateResilientGroup: RecyclerView? = null
        private var textViewHeaderTitle: TextView? = null
        private var imageMenushow: ImageView? = null
        var languageToLoad: String? = null
        private var resilientGrpWiseDetailsJSONArray: JSONArray? = null
        lateinit var resilientCRAGroupJSONArray: JSONArray

        var groupName: ArrayList<String> = ArrayList()
        var groupImagePath: ArrayList<String> = ArrayList()
        var webUrl: ArrayList<String> = ArrayList()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_new_update_kharip)
            if (AppSettings.getLanguage(this).equals("2", ignoreCase = true)) {
                languageToLoad = "mr"
            }else{
                languageToLoad = "en"
            }
            init()
            textViewHeaderTitle?.setText(R.string.climateTechnology)
            imageMenushow?.setVisibility(View.VISIBLE)
            imageMenushow?.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, DashboardScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            })
            climateResilientGroupList()
        }

        private fun init() {
            climateResilientGroup = findViewById(R.id.climateResilint)
            // dbtActivityGrp.setHasFixedSize(false)
            textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle)
            imageMenushow=findViewById(R.id.imageMenushow)
        }
        private fun climateResilientGroupList() {
            Log.d("languageToLoad12121", languageToLoad.toString())
            val jsonObject = JSONObject()
            try {
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)
                jsonObject.put("lang",languageToLoad)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api = AppInventorApi(
                    this,
                    APIServices.DBT,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getClimateResilientListNewUpdate(requestBody)
                DebugLog.getInstance().d("param1=" + responseCall.request().toString())
                DebugLog.getInstance().d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                api.postRequest(responseCall, this, 2)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
        override fun onFailure(th: Throwable?, i: Int) {
            DebugLog.getInstance().d("onResponse=$th")
        }
        override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
            DebugLog.getInstance().d("onResponse=$obj")
        }
        override fun onResponse(jSONObject: JSONObject?, i: Int) {
            if (i == 2) {
                if (jSONObject != null) {
                    DebugLog.getInstance().d("onResponse=$jSONObject")
                    val response = ResponseModel(jSONObject)
                    if (response.getStatus()) {

                        resilientGrpWiseDetailsJSONArray = response.getResilientyGrpArray()
                        Log.d("ClimateResilent","CRAMainGroup=="+"".toString())

                        // if (activityGrpWiseDetailsJSONArray?.length()!! > 0) {
                        val adaptorResilientTechnologyGrp =
                            ClimateResilientTechnology(
                                this,
                                this,
                                resilientGrpWiseDetailsJSONArray
                            )
                        climateResilientGroup?.setLayoutManager(
                            LinearLayoutManager(
                                this,
                                LinearLayoutManager.VERTICAL,
                                false)
                        )
                        climateResilientGroup?.setAdapter(adaptorResilientTechnologyGrp)
                        adaptorResilientTechnologyGrp.notifyDataSetChanged()
                        // }
                    }
                }
            }
        }

        override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
            val jsonObject = obj as JSONObject
            DebugLog.getInstance().d("jsonObject Data =$jsonObject")
            resilientCRAGroupJSONArray = jsonObject.getJSONArray("CRAGroups")
            Log.d("ClimateResilent","CRAGroup==="+resilientCRAGroupJSONArray.toString())
            val craGroppLength: Int = resilientCRAGroupJSONArray.length()
            for (i in 0 until craGroppLength) {
                val userDetail = resilientCRAGroupJSONArray.getJSONObject(i)
                groupName.add(userDetail.getString("GroupName"))
                groupImagePath.add(userDetail.getString("GroupImagePath"))
                webUrl.add(userDetail.getString("WebURL"))
                Log.d("ClimateResilent","groupName==="+groupName.toString())
                Log.d("ClimateResilent","groupImagePath==="+groupImagePath.toString())
                Log.d("ClimateResilent","webUrl==="+webUrl.toString())
            }
//        if (i == 2) {
            val intent = Intent(this, NewUpdateCRT::class.java)
            val b = Bundle()
            b.putSerializable("craGroppLength", craGroppLength)
            b.putSerializable("GroupName", groupName)
            b.putSerializable("GroupImagePath", groupImagePath)
            b.putSerializable("WebUrl", webUrl)
            intent.putExtras(b)
            startActivity(intent)
//        }
        }
        override fun onBackPressed() {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

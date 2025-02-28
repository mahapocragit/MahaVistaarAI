package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.BbtActivityGrpAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.ResponseModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class DbtSchemes : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener {

    private var activityGrpWiseDetailsJSONArray: JSONArray? = null
    private var dbtActivityGrp: RecyclerView? = null
    private var textViewHeaderTitle: TextView? = null
    private var imageMenushow: ImageView? = null
    var languageToLoad: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this@DbtSchemes).equals("2", ignoreCase = true)) {
            languageToLoad = "mr"
        } else {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_dbt_schemes)
        init()

        imageMenushow?.visibility = View.VISIBLE
        textViewHeaderTitle?.setText(R.string.dbtschema)
        imageMenushow?.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        dbtSchemesLists()
    }

    private fun init() {
        dbtActivityGrp = findViewById(R.id.dbtActivityGrp)
        // dbtActivityGrp.setHasFixedSize(false)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)

    }

    private fun dbtSchemesLists() {
        Log.d("languageToLoad12121", languageToLoad.toString())
        try {
            val secreateKey: String = APIServices.SSO_KEY
            //   val lang: String = "mr"
            val data: String = "No"

            //val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.DBT,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> =
                apiRequest.getDbtActivitiesDetails(secreateKey, languageToLoad, data)
            api.postRequest(responseCall, this, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onFailure(th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response =
                ResponseModel(jSONObject)
            if (response.status) {
                activityGrpWiseDetailsJSONArray = response.getActivityGrpArray()
                val adaptorDbtActivityGrp =
                    BbtActivityGrpAdapter(
                        this,
                        this,
                        activityGrpWiseDetailsJSONArray,
                        "dbtSchemes"
                    )
                dbtActivityGrp?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )
                dbtActivityGrp?.setAdapter(adaptorDbtActivityGrp)
                adaptorDbtActivityGrp.notifyDataSetChanged()
            } else {
                UIToastMessage.show(this, response.response)
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        val jsonObject = obj as JSONObject
        val dbtGrpID: String = jsonObject.getString("ActivityGroupID")
        val dbtGrpActivityName: String = jsonObject.getString("ActivityGroupName")
        DebugLog.getInstance().d("jsonObject=$jsonObject")

        if (i == 2) {
            val intent = Intent(this, DbtActivityDetails::class.java)
            intent.putExtra("ActivityGroupID", dbtGrpID)
            intent.putExtra("mActivityName", dbtGrpActivityName)
            startActivity(intent)
        }
    }
}
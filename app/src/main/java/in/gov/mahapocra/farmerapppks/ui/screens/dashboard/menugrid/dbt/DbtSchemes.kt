package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.dbt

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.ui.adapters.BbtActivityGrpAdapter
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class DbtSchemes : AppCompatActivity(), ApiCallbackCode, ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener {

    private lateinit var farmerRecyclerView: RecyclerView
    private lateinit var fpoRecyclerView: RecyclerView
    private lateinit var nrmRecyclerView: RecyclerView
    private var textViewHeaderTitle: TextView? = null
    private lateinit var farmerCardTV: TextView
    private lateinit var fpoCardTV: TextView
    private lateinit var nrmCardTV: TextView
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

        farmerCardTV.setOnClickListener {
            openRecyclerView(farmerRecyclerView)
        }

        fpoCardTV.setOnClickListener {
            openRecyclerView(fpoRecyclerView)
        }

        nrmCardTV.setOnClickListener {
            openRecyclerView(nrmRecyclerView)
        }
        dbtSchemesLists()
    }

    private fun openRecyclerView(recyclerView: RecyclerView) {
        if (recyclerView.visibility==View.VISIBLE) {
            recyclerView.visibility = View.GONE
        }else{
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun init() {
        fpoCardTV = findViewById(R.id.fpoCardTV)
        farmerCardTV = findViewById(R.id.farmerCardTV)
        nrmCardTV = findViewById(R.id.nrmCardTV)
        farmerRecyclerView = findViewById(R.id.farmerRecyclerView)
        fpoRecyclerView = findViewById(R.id.fpoRecyclerView)
        nrmRecyclerView = findViewById(R.id.nrmRecyclerView)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
    }

    private fun dbtSchemesLists() {
        try {
            val api =
                AppInventorApi(
                    this,
                    "https://uat-dbt.mahapocra.gov.in:8026/",
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.revampedDBTSchemes
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
        if (i == 1 && jSONObject != null){

            val farmerDataJSONArray = jSONObject.optJSONArray("farmerData")
            val fpoDataJSONArray = jSONObject.optJSONArray("fpoData")
            val nrmDataJSONArray = jSONObject.optJSONArray("nrmData")

            farmerRecyclerView.layoutManager = LinearLayoutManager(this)
            fpoRecyclerView.layoutManager = LinearLayoutManager(this)
            nrmRecyclerView.layoutManager = LinearLayoutManager(this)

            farmerRecyclerView.adapter = FarmerDBTRecyclerAdapter(farmerDataJSONArray, this)
            fpoRecyclerView.adapter = FarmerDBTRecyclerAdapter(fpoDataJSONArray, this)
            nrmRecyclerView.adapter = FarmerDBTRecyclerAdapter(nrmDataJSONArray, this)
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        val jsonObject = obj as JSONObject
        startActivity(Intent(this@DbtSchemes, DbtSchemesDetailsActivity::class.java).apply {
            putExtra("FARMERDBTRESPONSE", jsonObject.toString())
        })
    }
}
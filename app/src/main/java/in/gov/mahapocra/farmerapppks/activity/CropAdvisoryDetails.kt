package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.CropDetailsAdpter
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

class CropAdvisoryDetails : AppCompatActivity() , ApiJSONObjCallback, ApiCallbackCode,
    OnMultiRecyclerItemClickListener {

    lateinit var textViewHeaderTitle: TextView
    lateinit var textCropName: TextView
    lateinit var imageMenushow: ImageView
    var CropId: String? =null
    var CropNameEn: String? =null
    var CropNameMr: String? =null
    var TalukaId: String? =null
    var cropsap: String? =null
    private var CropDetailsJSONArray: JSONArray? = null
    lateinit var cropArray: JSONArray
    private var cropDetailsGroup: RecyclerView? = null
    var languageToLoad: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_advisory_details)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@CropAdvisoryDetails).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        init()
        onClick()
        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.crop_advisory)

        CropId = intent.getStringExtra("CropId")
        CropNameEn = intent.getStringExtra("CropNameEn")
        CropNameMr = intent.getStringExtra("CropNameMr")
        TalukaId = intent.getStringExtra("TalukaId")
        cropsap = intent.getStringExtra("CropSap")
        Log.d("CropId page 2", CropId.toString())
        Log.d("TalukaId page 2", TalukaId.toString())
        Log.d("cropsap page 2", cropsap.toString())
        if(languageToLoad.equals("en"))
        {
            textCropName.setText("Crop : "+ CropNameEn)
        }
        else
        {
            textCropName.setText("पीक : "+ CropNameMr)
        }

        getAutoCropAdvisory()
    }
    private fun init()
    {
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenushow = findViewById(R.id.imageMenushow)
        cropDetailsGroup = findViewById(R.id.cropDetailsList)
        textCropName = findViewById(R.id.textCropName)

    }
    private fun onClick()
    {
        imageMenushow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        })
    }

    private fun getAutoCropAdvisory() {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("crop_id",CropId)
            jsonObject.put("taluka_id",TalukaId)
            jsonObject.put("cropsap",cropsap.toBoolean())

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                APIServices.SSO,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getAutoCropAdvisory(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    override fun onFailure(th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {

                    CropDetailsJSONArray = response.getdataArray()
                    Log.d("CropDetails","CRAMainGroup=="+"".toString())

                    // if (activityGrpWiseDetailsJSONArray?.length()!! > 0) {
                    val cropDetailsAdpter =
                        CropDetailsAdpter(this, this, CropDetailsJSONArray,cropsap.toBoolean())
                    cropDetailsGroup?.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.VERTICAL,
                            false)
                    )
                    cropDetailsGroup?.setAdapter(cropDetailsAdpter)
                    cropDetailsAdpter.notifyDataSetChanged()
                    // }
                }else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        UIToastMessage.show(this,"no data found ")
    }
}
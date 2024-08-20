package `in`.gov.mahapocra.farmerapppks.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.DashboardAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryDetailAdaptr
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class CropStageAdvisory : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {
    private val currentStep = 0
    private lateinit var hsv : HorizontalScrollView

   lateinit var  cropStages: RecyclerView
   lateinit var  cropStagesDescrption: RecyclerView

    private var cropAdvisoryDetailsJSONArray: JSONArray? = null
    private var cropAdvisoryJSONArray: JSONArray ? = null

    lateinit var textViewHeaderTitle: TextView
    lateinit var imgBackArrow: ImageView
    lateinit var sowingDateTv: TextView
    lateinit var editCropImg: ImageView
    lateinit var relativeLayoutToolbar: RelativeLayout
    lateinit var cropAdvisoryLinearLayout: LinearLayout
    lateinit var comming_soon_LinearLayout: LinearLayout
    lateinit var comming_soon_LinearLayout1: LinearLayout

    var cropId: Int? = 0
    var wotrCropId: String? = "0"
    var cropName: String? = null
    var mUrl: String? =null
    private  var farmerId:Int=0
    private var villageID: Int = 0
    private var sowingDate:String =""

    private var gridView: GridView? = null

    lateinit var languageToLoad: String

    private val arrayCategery = arrayOf(
        "Crop Advisory", "Fertilizer Calculator",  "Crop Management", "Pests and Diseases"
    )

    private val arrayCategeryMarathi = arrayOf(
        "पीक सल्ला", "खत मात्रा गणक (कॅलक्यूलेटर)", "पीक व्यवस्थापन", "कीड व रोग"
    )

    var arrayCategeryImg = intArrayOf(
        R.drawable.crop_advsry,
        R.drawable.fertilizer_calculator,
        R.drawable.pest_management,
        R.drawable.pets_n_disease_img
    )


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CropStageAdvisory).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_crop_stage_advisory)
        init()

        setConfiguration()

        cropId = intent.getIntExtra("id",0)
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        cropName = intent.getStringExtra("mName")
       var dataSavedInLocal = intent.getStringExtra("dataSavedInLocal")
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        if(dataSavedInLocal.equals("dataSavedInLocal") && cropId==0  ){
            cropId =  AppSettings.getInstance().getIntValue(this, AppConstants.tmpCROPID, 0)
            wotrCropId =  AppSettings.getInstance().getValue(this, AppConstants.tmpWOTRID, AppConstants.tmpWOTRID)
            sowingDate = AppSettings.getInstance().getValue(this, AppConstants.tmpSOWINGDATE, AppConstants.tmpSOWINGDATE)
            mUrl =  AppSettings.getInstance().getValue(this, AppConstants.tmpMURL, AppConstants.tmpMURL)
            cropName = AppSettings.getInstance().getValue(this, AppConstants.tmpCROPNAME, AppConstants.tmpCROPNAME)
        }

        if (languageToLoad.equals("en", ignoreCase = true)) {
            gridView!!.adapter =
                DashboardAdapter(
                    this,
                    arrayCategery,
                    arrayCategeryImg,
                    "stage_single_item_grid"
                )
            Log.d("", "")
        } else if (languageToLoad.equals("mr", ignoreCase = true)) {
            gridView!!.adapter =
                DashboardAdapter(
                    this,
                   arrayCategeryMarathi,
                    arrayCategeryImg,
                    "stage_single_item_grid"
                )
        }

        gridView!!.onItemClickListener =
            OnItemClickListener { parent, v, position, id ->
                val txtLable = v.findViewById<View>(R.id.grid_item_label) as TextView
                val card_view = v.findViewById<View>(R.id.card_view) as CardView
                //Toast.makeText(DashboardScreen.this, txtLable.getText(), Toast.LENGTH_SHORT).show();
                Log.d("gridView", "Pos=$position")
                if (position == 0) {
                    cropAdvisoryLinearLayout.visibility = View.VISIBLE
                    comming_soon_LinearLayout.visibility = View.GONE
                   // card_view.setCardBackgroundColor(Color.YELLOW)
                   // card_view.background =resources(R.drawable.text_bg_with_border)
                }else if (position == 1){
                    val intent = Intent(this, FertilizerCalculatorAcitvity::class.java)
                    intent.putExtra("id",cropId)
                    intent.putExtra("mName",cropName)
                    intent.putExtra("wotr_crop_id",wotrCropId)
                    intent.putExtra("sowingDate",sowingDate)
                    startActivity(intent)
                }else if (position == 2){
                    cropAdvisoryLinearLayout.visibility = View.GONE
                    comming_soon_LinearLayout.visibility = View.VISIBLE
                }else if (position == 3){
                    val intent = Intent(this, PestsAndDiseasesStages::class.java)
                    intent.putExtra("cropId",cropId)
                    intent.putExtra("wotr_crop_id",wotrCropId)
                    intent.putExtra("sowingDate",sowingDate)
                    intent.putExtra("mUrl",mUrl)
                    intent.putExtra("mName",cropName)
                    startActivity(intent)
                }

                AppSettings.getInstance().setIntValue(this, AppConstants.tmpCROPID, cropId!!)
                AppSettings.getInstance().setValue(this, AppConstants.tmpWOTRID, wotrCropId)
                AppSettings.getInstance().setValue(this, AppConstants.tmpSOWINGDATE, sowingDate)
                AppSettings.getInstance().setValue(this, AppConstants.tmpMURL, mUrl)
                AppSettings.getInstance().setValue(this, AppConstants.tmpCROPNAME, cropName)
            }


        textViewHeaderTitle.setText(cropName)
        imgBackArrow.visibility = View.VISIBLE
        imgBackArrow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        })

        editCropImg.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SelectSowingDataAndFarmer::class.java)
            intent.putExtra("id", cropId)
            intent.putExtra("mUrl", mUrl)
            intent.putExtra("mName", cropName)
            intent.putExtra("editCrop", "EditCrop")
            startActivity(intent)
        })

        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)

        getCropStagesAndAdvisory()

    }

    private fun setConfiguration() {
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CropStageAdvisory).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@CropStageAdvisory))
            languageToLoad = "en"
        }
    }

    private fun init() {
        cropStages = findViewById<View>(R.id.cropStages) as RecyclerView
        cropStagesDescrption = findViewById<View>(R.id.cropStagesDescrption) as RecyclerView
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imageMenushow)
        sowingDateTv = findViewById(R.id.showingDateTv)
        editCropImg = findViewById(R.id.editCropImg)
        relativeLayoutToolbar = findViewById(R.id.relativeLayoutToolbar)
        cropAdvisoryLinearLayout = findViewById(R.id.cropAdvisoryLinearLayout)
        comming_soon_LinearLayout = findViewById(R.id.comming_soon_LinearLayout)
        comming_soon_LinearLayout1 = findViewById(R.id.comming_soon_LinearLayout1)
        gridView = findViewById<View>(R.id.gridViewJobs) as GridView
        gridView!!.columnWidth = GridView.AUTO_FIT
    }

    private fun getCropStagesAndAdvisory() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("crop_id",cropId)
            jsonObject.put("farmer_id",farmerId)
            jsonObject.put("lang",languageToLoad)


            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCropStagesAndAdvisory(requestBody)
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

    @SuppressLint("SuspiciousIndentation")
    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    sowingDate =jSONObject.getString("sowing_date")
                    sowingDateTv.setText(jSONObject.getString("sowing_date"))

                    cropAdvisoryDetailsJSONArray = response.getdataArray()
                    Log.d("CropAdvisoryDetails","=="+cropAdvisoryDetailsJSONArray+"".toString())

                     if (cropAdvisoryDetailsJSONArray?.length()!! > 0) {
                    val stagesAdvisoryAdapter =
                        StageAdvisoryAdapter(this, this, cropAdvisoryDetailsJSONArray )
                         cropStages?.setLayoutManager(
                        LinearLayoutManager(
                            this,
                            LinearLayoutManager.HORIZONTAL,
                            false)
                    )
                         cropStages?.setAdapter(stagesAdvisoryAdapter)
                         stagesAdvisoryAdapter.notifyDataSetChanged()
                     }
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
        Log.d("i and onj", "==" + i + "" + obj)
        if (i == 1) {
           // var pending : String= "No data"
            val cropDetail: JSONObject = obj as JSONObject
          // pending= ?  cropDetail.getString("pending") : "No data"
            var status = if (cropDetail.getString("status").equals("pending")) "pending" else "No data"
            if (status.equals("pending")){
                comming_soon_LinearLayout1.visibility =View.VISIBLE
                cropStagesDescrption.visibility =View.GONE
            }else {
                comming_soon_LinearLayout1.visibility =View.GONE
                cropStagesDescrption.visibility =View.VISIBLE
                cropAdvisoryJSONArray = cropDetail.getJSONArray("advisory")
                val stageAdvisoryDetailAdaptr =
                    StageAdvisoryDetailAdaptr(
                        this,
                        this,
                        cropAdvisoryJSONArray as JSONArray,
                        languageToLoad,
                        cropId.toString(),
                        villageID.toString()
                    )
                cropStagesDescrption?.setLayoutManager(
                    LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                cropStagesDescrption?.setAdapter(stageAdvisoryDetailAdaptr)
                stageAdvisoryDetailAdaptr.notifyDataSetChanged()
            }
        }
        if (i==2){
            relativeLayoutToolbar.visibility =View.GONE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, CropStageAdvisory::class.java)
        intent.putExtra("dataSavedInLocal","dataSavedInLocal")
        startActivity(intent)
    }




}
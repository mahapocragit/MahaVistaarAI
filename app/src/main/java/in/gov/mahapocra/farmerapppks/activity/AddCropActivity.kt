package `in`.gov.mahapocra.farmerapppks.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.adapter.TitleVideosDetailsAdpter
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.CropsCategName
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import `in`.gov.mahapocra.farmerapppks.models.response.VideoDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit



class AddCropActivity : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private var mainCropCategoryRecycle: RecyclerView? = null
    private lateinit var moviesImagesList: ArrayList<CropsCategName>
    private lateinit var videoDetailsList : ArrayList<VideoDetails>
    private lateinit var languageToLoad: String
    private lateinit var cropJsonArray: JSONArray

    private lateinit var textViewHeaderTitle: TextView
    private lateinit var imageMenuShow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AddCropActivity).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@AddCropActivity))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_add_crop)

        mainCropCategoryRecycle = findViewById(R.id.mainRecyclerView)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenuShow = findViewById(R.id.imageMenushow)
        textViewHeaderTitle.setText(R.string.select_crop)

        imageMenuShow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }
        getCropCategoriesAndCropDetails()
    }

    private fun getCropCategoriesAndCropDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getCropCategorywise(requestBody)
                api.postRequest(responseCall, this@AddCropActivity, 1)
            }
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }

    private fun showCropData(videoDetailsList: ArrayList<VideoDetails>) {
        val titleVideosAdapter =
            TitleVideosDetailsAdpter(
                this,
                videoDetailsList
            )

        mainCropCategoryRecycle?.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        mainCropCategoryRecycle?.setAdapter(titleVideosAdapter)
        titleVideosAdapter.notifyDataSetChanged()
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, k: Int) {
        if (k == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                cropJsonArray = response.getdataArray()
                videoDetailsList = ArrayList()
                val nCropSize : Int = cropJsonArray.length()
                for (i in 0 until nCropSize) {
                    val cropJsonObject:JSONObject = cropJsonArray.get(i) as JSONObject
                    val cropType: String = cropJsonObject.getString("type")
                    val cropCategoriesJsonArray: JSONArray = cropJsonObject.getJSONArray("crops")
                    val nCropsCategory : Int = cropCategoriesJsonArray.length()
                    moviesImagesList = ArrayList()
                    for (j in 0 until nCropsCategory){
                        val cropCategoriesJsonObject:JSONObject = cropCategoriesJsonArray.get(j) as JSONObject
                        val cropsId: Int = cropCategoriesJsonObject.get("id") as Int
                        val cropsName :String = cropCategoriesJsonObject.get("name") as String
                        var cropsImgUrl : String? = ""
                         cropsImgUrl = cropCategoriesJsonObject.get("image").toString()
                        if(cropsImgUrl == null){
                            cropsImgUrl = "https://cdn.pixabay.com/photo/2016/06/11/15/33/broccoli-1450274_640.png"
                        }
                        moviesImagesList.add(CropsCategName(cropsId,cropsName ,cropsImgUrl,"wotrCropsId"))
                    }
                    videoDetailsList.add(VideoDetails(i,cropType,moviesImagesList))
                    Log.d("videoDetailsList11111", videoDetailsList.toString())
                }
                showCropData(videoDetailsList)
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }


}
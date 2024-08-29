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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit



class AddCropActivity : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private var mainCropCategoryRecycle: RecyclerView? = null
    lateinit var moviesImagesList: ArrayList<CropsCategName>
    lateinit var bannerMoviesList: ArrayList<CropsCategName>
    lateinit var videiDetailsList : ArrayList<VideoDetails>
    lateinit var languageToLoad: String
    lateinit var cropJsonArray: JSONArray

    lateinit var textViewHeaderTitle: TextView
    lateinit var imageMenushow: ImageView

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
        imageMenushow = findViewById(R.id.imageMenushow)

        //imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.select_crop)

        imageMenushow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        })

        getCropCategoriesAndCropDetails()



//        moviesImagesList = ArrayList<CropsCategName>()
//        moviesImagesList.add(
//            CropsCategName(
//                1,
//                "Beetroot",
//                "https://static.vecteezy.com/system/resources/previews/022/619/812/original/one-continuous-line-drawing-of-whole-healthy-organic-red-radish-for-plantation-logo-identity-fresh-veggie-concept-for-edible-root-vegetable-icon-modern-single-line-draw-design-vector-illustration-png.png",
//
//            )
//        )
//        moviesImagesList.add(
//            CropsCategName(
//                2,
//                "Broccoli",
//                "https://cdn.pixabay.com/photo/2016/06/11/15/33/broccoli-1450274_640.png",
//
//            )
//        )
//        moviesImagesList.add(
//            CropsCategName(
//                3,
//                "Chili",
//                "https://cdn.pixabay.com/photo/2014/04/02/16/17/chili-306810_960_720.png",
//
//            )
//        )
//        moviesImagesList.add(
//            CropsCategName(
//                4,
//                "Eggplant",
//                "https://www.picng.com/upload/eggplant/png_eggplant_17822.png",
//
//            )
//        )
//
//
//        videiDetailsList = ArrayList<VideoDetails>()
//        videiDetailsList.add(VideoDetails(1,"Commercial Crops",moviesImagesList))
//        videiDetailsList.add(VideoDetails(2,"Food Crops",moviesImagesList))
//        videiDetailsList.add(VideoDetails(3,"Vegetable Crops",moviesImagesList))
//        videiDetailsList.add(VideoDetails(4,"Pulse and Cereal Crops",moviesImagesList))
//        videiDetailsList.add(VideoDetails(5,"Oil Seed Crops",moviesImagesList))
//        videiDetailsList.add(VideoDetails(6,"Fruit Crops",moviesImagesList))
//        videiDetailsList.add(VideoDetails(7,"Spices",moviesImagesList))
//
//        val titleVideosApdapter =
//            TitleVideosDetailsAdpter(
//                this,
//                videiDetailsList
//            )
//
//        mainCropCategoryRecycle?.setLayoutManager(
//            LinearLayoutManager(
//                this,
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//        )
//        mainCropCategoryRecycle?.setAdapter(titleVideosApdapter)
//        titleVideosApdapter.notifyDataSetChanged()


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
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCropCategorywise(requestBody)
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

    private fun showCropData(videiDetailsList: ArrayList<VideoDetails>) {
        val titleVideosApdapter =
            TitleVideosDetailsAdpter(
                this,
                videiDetailsList
            )

        mainCropCategoryRecycle?.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        mainCropCategoryRecycle?.setAdapter(titleVideosApdapter)
        titleVideosApdapter.notifyDataSetChanged()
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                cropJsonArray = response.getdataArray()
                videiDetailsList = ArrayList<VideoDetails>()
                val nCropSize : Int? = cropJsonArray.length()
                for (i in 0 until nCropSize!!) {
                    val cropJsonObject:JSONObject = cropJsonArray.get(i) as JSONObject
                    var cropType: String = cropJsonObject.getString("type")
                    var cropCategoriesJsonArray: JSONArray = cropJsonObject.getJSONArray("crops")
                    val nCropsCategory : Int = cropCategoriesJsonArray.length()
                    moviesImagesList = ArrayList<CropsCategName>()
                    for (j in 0 until nCropsCategory!!){
                        val cropCategoriesJsonObject:JSONObject = cropCategoriesJsonArray.get(j) as JSONObject
                        var cropsId: Int = cropCategoriesJsonObject.get("id") as Int
                      //  var wotrCropsId: String = cropCategoriesJsonObject.get("wotr_crop_id") as String
                        var cropsName :String = cropCategoriesJsonObject.get("name") as String
                        var cropsImgUrl : String? = ""
                         cropsImgUrl = cropCategoriesJsonObject.get("image").toString()
                        if(cropsImgUrl == null){
                            cropsImgUrl = "https://cdn.pixabay.com/photo/2016/06/11/15/33/broccoli-1450274_640.png"
                        }

                        moviesImagesList.add(CropsCategName(cropsId,cropsName ,cropsImgUrl,"wotrCropsId"))
                    }
                    videiDetailsList.add(VideoDetails(i,cropType,moviesImagesList))
                    Log.d("videiDetailsList11111", videiDetailsList.toString())
                }
                showCropData(videiDetailsList)


            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }


}
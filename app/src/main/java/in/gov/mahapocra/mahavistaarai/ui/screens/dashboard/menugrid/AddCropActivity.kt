package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.data.model.VideoDetails
import `in`.gov.mahapocra.mahavistaarai.ui.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.TitleVideosDetailsAdapter
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.util.Date
import java.util.Locale


class AddCropActivity : AppCompatActivity(), ApiCallbackCode, OnMultiRecyclerItemClickListener,
    DatePickerRequestListener {

    private var mainCropCategoryRecycle: RecyclerView? = null
    private lateinit var moviesImagesList: ArrayList<CropsCategName>
    private lateinit var videoDetailsList: ArrayList<VideoDetails>
    private lateinit var languageToLoad: String
    private lateinit var viewModel: FarmerViewModel
    private lateinit var cropJsonArray: JSONArray
    private lateinit var textViewHeaderTitle: TextView
    private lateinit var imageMenuShow: ImageView
    private lateinit var imgBackArrow: ImageView
    private var sowingDate: String = ""
    private lateinit var receivedJson: JSONObject
    private var cropId: Int = 0
    private var date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AddCropActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_add_crop)


        viewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        mainCropCategoryRecycle = findViewById(R.id.mainRecyclerView)
        imgBackArrow = findViewById(R.id.imgBackArrow)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenuShow = findViewById(R.id.imageMenushow)
        textViewHeaderTitle.setText(R.string.select_crop)

        if (languageToLoad != "mr") {
            textViewHeaderTitle.text = "Select Crop"
        } else {
            textViewHeaderTitle.text = "पीक निवडा"
        }

        imageMenuShow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }

        imgBackArrow.visibility = View.VISIBLE
        imgBackArrow.setOnClickListener {
            onBackPressed()
        }
        getCropCategoriesAndCropDetails()
    }

    private fun getCropCategoriesAndCropDetails() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api_key", "67840097657891")
            jsonObject.put("lang", languageToLoad)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.FARMER,
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
        var titleVideosAdapter =
            TitleVideosDetailsAdapter(
                this,
                videoDetailsList,
                "TitleVideosDetailsAdpter",
                this
            )
        val str = intent.getStringExtra("NO_NEED_TO_ADD_SOWING_DATE")
        if (str != null) {
            titleVideosAdapter =
                TitleVideosDetailsAdapter(
                    this,
                    videoDetailsList,
                    "NO_NEED_TO_ADD_SOWING_DATE",
                    this
                )
        }
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
            val response =
                ResponseModel(
                    jSONObject
                )

            if (response.status) {
                cropJsonArray = response.getdataArray()
                videoDetailsList = ArrayList()
                val nCropSize: Int = cropJsonArray.length()
                for (i in 0 until nCropSize) {
                    val cropJsonObject: JSONObject = cropJsonArray.get(i) as JSONObject

                    val cropType: String = cropJsonObject.getString("type")
                    val cropCategoriesJsonArray: JSONArray = cropJsonObject.getJSONArray("crops")
                    val nCropsCategory: Int = cropCategoriesJsonArray.length()
                    moviesImagesList = ArrayList()
                    for (j in 0 until nCropsCategory) {
                        val cropCategoriesJsonObject: JSONObject =
                            cropCategoriesJsonArray.get(j) as JSONObject
                        val cropsId: Int = cropCategoriesJsonObject.get("id") as Int
                        val cropsName: String = cropCategoriesJsonObject.get("name") as String
                        val wotrCropId: String =
                            cropCategoriesJsonObject.get("wotr_crop_id").toString()
                        var cropsImgUrl: String? = ""
                        cropsImgUrl = cropCategoriesJsonObject.get("image").toString()
                        val sowingDateUnfiltered = cropCategoriesJsonObject.optString("sowing_date")
                        val advisoryPdfUrl = cropCategoriesJsonObject.optString("advisory_pdf_url")
                        AppPreferenceManager(this).saveString("advisoryPdfUrl", advisoryPdfUrl)
                        val filteredSowingDate =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getSowingDateWithYear(sowingDateUnfiltered)
                            } else {
                                Log.d("TAG", "onResponse: versioncode is lesser")
                            }
                        if (cropsImgUrl == null) {
                            cropsImgUrl =
                                "https://cdn.pixabay.com/photo/2016/06/11/15/33/broccoli-1450274_640.png"
                        }
                        moviesImagesList.add(
                            CropsCategName(
                                cropsId,
                                cropsName,
                                cropsImgUrl,
                                wotrCropId,
                                filteredSowingDate.toString()
                            )
                        )
                    }
                    videoDetailsList.add(
                        VideoDetails(
                            i,
                            cropType,
                            moviesImagesList
                        )
                    )
                }
                showCropData(videoDetailsList)
            } else {
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSowingDateWithYear(sowingDateUnfiltered: String): String {
        val currentDate = getCurrentDate() // Returns YYYY-MM-DD
        val currentYear = Year.now().value

        // Parse and format the sowing date
        val sowingDateParts = sowingDateUnfiltered.split("/")
        val formattedSowingDate = sowingDateParts.takeIf { it.size == 2 }?.let {
            "%04d-%02d-%02d".format(currentYear, it[1].toInt(), it[0].toInt())
        } ?: return currentDate // Return current date if format is invalid

        val sowingDate = LocalDate.parse(formattedSowingDate)
        val today = LocalDate.parse(currentDate)

        val finalDate = if (today.isBefore(sowingDate)) {
            "${formattedSowingDate.substring(8)}-${
                formattedSowingDate.substring(
                    5,
                    7
                )
            }-${currentYear - 1}"
        } else {
            "${today.dayOfMonth.toString().padStart(2, '0')}-${
                today.monthValue.toString().padStart(2, '0')
            }-${today.year}"
        }

        return finalDate
    }


    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            receivedJson = obj as JSONObject
            AppUtility.getInstance().showDisabledFutureDatePicker(
                this,
                date,
                1,
                this
            )
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            sowingDate = "$day-$month-$year"
            cropId = receivedJson.optInt("id")
            viewModel.saveFarmerSelectedCrop(this, sowingDate, cropId)
            viewModel.saveFarmerSelectedCrop.observe(this) {
                if (it != null) {
                    if (it.get("status").toString() == "200") {
                        startActivity(Intent(this, DashboardScreen::class.java).apply {
                            putExtra("helloCrop", cropId)
                        })
                    }
                }
            }
        }
    }

}
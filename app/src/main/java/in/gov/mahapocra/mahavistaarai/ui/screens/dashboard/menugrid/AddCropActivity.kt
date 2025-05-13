package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Context
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
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.data.model.VideoDetails
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CropCategoriesAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.util.Date
import java.util.Locale


class AddCropActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener,
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
        switchLanguage(this, languageToLoad)
        setContentView(R.layout.activity_add_crop)


        viewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        mainCropCategoryRecycle = findViewById(R.id.mainRecyclerView)
        imgBackArrow = findViewById(R.id.imgBackArrow)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenuShow = findViewById(R.id.imageMenushow)
        textViewHeaderTitle.setText(R.string.select_crop)

        fetchCropInfo()

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
        viewModel.getCropCategoriesAndCropDetails(this, languageToLoad)
    }

    private fun fetchCropInfo() {
        viewModel.cropCategoryResponse.observe(this){
            val jSONObject = JSONObject(it.toString())
            val jsonDataArray = jSONObject.getJSONArray("data")
            var adapter = CropCategoriesAdapter(this, jsonDataArray, "TitleVideosDetailsAdpter", this)
            val str = intent.getStringExtra("NO_NEED_TO_ADD_SOWING_DATE")
            if (str != null) {
                adapter =
                    CropCategoriesAdapter(
                        this,
                        jsonDataArray,
                        "NO_NEED_TO_ADD_SOWING_DATE",
                        this
                    )
            }
            mainCropCategoryRecycle?.layoutManager = LinearLayoutManager(this)
            mainCropCategoryRecycle?.hasFixedSize()
            mainCropCategoryRecycle?.setAdapter(adapter)
        }
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

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }

}
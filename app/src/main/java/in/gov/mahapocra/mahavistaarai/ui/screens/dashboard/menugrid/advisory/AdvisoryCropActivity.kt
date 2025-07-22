package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAdvisoryCropBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.StageAdvisoryAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.StageAdvisoryDetailAdaptr
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class AdvisoryCropActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener, DatePickerRequestListener {

    private lateinit var binding: ActivityAdvisoryCropBinding
    private lateinit var viewModel: FarmerViewModel
    private var cropAdvisoryDetailsJSONArray: JSONArray? = null
    private var cropAdvisoryJSONArray: JSONArray? = null
    var cropId: Int? = 0
    private var cropName: String? = null
    private var farmerId: Int = 0
    private var villageID: Int = 0
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private lateinit var languageToLoad: String
    private var sowingDate: String = ""
    private var route: String = ""
    private val date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setting up values for language
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AdvisoryCropActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityAdvisoryCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[FarmerViewModel::class.java]

        binding.completedLabelTextView.text = getString(R.string.crop_stage_completed)
        binding.currentLabelTextView.text = getString(R.string.crop_stage_current)
        binding.pendingLabelTextView.text = getString(R.string.crop_stage_pending)

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD
            )
            startActivity(sharing)
        }
        binding.sowingInfoLayout.textView7.text = getString(R.string.sowing_date)

        //fetching values
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        sowingDate = intent.getStringExtra("sowingDate").toString()
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        route = intent.getStringExtra("ROUTE").toString()
        if (cropId == 0) {
            cropId = AppPreferenceManager(this).getInt("CROP_ID_SAVED")
            cropName = AppPreferenceManager(this).getString("CROP_NAME_SAVED")
            mUrl = AppPreferenceManager(this).getString("CROP_IMAGE_SAVED")
            sowingDate = AppPreferenceManager(this).getString("CROP_SOWING_DATE_SAVED").toString()
            wotrCropId = AppPreferenceManager(this).getString("CROP_WOTR_ID_SAVED")
        }
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)

        binding.sowingInfoLayout.editSowingDateIcon.setOnClickListener {
            AppUtility.getInstance().showDisabledFutureDatePicker(
                this,
                date,
                1,
                this
            )
        }

        binding.sowingInfoLayout.cropNameTextView.text = cropName
        binding.sowingInfoLayout.sowingDateTextView.text = sowingDate
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.crop_advisory)
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        observeCropStagesAndAdvisory()
        if (route!=""){
            val savedCropId = AppPreferenceManager(this).getInt("CROP_ID_SAVED")
            val savedCropSowingDate = AppPreferenceManager(this).getString("CROP_SOWING_DATE_SAVED")
            val savedCropName = AppPreferenceManager(this).getString("CROP_NAME_SAVED")
            savedCropSowingDate?.let { viewModel.getCropStagesAndAdvisory(this, savedCropId, it, languageToLoad) }
        }else{
            viewModel.getCropStagesAndAdvisory(this, cropId, sowingDate, languageToLoad)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardScreen::class.java))
    }

    private fun observeCropStagesAndAdvisory() {
        ProgressHelper.showProgressDialog(this)
        viewModel.getCropStagesAndAdvisoryResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {
                    if (jSONObject.getString("sowing_date").isNotEmpty()) {
                        binding.sowingInfoLayout.sowingDateTextView.text =
                            jSONObject.getString("sowing_date")
                    }
                    cropAdvisoryDetailsJSONArray = response.getdataArray()
                    if (cropAdvisoryDetailsJSONArray?.length()!! > 0) {
                        val stagesAdvisoryAdapter =
                            StageAdvisoryAdapter(this, this, cropAdvisoryDetailsJSONArray)
                        binding.cropStagesRecyclerView.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.cropStagesRecyclerView.adapter = stagesAdvisoryAdapter
                        val currentStagePos = stagesAdvisoryAdapter.getCurrentStagePosition()
                        if (currentStagePos != -1) {
                            binding.cropStagesRecyclerView.post {
                                binding.cropStagesRecyclerView.scrollToPosition(currentStagePos)
                            }
                        }
                        stagesAdvisoryAdapter.notifyDataSetChanged()
                    }
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
        viewModel.error.observe(this){
            ProgressHelper.disableProgressDialog()
            UIToastMessage.show(this, "Unable to fetch data")
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            val cropDetail: JSONObject = obj as JSONObject
            cropAdvisoryJSONArray = cropDetail.getJSONArray("advisory")
            if (cropAdvisoryJSONArray?.length() == 0) {
                Toast.makeText(
                    this,
                    "Advisory is not available for current stage",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val stageAdvisoryDetailAdapter = StageAdvisoryDetailAdaptr(
                this,
                this,
                cropAdvisoryJSONArray as JSONArray,
                languageToLoad,
                cropId.toString(),
                villageID.toString()
            )
            stageAdvisoryDetailAdapter.notifyDataSetChanged()
        }
        if (i == 2) {
            binding.relativeLayoutTopBar.relativeLayoutToolbar.visibility = View.GONE
        }
    }

    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            sowingDate = "$day-$month-$year"
            cropId?.let { viewModel.saveFarmerSelectedCrop(this, sowingDate, it) }
            viewModel.saveFarmerSelectedCrop.observe(this) {
                if (it != null) {
                    if (it.get("status").toString() == "200") {
                        viewModel.getCropStagesAndAdvisory(this, cropId, sowingDate, languageToLoad)
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
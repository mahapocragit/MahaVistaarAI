package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPestsAndDiseasesLibraryBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.PestAndDiseasesAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONArray
import org.json.JSONObject

class PestsAndDiseasesStages : AppCompatActivity() {

    private lateinit var binding: ActivityPestsAndDiseasesLibraryBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    var cropId: Int? = 0
    private var stagesId: Int = 0
    private var mUrl: String? = null
    lateinit var languageToLoad: String
    private var cropName: String? = null
    private var wotrCropId: String? = null
    private lateinit var stageJsonArray: JSONArray
    private var particularStagesDiseases: String = "AllStagesDiseases"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PestsAndDiseasesStages).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityPestsAndDiseasesLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root, window)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.pests_n_diseases)
        binding.sowingInfoLayout.textView7.text = getString(R.string.selected_crop)
        binding.tvAgroMetAdvisory.text = getString(R.string.different_stages_text)

        binding.sowingInfoLayout.sowingDateTextView.visibility = View.GONE
        binding.sowingInfoLayout.editSowingDateIcon.visibility = View.GONE

        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            startActivity(Intent(this@PestsAndDiseasesStages, DashboardScreen::class.java))
        }

        cropId = intent.getIntExtra("cropId", 0)
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        cropName = intent.getStringExtra("mName")
        particularStagesDiseases = intent.getStringExtra("ParticularStagesDiseases").toString()
        stagesId = intent.getIntExtra("id", 0)
        if (cropId == 0) {
            cropId = AppPreferenceManager(this).getInt("CROP_ID_SAVED")
            cropName = AppPreferenceManager(this).getString("CROP_NAME_SAVED")
            mUrl = AppPreferenceManager(this).getString("CROP_IMAGE_SAVED")
            wotrCropId = AppPreferenceManager(this).getString("CROP_WOTR_ID_SAVED")
        }
        AppSettings.getInstance()
            .setValue(this, AppConstants.tmpCROPNAME, cropName)

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", mUrl)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.PEST_AND_DISEASES_STAGES
            )
            startActivity(sharing)
        }

        if (cropName.isNullOrBlank()) {
            cropName = AppSettings.getInstance()
                .getValue(this, AppConstants.tmpCROPNAME, AppConstants.tmpCROPNAME)
        }

        if (cropId!! > 0) {
            observeCropStages()
            farmerViewModel.getCropStages(this, cropId, languageToLoad)
        } else {
            Toast.makeText(this, "No Crops Added", Toast.LENGTH_SHORT).show()
        }
        binding.sowingInfoLayout.cropNameTextView.text = "$cropName"
        val isGuest = AppSettings.getInstance().getBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        binding.chatbotIcon.setOnClickListener {
            if (!isGuest) {
                startActivity(Intent(this, ChatbotActivity::class.java))
            } else {
                AlertDialog.Builder(this)
                    .setMessage(R.string.bot_chat_login_redirect_mesage)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        // Handle login action here
                        startActivity(Intent(this, LoginScreen::class.java).apply {
                            putExtra("from", "dashboard")
                        })
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun observeCropStages() {
        ProgressHelper.showProgressDialog(this)
        farmerViewModel.getCropStagesResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    stageJsonArray = response.getdataArray()
                    val adapter = PestAndDiseasesAdapter(this, stageJsonArray)
                    binding.diseasesByStage.layoutManager = LinearLayoutManager(this)
                    binding.diseasesByStage.adapter = adapter
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
        farmerViewModel.error.observe(this){
            ProgressHelper.disableProgressDialog()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@PestsAndDiseasesStages, DashboardScreen::class.java))
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
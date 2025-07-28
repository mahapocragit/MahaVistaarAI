package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivitySopactivityBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.SOPAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONObject

class SOPActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivitySopactivityBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    var cropId: Int? = 0
    private var cropName: String? = null
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String = ""
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@SOPActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivitySopactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        //fetching values
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        sowingDate = intent.getStringExtra("sowingDate").toString()
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")

        if (cropId == 0) {
            cropId = AppPreferenceManager(this).getInt("CROP_ID_SAVED")
            cropName = AppPreferenceManager(this).getString("CROP_NAME_SAVED")
            mUrl = AppPreferenceManager(this).getString("CROP_IMAGE_SAVED")
            sowingDate = AppPreferenceManager(this).getString("CROP_SOWING_DATE_SAVED").toString()
            wotrCropId = AppPreferenceManager(this).getString("CROP_WOTR_ID_SAVED")
        }

        binding.sowingInfoLayout.sowingDateTextView.visibility = View.GONE
        binding.sowingInfoLayout.editSowingDateIcon.visibility = View.GONE
        binding.sowingInfoLayout.textView7.text = getString(R.string.selected_crop)
        binding.sowingInfoLayout.cropNameTextView.text = cropName

        cropId?.let { farmerViewModel.fetchSOPDate(this, it) }
        ProgressHelper.showProgressDialog(this)

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", mUrl)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.SOP_FROM_DASHBOARD
            )
            startActivity(sharing)
        }

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DashboardScreen::class.java
                )
            )
        }
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.sop_title)

        farmerViewModel.sopResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val dataArray = jsonObject.getJSONArray("data")
                if (dataArray.length() != 0) {
                    binding.sopRecyclerView.visibility = View.VISIBLE
                    binding.noDataFoundImageView.visibility = View.GONE
                    binding.sopTextView.visibility = View.GONE
                    binding.sopRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.sopRecyclerView.adapter = SOPAdapter(this, this, dataArray)
                } else {
                    binding.sopRecyclerView.visibility = View.GONE
                    binding.noDataFoundImageView.visibility = View.VISIBLE
                    binding.sopTextView.visibility = View.VISIBLE
                }
            }
        }

        farmerViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
            binding.sopRecyclerView.visibility = View.GONE
            binding.noDataFoundImageView.visibility = View.VISIBLE
            binding.sopTextView.visibility = View.VISIBLE
        }
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


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardScreen::class.java))
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

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 2) {
            val jsonObject = obj as JSONObject
            val webViewUrl = jsonObject.optString("url")
            val intent = Intent(this, SOPWebViewActivity::class.java)
            val b = Bundle()
            b.putSerializable("webUrl", webViewUrl)
            intent.putExtras(b)
            startActivity(intent)
        }
    }
}
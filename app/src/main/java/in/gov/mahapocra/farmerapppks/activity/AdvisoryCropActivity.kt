package `in`.gov.mahapocra.farmerapppks.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.StageAdvisoryDetailAdaptr
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityAdvisoryCropBinding
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONObject

class AdvisoryCropActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener {

    private lateinit var binding: ActivityAdvisoryCropBinding
    private var cropAdvisoryDetailsJSONArray: JSONArray? = null
    private var cropAdvisoryJSONArray: JSONArray? = null
    var cropId: Int? = 0
    private var cropName: String? = null
    private var farmerId: Int = 0
    private var villageID: Int = 0
    lateinit var languageToLoad: String
    private var sowingDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvisoryCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting up values for language
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AdvisoryCropActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }

        //fetching values
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        val cropStageResponseString = AppPreferenceManager(this).getString("CROP_STAGE_RESPONSE")
        val jSONObject = JSONObject(cropStageResponseString)
        val response = ResponseModel(jSONObject)
        if (response.status) {
            sowingDate = jSONObject.getString("sowing_date")
            binding.sowingInfoLayout.sowingDateTextView.text = jSONObject.getString("sowing_date")
            cropAdvisoryDetailsJSONArray = response.getdataArray()
            Log.d("RESPONSE_TAG", "onResponse: $cropAdvisoryDetailsJSONArray")

            if (cropAdvisoryDetailsJSONArray?.length()!! > 0) {
                val stagesAdvisoryAdapter =
                    StageAdvisoryAdapter(this, this, cropAdvisoryDetailsJSONArray)
                binding.cropStagesRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.cropStagesRecyclerView.adapter = stagesAdvisoryAdapter
                stagesAdvisoryAdapter.notifyDataSetChanged()
            }
        } else {
            UIToastMessage.show(this, response.response)
        }

        binding.sowingInfoLayout.cropNameTextView.text = cropName
        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "Crop Advisory"
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
}
package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityHealthCardBinding

class HealthCardActivity : AppCompatActivity() {

    var languageToLoad: String? = null
    private lateinit var binding: ActivityHealthCardBinding
    private lateinit var districtName: String
    private var districtID: Int = 0
    private lateinit var talukaName: String
    private var talukaID: Int = 0
    private lateinit var villageName: String
    private var villageID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@HealthCardActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }

        //Loading URL in webView
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        districtName =
            AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
        talukaName =
            AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
        villageName = AppSettings.getInstance()
            .getValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)

        districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
        talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

        binding.textViewDist.text = districtName
        binding.textViewTaluka.text = talukaName
        binding.textViewVillage.text = villageName
        binding.submitButton.setOnClickListener {
            val surveyNo = binding.edtSurveyNo.text.toString() // Move inside OnClickListener
            if (villageID != null) {
                if (surveyNo.isNotEmpty()) {

                } else {
                    Toast.makeText(this, "Please select survey number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select village", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
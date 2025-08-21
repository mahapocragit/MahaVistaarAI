package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityHealthCardBinding
import `in`.gov.mahapocra.mahavistaarai.graph_ql.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit

class HealthCardActivity : AppCompatActivity() {

    private lateinit var languageToLoad: String
    private lateinit var binding: ActivityHealthCardBinding
    private var bearerTokenString: String = ""
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@HealthCardActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityHealthCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        //Loading URL in webView
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        fetchHealthCardDetails()
        binding.relativeLayoutToolbar.textViewHeaderTitle.text =
            getString(R.string.soil_health_card)
        binding.relativeLayoutToolbar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutToolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewModel.soilHealthResponse.observe(this) { graphQlResponse ->
            ProgressHelper.disableProgressDialog()
            if (graphQlResponse == null) {
                return@observe
            }
            val jSONObject = JSONObject(graphQlResponse.toString())
            val dataObject = jSONObject.optJSONObject("data")
            val healthCardArray = dataObject?.optJSONArray("getTestForAuthUser")
            val soilHealthCardAdapter = SoilHealthCardAdapter(healthCardArray)
            binding.farmerRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.farmerRecyclerView.adapter = soilHealthCardAdapter
        }
        binding.submitButton.setOnClickListener {
            ProgressHelper.showProgressDialog(this)
            viewModel.soilHealth("+919356738043", bearerTokenString)
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

    fun fetchHealthCardDetails() {
        ProgressHelper.showProgressDialog(this)
        viewModel.graphQLResponse.observe(this) { graphQlResponse ->
            ProgressHelper.disableProgressDialog()
            if (graphQlResponse == null) {
                return@observe
            }
            val jSONObject = JSONObject(graphQlResponse.toString())
            val dataObject = jSONObject.optJSONObject("data")
            val generateAccessTokenObject = dataObject?.optJSONObject("generateAccessToken")
            bearerTokenString = generateAccessTokenObject?.optString("token").toString()
            val token = generateAccessTokenObject?.optString("token")
            val refreshToken = generateAccessTokenObject?.optString("refreshToken")
            Log.d("TAGGER", "onCreate: $token \nand $refreshToken")
        }
        viewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
            it?.let { msg -> Log.e("GraphQL", msg) }
        }
        viewModel.fetchAccessToken()
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.helpers.FirebaseHelper
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityRegistrationBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.splash.SplashScreenActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.RegistrationViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.isStrongPassword
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.toSHA512
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.SessionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Registration : AppCompatActivity(), AlertListEventListener {
    private lateinit var binding: ActivityRegistrationBinding
    private val registrationViewModel: RegistrationViewModel by viewModels()
    private var isUserLoggedIn: Boolean = false
    private lateinit var userName: String
    private lateinit var mob: String
    private lateinit var districtName: String
    private var districtID: Int = 0
    private lateinit var talukaName: String
    private var talukaID: Int = 0
    private lateinit var villageName: String
    private var villageID: Int = 0
    private var fAAPRegistrationID: String = ""
    private var sessionManager: SessionManager? = null

    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var villageJSONArray: JSONArray? = null
    private lateinit var languageToLoad: String
    private var versionName: String? = null
    private var token: String? = null
    private var machineId: String? = null
    private val farmerViewModel: FarmerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Registration)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        userName = intent.getStringExtra("name").toString()
        mob = intent.getStringExtra("mobile").toString()

        versionName = LocalCustom.getVersionName(this)
        FirebaseHelper(this).getFCMToken { fcmToken->
            token = fcmToken
        }
        sessionManager = SessionManager(this)
        setConfiguration()
        onclick()
        observeResponse()
    }

    private fun observeResponse() {
        farmerViewModel.talukaList.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response = ResponseModel(jSONObject)
                if (response.status) {
                    talukaJSONArray = response.getdataArray()
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }

        farmerViewModel.districtIdResponse.observe(this) {
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {
                    districtJSONArray = response.getdataArray()
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
        farmerViewModel.updateFCMTokenResponse.observe(this) {
            Log.d("TAGGER", "logoutFromApp: $it")
            if (it != null) {
                val jsonObject = JSONObject(it.toString())
                val response = jsonObject.optString("response")
                if (response == "FCM Cleared") {
                    AppSettings.getInstance().setValue(this, AppConstants.uName, AppConstants.uName)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uEmail, AppConstants.uEmail)
                    AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, 0)
                    AppSettings.getInstance().setValue(this, AppConstants.uDIST, AppConstants.uDIST)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, 0)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, 0)
                    AppSettings.getInstance()
                        .setValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uVILLAGEID, 0)
                    AppSettings.getInstance().setList(this, AppConstants.kFarmerCrop, null)
                    AppUtility.getInstance().clearAppSharedPrefData(this, AppConstants.kSHARED_PREF)
                    AppSettings.getInstance()
                        .setBooleanValue(this, AppConstants.userDataSaved, false)
                    val intent = Intent(this@Registration, SplashScreenActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("TAGGER", "logoutFromApp: $response")
                }
            }
        }

        registrationViewModel.getRegistrationResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    var intent = Intent(this, LoginScreen::class.java)
                    if (isUserLoggedIn) {
                        intent = Intent(this, DashboardScreen::class.java)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val message: String = jSONObject.getString("Message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        registrationViewModel.getVillageListResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject =
                    JSONObject(response.toString())
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.status) {
                    villageJSONArray = response.getdataArray()
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    private fun setConfiguration() {
        binding.textView5.text = getString(R.string.register_text_1)
        binding.textView6.text = getString(R.string.register_text_2)
        farmerViewModel.getDistrictData(this, languageToLoad)
    }

    @JvmName("getMachineId1")
    fun getMachineId(): String? {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun onclick() {
        binding.backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            machineId = getMachineId()
            isUserLoggedIn = false
            userValidationAndRegistration()
        }

        binding.textViewDist.setOnClickListener {
            showDistrict()
        }

        binding.textViewTaluka.setOnClickListener {
            showTaluka()
        }

        binding.textViewVillage.setOnClickListener {
            showVillage()
        }
    }


    private fun showTaluka() {
        if (talukaJSONArray == null) {
            if (districtID > 0) {
                farmerViewModel.fetchTalukaMasterData(this, languageToLoad)
            } else {
                UIToastMessage.show(
                    this,
                    resources.getString(R.string.error_farmer_select_district)
                )
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    talukaJSONArray,
                    2,
                    getString(R.string.farmer_select_taluka),
                    "name",
                    "code",
                    this,
                    this
                )
        }
    }

    private fun showVillage() {
        if (villageJSONArray == null) {
            if (talukaID > 0) {
                registrationViewModel.getVillageList(this, languageToLoad, talukaID)
            } else {
                UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(
                    villageJSONArray,
                    3,
                    getString(R.string.farmer_select_village),
                    "name",
                    "code",
                    this,
                    this
                )
        }
    }

    private fun showDistrict() {
        if (districtJSONArray == null) {
            farmerViewModel.getDistrictData(this, languageToLoad)
        } else {
            AppUtility.getInstance().showListDialogIndex(
                districtJSONArray,
                1,
                getString(R.string.farmer_select_district),
                "name",
                "code",
                this,
                this
            )
        }
    }

    private fun userValidationAndRegistration() {

        if (districtID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_district))
        } else if (talukaID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
        } else if (villageID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_village))
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("Name", userName)
//                jsonObject.put("EmailId", "")
                jsonObject.put("DistrictName", districtName)
                jsonObject.put("DistrictCode", districtID)
                jsonObject.put("TalukaName", talukaName)
                jsonObject.put("TalukaCode", talukaID)
                jsonObject.put("VillageName", villageName)
                jsonObject.put("VillageCode", villageID)
                jsonObject.put("Status", "Active")
                jsonObject.put("version_number", versionName)
                jsonObject.put("fcm_token", token)
                jsonObject.put("device_id", machineId)
                jsonObject.put("FAAPRegistrationID", fAAPRegistrationID)
//                jsonObject.put("Password", "")
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                registrationViewModel.getRegistrationRequest(
                    this, mob.trim { it <= ' ' },
                    mob.trim { it <= ' ' }, jsonObject
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.d("TAGGER", "userValidationAndRegistration: $jsonObject")
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {

        if (i == 1) {
            if (s1 != null) {
                districtID = s1.toInt()
            }

            if (s != null) {
                districtName = s
            }
            binding.textViewDist.text = s
            if (districtID > 0) {
                AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, districtID)
                farmerViewModel.fetchTalukaMasterData(this, languageToLoad)
            }
            talukaID = 0
            binding.textViewTaluka.text = ""
            binding.textViewTaluka.hint = resources.getString(R.string.farmer_select_taluka)
            binding.textViewTaluka.setHintTextColor(Color.GRAY)

            villageID = 0
            binding.textViewVillage.text = ""
            binding.textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            binding.textViewVillage.setHintTextColor(Color.GRAY)
        }


        if (i == 2) {
            if (s1 != "") {
                talukaID = s1!!.toInt()
            }
            if (s != null) {
                talukaName = s
            }
            binding.textViewTaluka.text = s
            villageJSONArray = null
            if (talukaID > 0) {
                registrationViewModel.getVillageList(this, languageToLoad, talukaID)
            }
            villageID = 0
            binding.textViewVillage.text = ""
            binding.textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            binding.textViewVillage.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            if (s1 != "") {
                villageID = s1!!.toInt()
            }

            villageName = s.toString()
            binding.textViewVillage.text = s
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
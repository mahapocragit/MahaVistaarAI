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
import com.google.firebase.messaging.FirebaseMessaging
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiConstants
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityProfileScreenBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.splash.SplashScreenActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.RegistrationViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.toSHA512
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.NetworkUtils
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.SessionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ProfileScreen : AppCompatActivity(), AlertListEventListener {
    private lateinit var binding: ActivityProfileScreenBinding
    private val registrationViewModel: RegistrationViewModel by viewModels()
    private var isUserLoggedIn: Boolean = false
    private var consentMessage: String? = null
    private lateinit var userName: String
    private lateinit var mob: String
    private lateinit var registerMob: String
    private lateinit var pass: String
    private lateinit var confirmPass: String
    private lateinit var emailid: String
    private lateinit var districtName: String
    private var districtID: Int = 0
    private lateinit var talukaName: String
    private var talukaID: Int = 0
    private lateinit var villageName: String
    private var villageID: Int = 0
    private var agristackId: String = ""
    private var fAAPRegistrationID: String = ""
    private var sessionManager: SessionManager? = null

    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var villageJSONArray: JSONArray? = null

    private lateinit var dialog: Dialog
    private var mobileNumberStatus: Boolean = false
    var farmerRegisterID: Int = 0
    private lateinit var languageToLoad: String
    private var versionName: String? = null
    private var token: String? = null
    private var machineId: String? = null
    private val farmerViewModel: FarmerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ProfileScreen)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)
        versionName = LocalCustom.getVersionName(this)
        token = FirebaseMessaging.getInstance().token.toString()
        sessionManager = SessionManager(this)
        setConfiguration()
        onclick()
        observeResponse()
    }

    private fun disableView() {
        binding.nameEditText.isEnabled = false
        binding.mobNoEditText.isEnabled = false
        binding.textViewVerify.isEnabled = false
        binding.textViewDist.isEnabled = false
        binding.textViewTaluka.isEnabled = false
        binding.textViewVillage.isEnabled = false
        binding.submitButton.isEnabled = false
    }

    private fun enableView() {
        binding.nameEditText.isEnabled = true
        binding.mobNoEditText.isEnabled = true
        binding.textViewVerify.isEnabled = true
        binding.textViewDist.isEnabled = true
        binding.textViewTaluka.isEnabled = true
        binding.textViewVillage.isEnabled = true
        binding.submitButton.isEnabled = true
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
                    val intent = Intent(this@ProfileScreen, SplashScreenActivity::class.java)
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

        farmerViewModel.consentResponse.observe(this) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val status = jsonObject.optInt("status")
                if (status == 200) {
                    Toast.makeText(this, consentMessage ?: "Consent Submitted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val responseText = jsonObject.optString("response")
                    Toast.makeText(this, responseText, Toast.LENGTH_SHORT).show()
                }
            }
        }

        registrationViewModel.getOTPRegisterResponse.observe(this) { response ->
            if (response != null) {
                val jSONObject = JSONObject(response.toString())
                if (jSONObject.optInt("status") == 200) {
                    Log.d("TAGGER", "onResponse: $jSONObject")
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    addVerificationDialog()
                } else if (jSONObject.optInt("status") == 201) {
                    Toast.makeText(this, R.string.otp_error, Toast.LENGTH_LONG).show()
                } else if (jSONObject.optInt("status") == 429) {
                    Toast.makeText(this, jSONObject.optString("response"), Toast.LENGTH_LONG).show()
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

    private fun getLocalizedValue(mrKey: String, enKey: String, default: String): String {
        val key = if (languageToLoad == "mr") mrKey else enKey
        return AppSettings.getInstance().getValue(this, key, default)
    }

    private fun setConfiguration() {
        farmerRegisterID = intent.getIntExtra("FAAPRegistrationID", 0)
        if (farmerRegisterID > 0) {
            binding.submitButton.text = getString(R.string.update_profile_text)
            fAAPRegistrationID = farmerRegisterID.toString()
            mobileNumberStatus = true
            userName =
                AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName)
            registerMob = AppSettings.getInstance()
                .getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
            Log.d("TAGGER", "setConfiguration: $registerMob")
            emailid =
                AppSettings.getInstance().getValue(this, AppConstants.uEmail, AppConstants.uEmail)
            districtName = getLocalizedValue(
                AppConstants.uDISTMR,
                AppConstants.uDIST,
                getString(R.string.farmer_select_district)
            )
            talukaName = getLocalizedValue(
                AppConstants.uTALUKAMR,
                AppConstants.uTALUKA,
                getString(R.string.farmer_select_taluka)
            )
            villageName = getLocalizedValue(
                AppConstants.uVILLAGEMR,
                AppConstants.uVILLAGE,
                getString(R.string.farmer_select_village)
            )
            districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
            talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
            villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
            val rawValue = AppSettings.getInstance().getSavedValue(this, AppConstants.AGRISTACKID)
            agristackId = if (rawValue.isNullOrEmpty() || rawValue == "null") "" else rawValue
            if (agristackId != "") {
                binding.consentLayout.visibility = View.VISIBLE
                binding.consentLayout.setOnClickListener {
                    showDialogForConsent()
                }
            }
            binding.nameEditText.setText(userName)
            binding.mobNoEditText.setText(registerMob)
            binding.textViewDist.text = districtName
            binding.textViewTaluka.text = talukaName
            binding.textViewVillage.text = villageName

            if (agristackId != "") {
                disableView()
            } else {
                enableView()
            }
        } else {
            binding.textView5.text = getString(R.string.register_text_1)
            binding.textView6.text = getString(R.string.register_text_2)
        }
        farmerViewModel.getDistrictData(this, languageToLoad)
    }

    @JvmName("getMachineId1")
    fun getMachineId(): String? {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun onclick() {

        binding.passwordChangeText.setOnClickListener {
            val intent = Intent(this, ForgetPassword::class.java)
            startActivity(intent)
        }

        binding.backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.mobNoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (farmerRegisterID > 0) {
                    mobileNumberStatus = false
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
        binding.submitButton.setOnClickListener {
            machineId = getMachineId()
            if (farmerRegisterID > 0) {
                isUserLoggedIn = true
                userValidationAndUpdateProfile()
            } else {
                isUserLoggedIn = false
                userValidationAndRegistration()
            }

        }
        binding.textViewVerify.setOnClickListener {
            sendOTP()
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


    private fun sendOTP() {
        mob = binding.mobNoEditText.text.toString()
        if (farmerRegisterID > 0 && mob != registerMob) {
            mobileNumberStatus = false
        }
        if (mob.isEmpty()) {
            binding.mobNoEditText.error = resources.getString(R.string.login_mob_err)
            binding.mobNoEditText.requestFocus()
        } else if (!AppUtility.getInstance().isValidPhoneNumber(mob)) {
            binding.mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            binding.mobNoEditText.requestFocus()
        } else {
            registrationViewModel.getOTPRegisterRequest(this, mob.trim { it <= ' ' })
        }
    }

    private fun userValidationAndUpdateProfile() {
        userName = binding.nameEditText.text.toString()
        mob = binding.mobNoEditText.text.toString()

        if (userName.isEmpty()) {
            binding.nameEditText.error = resources.getString(R.string.name_error)
            binding.nameEditText.requestFocus()
        } else if (mob.isEmpty() && !AppUtility.getInstance().isValidPhoneNumber(mob)) {
            binding.mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            binding.mobNoEditText.requestFocus()
        } else if (!mobileNumberStatus) {
            binding.mobNoEditText.error = resources.getString(R.string.regist_mob_verify_err)
            binding.mobNoEditText.requestFocus()
        } else if (districtID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_district))
        } else if (talukaID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
        } else if (villageID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_village))
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("Name", userName)
                jsonObject.put("EmailId", emailid)
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
                jsonObject.put("Password", "")
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                registrationViewModel.getRegistrationRequest(
                    this,
                    registerMob,
                    mob.trim { it <= ' ' },
                    jsonObject
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun userValidationAndRegistration() {
        userName = binding.nameEditText.text.toString()
        mob = binding.mobNoEditText.text.toString()

        if (userName.isEmpty()) {
            binding.nameEditText.error = resources.getString(R.string.name_error)
            binding.nameEditText.requestFocus()
        } else if (mob.isEmpty()) {
            binding.mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            binding.mobNoEditText.requestFocus()
        } else if (!mobileNumberStatus) {
            binding.mobNoEditText.error = resources.getString(R.string.regist_mob_verify_err)
            binding.mobNoEditText.requestFocus()
        } else if (districtID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_district))
        } else if (talukaID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
        } else if (villageID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_village))
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("Name", userName)
                jsonObject.put("EmailId", emailid)
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
                jsonObject.put("Password", toSHA512(pass))
                jsonObject.put("SecurityKey", ApiConstants.SSO_KEY)
                registrationViewModel.getRegistrationRequest(
                    this, mob.trim { it <= ' ' },
                    mob.trim { it <= ' ' }, jsonObject
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun addVerificationDialog() {
        var countDownTimer: CountDownTimer? = null
        countDownTimer?.cancel()
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_activity_verification)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val countdownTextview = dialog.findViewById<TextView>(R.id.countdownTextview)
        countDownTimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                countdownTextview.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                countdownTextview.text = "00:00"
            }
        }
        countDownTimer.start()

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = "Enter OTP"

        val receiveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resendOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)
        otpVerification(resendOTP)
        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {
            val enteredOTP: String = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                farmerViewModel.compareOtpReg(
                    this,
                    binding.mobNoEditText.text.toString(),
                    enteredOTP
                )
            }
            farmerViewModel.compareOtpResponseReg.observe(this) {
                if (it != null) {
                    val jSONObject = JSONObject(it.toString())
                    if (jSONObject.optInt("status") == 200) {
                        userVerification()
                    } else {
                        dialog.dismiss()
                        UIToastMessage.show(this, getString(R.string.wrong_OTP))
                    }
                }
            }
        }
        resendOTP.setOnClickListener {
            dialog.dismiss()
            sendOTP()
        }
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                dialog.show()
            }
        }
    }

    private fun userVerification() {
        binding.textViewVerify.text = resources.getString(R.string.reg_verified)
        binding.textViewVerify.setTextColor(Color.parseColor("#1d6b08"))
        binding.textViewVerify.background =
            ContextCompat.getDrawable(this, R.drawable.layout_button_background)
        binding.mobNoEditText.isEnabled = false
        mobileNumberStatus = true
        sessionManager?.setLoggedIn(true)
        dialog.dismiss()
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

    private fun otpVerification(resendOTP: Button) {
        // Disable and gray out the button before starting timer
        resendOTP.isEnabled = false
        resendOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        resendOTP.setTextColor(ContextCompat.getColor(this, R.color.white))

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resendOTP.text =
                    resources.getString(R.string.Time) + ":" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                // Enable and reset button appearance
                resendOTP.isEnabled = true
                resendOTP.text = resources.getString(R.string.Resend_OTP)
                resendOTP.setBackgroundColor(
                    ContextCompat.getColor(
                        this@ProfileScreen,
                        R.color.status_green
                    )
                )
                resendOTP.setTextColor(ContextCompat.getColor(this@ProfileScreen, R.color.white))
            }
        }.start()
    }

    private fun showDialogForConsent() {
        val confirmationDialog =
            AlertDialog.Builder(this).setTitle(R.string.withdraw_consent)
                .setMessage(R.string.withdraw_consent_desc_withdraw)
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    consentMessage = ContextCompat.getString(this, R.string.consent_withdrawn)
                    farmerViewModel.updateConsent(this, false)
                    logoutFromApp()
                    dialog.dismiss()
                }.setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
        confirmationDialog.show()
    }

    private fun logoutFromApp() {
        if (NetworkUtils.isInternetAvailable(this)) {
            farmerViewModel.updateFCMToken(this, "NA")
        } else {
            LocalCustom.createSnackbar(binding.root, "Internet not available!")
        }
    }
}
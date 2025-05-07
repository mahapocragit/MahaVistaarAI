package `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication

import android.app.AlertDialog
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.util.app_util.SessionManager
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import `in`.gov.mahapocra.mahavistaarai.ui.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


class Registration : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode,
    AlertListEventListener {

    private lateinit var nameEditText: EditText
    private lateinit var mobNoEditText: EditText
    private lateinit var emailId: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var textViewDist: TextView
    private lateinit var textViewTaluka: TextView
    private lateinit var textViewVillage: TextView
    private lateinit var textViewVerify: TextView
    private lateinit var backPressIcon: ImageView
    private lateinit var textView5: TextView
    private lateinit var textView6: TextView
    private lateinit var submitButton: Button
    private var isUserLoggedIn: Boolean = false

    private lateinit var sentOTP: String
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
    private lateinit var farmerViewModel : FarmerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Registration)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        LocalCustom.configureLocale(baseContext, languageToLoad)
        setContentView(R.layout.activity_registration)
        farmerViewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        versionName = LocalCustom.getVersionName(this)
        token = FirebaseMessaging.getInstance().token.toString()
        sessionManager = SessionManager(this)
        init()
        setConfiguration()
        onclick()
        observeResponse()
    }

    private fun observeResponse() {
        farmerViewModel.talukaList.observe(this){
            if (it!=null){
                val jSONObject = JSONObject(it.toString())
                val response = ResponseModel(jSONObject)
                if (response.status) {
                    talukaJSONArray = response.getdataArray()
                } else {
                    UIToastMessage.show(this, response.response)
                }
            }
        }
    }

    private fun setConfiguration() {
        farmerRegisterID = intent.getIntExtra("FAAPRegistrationID", 0)
        if (farmerRegisterID > 0) {
            submitButton.text = getString(R.string.update_profile_text)
            textView5.text = getString(R.string.user_info_text_1)
            textView6.text = getString(R.string.user_info_text_2)
            fAAPRegistrationID = farmerRegisterID.toString()
            mobileNumberStatus = true
            userName =
                AppSettings.getInstance().getValue(this, AppConstants.uName, AppConstants.uName)
            registerMob = AppSettings.getInstance()
                .getValue(this, AppConstants.uMobileNo, AppConstants.uMobileNo)
            emailid =
                AppSettings.getInstance().getValue(this, AppConstants.uEmail, AppConstants.uEmail)
            districtName =
                AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
            talukaName =
                AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
            villageName = AppSettings.getInstance()
                .getValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)
            districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
            talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
            villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
            passwordEditText.visibility = View.GONE
            confirmPasswordEditText.visibility = View.GONE
            nameEditText.setText(userName)
            mobNoEditText.setText(registerMob)
            emailId.setText(emailid)
            textViewDist.text = districtName
            textViewTaluka.text = talukaName
            textViewVillage.text = villageName

        } else {
            textView5.text = getString(R.string.register_text_1)
            textView6.text = getString(R.string.register_text_2)
        }
        getDistrictData()
    }

    @JvmName("getMachineId1")
    fun getMachineId(): String? {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun getDistrictData() {
        val jsonObject = JSONObject()
        try {
            // jsonObject.put("SecurityKey", APIServices.SSO_KEY)
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
                val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
                api.postRequest(responseCall, this@Registration, 1)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun init() {
        nameEditText = findViewById(R.id.nameEditText)
        mobNoEditText = findViewById(R.id.mobNoEditText)
        emailId = findViewById(R.id.emailId)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        textViewDist = findViewById(R.id.textViewDist)
        textViewTaluka = findViewById(R.id.textViewTaluka)
        textViewVillage = findViewById(R.id.textViewVillage)
        textViewVerify = findViewById(R.id.textViewVerify)
        backPressIcon = findViewById(R.id.backPressIcon)
        submitButton = findViewById(R.id.submitButton)
        textView5 = findViewById(R.id.textView5)
        textView6 = findViewById(R.id.textView6)
    }

    private fun onclick() {
        backPressIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mobNoEditText.addTextChangedListener(object : TextWatcher {
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
        submitButton.setOnClickListener {
            machineId = getMachineId()
            if (farmerRegisterID > 0) {
                isUserLoggedIn = true
                userValidationAndUpdateProfile()
            } else {
                isUserLoggedIn = false
                userValidationAndRegistration()
            }

        }
        textViewVerify.setOnClickListener {
            sendOTP()
        }
        textViewDist.setOnClickListener {
            showDistrict()
        }

        textViewTaluka.setOnClickListener {
            showTaluka()
        }

        textViewVillage.setOnClickListener {
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
                getVillageAgainstTaluka()
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
            getDistrictData()
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
        mob = mobNoEditText.text.toString()
        // verification.setTextColor(Color.parseColor("#000000"))

        if (farmerRegisterID > 0 && mob != registerMob) {
            mobileNumberStatus = false
        }
        if (mob.isEmpty()) {
            mobNoEditText.error = resources.getString(R.string.login_mob_err)
            mobNoEditText.requestFocus()
        } else if (!AppUtility.getInstance().isValidPhoneNumber(mob)) {
            mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            mobNoEditText.requestFocus()
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("MobileNo", mob.trim { it <= ' ' })
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

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
                    val responseCall: Call<JsonObject> = apiRequest.getOTPRequest(requestBody)
                    api.postRequest(responseCall, this@Registration, 2)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun userValidationAndUpdateProfile() {
        userName = nameEditText.text.toString()
        mob = mobNoEditText.text.toString()
        emailid = emailId.text.toString()

        if (userName.isEmpty()) {
            nameEditText.error = resources.getString(R.string.name_error)
            nameEditText.requestFocus()
        } else if (mob.isEmpty() && !AppUtility.getInstance().isValidPhoneNumber(mob)) {
            mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            mobNoEditText.requestFocus()
        } else if (!mobileNumberStatus) {
            mobNoEditText.error = resources.getString(R.string.regist_mob_verify_err)
            mobNoEditText.requestFocus()
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
                jsonObject.put("MobileNo", mob.trim { it <= ' ' })
                jsonObject.put("NewMobileNo", mob.trim { it <= ' ' })
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
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

                Log.d("TAGGER", "userValidationAndRegistration: $jsonObject")
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
                    val responseCall: Call<JsonObject> =
                        apiRequest.getRegistrationRequest(requestBody)
                    api.postRequest(responseCall, this@Registration, 3)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun userValidationAndRegistration() {
        userName = nameEditText.text.toString()
        mob = mobNoEditText.text.toString()
        pass = passwordEditText.text.toString()
        confirmPass = confirmPasswordEditText.text.toString()
        emailid = emailId.text.toString()

        if (userName.isEmpty()) {
            nameEditText.error = resources.getString(R.string.name_error)
            nameEditText.requestFocus()
        } else if (mob.isEmpty()) {
            mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            mobNoEditText.requestFocus()
        } else if (!mobileNumberStatus) {
            mobNoEditText.error = resources.getString(R.string.regist_mob_verify_err)
            mobNoEditText.requestFocus()
        } else if (pass.isEmpty()) {
            passwordEditText.error = resources.getString(R.string.password_error)
            passwordEditText.requestFocus()
        } else if (confirmPass.isEmpty()) {
            confirmPasswordEditText.error = resources.getString(R.string.conf_password_error)
            confirmPasswordEditText.requestFocus()
        } else if (pass != confirmPass) {
            confirmPasswordEditText.error = resources.getString(R.string.pass_equals_confirmpass)
            confirmPasswordEditText.requestFocus()
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
                jsonObject.put("MobileNo", mob.trim { it <= ' ' })
                jsonObject.put("NewMobileNo", mob.trim { it <= ' ' })
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
                jsonObject.put("Password", pass)
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

                Log.d("TAGGER", "userValidationAndRegistration: $jsonObject")
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
                    val responseCall: Call<JsonObject> =
                        apiRequest.getRegistrationRequest(requestBody)
                    api.postRequest(responseCall, this@Registration, 3)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun addVerificationDialog(sentOTP: String) {
        var countDownTimer: CountDownTimer? = null
        countDownTimer?.cancel()
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        Log.d("sentOTP111", sentOTP)
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
        val resendOTP = dialog.findViewById<Button>(R.id.resentOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)

        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {
            val enteredOTP: String = receiveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                receiveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                receiveOTPEditText.requestFocus()
            } else {
                userVerification(enteredOTP)
            }
        }
        resendOTP.setOnClickListener {
            dialog.dismiss()
            sendOTP()
        }
        dialog.show()
    }

    private fun userVerification(enteredOTP: String) {
        if (enteredOTP == this.sentOTP) {
            textViewVerify.text = resources.getString(R.string.reg_verified)
            textViewVerify.setTextColor(Color.parseColor("#1d6b08"))
            mobNoEditText.isEnabled = false
            mobileNumberStatus = true
            sessionManager?.setLoggedIn(true)
            dialog.dismiss()
        } else {
            Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
    }

    override fun onFailure(th: Throwable?, i: Int) {
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
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

        if (i == 2) {
            if (jSONObject != null) {
                if (jSONObject.optInt("status") == 200) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    sentOTP = jSONObject.optInt("otp").toString()
                    addVerificationDialog(sentOTP)
                }
            }
        }

        if (i == 3) {

            Log.d("TAGGER", "userValidationAndRegistration: $jSONObject")
            if (jSONObject != null) {

                if (jSONObject.optInt("status") == 200) {
                    val response: String = jSONObject.getString("response")
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    var intent = Intent(this, LoginScreen::class.java)
                    if (isUserLoggedIn){
                        intent = Intent(this, DashboardScreen::class.java)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val notifiCountValue: String = jSONObject.getString("Message")
                    Toast.makeText(this, notifiCountValue, Toast.LENGTH_LONG).show()
                }
            }
        }

        if (i == 5 && jSONObject != null) {
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

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {

        if (i == 1) {
            if (s1 != null) {
                districtID = s1.toInt()
            }

            if (s != null) {
                districtName = s
            }
            textViewDist.text = s
            if (districtID > 0) {
                farmerViewModel.fetchTalukaMasterData(this, languageToLoad)
            }
            talukaID = 0
            textViewTaluka.text = ""
            textViewTaluka.hint = resources.getString(R.string.farmer_select_taluka)
            textViewTaluka.setHintTextColor(Color.GRAY)

            villageID = 0
            textViewVillage.text = ""
            textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            textViewVillage.setHintTextColor(Color.GRAY)
        }


        if (i == 2) {
            if (s1 != "") {
                talukaID = s1!!.toInt()
            }
            if (s != null) {
                talukaName = s
            }
            textViewTaluka.text = s
            villageJSONArray = null
            if (talukaID > 0) {
                getVillageAgainstTaluka()
            }
            villageID = 0
            textViewVillage.text = ""
            textViewVillage.hint = resources.getString(R.string.farmer_select_village)
            textViewVillage.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            if (s1 != "") {
                villageID = s1!!.toInt()
            }

            villageName = s.toString()
            textViewVillage.text = s
        }

    }

    private fun getVillageAgainstTaluka() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("taluka_code", talukaID)

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
                val responseCall: Call<JsonObject> = apiRequest.kGetVillageList(requestBody)
                api.postRequest(responseCall, this@Registration, 5)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
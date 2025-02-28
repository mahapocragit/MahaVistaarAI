package `in`.gov.mahapocra.farmerapppks.ui.screens.authentication

import android.app.AlertDialog
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.data.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.data.api.APIServices
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.util.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.util.app_util.SessionManager
import `in`.gov.mahapocra.farmerapppks.data.model.ResponseModel
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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.DashboardScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


class Registration : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode, AlertListEventListener {

    private lateinit var nameEditText: EditText
    private lateinit var mobNoEditText: EditText
    private lateinit var emailId: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var textViewDist: TextView
    private lateinit var textViewTaluka: TextView
    private lateinit var textViewVillage: TextView
    private lateinit var textViewVerify: TextView
    private lateinit var submitButton: Button
    private lateinit var deleteAccountButton: TextView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Registration).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        var pinfo: PackageInfo? = null
        try {
            pinfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        versionName = pinfo?.versionName
        token = FirebaseMessaging.getInstance().token.toString()
        Log.d("token12345", "" + token)
        sessionManager = SessionManager(this)
        init()
        deleteAccountButton.visibility = View.GONE
        setConfiguration()
        onclick()
    }

    private fun setConfiguration() {
        farmerRegisterID = intent.getIntExtra("FAAPRegistrationID", 0)
        if (farmerRegisterID > 0) {
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
            deleteAccountButton.visibility = View.GONE
            nameEditText.setText(userName)
            mobNoEditText.setText(registerMob)
            emailId.setText(emailid)
            textViewDist.text = districtName
            textViewTaluka.text = talukaName
            textViewVillage.text = villageName

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
                    APIServices.SSO,
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
            DebugLog.getInstance().d("JSONException=$e")
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
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        textViewVerify = findViewById(R.id.textViewVerify)
        submitButton = findViewById(R.id.submitButton)
    }

    private fun onclick() {

        deleteAccountButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(this, "Your account has been deleted", Toast.LENGTH_SHORT).show()
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
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
                userValidationAndUpdateProfile()
            } else {
                userValidationAndRegistraton()
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
                fetchTalukaMasterData()
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
                    "id",
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
                "id",
                this,
                this
            )
        }
    }


    private fun sendOTP() {
        mob = mobNoEditText.text.toString()
        // verification.setTextColor(Color.parseColor("#000000"))

        if (farmerRegisterID > 0 && !mob.equals(registerMob)) {
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
                        APIServices.DBT,
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
                DebugLog.getInstance().d("JSONException=$e")
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
        } else if (mobileNumberStatus == false) {
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
                jsonObject.put("EmailId", emailid)
                jsonObject.put("DistrictName", districtName)
                jsonObject.put("DistrictID", districtID)
                jsonObject.put("TalukaName", talukaName)
                jsonObject.put("TalukaID", talukaID)
                jsonObject.put("VillageName", villageName)
                jsonObject.put("VillageCode", "")
                jsonObject.put("VillageID", villageID)
                jsonObject.put("Status", "Active")
                jsonObject.put("version_number", versionName)
                jsonObject.put("fcm_token", token)
                jsonObject.put("device_id", machineId)
                jsonObject.put("FAAPRegistrationID", fAAPRegistrationID)
                jsonObject.put("Password", "")
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        this,
                        APIServices.DBT,
                        "",
                        AppString(this).getkMSG_WAIT(),
                        true
                    )
                CoroutineScope(Dispatchers.IO).launch {
                    val retrofit: Retrofit = api.getRetrofitInstance()
                    val apiRequest = retrofit.create(APIRequest::class.java)
                    val responseCall: Call<JsonObject> = apiRequest.getRegistrationRequest(requestBody)
                    api.postRequest(responseCall, this@Registration, 3)
                }
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=$e")
                e.printStackTrace()
            }
        }
    }

    private fun userValidationAndRegistraton() {
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
        } else if (!pass.equals(confirmPass)) {
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
                jsonObject.put("EmailId", emailid)
                jsonObject.put("DistrictName", districtName)
                jsonObject.put("DistrictID", districtID)
                jsonObject.put("TalukaName", talukaName)
                jsonObject.put("TalukaID", talukaID)
                jsonObject.put("VillageName", villageName)
                jsonObject.put("VillageCode", "")
                jsonObject.put("VillageID", villageID)
                jsonObject.put("Status", "Active")
                jsonObject.put("version_number", versionName)
                jsonObject.put("fcm_token", token)
                jsonObject.put("device_id", machineId)
                jsonObject.put("FAAPRegistrationID", fAAPRegistrationID)
                jsonObject.put("Password", pass)
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppInventorApi(
                        this,
                        APIServices.DBT,
                        "",
                        AppString(this).getkMSG_WAIT(),
                        true
                    )
                CoroutineScope(Dispatchers.IO).launch {
                    val retrofit: Retrofit = api.getRetrofitInstance()
                    val apiRequest = retrofit.create(APIRequest::class.java)
                    val responseCall: Call<JsonObject> = apiRequest.getRegistrationRequest(requestBody)
                    api.postRequest(responseCall, this@Registration, 3)
                }
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
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
        countDownTimer = object : CountDownTimer(90000 , 1000) {
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
        if (enteredOTP.equals(this.sentOTP)) {
            textViewVerify.text = resources.getString(R.string.reg_verified)
            textViewVerify.setTextColor(Color.parseColor("#1d6b08"))
            mobNoEditText.setEnabled(false)
            mobileNumberStatus = true
            sessionManager?.setLoggedIn(true)
            dialog.dismiss()
        } else {
            Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj$i")
    }

    override fun onFailure(th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$th$i")
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

        if (i == 4 && jSONObject != null) {
            val response =
                ResponseModel(
                    jSONObject
                )
            if (response.status) {
                talukaJSONArray = response.getdataArray()
            } else {
                UIToastMessage.show(this, response.response)
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

        if (i == 2) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    val notifiCountValue: String = jSONObject.getString("Message")
                    Toast.makeText(this, notifiCountValue, Toast.LENGTH_LONG).show()
                    //UIToastMessage.showShortDuration(this, notifiCountValue)
                    sentOTP = jSONObject.getString("OTP")
                    addVerificationDialog(sentOTP)
                }
            }
        }
        if (i == 3) {
            if (jSONObject != null) {
                val response =
                    ResponseModel(
                        jSONObject
                    )
                if (response.getStatus()) {
                    val notifiCountValue: String = jSONObject.getString("Message")
                    Toast.makeText(this, notifiCountValue, Toast.LENGTH_LONG).show()
                    if (!(farmerRegisterID > 0)) {
                        farmerRegisterID = jSONObject.getInt("RegistrationID")
                        AppSettings.getInstance()
                            .setIntValue(this, AppConstants.fREGISTER_ID, farmerRegisterID)
                    }

                    val intent = Intent(this, DashboardScreen::class.java)
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
                fetchTalukaMasterData()
            }
            talukaID = 0
            textViewTaluka.text = ""
            textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
            textViewTaluka.setHintTextColor(Color.GRAY)

            villageID = 0
            textViewVillage.text = ""
            textViewVillage.setHint(resources.getString(R.string.farmer_select_village))
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
            textViewVillage.setHint(resources.getString(R.string.farmer_select_village))
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

    private fun fetchTalukaMasterData() {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("district_id", districtID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getTalukaList(requestBody)
                api.postRequest(responseCall, this@Registration, 4)
            }
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }

    }


    private fun getVillageAgainstTaluka() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("taluka_id", talukaID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.SSO,
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
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }
}
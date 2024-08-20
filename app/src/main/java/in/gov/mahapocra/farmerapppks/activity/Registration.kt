package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.app_util.SessionManager
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


class Registration : AppCompatActivity(), ApiJSONObjCallback, ApiCallbackCode,
    AlertListEventListener {

    lateinit var nameEditText: EditText
    lateinit var mobNoEditText: EditText
    lateinit var emailId: EditText
    lateinit var passwordEditText: EditText
    lateinit var confirmPasswordEditText: EditText
    lateinit var textViewDist: TextView
    lateinit var textViewTaluka: TextView
    lateinit var textViewVillage: TextView
    lateinit var textViewVerify: TextView
    lateinit var submitButton: Button

    lateinit var sentOTP: String
    lateinit var userName: String
    lateinit var mob: String
    lateinit var registerMob: String
    lateinit var pass: String
    lateinit var confrmPass: String
    lateinit var emailid: String
    lateinit var districtName: String
    private  var districtID: Int = 0
    lateinit var talukaName: String
    private var talukaID: Int = 0
    lateinit var villageName: String
    lateinit var villageCode: String
    private var villageID: Int = 0
    private var fAAPRegistrationID: String =""
    private var sessionManager: SessionManager? = null


    private var districtJSONArray: JSONArray? = null
    private var talukaJSONArray: JSONArray? = null
    private var villJSONArray: JSONArray? = null


    private lateinit var dialog: Dialog
    private var moblNumberStatus: Boolean = false
    var farmerRigisterID: Int = 0
    lateinit var languageToLoad: String

    var versionName:kotlin.String? = null
    var token:kotlin.String? = null
    var machineId:kotlin.String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@Registration).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_registration)
        var pinfo: PackageInfo? = null
        try {
            pinfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val versionNumber = pinfo!!.versionCode
        versionName = pinfo!!.versionName
        token = FirebaseInstanceId.getInstance().token
        Log.d("token12345", "" + token)

        sessionManager = SessionManager(this)
        init()
        setConfiguration()
        onclick()
    }
    private fun setConfiguration() {
        farmerRigisterID = intent.getIntExtra("FAAPRegistrationID",0)
        if (farmerRigisterID > 0){
            fAAPRegistrationID = farmerRigisterID.toString()
            moblNumberStatus= true
            userName= AppSettings.getInstance().getValue(this, AppConstants.uName,AppConstants.uName )
            registerMob= AppSettings.getInstance().getValue(this, AppConstants.uMobileNo,AppConstants.uMobileNo )
            emailid =  AppSettings.getInstance().getValue(this, AppConstants.uEmail, AppConstants.uEmail)
            districtName =  AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
            talukaName =  AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
            villageName  =  AppSettings.getInstance().getValue(this, AppConstants.uVILLAGE, AppConstants.uVILLAGE)
            districtID = AppSettings.getInstance().getIntValue(this, AppConstants.uDISTId, 0)
            talukaID = AppSettings.getInstance().getIntValue(this, AppConstants.uTALUKAID, 0)
            villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
            passwordEditText.visibility = View.GONE
            confirmPasswordEditText.visibility = View.GONE

            nameEditText.setText(userName)
            mobNoEditText.setText(registerMob)
            emailId.setText(emailid)
            textViewDist.setText(districtName)
            textViewTaluka.setText(talukaName)
            textViewVillage.setText(villageName)

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
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getDistrictList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun init() {
        nameEditText = findViewById(R.id.nameEditText);
        mobNoEditText = findViewById(R.id.mobNoEditText);
        emailId = findViewById(R.id.emailId);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        textViewDist = findViewById(R.id.textViewDist);
        textViewTaluka = findViewById(R.id.textViewTaluka);
        textViewVillage = findViewById(R.id.textViewVillage);
        textViewVerify = findViewById(R.id.textViewVerify);
        submitButton = findViewById(R.id.submitButton);
    }

    private fun onclick() {
//        mobNoEditText.setOnClickListener(View.OnClickListener {
//            val MobilePattern = "[0-9]{10}"
//            if (mobNoEditText.getText().toString().equals(MobilePattern)) {
//                Toast.makeText(this, "phone number is valid", Toast.LENGTH_SHORT)
//                    .show();
//
//            } else if (!mobNoEditText.getText().toString().equals(MobilePattern)) {
//
//                Toast.makeText(
//                    this,
//                    "Please enter valid 10 digit phone number",
//                    Toast.LENGTH_SHORT
//                ).show();
//            }
//        })

        mobNoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (farmerRigisterID > 0) {
                    Log.d("sssssss1", s.toString())
                    moblNumberStatus = false
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                Log.d("sssssss2", s.toString())
//                moblNumberStatus= false
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                Log.d("sssssss3", s.toString())
//                moblNumberStatus= false
            }
        })
        submitButton.setOnClickListener(View.OnClickListener {
            machineId = getMachineId()
            if (farmerRigisterID > 0){
                userValidationAndUpdateprofile()
            }else{
                userValidationAndRegistraton()
            }

        })
        textViewVerify.setOnClickListener(View.OnClickListener {
            sendOTP()
        })
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
                UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_district))
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(talukaJSONArray, 2, getString(R.string.farmer_select_taluka), "name", "id", this, this)
        }
    }

    private fun showVillage() {
        if (villJSONArray == null) {
            if (talukaID > 0) {
                getVillageAgainstTaluka()
            } else {
                UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
            }
        } else {
            AppUtility.getInstance()
                .showListDialogIndex(villJSONArray, 3, getString(R.string.farmer_select_village), "name", "code", this, this)
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

         if (farmerRigisterID > 0 && !mob.equals(registerMob) ) {
            moblNumberStatus= false
        }
        if (mob.isEmpty()) {
            mobNoEditText.error = resources.getString(R.string.login_mob_err)
            mobNoEditText.requestFocus()
        } else if (!AppUtility.getInstance().isValidPhoneNumber(mob)) {
            mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            mobNoEditText.requestFocus()
        }
        else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("MobileNo", mob.trim { it <= ' ' })
                jsonObject.put("SecurityKey", APIServices.SSO_KEY)

                val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                val api =
                    AppinventorApi(this, APIServices.DBT, "", AppString(this).getkMSG_WAIT(), true)
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getOTPRequest(requestBody)
                DebugLog.getInstance().d("param1=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                api.postRequest(responseCall, this, 2)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
    }

    private fun userValidationAndUpdateprofile() {
        userName = nameEditText.text.toString()
        mob = mobNoEditText.text.toString()
        emailid = emailId.text.toString()

        if (userName.isEmpty()) {
            nameEditText.error = resources.getString(R.string.name_error)
            nameEditText.requestFocus()
        }else if (mob.isEmpty() && !AppUtility.getInstance().isValidPhoneNumber(mob)) {
            mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            mobNoEditText.requestFocus()
        } else if (moblNumberStatus == false) {
            mobNoEditText.error = resources.getString(R.string.regist_mob_verify_err)
            mobNoEditText.requestFocus()
        }else if (districtID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_district))
        } else if (talukaID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
        } else if (villageID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_village))
        }else {
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
                    AppinventorApi(this, APIServices.DBT, "", AppString(this).getkMSG_WAIT(), true)
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getRegistrationRequest(requestBody)
                DebugLog.getInstance().d("param1=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                api.postRequest(responseCall, this, 3)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
    }

    private fun userValidationAndRegistraton() {
        userName = nameEditText.text.toString()
        mob = mobNoEditText.text.toString()
        pass = passwordEditText.text.toString()
        confrmPass = confirmPasswordEditText.text.toString()
        emailid = emailId.text.toString()

        Log.d("pass:confrmPass",pass+" ::"+confrmPass);

        if (userName.isEmpty()) {
            nameEditText.error = resources.getString(R.string.name_error)
            nameEditText.requestFocus()
        }else if (mob.isEmpty()) {
            mobNoEditText.error = resources.getString(R.string.login_mob_valid_err)
            mobNoEditText.requestFocus()
        }else if (moblNumberStatus == false) {
            mobNoEditText.error = resources.getString(R.string.regist_mob_verify_err)
            mobNoEditText.requestFocus()
        }else if (pass.isEmpty()) {
            passwordEditText.error = resources.getString(R.string.password_error)
            passwordEditText.requestFocus()
        }else if (confrmPass.isEmpty()) {
            confirmPasswordEditText.error = resources.getString(R.string.conf_password_error)
            confirmPasswordEditText.requestFocus()
        }else if (!pass.equals(confrmPass)) {
            confirmPasswordEditText.error = resources.getString(R.string.pass_equals_confirmpass)
            confirmPasswordEditText.requestFocus()
        }else if (districtID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_district))
        } else if (talukaID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_taluka))
        } else if (villageID == 0) {
            UIToastMessage.show(this, resources.getString(R.string.error_farmer_select_village))
        }else {
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
                    AppinventorApi(this, APIServices.DBT, "", AppString(this).getkMSG_WAIT(), true)
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest = retrofit.create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getRegistrationRequest(requestBody)
                DebugLog.getInstance().d("param1=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
                api.postRequest(responseCall, this, 3)
                DebugLog.getInstance().d("param=" + responseCall.request().toString())
                DebugLog.getInstance()
                    .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            } catch (e: JSONException) {
                DebugLog.getInstance().d("JSONException=" + e.toString())
                e.printStackTrace()
            }
        }
    }
    private fun addVerificationDialog(sentOTP: String) {
        Log.d("sentOTP", sentOTP)
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        Log.d("sentOTP111", sentOTP)
        dialog.setContentView(R.layout.dialog_activity_verification)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.setText("Enter OTP")
        Log.d("sentOTP22222", sentOTP)

        val revcieveOTPEditText = dialog.findViewById<EditText>(R.id.OptEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val resendOTP = dialog.findViewById<Button>(R.id.resentOTP)
        val cancelButton = dialog.findViewById<ImageView>(R.id.imageView_close)

        cancelButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        submitButton.setOnClickListener {
            var enteredOTP: String = revcieveOTPEditText.text.toString()
            if (enteredOTP.isEmpty()) {
                revcieveOTPEditText.error = resources.getString(R.string.regist_otp_err)
                revcieveOTPEditText.requestFocus()
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

        Log.d("shdfhsdf", enteredOTP)
        Log.d("testshvm1", this.sentOTP)
        if (enteredOTP.equals(this.sentOTP)) {
            textViewVerify.setText(resources.getString(R.string.reg_verified))
            textViewVerify.setTextColor(Color.parseColor("#1d6b08"))
            mobNoEditText.setEnabled(false)
            moblNumberStatus = true
            sessionManager?.setLoggedIn(true);
            dialog.dismiss()
        } else {
            Toast.makeText(this, R.string.wrong_OTP, Toast.LENGTH_LONG).show()
        }
    }


    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj"+i)
    }

    override fun onFailure(th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$th"+i)
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                districtJSONArray = response.getdataArray()
                Log.d("districtJSONArray11111",districtJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }

        if (i == 4 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                talukaJSONArray = response.getdataArray()
                Log.d("talukaJSONArray",talukaJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }

        if (i == 5 && jSONObject != null) {
            val response = ResponseModel(jSONObject)

            if (response.status) {
                villJSONArray = response.getdataArray()
                Log.d("villJSONArray",villJSONArray.toString())
            } else {
                UIToastMessage.show(this, response.response)
            }
        }


        if (i == 2) {
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    var notifiCountValue: String = jSONObject.getString("Message")
                    Toast.makeText(this, notifiCountValue, Toast.LENGTH_LONG).show()
                    //UIToastMessage.showShortDuration(this, notifiCountValue)
                    sentOTP = jSONObject.getString("OTP")
                    addVerificationDialog(sentOTP)
                }
            }
        }
        if(i==3){
            if (jSONObject != null) {
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    var notifiCountValue: String = jSONObject.getString("Message")
                    Toast.makeText(this, notifiCountValue, Toast.LENGTH_LONG).show()
                    if (!(farmerRigisterID>0)){
                        farmerRigisterID = jSONObject.getInt("RegistrationID")
                        AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, farmerRigisterID)
                    }

                    val intent = Intent(this, DashboardScreen::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    var notifiCountValue: String = jSONObject.getString("Message")
                    Toast.makeText(this, notifiCountValue, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun didSelectListItem(i: Int, s: String?, s1: String?) {


        if (i == 1) {
            if (s1 != null) {
                districtID = s1!!.toInt()
            }

            if (s != null) {
                districtName = s
            }
            textViewDist.setText(s)
            if (districtID > 0) {
                fetchTalukaMasterData()
            }
            talukaID = 0
            textViewTaluka.setText("")
            textViewTaluka.setHint(resources.getString(R.string.farmer_select_taluka))
            textViewTaluka.setHintTextColor(Color.GRAY)

            villageID = 0
            textViewVillage.setText("")
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
            textViewTaluka.setText(s)
            villJSONArray = null
            if (talukaID > 0) {
                getVillageAgainstTaluka()
            }
            villageID = 0
            textViewVillage.setText("")
            textViewVillage.setHint(resources.getString(R.string.farmer_select_village))
            textViewVillage.setHintTextColor(Color.GRAY)

        }

        if (i == 3) {
            if (s1 != "") {
                villageID = s1!!.toInt()
            }

            villageName = s.toString()
            textViewVillage.setText(s)
        }

    }

    private fun fetchTalukaMasterData() {

        val jsonObject = JSONObject()
        try {
//            jsonObject.put("SecurityKey", APIServices.SSO_KEY)
//            jsonObject.put("DistrictID", districtID)
            jsonObject.put("lang", languageToLoad)
            jsonObject.put("district_id", districtID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getTalukaList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 4)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }

    }


    private fun getVillageAgainstTaluka() {
        val jsonObject = JSONObject()
        try {
//            jsonObject.put("SecurityKey", APIServices.SSO_KEY)
//            jsonObject.put("DistrictID", districtID)
//            jsonObject.put("TalukaID", talukaID)

            jsonObject.put("lang", languageToLoad)
            jsonObject.put("taluka_id", talukaID)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.kGetVillageList(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 5)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance()
                .d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }


}
package `in`.gov.mahapocra.farmerapppks.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.OptonRclAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class FertilizerCalculatorAcitvity : AppCompatActivity(), ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener, ApiCallbackCode {
    lateinit var yesRadioButton: RadioButton
    lateinit var noRadioButton: RadioButton

    lateinit var lnrSoilTestNo: LinearLayout
    lateinit var lnrSoilTestYes: LinearLayout
    lateinit var lnrNpkFetch: LinearLayout
    lateinit var fertilizerCal: LinearLayout

    private lateinit var imageMenushow: ImageView

    private lateinit var textViewHeaderTitle: TextView
    private lateinit var restTv: TextView
    private lateinit var calculateTv: TextView
    private lateinit var fetchNPKTv: TextView
    private lateinit var cropNametxt: TextView
    private lateinit var availableOptionTv: TextView
    private lateinit var selectedOptionTv: TextView
    private lateinit var selectAnyOneOptionTV: TextView

    private lateinit var edNitrogen: EditText
    private lateinit var edPhosphorus: EditText
    private lateinit var edPotassium: EditText

    private lateinit var edNitrogen1: EditText
    private lateinit var edPhosphorus1: EditText
    private lateinit var edPotassium1: EditText

    private lateinit var edtSurveyNo: EditText
    private lateinit var edtAcre: EditText
    private lateinit var edtGuntha: EditText

    private lateinit var edtFYM: EditText

    private lateinit var fertlizerNameRcl: RecyclerView

    private var soitlTestOption: Int = 1
    var languageToLoad: String? = null

    private var fertilizerOptionValue: JSONArray? = null
    private var villageID: Int = 0
    var cropId: Int? = 0
    var wotrCropId: String? = null
    var sowingDate: String? = null
    lateinit var token: String
    var acrArea: String = ""
    var gunthaArea: String = ""
    var nitrogenValue: String = ""
    var phosphorusValue: String = ""
    var potassiumValue: String = ""
    var edtFYMValue: String = ""
    var totalAcrArea: Float = 0.0F
    var cropName: String? = null
    var availableOption: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@FertilizerCalculatorAcitvity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_fertilizer_calculator_acitvity)

        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        sowingDate = intent.getStringExtra("sowingDate")

        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

        yesRadioButton = findViewById(R.id.yesRadioButton)
        noRadioButton = findViewById(R.id.noRadioButton)
        lnrSoilTestNo = findViewById(R.id.lnr_soil_test_no)
        lnrSoilTestYes = findViewById(R.id.lnr_soil_test_yes)
        lnrNpkFetch = findViewById(R.id.lnr_npk_fetch)
        fertilizerCal = findViewById(R.id.lnrfertilizerCal)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle) as TextView
        restTv = findViewById(R.id.restTv) as TextView
        calculateTv = findViewById(R.id.calculateTv) as TextView
        fetchNPKTv = findViewById(R.id.fetchNPKTv) as TextView
        cropNametxt = findViewById(R.id.tvCropName) as TextView
        imageMenushow = findViewById(R.id.imageMenushow)
        availableOptionTv = findViewById(R.id.availableOptionTv) as TextView
        selectedOptionTv = findViewById(R.id.selectedOptionTv) as TextView
        selectAnyOneOptionTV = findViewById(R.id.selectAnyOneOptionTV) as TextView
        edNitrogen = findViewById(R.id.edNitrogen) as EditText
        edPhosphorus = findViewById(R.id.edPhosphorus) as EditText
        edPotassium = findViewById(R.id.edPotassium) as EditText

        edNitrogen1 = findViewById(R.id.edNitrogen1) as EditText
        edPhosphorus1 = findViewById(R.id.edPhosphorus1) as EditText
        edPotassium1 = findViewById(R.id.edPotassium1) as EditText

        edtSurveyNo = findViewById(R.id.edtSurveyNo) as EditText
        edtAcre = findViewById(R.id.edtAcre) as EditText
        edtGuntha = findViewById(R.id.edtGuntha) as EditText
        edtFYM = findViewById(R.id.edtFYM) as EditText

        fertlizerNameRcl = findViewById(R.id.fertlizerOpt_Rcl) as RecyclerView
        //  imgBackArrow = findViewById(R.id.imgBackArrow) as ImageView
        textViewHeaderTitle?.setText(R.string.fertilizer_calculator)

        lnrSoilTestNo.visibility = View.GONE
        lnrNpkFetch.visibility = View.GONE

        yesRadioButton.setOnClickListener({
            onRadioButtonClicked(it)
        })
        noRadioButton.setOnClickListener({
            onRadioButtonClicked(it)
        })
        restTv.setOnClickListener({
            resetEdittest()
        })
        calculateTv.setOnClickListener({
            validation()
        })
        fetchNPKTv.setOnClickListener({
            Log.d("restTv ", "restTv")
        })
        selectedOptionTv.setOnClickListener({
            selectedOptionTv.setBackground(ContextCompat.getDrawable(this, R.drawable.green_gradient_with_yellow_border))
            availableOptionTv.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bg_gradient))
           getSelectedSavedOption()
        })

        imageMenushow.visibility = View.VISIBLE
        imageMenushow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, CropStageAdvisory::class.java)
            intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
            startActivity(intent)
        })

        edtAcre.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!(s.contentEquals(""))) {
                    var AcreNo: Int = s.toString().toInt()
                    if (AcreNo > 99) {
                        Toast.makeText(
                            this@FertilizerCalculatorAcitvity,
                            "Please Enter Acre Area Should Be Less Than 99",
                            Toast.LENGTH_SHORT
                        ).show()
                        edtAcre.setText("0")
                    }
                }
            }
        })

        edtGuntha.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!(s.contentEquals(""))) {
                    var AcreNo: Int = s.toString().toInt()
                    if (AcreNo > 39) {
                        Toast.makeText(
                            this@FertilizerCalculatorAcitvity,
                            "Please Enter Gunbtha Area Should Be Less Than or equal to 39",
                            Toast.LENGTH_SHORT
                        ).show()
                        edtGuntha.setText("0")
                    }
                }
            }
        })
        cropNametxt.setText(cropName + " ")
    }

    private fun getSelectedSavedOption() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("farmer_id", "902")
            jsonObject.put("crop_id", cropId)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getFertilizerSavedFormula(requestBody)
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

    private fun resetEdittest() {
        edtAcre.setText("0")
        edtGuntha.setText("0")
        edNitrogen.setText("0")
        edPhosphorus.setText("0")
        edPotassium.setText("0")
        edtFYM.setText("0")
        totalAcrArea = 0.0F
    }

    private fun validation() {
        acrArea = edtAcre.text.toString()
        gunthaArea = edtGuntha.text.toString()
        if (gunthaArea.isNullOrBlank()) {
            gunthaArea = "0"
        }

        if (soitlTestOption == 0) {
            nitrogenValue = "0"
            phosphorusValue = "0"
            potassiumValue = "0"
            edtFYMValue = "0"
        } else {
            nitrogenValue = edNitrogen.text.toString()
            phosphorusValue = edPhosphorus.text.toString()
            potassiumValue = edPotassium.text.toString()
            edtFYMValue = edtFYM.text.toString()
        }
        if (cropId == 0) {
            UIToastMessage.show(
                this,
                resources.getString(R.string.fertilizer_availability_for_crop)
            )
        } else if (acrArea.isNullOrBlank()) {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_Acre_Area))
        } else if (nitrogenValue.isNullOrBlank()) {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_nitrogenValue))
        } else if (phosphorusValue.isNullOrBlank()) {
            UIToastMessage.show(
                this,
                resources.getString(R.string.Please_Enter_Your_phosphorusValue)
            )
        } else if (potassiumValue.isNullOrBlank()) {
            UIToastMessage.show(
                this,
                resources.getString(R.string.Please_Enter_Your_potassiumValue)
            )
        } else if (edtFYMValue.isNullOrBlank()) {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_FYMValue))
        } else {
            acrAreaCalucalation(acrArea.toInt(), gunthaArea.toInt())
        }
    }

    private fun acrAreaCalucalation(acrArea: Int, gunthaArea: Int) {
        val result = (gunthaArea * 0.025)
        totalAcrArea = ((acrArea + result).toFloat())
        if (totalAcrArea > 0) {
            getCalculatedFertlizerToken()
        } else {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_Acre_Area))
        }

    }


    private fun getCalculatedFertlizerToken() {
        try {
            getToken()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getToken() {
        try {
            val api =
                AppinventorApi(this, APIServices.WOTR, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> =
                apiRequest.getTokenFromWotr("8470807282", "PMU%40PoCRA%232023")
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCalculatedFertlizerData() {
//        try {
//
//            val api = AppinventorIncAPI(
//                this,
//               // "https://dev-0otqduj79ub8k68.api.raw-labs.com",
//                  "https://kisan.wotr.org.in/",
//                AppSession(this).getToken(),
//                AppString(this).getkMSG_WAIT(),
//                true
//            )
//          //  api.getRequestData("https://dev-0otqduj79ub8k68.api.raw-labs.com/calculator_value", this, 1)
//             api.getRequestData("https://kisan.wotr.org.in/Api_GoM/getNutrient_calculator?CropID=7&SowingDate=2023-12-01&IsNPK=1&SoilTestN=15&SoilTestP=20&SoilTestK=15&VillageCode=548229&FYM=5&TargetYield=20&PlotSize=4&PlotUnit=2&Token=$token", this, 2)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        val formatter: DateFormat = SimpleDateFormat("dd-mm-yyyy")
        val date = formatter.parse(sowingDate) as Date
        val newFormat = SimpleDateFormat("yyyy-mm-dd")
        val finalsowingDate = newFormat.format(date)
        try {
            val api = AppinventorApi(this, APIServices.WOTR, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            // val responseCall: Call<JsonObject> = apiRequest.getFertilzerCalculatedData("7", "2023-06-01","1","177","7","280","548229","0","0","4","2",token)
            val responseCall: Call<JsonObject> = apiRequest.getFertilzerCalculatedData(
                wotrCropId,
                finalsowingDate,
                soitlTestOption.toString(),
                nitrogenValue,
                phosphorusValue,
                potassiumValue,
                villageID.toString(),
                edtFYMValue,
                "0",
                totalAcrArea.toString(),
                "1",
                token
            )
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

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.yesRadioButton ->
                    if (checked) {
                        lnrSoilTestYes.visibility = View.VISIBLE
                        lnrSoilTestNo.visibility = View.GONE
                        lnrNpkFetch.visibility = View.GONE
                        soitlTestOption = 1
                        fertilizerOptionValue = null
                        availableOptionTv.visibility = View.INVISIBLE
                        fertlizerNameRcl.visibility = View.GONE
                        fertilizerCal.visibility = View.GONE
                    }

                R.id.noRadioButton ->
                    if (checked) {
                        lnrSoilTestNo.visibility = View.GONE
                        lnrNpkFetch.visibility = View.GONE
                        lnrSoilTestYes.visibility = View.GONE
                        soitlTestOption = 0
                        fertilizerOptionValue = null
                        availableOptionTv.visibility = View.INVISIBLE
                        fertlizerNameRcl.visibility = View.GONE
                        fertilizerCal.visibility = View.GONE
                    }
            }
        }
    }

    override fun onFailure(th: Throwable?, i: Int) {
        Log.d("Throwable", th.toString())
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        Log.d("Throwable", th.toString())
        Log.d("obj", obj.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onResponse(jSONObject: JSONObject?, code: Int) {
        try {
            if (code == 1) {
                if (jSONObject != null) {
                    availableOptionTv.visibility = View.INVISIBLE
                    Log.d("jSONObject", jSONObject.toString())
                    val response = ResponseModel(jSONObject)
                    var simpleFertilizersArray: JSONArray =
                        jSONObject.getJSONArray("SimpleFertilizers")
                    var complexFertilizersArray: JSONArray =
                        jSONObject.getJSONArray("ComplexFertilizers")

                    //Main JSONObject
                    var mainIndex: Int = 0
                    var q: Int = 0
                    var z: Int = 0
                    val optionFertilizerDataArray = JSONArray()
                    var optionJsonObject1 = JSONObject()
                    var optionArray1 = JSONArray()
                    if (complexFertilizersArray.length() > 0) {
                        for (j in 0 until complexFertilizersArray.length()) {
                            // for simple
                            if (j == 0) {
                                val otionJsonObject = JSONObject()
                                val optionArray = JSONArray()
                                for (k in 0 until simpleFertilizersArray.length()) {
                                    val fetilizerJsonObject = JSONObject()
                                    val option: JSONObject = simpleFertilizersArray.getJSONObject(k)
                                    var fertilizer: JSONArray = option.getJSONArray("Option")
                                    fetilizerJsonObject.put("fertilizer", fertilizer)
                                    optionArray.put(k, fetilizerJsonObject)
                                }
                                otionJsonObject.put("Option", optionArray)
                                optionFertilizerDataArray.put(mainIndex, otionJsonObject)
                                mainIndex++
                            }
                            // for complex
                            val fetilizerJsonObject1 = JSONObject()
                            val option: JSONObject = complexFertilizersArray.getJSONObject(j)
                            var fertilizer: JSONArray = option.getJSONArray("Option")
                            var cropAgeDays: String = ""
                            for (m in 0 until 1) {
                                val optionItem: JSONObject = fertilizer.getJSONObject(m)
                                cropAgeDays = optionItem.getString("CropAgeDays")
                            }
                            if (cropAgeDays.equals("0") && q > 1) {
                                optionJsonObject1.put("Option", optionArray1)
                                optionFertilizerDataArray.put(mainIndex, optionJsonObject1)
                                optionJsonObject1 = JSONObject()
                                optionArray1 = JSONArray()
                                z = 0
                                mainIndex++
                            }
                            fetilizerJsonObject1.put("fertilizer", fertilizer)
                            optionArray1.put(z, fetilizerJsonObject1)
                            q++
                            z++
                        }
                        optionJsonObject1.put("Option", optionArray1)
                        optionFertilizerDataArray.put(mainIndex, optionJsonObject1)
                    } else {
                        val otionJsonObject = JSONObject()
                        val optionArray = JSONArray()
                        for (k in 0 until simpleFertilizersArray.length()) {
                            val fetilizerJsonObject = JSONObject()
                            val option: JSONObject = simpleFertilizersArray.getJSONObject(k)
                            var fertilizer: JSONArray = option.getJSONArray("Option")
                            fetilizerJsonObject.put("fertilizer", fertilizer)
                            optionArray.put(k, fetilizerJsonObject)
                        }
                        otionJsonObject.put("Option", optionArray)
                        optionFertilizerDataArray.put(mainIndex, otionJsonObject)
                        mainIndex++
                    }
                    fertilizerOptionValue = optionFertilizerDataArray
                    DebugLog.getInstance().d("fertilizerCalacaltedValue=$fertilizerOptionValue")
                    availableOption = "fertilizerCalacaltedValue"
                    showCalculatorData()

                }
            }
            if (code == 2) {
                if (jSONObject != null) {
                    Log.d("jSONObject", jSONObject.toString())
                    val response = ResponseModel(jSONObject)
                    Log.d("response", response.toString())
                    var tokenData: JSONArray = response.getuserDataArray()
                    Log.d("tokenData", tokenData.toString())
                    var tokenJsonOnject: JSONObject = tokenData.getJSONObject(0)
                    token = tokenJsonOnject.getString("Token")

                    if (!token.isNullOrBlank()) {
                        getCalculatedFertlizerData()
                    }
                }
            }

            if (code == 3) {
                if (jSONObject != null) {
                    Log.d("jSONObject", jSONObject.toString())
                    val response = ResponseModel(jSONObject)
                    if (response.status) {
                        Toast.makeText(
                            this@FertilizerCalculatorAcitvity,
                            response.response,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
            if (code == 4) {
                if (jSONObject != null) {
                    val response = ResponseModel(jSONObject)
                    if (response.status) {
                        fertilizerOptionValue =  response.getdataArray()
                        Log.d("fertilizerOptionValue", fertilizerOptionValue.toString())
                        availableOption = "fertilizerSelectedValue"
                        showCalculatorData()

                    }

                }
            }
        } catch (e: JSONException) {
            e.printStackTrace();
        }
    }

    private fun showCalculatorData() {
        if (fertilizerOptionValue?.length()!! > 0) {
            if (availableOption.equals("fertilizerSelectedValue")){
                availableOptionTv.visibility = View.INVISIBLE
                selectAnyOneOptionTV.visibility = View.GONE
            }else{
                availableOptionTv.visibility = View.VISIBLE
                selectedOptionTv.setBackground(ContextCompat.getDrawable(this, R.drawable.green_bg_gradient))
                availableOptionTv.setBackground(ContextCompat.getDrawable(this, R.drawable.green_gradient_with_yellow_border))
                selectAnyOneOptionTV.visibility = View.VISIBLE
            }
            fertlizerNameRcl.visibility = View.VISIBLE
            fertilizerCal.visibility = View.VISIBLE

            val optionRcladpter =
                OptonRclAdapter(
                    this,
                    this,
                    fertilizerOptionValue,
                    availableOption
                )
            fertlizerNameRcl.setLayoutManager(
                LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            fertlizerNameRcl!!.adapter = optionRcladpter
            optionRcladpter!!.notifyDataSetChanged()
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            Log.d("obj", obj.toString())
            saveOption(obj as JSONObject?)
        }
    }

    private fun saveOption(obj: JSONObject?) {
        val jsonObject = JSONObject()
        var fertilizerOption: JSONArray = obj!!.getJSONArray("Option")
        Log.d("fertilizerOption", fertilizerOption.toString())

        try {
            jsonObject.put("farmer_id", "902")
            jsonObject.put("crop_id", cropId)
            jsonObject.put("soil_test_n", nitrogenValue)
            jsonObject.put("soil_test_p", phosphorusValue)
            jsonObject.put("soil_test_k", potassiumValue)
            jsonObject.put("fym", edtFYMValue)
            jsonObject.put("plot_size", totalAcrArea)
            jsonObject.put("plot_unit", "Acer")
            jsonObject.put("option", fertilizerOption)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppinventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.saveFertilizerFormula(requestBody)
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
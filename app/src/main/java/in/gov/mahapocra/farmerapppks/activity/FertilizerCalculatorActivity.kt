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
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.FertilizersRecyclerAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.app_util.DeleteApi
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class FertilizerCalculatorActivity : AppCompatActivity(), ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener, ApiCallbackCode {

    private lateinit var yesRadioButton: RadioButton
    private lateinit var noRadioButton: RadioButton

    private lateinit var lnrSoilTestNo: LinearLayout
    private lateinit var lnrSoilTestYes: LinearLayout
    private lateinit var lnrNpkFetch: LinearLayout
    private lateinit var fertilizerCal: LinearLayout
    private lateinit var imageMenuShow: ImageView

    private lateinit var textViewHeaderTitle: TextView
    private lateinit var restTv: TextView
    private lateinit var calculateTv: TextView
    private lateinit var fetchNPKTv: TextView
    private lateinit var cropNameTv: TextView
    private lateinit var availableOptionTv: TextView
    private lateinit var selectedOptionTv: TextView
    private lateinit var selectAnyOneOptionTV: TextView
    private lateinit var tvSowingDate: TextView

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
    private lateinit var fertilizerNameRcl: RecyclerView
    private var soilTestOption: Int = 1
    var languageToLoad: String? = null

    private var villageID: Int = 0
    private var cropId: Int? = 0
    private var wotrCropId: String? = null
    private var sowingDate: String? = null
    private lateinit var token: String
    private var acrArea: String = ""
    private var gunthaArea: String = ""
    private var nitrogenValue: String = ""
    private var phosphorusValue: String = ""
    private var potassiumValue: String = ""
    private var edtFYMValue: String = ""
    private var totalAcrArea: Float = 0.0F
    private var cropName: String? = null
    private var availableOption: String = ""
    private var fertilizerOptionValue: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fertilizer_calculator_activity)

        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@FertilizerCalculatorActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }

        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        sowingDate = intent.getStringExtra("sowingDate")
        Log.d("TAGGER", "onCreate: $cropId, $wotrCropId, $sowingDate, $cropName")

        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

        yesRadioButton = findViewById(R.id.yesRadioButton)
        noRadioButton = findViewById(R.id.noRadioButton)
        lnrSoilTestNo = findViewById(R.id.lnr_soil_test_no)
        lnrSoilTestYes = findViewById(R.id.lnr_soil_test_yes)
        lnrNpkFetch = findViewById(R.id.lnr_npk_fetch)
        fertilizerCal = findViewById(R.id.lnrfertilizerCal)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        restTv = findViewById(R.id.restTv)
        calculateTv = findViewById(R.id.calculateTv)
        fetchNPKTv = findViewById(R.id.fetchNPKTv)
        cropNameTv = findViewById(R.id.cropNameTextView)
        imageMenuShow = findViewById(R.id.imageMenushow)
        availableOptionTv = findViewById(R.id.availableOptionTv)
        selectedOptionTv = findViewById(R.id.selectedOptionTv)
        selectAnyOneOptionTV = findViewById(R.id.selectAnyOneOptionTV)
        tvSowingDate = findViewById(R.id.sowingDateTextView)
        edNitrogen = findViewById(R.id.edNitrogen)
        edPhosphorus = findViewById(R.id.edPhosphorus)
        edPotassium = findViewById(R.id.edPotassium)

        edNitrogen1 = findViewById(R.id.edNitrogen1)
        edPhosphorus1 = findViewById(R.id.edPhosphorus1)
        edPotassium1 = findViewById(R.id.edPotassium1)
        edtSurveyNo = findViewById(R.id.edtSurveyNo)
        edtAcre = findViewById(R.id.edtAcre)
        edtGuntha = findViewById(R.id.edtGuntha)
        edtFYM = findViewById(R.id.edtFYM)

        fertilizerNameRcl = findViewById(R.id.fertlizerOpt_Rcl)
        textViewHeaderTitle.setText(R.string.fertilizer_calculator)
        lnrSoilTestNo.visibility = View.GONE
        lnrNpkFetch.visibility = View.GONE

        yesRadioButton.setOnClickListener {
            onRadioButtonClicked(it)
        }
        noRadioButton.setOnClickListener {
            onRadioButtonClicked(it)
        }
        restTv.setOnClickListener {
            resetEditTest()
        }
        calculateTv.setOnClickListener {
            validation()
        }
        fetchNPKTv.setOnClickListener {
            Log.d("restTv ", "restTv")
        }
        selectedOptionTv.setOnClickListener {
            selectedOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_gradient_with_yellow_border
            )
            availableOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_bg_gradient
            )
            getSelectedSavedOption()
        }
        tvSowingDate.text = sowingDate
        tvSowingDate.setOnClickListener {
            startActivity(
                Intent(
                    this@FertilizerCalculatorActivity,
                    SelectSowingDataAndFarmer::class.java
                )
            )
        }
        availableOptionTv.setOnClickListener {
            availableOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_gradient_with_yellow_border
            )
            selectedOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_bg_gradient
            )
            validation()
        }

        imageMenuShow.visibility = View.VISIBLE
        imageMenuShow.setOnClickListener {
            val intent = Intent(this, CropStageAdvisory::class.java)
            intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
            startActivity(intent)
        }

        edtAcre.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!(s.contentEquals(""))) {
                    val acreNo: Int = s.toString().toInt()
                    if (acreNo > 99) {
                        Toast.makeText(
                            this@FertilizerCalculatorActivity,
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
                    val acreNo: Int = s.toString().toInt()
                    if (acreNo > 39) {
                        Toast.makeText(
                            this@FertilizerCalculatorActivity,
                            "Please Enter Guntha Area Should Be Less Than or equal to 39",
                            Toast.LENGTH_SHORT
                        ).show()
                        edtGuntha.setText("0")
                    }
                }
            }
        })
        cropNameTv.text = cropName
    }

    private fun getSelectedSavedOption() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("farmer_id", "902")
            jsonObject.put("crop_id", cropId)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            CoroutineScope(Dispatchers.IO).launch {
                val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
                val responseCall: Call<JsonObject> =
                    apiRequest.getFertilizerSavedFormula(requestBody)
                api.postRequest(responseCall, this@FertilizerCalculatorActivity, 4)
            }
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }

    private fun resetEditTest() {
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
        if (gunthaArea.isBlank()) {
            gunthaArea = "0"
        }

        if (soilTestOption == 0) {
            nitrogenValue = "0"
            phosphorusValue = "0"
            potassiumValue = "0"
            edtFYMValue = "0"
        } else {
            nitrogenValue = edNitrogen.text.toString()
            phosphorusValue = edPhosphorus.text.toString()
            potassiumValue = edPotassium.text.toString()
            edtFYMValue = edtFYM.text.toString()
            if (nitrogenValue == null || phosphorusValue == null || potassiumValue == null) {
                Toast.makeText(
                    this,
                    "Please enter valid numerical values for NPK",
                    Toast.LENGTH_SHORT
                ).show()
                return
            } else if ((nitrogenValue.toInt() < 60 || nitrogenValue.toInt() > 100) ||
                (phosphorusValue.toInt() < 60 || phosphorusValue.toInt() > 100) ||
                (potassiumValue.toInt() < 60 || potassiumValue.toInt() > 100)
            ) {
                Toast.makeText(this, "NPK values should be between 60 and 100", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }
        if (cropId == 0) {
            UIToastMessage.show(
                this,
                resources.getString(R.string.fertilizer_availability_for_crop)
            )
        } else if (acrArea.isBlank()) {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_Acre_Area))
        } else if (nitrogenValue.isBlank()) {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_nitrogenValue))
        } else if (phosphorusValue.isBlank()) {
            UIToastMessage.show(
                this,
                resources.getString(R.string.Please_Enter_Your_phosphorusValue)
            )
        } else if (potassiumValue.isBlank()) {
            UIToastMessage.show(
                this,
                resources.getString(R.string.Please_Enter_Your_potassiumValue)
            )
        } else if (edtFYMValue.isBlank()) {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_FYMValue))
        } else {
            acreAreaCalculation(acrArea.toInt(), gunthaArea.toInt())
        }
    }

    private fun acreAreaCalculation(acrArea: Int, gunthaArea: Int) {
        val result = (gunthaArea * 0.025)
        totalAcrArea = ((acrArea + result).toFloat())
        if (totalAcrArea > 0) {
            try {
                getToken()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            UIToastMessage.show(this, resources.getString(R.string.Please_Enter_Your_Acre_Area))
        }

    }

    private fun getToken() {
        try {
            val api =
                AppInventorApi(this, APIServices.WOTR, "", AppString(this).getkMSG_WAIT(), true)
            CoroutineScope(Dispatchers.IO).launch {
                val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
                val responseCall: Call<JsonObject> =
                    apiRequest.getTokenFromWotr("8470807282", "PMU%40PoCRA%232023")
                api.postRequest(responseCall, this@FertilizerCalculatorActivity, 2)
            }
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCalculatedFertilizerData() {
        val formatter: DateFormat = SimpleDateFormat("dd-mm-yyyy")
        val date = sowingDate?.let { formatter.parse(it) } as Date
        val newFormat = SimpleDateFormat("yyyy-mm-dd")
        val finalSowingDate = newFormat.format(date)
        try {
            val api =
                AppInventorApi(this, APIServices.WOTR, "", AppString(this).getkMSG_WAIT(), true)

            CoroutineScope(Dispatchers.IO).launch {
                val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getFertilizerCalculatedData(
                    wotrCropId, finalSowingDate, soilTestOption.toString(),
                    nitrogenValue, phosphorusValue, potassiumValue,
                    villageID.toString(), edtFYMValue, "0",
                    totalAcrArea.toString(), "1", token
                )
                api.postRequest(responseCall, this@FertilizerCalculatorActivity, 1)
            }
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
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
                        soilTestOption = 1
                        fertilizerOptionValue = null
                        availableOptionTv.visibility = View.INVISIBLE
                        fertilizerNameRcl.visibility = View.GONE
                        fertilizerCal.visibility = View.GONE
                    }

                R.id.noRadioButton ->
                    if (checked) {
                        lnrSoilTestNo.visibility = View.GONE
                        lnrNpkFetch.visibility = View.GONE
                        lnrSoilTestYes.visibility = View.GONE
                        soilTestOption = 0
                        fertilizerOptionValue = null
                        availableOptionTv.visibility = View.INVISIBLE
                        fertilizerNameRcl.visibility = View.GONE
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
            if (jSONObject != null) {
                when (code) {
                    1 -> {
                        availableOptionTv.visibility = View.INVISIBLE
                        Log.d("jSONObject", jSONObject.toString())
                        val simpleFertilizersArray: JSONArray =
                            jSONObject.getJSONArray("SimpleFertilizers")
                        val complexFertilizersArray: JSONArray =
                            jSONObject.getJSONArray("ComplexFertilizers")

                        //Main JSONObject
                        var mainIndex = 0
                        var q = 0
                        var z = 0
                        val optionFertilizerDataArray = JSONArray()
                        var optionJsonObject1 = JSONObject()
                        var optionArray1 = JSONArray()
                        if (complexFertilizersArray.length() > 0) {
                            for (j in 0 until complexFertilizersArray.length()) {
                                // for simple
                                if (j == 0) {
                                    val optionJsonObject = JSONObject()
                                    val optionArray = JSONArray()
                                    for (k in 0 until simpleFertilizersArray.length()) {
                                        val fertilizerJsonObject = JSONObject()
                                        val option: JSONObject =
                                            simpleFertilizersArray.getJSONObject(k)
                                        val fertilizer: JSONArray = option.getJSONArray("Option")
                                        fertilizerJsonObject.put("fertilizer", fertilizer)
                                        optionArray.put(k, fertilizerJsonObject)
                                    }
                                    optionJsonObject.put("Option", optionArray)
                                    optionFertilizerDataArray.put(mainIndex, optionJsonObject)
                                    mainIndex++
                                }
                                // for complex
                                val fertilizerJsonObject1 = JSONObject()
                                val option: JSONObject = complexFertilizersArray.getJSONObject(j)
                                val fertilizer: JSONArray = option.getJSONArray("Option")
                                var cropAgeDays = ""
                                for (m in 0 until 1) {
                                    val optionItem: JSONObject = fertilizer.getJSONObject(m)
                                    cropAgeDays = optionItem.getString("CropAgeDays")
                                }
                                if (cropAgeDays == "0" && q > 1) {
                                    optionJsonObject1.put("Option", optionArray1)
                                    optionFertilizerDataArray.put(mainIndex, optionJsonObject1)
                                    optionJsonObject1 = JSONObject()
                                    optionArray1 = JSONArray()
                                    z = 0
                                    mainIndex++
                                }
                                fertilizerJsonObject1.put("fertilizer", fertilizer)
                                optionArray1.put(z, fertilizerJsonObject1)
                                q++
                                z++
                            }
                            optionJsonObject1.put("Option", optionArray1)
                            optionFertilizerDataArray.put(mainIndex, optionJsonObject1)
                        } else {
                            val optionJsonObject = JSONObject()
                            val optionArray = JSONArray()
                            for (k in 0 until simpleFertilizersArray.length()) {
                                val fertilizerJsonObject = JSONObject()
                                val option: JSONObject = simpleFertilizersArray.getJSONObject(k)
                                val fertilizer: JSONArray = option.getJSONArray("Option")
                                fertilizerJsonObject.put("fertilizer", fertilizer)
                                optionArray.put(k, fertilizerJsonObject)
                            }
                            optionJsonObject.put("Option", optionArray)
                            optionFertilizerDataArray.put(mainIndex, optionJsonObject)
                            mainIndex++
                        }
                        fertilizerOptionValue = optionFertilizerDataArray
                        DebugLog.getInstance().d("fertilizerCalculatedValue=$fertilizerOptionValue")
                        availableOption = "fertilizerCalculatedValue"
                        showCalculatorData()
                    }

                    2 -> {
                        Log.d("jSONObject", jSONObject.toString())
                        val response = ResponseModel(jSONObject)
                        Log.d("response", response.toString())
                        val tokenData: JSONArray = response.getuserDataArray()
                        Log.d("tokenData", tokenData.toString())
                        val tokenJsonObject: JSONObject = tokenData.getJSONObject(0)
                        token = tokenJsonObject.getString("Token")

                        if (token.isNotBlank()) {
                            getCalculatedFertilizerData()
                        }
                    }

                    3 -> {
                        val response = ResponseModel(jSONObject)
                        if (response.status) {
                            Toast.makeText(
                                this@FertilizerCalculatorActivity,
                                response.response,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    4 -> {
                        val response = ResponseModel(jSONObject)
                        if (response.status) {
                            fertilizerOptionValue = response.getdataArray()
                            Log.d("fertilizerOptionValue", fertilizerOptionValue.toString())
                            availableOption = "fertilizerSelectedValue"
                            showCalculatorData()
                        }
                    }

                    5 -> {
                        val message = jSONObject.getString("response")
                        getSelectedSavedOption()
                        Toast.makeText(
                            this@FertilizerCalculatorActivity,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showCalculatorData() {
        if (fertilizerOptionValue?.length()!! > 0) {
            selectedOptionTv.visibility = View.VISIBLE
            availableOptionTv.visibility = View.VISIBLE
            if (availableOption == "fertilizerSelectedValue") {
                availableOptionTv.background =
                    ContextCompat.getDrawable(this, R.drawable.green_bg_gradient)
                selectAnyOneOptionTV.background =
                    ContextCompat.getDrawable(this, R.drawable.green_gradient_with_yellow_border)
            } else {
                selectedOptionTv.background =
                    ContextCompat.getDrawable(this, R.drawable.green_bg_gradient)
                availableOptionTv.background =
                    ContextCompat.getDrawable(this, R.drawable.green_gradient_with_yellow_border)
            }
            fertilizerNameRcl.visibility = View.VISIBLE
            fertilizerCal.visibility = View.VISIBLE

            val optionRclAdapter =
                FertilizersRecyclerAdapter(
                    this,
                    this,
                    fertilizerOptionValue,
                    availableOption
                )
            fertilizerNameRcl.setLayoutManager(
                LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            val itemClickListener: DeleteApi = object : DeleteApi {
                override fun deleteData(id: Int, farmerId: Int, cropId: Int) {
                    val jsonObject = JSONObject()
                    jsonObject.put("id", id)
                    jsonObject.put("farmer_id", farmerId)
                    jsonObject.put("crop_id", cropId)
                    val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
                    val api = AppInventorApi(
                        this@FertilizerCalculatorActivity,
                        APIServices.SSO,
                        "",
                        AppString(this@FertilizerCalculatorActivity).getkMSG_WAIT(),
                        true
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
                        val responseCall: Call<JsonObject> =
                            apiRequest.deleteFertilizerFromSavedList(requestBody)
                        api.postRequest(responseCall, this@FertilizerCalculatorActivity, 5)
                    }
                }
            }
            optionRclAdapter.setItemClickListener(deleteApi = itemClickListener)
            fertilizerNameRcl.adapter = optionRclAdapter
            optionRclAdapter.notifyDataSetChanged()
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
        val fertilizerOption: JSONArray = obj!!.getJSONArray("Option")
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
                AppInventorApi(this, APIServices.SSO, "", AppString(this).getkMSG_WAIT(), true)
            CoroutineScope(Dispatchers.IO).launch {
                val apiRequest = api.getRetrofitInstance().create(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.saveFertilizerFormula(requestBody)
                api.postRequest(responseCall, this@FertilizerCalculatorActivity, 3)
            }
        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=$e")
            e.printStackTrace()
        }
    }
}
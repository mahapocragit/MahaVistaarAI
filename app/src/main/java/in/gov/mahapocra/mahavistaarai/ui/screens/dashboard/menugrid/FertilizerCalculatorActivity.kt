package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.SelectSowingDataAndFarmer
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.FertilizersRecyclerAdapter
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.util.app_util.DeleteApi
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityFertilizerCalculatorActivityBinding
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class FertilizerCalculatorActivity : AppCompatActivity(), ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener, ApiCallbackCode {

    private lateinit var binding: ActivityFertilizerCalculatorActivityBinding

    private var soilTestOption: Int = 1
    var languageToLoad: String? = null

    private var villageID: Int = 0
    private var cropId: Int? = 0
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String? = null
    private var cropName: String? = null
    private lateinit var token: String
    private var acrArea: String = ""
    private var gunthaArea: String = ""
    private var edtFYMValue: String = ""
    private var nitrogenValue: String = ""
    private var potassiumValue: String = ""
    private var phosphorusValue: String = ""
    private var availableOption: String = ""
    private var totalAcrArea: Float = 0.0F
    private var fertilizerOptionValue: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFertilizerCalculatorActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@FertilizerCalculatorActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }

        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        wotrCropId = intent.getStringExtra("wotr_crop_id")
        mUrl = intent.getStringExtra("mUrl")
        sowingDate = intent.getStringExtra("sowingDate")

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
            Log.d("TAGGER", "onCreate: $cropName")
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", mUrl)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD
            )
            startActivity(sharing)
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.acreRadioButton -> {
                    // Acre selected
                    binding.areaTextView.text = getString(R.string.acre)
                    Toast.makeText(this, R.string.acre_selected, Toast.LENGTH_SHORT).show()
                }
                R.id.radioButton2 -> {
                    // Guntha selected
                    binding.areaTextView.text = getString(R.string.hectare)
                    Toast.makeText(this, R.string.hectare_selected, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.sowingInfoLayout.editSowingDateIcon.setOnClickListener {
            val sharing = Intent(this, SelectSowingDataAndFarmer::class.java)
            sharing.putExtra("id", cropId)
            sharing.putExtra("mName", cropName)
            sharing.putExtra("wotr_crop_id", wotrCropId)
            sharing.putExtra("mUrl", mUrl)
            AppPreferenceManager(this).saveString(
                AppConstants.ACTION_FROM_DASHBOARD,
                AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD
            )
            startActivity(sharing)
        }



        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)

        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.fertilizer_calculator)
        binding.lnrSoilTestNo.visibility = View.GONE
        binding.lnrNpkFetch.visibility = View.GONE

        binding.yesRadioButton.setOnClickListener {
            onRadioButtonClicked(it)
        }
        binding.noRadioButton.setOnClickListener {
            onRadioButtonClicked(it)
        }
        binding.restTv.setOnClickListener {
            resetEditTest()
        }
        binding.calculateTv.setOnClickListener {
            validation()
        }
        binding.fetchNPKTv.setOnClickListener {
        }
        binding.selectedOptionTv.setOnClickListener {
            binding.selectedOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_gradient_with_yellow_border
            )
            binding.availableOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_bg_gradient
            )
            getSelectedSavedOption()
        }
        binding.sowingInfoLayout.sowingDateTextView.text = sowingDate
        binding.sowingInfoLayout.sowingDateTextView.setOnClickListener {
            startActivity(
                Intent(
                    this@FertilizerCalculatorActivity,
                    SelectSowingDataAndFarmer::class.java
                )
            )
        }
        binding.availableOptionTv.setOnClickListener {
            binding.availableOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_gradient_with_yellow_border
            )
            binding.selectedOptionTv.background = ContextCompat.getDrawable(
                this,
                R.drawable.green_bg_gradient
            )
            validation()
        }

        binding.edtAcre.addTextChangedListener(object : TextWatcher {
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
                        binding.edtAcre.setText("0")
                    }
                }
            }
        })

        binding.edtGuntha.addTextChangedListener(object : TextWatcher {
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
                        binding.edtGuntha.setText("0")
                    }
                }
            }
        })
        binding.sowingInfoLayout.cropNameTextView.text = cropName
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardScreen::class.java))
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
        binding.edtAcre.setText("0")
        binding.edtGuntha.setText("0")
        binding.edNitrogen.setText("0")
        binding.edPhosphorus.setText("0")
        binding.edPotassium.setText("0")
        binding.edtFYM.setText("0")
        totalAcrArea = 0.0F
    }

    private fun validation() {
        binding.fertlizerOptRcl.visibility = View.VISIBLE
        acrArea = binding.edtAcre.text.toString()
        gunthaArea = binding.edtGuntha.text.toString()
        if (gunthaArea.isBlank()) {
            gunthaArea = "0"
        }

        if (soilTestOption == 0) {
            nitrogenValue = "0"
            phosphorusValue = "0"
            potassiumValue = "0"
            edtFYMValue = "0"
        } else {
            nitrogenValue = binding.edNitrogen.text.toString()
            phosphorusValue = binding.edPhosphorus.text.toString()
            potassiumValue = binding.edPotassium.text.toString()
            edtFYMValue = binding.edtFYM.text.toString()
            if (nitrogenValue == null || phosphorusValue == null || potassiumValue == null) {
                Toast.makeText(
                    this,
                    "Please enter valid numerical values for NPK",
                    Toast.LENGTH_SHORT
                ).show()
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
                        binding.lnrSoilTestYes.visibility = View.VISIBLE
                        binding.lnrSoilTestNo.visibility = View.GONE
                        binding.lnrNpkFetch.visibility = View.GONE
                        soilTestOption = 1
                        fertilizerOptionValue = null
                        binding.availableOptionTv.visibility = View.INVISIBLE
                        binding.fertlizerOptRcl.visibility = View.GONE
                        binding.lnrfertilizerCal.visibility = View.GONE
                    }

                R.id.noRadioButton ->
                    if (checked) {
                        binding.lnrSoilTestNo.visibility = View.GONE
                        binding.lnrNpkFetch.visibility = View.GONE
                        binding.lnrSoilTestYes.visibility = View.GONE
                        soilTestOption = 0
                        fertilizerOptionValue = null
                        binding.availableOptionTv.visibility = View.INVISIBLE
                        binding.fertlizerOptRcl.visibility = View.GONE
                        binding.lnrfertilizerCal.visibility = View.GONE
                    }
            }
        }
    }

    override fun onFailure(th: Throwable?, i: Int) {
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onResponse(jSONObject: JSONObject?, code: Int) {
        try {
            if (jSONObject != null) {
                when (code) {
                    1 -> {
                        binding.availableOptionTv.visibility = View.INVISIBLE
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
                        val response =
                            ResponseModel(
                                jSONObject
                            )
                        val tokenData: JSONArray = response.getuserDataArray()
                        val tokenJsonObject: JSONObject = tokenData.getJSONObject(0)
                        token = tokenJsonObject.getString("Token")

                        if (token.isNotBlank()) {
                            getCalculatedFertilizerData()
                        }
                    }

                    3 -> {
                        val response =
                            ResponseModel(
                                jSONObject
                            )
                        if (response.status) {
                            Toast.makeText(
                                this@FertilizerCalculatorActivity,
                                response.response,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    4 -> {
                        val response =
                            ResponseModel(
                                jSONObject
                            )
                        if (response.status) {
                            fertilizerOptionValue = response.getdataArray()
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
            binding.fertlizerOptRcl.visibility = View.VISIBLE
            binding.selectedOptionTv.visibility = View.VISIBLE
            binding.availableOptionTv.visibility = View.VISIBLE
            if (availableOption == "fertilizerSelectedValue") {
                binding.availableOptionTv.background =
                    ContextCompat.getDrawable(this, R.drawable.green_bg_gradient)
                binding.selectAnyOneOptionTV.background =
                    ContextCompat.getDrawable(this, R.drawable.green_gradient_with_yellow_border)
            } else {
                binding.selectedOptionTv.background =
                    ContextCompat.getDrawable(this, R.drawable.green_bg_gradient)
                binding.availableOptionTv.background =
                    ContextCompat.getDrawable(this, R.drawable.green_gradient_with_yellow_border)
            }
            binding.fertlizerOptRcl.visibility = View.VISIBLE
            binding.lnrfertilizerCal.visibility = View.VISIBLE

            val optionRclAdapter =
                FertilizersRecyclerAdapter(
                    this,
                    this,
                    fertilizerOptionValue,
                    availableOption
                )
            binding.fertlizerOptRcl.setLayoutManager(
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
            binding.fertlizerOptRcl.adapter = optionRclAdapter
            optionRclAdapter.notifyDataSetChanged()
        }else{
            binding.fertlizerOptRcl.visibility = View.GONE
            Toast.makeText(this, "NO DATA FOUND", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            saveOption(obj as JSONObject?)
        }
    }

    private fun saveOption(obj: JSONObject?) {
        val jsonObject = JSONObject()
        val fertilizerOption: JSONArray = obj!!.getJSONArray("Option")
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
            e.printStackTrace()
        }
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.ApiJSONObjCallback
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityFertilizerCalculatorActivityBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.FertilizersRecyclerAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.DateHelper.showDisabledFutureDatePicker
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ScoreBubbleHelper
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import `in`.gov.mahapocra.mahavistaarai.util.app_util.DeleteApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class FertilizerCalculatorActivity : AppCompatActivity(), ApiJSONObjCallback,
    OnMultiRecyclerItemClickListener, ApiCallbackCode, DatePickerRequestListener {

    private lateinit var binding: ActivityFertilizerCalculatorActivityBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var soilTestOption: Int = 0
    private lateinit var languageToLoad: String

    private var villageID: Int = 0
    private var cropId: Int? = 0
    private var wotrCropId: String? = null
    private var mUrl: String? = null
    private var sowingDate: String? = null
    private var cropName: String? = null
    private lateinit var token: String
    private var route: String = ""
    private var acrArea: String = ""
    private var gunthaArea: String = ""
    private var edtFYMValue: String = ""
    private var nitrogenValue: String = ""
    private var potassiumValue: String = ""
    private var phosphorusValue: String = ""
    private var availableOption: String = ""
    private var totalAcrArea: Float = 0.0F
    private var plotUnitCode = 3
    private var fertilizerOptionValue: JSONArray? = null
    private val date = Date()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@FertilizerCalculatorActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityFertilizerCalculatorActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.bubbleIconImageView.animate()
                .alpha(0f)
                .setDuration(500) // animation duration in ms
                .withEndAction {
                    binding.bubbleIconImageView.visibility = View.GONE
                    binding.bubbleIconImageView.alpha = 1f // reset alpha in case you show it again
                }
                .start()
        }
        cropId = intent.getIntExtra("id", 0)
        cropName = intent.getStringExtra("mName")
        wotrCropId = intent.getIntExtra("wotr_crop_id", 0).toString()
        mUrl = intent.getStringExtra("mUrl")
        sowingDate = intent.getStringExtra("sowingDate")

        binding.sowingInfoLayout.cropInfoCardView.setOnClickListener {
            val sharing = Intent(this, AddCropActivity::class.java)
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
        settingUpTheViewsAsPerLanguage()
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.acreRadioButton -> {
                    // Acre selected
                    plotUnitCode = 3
                    binding.areaTextView.text = getString(R.string.acre)
                    Toast.makeText(this, R.string.acre_selected, Toast.LENGTH_SHORT).show()
                }

                R.id.radioButton2 -> {
                    // Hectar selected
                    plotUnitCode = 1
                    binding.areaTextView.text = getString(R.string.hectare)
                    Toast.makeText(this, R.string.hectare_selected, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.sowingInfoLayout.editSowingDateIcon.setOnClickListener {
            showDisabledFutureDatePicker(
                this,
                date,
                1,
                this
            )
        }

        villageID = AppSettings.getInstance().getIntValue(this, AppConstants.uVILLAGEID, 0)
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.fertilizer_calculator)

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
            ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
            validation()
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
        var isUpdating = false
        binding.edtAcre.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return  // Prevent recursive calls when setting text programmatically

                val acreNo = s?.toString()?.toIntOrNull() ?: return  // Safely convert to Int, skip if invalid

                if (acreNo > 99) {
                    val messageRes = if (plotUnitCode == 3)
                        R.string.area_acre_exceed_warning
                    else
                        R.string.area_hectare_exceed_warning

                    Toast.makeText(this@FertilizerCalculatorActivity, messageRes, Toast.LENGTH_SHORT).show()

                    isUpdating = true
                    binding.edtAcre.setText("0")
                    binding.edtAcre.setSelection(binding.edtAcre.text.length)
                    isUpdating = false
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
                            R.string.guntha_exceed_warning,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.edtGuntha.setText("0")
                    }
                }
            }
        })
        binding.sowingInfoLayout.cropNameTextView.text = cropName
        val isGuest = AppSettings.getInstance().getBooleanValue(this, AppConstants.IS_USER_GUEST, false)
        binding.chatbotIcon.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f
            private var startX = 0f
            private var startY = 0f
            private val CLICK_THRESHOLD = 20 // px movement allowed

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        startX = event.rawX
                        startY = event.rawY
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val parent = v.parent as View
                        val newX = event.rawX + dX
                        val newY = event.rawY + dY

                        // calculate boundaries (you can adjust margin if needed)
                        val margin = 32 // px margin from edges
                        val maxX = parent.width - v.width - margin
                        val maxY = parent.height - v.height - margin
                        val minX = margin
                        val minY = margin

                        // constrain movement inside screen
                        val boundedX = newX.coerceIn(minX.toFloat(), maxX.toFloat())
                        val boundedY = newY.coerceIn(minY.toFloat(), maxY.toFloat())

                        v.animate()
                            .x(boundedX)
                            .y(boundedY)
                            .setDuration(0)
                            .start()
                    }

                    MotionEvent.ACTION_UP -> {
                        val diffX = abs(event.rawX - startX)
                        val diffY = abs(event.rawY - startY)

                        if (diffX < CLICK_THRESHOLD && diffY < CLICK_THRESHOLD) {
                            if (!isGuest) {
                                startActivity(Intent(this@FertilizerCalculatorActivity, ChatbotActivity::class.java))
                            } else {
                                AlertDialog.Builder(this@FertilizerCalculatorActivity)
                                    .setMessage(R.string.bot_chat_login_redirect_mesage)
                                    .setPositiveButton(R.string.yes) { dialog, _ ->
                                        startActivity(Intent(this@FertilizerCalculatorActivity, LoginScreen::class.java).apply {
                                            putExtra("from", "dashboard")
                                        })
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                        }
                    }
                }
                return true
            }
        })

        onBackPressedDispatcher.addCallback( object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(this@FertilizerCalculatorActivity, DashboardScreen::class.java))
            }
        })
    }

    private fun settingUpTheViewsAsPerLanguage() {
        binding.sowingInfoLayout.textView7.text = getString(R.string.sowing_date)
        binding.plotSizeTitleTextView.text = getString(R.string.plot_size)
        binding.acreRadioButton.text = getString(R.string.acre)
        binding.radioButton2.text = getString(R.string.hectare)
    }

    private fun getSelectedSavedOption() {
        val farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("farmer_id", farmerId)
            jsonObject.put("crop_id", cropId)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    AppEnvironment.FARMER.baseUrl,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            CoroutineScope(Dispatchers.IO).launch {
                val apiRequest = api.getRetrofitInstance().create(ApiService::class.java)
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
            if (plotUnitCode == 3) {
                Toast.makeText(
                    this@FertilizerCalculatorActivity,
                    R.string.Please_Enter_Your_Acre_Area,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@FertilizerCalculatorActivity,
                    R.string.Please_Enter_Your_Hectare_Area,
                    Toast.LENGTH_SHORT
                ).show()
            }
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
                AppInventorApi(
                    this,
                    AppEnvironment.WOTR.baseUrl,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            CoroutineScope(Dispatchers.IO).launch {
                val apiRequest = api.getRetrofitInstance().create(ApiService::class.java)
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

        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date = formatter.parse(sowingDate ?: "")
        val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val finalSowingDate = newFormat.format(date!!)
        try {
            val api =
                AppInventorApi(
                    this,
                    AppEnvironment.WOTR.baseUrl,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )

            CoroutineScope(Dispatchers.IO).launch {
                val apiRequest = api.getRetrofitInstance().create(ApiService::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getFertilizerCalculatedData(
                    wotrCropId, finalSowingDate, soilTestOption.toString(),
                    nitrogenValue, phosphorusValue, potassiumValue,
                    villageID.toString(), edtFYMValue, "0",
                    totalAcrArea.toString(), plotUnitCode.toString(), token
                )
                api.postRequest(responseCall, this@FertilizerCalculatorActivity, 1)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.id) {
                R.id.yesRadioButton ->
                    if (checked) {
                        binding.lnrSoilTestYes.visibility = View.VISIBLE
                        soilTestOption = 1
                        fertilizerOptionValue = null
                        binding.availableOptionTv.visibility = View.INVISIBLE
                        binding.fertlizerOptRcl.visibility = View.GONE
                        binding.lnrfertilizerCal.visibility = View.GONE
                    }

                R.id.noRadioButton ->
                    if (checked) {
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
                            binding.noDataFoundImageView.visibility = View.GONE
                            binding.noDataFoundTextView.visibility = View.GONE
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
                        AppEnvironment.FARMER.baseUrl,
                        "",
                        AppString(this@FertilizerCalculatorActivity).getkMSG_WAIT(),
                        true
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val apiRequest = api.getRetrofitInstance().create(ApiService::class.java)
                        val responseCall: Call<JsonObject> =
                            apiRequest.deleteFertilizerFromSavedList(requestBody)
                        api.postRequest(responseCall, this@FertilizerCalculatorActivity, 5)
                    }
                }
            }
            optionRclAdapter.setItemClickListener(deleteApi = itemClickListener)
            binding.fertlizerOptRcl.adapter = optionRclAdapter
            optionRclAdapter.notifyDataSetChanged()
        } else {
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
        // Step 1: Null check
        if (obj == null) {
            Log.e("FertilizerCalc", "saveOption: Input JSONObject is null")
            return
        }

        // Step 2: Extract JSONArray safely
        val fertilizerOption = obj.optJSONArray("Option")
        if (fertilizerOption == null) {
            Log.e("FertilizerCalc", "saveOption: 'Option' key is missing or not a JSONArray")
            return
        }

        // Optional: Warn if array is empty
        if (fertilizerOption.length() == 0) {
            Log.w("FertilizerCalc", "saveOption: 'Option' array is empty")
        }

        try {
            // Step 3: Construct JSON body
            val farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)

            val jsonObject = JSONObject().apply {
                put("farmer_id", farmerId)
                put("crop_id", cropId)
                put("soil_test_n", nitrogenValue)
                put("soil_test_p", phosphorusValue)
                put("soil_test_k", potassiumValue)
                put("fym", edtFYMValue)
                put("plot_size", totalAcrArea)
                put("plot_unit", binding.areaTextView.text.toString())
                put("option", fertilizerOption)
            }

            // Step 4: Prepare API request
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppInventorApi(
                this,
                AppEnvironment.FARMER.baseUrl,
                "",
                AppString(this).getkMSG_WAIT(),
                true
            )

            // Step 5: Make async network request using coroutine
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val apiRequest = api.getRetrofitInstance().create(ApiService::class.java)
                    val responseCall: Call<JsonObject> =
                        apiRequest.saveFertilizerFormula(requestBody)
                    api.postRequest(responseCall, this@FertilizerCalculatorActivity, 3)
                } catch (e: Exception) {
                    Log.e("FertilizerCalc", "API request failed", e)
                }
            }
        } catch (e: JSONException) {
            Log.e("FertilizerCalc", "saveOption: JSON exception", e)
        }
    }

    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            sowingDate = "$day-$month-$year"
            cropId?.let { farmerViewModel.saveFarmerSelectedCrop(this, sowingDate!!, it) }
            farmerViewModel.saveFarmerSelectedCrop.observe(this) {
                if (it != null) {
                    if (it.get("status").toString() == "200") {
                        binding.sowingInfoLayout.sowingDateTextView.text = sowingDate
                    }
                }
            }
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
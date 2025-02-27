package `in`.gov.mahapocra.farmerapppks.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.farmerapppks.AppPreferenceManager
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.activity.AboutPocra
import `in`.gov.mahapocra.farmerapppks.activity.AddCropActivity
import `in`.gov.mahapocra.farmerapppks.activity.AdvisoryCropActivity
import `in`.gov.mahapocra.farmerapppks.activity.BandavarActivity
import `in`.gov.mahapocra.farmerapppks.activity.ClimateResilintTechnology
import `in`.gov.mahapocra.farmerapppks.activity.ComingSoonActivity
import `in`.gov.mahapocra.farmerapppks.activity.DbtSchemes
import `in`.gov.mahapocra.farmerapppks.activity.DbtStatus
import `in`.gov.mahapocra.farmerapppks.activity.FertilizerCalculatorActivity
import `in`.gov.mahapocra.farmerapppks.activity.GisActivity
import `in`.gov.mahapocra.farmerapppks.activity.Grievances
import `in`.gov.mahapocra.farmerapppks.activity.HealthCardActivity
import `in`.gov.mahapocra.farmerapppks.activity.LoginScreen
import `in`.gov.mahapocra.farmerapppks.activity.MarketPrice
import `in`.gov.mahapocra.farmerapppks.activity.MyVillageProfilePdf
import `in`.gov.mahapocra.farmerapppks.activity.PestsAndDiseasesStages
import `in`.gov.mahapocra.farmerapppks.activity.Registration
import `in`.gov.mahapocra.farmerapppks.activity.SplashScreenActivity
import `in`.gov.mahapocra.farmerapppks.activity.TempDashboardActivity
import `in`.gov.mahapocra.farmerapppks.activity.TrainingLocationSelection
import `in`.gov.mahapocra.farmerapppks.activity.Warehouse
import `in`.gov.mahapocra.farmerapppks.activity.WeatherTempActivity
import `in`.gov.mahapocra.farmerapppks.adapter.DashboardAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.DrawerMenuAdapter
import `in`.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.Identify.Identify_dashboard
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.ApUtil
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppHelper
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.databinding.FragmentDashboardBinding
import `in`.gov.mahapocra.farmerapppks.models.response.CropsCategName
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import `in`.gov.mahapocra.farmerapppks.notification.NotificationListActivity
import `in`.gov.mahapocra.farmerapppks.webServices.ForceUpdateChecker
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DashboardFragment : Fragment(), ForceUpdateChecker.OnUpdateNeededListener,
    OnItemClickListener, ApiCallbackCode {

    private lateinit var binding: FragmentDashboardBinding
    private val TAG = "DashboardScreenTAG"
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var appPreferenceManager: AppPreferenceManager

    private var languageToLoad: String? = null
    private var farmerId: Int = 0
    private var cropId: Int = 0
    private var savedCropName: String = ""
    private var savedCropId: Int = 0
    private var savedCropSowingDate: String? = null
    private var savedCropWoTRId: String? = null
    private var savedCropImageUrl: String? = null
    private var jsonArray: JSONArray? = null
    private var showToast: Boolean = true

    private val arrayCategory = arrayOf(
        "Pest and Diseases", "Identify Pest/Disease", "Crop Advisory", "Fertilizer Calculator",
        "Climate Resilient Technology", "Soil Health Card", "Market Price",
        "Warehouse Availabilities", "DBT Schemes"
    )

    private val arrayCategoryMarathi = arrayOf(
        "कीड व रोग", "किटक/रोग ओळखा", "पीक सल्ला", "खत मात्रा गणक (कॅलक्यूलेटर)",
        "हवामान अनुकूल तंत्रज्ञान", "मृदा आरोग्य पत्रिका", "बाजारभाव",
        "गोदाम उपलब्धता", "थेट लाभ हस्तांतरण योजना"
    )

    private val arrayCategoryImg = intArrayOf(
        R.drawable.ladybug, R.drawable.pest, R.drawable.ecology, R.drawable.fertilizer,
        R.drawable.climate_change, R.drawable.soil, R.drawable.commodity,
        R.drawable.warehouse, R.drawable.ic_dbt
    )

    private var selectedCropList = ArrayList<CropsCategName>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        appPreferenceManager = AppPreferenceManager(requireContext())

        // Set language
        if (AppSettings.getLanguage(requireContext()) == "2") {
            languageToLoad = "hi"
        }
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        binding.dashboardScreen.dashboardScreenLayout.textView7.text = getFormattedTimestamp()

        binding.dashboardScreen.dashboardScreenLayout.temperatureLayout.setOnClickListener {
            startActivity(Intent(requireContext(), WeatherTempActivity::class.java))
        }

        binding.dashboardScreen.dashboardScreenLayout.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), TempDashboardActivity::class.java))
        }

        getFirebaseTokenFromServer()
        ForceUpdateChecker.with(requireContext()).onUpdateNeeded(this).check()
        setConfiguration()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        setupToolbarAndDrawer()

        // Set Data
        val userName = AppSettings.getInstance().getValue(requireContext(), AppConstants.uName, AppConstants.uName)
        val userNumber = AppSettings.getInstance().getValue(requireContext(), AppConstants.uMobileNo, AppConstants.uMobileNo)

        val hView = binding.navView.getHeaderView(0)
        val navUserName = hView.findViewById<TextView>(R.id.tv_farmerName)
        val navUserPhone = hView.findViewById<TextView>(R.id.tv_famerPhoneNumber)

        if (userName != "USER_NAME") {
            val capitalizeStrName = ApUtil.getCamelCaseStreing(userName)
            navUserName.text = capitalizeStrName.ifEmpty { userName }
            navUserPhone.text = userNumber
        }

        setupGridView()
        appPreferenceManager.clearAll()
        dashboardGridItemsLayoutSetup()


        binding.dashboardScreen.imgLangChange.setOnClickListener { openChangeLangPopup() }

        binding.dashboardScreen.imgNotification.setOnClickListener {
            val intent = Intent(requireContext(), ComingSoonActivity::class.java)
            intent.putExtra("notification", "mayu")
            startActivity(intent)
        }

//        binding.dashboardScreen.imgCallIcon.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                callingFun()
//            }
//        }
//
//        binding.dashboardScreen.dashboardScreenLayout.AddCropTv.setOnClickListener {
//            val intent = Intent(requireContext(), AddCropActivity::class.java)
//            appPreferenceManager.clearPreference(AppConstants.ACTION_FROM_DASHBOARD)
//            startActivity(intent)
//        }
//
//        setVersion()
//        getFarmerSelectedCrop(languageToLoad)
//        requestingPermissions()
//
//        val talukaID = AppSettings.getInstance().getIntValue(requireContext(), AppConstants.uTALUKAID, 0)
//        val talukaName = AppSettings.getInstance().getSavedValue(requireContext(), AppConstants.uTALUKA)
//
//        if (talukaID != 0) {
//            callForWeatherApi(talukaID)
//            binding.dashboardScreen.dashboardScreenLayout.weatherTalukaTV.text = talukaName
//        }

        return binding.root
    }

    private fun getFormattedTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getFirebaseTokenFromServer() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d(TAG, "FCM Token: $token")
        }
    }

    override fun onUpdateNeeded(updateUrl: String?) {
        val dialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle("New version available")
            .setMessage("Please, update app to new version to continue reposting.")
            .setPositiveButton("Update") { _, _ -> redirectStore(updateUrl) }
            .setNegativeButton("No, thanks") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun redirectStore(updateUrl: String?){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setConfiguration() {
        try {
            jsonArray = when {
                languageToLoad.equals("en", ignoreCase = true) -> AppHelper.getInstance().menuOption
                languageToLoad.equals("hi", ignoreCase = true) -> AppHelper.getInstance().menuOptionMarathi
                else -> return
            }

            val menuAdapter = DrawerMenuAdapter(requireContext(), jsonArray, farmerId)
            binding.menuListView1.adapter = menuAdapter
            binding.menuListView1.onItemClickListener = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupToolbarAndDrawer() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.dashboardScreen.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
            requireActivity(), binding.drawerLayout1, binding.dashboardScreen.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout1.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerSlideAnimationEnabled = true
    }

    private fun setupGridView() {
        binding.dashboardScreen.dashboardScreenLayout.gridViewJobs.numColumns = GridView.AUTO_FIT
        val adapter = if (languageToLoad == "en") {
            DashboardAdapter(requireContext(), arrayCategory, arrayCategoryImg, "single_item_grid")
        } else {
            DashboardAdapter(requireContext(), arrayCategoryMarathi, arrayCategoryImg, "single_item_grid")
        }
        binding.dashboardScreen.dashboardScreenLayout.gridViewJobs.adapter = adapter
    }

    private fun dashboardGridItemsLayoutSetup() {
        binding.dashboardScreen.dashboardScreenLayout.gridViewJobs.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    if (savedCropName.isEmpty()) {
                        val sharing = Intent(requireContext(), AddCropActivity::class.java)
                        appPreferenceManager.saveString(AppConstants.ACTION_FROM_DASHBOARD, AppConstants.PEST_AND_DISEASES_STAGES)
                        startActivity(sharing)
                    } else {
                        val intentPestsAndDiseases = Intent(requireContext(), PestsAndDiseasesStages::class.java).apply {
                            putExtra("cropId", savedCropId)
                            putExtra("wotr_crop_id", savedCropWoTRId)
                            putExtra("sowingDate", savedCropSowingDate)
                            putExtra("mUrl", savedCropImageUrl)
                            putExtra("mName", savedCropName)
                        }
                        startActivity(intentPestsAndDiseases)
                    }
                }
                1 -> startActivity(Intent(requireContext(), Identify_dashboard::class.java))
                2 -> {
                    if (savedCropName.isEmpty()) {
                        val sharing = Intent(requireContext(), AddCropActivity::class.java)
                        appPreferenceManager.saveString(AppConstants.ACTION_FROM_DASHBOARD, AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD)
                        startActivity(sharing)
                    } else {
                        val intent = Intent(requireContext(), AdvisoryCropActivity::class.java).apply {
                            putExtra("id", savedCropId)
                            putExtra("wotr_crop_id", savedCropWoTRId)
                            putExtra("mUrl", savedCropImageUrl)
                            putExtra("mName", savedCropName)
                        }
                        startActivity(intent)
                    }
                }
                3 -> {
                    if (savedCropName.isEmpty()) {
                        val comingSoonIntent = Intent(requireContext(), AddCropActivity::class.java)
                        appPreferenceManager.saveString(AppConstants.ACTION_FROM_DASHBOARD, AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD)
                        startActivity(comingSoonIntent)
                    } else {
                        val intent = Intent(requireContext(), FertilizerCalculatorActivity::class.java).apply {
                            putExtra("id", savedCropId)
                            putExtra("wotr_crop_id", savedCropWoTRId)
                            putExtra("mUrl", savedCropImageUrl)
                            putExtra("mName", savedCropName)
                            putExtra("sowingDate", savedCropSowingDate)
                        }
                        startActivity(intent)
                    }
                }
                4 -> startActivity(Intent(requireContext(), ClimateResilintTechnology::class.java))
                5 -> startActivity(Intent(requireContext(), HealthCardActivity::class.java))
                6 -> startActivity(Intent(requireContext(), MarketPrice::class.java))
                7 -> startActivity(Intent(requireContext(), Warehouse::class.java))
                8 -> startActivity(Intent(requireContext(), DbtSchemes::class.java))
            }
        }
    }

    private fun openChangeLangPopup() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_language_selector)

        val tvEnglish: TextView = dialog.findViewById(R.id.tv_eng)
        val tvMarathi: TextView = dialog.findViewById(R.id.tv_mar)

        tvEnglish.setOnClickListener {
            changeLanguage("en", "1")
            dialog.dismiss()
        }

        tvMarathi.setOnClickListener {
            changeLanguage("hi", "2")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun changeLanguage(languageCode: String, languagePref: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        AppSettings.setLanguage(requireContext(), languagePref)

        requireActivity().recreate() // Refresh the activity to apply changes
        getFarmerSelectedCrop(languageCode)
    }

    private fun getFarmerSelectedCrop(language: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("lang", language)
            jsonObject.put("farmer_id", farmerId)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(requireContext(), APIServices.SSO, "", AppString(requireContext()).getkMSG_WAIT(), true)

            val handler = Handler()
            val runnable = Runnable {
                val retrofit: Retrofit = api.getRetrofitInstance()
                val apiRequest: APIRequest = retrofit.create<APIRequest>(APIRequest::class.java)
                val responseCall: Call<JsonObject> = apiRequest.getFarmersSelectedCrop(requestBody)
                api.postRequest(responseCall, this, 2)
            }
            handler.post(runnable)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, l: Long) {
        try {
            val jsonObject = jsonArray?.getJSONObject(position)
            val id = jsonObject?.getInt("id")
            val navView = binding.navView

            when (id) {
                0 -> {
                    val intent = Intent(requireContext(), Registration::class.java)
                    intent.putExtra("FAAPRegistrationID", farmerId)
                    startActivity(intent)
                }
                1 -> {
                    if (farmerId > 0) {
                        val sharing = Intent(requireContext(), BandavarActivity::class.java)
                        startActivity(sharing)
                    } else {
                        UIToastMessage.show(requireContext(), "Please Login First...")
                    }
                }
                2 -> {
                    val dbtStatusIntent = Intent(requireContext(), DbtStatus::class.java)
                    dbtStatusIntent.putExtra("userPhoneNumber", navView.findViewById<TextView>(R.id.tv_famerPhoneNumber).text.toString())
                    startActivity(dbtStatusIntent)
                }
                3 -> startActivity(Intent(requireContext(), NotificationListActivity::class.java))
                4 -> startActivity(Intent(requireContext(), GisActivity::class.java))
                5 -> startActivity(Intent(requireContext(), TrainingLocationSelection::class.java))
                6 -> startActivity(Intent(requireContext(), LoginScreen::class.java))
                7 -> logoutFromApp()
                10 -> startActivity(Intent(requireContext(), AboutPocra::class.java))
                11 -> startActivity(Intent(requireContext(), MyVillageProfilePdf::class.java))
                12 -> startActivity(Intent(requireContext(), DbtStatus::class.java))
                13 -> {
                    if (farmerId > 0) {
                        val grievanceIntent = Intent(requireContext(), Grievances::class.java)
                        grievanceIntent.putExtra("FAAPRegistrationID", farmerId)
                        startActivity(grievanceIntent)
                    } else {
                        UIToastMessage.show(requireContext(), "Please Login First...")
                    }
                }
            }

            if (binding.drawerLayout1.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout1.closeDrawer(GravityCompat.START)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun logoutFromApp(){
        AppSettings.getInstance().setValue(requireContext(), AppConstants.uName, AppConstants.uName)
        AppSettings.getInstance().setValue(requireContext(), AppConstants.uMobileNo, AppConstants.uMobileNo)
        AppSettings.getInstance().setValue(requireContext(), AppConstants.uEmail, AppConstants.uEmail)
        AppSettings.getInstance().setIntValue(requireContext(), AppConstants.fREGISTER_ID, 0)
        AppSettings.getInstance().setValue(requireContext(), AppConstants.uDIST, AppConstants.uDIST)
        AppSettings.getInstance().setIntValue(requireContext(), AppConstants.uDISTId, 0)
        AppSettings.getInstance().setValue(requireContext(), AppConstants.uTALUKA, AppConstants.uTALUKA)
        AppSettings.getInstance().setIntValue(requireContext(), AppConstants.uTALUKAID, 0)
        AppSettings.getInstance().setValue(requireContext(), AppConstants.uVILLAGE, AppConstants.uVILLAGE)
        AppSettings.getInstance().setIntValue(requireContext(), AppConstants.uVILLAGEID, 0)
        AppSettings.getInstance().setList(requireContext(), AppConstants.kFarmerCrop, null)
        AppUtility.getInstance().clearAppSharedPrefData(requireContext(), AppConstants.kSHARED_PREF)
        AppSettings.getInstance().setBooleanValue(requireContext(), AppConstants.userDataSaved, false)
        val intent = Intent(requireContext(), SplashScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jsonObject: JSONObject?, requestCode: Int) {
        jsonObject ?: return  // Exit if the response is null

        when (requestCode) {
            1 -> {
                val registrationResponse = ResponseModel(jsonObject)
                if (registrationResponse.status) {
                    try {
                        val strName = jsonObject.getString("Name")
                        val strMobNo = jsonObject.getString("MobileNo")
                        val strEmailId = jsonObject.getString("EmailId")
                        val strFFAReg = jsonObject.getInt("FAAPRegistrationID")
                        val strDistName = jsonObject.getString("DistrictName")
                        val strDistId = jsonObject.getInt("DistrictID")
                        val strTalukaName = jsonObject.getString("TalukaName")
                        val strTalukaId = jsonObject.getInt("TalukaID")
                        val strVillageId = jsonObject.getInt("VillageID")
                        val strVillageName = jsonObject.getString("VillageName")

                        val appSettings = AppSettings.getInstance()
                        with(appSettings) {
                            setValue(requireContext(), AppConstants.uName, strName)
                            setValue(requireContext(), AppConstants.uMobileNo, strMobNo)
                            setValue(requireContext(), AppConstants.uEmail, strEmailId)
                            setIntValue(requireContext(), AppConstants.fREGISTER_ID, strFFAReg)
                            setValue(requireContext(), AppConstants.uDIST, strDistName)
                            setIntValue(requireContext(), AppConstants.uDISTId, strDistId)
                            setValue(requireContext(), AppConstants.uTALUKA, strTalukaName)
                            setIntValue(requireContext(), AppConstants.uTALUKAID, strTalukaId)
                            setValue(requireContext(), AppConstants.uVILLAGE, strVillageName)
                            setIntValue(requireContext(), AppConstants.uVILLAGEID, strVillageId)
                            setBooleanValue(requireContext(), AppConstants.userDataSaved, true)
                        }

                        val userName = appSettings.getValue(requireContext(), AppConstants.uName, AppConstants.uName)
                        val userNumber = appSettings.getValue(requireContext(), AppConstants.uMobileNo, AppConstants.uMobileNo)

                        val hView = binding.navView.getHeaderView(0)
                        val nav_user_name = hView.findViewById<TextView>(R.id.tv_farmerName)
                        val nav_user_phone = hView.findViewById<TextView>(R.id.tv_famerPhoneNumber)

                        if (userName != "USER_NAME") {
                            try {
                                val capitalizeStrName = ApUtil.getCamelCaseStreing(userName)
                                nav_user_name.text = if (capitalizeStrName.isNotEmpty()) capitalizeStrName else userName
                                nav_user_phone.text = userNumber
                            } catch (e: StringIndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }

//                        callForWeatherApi(strTalukaId)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            2 -> {
                val farmersSelectedCropResponse = ResponseModel(jsonObject)
                if (farmersSelectedCropResponse.status) {
                    val selectedCrops = farmersSelectedCropResponse.dataArrays
                    selectedCropList = ArrayList()

                    if (selectedCrops.length() > 0) {
                        for (j in 0 until selectedCrops.length()) {
                            try {
                                val selectedCrop = selectedCrops.getJSONObject(j)
                                savedCropId = selectedCrop.getInt("crop_id")
                                savedCropName = selectedCrop.getString("name")
                                savedCropImageUrl = selectedCrop.getString("image")
                                savedCropWoTRId = selectedCrop.getString("wotr_crop_id")

                                binding.dashboardScreen.dashboardScreenLayout.addChangeCropTV.text = "Change Crop"
                                binding.dashboardScreen.dashboardScreenLayout.addChangeCropIV.setImageDrawable(
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_swap))
                                binding.dashboardScreen.dashboardScreenLayout.savedCropNameCardView.visibility = View.VISIBLE
                                binding.dashboardScreen.dashboardScreenLayout.savedCropNameTextView.text = savedCropName
                                Picasso.get().load(savedCropImageUrl).fit().centerCrop().into(binding.dashboardScreen.dashboardScreenLayout.savedCropNameImageView)
                                binding.dashboardScreen.dashboardScreenLayout.yourCropTv.visibility = View.GONE
//                                getCropStagesAndAdvisory()

                                selectedCropList.add(
                                    CropsCategName(
                                        selectedCrop.getInt("crop_id"),
                                        selectedCrop.getString("name"),
                                        selectedCrop.getString("image"),
                                        selectedCrop.getString("wotr_crop_id")
                                    )
                                )
                            } catch (e: JSONException) {
                                throw RuntimeException(e)
                            }
                        }

//                        AppSettings.getInstance().setList(requireContext(), AppConstants.kFarmerCrop, selectedCropList)
                    } else {
//                        savedCropNameCardView.visibility = View.GONE
//                        yourCropTv.visibility = View.VISIBLE
//                        yourCropTv.text = "No crop added"
//                        addChangeCropTV.text = "Add Crop"
//                        addChangeCropIV.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_add_24))
                    }

                    if (selectedCropList.isNotEmpty()) {
//                        alertHeadingTv.visibility = View.GONE
//                        showCropList(selectedCropList)
                    } else {
//                        selectedCropListRecyc.visibility = View.GONE
                    }
                } else {
                    UIToastMessage.show(requireContext(), farmersSelectedCropResponse.response)
                }
//                updateSavedCropDetails()
            }

            3 -> {
                val deleteSelectedCropResponse = ResponseModel(jsonObject)
                if (deleteSelectedCropResponse.status) {
                    if (showToast) {
                        UIToastMessage.show(requireContext(), deleteSelectedCropResponse.response)
                    }
                    AppSettings.getInstance().setList(requireContext(), AppConstants.kFarmerCrop, null)
                    selectedCropList.clear()
//                    getFarmerSelectedCrop(languageToLoad)
                } else {
                    UIToastMessage.show(requireContext(), deleteSelectedCropResponse.response)
                }
            }

            4 -> {
                if (jsonObject != null) {
                    appPreferenceManager.saveString("CROP_STAGE_RESPONSE", jsonObject.toString())
                    val response = ResponseModel(jsonObject)

                    if (response.status) {
                        try {
                            savedCropSowingDate = jsonObject.getString("sowing_date")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        UIToastMessage.show(requireContext(), response.response)
                    }
                }
            }

            5 -> {
                if (jsonObject != null) {
                    appPreferenceManager.saveString(AppConstants.WEATHER_RESPONSE, jsonObject.toString())
                    val response = ResponseModel(jsonObject)

                    if (response.status) {
                        val temperatureObject = jsonObject.optJSONObject("Temperature")
                        val tempMin = temperatureObject?.optInt("min") ?: 0
                        val tempMax = temperatureObject?.optInt("max") ?: 0
                        val temperature = "$tempMin°C / $tempMax°C"
//                        temperatureTextView.text = temperature
                    }
                }
            }
        }
    }


}
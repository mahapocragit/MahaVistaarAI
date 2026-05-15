package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.DeclareYourCropDialogBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.DialogPromotionalPopupBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.EtlCrossedDialogBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentMyDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CropRecyclerSapAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.AuthenticateFarmerIdActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.etl.AgriStackAdvisoryActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.Warehouse
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate.ClimateResilientTechnology
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.DBTActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice.MarketPrice
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.SoilHealthCardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.sop.SOPActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.ui.PestIdentificationActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala.ShetishalaActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.WeatherActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.NewDashboardMainActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.FarmDetailsActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.CropSelectionAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.MahavistaarViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.getLatestAdvisoriesAsJsonArray
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.UriFileHelper.openYouTube
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyDashboardFragment : Fragment(), RecyclerItemClickListener {

    private var _binding: FragmentMyDashboardBinding? = null
    private val mahavistaarViewModel: MahavistaarViewModel by viewModels()
    private var isPromoDialogShowing = false
    private val binding get() = _binding!!
    private var selectedFarmPosition = -1
    private var selectedDeletedCropPosition = -1
    private var selectedUpdateParentPosition = -1
    private var selectedFarmObject: JSONObject? = null
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var languageToLoad: String = "en"
    private var cropsJsonArray = JSONArray()
    private lateinit var myAdapter: MyDashboardAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var appPreferenceManager: AppPreferenceManager
    private var savedCropId = 0
    private var savedCropName = ""
    private var savedCropSowingDate: String? = null
    private var savedCropWoTRId: String? = null
    private var savedCropImageUrl: String? = null
    private var myFarmsAdapter: MyFarmsDCSAdapter? = null
    private var selectedCropIdForDCS = 0
    private var selectedCropSowingDateForDCS = ""
    private var currentPage = 0
    private var isPromoFetched = false
    private var etlAdvisoryJsonArray: JSONArray = JSONArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeResponse()
        setUpListeners()
        hitApis()
        showDialogs()
    }

    private fun showDialogs() {
        if (appPreferenceManager.getBoolean("SHOW_PROMO_DIALOG") && !isPromoFetched) {
            isPromoFetched = true
            val rawValue = appPreferenceManager.getString("FARMER_POPUP_ID").toString()
            if (rawValue != "null" && rawValue != null) {
                farmerViewModel.getFarmDetails()
                farmerViewModel.fetchCropsForDCS()
            } else {
                farmerViewModel.getPromoBanner()
            }
        }
    }

    // ✅ Setup RecyclerView only once
    private fun setupRecyclerView() {
        layoutManager = GridLayoutManager(
            requireContext(),
            2,
            RecyclerView.HORIZONTAL,
            false
        )

        // empty adapter initially
        binding.myDashboardRecyclerView.layoutManager = layoutManager
        myAdapter = MyDashboardAdapter(languageToLoad, JSONArray(), this)
        binding.myDashboardRecyclerView.adapter = myAdapter
    }

    private fun hitApis() {
        farmerViewModel.getFarmSummery()
        authViewModel.getCustomisedDashboardList()
        farmerViewModel.getFarmerSelectedCrop(languageToLoad)
    }

    // ✅ Arrow click logic
    private fun setUpListeners() {

        languageToLoad = "mr"
        if (AppSettings.getLanguage(requireContext()).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }

        appPreferenceManager = AppPreferenceManager(requireContext())
        savedCropId = appPreferenceManager.getInt("CROP_ID_SAVED")
        savedCropName = appPreferenceManager.getString("CROP_NAME_SAVED").toString()
        savedCropSowingDate = appPreferenceManager.getString("CROP_SOWING_DATE_SAVED").toString()
        savedCropWoTRId = appPreferenceManager.getString("CROP_WOTR_ID_SAVED")
        Log.d(TAG, "setUpListeners: ${savedCropWoTRId ?: "wtf"}")
        savedCropImageUrl = appPreferenceManager.getString("CROP_IMAGE_SAVED")
        val etlJsonString = appPreferenceManager.getString(AppConstants.ETL_ADVISORY_ARRAY)
        try {
            etlAdvisoryJsonArray = JSONArray(etlJsonString)
        } catch (_: Exception) {
        }
        binding.etlWarningCard.visibility =
            if (etlAdvisoryJsonArray.length() != 0) View.VISIBLE else View.GONE
        updateNavigationButtons()
        binding.navigateLeft.isEnabled = false

        binding.navigateRight.setOnClickListener {
            val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
            val nextPosition = firstVisible + 4

            if (nextPosition < myAdapter.itemCount) {
                binding.myDashboardRecyclerView.smoothScrollToPosition(nextPosition)
            }
        }

        binding.navigateLeft.setOnClickListener {
            val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
            val prevPosition = (firstVisible - 4).coerceAtLeast(0)

            binding.myDashboardRecyclerView.smoothScrollToPosition(prevPosition)
        }

        binding.myDashboardRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()

                val newPage = firstVisible / 4

                if (newPage != currentPage) {
                    updateIndicator(newPage)
                    currentPage = newPage
                }

                binding.navigateLeft.isEnabled = firstVisible > 0
                binding.navigateRight.isEnabled =
                    firstVisible + 4 < myAdapter.itemCount
            }
        })

        binding.farmSummeryCardView.setOnClickListener {

            val rawValue = appPreferenceManager.getString("FARMER_POPUP_ID").toString()
            if (rawValue != "null" && rawValue != null) {
                startActivity(Intent(context, FarmDetailsActivity::class.java))
            } else {
                showAgristackLinkingDialog()
            }
        }

        setETLAlertDialog()
    }

    private fun showAgristackLinkingDialog() {
        val context = ContextThemeWrapper(requireContext(), R.style.Theme_FarmerApp)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.famer_id_login_dialog, null)
        val agristackLoginDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        confirmButton.setOnClickListener {
            startActivity(Intent(requireContext(), AuthenticateFarmerIdActivity::class.java))
            agristackLoginDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            appPreferenceManager.saveBoolean("AGRISTACK_LOGIN_DIALOG", true)
            agristackLoginDialog.dismiss()
        }

        agristackLoginDialog.show()
    }

    private fun updateNavigationButtons() {
        val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()

        binding.navigateLeft.isEnabled = firstVisible > 0

        binding.navigateRight.isEnabled =
            firstVisible + 4 < myAdapter.itemCount
    }

    // ✅ Observers
    private fun observeResponse() {

        farmerViewModel.getFarmSummeryResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()

                    val jsonResponse = JSONObject(state.data.toString())
                    val dataObject = jsonResponse.optJSONObject("data")

                    val farmArea = dataObject.optDouble("total_plot_area")
                    val farmCount = dataObject.optInt("total_farms")
                    val totalVillages = dataObject.optInt("total_villages")
                    val totalCrops = dataObject.optInt("total_crops")

                    binding.totalAreaTextView.text = farmArea.toString()
                    binding.totalFarmTextView.text = farmCount.toString()
                    binding.totalCropsTextView.text = totalCrops.toString()
                    binding.totalVillagesTextView.text =
                        "${getString(R.string.villages)} $totalVillages"
                    appPreferenceManager.saveBoolean("IS_A_FARMER", true)
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Log.d(TAG, "observeResponse: ${state.message}")
                    if (state.message != "HTTP 404 Not Found") {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    appPreferenceManager.saveBoolean("IS_A_FARMER", false)
                }
            }
        }

        authViewModel.getCustomisedDashboardResponse.observe(viewLifecycleOwner) { state ->
            when (state) {

                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()

                    val jsonResponse = JSONObject(state.data.toString())
                    val dataObject = jsonResponse.optJSONObject("data")
                    val customisedDashboardList = dataObject.optJSONArray("cust_dash")

                    // ✅ update adapter (no re-setup RecyclerView)
                    myAdapter = MyDashboardAdapter(languageToLoad, customisedDashboardList, this)
                    binding.myDashboardRecyclerView.adapter = myAdapter
                    setupPageIndicator(myAdapter.itemCount)
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.getFarmDetailsResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val dataObject = jsonObject.optJSONObject("data")
                    val farmsArray = dataObject?.optJSONArray("farm_details")
                    if ((farmsArray?.length() ?: 0) > 0) {

                        if (myFarmsAdapter == null) {

                            showDialogForDCS(
                                farmsArray ?: JSONArray()
                            )
                        } else {
                            myFarmsAdapter?.updateEntireData(
                                farmsArray ?: JSONArray()
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Log.d(TAG, "observeResponse: ${state.message}")
                    if (state.message != "HTTP 404 Not Found") {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        farmerViewModel.fetchCropsForDCSResponse.observe(viewLifecycleOwner) { state ->
            cropsJsonArray = JSONArray()
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val dataObject = jsonObject.optJSONArray("data")
                    cropsJsonArray = dataObject ?: JSONArray()
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.saveFarmCropDCSResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {

                    ProgressHelper.disableProgressDialog()


                    val jSONObject = JSONObject(state.data.toString())
                    val response = jSONObject.optString("response") ?: "Crop Saved Successfully"
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()

                    // Reload farms from backend
                    farmerViewModel.getFarmDetails()
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }

        farmerViewModel.deleteFarmCropDCSResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {

                    ProgressHelper.disableProgressDialog()

                    try {

                        val farmObject =
                            selectedFarmObject ?: return@observe

                        val cropsArray =
                            farmObject.optJSONArray("crops")
                                ?: return@observe

                        val updatedArray = JSONArray()

                        for (i in 0 until cropsArray.length()) {

                            if (i != selectedDeletedCropPosition) {

                                updatedArray.put(
                                    cropsArray.getJSONObject(i)
                                )
                            }
                        }

                        // Update original farm object
                        farmObject.put(
                            "crops",
                            updatedArray
                        )

                        // Refresh parent item
                        myFarmsAdapter?.notifyItemChanged(
                            selectedFarmPosition
                        )

                        val jSONObject = JSONObject(state.data.toString())
                        val response =
                            jSONObject.optString("response") ?: "Crop Deleted Successfully"
                        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }

        farmerViewModel.updateFarmCropDCSResponse.observe(
            viewLifecycleOwner
        ) { state ->

            when (state) {

                is UiState.Loading -> {

                    ProgressHelper.showProgressDialog(
                        requireContext()
                    )
                }

                is UiState.Success -> {

                    ProgressHelper.disableProgressDialog()

                    Toast.makeText(
                        requireContext(),
                        "Crop updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reload farms API
                    farmerViewModel.getFarmDetails()
                }

                is UiState.Error -> {

                    ProgressHelper.disableProgressDialog()
                }
            }
        }

        farmerViewModel.getPromoBannerResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    val jsonObject = JSONObject(state.data.toString())
                    val dataObject = jsonObject.optJSONObject("data")
                    if (dataObject != null) {
                        val imageUrl = dataObject.optString("url")
                        val page = dataObject.optString("page")
                        if (page == "youtube") {
                            val youtubeUrl = dataObject.optString("video_url")
                            showPromotionalDialog(imageUrl, page, youtubeUrl)
                        } else {
                            showPromotionalDialog(imageUrl, page)
                        }
                    }

                }

                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.getFarmerSelectedCrop.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    context?.let { ProgressHelper.showProgressDialog(it) }
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()

                    val jsonObject = JSONObject(state.data.toString())
                    val selectedCrops = jsonObject.optJSONArray("Data")

                    if (selectedCrops != null && selectedCrops.length() > 0) {
                        for (i in 0 until selectedCrops.length()) {
                            val selectedCrop = selectedCrops.getJSONObject(i)
                            savedCropId = selectedCrop.getInt("crop_id")
                            savedCropName = selectedCrop.getString("name")
                            savedCropImageUrl = selectedCrop.getString("image")
                            savedCropSowingDate = selectedCrop.getString("sowing_date")
                            savedCropWoTRId = selectedCrop.getString("wotr_crop_id")
                        }
                    }

                    Log.d("TAGGER", "observeResponse: $jsonObject")
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPromotionalDialog(
        imageUrl: String,
        page: String,
        videoUrl: String = ""
    ) {
        if (isPromoDialogShowing || !isAdded) return

        appPreferenceManager.saveBoolean("SHOW_PROMO_DIALOG", false)

        val promoView = DialogPromotionalPopupBinding.inflate(layoutInflater)

        val promoDialog = AlertDialog.Builder(requireContext())
            .setView(promoView.root)
            .create()

        promoDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        isPromoDialogShowing = true

        promoDialog.setOnDismissListener {
            isPromoDialogShowing = false
        }

        promoView.closeImage.setOnClickListener {
            promoDialog.dismiss()
        }

        Glide.with(this) // 👈 use Fragment as lifecycle owner
            .load(imageUrl)
            .into(promoView.previewImage)

        promoView.previewImage.setOnClickListener {
            if (videoUrl.isEmpty()) {
                redirectToScreen(page)
            } else {
                openYouTube(requireContext(), videoUrl)
            }
            promoDialog.dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)

            if (isAdded && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                promoDialog.show()
            }

            delay(10_000)

            if (promoDialog.isShowing) {
                promoDialog.dismiss()
            }
        }
    }

    private fun showDialogForDCS(
        myFarmsJsonArray: JSONArray
    ) {

        val binding =
            DeclareYourCropDialogBinding.inflate(layoutInflater)


        appPreferenceManager.saveBoolean("SHOW_PROMO_DIALOG", false)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        binding.dialogCancelImageView.setOnClickListener {
            dialog.dismiss()
        }

        binding.myFarmsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        myFarmsAdapter =
            MyFarmsDCSAdapter(myFarmsJsonArray, this, languageToLoad)

        binding.myFarmsRecyclerView.adapter =
            myFarmsAdapter

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRecyclerItemClick(i: Int, obj: Any) {
        val jsonObject = obj as JSONObject
        when (i) {

            CUSTOMISED_DASHBOARD_REDIRECTION -> {
                // dashboard click

                val redirect = jsonObject.optString("page")
                Log.d(TAG, "onRecyclerItemClick: $redirect")
                redirectToScreen(redirect)
            }

            CROP_SELECTION_DCS -> {

                val position =
                    jsonObject.optInt("adapter_position")

                showCropSelectionDialog(cropsJsonArray) { selectedCrop ->

                    val cropId =
                        selectedCrop.optInt("id")

                    val cropName =
                        selectedCrop.optString("name")

                    val cropNameMr =
                        selectedCrop.optString("name_mr")

                    selectedCropIdForDCS = cropId

                    // update JSON directly
                    jsonObject.put(
                        "selected_crop_name",
                        if (languageToLoad == "en") cropName else cropNameMr
                    )

                    // refresh only one item
                    myFarmsAdapter?.notifyItemChanged(position)
                }

                return
            }

            SOWING_DATE_SELECTION_DCS -> {

                val position =
                    jsonObject.optInt("adapter_position")

                val calendar = Calendar.getInstance()

                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, selectedYear, selectedMonth, selectedDay ->

                        val selectedCalendar = Calendar.getInstance()

                        selectedCalendar.set(
                            selectedYear,
                            selectedMonth,
                            selectedDay
                        )

                        val formattedDate = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.ENGLISH
                        ).format(selectedCalendar.time)

                        // update json
                        jsonObject.put(
                            "selected_sowing_date",
                            formattedDate
                        )
                        selectedCropSowingDateForDCS = formattedDate
                        // refresh only one item
                        myFarmsAdapter?.notifyItemChanged(position)

                        Log.d("DATE", formattedDate)

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                datePickerDialog.show()

                try {

                    val datePicker =
                        datePickerDialog.datePicker

                    for (i in 0 until datePicker.childCount) {

                        val child =
                            datePicker.getChildAt(i)

                        child.layoutDirection =
                            View.LAYOUT_DIRECTION_LTR
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            SAVE_CROP_FOR_DCS -> {

                selectedFarmPosition = jsonObject.optInt("adapter_position")
                selectedFarmObject = jsonObject

                val farmId = jsonObject.optString("farm_id")
                if (selectedCropIdForDCS == null || selectedCropIdForDCS == 0) {
                    Toast.makeText(
                        requireContext(),
                        "Please select crop",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                if (selectedCropSowingDateForDCS.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select sowing date",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                farmerViewModel.saveFarmCropDCS(
                    CryptoHelper.encryptField(
                        selectedCropIdForDCS.toString()
                    ).toString(),

                    CryptoHelper.encryptField(
                        selectedCropSowingDateForDCS
                    ).toString(),

                    CryptoHelper.encryptField(
                        farmId
                    ).toString()
                )
            }

            DELETE_CROP_FOR_DCS -> {
                selectedFarmObject =
                    jsonObject.optJSONObject("farm_object")

                selectedFarmPosition =
                    jsonObject.optInt("parent_position")

                selectedDeletedCropPosition =
                    jsonObject.optInt("crop_position")

                val declarationId =
                    jsonObject.optString("declaration_id")

                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Crop")
                    .setMessage("Do you really want to delete the crop?")
                    .setPositiveButton("Delete") { dialog, _ ->

                        // DELETE LOGIC HERE
                        farmerViewModel.deleteFarmCropForDCS(
                            CryptoHelper.encryptField(declarationId).toString()
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            UPDATE_CROP_FOR_DCS -> {

                selectedFarmObject =
                    jsonObject.optJSONObject("farm_object")

                selectedUpdateParentPosition =
                    jsonObject.optInt("parent_position")

                val selectedUpdateDeclarationId =
                    jsonObject.optString("declaration_id")

                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, selectedYear, selectedMonth, selectedDay ->

                        val selectedCalendar = Calendar.getInstance()

                        selectedCalendar.set(
                            selectedYear,
                            selectedMonth,
                            selectedDay
                        )

                        val formattedDate = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.ENGLISH
                        ).format(selectedCalendar.time)

                        // update json
                        jsonObject.put(
                            "selected_sowing_date",
                            formattedDate
                        )
                        selectedCropSowingDateForDCS = formattedDate
                        farmerViewModel.updateFarmCropForDCS(
                            CryptoHelper.encryptField(selectedUpdateDeclarationId).toString(),
                            CryptoHelper.encryptField(selectedCropSowingDateForDCS).toString()
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                datePickerDialog.show()
            }
        }
    }

    private fun showCropSelectionDialog(
        jsonArray: JSONArray,
        onItemSelected: (JSONObject) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_crop_selection, null)

        val etSearch = dialogView.findViewById<EditText>(R.id.etSearch)
        val ivClose = dialogView.findViewById<ImageView>(R.id.ivClose)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)

        val cropList = mutableListOf<JSONObject>()

        for (i in 0 until jsonArray.length()) {
            cropList.add(jsonArray.getJSONObject(i))
        }


        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val adapter = CropSelectionAdapter(cropList.toMutableList()) { selectedCrop ->
            onItemSelected(selectedCrop)
            dialog.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                val filteredList = cropList.filter {
                    it.optString("name").lowercase().contains(query) ||
                            it.optString("name_mr").lowercase().contains(query)
                }

                adapter.updateList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        dialog.show()
    }

    private fun redirectToScreen(testValue: String) {
        val targetIntent = when (testValue.lowercase()) {

            "advisory" ->
                if (savedCropName.isEmpty()) {
                    appPreferenceManager.saveString(
                        AppConstants.ACTION_FROM_DASHBOARD,
                        AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD
                    )

                    Intent(requireContext(), AddCropActivity::class.java)

                } else {
                    Intent(requireContext(), AdvisoryCropActivity::class.java).apply {
                        putExtra("id", savedCropId)
                        putExtra("wotr_crop_id", savedCropWoTRId ?: "0".toInt())
                        putExtra("mUrl", savedCropImageUrl)
                        putExtra("mName", savedCropName)
                        putExtra("sowingDate", savedCropSowingDate)
                    }
                }

            "sop" -> Intent(requireContext(), SOPActivity::class.java)

            "fertilizer" -> Intent(requireContext(), FertilizerCalculatorActivity::class.java)

            "pestdisease" -> Intent(requireContext(), PestsAndDiseasesStages::class.java)

            "weather" -> Intent(requireContext(), WeatherActivity::class.java)

            "shc", "soilcard" ->
                Intent(requireContext(), SoilHealthCardActivity::class.java)

            "climatetech" ->
                Intent(requireContext(), ClimateResilientTechnology::class.java)

            "market", "marketprice" ->
                Intent(requireContext(), MarketPrice::class.java)

            "shetishala" ->
                Intent(requireContext(), ShetishalaActivity::class.java)

            "warehouse" ->
                Intent(requireContext(), Warehouse::class.java)

            "customhire" ->
                Intent(requireContext(), CHCenterActivity::class.java)

            "videos" ->
                Intent(requireContext(), VideosActivity::class.java)

            "dbtschemes" ->
                Intent(requireContext(), DBTActivity::class.java)

            "dashboard" ->
                Intent(requireContext(), NewDashboardMainActivity::class.java)

            "etl_page" ->
                Intent(requireContext(), AgriStackAdvisoryActivity::class.java)

            "pestdetection" ->
                Intent(requireContext(), PestIdentificationActivity::class.java)

            else ->
                Intent(requireContext(), NewDashboardMainActivity::class.java)
        }

        startActivity(targetIntent)
    }

    private fun setupPageIndicator(itemCount: Int) {

        val itemsPerPage = 4
        if (itemCount == 0) return

        val pageCount = (itemCount + itemsPerPage - 1) / itemsPerPage

        binding.pageIndicatorLayout.removeAllViews()

        for (i in 0 until pageCount) {

            val indicator = ImageView(requireContext())

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.marginEnd = 8
            indicator.layoutParams = params

            indicator.setImageResource(R.drawable.dot_unselected)

            binding.pageIndicatorLayout.addView(indicator)
        }

        updateIndicator(0)
    }

    private fun setETLAlertDialog() {
        binding.etlWarningCard.setOnClickListener {

            // Use ViewBinding instead of manual inflate
            val dialogBinding =
                EtlCrossedDialogBinding.inflate(LayoutInflater.from(requireContext()))

            val cropRecyclerSapAdapter =
                CropRecyclerSapAdapter(getLatestAdvisoriesAsJsonArray(etlAdvisoryJsonArray))

            dialogBinding.cropSapRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = cropRecyclerSapAdapter
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

            dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

            // Close button
            dialogBinding.closeIcon.setOnClickListener {
                dialog.dismiss()
            }

            // Redirect button
            dialogBinding.redirectToETLButton.setOnClickListener {
                dialog.dismiss()
                startActivity(
                    Intent(requireContext(), AgriStackAdvisoryActivity::class.java)
                )
            }

            // Adjust RecyclerView height (max 3 items)
            dialogBinding.cropSapRecyclerView.viewTreeObserver
                .addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {

                        dialogBinding.cropSapRecyclerView.viewTreeObserver
                            .removeOnGlobalLayoutListener(this)

                        val itemCount = cropRecyclerSapAdapter.itemCount
                        val visibleItems = minOf(itemCount, 3)

                        val itemView =
                            dialogBinding.cropSapRecyclerView
                                .findViewHolderForAdapterPosition(0)
                                ?.itemView

                        itemView?.let {
                            val itemHeight = it.height
                            val maxHeight = itemHeight * visibleItems

                            dialogBinding.cropSapRecyclerView.layoutParams.height =
                                maxHeight
                            dialogBinding.cropSapRecyclerView.requestLayout()
                        }
                    }
                })

            dialog.show()
        }
    }

    private fun updateIndicator(position: Int) {

        for (i in 0 until binding.pageIndicatorLayout.childCount) {

            val dot = binding.pageIndicatorLayout.getChildAt(i) as ImageView

            if (i == position) {
                dot.setImageResource(R.drawable.dot_selected)
            } else {
                dot.setImageResource(R.drawable.dot_unselected)
            }
        }
    }

    companion object {
        const val CUSTOMISED_DASHBOARD_REDIRECTION = 1
        const val CROP_SELECTION_DCS = 2
        const val SOWING_DATE_SELECTION_DCS = 3
        const val SAVE_CROP_FOR_DCS = 4
        const val DELETE_CROP_FOR_DCS = 5
        const val UPDATE_CROP_FOR_DCS = 6
    }

    override fun onResume() {
        super.onResume()
        hitApis()
    }
}
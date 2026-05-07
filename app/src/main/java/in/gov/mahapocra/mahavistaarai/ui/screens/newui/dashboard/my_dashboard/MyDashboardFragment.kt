package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.DeclareYourCropDialogBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentMyDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.etl.AgriStackAdvisoryActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.Warehouse
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate.ClimateResilientTechnology
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.DBTActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice.MarketPrice
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.SoilHealthCardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.sop.SOPActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala.ShetishalaActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.WeatherActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.NewDashboardMainActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.FarmDetailsActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyDashboardFragment : Fragment(), RecyclerItemClickListener {

    private var _binding: FragmentMyDashboardBinding? = null
    private val binding get() = _binding!!

    private val farmerViewModel: FarmerViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

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
    }

    // ✅ Setup RecyclerView only once
    private fun setupRecyclerView() {
        layoutManager = GridLayoutManager(
            requireContext(),
            2,
            RecyclerView.HORIZONTAL,
            false
        )

        binding.myDashboardRecyclerView.layoutManager = layoutManager

        // empty adapter initially
        myAdapter = MyDashboardAdapter(JSONArray(), this)
        binding.myDashboardRecyclerView.adapter = myAdapter

        // OPTIONAL → only if you want snapping
        // PagerSnapHelper().attachToRecyclerView(binding.myDashboardRecyclerView)
    }

    private fun hitApis() {
        farmerViewModel.getFarmSummery()
        authViewModel.getCustomisedDashboardList()
        farmerViewModel.getFarmDetails()
        farmerViewModel.fetchCropsForDCS()
    }

    // ✅ Arrow click logic
    private fun setUpListeners() {
        appPreferenceManager = AppPreferenceManager(requireContext())
        savedCropId = appPreferenceManager.getInt("CROP_ID_SAVED")
        savedCropName = appPreferenceManager.getString("CROP_NAME_SAVED").toString()
        savedCropSowingDate = appPreferenceManager.getString("CROP_SOWING_DATE_SAVED").toString()
        savedCropWoTRId = appPreferenceManager.getString("CROP_WOTR_ID_SAVED")
        savedCropImageUrl = appPreferenceManager.getString("CROP_IMAGE_SAVED")

        binding.navigateLeft.isEnabled = false

        binding.navigateRight.setOnClickListener {
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val nextPosition = firstVisible + 4

            if (nextPosition < myAdapter.itemCount) {
                binding.myDashboardRecyclerView.smoothScrollToPosition(nextPosition)
            }
        }

        binding.navigateLeft.setOnClickListener {
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val prevPosition = (firstVisible - 4).coerceAtLeast(0)

            binding.myDashboardRecyclerView.smoothScrollToPosition(prevPosition)
        }

        binding.myDashboardRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val first = layoutManager.findFirstVisibleItemPosition()
                val last = layoutManager.findLastVisibleItemPosition()

                binding.navigateLeft.isEnabled = first > 0
                binding.navigateRight.isEnabled = last < myAdapter.itemCount - 1
            }
        })

        binding.farmSummeryCardView.setOnClickListener {
            startActivity(Intent(context, FarmDetailsActivity::class.java))
        }
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

                    binding.totalAreaTextView.text = farmArea.toString()
                    binding.totalFarmTextView.text = farmCount.toString()
                    binding.totalVillagesTextView.text = "Villages: $totalVillages"
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
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
                    myAdapter = MyDashboardAdapter(customisedDashboardList, this)
                    binding.myDashboardRecyclerView.adapter = myAdapter
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
                    showDialogForDCS(farmsArray ?: JSONArray())
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
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
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }
    }

    private fun showDialogForDCS(
        myFarmsJsonArray: JSONArray
    ) {

        val binding =
            DeclareYourCropDialogBinding.inflate(layoutInflater)

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
            MyFarmsDCSAdapter(myFarmsJsonArray, this)

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

                    selectedCropIdForDCS = cropId

                    // update JSON directly
                    jsonObject.put(
                        "selected_crop_name",
                        cropName
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

            SAVE_CROP_FOR_DCS->{
                Log.d(TAG, "onRecyclerItemClick: $jsonObject")
                val farmId = jsonObject.optString("farm_id")
                farmerViewModel.saveFarmCropDCS(
                    CryptoHelper.encryptField(selectedCropIdForDCS.toString()).toString(),
                    CryptoHelper.encryptField(selectedCropSowingDateForDCS).toString(),
                    CryptoHelper.encryptField(farmId.toString()).toString(),
                )
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

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
            "advisory" -> Intent(requireContext(), AdvisoryCropActivity::class.java).apply {
                putExtra("id", savedCropId)
                putExtra("wotr_crop_id", savedCropWoTRId?.toInt())
                putExtra("mUrl", savedCropImageUrl)
                putExtra("mName", savedCropName)
                putExtra("sowingDate", savedCropSowingDate)
            }

            "sop" -> Intent(requireContext(), SOPActivity::class.java)
            "fertilizer" -> Intent(requireContext(), FertilizerCalculatorActivity::class.java)
            "pestdisease" -> Intent(requireContext(), PestsAndDiseasesStages::class.java)
            "weather" -> Intent(requireContext(), WeatherActivity::class.java)
            "shc" -> Intent(requireContext(), SoilHealthCardActivity::class.java)
            "climatetech" -> Intent(requireContext(), ClimateResilientTechnology::class.java)
            "market" -> Intent(requireContext(), MarketPrice::class.java)
            "shetishala" -> Intent(requireContext(), ShetishalaActivity::class.java)
            "warehouse" -> Intent(requireContext(), Warehouse::class.java)
            "customhire" -> Intent(requireContext(), CHCenterActivity::class.java)
            "videos" -> Intent(requireContext(), VideosActivity::class.java)
            "dbtschemes" -> Intent(requireContext(), DBTActivity::class.java)
            "dashboard" -> Intent(requireContext(), NewDashboardMainActivity::class.java)
            "etl_page" -> Intent(requireContext(), AgriStackAdvisoryActivity::class.java)
            else -> Intent(requireContext(), NewDashboardMainActivity::class.java)
        }
        startActivity(targetIntent)
    }

    companion object {
        const val CUSTOMISED_DASHBOARD_REDIRECTION = 1
        const val CROP_SELECTION_DCS = 2
        const val SOWING_DATE_SELECTION_DCS = 3
        const val SAVE_CROP_FOR_DCS = 4
    }
}
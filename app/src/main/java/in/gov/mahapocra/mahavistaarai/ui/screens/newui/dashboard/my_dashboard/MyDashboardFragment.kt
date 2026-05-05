package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
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
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.GridSpacingItemDecoration
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.NewDashboardMainActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.FarmDetailsActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.TokenSessionManager.getAccessToken
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject

class MyDashboardFragment : Fragment(), RecyclerItemClickListener {

    private var _binding: FragmentMyDashboardBinding? = null
    private val binding get() = _binding!!

    private val farmerViewModel: FarmerViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var myAdapter: MyDashboardAdapter
    private lateinit var layoutManager: GridLayoutManager

    private lateinit var appPreferenceManager: AppPreferenceManager
    private var savedCropId = 0
    private var savedCropName = ""
    private var savedCropSowingDate: String? = null
    private var savedCropWoTRId: String? = null
    private var savedCropImageUrl: String? = null

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
        setUpListeners()
        observeResponse()
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

        val farmerRegId = AppSettings.getInstance()
            .getIntValue(requireContext(), AppConstants.fREGISTER_ID, 0)

        farmerViewModel.getFarmSummery()

        authViewModel.getCustomisedDashboardList(
            CryptoHelper.encryptField(farmerRegId.toString()).toString()
        )
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRecyclerItemClick(i: Int, obj: Any) {
        val jSONObject = obj as JSONObject
        val redirect = jSONObject.optString("page")
        redirectToScreen(redirect)
        Log.d(TAG, "onRecyclerItemClick: $redirect")
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
}
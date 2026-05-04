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
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.GridSpacingItemDecoration
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.FarmDetailsActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.TokenSessionManager.getAccessToken
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject

class MyDashboardFragment : Fragment() {

    private var _binding: FragmentMyDashboardBinding? = null
    private val binding get() = _binding!!

    private val farmerViewModel: FarmerViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var myAdapter: MyDashboardAdapter
    private lateinit var layoutManager: GridLayoutManager

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
        myAdapter = MyDashboardAdapter(JSONArray())
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
                    myAdapter = MyDashboardAdapter(customisedDashboardList)
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
}
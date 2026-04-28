package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.my_dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentMyDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONObject
import kotlin.getValue

class MyDashboardFragment : Fragment() {

    private var _binding: FragmentMyDashboardBinding? = null
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataList = listOf(
            MyDashboardModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_weather_mp)!!,
                "Weather",
                "22.6°/ 29.8°"
            ),
            MyDashboardModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_crop_advisory_mp)!!,
                "Crop Advisory", "Grow Crops Better"
            ),
            MyDashboardModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_schemes_mp)!!,
                "My Schemes", "Schemes and DBT"
            ),
            MyDashboardModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_market_price_sf)!!,
                "Market Price", "Avg. Price 6800"
            ),
            MyDashboardModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_warehouse_mp)!!,
                "Warehouse", "Find storage nearby"
            ),
            MyDashboardModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_videos_mp)!!,
                "Videos", "Smart farming videos"
            )
        )

        val myAdapter = MyDashboardAdapter(dataList)

        binding.myDashboardRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                horizontalSpacing = 16, // adjust
                verticalSpacing = 54     // 👈 reduce this
            )
        )

        binding.myDashboardRecyclerView.apply {
            adapter = myAdapter
            val layoutManager = GridLayoutManager(
                requireContext(),
                2,
                RecyclerView.HORIZONTAL,
                false
            )

            binding.myDashboardRecyclerView.apply {
                this.layoutManager = layoutManager
                adapter = myAdapter

                PagerSnapHelper().attachToRecyclerView(this)
            }
        }
        observeResponse()
        farmerViewModel.getFarmDetails("79335694125")
    }

    private fun observeResponse(){
        farmerViewModel.getFarmDetailsResponse.observe(viewLifecycleOwner){ state ->
            when(state){
                is UiState.Loading->{
                    context?.let { ProgressHelper.showProgressDialog(it) }
                }
                is UiState.Success->{
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
                is UiState.Error->{
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
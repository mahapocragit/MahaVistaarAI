package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.smart_farming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentSmartFarmingBinding

class SmartFarmingFragment : Fragment() {

    private var _binding: FragmentSmartFarmingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmartFarmingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataList = listOf(
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_climate_resilient_sf)!!,
                "Climate Resilient"
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_videos_sf)!!,
                "Videos"
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_shetishala_sf)!!,
                "Shetishala"
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_magazine_sf)!!,
                "Magazine"
            )
        )

        val adapter = SmartFarmingAdapter(dataList)

        binding.smartFarmingRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
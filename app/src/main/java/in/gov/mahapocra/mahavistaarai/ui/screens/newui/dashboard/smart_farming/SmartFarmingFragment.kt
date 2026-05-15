package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.smart_farming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import `in`.co.appinventor.services_api.listener.OnRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentSmartFarmingBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.magazine.MagazineDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate.ClimateResilientTechnology
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.sop.SOPActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala.ShetishalaActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosActivity
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager

class SmartFarmingFragment : Fragment(), OnRecyclerItemClickListener {

    private var _binding: FragmentSmartFarmingBinding? = null
    private val binding get() = _binding!!
    private lateinit var appPreferenceManager: AppPreferenceManager
    private var savedCropId = 0
    private var savedCropName = ""
    private var savedCropImageUrl = ""
    private var savedCropSowingDate = ""
    private var savedCropWoTRId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmartFarmingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        savedCropId = AppPreferenceManager(requireContext()).getInt("CROP_ID_SAVED")
        savedCropName = AppPreferenceManager(requireContext()).getString("CROP_NAME_SAVED")?:""
        savedCropImageUrl = AppPreferenceManager(requireContext()).getString("CROP_IMAGE_SAVED")?:""
        savedCropSowingDate = AppPreferenceManager(requireContext()).getString("CROP_SOWING_DATE_SAVED")?:""
        savedCropWoTRId = AppPreferenceManager(requireContext()).getString("CROP_WOTR_ID_SAVED")?:""

        val dataList = listOf(
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_climate_resilient_sf)!!,
                getString(R.string.climateTechnology)
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_pest_disease_sf)!!,
                getString(R.string.pests_n_diseases)
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_sop_sf)!!,
                getString(R.string.sop_title)
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_shetishala_sf)!!,
                getString(R.string.shetishala)
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_magazine_sf)!!,
                getString(R.string.magazine)
            ),
            SmartFarmingModel(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_videos_sf)!!,
                getString(R.string.videos)
            ),
        )

        val adapter = SmartFarmingAdapter(dataList, this)

        binding.smartFarmingRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRecyclerViewItemClick(obj: Any?) {
        if (obj != null) {
            val receivedObject = obj as SmartFarmingModel
            when (receivedObject.title) {
                getString(R.string.climateTechnology) -> {
                    startActivity(Intent(context, ClimateResilientTechnology::class.java))
                }
                getString(R.string.videos) -> {
                    startActivity(Intent(context, VideosActivity::class.java))
                }
                getString(R.string.shetishala) -> {
                    startActivity(Intent(context, ShetishalaActivity::class.java))
                }
                getString(R.string.magazine) -> {
                    startActivity(Intent(context, MagazineDashboardActivity::class.java))
                }
                getString(R.string.pests_n_diseases)->{
                    val intent = Intent(requireContext(), PestsAndDiseasesStages::class.java)
                    intent.putExtra("id", savedCropId)
                    intent.putExtra("wotr_crop_id", savedCropWoTRId)
                    intent.putExtra("sowingDate", savedCropSowingDate)
                    intent.putExtra("mUrl", savedCropImageUrl)
                    intent.putExtra("mName", savedCropName)
                    startActivity(intent)
                }
                getString(R.string.sop_title)->{
                    val intent = Intent(requireContext(), SOPActivity::class.java)
                    intent.putExtra("id", savedCropId)
                    intent.putExtra("wotr_crop_id", savedCropWoTRId)
                    intent.putExtra("mUrl", savedCropImageUrl)
                    intent.putExtra("mName", savedCropName)
                    startActivity(intent)
                }
            }
        }
    }
}
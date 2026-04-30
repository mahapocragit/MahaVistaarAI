package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.ResponseModel
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentAgriServicesBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.AddCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.Warehouse
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice.MarketPrice
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.ui.PestIdentificationActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.CostCalculatorDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject


class AgriServicesFragment : Fragment(), OnMultiRecyclerItemClickListener {
    private var _binding: FragmentAgriServicesBinding? = null
    private val farmerViewModel: FarmerViewModel by viewModels()
    private lateinit var appPreferenceManager: AppPreferenceManager
    private lateinit var languageToLoad: String
    private val binding get() = _binding!!
    private var showToast = true
    private var cropId = 0
    private var farmerId = 0
    private var savedCropId = 0
    private var savedCropName = ""
    private var savedCropSowingDate: String? = null
    private var savedCropWoTRId: String? = null
    private var savedCropImageUrl: String? = null
    private var selectedCropList: ArrayList<CropsCategName>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgriServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(requireContext()).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        showToast = true
        setUpListeners()
    }

    private fun setUpListeners() {
        observeResponse()
        farmerId =
            AppSettings.getInstance().getIntValue(requireContext(), AppConstants.fREGISTER_ID, 0)
        appPreferenceManager = AppPreferenceManager(requireContext())
        binding.marketPriceLayout.containerIcon.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.market_bubble_as
            )
        })

        binding.addCropCardView.setOnClickListener {
            val intent = Intent(
                requireContext(),
                AddCropActivity::class.java
            ).putExtra(
                "callerActivity", "DashboardCallerActivity"
            )
            startActivity(intent)
        }

        binding.changeCropText.setOnClickListener {
            val intent = Intent(
                requireContext(),
                AddCropActivity::class.java
            )
            startActivity(intent)
        }

        binding.deleteCropImageView.setOnClickListener {
            cropId = savedCropId
            deleteDialog()
        }

        binding.takePictureButton.setOnClickListener {
            startActivity(Intent(context, PestIdentificationActivity::class.java))
        }

        binding.marketPriceLayout.txtTitle.text = "Real-Time Market Prices"
        binding.marketPriceLayout.shortDescriptionTextView.text = "Live rates from 200+ Mandis"
        binding.marketPriceLayout.root.setOnClickListener {
            startActivity(Intent(context, MarketPrice::class.java))
        }

        binding.warehouseLayout.containerIcon.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.warehouse_bubble_as
            )
        })
        binding.warehouseLayout.txtTitle.text = "Warehouse Availability"
        binding.warehouseLayout.shortDescriptionTextView.text = "Find storage space near you"
        binding.warehouseLayout.root.setOnClickListener {
            startActivity(Intent(context, Warehouse::class.java))
        }

        binding.chcCentreLayout.containerIcon.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.chc_bubble_as
            )
        })
        binding.chcCentreLayout.txtTitle.text = "CHC Center"
        binding.chcCentreLayout.shortDescriptionTextView.text = "Find local trading hubs"
        binding.chcCentreLayout.root.setOnClickListener {
            startActivity(Intent(context, CHCenterActivity::class.java))
        }

        binding.costCalculatorCard.setOnClickListener {
            startActivity(Intent(context, CostCalculatorDashboardActivity::class.java))
        }

        binding.fertilizerCalculatorCard.setOnClickListener {
            startActivity(Intent(context, FertilizerCalculatorActivity::class.java))
        }
        val farmerRegId =
            AppSettings.getInstance().getIntValue(context, AppConstants.fREGISTER_ID, 0)
        farmerViewModel.getFarmerSelectedCrop(farmerRegId, "en")
    }

    private fun deleteDialog() {
        val dialog: Dialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.delete_crop_title)
            .setMessage(R.string.delete_crop_message)
            .setPositiveButton(
                R.string.delete_crop_yes
            ) { _: DialogInterface?, _: Int ->
                farmerViewModel.deleteFarmerSelectedCrop(farmerId, cropId)
            }
            .setNegativeButton(
                R.string.delete_crop_no
            ) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun observeResponse() {
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
                        selectedCropList = ArrayList()
                        for (i in 0 until selectedCrops.length()) {
                            val selectedCrop = selectedCrops.getJSONObject(i)
                            savedCropId = selectedCrop.getInt("crop_id")
                            savedCropName = selectedCrop.getString("name")
                            savedCropImageUrl = selectedCrop.getString("image")
                            savedCropSowingDate = selectedCrop.getString("sowing_date")
                            savedCropWoTRId = selectedCrop.getString("wotr_crop_id")
                            binding.apply {
                                addChangeCropTV.setText(R.string.change_Crop)
                                addChangeCropIV.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_swap
                                    )
                                )
                                savedCropNameCardView.visibility = View.VISIBLE
                                savedCropNameTextView.text = savedCropName
                                yourCropTv.visibility = View.GONE
                                addCropCardView.visibility = View.GONE
                            }
                            Glide.with(binding.savedCropNameImageView.context)
                                .load(savedCropImageUrl)
                                .placeholder(R.drawable.ic_no_data_found) // optional while loading
                                .error(R.drawable.ic_no_data_found) // fallback image
                                .centerCrop()
                                .into(binding.savedCropNameImageView)
                        }
                        appPreferenceManager.saveInt("CROP_ID_SAVED", savedCropId)
                        appPreferenceManager.saveString("CROP_NAME_SAVED", savedCropName)
                        appPreferenceManager.saveString("CROP_IMAGE_SAVED", savedCropImageUrl)
                        appPreferenceManager.saveString("CROP_SOWING_DATE_SAVED", savedCropSowingDate)
                        appPreferenceManager.saveString("CROP_WOTR_ID_SAVED", savedCropWoTRId)
                        selectedCropList?.add(
                            CropsCategName(
                                savedCropId,
                                savedCropName,
                                savedCropImageUrl,
                                savedCropWoTRId
                            )
                        )
                    } else {
                        showEmptyCropUI()
                    }

                    Log.d("TAGGER", "observeResponse: $jsonObject")
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        farmerViewModel.deleteFarmerSelectedCrop.observe(viewLifecycleOwner) { state ->
            when (state) {

                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val deleteResponse = ResponseModel(jsonObject)

                    if (deleteResponse.getStatus()) {
                        if (showToast) {
                            UIToastMessage.show(requireContext(), getString(R.string.selected_crop_deleted))
                        }

                        AppSettings.getInstance().setList(requireContext(), AppConstants.kFarmerCrop, null)
                        selectedCropList?.clear()
                        farmerViewModel.getFarmerSelectedCrop(farmerId, languageToLoad)
                        savedCropName = ""
                        AppPreferenceManager(requireContext()).saveInt("CROP_ID_SAVED", 0)
                        AppPreferenceManager(requireContext()).clearPreference("CROP_NAME_SAVED")
                        AppPreferenceManager(requireContext()).clearPreference("CROP_IMAGE_SAVED")
                        AppPreferenceManager(requireContext()).clearPreference("CROP_SOWING_DATE_SAVED")
                        AppPreferenceManager(requireContext()).clearPreference("CROP_WOTR_ID_SAVED")
                    } else {
                        UIToastMessage.show(requireContext(), deleteResponse.getResponse())
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun showEmptyCropUI() {
        binding.apply {
            savedCropNameCardView.visibility = View.GONE
            yourCropTv.visibility = View.VISIBLE
            yourCropTv.setText(R.string.no_crops_added)

            addCropCardView.visibility = View.VISIBLE
            addChangeCropTV.setText(R.string.add_Crop)
            addChangeCropIV.setImageResource(R.drawable.baseline_add_24)
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments

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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.AddCropForDcsDialogBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentFarmDetailsBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.CropSelectionAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.FarmDetailsAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.FirebaseTopicHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FarmDetailsFragment : Fragment(), RecyclerItemClickListener {

    private var _binding: FragmentFarmDetailsBinding? = null
    private val binding get() = _binding!!
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var cropsJsonArray = JSONArray()
    private var languageToLoad: String = "en"
    private var adapter = FarmDetailsAdapter(languageToLoad, JSONArray(), this)
    private var farmId = ""
    private var selectedCropIdForDCS = 0
    private var selectedCropSowingDateForDCS = ""
    private var actionableCropId = 0
    private var farmData: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        languageToLoad = "mr"
        if (AppSettings.getLanguage(context).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(requireContext(), languageToLoad)
        observeResponse()
        init()
    }

    private fun init() {
        farmData = arguments?.getString(ARG_FARM_DATA)
        Log.d(TAG, "init: ${farmData ?: "NULL"}")
        if (farmData != null) {
            val jsonObject = JSONObject(farmData.toString())
            farmId = jsonObject.optString("farm_id")
            val ownerName = jsonObject.optString("owner_name")
            val surveyNumber = jsonObject.optString("survey_no")
            val villageName = jsonObject.optString("village_name")
            val villageNameMr = jsonObject.optString("village_name_mr")
            val totalArea = jsonObject.optDouble("total_plot_area")
            binding.nameTextView.text = buildString {
                append("${getString(R.string.name)}: ")
                append(" $ownerName")
            }
            binding.surveyNumberTextView.text = buildString {
                append("${getString(R.string.survey_no)}: ")
                append(" $surveyNumber")
            }
            binding.totalAreaTextView.text = buildString {
                append("$totalArea ")
                append(getString(R.string.hectare_mdash))
            }
            binding.farmIdTextView.text = buildString {
                append("${getString(R.string.farmid)}: ")
                append(farmId)
            }
            binding.villageNameTextView.text =
                if (languageToLoad == "en") villageName else villageNameMr
        }

        binding.cropDSCRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cropDSCRecyclerView.adapter = adapter

        farmerViewModel.getFarmCropDCS(CryptoHelper.encryptField(farmId).toString())
        farmerViewModel.fetchCropsForDCS()
        binding.addCropForFarmLayout.setOnClickListener {
            openDialogForSavingCropForDCS()
        }
    }

    private fun openDialogForSavingCropForDCS() {
        val dialogBinding =
            AddCropForDcsDialogBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        dialogBinding.sowingDateCardView.setOnClickListener {
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
                    selectedCropSowingDateForDCS = formattedDate
                    dialogBinding.sowingDateCropDCSTextView.text = formattedDate.toString()

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
        dialogBinding.selectCropCardView.setOnClickListener {
            showCropSelectionDialog(cropsJsonArray) { selectedCrop ->
                val cropId =
                    selectedCrop.optInt("id")
                val cropName =
                    selectedCrop.optString("name")
                val cropNameMr =
                    selectedCrop.optString("name_mr")
                selectedCropIdForDCS = cropId
                dialogBinding.cropNameForDCSTextView.text =
                    if (languageToLoad == "en") cropName else cropNameMr
            }
        }

        dialogBinding.saveCropButton.setOnClickListener {

            // 1️⃣ Validate Crop Selection
            if (selectedCropIdForDCS == null || selectedCropIdForDCS == 0) {
                Toast.makeText(requireContext(), "Please select crop", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2️⃣ Validate Sowing Date
            if (selectedCropSowingDateForDCS.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select sowing date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3️⃣ Validate Farm ID (extra safety)
            if (farmId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Farm not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            actionableCropId = selectedCropIdForDCS
            // 4️⃣ If everything is valid → Call API
            farmerViewModel.saveFarmCropDCS(
                CryptoHelper.encryptField(selectedCropIdForDCS.toString()).toString(),
                CryptoHelper.encryptField(selectedCropSowingDateForDCS).toString(),
                CryptoHelper.encryptField(farmId).toString(),
            )

            dialog.dismiss()
        }

        dialogBinding.cancelCropButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

    private fun observeResponse() {
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
                    farmerViewModel.getFarmCropDCS(CryptoHelper.encryptField(farmId).toString())
                    val topic = "crop_$actionableCropId"
                    FirebaseTopicHelper.subscribeToTopic(topic) { subscribed ->
                        if (subscribed) {
                            farmerViewModel.saveSubscribedTopic(
                                topic
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }

        farmerViewModel.getFarmCropDCSResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jSONObject = JSONObject(state.data.toString())
                    val dataObject = jSONObject.optJSONObject("data")
                    val cropsArray = dataObject?.optJSONArray("crops")
                    if (cropsArray?.length() == 2) {
                        binding.addCropForFarmLayout.visibility = View.GONE
                        binding.cropTitleTextView.text =
                            getString(R.string.max_crop_limit_added)
                    } else {
                        binding.addCropForFarmLayout.visibility = View.VISIBLE
                        binding.cropTitleTextView.text =
                            getString(R.string.please_add_the_crops_message)
                    }
                    adapter = FarmDetailsAdapter(languageToLoad, cropsArray ?: JSONArray(), this)
                    binding.cropDSCRecyclerView.adapter = adapter
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    if (state.message == "HTTP 404 Not Found") {
                        adapter = FarmDetailsAdapter(languageToLoad, JSONArray(), this)
                        binding.cropDSCRecyclerView.adapter = adapter
                    }
                }
            }
        }

        farmerViewModel.updateFarmCropDCSResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(requireContext())
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jSONObject = JSONObject(state.data.toString())
                    farmerViewModel.getFarmCropDCS(CryptoHelper.encryptField(farmId).toString())
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
                    val jSONObject = JSONObject(state.data.toString())
                    val response = jSONObject.optString("response") ?: "Crop Deleted Successfully"
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()
                    farmerViewModel.getFarmCropDCS(CryptoHelper.encryptField(farmId).toString())
                    val topic = "crop_$actionableCropId"
                    FirebaseTopicHelper.unSubscribeToTopic(topic) { unsubscribed ->
                        if (unsubscribed) {
                            farmerViewModel.deleteSubscribedTopics(
                                listOf(topic)
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
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
    }

    override fun onRecyclerItemClick(flag: Int, jsonObject: Any) {
        val dataObject = jsonObject as JSONObject
        val declarationId = dataObject.optInt("declaration_id").toString()
        when (flag) {
            UPDATE_CROP -> {
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
                            CryptoHelper.encryptField(declarationId).toString(),
                            CryptoHelper.encryptField(selectedCropSowingDateForDCS).toString()
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                datePickerDialog.show()
            }

            DELETE_CROP -> {
                actionableCropId = dataObject.optInt("crop_id")
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
        }
    }

    companion object {
        const val UPDATE_CROP = 1
        const val DELETE_CROP = 2
        const val ARG_FARM_DATA = "farm_data"

        fun newInstance(
            farmData: String?
        ): FarmDetailsFragment {

            return FarmDetailsFragment().apply {

                arguments = Bundle().apply {
                    putString(ARG_FARM_DATA, farmData)
                }
            }
        }
    }
}
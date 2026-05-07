package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.UiState
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDetailedFarmBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.AddCropForDcsDialogBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard.CropSelectionAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard.MyFarmsDCSAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailedFarmActivity : AppCompatActivity(), RecyclerItemClickListener {
    private lateinit var binding: ActivityDetailedFarmBinding
    private val farmerViewModel: FarmerViewModel by viewModels()
    private var cropsJsonArray = JSONArray()
    private var adapter = FarmDetailsAdapter(JSONArray(), this)
    private var farmId = ""
    private var myFarmsAdapter: MyFarmsDCSAdapter? = null
    private var selectedCropIdForDCS = 0
    private var selectedCropSowingDateForDCS = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailedFarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        observeResponse()
        init()
    }

    private fun observeResponse() {
        farmerViewModel.saveFarmCropDCSResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    farmerViewModel.getFarmCropDCS(CryptoHelper.encryptField(farmId).toString())
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }

        farmerViewModel.getFarmCropDCSResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jSONObject = JSONObject(state.data.toString())
                    val dataObject = jSONObject.optJSONObject("data")
                    val cropsArray = dataObject?.optJSONArray("crops")
                    adapter = FarmDetailsAdapter(cropsArray ?: JSONArray(), this)
                    binding.cropDSCRecyclerView.adapter = adapter
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                }
            }
        }

        farmerViewModel.updateFarmCropDCSResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
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

        farmerViewModel.deleteFarmCropDCSResponse.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
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

        farmerViewModel.fetchCropsForDCSResponse.observe(this) { state ->
            cropsJsonArray = JSONArray()
            when (state) {
                is UiState.Loading -> {
                    ProgressHelper.showProgressDialog(this)
                }

                is UiState.Success -> {
                    ProgressHelper.disableProgressDialog()
                    val jsonObject = JSONObject(state.data.toString())
                    val dataObject = jsonObject.optJSONArray("data")
                    cropsJsonArray = dataObject ?: JSONArray()
                }

                is UiState.Error -> {
                    ProgressHelper.disableProgressDialog()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun init() {

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "Farm lands & crops"
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this@DetailedFarmActivity, FarmDetailsActivity::class.java))
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@DetailedFarmActivity, FarmDetailsActivity::class.java))
            }
        })

        val farmData = intent.getStringExtra("FARM_DETAIL_DATA")
        if (farmData != null) {
            val jsonObject = JSONObject(farmData)
            farmId = jsonObject.optString("farm_id")
            val ownerName = jsonObject.optString("owner_name")
            val surveyNumber = jsonObject.optString("survey_no")
            val villageName = jsonObject.optString("village_name")
            val totalArea = jsonObject.optDouble("total_plot_area")
            binding.nameTextView.text = buildString {
                append("Name")
                append(" $ownerName")
            }
            binding.surveyNumberTextView.text = buildString {
                append("Survey/LPM/Compartment No.")
                append(" $surveyNumber")
            }
            binding.totalAreaTextView.text = buildString {
                append(totalArea)
                append(" acre")
            }
            binding.villageNameTextView.text = villageName
        }

        binding.cropDSCRecyclerView.layoutManager = LinearLayoutManager(this)
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

        val dialog = AlertDialog.Builder(this@DetailedFarmActivity)
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        dialogBinding.sowingDateCardView.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                this,
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
        dialogBinding.selectCropCardView.setOnClickListener {
            showCropSelectionDialog(cropsJsonArray) { selectedCrop ->
                val cropId =
                    selectedCrop.optInt("id")
                val cropName =
                    selectedCrop.optString("name")
                selectedCropIdForDCS = cropId
                dialogBinding.cropNameForDCSTextView.text = cropName
            }
        }

        dialogBinding.saveCropButton.setOnClickListener {
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

    override fun onRecyclerItemClick(flag: Int, jsonObject: Any) {
        val dataObject = jsonObject as JSONObject
        val declarationId = dataObject.optInt("declaration_id").toString()
        when (flag) {
            UPDATE_CROP -> {
                farmerViewModel.updateFarmCropForDCS(
                    CryptoHelper.encryptField(declarationId).toString(),
                    CryptoHelper.encryptField("2026-03-01").toString()
                )
            }

            DELETE_CROP -> {
                farmerViewModel.deleteFarmCropForDCS(
                    CryptoHelper.encryptField(declarationId).toString()
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


        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val adapter = CropSelectionAdapter(cropList.toMutableList()) { selectedCrop ->
            onItemSelected(selectedCrop)
            dialog.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
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

    companion object {
        const val UPDATE_CROP = 1
        const val DELETE_CROP = 2
    }
}
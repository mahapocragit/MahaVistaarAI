package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAddCropBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CropCategoriesAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import org.json.JSONObject
import java.util.Calendar


class AddCropActivity : AppCompatActivity(), OnMultiRecyclerItemClickListener,
    DatePickerRequestListener {
    private lateinit var languageToLoad: String
    private lateinit var viewModel: FarmerViewModel
    private lateinit var textViewHeaderTitle: TextView
    private lateinit var imageMenuShow: ImageView
    private lateinit var imgBackArrow: ImageView
    private lateinit var receivedJson: JSONObject
    private var cropId: Int = 0
    private lateinit var binding: ActivityAddCropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AddCropActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityAddCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root, window)

        viewModel = ViewModelProvider(this)[FarmerViewModel::class.java]
        imgBackArrow = findViewById(R.id.imgBackArrow)
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imageMenuShow = findViewById(R.id.imageMenushow)
        textViewHeaderTitle.setText(R.string.select_crop)

        fetchCropInfo()
        textViewHeaderTitle.text = getString(R.string.select_crop)

        imageMenuShow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }

        imgBackArrow.visibility = View.VISIBLE
        imgBackArrow.setOnClickListener {
            onBackPressed()
        }
        viewModel.getCropCategoriesAndCropDetails(this, languageToLoad)
        ProgressHelper.showProgressDialog(this)
    }

    private fun fetchCropInfo() {
        viewModel.cropCategoryResponse.observe(this) {
            ProgressHelper.disableProgressDialog()
            val jSONObject = JSONObject(it.toString())
            val jsonDataArray = jSONObject.getJSONArray("data")
            var adapter =
                CropCategoriesAdapter(this, jsonDataArray, "TitleVideosDetailsAdpter", this)
            val str = intent.getStringExtra("NO_NEED_TO_ADD_SOWING_DATE")
            if (str != null) {
                adapter =
                    CropCategoriesAdapter(
                        this,
                        jsonDataArray,
                        "NO_NEED_TO_ADD_SOWING_DATE",
                        this
                    )
            }
            binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.mainRecyclerView.hasFixedSize()
            binding.mainRecyclerView.setAdapter(adapter)
        }
        viewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            receivedJson = obj as JSONObject
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
                onDateSelected(1, d, m, y) // ✅ Manually invoke
            }, year, month, day)

            datePickerDialog.setTitle(getString(R.string.select_sowing_date))
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    override fun onDateSelected(i: Int, day: Int, month: Int, year: Int) {
        if (i == 1) {
            val sowingDate = "$day-${month + 1}-$year"
            cropId = receivedJson.optInt("id")
            viewModel.saveFarmerSelectedCrop(this, sowingDate, cropId)
            viewModel.saveFarmerSelectedCrop.observe(this) {
                if (it != null) {
                    if (it.get("status").toString() == "200") {
                        Toast.makeText(this, R.string.selected_crop_saved, Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, DashboardScreen::class.java).apply {
                            putExtra("savedCropResponse", it.get("status").toString())
                        })
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }

}
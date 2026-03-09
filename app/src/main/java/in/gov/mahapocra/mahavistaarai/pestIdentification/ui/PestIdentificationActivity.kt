package `in`.gov.mahapocra.mahavistaarai.pestIdentification.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityPestIdentificationBinding
import `in`.gov.mahapocra.mahavistaarai.pestIdentification.CropAdapter
import `in`.gov.mahapocra.mahavistaarai.pestIdentification.CropModel
import `in`.gov.mahapocra.mahavistaarai.pestIdentification.data.model.AnalysisData
import `in`.gov.mahapocra.mahavistaarai.pestIdentification.data.repository.PredictRepository
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PestIdentificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPestIdentificationBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var appPreferenceManager: AppPreferenceManager
    private var cameraImageUri: Uri? = null
    private var cropList = ArrayList<CropModel>()
    private var selectedCropId: Int? = null
    private var farmerId = 0
    private var predictRespStoredId: Int = 0
    private lateinit var predictResult: String
    var languageToLoad = "mr"
    private var analysisData = AnalysisData(0, "", "", false, "", "", "")

    // --- Permissions ---
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                cameraLauncher.launch(
                    Intent(this, CameraActivity::class.java)
                )
            } else showToast("Camera permission denied")
        }

    // --- Camera Capture ---
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                val uriString = result.data?.getStringExtra("image_uri")
                val imageUri = uriString?.let { Uri.parse(it) }

                imageUri?.let {
                    cameraImageUri = imageUri
                    showToast("Camera image captured")
                    showPreviewLayout()
                }
            }
        }

    // --- Gallery Picker ---
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                if (isValidImage(it)) {
                    cameraImageUri = it
                    showToast("Image selected")
                    showPreviewLayout()
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.snackbar_pnd_message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (AppSettings.getLanguage(this@PestIdentificationActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityPestIdentificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.pest_identification_text)
        binding.relativeLayoutTopBar.imageViewHeaderBack.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imageViewHeaderBack.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        FirebaseMessaging.getInstance().unsubscribeFromTopic("generic_notifications")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to topic")
                } else {
                    Log.e("FCM", "Subscription failed", task.exception)
                }
            }

        appPreferenceManager = AppPreferenceManager(this)
        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        applyWindowInsets()
        handleBackPress()
        initViewModel()
        observeViewModel()
        setUpListeners()
        updateViewVisibility()
    }

    // ----------------------------- UI SETUP -----------------------------

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun isValidImage(uri: Uri): Boolean {
        val maxSize = 10 * 1024 * 1024 // 10 MB

        // ✅ Check file size
        val fileSize = contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0
        if (fileSize > maxSize) return false

        // ✅ Check MIME type
        val mimeType = contentResolver.getType(uri) ?: return false
        val allowedTypes = listOf(
            "image/jpeg",
            "image/jpg",
            "image/png"
        )

        return mimeType in allowedTypes
    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@PestIdentificationActivity, DashboardScreen::class.java))
            }
        })
    }

    private fun initViewModel() {
        val repo = PredictRepository(this)
        val factory = MainViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    // ----------------------------- OBSERVERS -----------------------------

    private fun observeViewModel() {
        // 1) Crop List
        viewModel.getCropListResponse.observe(this) { result ->
            try {
                val json = JSONObject(result)
                val dataArray = json.optJSONArray("data")

                if (dataArray != null && dataArray.length() > 0) {
                    // ✅ Save latest successful response
                    appPreferenceManager.saveString(
                        "CROP_LIST_JSON_ARRAY",
                        dataArray.toString()
                    )

                    loadCropsFromJsonArray(dataArray)
                } else {
                    loadFromCacheOrShowError()
                }

            } catch (e: Exception) {
                loadFromCacheOrShowError()
            }
        }

        // 2) Predict Response
        viewModel.result.observe(this) { result ->
            ProgressHelper.disableProgressDialog()
            Log.d("TAGGER", "observeViewModel: $result")
            try {
                predictResult = result
                val json = JSONObject(result)
                val success = json.optBoolean("success")
                val message = json.optString("message")
                val dataJSONObject = json.optJSONObject("data")
                val predictionArray = dataJSONObject?.optJSONArray("predictions")
                val predictionObject = predictionArray?.get(0) as JSONObject
                val diseaseId = predictionObject.optString("disease_id")
                viewModel.getPestAdvisory(this, diseaseId)
                cameraImageUri?.let { uri ->
                    val compressedUri = compressImageUri(uri, 70)
                    if (compressedUri != null) {
                        analysisData = AnalysisData(
                            farmerId = farmerId,
                            cropId = selectedCropId.toString(),
                            sowingDate = convertDateToServerFormat(binding.editSowingDate.text.toString()),
                            isSuccess = success,
                            responseString = result,
                            imageUri = compressedUri.toString(),
                            diseaseId = diseaseId
                        )
                    }
                }
                showToast(message)
            } catch (e: Exception) {
                Log.d("TAGGER", "observeViewModel: $e")
                showToast("Invalid response format")
                e.printStackTrace()
            }
        }

        viewModel.pestAdvisoryResponse.observe(this) {
            val json = JSONObject(it.toString())
            Log.d("TAGGER", "observeViewModel:>> $json")
            val cropNameMr = json.optString("crop_name_mr")
            val predictionJsonArray = json.optJSONArray("data")
            startActivity(Intent(this, ResultActivity::class.java).apply {
                putExtra("cropNameMr", cropNameMr.toString())
                putExtra("resultObjectString", predictResult)
                putExtra("imageUriString", cameraImageUri.toString())
                putExtra("predictionJsonObject", predictionJsonArray?.toString())
                putExtra("analysis_data", analysisData)
            })
        }

        viewModel.error.observe(this) {
            Log.d("TAGGER", "observeViewModel: $it")
        }
    }

    private fun loadCropsFromJsonArray(jsonArray: JSONArray) {
        cropList.clear()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            cropList.add(
                CropModel(
                    id = obj.getInt("id"),
                    name = if (languageToLoad == "en")
                        obj.getString("name")
                    else
                        obj.getString("name_mr")
                )
            )
        }

        showCropDialog()
    }

    private fun showCropDialog() {
        val dialog = Dialog(this).apply {
            setContentView(R.layout.dialog_search_list)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val searchEt = dialog.findViewById<EditText>(R.id.search_et)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)

        val cropAdapter = CropAdapter(cropList) { selectedCrop ->
            binding.editCropName.setText(selectedCrop.name)
            selectedCropId = selectedCrop.id
            dialog.dismiss()
        }

        recyclerView.apply {
            adapter = cropAdapter
            layoutManager = LinearLayoutManager(this@PestIdentificationActivity)
        }

        searchEt.addTextChangedListener {
            cropAdapter.filter(it.toString())
        }

        dialog.show()
    }

    private fun loadFromCacheOrShowError() {
        val cachedJson = appPreferenceManager.getString("CROP_LIST_JSON_ARRAY")

        if (!cachedJson.isNullOrEmpty()) {
            val cachedArray = JSONArray(cachedJson)
            loadCropsFromJsonArray(cachedArray)
        } else {
            Toast.makeText(
                this,
                "Unable to load crop list. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ----------------------------- LISTENERS -----------------------------

    private fun setUpListeners() {
        binding.cameraLayout.setOnClickListener { checkCameraPermission() }
        binding.galleryLayout.setOnClickListener { pickImage.launch("image/*") }
        binding.editCropName.setOnClickListener { viewModel.fetchCropList() }
        binding.editSowingDate.setOnClickListener { openDatePicker() }

        binding.deleteImageView.setOnClickListener {
            cameraImageUri = null
            updateViewVisibility()
            showToast("Image removed")
        }

        binding.btnAnalyzeSubmit.setOnClickListener {
            handleSubmit()
        }
    }

    private fun handleSubmit() {
        val cropType = binding.editCropName.text.toString()
        val date = binding.editSowingDate.text.toString()
        val image = cameraImageUri

        if (cropType.isBlank() || date.isBlank() || image == null) {
            showToast("Please fill all fields")
            return
        }

        // Compress before submitting
        val compressedUri = compressImageUri(image, 70)
        if (compressedUri != null) {
            viewModel.submit(
                this,
                selectedCropId!!,
                cropType,
                convertDateToServerFormat(date),
                compressedUri
            )
        } else showToast("Failed to process image")
    }

    // ----------------------------- CAMERA / FILE -----------------------------

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(
                Intent(this, CameraActivity::class.java)
            )
        } else requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    // ----------------------------- IMAGE PREVIEW -----------------------------

    private fun showPreviewLayout() {
        binding.imageSelectorLayout.visibility = View.GONE
        binding.previewLayout.visibility = View.VISIBLE
        binding.previewImage.setImageURI(cameraImageUri)
    }

    private fun updateViewVisibility() {
        if (cameraImageUri != null) showPreviewLayout()
        else {
            binding.previewLayout.visibility = View.GONE
            binding.imageSelectorLayout.visibility = View.VISIBLE
        }
    }

    // ----------------------------- HELPERS -----------------------------

    fun convertDateToServerFormat(dateStr: String): String {
        val input = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val output = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return input.parse(dateStr)?.let { output.format(it) } ?: ""
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    // ----------------------------- DATE PICKER -----------------------------

    private fun openDatePicker() {
        if (isFinishing || isDestroyed) return

        val calendar = Calendar.getInstance()

        val locale = Locale.ENGLISH
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        val contextWrapper = ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog)
        contextWrapper.apply { applyOverrideConfiguration(config) }

        val dialog = DatePickerDialog(
            contextWrapper,
            { _, year, month, day ->
                binding.editSowingDate.setText("$day/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.maxDate = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        dialog.show()
    }

    // ----------------------------- IMAGE COMPRESSION -----------------------------

    private fun compressImageUri(sourceUri: Uri, quality: Int = 95): Uri? {
        return try {
            // 1️⃣ Decode bitmap at ORIGINAL size
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, sourceUri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
                    decoder.isMutableRequired = false
                }
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, sourceUri)
            }

            // 2️⃣ Fix rotation using EXIF (CRITICAL)

            // 3️⃣ Create compressed file
            val compressedFile = File(
                cacheDir,
                "IMG_COMPRESSED_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(compressedFile).use { out ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality,
                    out
                )
            }

            Uri.fromFile(compressedFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.newshc

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityShcPdfViewBinding
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.app_util.StringFormatter.extractCropObjects
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ProgressHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

class PdfWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShcPdfViewBinding
    private lateinit var languageToLoad: String
    private var soilHealthCardId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@PdfWebViewActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityShcPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.soil_health_card)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



        val healthCardJsonStr = intent.getStringExtra("healthCardJson")
        val healthCardJson = healthCardJsonStr?.let { JSONObject(healthCardJsonStr) }
        healthCardJson?.let { setUpUILayout(healthCardJson) }

        val healthCardUrl = buildString {
            append(AppEnvironment.FARMER.baseUrl)
            append("shcServices/download_health_card/")
            append(soilHealthCardId)
        }
        loadPdf(healthCardUrl)

        binding.downloadButton.setOnClickListener {
            if (soilHealthCardId == ""){
                Toast.makeText(this, "No soil health card available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val healthCardUrl = buildString {
                append(AppEnvironment.FARMER.baseUrl)
                append("shcServices/download_health_card/")
                append(soilHealthCardId)
            }
            downloadPdf(healthCardUrl)
        }
    }

    private fun downloadPdf(url: String) {
        Log.d("TAGGER", "downloadPdf: $url")
        try {
            val request = DownloadManager.Request(Uri.parse(url))

            // Add browser user-agent header if needed
            request.addRequestHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
            )

            request.setTitle("Soil Health Card")
            request.setDescription("Downloading PDF…")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
            )

            val fileName = "SoilHealthCard_${System.currentTimeMillis()}.pdf"
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(this, "Download started…", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpUILayout(healthCardJson: JSONObject) {
        val fertilizerCombinationJSONArray = JSONArray()
        Log.d(TAG, "setUpUILayout: $healthCardJson")
        soilHealthCardId = healthCardJson.optString("id")
        val farmerJson = healthCardJson.optJSONObject("farmer")
        val plotJson = healthCardJson.optJSONObject("plot")
        val villageJson = healthCardJson.optJSONObject("village")
        val talukaJson = healthCardJson.optJSONObject("block")
        val districtJson = healthCardJson.optJSONObject("district")
        val rdfValuesJson = healthCardJson.optJSONObject("rdfValues")
        val fertilizerRecommendationJson =
            rdfValuesJson?.optJSONArray("fertilizerRecommendation_details")

        val cropObjects =
            fertilizerRecommendationJson?.let { extractCropObjects(it) } ?: emptyList()
        val cropNames = cropObjects.map { it.optString("crop") } // only names for dropdown



        binding.soilHealthCardLayout.addCropCardView.setOnClickListener {
            if (cropNames.isNotEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Select Crop")

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cropNames)

                builder.setAdapter(adapter) { _, which ->
                    binding.soilHealthCardLayout.fymTextView.visibility = View.VISIBLE
                    binding.soilHealthCardLayout.fymCardView.visibility = View.VISIBLE
                    val selectedJson = cropObjects[which]   // ✅ full JSON object
                    val selectedCrop = selectedJson.optString("crop")
                    val fymValueText = selectedJson.optString("fym")
                    val fymUnitText = selectedJson.optString("Fymunit")
                    val fymText = "Field yard manure / Compost / Fertilizer \n($fymUnitText) - $fymValueText"
                    binding.soilHealthCardLayout.fymTextView.text = fymText

                    // Show crop name in UI
                    binding.soilHealthCardLayout.addChangeCropTV.text = selectedCrop
                    binding.soilHealthCardLayout.selectedCropTextView.text = selectedCrop
                    // Print / log the full JSON object
                    // ✅ Reset old data
                    while (fertilizerCombinationJSONArray.length() > 0) {
                        fertilizerCombinationJSONArray.remove(0)
                    }

                    // Wrap combOne and combTwo with keys
                    val combOneWrapper = JSONObject().apply {
                        put("combination1", selectedJson.optJSONArray("combOne"))
                    }

                    val combTwoWrapper = JSONObject().apply {
                        put("combination2", selectedJson.optJSONArray("combTwo"))
                    }

                    // Add to parent array
                    fertilizerCombinationJSONArray.put(combOneWrapper)
                    fertilizerCombinationJSONArray.put(combTwoWrapper)

                    // ✅ Update recycler
                    binding.soilHealthCardLayout.fertilizerCombinationRecyclerView.layoutManager =
                        LinearLayoutManager(this)
                    binding.soilHealthCardLayout.fertilizerCombinationRecyclerView.adapter =
                        FertilizerCombinationAdapter(fertilizerCombinationJSONArray)
                }
                builder.show()
            } else {
                Toast.makeText(this, "No crops available", Toast.LENGTH_SHORT).show()
            }
        }


        val parameterInfosJson = rdfValuesJson?.optJSONArray("parameterInfos")
        binding.soilHealthCardLayout.farmerName.text = farmerJson?.optString("name")
        soilHealthCardId = healthCardJson.optString("id")
        binding.soilHealthCardLayout.shcNo.text = healthCardJson.optString("computedID")
        binding.soilHealthCardLayout.surveyNumberTextView.text = plotJson?.optString("surveyNo")
        binding.soilHealthCardLayout.farmSizeTextView.text = buildString {
            append(plotJson?.optDouble("area").toString())
            append(" acres")
        }
        binding.soilHealthCardLayout.villageTextView.text = villageJson?.optString("name")
        binding.soilHealthCardLayout.talukaTextView.text = talukaJson?.optString("name")
        binding.soilHealthCardLayout.districtTextView.text = districtJson?.optString("name")

        binding.soilHealthCardLayout.soilTestResultRecyclerView.layoutManager =
            LinearLayoutManager(this)
        binding.soilHealthCardLayout.soilTestResultRecyclerView.adapter =
            parameterInfosJson?.let {
                SoilTestResultAdapter(
                    parameterInfosJson
                )
            }
    }

    private fun loadPdf(url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(url)
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/131.0"
                    )
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val file = File(cacheDir, "health_card.pdf")

                    response.body?.byteStream()?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        binding.pdfView.fromFile(file)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .load()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PdfWebViewActivity,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
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
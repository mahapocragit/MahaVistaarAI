package `in`.gov.mahapocra.mahavistaarai.util

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.URLUtil
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants.TAG
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import java.io.File
import java.net.NetworkInterface
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object LocalCustom {
    fun configureLocale(baseContext: Context, languageToLoad: String): Context {
        val locale = Locale.forLanguageTag(languageToLoad)
        Locale.setDefault(locale)

        val config = Configuration(baseContext.resources.configuration)

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                config.setLocale(locale)
                baseContext.createConfigurationContext(config)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                config.setLocale(locale)
                baseContext.createConfigurationContext(config)
            }
            else -> {
                @Suppress("DEPRECATION")
                config.locale = locale
                @Suppress("DEPRECATION")
                baseContext.resources.updateConfiguration(
                    config,
                    baseContext.resources.displayMetrics
                )
                baseContext
            }
        }
    }

    fun switchLanguage(context: Context, lang: String) {
        val currentLang = Locale.getDefault().language
        if (currentLang != lang) {
            AppSettings.setLanguage(context, lang)  // Save to SharedPreferences
            if (context is Activity) {
                context.recreate()  // Only recreate if language is different
            }
        }
    }

    fun generateRandom10DigitNumber(): Int {
        return (100000000..999999999).random()
    }

    fun downloadPdf(context: Context, pdfUrl: String) {
        val fileName = URLUtil.guessFileName(pdfUrl, null, "application/pdf")

        val request = DownloadManager.Request(Uri.parse(pdfUrl)).apply {
            setTitle("Downloading PDF")
            setDescription("Downloading $fileName")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setMimeType("application/pdf")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    fun getVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "unknown"
        }
    }

    fun getSowingDateWithYear(sowingDateUnfiltered: String): String {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        var formattedDate = ""

        if (sowingDateUnfiltered.contains("/")) {
            val parts = sowingDateUnfiltered.split("/")
            if (parts.size == 2) {
                try {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()

                    // Agri year: June to May
                    val adjustedYear = if (month in 6..12) currentYear else currentYear - 1

                    // Changed format to DD-MM-YYYY
                    formattedDate =
                        String.format(Locale.US, "%02d-%02d-%04d", day, month, adjustedYear)
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Invalid number format in sowing date: $sowingDateUnfiltered",
                        e
                    )
                    formattedDate = ""
                }
            } else {
                Log.e(TAG, "Invalid date format: $sowingDateUnfiltered")
            }
        } else {
            Log.e(TAG, "Unsupported date format: $sowingDateUnfiltered")
        }

        return formattedDate
    }

    fun getSowingDateInDayMonthYearFormat(sowingDateUnfiltered: String): String {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        var formattedDate = ""

        if (sowingDateUnfiltered.contains("/")) {
            val parts = sowingDateUnfiltered.split("/")
            if (parts.size == 2) {
                try {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    val adjustedYear = if (month > currentMonth) currentYear - 1 else currentYear
                    formattedDate =
                        String.format(Locale.US, "%02d-%02d-%04d", day, month, adjustedYear)
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Invalid number format in sowing date: $sowingDateUnfiltered",
                        e
                    )
                    formattedDate = ""
                }
            } else {
                Log.e(TAG, "Invalid date format: $sowingDateUnfiltered")
            }
        } else {
            Log.e(TAG, "Unsupported date format: $sowingDateUnfiltered")
        }

        return formattedDate
    }

    fun isStrongPassword(password: String): Boolean {
        val minLength = 8
        val specialChars = "!@#\$%^&*()_+=|<>?{}\\[\\]~-"

        val commonPasswords = setOf(
            "password", "123456", "12345678", "qwerty", "abc123", "111111",
            "letmein", "123123", "welcome", "admin", "passw0rd", "iloveyou"
        )

        if (password.length < minLength) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { it in specialChars }) return false
        if (password.lowercase() in commonPasswords) return false

        return true
    }

    fun extractUniqueCommNames(jsonArray: JSONArray): Array<String> {
        val commNamesSet = mutableSetOf<String>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            if (obj.has("comm_name")) {
                commNamesSet.add(obj.getString("comm_name"))
            }
        }
        return commNamesSet.toTypedArray()
    }

    fun showCommNameDialog(
        context: Context,
        commNames: Array<String>,
        onItemSelected: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_searchable_list, null)
        val searchView = dialogView.findViewById<SearchView>(R.id.search_view)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, commNames.toList())
        listView.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.search_commodity)
            .setView(dialogView)
            .setNegativeButton(R.string.okay, null)
            .create()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            selectedItem?.let {
                onItemSelected(it)
                dialog.dismiss()
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        dialog.show()
    }

    fun toSHA512(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun encodeToBase64(input: String): String {
        return Base64.encode(input.encodeToByteArray())
    }

    fun getMobileOrWifiIp(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in Collections.list(interfaces)) {
                for (addr in Collections.list(intf.inetAddresses)) {
                    if (!addr.isLoopbackAddress && addr.hostAddress.indexOf(':') < 0) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getLocationUsingLocationManager(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)

        for (provider in providers.reversed()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                return location
            }
        }
        return null
    }

    fun convertDateFormat(dateStr: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateStr)
        return outputFormat.format(date!!)
    }

    fun getLatestAdvisoriesAsJsonArray(advisoryArray: JSONArray): JSONArray {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        // Step 1: Find the latest date
        val latestDate = (0 until advisoryArray.length()).maxOfOrNull {
            dateFormat.parse(
                advisoryArray.getJSONObject(it).getString("cropsap_advisory_date")
            )
        }

        // Step 2: Filter advisories with the latest date and collect into a new JSONArray
        val filteredArray = JSONArray()
        for (i in 0 until advisoryArray.length()) {
            val obj = advisoryArray.getJSONObject(i)
            val objDate = dateFormat.parse(obj.getString("cropsap_advisory_date"))
            if (objDate == latestDate) {
                filteredArray.put(obj)
            }
        }

        return filteredArray
    }

    fun uiResponsive(view: View) {
        val startPadding = view.paddingStart
        val topPadding = view.paddingTop
        val endPadding = view.paddingEnd
        val bottomPadding = view.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                bars.left + startPadding,
                bars.top + topPadding,
                bars.right + endPadding,
                bars.bottom + bottomPadding
            )
            insets
        }
    }

    fun createSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM // or Gravity.CENTER
        params.setMargins(24, 0, 24, 200) // left, top, right, bottom
        view.layoutParams = params
        snackbar.show()
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(
            Date()
        )
    }

    fun getSevenDaysBeforeDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7) // Go back 7 days
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).format(calendar.time)
    }

    fun createPartFromString(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part {
        val file =
            File(fileUri.path!!) // If you have URI from storage, you may need a proper file copy
        val requestFile = RequestBody.create(
            context.contentResolver.getType(fileUri)?.toMediaTypeOrNull(),
            file
        )
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun downloadFile(context: Context, fileUrl: String) {
        try {
            val uri = Uri.parse(fileUrl)

            // Guess filename from URL + headers
            val fileName = URLUtil.guessFileName(fileUrl, null, null)

            val request = DownloadManager.Request(uri)
            request.setTitle(fileName)
            request.setDescription("Downloading file...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            request.setAllowedOverMetered(true)
            request.setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Downloading: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


}
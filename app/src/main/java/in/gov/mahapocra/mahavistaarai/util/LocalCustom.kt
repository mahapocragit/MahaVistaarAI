package `in`.gov.mahapocra.mahavistaarai.util

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.URLUtil
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object LocalCustom {
    fun configureLocale(baseContext: Context, languageToLoad: String): Context {
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)

        val config = Configuration(baseContext.resources.configuration)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                config.setLocale(locale)
                return baseContext.createConfigurationContext(config)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                config.setLocale(locale)
                return baseContext.createConfigurationContext(config)
            }

            else -> {
                // Deprecated, but fallback for older devices
                config.locale = locale
                baseContext.resources.updateConfiguration(
                    config,
                    baseContext.resources.displayMetrics
                )
                return baseContext
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
                        "TAGGER",
                        "Invalid number format in sowing date: $sowingDateUnfiltered",
                        e
                    )
                    formattedDate = ""
                }
            } else {
                Log.e("TAGGER", "Invalid date format: $sowingDateUnfiltered")
            }
        } else {
            Log.e("TAGGER", "Unsupported date format: $sowingDateUnfiltered")
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
                        "TAGGER",
                        "Invalid number format in sowing date: $sowingDateUnfiltered",
                        e
                    )
                    formattedDate = ""
                }
            } else {
                Log.e("TAGGER", "Invalid date format: $sowingDateUnfiltered")
            }
        } else {
            Log.e("TAGGER", "Unsupported date format: $sowingDateUnfiltered")
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

    fun showCaptchaDialog(context: Context, onResult: (Boolean) -> Unit) {
        var captcha = CaptchaGenerator.generateCaptchaBitmap(300, 100)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_captcha, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.captchaImage)
        val inputField = dialogView.findViewById<EditText>(R.id.captchaInput)
        val regenerateCaptchaTextView =
            dialogView.findViewById<TextView>(R.id.regenerateCaptchaTextView)

        imageView.setImageBitmap(captcha.bitmap)
        regenerateCaptchaTextView.setOnClickListener {
            captcha = CaptchaGenerator.generateCaptchaBitmap(300, 100)
            imageView.setImageBitmap(captcha.bitmap)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.verify_captcha))
            .setView(dialogView)
            .setPositiveButton(R.string.reg_submit, null)  // Override later
            .setNegativeButton(R.string.cancel_it) { _, _ ->
                onResult(false)
            }
            .create()

        dialog.setOnShowListener {
            val submitButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            submitButton.setOnClickListener {
                val userInput = inputField.text.toString().trim().uppercase()
                val captchaCode = captcha.code.uppercase()

                when {
                    userInput.isEmpty() -> {
                        inputField.error = "Please enter the CAPTCHA"
                    }

                    userInput.length != 6 -> {
                        inputField.error = "CAPTCHA must be exactly 6 characters"
                    }

                    userInput == captchaCode -> {
                        Toast.makeText(context, "CAPTCHA Verified ✅", Toast.LENGTH_SHORT).show()
                        onResult(true)
                        dialog.dismiss()
                    }

                    else -> {
                        Toast.makeText(context, "Incorrect CAPTCHA ❌", Toast.LENGTH_SHORT).show()
                        inputField.text?.clear()
                        inputField.requestFocus()
                    }
                }
            }
        }

        dialog.show()
    }

    fun toSHA512(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
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
        // Handle insets manually
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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

}
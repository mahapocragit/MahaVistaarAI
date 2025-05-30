package `in`.gov.mahapocra.mahavistaarai.util

import android.app.Activity
import android.app.DownloadManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.webkit.URLUtil
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor

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
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val versionName = packageInfo!!.versionName
        return versionName
    }

    fun getSowingDateWithYear(sowingDateUnfiltered: String): String {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        var formattedDate = ""

        if (sowingDateUnfiltered.contains("/")) {
            val parts = sowingDateUnfiltered.split("/")
            if (parts.size == 2) {
                try {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    val adjustedYear = if (month > currentMonth) currentYear - 1 else currentYear
                    formattedDate = String.format(Locale.US, "%04d-%02d-%02d", adjustedYear, month, day)
                } catch (e: Exception) {
                    Log.e("TAGGER", "Invalid number format in sowing date: $sowingDateUnfiltered", e)
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
                    formattedDate = String.format(Locale.US, "%02d-%02d-%04d", day, month, adjustedYear)
                } catch (e: Exception) {
                    Log.e("TAGGER", "Invalid number format in sowing date: $sowingDateUnfiltered", e)
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


    fun logThis(message:String){
        Log.d("TAGGER", "logThis: $message")
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
            .setNegativeButton(R.string.cancel, null)
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
        val regenerateCaptchaTextView = dialogView.findViewById<TextView>(R.id.regenerateCaptchaTextView)

        imageView.setImageBitmap(captcha.bitmap)
        regenerateCaptchaTextView.setOnClickListener {
            captcha = CaptchaGenerator.generateCaptchaBitmap(300, 100)
            imageView.setImageBitmap(captcha.bitmap)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Verify CAPTCHA")
            .setView(dialogView)
            .setPositiveButton("Submit", null)  // Override later
            .setNegativeButton("Cancel") { _, _ ->
                onResult(false)  // User canceled, so false
            }
            .create()

        dialog.setOnShowListener {
            val submitButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            submitButton.setOnClickListener {
                val userInput = inputField.text.toString().trim().uppercase()
                val captchaCode = captcha.code.uppercase()

                if (userInput.isEmpty()) {
                    inputField.error = "Please enter the CAPTCHA"
                    return@setOnClickListener
                }

                if (userInput == captchaCode) {
                    Toast.makeText(context, "CAPTCHA Verified ✅", Toast.LENGTH_SHORT).show()
                    onResult(true)  // Notify success
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Incorrect CAPTCHA ❌", Toast.LENGTH_SHORT).show()
                    inputField.text?.clear()
                    inputField.requestFocus()
                }
            }
        }

        dialog.show()
    }

}
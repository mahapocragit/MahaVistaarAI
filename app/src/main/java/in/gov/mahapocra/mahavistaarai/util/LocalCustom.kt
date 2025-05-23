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
import android.webkit.URLUtil
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.data.api.APIKeys
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
            UIToastMessage.show(context, "Downloading $fileName")
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

    fun verifyWithRecaptcha(context: Context, callback: (Boolean) -> Unit) {
        SafetyNet.getClient(context).verifyWithRecaptcha(APIKeys.SITE_KEY)
            .addOnSuccessListener(ContextCompat.getMainExecutor(context)) { response ->
                val userResponseToken = response.tokenResult
                callback(userResponseToken?.isNotEmpty() == true)
            }
            .addOnFailureListener(ContextCompat.getMainExecutor(context)) { e ->
                if (e is ApiException) {
                    Log.d(TAG, "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                } else {
                    Log.d(TAG, "Error: ${e.message}")
                }
                callback(false)
            }
    }

}
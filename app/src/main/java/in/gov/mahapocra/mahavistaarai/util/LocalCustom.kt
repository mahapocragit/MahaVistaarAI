package `in`.gov.mahapocra.mahavistaarai.util

import android.app.Activity
import android.app.DownloadManager
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
import `in`.co.appinventor.services_api.settings.AppSettings
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeParseException
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
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val versionName = packageInfo!!.versionName
        return versionName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSowingDateWithYear(sowingDateUnfiltered: String): String {
        Log.d("TAGGER", "getSowingDateWithYear: $sowingDateUnfiltered")

        val currentDate = getCurrentDate() // e.g., "2025-05-13"
        val currentYear = Year.now().value

        // Step 1: Convert Marathi/Devanagari numerals to English
        val normalizedDateInput = convertToEnglishDigits(sowingDateUnfiltered)

        // Step 2: Format date into yyyy-MM-dd
        val formattedSowingDate = when {
            normalizedDateInput.contains("/") -> {
                val parts = normalizedDateInput.split("/")
                if (parts.size == 2) {
                    "%04d-%02d-%02d".format(currentYear, parts[1].toInt(), parts[0].toInt())
                } else {
                    Log.e("TAGGER", "Invalid slash-format sowing date: $normalizedDateInput")
                    return currentDate
                }
            }

            normalizedDateInput.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                // Already in yyyy-MM-dd
                normalizedDateInput
            }

            else -> {
                Log.e("TAGGER", "Unknown sowing date format: $normalizedDateInput")
                return currentDate
            }
        }

        // Step 3: Safely parse the date
        val sowingDate = try {
            LocalDate.parse(formattedSowingDate)
        } catch (e: DateTimeParseException) {
            Log.e("TAGGER", "Date parsing failed for: $formattedSowingDate", e)
            return currentDate
        }

        val today = LocalDate.parse(currentDate)

        // Step 4: Decide final return date format
        val finalDate = if (today.isBefore(sowingDate)) {
            "${formattedSowingDate.substring(8)}-${formattedSowingDate.substring(5, 7)}-${currentYear - 1}"
        } else {
            "${today.dayOfMonth.toString().padStart(2, '0')}-${
                today.monthValue.toString().padStart(2, '0')
            }-${today.year}"
        }

        return finalDate
    }

    // Converts Marathi numerals (०-९) to English (0-9)
    private fun convertToEnglishDigits(input: String): String {
        return input.map {
            when (it) {
                in '०'..'९' -> (it.code - '०'.code + '0'.code).toChar()
                else -> it
            }
        }.joinToString("")
    }

    // Returns the current date in yyyy-MM-dd format
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

}
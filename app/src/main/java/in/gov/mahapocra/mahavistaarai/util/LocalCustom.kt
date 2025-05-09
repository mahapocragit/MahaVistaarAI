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
import android.webkit.URLUtil
import `in`.co.appinventor.services_api.settings.AppSettings
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
}
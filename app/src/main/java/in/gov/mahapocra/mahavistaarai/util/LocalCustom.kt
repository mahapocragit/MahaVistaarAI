package `in`.gov.mahapocra.mahavistaarai.util

import android.app.DownloadManager
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import javax.net.ssl.HttpsURLConnection

object LocalCustom {
    fun configureLocale(baseContext: Context, languageToLoad:String){
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
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

     fun loadPdfFromUrl(url: String, webView: PDFView) {
        CoroutineScope(Dispatchers.IO).launch {
            // Fetch the InputStream of the PDF file.
            val inputStream = fetchPdfStream(url)

            // Switch back to the main thread to update UI.
            withContext(Dispatchers.Main) {
                inputStream?.let {
                    // Load the PDF into the PDFView.
                    webView.fromStream(it).load()
                }
            }
        }
    }

    // Fetches the PDF InputStream from the given URL.
    // Uses HTTPS connection to securely download the PDF file.
    private fun fetchPdfStream(urlString: String): InputStream? {
        return try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpsURLConnection

            // Check if the connection response is successful (HTTP 200 OK).
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedInputStream(urlConnection.inputStream)
            } else {
                null
            }
        } catch (e: IOException) {
            // Print the error in case of a network failure.
            e.printStackTrace()
            null
        }
    }
}
package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object UriFileHelper {

    fun uriToFilePart(uri: Uri, partName: String, contentResolver: ContentResolver): MultipartBody.Part {
        val fileName = getFileName(uri, contentResolver)
        val prefix = if (fileName.length >= 3) fileName else "tmpfile"

        val tempFile = File.createTempFile(prefix, null, null) // suffix null = no extension
        contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = tempFile.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, requestFile)
    }

    private fun getFileName(uri: Uri, contentResolver: ContentResolver): String {
        var name = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name.ifEmpty { "tempfile" }
    }

    fun openYouTube(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage("com.google.android.youtube") // Force YouTube app
            context.startActivity(intent)
        } catch (e: Exception) {
            // If YouTube app not installed → open in browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

}
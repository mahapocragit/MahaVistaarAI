package `in`.gov.mahapocra.mahavistaarai.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object UriFileHelper {

    fun uriToFilePart(uri: Uri, fieldName: String, contentResolver: ContentResolver): MultipartBody.Part {
        // Get file name
        var fileName = "tempFile"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }

        // Create a temp file in cache
        val tempFile = File.createTempFile(
            fileName.substringBeforeLast("."),
            "." + fileName.substringAfterLast(".")
        )

        // Copy content from URI to temp file
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        // Prepare RequestBody
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

        // Return only the MultipartBody.Part
        return MultipartBody.Part.createFormData(fieldName, fileName, requestBody)
    }

}
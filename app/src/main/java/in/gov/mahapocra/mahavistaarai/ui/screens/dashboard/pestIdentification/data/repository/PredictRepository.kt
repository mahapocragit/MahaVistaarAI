package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.data.repository

import android.content.Context
import android.net.Uri
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class PredictRepository(private val context: Context) {

    suspend fun predict(cropId: Int, cropType: String, sowingDate: String, imageUri: Uri) =
        withContext(Dispatchers.IO) {

            // Determine MIME type from URI
            val mime = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val extension = when (mime) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                else -> ".jpg" // fallback
            }

            // Create temp file with proper extension
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}$extension")
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }

            // Prepare MultipartBody with correct MIME
            val imageBody = file.asRequestBody(mime.toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, imageBody)

            // Prepare text parts
            val cropIdPart = cropId.toString().toRequestBody("text/plain".toMediaType())
            val cropPart = cropType.toRequestBody("text/plain".toMediaType())
            val datePart = sowingDate.toRequestBody("text/plain".toMediaType())

            // Call API
            RetrofitClient.getInstance(AppEnvironment.PREDICT_URL.baseUrl)
                .predictPest(cropIdPart, cropPart, datePart, imagePart)
        }

    suspend fun submitFeedback(bearerToken: String, responseId: Int, feedbackStr: String) =
        withContext(Dispatchers.IO) {

            // Prepare text parts
            val cropIdPart = responseId.toString().toRequestBody("text/plain".toMediaType())
            val cropPart = feedbackStr.toRequestBody("text/plain".toMediaType())

            // Call API
            //RetrofitClient.getInstance(AppEnvironment.DBT_BASE_URL.baseUrl).submitFeedback(cropIdPart,cropPart)
            RetrofitClient.getInstance(AppEnvironment.FARMER.baseUrl).submitFeedback(bearerToken, cropIdPart, cropPart)
        }

    suspend fun fetchCropList(accessToken: String) = withContext(Dispatchers.IO) {
        RetrofitClient.getInstance(AppEnvironment.FARMER.baseUrl).fetchCropList("Bearer $accessToken")
    }

    suspend fun getPestAdvisory(pestId: String, bearerToken: String) = withContext(Dispatchers.IO) {
        val part = MultipartBody.Part.createFormData(
            name = "pd_id",
            value = pestId
        )
        RetrofitClient
            .getInstance(AppEnvironment.FARMER.baseUrl)
            .getPestAdvisory("Bearer $bearerToken", part)
    }

    suspend fun storeResponse(bearerToken: String,
        farmerId: Int,
        cropId: String,
        sowingDate: String,
        isSuccess: Boolean,
        responseString: String,
        imageUri: Uri,
        diseaseId:String
    ) =
        withContext(Dispatchers.IO) {
            // Determine MIME type from URI
            val mime = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val extension = when (mime) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                else -> ".jpg" // fallback
            }

            // Create temp file with proper extension
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}$extension")
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }

            // Prepare MultipartBody with correct MIME
            val imageBody = file.asRequestBody(mime.toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, imageBody)

            // Prepare text parts
            val farmerId = farmerId.toString().toRequestBody("text/plain".toMediaType())
            val cropPart = cropId.toRequestBody("text/plain".toMediaType())
            val datePart = sowingDate.toRequestBody("text/plain".toMediaType())
            val successPart = isSuccess.toString().toRequestBody("text/plain".toMediaType())
            val responsePart = responseString.toRequestBody("text/plain".toMediaType())
            val diseaseIdPart = diseaseId.toRequestBody("text/plain".toMediaType())

            // Call API
            RetrofitClient.getInstance(AppEnvironment.FARMER.baseUrl).storeResponseAgainstCropImage(
                "Bearer $bearerToken",
                imagePart,
                farmerId,
                cropPart,
                datePart,
                successPart,
                responsePart,
                diseaseIdPart
            )
        }
}
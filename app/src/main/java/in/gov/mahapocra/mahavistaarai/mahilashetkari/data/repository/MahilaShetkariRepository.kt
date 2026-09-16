package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository

import com.google.gson.Gson
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.NetworkModule
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api.CertificateApi
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api.MahilaShetkariApi
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.ApiResult
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.AadhaarVerifyData
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApiResponseDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationStatusDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.CasteCategoryDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.CertificateByAckRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.DistrictDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ErrorEnvelope
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.SendOtpData
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.SendOtpRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.SubmitData
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.TalukaDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.VerifyOtpRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.VillageDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.WorkTypeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

class MahilaShetkariRepository(
    private val api: MahilaShetkariApi,
    private val certificateApi: CertificateApi
) {

    suspend fun getDistricts(divisionId: Int? = null): ApiResult<List<DistrictDto>> =
        safeCall { api.getDistricts(divisionId) }

    suspend fun getTalukas(districtId: Int): ApiResult<List<TalukaDto>> =
        safeCall { api.getTalukas(districtId) }

    suspend fun getVillages(talukaId: Int): ApiResult<List<VillageDto>> =
        safeCall { api.getVillages(talukaId) }

    suspend fun getWorkTypes(): ApiResult<List<WorkTypeDto>> =
        safeCall { api.getWorkTypes() }

    suspend fun getCasteCategories(): ApiResult<List<CasteCategoryDto>> =
        safeCall { api.getCasteCategories() }

    suspend fun sendOtp(aadhaarNo: String): ApiResult<SendOtpData> =
        safeCall { api.sendAadhaarOtp(SendOtpRequest(aadhaarNo)) }

    suspend fun verifyOtp(aadhaarNo: String, txn: String, otp: String): ApiResult<AadhaarVerifyData> =
        safeCall { api.verifyAadhaarOtp(VerifyOtpRequest(aadhaarNo, txn, otp)) }

    suspend fun submitApplication(request: ApplicationRequest): ApiResult<SubmitData> =
        safeCall { api.submitApplication(request) }

    suspend fun getStatusByAck(ackNo: String): ApiResult<ApplicationStatusDto> =
        safeCall { api.getStatusByAck(ackNo) }

    suspend fun getStatusByNameVillage(name: String, villageId: Int): ApiResult<ApplicationStatusDto> =
        safeCall { api.getStatusByNameVillage(name, villageId) }

    /** Two-step flow per CERTIFICATE_API.md: (1) resolve the ack. no. to a
     *  download_url via the certificate service's by-ack endpoint — this is
     *  also where "pending review" / "rejected" errors surface — then
     *  (2) stream the PDF bytes from that URL. Runs on its own base URL/host
     *  (NetworkModule.CERTIFICATE_BASE_URL), separate from the main app API,
     *  and the PDF step returns raw bytes rather than an ApiResponseDto<T>
     *  envelope, so neither step can reuse safeCall(). Bytes are read here
     *  (still on the IO dispatcher) so callers never need to touch the raw
     *  ResponseBody / do blocking I/O themselves. */
    suspend fun downloadCertificate(ackNo: String): ApiResult<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val byAckResponse = certificateApi.getByAck(CertificateByAckRequest(ackNo))
            val byAckBody = byAckResponse.body()
            val downloadUrl = if (byAckResponse.isSuccessful && byAckBody?.success == true) {
                byAckBody.data?.downloadUrl
            } else {
                null
            }
            if (downloadUrl == null) {
                val errorText = byAckResponse.errorBody()?.string()
                val parsed = errorText?.let {
                    runCatching { Gson().fromJson(it, ErrorEnvelope::class.java) }.getOrNull()
                }
                return@withContext ApiResult.Error(
                    parsed?.message ?: byAckBody?.message ?: "Could not fetch the certificate."
                )
            }

            val fullUrl = NetworkModule.CERTIFICATE_BASE_URL.trimEnd('/') + downloadUrl
            val pdfResponse = certificateApi.downloadPdf(fullUrl)
            if (pdfResponse.isSuccessful && pdfResponse.body() != null) {
                ApiResult.Success(pdfResponse.body()!!.bytes())
            } else {
                ApiResult.Error("Could not download the certificate.")
            }
        } catch (e: IOException) {
            ApiResult.Error("Network error. Check your internet connection.")
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Something went wrong.")
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> Response<ApiResponseDto<T>>): ApiResult<T> =
        withContext(Dispatchers.IO) {
            try {
                val response = block()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (body.success) {
                        ApiResult.Success(body.data as T, body.message)
                    } else {
                        // 200/201 with success:false — envelope still carries the message.
                        ApiResult.Error(body.message, body.errors)
                    }
                } else {
                    // Non-2xx (400/404/500) — the envelope is in errorBody(), not body().
                    val errorJson = response.errorBody()?.string()
                    val parsed = errorJson?.let {
                        runCatching { Gson().fromJson(it, ErrorEnvelope::class.java) }.getOrNull()
                    }
                    ApiResult.Error(
                        parsed?.message ?: "Something went wrong (HTTP ${response.code()}).",
                        parsed?.errors
                    )
                }
            } catch (e: IOException) {
                ApiResult.Error("Network error. Check your internet connection and try again.")
            } catch (e: Exception) {
                ApiResult.Error(e.localizedMessage ?: "Something went wrong. Please try again.")
            }
        }
}

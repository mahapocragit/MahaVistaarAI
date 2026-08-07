package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository

import com.google.gson.Gson
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api.MahilaShetkariApi
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.ApiResult
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.AadhaarVerifyData
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApiResponseDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApplicationStatusDto
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

class MahilaShetkariRepository(private val api: MahilaShetkariApi) {

    suspend fun getDistricts(divisionId: Int? = null): ApiResult<List<DistrictDto>> =
        safeCall { api.getDistricts(divisionId) }

    suspend fun getTalukas(districtId: Int): ApiResult<List<TalukaDto>> =
        safeCall { api.getTalukas(districtId) }

    suspend fun getVillages(talukaId: Int): ApiResult<List<VillageDto>> =
        safeCall { api.getVillages(talukaId) }

    suspend fun getWorkTypes(): ApiResult<List<WorkTypeDto>> =
        safeCall { api.getWorkTypes() }

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

    /** Certificate download is a raw PDF stream on success, but a JSON error
     *  envelope (application/json) on failure — see API_README.md section 4.3 —
     *  so it can't reuse the generic ApiResponseDto<T> path used everywhere else.
     *  Bytes are read here (still on the IO dispatcher) so callers never need
     *  to touch the raw ResponseBody / do blocking I/O themselves. */
    suspend fun downloadCertificate(ackNo: String): ApiResult<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val response: Response<ResponseBody> = api.downloadCertificate(ackNo)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!.bytes())
            } else {
                val errorText = response.errorBody()?.string()
                val parsed = errorText?.let {
                    runCatching { Gson().fromJson(it, ErrorEnvelope::class.java) }.getOrNull()
                }
                ApiResult.Error(parsed?.message ?: "Could not download the certificate.")
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

package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api

import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.ApiResponseDto
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.CertificateByAckRequest
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto.CertificateData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/** Woman Farmer Certificate service — see CERTIFICATE_API.md. Lives on its own
 *  base URL (NetworkModule.CERTIFICATE_BASE_URL), separate from the main
 *  "mahila-shetkari-service" API. */
interface CertificateApi {

    @POST("mahila-shetkari-service/certificate/api/by-ack/")
    suspend fun getByAck(
        @Body body: CertificateByAckRequest
    ): Response<ApiResponseDto<CertificateData>>

    /** [url] must be the full absolute URL (base URL + data.download_url) —
     *  an @Url value overrides the Retrofit client's base URL entirely. */
    @GET
    suspend fun downloadPdf(@Url url: String): Response<ResponseBody>
}
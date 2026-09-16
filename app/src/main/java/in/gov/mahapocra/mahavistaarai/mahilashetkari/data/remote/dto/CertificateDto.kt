package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CertificateByAckRequest(@SerializedName("ack_no") val ackNo: String)

/** See CERTIFICATE_API.md section 1/3 — data.download_url is a path relative
 *  to the certificate service's base URL, not the main app API's host. */
data class CertificateData(
    @SerializedName("certificate_no") val certificateNo: String,
    @SerializedName("applicant_name") val applicantName: String,
    val village: String?,
    val taluka: String?,
    val district: String?,
    @SerializedName("issued_on") val issuedOn: String?,
    @SerializedName("pdf_size_bytes") val pdfSizeBytes: Long? = null,
    @SerializedName("download_url") val downloadUrl: String
)
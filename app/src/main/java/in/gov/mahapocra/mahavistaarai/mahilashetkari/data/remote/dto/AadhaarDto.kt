package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(@SerializedName("aadhaar_no") val aadhaarNo: String)

data class SendOtpData(val txn: String)

data class VerifyOtpRequest(
    @SerializedName("aadhaar_no") val aadhaarNo: String,
    val txn: String,
    val otp: String
)

data class AadhaarVerifyData(
    val name: String,
    @SerializedName("name_in_marathi") val nameInMarathi: String,
    val dob: String,
    val age: Int,
    val gender: String,
    val relation: String,
    val address: String,
    val pincode: String,
    // Full base64 data URI (data:image/jpeg;base64,...), not a file path/URL
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("vault_id") val vaultId: String
)

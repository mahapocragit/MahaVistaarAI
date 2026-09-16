package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApplicationRequest(
    @SerializedName("applicant_name") val applicantName: String,
    @SerializedName("applicant_name_mr") val applicantNameMr: String,
    @SerializedName("aadhaar_no") val aadhaarNo: String,
    @SerializedName("photo_url") val photoUrl: String,
    @SerializedName("caste_category") val casteCategory: Int? = null,
    val gender: String? = null,
    val mobile: String,
    @SerializedName("permanent_address") val permanentAddress: String? = null,
    @SerializedName("current_address") val currentAddress: String? = null,
    val dob: String? = null,
    val age: Int? = null,
    val district: Int? = null,
    val taluka: Int? = null,
    val village: Int? = null,
    @SerializedName("work_types") val workTypes: List<Int>,
    val declaration: Boolean,
    @SerializedName("family_member_has_farmer_id") val hasFamilyFarmerId: Boolean,
    @SerializedName("family_member_farmer_id") val farmerId: String? = null,
    @SerializedName("family_member_farmer_name") val farmerName: String? = null
)

data class SubmitData(@SerializedName("acknowledgment_no") val acknowledgmentNo: String)

data class ApplicationStatusDto(
    @SerializedName("acknowledgment_no") val acknowledgmentNo: String,
    @SerializedName("applicant_name") val applicantName: String,
    val mobile: String,
    @SerializedName("district_name") val districtName: String?,
    @SerializedName("taluka_name") val talukaName: String?,
    @SerializedName("village_name") val villageName: String?,
    val status: String,
    @SerializedName("status_display") val statusDisplay: String,
    @SerializedName("status_step") val statusStep: Int,
    @SerializedName("rejection_reason") val rejectionReason: String? = null,
    @SerializedName("submitted_at") val submittedAt: String? = null,
    @SerializedName("reviewed_at") val reviewedAt: String? = null,
    @SerializedName("work_types") val workTypes: List<WorkTypeDto>? = null,
    @SerializedName("has_land") val hasLand: Boolean = false,
    @SerializedName("has_certificate") val hasCertificate: Boolean = false
)

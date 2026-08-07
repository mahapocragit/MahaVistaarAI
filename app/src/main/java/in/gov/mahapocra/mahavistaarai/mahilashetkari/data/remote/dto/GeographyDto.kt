package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DivisionDto(
    val id: Int,
    @SerializedName("division_name") val divisionName: String,
    @SerializedName("division_name_mr") val divisionNameMr: String,
    @SerializedName("division_code") val divisionCode: String
)

data class DistrictDto(
    val id: Int,
    @SerializedName("district_name") val districtName: String,
    // API typo in the field name: "distrcit_name_mr" (not "district_name_mr")
    @SerializedName("distrcit_name_mr") val districtNameMr: String,
    val code: String,
    @SerializedName("division_id") val divisionId: Int
)

data class TalukaDto(
    val id: Int,
    @SerializedName("taluka_name") val talukaName: String,
    // API field is named "_ar" but actually holds the Marathi label
    @SerializedName("taluka_name_ar") val talukaNameMr: String,
    @SerializedName("taluka_code") val talukaCode: String,
    @SerializedName("subdivision_id") val subdivisionId: Int
)

data class VillageDto(
    val id: Int,
    @SerializedName("village_name") val villageName: String,
    // API typo in the field name: "villge_name_mr" (not "village_name_mr")
    @SerializedName("villge_name_mr") val villageNameMr: String,
    @SerializedName("village_code") val villageCode: String,
    @SerializedName("taluka_id") val talukaId: Int
)

data class WorkTypeDto(
    val id: Int,
    val name: String,
    @SerializedName("name_mr") val nameMr: String,
    val description: String? = null
)

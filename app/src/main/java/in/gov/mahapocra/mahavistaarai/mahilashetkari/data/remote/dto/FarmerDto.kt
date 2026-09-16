package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FarmerVerifyRequest(@SerializedName("farmer_id") val farmerId: String)

data class FarmerVerifyData(@SerializedName("farmer_name") val farmerName: String)

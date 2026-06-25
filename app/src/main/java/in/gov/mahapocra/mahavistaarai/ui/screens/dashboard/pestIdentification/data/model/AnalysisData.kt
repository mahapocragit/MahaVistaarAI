package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnalysisData(
    val farmerId: Int,
    val cropId: String,
    val sowingDate: String,
    val isSuccess: Boolean,
    val responseString: String,
    val imageUri: String,
    val diseaseId: String
) : Parcelable

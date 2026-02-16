package `in`.gov.mahapocra.mahavistaarai.pestIdentification.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnalysisData(
    val farmerId: Int,
    val cropId: String,
    val sowingDate: String,
    val isSuccess: Boolean,
    val responseString: String,
    val imageUri: Uri,
    val diseaseId: String
) : Parcelable

package `in`.gov.mahapocra.farmerapppks.models.response

data class Option(
    val CropAgeDays: String,
    val FertilizerName: String,
    val Price: Int,
    val Quantity: String,
    val TargetDate: String,
    val Type: String
)
package `in`.gov.mahapocra.mahavistaarai.data.model

data class SubCategory(
    val id: Int,
    val name: String,
    val category: Int,
    var isSelected: Boolean = false
)

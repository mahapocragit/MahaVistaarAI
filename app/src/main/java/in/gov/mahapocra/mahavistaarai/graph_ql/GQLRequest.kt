package `in`.gov.mahapocra.mahavistaarai.graph_ql

data class GQLRequest(
    val query: String,
    val variables: Variables
)

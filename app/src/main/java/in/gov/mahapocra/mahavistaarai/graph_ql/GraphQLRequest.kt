package `in`.gov.mahapocra.mahavistaarai.graph_ql

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, String>
)
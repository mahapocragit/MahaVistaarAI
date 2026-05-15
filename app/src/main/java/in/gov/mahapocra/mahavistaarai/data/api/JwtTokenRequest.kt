package `in`.gov.mahapocra.mahavistaarai.data.api

data class JwtTokenRequest(
    val mobile: String,
    val name: String,
    val role: String
)

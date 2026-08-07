package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.dto

/** Every endpoint on the UAT server replies with this same envelope, whether
 *  the HTTP status is 200/201 (success) or 400/404/500 (error) — see the
 *  "Standard Response Format" section of API_README.md. */
data class ApiResponseDto<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: Map<String, List<String>>? = null
)

/** Retrofit only populates response.body() for 2xx HTTP codes. Several
 *  documented errors (400/404) arrive in response.errorBody() instead, using
 *  the same envelope shape minus a typed `data`. Parsed with this instead of
 *  the generic ApiResponseDto<T> to sidestep Gson's generic type erasure. */
data class ErrorEnvelope(
    val success: Boolean = false,
    val message: String = "",
    val errors: Map<String, List<String>>? = null
)

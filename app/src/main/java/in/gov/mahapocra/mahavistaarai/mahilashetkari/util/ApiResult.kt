package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util

/**
 * Wraps the outcome of a repository call. Every API on the UAT server returns
 * the same {success, message, data, errors} envelope regardless of HTTP status
 * (see API_README.md), so this is the single shape the UI layer reacts to.
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T, val message: String = "") : ApiResult<T>()
    data class Error(val message: String, val fieldErrors: Map<String, List<String>>? = null) : ApiResult<Nothing>()
}

/** Generic loading/success/error wrapper for one-shot screen state. */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val fieldErrors: Map<String, List<String>>? = null) : UiState<Nothing>()
}

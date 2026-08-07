package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util

/** Client-side validation mirrors the rules documented in API_README.md so
 *  users get instant feedback instead of a round trip for obvious mistakes.
 *  The server remains the source of truth — its error messages are always
 *  shown as-is when a call fails. */
object Validators {

    fun aadhaarError(value: String): String? = when {
        value.isBlank() -> "Aadhaar number is required."
        !value.all { it.isDigit() } || value.length != 12 -> "Aadhaar number must be exactly 12 digits."
        else -> null
    }

    fun otpError(value: String): String? = when {
        value.isBlank() -> "OTP is required."
        !value.all { it.isDigit() } || value.length != 6 -> "Enter the 6-digit OTP."
        else -> null
    }

    fun mobileError(value: String): String? = when {
        value.isBlank() -> "Mobile number is required."
        !value.all { it.isDigit() } || value.length != 10 || value[0] !in '6'..'9' ->
            "Enter a valid 10-digit mobile number starting with 6-9."
        else -> null
    }

    fun nameError(value: String): String? = when {
        value.trim().length < 2 -> "Please enter a valid full name (at least 2 characters)."
        else -> null
    }

    fun declarationError(accepted: Boolean): String? =
        if (!accepted) "Declaration must be accepted." else null

    fun workTypesError(selected: List<Int>): String? =
        if (selected.isEmpty()) "Please select at least one type of agricultural work." else null
}

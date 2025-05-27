package `in`.gov.mahapocra.mahavistaarai.util

object OtpRateLimiter {

    private const val MAX_OTP_LIMIT = 5
    private const val BLOCK_TIME_MILLIS = 15 * 60 * 1000L // 15 minutes

    // Tracks OTP requests and block time
    private val otpRequestMap = mutableMapOf<String, OtpInfo>()

    data class OtpInfo(
        var requestCount: Int,
        var lastRequestTime: Long,
        var blockedUntil: Long = 0L
    )

    fun canSendOtp(mobileNumber: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val info = otpRequestMap[mobileNumber]

        if (info != null) {
            if (currentTime < info.blockedUntil) {
                // Still blocked
                return false
            }

            // Reset count if last request was long ago (optional)
            if (currentTime - info.lastRequestTime > BLOCK_TIME_MILLIS) {
                otpRequestMap[mobileNumber] = OtpInfo(1, currentTime)
                return true
            }

            // Check if limit reached
            if (info.requestCount >= MAX_OTP_LIMIT) {
                info.blockedUntil = currentTime + BLOCK_TIME_MILLIS
                return false
            }

            // Allow and increment
            info.requestCount++
            info.lastRequestTime = currentTime
            return true
        } else {
            // First-time request
            otpRequestMap[mobileNumber] = OtpInfo(1, currentTime)
            return true
        }
    }

    fun getBlockedTimeLeft(mobileNumber: String): Long {
        val currentTime = System.currentTimeMillis()
        val blockedUntil = otpRequestMap[mobileNumber]?.blockedUntil ?: 0
        return (blockedUntil - currentTime).coerceAtLeast(0L)
    }
}
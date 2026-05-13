package `in`.gov.mahapocra.mahavistaarai.ui.maps_sample

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

data class FarmerLoginSession(
    val token: String,
    val refreshToken: String,
    val userId: String,
)

class MahanidanFarmerAuthClient {
    suspend fun loginFarmer(
        farmerId: String,
        loginPin: String,
        environment: String,
    ): FarmerLoginSession = withContext(Dispatchers.IO) {
        val connection = (URL("${baseUrlUMFor(environment)}/validateLoginPin").openConnection() as HttpURLConnection)
            .apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 20_000
                readTimeout = 20_000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("X-Client-Type", "Mobile")
            }

        val body = JSONObject()
            .put("loginType", encrypt("USERNAME"))
            .put("value", encrypt(farmerId))
            .put("enteredPin", encrypt(loginPin))
            .toString()

        connection.outputStream.use { output ->
            output.write(body.toByteArray(StandardCharsets.UTF_8))
        }

        val responseCode = connection.responseCode
        val responseText = readResponse(connection, responseCode)
        val responseJson = responseText.takeIf { it.isNotBlank() }?.let(::JSONObject) ?: JSONObject()
        val statusCode = responseJson.optInt("statusCode", responseCode)
        if (responseCode !in 200..299 || statusCode != 200) {
            val message = responseJson.optString("message").ifBlank {
                "Farmer login failed ($responseCode)"
            }
            throw IllegalStateException(message)
        }

        val payload = responseJson.optJSONObject("response")
            ?: throw IllegalStateException("Farmer login did not return a session")
        val token = payload.optString("access_token")
        val refreshToken = payload.optString("refresh_token")
        val userId = extractUserId(token, payload.optString("userId"))

        if (token.isBlank() || refreshToken.isBlank() || userId.isBlank()) {
            throw IllegalStateException("Farmer login returned incomplete session data")
        }

        FarmerLoginSession(
            token = token,
            refreshToken = refreshToken,
            userId = userId,
        )
    }

    private fun baseUrlUMFor(environment: String): String {
        return when (environment.trim().lowercase()) {
            "stg", "staging" -> "https://apaims2.0.vassarlabs.com/gateway/um"
            else -> "http://mahanidan.vassarlabs.com/newUm"
        }
    }

    private fun readResponse(connection: HttpURLConnection, responseCode: Int): String {
        val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
        if (stream == null) {
            return ""
        }
        BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
            return reader.readText()
        }
    }

    private fun extractUserId(token: String, fallback: String): String {
        val payload = decodeTokenPayload(token) ?: return fallback
        val userIdFromToken = payload.optString("userUUID").ifBlank {
            payload.optString("sub")
        }
        return userIdFromToken.ifBlank { fallback }
    }

    private fun decodeTokenPayload(token: String): JSONObject? {
        val parts = token.split(".")
        if (parts.size < 2) {
            return null
        }

        return try {
            val decoded = Base64.decode(
                parts[1],
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
            )
            JSONObject(String(decoded, StandardCharsets.UTF_8))
        } catch (_: Exception) {
            null
        }
    }

    private fun encrypt(value: String): String {
        val publicKey = PUBLIC_KEY
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        val keyBytes = Base64.decode(publicKey, Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(keySpec))
        return Base64.encodeToString(
            cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8)),
            Base64.NO_WRAP,
        )
    }

    private companion object {
        const val PUBLIC_KEY = """
-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8pOu0QUopx3dFt3hSXR5SWfVELfeHtMCcJ1xENUWTkrLBAvW58OXQvTwTmZ/gNQwsQcvkRcFhm6FOGL5BUeaumliznrZhiGXUzReOE5ww6sVsGXgrEEnw08+M9KRWqv6L4dUUAiDkiYjqTbSIBuLdJvqBBesSzWoo6KX8g7xZhQIDAQAB
-----END PUBLIC KEY-----
"""
    }
}

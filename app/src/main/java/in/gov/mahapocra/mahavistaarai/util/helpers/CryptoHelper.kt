package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    fun encryptField(value: String?): String? {
        if (value.isNullOrEmpty()) return null

        return try {
            // Base64 decode key (must be 32 bytes for AES-256)
            val base64Key = "WROMmOQutvHhEIzU2dVZUvNytH3I6R11CoXb+nQ8N6g="
            val keyBytes = Base64.decode(base64Key, Base64.DEFAULT)
            val keySpec = SecretKeySpec(keyBytes, "AES")

            // Generate 12-byte IV
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv)

            // Cipher setup
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv) // 128-bit auth tag
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec)

            // Encrypt
            val ciphertextWithTag = cipher.doFinal(value.toByteArray(Charsets.UTF_8))

            // In Java/Kotlin, GCM appends tag to ciphertext → split manually
            val tagLength = 16
            val ciphertext = ciphertextWithTag.copyOfRange(0, ciphertextWithTag.size - tagLength)
            val tag = ciphertextWithTag.copyOfRange(ciphertextWithTag.size - tagLength, ciphertextWithTag.size)

            // Combine iv + tag + ciphertext (same as PHP)
            val combined = iv + tag + ciphertext

            Base64.encodeToString(combined, Base64.NO_WRAP)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun decryptField(encrypted: String?): String? {
        if (encrypted.isNullOrEmpty()) return null

        return try {
            // Decode key (must be same as encryption key)
            val base64Key = "WROMmOQutvHhEIzU2dVZUvNytH3I6R11CoXb+nQ8N6g="
            val keyBytes = Base64.decode(base64Key, Base64.DEFAULT)
            val keySpec = SecretKeySpec(keyBytes, "AES")

            // Decode input
            val data = Base64.decode(encrypted, Base64.DEFAULT)

            // Minimum length check (12 IV + 16 TAG)
            if (data.size < 28) return null

            // Extract parts
            val iv = data.copyOfRange(0, 12)
            val tag = data.copyOfRange(12, 28)
            val ciphertext = data.copyOfRange(28, data.size)

            // In Kotlin/Java, tag must be appended to ciphertext
            val ciphertextWithTag = ciphertext + tag

            // Setup cipher
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec)

            // Decrypt
            val decryptedBytes = cipher.doFinal(ciphertextWithTag)

            String(decryptedBytes, Charsets.UTF_8)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
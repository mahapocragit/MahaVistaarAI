package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    private const val START: String = "w7t7k"
    private const val MIDDLE: String = "xamzee"
    private const val END: String = "fh58v"
    private const val SECRET = START + MIDDLE + END

    private fun getKey(): SecretKeySpec {
        val decoded = Base64.decode(SECRET, Base64.DEFAULT)

        // Ensure 32 bytes key (AES-256)
        val sha = MessageDigest.getInstance("SHA-256")
        val key = sha.digest(decoded)

        return SecretKeySpec(key, "AES")
    }

    private fun getIV(): IvParameterSpec {
        val ivBytes = SECRET.toByteArray(Charsets.UTF_8)
        return IvParameterSpec(ivBytes.copyOf(16)) // must be 16 bytes
    }

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey(), getIV())

        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    fun decrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, getKey(), getIV())

        val decoded = Base64.decode(value, Base64.DEFAULT)
        val decrypted = cipher.doFinal(decoded)

        return String(decrypted, Charsets.UTF_8)
    }
}
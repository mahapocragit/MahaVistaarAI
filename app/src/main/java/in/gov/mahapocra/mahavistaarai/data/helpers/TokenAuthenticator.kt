package `in`.gov.mahapocra.mahavistaarai.data.helpers

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.splash.SplashScreenActivity
import `in`.gov.mahapocra.mahavistaarai.util.TokenSessionManager
import okhttp3.Authenticator
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.internal.http2.Http2Reader
import org.json.JSONObject


class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        // Avoid infinite loop
        if (responseCount(response) >= 2) {
            return null
        }

        val newTokens = refreshToken()

        return if (newTokens != null) {

            // ✅ Save new tokens
            TokenSessionManager.saveTokens(newTokens.first, newTokens.second)

            // ✅ Retry original request with new token
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.first}")
                .build()

        } else {
            // ❌ Refresh token also expired → logout
            TokenSessionManager.clear()

//            // Navigate to Login (must be on main thread)
            Handler(Looper.getMainLooper()).post {
                val intent = Intent(context, LoginScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }

            null
        }
    }

    fun refreshToken(): Pair<String, String>? {
        return try {
            val client = OkHttpClient()

            val requestBody = FormBody.Builder()
                .add("refresh_token", TokenSessionManager.getRefreshToken() ?: "")
                .build()

            val request = Request.Builder()
                .url("https://stage-farmers-app-api.mahapocra.gov.in/jwtServices/refresh_token")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            Log.d("REFRESH_API", "code: ${response.code}")
            Log.d("REFRESH_API", "body: $response")

            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                val newAccess = json.getString("access_token")
                val newRefresh = json.getString("refresh_token")

                Pair(newAccess, newRefresh)
            } else {
                null
            }

        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var res = response.priorResponse
        while (res != null) {
            count++
            res = res.priorResponse
        }
        return count
    }
}

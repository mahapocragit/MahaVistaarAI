package `in`.gov.mahapocra.mahavistaarai.data.helpers

import `in`.gov.mahapocra.mahavistaarai.util.TokenSessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = TokenSessionManager.getAccessToken()

        val request = chain.request().newBuilder()
            .apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(request)
    }
}

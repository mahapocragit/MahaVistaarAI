package `in`.gov.mahapocra.mahavistaarai.data.helpers

import com.google.gson.GsonBuilder
import `in`.gov.mahapocra.mahavistaarai.application.MyApplication
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object RetrofitHelper {

    fun createRetrofitInstance(baseURL: String?): Retrofit {
        val gson = GsonBuilder().create()
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getUnsafeOkHttpClient(): OkHttpClient {
        val dns = object : Dns {
            override fun lookup(hostname: String): List<InetAddress> {
                return InetAddress.getAllByName(hostname)
                    .filter { it is Inet4Address }
            }
        }

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .dns(dns) // ✅ no cast
            // ✅ ADD THIS
            .addInterceptor(AuthInterceptor())
            // ✅ ADD THIS (MAIN)
            .authenticator(TokenAuthenticator(MyApplication.instance))
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json;versions=1")
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .addHeader("Content-Encoding", "gzip")
                    .build()
                chain.proceed(request)
            }
            .build()
    }
}


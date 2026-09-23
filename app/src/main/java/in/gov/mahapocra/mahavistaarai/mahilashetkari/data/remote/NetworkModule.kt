package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote

import `in`.gov.mahapocra.mahavistaarai.application.MyApplication
import `in`.gov.mahapocra.mahavistaarai.data.helpers.AuthInterceptor
import `in`.gov.mahapocra.mahavistaarai.data.helpers.TokenAuthenticator
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api.CertificateApi
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api.MahilaShetkariApi
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

/** UAT server root. Endpoint paths (declared per-call in MahilaShetkariApi)
 *  already include the "mahila-shetkari-service/api/" prefix from
 *  API_README.md, so the base URL here is just the bare host. */
object NetworkModule {

    private const val BASE_URL = "https://uat-mahakrishi.mahaitgov.in/"
    const val CERTIFICATE_BASE_URL = "https://uat-mahakrishi.mahaitgov.in/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val mahilaShetkariApi: MahilaShetkariApi by lazy {
        retrofit.create(MahilaShetkariApi::class.java)
    }

    private val certificateRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CERTIFICATE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val certificateApi: CertificateApi by lazy {
        certificateRetrofit.create(CertificateApi::class.java)
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

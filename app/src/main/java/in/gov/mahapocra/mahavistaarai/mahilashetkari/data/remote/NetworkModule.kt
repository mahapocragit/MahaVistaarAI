package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote

import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api.CertificateApi
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.remote.api.MahilaShetkariApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** UAT server root. Endpoint paths (declared per-call in MahilaShetkariApi)
 *  already include the "mahila-shetkari-service/api/" prefix from
 *  API_README.md, so the base URL here is just the bare host. */
object NetworkModule {

    //private const val BASE_URL = "http://uat-mahakrishi.mahaitgov.in/"
    private const val BASE_URL = "http://40.81.91.247/"

    /** Woman Farmer Certificate service root — see CERTIFICATE_API.md.
     *  Must stay on the real domain, not the bare IP: it's a separate vhost on
     *  the same Apache server, and requests without the matching Host header
     *  (i.e. hit via IP) 404 even though mahila-shetkari-service resolves fine
     *  over the IP as the server's default vhost. */
    //const val CERTIFICATE_BASE_URL = "https://uat-mahakrishi.mahaitgov.in/"
    const val CERTIFICATE_BASE_URL = "http://40.81.91.247/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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
}

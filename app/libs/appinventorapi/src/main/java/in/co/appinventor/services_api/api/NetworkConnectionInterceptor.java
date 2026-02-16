package in.co.appinventor.services_api.api;

import android.content.Context;

import java.io.IOException;

import in.co.appinventor.services_api.util.Utility;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class NetworkConnectionInterceptor implements Interceptor {

    private Context mContext;
    public NetworkConnectionInterceptor(Context applicationContext) {
        if (applicationContext == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        mContext = applicationContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (mContext == null || !Utility.checkConnection(mContext)) {
            throw new IOException("No internet connection");
        }
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}




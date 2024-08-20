package in.co.appinventor.services_api.api;

import android.content.Context;

import java.io.IOException;

import in.co.appinventor.services_api.app_util.AppConstants;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class NetworkConnectionInterceptor implements Interceptor {

    private Context mContext;


    // Exception occurs in null pointer here
    public NetworkConnectionInterceptor(Context applicationContext) {
        mContext = applicationContext;
    }



    @Override
    public Response intercept(Chain chain) throws IOException {
//        if (!NetworkUtils.isConnected()) {
//            throw new NoInternetConnectException();
//        }
        isInternetConnected();

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    private void isInternetConnected() {
        if (!Utility.checkConnection(this.mContext)) {
            UIToastMessage.show(this.mContext, AppConstants.MESSAGE_NETWORK_UNAVAILABLE);
        }
    }
}




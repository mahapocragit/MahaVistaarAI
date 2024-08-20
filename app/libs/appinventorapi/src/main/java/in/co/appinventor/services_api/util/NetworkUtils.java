package in.co.appinventor.services_api.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/* renamed from: in.co.appinventor.services_api.util.NetworkUtils */
public class NetworkUtils {
    private static final int TYPE_MOBILE = 2;
    private static final int TYPE_NOT_CONNECTED = 0;
    private static final int TYPE_WIFI = 1;

    public static boolean isWifiConnected(Context mContext) {
        @SuppressLint("WrongConstant") ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService("connectivity");
        NetworkInfo mWifi = connManager.getNetworkInfo(1);
        return mWifi.isConnected();
    }

    public static boolean isNetworkAvailable(Context context) {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getConnectivityStatus(Context context) {
        @SuppressLint("WrongConstant") NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == 1) {
                return 1;
            }
            if (activeNetwork.getType() == 0) {
                return 2;
            }
        }
        return TYPE_NOT_CONNECTED;
    }
}

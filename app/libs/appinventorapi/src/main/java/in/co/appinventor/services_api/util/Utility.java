package in.co.appinventor.services_api.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.InputStream;
import java.io.OutputStream;

/* renamed from: in.co.appinventor.services_api.util.Utility */
public class Utility {
    public static Bitmap decodeFile(String path, int swidth, int sheight) {
        boolean withinBounds = true;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if (bounds.outWidth == -1) {
            return null;
        }
        int width = bounds.outWidth;
        int height = bounds.outHeight;
        int sampleSize = 1;
        if (width > swidth || height > sheight) {
            withinBounds = false;
        }
        if (!withinBounds) {
            sampleSize = Math.round(Math.max(((float) width) / ((float) swidth), ((float) height) / ((float) sheight)));
        }
        BitmapFactory.Options resample = new BitmapFactory.Options();
        resample.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(path, resample);
    }

    public static void copyStream(InputStream is, OutputStream os) {
        try {
            byte[] bytes = new byte[1024];
            while (true) {
                int count = is.read(bytes, 0, 1024);
                if (count != -1) {
                    os.write(bytes, 0, count);
                } else {
                    return;
                }
            }
        } catch (Exception e) {
        }
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            // For older devices (pre-Android 6.0)
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    }

}

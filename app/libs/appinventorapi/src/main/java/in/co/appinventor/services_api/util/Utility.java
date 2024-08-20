package in.co.appinventor.services_api.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import in.co.appinventor.services_api.app_util.AppConstants;

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

    public static String uniqueDeviceID(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            try {
                throw new Exception("Implement runtime permission before using this method");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String tmDevice = "" + tm.getDeviceId();
        if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            try {
                throw new Exception("Implement runtime permission before using this method");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return new UUID((long) ("" + Settings.Secure.getString(context.getContentResolver(), "android_id")).hashCode(), (((long) tmDevice.hashCode()) << 32) | ((long) ("" + tm.getSimSerialNumber()).hashCode())).toString();
    }

    public static boolean checkConnection(Context c) {
        @SuppressLint("WrongConstant") NetworkInfo networkInfo = ((ConnectivityManager) c.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == 1) {
            System.out.println("true wifi");
            return true;
        } else if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == 0) {
            System.out.println("true edge");
            return true;
        } else if (networkInfo == null || !networkInfo.isConnected()) {
            System.out.println(AppConstants.kSOUP_FLAG_FLASE);
            return false;
        } else {
            System.out.println("true net");
            return true;
        }
    }
}

package in.co.appinventor.services_api.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.WindowManager;

/* renamed from: in.co.appinventor.services_api.util.DensityUtils */
public class DensityUtils {
    public static int dp2px(Context context, int dp) {
        return (int) (((double) (((float) dp) * context.getResources().getDisplayMetrics().density)) + 0.5d);
    }

    public static int px2dp(Context context, int px) {
        return (int) (((double) (((float) px) / context.getResources().getDisplayMetrics().density)) + 0.5d);
    }

    @SuppressLint("WrongConstant")
    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
    }

    @SuppressLint("WrongConstant")
    public static int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight();
    }
}

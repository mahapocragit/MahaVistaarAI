package in.co.appinventor.services_api.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import in.co.appinventor.services_api.app_util.AppConstants;

/* renamed from: in.co.appinventor.services_api.util.Screen */
public class Screen {
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", AppConstants.kDEVICE_TYPE);
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getScreenHeight(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getScreenWidth(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}

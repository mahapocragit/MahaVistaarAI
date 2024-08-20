package in.co.appinventor.services_api.debug;

import android.util.Log;

/* renamed from: in.co.appinventor.services_api.debug.DebugLog */
public class DebugLog {
    private static final String LOGTAG = "appinventor.co.in";
    public static boolean isLoggingEnabled = true;
    private static final DebugLog ourInstance = new DebugLog();

    private DebugLog() {
    }

    public static DebugLog getInstance() {
        return ourInstance;
    }

    private boolean isIsLoggingEnabled() {
        return isLoggingEnabled;
    }

    public void initLoggingEnabled(boolean flag) {
        isLoggingEnabled = flag;
    }

    /* renamed from: e */
    public void e(String nMessage) {
        if (isLoggingEnabled) {
            Log.e(LOGTAG, nMessage);
        }
    }

    /* renamed from: w */
    public void w(String nMessage) {
        if (isLoggingEnabled) {
            Log.w(LOGTAG, nMessage);
        }
    }

    /* renamed from: d */
    public void d(String nMessage) {
        if (isLoggingEnabled) {
            Log.d(LOGTAG, nMessage);
        }
    }

    /* renamed from: i */
    public void i(String nMessage) {
        if (isLoggingEnabled) {
            Log.i(LOGTAG, nMessage);
        }
    }

    /* renamed from: v */
    public void v(String nMessage) {
        if (isLoggingEnabled) {
            Log.v(LOGTAG, nMessage);
        }
    }

    public void console(String message) {
        if (isLoggingEnabled) {
            System.out.println(message);
        }
    }
}

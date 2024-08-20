package in.co.appinventor.services_api.app_util;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/* renamed from: in.co.appinventor.services_api.app_util.ApiError */
public class ApiError extends VolleyError {
    public final NetworkResponse networkResponse;
    private long networkTimeMs;

    public ApiError() {
        this.networkResponse = null;
    }

    public ApiError(NetworkResponse response) {
        this.networkResponse = response;
    }

    public ApiError(String exceptionMessage) {
        super(exceptionMessage);
        this.networkResponse = null;
    }

    public ApiError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        this.networkResponse = null;
    }

    public ApiError(Throwable cause) {
        super(cause);
        this.networkResponse = null;
    }

    /* access modifiers changed from: package-private */
    public void setNetworkTimeMs(long networkTimeMs2) {
        this.networkTimeMs = networkTimeMs2;
    }

    public long getNetworkTimeMs() {
        return this.networkTimeMs;
    }
}

package in.co.appinventor.services_api.listener;

import in.co.appinventor.services_api.app_util.ApiError;

/* renamed from: in.co.appinventor.services_api.listener.ApiVolleyCallback */
public class ApiVolleyCallback {

    /* renamed from: in.co.appinventor.services_api.listener.ApiVolleyCallback$ErrorListener */
    public interface ErrorListener {
        void onErrorResponse(ApiError apiError);
    }

    /* renamed from: in.co.appinventor.services_api.listener.ApiVolleyCallback$Listener */
    public interface Listener<T> {
        void onResponse(T t);
    }
}

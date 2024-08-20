package in.co.appinventor.services_api.listener;

import org.json.JSONObject;

/* renamed from: in.co.appinventor.services_api.listener.ApiCallbackCode */
public interface ApiCallbackCode {
    void onFailure(Object obj, Throwable th, int i);

    void onResponse(JSONObject jSONObject, int i);
}

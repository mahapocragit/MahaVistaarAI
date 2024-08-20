package in.co.appinventor.services_api.listener;

import org.json.JSONObject;

/* renamed from: in.co.appinventor.services_api.listener.ApiCallbackResponse */
public interface ApiCallbackResponse {
    void onFailure(JSONObject jSONObject, Throwable th);

    void onResponse(JSONObject jSONObject);
}

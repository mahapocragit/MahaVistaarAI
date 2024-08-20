package in.co.appinventor.services_api.listener;

import org.json.JSONObject;

/* renamed from: in.co.appinventor.services_api.listener.ApiJSONObjCallback */
public interface ApiJSONObjCallback {
    void onFailure(Throwable th, int i);

    void onResponse(JSONObject jSONObject, int i);
}

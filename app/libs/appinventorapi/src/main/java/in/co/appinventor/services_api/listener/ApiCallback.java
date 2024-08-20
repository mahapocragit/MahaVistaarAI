package in.co.appinventor.services_api.listener;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Response;

/* renamed from: in.co.appinventor.services_api.listener.ApiCallback */
public interface ApiCallback {
    void onFailure(Object obj, Throwable th, int i);

    void onResponse(Call<JsonObject> call, Response<JsonObject> response, int i);
}

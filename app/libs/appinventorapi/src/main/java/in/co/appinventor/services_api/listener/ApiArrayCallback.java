package in.co.appinventor.services_api.listener;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.Response;

/* renamed from: in.co.appinventor.services_api.listener.ApiArrayCallback */
public interface ApiArrayCallback {
    void onFailure(Call<JsonArray> call, Throwable th, int i);

    void onResponse(Call<JsonArray> call, Response<JsonArray> response, int i);
}

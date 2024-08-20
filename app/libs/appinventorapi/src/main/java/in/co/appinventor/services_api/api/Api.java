package in.co.appinventor.services_api.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/* renamed from: in.co.appinventor.services_api.api.Api */
public interface Api {
    @GET
    Call<JsonObject> getCommonRequestDataApi(@Url String str);

    @GET
    Call<JsonArray> getCommonRequestJSONArrayDataApi(@Url String str);

    @GET
    Call<ResponseBody> getCommonRequestStringDataApi(@Url String str);
}

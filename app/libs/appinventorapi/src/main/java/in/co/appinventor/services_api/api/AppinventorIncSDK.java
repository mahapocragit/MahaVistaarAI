package in.co.appinventor.services_api.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.co.appinventor.services_api.app_util.AppConstants;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.helper.SDKHelper;
import in.co.appinventor.services_api.listener.ApiArrayCallback;
import in.co.appinventor.services_api.listener.ApiCallback;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiCallbackResponse;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.listener.ApiStringCallback;
import in.co.appinventor.services_api.util.FileUtils;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* renamed from: in.co.appinventor.services_api.api.AppinventorIncSDK */
public class AppinventorIncSDK {
    private static final AppinventorIncSDK ourInstance = new AppinventorIncSDK();
    private int appColorTheme = -16776961;
    /* access modifiers changed from: private */
    public String authToken;
    private String baserURL;
    private Context mContext;
    private String mMessage;
    /* access modifiers changed from: private */
    public ProgressDialog mProgressDialog = null;
    private boolean mShowProgressDialog;

    public static AppinventorIncSDK getInstance() {
        return ourInstance;
    }

    private AppinventorIncSDK() {
    }

    public void initAPI(int appColorTheme2) {
        this.appColorTheme = appColorTheme2;
    }

    private void isInitAPI() {
        if (this.appColorTheme == -16776961) {
            try {
                throw new Exception("Please use initAPI in application class, Before using the method.  Add your app theme color code.example primary color");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public AppinventorIncSDK(Context mContext2, String baseURL, String authToken2, String mMessage2, boolean showProgressDialog) {
        this.mContext = mContext2;
        this.baserURL = baseURL;
        this.authToken = authToken2;
        this.mMessage = mMessage2;
        this.mShowProgressDialog = showProgressDialog;
        if (Utility.checkConnection(mContext2) && this.mShowProgressDialog) {
            this.mProgressDialog = new ProgressDialog(mContext2);
            if (this.mMessage == null || this.mMessage.isEmpty()) {
                this.mProgressDialog.setMessage("");
            } else {
                this.mProgressDialog.setMessage(this.mMessage);
            }
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.show();
        }
    }

    public Retrofit getRetrofitInstance() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(200000, TimeUnit.SECONDS);
        builder.connectTimeout(200000, TimeUnit.SECONDS);
        builder.addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Request.Builder ongoing = chain.request().newBuilder();
                ongoing.addHeader("Accept", "application/json;versions=1");
                ongoing.addHeader("Content-Type", "application/json; charset=UTF-8");
                ongoing.addHeader("Content-Encoding", "gzip");
                ongoing.addHeader("Authorization", AppinventorIncSDK.this.authToken);
                return chain.proceed(ongoing.build());
            }
        });
        OkHttpClient client = builder.build();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        return new Retrofit.Builder().baseUrl(this.baserURL).client(client).addConverterFactory(GsonConverterFactory.create(gsonBuilder.create())).build();
    }

    private void isInternetConnected() {
        if (!Utility.checkConnection(this.mContext)) {
            UIToastMessage.show(this.mContext, AppConstants.MESSAGE_NETWORK_UNAVAILABLE);
        }
    }

    public void getRequestWithRetrofitData(String url, final ApiCallback apiCallback) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestDataApi(url).enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    DebugLog.getInstance().d("onResponse=====" + ((JsonObject) response.body()));
                    apiCallback.onResponse(call, response, 0);
                    return;
                }
                DebugLog.getInstance().d("onResponse=====" + response.toString());
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                apiCallback.onFailure(call, t, 0);
                DebugLog.getInstance().d("onFailure=====" + t.toString());
            }
        });
    }

    public void getRequestData(String url, final ApiJSONObjCallback apiCallback, final int requestCode) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestDataApi(url).enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    JsonObject loginResponse = (JsonObject) response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(loginResponse.toString());
                        DebugLog.getInstance().d("onResponse=====" + loginResponse);
                        apiCallback.onResponse(jsonObject, requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    DebugLog.getInstance().d("onResponse=====" + response.toString());
                }
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                apiCallback.onFailure(t, requestCode);
                DebugLog.getInstance().d("onFailure=====" + t.toString());
            }
        });
    }

    public void getRequestStringData(String url, final ApiStringCallback apiCallback, final int requestCode) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestStringDataApi(url).enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    try {
                        String res = ((ResponseBody) response.body()).string();
                        DebugLog.getInstance().d("onResponse=====" + res);
                        apiCallback.onResponse(res, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    DebugLog.getInstance().d("onResponse=====" + response.toString());
                }
            }

            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                apiCallback.onFailure(t, requestCode);
                DebugLog.getInstance().d("onFailure=====" + t.toString());
            }
        });
    }

    public void getJSONArrayRequestData(String url, final ApiArrayCallback apiCallback) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestJSONArrayDataApi(url).enqueue(new Callback<JsonArray>() {
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull retrofit2.Response<JsonArray> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    DebugLog.getInstance().d("onResponse=====" + ((JsonArray) response.body()));
                    apiCallback.onResponse(call, response, 0);
                    return;
                }
                DebugLog.getInstance().d("onResponse=====" + response.toString());
            }

            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                apiCallback.onFailure(call, t, 0);
                DebugLog.getInstance().d("onFailure=====" + t.toString());
            }
        });
    }

    public void getRequestDataWithCallback(String url, final ApiCallbackResponse apiCallback) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestDataApi(url).enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    JsonObject serverResponse = (JsonObject) response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(serverResponse));
                        DebugLog.getInstance().d("onResponse=====" + serverResponse);
                        apiCallback.onResponse(jsonObject);
                        JSONObject jSONObject = jsonObject;
                    } catch (JSONException e2) {
                        e2 = e2;
                        e2.printStackTrace();
                    }
                } else {
                    apiCallback.onResponse((JSONObject) null);
                    DebugLog.getInstance().d("onResponse=====" + response.toString());
                }
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                try {
                    apiCallback.onFailure(new JSONObject(String.valueOf(call)), t);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postRequestCallback(Call<JsonObject> responseCall, final ApiCallbackResponse callbackResponse) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        responseCall.enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    JsonObject serverResponse = (JsonObject) response.body();
                    DebugLog.getInstance().d("onResponse=====" + serverResponse);
                    try {
                        callbackResponse.onResponse(new JSONObject(String.valueOf(serverResponse)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    callbackResponse.onResponse((JSONObject) null);
                }
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                try {
                    callbackResponse.onFailure(new JSONObject(String.valueOf(call)), t);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postRequest(Call<JsonObject> responseCall, final ApiCallbackCode apiCallback, final int requestCode) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        responseCall.enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    JsonObject serverResponse = (JsonObject) response.body();
                    DebugLog.getInstance().d("onResponse=====" + serverResponse);
                    try {
                        apiCallback.onResponse(new JSONObject(String.valueOf(serverResponse)), requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    apiCallback.onResponse((JSONObject) null, requestCode);
                }
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                try {
                    apiCallback.onFailure(call, t, requestCode);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postRequestMultipleImgUpload(Call<JsonObject> responseCall, final ApiCallbackCode apiCallback, final int requestCode) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        responseCall.enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    JsonObject serverResponse = (JsonObject) response.body();
                    DebugLog.getInstance().d("onResponse=====" + serverResponse);
                    try {
                        apiCallback.onResponse(new JSONObject(String.valueOf(serverResponse)), requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    apiCallback.onResponse((JSONObject) null, requestCode);
                }
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                try {
                    apiCallback.onFailure(call, t, requestCode);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        return MultipartBody.Part.createFormData(partName, file.getName(), RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file));
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
    }

    public void postRequestSingleImgUpload(Call<JsonObject> responseCall, final ApiCallbackCode apiCallback, final int requestCode) {
        isInitAPI();
        isInternetConnected();
        isRegisterWithSDK();
        responseCall.enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    JsonObject serverResponse = (JsonObject) response.body();
                    DebugLog.getInstance().d("onResponse=====" + serverResponse);
                    try {
                        apiCallback.onResponse(new JSONObject(String.valueOf(serverResponse)), requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    apiCallback.onResponse((JSONObject) null, requestCode);
                }
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (AppinventorIncSDK.this.mProgressDialog != null && AppinventorIncSDK.this.mProgressDialog.isShowing()) {
                    AppinventorIncSDK.this.mProgressDialog.dismiss();
                }
                try {
                    apiCallback.onFailure(call, t, requestCode);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void isRegisterWithSDK() {
        if (SDKHelper.getInstance().isAppRegister(this.mContext.getPackageName())) {
            DebugLog.getInstance().d(APIConstants.app_reg);
            return;
        }
        DebugLog.getInstance().d(APIConstants.app_not_reg);
        System.exit(1);
    }
}

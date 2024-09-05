package in.co.appinventor.services_api.api;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.co.appinventor.services_api.app_util.AppConstants;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.helper.SDKHelper;
import in.co.appinventor.services_api.listener.ApiArrayCallback;
import in.co.appinventor.services_api.listener.ApiCallback;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiCallbackResponse;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


    /* renamed from: in.co.appinventor.services_api.api.AppinventorApi */
    public class AppInventorApi {
        private static final AppInventorApi ourInstance = new AppInventorApi();
        /* access modifiers changed from: private */
        public String authToken;
        private String baserURL;
        private Context mContext;
        private String mMessage;
        /* access modifiers changed from: private */
        public ProgressDialog mProgressDialog = null;
        private boolean mShowProgressDialog;

        public static AppInventorApi getInstance() {
            return ourInstance;
        }

        private AppInventorApi() {
        }

        public AppInventorApi(Context mContext2, String baseURL, String authTokenTest, String mMessage2, boolean showProgressDialog) {
            this.mContext = mContext2;
            this.baserURL = baseURL;
            this.authToken = authTokenTest;
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
            Gson gson = new GsonBuilder().setLenient().create();
            builder.addInterceptor(chain -> {
                Request.Builder ongoing = chain.request().newBuilder();
                ongoing.addHeader("Accept", "application/json;versions=1");
                ongoing.addHeader("Content-Type", "application/json; charset=UTF-8");
                ongoing.addHeader("Content-Encoding", "gzip");
                ongoing.addHeader("authoritytoken", AppInventorApi.this.authToken);
                return chain.proceed(ongoing.build());
            });

            return new Retrofit.Builder()
                    .baseUrl(this.baserURL)
                    .client(getUnsafeOkHttpClient(mContext).build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        public Retrofit getRetrofitInstanceContentType() {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.addInterceptor(new Interceptor() {
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "application/json");
                    ongoing.addHeader("Content-Type", "application/json");
                    ongoing.addHeader("Content-Encoding", "gzip");
                    ongoing.addHeader("vary", "origin,accept-encoding");
                    return chain.proceed(ongoing.build());
                }
            });
            return new Retrofit.Builder().baseUrl(this.baserURL).client(builder.build()).addConverterFactory(GsonConverterFactory.create()).build();
        }

        public OkHttpClient.Builder getUnsafeOkHttpClient(Context context) {

            try {
            /*// loading CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.wotrorgin2024);
            Certificate ca;
            try {
                ca = cf.generateCertificate(cert);
            } finally { cert.close(); }

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);*/
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String
                                    authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String
                                    authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(1, TimeUnit.MINUTES);
                builder.readTimeout(60, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
           //     builder.callTimeout(30, TimeUnit.SECONDS);
//                .addNetworkInterceptor(loggingInterceptor)
                builder.followRedirects(false);
//                .addNetworkInterceptor(interceptor)
                builder.addInterceptor(new NetworkConnectionInterceptor(context));
//                builder.addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request request = chain.request();
//                        okhttp3.Response response = chain.proceed(request);
//                        return response;
//
//                    }
//                });

                builder.addInterceptor(chain -> {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "application/json;versions=1");
                    ongoing.addHeader("Content-Type", "application/json; charset=UTF-8");
                    ongoing.addHeader("Content-Encoding", "gzip");
                    ongoing.addHeader("authoritytoken", AppInventorApi.this.authToken);
                    return chain.proceed(ongoing.build());
                });

                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                return builder;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void isInternetConnected() {
            if (!Utility.checkConnection(this.mContext)) {
                UIToastMessage.show(this.mContext, AppConstants.MESSAGE_NETWORK_UNAVAILABLE);
            }
        }

        public void getRequestWithRetrofitData(String url, final ApiCallback apiCallback) {
            isInternetConnected();
            isRegisterWithSDK();
            ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestDataApi(url).enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        DebugLog.getInstance().d("onResponse=====" + ((JsonObject) response.body()));
                        apiCallback.onResponse(call, response, 0);
                        return;
                    }
                    DebugLog.getInstance().d("onResponse=====" + response.toString());
                }

                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
                    }
                    apiCallback.onFailure(call, t, 0);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                }
            });
        }

        public void getRequestData(String url, final ApiJSONObjCallback apiCallback, final int requestCode) {
            DebugLog.getInstance().d("responseCall=====" + url);
            DebugLog.getInstance().d("apiCallback=====" + apiCallback);
            DebugLog.getInstance().d("requestCode=====" + requestCode);
            isInternetConnected();
            isRegisterWithSDK();
            ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestDataApi(url).enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
                    }
                    apiCallback.onFailure(t, requestCode);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                }
            });
        }

        public void getJSONArrayRequestData(String url, final ApiArrayCallback apiCallback) {
            isInternetConnected();
            isRegisterWithSDK();
            ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestJSONArrayDataApi(url).enqueue(new Callback<JsonArray>() {
                public void onResponse(@NonNull Call<JsonArray> call, @NonNull retrofit2.Response<JsonArray> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        DebugLog.getInstance().d("onResponse=====" + ((JsonArray) response.body()));
                        apiCallback.onResponse(call, response, 0);
                        return;
                    }
                    DebugLog.getInstance().d("onResponse=====" + response.toString());
                }

                public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
                    }
                    apiCallback.onFailure(call, t, 0);
                    DebugLog.getInstance().d("onFailure=====" + t.toString());
                }
            });
        }

        public void getRequestDataWithCallback(String url, final ApiCallbackResponse apiCallback) {
            isInternetConnected();
            isRegisterWithSDK();
            ((Api) getRetrofitInstance().create(Api.class)).getCommonRequestDataApi(url).enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
            isInternetConnected();
            isRegisterWithSDK();
            responseCall.enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
            DebugLog.getInstance().d("responseCall=====" + responseCall);
            DebugLog.getInstance().d("apiCallback=====" + apiCallback);
            DebugLog.getInstance().d("requestCode=====" + requestCode);
            isInternetConnected();
            //isRegisterWithSDK();
            responseCall.enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    Log.d("APIINVENTORTAG", "onResponse: "+response.body());
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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

        public void postRequestWithJSONObjParam(Call<JsonObject> responseCall, final ApiCallbackCode apiCallback, final int requestCode) {
            isInternetConnected();
            isRegisterWithSDK();
            responseCall.enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
            isInternetConnected();
            isRegisterWithSDK();
            responseCall.enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
            isInternetConnected();
            isRegisterWithSDK();
            responseCall.enqueue(new Callback<JsonObject>() {
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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
                    if (AppInventorApi.this.mProgressDialog != null && AppInventorApi.this.mProgressDialog.isShowing()) {
                        AppInventorApi.this.mProgressDialog.dismiss();
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

        public String getPublicIPAddress(Context context) {
            isRegisterWithSDK();
            @SuppressLint("WrongConstant") final NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            RunnableFuture<String> futureRun = new FutureTask<>(new Callable<String>() {
                public String call() throws Exception {
                    if (info == null || !info.isAvailable() || !info.isConnected()) {
                        return null;
                    }
                    StringBuilder response = new StringBuilder();
                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://checkip.amazonaws.com/").openConnection();
                        urlConnection.setRequestProperty("User-Agent", "Android-device");
                        urlConnection.setReadTimeout(15000);
                        urlConnection.setConnectTimeout(15000);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setRequestProperty("Content-type", "application/json");
                        urlConnection.connect();
                        if (urlConnection.getResponseCode() == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
                            while (true) {
                                String line = reader.readLine();
                                if (line != null) {
                                    response.append(line);
                                }
                            }
                        }
                        urlConnection.disconnect();
                        return response.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
            new Thread(futureRun).start();
            try {
                return (String) futureRun.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getStringResponseFromServer(Context context, final String url) {
            isRegisterWithSDK();
            @SuppressLint("WrongConstant") final NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            RunnableFuture<String> futureRun = new FutureTask<>(new Callable<String>() {
                public String call() throws Exception {
                    if (info == null || !info.isAvailable() || !info.isConnected()) {
                        return null;
                    }
                    StringBuilder response = new StringBuilder();
                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                        urlConnection.setRequestProperty("User-Agent", "Android-device");
                        urlConnection.setReadTimeout(15000);
                        urlConnection.setConnectTimeout(15000);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setRequestProperty("Content-type", "application/json");
                        urlConnection.connect();
                        if (urlConnection.getResponseCode() == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
                            while (true) {
                                String line = reader.readLine();
                                if (line != null) {
                                    response.append(line);
                                }
                            }
                        }
                        urlConnection.disconnect();
                        return response.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
            new Thread(futureRun).start();
            try {
                return (String) futureRun.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
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


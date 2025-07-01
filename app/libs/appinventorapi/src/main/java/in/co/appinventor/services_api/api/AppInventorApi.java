package in.co.appinventor.services_api.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppInventorApi {
    private static final AppInventorApi ourInstance = new AppInventorApi();
    public String authToken;
    private String baserURL;
    private Context mContext;
    private String mMessage;
    public ProgressDialog mProgressDialog = null;
    private boolean mShowProgressDialog;

    public static AppInventorApi getInstance() {
        return ourInstance;
    }

    private AppInventorApi() {
    }

    public AppInventorApi(Context context, String baseURL, String authTokenTest, String mMessage2, boolean showProgressDialog) {
        this.mContext = context;
        this.baserURL = baseURL;
        this.authToken = authTokenTest;
        this.mMessage = mMessage2;
        this.mShowProgressDialog = showProgressDialog;

        if (Utility.checkConnection(context) && this.mShowProgressDialog) {
            // Only show progress dialog if context is a valid Activity and not finishing
            if (context instanceof Activity && !((Activity) context).isFinishing()) {
                this.mProgressDialog = new ProgressDialog(context);
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
    }

    public Retrofit getRetrofitInstance() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
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

    public OkHttpClient.Builder getUnsafeOkHttpClient(Context context) {

        try {
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
            builder.followRedirects(false);
            builder.addInterceptor(new NetworkConnectionInterceptor(context.getApplicationContext()));

            builder.addInterceptor(chain -> {
                Request.Builder ongoing = chain.request().newBuilder();
                ongoing.addHeader("Accept", "application/json;versions=1");
                ongoing.addHeader("Content-Type", "application/json; charset=UTF-8");
                ongoing.addHeader("Content-Encoding", "gzip");
                ongoing.addHeader("authoritytoken", AppInventorApi.this.authToken);
                return chain.proceed(ongoing.build());
            });

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void isInternetConnected() {
        if (!Utility.checkConnection(mContext)) {
            UIToastMessage.show(mContext, "No internet connection.");
        }
    }

    public void postRequest(Call<JsonObject> responseCall, final ApiCallbackCode apiCallback, final int requestCode) {
        isInternetConnected();
        responseCall.enqueue(new Callback<JsonObject>() {
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    if (mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
                        try {
                            mProgressDialog.dismiss();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } finally {
                            mProgressDialog = null;
                        }
                    }
                }
                if (response.isSuccessful()) {
                    JsonObject serverResponse = response.body();
                    try {
                        apiCallback.onResponse(new JSONObject(String.valueOf(serverResponse)), requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    apiCallback.onResponse(null, requestCode);
                }
            }

            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    if (mContext instanceof Activity) {
                        Activity activity = (Activity) mContext;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!activity.isFinishing() && !activity.isDestroyed()) {
                                try {
                                    mProgressDialog.dismiss();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                if (t instanceof SocketTimeoutException) {
                    UIToastMessage.show(mContext, "Request timed out. Please try again.");
                } else if (t instanceof IOException && t.getMessage().contains("Socket closed")) {
                    UIToastMessage.show(mContext, "Connection was closed. Please retry.");
                } else {
                    UIToastMessage.show(mContext, "Something went wrong.");
                }

                try {
                    apiCallback.onFailure(call, t, requestCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
}


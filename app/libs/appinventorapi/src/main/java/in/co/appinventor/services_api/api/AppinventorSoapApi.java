package in.co.appinventor.services_api.api;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import in.co.appinventor.services_api.app_util.AppConstants;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.helper.SDKHelper;
import in.co.appinventor.services_api.listener.SoapApiCallback;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/* renamed from: in.co.appinventor.services_api.api.AppinventorSoapApi */
public class AppinventorSoapApi {
    private static final AppinventorSoapApi ourInstance = new AppinventorSoapApi();
    private String authToken;
    private String baserURL;
    private Context mContext;
    private String mMessage;
    /* access modifiers changed from: private */
    public ProgressDialog mProgressDialog = null;
    private boolean mShowProgressDialog;

    public static AppinventorSoapApi getInstance() {
        return ourInstance;
    }

    private AppinventorSoapApi() {
    }

    public AppinventorSoapApi(Context mContext2, String baseURL, String authToken2, String mMessage2, boolean showProgressDialog) {
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
            this.mProgressDialog.show();
        }
    }

    private void isInternetConnected() {
        if (!Utility.checkConnection(this.mContext)) {
            UIToastMessage.show(this.mContext, AppConstants.MESSAGE_NETWORK_UNAVAILABLE);
        }
    }

    public Request makeOkHttpRequest(RequestBody body) {
        return new Request.Builder().url(this.baserURL).post(body).addHeader("Content-Type", "application/xml").addHeader("Authorization", this.authToken).addHeader("cache-control", "no-cache").build();
    }

    public void postSOAPRequest(String soapString, final SoapApiCallback apiCallback, final int requestCode) {
        isInternetConnected();
        isRegisterWithSDK();
        new OkHttpClient().newCall(makeOkHttpRequest(RequestBody.create(MediaType.parse("application/xml"), soapString))).enqueue(new Callback() {
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (AppinventorSoapApi.this.mProgressDialog != null && AppinventorSoapApi.this.mProgressDialog.isShowing()) {
                    AppinventorSoapApi.this.mProgressDialog.dismiss();
                }
                try {
                    apiCallback.onFailure(call.toString(), requestCode);
                    DebugLog.getInstance().d("onFailure=====" + call.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (AppinventorSoapApi.this.mProgressDialog != null && AppinventorSoapApi.this.mProgressDialog.isShowing()) {
                    AppinventorSoapApi.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    String serverResponse = response.body().string();
                    DebugLog.getInstance().d("onResponse=====" + serverResponse);
                    apiCallback.onResponse(serverResponse, requestCode);
                    return;
                }
                apiCallback.onResponse((String) null, requestCode);
            }
        });
    }

    public void postRequest(HashMap<String, String> params, final SoapApiCallback apiCallback, final int requestCode) {
        isInternetConnected();
        isRegisterWithSDK();
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        client.newCall(makeOkHttpRequest(builder.build())).enqueue(new Callback() {
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (AppinventorSoapApi.this.mProgressDialog != null && AppinventorSoapApi.this.mProgressDialog.isShowing()) {
                    AppinventorSoapApi.this.mProgressDialog.dismiss();
                }
                try {
                    apiCallback.onFailure(call.toString(), requestCode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (AppinventorSoapApi.this.mProgressDialog != null && AppinventorSoapApi.this.mProgressDialog.isShowing()) {
                    AppinventorSoapApi.this.mProgressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    apiCallback.onResponse(response.body().string(), requestCode);
                    return;
                }
                apiCallback.onResponse((String) null, requestCode);
            }
        });
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

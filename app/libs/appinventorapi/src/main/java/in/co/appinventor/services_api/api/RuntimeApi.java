package in.co.appinventor.services_api.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.helper.SDKHelper;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;

/* renamed from: in.co.appinventor.services_api.api.RuntimeApi */
public class RuntimeApi implements DialogInterface.OnCancelListener {
    private static final String MESSAGE_NETWORK_UNAVAILABLE = "No Internet Connection";
    private String authToken;
    private String baserURL;
    private boolean isInternetNotConnected = false;
    private boolean isTaskCancelled = false;
    private boolean isUnknownHostException = false;
    private String mCallbackResult = "{\"status\":\"false\",\"message\":\"Network unavailable\"}";
    private Context mContext;
    private boolean mIsCancellable;
    private String mMessage;
    /* access modifiers changed from: private */
    public ProgressDialog mProgressDialog = null;
    private String mProgressMessage = "";
    private boolean mShowProgressDialog;

    public RuntimeApi(Context mContext2, String authToken2, boolean showProgressDialog, String progressMessage, boolean isCancellable) {
        this.mContext = mContext2;
        this.authToken = authToken2;
        this.mShowProgressDialog = showProgressDialog;
        this.mProgressMessage = progressMessage;
        this.mIsCancellable = isCancellable;
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

    public void getMethod(String mURL, RequestParams requestParams, final int requestCode, final ApiCallbackCode apiCallbackEventListener) {
        try {
            isRegisterWithSDK();
            DebugLog.getInstance().d("REST Request URL:" + mURL);
            if (!Utility.checkConnection(this.mContext)) {
                UIToastMessage.show(this.mContext, "No Internet Connection");
            } else if (requestParams != null) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.addHeader("Accept", "application/json");
                client.addHeader("authoritytoken", this.authToken);
                client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                client.setMaxRetriesAndTimeout(2, 200000);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load((InputStream) null, (char[]) null);
                MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                client.setSSLSocketFactory(sf);
                client.get(mURL, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                        apiCallbackEventListener.onResponse(responseString, requestCode);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                    }

                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        apiCallbackEventListener.onFailure(errorResponse, throwable, requestCode);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        apiCallbackEventListener.onFailure(errorResponse, throwable, requestCode);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        apiCallbackEventListener.onFailure(responseString, throwable, requestCode);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                    }

                    public void onCancel() {
                        super.onCancel();
                    }

                    public void onRetry(int retryNo) {
                        super.onRetry(retryNo);
                    }
                });
            } else {
                DebugLog.getInstance().d("REST Request : " + mURL);
            }
        } catch (Exception ex) {
            DebugLog.getInstance().d("REST Exception : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void postMethod(final String mURL, RequestParams requestParams, final int requestCode, final ApiCallbackCode apiCallbackEventListener) {
        try {
            isRegisterWithSDK();
            if (!Utility.checkConnection(this.mContext)) {
                UIToastMessage.show(this.mContext, "No Internet Connection");
            } else if (requestParams != null) {
                DebugLog.getInstance().d("REST Request : " + requestParams.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.addHeader("Accept", "application/json");
                client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                client.setMaxRetriesAndTimeout(5, 200000);
                client.addHeader("authoritytoken", this.authToken);
                client.setSSLSocketFactory(new MyCustomSSLFactory(MyCustomSSLFactory.getKeystore()));
                client.post(mURL, requestParams, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                        apiCallbackEventListener.onResponse(responseString, requestCode);
                    }

                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                        apiCallbackEventListener.onFailure(errorResponse, throwable, requestCode);
                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                        apiCallbackEventListener.onFailure(errorResponse, throwable, requestCode);
                    }

                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        if (RuntimeApi.this.mProgressDialog != null && RuntimeApi.this.mProgressDialog.isShowing()) {
                            RuntimeApi.this.mProgressDialog.dismiss();
                        }
                        apiCallbackEventListener.onFailure(responseString, throwable, requestCode);
                    }

                    public void onCancel() {
                        super.onCancel();
                    }

                    public void onRetry(int retryNo) {
                        super.onRetry(retryNo);
                        DebugLog.getInstance().d("onRetry : " + mURL);
                    }
                });
            } else {
                DebugLog.getInstance().d("REST Request : " + mURL);
            }
        } catch (Exception ex) {
            DebugLog.getInstance().d("REST Exception : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void onCancel(DialogInterface dialog) {
        this.isTaskCancelled = true;
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

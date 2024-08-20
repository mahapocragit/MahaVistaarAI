package in.co.appinventor.services_api.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.helper.SDKHelper;
import in.co.appinventor.services_api.listener.Callback;
import in.co.appinventor.services_api.util.Utility;

/* renamed from: in.co.appinventor.services_api.api.AppinventorNativeApi */
public class AppinventorNativeApi implements DialogInterface.OnCancelListener {
    private static final int METHOD_GET = 0;
    private static final int METHOD_POST = 1;
    /* access modifiers changed from: private */
    public static ProgressDialog mProgressDialog;
    private static final AppinventorNativeApi ourInstance = new AppinventorNativeApi();
    private final int CONNECT_TIMEOUT_MILLIS = 16000;
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean mShowProgressDialog;

    private AppinventorNativeApi() {
    }

    public static AppinventorNativeApi getInstance() {
        return ourInstance;
    }

    public AppinventorNativeApi(Context mContext2, boolean showProgressDialog) {
        this.mContext = mContext2;
        this.mShowProgressDialog = showProgressDialog;
        if (Utility.checkConnection(mContext2) && this.mShowProgressDialog) {
            mProgressDialog = new ProgressDialog(mContext2);
            mProgressDialog.setMessage("");
            mProgressDialog.show();
        }
    }

    public String postRequest(String urlString, Map<String, Object> params) {
        isRegisterWithSDK();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"));
            StringBuilder response = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    response.append(line);
                } else {
                    bufferedReader.close();
                    conn.getInputStream().close();
                    conn.disconnect();
                    return response.toString();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public String postRequestWithAuthorization(String urlString, String auth, Map<String, Object> params) {
        isRegisterWithSDK();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            if (auth != null) {
                conn.setRequestProperty("Authorization", auth);
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"));
            StringBuilder response = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    response.append(line);
                } else {
                    bufferedReader.close();
                    conn.getInputStream().close();
                    conn.disconnect();
                    return response.toString();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public String getRequestWithThreadSafe(String fullURL) {
        isRegisterWithSDK();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        StringBuilder response = new StringBuilder();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(fullURL).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-length", "0");
            urlConnection.setRequestMethod("GET");
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        response.append(line);
                    } else {
                        reader.close();
                        urlConnection.disconnect();
                        return response.toString();
                    }
                }
            } else {
                urlConnection.disconnect();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return response.toString();
        }
    }

    public String getRequest(String fullURL) {
        isRegisterWithSDK();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(fullURL).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-length", "0");
            urlConnection.setRequestMethod("GET");
            StringBuilder response = new StringBuilder();
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        response.append(line);
                    } else {
                        reader.close();
                        urlConnection.disconnect();
                        return response.toString();
                    }
                }
            } else {
                urlConnection.disconnect();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAsyncRequest(String fullURL) {
        AppinventorGetAsyncTask asyncTask = new AppinventorGetAsyncTask();
        String[] strArr = new String[1];
        strArr[METHOD_GET] = fullURL;
        return asyncTask.doInBackground(strArr);
    }

    public void postRequestCallback(final String fullURL, final HashMap<String, String> params, final Callback callback) {
        isRegisterWithSDK();
        new Thread(new Runnable() {
            public void run() {
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(fullURL).openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("charset", "utf-8");
                    urlConnection.setRequestMethod("POST");
                    if (params != null) {
                        StringBuilder postData = new StringBuilder();
                        for (Map.Entry<String, String> item : params.entrySet()) {
                            if (postData.length() != 0) {
                                postData.append('&');
                            }
                            postData.append(URLEncoder.encode(item.getKey(), "UTF-8"));
                            postData.append('=');
                            postData.append(URLEncoder.encode(String.valueOf(item.getValue()), "UTF-8"));
                        }
                        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                        urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                        urlConnection.getOutputStream().write(postDataBytes);
                    }
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200 && callback != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        while (true) {
                            String line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            response.append(line);
                        }
                        if (AppinventorNativeApi.this.mShowProgressDialog && AppinventorNativeApi.mProgressDialog != null && AppinventorNativeApi.mProgressDialog.isShowing()) {
                            AppinventorNativeApi.mProgressDialog.dismiss();
                        }
                        callback.onSuccess(response.toString());
                        reader.close();
                    } else if (callback != null) {
                        if (AppinventorNativeApi.this.mShowProgressDialog && AppinventorNativeApi.mProgressDialog != null && AppinventorNativeApi.mProgressDialog.isShowing()) {
                            AppinventorNativeApi.mProgressDialog.dismiss();
                        }
                        callback.onError(responseCode, urlConnection.getResponseMessage());
                    }
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (AppinventorNativeApi.this.mShowProgressDialog && AppinventorNativeApi.mProgressDialog != null && AppinventorNativeApi.mProgressDialog.isShowing()) {
                        AppinventorNativeApi.mProgressDialog.dismiss();
                    }
                    if (callback != null) {
                        callback.onError(500, e.getLocalizedMessage());
                    }
                }
            }
        }).start();
    }

    public void getRequestWithParamCallback(final String fullURL, final HashMap<String, String> params, final Callback callback) {
        isRegisterWithSDK();
        new Thread(new Runnable() {
            public void run() {
                try {
                    String url = fullURL;
                    for (Map.Entry<String, String> item : params.entrySet()) {
                        String key = URLEncoder.encode(item.getKey(), "UTF-8");
                        String value = URLEncoder.encode(item.getValue(), "UTF-8");
                        if (!url.contains("?")) {
                            url = url + "?" + key + "=" + value;
                        } else {
                            url = url + "&" + key + "=" + value;
                        }
                    }
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("charset", "utf-8");
                    urlConnection.setRequestMethod("GET");
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200 && callback != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        while (true) {
                            String line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            response.append(line);
                        }
                        if (AppinventorNativeApi.this.mShowProgressDialog && AppinventorNativeApi.mProgressDialog != null && AppinventorNativeApi.mProgressDialog.isShowing()) {
                            AppinventorNativeApi.mProgressDialog.dismiss();
                        }
                        callback.onSuccess(response.toString());
                        reader.close();
                    } else if (callback != null) {
                        if (AppinventorNativeApi.this.mShowProgressDialog && AppinventorNativeApi.mProgressDialog != null && AppinventorNativeApi.mProgressDialog.isShowing()) {
                            AppinventorNativeApi.mProgressDialog.dismiss();
                        }
                        callback.onError(responseCode, urlConnection.getResponseMessage());
                    }
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (AppinventorNativeApi.this.mShowProgressDialog && AppinventorNativeApi.mProgressDialog != null && AppinventorNativeApi.mProgressDialog.isShowing()) {
                        AppinventorNativeApi.mProgressDialog.dismiss();
                    }
                    if (callback != null) {
                        callback.onError(500, e.getLocalizedMessage());
                    }
                }
            }
        }).start();
    }

    public void getRequestWithParamCallbackWithoutThread(String fullURL, HashMap<String, String> params, Callback callback) {
        isRegisterWithSDK();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        String url = fullURL;
        try {
            for (Map.Entry<String, String> item : params.entrySet()) {
                String key = URLEncoder.encode(item.getKey(), "UTF-8");
                String value = URLEncoder.encode(item.getValue(), "UTF-8");
                if (!url.contains("?")) {
                    url = url + "?" + key + "=" + value;
                } else {
                    url = url + "&" + key + "=" + value;
                }
            }
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestMethod("GET");
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200 && callback != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    response.append(line);
                }
                if (this.mShowProgressDialog && mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                callback.onSuccess(response.toString());
                reader.close();
            } else if (callback != null) {
                if (this.mShowProgressDialog && mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                callback.onError(responseCode, urlConnection.getResponseMessage());
            }
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            if (this.mShowProgressDialog && mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (callback != null) {
                callback.onError(500, e.getLocalizedMessage());
            }
        }
    }

    public void onCancel(DialogInterface dialog) {
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

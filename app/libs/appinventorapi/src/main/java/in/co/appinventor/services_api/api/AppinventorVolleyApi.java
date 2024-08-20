package in.co.appinventor.services_api.api;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import in.co.appinventor.services_api.app_util.ApiError;
import in.co.appinventor.services_api.app_util.AppConstants;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.helper.SDKHelper;
import in.co.appinventor.services_api.listener.ApiVolleyCallback;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;

/* renamed from: in.co.appinventor.services_api.api.AppinventorVolleyApi */
public class AppinventorVolleyApi {
    private String authToken;
    private String baserURL;
    private Context mContext;
    private String mMessage;
    /* access modifiers changed from: private */
    public ProgressDialog mProgressDialog = null;
    private boolean mShowProgressDialog;

    public AppinventorVolleyApi(Context mContext2, String mMessage2, boolean showProgressDialog) {
        this.mContext = mContext2;
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

    public void getStringRequest(String fullURL, final ApiVolleyCallback.Listener<String> apiResponse, final ApiVolleyCallback.ErrorListener apiError) {
        isInternetConnected();
        isRegisterWithSDK();
        Volley.newRequestQueue(this.mContext).add(new StringRequest(0, fullURL, new Response.Listener<String>() {
            public void onResponse(String response) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiError.onErrorResponse(new ApiError((Throwable) error));
            }
        }));
    }

    public void getJSONObjRequest(String fullURL, final ApiVolleyCallback.Listener<JSONObject> apiResponse, final ApiVolleyCallback.ErrorListener apiError) {
        isInternetConnected();
        isRegisterWithSDK();
        Volley.newRequestQueue(this.mContext).add(new JsonObjectRequest(0, fullURL, (JSONObject) null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                ApiError error1 = new ApiError(error);
                apiError.onErrorResponse(error1);
            }
        }));
    }

    public void getJSONObjRequestHeaderAuth(String baseURL, JSONObject param, HashMap<String, String> headers, final ApiVolleyCallback.Listener<JSONObject> apiResponse, final ApiVolleyCallback.ErrorListener apiError) {
        isInternetConnected();
        isRegisterWithSDK();
        final HashMap<String, String> hashMap = headers;
        VolleySingleton.getInstance(this.mContext).addToRequestQueue(new JsonObjectRequest(0, baseURL, param, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiError.onErrorResponse(new ApiError((Throwable) error));
                NetworkResponse response = error.networkResponse;
                if ((apiError instanceof ServerError) && response != null) {
                    try {
                        DebugLog.getInstance().d("VollServer ServerError" + new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"))).toString());
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                return hashMap;
            }
        });
    }

    public void getJSONArrayRequest(String fullURL, final ApiVolleyCallback.Listener<JSONArray> apiResponse, final ApiVolleyCallback.ErrorListener apiError) {
        isInternetConnected();
        isRegisterWithSDK();
        Volley.newRequestQueue(this.mContext).add(new JsonArrayRequest(0, fullURL, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiError.onErrorResponse(new ApiError((Throwable) error));
            }
        }));
    }

    public void postJSONObjRequest(String baseURL, JSONObject param, final ApiVolleyCallback.Listener<JSONObject> apiResponse, final ApiVolleyCallback.ErrorListener apiError) {
        isInternetConnected();
        isRegisterWithSDK();
        VolleySingleton.getInstance(this.mContext).addToRequestQueue(new JsonObjectRequest(1, baseURL, param, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiError.onErrorResponse(new ApiError((Throwable) error));
                NetworkResponse response = error.networkResponse;
                if ((apiError instanceof ServerError) && response != null) {
                    try {
                        DebugLog.getInstance().d("VollServer ServerError" + new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"))).toString());
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }));
    }

    public void postJSONObjRequestHeaderAuth(String baseURL, JSONObject param, HashMap<String, String> headers, final ApiVolleyCallback.Listener<JSONObject> apiResponse, final ApiVolleyCallback.ErrorListener apiError) {
        isInternetConnected();
        isRegisterWithSDK();
        final HashMap<String, String> hashMap = headers;
        VolleySingleton.getInstance(this.mContext).addToRequestQueue(new JsonObjectRequest(1, baseURL, param, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (AppinventorVolleyApi.this.mProgressDialog != null && AppinventorVolleyApi.this.mProgressDialog.isShowing()) {
                    AppinventorVolleyApi.this.mProgressDialog.dismiss();
                }
                apiError.onErrorResponse(new ApiError((Throwable) error));
                NetworkResponse response = error.networkResponse;
                if ((apiError instanceof ServerError) && response != null) {
                    try {
                        DebugLog.getInstance().d("VollServer ServerError" + new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"))).toString());
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                return hashMap;
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

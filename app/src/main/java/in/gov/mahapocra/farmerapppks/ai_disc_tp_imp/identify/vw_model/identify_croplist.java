package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.vw_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.getIdentify_crops_response;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.identify_model_croplist;
import in.gov.mahapocra.farmerapppks.application.MyApplication;
import in.gov.mahapocra.farmerapppks.util.HttpClientObj;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class identify_croplist extends ViewModel {

    private static final String TAG = "identify_cropList";
    public MutableLiveData<getIdentify_crops_response> list;

    public LiveData<getIdentify_crops_response> getting_crops() {
        list = new MutableLiveData<>();
        getCrops();
        return list;
    }

    public void getCrops() {

        // 🔹 Make sure to initialize AndroidNetworking with OkHttpClient
        AndroidNetworking.initialize(MyApplication.Companion.getInstance(), HttpClientObj.INSTANCE.getOKHttpClient());

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getmodels_aidisc")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", response.toString());
                        identify_model_croplist output = new identify_model_croplist();
                        ArrayList<String> cropnames_dis = new ArrayList<>();
                        ArrayList<String> cropnames_pest = new ArrayList<>();

                        try {
                            JSONArray array = response.getJSONArray("identifiercroplist_dis");
                            JSONArray array1 = response.getJSONArray("identifiercroplist_pest");

                            for (int i = 0; i < array.length(); i++) {
                                cropnames_dis.add(array.getString(i));
                            }
                            for (int i = 0; i < array1.length(); i++) {
                                cropnames_pest.add(array1.getString(i));
                            }

                            output.setcropsd(cropnames_dis);
                            output.setcropsp(cropnames_pest);
                            list.setValue(new getIdentify_crops_response(output));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new getIdentify_crops_response(e));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("API_ERROR", "Error Code: " + anError.getErrorCode());
                        Log.e("API_ERROR", "Error Detail: " + anError.getErrorDetail());
                        Log.e("API_ERROR", "Error Body: " + anError.getErrorBody());
                        Log.e("API_ERROR", "Error Response: " + anError.getResponse());
                    }
                });
    }

}


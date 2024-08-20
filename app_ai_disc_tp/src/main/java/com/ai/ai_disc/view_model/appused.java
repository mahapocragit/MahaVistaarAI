package com.ai.ai_disc.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.appuse_model;
import com.ai.ai_disc.model.appuse_response;
import com.ai.ai_disc.model.appuusemodel;
import com.ai.ai_disc.model.editprofile_model;
import com.ai.ai_disc.model.editprofile_response;
import com.ai.ai_disc.model.reportmap_latlong_model;
import com.ai.ai_disc.model.reportmap_latlong_response;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class appused extends ViewModel {

    private static final String TAG = "appuse data";
    public MutableLiveData<appuse_response> list;

    public LiveData<appuse_response> getting_crops(int user_id, String lat,String lon,String usertype) {
        // if (list == null) {
        list = new MutableLiveData<appuse_response>();
        getcrops( user_id,  lat, lon,usertype);
        // }
        return list;
    }


    public void getcrops(int user_id, String lat,String lon,String usertype) {

            JSONObject object = new JSONObject();
            try {

                object.put("userid", user_id);
                object.put("lat", lat);
                object.put("longt", lon);
                object.put("usertype", usertype);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/appuse_data")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        appuusemodel output = new appuusemodel();

                        boolean res = false;
                        try {

                            res = response.getBoolean("result");
                            //Log.d(TAG, "onResponse: " + String.valueOf(res));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new appuse_response(e));
                        }
                        if (res) {
                            output.setcropid(res);
                            list.setValue(new appuse_response(output));
                        } else {
                            ANError error = null;
                            list.setValue(new appuse_response(error));
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        list.setValue(new appuse_response(error));
                    }
                });
    }

}

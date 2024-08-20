package com.ai.ai_disc.view_model;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.Farmer.GPSTracker;
import com.ai.ai_disc.farmersprofile_fragment;
import com.ai.ai_disc.model.getIdentifier_crops_response;
import com.ai.ai_disc.model.identifier_model_croplist;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class identifier_croplist extends ViewModel  {

    private static final String TAG = "identifier_croplist";
    public MutableLiveData<getIdentifier_crops_response> list;

    public LiveData<getIdentifier_crops_response> getting_crops() {
        // if (list == null) {
        list = new MutableLiveData<getIdentifier_crops_response>();
        getcrops();
        // }
        return list;
    }


    public void getcrops() {

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getmodels_aidisc")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "onResponse: " + response.toString());
                        identifier_model_croplist output = new identifier_model_croplist();
                        ArrayList<String> cropnames_dis=new ArrayList<String>();
                        ArrayList<String> cropnames_pest=new ArrayList<String>();
                        try {
                            JSONArray array = response.getJSONArray("identifiercroplist_dis");
                            JSONArray array1 = response.getJSONArray("identifiercroplist_pest");
                            //Log.d(TAG, "onResponse: " + array1);
                            for (int i = 0; i < array.length(); i++) {

                                String crop_name = array.getString(i);
                                cropnames_dis.add(crop_name);
                            }
                            for (int i = 0; i < array1.length(); i++) {

                                String crop_name = array1.getString(i);
                                cropnames_pest.add(crop_name);
                            }
                            output.setcropsd(cropnames_dis);
                            output.setcropsp(cropnames_pest);
                            list.setValue(new getIdentifier_crops_response(output));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new getIdentifier_crops_response(e));
                        }
                    }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new getIdentifier_crops_response(error));
                    }
                });
    }

}

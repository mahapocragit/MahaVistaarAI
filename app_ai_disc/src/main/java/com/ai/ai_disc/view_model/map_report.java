package com.ai.ai_disc.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.map_info_model;
import com.ai.ai_disc.model.map_info_response;
import com.ai.ai_disc.model.map_report_response;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class map_report extends ViewModel {

    private static final String TAG = "globeview_report";
    public MutableLiveData<map_report_response> list;

    public LiveData<map_report_response> getting_crops() {
        // if (list == null) {
        list = new MutableLiveData<map_report_response>();
        getcrops();
        // }
        return list;
    }


    public void getcrops() {

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_report_map")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        map_info_model output = new map_info_model();
                        ArrayList<String> cropnamesd=new ArrayList<String>();
                        ArrayList<String> cropnamesp=new ArrayList<String>();
                        ArrayList<String> cropidd=new ArrayList<String>();
                        ArrayList<String> cropidp=new ArrayList<String>();
                        ArrayList<JSONArray> dis=new ArrayList<JSONArray>();
                        ArrayList<JSONArray> disnames=new ArrayList<JSONArray>();
                        ArrayList<JSONArray> pest=new ArrayList<JSONArray>();
                        ArrayList<JSONArray> pestnames=new ArrayList<JSONArray>();

                        try {
                            JSONArray arraydisease = response.getJSONArray("crop_list_disease");
                            JSONArray arraypest = response.getJSONArray("crop_list_pest");

                            for (int i = 0; i < arraydisease.length(); i++) {

                                JSONObject object = (JSONObject) arraydisease.get(i);

                                cropnamesd.add(object.getString("crop_name"));
                                cropidd.add(object.getString("crop_id"));
                                disnames.add(object.getJSONArray("crop_diseasenames"));
                                dis.add(object.getJSONArray("crop_diseases"));

                            }

                            for (int i = 0; i < arraypest.length(); i++) {

                                JSONObject object = (JSONObject) arraypest.get(i);

                                cropnamesp.add(object.getString("crop_name"));
                                cropidp.add(object.getString("crop_id"));
                                pestnames.add(object.getJSONArray("crop_pestnames"));
                                pest.add(object.getJSONArray("crop_pest"));

                            }
                            output.setcrops(cropnamesd);
                            output.setcropsp(cropnamesp);
                            output.setcrop_id(cropidd);
                            output.setcrop_idp(cropidp);
                            output.setdisname(disnames);
                            output.setdisid(dis);
                            output.setpestname(pestnames);
                            output.setpestid(pest);


                            list.setValue(new map_report_response(output));


                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new map_report_response(e));
                        }



                    }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new map_report_response(error));
                    }
                });
    }

}

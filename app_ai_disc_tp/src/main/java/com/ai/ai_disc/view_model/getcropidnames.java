package com.ai.ai_disc.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.getIdentifier_crops_response;
import com.ai.ai_disc.model.identifier_model_croplist;
import com.ai.ai_disc.model.map_info_response;
import com.ai.ai_disc.model.report_info_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class getcropidnames extends ViewModel  {

    private static final String TAG = "get crop and disease/pest";
    public MutableLiveData<map_info_response> list;

    public LiveData<map_info_response> getting_crops(int type) {
        // if (list == null) {
        list = new MutableLiveData<map_info_response>();
        getcrops(type);
        // }
        return list;
    }


    public void getcrops(int type) {
        JSONObject object = new JSONObject();
        try {

            object.put("type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getcrop_dis_pest")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse:hhhhhhhhhhhhhhh " + response.toString());
                        report_info_model output = new report_info_model();
                        ArrayList<String> cropnames=new ArrayList<String>();
                        ArrayList<String> cropnamesid=new ArrayList<String>();
                        ArrayList<JSONArray> typeid=new ArrayList<>();
                        ArrayList<JSONArray> typename=new ArrayList<>();
                        try {
                            JSONArray array = response.getJSONArray("output");
                            //Log.d(TAG, "onResponse: " + array1);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                cropnames.add(object.getString("crop"));
                                cropnamesid.add(object.getString("cropid"));
                                typename.add(object.getJSONArray("diseasepest"));
                                typeid.add(object.getJSONArray("diseasepestid"));

                            }
                            output.setcropid(cropnamesid);
                            output.setcropname(cropnames);
                            output.setdpid(typeid);
                            output.setdpnames(typename);
                            list.setValue(new map_info_response(output));


                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new map_info_response(e));
                        }


                    }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new map_info_response(error));
                    }
                });
    }

}

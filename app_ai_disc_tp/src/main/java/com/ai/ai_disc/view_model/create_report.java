package com.ai.ai_disc.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.create_report_response;
import com.ai.ai_disc.model.createreport_model;
import com.ai.ai_disc.model.map_info_response;
import com.ai.ai_disc.model.report_info_model;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class create_report extends ViewModel  {

    private static final String TAG = "create report";
    public MutableLiveData<create_report_response> list;

    public LiveData<create_report_response> getting_crops(int type,int id, int user, double lat,double lon,String extra,int cropid) {
        // if (list == null) {
        list = new MutableLiveData<create_report_response>();
        getcrops(type, id,  user,  lat, lon, extra, cropid);
        // }
        return list;
    }


    public void getcrops(int type,int id, int user, double lat,double lon,String extra,int cropid) {
        JSONObject object = new JSONObject();
        try {

            object.put("type", type);
            object.put("id", id);
            object.put("userid", user);
            object.put("lat", lat);
            object.put("lon", lon);
            object.put("info", extra);
            object.put("cropid", cropid);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_report")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "onResponse:hhhhhhhhhhhhhhh " + response.toString());
                        createreport_model output = new createreport_model();
                        String reportsmain="";
                        String reports="";
                        //Log.d(TAG, "onResponse:hhhhhhhhhhhhhhh " + response.toString());
                        boolean res = false;
                        try {
                              reports = response.getString("reportid");
                            res = response.getBoolean("result");
                            //Log.d(TAG, "onResponse: " + array1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new create_report_response(e));
                        }
                        if (res){

                            output.setreport(reports);
                            list.setValue(new create_report_response(output));
                        }else{output.setreport(reports);
                            list.setValue(new create_report_response(output));

                        }



                    }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new create_report_response(error));
                    }
                });
    }

}

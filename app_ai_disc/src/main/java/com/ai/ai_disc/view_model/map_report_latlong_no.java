package com.ai.ai_disc.view_model;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.reportmap_latlong_model;
import com.ai.ai_disc.model.reportmap_latlong_response;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class map_report_latlong_no extends ViewModel {

    private static final String TAG = "latlong put";
    public MutableLiveData<reportmap_latlong_response> list;

    public LiveData<reportmap_latlong_response> getting_crops(int crop_id, int dp_id,int typ) {
        // if (list == null) {
        list = new MutableLiveData<reportmap_latlong_response>();
        getcrops(crop_id, dp_id,typ);
        // }
        return list;
    }


    public void getcrops(int crop_id, int dp_id,int typ) {

            JSONObject object = new JSONObject();
            try {

                object.put("crop_id", crop_id);
                object.put("dp_id", dp_id);
                object.put("typ", typ);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_report_latlong")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        reportmap_latlong_model output = new reportmap_latlong_model();
                        ArrayList<String> formid=new ArrayList<String>();
                        ArrayList<String> numb=new ArrayList<String>();
                        ArrayList<String> lat=new ArrayList<String>();
                        ArrayList<String> lon=new ArrayList<String>();


                        try {
                            JSONArray arraylatlon = response.getJSONArray("list_latlong");

                            for (int i = 0; i < arraylatlon.length(); i++) {

                                JSONObject object = (JSONObject) arraylatlon.get(i);

                                formid.add(object.getString("formid"));
                                numb.add(object.getString("num_image"));
                                lat.add(object.getString("lat"));
                                lon.add(object.getString("lon"));

                            }
                            output.setformid(formid);
                            output.setnumb(numb);
                            output.setlat(lat);
                            output.setlong(lon);

                            list.setValue(new reportmap_latlong_response(output));




                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new reportmap_latlong_response(e));
                        }



                    }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new reportmap_latlong_response(error));
                    }
                });
    }

}

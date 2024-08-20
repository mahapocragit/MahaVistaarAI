package com.ai.ai_disc.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.create_report_response;
import com.ai.ai_disc.model.createreport_model;
import com.ai.ai_disc.model.getIdentifier_crops_response;
import com.ai.ai_disc.model.myreport_model;
import com.ai.ai_disc.model.myreports_response;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class myreport_view extends ViewModel  {

    private static final String TAG = "create report";
    public MutableLiveData<myreports_response> list;

    public LiveData<myreports_response> getting_crops( int user) {
        // if (list == null) {
        list = new MutableLiveData<myreports_response>();
        getcrops(user);
        // }
        return list;
    }


    public void getcrops( int user) {
        JSONObject object = new JSONObject();
        try {

            object.put("userid", user);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_myreport")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        myreport_model output = new myreport_model();
                         ArrayList<String> lats =new ArrayList<>();
                         ArrayList<String> longs =new ArrayList<>();
                         ArrayList<String> dpname =new ArrayList<>();
                         ArrayList<String> cropname =new ArrayList<>();
                         ArrayList<Integer> whichtype =new ArrayList<>();
                         ArrayList<Integer> reportid =new ArrayList<>();
                        ArrayList<Integer> cropid =new ArrayList<>();
                        ArrayList<Integer> dpid =new ArrayList<>();
                         ArrayList<String> dates =new ArrayList<>();
                         ArrayList<Integer> numbs =new ArrayList<>();
                         try{
                             JSONArray array = response.getJSONArray("reportlist");
                             for (int i = 0; i < array.length(); i++) {

                                 JSONObject object = (JSONObject) array.get(i);
                                 lats.add(object.getString("lat"));
                                 longs.add(object.getString("lon"));
                                 dpname.add(object.getString("dpname"));
                                 cropname.add(object.getString("cropname"));
                                 whichtype.add(object.getInt("dptype"));
                                 dates.add(object.getString("dates"));
                                 numbs.add(Integer.parseInt(object.getString("numbers")));
                                 reportid.add(Integer.parseInt(object.getString("reportid")));
                                 dpid.add(Integer.parseInt(object.getString("dptypeid")));
                                 cropid.add(Integer.parseInt(object.getString("cropid")));

                             }
                             output.setcropname(cropname);
                             output.setdates(dates);
                             output.setdpname(dpname);
                             output.setlats(lats);
                             output.setlongss(longs);
                             output.setnumbs(numbs);
                             output.setreportid(reportid);
                             output.setwhichtype(whichtype);
                             output.setcropid(cropid);
                             output.setdpids(dpid);
                             list.setValue(new myreports_response(output));
                             //Log.d(TAG, "onResponse:hhhhhhhhhhhhhhh " + response.toString());
                         }
                         catch (JSONException e) {
                             e.printStackTrace();
                             list.setValue(new myreports_response(e));
                         }

                         }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new myreports_response(error));
                    }
                });
    }

}

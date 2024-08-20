package com.ai.ai_disc.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.create_report_response;
import com.ai.ai_disc.model.createreport_model;
import com.ai.ai_disc.model.editprofile_model;
import com.ai.ai_disc.model.editprofile_response;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class editprofile_view extends ViewModel  {

    private static final String TAG = "edit profile";
    public MutableLiveData<editprofile_response> list;

    public LiveData<editprofile_response> getting_crops(int uid,String username,String password, String fname, String lname,String phone,String email) {
        // if (list == null) {
        list = new MutableLiveData<editprofile_response>();
        getcrops( uid, username, password,  fname,  lname, phone, email);
        // }
        return list;
    }


    public void getcrops(int uid,String username,String password, String fname, String lname,String phone,String email) {
        JSONObject object = new JSONObject();
        try {

            object.put("username", username);
            object.put("existuserid", uid);
            object.put("password", password);
            object.put("fname", fname);
            object.put("lname", lname);
            object.put("phone", phone);
            object.put("email", email);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/editprofile")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "onResponse:hhhhhhhhhhhhhhh " + response.toString());
                        editprofile_model output = new editprofile_model();

                        boolean res = false;
                        int resp=0;
                        try {

                            res = response.getBoolean("result");
                            resp = response.getInt("response");
                            //Log.d(TAG, "onResponse: " + array1);
                            output.setresp(resp);
                            output.setresult(res);
                            list.setValue(new editprofile_response(output));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new editprofile_response(e));
                        }




                    }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new editprofile_response(error));
                    }
                });
    }

}

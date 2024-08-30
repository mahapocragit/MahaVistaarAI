package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.vw_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.getIdentify_crops_response;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.identify_model_croplist;

public class identify_croplist extends ViewModel {

    private static final String TAG = "identify_cropList";
    public MutableLiveData<getIdentify_crops_response> list;

    public LiveData<getIdentify_crops_response> getting_crops() {
        list = new MutableLiveData<>();
        getCrops();
        return list;
    }

    public void getCrops() {
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getmodels_aidisc")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "onResponse: " + response.toString());
                        identify_model_croplist output = new identify_model_croplist();
                        ArrayList<String> cropnames_dis = new ArrayList<String>();
                        ArrayList<String> cropnames_pest = new ArrayList<String>();
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
                            list.setValue(new getIdentify_crops_response(output));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.setValue(new getIdentify_crops_response(e));
                        }
                    }


                    @Override
                    public void onError(ANError error) {

                        list.setValue(new getIdentify_crops_response(error));
                    }
                });
    }

}


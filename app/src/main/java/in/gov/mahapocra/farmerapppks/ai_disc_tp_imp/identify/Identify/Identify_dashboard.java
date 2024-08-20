package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.Identify;




import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.activity.DashboardScreen;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.Farmer.RecyclerTouchListener;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.adapter.DashboardAdapter;

import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.detection_instance;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.farmerdiseasepestIdentify;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.Model_Dashboard;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.detect_ins;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.getIdentify_crops_response;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.model_identify.identify_model_croplist;
import in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify.vw_model.identify_croplist;
import in.gov.mahapocra.farmerapppks.notification.NotificationListActivity;
import in.gov.mahapocra.farmerapppks.notification.ReadNotificationActivity;


public class Identify_dashboard extends AppCompatActivity {
    private static ArrayList<String> discrops, pestcrops, croplist, discrops_img;
    private static ArrayList<Integer> number_exist;
    RecyclerView recyclerView;
    DashboardAdapter dashboardAdapter;
    ArrayList<Model_Dashboard> model_dashboardContents;

    TextView textViewHeaderTitle;
    ImageView imageMenushow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle);
        imageMenushow = findViewById(R.id.imageMenushow);

        imageMenushow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.identify_Pest_Disease);

        imageMenushow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Identify_dashboard.this, DashboardScreen.class);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        model_dashboardContents = new ArrayList<>();
        getinfo();

        dashboardAdapter = new DashboardAdapter(getApplicationContext(), model_dashboardContents);
        recyclerView.setAdapter(dashboardAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String cropname = croplist.get(position);
                go_to_identify_disease(cropname, number_exist.get(position));
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }
        ));
    }

    public void go_to_identify_disease(String name, int numb) {
        if (numb == 1) {
            String cropprediction = "disease" + "_detection_" + name.replace(" ", "");
            //Intent gotopage =new Intent(this, farmerdiseasepestIdentify.class);
            Intent gotopage = new Intent(this, Identify_img_upload.class);
            gotopage.putExtra("crop", name);
            gotopage.putExtra("type", "disease");
            gotopage.putExtra("url", cropprediction.toLowerCase());
            gotopage.putExtra("farmerIdentification", "Farmer_prediction");

            detect_ins.getInstance().settype("disease");
            detect_ins.getInstance().setcrop(name);
            detect_ins.getInstance().setnumb(numb);
            detect_ins.getInstance().setdetection("Farmer_prediction");
            detect_ins.getInstance().seturl(cropprediction.toLowerCase());
            startActivity(gotopage);
        } else {
            if (numb == 2) {
                String cropprediction = "pest" + "_detection_" + name.replace(" ", "");
                Intent gotopage = new Intent(this, farmerdiseasepestIdentify.class);
                gotopage.putExtra("crop", name);
                gotopage.putExtra("type", "pest");
                gotopage.putExtra("url", cropprediction.toLowerCase());
                gotopage.putExtra("farmerIdentification", "Farmer_prediction");

                //Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, cropprediction, Toast.LENGTH_SHORT).show();
                detect_ins.getInstance().settype("pest");
                detect_ins.getInstance().setcrop(name);
                detect_ins.getInstance().setnumb(numb);
                detect_ins.getInstance().setdetection("Farmer_prediction");
                detect_ins.getInstance().seturl(cropprediction.toLowerCase());
                startActivity(gotopage);
            } else {
            /*Intent intent = new Intent(this, Farmer_Disease_identifier_pest_Identifier_Activity.class);
            detect_ins.getInstance().setcrop(name);
            detect_ins.getInstance().setnumb(numb);
            detect_ins.getInstance().setdetection("Farmer_prediction");

            intent.putExtra("crop",name);
            intent.putExtra("number",numb);
            intent.putExtra("farmerIdentification","Farmer_prediction");
            startActivity(intent);*/


                String cropprediction="disease"+"detection"+name.replace(" ", "");;
                Intent gotopage =new Intent( this, farmerdiseasepestIdentify.class);
                gotopage.putExtra("crop",name);
                gotopage.putExtra("type","disease");
                gotopage.putExtra("url",cropprediction.toLowerCase());
                gotopage.putExtra("farmerIdentification","Farmer_prediction");

                detection_instance.getInstance().settype("disease");
                detection_instance.getInstance().setcrop(name);
                detection_instance.getInstance().setnumb(numb);
                detection_instance.getInstance().setdetection("Farmer_prediction");
                detection_instance.getInstance().seturl(cropprediction.toLowerCase());
                startActivity(gotopage);
            }
        }

       /* Intent intent = new Intent(Farmer_DiseasIdentifier_profile1.this, Identify_disease.class);
        intent.putExtra("crop", name);
        intent.putExtra("farmerIdentification",farmerIdentification);
        startActivity(intent);*/
    }

    void getinfo(){
        identify_croplist viewmodel= ViewModelProviders.of(this).get(identify_croplist.class);
        viewmodel.getting_crops().observe(this, new Observer<getIdentify_crops_response>() {
            @Override
            public void onChanged(getIdentify_crops_response getIdentify_crops_response) {

                if(getIdentify_crops_response.getModel() != null){
                    identify_model_croplist model = getIdentify_crops_response.getModel();
                    discrops=model.getcropsd();
                    pestcrops=model.getcropsp();
                    getcroplist();
                    discrops_img=new ArrayList<>();

                    JSONObject object = new JSONObject();
                    try {
                        JSONArray idf=new JSONArray(croplist);
                        object.put("list", idf);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_crop_img")
                            .addJSONObjectBody(object)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {
                                        discrops_img.clear();
                                        JSONArray array = jsonObject.getJSONArray("list");
                                        //Log.d("mmmm",jsonObject.toString());
                                        for (int i = 0; i < array.length(); i++) {
                                            discrops_img.add(array.getString(i));
                                        }
                                        for (int i = 0; i < croplist.size(); i++) {
                                            Model_Dashboard model_dashboardContent = new Model_Dashboard();
                                            model_dashboardContent.setGetGridText(croplist.get(i));
                                            model_dashboardContent.setGridImage(discrops_img.get(i));
                                            model_dashboardContents.add(model_dashboardContent);
                                        }
                                        dashboardAdapter.notifyDataSetChanged();
                                        recyclerView.setAdapter(dashboardAdapter);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                }
                            });


                }

            }
        });

    }

    void getcroplist() {

        ArrayList<String> cpl = new ArrayList<>();
        ArrayList<Integer> num = new ArrayList<>();
        if (discrops.isEmpty()) {

        }
        if (pestcrops.isEmpty()) {

        }
        for (int h = 0; h < discrops.size(); h += 1) {
            String cropf = discrops.get(h);
            cpl.add(cropf);
            if (pestcrops.contains(cropf)) {
                num.add(3);
            } else {
                num.add(1);
            }

        }
        for (int k = 0; k < pestcrops.size(); k += 1) {
            String crop = pestcrops.get(k);
            if (cpl.contains(crop)) {
                int g = 0;
            } else {
                cpl.add(crop);
                num.add(2);
            }
        }
        croplist = cpl;
        number_exist = num;
        //Log.d("after",croplist.toString());
    }

}
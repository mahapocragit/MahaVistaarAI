package com.ai.ai_disc;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONException;
import org.json.JSONObject;

public class Detect_disease_info extends AppCompatActivity {

    TextView text1,text2,text3,text4,text5,text6,text7,text8,text9,text10;
    TextView text11,text12,text13,text14,text15,text16,text17,text18,text19,text20;
    TextView text21,text22,text23,text24,text25,text26,text27,text28,text29,text30;

    LinearLayout main_layout;
    ProgressDialog progress;
    InternetReceiver internet = new InternetReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_disease_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         setTitle("AI-DISC");

         Intent intent = getIntent();
         Bundle data= intent.getExtras();
         String disease_code=data.getString("disease_code");

         main_layout=(LinearLayout)findViewById(R.id.main_layout);



        progress = new ProgressDialog(Detect_disease_info.this);
        progress.setCancelable(false);
        progress.setMessage("Loading.Please wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

         initialize();

         get_disease_info(disease_code);

    }

    public void get_disease_info(String disease_code){
/*
{
  "disease_id": "sample string 1",
  "language": "sample string 2"
}
 */



        JSONObject object = new JSONObject();
        try {
            object.put("disease_id",disease_code);
            object.put("language","ENGLISH");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(" object is:"+object);


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_disease_info")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        progress.cancel();

                        System.out.print(" response :"+response);
/*
{
  "Id": "sample string 1",
  "disease_id": "sample string 2",
  "disease_local_name": "sample string 3",
  "disease_english_name": "sample string 4",
  "disease_path_sc_name": "sample string 5",
  "disease_path_geo_dist": "sample string 6",
  "disease_path_cycle": "sample string 7",
  "disease_symptom": "sample string 8",
  "disease_control": "sample string 9",
  "disease_prevention": "sample string 10",
  "disease_spread_mode": "sample string 11",
  "disease_p_source": "sample string 12",
  "disease_s_source": "sample string 13",
  "disease_path_p_host": "sample string 14",
  "disease_path_A_host": "sample string 15",
  "Disease_PathCongEnv": "sample string 16",
  "Disease_OccurancePd": "sample string 17",
  "crop_name": "sample string 18",
  "language": "sample string 19"
}
 */
                        try {
                            String id=  response.getString("Id");
                            String disease_id=  response.getString("disease_id");
                            String disease_local_name=   response.getString("disease_local_name");
                            String disease_english_name=  response.getString("disease_english_name");
                            String disease_path_sc_name=   response.getString("disease_path_sc_name");
                            String disease_path_geo_dist=   response.getString("disease_path_geo_dist");
                            String disease_path_cycle=   response.getString("disease_path_cycle");
                            String disease_symptom=   response.getString("disease_symptom");
                            String disease_control=   response.getString("disease_control");
                            String disease_prevention=   response.getString("disease_prevention");
                            String disease_spread_mode=   response.getString("disease_spread_mode");
                            String disease_p_source=   response.getString("disease_p_source");
                            String disease_s_source=   response.getString("disease_s_source");
                            String disease_path_p_host=   response.getString("disease_path_p_host");
                            String disease_path_a_host=   response.getString("disease_path_A_host");
                            String disease_pathCongEnv=   response.getString("Disease_PathCongEnv");
                            String disease_occurancePd=   response.getString("Disease_OccurancePd");
                            String crop_name=   response.getString("crop_name");
                            String language=  response.getString("language");
                            boolean found=  response.getBoolean("found");

                            if(!found){

                                main_layout.removeAllViews();
                                LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                                TextView text = new TextView(Detect_disease_info.this);
                                text.setText("Disease not found");
                                text.setTextSize(20);
                                text.setLayoutParams(text_params);
                                text.setGravity(Gravity.CENTER|Gravity.TOP);
                                main_layout.addView(text);

                            }else{

                                text2.setText(disease_local_name);
                                text4.setText(disease_english_name);
                                text6.setText(disease_path_sc_name);
                                text8.setText(disease_path_geo_dist);
                                text10.setText(disease_path_cycle);
                                text12.setText(disease_symptom);
                                text14.setText(disease_control);
                                text16.setText(disease_prevention);
                                text18.setText(disease_spread_mode);
                                text20.setText(disease_p_source);
                                text22.setText(disease_s_source);
                                text24.setText(disease_path_p_host);
                                text26.setText(disease_path_a_host);
                                text28.setText(disease_pathCongEnv);
                                text30.setText(disease_occurancePd);

                                System.out.println(" 1:"+id);
                                System.out.println(" 2:"+disease_id);
                                System.out.println(" 3:"+disease_local_name);
                                System.out.println(" 4:"+disease_english_name);
                                System.out.println(" 5:"+disease_path_sc_name);
                                System.out.println(" 6:"+disease_path_geo_dist);
                                System.out.println(" 7:"+disease_path_cycle);
                                System.out.println(" 8:"+disease_symptom);
                                System.out.println(" 9:"+disease_control);
                                System.out.println(" 10:"+disease_prevention);
                                System.out.println(" 11:"+disease_spread_mode);
                                System.out.println(" 12:"+disease_p_source);
                                System.out.println(" 13:"+disease_s_source);
                                System.out.println(" 14:"+disease_path_p_host);
                                System.out.println(" 15:"+disease_path_a_host);
                                System.out.println(" 16:"+disease_pathCongEnv);
                                System.out.println(" 17:"+disease_occurancePd);
                                System.out.println(" 18:"+crop_name);
                                System.out.println(" 19:"+language);
                            }







                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                    @Override
                    public void onError(ANError error) {


                        progress.cancel();


                        main_layout.removeAllViews();
                        LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                        TextView text = new TextView(Detect_disease_info.this);
                        text.setText("Error occured.");
                        text.setTextSize(20);
                        text.setLayoutParams(text_params);
                        text.setGravity(Gravity.CENTER|Gravity.TOP);
                        main_layout.addView(text);


                    }
                });








    }

    public void initialize(){

        text1=(TextView)findViewById(R.id.text1);
        text2=(TextView)findViewById(R.id.text2);
        text3=(TextView)findViewById(R.id.text3);
        text4=(TextView)findViewById(R.id.text4);
        text5=(TextView)findViewById(R.id.text5);
        text6=(TextView)findViewById(R.id.text6);
        text7=(TextView)findViewById(R.id.text7);
        text8=(TextView)findViewById(R.id.text8);
        text9=(TextView)findViewById(R.id.text9);
        text10=(TextView)findViewById(R.id.text10);
        text11=(TextView)findViewById(R.id.text11);
        text12=(TextView)findViewById(R.id.text12);
        text13=(TextView)findViewById(R.id.text13);
        text14=(TextView)findViewById(R.id.text14);
        text15=(TextView)findViewById(R.id.text15);
        text16=(TextView)findViewById(R.id.text16);
        text17=(TextView)findViewById(R.id.text17);
        text18=(TextView)findViewById(R.id.text18);
        text19=(TextView)findViewById(R.id.text19);
        text20=(TextView)findViewById(R.id.text20);
        text21=(TextView)findViewById(R.id.text21);
        text22=(TextView)findViewById(R.id.text22);
        text23=(TextView)findViewById(R.id.text23);
        text24=(TextView)findViewById(R.id.text24);
        text25=(TextView)findViewById(R.id.text25);
        text26=(TextView)findViewById(R.id.text26);
        text27=(TextView)findViewById(R.id.text27);
        text28=(TextView)findViewById(R.id.text28);
        text29=(TextView)findViewById(R.id.text29);
        text30=(TextView)findViewById(R.id.text30);


        text1.setText("Disease Local name");
        text2.setText("");
        text3.setText("Disease English name");
        text4.setText("");
        text5.setText("Disease Scientific name");
        text6.setText("");
        text7.setText("Disease Geographical Area");
        text8.setText("");
        text9.setText("Disease Lifecycle");
        text10.setText("");
        text11.setText("Disease Symptom");
        text12.setText("");
        text13.setText("Disease Control Measure");
        text14.setText("");
        text15.setText("Disease Prevention Measure");
        text16.setText("");
        text17.setText("Disease Spread Mode");
        text18.setText("");
        text19.setText("Disease Primary Source");
        text20.setText("");
        text21.setText("Disease Secondary Source");
        text22.setText("");
        text23.setText("Disease_path_p_host");
        text23.setVisibility(View.GONE);
        text24.setText("");
        text24.setVisibility(View.GONE);
        text25.setText("disease_path_a_host");
        text25.setVisibility(View.GONE);
        text26.setText("");
        text26.setVisibility(View.GONE);
        text27.setText("disease_pathCongEnv");
        text27.setVisibility(View.GONE);
        text28.setText("");
        text28.setVisibility(View.GONE);
        text29.setText("Disease Occurance Period");
        text30.setText("");


    }

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet,intentFilter);




    }


    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(internet);

    }

}

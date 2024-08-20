package com.ai.ai_disc.Farmer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.ai.ai_disc.farmersprofile_fragment1;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by Administrator on 14-02-2017.
 */

public class json_new_submit_query_asyntask extends AsyncTask<String,Void,String>
{

    String value_returned;
    String id_returned="";
    Context context1;
    String user_id;
    String organization_id;
    ProgressDialog progressDialog1;
    Button submit1;
    String image1_proper;
    JSONObject obj;
    public json_new_submit_query_asyntask(Context context, ProgressDialog progressDialog, Button submit){
        context1=context;
        progressDialog1=progressDialog;
        submit1=submit;
    }


    @Override
    protected String doInBackground(String... param)
    {


        try {
            user_id=param[0];
            //organization_id=param[1];
            String description=param[1];
            String crop_id=param[2];
            String firebase_token="";

             obj= new JSONObject();

            String image_1= send_query2_layout.image_data[0];
          /*  String image_2= send_query2_layout.image_data[1];
            String image_3= send_query2_layout.image_data[2];*/
         /*   String video_1= send_query2_layout.video_data[0];
            String video_2= send_query2_layout.video_data[1];
            String video_3= send_query2_layout.video_data[2];
            String audio_1= send_query2_layout.audio_data[0];
            String audio_2= send_query2_layout.audio_data[1];
            String audio_3= send_query2_layout.audio_data[2];*/
            if(image_1!=null)
            {
                if(!image_1.isEmpty())
                {
                    image1_proper=image_1.replaceAll("\n","");

                }
                else
                {
                    image1_proper="";
                }

            }
            else
            {
                 image1_proper="";
            }

        /*    String image2_proper=image_2.replaceAll("\n","");
            String image3_proper=image_3.replaceAll("\n","");*/
       /*     String video1_proper=video_1.replaceAll("\n","");
            String video2_proper=video_2.replaceAll("\n","");
            String video3_proper=video_3.replaceAll("\n","");
            String audio1_proper=audio_1.replaceAll("\n","");
            String audio2_proper=audio_2.replaceAll("\n","");
            String audio3_proper=audio_3.replaceAll("\n","");*/
            // String pdf_proper=pdf.replaceAll("\n","");


            obj.put("image_path",image1_proper);
       /*     obj.put("image_2",image2_proper);
            obj.put("image_3",image3_proper);*/
            obj.put("query_description",description);
            obj.put("crop_id",crop_id);
            obj.put("farmer_id",user_id);
            obj.put("query_status","0");
            obj.put("language_type","English");

            Log.d("ImagePath",image1_proper);


            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/farmersendquery")
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                        @Override
                        public void onResponse(Response okHttpResponse, String response) {

                            if(!response.isEmpty()){

                                if(!response.equals("0"))
                                {
                                    Toast.makeText(context1, "Your Query has been submitted successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context1, farmersprofile_fragment1.class);
                                    intent.putExtra("user_id",user_id);
                                    // intent.putExtra("organization_id",organization_id);
                                    context1.startActivity(intent);
                                }
                            }else{
                                Toast.makeText(context1,"query not submitted",Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            //  System.out.println("inside android  error , in send query");

                            Toast.makeText(context1,"query not submitted",Toast.LENGTH_LONG).show();

                        }
                    });


          /*  obj.put("video_1",video1_proper);
            obj.put("video_2",video2_proper);
            obj.put("video_3",video3_proper);
            obj.put("audio_1",audio1_proper);
            obj.put("audio_2",audio2_proper);
            obj.put("audio_3",audio3_proper);*/
            System.out.println("userId" + user_id);
            System.out.println("cropId" + crop_id);
            System.out.println("desc" + description);
            System.out.println("imagepath" +image1_proper );
        /*    obj.put("item_id",commodity_name_code);
            obj.put("main_problem_id",commodity_type_code);
            obj.put("problem_area_id",problem_area_code);
            obj.put("pdf_file","");
            obj.put("language","english");
            obj.put("desc",description);
            obj.put("kvk_id",organization_id);
            obj.put("user_id",user_id);
            obj.put("token_id",firebase_token);*/

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return value_returned;
    }

    @Override
    protected void onPostExecute(String result)
    {

        progressDialog1.dismiss();
        submit1.setEnabled(true);



    }



}


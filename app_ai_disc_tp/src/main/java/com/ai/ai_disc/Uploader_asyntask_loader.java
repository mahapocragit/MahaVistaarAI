package com.ai.ai_disc;

import android.content.Context;


import androidx.loader.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Uploader_asyntask_loader extends AsyncTaskLoader<String> {

//    String institute_code;
    public Uploader_asyntask_loader(Context context) {
        super(context);
  //      this.institute_code=institute_code;
    }

    @Override
    public String loadInBackground() {
        System.out.println("4");


        String json_string = "";
        try {
            //   institute_information
            String url_address = "https://nibpp.krishimegh.in/Api/nibpp/Add_image_data";
            System.out.println("point 80" + url_address);

            URL url = new URL(url_address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");


           /*
            {
  "institute_code": "sample string 1"
}
            */
          /*  JSONObject jo = new JSONObject();
            jo.put("institute_code",institute_code);


            OutputStream os=connection.getOutputStream();
            System.out.println("point 46.1:json object:"+jo);
            DataOutputStream dos= new DataOutputStream(os);
            System.out.println("point 46.2");


            dos.writeBytes(String.valueOf(jo));

            System.out.println(" dataoutputstream : sending data"+dos);

            System.out.println("point 52.0");
            dos.close();
            os.close();


*/



            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("point 88 ");
            String line;

            System.out.println("point 89:");
            StringBuilder res = new StringBuilder();
            System.out.println("point 90 :");
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("point 91 :");
                res.append(line);
                System.out.println("response output of line:" + line + "and total:" + res);
            }
            bufferedReader.close();


            connection.disconnect();
            System.out.println("point 93 ");
            json_string = res.toString();

            // JSONObject jo = new JSONObject(json_string);


             /*   boolean status= jo.getBoolean("Status");
                String message=jo.getString("Message");

                if(status){

                    // Toast.makeText(Forgot_account.this,"email sent successfully to your email ,which contain username and password.",Toast.LENGTH_LONG).show();
                    show_dialog("Email sent successfully to your email id .Please check email id");
                }else{
                    // Toast.makeText(Forgot_account.this,"email  not sent.",Toast.LENGTH_LONG).show();
                    show_dialog("Email not sent.");
                }
                */


            System.out.println(" 102 ");

        } catch (MalformedURLException e) {
          //  System.out.println("start malformed url exception 103 ");
            e.printStackTrace();
          //  System.out.println("end malformed url exception 104 ");
        } catch (ProtocolException e) {
          //  System.out.println("start protocol exception 105");
            e.printStackTrace();
          //  System.out.println("end protocol exception 106 ");
        } catch (IOException e) {
          //  System.out.println(" start io exception 107");
            e.printStackTrace();
          //  System.out.println("end io exception 108");
        }

        if(json_string==null){
            json_string="";
        }

        return json_string;

    }
}


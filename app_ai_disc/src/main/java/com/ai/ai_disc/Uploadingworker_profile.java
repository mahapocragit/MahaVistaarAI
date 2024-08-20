package com.ai.ai_disc;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class Uploadingworker_profile extends Worker {

    private static final String TAG = "Uploadingworker_profile";
    Context context;
    final int chunkSize = 2048;

    public Uploadingworker_profile(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {


        String url = getInputData().getString("url");
        String id = getInputData().getString("id");
        if (getInputData().getString("code").matches("0")){
        return upload_image_data_synchronous(context, converting_string(context, url), id);}
        else{
            return upload_image_data_synchronous_farmer(context, converting_string(context, url), id);
        }


    }

    public String converting_string(Context ctx, String file_path) {

        String file_path_string = "";

        //System.out.println(" inside from converting is:" + Thread.currentThread().getName());
        if (!file_path.isEmpty()) {


            Uri address_to_convert = Uri.parse(file_path);
            //System.out.println("Uri is:" + address_to_convert);

            try {

                InputStream inputstream = null;
                inputstream = ctx.getContentResolver().openInputStream(address_to_convert);


                //System.out.println(" input stream is :");
                int length_image = 0;

                length_image = inputstream.available();
                //System.out.println("  length:" + length_image);
                byte[] data_in_byte_image = new byte[length_image];
                //System.out.println("  length of data in byte " + data_in_byte_image.length);
                //System.out.println(" data in byte " + data_in_byte_image.length);
                inputstream.read(data_in_byte_image);
                inputstream.close();
                file_path_string = "";
                file_path_string = Base64.encodeToString(data_in_byte_image, Base64.DEFAULT);
                //Log.d("filePathString", file_path_string);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Error in converting", Toast.LENGTH_LONG).show();
                file_path_string = "";
            }
        } else {

            file_path_string = "";
            System.out.println(" inside else ");
        }

        return file_path_string;
    }

    private void upload_image_data(Context ctx, String file_path_string, String record_id) {


       // Log.i(TAG, "inside from uploading is " + Thread.currentThread().getName());

        if (file_path_string.isEmpty()) {

            // Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
          //  Log.i(TAG, "upload_image_data: not uploaded ");
            // return Result.failure();
        }

        String image_proper = file_path_string.replaceAll("\n", "");
        JSONObject object = new JSONObject();
        try {
            object.put("record_id", record_id);
            object.put("image_path", image_proper);



/*
{
  "record_id": "sample string 1",
  "image_path": "sample string 2"
}
 */

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_image_data_profile")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(" response :" + response);
                        try {
                            boolean result = response.getBoolean("result");
                            String message = response.getString("message");
                            if (result) {

                              //  Log.i(TAG, "onResponse:  uploaded ");

                            } else {

                                // Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
                                // Result.failure();
                               // Log.i(TAG, "onResponse: not uploaded");
                                // upload.setEnabled(true);
                                // upload.setText("RETRY");
                                //   if(compress.getVisibility()==View.VISIBLE){

                                // }else{
                                // compress.setVisibility(View.);
                                // }


                                // show_dialog("Image is not added.");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            // Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
                            //upload.setEnabled(true);
                           // Log.i(TAG, "onResponse: not uploaded");
                            // upload.setText("RETRY");
                            //compress.setVisibility(View.VISIBLE);
                            // Result.failure();

                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        //  Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();

                       // Log.i(TAG, "onError: not uploaded");

                    }
                });


    }

    private Result upload_image_data_synchronous(Context ctx, String file_path_string, String user_name) {


      //  Log.i(TAG, "inside from uploading is " + Thread.currentThread().getName());

        if (file_path_string.isEmpty()) {

            // Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
           // Log.i(TAG, "upload_image_data: not uploaded ");
            return Result.failure();
        }

        String image_proper = file_path_string.replaceAll("\n", "");
        JSONObject object = new JSONObject();
        try {
            object.put("user_name", user_name);
            object.put("image_path", image_proper);




/*
{
  "record_id": "sample string 1",
  "image_path": "sample string 2"
}
 */

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ANRequest request = AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_image_data_profile")
                .addJSONObjectBody(object)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
           // Log.d(TAG, "response : " + jsonObject.toString());


            try {
                boolean result = jsonObject.getBoolean("result");
                String message = jsonObject.getString("message");

                if (result) {

                  //  Log.i(TAG, "onResponse:  uploaded ");
                    return Result.success();

                } else {

                  //  Log.i(TAG, "onResponse: not uploaded");
                    return Result.failure();

                }

            } catch (JSONException e) {
                e.printStackTrace();
                return Result.failure();
            }

        } else {
            ANError error = response.getError();
            // Handle Error
            return Result.failure();
        }


    }
    private Result upload_image_data_synchronous_farmer(Context ctx, String file_path_string, String user_name) {


        //Log.i(TAG, "inside from uploading is " + Thread.currentThread().getName());

        if (file_path_string.isEmpty()) {

            // Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
           // Log.i(TAG, "upload_image_data: not uploaded ");
            return Result.failure();
        }

        String image_proper = file_path_string.replaceAll("\n", "");
        JSONObject object = new JSONObject();
        try {
            object.put("user_name", user_name);
            object.put("image_path", image_proper);




/*
{
  "record_id": "sample string 1",
  "image_path": "sample string 2"
}
 */

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ANRequest request = AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_image_data_profile_farmer")
                .addJSONObjectBody(object)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
           // Log.d(TAG, "response : " + jsonObject.toString());


            try {
                boolean result = jsonObject.getBoolean("result");
                String message = jsonObject.getString("message");

                if (result) {

                   // Log.i(TAG, "onResponse:  uploaded ");
                    return Result.success();

                } else {

                   // Log.i(TAG, "onResponse: not uploaded");
                    return Result.failure();

                }

            } catch (JSONException e) {
                e.printStackTrace();
                return Result.failure();
            }

        } else {
            ANError error = response.getError();
            // Handle Error
            return Result.failure();
        }


    }

}

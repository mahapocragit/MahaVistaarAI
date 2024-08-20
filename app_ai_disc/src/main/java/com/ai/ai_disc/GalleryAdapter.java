package com.ai.ai_disc;



import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GalleryAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater inflater;

    ArrayList<Uri> mArrayUri;
    String file_path_string="";
    String [] mArray_file;
    public static final double SPACE_KB = 1024;
    public static final double SPACE_MB = 1024 * SPACE_KB;
    public static final double SPACE_GB = 1024 * SPACE_MB;
    private static final String TAG = "GalleryAdapter";

    public GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri) {

        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
        mArray_file= new String[mArrayUri.size()];


    }

    @Override
    public int getCount() {
        return mArrayUri.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayUri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.gv_item, parent, false);

        ImageView  ivGallery = (ImageView)itemView.findViewById(R.id.ivGallery);
        ImageView  compress_image = (ImageView)itemView.findViewById(R.id.compress_image);
        ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar);
        Button upload = (Button)itemView.findViewById(R.id.upload);
        Button compress = (Button)itemView.findViewById(R.id.compress);
        TextView text_size = (TextView) itemView.findViewById(R.id.image_size);
        TextView text_size_compressed = (TextView) itemView.findViewById(R.id.image_size_compressed);
        Button upload_compress_image = (Button)itemView.findViewById(R.id.upload_compress_image);

        upload_compress_image.setVisibility(View.GONE);

        compress_image.setVisibility(View.GONE);
        text_size_compressed.setVisibility(View.GONE);


        if( mArrayUri.get(position)!=null ){


            InputStream inputstream = null;
            try {
                inputstream = ctx.getContentResolver().openInputStream(mArrayUri.get(position));

                System.out.println(" input stream is :");
                int length_image = 0;

                length_image = inputstream.available();
                System.out.println(" Length :"+(length_image));
                inputstream.close();
              //  System.out.println(" size uncompressed:"+length_calculation(length_image));
               text_size.setText("SIZE : "+length_calculation(length_image));

            } catch (FileNotFoundException e) {
                e.printStackTrace();

                Toast.makeText(ctx,"Error 2",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ctx,"Error 1",Toast.LENGTH_LONG).show();
            }



        }
        Glide.with(ctx).load(mArrayUri.get(position)).into(ivGallery);

        progressBar.setVisibility(View.GONE);


        compress.setOnClickListener((View v)->{



            compress_image.setVisibility(View.VISIBLE);
            text_size_compressed.setVisibility(View.VISIBLE);
            compress.setEnabled(false);
            compress.setText("COMPRESSING");
            upload.setEnabled(false);



            try {
              File  compressedImageFile = new Compressor(ctx).compressToFile(FileUtils.from(ctx, mArrayUri.get(position)));
              mArray_file[position]=compressedImageFile.getAbsolutePath();


               // Log.i(TAG, "file is: "+compressedImageFile.getAbsolutePath());
              //  Log.i(TAG, "length file compressed is: "+length_calculation(get_file_length(compressedImageFile)));

                compress_image.setImageURI(Uri.fromFile(compressedImageFile));
                text_size_compressed.setText("COMPRESSED SIZE :"+length_calculation(get_file_length(compressedImageFile)));

            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(ctx,"Error ",Toast.LENGTH_LONG).show();
            }

            compress.setVisibility(View.GONE);
            upload_compress_image.setVisibility(View.VISIBLE);
            upload.setEnabled(true);


        });

        upload_compress_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload.setEnabled(false);
                upload_compress_image.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                /*

                Observable.fromCallable(() -> {

                    System.out.println(" inside from callable upload compress image  is:"+Thread.currentThread().getName());
                   converting_string(Uri.fromFile(new File(mArray_file[position])).toString());

                    System.out.println(" File name 1:"+mArray_file[position]);


                    upload_image_data(mArrayUri.get(position).toString(),Data1.record_id,upload,progressBar);
                    return false;
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result) -> {
                            //Use result for something
                            System.out.println(" result upload compress is:"+result);
                            System.out.println(" Thread upload compress is:"+Thread.currentThread().getName());



                        });

                 */


            }


        });



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                upload.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                compress.setVisibility(View.GONE);
                upload_compress_image.setVisibility(View.GONE);

                Observable.fromCallable(() -> {

                    System.out.println(" inside from callable is:"+Thread.currentThread().getName());
                    converting_string(mArrayUri.get(position).toString());

                    upload_image_data(mArrayUri.get(position).toString(),Data1.record_id,upload,progressBar);
                    return false;
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result) -> {
                            //Use result for something
                            System.out.println(" result is:"+result);
                            System.out.println(" Thread  is:"+Thread.currentThread().getName());



                        });




            }

        });

        return itemView;
    }



    public String length_calculation(int sizeInBytes){


        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);

        try {
            if ( sizeInBytes < SPACE_KB ) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if ( sizeInBytes < SPACE_MB ) {
                return nf.format(sizeInBytes/SPACE_KB) + " KB";
            } else if ( sizeInBytes < SPACE_GB ) {
                return nf.format(sizeInBytes/SPACE_MB) + " MB";
            } else {
                return nf.format(sizeInBytes) + " Byte(s)";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }
    }

    public int get_file_length(File file ){

        int length_image = 0;
        InputStream inputstream=null;
        try {
            inputstream = ctx.getContentResolver().openInputStream(Uri.fromFile(file));
            length_image = inputstream.available();
            inputstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  length_image;

    }





    private void upload_image_data(String address,String record_id,Button upload,ProgressBar progressBar) {

        System.out.println(" inside from uploading is:"+Thread.currentThread().getName());

       // converting_string(address);

        System.out.println(" address:"+address);

        if(file_path_string.isEmpty()){

           // progress.cancel();
           // show_dialog("Image is not added.");
            upload.setEnabled(true);
            upload.setText("RETRY");
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
            return;
        }

        String image_proper= file_path_string.replaceAll("\n","");
        JSONObject object = new JSONObject();
        try {
            object.put("record_id",record_id);
            object.put("image_path",image_proper);



/*
{
  "record_id": "sample string 1",
  "image_path": "sample string 2"
}
 */

        } catch (JSONException e) {
            e.printStackTrace();
        }

       // https://nibpp.krishimegh.in/Api/Upload/Add_image_data
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_image_data")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        System.out.print(" response :"+response);


                        progressBar.setVisibility(View.GONE);

                       // upload_compress_image.setVisibility(View.GONE);



                            /*
                            {
{
  "message": "sample string 1",
  "result": true
}
                             */


                        try {


                            boolean result = response.getBoolean("result");
                            String message=response.getString("message");



                            if(result){

                                Toast.makeText(ctx,"Uploaded.",Toast.LENGTH_LONG).show();

                                 upload.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct, 0, 0, 0);
                                 upload.setText("Uploaded");
                                 upload.setEnabled(false);
                                 //compress.setVisibility(View.GONE);
                                // upload_compressed_image.setVisibility(View.GONE);

                                // reset();



                            }else {

                                Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
                                upload.setEnabled(true);
                                upload.setText("RETRY");
                             //   if(compress.getVisibility()==View.VISIBLE){

                               // }else{
                                   // compress.setVisibility(View.);
                               // }


                               // show_dialog("Image is not added.");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();
                            upload.setEnabled(true);
                            upload.setText("RETRY");
                            //compress.setVisibility(View.VISIBLE);

                        }




                    }
                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(ctx,"Not Uploaded.",Toast.LENGTH_LONG).show();

                        upload.setEnabled(true);
                        upload.setText("RETRY");
                        progressBar.setVisibility(View.GONE);
                       // compress.setVisibility(View.VISIBLE);

                        }
                });






    }

    public void converting_string(String file_path){


        System.out.println(" inside from converting is:"+Thread.currentThread().getName());
        if(!file_path.isEmpty()){


            Uri address_to_convert=Uri.parse(file_path);
            System.out.println("Uri is:"+address_to_convert);

            try {

                InputStream inputstream=null;
                inputstream = ctx.getContentResolver().openInputStream(address_to_convert);



                System.out.println(" input stream is :");
                int length_image = 0;

                length_image = inputstream.available();
                System.out.println("  length:" + length_image);
                byte[] data_in_byte_image = new byte[length_image];
                System.out.println("  length of data in byte " + data_in_byte_image.length);
                System.out.println(" data in byte " + data_in_byte_image.length);
                inputstream.read(data_in_byte_image);
                inputstream.close();
                //System.out.println(" after inputstream close");
                file_path_string="";
                file_path_string = Base64.encodeToString(data_in_byte_image,Base64.DEFAULT);





            }catch(IOException e){
                e.printStackTrace();
                Toast.makeText(ctx,"Error in converting",Toast.LENGTH_LONG).show();
                file_path_string="";
            }
        }else{

            file_path_string="";
            System.out.println(" inside else ");
        }
    }


}
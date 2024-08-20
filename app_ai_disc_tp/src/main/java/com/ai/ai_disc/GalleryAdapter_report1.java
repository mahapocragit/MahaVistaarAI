package com.ai.ai_disc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;


public class GalleryAdapter_report1 extends RecyclerView.Adapter<GalleryAdapter_report1.MyViewHolder> {

    public static final double SPACE_KB = 1024;
    public static final double SPACE_MB = 1024 * SPACE_KB;
    public static final double SPACE_GB = 1024 * SPACE_MB;
    private static final String TAG = "GalleryAdapter1";
    Context ctx;
    ArrayList<Uri> mArrayUri;
    String file_path_string = "";
    String[] mArray_file;
    ArrayList<Uri> list_uploading;
    String[] output;
    WorkManager mWorkManager;
    String form_id;

    public GalleryAdapter_report1(Context ctx, ArrayList<Uri> mArrayUri, int form_id) {

        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
        mArray_file = new String[mArrayUri.size()];
        list_uploading = new ArrayList<Uri>();
        mWorkManager = WorkManager.getInstance();
        output = new String[mArrayUri.size()];
        this.form_id = String.valueOf(form_id);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gv_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setIsRecyclable(false);

        //Log.i(TAG, "onBindViewHolder: " + output[position]);
        if (output[position] == null) {
            output[position] = "not_started";
        }


        hide_visible(position, holder.ivGallery, holder.text_size,
                holder.compress_image, holder.text_size_compressed,
                holder.compress, holder.upload_compress_image,
                holder.upload, holder.progressBar);


        holder.compress.setOnClickListener((View v) -> {


            holder.compress_image.setVisibility(View.VISIBLE);
            holder.text_size_compressed.setVisibility(View.VISIBLE);
            holder.compress.setEnabled(false);
            holder.compress.setText("COMPRESSING");
            holder.upload.setEnabled(false);


            try {
                File compressedImageFile = new Compressor(ctx).compressToFile(FileUtils.from(ctx, mArrayUri.get(position)));
                mArray_file[position] = compressedImageFile.getAbsolutePath();

                holder.compress_image.setImageURI(Uri.fromFile(compressedImageFile));
                holder.text_size_compressed.setText("COMPRESSED SIZE :" + length_calculation(get_file_length(Uri.fromFile(compressedImageFile))));

            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(ctx, "Error ", Toast.LENGTH_LONG).show();
            }

            holder.compress.setVisibility(View.GONE);
            holder.upload_compress_image.setVisibility(View.VISIBLE);
            holder.upload.setEnabled(true);


        });

        holder.upload_compress_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Data data = new Data.Builder()
                        .putString("url", Uri.fromFile(new File(mArray_file[position])).toString())
                        .putString("id", form_id)
                        .build();

                OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(Uploadingworker_report.class)
                        .setInputData(data).build();


                output[position] = "uploading";

                hide_visible(position, holder.ivGallery, holder.text_size,
                        holder.compress_image, holder.text_size_compressed,
                        holder.compress, holder.upload_compress_image,
                        holder.upload, holder.progressBar);
                mWorkManager.enqueue(mRequest);

                LiveData<WorkInfo> liveData = mWorkManager.getWorkInfoByIdLiveData(mRequest.getId());

                liveData.observe((LifecycleOwner) ctx, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                            output[position] = "success";

                            hide_visible(position, holder.ivGallery, holder.text_size,
                                    holder.compress_image, holder.text_size_compressed,
                                    holder.compress, holder.upload_compress_image,
                                    holder.upload, holder.progressBar);
                        }
                        if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {

                            output[position] = "failed";

                            hide_visible(position, holder.ivGallery, holder.text_size,
                                    holder.compress_image, holder.text_size_compressed,
                                    holder.compress, holder.upload_compress_image,
                                    holder.upload, holder.progressBar);
                        }
                    }
                });


            }
        });


        holder.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Data data = new Data.Builder()
                        .putString("url", mArrayUri.get(position).toString())
                        .putString("id",form_id)
                        .build();

                OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(Uploadingworker_report.class)
                        .setInputData(data).build();

                output[position] = "uploading";

                hide_visible(position, holder.ivGallery, holder.text_size,
                        holder.compress_image, holder.text_size_compressed,
                        holder.compress, holder.upload_compress_image,
                        holder.upload, holder.progressBar);
                mWorkManager.enqueue(mRequest);

                mWorkManager.getWorkInfoByIdLiveData(mRequest.getId())
                        .observe((LifecycleOwner) ctx, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(@Nullable WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                    output[position] = "success";

                                    hide_visible(position, holder.ivGallery, holder.text_size,
                                            holder.compress_image, holder.text_size_compressed,
                                            holder.compress, holder.upload_compress_image,
                                            holder.upload, holder.progressBar);
                                }
                                if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {

                                    output[position] = "failed";

                                    hide_visible(position, holder.ivGallery, holder.text_size,
                                            holder.compress_image, holder.text_size_compressed,
                                            holder.compress, holder.upload_compress_image,
                                            holder.upload, holder.progressBar);
                                }
                            }
                        });

            }

        });

    }

    @Override
    public int getItemCount() {
        return mArrayUri.size();
    }

    public String length_calculation(int sizeInBytes) {


        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);

        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            } else {
                return nf.format(sizeInBytes) + " Byte(s)";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }
    }

    public int get_file_length(Uri file) {

        int length_image = 0;
        InputStream inputstream = null;
        try {
            inputstream = ctx.getContentResolver().openInputStream(file);
            length_image = inputstream.available();
            inputstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return length_image;

    }

    public void hide_visible(int position, ImageView ivGallery, TextView text_size,
                             ImageView compress_image, TextView text_size_compressed,
                             Button compress, Button upload_compress_image,
                             Button upload, ProgressBar progressBar) {

        if (output[position].equals("not_started")) {

            ivGallery.setVisibility(View.VISIBLE);
            text_size.setVisibility(View.VISIBLE);


            text_size.setText("SIZE : " + length_calculation(get_file_length(mArrayUri.get(position))));
            Glide.with(ctx).load(mArrayUri.get(position)).into(ivGallery);

            if (mArray_file[position] == null) {

                compress_image.setVisibility(View.GONE);
                text_size_compressed.setVisibility(View.GONE);
                compress.setVisibility(View.VISIBLE);
                upload_compress_image.setVisibility(View.GONE);

            } else {

                compress_image.setVisibility(View.VISIBLE);
                text_size_compressed.setVisibility(View.VISIBLE);
                compress_image.setImageURI(Uri.fromFile(new File(mArray_file[position])));
                text_size_compressed.setText("COMPRESSED SIZE :" + length_calculation(get_file_length(Uri.fromFile(new File(mArray_file[position])))));

                compress.setVisibility(View.GONE);
                upload_compress_image.setVisibility(View.VISIBLE);
            }

            upload.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }

        if (output[position].equals("uploading")) {


            ivGallery.setVisibility(View.VISIBLE);
            text_size.setVisibility(View.VISIBLE);

            text_size.setText("SIZE : " + length_calculation(get_file_length(mArrayUri.get(position))));
            Glide.with(ctx).load(mArrayUri.get(position)).into(ivGallery);

            if (mArray_file[position] == null) {

                compress_image.setVisibility(View.GONE);
                text_size_compressed.setVisibility(View.GONE);


            } else {

                compress_image.setVisibility(View.VISIBLE);
                text_size_compressed.setVisibility(View.VISIBLE);
                compress_image.setImageURI(Uri.fromFile(new File(mArray_file[position])));
                text_size_compressed.setText("COMPRESSED SIZE :" + length_calculation(get_file_length(Uri.fromFile(new File(mArray_file[position])))));


            }
            compress.setVisibility(View.GONE);
            upload_compress_image.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        }
        if (output[position].equals("success")) {


            ivGallery.setVisibility(View.VISIBLE);
            text_size.setVisibility(View.VISIBLE);

            text_size.setText("SIZE : " + length_calculation(get_file_length(mArrayUri.get(position))));
            Glide.with(ctx).load(mArrayUri.get(position)).into(ivGallery);

            if (mArray_file[position] == null) {
                compress_image.setVisibility(View.GONE);
                text_size_compressed.setVisibility(View.GONE);


            } else {

                compress_image.setVisibility(View.VISIBLE);
                text_size_compressed.setVisibility(View.VISIBLE);
                compress_image.setImageURI(Uri.fromFile(new File(mArray_file[position])));
                text_size_compressed.setText("COMPRESSED SIZE :" + length_calculation(get_file_length(Uri.fromFile(new File(mArray_file[position])))));


            }

            upload.setVisibility(View.VISIBLE);
            upload.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct, 0, 0, 0);
            upload.setText("Uploaded");
            upload.setEnabled(false);
            progressBar.setVisibility(View.GONE);
            compress.setVisibility(View.GONE);
            upload_compress_image.setVisibility(View.GONE);


        }

        if (output[position].equals("failed")) {


            ivGallery.setVisibility(View.VISIBLE);
            text_size.setVisibility(View.VISIBLE);

            text_size.setText("SIZE : " + length_calculation(get_file_length(mArrayUri.get(position))));
            Glide.with(ctx).load(mArrayUri.get(position)).into(ivGallery);

            if (mArray_file[position] == null) {

                compress_image.setVisibility(View.GONE);
                text_size_compressed.setVisibility(View.GONE);
                compress.setVisibility(View.VISIBLE);
                upload_compress_image.setVisibility(View.GONE);

            } else {

                compress_image.setVisibility(View.VISIBLE);
                text_size_compressed.setVisibility(View.VISIBLE);
                compress_image.setImageURI(Uri.fromFile(new File(mArray_file[position])));
                text_size_compressed.setText("COMPRESSED SIZE :" + length_calculation(get_file_length(Uri.fromFile(new File(mArray_file[position])))));

                compress.setVisibility(View.GONE);
                upload_compress_image.setVisibility(View.VISIBLE);
            }

            upload.setVisibility(View.VISIBLE);
            upload.setText("RETRY");
            progressBar.setVisibility(View.GONE);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivGallery;
        ImageView compress_image;
        ProgressBar progressBar;
        Button upload;
        Button compress;
        TextView text_size;
        TextView text_size_compressed;
        Button upload_compress_image;

        public MyViewHolder(View view) {
            super(view);

            ivGallery = (ImageView) view.findViewById(R.id.ivGallery);
            compress_image = (ImageView) view.findViewById(R.id.compress_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            upload = (Button) itemView.findViewById(R.id.upload);
            compress = (Button) itemView.findViewById(R.id.compress);
            text_size = (TextView) itemView.findViewById(R.id.image_size);
            text_size_compressed = (TextView) itemView.findViewById(R.id.image_size_compressed);
            upload_compress_image = (Button) itemView.findViewById(R.id.upload_compress_image);


        }
    }


}
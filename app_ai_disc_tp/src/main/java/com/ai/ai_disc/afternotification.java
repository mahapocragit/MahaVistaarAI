package com.ai.ai_disc;


import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class afternotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afternotification);
        Button bv=findViewById(R.id.bv);

        bv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imagesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString() + File.separator + "ai_disc_";
                Uri uri = Uri.parse(imagesDir);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(uri, "*/*");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent nb= new Intent(afternotification.this,history.class);
        startActivity(nb);
        finish();
    }
}
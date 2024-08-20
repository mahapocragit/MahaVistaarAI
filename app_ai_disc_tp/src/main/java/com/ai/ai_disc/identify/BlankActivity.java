package com.ai.ai_disc.identify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ai.ai_disc.R;
import com.ai.ai_disc.farmersprofile_fragment1;

public class BlankActivity extends AppCompatActivity {
    Button aidisc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_activity);
        aidisc = findViewById(R.id.aidisc);
        aidisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(getApplicationContext(), farmersprofile_fragment1.class);
                Intent i = new Intent(getApplicationContext(), Identify_dashboard.class);
                startActivity(i);
            }
        });
    }
}

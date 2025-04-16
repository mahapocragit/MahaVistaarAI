package in.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import in.gov.mahapocra.farmerapp.R;
import in.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify.Identify.Identify_dashboard;


public class NetNotWorking extends AppCompatActivity {

    Button retry;
    String status_of_internet="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_not_working);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");

        retry=(Button)findViewById(R.id.retry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                status_of_internet= NetStatusCheck.get_connectivity_status(NetNotWorking.this);

                if(status_of_internet.equals("not_connected")){
                    Toast.makeText(getApplicationContext(),"Internet not connected.",Toast.LENGTH_LONG).show();
                }else{

                    Intent intent = new Intent(getApplicationContext(), Identify_dashboard.class);
                    startActivity(intent);

                    finish();
                    Toast.makeText(getApplicationContext(),"Internet Connected.",Toast.LENGTH_LONG).show();

                }
            }
        });


    }
}
package com.ai.ai_disc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Admin_profile extends AppCompatActivity {

    ArrayList<String> web;
    ArrayList<Integer> imageId ;


    @BindView(R.id.gridc)
    GridView grid;

    InternetReceiver internet ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_admin_profile);
        TextView farm=findViewById(R.id.user_farm);
        TextView expe=findViewById(R.id.user_expert);

        TextView idn=findViewById(R.id.idn);
        TextView chat=findViewById(R.id.chat);

        TextView rpt=findViewById(R.id.rpt);
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getadmindata")
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    farm.setText("Total Farmer = "+response.getString("farmer"));
                    expe.setText("Total Expert = "+response.getString("expert"));
                    idn.setText("Total Identification = "+response.getString("idn"));
                    chat.setText("Total Query Chat = "+response.getString("chat"));
                    rpt.setText("Total Reports = "+response.getString("rpt"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });




        ButterKnife.bind(this);

        internet = new InternetReceiver();
        web=new ArrayList<String>();
        imageId=new ArrayList<Integer>();






        web.add("Expert Settings");
        web.add("Add token to users");
        web.add("Send Notification");
        web.add("Activity Check");
        web.add("See All Reports");
        web.add("See All Chats");
        web.add("See All Identification");
        imageId.add(R.drawable.expert);
        imageId.add(R.drawable.done);
        imageId.add(R.drawable.send);
        imageId.add(R.drawable.view_record);
        imageId.add(R.drawable.dashboard);
        imageId.add(R.drawable.dashboard);
        imageId.add(R.drawable.dashboard);
        custom_grid_adapter1 adapter1 = new custom_grid_adapter1(Admin_profile.this, web, imageId);
        grid.setAdapter(adapter1);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if(position==0){

                    Intent intent6 = new Intent(Admin_profile.this, expert_setting.class);
                    startActivity(intent6);

                }if(position==1){

                    Intent intent6 = new Intent(Admin_profile.this, add_token.class);
                    startActivity(intent6);

                }
                if(position==2){

                    Intent intent6 = new Intent(Admin_profile.this, sendnotification.class);
                    startActivity(intent6);

                }
                if(position==3){

                    Intent intent6 = new Intent(Admin_profile.this, alarmservice.class);



                    startActivity(intent6);

                }
                if(position==4){

                    Intent intent6 = new Intent(Admin_profile.this, admin_reports.class);



                    startActivity(intent6);

                }
                if(position==5){

                    Intent intent6 = new Intent(Admin_profile.this, admin_chat.class);



                    startActivity(intent6);

                }
                if(position==6){

                    Intent intent6 = new Intent(Admin_profile.this, admin_identify.class);



                    startActivity(intent6);

                }

            }
        });


    }

        public  class gettoken extends AsyncTask<String,String,String>{
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }
}

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Admin_profile.this);
        alertDialogBuilder.setTitle("Exit");
        alertDialogBuilder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {//yes
                    public void onClick(final DialogInterface dialog, int id) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent a = new Intent(Intent.ACTION_MAIN);
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);

                                dialog.cancel();
                            }
                        }, 10);

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {//no
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflator= getMenuInflater();
        inflator.inflate(R.menu.menu1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.logout:

                AlertDialog.Builder opt = new AlertDialog.Builder(Admin_profile.this);
                opt.setTitle("Are you sure ?");
                opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shared_pref.remove_shared_preference(Admin_profile.this);
                        Intent intent = new Intent(Admin_profile.this, Login.class);
                        startActivity(intent);
                        finish();



                    }
                });
                opt.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                opt.show();

                break;
            default:
                break;

        }

        return true;
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

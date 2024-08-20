package com.ai.ai_disc;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Disease_list extends AppCompatActivity {


    Button add_disease;
    ListView list_disease;
    Spinner crop_list_spinner;

    ArrayList<String> crop_array;
    ArrayList<String> crop_id_array;
    ArrayList<String> disease_array;
    ArrayList<String> disease_id_array;
    EditText disease_name_add, local_disease_name, scientific_disease_name;
    ArrayAdapter adapter;
    ArrayAdapter list_adapter;
    Button add;

    TextView no_disease_record, list_disease_text;

    ListView list;
    String selected_crop_id = "";

    BottomSheetDialog dialog;
    InternetReceiver internet = new InternetReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Add Disease");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list_disease = (ListView) findViewById(R.id.list);
        crop_list_spinner = (Spinner) findViewById(R.id.crop_list);

        list = (ListView) findViewById(R.id.list);
        add = (Button) findViewById(R.id.add);
        no_disease_record = (TextView) findViewById(R.id.no_disease);
        list_disease_text = (TextView) findViewById(R.id.list_disease);


        no_disease_record.setVisibility(View.GONE);
        list_disease_text.setVisibility(View.GONE);

//        add_disease.setVisibility(View.GONE);
        //   disease_name_add.setVisibility(View.GONE);
        //   local_disease_name.setVisibility(View.GONE);
        //   scientific_disease_name.setVisibility(View.GONE);

        getWindow().setSoftInputMode(1);

        crop_array = new ArrayList<String>();
        crop_array.add("select crop");

        crop_id_array = new ArrayList<String>();
        crop_id_array.add("");

        disease_array = new ArrayList<String>();
        disease_id_array = new ArrayList<String>();


        adapter = new ArrayAdapter(Disease_list.this, android.R.layout.simple_list_item_1, crop_array);
        crop_list_spinner.setAdapter(adapter);


        list_adapter = new ArrayAdapter(Disease_list.this, android.R.layout.simple_list_item_1, disease_array);
        list.setAdapter(list_adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  add_disease.setVisibility(View.VISIBLE);
                // disease_name_add.setVisibility(View.VISIBLE);
                // local_disease_name.setVisibility(View.VISIBLE);
                // scientific_disease_name.setVisibility(View.VISIBLE);

                View view = getLayoutInflater().inflate(R.layout.add_disease_bottomsheet, null);

                dialog = new BottomSheetDialog(Disease_list.this);
                dialog.setContentView(view);
                dialog.show();

                disease_name_add = (EditText) dialog.findViewById(R.id.name_disease);
                add_disease = (Button) dialog.findViewById(R.id.add_disease);
                local_disease_name = (EditText) dialog.findViewById(R.id.local_name_disease);
                scientific_disease_name = (EditText) dialog.findViewById(R.id.scientific_name_disease);

                add_disease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validation()) {
                            adding_disease(disease_name_add.getText().toString(), selected_crop_id, local_disease_name.getText().toString(), scientific_disease_name.getText().toString());
                        }
                    }
                });
            }
        });

        /*

        add_disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(validation()){
                    adding_disease(disease_name_add.getText().toString(),selected_crop_id,local_disease_name.getText().toString(),scientific_disease_name.getText().toString());
                }

            }
        });

        */

        crop_list_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_crop_id = crop_id_array.get(position);

                if (!selected_crop_id.isEmpty()) {

                    getting_list_disease(selected_crop_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getting_list_crop();
    }

    public boolean validation() {


        if (disease_name_add.getText().toString().isEmpty()) {

            Toast.makeText(Disease_list.this, "Disease name is empty.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (selected_crop_id.isEmpty()) {


            Toast.makeText(Disease_list.this, "Crop is not selected.", Toast.LENGTH_LONG).show();
            return false;
        }
        /*
        if(local_disease_name.getText().toString().isEmpty()){


            Toast.makeText(Disease_list.this,"Local disease name is empty.",Toast.LENGTH_LONG).show();
            return  false;
        }

        */
        if (scientific_disease_name.getText().toString().isEmpty()) {


            Toast.makeText(Disease_list.this, "Scientific name is empty.", Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    public void adding_disease(String disease_name, String crop_id, String local_name, String scientific_name) {

        /*
        {
  "disease_name": "sample string 1",
  "crop_id": "sample string 2"
}
         */

        JSONObject object = new JSONObject();
        try {
            object.put("disease_name", disease_name);
            object.put("local_name", local_name);
            object.put("scientific_name", scientific_name);
            object.put("crop_id", crop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Api/Upload/Add_disease
        // https://dbtdare.icar.gov.in/android/Api/Students/Add_disease
        //http://192.168.183.165/API/Api/Upload/Add_disease

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Add_disease")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {


                            String message = response.getString("message");
                            boolean result = response.getBoolean("result");


                            if (result) {

                                Toast.makeText(Disease_list.this, "Disease is added", Toast.LENGTH_SHORT).show();

                                // add_disease.setVisibility(View.GONE);
                                // disease_name_add.setVisibility(View.GONE);
                                // local_disease_name.setVisibility(View.GONE);
                                // scientific_disease_name.setVisibility(View.GONE);

                                dialog.cancel();
                                disease_name_add.setText("");
                                local_disease_name.setText("");
                                scientific_disease_name.setText("");
                                getting_list_disease(selected_crop_id);


                            } else {
                                Toast.makeText(Disease_list.this, "Disease is not added", Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(Disease_list.this, "There is error in adding disease.", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void getting_list_crop() {


        // Api/Upload/list_crop
        // https://dbtdare.icar.gov.in/android/Api/Students/list_crop
        //http://192.168.183.165/API/Api/Upload/list_crop

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/list_crop")

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


/*
{
  "diseasereport_list": [
    {
      "id": "sample string 1",
      "name": "sample string 2"
    },
    {
      "id": "sample string 1",
      "name": "sample string 2"
    }
  ]
}
 */

                        crop_array.clear();
                        crop_id_array.clear();
                        crop_array.add(0, "select crop");
                        crop_id_array.add(0, "");
                        try {


                            JSONArray array = response.getJSONArray("cropreport_list");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);

                                String id = object.getString("id");
                                String name = object.getString("name");
                                crop_array.add(i + 1, name);
                                crop_id_array.add(i + 1, id);
                            }

                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(Disease_list.this, "There is error in list of crop.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getting_list_disease(String crop_id) {


        list_disease_text.setVisibility(View.GONE);
        no_disease_record.setVisibility(View.GONE);
        /*
        {
  "crop_id": "sample string 1"
}
         */
        JSONObject object = new JSONObject();
        try {
            object.put("crop_id", crop_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(" object sending:" + object);


        // Api/Upload/list_disease
        // https://dbtdare.icar.gov.in/android/Api/Students/list_disease
        //http://192.168.183.165/API/Api/Upload/list_disease

        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/list_disease")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        System.out.println(" response in list of disease:" + response);
/*
{
  "diseasereport_list": [
    {
      "id": "sample string 1",
      "name": "sample string 2"
    },
    {
      "id": "sample string 1",
      "name": "sample string 2"
    }
  ]
}
 */

                        disease_id_array.clear();
                        disease_array.clear();

                        try {


                            JSONArray array = response.getJSONArray("diseasereport_list");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);

                                String id = object.getString("id");
                                String name = object.getString("name");
                                disease_array.add(name);
                                disease_id_array.add(id);
                            }

                            if (disease_array.size() == 0) {

                                list_disease_text.setVisibility(View.GONE);
                                no_disease_record.setVisibility(View.VISIBLE);

                            } else {
                                list_disease_text.setVisibility(View.VISIBLE);
                                no_disease_record.setVisibility(View.GONE);
                                list_adapter.notifyDataSetChanged();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(Disease_list.this, "There is error in list of disease.", Toast.LENGTH_LONG).show();
                    }
                });
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                Intent intent = new Intent(Disease_list.this, Login.class);
                startActivity(intent);

                shared_pref.remove_shared_preference(Disease_list.this);

                break;
            default:
                break;

        }

        return true;
    }

 */

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case android.R.id.home:


                finish();

                break;
            default:
                break;

        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internet, intentFilter);

        // InternetReceiver internet = new InternetReceiver();


    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(internet);

    }

}

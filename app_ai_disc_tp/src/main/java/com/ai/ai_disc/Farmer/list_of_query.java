package com.ai.ai_disc.Farmer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.Login;
import com.ai.ai_disc.MultiChat_FarmerExpertQuery_Activity;
import com.ai.ai_disc.R;
import com.ai.ai_disc.farmersprofile_fragment1;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import com.ai.ai_disc.shared_pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class list_of_query extends AppCompatActivity
{
    String cropId="",cropName="";
    Button newQueryButton;
    String userId="";
    ListView list_query;
    TextView centeredText;
    ArrayList<ArrayList<String>> full_list_query;
    RelativeLayout centeredRelativeLayout;
    ArrayList<String> query_details;
    TextView list_of_query_text_view;
    List<QueryDetails1> queryDetailsList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_query);
        newQueryButton=(Button)findViewById(R.id.new_query_button);
        centeredRelativeLayout = (RelativeLayout) findViewById(R.id.centeredRelative);
        centeredText = (TextView) findViewById(R.id.centeredText);
        list_of_query_text_view = (TextView) findViewById(R.id.textView4);
        query_details=new ArrayList<>();
        queryDetailsList=new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        full_list_query = new ArrayList<ArrayList<String>>();
        list_query = (ListView) findViewById(R.id.list_view);

        Intent intent = getIntent();
        RelativeLayout centeredRelativeLayout;
        Bundle data = intent.getExtras();
       /* cropId=data.getString("cropID");
        cropName=data.getString("cropName");*/
        userId= user_singleton.getInstance().getUser_id();

        get_list_query();

        list_query.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {


                //ArrayList<String> selected_query = queryDetailsList.get(position);
                Intent intent1 = new Intent(list_of_query.this, MultiChat_FarmerExpertQuery_Activity.class);
                Bundle args = new Bundle();
                args.putString("desc",queryDetailsList.get(position).getDescription());
                args.putString("solution",queryDetailsList.get(position).getQuery_resolution());
                args.putString("imagePath",queryDetailsList.get(position).getImagePath());
                args.putString("farmerId",queryDetailsList.get(position).getUser_id());
                args.putString("expertId",queryDetailsList.get(position).getExpertId());
                args.putString("queryId",queryDetailsList.get(position).getQuery_id());
                args.putString("cropId",queryDetailsList.get(position).getCropId());
                args.putString("userType",queryDetailsList.get(position).getUserType());
                args.putString("farmerName",queryDetailsList.get(position).getFirstName());
                args.putString("crop",queryDetailsList.get(position).getcrop());
                    args.putString("status_run",queryDetailsList.get(position).getstatus_run());



                intent1.putExtra("bundle",args);
                startActivity(intent1);

            /*    Intent intent1 = new Intent(list_of_query.this, Selected_query_details1.class);
                Bundle args = new Bundle();
                args.putString("desc",queryDetailsList.get(position).getDescription());
                args.putString("solution",queryDetailsList.get(position).getQuery_resolution());
                args.putString("imagePath",queryDetailsList.get(position).getImagePath());
                intent1.putExtra("bundle",args);
                startActivity(intent1);*/

               /*// args.putParcelable("QuestionListExtra", (Parcelable) queryDetailsList);
                //args.putSerializable("queryList",(Serializable)queryDetailsList);
                args.putString("position", String.valueOf(position));*/


                //intent1.putExtra("details", queryDetailsList.get(position));


            }
        });

        newQueryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               // Toast.makeText(list_of_query.this,cropId,Toast.LENGTH_LONG).show();
              //
                Intent intent1=new Intent(list_of_query.this, send_query2_layout.class);
                Bundle data1 = new Bundle();
               /* data1.putString("cropID",cropId);
                data1.putString("cropName",cropName);*/
                intent1.putExtras(data1);
                startActivity(intent1);
            }
        });



    }


    public void get_list_query()
    {
        //newly added token
        String token_key = "PMAK-646d993c4ae18d7b534b6aad-afe1811f3b9c9feff73dc2633c1410b885";
        //AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/query_user_for_farmer11?user_id=" + userId)
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/query_user_for_farmer11?user_id=" + userId)
                .addHeaders("ranjan_api", token_key)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        full_list_query.clear();
                        query_details.clear();
                        queryDetailsList.clear();
                        try
                        {
                            for (int i = 0; i < response.length(); i++)
                            {
                                QueryDetails1 queryDetails=new QueryDetails1();
                                JSONObject object = (JSONObject) response.get(i);
                                String query_id = object.optString("Query_id");
                                String query_resolution = object.optString("Query_resolution");
                                String queryStatus = object.optString("Query_status");
                                String expertId = object.optString("expert_id");
                                String imagePath= object.optString("image_1");
                                String language = object.optString("language");
                                String description = object.optString("desc");
                                String user_id = object.optString("user_id");
                                String firstName=object.optString("firstName");
                                String userType=object.optString("userType");
                                String cropId=object.optString("crop_id");
                                String status_run=object.optString("status_run");
                              //  list_query1.add(description);
                                queryDetails.setQuery_id(query_id);
                                queryDetails.setQuery_resolution(query_resolution);
                                queryDetails.setQueryStatus(queryStatus);
                                queryDetails.setExpertId(expertId);
                                queryDetails.setImagePath(imagePath);
                                queryDetails.setLanguage(language);
                                queryDetails.setDescription(description);
                                queryDetails.setUser_id(user_id);
                                queryDetails.setFirstName(firstName);
                                queryDetails.setUserType(userType);
                                queryDetails.setCropId(cropId);
                                queryDetails.setstatus_run(status_run);
                                queryDetails.setcrop(object.optString("crop_name"));
                                queryDetailsList.add(queryDetails);

                               // query_id_list.add(i, query_id);


                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (queryDetailsList.size() == 0)
                        {

                            list_query.setVisibility(View.GONE);
                            centeredRelativeLayout.setVisibility(View.VISIBLE);
                            centeredText.setVisibility(View.VISIBLE);
                            centeredText.setText("No query found");
                            list_of_query_text_view.setVisibility(View.GONE);


                        } else {

                            list_query.setAdapter(new custom_adapter_class5(list_of_query.this, queryDetailsList));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(list_of_query.this,"Error" , Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {



            case android.R.id.home:

                finish();

                break;
            case R.id.logout:

                signout();

                break;
            default:
                break;

        }

        return true;
    }
    private void signout() {
        AlertDialog.Builder opt = new AlertDialog.Builder(list_of_query.this);
        opt.setTitle("Are you sure ?");
        opt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shared_pref.remove_shared_preference(list_of_query.this);
                Intent intent = new Intent(list_of_query.this, Login.class);
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
    }

}
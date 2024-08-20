package com.ai.ai_disc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ai.ai_disc.Farmer.Farmer_SendQueryDashboard;
import com.ai.ai_disc.Farmer.QueryDetails1;
import com.ai.ai_disc.Farmer.custom_adapter_class5;
import com.ai.ai_disc.Farmer.head_query_list;
import com.ai.ai_disc.Farmer.list_of_query;
import com.ai.ai_disc.Farmer.send_query2_layout;
import com.ai.ai_disc.model.report_adapter18;
import com.ai.ai_disc.model.reportdata;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_expert#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_expert extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //ArrayList<String> query_details;
    //TextView list_of_query_text_view;
    List<QueryDetails1> queryDetailsList;
    List<QueryDetails1> extrasearch;
    GridView grid;
    LinearLayout bline,s_lay;
    ArrayList<String> web;
    ArrayList<Integer> imageId ;
    TextView farmerquerylist;
    boolean onoff;
    View views;
    int s;
    int tool;
    ImageView srch,cancel,searchop;
    EditText stext;
    InternetReceiver internet ;
    public Fragment_expert() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment_mod_dis.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_expert newInstance(String param1, String param2) {
        Fragment_expert fragment = new Fragment_expert();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            //Toast.makeText(getContext(), mParam1, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_expert, container, false);
         farmerquerylist=view.findViewById(R.id.textView15);
        grid=view.findViewById(R.id.grid);
        bline=view.findViewById(R.id.bline);
        stext=view.findViewById(R.id.svt);
        srch=view.findViewById(R.id.dots);
        cancel=view.findViewById(R.id.reset);
        cancel.setVisibility(View.GONE);
        s_lay=view.findViewById(R.id.s_lay);
        searchop=view.findViewById(R.id.search);
        s_lay.setVisibility(View.GONE);
        tool=0;
        SwipeRefreshLayout lay= view.findViewById(R.id.refr);
        lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshdata();
                lay.setRefreshing(false);
            }
        });

        srch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!stext.getText().toString().matches("")){
                    searchgo(stext.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Enter something", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setAdapter(new custom_adapter_class5(getContext(), queryDetailsList));
                //search.setText("GO");
                stext.getText().clear();
                cancel.setVisibility(View.GONE);
            }
        });
        searchop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tool==1){
//                    imageView6.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.ic_menu_search, getContext().getTheme()));
                    //grid.setAdapter(ca);
                    tool=0;
                    s_lay.setVisibility(View.GONE);

                    //sr.setVisibility(View.GONE);
                }else{
                    s_lay.setVisibility(View.VISIBLE);
                    tool=1;
                    //sr.setVisibility(View.VISIBLE);
                }
            }
        });

        s=0;
        queryDetailsList=new ArrayList<>();
        extrasearch=new ArrayList<>();
        get_list_query();

        onoff=false;
        ImageView slide=view.findViewById(R.id.dots);
        views=view.findViewById(R.id.viewslide);
        //views.setVisibility(View.GONE);

        LinearLayout cardcall=view.findViewById(R.id.cardcall);
        //cardcall.setVisibility(View.GONE);

//        slide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view1) {
//                if(!onoff){
//                    views.setVisibility(View.VISIBLE);
////                    TranslateAnimation animate = new TranslateAnimation(
////                            +views.getWidth()+100,
////                            0 ,
////                            0,
////                            0);
////                    animate.setDuration(500);
////                    animate.setFillAfter(true);
////                    views.startAnimation(animate);
//
//                    slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dropdown2, getContext().getTheme()));
//                } else {
//                    //view.setVisibility(View.INVISIBLE);
////                    TranslateAnimation animate = new TranslateAnimation(
////                            0,
////                            +views.getWidth()+100,
////                            0,
////                            0);
////                    animate.setDuration(500);
////                    animate.setFillAfter(true);
////                    views.startAnimation(animate);
//                    views.setVisibility(View.GONE);
//                    slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
//                    //view.setVisibility(View.GONE);
//                }
//                onoff = !onoff;
//            }
//        });

//        farmerquerylist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent ibt=new Intent(getContext(), list_of_query.class);
//                startActivity(ibt);
//            }
//        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(getContext(), MultiChat_FarmerExpertQuery_Activity.class);
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
                chat_singleton.getInstance().setchat(queryDetailsList.get(position));
                startActivity(intent1);
            }
        });
        ImageView add=view.findViewById(R.id.create);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onoff=false;
//                TranslateAnimation animate = new TranslateAnimation(
//                        0,
//                        +views.getWidth()+100,
//                        0,
//                        0);
//                animate.setDuration(500);
//                animate.setFillAfter(true);
//                views.startAnimation(animate);
                //views.setVisibility(View.GONE);
                //slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));

                Intent intent1=new Intent(getContext(), send_query2_layout.class);
                Bundle data1 = new Bundle();
               /* data1.putString("cropID",cropId);
                data1.putString("cropName",cropName);*/
                intent1.putExtras(data1);
                startActivity(intent1);
            }
        });
        ImageView sear=view.findViewById(R.id.search);
        sear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onoff=false;
//                TranslateAnimation animate = new TranslateAnimation(
//                        0,
//                        +views.getWidth()+100,
//                        0,
//                        0);
//                animate.setDuration(500);
//                animate.setFillAfter(true);
//                views.startAnimation(animate);
                //views.setVisibility(View.GONE);
                //slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));

                if (s==0){
                    s_lay.setVisibility(View.VISIBLE);
                    s=1;
                }else {
                    s_lay.setVisibility(View.GONE);
                    s=0;
                }
            }
        });
        ImageView ref=view.findViewById(R.id.refresh);
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onoff=false;
//                TranslateAnimation animate = new TranslateAnimation(
//                        0,
//                        +views.getWidth()+100,
//                        0,
//                        0);
//                animate.setDuration(500);
//                animate.setFillAfter(true);
//                views.startAnimation(animate);
                //views.setVisibility(View.GONE);
                //slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
                get_list_query();
            }
        });
        ImageView call=view.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),farmer_appointment.class));
            }
        });

        return view;
    }

    private void refreshdata() {
        get_list_query();
    }

    private void searchgo(String value) {
        extrasearch.clear();
        for(int s=0;s< queryDetailsList.size();s+=1){
            QueryDetails1 ad=queryDetailsList.get(s);

            if (ad.getcrop().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getDescription().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getQuery_resolution().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") ){
                extrasearch.add(ad);
            }
        }
        if (extrasearch.size()!=0) {
            //datare = new custom_adapter_class5(getContext(), extrasearch);
            grid.setAdapter(new custom_adapter_class5(getContext(), extrasearch));
            //search.setText("GO");
            cancel.setVisibility(View.VISIBLE);

            Toast.makeText(getContext(), "Showing "+String.valueOf(extrasearch.size())+" Results", Toast.LENGTH_SHORT).show();
            //imageView6.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_backspace_24, getContext().getTheme()));
        }
        else{
            //search.setText("GO");
            cancel.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Nothing Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void get_list_query()
    {

        //newly added token
        String token_key = "PMAK-646d993c4ae18d7b534b6aad-afe1811f3b9c9feff73dc2633c1410b885";
        //AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/query_user_for_farmer11?user_id=" + user_singleton.getInstance().getUser_id())
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/query_user_for_farmer11?user_id=" + user_singleton.getInstance().getUser_id())
                .addHeaders("ranjan_api", token_key)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

//                        full_list_query.clear();
//                        query_details.clear();
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
                                queryDetails.settim(object.optString("times"));
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
                            farmerquerylist.setVisibility(View.VISIBLE);
                            grid.setVisibility(View.GONE);
                            bline.setVisibility(View.GONE);


//                            list_query.setVisibility(View.GONE);
//                            centeredRelativeLayout.setVisibility(View.VISIBLE);
//                            centeredText.setVisibility(View.VISIBLE);
//                            centeredText.setText("No query found");
//                            list_of_query_text_view.setVisibility(View.GONE);


                        } else {
                            farmerquerylist.setVisibility(View.GONE);

                            grid.setAdapter(new custom_adapter_class5(getContext(), queryDetailsList));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(getContext(),"Error" , Toast.LENGTH_LONG).show();
                    }
                });
    }

}
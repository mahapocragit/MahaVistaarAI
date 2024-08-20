package com.ai.ai_disc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import com.ai.ai_disc.Farmer.Model_expert_query_content;
import com.ai.ai_disc.Farmer.QueryDetails1;
import com.ai.ai_disc.Farmer.custom_adapter_class5;
import com.ai.ai_disc.Farmer.custom_adapter_query_answer;
import com.ai.ai_disc.Farmer.head_query_list;
import com.ai.ai_disc.Farmer.send_query2_layout;
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
 * Use the {@link Fragment_expertise#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_expertise extends Fragment {

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
    GridView grid;
    LinearLayout bline,s_lay;
    TextView vb;
    boolean onoff;
    View views;
    ArrayList<String> web;
    ArrayList<Integer> imageId ;
    TextView farmerquerylist;
    List<Model_expert_query_content> expertQueryList;
    custom_adapter_query_answer custom_adapter_query_answer1;
    InternetReceiver internet ;
    ImageView srch,cancel,searchop;
    EditText stext;
    List<QueryDetails1> extrasearch;
    int tool;
    public Fragment_expertise() {
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
    public static Fragment_expertise newInstance(String param1, String param2) {
        Fragment_expertise fragment = new Fragment_expertise();
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
         //vb=view.findViewById(R.id.textView);

        grid=view.findViewById(R.id.grid);
        expertQueryList=new ArrayList<>();
        bline=view.findViewById(R.id.bline);

        stext=view.findViewById(R.id.svt);
        srch=view.findViewById(R.id.dots);
        cancel=view.findViewById(R.id.reset);
        cancel.setVisibility(View.GONE);
        s_lay=view.findViewById(R.id.s_lay);
        searchop=view.findViewById(R.id.search);
        s_lay.setVisibility(View.GONE);
        tool=0;
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


        extrasearch=new ArrayList<>();
        queryDetailsList=new ArrayList<>();
        getting_query_list();

        onoff=false;
        ImageView slide=view.findViewById(R.id.dots);
        views=view.findViewById(R.id.viewslide);
        //views.setVisibility(View.GONE);

        LinearLayout cardcreate=view.findViewById(R.id.cardcreate);
        cardcreate.setVisibility(View.GONE);
        SwipeRefreshLayout lay= view.findViewById(R.id.refr);
        lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshdata();
                lay.setRefreshing(false);
            }
        });
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

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(getContext(), MultiChat_ExpertQuery_Activity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("queryId",expertQueryList.get(position).getQuery_id());
//                bundle.putString("expertId",user_singleton.getInstance().getUser_id());
//                bundle.putString("cropId", expertQueryList.get(position).getCropID());
//                bundle.putString("farmerId",expertQueryList.get(position).getFarmerId());
//
//                bundle.putString("farmerName",expertQueryList.get(position).getFarmer_name());
//                bundle.putString("crop",expertQueryList.get(position).getcrop_name());
//                bundle.putString("desc",expertQueryList.get(position).getFarmer_query());
//                bundle.putString("imagePath",expertQueryList.get(position).getImg_path());
//                bundle.putString("userType","7");
//
//                bundle.putString("status_run",expertQueryList.get(position).getstatus_run());
//                intent.putExtra("bundle",bundle);
                                            //intent.putExtras(bundle);
                                            chat_expert_singleton.getInstance().setchat(expertQueryList.get(position));
                                            startActivity(intent);
                                        }
                                    });

                ImageView ref = view.findViewById(R.id.refresh);
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
                getting_query_list();
            }
        });
        ImageView appoint=view.findViewById(R.id.call);
        appoint.setEnabled(true);
        appoint.setOnClickListener(new View.OnClickListener() {
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
                Intent nb=new Intent(getContext(), expert_appointment.class);
                        getContext().startActivity(nb);
            }
        });

        return view;
    }

    private void refreshdata() {
        getting_query_list();
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

    public void  getting_query_list()
    {
        //newly added token
        String token_key = "PMAK-646d993c4ae18d7b534b6aad-afe1811f3b9c9feff73dc2633c1410b885";
        //AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Query_resolution1?expertId=" + user_singleton.getInstance().getUser_id())
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/Query_resolution1?expertId=" + user_singleton.getInstance().getUser_id())
                .addHeaders("ranjan_api", token_key)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("response :",response.toString());
                        expertQueryList.clear();
                        ArrayList<String>  query_list= new ArrayList<String>();
                        ArrayList<String> list_scientist_id = new ArrayList<String>();
                        try {
                            // System.out.println("p.5:");
                            for (int i = 0; i < response.length(); i++)
                            {


                                JSONObject jso = (JSONObject) response.get(i);
                                Model_expert_query_content model_expert_query_content=new Model_expert_query_content();

                                String Query_id = (String) jso.optString("Query_id");

                                String farmer_name= jso.optString("farmer_name");

                                String farmer_address=jso.optString("farmer_address");
                                String expert_name=jso.optString("expert_nmae");
                                String expert_address=jso.optString("expert_address");
                                String farmer_query=jso.optString("farmer_query");

                                String img_path=jso.optString("img_path");
                                String query_resolution=jso.optString("query_resolution");
                                String query_status=jso.optString("query_status");
                                String crop_id=jso.optString("cropId");
                                String crop=jso.optString("crop");
                                String times= jso.optString("times");
                                String farmerID=jso.optString("farmerId");
                                String userType= jso.optString("userType");

                                model_expert_query_content.setQuery_id(Query_id);
                                model_expert_query_content.setFarmer_name(farmer_name);
                                model_expert_query_content.setFarmer_address(farmer_address);
                                model_expert_query_content.setExpert_nmae(expert_name);
                                model_expert_query_content.setExpert_address(expert_address);
                                model_expert_query_content.setFarmer_query(farmer_query);
                                model_expert_query_content.setImg_path(img_path);
                                model_expert_query_content.setQuery_resolution(query_resolution);
                                model_expert_query_content.setQuery_status(query_status);
                                model_expert_query_content.setCropID(crop_id);
                                model_expert_query_content.setFarmerId(farmerID);
                                model_expert_query_content.setUserType(userType);
                                model_expert_query_content.setstatus_run("0");
                                model_expert_query_content.setcrop_name(crop);
                                model_expert_query_content.settimes(times);
                                expertQueryList.add(model_expert_query_content);
                            }
                            //vb.setText("Problems on "+expert_singleton.getInstance().getcrop());
                            if(expertQueryList.size()==0)
                            {
                                farmerquerylist.setVisibility(View.VISIBLE);
                                grid.setVisibility(View.GONE);
                                bline.setVisibility(View.GONE);

                            }
                            else
                            {if (expertQueryList.get(0).getCropID().isEmpty()){
                                farmerquerylist.setVisibility(View.VISIBLE);
                                grid.setVisibility(View.GONE);
                                bline.setVisibility(View.GONE);
                            }

                                farmerquerylist.setVisibility(View.GONE);
                                custom_adapter_query_answer1=new custom_adapter_query_answer(getContext(),expertQueryList);
                                grid.setAdapter(custom_adapter_query_answer1);

                                // list_query1.setAdapter(new custom_adapter_query_answer(head_query_list.this,expertQueryList));

                            }


                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }




                    }@Override
                public void onError(ANError anError) {


                    Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                }
                });
    }
}
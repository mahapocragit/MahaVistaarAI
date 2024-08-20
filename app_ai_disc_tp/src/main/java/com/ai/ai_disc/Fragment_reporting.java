package com.ai.ai_disc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ai.ai_disc.model.expert_list_adapter18;
import com.ai.ai_disc.model.getall_appoinment_model;
import com.ai.ai_disc.model.getall_report_model;
import com.ai.ai_disc.model.history_model;
import com.ai.ai_disc.model.map_info_response;
import com.ai.ai_disc.model.report_adapter18;
import com.ai.ai_disc.model.report_info_model;
import com.ai.ai_disc.model.reportdata;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_reporting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_reporting extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    boolean onoff;
    View views;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int numb;
    LinearLayout cardcall,searchhere;
    TextInputEditText searchtext;
    RadioGroup group;
    ImageView cross,searched,filtered,createnew, addnew ,refreshed,optbutton;
    ImageView search;
    private RecyclerView grid;
    Custom_grid_reporting adapter_grid;
    report_adapter18 datare;
    ArrayList<reportdata> list;
    LatLng loct;
    TextView noreports;
    int tool,sort,fil;
    report_adapter18 ca;
    CardView sortlay,opt,cardfilter;
    EditText sr;
    ArrayList<String> followed;
    ArrayList<reportdata> s_index;
    public Fragment_reporting() {
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
    public static Fragment_reporting newInstance(String param1, String param2) {

        Fragment_reporting fragment = new Fragment_reporting();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_reporting, container, false);
        tool=0;
                sort=0;
                fil=0;

        onoff=false;
        noreports=view.findViewById(R.id.noreports);
         searched=view.findViewById(R.id.search);
        createnew=view.findViewById(R.id.create_report);
        addnew=view.findViewById(R.id.more);
        refreshed=view.findViewById(R.id.refresh);
        filtered=view.findViewById(R.id.filtered);
        searchhere=view.findViewById(R.id.searchhere);
        searchtext=view.findViewById(R.id.searchtext);
        search=view.findViewById(R.id.searchbutton);
        cross=view.findViewById(R.id.imageView13);
        group=view.findViewById(R.id.radio);
        opt=view.findViewById(R.id.opt);
        optbutton=view.findViewById(R.id.optbutton);
        sortlay=view.findViewById(R.id.sortlay);
        sortlay.setVisibility(View.GONE);
        cross.setVisibility(View.GONE);
        searchhere.setVisibility(View.GONE);
        cardfilter=view.findViewById(R.id.cardfilter);
        cardfilter.setVisibility(View.GONE);

        SwipeRefreshLayout lay= view.findViewById(R.id.refr);
        lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshreports();
                lay.setRefreshing(false);
            }
        });
        opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fil==0){
                    fil=1;
                    cardfilter.setVisibility(View.VISIBLE);
                    optbutton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_expand_less_24, getContext().getTheme()));
                }else{
                    fil=0;
                    cardfilter.setVisibility(View.GONE);
                    sortlay.setVisibility(View.GONE);
                    optbutton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, getContext().getTheme()));
                }
            }
        });

        refreshed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onoff=false;
//
//                views.setVisibility(View.GONE);
//                slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
                refreshreports();
            }
        });
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onoff=false;
//
//                views.setVisibility(View.GONE);
//                slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
                numb+=10;
                addreports();
            }
        });
        createnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onoff=false;
//
//                views.setVisibility(View.GONE);
//                slide.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getContext().getTheme()));
                addnew();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hg=searchtext.getText().toString();
                if (hg.matches("")){
                    Toast.makeText(getContext(), "Enter Something", Toast.LENGTH_SHORT).show();
                }
                else{
                    goforfliter(hg);
                }
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cross.setVisibility(View.GONE);
                searchhere.setVisibility(View.GONE);
                searchtext.getText().clear();
                grid.setAdapter(ca);
                tool=0;
            }
        });
        searched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tool==1){
//                    imageView6.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.ic_menu_search, getContext().getTheme()));
                   //grid.setAdapter(ca);
                    tool=0;
                    searchhere.setVisibility(View.GONE);

                    //sr.setVisibility(View.GONE);
                }else{
                    searchhere.setVisibility(View.VISIBLE);
                    tool=1;
                    //sr.setVisibility(View.VISIBLE);
                }

            }
        });
        filtered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sort==0){
                    sortlay.setVisibility(View.VISIBLE);
                    sort=1;
                }else{
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }
            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int nm=group.getCheckedRadioButtonId();
                RadioButton btt=view.findViewById(nm);
                if (btt.getText().toString().matches("Most Liked")){
                        likedreports();
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }else{if (btt.getText().toString().matches("Followed First")){
                    followedreports();
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }
                else{
                    datedreports();
                    sortlay.setVisibility(View.GONE);
                    sort=0;
                }

                }
            }
        });

        s_index=new ArrayList<>();
        grid=view.findViewById(R.id.grid);
        grid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (fil==1){
                fil=0;
                cardfilter.setVisibility(View.GONE);
                sortlay.setVisibility(View.GONE);
                optbutton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, getContext().getTheme()));
            }}
        });
        list=new ArrayList<>();
        numb=0;


        JSONObject objectd = new JSONObject();
        try {
            objectd.put("user_id", user_singleton.getInstance().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        followed=new ArrayList<>();
        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getfollows")
                .addJSONObjectBody(objectd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         JSONArray array = null;
                                         //Log.d("hhh",response.toString());
                                         try {
                                             array = response.getJSONArray("list");
                                             for (int i = 0; i < array.length(); i++) {

                                                 followed.add((String) array.getString(i));}
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                         //Log.d(TAG, "onResponse: " + array1);
                                         addreports();
                                     }

                                     @Override
                                     public void onError(ANError anError) {

                                     }
                                 });



        ca = new report_adapter18(getContext(), list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        grid.setLayoutManager(mLayoutManager);
        grid.setItemAnimator(new DefaultItemAnimator());
        grid.setAdapter(ca);

//        adapter_grid=new Custom_grid_reporting(getContext(),list);
//        grid.setAdapter(adapter_grid);
//        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent nb=new Intent(getContext(),report_page.class);
//
//                nb.putExtra("record_id",list.get(position).getrecord_id());
//                nb.putExtra("dpname",list.get(position).getdpname());
//                nb.putExtra("report_type",list.get(position).getreport_type());
//                nb.putExtra("crop_name",list.get(position).getcrop_name());
//
//                nb.putExtra("head",list.get(position).gethead());
//                nb.putExtra("dated",list.get(position).getdated());
//                nb.putExtra("info",list.get(position).getinfo());
//                nb.putExtra("place",list.get(position).getlocate());
//                startActivity(nb);
//
//            }
//        });
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(fllike,
                new IntentFilter("follow"));
        return view;
    }

    private void addnew() {
        Intent nb=new Intent(getContext(), create_report.class);
        startActivity(nb);
    }
    BroadcastReceiver fllike=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int signal = intent.getIntExtra("position", 0);
            if (signal!=0) {

                List<ImageView> fl = ca.getImageView();
                List<TextView> fltext = ca.getTextViews();
                ArrayList<reportdata> list1=ca.getreport();
                String usr = intent.getStringExtra("user");
                for (int g = 0; g < list1.size(); g += 1) {
                    if (list1.get(g).getuser_id().matches(usr)) {
                        if (signal == 1) {
                            fltext.get(g).setText("following");
                            fl.get(g).setVisibility(View.VISIBLE);
                            list1.get(g).setfl(true);
                        } else {
                            fltext.get(g).setText("follow");
                            fl.get(g).setVisibility(View.GONE);
                            list1.get(g).setfl(false);
                        }
                    }
                }
            }
        }
    };


    private void addreports() {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();
        int fg=list.size();
        try {

            object.put("number", numb);
            object.put("fl", user_singleton.getInstance().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_reports11")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Log.d("datatattt",response.toString());
                            JSONArray array = response.getJSONArray("list");
                            //Log.d(TAG, "onResponse: " + array1);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                reportdata data=new reportdata();
                                data.setcrop_name(object.getString("crop_name"));
                                data.setlocate(object.getString("locate"));
                                data.setdpname(object.getString("dp_name"));
                                data.setdated(object.getString("dated"));

                                data.setreport_type(object.getString("report_type"));
                                data.setrecord_id(object.getString("record_id"));
                                data.setuser_name(object.getString("nm"));
                                data.sethead(object.getString("head"));
                                data.setuser_id(object.getString("user_id"));
                                data.setinfo(object.getString("info"));
                                data.setlikednumber(object.getString("likednumber"));
                                data.setprio(object.getString("priority"));
                                data.setfl(followed.contains(object.getString("user_id")));
                                data.setlk(object.getString("like").matches("1"));

                                list.add(data);

                            }
                            ca.notifyDataSetChanged();
                            fil=0;
                            cardfilter.setVisibility(View.GONE);
                            sortlay.setVisibility(View.GONE);
                            optbutton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, getContext().getTheme()));

                            int hg=list.size();
                            if (hg==0){
                                noreports.setVisibility(View.VISIBLE);
                                createnew.setVisibility(View.GONE);
                                filtered.setVisibility(View.GONE);
                                searched.setVisibility(View.GONE);
                            }else{
                                noreports.setVisibility(View.GONE);
                                opt.setVisibility(View.VISIBLE);
                                createnew.setVisibility(View.VISIBLE);
                                filtered.setVisibility(View.VISIBLE);
                                searched.setVisibility(View.VISIBLE);
                                if (hg==fg){
                                    //cardcall.setVisibility(View.GONE);
                                    //Toast.makeText(getContext(), "Showing All Reports", Toast.LENGTH_SHORT).show();

                                }else{
                                    //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_post_add_24, getContext().getTheme()));
                                    //Toast.makeText(getContext(), "Showing "+String.valueOf(list.size())+" Reports", Toast.LENGTH_SHORT).show();
                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
}
    private void goforfliter(String value) {
        //search.setText("searching...");
        s_index.clear();
        for(int s=0;s< list.size();s+=1){
            reportdata ad=list.get(s);

            if (ad.getdpname().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getdated().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.gethead().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") ||ad.getcrop_name().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getlocate().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)") || ad.getuser_name().trim().toLowerCase().matches("(.*)"+value.toLowerCase().trim()+"(.*)")){
                s_index.add(ad);
            }
        }
        if (s_index.size()!=0) {
            datare = new report_adapter18(getContext(), s_index);
            grid.setAdapter(datare);
            //search.setText("GO");
            cross.setVisibility(View.VISIBLE);

            Toast.makeText(getContext(), "Showing "+String.valueOf(s_index.size())+" Results", Toast.LENGTH_SHORT).show();
            //imageView6.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_backspace_24, getContext().getTheme()));
        }
        else{
            //search.setText("GO");
            Toast.makeText(getContext(), "Nothing Found", Toast.LENGTH_SHORT).show();
        }


    }
    private void refreshreports() {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();
        list.clear();
        int fg= 0;
        numb=0;
        try {

            object.put("number", numb);object.put("fl", user_singleton.getInstance().getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_reports11")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                           // Log.d("datatattt",response.toString());
                            JSONArray array = response.getJSONArray("list");
                            //Log.d(TAG, "onResponse: " + array1);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                reportdata data=new reportdata();


                                data.setcrop_name(object.getString("crop_name"));
                                data.setlocate(object.getString("locate"));
                                data.setdpname(object.getString("dp_name"));
                                data.setdated(object.getString("dated"));

                                data.setreport_type(object.getString("report_type"));
                                data.setrecord_id(object.getString("record_id"));
                                data.setuser_name(object.getString("nm"));
                                data.sethead(object.getString("head"));
                                data.setinfo(object.getString("info"));
                                data.setuser_id(object.getString("user_id"));
                                data.setlikednumber(object.getString("likednumber"));
                                data.setprio(object.getString("priority"));
                                data.setfl(followed.contains(object.getString("user_id")));
                                data.setlk(object.getString("like").matches("1"));

                                list.add(data);

                            }
                            ca.notifyDataSetChanged();
                            grid.setAdapter(ca);
                            fil=0;
                            cardfilter.setVisibility(View.GONE);
                            sortlay.setVisibility(View.GONE);
                            optbutton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_dehaze1_24, getContext().getTheme()));

                            int hg=list.size();

                            if (hg==0){
                                noreports.setVisibility(View.VISIBLE);
                                createnew.setVisibility(View.GONE);
                                filtered.setVisibility(View.GONE);
                                searched.setVisibility(View.GONE);
                            }else{
                                noreports.setVisibility(View.GONE);
                                opt.setVisibility(View.VISIBLE);
                                createnew.setVisibility(View.VISIBLE);
                                filtered.setVisibility(View.VISIBLE);
                                searched.setVisibility(View.VISIBLE);
                            }
//                            if (hg==fg){
//                                //cardcall.setVisibility(View.GONE);
//                                Toast.makeText(getContext(), "Showing All Reports", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
//                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }
    private void followedreports() {
        ArrayList<reportdata> dfk=
                (ArrayList<reportdata>) list.clone();
        try{
            dfk.sort(new Comparator<reportdata>() {
                @Override
                public int compare(reportdata o1, reportdata o2) {
                    return Integer.compare(Integer.parseInt(o2.getprio()), Integer.parseInt(o1.getprio()));
                }
            });
            report_adapter18 datard=new report_adapter18(getContext(),dfk);
            grid.setAdapter(datard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void likedreports() {
        ArrayList<reportdata> dfk=
         (ArrayList<reportdata>) list.clone();
        try{
            dfk.sort(new Comparator<reportdata>() {
                @Override
                public int compare(reportdata o1, reportdata o2) {
                    return Integer.compare(Integer.parseInt(o2.getlikednumber()), Integer.parseInt(o1.getlikednumber()));
                }
            });
            report_adapter18 datard=new report_adapter18(getContext(),dfk);
            grid.setAdapter(datard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void datedreports() {

            report_adapter18 datard=new report_adapter18(getContext(),list);
            grid.setAdapter(datard);

    }
    private void filteredreports() {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();
        list.clear();
        int fg= 0;
        numb=0;
        try {

            object.put("number", numb);object.put("fl", user_singleton.getInstance().getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_reports11")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                           // Log.d("datatattt",response.toString());
                            JSONArray array = response.getJSONArray("list");
                            //Log.d(TAG, "onResponse: " + array1);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = (JSONObject) array.get(i);
                                reportdata data=new reportdata();


                                data.setcrop_name(object.getString("crop_name"));
                                data.setlocate(object.getString("locate"));
                                data.setdpname(object.getString("dp_name"));
                                data.setdated(object.getString("dated"));

                                data.setreport_type(object.getString("report_type"));
                                data.setrecord_id(object.getString("record_id"));
                                data.setuser_name(object.getString("nm"));
                                data.sethead(object.getString("head"));
                                data.setinfo(object.getString("info"));
                                data.setuser_id(object.getString("user_id"));
                                data.setlikednumber(object.getString("likednumber"));
                                if (followed.contains(object.getString("user_id"))){
                                    data.setfl(true);
                                }
                                if (object.getString("like").matches("1")){
                                    data.setlk(true);
                                }

                                list.add(data);

                            }
                            ca.notifyDataSetChanged();

                            int hg=list.size();
                            if (hg==fg){
                                cardcall.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Showing All Reports", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }
}
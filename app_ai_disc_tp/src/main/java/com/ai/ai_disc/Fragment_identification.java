package com.ai.ai_disc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.DashboardAdapter;
import com.ai.ai_disc.Farmer.DashboardAdapter_old;
import com.ai.ai_disc.Farmer.Farmer_DiseasIdentifier_profile1;
import com.ai.ai_disc.Farmer.Farmer_Disease_identifier_pest_Identifier_Activity;
import com.ai.ai_disc.Farmer.Model_DashboardContent;
import com.ai.ai_disc.Farmer.Model_DashboardContent1;
import com.ai.ai_disc.Farmer.RecyclerTouchListener;
import com.ai.ai_disc.model.getIdentifier_crops_response;
import com.ai.ai_disc.model.identifier_model_croplist;
import com.ai.ai_disc.view_model.identifier_croplist;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_identification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_identification extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ArrayList<String> discrops,pestcrops,croplist,discrops_img;
    private static ArrayList<Integer> number_exist;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    DashboardAdapter_old dashboardAdapter;
    ArrayList<Model_DashboardContent1> model_dashboardContents;
    public Fragment_identification() {
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
    public static Fragment_identification newInstance(String param1, String param2) {
        Fragment_identification fragment = new Fragment_identification();
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
        // Inflate the layout for this fragment
        //ArrayList<String> croplist=getArguments().getStringArrayList("argu1");
        //ArrayList<Integer> discrops_img=getArguments().getIntegerArrayList("argu2");
        //Log.d("crop_name",discrops_img.toString());
        //ArrayList<Integer> number_exist=getArguments().getIntegerArrayList("argu3");
        View view= inflater.inflate(R.layout.fragment_identification, container, false);
         recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager((FragmentActivity) this.getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        model_dashboardContents= new ArrayList<>();
        getinfo();
//        ImageView bnq=view.findViewById(R.id.imageView10);
//        bnq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(),sendnotification.class));
//            }
//        });

        //creating adapter
         dashboardAdapter = new DashboardAdapter_old((FragmentActivity) this.getContext(), model_dashboardContents);
        //attaching adapter to the RecyclerView
        recyclerView.setAdapter(dashboardAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener((FragmentActivity) this.getContext(), recyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                String cropname= croplist.get(position);
                go_to_identify_disease(cropname,number_exist.get(position));

            }

            @Override
            public void onLongClick(View view, int position)
            {

            }

        }));
        return view;
    }
    public void go_to_identify_disease(String name,int numb)
    {

        if (numb==1){
            String cropprediction="disease"+"_detection_"+name.replace(" ", "");;
            Intent gotopage =new Intent((FragmentActivity) this.getContext(), farmerdiseasepestIdentify.class);
            gotopage.putExtra("crop",name);
            gotopage.putExtra("type","disease");
            gotopage.putExtra("url",cropprediction.toLowerCase());
            gotopage.putExtra("farmerIdentification","Farmer_prediction");

            detection_instance.getInstance().settype("disease");
            detection_instance.getInstance().setcrop(name);
            detection_instance.getInstance().setnumb(numb);
            detection_instance.getInstance().setdetection("Farmer_prediction");
            detection_instance.getInstance().seturl(cropprediction.toLowerCase());
            startActivity(gotopage);
        }else{if (numb==2)
        {
            String cropprediction="pest"+"_detection_"+name.replace(" ", "");;
            Intent gotopage =new Intent((FragmentActivity) this.getContext(), farmerdiseasepestIdentify.class);
            gotopage.putExtra("crop",name);
            gotopage.putExtra("type","pest");
            gotopage.putExtra("url",cropprediction.toLowerCase());
            gotopage.putExtra("farmerIdentification","Farmer_prediction");

            //Toast.makeText(Farmer_Disease_identifier_pest_Identifier_Activity.this, cropprediction, Toast.LENGTH_SHORT).show();
            detection_instance.getInstance().settype("pest");
            detection_instance.getInstance().setcrop(name);
            detection_instance.getInstance().setnumb(numb);
            detection_instance.getInstance().setdetection("Farmer_prediction");
            detection_instance.getInstance().seturl(cropprediction.toLowerCase());
            startActivity(gotopage);
        }else{
            Intent intent = new Intent((FragmentActivity) this.getContext(), Farmer_Disease_identifier_pest_Identifier_Activity.class);
            detection_instance.getInstance().setcrop(name);
            detection_instance.getInstance().setnumb(numb);
            detection_instance.getInstance().setdetection("Farmer_prediction");

            intent.putExtra("crop",name);
            intent.putExtra("number",numb);
            intent.putExtra("farmerIdentification","Farmer_prediction");
            startActivity(intent);
        }
        }
//        intent.putExtra("crop", name);
//        intent.putExtra("number", numb);
//        intent.putExtra("farmerIdentification","farmers");


       /* Intent intent = new Intent(Farmer_DiseasIdentifier_profile1.this, Identify_disease.class);
        intent.putExtra("crop", name);
        intent.putExtra("farmerIdentification",farmerIdentification);
        startActivity(intent);*/
    }

    void getcroplist(){
//        croplist.clear();
//        number_exist.clear();
        //Log.d("before",discrops.toString());
        ArrayList<String> cpl=new ArrayList<>();
        ArrayList<Integer> num=new ArrayList<>();
        if (discrops.isEmpty()){

        }
        if (pestcrops.isEmpty()){

        }
        for (int h=0;h<discrops.size();h+=1){
            String cropf=discrops.get(h);
            cpl.add(cropf);
            if (pestcrops.contains(cropf)){
                num.add(3);
            }else{
                num.add(1);
            }

        }
        for(int k =0;k<pestcrops.size();k+=1){
            String crop=pestcrops.get(k);
            if (cpl.contains(crop)){
                int g=0;
            }else{cpl.add(crop);
                num.add(2);

            }
        }
        croplist=cpl;
        number_exist=num;
        //Log.d("after",croplist.toString());
    }


    void getinfo(){

        identifier_croplist viewmodel= ViewModelProviders.of((FragmentActivity) this.getContext()).get(identifier_croplist.class);
        viewmodel.getting_crops().observe((LifecycleOwner) this.getContext(), new Observer<getIdentifier_crops_response>() {
            @Override
            public void onChanged(getIdentifier_crops_response getIdentifier_crops_response) {
                if (getIdentifier_crops_response.getModel()!=null){
                    identifier_model_croplist model = getIdentifier_crops_response.getModel();
                    discrops=model.getcropsd();
                    pestcrops=model.getcropsp();
                    getcroplist();
                    //getImages();

                    discrops_img=new ArrayList<>();

                    JSONObject object = new JSONObject();
                    try {
                        JSONArray idf=new JSONArray(croplist);

                        object.put("list", idf);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.d("mmmm",object.toString());
                    AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_crop_img")
                            .addJSONObjectBody(object)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {
                                        discrops_img.clear();
                                        JSONArray array = jsonObject.getJSONArray("list");
                                        //Log.d("mmmm",jsonObject.toString());
                                        for (int i = 0; i < array.length(); i++) {
                                            discrops_img.add(array.getString(i));
                                        }
                                        for (int i = 0; i < croplist.size(); i++) {
                                            Model_DashboardContent1 model_dashboardContent = new Model_DashboardContent1();
                                            model_dashboardContent.setGetGridText(croplist.get(i));
                                            model_dashboardContent.setGridImage(discrops_img.get(i));
                                            model_dashboardContents.add(model_dashboardContent);
                                        }
                                        dashboardAdapter.notifyDataSetChanged();
                                        recyclerView.setAdapter(dashboardAdapter);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {

                                }
                            });
    }}}
    );
}
}
package com.ai.ai_disc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.ai_disc.Farmer.DashboardAdapter;
import com.ai.ai_disc.Farmer.Farmer_Disease_identifier_pest_Identifier_Activity;
import com.ai.ai_disc.Farmer.Model_DashboardContent;
import com.ai.ai_disc.Farmer.Model_expert_query_content;
import com.ai.ai_disc.Farmer.RecyclerTouchListener;
import com.ai.ai_disc.Farmer.custom_adapter_query_answer;
import com.ai.ai_disc.Farmer.head_query_list;
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
 * Use the {@link Fragment_chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_chat extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView list_query1;
    Context context;
    RelativeLayout layout;
    String userId="";
    List<Model_expert_query_content> expertQueryList;
    ArrayList<ArrayList<String>> full_details;
    custom_adapter_query_answer custom_adapter_query_answer1;
    String expertId;

    public Fragment_chat() {
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
    public static Fragment_chat newInstance(String param1, String param2) {
        Fragment_chat fragment = new Fragment_chat();
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



        View view= inflater.inflate(R.layout.content_head_query_list, container, false);
        full_details = new ArrayList<ArrayList<String>>();
        userId= user_singleton.getInstance().getUser_id();
        expertQueryList=new ArrayList<>();
        expertId= user_singleton.getInstance().getUser_id();

        list_query1=view.findViewById(R.id.list_query);
        layout=view.findViewById(R.id.content_head_query_list);

        //getting_query_list();

        list_query1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(getContext(), MultiChat_FarmerExpertQuery_Activity.class);
                Bundle bundle=new Bundle();

                bundle.putString("farmerName",expertQueryList.get(position).getFarmer_name());
                bundle.putString("desc",expertQueryList.get(position).getFarmer_query());
                bundle.putString("queryId",expertQueryList.get(position).getQuery_id());
                bundle.putString("imagePath",expertQueryList.get(position).getImg_path());
                bundle.putString("expertId",expertId);
                bundle.putString("cropId", expertQueryList.get(position).getCropID());
                bundle.putString("farmerId",expertQueryList.get(position).getFarmerId());
                bundle.putString("userType","7");
                intent.putExtra("bundle",bundle);
                //intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }


}
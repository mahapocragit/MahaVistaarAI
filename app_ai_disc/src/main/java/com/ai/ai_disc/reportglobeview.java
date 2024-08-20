package com.ai.ai_disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ai.ai_disc.model.map_info_model;
import com.ai.ai_disc.model.map_report_response;
import com.ai.ai_disc.model.reportmap_latlong_model;
import com.ai.ai_disc.model.reportmap_latlong_response;
import com.ai.ai_disc.view_model.map_report;
import com.ai.ai_disc.view_model.map_report_latlong_no;
import com.ai.ai_disc.view_model.map_report_latlong_no_date;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class reportglobeview extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    map_report ViewModel;
    map_report_latlong_no viewmodel2;
     map_report_latlong_no_date viewmodeldate;
    InternetReceiver internet ;
    private static String dp;

    final Calendar myCalendar= Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    // below are the latitude and longitude
    // of 4 different locations.
    LatLng delhi = new LatLng(28.63, 77.17);
    LatLng kolkata = new LatLng(22.55, 88.34);
CardView carddate;
    private final LatLngBounds dfg = new LatLngBounds(
            new LatLng(5.15, 67.4), new LatLng(37.35, 101.23));
// Constrain the camera target to the Adelaide bounds.
 int[] fromdate;
     int[] todate;
    static String  fromd,tod;
     TextView dateend, datestart;

    // creating array list for adding all our locations.
    private ArrayList<LatLng> locationArrayList;
    boolean onoff;
    View view;
    static int typo;
    private static int crop, diseasepest,type_occur;
     ProgressBar load;
    TextView tyd;
    ArrayList<String> crop_d,crop_p,crop_idd,crop_idp;
    ArrayList<JSONArray> diseaseid,disname,pestid,pestname;
    Spinner spinnercrop,spinnereffect,spinnerperiod;

    private final String[] period={"ALL","Select Period"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_globe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Reports");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationArrayList=new ArrayList<>();
        locationArrayList.add(delhi);

        load=findViewById(R.id.progressBar2);
        load.setVisibility(View.GONE);
        carddate=findViewById(R.id.carddate);
        carddate.setVisibility(View.GONE);
        dateend=findViewById(R.id.date_end);
        datestart=findViewById(R.id.date_start);


//        datestart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showTruitonDatePickerDialog(view);
//
//            }
//        });
//        dateend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(fromdate!=null){
//                showToDatePickerDialog(view);}
//            }
//        });

        Switch dp = findViewById(R.id.switch1);
        dp.setChecked(false);
        dp.setShowText(false);
        tyd=findViewById(R.id.textView2r);
        tyd.setText("Disease");

//        carddate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                datepicker();
//            }
//        });



        Button slide=findViewById(R.id.togglefilter);
        view=findViewById(R.id.viewslide);
        view.setVisibility(View.GONE);

        slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(!onoff){
                    view.setVisibility(View.VISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,
                            0,
                            - view.getHeight(),
                            0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    view.startAnimation(animate);
                } else {
                    view.setVisibility(View.INVISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,
                            0,
                            0,
                            -view.getHeight());
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    view.startAnimation(animate);
                }
                onoff = !onoff;
            }
        });
        spinnercrop=findViewById(R.id.mapcrop);
        spinnerperiod=findViewById(R.id.dateuptospin);
        spinnereffect=findViewById(R.id.mapdisease);

        getallmapinfo();

        dp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    tyd.setText("Pest");
                    type_occur=2;
                    designslide(2);
                }else{
                    tyd.setText("Disease");
                    type_occur=1;
                    designslide(1);

                }
            }
        });


    }

    private void getallmapinfo(){
        load.setVisibility(View.VISIBLE);
        dateend.setVisibility(View.GONE);
        datestart.setVisibility(View.GONE);
        ViewModel= ViewModelProviders.of(reportglobeview.this).get(map_report.class);
        ViewModel.getting_crops().observe(reportglobeview.this, new Observer<map_report_response>() {
            @Override
            public void onChanged(map_report_response map_report_response) {
                load.setVisibility(View.GONE);
                if (map_report_response.getModel() != null){
                    map_info_model model = map_report_response.getModel();
                    crop_d=model.getcrops();
                    crop_idd=model.getcrop_id();
                    crop_p=model.getcropsp();
                    crop_idp=model.getcrop_idp();
                    diseaseid=model.getdisid();
                    disname=model.getdisname();
                    pestid=model.getpestid();
                    pestname=model.getpestname();
                    crop_d.add(0,"Choose crop");
                    crop_p.add(0,"Choose crop");
                    designslide(1);


                }
                else {

                    Toast.makeText(reportglobeview.this, "Error !!!", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }
    private void designslide(int typ){
        carddate.setVisibility(View.GONE);
        if (typ==1){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(reportglobeview.this,
                    android.R.layout.simple_spinner_item,crop_d);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnercrop.setAdapter(adapter);
            spinnercrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i==0){
                        spinnereffect.setEnabled(false);
                        spinnerperiod.setEnabled(false);
                    }else
                    {String a=adapterView.getItemAtPosition(i).toString();
                        int crpidd=Integer.parseInt(crop_idd.get(i-1));
                        crop=crpidd;

                        ArrayList<String> listdatas = new ArrayList<String>();
                        JSONArray jArray = disname.get(i-1);
                        if (jArray != null) {
                            for (int ii=0;ii<jArray.length();ii++){
                                listdatas.add(jArray.optString(ii));
                            }
                        }
                        ArrayList<String> listdatasii = new ArrayList<String>();
                        JSONArray jArrayi = diseaseid.get(i-1);
                        if (jArrayi != null) {
                            for (int ii=0;ii<jArrayi.length();ii++){
                                listdatasii.add(jArrayi.optString(ii));
                            }
                        }

                        spinnereffect.setEnabled(true);

                        listdatas.add(0,"All");
                        ArrayAdapter<String> adapterq = new ArrayAdapter<String>(reportglobeview.this,
                                android.R.layout.simple_spinner_item,listdatas);
                        adapterq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnereffect.setAdapter(adapterq);
                        spinnereffect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterViewq, View views, int i1, long ll) {
                                try{
                                if (i1==0){
                                    mMap.clear();
                                    dp="All";
                                    carddate.setVisibility(View.GONE);
                                    Toast.makeText(reportglobeview.this, " All disease of "+a+" !",Toast.LENGTH_LONG).show();
                                    diseasepest=0;
                                    typo=3;
                                    periodspin();
                                    //mapbuildup(crpidd,22,3);


                                }else{mMap.clear();
                                    carddate.setVisibility(View.GONE);
                                    String z=adapterViewq.getItemAtPosition(i1).toString();
                                    int disidi=Integer.parseInt(listdatasii.get(i1-1));
                                    Toast.makeText(reportglobeview.this, z+" disease of "+a+" !",Toast.LENGTH_LONG).show();
                                    dp=z;
                                    typo=1;
                                    diseasepest=disidi;
                                    periodspin();

                                }} catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }


                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                Toast.makeText(reportglobeview.this, "hurray",Toast.LENGTH_LONG).show();
                                typo=3;
                                diseasepest=0;
                                periodspin();
                            }
                        });
                    }}

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    spinnereffect.setEnabled(false);
                    Toast.makeText(reportglobeview.this, "hurray",Toast.LENGTH_LONG).show();
                }
            });
        }


        else {


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(reportglobeview.this,
                    android.R.layout.simple_spinner_item,crop_p);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnercrop.setAdapter(adapter);
            spinnercrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try{

                    if (i==0){
                        spinnereffect.setEnabled(false);
                        spinnerperiod.setEnabled(false);
                        crop=0;
                    }else
                    {String a=adapterView.getItemAtPosition(i).toString();
                        int crpidd=Integer.parseInt(crop_idp.get(i-1));
                        crop=crpidd;
                        ArrayList<String> listdata = new ArrayList<String>();

                        JSONArray jArray = pestname.get(i-1);
                        if (jArray != null) {
                            for (int ii=0;ii<jArray.length();ii++){
                                listdata.add(jArray.optString(ii));
                            }
                        }
                        ArrayList<String> listdatasii = new ArrayList<String>();
                        JSONArray jArrayi = pestid.get(i-1);
                        if (jArrayi != null) {
                            for (int ii=0;ii<jArrayi.length();ii++){
                                listdatasii.add(jArrayi.optString(ii));
                            }
                        }
                        spinnereffect.setEnabled(true);
                        listdata.add(0,"All");
                        ArrayAdapter<String> adapterq = new ArrayAdapter<String>(reportglobeview.this,
                                android.R.layout.simple_spinner_item,listdata);
                        adapterq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnereffect.setAdapter(adapterq);
                        spinnereffect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterViewq, View view, int i1, long l) {
                                if (i1==0){
                                    mMap.clear();
                                    carddate.setVisibility(View.GONE);
                                    Toast.makeText(reportglobeview.this, "All pest of "+a+" !",Toast.LENGTH_LONG).show();
                                    dp="All";
                                    diseasepest=0;
                                    typo=4;
                                    periodspin();


                                }else{
                                    mMap.clear();
                                    carddate.setVisibility(View.GONE);
                                    String z=adapterViewq.getItemAtPosition(i1).toString();
                                    int pestii=Integer.parseInt(listdatasii.get(i1-1));
                                    Toast.makeText(reportglobeview.this, z+" pest of "+a+" !",Toast.LENGTH_LONG).show();
                                    dp=z;
                                    diseasepest=pestii;
                                    typo=2;
                                    periodspin();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                diseasepest=0;
                                typo=4;
                                periodspin();
                            }
                        });
                    }
} catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    crop=0;
                    spinnereffect.setEnabled(false);
                }
            });
        }

    }
    void periodspin(){
        spinnerperiod.setEnabled(true);
        ArrayAdapter<String> adapterqq = new ArrayAdapter<String>(reportglobeview.this,
                android.R.layout.simple_spinner_item,period);
        adapterqq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerperiod.setAdapter(adapterqq);
        spinnerperiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:{mapbuildup(crop,diseasepest,typo);
                        carddate.setVisibility(View.GONE);

                    break;}
                    case 1:{
                        carddate.setVisibility(View.VISIBLE);
                        datestart.setVisibility(View.VISIBLE);
                        //showTruitonDatePickerDialog();
                        datepicker();
                        break;
                    }
                }

                }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mapbuildup(crop,diseasepest,typo);
            }
        });
    }

//    public void showTruitonDatePickerDialog() {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//    }
//
//    private  void showToDatePickerDialog() {
//        DialogFragment newFragment = new ToDatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//    }

    private void mapbuildup(int a, int b, int c){
        load.setVisibility(View.VISIBLE);
        viewmodel2= ViewModelProviders.of(reportglobeview.this).get(map_report_latlong_no.class);
        viewmodel2.getting_crops(a,b,c).observe(reportglobeview.this, new Observer<reportmap_latlong_response>() {
            @Override
            public void onChanged(reportmap_latlong_response reportmap_latlong_response) {
                load.setVisibility(View.GONE);
                if (reportmap_latlong_response.getModel() != null){
                    reportmap_latlong_model model = reportmap_latlong_response.getModel();
                    ArrayList<String> formid=model.getformid();
                    ArrayList<String> numb=model.getnumb();
                    ArrayList<String> lat=model.getlat();
                    ArrayList<String> lon=model.getlong();
                    ArrayList<Float> lat1=new ArrayList<>();
                    ArrayList<Float> long1=new ArrayList<>();
                    ArrayList<Integer> numb1=new ArrayList<>();
                    if (formid.size()==0){
                        Toast.makeText(reportglobeview.this, "No reports found !", Toast.LENGTH_SHORT).show();
                        mMap.clear();
                    }else{
                        Toast.makeText(reportglobeview.this, String.valueOf(formid.size())+" reports found !", Toast.LENGTH_SHORT).show();

                        for (int ih=0;ih<formid.size();ih+=1){
                        float lt= Float.parseFloat(String.format("%.2f",Float.parseFloat(lat.get(ih))));
                        float ln= Float.parseFloat(String.format("%.2f",Float.parseFloat(lon.get(ih))));
                        int nm= Integer.parseInt(numb.get(ih));

                        if (lat1.contains(lt) && long1.contains(ln)){
                            int bnm=numb1.get(lat1.indexOf(lt));
                            bnm+=nm;
                            numb1.set(lat1.indexOf(lt),bnm);

                        }else{
                            lat1.add(lt);
                            long1.add(ln);
                            numb1.add(nm);
                        }
                    }
                    setonmap(lat1,long1,numb1);}




                }else {

                    Toast.makeText(reportglobeview.this, "Error !!!hiiii", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setLatLngBoundsForCameraTarget(dfg);
        // inside on map ready method
        // we will be displaying all our markers.
        // for adding markers we are running for loop and
        // inside that we are drawing marker on our map.
        for (int i = 0; i < locationArrayList.size(); i++) {

            // below line is use to add marker to each location of our array list.
            mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("ICAR-IASRI"));

            // below lin is use to zoom our camera on map.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

            // below line is use to move our camera to the specific location.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
        }
    }
    private void setonmap(ArrayList<Float> a,ArrayList<Float> b,ArrayList<Integer> c){
        mMap.clear();

        for (int i = 0; i < a.size(); i++) {
            LatLng ab=new LatLng(a.get(i),b.get(i));


            // below line is use to add marker to each location of our array list.
            mMap.addMarker(new MarkerOptions().position(ab).title(dp+" : "+String.valueOf(c.get(i))));

            // below lin is use to zoom our camera on map.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

            // below line is use to move our camera to the specific location.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ab));
        }
    }

//    public static class DatePickerFragment extends DialogFragment implements
//            DatePickerDialog.OnDateSetListener {
//        @NonNull
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//            DatePickerDialog datePickerDialog;
//            datePickerDialog = new DatePickerDialog(getActivity(),this, year,
//                    month,day);
//            return datePickerDialog;
//        }
//
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            // Do something with the date chosen by the user
//            fromdate= new int[]{year,month,day};
//            datestart.setText(String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year));
//            //Toast.makeText(getContext(), "start"+String.valueOf(day),Toast.LENGTH_SHORT).show();
//            //showToDatePickerDialog();
//            dateend.setVisibility(View.VISIBLE);
//
//
//        }
//
//    }
//    public static class ToDatePickerFragment extends DialogFragment implements
//            DatePickerDialog.OnDateSetListener {
//        // Calendar startDateCalendar=Calendar.getInstance();
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
////            String getfromdate = fromdate.toString().trim();
////            String getfrom[] = getfromdate.split("/");
//            int year,month,day;
//            year= fromdate[0];
//            month = fromdate[1];
//            day = fromdate[2];
//            final Calendar c = Calendar.getInstance();
//            c.set(year,month,day+1);
//            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),this, year,month,day);
//            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
//            datePickerDialog.setTitle("Set Last Date !");
//            tod=null;
//            return datePickerDialog;
//        }
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//
//            todate= new int[]{year,month,day};
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.MONTH, month);
//            calendar.set(Calendar.DAY_OF_MONTH, day);
//            tod=simpleDateFormat.format(calendar.getTime());
//            dateend.setText(tod);
//            if(dateend!=null && datestart!=null){
//                getreportsondates();
//            }
//            //Toast.makeText(getContext(), "end"+String.valueOf(day),Toast.LENGTH_SHORT).show();
//        }
//    }
    private void datepicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(reportglobeview.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);

                fromdate = new int[]{i, i1, i2};
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                fromd=simpleDateFormat.format(calendar.getTime());
                datestart.setText("From "+simpleDateFormat1.format(calendar.getTime()));
                //Toast.makeText(reportglobeview.this, fromd,Toast.LENGTH_SHORT).show();
                //showToDatePickerDialog();
                dateend.setVisibility(View.VISIBLE);
                datepickertodate();

            }
            },Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("From !");
        fromd=null;
        datePickerDialog.show();

        //Toast.makeText(reportglobeview.this, "hi",Toast.LENGTH_SHORT).show();

    }
    private void datepickertodate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(reportglobeview.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                todate= new int[]{i,i1,i2};
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                tod=simpleDateFormat.format(calendar.getTime());
                dateend.setText("To "+simpleDateFormat1.format(calendar.getTime()));
                if(tod!=null && fromd!=null){
                    getreportsondates();
                }
            }
        },fromdate[0],fromdate[1],fromdate[2]);
        datePickerDialog.setTitle("Upto !");
        final Calendar c = Calendar.getInstance();
        final Calendar c1 = Calendar.getInstance();
        c.set(fromdate[0],fromdate[1],fromdate[2]+1);
        c1.set(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
        tod=null;
        datePickerDialog.show();

                //Toast.makeText(reportglobeview.this, "hi",Toast.LENGTH_SHORT).show();

    }
    private  void getreportsondates(){

        load.setVisibility(View.VISIBLE);
        viewmodeldate= ViewModelProviders.of(reportglobeview.this).get(map_report_latlong_no_date.class);
        viewmodeldate.getting_crops(crop,diseasepest,typo,fromd,tod).observe(reportglobeview.this, new Observer<reportmap_latlong_response>() {
            @Override
            public void onChanged(reportmap_latlong_response reportmap_latlong_response) {
                load.setVisibility(View.GONE);
                if (reportmap_latlong_response.getModel() != null){
                    reportmap_latlong_model model = reportmap_latlong_response.getModel();
                    ArrayList<String> formid=model.getformid();
                    ArrayList<String> numb=model.getnumb();
                    ArrayList<String> lat=model.getlat();
                    ArrayList<String> lon=model.getlong();
                    ArrayList<Float> lat1=new ArrayList<>();
                    ArrayList<Float> long1=new ArrayList<>();
                    ArrayList<Integer> numb1=new ArrayList<>();
                    if (formid.size()==0){
                        Toast.makeText(reportglobeview.this, "No reports found !", Toast.LENGTH_SHORT).show();
                        mMap.clear();
                    }else{
                        Toast.makeText(reportglobeview.this, String.valueOf(formid.size())+" reports found !", Toast.LENGTH_SHORT).show();
                        for (int ih=0;ih<formid.size();ih+=1){
                            float lt= Float.parseFloat(String.format("%.2f",Float.parseFloat(lat.get(ih))));
                            float ln= Float.parseFloat(String.format("%.2f",Float.parseFloat(lon.get(ih))));
                            int nm= Integer.parseInt(numb.get(ih));

                            if (lat1.contains(lt) && long1.contains(ln)){
                                int bnm=numb1.get(lat1.indexOf(lt));
                                bnm+=nm;
                                numb1.set(lat1.indexOf(lt),bnm);

                            }else{
                                lat1.add(lt);
                                long1.add(ln);
                                numb1.add(nm);
                            }
                        }
                        setonmap(lat1,long1,numb1);}




                }else {

                    Toast.makeText(reportglobeview.this, "Error !!!", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


}
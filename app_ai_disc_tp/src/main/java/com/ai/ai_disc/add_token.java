package com.ai.ai_disc;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ai.ai_disc.Farmer.Account_expert;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_token extends AppCompatActivity {
    EditText user, pass, type;
    String usd, pasd, tyuu;
    ArrayList<String> usr;
    ArrayList<String> passed;
    Button submit, getcsv;
    int nh;
    int ACTIVITY_CHOOSE_FILE1 = 105, PERMISSION_REQUEST_CODE = 106;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tyuu = "1";
        nh = 0;
        usr=new ArrayList<>();
        passed=new ArrayList<>();
        setContentView(R.layout.activity_add_token);
        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        type = findViewById(R.id.editTextTextPersonName3);
        getcsv = findViewById(R.id.button4);
        getcsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("text/csv");
//                startActivityForResult(Intent.createChooser(intent, "Open CSV"), 105);

            }
        });


        //img.setText("Images Loading...");
        //Log.d("hhhh",object.toString());
        try {
            AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/getupassappu")
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray arr = response.getJSONArray("list");
                        for (int i = 0; i < arr.length(); i += 1) {
                            JSONObject bv = (JSONObject) arr.get(i);
                            String usf = bv.getString("user_name");
                            String ps = bv.getString("password");
                            if (!usf.matches("") && !ps.matches("")) {
                                usr.add(usf);
                                passed.add(ps);

                            }

                        }
                        //Log.d("vvvv", "done");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(ANError anError) {

                }
            });
            //Log.d("hhhh",jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getiddd();
//                usd = usr.get(nh).trim();
//                pasd = passed.get(nh).trim();
//                nh += 1;
////                 tyuu=type.getText().toString();
//
//                db.collection("loginaidisc")
//                        .whereEqualTo("username",usd)
//                        .whereEqualTo("password",pasd)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    boolean found=false;
//                                    String id="";
//                                    String user_type="";
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d("jjjjjj", document.getId() + " => " + document.getData());
//                                         user_type = (String) document.getData().get("usertype");
//                                        found = true;
//                                        //progress.cancel();
//                                        id = String.valueOf(document.getId());
//                                    }
//                                    Log.d("iiddd",id+"ggggg");
//                                        if (!id.isEmpty()){
//                                            Toast.makeText(add_token.this, user_type+" already have id "+id, Toast.LENGTH_LONG).show();
//                                        }else{
//                                            AlertDialog.Builder bn = new AlertDialog.Builder(add_token.this);
//                                            bn.setTitle("Are you sure?").setMessage(usd + " : " + pasd + " : " + tyuu).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    settoken(tyuu, usd, pasd);
//                                                }
//                                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            }).show();
//                                        }
//                                        }
//
//                                    }}).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });

            }
        });


    }
    void getiddd(){
        usd = usr.get(nh).trim();
        pasd = passed.get(nh).trim();
        nh += 1;
//                 tyuu=type.getText().toString();

        db.collection("loginaidisc")
                .whereEqualTo("username",usd)
                .whereEqualTo("password",pasd)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean found=false;
                            String id="";
                            String user_type="";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // Log.d("jjjjjj", document.getId() + " => " + document.getData());
                                user_type = (String) document.getData().get("usertype");
                                found = true;
                                //progress.cancel();
                                id = String.valueOf(document.getId());
                            }
                            //Log.d("iiddd",id+"ggggg");
                            if (!id.isEmpty()){
                                Toast.makeText(add_token.this, user_type+" already have id "+id, Toast.LENGTH_LONG).show();
                                getiddd();
                            }else{
                                AlertDialog.Builder bn = new AlertDialog.Builder(add_token.this);
                                bn.setTitle("Are you sure?").setMessage(usd + " : " + pasd + " : " + tyuu).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        settoken(tyuu, usd, pasd);
                                    }
                                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getiddd();
                                    }
                                }).show();
                            }
                        }

                    }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void settoken(String type, String userName_entered, String password_entered) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", userName_entered.trim());
        user.put("password", password_entered.trim());
        user.put("usertype", type.trim());
        db.collection("loginaidisc")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(add_token.this, userName_entered + " : message: account token added", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(add_token.this, Login.class);
                        getiddd();
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d("Register_token", "Error-F19");
            }
        });
    }
}


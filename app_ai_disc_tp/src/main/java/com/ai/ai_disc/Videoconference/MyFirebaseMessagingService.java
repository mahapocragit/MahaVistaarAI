package com.ai.ai_disc.Videoconference;

import static android.media.RingtoneManager.getDefaultUri;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ai.ai_disc.Login;
import com.ai.ai_disc.MultiChat_ExpertQuery_Activity;
import com.ai.ai_disc.MultiChat_FarmerExpertQuery_Activity;
import com.ai.ai_disc.R;
import com.ai.ai_disc.Splash_Activity;
import com.ai.ai_disc.expert_appointment;
import com.ai.ai_disc.expertprofile_fragment;
import com.ai.ai_disc.farmersprofile_fragment1;
import com.ai.ai_disc.helper_running;
import com.ai.ai_disc.history;
import com.ai.ai_disc.history_page;
import com.ai.ai_disc.model.reportdata;
import com.ai.ai_disc.notify_singleton;
import com.ai.ai_disc.report_page;
import com.ai.ai_disc.report_singleton;
import com.ai.ai_disc.user_singleton;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        //Log.e("newToken", token);

        //Log.d(TAG, "onNewToken: " + token);
        save_token(token);
//Add your token in your sharepreferences.
        //  getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", token).apply();
    }

    public void save_token(String token) {

        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_pref.sp.edit();
        editor.putString("token", token);

        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        /*if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }*/

        //Log.d(TAG, "onMessageReceived: ");
        //Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {

            String request = remoteMessage.getData().get("request");
            if (request.equals("queue")) {

               /* Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());*/
                String email_id = remoteMessage.getData().get("email");
                String my_token = remoteMessage.getData().get("my_token");


                save(email_id,my_token);

                String title="Request from "+email_id;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    addNotification(title,"abc");
                }else{
                    addNotification1(title,"abc");

                }

               // Intent intent = new Intent(getApplicationContext(), List_request.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );

              //  startActivity(intent);
            }

            if (request.equals("call"))
            {

               /* Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());*/
                String email_id = remoteMessage.getData().get("email");
                String my_token = remoteMessage.getData().get("my_token");

                Intent intent = new Intent(getApplicationContext(), Incoming.class);
                intent.putExtra("email", email_id);
                intent.putExtra("my_token", my_token);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            if (request.equals("answer"))
            {

               /* Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());*/
                String email_id = remoteMessage.getData().get("email");
                String my_token = remoteMessage.getData().get("my_token");
                String room_id_get = remoteMessage.getData().get("room_id");

                Intent intent = new Intent("custom-action-local-broadcast");
                intent.putExtra("room_id",room_id_get);
                intent.putExtra("status","accepted");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

              //  add_to_room(room_id_get);
            }

            if (request.equals("reject")) {

                /*Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());*/
                String email_id = remoteMessage.getData().get("email");
                String my_token = remoteMessage.getData().get("my_token");
                String room_id_get = remoteMessage.getData().get("room_id");

                Intent intent = new Intent("custom-action-local-broadcast");
                intent.putExtra("status","not_accepted");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                //  add_to_room(room_id_get);
            }
            if (request.equals("appointment_to_expert")) {

//                Log.d(TAG, "onMessageReceived:  " + request);
//                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("username"));
//                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
//                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());

                String username = remoteMessage.getData().get("username");
                String date = remoteMessage.getData().get("date");
                String title="Video appointment from "+username+" on "+date;
                //String room_id_get = remoteMessage.getData().get("room_id");
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    setNotification_appointment(title,"Tap to open app");

            }
            if (request.equals("news")) {

//                Log.d(TAG, "onMessageReceived:  " + request);
//                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("username"));
//                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
//                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());

                String news = remoteMessage.getData().get("news");
                String my_token = remoteMessage.getData().get("username");

                String title = "New report by " + my_token;
                setNotification(title, news);
            }
            if (request.equals("follow")) {


                String my_token = remoteMessage.getData().get("username");
                String title="You are followed by "+my_token;
                setNotification_follow(title,"Tap to open app");

            }
            if (request.equals("chat_farmer")) {


                String my_token = remoteMessage.getData().get("username");
                String msg = remoteMessage.getData().get("msg");
                String report = remoteMessage.getData().get("report");
                String title="Query "+report+" : "+msg;
                String inf="Reply from "+my_token;
//                if (notify_singleton.getInstance().getmsg().matches("0")){
                setNotification_chat(title,inf,report);

                Intent intent = new Intent("chat_refresh_message");
                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                intent.putExtra("position",1);
                intent.putExtra("query",report);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }
            if (request.equals("admin")) {

                String head = remoteMessage.getData().get("head");
                String msg = remoteMessage.getData().get("text");
                String type = remoteMessage.getData().get("type");
                if (type.matches("1")){
                    setNotificationadmin_update(head,msg);
                }else{
                if (type.matches("3")){

                    setNotification_feature(head,msg);
                }else{
                    setNotificationadmin_programme(head,msg,type);
                }
                }
            }
            if (request.equals("coming_call")) {

                String scode = remoteMessage.getData().get("scode");
                String app = remoteMessage.getData().get("app");
                String query = remoteMessage.getData().get("query");

                    setNotification_call(scode,app,query);

            }
            if (request.equals("comment")) {

                String report = remoteMessage.getData().get("report");
                String msg = remoteMessage.getData().get("msg");
                String username = remoteMessage.getData().get("username");
                Intent intent = new Intent("comment");
                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                intent.putExtra("position",1);
                intent.putExtra("report",report);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                addreport(report,msg,username);

            }
        }


        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            //Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification().getTitle());
           // Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification().getBody());
            //  showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }


    public void add_to_room(String room_name)
    {


        //Log.d(TAG, "add_to_room:  myfirebase ");
        try {
            // object creation of JitsiMeetConferenceOptions
            // class by the name of options
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(""))
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()

                .setRoom(room_name)

                .build();



        JitsiMeetActivity.launch(getApplicationContext(), options);
    }
    public void save(String email, String token)
    {

        shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
        String list = shared_pref.sp.getString("list", "");

        //Log.d(TAG, "save: "+list);

        if(!list.isEmpty()){
            //Log.d(TAG, "save: not empty");
            try {
                String  DATE_FORMAT_8 = "yyyy-MM-dd HH:mm:ss";
                JSONObject list_json = new JSONObject(list);
                JSONArray array = list_json.getJSONArray("array");
                JSONObject object = new JSONObject();
                object.put("email",email);
                object.put("token",token);

                object.put("date",getCurrentDate(DATE_FORMAT_8));



              //  Log.d(TAG, "save: "+getCurrentDate(DATE_FORMAT_8));
                array.put(object);
                list_json.put("array",array);
               // Log.d(TAG, "save: "+list_json);
                shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.sp.edit();
                editor.putString("list",list_json.toString());

                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
           // Log.d(TAG, "save: empty");
            try {

                String  DATE_FORMAT_8 = "yyyy-MM-dd HH:mm:ss";
                JSONObject list_json1 = new JSONObject();
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("email",email);
                object.put("token",token);
                object.put("date",getCurrentDate(DATE_FORMAT_8));





                array.put(object);
                list_json1.put("array",array);
                //Log.d(TAG, "save: "+list_json1);
                shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.sp.edit();
                editor.putString("list",list_json1.toString());
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }
    public String getCurrentDate(String DATE_FORMAT_1)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        Date today = new Date();
        return dateFormat.format(today);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification(String title, String message)
    {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String id = "video_channel_id";
        CharSequence name ="video_channel_name";

        String description ="This is Notification channel of NIBPP Video app";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);

        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.GREEN);
        mChannel.enableVibration(true);
        // mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);



            /*
            JSONObject message_extra = new JSONObject(message);
            String data= message_extra.getString("message");
            String audio_data= message_extra.getString("audio");
            System.out.println("audio data:"+audio_data);

            String video_data= message_extra.getString("video");
            String image_data= message_extra.getString("image");
            // System.out.println("video data:"+video_data);
            // System.out.println("image data:"+image_data);

            Intent intent_activity = new Intent(this,Notification_activity.class);
            intent_activity.putExtra("data",message);
            //   Bundle bundle = new Bundle();
            //  bundle.putString("data",message);
            PendingIntent pendingIntentYes = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),intent_activity,PendingIntent.FLAG_UPDATE_CURRENT);


             */

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)

                    .setChannelId(id);



            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify((int) System.currentTimeMillis(), builder.build());



    }

    //admin
    private void setNotificationadmin_programme(String title,String notificationMessage,String url) {

        String CHANNEL_ID = "my_channel_01";

//**add this line**
        int requestID = (int) System.currentTimeMillis();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }

    private void setNotificationadmin_update(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_01";

//**add this line**
        int requestID = (int) System.currentTimeMillis();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
        Intent notificationIntent;
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            //
            notificationIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        } catch (android.content.ActivityNotFoundException anfe) {
            notificationIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        }

        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification_feature(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_0feature";

//**add this line**
        int requestID = (int) System.currentTimeMillis();
        //final String appPackageName = getPackageName();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);


        String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        PendingIntent contentIntent;
            if (!appRunningBackground){
//                if (vcb.toLowerCase().trim().matches("farmer")){
//                    notificationIntent = new Intent(getApplicationContext(), farmersprofile_fragment1.class);
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
//                }else{
//                    notificationIntent = new Intent(getApplicationContext(), expertprofile_fragment.class);
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
                contentIntent=null;
            }
            else{
                notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                //PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    contentIntent = PendingIntent.getActivity
                            (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_ONE_SHOT);

                }
                else
                {
                    contentIntent = PendingIntent.getActivity
                            (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

                }
            }





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);

        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification_feature1(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_0feature";

//**add this line**
        int requestID = (int) System.currentTimeMillis();
        //final String appPackageName = getPackageName();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);


        String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;

        if (!appRunningBackground){
            if (vcb.toLowerCase().trim().matches("farmer")){
                notificationIntent = new Intent(getApplicationContext(), farmersprofile_fragment1.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            }else{
                notificationIntent = new Intent(getApplicationContext(), expertprofile_fragment.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        else{
            notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        }


        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);

        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(0, mBuilder.build());

    }


    private void setNotification(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_0feature";

//**add this line**
        int requestID = (int) System.currentTimeMillis();
        final String appPackageName = getPackageName();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
        String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent=new Intent() ;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        try{

            if (!appRunningBackground){
                if (vcb.toLowerCase().trim().matches("farmer")){
                    notificationIntent = new Intent(getApplicationContext(), farmersprofile_fragment1.class);
                }else{
                    notificationIntent = new Intent(getApplicationContext(), expertprofile_fragment.class);
                }
            }
            else{
                notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("gggggggggggggf",e.toString());
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification_follow(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_0feature";

//**add this line**
        int requestID = (int) System.currentTimeMillis();
        final String appPackageName = getPackageName();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
        String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent=new Intent() ;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        try{

            if (!appRunningBackground){
                if (vcb.toLowerCase().trim().matches("farmer")){
                    notificationIntent = new Intent(getApplicationContext(), history.class);
                }else{
                    notificationIntent = new Intent(getApplicationContext(), history.class);
                }
            }
            else{
                notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
           // Log.d("gggggggggggggf",e.toString());
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void addreport(String report,String msg,String user) {
        //add.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_access_time_24, getContext().getTheme()));
        JSONObject object = new JSONObject();

        try {

            object.put("report_id", report);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://nibpp.krishimegh.in/Api/nibpp/get_all_rep")
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Log.d("datatattt",response.toString());
                                reportdata data=new reportdata();
                                data.setcrop_name(response.getString("crop_name"));
                                data.setlocate(response.getString("locate"));
                                data.setdpname(response.getString("dp_name"));
                                data.setdated(response.getString("dated"));

                                data.setreport_type(response.getString("report_type"));
                                data.setrecord_id(response.getString("record_id"));
                                data.setuser_name(response.getString("nm"));
                                data.sethead(response.getString("head"));
                                data.setuser_id(response.getString("user_id"));
                                data.setinfo(response.getString("info"));
                            report_singleton.getInstance().setdata(data);
                            setNotification_comment(report,msg,user);

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }
    private void setNotification_comment(String report,String msg,String user) {

        String CHANNEL_ID = "my_channel_0comment";

//**add this line**
        int requestID = (int) System.currentTimeMillis();
        final String appPackageName = getPackageName();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
        String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent=new Intent() ;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        String notificationMessage="";
        notificationMessage=user+" : "+msg;
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        try{

            if (!appRunningBackground){


                notificationIntent = new Intent(getApplicationContext(), report_page.class);



            }
            else{
                notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("gggggggggggggf",e.toString());
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Report "+report)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification_call(String scode,String app,String query) {

        String CHANNEL_ID = "my_channel_0call";

//**add this line**
        int requestID = (int) System.currentTimeMillis();
        final String appPackageName = getPackageName();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
       String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent=new Intent() ;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        String notificationMessage="";
        if (vcb.toLowerCase().trim().matches("farmer")){notificationMessage="Expert has joined Video call, Pls Join";}else{notificationMessage="Farmer has joined Video call, Pls Join";}
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        try{

        if (!appRunningBackground){


                notificationIntent = new Intent(getApplicationContext(), VCActivity.class);

            notificationIntent.putExtra("query",query);

            notificationIntent.putExtra("app_id",app);
            notificationIntent.putExtra("scode",scode);

        }
        else{
             notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);
        }
        } catch (Exception e) {
            e.printStackTrace();
           // Log.d("gggggggggggggf",e.toString());
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Video Calling")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification_app(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_0feature";

//**add this line**
        int requestID = (int) System.currentTimeMillis();
        final String appPackageName = getPackageName();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
        Intent notificationIntent;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        PendingIntent contentIntent;
        if (!appRunningBackground){

                //notify_singleton.getInstance().setnot(report);
                notificationIntent = new Intent(getApplicationContext(), MultiChat_ExpertQuery_Activity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

            contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification_chat(String title,String notificationMessage,String report) {

        String CHANNEL_ID = "my_channel_01_chat";

//**add this line**
        int requestID = (int) System.currentTimeMillis();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
        String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        PendingIntent contentIntent;
        if (!appRunningBackground){
                if (vcb.toLowerCase().trim().matches("farmer")){
                    notify_singleton.getInstance().setnot(report);
                    notificationIntent = new Intent(getApplicationContext(), MultiChat_FarmerExpertQuery_Activity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                }else{
                    notify_singleton.getInstance().setnot(report);
                    notificationIntent = new Intent(getApplicationContext(), MultiChat_ExpertQuery_Activity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            //contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            //contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        //PendingIntent pendingIntent ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }


//            if (helper_running.isAppRunning(getApplicationContext(),appPackageName)){
//                if (vcb.toLowerCase().trim().matches("farmer")){
//                    notify_singleton.getInstance().setmsg(report);
//                    notificationIntent = new Intent(getApplicationContext(), farmersprofile_fragment1.class);
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
//                    contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                }else{
//                    notify_singleton.getInstance().setmsg(report);
//                    notificationIntent = new Intent(getApplicationContext(), expertprofile_fragment.class);
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
//                    contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                }
//            }
//            else{
//                notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);
//                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
//                contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, 0);
//
//            }



        //PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification_appointment(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_01_app";

//**add this line**
        int requestID = (int) System.currentTimeMillis();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);
        String vcb= user_singleton.getInstance().getUser_type();
        Intent notificationIntent;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        PendingIntent contentIntent;
        if (!appRunningBackground){

                notificationIntent = new Intent(getApplicationContext(), expert_appointment.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

            //contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            //contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        //PendingIntent contentIntent ;//= PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void setNotification1(String title,String notificationMessage) {

        String CHANNEL_ID = "my_channel_0chat";

//**add this line**
        int requestID = (int) System.currentTimeMillis();

        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(NotificationManager.class);

        Intent notificationIntent = new Intent(getApplicationContext(), Splash_Activity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());

    }

    private void addNotification1(String title, String message) {

//======================================================================================================
// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().


          /*  JSONObject message_extra = new JSONObject(message);

            String   data= message_extra.getString("message");
            String audio_data= message_extra.getString("audio");
            System.out.println("audio data:"+audio_data);

            String video_data= message_extra.getString("video");
            String image_data= message_extra.getString("image");
            System.out.println("video data:"+video_data);
            System.out.println("image data:"+image_data);

            Intent intent_activity = new Intent(this,Notification_activity.class);
            intent_activity.putExtra("data",message);
            // Bundle bundle = new Bundle();
            //  bundle.putString("data",message);
            PendingIntent pendingIntentYes = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),intent_activity, PendingIntent.FLAG_UPDATE_CURRENT);


           */

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_SOUND);




            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify((int) System.currentTimeMillis(), builder.build());








    }
}

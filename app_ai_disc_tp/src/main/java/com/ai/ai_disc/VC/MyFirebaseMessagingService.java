package com.ai.ai_disc.VC;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.ai.ai_disc.R;
import com.ai.ai_disc.shared_pref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
       // Log.e("newToken", token);

        //Log.d(TAG, "onNewToken: " + token);
        Log.d(TAG,"NewTokenGenerated" + token);
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
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
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

        Log.d(TAG, "onMessageReceived: ");
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0)
        {

            String request = remoteMessage.getData().get("request");
            if (request.equals("queue")) {

                Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
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
            if (request.equals("appointment_to_expert")) {

                Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("username"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());

                String email_id = remoteMessage.getData().get("username");
                String my_token = remoteMessage.getData().get("my_token");
                String title="Request from1 "+email_id;
                //String room_id_get = remoteMessage.getData().get("room_id");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    addNotification(title,"abc");
                }else{
                    addNotification1(title,"abc");

                }

//                Intent intent = new Intent("custom-action-local-broadcast");
//                intent.putExtra("status","not_accepted");
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                //  add_to_room(room_id_get);
            }

            if (request.equals("call"))
            {

                Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
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

                Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
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

                Log.d(TAG, "onMessageReceived:  " + request);
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("email"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("my_token"));
                Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
                String email_id = remoteMessage.getData().get("email");
                String my_token = remoteMessage.getData().get("my_token");
                String room_id_get = remoteMessage.getData().get("room_id");

                Intent intent = new Intent("custom-action-local-broadcast");
                intent.putExtra("status","not_accepted");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                //  add_to_room(room_id_get);
            }


        }


        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification().getBody());
            //  showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    public void add_to_room(String room_name)
    {


        Log.d(TAG, "add_to_room:  myfirebase ");
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

        Log.d(TAG, "save: "+list);

        if(!list.isEmpty()){
            Log.d(TAG, "save: not empty");
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
                Log.d(TAG, "save: "+list_json);
                shared_pref.sp = getSharedPreferences(shared_pref.pre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared_pref.sp.edit();
                editor.putString("list",list_json.toString());

                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            {
            Log.d(TAG, "save: empty");
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
                Log.d(TAG, "save: "+list_json1);
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
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(title)

                    .setChannelId(id);



            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify((int) System.currentTimeMillis(), builder.build());



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
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_SOUND);




            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify((int) System.currentTimeMillis(), builder.build());








    }
}

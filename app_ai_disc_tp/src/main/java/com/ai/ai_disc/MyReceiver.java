package com.ai.ai_disc;

import static android.media.RingtoneManager.getDefaultUri;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {
    String CHANNEL_ID = "my_channel_01_app";
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getStringExtra("myAction") != null &&
                intent.getStringExtra("myAction").equals("set_alarm_book")){

            int requestID = (int) System.currentTimeMillis();
            String msg="Your appointment slot of QUERY : "+intent.getStringExtra("query");
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //example for large icon
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Appointment is live !")
                    .setContentText(msg)
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Intent i;
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(runningAppProcessInfo);
            boolean appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            if (!appRunningBackground) {
                i=null;
            }else {
                i = new Intent(context, Splash_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            PendingIntent pendingIntent ;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity
                        (context, requestID, i, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                //Log.d("gggg111",intent.getStringExtra("myAction"));
            }
            else
            {
                pendingIntent = PendingIntent.getActivity
                        (context, requestID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                //Log.d("gggg2222",intent.getStringExtra("myAction"));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "AI-DISC";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                assert manager != null;
                manager.createNotificationChannel(mChannel);
            }

            builder.setLights(0xFFb71c1c, 1000, 2000);
            builder.setSound(alarmSound);
            builder.setChannelId(CHANNEL_ID);
            builder.setContentIntent(pendingIntent);
            manager.notify(12345, builder.build());
    }
}}

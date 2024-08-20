package com.ai.ai_disc;

import static com.google.firebase.firestore.core.CompositeFilter.Operator.OR;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager ;
import android.app.DatePickerDialog ;
import android.app.Notification ;
import android.app.PendingIntent ;
import android.content.Context ;
import android.content.Intent ;
import android.os.Build;
import android.os.Bundle ;

import android.os.SystemClock;
import android.util.Log;
import android.view.View ;
import android.widget.Button ;
import android.widget.DatePicker ;

import java.text.ParseException;
import java.text.SimpleDateFormat ;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar ;
import java.util.Date ;
import java.util.Locale ;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class alarmservice extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    TextView btnDate ;
    EditText bh;
    String dt;
    final Calendar myCalendar = Calendar. getInstance () ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmservice);

        bh=findViewById(R.id.edit);
        btnDate = findViewById(R.id.tvDate) ;

    }
    private void scheduleNotification (Notification notification , long delay) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher.class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;

        PendingIntent pendingIntent = PendingIntent.getBroadcast ( this, 0 , notificationIntent , PendingIntent.FLAG_MUTABLE ) ;
        long futureInMillis = delay +SystemClock. elapsedRealtime () ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }
    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText("hellllllllo") ;
        builder.setSmallIcon(R.mipmap.ic_launcher) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet (DatePicker view , int year , int monthOfYear , int dayOfMonth) {
            myCalendar .set(Calendar. YEAR , year) ;
            myCalendar .set(Calendar. MONTH , monthOfYear) ;
            myCalendar .set(Calendar. DAY_OF_MONTH , dayOfMonth) ;
            updateLabel() ;
        }
    } ;
    public void setDate (View view) {
//        new DatePickerDialog(
//                alarmservice.this, date ,
//                myCalendar .get(Calendar. YEAR ) ,
//                myCalendar .get(Calendar. MONTH ) ,
//                myCalendar .get(Calendar. DAY_OF_MONTH )
//        ).show() ;
        dt=bh.getText().toString();
        updateLabel();
    }

    private void updateLabel () {
        String myFormat = "yyyy/MM/dd HH:mm:ss" ; //In which you need put here   "29-11-2022 18:55:03"
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat) ;
        Date date = null ;
        try{
            //String gb=sdf.format(dt);
            date = sdf.parse(dt);
            btnDate .setText(sdf.format(date)) ;

        } catch (ParseException e) {
            e.printStackTrace();
        }
//
//
        long diffInMs =   date.getTime()-new Date().getTime();

        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        long when = SystemClock.elapsedRealtime () + 30000;

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("myAction", "mDoNotify");
        PendingIntent pendingIntent ;//= PendingIntent.getBroadcast(this, 0, intent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent, PendingIntent.FLAG_MUTABLE);
            Toast.makeText(alarmservice.this, "hhhhhhhhh1 ", Toast.LENGTH_SHORT).show();
        }
        else
        {
            pendingIntent = PendingIntent.getBroadcast
                    (this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Toast.makeText(alarmservice.this, "hhhhhhhhh2 "+String.valueOf(diffInSec*1000), Toast.LENGTH_SHORT).show();
        }
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime ()+diffInSec*1000, pendingIntent);



//        long diffInSec = TimeUnit.MILLISECONDS.toMillis(diffInMs);
      //Toast.makeText(alarmservice.this, "hhhhhhhhh ", Toast.LENGTH_SHORT).show();
//        scheduleNotification(getNotification( btnDate.getText().toString()) , diffInSec*1000 ) ;
    }
}
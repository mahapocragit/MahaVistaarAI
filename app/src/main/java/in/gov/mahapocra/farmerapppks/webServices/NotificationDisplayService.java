package in.gov.mahapocra.farmerapppks.webServices;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.activity.SplashScreen;

public class NotificationDisplayService extends AppCompatActivity {
    private static final int NotifyId = 1;
    public static final String chanelId = "Chanel_Id";
    private static final String chanelName = "Chanel_Name";
    private static final String chanelDescription = "Chanel_Description";
    private static String token = "";

    public NotificationDisplayService(){

    }

    public static void displayService(Context mContext, String mMessageTitle, String mMessageContent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(chanelId,chanelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(chanelDescription);
            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            // NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        showNotification(mContext,mMessageTitle,mMessageContent);
    }



    public static void showNotification(Context context, String title, String message){

        // To open an Intent on click by Notification
        Intent intent = new Intent(context, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,NotifyId,intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, chanelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NotifyId,builder.build());
    }


    public static String getFCMToken(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    token = task.getResult().getToken();
                    Log.i("token",token);
                }
            }
        });
        return token;
    }

}

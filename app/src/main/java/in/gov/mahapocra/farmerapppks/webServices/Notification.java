package in.gov.mahapocra.farmerapppks.webServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import in.gov.mahapocra.farmerapppks.R;
import in.gov.mahapocra.farmerapppks.activity.LoginScreen;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notification {
    String channelId = "default_channel_id";
    private Context mCtx;
    private static Notification mInstance;
    PendingIntent pendingIntent;

    private Notification(Context context) {
        mCtx = context;
    }

    public static synchronized Notification getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Notification(context);
        }
        return mInstance;
    }


    public void displayNotification(String title, String body) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx,channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body);

        Intent resultIntent = new Intent(mCtx, LoginScreen.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        String loginType = AppSettings.getInstance().getValue(mCtx,ApConstants.kLOGIN_TYPE,ApConstants.kLOGIN_TYPE);
//        if(loginType.equalsIgnoreCase(ApConstants.kCOORD_TYPE) || loginType.equalsIgnoreCase(ApConstants.kPS_TYPE) ||loginType.equalsIgnoreCase(ApConstants.kPMU_TYPE) ||loginType.equalsIgnoreCase(ApConstants.kCA_TYPE) ) {
//            Intent resultIntent = new Intent(mCtx, NotificationListActivity.class);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
//        else {
//            Intent resultIntent = new Intent(mCtx, LoginActivity.class);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        }

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, mBuilder.build());
        }
    }
}

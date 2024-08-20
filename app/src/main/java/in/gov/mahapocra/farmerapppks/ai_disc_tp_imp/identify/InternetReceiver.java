package in.gov.mahapocra.farmerapppks.ai_disc_tp_imp.identify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String status = NetworkStatusChecking.get_connectivity_status(context);

        if(status.equals("not_connected")){
            Intent intent1= new Intent(context,InternetNotWorking.class);
            //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);


        }


    }
}
package in.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkStatusChecking {


    public static String get_connectivity_status(Context context){

        ConnectivityManager cm= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo network =cm.getActiveNetworkInfo();
        if(network!=null){

            if(network.getType() == ConnectivityManager.TYPE_WIFI)
                return "connected";

            if(network.getType() == ConnectivityManager.TYPE_MOBILE)
                return "connected";
        }


        return "not_connected";
    }
}
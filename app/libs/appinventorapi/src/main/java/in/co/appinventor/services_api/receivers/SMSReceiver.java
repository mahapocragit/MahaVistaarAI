package in.co.appinventor.services_api.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import in.co.appinventor.services_api.app_util.AppConstants;
import in.co.appinventor.services_api.listener.SMSListener;

/* renamed from: in.co.appinventor.services_api.receivers.SMSReceiver */
public class SMSReceiver extends BroadcastReceiver {
    private static String receiverString;
    private static SMSListener smsListener;
    private String TAG = AppConstants.MAIN_FOLDER;

    public static void bind(SMSListener listener, String sender) {
        smsListener = listener;
        receiverString = sender;
    }

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            try {
                for (Object aPdusObj : (Object[]) bundle.get("pdus")) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.e(this.TAG, "Received SMS: " + message + ", Sender: " + senderAddress);
                    smsListener.onOTPReceived(message);
                }
            } catch (Exception e) {
                Log.e(this.TAG, "Exception: " + e.getMessage());
            }
        }
    }

    public static void unbind() {
        smsListener = null;
        receiverString = null;
    }
}

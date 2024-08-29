package `in`.gov.mahapocra.farmerapppks.webServices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FBaseMessagingService : FirebaseMessagingService() {

    var channelId = "default_channel_id"
    var channelDescription = "Default Channel"
    private var notificationManager: NotificationManager? = null
    var mcontext: Context? = null
    private val s: String? = null

    var token = FirebaseInstanceId.getInstance().token


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("token", token.toString())
        super.onMessageReceived(remoteMessage)
        Log.d("firebaseMassage", remoteMessage.toString())
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            Log.d("firebaseMassage","FCM Title="+title)
            Log.d("firebaseMassage","FCM body="+body)
            sendNotification(title.toString(), body.toString())
        }
    }


    private fun sendNotification(title: String, body: String) {
        Log.d("message22", body)
        if (notificationManager == null) {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            Log.d("message111", "notificationManager")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("message2222", "SDK_INT")
            var notificationChannel = notificationManager!!.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                Log.d("message333", "notificationChannel")
                val importance = NotificationManager.IMPORTANCE_HIGH //Set the importance level
                notificationChannel = NotificationChannel(channelId, channelDescription, importance)
                notificationChannel.lightColor = Color.GREEN //Set if it is necesssary
                notificationChannel.enableVibration(true) //Set if it is necesssary
                notificationManager!!.createNotificationChannel(notificationChannel)
            }

            Notification.getInstance(this).displayNotification(title,body)

        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

}
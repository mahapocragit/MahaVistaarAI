package `in`.gov.mahapocra.mahavistaarai.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.gov.mahapocra.mahavistaarai.R


class FBaseMessagingService : FirebaseMessagingService() {

    private val channelId = "default_channel_id"
    private val channelDescription = "Default Channel"
    private var notificationManager: NotificationManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Log the data payload if it exists
        remoteMessage.data.isNotEmpty().let {
            Log.d("firebaseMessage", "Message data payload: ${remoteMessage.data}")
        }

        // Handle notification payload
        remoteMessage.notification?.let {
            val title = it.title ?: "Notification"
            val body = it.body ?: ""
            Log.d("firebaseMessage", "FCM Title=$title, FCM Body=$body")
            sendNotification(title, body)
        }
    }

    private fun sendNotification(title: String, body: String) {
        if (notificationManager == null) {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

        // Create the Notification Channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel = notificationManager?.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                notificationChannel = NotificationChannel(channelId, channelDescription, importance).apply {
                    lightColor = Color.GREEN
                    enableVibration(true)
                }
                notificationManager?.createNotificationChannel(notificationChannel)
            }
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.app_icon_store) // Replace with your app's icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        notificationManager?.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebaseMessage", "New token: $token")
        // You can send this token to your backend server if needed.
    }
}
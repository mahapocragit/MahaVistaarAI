package `in`.gov.mahapocra.mahavistaarai.data.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.gov.mahapocra.mahavistaarai.R
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @SuppressLint("NotificationPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val channelId = "default_channel_id"
        // Get the notification data from the remote message
        val notificationData = remoteMessage.data

        // Extract the necessary information from the data
        val title = notificationData["title"] ?: ""
        val body = notificationData["body"] ?: ""

        // Create a notification and show it
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.farmer_appfinal_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())

    }

}
package `in`.gov.mahapocra.mahavistaarai.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.pocra.PocraDbtSchemes

class FBaseMessagingService : FirebaseMessagingService() {

    private val channelId = "default_channel_id"
    private val channelName = "Default Channel"
    private val tag = "FBaseMessagingService"

    private val notificationManager: NotificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Log data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(tag, "Data payload: ${remoteMessage.data}")

            val title = remoteMessage.data["title"] ?: "Notification"
            val body = remoteMessage.data["body"] ?: "You have a new message"
            val testValue = remoteMessage.data["page"]
            sendNotification(title, body, testValue)
        }
    }

    private fun sendNotification(title: String, body: String, testValue: String?) {
        createNotificationChannelIfNeeded()

        val targetIntent = when (testValue?.lowercase()) {
            "dashboard" -> Intent(this, DashboardScreen::class.java)
            "advisory" -> Intent(this, PocraDbtSchemes::class.java)
            else -> Intent(this, DashboardScreen::class.java) // default fallback
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            targetIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId).apply {
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(R.drawable.app_icon_store) // replace with your app's notification icon
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }.build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notificationManager.getNotificationChannel(channelId) == null
        ) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for FCM notifications"
                enableVibration(true)
                lightColor = Color.GREEN
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(tag, "New token: $token")
        // You can send this token to your server if needed
    }
}


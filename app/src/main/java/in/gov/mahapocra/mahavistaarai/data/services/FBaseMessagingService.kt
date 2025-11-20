package `in`.gov.mahapocra.mahavistaarai.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.ApiService
import `in`.gov.mahapocra.mahavistaarai.data.api.AppEnvironment
import `in`.gov.mahapocra.mahavistaarai.data.helpers.RetrofitHelper
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.notification.DetailedNotificationActivity
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

            val notificationId = remoteMessage.data["not_id"] ?: "0"
            val title = remoteMessage.data["title"] ?: "Notification"
            val body = remoteMessage.data["body"] ?: "You have a new message"
            val page = remoteMessage.data["page"] ?: "You have a new message"
            Log.d(tag, "onMessageReceived: $page")
            sendNotification(title, body, page, notificationId)
        }
    }

    private fun sendNotification(
        title: String,
        body: String,
        page: String,
        notificationId: String?
    ) {
        createNotificationChannelIfNeeded()

        val targetIntent = if (page == "chatbot") {
            Intent(this, ChatbotActivity::class.java).putExtra("id", notificationId?.toLong())
        } else {
            Intent(this, DetailedNotificationActivity::class.java)
        }
        targetIntent.apply {
            putExtra("ROUTE", "NOTIFICATION_TRAY")
            putExtra("id", notificationId?.toLong())
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            targetIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(body) // this is your full long message
            .setBigContentTitle(title)
            .setSummaryText("Tap to view")

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon_store) // your notification icon
            .setContentTitle(title)
            .setContentText(body.take(40) + "...") // short preview
            .setStyle(bigTextStyle) // this makes it expandable
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

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
        val farmerId = AppSettings.getInstance()
            .getIntValue(applicationContext, AppConstants.fREGISTER_ID, 0)

        // Do network call in coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = RetrofitHelper.createRetrofitInstance(AppEnvironment.FARMER.baseUrl)
                val apiRequest = retrofit.create(ApiService::class.java)
                apiRequest.updateFCMToken(farmerId, token)
                Log.d(tag, "FCM token updated successfully")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}


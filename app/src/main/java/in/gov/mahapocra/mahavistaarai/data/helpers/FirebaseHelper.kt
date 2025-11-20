package `in`.gov.mahapocra.mahavistaarai.data.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseHelper(private val context: Context) {

    private val databaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("mahavistardb/appVersion")

    init {
        checkForUpdate()
    }

    private fun checkForUpdate() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val remoteAppVersion = snapshot.getValue(Int::class.java)
                val currentAppVersion = getCurrentAppVersion()
                if (remoteAppVersion != null && remoteAppVersion > currentAppVersion) {
                    showUpdateDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read appVersion", error.toException())
            }
        })
    }

    private fun getCurrentAppVersion(): Int {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            1 // fallback
        }
    }

    private fun showUpdateDialog() {
        val activity = context as? Activity
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            Log.w("FirebaseHelper", "Cannot show dialog: invalid Activity context")
            return
        }

        activity.runOnUiThread {
            AlertDialog.Builder(activity)
                .setTitle("Update Available")
                .setMessage("A new version of the app is available. Please update to continue.")
                .setPositiveButton("Update") { dialog, _ ->
                    openPlayStore()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun openPlayStore() {
        val packageName = context.packageName
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (e: android.content.ActivityNotFoundException) {
            // Fallback to browser if Play Store app not found
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    fun getFCMToken(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", "Fetched token: $token")
                callback(token)
            } else {
                Log.e("FCM Token", "Fetching token failed", task.exception)
                callback("") // or handle error appropriately
            }
        }
    }
}
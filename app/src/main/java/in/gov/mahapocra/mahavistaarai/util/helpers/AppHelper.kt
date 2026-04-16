package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog

class AppHelper(private val context: Context) {

    fun getCurrentAppVersion(): Int {
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

    fun showUpdateDialog() {
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
        } catch (e: ActivityNotFoundException) {
            // Fallback to browser if Play Store app not found
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }
}
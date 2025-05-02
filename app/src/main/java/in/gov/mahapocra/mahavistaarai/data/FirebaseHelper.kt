package `in`.gov.mahapocra.mahavistaarai.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

                Log.d("Firebase", "Remote: $remoteAppVersion | Current: $currentAppVersion")

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
        return context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionCode  // deprecated in API 28+, use versionCode for older, or versionName for newer
    }

    private fun showUpdateDialog() {
        AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("A new version of the app is available. Please update to continue.")
            .setPositiveButton("Update") { dialog, _ ->
                openPlayStore()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
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
}
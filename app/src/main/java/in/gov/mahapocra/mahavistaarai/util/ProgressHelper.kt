package `in`.gov.mahapocra.mahavistaarai.util

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.view.WindowManager
import `in`.gov.mahapocra.mahavistaarai.R

object ProgressHelper {

    private var progressDialog: ProgressDialog? = null

    fun showProgressDialog(context: Context) {
        // Don't recreate if already showing
        if (progressDialog == null || progressDialog?.isShowing != true) {
            progressDialog = ProgressDialog(context).apply {
                setMessage(context.getString(R.string.please_wait))
                setCancelable(true)
                try {
                    show()
                } catch (e: WindowManager.BadTokenException) {
                    e.printStackTrace()
                    // Activity is likely finishing — ignore
                }
            }
        }
    }

    fun disableProgressDialog(context: Context? = null) {
        try {
            if (progressDialog?.isShowing == true) {
                if (context is Activity && !context.isFinishing && !context.isDestroyed) {
                    progressDialog?.dismiss()
                } else if (context == null) {
                    // fallback in case we can't check activity state
                    progressDialog?.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            progressDialog = null
        }
    }
}

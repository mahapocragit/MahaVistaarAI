package `in`.gov.mahapocra.mahavistaarai.util

import android.app.ProgressDialog
import android.content.Context
import `in`.gov.mahapocra.mahavistaarai.R

object ProgressHelper {

    private var progressDialog: ProgressDialog? = null

    fun showProgressDialog(context: Context) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setMessage(context.getString(R.string.please_wait))
            progressDialog?.setCancelable(false)
        }

        // Show dialog only if not already showing
        if (progressDialog?.isShowing != true) {
            progressDialog?.show()
        }
    }

    fun disableProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null // clean up the reference
    }
}
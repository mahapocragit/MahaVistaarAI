package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import `in`.gov.mahapocra.mahavistaarai.R

object ProgressHelper {

    private var dialog: Dialog? = null

    fun showProgressDialog(context: Context, message: String? = null) {

        if (dialog?.isShowing == true) return

        val activity = context as? Activity ?: return

        if (activity.isFinishing || activity.isDestroyed) return

        dialog = Dialog(activity).apply {

            requestWindowFeature(Window.FEATURE_NO_TITLE)

            setContentView(R.layout.dialog_loading)

            setCancelable(false)

            findViewById<TextView>(R.id.tvLoading)?.text =
                message ?: activity.getString(R.string.please_wait)

            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            show()
        }
    }

    fun disableProgressDialog() {
        try {
            dialog?.dismiss()
        } catch (_: Exception) {
        } finally {
            dialog = null
        }
    }
}

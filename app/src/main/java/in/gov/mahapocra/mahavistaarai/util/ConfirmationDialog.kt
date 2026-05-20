package `in`.gov.mahapocra.mahavistaarai.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import `in`.gov.mahapocra.mahavistaarai.R

class ConfirmationDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val positiveText: String = "Yes",
    private val negativeText: String = "No",
    private val onPositiveClick: () -> Unit,
    private val onNegativeClick: (() -> Unit)? = null
) {

    private val dialog = Dialog(context)

    fun show() {
        val view = LayoutInflater.from(dialog.context)
            .inflate(R.layout.dialog_confirmation, null)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnPositive = view.findViewById<Button>(R.id.btnPositive)
        val btnNegative = view.findViewById<Button>(R.id.btnNegative)

        tvTitle.text = title
        tvMessage.text = message

        btnPositive.text = positiveText
        btnNegative.text = negativeText

        btnPositive.setOnClickListener {
            onPositiveClick.invoke()
            dialog.dismiss()
        }

        btnNegative.setOnClickListener {
            onNegativeClick?.invoke()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            (dialog.context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
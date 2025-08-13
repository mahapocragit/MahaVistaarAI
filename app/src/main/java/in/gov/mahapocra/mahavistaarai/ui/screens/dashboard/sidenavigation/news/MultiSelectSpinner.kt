package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news

import android.R
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner

class MultiSelectSpinner(context: Context, attrs: AttributeSet? = null) : AppCompatSpinner(context, attrs),
    DialogInterface.OnMultiChoiceClickListener {

    private var items: Array<String> = arrayOf()
    private var selected: BooleanArray = booleanArrayOf()
    private var listener: ((selectedItems: List<String>) -> Unit)? = null

    override fun performClick(): Boolean {
        AlertDialog.Builder(context)
            .setMultiChoiceItems(items, selected, this)
            .setPositiveButton("OK") { _, _ ->
                listener?.invoke(getSelectedItems())
                updateSpinnerText()
            }
            .show()
        return true
    }

    fun setItems(items: List<String>, preselected: Set<String> = emptySet()) {
        this.items = items.toTypedArray()
        this.selected = items.map { preselected.contains(it) }.toBooleanArray()
        updateSpinnerText()
    }

    private fun updateSpinnerText() {
        val displayText = getSelectedItems().joinToString(", ").ifEmpty { "Select Subcategories" }
        val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, listOf(displayText))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        super.setAdapter(adapter)
    }

    private fun getSelectedItems(): List<String> {
        return items.filterIndexed { index, _ -> selected[index] }
    }

    override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
        selected[which] = isChecked
    }

    fun setOnSelectionListener(listener: (List<String>) -> Unit) {
        this.listener = listener
    }
}
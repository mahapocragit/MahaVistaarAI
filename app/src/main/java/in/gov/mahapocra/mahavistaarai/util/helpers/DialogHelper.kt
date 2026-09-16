package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SearchView
import `in`.co.appinventor.services_api.listener.AlertListEventListener
import org.json.JSONArray
import org.json.JSONException

object DialogHelper {
    fun showListDialogIndex(
        ja: JSONArray?,
        requestCode: Int,
        title: String,
        type: String,
        typeId: String,
        act: Activity,
        callBackListener: AlertListEventListener
    ) {
        if (ja==null){
            return
        }
        // Original complete data
        val originalItems = mutableListOf<String>()
        val originalIds = mutableListOf<String>()

        for (i in 0 until ja.length()) {
            try {
                val obj = ja.getJSONObject(i)

                val item = obj.optString(type, "")
                val id = obj.optString(typeId, "")

                if (item.isNotEmpty() && id.isNotEmpty()) {
                    originalItems.add(item)
                    originalIds.add(id)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Dialog layout
        val container = LinearLayout(act).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                24,
                10,
                24,
                10
            )
        }

        // Search box
        val searchView = SearchView(act).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            queryHint = "Search..."
            isIconified = false
        }

        // List
        val listView = ListView(act).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        container.addView(searchView)
        container.addView(listView)

        // Adapter data
        val filteredItems = originalItems.toMutableList()
        val filteredIds = originalIds.toMutableList()

        val adapter = ArrayAdapter(
            act,
            R.layout.simple_list_item_1,
            filteredItems
        )

        listView.adapter = adapter

        // Create dialog
        val dialog = AlertDialog.Builder(act)
            .setTitle(title)
            .setView(container)
            .create()

        // Search/filter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                val searchText = newText
                    ?.trim()
                    ?.lowercase()
                    ?: ""

                filteredItems.clear()
                filteredIds.clear()

                for (i in originalItems.indices) {

                    if (originalItems[i]
                            .lowercase()
                            .contains(searchText)
                    ) {
                        filteredItems.add(originalItems[i])
                        filteredIds.add(originalIds[i])
                    }
                }

                adapter.notifyDataSetChanged()

                return true
            }
        })

        // Item click
        listView.setOnItemClickListener { _, _, position, _ ->

            if (position >= 0 && position < filteredItems.size) {

                val selectedText = filteredItems[position]
                val selectedId = filteredIds[position]

                callBackListener.didSelectListItem(
                    requestCode,
                    selectedText,
                    selectedId
                )

                dialog.dismiss()
            }
        }

        dialog.show()

        // Make dialog larger
        dialog.window?.setLayout(
            (act.resources.displayMetrics.widthPixels * 0.90).toInt(),
            (act.resources.displayMetrics.heightPixels * 0.75).toInt()
        )
    }

}
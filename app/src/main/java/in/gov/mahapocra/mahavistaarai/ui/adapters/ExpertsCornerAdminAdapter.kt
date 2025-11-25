package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DateHelper.formatDate
import org.json.JSONArray
import org.json.JSONObject

class ExpertsCornerAdminAdapter(private val posts: JSONArray) :
    RecyclerView.Adapter<ExpertsCornerAdminAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_experts_posts_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postObject = posts.optJSONObject(position)
        postObject?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return posts.length()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val context = view.context
        private val titleTextView: TextView = view.findViewById(R.id.textView3)
        private val descriptionTextView: TextView = view.findViewById(R.id.textView19)
        private val redirectTextView: TextView = view.findViewById(R.id.redirectTextView)
        private val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        private val expertsNameTextView: TextView = view.findViewById(R.id.expertsNameTextView)
        private val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        private val remarkText: TextView = view.findViewById(R.id.remarkText)
        private val deleteButton: ImageView = view.findViewById(R.id.deleteButton)

        fun bind(postObject: JSONObject) {
            val farmersName =
                AppSettings.getInstance().getValue(context, AppConstants.uName, AppConstants.uName)
            val title = postObject.optString("title", "No Title")
            val description = postObject.optString("description", "No Description")
            val fileUrl = postObject.optString("file_url", "No Description")
            val statusLabel = postObject.optString("status_label", "No Description")
            val date = postObject.optString("created_at", "")
            val remark = postObject.optString("remark", "No Remark")
            Log.d(TAG, "bind: $remark")
            titleTextView.text = title
            descriptionTextView.text = description
            statusTextView.text = statusLabel
            remarkText.visibility = if (remark != "null") View.VISIBLE else View.GONE
            remarkText.text = remark
            expertsNameTextView.text = farmersName
            if (date != "") {
                dateTextView.text = formatDate(date)
            } else {
                dateTextView.visibility = View.GONE
            }
            redirectTextView.setOnClickListener {
                LocalCustom.downloadFile(redirectTextView.context, fileUrl)
            }
            deleteButton.setOnClickListener {
                
            }
        }
    }
}
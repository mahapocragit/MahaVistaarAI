package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DateHelper.formatDate
import org.json.JSONArray
import org.json.JSONObject

class ExpertsCornerFarmerAdapter(private val posts: JSONArray) :
    RecyclerView.Adapter<ExpertsCornerFarmerAdapter.ViewHolder>() {

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
        private val titleTextView: TextView = view.findViewById(R.id.textView3)
        private val descriptionTextView: TextView = view.findViewById(R.id.textView19)
        private val redirectTextView: TextView = view.findViewById(R.id.redirectTextView)
        private val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        private val expertsNameTextView: TextView = view.findViewById(R.id.expertsNameTextView)
        private val remarkText: TextView = view.findViewById(R.id.remarkText)
        private val dateTextView: TextView = view.findViewById(R.id.dateTextView)

        fun bind(postObject: JSONObject) {
            val expertsName = postObject.optString("expert_name", "No Name")
            val title = postObject.optString("title", "No Title")
            val description = postObject.optString("description", "No Description")
            val fileUrl = postObject.optString("file_url", "No Description")
            val date = postObject.optString("created_at", "")
            expertsNameTextView.text = expertsName
            titleTextView.text = title
            descriptionTextView.text = description
            statusTextView.visibility = View.GONE
            remarkText.visibility = View.GONE
            if (date!="") {
                dateTextView.text = formatDate(date)
            }else{
                dateTextView.visibility = View.GONE
            }
            redirectTextView.setOnClickListener {
                LocalCustom.downloadFile(redirectTextView.context, fileUrl)
            }
        }
    }
}
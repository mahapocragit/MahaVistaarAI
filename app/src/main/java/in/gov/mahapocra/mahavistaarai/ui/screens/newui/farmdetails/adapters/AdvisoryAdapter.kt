package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.helper.JsonObject
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class AdvisoryAdapter(
    private val jsonArray: JSONArray,
    private val onItemClick: (JSONObject) -> Unit
) : RecyclerView.Adapter<AdvisoryAdapter.AdvisoryViewHolder>() {

    inner class AdvisoryViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.titleAdvisory)
        private val tvDate: TextView = itemView.findViewById(R.id.sowingDateAdvisory)
        private val tvDescription: TextView =
            itemView.findViewById(R.id.descriptionAdvisory)

        fun bind(item: JSONObject) {

            tvTitle.text = item.optString("title")
            tvDescription.text = item.optString("body")
            tvDate.text = item.optString("date")

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdvisoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_advisory_farm, parent, false)

        return AdvisoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AdvisoryViewHolder,
        position: Int
    ) {
        holder.bind(jsonArray.get(position) as JSONObject)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}
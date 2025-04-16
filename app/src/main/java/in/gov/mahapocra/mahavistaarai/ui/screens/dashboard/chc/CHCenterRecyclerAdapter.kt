package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray

class CHCenterRecyclerAdapter(private val tempStrArr: JSONArray) : RecyclerView.Adapter<CHCenterRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chcUserName: TextView = itemView.findViewById(R.id.chcUserName)
        val chcNameTV: TextView = itemView.findViewById(R.id.chcNameTV)
        val contactNumberTV: TextView = itemView.findViewById(R.id.contactNumberTV)
        val chcUserDistance: TextView = itemView.findViewById(R.id.chcUserDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_chc_center, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tempStrArr.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jsonObject = tempStrArr.optJSONObject(position) // Use optJSONObject instead of direct indexing
        holder.chcUserName.text = jsonObject?.optString("contact_name")
        holder.contactNumberTV.text = jsonObject?.optString("contact_no")?.let { maskNumber(it) }
        holder.chcNameTV.text = jsonObject?.optString("chcname")
        holder.chcUserDistance.text = "${jsonObject?.optString("distance")} kms"
    }

    private fun maskNumber(number: String): String {
        return if (number.length >= 10) {
            "*******" + number.takeLast(3)
        } else {
            "Invalid number"
        }
    }
}

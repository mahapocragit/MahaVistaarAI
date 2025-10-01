package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardAdapter(private val items: JSONArray, val selectedValue: String) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.getJSONObject(position)
        holder.bind(item, position, selectedValue)
    }

    override fun getItemCount(): Int = items.length()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val personRankTextView: TextView = itemView.findViewById(R.id.personRankTextView)
        private val personNameTextView: TextView = itemView.findViewById(R.id.personNameTextView)
        private val personTalukaTextView: TextView =
            itemView.findViewById(R.id.personTalukaTextView)

        fun bind(item: JSONObject, position: Int, selectedValue: String) {
            val name = item.optString("user_name")
            val village = item.optString("village")
            val taluka = item.optString("taluka")
            val district = item.optString("district")
            personRankTextView.text = (position.plus(1)).toString()
            personNameTextView.text = name
            personTalukaTextView.text = when (selectedValue) {
                "taluka" -> village
                "district" -> "$village ($taluka)"
                else -> district
            }
        }
    }
}

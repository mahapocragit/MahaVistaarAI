package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray

class LeaderboardAdapter(private val items: JSONArray) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.getJSONObject(position)
        val name = item.optString("name")
        val score = item.optInt("score")
        holder.bind(name, score)
    }

    override fun getItemCount(): Int = items.length()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val personNameTextView: TextView = itemView.findViewById(R.id.personNameTextView)
        private val personScoreTextView: TextView = itemView.findViewById(R.id.personScoreTextView)

        fun bind(name: String, score: Int) {
            personNameTextView.text = name
            personScoreTextView.text = "$score Q"
        }
    }
}

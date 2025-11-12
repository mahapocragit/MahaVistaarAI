package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.leaderboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardNewAdapter(private val items: JSONArray, val selectedValue: String) :
    RecyclerView.Adapter<LeaderboardNewAdapter.ViewHolder>() {

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
        private val personScoreTextView: TextView = itemView.findViewById(R.id.personScoreTextView)
        private val rankImageView: ImageView = itemView.findViewById(R.id.rankImageView)
        private val personTalukaTextView: TextView =
            itemView.findViewById(R.id.personTalukaTextView)

        fun bind(item: JSONObject, position: Int, selectedValue: String) {
            val name = item.optString("username")
            val taluka = item.optString("taluka")
            val district = item.optString("district")
            val recordCount = item.optInt("record_count")

            personRankTextView.text = (position.plus(1)).toString()
            personNameTextView.text = name
            personScoreTextView.text = formatPoints(recordCount)
            personTalukaTextView.text = when (selectedValue) {
                "taluka" -> taluka
                "district" -> taluka
                else -> district
            }

            when (position) {
                0 -> { rankImageView.setImageResource(R.drawable.golden_plain_ic) }
                1 -> { rankImageView.setImageResource(R.drawable.silver_plain_ic) }
                2 -> { rankImageView.setImageResource(R.drawable.bronze_plain_ic) }
                else -> { rankImageView.setImageResource(R.drawable.user_dummy_ic) }
            }
        }

        fun formatPoints(points: Int): String {
            return if (points >= 1000) {
                // Divide by 1000 and show one decimal if needed
                val formatted = points / 1000.0
                if (formatted % 1.0 == 0.0)
                    "${formatted.toInt()}k" // e.g., 1000 -> "1k"
                else
                    String.format("%.1fk", formatted) // e.g., 1450 -> "1.4k"
            } else {
                points.toString()
            }
        }
    }
}

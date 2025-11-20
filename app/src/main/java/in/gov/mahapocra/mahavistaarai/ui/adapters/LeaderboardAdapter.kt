package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
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
        private val rankImageView: ImageView = itemView.findViewById(R.id.rankImageView)
        private val personTalukaTextView: TextView =
            itemView.findViewById(R.id.personTalukaTextView)

        fun bind(item: JSONObject, position: Int, selectedValue: String) {
            var languageToLoad = "mr"
            if (AppSettings.getLanguage(itemView.context)
                    .equals("1", ignoreCase = true)
            ) {
                languageToLoad = "en"
            }
            val name = item.optString("user_name")
            val taluka = item.optString("taluka")
            val district = item.optString("district")
            val talukaMr = item.optString("taluka_mr")
            val districtMr = item.optString("district_mr")
            personRankTextView.text = (position.plus(1)).toString()
            personNameTextView.text = name
            personTalukaTextView.text = when (selectedValue) {
                "taluka" -> if (languageToLoad=="en")taluka else talukaMr
                "district" -> if (languageToLoad=="en")taluka else talukaMr
                else -> if (languageToLoad=="en")district else districtMr
            }
            when(position) {
                0 -> {
                    rankImageView.setImageResource(R.drawable.golden_plain_ic)
                }

                1 -> {
                    rankImageView.setImageResource(R.drawable.silver_plain_ic)
                }

                2 -> {
                    rankImageView.setImageResource(R.drawable.bronze_plain_ic)
                }

                else -> {
                    rankImageView.setImageResource(R.drawable.user_dummy_ic)
                }
            }
        }
    }
}

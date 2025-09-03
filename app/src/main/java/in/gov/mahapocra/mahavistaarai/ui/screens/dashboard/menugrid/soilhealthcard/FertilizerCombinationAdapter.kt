package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

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

class FertilizerCombinationAdapter(private val jsonArray: JSONArray) :
    RecyclerView.Adapter<FertilizerCombinationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_health_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val combinationHeaderTextView: TextView =
            itemView.findViewById(R.id.combinationHeaderTextView)
        val combTitleOneTextView: TextView = itemView.findViewById(R.id.combTitleOneTextView)
        val combTitleTwoTextView: TextView = itemView.findViewById(R.id.combTitleTwoTextView)
        val combTitleThreeTextView: TextView = itemView.findViewById(R.id.combTitleThreeTextView)
        val combValOneTextView: TextView = itemView.findViewById(R.id.combValOneTextView)
        val combValTwoTextView: TextView = itemView.findViewById(R.id.combValTwoTextView)
        val combValThreeTextView: TextView = itemView.findViewById(R.id.combValThreeTextView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = jsonArray.getJSONObject(position)
        holder.combinationHeaderTextView.text = buildString {
            append("Combination ")
            append((position + 1))
        }
        val combinationJsonArray = item.optJSONArray("combination${position + 1}")
        if (combinationJsonArray != null) {
            for (i in 0 until combinationJsonArray.length()) {
                val combinationObject = combinationJsonArray.getJSONObject(i)
                val fertilizerObject = combinationObject?.optJSONObject("fertilizer")
                val quantityFertilizer = combinationObject?.optString("bags")
                val unitsObject = combinationObject?.optJSONObject("units")
                val feObject = unitsObject?.optJSONObject("Fe")
                val unitsFertilizer = feObject?.optString("unit")
                val fertilizerName = fertilizerObject?.optString("name")
                if (i == 0) {
                    holder.combTitleOneTextView.text = fertilizerName
                    holder.combValOneTextView.text = buildString {
                        append(quantityFertilizer)
                        append(" ")
                        append(unitsFertilizer)
                    }
                }
                if (i == 1) {
                    holder.combTitleTwoTextView.text = fertilizerName
                    holder.combValTwoTextView.text = buildString {
                        append(quantityFertilizer)
                        append(" ")
                        append(unitsFertilizer)
                    }
                }
                if (i == 2) {
                    holder.combTitleThreeTextView.text = fertilizerName
                    holder.combValThreeTextView.text = buildString {
                        append(quantityFertilizer)
                        append(" ")
                        append(unitsFertilizer)
                    }
                }
                Log.d("TAGGER", "onBindViewHolder: $fertilizerObject")
            }
        }
    }
}
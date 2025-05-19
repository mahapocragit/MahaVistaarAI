package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class MahadbtSchemesAdapter(val schemesJSONArray: JSONArray, val languageToLoad: String) :
    RecyclerView.Adapter<MahadbtSchemesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val schemeNameTitleTextView =
            itemView.findViewById<TextView>(R.id.schemeNameTitleTextView)
        internal val pocraMahaDBTCardView =
            itemView.findViewById<CardView>(R.id.pocraMahaDBTCardView)
        internal val schemesRecyclerView =
            itemView.findViewById<RecyclerView>(R.id.schemesRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mahadbt_schemes_details_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return schemesJSONArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jsonObject = schemesJSONArray[position] as JSONObject
        val schemesArray = jsonObject.optJSONArray("Schemes")
        val schemeName = if (languageToLoad == "en") {
            jsonObject.optString("SchemeCategoryName")
        } else {
            jsonObject.optString("SchemeCategoryNameMr")
        }

        holder.schemeNameTitleTextView.text = schemeName

        // Set up the nested RecyclerView (initially hidden)
        holder.schemesRecyclerView.layoutManager =
            LinearLayoutManager(holder.schemesRecyclerView.context)
        holder.schemesRecyclerView.adapter =
            SchemeListRecyclerAdapter(schemesArray, languageToLoad)
        holder.schemesRecyclerView.visibility = View.GONE

        // Toggle on card click
        holder.pocraMahaDBTCardView.setOnClickListener {
            holder.schemesRecyclerView.visibility =
                if (holder.schemesRecyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray

class LandAdapter(
    private val jsonArray: JSONArray
) : RecyclerView.Adapter<LandAdapter.LandViewHolder>() {

    inner class LandViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val villageNameTextView = view.findViewById<TextView>(R.id.villageNameTextView)
        val ownerNameTextView = view.findViewById<TextView>(R.id.ownerNameTextView)
        val surveyNumberTextView = view.findViewById<TextView>(R.id.surveyNumberTextView)
        val totalPlotAreaTextView = view.findViewById<TextView>(R.id.totalPlotAreaTextView)
        val viewFarmDetailButton = view.findViewById<TextView>(R.id.viewFarmDetailButton)
        //        val khata = view.findViewById<TextView>(R.id.tvKhata)
        //        val cultivatedArea = view.findViewById<TextView>(R.id.tvCultivatedArea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_farm_details, parent, false)
        return LandViewHolder(view)
    }

    override fun onBindViewHolder(holder: LandViewHolder, position: Int) {
        val context = holder.itemView.context
        val obj = jsonArray.getJSONObject(position)

        val village = obj.optString("village_name")
        val ownerName = obj.optString("owner_name")
        val survey = obj.optString("survey_no")
        val totalArea = obj.optString("total_plot_area")
        //        val khata = obj.optString("khata_no")
        //        val cultivatedArea = obj.optString("cultivated_area")

        holder.villageNameTextView.text = village
        holder.surveyNumberTextView.text = survey
        holder.totalPlotAreaTextView.text = "$totalArea acre"
        holder.ownerNameTextView.text = ownerName
        holder.viewFarmDetailButton.setOnClickListener {
            context.startActivity(Intent(context, DetailedFarmActivity::class.java).apply {
                putExtra("FARM_DETAIL_DATA", obj.toString())
            })
        }
        //        holder.khata.text = khata
        //        holder.cultivatedArea.text = cultivatedArea
    }

    override fun getItemCount(): Int = jsonArray.length()
}
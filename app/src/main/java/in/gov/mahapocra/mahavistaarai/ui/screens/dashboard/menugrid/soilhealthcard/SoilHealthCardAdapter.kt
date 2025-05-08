package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class SoilHealthCardAdapter(private val farmerJsonArray: JSONArray?) :
    RecyclerView.Adapter<SoilHealthCardAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val farmerName: TextView = itemView.findViewById(R.id.farmerName)
        val farmSize: TextView = itemView.findViewById(R.id.farmSize)
        val shcNo: TextView = itemView.findViewById(R.id.shcNo)
        val surveyNo: TextView = itemView.findViewById(R.id.surveyNumber)
        val nitrogenImageView: ImageView = itemView.findViewById(R.id.nitrogenImageView)
        val phosphorusImageView: ImageView = itemView.findViewById(R.id.phosphorusImageView)
        val potassiumImageView: ImageView = itemView.findViewById(R.id.potassiumImageView)

        val ecImageView: ImageView = itemView.findViewById(R.id.ecImageView)
        val ocImageView: ImageView = itemView.findViewById(R.id.ocImageView)
        val phImageView: ImageView = itemView.findViewById(R.id.phImageView)
        val soilHealthReportLinearLayout: LinearLayout =
            itemView.findViewById(R.id.soilHealthReportLinearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_soil_health_card, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        farmerJsonArray?.let {
            val farmerObject: JSONObject = it.getJSONObject(position)
            val npkUnfiltered = farmerObject.optJSONObject("soil_params")
            val nitrogen = npkUnfiltered.getString("n")
            val phosphorus = npkUnfiltered.getString("p")
            val potassium = npkUnfiltered.getString("k")
            val electricalConductivity = npkUnfiltered.getString("ec")
            val organicCarbon = npkUnfiltered.getString("oc")
            val potentialOfHydrogen = npkUnfiltered.getString("ph")
            mapNPKByColors(nitrogen, phosphorus, potassium, electricalConductivity, organicCarbon, potentialOfHydrogen, holder)
            holder.farmerName.text = farmerObject.optString("farmer_name", "N/A")
            holder.farmSize.text = "${farmerObject.optString("farmsize", "0")} acres"
            holder.shcNo.text = "${farmerObject.optString("shc_no", "N/A")}"
            holder.surveyNo.text = "${farmerObject.optInt("survey_number", 0)}"
            holder.soilHealthReportLinearLayout.setOnClickListener {
                val intent = Intent(holder.farmerName.context, PdfWebViewActivity::class.java)
                intent.putExtra(
                    "pdf_url",
                    farmerObject.optString(
                        "url",
                        "https://s3.object.webwerksvmx.com/ffsauditlogs/gis-data/gis-data/SHC/531379.pdf"
                    )
                )
                intent.putExtra("shcNumber", farmerObject.optString("shc_no", "N/A"))
                holder.soilHealthReportLinearLayout.context.startActivity(intent)
            }
        }
    }

    private fun mapNPKByColors(
        nitrogen: String,
        phosphorus: String,
        potassium: String,
        electricalConductivity: String,
        organicCarbon: String,
        potentialOfHydrogen: String,
        holder: FarmerViewHolder
    ) {
        val nitroDouble = nitrogen.toDouble()
        val phosDouble = phosphorus.toDouble()
        val potasDouble = potassium.toDouble()
        val electricalConductivityDouble = electricalConductivity.toDouble()
        val organicCarbonDouble = organicCarbon.toDouble()
        val potentialOfHydrogenDouble = potentialOfHydrogen.toDouble()


        if (nitroDouble < 280) {
            holder.nitrogenImageView.setImageResource(R.drawable.ic_soil_health_card_high)
        } else if (nitroDouble > 280 && nitroDouble < 560){
            holder.nitrogenImageView.setImageResource(R.drawable.ic_soil_health_card_low)
        }else{
            holder.nitrogenImageView.setImageResource(R.drawable.ic_soil_health_card_mid)
        }

        if (phosDouble < 14) {
            holder.phosphorusImageView.setImageResource(R.drawable.ic_soil_health_card_high)
        } else if (phosDouble > 15 && phosDouble < 28){
            holder.phosphorusImageView.setImageResource(R.drawable.ic_soil_health_card_low)
        }else{
            holder.phosphorusImageView.setImageResource(R.drawable.ic_soil_health_card_mid)
        }

        if (potasDouble < 150) {
            holder.potassiumImageView.setImageResource(R.drawable.ic_soil_health_card_high)
        } else if (potasDouble > 150 && potasDouble < 250){
            holder.potassiumImageView.setImageResource(R.drawable.ic_soil_health_card_low)
        }else{
            holder.potassiumImageView.setImageResource(R.drawable.ic_soil_health_card_mid)
        }

        if (electricalConductivityDouble < 2) {
            holder.ecImageView.setImageResource(R.drawable.ic_soil_health_card_high)
        } else if (electricalConductivityDouble > 2 && electricalConductivityDouble < 4){
            holder.ecImageView.setImageResource(R.drawable.ic_soil_health_card_low)
        }else{
            holder.ecImageView.setImageResource(R.drawable.ic_soil_health_card_mid)
        }

        if (organicCarbonDouble < 0.4) {
            holder.ocImageView.setImageResource(R.drawable.ic_soil_health_card_high)
        } else if (organicCarbonDouble > 0.4 && organicCarbonDouble < 0.8){
            holder.ocImageView.setImageResource(R.drawable.ic_soil_health_card_low)
        }else{
            holder.ocImageView.setImageResource(R.drawable.ic_soil_health_card_mid)
        }

        if (potentialOfHydrogenDouble < 6.5) {
            holder.phImageView.setImageResource(R.drawable.ic_soil_health_card_high)
        } else if (potentialOfHydrogenDouble > 6.5 && potentialOfHydrogenDouble < 7.5){
            holder.phImageView.setImageResource(R.drawable.ic_soil_health_card_low)
        }else{
            holder.phImageView.setImageResource(R.drawable.ic_soil_health_card_mid)
        }
    }

    override fun getItemCount(): Int = farmerJsonArray?.length() ?: 0
}


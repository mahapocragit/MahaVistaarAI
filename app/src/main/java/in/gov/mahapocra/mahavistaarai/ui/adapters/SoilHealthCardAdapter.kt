package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.PdfWebViewActivity
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
            mapNPKByColors(
                nitrogen,
                phosphorus,
                potassium,
                electricalConductivity,
                organicCarbon,
                potentialOfHydrogen,
                holder
            )
            holder.farmerName.text = farmerObject.optString("farmer_name", "N/A")
            holder.farmSize.text = "${farmerObject.optString("farmsize", "0")} acres"
            holder.shcNo.text = "${farmerObject.optString("shc_no", "N/A")}"
            holder.surveyNo.text = "${farmerObject.optInt("survey_number", 0)}"
            holder.soilHealthReportLinearLayout.setOnClickListener {
                holder.soilHealthReportLinearLayout.context.startActivity(
                    Intent(
                        holder.farmerName.context,
                        PdfWebViewActivity::class.java
                    ).apply {
                        putExtra(
                            "pdf_url",
                            farmerObject.optString(
                                "url",
                                "https://s3.object.webwerksvmx.com/ffsauditlogs/gis-data/gis-data/SHC/531379.pdf"
                            )
                        )
                        putExtra("shcNumber", farmerObject.optString("shc_no", "N/A"))
                    })
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
        val nitroDouble = nitrogen.toDoubleOrNull()
        val phosDouble = phosphorus.toDoubleOrNull()
        val potasDouble = potassium.toDoubleOrNull()
        val electricalConductivityDouble = electricalConductivity.toDoubleOrNull()
        val organicCarbonDouble = organicCarbon.toDoubleOrNull()
        val potentialOfHydrogenDouble = potentialOfHydrogen.toDoubleOrNull()

        // You can either assign default values or skip updates if the value is null
        nitroDouble?.let {
            holder.nitrogenImageView.setImageResource(
                when {
                    it < 280 -> R.drawable.ic_soil_health_card_high
                    it in 280.0..560.0 -> R.drawable.ic_soil_health_card_low
                    else -> R.drawable.ic_soil_health_card_mid
                }
            )
        }

        phosDouble?.let {
            holder.phosphorusImageView.setImageResource(
                when {
                    it < 14 -> R.drawable.ic_soil_health_card_high
                    it in 15.0..28.0 -> R.drawable.ic_soil_health_card_low
                    else -> R.drawable.ic_soil_health_card_mid
                }
            )
        }

        potasDouble?.let {
            holder.potassiumImageView.setImageResource(
                when {
                    it < 150 -> R.drawable.ic_soil_health_card_high
                    it in 150.0..250.0 -> R.drawable.ic_soil_health_card_low
                    else -> R.drawable.ic_soil_health_card_mid
                }
            )
        }

        electricalConductivityDouble?.let {
            holder.ecImageView.setImageResource(
                when {
                    it < 2 -> R.drawable.ic_soil_health_card_high
                    it in 2.0..4.0 -> R.drawable.ic_soil_health_card_low
                    else -> R.drawable.ic_soil_health_card_mid
                }
            )
        }

        organicCarbonDouble?.let {
            holder.ocImageView.setImageResource(
                when {
                    it < 0.4 -> R.drawable.ic_soil_health_card_high
                    it in 0.4..0.8 -> R.drawable.ic_soil_health_card_low
                    else -> R.drawable.ic_soil_health_card_mid
                }
            )
        }

        potentialOfHydrogenDouble?.let {
            holder.phImageView.setImageResource(
                when {
                    it < 6.5 -> R.drawable.ic_soil_health_card_high
                    it in 6.5..7.5 -> R.drawable.ic_soil_health_card_low
                    else -> R.drawable.ic_soil_health_card_mid
                }
            )
        }
    }

    override fun getItemCount(): Int = farmerJsonArray?.length() ?: 0
}


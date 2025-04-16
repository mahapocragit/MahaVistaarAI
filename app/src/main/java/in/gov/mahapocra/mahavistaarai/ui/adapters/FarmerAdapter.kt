package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard.PdfWebViewActivity
import org.json.JSONArray
import org.json.JSONObject

class FarmerAdapter(private val farmerJsonArray: JSONArray?) :
    RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val farmerName: TextView = itemView.findViewById(R.id.farmerName)
        val farmSize: TextView = itemView.findViewById(R.id.farmSize)
        val shcNo: TextView = itemView.findViewById(R.id.shcNo)
        val surveyNo: TextView = itemView.findViewById(R.id.surveyNumber)
        val farmerSoilHealthCard: CardView = itemView.findViewById(R.id.farmerSoilHealthCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.farmer_shc_layout_item, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        farmerJsonArray?.let {
            val farmerObject: JSONObject = it.getJSONObject(position)

            holder.farmerName.text = farmerObject.optString("farmer_name", "N/A")
            holder.farmSize.text = "Farm Size: ${farmerObject.optString("farmsize", "0")} acres"
            holder.shcNo.text = "SHC No: ${farmerObject.optString("shc_no", "N/A")}"
            holder.surveyNo.text = "Survey No: ${farmerObject.optInt("survey_number", 0)}"
            holder.farmerSoilHealthCard.setOnClickListener {
                val intent = Intent(holder.farmerName.context, PdfWebViewActivity::class.java)
                intent.putExtra("pdf_url", farmerObject.optString("url", "https://s3.object.webwerksvmx.com/ffsauditlogs/gis-data/gis-data/SHC/531379.pdf"))
                holder.farmerSoilHealthCard.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = farmerJsonArray?.length() ?: 0
}


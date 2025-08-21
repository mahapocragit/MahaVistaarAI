package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.soilhealthcard

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class SoilHealthCardAdapter(private val farmerJsonArray: JSONArray?) :
    RecyclerView.Adapter<SoilHealthCardAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val farmerNameTextView = itemView.findViewById<TextView>(R.id.farmerName)
        internal val soilHealthCardNoTextView = itemView.findViewById<TextView>(R.id.shcNo)
        internal val surveyNumberTextView = itemView.findViewById<TextView>(R.id.surveyNumber)
        internal val farmSizeTextView = itemView.findViewById<TextView>(R.id.farmSize)
        internal val soilHealthReportLL =
            itemView.findViewById<LinearLayout>(R.id.soilHealthReportLinearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_soil_health_card, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        val context = holder.itemView.context
        farmerJsonArray?.let {
            val healthObject: JSONObject = it.getJSONObject(position)
            val status = healthObject.optString("status")
            val healthCardNumber = healthObject.optString("computedID")
            val farmerObject = healthObject.optJSONObject("farmer")
            val plotObject = healthObject.optJSONObject("plot")
            val surveyNo = plotObject.optString("surveyNo")
            val farmerName = farmerObject.optString("name")
            holder.farmerNameTextView.text = farmerName
            holder.soilHealthCardNoTextView.text = healthCardNumber
            holder.surveyNumberTextView.text = surveyNo
            holder.farmSizeTextView.text = status
            holder.soilHealthReportLL.setOnClickListener {
                context.startActivity(
                    Intent(context, PdfWebViewActivity::class.java)
                        .apply {
                            putExtra("healthCardObj", healthObject.toString())
                        })
            }
        }
    }

    override fun getItemCount(): Int = farmerJsonArray?.length() ?: 0
}


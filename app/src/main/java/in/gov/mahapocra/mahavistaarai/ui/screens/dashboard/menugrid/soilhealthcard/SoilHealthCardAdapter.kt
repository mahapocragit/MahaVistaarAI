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
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.getRatingImageResource
import org.json.JSONArray
import org.json.JSONObject

class SoilHealthCardAdapter(private val soilHealthCardArrayJsonArray: JSONArray?) :
    RecyclerView.Adapter<SoilHealthCardAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val farmerName: TextView = itemView.findViewById(R.id.farmerName)
        val farmSize: TextView = itemView.findViewById(R.id.farmSize)
        val shcNo: TextView = itemView.findViewById(R.id.shcNo)
        val surveyNo: TextView = itemView.findViewById(R.id.surveyNumber)
        val nitrogenImageView: ImageView = itemView.findViewById(R.id.nitrogenImageView)
        val phosphorusImageView: ImageView = itemView.findViewById(R.id.phosphorusImageView)
        val potassiumImageView: ImageView = itemView.findViewById(R.id.potassiumImageView)
        val soilHealthReportLinearLayout: LinearLayout =
            itemView.findViewById(R.id.soilHealthReportLinearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_soil_health_card, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        val context = holder.itemView.context
        soilHealthCardArrayJsonArray?.let {
            val templateJson: JSONObject = it.getJSONObject(position)
            val farmerObject = templateJson.optJSONObject("farmer")
            val rdfValuesObject = templateJson.optJSONObject("rdfValues")
            val parameterInfosArray = rdfValuesObject?.optJSONArray("parameterInfos")
            for (position in 0 until parameterInfosArray!!.length()) {
                val jsonObject = parameterInfosArray.optJSONObject(position)
                val parameterKey = jsonObject?.optString("key")
                val parameterRating = jsonObject?.optString("rating")
                if (parameterKey == "n") {
                    parameterRating?.let { rating ->
                        holder.nitrogenImageView.setImageResource(
                            getRatingImageResource(rating)
                        )
                    }
                }
                if (parameterKey == "p") {
                    parameterRating?.let { rating ->
                        holder.phosphorusImageView.setImageResource(
                            getRatingImageResource(rating)
                        )
                    }
                }
                if (parameterKey == "k") {
                    parameterRating?.let { rating ->
                        holder.potassiumImageView.setImageResource(
                            getRatingImageResource(rating)
                        )
                    }
                }
            }
            val plotObject = templateJson.optJSONObject("plot")
            holder.farmerName.text = farmerObject?.optString("name", "N/A")
            holder.farmSize.text = buildString {
                append(plotObject?.optString("area", "0"))
                append(" acres")
            }
            holder.shcNo.text = "${templateJson.optString("computedID", "N/A")}"
            holder.surveyNo.text = "${plotObject?.optInt("surveyNo", 0)}"
            holder.soilHealthReportLinearLayout.setOnClickListener {
                context.startActivity(
                    Intent(
                        context,
                        PdfWebViewActivity::class.java
                    ).putExtra("healthCardJson", templateJson.toString())
                )
            }
        }
    }

    override fun getItemCount(): Int = soilHealthCardArrayJsonArray?.length() ?: 0
}


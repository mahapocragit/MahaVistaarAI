package `in`.gov.mahapocra.farmerapppks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapppks.R
import org.json.JSONArray
import org.json.JSONObject

class FarmerAdapter(private val farmerJsonArray: JSONArray?) :
    RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val farmerName: TextView = itemView.findViewById(R.id.farmerName)
        val farmSize: TextView = itemView.findViewById(R.id.farmSize)
        val shcNo: TextView = itemView.findViewById(R.id.shcNo)
        val surveyNo: TextView = itemView.findViewById(R.id.surveyNumber)
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
        }
    }

    override fun getItemCount(): Int = farmerJsonArray?.length() ?: 0
}


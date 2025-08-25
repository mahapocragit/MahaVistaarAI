package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R

class CostCalculatorAdapter: RecyclerView.Adapter<CostCalculatorAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cost_calculator_crop_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val context = holder.itemView.context
        holder.cropName.text = "Kharif Maize"
        holder.selectedCropLinearLayout.setOnClickListener {
            context.startActivity(Intent(context, CropCostCalculationActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cropName: TextView = itemView.findViewById(R.id.cropNameTextView)
        val selectedCropLinearLayout: LinearLayout = itemView.findViewById(R.id.selectedCropCalculationLinearLayout)
    }
}
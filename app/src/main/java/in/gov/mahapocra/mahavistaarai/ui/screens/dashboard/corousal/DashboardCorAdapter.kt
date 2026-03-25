package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.corousal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R

class DashboardCorAdapter(
    private val items: List<DashboardItemCor>,
    private val handleViewClick: HandleViewClick
) : RecyclerView.Adapter<DashboardCorAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val value: TextView = view.findViewById(R.id.tvValue)
        val container: LinearLayout = view.findViewById(R.id.cardContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val realPosition = position % items.size   // 🔁 circular mapping
        val item = items[realPosition]

        holder.title.text = item.title
        holder.value.text = item.value
        holder.container.setBackgroundResource(item.gradientRes)
        holder.container.setOnClickListener {
            handleViewClick.onCorItemClick(realPosition)
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE // 🔥 infinite
}
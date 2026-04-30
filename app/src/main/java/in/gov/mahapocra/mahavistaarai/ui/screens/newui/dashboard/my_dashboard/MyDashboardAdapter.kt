package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemMyDashboardBinding

class MyDashboardAdapter(
    private val list: List<MyDashboardModel>
) : RecyclerView.Adapter<MyDashboardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMyDashboardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyDashboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        holder.itemView.layoutParams.width = screenWidth / 2
        holder.binding.imageView.setImageDrawable(item.drawable)
        holder.binding.titleTextView.text = item.title
        holder.binding.descriptionTextView.text = item.description
    }

    override fun getItemCount(): Int = list.size
}
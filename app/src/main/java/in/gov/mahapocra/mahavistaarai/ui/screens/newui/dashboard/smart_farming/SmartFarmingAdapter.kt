package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.smart_farming

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemSmartFarmingBinding

class SmartFarmingAdapter(
    private val list: List<SmartFarmingModel>,
    private val listener:OnRecyclerItemClickListener
) : RecyclerView.Adapter<SmartFarmingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSmartFarmingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSmartFarmingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.imageView.setImageDrawable(item.drawable)
        holder.binding.textView.text = item.title
        holder.itemView.setOnClickListener {
            listener.onRecyclerViewItemClick(item)
        }
    }

    override fun getItemCount(): Int = list.size
}
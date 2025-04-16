package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.forum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapp.databinding.ItemForumSingleViewBinding

class ForumViewPostAdapter : RecyclerView.Adapter<ForumViewPostAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemForumSingleViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.textView8.text = "$position Mins Ago" // Example binding
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position) // FIXED
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForumSingleViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}
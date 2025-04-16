package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.forum

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapp.databinding.ItemTrendingForumBinding

class ForumTrendingAdapter : RecyclerView.Adapter<ForumTrendingAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemTrendingForumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.textView22.text = "3$position Discussions" // Example binding
            binding.cardTrendingView.setOnClickListener {
                binding.textView21.context.startActivity(Intent(binding.textView21.context, ForumViewActivity::class.java))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position) // FIXED
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrendingForumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}
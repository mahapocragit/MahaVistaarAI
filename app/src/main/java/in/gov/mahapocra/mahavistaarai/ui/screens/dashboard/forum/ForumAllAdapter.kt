package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.forum

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemForumAllBinding

class ForumAllAdapter : RecyclerView.Adapter<ForumAllAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemForumAllBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.textView8.text = "$position Mins Ago" // Example binding
            binding.cardAllView.setOnClickListener {
                binding.cardAllView.context.startActivity(Intent(binding.cardAllView.context, ForumPostDetailsActivity::class.java))
            }
            binding.commentLayout.setOnClickListener {
                binding.commentLayout.context.startActivity(Intent(binding.commentLayout.context, SingleReplyActivity::class.java))
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
        val binding = ItemForumAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}
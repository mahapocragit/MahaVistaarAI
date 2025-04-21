package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemVideosBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemVideosCategoryBinding
import org.json.JSONArray
import org.json.JSONObject

class VideosCategoryAdapter(private val categoryArray:JSONArray) : RecyclerView.Adapter<VideosCategoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemVideosCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jsonObject: JSONObject) {
            val activityName = jsonObject.optString("name")
            binding.textView21.text = activityName
            binding.cardTrendingView.setOnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, VideosDetailedActivity::class.java).apply {
                    putExtra("videosJsonObject", jsonObject.toString())
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVideosCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val farmerObject = categoryArray.getJSONObject(position)
        holder.bind(farmerObject)
    }
}
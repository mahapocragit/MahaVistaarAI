package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemVideosCategoryBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video.VideosDetailedActivity
import org.json.JSONArray
import org.json.JSONObject

class VideosCategoryAdapter(
    private val categoryArray: JSONArray,
    private val languageToLoad: String
) : RecyclerView.Adapter<VideosCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemVideosCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(videoCategory: JSONObject) {
            val context = binding.root.context
            val title = if (languageToLoad == "mr") {
                videoCategory.optString("name_mr")
            } else {
                videoCategory.optString("name")
            }

            binding.textView21.text = title

            Glide.with(binding.imageView23)
                .load(videoCategory.optString("thumbnail"))
                .into(binding.imageView23)

            val videosCount = videoCategory.optJSONArray("links")?.length() ?: 0
            binding.videosCount.text = buildString {
                append(videosCount)
                append(" ")
                append(binding.root.context.getString(R.string.videos))
            }

            binding.cardTrendingView.setOnClickListener {
                val intent = Intent(context, VideosDetailedActivity::class.java).apply {
                    putExtra("videosJsonObject", videoCategory.toString())
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVideosCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = categoryArray.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categoryArray.optJSONObject(position) ?: return
        holder.bind(item)
    }
}
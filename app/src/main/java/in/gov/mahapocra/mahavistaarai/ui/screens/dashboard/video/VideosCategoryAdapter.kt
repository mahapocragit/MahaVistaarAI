package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemVideosCategoryBinding
import org.json.JSONArray
import org.json.JSONObject

class VideosCategoryAdapter(
    private val categoryArray: JSONArray,
    private val languageToLoad: String
) : RecyclerView.Adapter<VideosCategoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemVideosCategoryBinding, val languageToLoad: String) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jsonObject: JSONObject, languageToLoad: String) {
            var activityName = jsonObject.optString("name")
            if (languageToLoad == "mr") {
                activityName = jsonObject.optString("name_mr")
            }
            binding.textView21.text = activityName
            Glide.with(binding.imageView23).load(jsonObject.optString("thumbnail"))
                .into(binding.imageView23)
            binding.cardTrendingView.setOnClickListener {
                binding.root.context.startActivity(
                    Intent(
                        binding.root.context,
                        VideosDetailedActivity::class.java
                    ).apply {
                        putExtra("videosJsonObject", jsonObject.toString())
                    })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVideosCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, languageToLoad)
    }

    override fun getItemCount(): Int {
        return categoryArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val farmerObject = categoryArray.getJSONObject(position)
        holder.bind(farmerObject, languageToLoad)
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemVideosBinding
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray
import org.json.JSONObject

class VideosAdapter(
    private val categoryArray: JSONArray,
    val recyclerItemClickListener: RecyclerItemClickListener
) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemVideosBinding, private val listener: RecyclerItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jsonObject: JSONObject) {
            val activityName = jsonObject.optString("name")
            binding.textView21.text = activityName
            binding.cardTrendingView.setOnClickListener {
                val videoUrl = jsonObject.optString("link")
                val context = binding.root.context
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                intent.setPackage("com.google.android.youtube") // optional: open in YouTube app
                try {
                    listener.onRecyclerItemClick(1, videoUrl)
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // fallback to any browser if YouTube app not found
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)))
                }
            }
            Glide.with(binding.imageView23).load(jsonObject.optString("thumnail"))
                .into(binding.imageView23)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return categoryArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val farmerObject = categoryArray.getJSONObject(position)
        holder.bind(farmerObject)
    }
}
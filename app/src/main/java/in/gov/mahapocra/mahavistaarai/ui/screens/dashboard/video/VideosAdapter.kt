package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.ActivityNotFoundException
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.FarmersDbtItemViewBinding
import org.json.JSONArray
import org.json.JSONObject

class VideosAdapter(private val categoryArray:JSONArray) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {
    class ViewHolder(val binding: FarmersDbtItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jsonObject: JSONObject) {
            val activityName = jsonObject.optString("name")
            binding.dbtSchemeName.text = activityName
            binding.farmerDbtCardView.setOnClickListener {
                val videoUrl = jsonObject.optString("link")
                val context = binding.root.context
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                intent.setPackage("com.google.android.youtube") // optional: open in YouTube app
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // fallback to any browser if YouTube app not found
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FarmersDbtItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
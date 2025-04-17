package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.FarmersDbtItemViewBinding
import org.json.JSONArray
import org.json.JSONObject

class VideosCategoryAdapter(private val categoryArray:JSONArray) : RecyclerView.Adapter<VideosCategoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: FarmersDbtItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jsonObject: JSONObject) {
            val activityName = jsonObject.optString("name")
            binding.dbtSchemeName.text = activityName
            binding.farmerDbtCardView.setOnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, VideosDetailedActivity::class.java).apply {
                    putExtra("videosJsonObject", jsonObject.toString())
                })
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
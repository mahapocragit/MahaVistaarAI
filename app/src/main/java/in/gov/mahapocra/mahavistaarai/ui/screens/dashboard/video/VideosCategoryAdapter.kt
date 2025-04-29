package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.video

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemVideosCategoryBinding
import org.json.JSONArray
import org.json.JSONObject

class VideosCategoryAdapter(private val categoryArray:JSONArray) : RecyclerView.Adapter<VideosCategoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemVideosCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jsonObject: JSONObject) {
            val activityName = jsonObject.optString("name")
            binding.textView21.text = activityName
            setImageByActivityName(activityName, binding)
            binding.cardTrendingView.setOnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, VideosDetailedActivity::class.java).apply {
                    putExtra("videosJsonObject", jsonObject.toString())
                })
            }
        }

        private fun setImageByActivityName(activityName: String, binding: ItemVideosCategoryBinding) {
            when(activityName){
                "NRM"->binding.imageView23.setImageResource(R.drawable.nrm)
                "CRT"->binding.imageView23.setImageResource(R.drawable.crt)
                "Crop Cultivation"->binding.imageView23.setImageResource(R.drawable.cropcultivation)
                "Crop Care"->binding.imageView23.setImageResource(R.drawable.cropcare)
                "Agroforestry"->binding.imageView23.setImageResource(R.drawable.agroforestry)
                "Farmer Group"->binding.imageView23.setImageResource(R.drawable.farmergroup)
                "Agri Business"->binding.imageView23.setImageResource(R.drawable.agribusiness)
                "Success Story"->binding.imageView23.setImageResource(R.drawable.successstory)
                "Animal Husbandry"->binding.imageView23.setImageResource(R.drawable.animalhubandary)
                "FFS"->binding.imageView23.setImageResource(R.drawable.ffs)
                "Other"->binding.imageView23.setImageResource(R.drawable.other)
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
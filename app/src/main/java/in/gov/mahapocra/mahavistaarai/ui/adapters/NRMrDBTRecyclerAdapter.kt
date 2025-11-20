package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.FarmersDbtItemViewBinding
import org.json.JSONArray

class NRMrDBTRecyclerAdapter(
    private val farmersJSONArray: JSONArray,
    private val languageToLoad: String
) : RecyclerView.Adapter<NRMrDBTRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FarmersDbtItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return farmersJSONArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val farmerObject = farmersJSONArray.getJSONObject(position)
        val activityName = if (languageToLoad == "mr"){
            farmerObject.getString("activityNameMr")
        }else{
            farmerObject.getString("activityName")
        }
        holder.bind(activityName)
    }

    class ViewHolder(
        private val binding: FarmersDbtItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activityName: String) {
            binding.dbtSchemeName.text = activityName
        }
    }
}
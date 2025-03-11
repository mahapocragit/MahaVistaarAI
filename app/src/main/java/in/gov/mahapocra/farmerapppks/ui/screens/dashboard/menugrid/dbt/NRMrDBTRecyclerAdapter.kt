package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.dbt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.farmerapppks.databinding.FarmersDbtItemViewBinding
import org.json.JSONArray
import org.json.JSONObject

class NRMrDBTRecyclerAdapter(
    private val farmersJSONArray: JSONArray,
    private val languageToLoad: String,
    private val listener: OnMultiRecyclerItemClickListener
) : RecyclerView.Adapter<NRMrDBTRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FarmersDbtItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
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
        holder.bind(activityName, farmerObject)
    }

    class ViewHolder(
        private val binding: FarmersDbtItemViewBinding,
        private val listener: OnMultiRecyclerItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activityName: String, jsonObject: JSONObject) {
            binding.dbtSchemeName.text = activityName
            binding.farmerDbtCardView.setOnClickListener {
//                listener.onMultiRecyclerViewItemClick(1, jsonObject)
            }
        }
    }
}
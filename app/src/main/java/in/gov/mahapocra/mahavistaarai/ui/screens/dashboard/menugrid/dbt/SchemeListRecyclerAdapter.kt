package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.databinding.FarmersDbtItemViewBinding
import org.json.JSONArray
import org.json.JSONObject

class SchemeListRecyclerAdapter(
    private val farmersJSONArray: JSONArray,
    private val languageToLoad: String
) : RecyclerView.Adapter<SchemeListRecyclerAdapter.ViewHolder>() {


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
            farmerObject.getString("SchemeNameMarathi")
        }else{
            farmerObject.getString("SchemeName")
        }
        holder.bind(activityName, farmerObject)
    }

    class ViewHolder(
        private val binding: FarmersDbtItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activityName: String, jsonObject: JSONObject) {
            binding.dbtSchemeName.text = activityName
            binding.farmerDbtCardView.setOnClickListener {
                val intent = Intent(binding.farmerDbtCardView.context, DbtSchemesDetailsActivity::class.java)
                intent.putExtra("FARMERDBTRESPONSE", jsonObject.toString())
                binding.farmerDbtCardView.context.startActivity(intent)
            }
        }
    }
}
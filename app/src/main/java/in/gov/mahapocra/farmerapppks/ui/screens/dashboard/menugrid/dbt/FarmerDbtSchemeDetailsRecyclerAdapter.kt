package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.dbt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.farmerapppks.databinding.FarmersDbtItemViewBinding
import org.json.JSONArray
import org.json.JSONObject

class FarmerDbtSchemeDetailsRecyclerAdapter(
    private val importantDocumentsArray: List<String>
) : RecyclerView.Adapter<FarmerDbtSchemeDetailsRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FarmersDbtItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return importantDocumentsArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(importantDocumentsArray[position])
    }

    class ViewHolder(private val binding: FarmersDbtItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(activityName: String) {
            binding.dbtSchemeName.text = activityName
        }
    }
}
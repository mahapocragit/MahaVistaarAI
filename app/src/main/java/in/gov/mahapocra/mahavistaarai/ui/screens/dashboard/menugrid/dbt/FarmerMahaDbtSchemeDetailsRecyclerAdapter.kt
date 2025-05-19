package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.FarmersDbtItemViewBinding
import org.json.JSONArray

class FarmerMahaDbtSchemeDetailsRecyclerAdapter(
    private val importantDocumentsArray: JSONArray
) : RecyclerView.Adapter<FarmerMahaDbtSchemeDetailsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FarmersDbtItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return importantDocumentsArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val documentName = importantDocumentsArray.optString(position)
        holder.bind(documentName)
    }

    class ViewHolder(private val binding: FarmersDbtItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(documentName: String) {
            binding.dbtSchemeName.text = "• $documentName"
        }
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemCropBinding
import org.json.JSONObject

class CropSelectionAdapter(
    private var list: MutableList<JSONObject>,
    private val onClick: (JSONObject) -> Unit
) : RecyclerView.Adapter<CropSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCropBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCropBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val obj = list[position]

        holder.binding.tvCropName.text =
            "${obj.optString("name")} (${obj.optString("name_mr")})"

        holder.binding.root.setOnClickListener {
            onClick(obj)
        }
    }

    fun updateList(newList: List<JSONObject>) {
        list = newList.toMutableList()
        notifyDataSetChanged()
    }
}
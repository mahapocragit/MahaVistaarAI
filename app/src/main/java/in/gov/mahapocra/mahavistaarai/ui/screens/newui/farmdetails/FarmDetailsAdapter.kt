package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.DcsCropInfoItemBinding
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray

class FarmDetailsAdapter(
    private val jsonArray: JSONArray,
    private val listener: RecyclerItemClickListener
) : RecyclerView.Adapter<FarmDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DcsCropInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DcsCropInfoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = jsonArray.optJSONObject(position)
        holder.binding.cropNameTextView.text = item.optString("crop_name")
        holder.binding.sowingDateTextView.text = "Sown on ${item.optString("sowing_date")}"
        holder.binding.updateCropImageView.setOnClickListener {
            listener.onRecyclerItemClick(1, item)
        }
        holder.binding.deleteCropImageView.setOnClickListener {
            listener.onRecyclerItemClick(2, item)
        }
    }

    override fun getItemCount(): Int = jsonArray.length()
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.DcsCropInfoHorzItemBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.DcsCropInfoItemBinding
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray
import org.json.JSONObject

class CropListDetailsAdapter(
    private val jsonArray: JSONArray,
    private val listener: RecyclerItemClickListener,
    private val parentFarmObject: JSONObject,
    private val parentPosition: Int
) : RecyclerView.Adapter<CropListDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DcsCropInfoHorzItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DcsCropInfoHorzItemBinding.inflate(
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

            val updateObject = JSONObject()

            updateObject.put(
                "crop_position",
                position
            )

            updateObject.put(
                "parent_position",
                parentPosition
            )

            updateObject.put(
                "farm_object",
                parentFarmObject
            )

            updateObject.put(
                "declaration_id",
                item.optString("declaration_id")
            )

            updateObject.put(
                "crop_object",
                item
            )

            listener.onRecyclerItemClick(
                6,
                updateObject
            )
        }
        holder.binding.deleteCropImageView.setOnClickListener {

            val deleteObject = JSONObject()

            deleteObject.put(
                "crop_position",
                position
            )

            deleteObject.put(
                "parent_position",
                parentPosition
            )

            deleteObject.put(
                "farm_object",
                parentFarmObject
            )

            deleteObject.put(
                "declaration_id",
                item.optString("declaration_id")
            )

            listener.onRecyclerItemClick(
                5,
                deleteObject
            )
        }
    }

    override fun getItemCount(): Int = jsonArray.length()
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.MyFarmsItemViewBinding
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray

class MyFarmsDCSAdapter(
    private val jsonArray: JSONArray,
    private val listeners: RecyclerItemClickListener
) : RecyclerView.Adapter<MyFarmsDCSAdapter.LandViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class LandViewHolder(
        val binding: MyFarmsItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LandViewHolder {

        val binding = MyFarmsItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LandViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LandViewHolder,
        position: Int
    ) {

        val binding = holder.binding

        val obj = jsonArray.getJSONObject(position)

        binding.farmIdTextView.text =
            obj.optString("farm_id")

        binding.surveyNumberTextView.text =
            "Survey No. ${obj.optString("survey_no")}"

        binding.areaTextView.text =
            "${obj.optString("total_plot_area")} Acre"

        // selected crop name
        binding.cropNameForDCSTextView.text =
            obj.optString("selected_crop_name", "Select Crop")



        binding.selectCropCardView.setOnClickListener {

            obj.put("adapter_position", position)

            listeners.onRecyclerItemClick(2, obj)
        }
        binding.sowingDateCropDCSTextView.text =
            obj.optString("selected_sowing_date", "Select Date")
        binding.sowingDateCardView.setOnClickListener {

            obj.put("adapter_position", position)

            listeners.onRecyclerItemClick(3, obj)
        }

        val isExpanded =
            expandedPositions.contains(position)

        expandView(isExpanded, binding)

        binding.expandOperationImageView.setOnClickListener {

            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }

            notifyItemChanged(position)
        }

        binding.cancelCropButton.setOnClickListener {

            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }

            notifyItemChanged(position)
        }

        binding.saveCropButton.setOnClickListener {
            listeners.onRecyclerItemClick(4, obj)
            expandedPositions.remove(position)
        }
    }

    private fun expandView(
        expanded: Boolean,
        binding: MyFarmsItemViewBinding
    ) {

        binding.selectCropTextView.visibility =
            if (expanded) View.VISIBLE else View.GONE

        binding.selectCropCardView.visibility =
            if (expanded) View.VISIBLE else View.GONE

        binding.sowingDateTextView.visibility =
            if (expanded) View.VISIBLE else View.GONE

        binding.sowingDateCardView.visibility =
            if (expanded) View.VISIBLE else View.GONE

        binding.buttonsLayout.visibility =
            if (expanded) View.VISIBLE else View.GONE

        binding.expandOperationImageView.rotation =
            if (expanded) 180f else 0f
    }

    override fun getItemCount(): Int =
        jsonArray.length()
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.databinding.MyFarmsItemViewBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.CropListDetailsAdapter
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray
import org.json.JSONObject

class MyFarmsDCSAdapter(
    private val jsonArray: JSONArray,
    private val listener: RecyclerItemClickListener
) : RecyclerView.Adapter<MyFarmsDCSAdapter.LandViewHolder>() {

    // Expand / Collapse Saved Crops Recycler
    private val expandedItems = mutableSetOf<Int>()

    // Expand / Collapse Add Crop Form
    private val expandedCropFormItems = mutableSetOf<Int>()

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

        val farmObject =
            jsonArray.getJSONObject(position)

        setupBasicDetails(binding, farmObject)

        setupCropSection(
            binding,
            farmObject,
            position
        )

        setupExpandCollapse(
            binding,
            position
        )

        setupSavedCropsRecycler(
            binding,
            farmObject
        )
    }

    // ---------------------------------------------------
    // Basic Farm Details
    // ---------------------------------------------------

    private fun setupBasicDetails(
        binding: MyFarmsItemViewBinding,
        obj: JSONObject
    ) {

        binding.farmIdTextView.text =
            obj.optString("farm_id")

        binding.surveyNumberTextView.text =
            "Survey No. ${obj.optString("survey_no")}"

        binding.areaTextView.text =
            "${obj.optString("total_plot_area")} Acre"
    }

    // ---------------------------------------------------
    // Crop Section
    // ---------------------------------------------------

    private fun setupCropSection(
        binding: MyFarmsItemViewBinding,
        obj: JSONObject,
        position: Int
    ) {

        // Selected Crop
        binding.cropNameForDCSTextView.text =
            obj.optString(
                "selected_crop_name",
                "Select Crop"
            )

        // Selected Date
        binding.sowingDateCropDCSTextView.text =
            obj.optString(
                "selected_sowing_date",
                "Select Date"
            )

        // Restore Form State
        val isCropFormExpanded =
            expandedCropFormItems.contains(position)

        updateExpandedViewForAddCrop(
            binding,
            isCropFormExpanded
        )

        // Select Crop
        binding.selectCropCardView.setOnClickListener {

            obj.put(
                "adapter_position",
                position
            )

            listener.onRecyclerItemClick(
                2,
                obj
            )
        }

        // Select Date
        binding.sowingDateCardView.setOnClickListener {

            obj.put(
                "adapter_position",
                position
            )

            listener.onRecyclerItemClick(
                3,
                obj
            )
        }

        // Open Form
        binding.addCropLayout.setOnClickListener {

            expandedCropFormItems.add(position)

            notifyItemChanged(position)
        }

        // Cancel Form
        binding.cancelCropButton.setOnClickListener {

            expandedCropFormItems.remove(position)

            notifyItemChanged(position)
        }

        // Save Crop
        binding.saveCropButton.setOnClickListener {

            expandedCropFormItems.remove(position)

            notifyItemChanged(position)

            listener.onRecyclerItemClick(
                4,
                obj
            )
        }
    }

    // ---------------------------------------------------
    // Expand / Collapse Saved Crops
    // ---------------------------------------------------

    private fun setupExpandCollapse(
        binding: MyFarmsItemViewBinding,
        position: Int
    ) {

        val isExpanded =
            expandedItems.contains(position)

        updateExpandedView(
            binding,
            isExpanded
        )

        binding.expandOperationImageView.setOnClickListener {

            toggleExpansion(position)
        }
    }

    private fun toggleExpansion(position: Int) {

        if (expandedItems.contains(position)) {

            expandedItems.remove(position)

        } else {

            expandedItems.add(position)
        }

        notifyItemChanged(position)
    }

    private fun updateExpandedView(
        binding: MyFarmsItemViewBinding,
        expanded: Boolean
    ) {

        binding.savedCropsRecyclerView.visibility =
            if (expanded) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.addCropLayout.visibility =
            if (expanded) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.expandOperationImageView.rotation =
            if (expanded) {
                180f
            } else {
                0f
            }
    }

    // ---------------------------------------------------
    // Add Crop Form Visibility
    // ---------------------------------------------------

    private fun updateExpandedViewForAddCrop(
        binding: MyFarmsItemViewBinding,
        expanded: Boolean
    ) {

        binding.selectCropTextView.visibility =
            if (expanded) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.selectCropCardView.visibility =
            if (expanded) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.sowingDateTextView.visibility =
            if (expanded) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.sowingDateCardView.visibility =
            if (expanded) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.buttonsLayout.visibility =
            if (expanded) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    // ---------------------------------------------------
    // Saved Crops Recycler
    // ---------------------------------------------------

    private fun setupSavedCropsRecycler(
        binding: MyFarmsItemViewBinding,
        obj: JSONObject
    ) {

        val cropsArray =
            obj.optJSONArray("crops")
                ?: JSONArray()

        binding.savedCropsRecyclerView.layoutManager =
            LinearLayoutManager(
                binding.root.context
            )

        binding.savedCropsRecyclerView.adapter =
            CropListDetailsAdapter(
                cropsArray,
                listener
            )
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}
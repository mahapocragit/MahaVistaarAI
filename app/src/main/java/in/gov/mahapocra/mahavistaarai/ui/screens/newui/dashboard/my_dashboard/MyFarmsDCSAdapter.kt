package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.MyFarmsItemViewBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.CropListDetailsAdapter
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray
import org.json.JSONObject

class MyFarmsDCSAdapter(
    private var jsonArray: JSONArray,
    private val listener: RecyclerItemClickListener,
    private val language: String
) : RecyclerView.Adapter<MyFarmsDCSAdapter.LandViewHolder>() {

    // Expand / Collapse Saved Crops Recycler
    private val expandedItems = mutableSetOf<Int>()
    private var showAddCropButton = true

    // Expand / Collapse Add Crop Form
    private val expandedCropFormItems = mutableSetOf<Int>()

    class LandViewHolder(
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
            farmObject,
            position
        )
    }

    // ---------------------------------------------------
    // Basic Farm Details
    // ---------------------------------------------------

    private fun setupBasicDetails(
        binding: MyFarmsItemViewBinding,
        obj: JSONObject
    ) {

        val context = binding.root.context
        binding.farmIdTextView.text =
            obj.optString("farm_id")

        binding.surveyNumberTextView.text =
            buildString {
                append(context.getString(R.string.survey_no))
                append(" ")
                append(obj.optString("survey_no"))
            }

        binding.areaTextView.text =
            buildString {
                append(obj.optString("total_plot_area"))
                append(" ")
                append(context.getString(R.string.acre))
            }
    }

    // ---------------------------------------------------
    // Crop Section
    // ---------------------------------------------------

    private fun setupCropSection(
        binding: MyFarmsItemViewBinding,
        obj: JSONObject,
        position: Int
    ) {
        Log.d(TAG, "setupCropSection: $obj")
        // Selected Crop
        binding.cropNameForDCSTextView.text =
            obj.optString(
                "selected_crop_name",
                binding.root.context.getString(R.string.select_crop)
            )
        val villageName = obj.optString("village_name")
        val villageNameMr = obj.optString("village_name_mr")
        // Selected Date
        binding.sowingDateCropDCSTextView.text =
            obj.optString(
                "selected_sowing_date",
                binding.root.context.getString(R.string.farmer_select_date)
            )

        // Restore Form State
        val isCropFormExpanded =
            expandedCropFormItems.contains(position)

        updateExpandedViewForAddCrop(
            binding,
            isCropFormExpanded
        )

        binding.villageName.text = if (language == "en") villageName else villageNameMr

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

            val selectedCropName =
                obj.optString("selected_crop_name")

            val selectedSowingDate =
                obj.optString("selected_sowing_date")

            // 🔴 Validate Crop
            if (selectedCropName.isNullOrEmpty() ||
                selectedCropName == binding.root.context.getString(R.string.select_crop)
            ) {
                Toast.makeText(
                    binding.root.context,
                    "Please select crop",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 🔴 Validate Date
            if (selectedSowingDate.isNullOrEmpty() ||
                selectedSowingDate == binding.root.context.getString(R.string.farmer_select_date)
            ) {
                Toast.makeText(
                    binding.root.context,
                    "Please select sowing date",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // If valid → call listener
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
                if (showAddCropButton) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
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
        obj: JSONObject,
        position: Int
    ) {

        val cropsArray =
            obj.optJSONArray("crops")
                ?: JSONArray()

        showAddCropButton = cropsArray.length() < 2
        if (!showAddCropButton) {
            binding.addCropLayout.visibility = View.GONE
        } else {
            val isExpanded =
                expandedItems.contains(position)
            if (isExpanded) {
                binding.addCropLayout.visibility = View.VISIBLE
            }
        }
        binding.savedCropsRecyclerView.apply {

            layoutManager =
                LinearLayoutManager(context)

            setHasFixedSize(true)

            isNestedScrollingEnabled = false

            adapter =
                CropListDetailsAdapter(
                    cropsArray,
                    listener,
                    obj,
                    position
                )
        }
    }

    fun updateEntireData(
        updatedArray: JSONArray
    ) {

        jsonArray = updatedArray

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.data.model.SubCategory
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemSubCategoryBinding

class SubCategoryAdapter(
    private val subCategories: List<SubCategory>,
    private val onSelectionChanged: (List<Int>) -> Unit
) : RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder>() {

    inner class SubCategoryViewHolder(val binding: ItemSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val binding = ItemSubCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        val subCategory = subCategories[position]
        holder.binding.subCategoryCheckBox.text = subCategory.name
        holder.binding.subCategoryCheckBox.isChecked = subCategory.isSelected

        holder.binding.subCategoryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            subCategory.isSelected = isChecked
            onSelectionChanged(subCategories.filter { it.isSelected }.map { it.id })
        }

        holder.binding.root.setOnClickListener {
            subCategory.isSelected = !subCategory.isSelected
            onSelectionChanged(subCategories.filter { it.isSelected }.map { it.id })
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = subCategories.size
}


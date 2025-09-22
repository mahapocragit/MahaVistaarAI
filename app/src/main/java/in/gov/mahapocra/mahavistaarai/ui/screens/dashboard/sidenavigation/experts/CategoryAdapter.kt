package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.data.model.Category
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<Category>,
    private val onSelectionChanged: (List<Int>) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryCheckBox.text = category.name
        holder.binding.categoryCheckBox.isChecked = category.isSelected

        holder.binding.categoryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            category.isSelected = isChecked
            onSelectionChanged(categories.filter { it.isSelected }.map { it.id })
        }

        holder.binding.categoryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            category.isSelected = isChecked
            onSelectionChanged(categories.filter { it.isSelected }.map { it.id })
        }
    }

    override fun getItemCount(): Int = categories.size
}


package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R

class AdvisoryAdapter(
    private val list: List<AdvisoryModel>,
    private val onItemClick: (AdvisoryModel) -> Unit
) : RecyclerView.Adapter<AdvisoryAdapter.AdvisoryViewHolder>() {

    inner class AdvisoryViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.titleAdvisory)
        private val tvDate: TextView = itemView.findViewById(R.id.sowingDateAdvisory)
        private val tvDescription: TextView =
            itemView.findViewById(R.id.descriptionAdvisory)

        fun bind(item: AdvisoryModel) {

            tvTitle.text = item.title
            tvDate.text = item.date
            tvDescription.text = item.description

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdvisoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_advisory_farm, parent, false)

        return AdvisoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AdvisoryViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ItemMyDashboardBinding
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray

class MyDashboardAdapter(
    private val language: String,
    private val jsonArray: JSONArray,
    private val itemClickListener: RecyclerItemClickListener
) : RecyclerView.Adapter<MyDashboardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMyDashboardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyDashboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = jsonArray.optJSONObject(position)

        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

// convert 16dp padding (8dp start + 8dp end) to px
        val padding = (16 * displayMetrics.density).toInt()

// convert item margins (approx 12dp total)
        val itemMargin = (12 * displayMetrics.density).toInt()

        val availableWidth = screenWidth - padding - itemMargin

        val params = holder.itemView.layoutParams
        params.width = availableWidth / 2
        holder.itemView.layoutParams = params

        val title = item.optString("title")
        val titleMr = item.optString("title_mr")
        val description = item.optString("data")
        val descriptionMr = item.optString("data_mr")
        val imageName = item.optString("image")

        holder.binding.titleTextView.text = if (language == "en") title else titleMr
        holder.binding.descriptionTextView.text =
            if (language == "en") description else descriptionMr

        val context = holder.itemView.context
        val resId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )

        holder.binding.imageView.setImageResource(
            if (resId != 0) resId else R.drawable.ic_weather_mp
        )
        holder.binding.root.setOnClickListener {
            itemClickListener.onRecyclerItemClick(1, item)
        }
    }

    override fun getItemCount(): Int = jsonArray.length()
}
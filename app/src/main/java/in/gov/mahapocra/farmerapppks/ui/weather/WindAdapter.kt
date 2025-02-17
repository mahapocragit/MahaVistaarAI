package `in`.gov.mahapocra.farmerapppks.ui.weather

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapppks.R

class WindAdapter(private val itemList: List<Item>, private val actName:String) : RecyclerView.Adapter<WindAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTextView.text = item.title
        when(actName){
            "temp" -> holder.imageView.setImageResource(R.drawable.weather_ic)
            "rain" -> holder.imageView.setImageResource(R.drawable.rain_ic)
            "humidity" -> holder.imageView.setImageResource(R.drawable.humidity_ic)
            "wind" -> holder.imageView.setImageResource(R.drawable.wind_ic)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val imageView: ImageView = itemView.findViewById(R.id.imageView11)
    }
}

package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.util.DayMatcher.isTodayMatchingMarathiDay
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom

class ShetishalaScheduleMeetAdapter(private val day: String, private val jsonArray: JSONArray) :
    RecyclerView.Adapter<ShetishalaScheduleMeetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val cropTextView: TextView = itemView.findViewById(R.id.cropTextView)
        val zoomLinkTextView: TextView = itemView.findViewById(R.id.zoomLinkTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shetishala_item_view_meet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = jsonArray.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = jsonArray.getJSONObject(position)
        val time = item.optString("time")
        val topic = item.optString("topic")
        val link = item.optString("link")


        holder.timeTextView.text = time
        holder.cropTextView.text = topic

        holder.zoomLinkTextView.setOnClickListener {
            if (isTodayMatchingMarathiDay(day)) {
                val browserIntent = Intent(Intent.ACTION_VIEW, link.toUri())
                holder.itemView.context.startActivity(browserIntent)
            } else {
                AlertDialog.Builder(holder.itemView.context).setTitle("This meeting is not available today. It is scheduled for $day.").setPositiveButton("OK", null).show()
            }
        }
    }
}

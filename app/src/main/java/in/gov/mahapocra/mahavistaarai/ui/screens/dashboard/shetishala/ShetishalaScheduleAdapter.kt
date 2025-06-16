package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.shetishala

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray

class ShetishalaScheduleAdapter(private val jsonArray: JSONArray) :
    RecyclerView.Adapter<ShetishalaScheduleAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val zoomRecyclerView: RecyclerView = itemView.findViewById(R.id.zoomRecyclerView)
        val tableLinearLayout: LinearLayout = itemView.findViewById(R.id.tableLinearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shetishala_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = jsonArray.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = jsonArray.getJSONObject(position)
        val title = item.optString("day")
        holder.dayTextView.text = title
        holder.zoomRecyclerView.layoutManager = LinearLayoutManager(holder.dayTextView.context)
        holder.zoomRecyclerView.adapter = ShetishalaScheduleMeetAdapter(title, item.optJSONArray("sessions"))
        holder.zoomRecyclerView.setHasFixedSize(true)

//        if (position%2==0) {
//            holder.tableLinearLayout.setBackgroundColor(Color.parseColor("#A8931C"))
//        }else{
//            holder.tableLinearLayout.setBackgroundColor(Color.parseColor("#93A81C"))
//        }
    }
}

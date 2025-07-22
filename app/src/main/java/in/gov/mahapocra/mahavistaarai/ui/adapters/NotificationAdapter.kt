package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class NotificationAdapter(private val jsonArray: JSONArray, private val callback: (JSONObject) -> Unit) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jsonObject = jsonArray[position] as JSONObject
        val title = jsonObject.optString("title")
        val body = jsonObject.optString("body")
        val notificationDate = jsonObject.optString("notification_date")
        val notificationStatus = jsonObject.optInt("is_read")
        if (notificationStatus == 1) {
            holder.notificationCard.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.white_dim_dark)
        }
        holder.notificationTitle.text = title
        holder.notificationMessage.text = body
        holder.notificationDate.text = notificationDate
        holder.notificationCardView.setOnClickListener {
            callback(jsonObject)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationTitle: TextView = itemView.findViewById(R.id.notificationTitle)
        val notificationMessage: TextView = itemView.findViewById(R.id.notificationMessage)
        val notificationDate: TextView = itemView.findViewById(R.id.notificationDate)
        val notificationCardView: LinearLayout = itemView.findViewById(R.id.notificationCardView)
        val notificationCard: CardView = itemView.findViewById(R.id.notificationCard)
    }
}
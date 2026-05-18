package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.OnDeleteClick
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import org.json.JSONArray

class CHCenterRecyclerAdapter(
    private val tempStrArr: JSONArray,
    private val onItemClick: OnDeleteClick
) : RecyclerView.Adapter<CHCenterRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chcUserName: TextView = itemView.findViewById(R.id.chcUserName)
        val chcNameTV: TextView = itemView.findViewById(R.id.chcNameTV)
        val contactNumberTV: TextView = itemView.findViewById(R.id.contactNumberTV)
        val chcUserDistance: TextView = itemView.findViewById(R.id.chcUserDistance)
        val contactButton: Button = itemView.findViewById(R.id.contactButton)
        val redirectToLocation: CardView = itemView.findViewById(R.id.redirectToLocation)
        val equipmentList: Button = itemView.findViewById(R.id.equipmentList)
        val equipmentText: TextView = itemView.findViewById(R.id.equipmentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_chc_new, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tempStrArr.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var isEquipmentShowing = false
        val context = holder.itemView.context
        val jsonObject =
            tempStrArr.optJSONObject(position) // Use optJSONObject instead of direct indexing
        val latLoc = jsonObject.optString("lat")
        val lonLoc = jsonObject.optString("lon")
        val equipments = jsonObject.optString("equipment")
        holder.chcUserName.text = jsonObject?.optString("contact_name")
        holder.contactNumberTV.text = jsonObject?.optString("contact_no")
        holder.chcNameTV.text = jsonObject?.optString("chcname")
        holder.chcUserDistance.text = "${jsonObject?.optString("distance")} kms"
        holder.contactButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${jsonObject?.optString("contact_no").toString()}")
            }
            context.startActivity(intent)
        }
        holder.redirectToLocation.setOnClickListener {
            redirectToLatLong(context, latLoc, lonLoc)
        }
        holder.equipmentList.setOnClickListener {
           holder.equipmentText.visibility = if(isEquipmentShowing) View.GONE else View.VISIBLE
            isEquipmentShowing = !isEquipmentShowing
        }
        holder.equipmentText.text = formattedEquipmentText(
            equipments
        )
    }

    private fun redirectToLatLong(context: Context, lat: String, long: String) {
        val gmmIntentUri = Uri.parse("geo:$lat,$long?q=$lat,$long(Label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps") // Ensure it opens in Google Maps app

        // Check if Google Maps is installed before starting activity
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // If Google Maps is not installed, open in a browser
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$long")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    private fun formattedEquipmentText(jsonArray: String): String {
        val equipmentArray = JSONArray(jsonArray)
        if (equipmentArray.length() == 0) return "Unable to fetch equipment list"

        val equipmentList = StringBuilder()
        for (i in 0 until equipmentArray.length()) {
            val equipmentObject = equipmentArray.getJSONObject(i)
            val equipmentName = equipmentObject.getString("equipment")
            equipmentList.append("• ").append(equipmentName).append("\n")
        }

        return equipmentList.toString().trim()
    }
}
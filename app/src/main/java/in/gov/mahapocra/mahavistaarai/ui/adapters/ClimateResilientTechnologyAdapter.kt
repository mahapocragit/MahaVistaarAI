package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ClimateResilientTechnologyAdapter(
    private val mContext: Context?,
    private val listener: OnMultiRecyclerItemClickListener,
    private val mJSONArray: JSONArray?
) : RecyclerView.Adapter<ClimateResilientTechnologyAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.sop_category_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.setData(mJSONArray!!.getJSONObject(position), listener)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return mJSONArray?.length() ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView2: TextView = itemView.findViewById(R.id.nameTextView2)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

        fun setData(jsonObject: JSONObject, listener: OnMultiRecyclerItemClickListener) {
            try {
                nameTextView2.text = jsonObject.getString("MainGroupSRNo")
                nameTextView.text = jsonObject.getString("MainGroupName")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            itemView.setOnClickListener { listener.onMultiRecyclerViewItemClick(2, jsonObject) }
        }
    }
}

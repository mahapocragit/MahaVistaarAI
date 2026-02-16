package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject

class StageAdvisoryAdapter(
    private var context: Context? = null,
    private var listener: OnMultiRecyclerItemClickListener,
    private var cropAdvisoryDetailsJSONArray: JSONArray?
) : RecyclerView.Adapter<StageAdvisoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.stage_view_temp,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    override fun getItemCount(): Int {
        return cropAdvisoryDetailsJSONArray?.length()!!
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val seeMoreTextView: TextView = itemView.findViewById(R.id.seeMoreTextView)
        val stage: RelativeLayout = itemView.findViewById(R.id.stage)
        val cropStagesInfoRecyclerView: RecyclerView =
            itemView.findViewById(R.id.cropStagesInfoRecyclerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advisoryJsonDetails: JSONObject =
            cropAdvisoryDetailsJSONArray?.get(position) as JSONObject
        val cropAdvisoryJSONArray = advisoryJsonDetails.getJSONArray("advisory")
        val sAdapter =
            StageAdvisoryDetailAdapter(holder.stage.context, listener, cropAdvisoryJSONArray)
        holder.cropStagesInfoRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        holder.cropStagesInfoRecyclerView.visibility = View.GONE
        holder.seeMoreTextView.setOnClickListener {
            if (holder.cropStagesInfoRecyclerView.visibility == View.VISIBLE) {
                holder.cropStagesInfoRecyclerView.visibility = View.GONE
                holder.seeMoreTextView.text = holder.itemView.context.getString(R.string.see_more)
            } else {
                holder.cropStagesInfoRecyclerView.visibility = View.VISIBLE
                holder.seeMoreTextView.text = holder.itemView.context.getString(R.string.see_less)
            }
        }
        holder.cropStagesInfoRecyclerView.adapter = sAdapter
        val status = advisoryJsonDetails.getString("status")
        if (status.equals("current")) {
            holder.cropStagesInfoRecyclerView.visibility = View.VISIBLE
            holder.seeMoreTextView.text = holder.itemView.context.getString(R.string.see_less)
            holder.stage.setBackgroundResource(R.drawable.current_round_background_status)

        } else if (status.equals("completed")) {
            holder.cropStagesInfoRecyclerView.visibility = View.GONE
            holder.stage.setBackgroundResource(R.drawable.baseline_check_circle_24)
        }else{
            holder.cropStagesInfoRecyclerView.visibility = View.GONE
            holder.stage.setBackgroundResource(R.drawable.pending_round_backgroud_stages)
        }
        holder.titleTextView.text = advisoryJsonDetails.getString("stage")

        holder.stage.setOnClickListener {
            listener.onMultiRecyclerViewItemClick(
                1,
                cropAdvisoryDetailsJSONArray?.get(position) as JSONObject
            )
        }

        if (status.equals("current")) {
            listener.onMultiRecyclerViewItemClick(
                1,
                cropAdvisoryDetailsJSONArray?.get(position) as JSONObject
            )
        }
    }

    fun getCurrentStagePosition(): Int {
        cropAdvisoryDetailsJSONArray?.let {
            for (i in 0 until it.length()) {
                val item = it.getJSONObject(i)
                if (item.getString("status") == "current") {
                    return i
                }
            }
        }
        return -1 // No current stage
    }
}
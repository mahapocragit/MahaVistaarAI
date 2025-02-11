package `in`.gov.mahapocra.farmerapppks.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.farmerapppks.R
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
        val stepLine: View = itemView.findViewById(R.id.stepLine)
        val stage: RelativeLayout = itemView.findViewById(R.id.stage)
        val rightTick: ImageView = itemView.findViewById(R.id.right_tick)
        val cropStagesInfoRecyclerView: RecyclerView =
            itemView.findViewById(R.id.cropStagesInfoRecyclerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advisoryJsonDetails: JSONObject =
            cropAdvisoryDetailsJSONArray?.get(position) as JSONObject
        val cropAdvisoryJSONArray = advisoryJsonDetails.getJSONArray("advisory")
        val sAdapter =
            StageAdvisoryDetailAdaptr(context, listener, cropAdvisoryJSONArray, "mr", "", "")
        holder.cropStagesInfoRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.cropStagesInfoRecyclerView.adapter = sAdapter
        val status = advisoryJsonDetails.getString("status")
        if (position == (cropAdvisoryDetailsJSONArray?.length())!! - 1) {
            holder.stepLine.visibility = View.GONE
        }
        if (status.equals("current")) {
            holder.stage.setBackgroundResource(R.drawable.current_round_background_status)
            holder.rightTick.visibility = View.GONE

        } else if (status.equals("completed")) {
            holder.stage.setBackgroundResource(R.drawable.completed_background_stages)
            holder.stepLine.setBackgroundResource(R.color.bg_green)
            holder.rightTick.visibility = View.VISIBLE
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
}
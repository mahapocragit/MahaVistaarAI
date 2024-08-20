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
    ): StageAdvisoryAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.stage_view,
            parent,
            false
        )
        return StageAdvisoryAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cropAdvisoryDetailsJSONArray?.length()!!
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val stepLine: View = itemView.findViewById(R.id.stepLine)
        val steage: RelativeLayout = itemView.findViewById(R.id.stage)
        val stages: LinearLayout = itemView.findViewById(R.id.stages)
        val right_tick: ImageView = itemView.findViewById(R.id.right_tick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advisoryJsonDetails: JSONObject =
            cropAdvisoryDetailsJSONArray?.get(position) as JSONObject
        Log.d("advisoryJsonDetails=" + position, advisoryJsonDetails.toString())
        val status = advisoryJsonDetails.getString("status")
        if (position == (cropAdvisoryDetailsJSONArray?.length())!! - 1) {
            holder.stepLine.visibility = View.GONE
        }
        if (status.equals("current")) {
            holder.steage.setBackgroundResource(R.drawable.current_round_background_status)
            holder.right_tick.visibility = View.GONE

        } else if (status.equals("completed")) {
            holder.steage.setBackgroundResource(R.drawable.completed_background_stages)
            holder.stepLine.setBackgroundResource(R.color.bg_green)
            holder.right_tick.visibility = View.VISIBLE
        }
        holder.titleTextView.setText(advisoryJsonDetails.getString("stage"))

        holder.stages.setOnClickListener(View.OnClickListener {
            listener.onMultiRecyclerViewItemClick(
                1,
                cropAdvisoryDetailsJSONArray?.get(position) as JSONObject
            )
        })

        if (status.equals("current")) {
            listener.onMultiRecyclerViewItemClick(
                1,
                cropAdvisoryDetailsJSONArray?.get(position) as JSONObject
            )
        }
    }


}
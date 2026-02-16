package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CropCategoriesAdapter(
    private var context: Context,
    private var dataJSONArray: JSONArray,
    private var callerActivity: String,
    private var multiRecyclerItemClickListener: OnMultiRecyclerItemClickListener
) : RecyclerView.Adapter<CropCategoriesAdapter.ViewHolder>(),
    OnMultiRecyclerItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.mail_title_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = dataJSONArray[position] as JSONObject
            val advisoryArray = jsonObject.optJSONArray("crops")
            holder.textView.text = jsonObject.optString("type").toString()
            val cropStageDetailsAdapter = CropStageDetailsAdapter( context, advisoryArray, this, callerActivity)
            holder.videosRecyclerView.setLayoutManager(
                GridLayoutManager(context, 4)
            )
            holder.videosRecyclerView.setAdapter(cropStageDetailsAdapter)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return dataJSONArray.length()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = itemView.findViewById(R.id.titleTextView)
        val videosRecyclerView: RecyclerView = itemView.findViewById(R.id.videosRecyclerView)

    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            multiRecyclerItemClickListener.onMultiRecyclerViewItemClick(i, obj)
        }
    }

}
package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.VideoDetails
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.ArrayList
import java.util.Date

class TitleVideosDetailsAdapter(
    private var context: Context? = null,
    private var videoDetailsList: List<VideoDetails>,
    private var callerActivity: String,
    private var multiRecyclerItemClickListener: OnMultiRecyclerItemClickListener
) : RecyclerView.Adapter<TitleVideosDetailsAdapter.ViewHolder>(),
    OnMultiRecyclerItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.mail_title_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {

            holder.textView.text = videoDetailsList[position].titles
            val imagesList: ArrayList<CropsCategName>? = videoDetailsList[position].moviesImagesList

            val videosImageAdapter =
                VideosImageDetailsAdapter(
                    context,
                    imagesList,
                    this,
                    callerActivity
                )

            holder.videosRecyclerView.setLayoutManager(
                GridLayoutManager(context, 4)
            )
            holder.videosRecyclerView.setAdapter(videosImageAdapter)
            videosImageAdapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return videoDetailsList.size
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
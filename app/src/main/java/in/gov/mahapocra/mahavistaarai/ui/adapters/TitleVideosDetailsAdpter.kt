package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.VideoDetails
import org.json.JSONException
import java.util.ArrayList

class TitleVideosDetailsAdapter(private var context: Context? = null, private var videoDetailsList: List<VideoDetails>, private var callerActivity:String): RecyclerView.Adapter<TitleVideosDetailsAdapter.ViewHolder>(),
    OnMultiRecyclerItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.mail_title_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {

            holder.textView.text = videoDetailsList[position].titles
            val imagesList: ArrayList<CropsCategName>? = videoDetailsList[position].moviesImagesList

            holder.pimg.setOnClickListener {
                Log.d("gfgfghgf", "test6666")
                holder.videosImageView.smoothScrollBy(-1000, 0);

            }

            holder.nimg.setOnClickListener {
                holder.pimg.visibility = View.VISIBLE
                val abc = videoDetailsList[position].id
                Log.d("dgfgfdgdf", abc.toString())
                holder.videosImageView.smoothScrollBy(200, 1000);

            }

            val videosImageAdapter =
                VideosImageDetailsAdapter(
                    context,
                    imagesList,
                    this,
                    callerActivity
                )

            holder.videosImageView.setLayoutManager(
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            holder.videosImageView.setAdapter(videosImageAdapter)
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
        val videosImageView: RecyclerView =itemView.findViewById(R.id.videosDetailsImages)
        val pimg: ImageView = itemView.findViewById(R.id.pimg)
        val nimg: ImageView = itemView.findViewById(R.id.nimg)

    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }

}
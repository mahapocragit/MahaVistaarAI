package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.magazine

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray

class MagazineRecyclerAdapter(val jsonArray: JSONArray) :
    RecyclerView.Adapter<MagazineRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_magazine_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val jsonObject = jsonArray.getJSONObject(position)
        val pdfUrl = jsonObject.optString("pdf_url")
        holder.magazineTitleTextView.text = jsonObject.optString("title")
        holder.magazineDateTextView.text = jsonObject.optString("date")
        holder.downloadButton.setOnClickListener {
            val request = DownloadManager.Request(Uri.parse(pdfUrl))
            request.setTitle("Downloading PDF")
            request.setDescription("Please wait...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setAllowedOverMetered(true)
            request.setAllowedOverRoaming(true)

            // File name
            val fileName = "document_${System.currentTimeMillis()}.pdf"

            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )

            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            downloadManager.enqueue(request)

            Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show()
        }
        Glide.with(context).load(jsonObject.optString("image_url")).into(holder.magazineThumbnailImageView)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val magazineTitleTextView: TextView = view.findViewById(R.id.magazineTitleTextView)
        val magazineDateTextView: TextView = view.findViewById(R.id.magazineDateTextView)
        val magazineThumbnailImageView: ImageView = view.findViewById(R.id.magazineThumbnailImageView)
        val downloadButton: ImageView = view.findViewById(R.id.downloadButton)
    }
}
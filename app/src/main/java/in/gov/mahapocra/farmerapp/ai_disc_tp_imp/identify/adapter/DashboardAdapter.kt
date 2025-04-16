package `in`.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import `in`.gov.mahapocra.farmerapp.R
import `in`.gov.mahapocra.farmerapp.ai_disc_tp_imp.identify.model_identify.Model_Dashboard

class DashboardAdapter(
    private val context: Context,
    dashboardContents: ArrayList<Model_Dashboard>
) :
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
    private val model_dashboardContents: ArrayList<Model_Dashboard> = dashboardContents

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.content_dashboard, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.gridText.text = model_dashboardContents[i].getGridText
        try {
            val imagePath =
                "https://nibpp.krishimegh.in/Content/reportingbase/crop_file/" + model_dashboardContents[i].gridImage
            Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.facilities)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.gray)
                .into(viewHolder.gridImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return model_dashboardContents.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val gridText: TextView = view.findViewById(R.id.grid_text)
        val gridImage: ImageView = view.findViewById(R.id.grid_image)
    }
}

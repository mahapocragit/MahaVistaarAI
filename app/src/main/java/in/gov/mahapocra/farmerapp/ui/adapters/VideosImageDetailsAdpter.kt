package `in`.gov.mahapocra.farmerapp.ui.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.farmerapp.R
import `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.menugrid.pest.SelectSowingDataAndFarmer
import `in`.gov.mahapocra.farmerapp.data.model.CropsCategName
import `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.farmerapp.util.AppPreferenceManager
import `in`.gov.mahapocra.farmerapp.util.app_util.AppConstants
import org.json.JSONException


class VideosImageDetailsAdapter(
    private var context: Context? = null,
    private var moviesImageList: ArrayList<CropsCategName>?,
    private var listener: OnMultiRecyclerItemClickListener,
    private var callerActivity: String
) : RecyclerView.Adapter<VideosImageDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.videos_images_details_layout,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return moviesImageList?.size!!
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videosImage: ImageView = itemView.findViewById(R.id.videosImags)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val closeImag: ImageView = itemView.findViewById(R.id.closeImag)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.titleTextView.text = moviesImageList?.get(position)?.getmName()
//            context?.let {
//                Glide.with(it)
//                    .load(moviesImageList?.get(position)?.getmUrl())
//                    .into(holder.videosImage)
//            }
            val transformation = RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(2f)
                .cornerRadiusDp(40f)
                .oval(false)
                .build()

            Picasso.get()
                .load(moviesImageList?.get(position)?.getmUrl())
                .transform(transformation)
                .resize(180, 180)
                .centerCrop()
                .into(holder.videosImage)
            holder.videosImage.setOnClickListener {
                val source =
                    context?.let { it1 -> AppPreferenceManager(it1).getString(AppConstants.ACTION_FROM_DASHBOARD) }
                if (source.equals(AppConstants.PEST_AND_DISEASES_STAGES)) {
                    val intent = Intent(context, PestsAndDiseasesStages::class.java)
                    intent.putExtra("cropId", moviesImageList?.get(position)?.id)
                    intent.putExtra("wotr_crop_id", moviesImageList?.get(position)?.wotr_id)
                    intent.putExtra("mUrl", moviesImageList?.get(position)?.getmUrl())
                    intent.putExtra("mName", moviesImageList?.get(position)?.getmName())
                    context?.startActivity(intent)
                }else if (source.equals(AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD)) {
                    val intent = Intent(context, AdvisoryCropActivity::class.java)
                    intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
                    intent.putExtra("id", moviesImageList?.get(position)?.id)
                    intent.putExtra("mName", moviesImageList?.get(position)?.getmName())
                    intent.putExtra("editCrop", "NoEditCrop")
                    intent.putExtra("sowingDate", moviesImageList?.get(position)?.sowing_date_general)
                    context?.startActivity(intent)
                }else if (source.equals(AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD)) {
                    val intent = Intent(context, FertilizerCalculatorActivity::class.java)
                    intent.putExtra("id", moviesImageList?.get(position)?.id)
                    intent.putExtra("wotr_crop_id", moviesImageList?.get(position)?.wotr_id)
                    intent.putExtra("mUrl", moviesImageList?.get(position)?.getmUrl())
                    intent.putExtra("mName", moviesImageList?.get(position)?.getmName())
                    intent.putExtra("sowingDate",  moviesImageList?.get(position)?.sowing_date_general)
                    intent.putExtra("editCrop", "NoEditCrop")
                    context?.startActivity(intent)
                } else {
                    val intent = Intent(context, SelectSowingDataAndFarmer::class.java)
                    intent.putExtra("id", moviesImageList?.get(position)?.id)
                    intent.putExtra("wotr_crop_id", moviesImageList?.get(position)?.wotr_id)
                    intent.putExtra("mUrl", moviesImageList?.get(position)?.getmUrl())
                    intent.putExtra("mName", moviesImageList?.get(position)?.getmName())
                    context?.startActivity(intent)
                }
            }
            if (callerActivity == "dashboardScreen") {
                holder.closeImag.visibility = View.VISIBLE
                holder.closeImag.setOnClickListener {
                    val dialog = AlertDialog.Builder(context!!)
                        .setCancelable(false)
                        .setTitle("Delete Crop")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton(
                            "Yes"
                        ) { _, _ ->
                            listener.onMultiRecyclerViewItemClick(
                                2,
                                moviesImageList?.get(position)!!.id
                            )
                        }.setNegativeButton(
                            "No, thanks"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
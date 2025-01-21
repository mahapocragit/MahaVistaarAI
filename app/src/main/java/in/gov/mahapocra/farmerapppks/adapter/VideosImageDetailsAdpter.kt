package `in`.gov.mahapocra.farmerapppks.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.activity.CropStageAdvisory
import `in`.gov.mahapocra.farmerapppks.activity.SelectSowingDataAndFarmer
import `in`.gov.mahapocra.farmerapppks.models.response.CropsCategName
import org.json.JSONException


class VideosImageDetailsAdpter(
    private var context: Context? = null,
    private var moviesImageList: ArrayList<CropsCategName>?,
    private var listener: OnMultiRecyclerItemClickListener,
    private var callerAcitvity: String
) : RecyclerView.Adapter<VideosImageDetailsAdpter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideosImageDetailsAdpter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.videos_images_details_layout,
            parent,
            false
        )
        return VideosImageDetailsAdpter.ViewHolder(view)
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
            holder.titleTextView.setText(moviesImageList?.get(position)?.getmName())
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
                when (callerAcitvity) {
                    "dashboardScreen" -> {
                        //   UIToastMessage.show(context,"Farmer Advisory")
                        val intent = Intent(context, CropStageAdvisory::class.java)
                        intent.putExtra("id", moviesImageList?.get(position)?.id)
                        intent.putExtra("wotr_crop_id", moviesImageList?.get(position)?.wotr_id)
                        intent.putExtra("mUrl", moviesImageList?.get(position)?.getmUrl())
                        intent.putExtra("mName", moviesImageList?.get(position)?.getmName())
                        context?.startActivity(intent)
                    }
                    "NO_NEED_TO_ADD_SOWING_DATE" -> {
                        val intent = Intent(context, CropStageAdvisory::class.java)
                        intent.putExtra("id", moviesImageList?.get(position)?.id)
                        intent.putExtra("wotr_crop_id", moviesImageList?.get(position)?.wotr_id)
                        intent.putExtra("mUrl", moviesImageList?.get(position)?.getmUrl())
                        intent.putExtra("mName", moviesImageList?.get(position)?.getmName())
                        intent.putExtra("editCrop", "NoEditCrop")
                        context?.startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(context, SelectSowingDataAndFarmer::class.java)
                        intent.putExtra("id", moviesImageList?.get(position)?.id)
                        intent.putExtra("wotr_crop_id", moviesImageList?.get(position)?.wotr_id)
                        intent.putExtra("mUrl", moviesImageList?.get(position)?.getmUrl())
                        intent.putExtra("mName", moviesImageList?.get(position)?.getmName())
                        intent.putExtra("editCrop", "NoEditCrop")
                        context?.startActivity(intent)
                    }
                }
            }
            if (callerAcitvity.equals("dashboardScreen")) {
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
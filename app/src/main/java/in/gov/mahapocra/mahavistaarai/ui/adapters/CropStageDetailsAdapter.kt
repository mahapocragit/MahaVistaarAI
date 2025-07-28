package `in`.gov.mahapocra.mahavistaarai.ui.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
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
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.SOPActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.advisory.AdvisoryCropActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.PestsAndDiseasesStages
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class CropStageDetailsAdapter(
    private var context: Context? = null,
    private var cropStageDetailsJsonArray: JSONArray,
    private var listener: OnMultiRecyclerItemClickListener,
    private var callerActivity: String
) : RecyclerView.Adapter<CropStageDetailsAdapter.ViewHolder>() {

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
        return cropStageDetailsJsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videosImage: ImageView = itemView.findViewById(R.id.videosImags)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val closeImag: ImageView = itemView.findViewById(R.id.closeImag)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            val jsonObject = cropStageDetailsJsonArray.getJSONObject(position) as JSONObject
            holder.titleTextView.text = jsonObject.optString("name")
            val transformation = RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(2f)
                .cornerRadiusDp(40f)
                .oval(false)
                .build()

            Picasso.get()
                .load(jsonObject.optString("image"))
                .transform(transformation)
                .resize(180, 180)
                .centerCrop()
                .into(holder.videosImage)
            holder.videosImage.setOnClickListener {
                val source =
                    context?.let { it1 -> AppPreferenceManager(it1).getString(AppConstants.ACTION_FROM_DASHBOARD) }
                if (source.equals(AppConstants.PEST_AND_DISEASES_STAGES)) {
                    val intent = Intent(context, PestsAndDiseasesStages::class.java)
                    intent.putExtra("cropId", jsonObject.optInt("id"))
                    intent.putExtra("wotr_crop_id", jsonObject.optInt("wotr_crop_id"))
                    intent.putExtra("mUrl", jsonObject.optString("image"))
                    intent.putExtra("mName", jsonObject.optString("name"))
                    context?.startActivity(intent)
                }else if (source.equals(AppConstants.PEST_AND_DISEASES_FROM_DASHBOARD)) {
                    val intent = Intent(context, AdvisoryCropActivity::class.java)
                    intent.putExtra("dataSavedInLocal", "dataSavedInLocal")
                    intent.putExtra("id", jsonObject.optInt("id"))
                    intent.putExtra("mName", jsonObject.optString("name"))
                    intent.putExtra("editCrop", "NoEditCrop")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra("sowingDate", LocalCustom.getSowingDateWithYear(jsonObject.optString("sowing_date")))
                    }
                    context?.startActivity(intent)
                }else if (source.equals(AppConstants.FERTILIZER_CALCULATOR_FROM_DASHBOARD)) {
                    val intent = Intent(context, FertilizerCalculatorActivity::class.java)
                    intent.putExtra("id", jsonObject.optInt("id"))
                    intent.putExtra("wotr_crop_id", jsonObject.optInt("wotr_crop_id"))
                    intent.putExtra("mUrl", jsonObject.optString("image"))
                    intent.putExtra("mName", jsonObject.optString("name"))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra("sowingDate", LocalCustom.getSowingDateInDayMonthYearFormat(jsonObject.optString("sowing_date")))
                    }
                    intent.putExtra("editCrop", "NoEditCrop")
                    context?.startActivity(intent)
                }else if (source.equals(AppConstants.SOP_FROM_DASHBOARD)) {
                    val intent = Intent(context, SOPActivity::class.java)
                    intent.putExtra("id", jsonObject.optInt("id"))
                    intent.putExtra("wotr_crop_id", jsonObject.optInt("wotr_crop_id"))
                    intent.putExtra("mUrl", jsonObject.optString("image"))
                    intent.putExtra("mName", jsonObject.optString("name"))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra("sowingDate", LocalCustom.getSowingDateWithYear(jsonObject.optString("sowing_date")))
                    }
                    intent.putExtra("editCrop", "NoEditCrop")
                    context?.startActivity(intent)
                } else {
                    listener.onMultiRecyclerViewItemClick(1, JSONObject().apply {
                        put("id", jsonObject.optInt("id"))
                        put("wotr_crop_id", jsonObject.optInt("wotr_crop_id"))
                        put("mUrl", jsonObject.optString("image"))
                        put("mName", jsonObject.optString("name"))
                    })
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
                                jsonObject.optInt("id")
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
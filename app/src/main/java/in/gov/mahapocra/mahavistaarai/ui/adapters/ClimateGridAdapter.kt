package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.model.ClimateGridModel
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.DiseaseInformation

class ClimateGridAdapter(
    private val mContext: Context,
    climateGridModelArrayList: ArrayList<ClimateGridModel>,
    var activity: String
) : ArrayAdapter<ClimateGridModel?>(mContext, 0, climateGridModelArrayList) {
    var imageUrl: String? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listItemView = LayoutInflater.from(context)
                .inflate(R.layout.single_climate_grid, parent, false)
        }
        val climateGridModel: ClimateGridModel? = getItem(position)
        val climateName = listItemView.findViewById<TextView>(R.id.climate_name)
        val gridItem = listItemView.findViewById<CardView>(R.id.gridItem)
        val climateImage = listItemView.findViewById<ImageView?>(R.id.climate_image)
        climateName.text = climateGridModel?.climate_name
        imageUrl = climateGridModel?.climate_image
        loadImage(climateImage, imageUrl)

        if (activity.equals("PestAndDiseasesAdpater", ignoreCase = true)) {
            gridItem.setOnClickListener { v: View ->
                val intent = Intent(v.context, DiseaseInformation::class.java)
                intent.putExtra("id", climateGridModel?.webUrl?.toInt())
                val currentString: String = climateGridModel?.climate_name ?: ""
                val separated: Array<String?> =
                    currentString.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                intent.putExtra("name", separated[1])
                mContext.startActivity(intent)
            }
        }
        return listItemView
    }

    companion object {
        fun loadImage(climateImage: ImageView?, image_url: String?) {
            try {
                Picasso.get().invalidate(image_url)
                Picasso.get()
                    .load(image_url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .resize(500, 400)
                    .centerCrop()
                    .into(climateImage)
            } catch (ex: Exception) {
                ex.toString()
            }
        }
    }
}

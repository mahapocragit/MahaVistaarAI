package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class MarketPriceAdapter(private val context: Context, private val mOriginalArray: JSONArray) :
    RecyclerView.Adapter<MarketPriceAdapter.ViewHolder>() {
    private var mFilteredArray: JSONArray

    init {
        this.mFilteredArray = JSONArray()
        for (i in 0..<mOriginalArray.length()) {
            try {
                mFilteredArray.put(mOriginalArray.getJSONObject(i))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_market_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        try {
            viewHolder.onBind(context, mFilteredArray.getJSONObject(position))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return mFilteredArray.length()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val tvCropName: TextView = v.findViewById(R.id.tv_crop_name)
        private val tvCropDate: TextView = v.findViewById(R.id.tv_crop_date)
        private val tvMaxValue: TextView = v.findViewById(R.id.tvMaxValue)
        private val tvAvgValue: TextView = v.findViewById(R.id.tvAvgValue)
        private val tvMinValue: TextView = v.findViewById(R.id.tvMinValue)
        private val unitForQuantity: TextView = v.findViewById(R.id.unitForQuantity)
        private val marketPriceImageView: ImageView = v.findViewById(R.id.marketPriceImageView)

        @Throws(JSONException::class)
        fun onBind(context: Context, jsonObject: JSONObject) {
            val commCropName = jsonObject.getString("comm_name")
            val variableCropName = jsonObject.getString("variety_name")
            tvCropName.text = "$commCropName ($variableCropName)"
            tvCropDate.text = jsonObject.getString("date")
            tvMaxValue.text = jsonObject.getString("max_price")
            tvAvgValue.text = jsonObject.getString("avg_price")
            tvMinValue.text = jsonObject.getString("min_price")
            unitForQuantity.text = String.format(" %s", jsonObject.getString("unit"))
            val imageUrl = jsonObject.getString("img_url")
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .transform(RoundedCorners(30))
                    .into(marketPriceImageView)
            } else {
                Glide.with(context)
                    .load(R.drawable.marketimage)
                    .transform(RoundedCorners(30))
                    .into(marketPriceImageView)
            }
        }
    }

    fun filter(query: String?) {
        var query = query
        mFilteredArray = JSONArray()
        if (query == null || query.trim { it <= ' ' }.isEmpty()) {
            mFilteredArray = mOriginalArray
        } else {
            query = query.lowercase(Locale.getDefault())
            for (i in 0..<mOriginalArray.length()) {
                try {
                    val obj = mOriginalArray.getJSONObject(i)
                    if (obj.getString("comm_name").lowercase(Locale.getDefault()).contains(query)) {
                        mFilteredArray.put(obj)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        notifyDataSetChanged()
    }
}

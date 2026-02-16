package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BbtActivityGrpAdapter(
    private val context: Context,
    private val listener: OnMultiRecyclerItemClickListener,
    private val mJSONArray: JSONArray,
    private val calledActivituy: String
) : RecyclerView.Adapter<BbtActivityGrpAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.single_dbt_activity_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.setData(mJSONArray.getJSONObject(position), listener)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return mJSONArray.length()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        OnMultiRecyclerItemClickListener {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val data1Title: TextView = itemView.findViewById(R.id.data1_title)
        private val data1Value: TextView = itemView.findViewById(R.id.data1_value)
        private val data2Title: TextView = itemView.findViewById(R.id.data2_title)
        private val data2Value: TextView = itemView.findViewById(R.id.data2_value)
        private val data3Title: TextView = itemView.findViewById(R.id.data3_title)
        private val data3Value: TextView = itemView.findViewById(R.id.data3_value)
        private val data4Value: TextView = itemView.findViewById(R.id.data4_value)
        private val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.main_linr_layout)

        fun setData(
            jsonObject: JSONObject,
            listener: OnMultiRecyclerItemClickListener
        ) {
            try {
                if (calledActivituy.equals("dbtSchemes", ignoreCase = true)) {
                    mainLinearLayout.visibility = View.GONE
                    nameTextView.text = jsonObject.getString("ActivityGroupName")
                } else if (calledActivituy.equals("OptonRclAdapter", ignoreCase = true)) {
                    mainLinearLayout.visibility = View.VISIBLE
                    // nameTextView.setText(jsonObject.getString("sowing_date"));
                    val mJSONArray = jsonObject.getJSONArray("fertilizer")
                    // String total_Estimated_Cost =jsonObject.getString("total Estimated Cost");
                    var cropAgeDays = ""
                    var targetDate = ""
                    var price = 0
                    for (i in 0..<mJSONArray.length()) {
                        val tittle = mJSONArray.getJSONObject(i).getString("FertilizerName")
                        val quantity = mJSONArray.getJSONObject(i).getString("Quantity")
                        val priceValue = mJSONArray.getJSONObject(i).getInt("Price")
                        cropAgeDays = mJSONArray.getJSONObject(i).getString("CropAgeDays")
                        targetDate = mJSONArray.getJSONObject(i).getString("TargetDate")

                        price += priceValue
                        if (i == 0) {
                            data1Title.text = tittle
                            data1Value.text = quantity
                        }
                        if (i == 1) {
                            data2Title.text = tittle
                            data2Value.text = quantity
                        }
                        if (i == 2) {
                            data3Title.text = tittle
                            data3Value.text = quantity
                        }
                    }
                    data4Value.text = buildString {
                        append("")
                        append(price)
                    }
                    if (cropAgeDays.equals("0", ignoreCase = true)) {
                        nameTextView.text = buildString {
                            append(targetDate)
                            append(" | ")
                            append(context.getString(R.string.At_Sowing))
                        }
                    } else {
                        nameTextView.text = buildString {
                            append("$targetDate | $cropAgeDays ")
                            append(
                                context.getString(
                                    R.string.Days_After_Sowing
                                )
                            )
                        }
                    }
                } else {
                    nameTextView.text = jsonObject.getString("advisory")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            itemView.setOnClickListener {
                if (calledActivituy.equals("dbtSchemes", ignoreCase = true)) {
                    listener.onMultiRecyclerViewItemClick(2, jsonObject)
                }
            }
        }

        override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        }
    }
}

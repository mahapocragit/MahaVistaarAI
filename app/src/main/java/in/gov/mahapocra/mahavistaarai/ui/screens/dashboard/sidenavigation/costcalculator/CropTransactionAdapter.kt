package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DateHelper.convertDateFormat
import org.json.JSONArray

class CropTransactionAdapter(
    private val jsonArray: JSONArray,
    private val language:String,
    private val onDeleteClick: OnDeleteClick
) :
    RecyclerView.Adapter<CropTransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.crop_transaction_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.context
        val cropObj = jsonArray.getJSONObject(position)
        cropObj.getInt("id")
        val transactionType = cropObj.getString("type")
        val transactionCategory = cropObj.getString("category")
        val transactionCategoryMr = cropObj.getString("category_mr")
        val transactionDate = cropObj.getString("date")
        val transactionAmount = cropObj.getString("price")
        val transactionName = cropObj.getString("name")
        cropObj.getString("yield")
        cropObj.getString("price_per_unit")
        holder.transactionDateTextView.text = convertDateFormat(transactionDate)
        if (transactionType == "income") {
            holder.cropTransactionImage.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.income_icon
                )
            )
            holder.transactionAmountTextView.setTextColor("#22C55E".toColorInt())
            holder.transactionTypeTextView.text = transactionName
            holder.transactionNameTextView.visibility = View.GONE
        } else {
            holder.cropTransactionImage.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.expense_icon
                )
            )
            holder.transactionAmountTextView.setTextColor("#EF4444".toColorInt())
            if (transactionCategory == null || transactionCategory == "null" || transactionCategory == "") {
                holder.transactionTypeTextView.text = "Expense"
            } else {
                holder.transactionTypeTextView.text = if(language.equals("en",ignoreCase = true))transactionCategory else transactionCategoryMr
                if (transactionName.isEmpty()) {
                    holder.transactionNameTextView.visibility = View.GONE
                } else {
                    holder.transactionNameTextView.text = transactionName
                }
            }
        }
        holder.transactionAmountTextView.text = buildString {
            append("₹")
            append(transactionAmount)
        }
        holder.cropTransactionCard.setOnClickListener {
            onDeleteClick.onDeleteClick(0, cropObj)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionTypeTextView: TextView = itemView.findViewById(R.id.transactionTypeTextView)
        val transactionNameTextView: TextView = itemView.findViewById(R.id.transactionNameTextView)
        val transactionDateTextView: TextView = itemView.findViewById(R.id.transactionDateTextView)
        val cropTransactionImage: ImageView = itemView.findViewById(R.id.cropTransactionImage)
        val cropTransactionCard: LinearLayout = itemView.findViewById(R.id.cropTransactionCard)
        val transactionAmountTextView: TextView =
            itemView.findViewById(R.id.transactionAmountTextView)
    }
}

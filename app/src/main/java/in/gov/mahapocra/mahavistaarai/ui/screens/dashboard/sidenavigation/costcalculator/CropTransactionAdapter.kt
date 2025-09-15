import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import androidx.core.graphics.toColorInt

class CropTransactionAdapter(private val jsonArray: JSONArray) :
    RecyclerView.Adapter<CropTransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.crop_transaction_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cropObj = jsonArray.getJSONObject(position)
        val transactionType = cropObj.getString("type")
        val transactionCategory = cropObj.getString("category")
        val transactionDate = cropObj.getString("date")
        val transactionAmount = cropObj.getString("price")
        val transactionName = cropObj.getString("name")
        holder.transactionDateTextView.text = transactionDate
        if (transactionType == "income") {
            holder.cropTransactionImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.income_icon))
            holder.transactionAmountTextView.setTextColor("#22C55E".toColorInt())
            holder.transactionTypeTextView.text = transactionName
            holder.transactionNameTextView.visibility = View.GONE
        } else {
            holder.cropTransactionImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.expense_icon))
            holder.transactionAmountTextView.setTextColor("#EF4444".toColorInt())
            if (transactionCategory == null || transactionCategory == "null" || transactionCategory == "") {
                holder.transactionTypeTextView.text = "Expense"
            } else {
                holder.transactionTypeTextView.text = transactionCategory
                if (transactionName.isEmpty()) {
                    holder.transactionNameTextView.visibility = View.GONE
                }else{
                    holder.transactionNameTextView.text = transactionName
                }
            }
        }
        holder.transactionAmountTextView.text = "₹$transactionAmount"
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionTypeTextView: TextView = itemView.findViewById(R.id.transactionTypeTextView)
        val transactionNameTextView: TextView = itemView.findViewById(R.id.transactionNameTextView)
        val transactionDateTextView: TextView = itemView.findViewById(R.id.transactionDateTextView)
        val cropTransactionImage: ImageView = itemView.findViewById(R.id.cropTransactionImage)
        val transactionAmountTextView: TextView =
            itemView.findViewById(R.id.transactionAmountTextView)
    }
}

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import androidx.core.graphics.toColorInt
import `in`.gov.mahapocra.mahavistaarai.databinding.DialogAddIncomeLayoutBinding
import `in`.gov.mahapocra.mahavistaarai.databinding.EditExpenseLayoutBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.OnDeleteClick
import `in`.gov.mahapocra.mahavistaarai.util.DateHelper.convertDateFormat

class CropTransactionAdapter(
    private val jsonArray: JSONArray,
    private val onDeleteClick: OnDeleteClick
) :
    RecyclerView.Adapter<CropTransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.crop_transaction_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val cropObj = jsonArray.getJSONObject(position)
        val transactionId = cropObj.getInt("id")
        val transactionType = cropObj.getString("type")
        val transactionCategory = cropObj.getString("category")
        val transactionDate = cropObj.getString("date")
        val transactionAmount = cropObj.getString("price")
        val transactionName = cropObj.getString("name")
        val transactionYield = cropObj.getString("yield")
        val transactionPricePerUnit = cropObj.getString("price_per_unit")
        holder.transactionDateTextView.text = transactionDate
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
                holder.transactionTypeTextView.text = transactionCategory
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
            if (transactionType == "income") {
                //INCOME
                val binding = DialogAddIncomeLayoutBinding.inflate(LayoutInflater.from(context))
                val expenseDialog = AlertDialog.Builder(context).setView(binding.root)
                val dialog = expenseDialog.create()
                binding.cancelText.setOnClickListener {
                    dialog.dismiss()
                }
                binding.deleteText.setOnClickListener {
                    onDeleteClick.onDeleteClick(transactionId)
                    dialog.dismiss()
                }
                binding.yieldText.setText(transactionYield)
                binding.pricePerUnitText.setText(transactionPricePerUnit)
                binding.totalPriceTextView.text = buildString {
                    append("Total Price: ₹")
                    append(transactionAmount)
                }
                binding.incomeCalendarDateTextView.text = convertDateFormat(transactionDate)
                binding.incomeNameEditText.setText(transactionName)
                dialog.show()
            } else {
                //EXPENSE
                val binding = EditExpenseLayoutBinding.inflate(LayoutInflater.from(context))
                val expenseDialog = AlertDialog.Builder(context).setView(binding.root)
                val dialog = expenseDialog.create()
                binding.cancelText.setOnClickListener {
                    dialog.dismiss()
                }
                binding.categoryNameTextView.text = transactionCategory
                binding.priceEditText2.setText(transactionAmount)
                binding.expenseNameEditText2.setText(transactionName)
                binding.expenseCalendarDateTextView.text = convertDateFormat(transactionDate)
                binding.deleteText.setOnClickListener {
                    onDeleteClick.onDeleteClick(transactionId)
                    dialog.dismiss()
                }
                dialog.show()
            }
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

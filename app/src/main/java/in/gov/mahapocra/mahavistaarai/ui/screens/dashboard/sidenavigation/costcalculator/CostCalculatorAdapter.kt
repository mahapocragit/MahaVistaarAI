import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.CropCostCalculationActivity
import org.json.JSONArray

class CostCalculatorAdapter(private val jsonArray: JSONArray) :
    RecyclerView.Adapter<CostCalculatorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cost_calculator_crop_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context: Context = holder.itemView.context
        val cropObj = jsonArray.getJSONObject(position)

        val cropId = cropObj.getInt("crop_id")
        val name = cropObj.getString("name")
        val imageUrl = cropObj.getString("image")
        val total = cropObj.getInt("total")

        holder.cropName.text = name
        holder.cropTotal.text = "₹$total"

        // Load image with Glide (make sure Glide is in dependencies)
        Glide.with(context)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // fallback image
            .into(holder.cropImage)

        holder.selectedCropLinearLayout.setOnClickListener {
            val intent = Intent(context, CropCostCalculationActivity::class.java)
            intent.putExtra("crop_id", cropId)
            intent.putExtra("crop_name", name)
            intent.putExtra("crop_image", imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropName: TextView = itemView.findViewById(R.id.cropNameTextView)
        val cropTotal: TextView = itemView.findViewById(R.id.cropTotalTextView)
        val cropImage: ImageView = itemView.findViewById(R.id.cropImageView)
        val selectedCropLinearLayout: LinearLayout =
            itemView.findViewById(R.id.selectedCropCalculationLinearLayout)
    }
}

package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import `in`.gov.mahapocra.mahavistaarai.R

class DashboardAdapter(
    private val context: Context,
    private val mobileValues: Array<String>,
    private val mobileImg: IntArray
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridView: View?
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.single_item_grid, parent, false)
            val textView = gridView.findViewById<TextView>(R.id.grid_item_label)
            textView.text = mobileValues[position]
            val imageView = gridView.findViewById<ImageView>(R.id.grid_item_image)
            imageView.setImageResource(mobileImg[position])
            notifyDataSetChanged()
        } else {
            gridView = convertView
        }
        return gridView
    }

    override fun getCount(): Int {
        return mobileValues.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}

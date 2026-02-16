package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONException

class DrawerMenuAdapter(
    private val mContext: Context,
    private val mDataArray: JSONArray
) : BaseAdapter() {
    override fun getCount(): Int {
        return mDataArray.length()
    }

    override fun getItem(position: Int): Any? {
        try {
            return mDataArray.getJSONObject(position)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        val viewHolder: ViewHolder?

        if (convertView == null) {
            viewHolder = ViewHolder()

            val mLayoutInflater =
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            convertView = mLayoutInflater.inflate(R.layout.list_menu_drawer, null)
            viewHolder.iconImageView = convertView.findViewById(R.id.iconImageView)
            viewHolder.nameTextView = convertView.findViewById(R.id.nameTextView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        try {
            val jsonObject = mDataArray.getJSONObject(position)

            viewHolder.nameTextView?.text = jsonObject.getString("name")
            val resId = mContext.resources.getIdentifier(
                jsonObject.getString("icon"),
                "mipmap",
                mContext.packageName
            )
            viewHolder.iconImageView?.setImageResource(resId)

            if (jsonObject.getString("icon").equals("ic_list_black", ignoreCase = true)) {
                viewHolder.iconImageView?.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN)
            } else {
                viewHolder.iconImageView?.colorFilter = null
            }

            if (jsonObject.getString("icon") == "partners_ic") {
                viewHolder.iconImageView!!.setImageResource(R.drawable.partners_ic)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return convertView
    }

    private class ViewHolder {
        var nameTextView: TextView? = null
        var iconImageView: ImageView? = null
    }
}

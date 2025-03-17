package `in`.gov.mahapocra.farmerapppks.ui.screens.chc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapppks.R


class CHCenterRecyclerAdapter(private val tempStrArr: List<String>) : RecyclerView.Adapter<CHCenterRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chcUserName: TextView = itemView.findViewById(R.id.chcUserName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_chc_center, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tempStrArr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chcUserName.text = tempStrArr[position]
    }
}
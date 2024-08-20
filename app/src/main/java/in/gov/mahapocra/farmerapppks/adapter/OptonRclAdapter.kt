package `in`.gov.mahapocra.farmerapppks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.farmerapppks.R
import org.json.JSONArray
import org.json.JSONObject

class OptonRclAdapter (
    private var context: Context? = null,
    private var listener: OnMultiRecyclerItemClickListener,
    private var fertilizerCalacaltedValue: JSONArray?,
    private var availableOption: String
) : RecyclerView.Adapter<OptonRclAdapter.ViewHolder>(), OnMultiRecyclerItemClickListener {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.optin_item,
            parent,
            false
        )
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return fertilizerCalacaltedValue?.length()!!
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val optiontv: TextView = itemView.findViewById(R.id.optiontv)
        val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        val fertlizerDateRcl: RecyclerView = itemView.findViewById(R.id.fertlizerDate_Rcl)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fertilizerCalacaltedDetails: JSONObject =
            fertilizerCalacaltedValue?.get(position) as JSONObject
        //val option = fertilizerCalacaltedDetails.getString("status")
        for (i in 0 until fertilizerCalacaltedValue!!.length()) {
            holder.optiontv.setText(context!!.getResources().getString(R.string.Option) + " ${position+1}")
           // holder.optiontv.setText("Option${position+1}")
          //  holder.optiontv.setText(context?.getText(R.string.Option)  ${position+1})

            val adaptorDbtActivityGrp =
                BbtActivityGrpAdapter(
                    context,
                    this,
                    if (availableOption.equals("fertilizerSelectedValue")){
                        (fertilizerCalacaltedValue?.get(position) as JSONObject).getJSONArray("option")
                    }else{
                        (fertilizerCalacaltedValue?.get(position) as JSONObject).getJSONArray("Option")
                    }
                    ,
                    "OptonRclAdapter"
                )
            holder.fertlizerDateRcl.setLayoutManager(
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            holder.fertlizerDateRcl.setAdapter(adaptorDbtActivityGrp)
            adaptorDbtActivityGrp.notifyDataSetChanged()
           }

        holder.mainLinearLayout.setOnClickListener(View.OnClickListener {
            listener.onMultiRecyclerViewItemClick(1,fertilizerCalacaltedValue?.get(position))
        })
    }
    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }

}
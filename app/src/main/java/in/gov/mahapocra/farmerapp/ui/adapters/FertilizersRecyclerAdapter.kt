package `in`.gov.mahapocra.farmerapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.farmerapp.R
import `in`.gov.mahapocra.farmerapp.util.app_util.DeleteApi
import org.json.JSONArray
import org.json.JSONObject

class FertilizersRecyclerAdapter (
    private var context: Context? = null,
    private var listener: OnMultiRecyclerItemClickListener,
    private var fertilizerCalculatedValue: JSONArray?,
    private var availableOption: String
) : RecyclerView.Adapter<FertilizersRecyclerAdapter.ViewHolder>(), OnMultiRecyclerItemClickListener,
    DeleteApi {

    private lateinit var deleteApi: DeleteApi

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

    fun setItemClickListener(deleteApi: DeleteApi) {
        this.deleteApi = deleteApi
    }

    override fun getItemCount(): Int {
        return fertilizerCalculatedValue?.length()!!
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val optionTv: TextView = itemView.findViewById(R.id.optiontv)
        val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        val deleteOptionImageView: ImageView = mainLinearLayout.findViewById(R.id.deleteOptionImageView)
        val saveOptionImageView: ImageView = mainLinearLayout.findViewById(R.id.optionImg)
        val fertilizerDateRcl: RecyclerView = itemView.findViewById(R.id.fertlizerDate_Rcl)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        for (i in 0 until (fertilizerCalculatedValue?.length() ?: 0)) {
            holder.optionTv.text = context?.getResources()?.getString(R.string.Option) + " ${position+1}"
            val adaptorDbtActivityGrp =
                BbtActivityGrpAdapter(
                    context,
                    this,
                    if (availableOption == "fertilizerSelectedValue"){
                        holder.deleteOptionImageView.visibility = View.VISIBLE
                        holder.saveOptionImageView.visibility = View.GONE
                        (fertilizerCalculatedValue?.get(position) as JSONObject).getJSONArray("option")
                    }else{
                        holder.deleteOptionImageView.visibility = View.GONE
                        holder.saveOptionImageView.visibility = View.VISIBLE
                        (fertilizerCalculatedValue?.get(position) as JSONObject).getJSONArray("Option")
                    }
                    ,
                    "OptonRclAdapter"
                )
            holder.fertilizerDateRcl.setLayoutManager(
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            holder.fertilizerDateRcl.setAdapter(adaptorDbtActivityGrp)
            adaptorDbtActivityGrp.notifyDataSetChanged()
           }

        holder.mainLinearLayout.setOnClickListener {
            listener.onMultiRecyclerViewItemClick(1, fertilizerCalculatedValue?.get(position))
        }

        holder.deleteOptionImageView.setOnClickListener {
            val id =  (fertilizerCalculatedValue?.get(position) as JSONObject).getInt("id")
            val farmerId =  (fertilizerCalculatedValue?.get(position) as JSONObject).getInt("farmer_id")
            val cropId =  (fertilizerCalculatedValue?.get(position) as JSONObject).getInt("crop_id")
            deleteApi.deleteData(id, farmerId, cropId)
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }

    override fun deleteData(id: Int, farmerId: Int, cropId: Int) {
        TODO("Not yet implemented")
    }


}
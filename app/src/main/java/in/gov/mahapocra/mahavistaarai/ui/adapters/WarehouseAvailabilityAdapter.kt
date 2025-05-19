package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.data.model.WareHouseModel
import `in`.gov.mahapocra.mahavistaarai.databinding.WareHouseAvailabilityBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class WarehouseAvailabilityAdapter(
    private val mContext: Context,
    listener: OnMultiRecyclerItemClickListener, private val mJSONArray: JSONArray?
) :
    RecyclerView.Adapter<WarehouseAvailabilityAdapter.ViewHolder>() {

    private val listener: OnMultiRecyclerItemClickListener = listener
    private var warehouseName: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WareHouseAvailabilityBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        try {
            viewHolder.onBind(mJSONArray!!.getJSONObject(position), listener)
            viewHolder.binding.tvContactNumber.setOnClickListener {
                val phoneNumber = viewHolder.binding.tvContactNumber.text.toString()
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                mContext.startActivity(intent)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return mJSONArray?.length() ?: 0
    }

    inner class ViewHolder(val binding: WareHouseAvailabilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(jsonObject: JSONObject?, listener: OnMultiRecyclerItemClickListener?) {
            val wareHouseModel = WareHouseModel(jsonObject)
            warehouseName = wareHouseModel.getWarehouseName()
            val recordedDate: String = wareHouseModel.getRecordedDate()
            val village: String = wareHouseModel.getVillage()
            val address: String = wareHouseModel.getAddress()
            val phone: String = wareHouseModel.getPhone()
            val availableCapacity: String = wareHouseModel.getAvailableCapacity()
            val upperString = warehouseName!!.substring(0, 1)
                .uppercase(Locale.getDefault()) + warehouseName!!.substring(1).lowercase(
                Locale.getDefault()
            )
            binding.wareHouseName.text = upperString
            binding.recordDate.text = recordedDate
            binding.tvAddress.text = address
            binding.tvContactNumber.text = phone
            binding.tvTotalAvailableCapacity.text = "$availableCapacity MT"
        }
    }
}

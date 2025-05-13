package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.co.appinventor.services_api.widget.UIToastMessage
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.data.model.CropsCategName
import `in`.gov.mahapocra.mahavistaarai.data.model.VideoDetails
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.ArrayList
import java.util.Date

class CropCategoriesAdapter(
    private var context: Context,
    private var dataJSONArray: JSONArray,
    private var callerActivity: String,
    private var multiRecyclerItemClickListener: OnMultiRecyclerItemClickListener
) : RecyclerView.Adapter<CropCategoriesAdapter.ViewHolder>(),
    OnMultiRecyclerItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.mail_title_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val jsonObject = dataJSONArray[position] as JSONObject
            val advisoryArray = jsonObject.optJSONArray("crops")
            holder.textView.text = jsonObject.optString("type").toString()
            val cropStageDetailsAdapter = CropStageDetailsAdapter( context, advisoryArray, this, callerActivity)
            holder.videosRecyclerView.setLayoutManager(
                GridLayoutManager(context, 4)
            )
            holder.videosRecyclerView.setAdapter(cropStageDetailsAdapter)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return dataJSONArray.length()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = itemView.findViewById(R.id.titleTextView)
        val videosRecyclerView: RecyclerView = itemView.findViewById(R.id.videosRecyclerView)

    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        if (i == 1) {
            multiRecyclerItemClickListener.onMultiRecyclerViewItemClick(i, obj)
        }
    }

}
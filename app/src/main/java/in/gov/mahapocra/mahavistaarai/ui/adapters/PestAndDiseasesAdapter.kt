package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.pest.DiseaseInformation
import org.json.JSONArray
import org.json.JSONException

class PestAndDiseasesAdapter (private var context: Context? = null,
                              private var diseasesDetails: JSONArray
): RecyclerView.Adapter<PestAndDiseasesAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.particular_stage_diseases, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jsonObject = diseasesDetails.getJSONObject(position)
        try {
            holder.diseaseName.text = jsonObject.optString("type")
            holder.diseaseScrpt.text = jsonObject.optString("subtitle")
            loadImage(holder.climateImage, jsonObject.optString("image"))

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        holder.main_ln.setOnClickListener {
            val intent = Intent(context, DiseaseInformation::class.java)
            intent.putExtra("name", jsonObject.optString("title"))
            intent.putExtra("id", jsonObject.optInt("id"))
            context?.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return diseasesDetails.length()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var diseaseName: TextView = itemView.findViewById(R.id.disease_name)
        var diseaseScrpt: TextView = itemView.findViewById(R.id.diseases_scrpt)
        var climateImage: ImageView = itemView.findViewById(R.id.climate_image)
        var main_ln: LinearLayout = itemView.findViewById(R.id.main_ln)
    }

    private fun loadImage(climateImage: ImageView?, image_url: String?) {
        try {
            Picasso.get().invalidate(image_url)
            Picasso.get()
                .load(image_url)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .resize(400, 400)
                .centerCrop()
                .into(climateImage)
        } catch (ex: Exception) {
            ex.toString()
        }
    }

}
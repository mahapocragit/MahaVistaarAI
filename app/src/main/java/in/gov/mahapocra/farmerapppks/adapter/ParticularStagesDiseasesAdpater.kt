package `in`.gov.mahapocra.farmerapppks.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
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
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.activity.DiseaseInformation
import `in`.gov.mahapocra.farmerapppks.activity.PestsAndDiseasesStages
import `in`.gov.mahapocra.farmerapppks.models.response.DiseaseStages
import `in`.gov.mahapocra.farmerapppks.models.response.DiseasesDetails
import org.json.JSONException

class ParticularStagesDiseasesAdpater (private var context: Context? = null,
                                        private var diseasesDetails: List<DiseasesDetails>
): RecyclerView.Adapter<ParticularStagesDiseasesAdpater.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.particular_stage_diseases, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.diseaseName.text = diseasesDetails.get(position).type
            holder.diseaseScrpt.text = diseasesDetails.get(position).decription
            loadImage(holder.climateImage, diseasesDetails.get(position).img)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        holder.main_ln.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, DiseaseInformation::class.java)
            intent.putExtra("name",diseasesDetails.get(position).name)
            intent.putExtra("id",diseasesDetails.get(position).id)
            context?.startActivity(intent)

        })
    }

    override fun getItemCount(): Int {
        return diseasesDetails.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var diseaseName: TextView = itemView.findViewById<TextView>(R.id.disease_name)
        var diseaseScrpt: TextView = itemView.findViewById<TextView>(R.id.diseases_scrpt)
        var climateImage: ImageView = itemView.findViewById<ImageView>(R.id.climate_image)
        var main_ln: LinearLayout = itemView.findViewById<LinearLayout>(R.id.main_ln)
    }

    fun loadImage(climateImage: ImageView?, image_url: String?) {
        try {
            Picasso.get().invalidate(image_url)
            Picasso.get()
                .load(image_url)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .placeholder(R.drawable.ic_thumbnail)
                .resize(400, 400)
                .centerCrop()
                .error(R.drawable.ic_thumbnail)
                .into(climateImage)
        } catch (ex: Exception) {
            ex.toString()
        }
    }

}
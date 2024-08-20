package `in`.gov.mahapocra.farmerapppks.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.activity.PestsAndDiseasesStages
import `in`.gov.mahapocra.farmerapppks.models.response.ClimateGridModel
import `in`.gov.mahapocra.farmerapppks.models.response.DiseaseStages
import `in`.gov.mahapocra.farmerapppks.models.response.DiseasesDetails
import org.json.JSONException
import java.io.Serializable


class PestAndDiseasesAdpater(
    private var context: Context? = null, private var diseaseStages: List<DiseaseStages>,
) : RecyclerView.Adapter<PestAndDiseasesAdpater.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.pest_diseases_stages, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.stages.text = diseaseStages.get(position).name
            val climateModelArrayList: ArrayList<ClimateGridModel> = ArrayList<ClimateGridModel>()
            var groupName: ArrayList<String> = ArrayList()
            var webUrl: ArrayList<String> = ArrayList()
            climateModelArrayList.clear()
            val diseasesDetails: ArrayList<DiseasesDetails> =
                diseaseStages.get(position).diseasesDetails
            var diseasesDetailsSize = diseasesDetails.size
//            if(diseasesDetailsSize > 2){
//                holder.seeMoreTx2.visibility = View.VISIBLE
//            }
            val enrolmentData: ArrayList<Any?> = ArrayList<Any?>()
            for (i in 0 until diseasesDetailsSize) {
                val groupimagePath: String? = diseasesDetails.get(i).img
                val id: Int = diseasesDetails.get(i).id
                groupName.add(diseasesDetails.get(i).decription + "\n")
                climateModelArrayList.add(
                    ClimateGridModel(
                        groupName[i],
                        groupimagePath,
                        id.toString()
                    )
                )
            }
            val adapter = context?.let { ClimateGridAdapter(it, climateModelArrayList, "PestAndDiseasesAdpater") }
            holder.gridView?.setAdapter(adapter)
            adapter?.notifyDataSetChanged()
            holder.seeMoreTx2.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, PestsAndDiseasesStages::class.java)
                intent.putExtra("id", diseaseStages?.get(position)?.id)
                intent.putExtra("name", diseaseStages?.get(position)?.name)
                val args = Bundle()
                args.putSerializable("diseasesDetails", diseaseStages.get(position).diseasesDetails as Serializable?)
              //  args.putSerializable("diseasesDetails", 'diseaseStages?.get(position)?.diseasesDetails' as Serializable?)
                intent.putExtra("BUNDLE",args)
                intent.putExtra("ParticularStagesDiseases", "ParticularStagesDiseases")
                context?.startActivity(intent)
            })

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return diseaseStages.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stages: TextView = itemView.findViewById(R.id.stages)
        val gridView: GridView = itemView.findViewById(R.id.gridViewJobs)
        val seeMoreTx2: TextView = itemView.findViewById(R.id.seeMoreTx2)
    }


}
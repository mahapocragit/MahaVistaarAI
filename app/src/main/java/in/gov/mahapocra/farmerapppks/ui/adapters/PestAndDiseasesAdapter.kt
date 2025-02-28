package `in`.gov.mahapocra.farmerapppks.ui.adapters

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
import `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.menugrid.PestsAndDiseasesStages
import `in`.gov.mahapocra.farmerapppks.data.model.ClimateGridModel
import `in`.gov.mahapocra.farmerapppks.data.model.DiseaseStages
import `in`.gov.mahapocra.farmerapppks.data.model.DiseasesDetails
import org.json.JSONException
import java.io.Serializable


class PestAndDiseasesAdapter(
    private var context: Context? = null, private var diseaseStages: List<DiseaseStages>,
) : RecyclerView.Adapter<PestAndDiseasesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.pest_diseases_stages, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.stages.text = diseaseStages[position].name
            val climateModelArrayList: ArrayList<ClimateGridModel> = ArrayList()
            val groupName: ArrayList<String> = ArrayList()
            climateModelArrayList.clear()
            val diseasesDetails: ArrayList<DiseasesDetails> =
                diseaseStages[position].diseasesDetails
            val diseasesDetailsSize = diseasesDetails.size
            for (i in 0 until diseasesDetailsSize) {
                val groupImagePath: String? = diseasesDetails[i].img
                val id: Int = diseasesDetails[i].id
                groupName.add(diseasesDetails[i].decription + "\n")
                climateModelArrayList.add(
                    ClimateGridModel(
                        groupName[i],
                        groupImagePath,
                        id.toString()
                    )
                )
            }
            val adapter = context?.let { ClimateGridAdapter(it, climateModelArrayList, "PestAndDiseasesAdpater") }
            holder.gridView.adapter = adapter
            adapter?.notifyDataSetChanged()
            holder.seeMoreTx2.setOnClickListener {
                val intent = Intent(context, PestsAndDiseasesStages::class.java)
                intent.putExtra("id", diseaseStages[position].id)
                intent.putExtra("name", diseaseStages[position].name)
                val args = Bundle()
                args.putSerializable(
                    "diseasesDetails",
                    diseaseStages[position].diseasesDetails as Serializable?
                )
                intent.putExtra("BUNDLE", args)
                intent.putExtra("ParticularStagesDiseases", "ParticularStagesDiseases")
                context?.startActivity(intent)
            }

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
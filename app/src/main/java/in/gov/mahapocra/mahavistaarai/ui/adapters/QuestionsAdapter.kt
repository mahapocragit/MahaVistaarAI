package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray

class QuestionsAdapter(
    private val questionsArray: JSONArray?
) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val questionObject = questionsArray?.getJSONObject(position)

        holder.tvQuestion.text = questionObject?.getString("question")

        // Remove old listener to avoid unwanted triggers
        holder.rgAnswer.setOnCheckedChangeListener(null)

        // Restore previously selected answer
        when (questionObject?.opt("answer")) {
            true -> holder.rbYes.isChecked = true
            false -> holder.rbNo.isChecked = true
            else -> holder.rgAnswer.clearCheck()
        }

        // Save answer as Boolean
        holder.rgAnswer.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbYes -> questionObject?.put("answer", true)
                R.id.rbNo -> questionObject?.put("answer", false)
            }
        }
    }

    override fun getItemCount(): Int = questionsArray?.length()?:0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        val rgAnswer: RadioGroup = itemView.findViewById(R.id.rgAnswer)
        val rbYes: RadioButton = itemView.findViewById(R.id.rbYes)
        val rbNo: RadioButton = itemView.findViewById(R.id.rbNo)
    }
}

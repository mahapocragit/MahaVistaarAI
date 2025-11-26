package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.DayMatcher.isTodayMatchingMarathiDay
import `in`.gov.mahapocra.mahavistaarai.util.app_util.RecyclerItemClickListener
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShetishalaScheduleMeetAdapter(private val day: String, private val jsonArray: JSONArray, private val listener: RecyclerItemClickListener) :
    RecyclerView.Adapter<ShetishalaScheduleMeetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val cropTextView: TextView = itemView.findViewById(R.id.cropTextView)
        val zoomLinkTextView: TextView = itemView.findViewById(R.id.zoomLinkTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shetishala_item_view_meet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = jsonArray.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var languageToLoad = "mr"
        if (AppSettings.getLanguage(holder.itemView.context)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        val item = jsonArray.getJSONObject(position)
        val time = item.optString("time")
        val topic = item.optString("topic")
        val link = item.optString("link")

        holder.timeTextView.text = time
        holder.cropTextView.text = topic

        holder.zoomLinkTextView.setOnClickListener {
            if (isTodayMatchingMarathiDay(day)) {
                listener.onRecyclerItemClick(1, item)
                val browserIntent = Intent(Intent.ACTION_VIEW, link.toUri())
                holder.itemView.context.startActivity(browserIntent)
            } else {
                var zoomMeetingTemplateLabel = "ही बैठक आज उपलब्ध नाही. ती $day साठी निर्धारित आहे."
                if (languageToLoad == "en") {
                    zoomMeetingTemplateLabel =
                        "This meeting is not available today. It is scheduled for $day."
                }
                AlertDialog.Builder(holder.itemView.context).setTitle(zoomMeetingTemplateLabel)
                    .setPositiveButton(R.string.okay, null).show()
            }
        }
    }

    // Check if current time is before meeting time
    private fun isUserTooEarly(meetingTime: String): Boolean {
        val convertedMeetingTime = convertMarathiTimeTo24Hour(meetingTime) ?: return false
        val currentTime = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date())

        return currentTime < convertedMeetingTime
    }

    private fun convertMarathiTimeTo24Hour(time: String): String? {
        val marathiToEnglishDigits = mapOf(
            '०' to '0', '१' to '1', '२' to '2', '३' to '3', '४' to '4',
            '५' to '5', '६' to '6', '७' to '7', '८' to '8', '९' to '9'
        )

        val parts = time.trim().split(Regex("[\\s.]+"), limit = 2)
        if (parts.size < 2) return null

        val period = parts[0]
        val rawTime = parts[1]
        val englishTime = rawTime.map { marathiToEnglishDigits[it] ?: it }.joinToString("")

        val meridiem = when (period) {
            "सकाळी" -> "AM"
            "सायं", "संध्याकाळी", "रात्री" -> "PM"
            else -> ""
        }

        val fullTime = "$englishTime $meridiem"

        return try {
            val inputFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val date = inputFormat.parse(fullTime)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            null
        }
    }

    // Show a popup if too early
    private fun showTooEarlyDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.you_re_too_early)
            .setMessage(R.string.the_meeting_hasn_t_started_yet_please_try_again_closer_to_the_scheduled_time)
            .setPositiveButton(R.string.okay, null)
            .show()
    }
}

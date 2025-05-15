package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray

class NewsAdapter(val jsonArray: JSONArray, val language: String) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsEventTitleTextView: TextView = view.findViewById(R.id.newsEventTitleTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val newsEventDescriptionTextView: TextView =
            view.findViewById(R.id.newsEventDescriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jsonObject = jsonArray.getJSONObject(position)
        val newsUrlToLoad = jsonObject.optString("link")
        val englishTitle = jsonObject.optString("news_headline")
        val englishDescription = jsonObject.optString("short_description")
        val date = jsonObject.optString("date")
        Log.d("TAGGER", "onBindViewHolder: ${formatDate(date)}")

        val translationsJsonObject = jsonObject.optJSONObject("translations")
        val marathiJsonObject = translationsJsonObject?.optJSONObject("mr")
        val marathiTitle = marathiJsonObject?.optString("news_headline")
        val marathiDescription = marathiJsonObject?.optString("short_description")

        val fullDescription = if (language == "mr") marathiDescription else englishDescription
        val title = if (language == "mr") marathiTitle else englishTitle

        holder.newsEventTitleTextView.text = title
        holder.dateTextView.text = buildString {
            append(holder.dateTextView.context.getString(R.string.date))
            append("  ")
            append(formatDate(date))
        }

        // Set up expand/collapse logic
        val words = fullDescription?.split(" ") ?: emptyList()
        val preview = words.take(10).joinToString(" ")
        var isExpanded = false

        holder.newsEventDescriptionTextView.text = getSpannableDescription(
            holder,
            fullDescription.orEmpty(),
            preview,
            isExpanded
        )

        holder.newsEventDescriptionTextView.setOnClickListener {
            isExpanded = !isExpanded
            holder.newsEventDescriptionTextView.text = getSpannableDescription(
                holder,
                fullDescription.orEmpty(),
                preview,
                isExpanded
            )
        }

        holder.newsEventTitleTextView.setOnClickListener {
            holder.newsEventTitleTextView.context.startActivity(
                Intent(
                    holder.newsEventTitleTextView.context,
                    NewsWebViewActivity::class.java
                ).apply {
                    putExtra("newsUrlToLoad", newsUrlToLoad)
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    private fun getSpannableDescription(
        holder: ViewHolder,
        fullText: String,
        previewText: String,
        isExpanded: Boolean
    ): SpannableString {
        val suffix =
            if (isExpanded) holder.newsEventTitleTextView.context.getString(R.string.read_less)
            else holder.newsEventTitleTextView.context.getString(R.string.read_more)
        val displayText = if (isExpanded) fullText + suffix else previewText + suffix
        val spannable = SpannableString(displayText)

        val start = displayText.length - suffix.length
        val end = displayText.length

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#76a5df")), // Change to your preferred color
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.NORMAL),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    fun formatDate(dateTime: String): String {
        return dateTime.substringBefore("T")
    }
}
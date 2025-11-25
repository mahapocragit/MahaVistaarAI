package `in`.gov.mahapocra.mahavistaarai.ui.adapters


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppConstants.TAG
import org.json.JSONArray
import org.json.JSONObject


class StageAdvisoryDetailAdapter(
    private var context: Context,
    private var listener: OnMultiRecyclerItemClickListener,
    private var cropAdvisoryDetailsJSONArray: JSONArray
) : RecyclerView.Adapter<StageAdvisoryDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.stage_advisory_details_temp,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cropAdvisoryDetailsJSONArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropAdvisoryTextView: TextView = itemView.findViewById(R.id.cropSapadvisoryTv)
        val cropImage: ImageView = itemView.findViewById(R.id.crop_image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advisoryJsonDetails: JSONObject =
            cropAdvisoryDetailsJSONArray.get(position) as JSONObject
        val img = advisoryJsonDetails.getString("img")
        val dataString: String = advisoryJsonDetails.getString("data")
        advisoryJsonDetails.getString("id")
        val data =  removeHtml(dataString)

        if (data.length > 50) {
            val textData = data.take(49)
            val readMore: String = context.getString(R.string.read_more)
            val finalString = textData + readMore
            val   sb = SpannableStringBuilder(finalString)
            val fcs = ForegroundColorSpan(Color.rgb(100, 116, 139))
            val   bss = StyleSpan(Typeface.BOLD)
            sb.setSpan(fcs, 52, 61, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            sb.setSpan(bss, 52, 61, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            holder.cropAdvisoryTextView.text = sb
        } else {
            holder.cropAdvisoryTextView.text = data
        }
        holder.cropAdvisoryTextView.setOnClickListener {
            val textView = TextView(context)
            textView.text = HtmlCompat.fromHtml(dataString, HtmlCompat.FROM_HTML_MODE_LEGACY)
            textView.textSize = 16f
            textView.setPadding(70, 16, 32, 16)
            textView.autoLinkMask = Linkify.WEB_URLS
            textView.movementMethod = LinkMovementMethod.getInstance()

            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.Crop_details))
                .setView(textView)
                .setNegativeButton(context.getString(R.string.okay)) { dialog, _ -> dialog.dismiss() }
                .show()
            Log.d(TAG, "onBindViewHolder: expanded")
        }

        try {
            Picasso.get().invalidate(img)
            Picasso.get()
                .load(img)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .resize(450, 450)
                .centerCrop()
                .into(holder.cropImage)
        } catch (ex: Exception) {
            ex.toString()
        }
    }

    private fun removeHtml( original:String): String {
        return android.text.Html.fromHtml(original).toString()
    }
}
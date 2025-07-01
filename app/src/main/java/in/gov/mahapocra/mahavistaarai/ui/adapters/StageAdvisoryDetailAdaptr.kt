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
import org.json.JSONArray
import org.json.JSONObject


class StageAdvisoryDetailAdaptr(
    private var context: Context,
    private var listener: OnMultiRecyclerItemClickListener,
    private var cropAdvisoryDetailsJSONArray: JSONArray,
    private var languageToLoad: String,
    private var cropId: String,
    private var villageID: String
) : RecyclerView.Adapter<StageAdvisoryDetailAdaptr.ViewHolder>() {

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
        val cropSapadvisoryTv: TextView = itemView.findViewById(R.id.cropSapadvisoryTv)
        val onFeedback: TextView = itemView.findViewById(R.id.onFeedback)
        val crop_image: ImageView = itemView.findViewById(R.id.crop_image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advisoryJsonDetails: JSONObject =
            cropAdvisoryDetailsJSONArray.get(position) as JSONObject
       // val num = advisoryJsonDetails.getString("num")
        val img = advisoryJsonDetails.getString("img")
        val datas: String = advisoryJsonDetails.getString("data")
        val id: String = advisoryJsonDetails.getString("id")
        val data =  removeHtml(datas)

        if (data?.length!! > 50) {
            val textData = data.substring(0, 49)
            val readMore: String = context!!.getString(R.string.read_more)
            val finalString = textData + readMore
            val   sb = SpannableStringBuilder(finalString)
            val fcs = ForegroundColorSpan(Color.rgb(0, 125, 255))
            val   bss = StyleSpan(Typeface.BOLD)
            sb.setSpan(fcs, 52, 61, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            sb.setSpan(bss, 52, 61, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            holder.cropSapadvisoryTv.text = sb
        } else {
            holder.cropSapadvisoryTv.text = data
        }
        holder.cropSapadvisoryTv.setOnClickListener {
            val textView = TextView(context)
            textView.text = HtmlCompat.fromHtml(datas, HtmlCompat.FROM_HTML_MODE_LEGACY)
            textView.textSize = 16f
            textView.setPadding(16, 16, 16, 16)
            textView.autoLinkMask = Linkify.WEB_URLS
            textView.movementMethod = LinkMovementMethod.getInstance()

            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.Crop_details))
                .setView(textView)
                .setNegativeButton(context.getString(R.string.okay)) { dialog, _ -> dialog.dismiss() }
                .show()
        }

        try {
            Picasso.get().invalidate(img)
            Picasso.get()
                .load(img)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .resize(450, 450)
                .centerCrop()
                .into(holder.crop_image)
        } catch (ex: Exception) {
            ex.toString()
        }
    }

    private fun removeHtml( original:String): String? {
        if (original == null)
            return null
        return android.text.Html.fromHtml(original).toString()
    }
}
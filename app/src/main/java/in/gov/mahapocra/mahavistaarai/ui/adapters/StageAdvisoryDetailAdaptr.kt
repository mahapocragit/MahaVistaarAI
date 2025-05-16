package `in`.gov.mahapocra.mahavistaarai.ui.adapters


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject


class StageAdvisoryDetailAdaptr(
    private var context: Context? = null,
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
            val alert = AlertDialog.Builder(context!!)
            val wv = WebView(context!!)
            wv.loadDataWithBaseURL(
                "file:///android_asset/",
                datas, "text/html", "utf-8", null
            )
            wv.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    Log.d("TAGGER", "onBindViewHolder: $url")

                    if (url.contains("youtube.com") || url.contains("youtu.be")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view.context.startActivity(intent)
                        return true  // Prevents WebView from loading it
                    }

                    view.loadUrl(url)
                    return true
                }
            }

            alert.setView(wv)
            alert.setTitle(context!!.getString(R.string.Crop_details))
            alert.setNegativeButton(
                context!!.getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }

            alert.show()
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
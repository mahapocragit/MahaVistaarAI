package `in`.gov.mahapocra.farmerapppks.adapter


import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.activity.CropStageAdvisory
import `in`.gov.mahapocra.farmerapppks.fragment.advisory.AdvisoryFeedback
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale


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
            R.layout.stage_advisory_details,
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
        holder.cropSapadvisoryTv.setOnClickListener(View.OnClickListener {
            val alert = AlertDialog.Builder(context!!)
            val wv = WebView(context!!)
            wv.loadDataWithBaseURL(
                "file:///android_asset/",
                datas, "text/html", "utf-8", null
            )
            wv.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }
            }
            alert.setView(wv)
            changeLocalLang()
            alert.setTitle(context!!.getString(R.string.Crop_details))
            alert.setNegativeButton(
                context!!.getString(R.string.cancel)
            ) {
                    dialog, _ -> dialog.dismiss()
            }

            alert.show()
        })

        try {
            Picasso.get().invalidate(img)
            Picasso.get()
                .load(img)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .placeholder(R.drawable.ic_thumbnail)
                .resize(450, 450)
                .centerCrop()
                .error(R.drawable.ic_thumbnail)
                .into(holder.crop_image)
        } catch (ex: Exception) {
            ex.toString()
        }
        holder.onFeedback.setOnClickListener({

//            val advisoryFeedback = AdvisoryFeedback()
//           val fragmentManager = (context as CropStageAdvisory).supportFragmentManager
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.add(R.id.frameLayout, AdvisoryFeedback())
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()

            if ((context as CropStageAdvisory).supportFragmentManager
                    .findFragmentById(R.id.frameLayout) == null
            ) {

                val advisoryFeedback = AdvisoryFeedback()
                val bundle = Bundle()
                bundle.putString("cropId", cropId)
                bundle.putString("villageId", villageID)
                bundle.putString("cropsapadvisoryCropId", "")
                bundle.putString("advisoryCropId", id)
                advisoryFeedback.arguments = bundle
                val fragmentTransaction =
                    (context as CropStageAdvisory).supportFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.frameLayout, advisoryFeedback)
                fragmentTransaction.commit()

                listener.onMultiRecyclerViewItemClick(
                    2,
                    cropAdvisoryDetailsJSONArray.get(position) as JSONObject
                )
            }

        })

    }

    private fun changeLocalLang() {
        if (languageToLoad.equals("mr")){
            val languageToLoad = "hi"

            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            context!!.resources.updateConfiguration(
                config,
                context!!.resources.displayMetrics
            )
            AppSettings.setLanguage(context, "2")

        }
    }

    private fun removeHtml( original:String): String? {
        if (original == null)
            return null
        return android.text.Html.fromHtml(original).toString()
    }
}
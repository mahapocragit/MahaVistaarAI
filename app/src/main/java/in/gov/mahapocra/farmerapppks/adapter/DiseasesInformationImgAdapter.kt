package `in`.gov.mahapocra.farmerapppks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import `in`.gov.mahapocra.farmerapppks.R
import org.json.JSONException

class DiseasesInformationImgAdapter(private var context: Context? = null, private var diseasesImages: ArrayList<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return diseasesImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater
            .from(container.context)
            .inflate(R.layout.diseases_iformation_images, container, false)
        val bannerImage: ImageView = view.findViewById(R.id.climate_image)
        context?.let {
            Glide.with(it)
                .load(diseasesImages.get(position))
                .override(1400, 800) // resizing
                .centerCrop()
                .into(bannerImage)
        }
        container.addView(view)
        return  view
    }

    override  fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}
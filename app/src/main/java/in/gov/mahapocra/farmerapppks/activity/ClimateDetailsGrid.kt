package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.ClimateGridAdapter
import `in`.gov.mahapocra.farmerapppks.models.response.ClimateGridModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class ClimateDetailsGrid : AppCompatActivity() {
    private var gridView: GridView? = null
    private var textViewHeaderTitle: TextView? = null
    private var imgBackArrow: ImageView? = null
    private var relClimateDeatils: RelativeLayout? = null
    val climateModelArrayList: ArrayList<ClimateGridModel> = ArrayList<ClimateGridModel>()

    var groupName: ArrayList<String> = ArrayList()
    var groupImagePath: ArrayList<String> = ArrayList()
    var webUrl: ArrayList<String> = ArrayList()
    var languageToLoad: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_climate_details_grid)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ClimateDetailsGrid).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@ClimateDetailsGrid))
            languageToLoad = "en"
        }
        init()
        textViewHeaderTitle?.setText(R.string.climate_resilient_technology)
        val b = intent.extras
        val craGroppLength = b!!.getInt("craGroppLength")
         groupName = b.getStringArrayList("GroupName") as ArrayList<String>
         groupImagePath = b.getStringArrayList("GroupImagePath") as ArrayList<String>
         webUrl = b.getStringArrayList("WebUrl") as ArrayList<String>
        Log.d("ClimateResilent","groupName==="+groupName.toString())
        Log.d("ClimateResilent","groupImagePath==="+groupImagePath.toString())
        Log.d("ClimateResilent","webUrl==="+webUrl.toString())
        climateModelArrayList.clear()
        for (i in 0 until groupName.size) {
           val groupimagePath: String = groupImagePath[i]
           val webUrl: String = webUrl[i]
            Log.d("groupImagePath[i]",groupImagePath[i])
            climateModelArrayList.add(ClimateGridModel(groupName[i], groupimagePath,webUrl))
        }
        val adapter = ClimateGridAdapter(this, climateModelArrayList,"ClimateDetailsGrid")
        gridView?.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        gridView!!.onItemClickListener =
            OnItemClickListener { parents, view, position, ids ->
                Log.d("gridView", "Pos=$position")
                val url:String= webUrl[position]
                Log.d("url", url)
                val intent = Intent(this, ResilientWebUrl::class.java)
                val b = Bundle()
                b.putSerializable("webUrl", url)
                intent.putExtras(b)
                startActivity(intent)
            }
        imgBackArrow?.setVisibility(View.VISIBLE)
        imgBackArrow?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ClimateResilintTechnology::class.java)
            startActivity(intent)
        })
    }
    override fun onResume() {
        super.onResume()
    }
     fun init() {
         gridView = findViewById<View>(R.id.gridViewJobs) as GridView
         relClimateDeatils = findViewById<View>(R.id.relClimateDeatils) as RelativeLayout
         textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle)
         imgBackArrow=findViewById(R.id.imgBackArrow)
     }
    override fun onBackPressed() {
        climateModelArrayList.clear()
        val intent = Intent(this, ClimateResilintTechnology::class.java)
        startActivity(intent)
        finish()
    }
}
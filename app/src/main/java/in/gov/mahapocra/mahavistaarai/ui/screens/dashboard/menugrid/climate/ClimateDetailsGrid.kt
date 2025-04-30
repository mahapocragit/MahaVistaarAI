package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.climate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ClimateGridAdapter
import `in`.gov.mahapocra.mahavistaarai.data.model.ClimateGridModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom

class ClimateDetailsGrid : AppCompatActivity() {
    private var gridView: GridView? = null
    private var textViewHeaderTitle: TextView? = null
    private var imgBackArrow: ImageView? = null
    private val climateModelArrayList: ArrayList<ClimateGridModel> = ArrayList()

    private var groupName: ArrayList<String> = ArrayList()
    private var groupImagePath: ArrayList<String> = ArrayList()
    private var webUrl: ArrayList<String> = ArrayList()
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_climate_details_grid)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@ClimateDetailsGrid).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@ClimateDetailsGrid))
            languageToLoad = "en"
        }
        LocalCustom.configureLocale(baseContext, languageToLoad)
        init()
        textViewHeaderTitle?.setText(R.string.climate_resilient_technology)
        val b = intent.extras
        b!!.getInt("craGroppLength")
        groupName = b.getStringArrayList("GroupName") as ArrayList<String>
        groupImagePath = b.getStringArrayList("GroupImagePath") as ArrayList<String>
        webUrl = b.getStringArrayList("WebUrl") as ArrayList<String>
        climateModelArrayList.clear()
        for (i in 0 until groupName.size) {
            val groupimagePath: String = groupImagePath[i]
            val webUrl: String = webUrl[i]
            climateModelArrayList.add(ClimateGridModel(groupName[i], groupimagePath, webUrl))
        }
        val adapter = ClimateGridAdapter(this, climateModelArrayList, "ClimateDetailsGrid")
        gridView?.adapter = adapter
        adapter.notifyDataSetChanged()
        gridView!!.onItemClickListener =
            OnItemClickListener { parents, view, position, ids ->
                val url: String = webUrl[position]
                val intent = Intent(this, ResilientWebUrl::class.java)
                val b = Bundle()
                b.putSerializable("webUrl", url)
                intent.putExtras(b)
                startActivity(intent)
            }
        imgBackArrow?.visibility = View.VISIBLE
        imgBackArrow?.setOnClickListener {
            val intent = Intent(this, ClimateResilientTechnology::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        gridView = findViewById<View>(R.id.gridViewJobs) as GridView
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle)
        imgBackArrow = findViewById(R.id.imgBackArrow)
    }

    override fun onBackPressed() {
        climateModelArrayList.clear()
        val intent = Intent(this, ClimateResilientTechnology::class.java)
        startActivity(intent)
        finish()
    }
}
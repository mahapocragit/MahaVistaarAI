package `in`.gov.mahapocra.farmerapppks.activity


import `in`.co.appinventor.services_api.api.AppinventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.debug.DebugLog
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.DrawerMenuAdapter
import `in`.gov.mahapocra.farmerapppks.adapter.RecyclerViewAdapter
import `in`.gov.mahapocra.farmerapppks.api.APIRequest
import `in`.gov.mahapocra.farmerapppks.api.APIServices
import `in`.gov.mahapocra.farmerapppks.app_util.AppConstants
import `in`.gov.mahapocra.farmerapppks.app_util.AppHelper
import `in`.gov.mahapocra.farmerapppks.app_util.AppString
import `in`.gov.mahapocra.farmerapppks.models.response.DataModel
import `in`.gov.mahapocra.farmerapppks.models.response.ResponseModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import java.util.*


class Dashboard : AppCompatActivity(), OnMultiRecyclerItemClickListener, AdapterView.OnItemClickListener, RecyclerViewAdapter.ItemListener,
    ApiCallbackCode {

    private var recyclerView: RecyclerView? = null
    private var mMenuListView: ListView? = null
    private var jsonArray: JSONArray? = null
   // lateinit var navigationView: NavigationView
    lateinit var aboutPocra: TextView
    lateinit var villageProfile: TextView
     private var tv_farmerName: TextView? = null
    private var tv_famerPhoneNumber: TextView? = null
    private var nav_view = null
    var farmerId : Int ? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initComponents()
        setConfiguration()
        setOnClick()

    }

    private fun getUserDetails()
    {
        val jsonObject = JSONObject()
        //var farmerId: Int = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        Log.d("farmerId11111", farmerId.toString());
        try {
            jsonObject.put("SecurityKey", APIServices.SSO_KEY)
            jsonObject.put("FAAPRegistrationID", farmerId)
            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api = AppinventorApi(this, APIServices.DBT, "", AppString(this).getkMSG_WAIT(), true)
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getGetRegistration(requestBody)
            DebugLog.getInstance().d("param1=" + responseCall.request().toString())
            DebugLog.getInstance().d("param2=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            api.postRequest(responseCall, this, 1)
            DebugLog.getInstance().d("param=" + responseCall.request().toString())
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()))
            //editor.putString(userRegistration, "UserRegistration")
          //  Log.d("User_Registration===",userRegistration )

        } catch (e: JSONException) {
            DebugLog.getInstance().d("JSONException=" + e.toString())
            e.printStackTrace()
        }
    }

    private fun initComponents() {

        farmerId = AppSettings.getInstance().getIntValue(this, AppConstants.fREGISTER_ID, 0)
        if (farmerId!! > 0) {
            getUserDetails()
        }

        mMenuListView = findViewById(R.id.menuListView)
      // navigationView = findViewById(R.id.nav_view)
        aboutPocra = findViewById(R.id.aboutPocra)
        villageProfile = findViewById(R.id.villageProfile)
        tv_farmerName = findViewById(R.id.tv_farmerName)
        tv_famerPhoneNumber = findViewById(R.id.tv_famerPhoneNumber)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
       // (supportActionBar as Toolbar?)!!.setNavigationIcon(R.drawable.hamburger_menuicon)
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout

        val toggle = ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        toggle.setToolbarNavigationClickListener(View.OnClickListener {
            drawer.openDrawer(
                    GravityCompat.START
            )
        })
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.hamburger_menuicon);

        drawer.addDrawerListener(toggle)
        toggle.syncState()
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
        }
       // nav_view = findViewById(R.id.nav_view)
        var userNeme: String= AppSettings.getInstance().getValue(this, AppConstants.uName,AppConstants.uName )
        var userNumber: String= AppSettings.getInstance().getValue(this, AppConstants.uMobileNo,AppConstants.uMobileNo )
        Log.d("getStrName=", userNeme)
       // tv_farmerName.setText(getStrName)

      //  nav_view?.let { navigationView.inflateHeaderView(it) }
      //  tv_farmerName.setText("hfghjkghhj")


        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val hView = navigationView.getHeaderView(0)
        val nav_user_name = hView.findViewById<View>(R.id.tv_farmerName) as TextView
        val nav_user_phone = hView.findViewById<View>(R.id.tv_famerPhoneNumber) as TextView
        if (!userNeme.equals("USER_NAME")) {
            nav_user_name.setText(userNeme)
            nav_user_phone.setText(userNumber)
        }
        recyclerView = findViewById(R.id.recyclerView)
        val arrayList = ArrayList<Any>()
        arrayList.add(DataModel(resources.getText(R.string.pocra_village) as String?, R.drawable.dashboard_icon1, "#EAF2E0"))
        arrayList.add(DataModel("Climate Resilent Technology", R.drawable.climate_resilient, "#EAF2E0"))
        arrayList.add(DataModel("DBT Schemes", R.drawable.pocra_dbt_icon, "#EAF2E0"))
        arrayList.add(DataModel("Soil Health Card", R.drawable.soil_health_card, "#EAF2E0"))
        arrayList.add(DataModel("Market Price", R.drawable.market_price, "#EAF2E0"))
        arrayList.add(DataModel("Warehouse Availabilities", R.drawable.warehouse_availabilities, "#EAF2E0"))
        arrayList.add(DataModel("GIS", R.drawable.gis, "#EAF2E0"))
        arrayList.add(DataModel("Pest Management", R.drawable.pest_management, "#EAF2E0"))
        arrayList.add(DataModel("Bandhavan", R.drawable.bandhavan, "#EAF2E0"))
        arrayList.add(DataModel("Crop Advisory", R.drawable.pocra_logo, "#EAF2E0"))
        val adapters = RecyclerViewAdapter(this, arrayList, this)
        Log.d("arrayList11111", arrayList.toString())
        recyclerView!!.adapter = adapters
        val manager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager

        val navHeader = LayoutInflater.from(this)
            .inflate(R.layout.nav_header_main, null) as LinearLayout
        navigationView.addHeaderView(navHeader);

    }
    private fun setOnClick()
    {
        aboutPocra.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, AboutPocra::class.java)
            startActivity(intent)
        })
        villageProfile.setOnClickListener(View.OnClickListener {
            val intent2 = Intent(applicationContext, MyVillageProfile::class.java)
            startActivity(intent2)
        })
    }

    private fun setConfiguration() {
        try {
            jsonArray = AppHelper.getInstance().getMenuOption()
            Log.d("jsonArray111", jsonArray.toString())
            Log.d("farmerIdfarmerId", farmerId.toString())
            var menuAdapter = DrawerMenuAdapter(this, jsonArray,farmerId)
            mMenuListView!!.adapter = menuAdapter
        }
        catch (e: JSONException){
            Log.d("JSONException", e.toString())
        }
        mMenuListView!!.onItemClickListener = this
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
        try {
            val jsonObject = jsonArray!!.getJSONObject(i)
            val id = jsonObject.getInt("id")
            Log.d("id1111", id.toString())
            if (id == 0) {
                val intent = Intent(this, Registration::class.java)
                startActivity(intent)
            }
            if (id == 1) { //Intent intent = new Intent(this, FinancialDetailsActivity.class);
                val intent = Intent(this, CommingSoonActivity::class.java)
                startActivity(intent)
            }
            if (id == 2) {
                val intent = Intent(this, CommingSoonActivity::class.java)
                startActivity(intent)
            }
            if (id == 3) {
                val intent = Intent(this, CommingSoonActivity::class.java)
                startActivity(intent)
            }
            if (id == 4) {
                val intent = Intent(this, CommingSoonActivity::class.java)

                startActivity(intent)
            }
            if (id == 5) {
                val intent = Intent(this, CommingSoonActivity::class.java)
                startActivity(intent)
            }
            if (id == 6) {
                val intent = Intent(this, LoginScreen::class.java)

                startActivity(intent)
            }
            if (id == 7) {
                //AppSettings.getInstance().setBooleanValue(this, AppConstants.kIS_OFFLINE, false)
                AppUtility.getInstance().clearAppSharedPrefData(this, AppConstants.kSHARED_PREF)
                val intent = Intent(this, SplashScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
        }catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        TODO("Not yet implemented")
    }
    override fun onItemClick(item: DataModel?) {
        Log.d("item1111", item.toString())
        when (item!!.text) {
            "Weather" -> {
//                val weatherIntent = Intent(this@Dashboard, ForecastWeather::class.java)
//                startActivity(weatherIntent)
            }
            "Climate Resilent Technology" -> {
                val parkingIntent = Intent(this@Dashboard, CommingSoonActivity::class.java)
                startActivity(parkingIntent)
            }
            "DBT Schemes" -> {
                val Dbt_schemesIntent = Intent(this@Dashboard, DbtSchemes::class.java)
                startActivity(Dbt_schemesIntent)
            }
            "Soil Health Card" -> {
                val healthCardActivityIntent = Intent(this@Dashboard, HealthCardActivity::class.java)
                startActivity(healthCardActivityIntent)
            }
            "Market Price" -> {
                val parkingIntent = Intent(this@Dashboard, CommingSoonActivity::class.java)
                startActivity(parkingIntent)
            }
            "Warehouse Availabilities" -> {
                val warehouseIntent = Intent(this@Dashboard, Warehouse::class.java)
                startActivity(warehouseIntent)
            }
            "GIS" -> {
                val gisIntent = Intent(this@Dashboard, Gis::class.java)
                startActivity(gisIntent)
            }
            "Pest Management" -> {
                val parkingIntent = Intent(this@Dashboard, CommingSoonActivity::class.java)
                startActivity(parkingIntent)
            }
            "Bandhavan" -> {
                val parkingIntent = Intent(this@Dashboard, CommingSoonActivity::class.java)
                startActivity(parkingIntent)
            }
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        DebugLog.getInstance().d("onResponse=$obj")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        if (i == 1) {
            if (jSONObject != null) {
                Log.d("jSONObject232dacsboard", jSONObject.toString())
                DebugLog.getInstance().d("onResponse=$jSONObject")
                val response = ResponseModel(jSONObject)
                if (response.getStatus()) {
                    var strName =  jSONObject.getString("Name")
                    var strMobNo: String =  jSONObject.getString("MobileNo")
                    var strEmailId: String =  jSONObject.getString("EmailId")
                    var strFFAReg: Int =  jSONObject.getInt("FAAPRegistrationID")
                    var strDistName: String =  jSONObject.getString("DistrictName")
                    var strDistId: Int =  jSONObject.getInt("DistrictID")
                    var strTalukaName: String =  jSONObject.getString("TalukaName")
                    var strTalukaId: Int =  jSONObject.getInt("TalukaID")


                    //FarmerRegstredID = "3"
                    AppSettings.getInstance().setValue(this, AppConstants.uName, strName)
                    AppSettings.getInstance().setValue(this, AppConstants.uMobileNo, strMobNo)
                    AppSettings.getInstance().setValue(this, AppConstants.uEmail, strEmailId)
                    AppSettings.getInstance().setIntValue(this, AppConstants.fREGISTER_ID, strFFAReg)
                    AppSettings.getInstance().setValue(this, AppConstants.uDIST, strDistName)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uDISTId, strDistId)
                    AppSettings.getInstance().setValue(this, AppConstants.uTALUKA, strTalukaName)
                    AppSettings.getInstance().setIntValue(this, AppConstants.uTALUKAID, strTalukaId)
                    Log.d("SplashScreen::","onResponse"+response);

                    var districtName: String  =  AppSettings.getInstance().getValue(this, AppConstants.uDIST, AppConstants.uDIST)
                    var taluka: String  =  AppSettings.getInstance().getValue(this, AppConstants.uTALUKA, AppConstants.uTALUKA)
                    Log.d("districtName::taluka",districtName+" ::"+taluka);

                }
            }
        }
    }
}







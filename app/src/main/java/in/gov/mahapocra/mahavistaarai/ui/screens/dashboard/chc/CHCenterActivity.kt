package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import `in`.co.appinventor.services_api.api.AppInventorApi
import `in`.co.appinventor.services_api.app_util.AppUtility
import `in`.co.appinventor.services_api.listener.ApiCallbackCode
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.data.api.APIRequest
import `in`.gov.mahapocra.mahavistaarai.data.api.APIServices
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChcenterBinding
import `in`.gov.mahapocra.mahavistaarai.util.app_util.AppString
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Retrofit

class CHCenterActivity : AppCompatActivity(), ApiCallbackCode {

    private lateinit var binding: ActivityChcenterBinding
    private lateinit var adapter: CHCenterRecyclerAdapter
    private var tempStrArr = mutableListOf<String>()
    private var locationLat = 18.201176
    private var locationLong = 75.357841

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChcenterBinding.inflate(layoutInflater)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(binding.root)

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.chc_title)

        for (i in 1..10) {
            tempStrArr.add("Hello $i")
        }
        fetchDataForCHC()
        setupMapView()
        toggleView(true)
        binding.listViewToggleButton.setOnClickListener { toggleView(true) }
        binding.mapViewToggleButton.setOnClickListener { toggleView(false) }
    }

    private fun fetchDataForCHC() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api_key", APIServices.SSO_KEY)
            jsonObject.put("lat", locationLat)
            jsonObject.put("lon", locationLong)

            val requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString())
            val api =
                AppInventorApi(
                    this,
                    APIServices.FARMER,
                    "",
                    AppString(this).getkMSG_WAIT(),
                    true
                )
            val retrofit: Retrofit = api.getRetrofitInstance()
            val apiRequest = retrofit.create(APIRequest::class.java)
            val responseCall: Call<JsonObject> = apiRequest.getCHCInformation(requestBody)
            api.postRequest(responseCall, this, 4)
        } catch (e: Exception) {
            Log.d("TAGGER", "fetchDataForCHC: $e")
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissions.any {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    private fun toggleView(showRecyclerView: Boolean) {
        if (showRecyclerView) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.mapView.visibility = View.GONE
            binding.listViewToggleButton.apply {
                background =
                    ContextCompat.getDrawable(this@CHCenterActivity, R.drawable.shape_right_green)
                setTextColor(Color.WHITE)
            }
            binding.mapViewToggleButton.apply {
                background =
                    ContextCompat.getDrawable(this@CHCenterActivity, R.drawable.shape_left_white)
                setTextColor(Color.BLACK)
            }
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.mapView.visibility = View.VISIBLE
            binding.listViewToggleButton.apply {
                background =
                    ContextCompat.getDrawable(this@CHCenterActivity, R.drawable.shape_right)
                setTextColor(Color.BLACK)
            }
            binding.mapViewToggleButton.apply {
                background = ContextCompat.getDrawable(this@CHCenterActivity, R.drawable.shape_left)
                setTextColor(Color.WHITE)
            }
            checkPermissions()
        }
    }

    override fun onFailure(obj: Any?, th: Throwable?, i: Int) {
        TODO("Not yet implemented")
    }

    override fun onResponse(jSONObject: JSONObject?, i: Int) {
        Log.d("TAGGER", "onResponse: $jSONObject")
        if (jSONObject != null) {
            val data = jSONObject.optJSONArray("data")
            adapter = CHCenterRecyclerAdapter(data)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter

            // Iterate through the data and add markers to the map
            for (index in 0 until data.length()) {
                val item = data.getJSONObject(index)
                val lat = item.optString("lat")
                val lon = item.optString("lon")

                // Convert lat and lon to Double
                val latitude = lat.toDoubleOrNull()
                val longitude = lon.toDoubleOrNull()

                // Check if the latitude and longitude are valid
                if (latitude != null && longitude != null) {
                    val geoPoint = GeoPoint(latitude, longitude)

                    // Create a marker
                    val marker = Marker(binding.mapView)
                    marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_red_location)
                    marker.position = geoPoint
                    marker.title =
                        "Marker $index" // Optional: You can set a custom title or other attributes
                    marker.snippet =
                        "Lat: $latitude, Lon: $longitude" // Optional: Show additional info in the marker's snippet

                    // Set a click listener for the marker
                    marker.setOnMarkerClickListener { marker, _ ->
                        val bottomSheet = item.optJSONArray("equipment")?.let {
                            MarkerBottomSheetFragment.newInstance(
                                item.optString("contact_name"), item.optString("chcname"), "${
                                    item.optString("distance")
                                } kms", latitude, longitude, it
                            )
                        }
                        bottomSheet?.show(supportFragmentManager, bottomSheet.tag)
                        true
                    }

                    // Add marker to the map
                    binding.mapView.overlays.add(marker)
                }
            }
        }
    }

    private fun setupMapView() {
        // Initialize MapView
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)

        // Set map center and zoom level
        val mapController = binding.mapView.controller
        val startPoint = GeoPoint(locationLat, locationLong) // Mumbai, India
        mapController.setZoom(12.0)
        mapController.setCenter(startPoint)

        // Add Marker
        val marker = Marker(binding.mapView)
        marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_location)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Current Location"
        binding.mapView.overlays.add(marker)
    }
}
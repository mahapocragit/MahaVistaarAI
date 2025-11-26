package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityChcenterBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.CHCenterRecyclerAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.ChatbotActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.LeaderboardViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.CROP_ADVISORY_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.CUSTOM_HIRING_CENTRE_POINT
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AnimationHelper
import `in`.gov.mahapocra.mahavistaarai.util.helpers.DraggableTouchListener
import `in`.gov.mahapocra.mahavistaarai.util.helpers.ScoreBubbleHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class CHCenterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChcenterBinding
    private lateinit var adapter: CHCenterRecyclerAdapter
    private val farmerViewModel: FarmerViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001
    private lateinit var languageToLoad: String
    private var locationLat = 18.914708311426686
    private var locationLong = 72.81793873488796

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@CHCenterActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityChcenterBinding.inflate(layoutInflater)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(binding.root)
        uiResponsive(binding.root)

        lifecycleScope.launch {
            delay(5000) // 5 seconds
            binding.bubbleIconImageView.visibility = View.GONE
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissions()
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = getString(R.string.chc_title)
        binding.toolbar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(this@CHCenterActivity, DashboardScreen::class.java))
            }
        })
        observeResponse()
        AnimationHelper.shrinkLeftToCenter(binding.bubbleIconImageView)
        toggleView(true)
        binding.listViewToggleButton.setOnClickListener { toggleView(true) }
        binding.mapViewToggleButton.setOnClickListener { toggleView(false) }
        setupMapView(locationLat, locationLong)
        binding.chatbotIcon.setOnTouchListener(DraggableTouchListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        })
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        } else {
            checkGPSEnabledAndFetchLocation()
        }
    }

    private fun checkGPSEnabledAndFetchLocation() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS is enabled
            fetchLocation()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, 101)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(this, "Failed to open GPS settings", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "GPS is not enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                fetchLocation()
            } else {
                Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeResponse() {

        leaderboardViewModel.responseUpdateUserPoints.observe(this){ response->
            if (response!=null){
                val jSONObject = JSONObject(response.toString())
                val status = jSONObject.optInt("status")
                if (status==200){
                    ScoreBubbleHelper.showScoreBubble(binding.root, "+10🔥 Points Added")
                }
            }
        }

        farmerViewModel.chcCentersResponse.observe(this){
            if (it!=null){
                val jSONObject = JSONObject(it.toString())
                jSONObject.optJSONArray("data")?.let { data ->
                    adapter = CHCenterRecyclerAdapter(data)
                    binding.recyclerView.apply {
                        layoutManager = LinearLayoutManager(this@CHCenterActivity)
                        adapter = this@CHCenterActivity.adapter
                    }

                    repeat(data.length()) { index ->
                        data.optJSONObject(index)?.let { item ->
                            val latitude = item.optString("lat").toDoubleOrNull()
                            val longitude = item.optString("lon").toDoubleOrNull()

                            if (latitude != null && longitude != null) {
                                Marker(binding.mapView).apply {
                                    position = GeoPoint(latitude, longitude)
                                    icon = ContextCompat.getDrawable(this@CHCenterActivity, R.drawable.ic_red_location)
                                    title = "Marker $index"
                                    snippet = "Lat: $latitude, Lon: $longitude"
                                    setOnMarkerClickListener { _, _ ->
                                        item.optJSONArray("equipment")?.let { equipment ->
                                            MarkerBottomSheetFragment.newInstance(
                                                item.optString("contact_name"),
                                                item.optString("chcname"),
                                                "${item.optString("distance")} kms",
                                                latitude,
                                                longitude,
                                                equipment
                                            ).show(supportFragmentManager, "MarkerBottomSheet")
                                        }
                                        true
                                    }
                                    binding.mapView.overlays.add(this)
                                }
                            }
                        }
                    }
                    leaderboardViewModel.updateUserPoints(this, CUSTOM_HIRING_CENTRE_POINT)
                }
            }
        }

        farmerViewModel.error.observe(this){
            Log.d(TAG, "observeResponse: $it")
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

    private fun setupMapView(latitude: Double, longitude: Double) {
        // Clear existing overlays to prevent duplicate markers
        binding.mapView.overlays.clear()

        // Initialize MapView
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)

        // Set map center and zoom level
        val mapController = binding.mapView.controller
        val startPoint = GeoPoint(latitude, longitude)
        mapController.setZoom(12.0)
        mapController.setCenter(startPoint)

        // Add Marker
        val marker = Marker(binding.mapView)
        marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_location)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Current Location"
        binding.mapView.overlays.add(marker)

        binding.mapView.invalidate()
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                locationLat = location.latitude
                locationLong = location.longitude
                Toast.makeText(this, "Location Updated!!", Toast.LENGTH_LONG).show()
                setupMapView(locationLat, locationLong)
            } else {
                Toast.makeText(this, "Unable to Fetch Location!!", Toast.LENGTH_SHORT).show()
            }

            // Either way, attempt to fetch data (you can decide to block this if location is null)
            farmerViewModel.fetchDataForCHC(this, locationLat, locationLong)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}
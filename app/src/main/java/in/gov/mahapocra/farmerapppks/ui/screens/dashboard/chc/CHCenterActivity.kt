package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.chc

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityChcenterBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class CHCenterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChcenterBinding
    private lateinit var adapter: CHCenterRecyclerAdapter
    private var tempStrArr = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChcenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = "CHC Center"

        for (i in 1..10) {
            tempStrArr.add("Hello $i")
        }
        toggleView(true)
        binding.listViewToggleButton.setOnClickListener { toggleView(true) }
        binding.mapViewToggleButton.setOnClickListener { toggleView(false) }
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

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume() // Resume map view
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause() // Pause map view
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

            adapter = CHCenterRecyclerAdapter(tempStrArr)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
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
            binding.mapView.setTileSource(TileSourceFactory.OpenTopo)

            // Enable Zoom
            binding.mapView.setMultiTouchControls(true)

            // Set Initial Map Position
            val mapController = binding.mapView.controller
            val startPoint = GeoPoint(37.7749, -122.4194) // Example: San Francisco
            mapController.setZoom(15.0)
            mapController.setCenter(startPoint)

            // Add Marker
            val marker = Marker(binding.mapView)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "San Francisco"
            binding.mapView.overlays.add(marker)

            checkPermissions()
        }
    }
}
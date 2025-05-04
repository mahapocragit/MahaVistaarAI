package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import `in`.gov.mahapocra.mahavistaarai.R
import org.json.JSONArray
import org.json.JSONObject


class MarkerBottomSheetFragment : BottomSheetDialogFragment() {

    private var title: String? = null
    private var snippet: String? = null
    private var equipments: String? = null
    private var distance: String? = null
    private var lat: Double? = null
    private var long: Double? = null

    companion object {
        fun newInstance(
            title: String?,
            snippet: String?,
            distance: String?,
            lat: Double,
            long: Double,
            equipmentArray: JSONArray
        ): MarkerBottomSheetFragment {
            val fragment = MarkerBottomSheetFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("snippet", snippet)
            args.putString("distance", distance)
            args.putDouble("lat", lat)
            args.putDouble("long", long)
            args.putString("equipments", equipmentArray.toString())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_marker_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = arguments?.getString("title")
        snippet = arguments?.getString("snippet")
        distance = arguments?.getString("distance")
        equipments = arguments?.getString("")
        lat = arguments?.getDouble("lat")
        long = arguments?.getDouble("long")
        equipments = arguments?.getString("equipments")

        // Set the title and snippet to your bottom sheet's views, for example:
        view.findViewById<TextView>(R.id.titleText).text = snippet
        view.findViewById<TextView>(R.id.snippetText).text = title
        view.findViewById<TextView>(R.id.equipmentText).text = equipments?.let {
            formattedEquipmentText(
                it
            )
        }
        view.findViewById<TextView>(R.id.distanceText).text = distance
        view.findViewById<Button>(R.id.redirectButton).setOnClickListener {

            // Create an intent to open Google Maps
            val gmmIntentUri = Uri.parse("geo:$lat,$long?q=$lat,$long(Label)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Ensure it opens in Google Maps app

            // Check if Google Maps is installed before starting activity
            if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // If Google Maps is not installed, open in a browser
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$long")
                startActivity(Intent(Intent.ACTION_VIEW, webUri))
            }
        }
    }

    private fun formattedEquipmentText(jsonArray: String): String {
        val equipmentArray = JSONArray(jsonArray)
        if (equipmentArray.length() == 0) return "Unable to fetch equipment list"

        val equipmentList = StringBuilder()
        for (i in 0 until equipmentArray.length()) {
            val equipmentObject = equipmentArray.getJSONObject(i)
            val equipmentName = equipmentObject.getString("equipment")
            equipmentList.append("• ").append(equipmentName).append("\n")
        }

        return equipmentList.toString().trim()
    }
}
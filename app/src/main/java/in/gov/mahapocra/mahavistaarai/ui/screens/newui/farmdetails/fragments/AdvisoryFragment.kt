package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.AdvisoryAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import org.json.JSONObject

class AdvisoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var advisoryAdapter: AdvisoryAdapter
    private val viewmodel: FarmerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_advisory,
            container,
            false
        )

        recyclerView = view.findViewById(R.id.advisoryRecyclerView)
        val noAdvisoryTextView = view.findViewById<TextView>(R.id.noAdvisoryTextView)


        viewmodel.farmerSpecificAdvisoryResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val jsonObject = JSONObject(response.toString())
                val advisoriesArray = jsonObject.getJSONArray("advisories")
                Log.d(TAG, "onCreateView: ${advisoriesArray.length()}")
                if (advisoriesArray.length() == 0) {
                    noAdvisoryTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    noAdvisoryTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    advisoryAdapter = AdvisoryAdapter(
                        advisoriesArray
                    ) { selectedItem ->
                        val dialogView = layoutInflater.inflate(
                            R.layout.item_detailed_advisory,
                            null
                        )

                        val dialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .create()

                        // Example:
                        val closeDialogButton =
                            dialogView.findViewById<ImageView>(R.id.closeDialogImageView)
                        closeDialogButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        val titleAdvisory = dialogView.findViewById<TextView>(R.id.titleAdvisory)
                        val descriptionAdvisory =
                            dialogView.findViewById<TextView>(R.id.descriptionAdvisory)
                        val sowingDateAdvisory =
                            dialogView.findViewById<TextView>(R.id.sowingDateAdvisory)
                        val bodyTextView = dialogView.findViewById<TextView>(R.id.bodyTextView)

                        // Set your selectedItem data here
                        titleAdvisory.text = selectedItem.optString("title")
                        descriptionAdvisory.text = selectedItem.optString("body")
                        sowingDateAdvisory.text = selectedItem.optString("date")
                        bodyTextView.text = selectedItem.optString("description")

                        dialog.show()
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        // Item clicked
                        submitSelectedItem(selectedItem)
                    }

                    recyclerView.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = advisoryAdapter
                        setHasFixedSize(true)
                    }
                }
            }
        }
        viewmodel.farmerSpecificAdvisory(requireContext())

        return view
    }

    private fun submitSelectedItem(item: JSONObject) {

        // API submission here

        Log.d("Advisory", "Title: ${item.optString("title")}")
        Log.d("Advisory", "Body: ${item.optString("body")}")
        Log.d("Advisory", "Description: ${item.optString("description")}")
        Log.d("Advisory", "Date: ${item.optString("date")}")
    }
}

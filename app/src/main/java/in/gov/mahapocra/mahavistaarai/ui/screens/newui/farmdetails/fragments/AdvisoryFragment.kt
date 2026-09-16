package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.AdvisoryAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters.AdvisoryModel
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.FarmerViewModel
import org.json.JSONObject
import kotlin.getValue

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

        // Sample data
        val advisoryList = listOf(
            AdvisoryModel(
                title = "Corn crop advisory",
                date = "11 Feb 2026",
                description = "Early signs of armyworm infestation observed in southern fields. Scout immediately.",
                body = "Farmers are advised to take necessary precautions."
            ),
            AdvisoryModel(
                title = "Rice crop advisory",
                date = "10 Feb 2026",
                description = "Irrigation level too low. Ensure adequate water flow for transplanting phase.",
                body = "Ensure proper drainage and monitor crops regularly."
            )
        )

        advisoryAdapter = AdvisoryAdapter(
            advisoryList
        ) { selectedItem ->
            val dialogView = layoutInflater.inflate(
                R.layout.item_detailed_advisory,
                null
            )

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Example:
             val closeDialogButton = dialogView.findViewById<ImageView>(R.id.closeDialogImageView)
            closeDialogButton.setOnClickListener {
                dialog.dismiss()
            }
            // val description = dialogView.findViewById<TextView>(R.id.tvDescription)

            // Set your selectedItem data here
            // title.text = selectedItem.optString("title")
            // description.text = selectedItem.optString("description")

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

        viewmodel.farmerSpecificAdvisoryResponse.observe(viewLifecycleOwner){ response->
            if (response!=null){
                val jsonObject = JSONObject(response.toString())
                val advisoriesArray = jsonObject.getJSONArray("advisories")
            }
        }
        viewmodel.farmerSpecificAdvisory(requireContext())

        return view
    }

    private fun submitSelectedItem(item: AdvisoryModel) {

        // API submission here

        Log.d("Advisory", "Title: ${item.title}")
        Log.d("Advisory", "Date: ${item.date}")
        Log.d("Advisory", "Description: ${item.description}")
        Log.d("Advisory", "Body: ${item.body}")
    }
}

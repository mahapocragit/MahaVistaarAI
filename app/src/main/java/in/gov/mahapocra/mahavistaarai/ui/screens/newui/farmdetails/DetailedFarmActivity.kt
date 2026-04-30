package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDetailedFarmBinding
import `in`.gov.mahapocra.mahavistaarai.util.helpers.AppHelper
import org.json.JSONObject

class DetailedFarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailedFarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailedFarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init() {

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = "Farm lands & crops"
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            startActivity(Intent(this@DetailedFarmActivity, FarmDetailsActivity::class.java))
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@DetailedFarmActivity, FarmDetailsActivity::class.java))
            }
        })

        val farmData = intent.getStringExtra("FARM_DETAIL_DATA")
        if (farmData != null) {
            val jsonObject = JSONObject(farmData)
            val ownerName = jsonObject.optString("owner_name")
            val surveyNumber = jsonObject.optString("survey_no")
            val villageName = jsonObject.optString("village_name")
            val totalArea = jsonObject.optDouble("total_plot_area")
            binding.nameTextView.text = buildString {
                append("Name")
                append(" $ownerName")
            }
            binding.surveyNumberTextView.text = buildString {
                append("Survey/LPM/Compartment No.")
                append(" $surveyNumber")
            }
            binding.totalAreaTextView.text = buildString {
                append(totalArea)
                append(" acre")
            }
            binding.villageNameTextView.text = villageName
        }
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityExpertsCornerFarmerBinding
import org.json.JSONArray

class ExpertsCornerFarmerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpertsCornerFarmerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExpertsCornerFarmerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = "Experts Corner"

        val jsonString = """
    [
        {"title": "What are the safest pesticides to use for farming", "description": "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum"},
        {"title": "What are the safest pesticides to use for farming", "description": "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum"},
        {"title": "What are the safest pesticides to use for farming", "description": "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum"},
        {"title": "What are the safest pesticides to use for farming", "description": "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum"},
        {"title": "What are the safest pesticides to use for farming", "description": "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum"},
        {"title": "What are the safest pesticides to use for farming", "description": "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum"}
    ]
"""
        val jsonArray = JSONArray(jsonString)
        val adapter = ExpertsCornerAdminAdapter(jsonArray)
        binding.expertsCornerFarmerRecycler.layoutManager = LinearLayoutManager(this)
        binding.expertsCornerFarmerRecycler.adapter = adapter
    }
}
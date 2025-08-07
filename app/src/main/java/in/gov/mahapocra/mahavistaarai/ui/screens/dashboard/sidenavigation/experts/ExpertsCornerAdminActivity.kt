package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.experts

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityExpertsCornerAdminBinding
import org.json.JSONArray

class ExpertsCornerAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpertsCornerAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExpertsCornerAdminBinding.inflate(layoutInflater)
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

        binding.expertsCornerAdminRecycler.layoutManager = LinearLayoutManager(this)
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
        binding.expertsCornerAdminRecycler.adapter = adapter

        binding.addPostToggleButton.apply {
            background =
                ContextCompat.getDrawable(this@ExpertsCornerAdminActivity, R.drawable.shape_right_green)
            setTextColor(Color.WHITE)
        }
        binding.myPostToggleButton.apply {
            background =
                ContextCompat.getDrawable(this@ExpertsCornerAdminActivity, R.drawable.shape_left_white)
            setTextColor(Color.BLACK)
        }
        binding.addPostToggleButton.setOnClickListener { toggleView(true) }
        binding.myPostToggleButton.setOnClickListener { toggleView(false) }
    }

    private fun toggleView(showAddView: Boolean) {
        if (showAddView) {
            binding.addPostLayout.visibility = View.VISIBLE
            binding.myPostLayout.visibility = View.GONE
            binding.addPostToggleButton.apply {
                background =
                    ContextCompat.getDrawable(this@ExpertsCornerAdminActivity, R.drawable.shape_right_green)
                setTextColor(Color.WHITE)
            }
            binding.myPostToggleButton.apply {
                background =
                    ContextCompat.getDrawable(this@ExpertsCornerAdminActivity, R.drawable.shape_left_white)
                setTextColor(Color.BLACK)
            }
        } else {
            binding.addPostLayout.visibility = View.GONE
            binding.myPostLayout.visibility = View.VISIBLE
            binding.addPostToggleButton.apply {
                background =
                    ContextCompat.getDrawable(this@ExpertsCornerAdminActivity, R.drawable.shape_right)
                setTextColor(Color.BLACK)
            }
            binding.myPostToggleButton.apply {
                background = ContextCompat.getDrawable(this@ExpertsCornerAdminActivity, R.drawable.shape_left)
                setTextColor(Color.WHITE)
            }
        }
    }
}
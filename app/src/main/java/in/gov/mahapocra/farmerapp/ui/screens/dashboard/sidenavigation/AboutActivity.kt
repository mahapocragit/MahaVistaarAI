package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.sidenavigation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.farmerapp.R
import `in`.gov.mahapocra.farmerapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.imgBackArrow.visibility = View.VISIBLE
        binding.toolbarLayout.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbarLayout.textViewHeaderTitle.text = getString(R.string.about)
    }
}
package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityAboutBinding

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

        binding.textView13.text =
            "Welcome to the MahaVISTAAR-AI App, your trusted companion in modern farming at your fingertips, from the Department of Agriculture, Government of Maharashtra.\n" +
                    "Farmer App is the one-stop solution for farmers which is focused on empowering farmers by providing essential agricultural information, real-time weather updates, market prices, pest identification and control tips, agromet advisories, warehouse and CHC information, expert advice to help farmers make informed decisions and many more.  By integrating technology with climate-resilient farming practices, we aim to promote sustainable and profitable agriculture. From monitoring your crops to connecting with fellow farmers, we ensure you’re never alone on your farming journey. Let’s grow together!"
    }
}
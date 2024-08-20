package `in`.gov.mahapocra.farmerapppks.activity

import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.fragments.CropAdvisoryFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CropCurrentAdvisoryAndFeedback : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_advisory_detaits_and_feedback)

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, CropAdvisoryFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
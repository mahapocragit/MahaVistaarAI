package `in`.gov.mahapocra.mahavistaarai.chms

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityRandomObservationDashboardCaBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.authentication.LoginScreen
import `in`.gov.mahapocra.mahavistaarai.util.AppPreferenceManager

class RandomObservationDashboardCA : AppCompatActivity() {
    private lateinit var binding: ActivityRandomObservationDashboardCaBinding
    private lateinit var appPreferenceManager: AppPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomObservationDashboardCaBinding.inflate(
            layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
            getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener(View.OnClickListener { v: View? -> onBackPressed() })
        appPreferenceManager = AppPreferenceManager(this)
        setUpListeners()
    }
    private fun setUpListeners() {
        binding.btnChmsCrop.setOnClickListener {
            val intent = Intent(this, CropImageRandomForCA::class.java)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                finish() // back arrow click
                true
            }

            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { dialog, _ ->

                appPreferenceManager.clearAll()
                startActivity(
                    Intent(
                        this@RandomObservationDashboardCA,
                        LoginScreen::class.java
                    )
                )
                dialog.dismiss()
                // TODO: Clear session / preferences here
                // AppSettings.getInstance().clear(this)

                // Navigate to Login screen
                // startActivity(Intent(this, LoginActivity::class.java))
                // finishAffinity()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}
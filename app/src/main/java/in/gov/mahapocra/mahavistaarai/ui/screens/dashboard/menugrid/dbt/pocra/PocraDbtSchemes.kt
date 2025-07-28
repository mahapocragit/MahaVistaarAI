package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.pocra

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDbtSchemesBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.DashboardScreen
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.DbtSchemesViewModel
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.uiResponsive
import `in`.gov.mahapocra.mahavistaarai.util.ProgressHelper
import org.json.JSONObject

class PocraDbtSchemes : AppCompatActivity(), OnMultiRecyclerItemClickListener {

    private val dbtSchemesViewModel: DbtSchemesViewModel by viewModels()
    private lateinit var binding: ActivityDbtSchemesBinding
    var languageToLoad: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad =
            if (AppSettings.getLanguage(this@PocraDbtSchemes).equals("2", ignoreCase = true)) {
                "mr"
            } else {
                "en"
            }
        switchLanguage(this, languageToLoad)
        binding = ActivityDbtSchemesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiResponsive(binding.root)

        dbtSchemesLists()
        ProgressHelper.showProgressDialog(this)
        dbtSchemesViewModel.getDBTSchemes(this)
        binding.relativeLayoutTopBar.imageMenushow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.textViewHeaderTitle.setText(R.string.dbtschema)
        binding.relativeLayoutTopBar.imageMenushow.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        binding.applyForPocraTextView.setOnClickListener {
            Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.farmerCardTV.setOnClickListener {
            openRecyclerView(binding.farmerRecyclerView)
        }

        binding.ibcImageView.setOnClickListener {
            openRecyclerView(binding.farmerRecyclerView)
        }

        binding.fpoCardTV.setOnClickListener {
            openRecyclerView(binding.fpoRecyclerView)
        }

        binding.acImageView.setOnClickListener {
            openRecyclerView(binding.fpoRecyclerView)
        }

        binding.nrmCardTV.setOnClickListener {
            openRecyclerView(binding.nrmRecyclerView)
        }

        binding.sawcpImageView.setOnClickListener {
            openRecyclerView(binding.nrmRecyclerView)
        }
    }

    private fun openRecyclerView(recyclerView: RecyclerView) {
        if (recyclerView.visibility == View.VISIBLE) {
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun dbtSchemesLists() {
        dbtSchemesViewModel.responseUrlDbtSchemes.observe(this) {
            ProgressHelper.disableProgressDialog()
            if (it != null) {
                val jSONObject = JSONObject(it.toString())
                val farmerDataJSONArray = jSONObject.optJSONArray("farmerData")
                val fpoDataJSONArray = jSONObject.optJSONArray("fpoData")
                val nrmDataJSONArray = jSONObject.optJSONArray("nrmData")

                binding.farmerRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.fpoRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.nrmRecyclerView.layoutManager = LinearLayoutManager(this)

                binding.farmerRecyclerView.adapter =
                    FarmerDBTRecyclerAdapter(farmerDataJSONArray, languageToLoad, this)
                binding.fpoRecyclerView.adapter =
                    FarmerDBTRecyclerAdapter(fpoDataJSONArray, languageToLoad, this)
                binding.nrmRecyclerView.adapter =
                    NRMrDBTRecyclerAdapter(nrmDataJSONArray, languageToLoad)
            }
        }

        dbtSchemesViewModel.error.observe(this) {
            ProgressHelper.disableProgressDialog()
        }
    }

    override fun onMultiRecyclerViewItemClick(i: Int, obj: Any?) {
        val jsonObject = obj as JSONObject
        startActivity(Intent(this@PocraDbtSchemes, PocraSchemesDetailsActivity::class.java).apply {
            putExtra("FARMERDBTRESPONSE", jsonObject.toString())
        })
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}
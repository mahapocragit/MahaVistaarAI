package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import `in`.co.appinventor.services_api.app_util.AppConstants
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.graph_ql.GraphQLApi
import `in`.gov.mahapocra.mahavistaarai.graph_ql.GraphQLRequest
import `in`.gov.mahapocra.mahavistaarai.graph_ql.GraphQLResponse
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityDbtactivityBinding
import `in`.gov.mahapocra.mahavistaarai.graph_ql.AuthViewModel
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.mahadbt.MahaDbtSchemesActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt.pocra.PocraDbtSchemes
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DBTActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDbtactivityBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@DBTActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityDbtactivityBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setContentView(binding.root)

        binding.toolbar.textViewHeaderTitle.text = getString(R.string.dbtschema)
        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.pocraDBTCardView.setOnClickListener {
            startActivity(Intent(this, PocraDbtSchemes::class.java).apply {
                putExtra("dbtFromDashboard", "pocraDBTCardView")
            })
        }

        binding.mahaDBTCardView.setOnClickListener {
            startActivity(Intent(this, MahaDbtSchemesActivity::class.java).apply {
                putExtra("dbtFromDashboard", "mahaDBTCardView")
            })
        }

        viewModel.graphQLResponse.observe(this) { graphQlResponse ->
            val token = graphQlResponse?.data?.generateAccessToken?.token
            val newRefreshToken = graphQlResponse?.data?.generateAccessToken?.refreshToken
            token?.let { Log.d("RefreshToken 1", it) }
            newRefreshToken?.let { Log.d("RefreshToken 2", it) }
        }
        viewModel.error.observe(this) {
            it?.let { msg -> Log.e("GraphQL", msg) }
        }
        viewModel.fetchAccessToken()
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
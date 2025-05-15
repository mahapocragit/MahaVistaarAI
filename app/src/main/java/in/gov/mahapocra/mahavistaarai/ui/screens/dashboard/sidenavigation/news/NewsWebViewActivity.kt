package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.news

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityNewsWebViewBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage

class NewsWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsWebViewBinding
    private lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@NewsWebViewActivity).equals("1", ignoreCase = true)) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding =  ActivityNewsWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLayoutTopBar.textViewHeaderTitle.text = getString(R.string.news)
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val newsUrlToLoad = intent.getStringExtra("newsUrlToLoad").toString()
        setUpWebView(newsUrlToLoad)
    }

    private fun setUpWebView(urlToLoad:String) {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(urlToLoad)
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
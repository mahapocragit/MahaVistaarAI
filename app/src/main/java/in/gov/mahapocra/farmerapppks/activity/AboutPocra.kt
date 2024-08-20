package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AboutPocra : AppCompatActivity() {

    lateinit var textViewHeaderTitle: TextView
    lateinit var imageBackArrow: ImageView
    lateinit var aboutPocraText1: TextView
    lateinit var aboutPocraText2: TextView
    lateinit var languageToLoad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "mr"
        if (AppSettings.getLanguage(this@AboutPocra).equals("1", ignoreCase = true)) {
            Log.d("getStrName=", AppSettings.getLanguage(this@AboutPocra))
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_about_pocra)
        init()
        onClick()
        imageBackArrow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.about_pocra)
    }
    private fun init()
    {
        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imageBackArrow=findViewById(R.id.imgBackArrow);
        aboutPocraText1=findViewById(R.id.aboutPocraText1);
        aboutPocraText2=findViewById(R.id.aboutPocraText2);

    }
    private fun onClick()
    {
        aboutPocraText1.setOnClickListener(View.OnClickListener {
//            Toast.makeText(this, "Comming Soon...", Toast.LENGTH_SHORT)
//                .show();
            val intent = Intent(this, PocraVillages::class.java)
            startActivity(intent)
        })
        aboutPocraText2.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, R.string.comming_Soon, Toast.LENGTH_SHORT)
                .show();
        })
        imageBackArrow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        })
    }
}
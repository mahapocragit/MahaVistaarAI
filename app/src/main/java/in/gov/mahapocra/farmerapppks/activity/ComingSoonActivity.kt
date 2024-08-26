package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class ComingSoonActivity : AppCompatActivity() {
    private lateinit var imageBackArrow: ImageView
    var languageToLoad: String? = null
    var nameTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppSettings.getLanguage(this).equals("2", ignoreCase = true)) {
            languageToLoad = "mr"
        }else{
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_comming_soon)
        init()
        val strNotification = intent.getStringExtra("notification")
        Log.d("Flag=", "notification=$strNotification");
        if(strNotification.equals("mayu"))
        {
            nameTextView?.setText(R.string.NotificationMsg)
        }
        onClick()
    }
    private fun init()
    {
        imageBackArrow=findViewById(R.id.imgBackArrow)
        nameTextView=findViewById(R.id.textNotificationMsg)
    }
    private fun onClick()
    {
        imageBackArrow.setOnClickListener {
            finish()
        }
    }
}
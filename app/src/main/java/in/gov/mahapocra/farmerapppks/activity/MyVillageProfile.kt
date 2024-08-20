package `in`.gov.mahapocra.farmerapppks.activity

import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.farmerapppks.R
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyVillageProfile : AppCompatActivity() {

    lateinit var vcmrcText: TextView
    lateinit var nrmText: TextView
    lateinit var fpoText: TextView
    lateinit var mapText: TextView
    lateinit var waterBalText: TextView
    lateinit var textViewHeaderTitle: TextView
    lateinit var imageViewCall: ImageView
    lateinit var imgBackArrow: ImageView
    var languageToLoad: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageToLoad = "hi"
        if (AppSettings.getLanguage(this@MyVillageProfile).equals("1", ignoreCase = true))
        {
            languageToLoad = "en"
        }
        setContentView(R.layout.activity_my_village_profile)
        init()
        onClick()
        imageViewCall.setVisibility(View.VISIBLE);
        imgBackArrow.setVisibility(View.VISIBLE);
        textViewHeaderTitle.setText(R.string.village_profile)
    }
    private fun init()
    {

        vcmrcText=findViewById(R.id.text1);
        nrmText=findViewById(R.id.text2);
        fpoText=findViewById(R.id.text3);
        mapText=findViewById(R.id.text4);
        waterBalText=findViewById(R.id.text5);

        textViewHeaderTitle=findViewById(R.id.textViewHeaderTitle);
        imgBackArrow=findViewById(R.id.imgBackArrow);
        imageViewCall=findViewById(R.id.imageViewCall);
    }

   private fun onClick()
   {
       vcmrcText.setOnClickListener(View.OnClickListener {
           Toast.makeText(this, "Clicked VCMRC", Toast.LENGTH_SHORT)
               .show();
       })
       nrmText.setOnClickListener(View.OnClickListener {
           Toast.makeText(this, "Clicked NRM", Toast.LENGTH_SHORT)
               .show();
       })
       imgBackArrow.setOnClickListener(View.OnClickListener {
           finish()
       })
   }
}
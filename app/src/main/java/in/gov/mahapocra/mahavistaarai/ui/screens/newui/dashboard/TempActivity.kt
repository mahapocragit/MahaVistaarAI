package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mahanidan.vcccore.common.i18n.NativeUiConfig
import com.mahanidan.vcccore.my_farms.view.MyFarmsUi
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG

class TempActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uiContext = NativeUiConfig.wrapContext(this)
        val surveyNumber = intent.getStringExtra("survey_number")
        val censusCode = intent.getIntExtra("census_code", 0)
        Log.d(TAG, "onCreate: census code: $censusCode and survey: $surveyNumber")
        val featureView = MyFarmsUi.createView(uiContext, surveyNo = surveyNumber, villageCode = censusCode.toString())
        setContentView(featureView)
    }
}
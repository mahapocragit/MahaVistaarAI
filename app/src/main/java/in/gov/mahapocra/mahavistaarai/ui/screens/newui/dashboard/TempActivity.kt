package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mahanidan.vcccore.common.i18n.NativeUiConfig
import com.mahanidan.vcccore.my_farms.view.MyFarmsUi
import `in`.gov.mahapocra.mahavistaarai.R

class TempActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uiContext = NativeUiConfig.wrapContext(this)
        val featureView = MyFarmsUi.createView(uiContext)
        setContentView(featureView)
        ViewCompat.setOnApplyWindowInsetsListener(featureView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
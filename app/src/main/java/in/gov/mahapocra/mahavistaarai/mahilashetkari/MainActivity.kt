package `in`.gov.mahapocra.mahavistaarai.mahilashetkari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import `in`.gov.mahapocra.mahavistaarai.application.MyApplication
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.navigation.MsNavGraph
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.theme.MahilaShetkariTheme
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.LanguagePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MyApplication
        val languagePreference = LanguagePreference(applicationContext)

        setContent {
            MahilaShetkariTheme {
                MsNavGraph(
                    repository = app.container.repository,
                    languagePreference = languagePreference
                )
            }
        }
    }
}

package `in`.gov.mahapocra.mahavistaarai.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocalCustom {
    fun configureLocale(baseContext: Context, languageToLoad:String){
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
    }
}
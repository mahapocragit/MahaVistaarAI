package `in`.gov.mahapocra.mahavistaarai.util

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ScoreBubbleHelper {

    fun showSnackbar(view: View, message:String){
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }
}
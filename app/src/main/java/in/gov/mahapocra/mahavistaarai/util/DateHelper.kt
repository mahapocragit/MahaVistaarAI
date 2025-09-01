package `in`.gov.mahapocra.mahavistaarai.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateHelper {
    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }
}
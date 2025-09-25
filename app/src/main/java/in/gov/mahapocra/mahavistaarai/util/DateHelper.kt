package `in`.gov.mahapocra.mahavistaarai.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateHelper {
    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }

    fun getTodayDate(format: String = "dd/MM/yyyy"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date())
    }

    fun convertDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

            val date: Date = inputFormat.parse(inputDate)!!
            outputFormat.format(date)
        } catch (e: Exception) {
            "" // return empty string if parsing fails
        }
    }

    fun convertDateFormat(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val date: Date = inputFormat.parse(inputDate)!!
            outputFormat.format(date)
        } catch (e: Exception) {
            "" // fallback if parsing fails
        }
    }
}
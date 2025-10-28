package `in`.gov.mahapocra.mahavistaarai.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import `in`.co.appinventor.services_api.listener.DatePickerRequestListener
import `in`.gov.mahapocra.mahavistaarai.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    fun showDisabledFutureDatePicker(
        context: Context,
        date: Date?,
        requestCode: Int,
        callbackListener: DatePickerRequestListener
    ) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val maxDate = calendar.apply { add(Calendar.DAY_OF_MONTH, 15) }.timeInMillis

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.before(maxDate)) // ✅ Disable dates after +15 days
            // .setStart(today) // (Optional) uncomment to disable past dates
            // .setValidator(CompositeDateValidator.allOf(listOf(DateValidatorPointForward.from(today), DateValidatorPointBackward.before(maxDate))))
            .build()

        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(context.getString(R.string.select_sowing_date))
            .setSelection(today)
            .setTheme(R.style.CustomMaterialDatePickerTheme) // 🎨 Apply custom theme
            .setCalendarConstraints(constraintsBuilder)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR) // 👈 Removes preview pane, compact look

        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedCalendar = Calendar.getInstance().apply { timeInMillis = selection }
            val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)
            val month = selectedCalendar.get(Calendar.MONTH) + 1
            val year = selectedCalendar.get(Calendar.YEAR)
            callbackListener.onDateSelected(requestCode, day, month, year)
        }

        datePicker.show((context as AppCompatActivity).supportFragmentManager, "DATE_PICKER")
    }
}
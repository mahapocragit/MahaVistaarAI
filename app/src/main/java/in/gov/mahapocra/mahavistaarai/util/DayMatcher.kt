package `in`.gov.mahapocra.mahavistaarai.util

import java.util.Calendar

object DayMatcher {

    private val marathiDayMap = mapOf(
        "रविवार" to Calendar.SUNDAY,
        "सोमवार" to Calendar.MONDAY,
        "मंगळवार" to Calendar.TUESDAY,
        "बुधवार" to Calendar.WEDNESDAY,
        "गुरुवार" to Calendar.THURSDAY,
        "शुक्रवार" to Calendar.FRIDAY,
        "शनिवार" to Calendar.SATURDAY
    )

    fun isTodayMatchingMarathiDay(marathiDay: String): Boolean {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val targetDay = marathiDayMap[marathiDay.trim()]
        return targetDay != null && today == targetDay
    }
}
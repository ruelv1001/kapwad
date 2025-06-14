package kapwad.reader.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * Calculates Age based on date given
 * Sample date given: 08/24/2023 (month/day/year)
 * PH Date
 */
fun String.calculateAge(): Int {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy")
    val currentDate = Date()
    val birthDateObject = dateFormat.parse(this)
    val currentCalendar = Calendar.getInstance()
    val birthCalendar = Calendar.getInstance()

    currentCalendar.time = currentDate
    birthCalendar.time = birthDateObject

    var age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

    if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return age
}

/**
 * Remove commas from string
 */
fun String.removeCommas(): String {
    return this.replace(",", "")
}

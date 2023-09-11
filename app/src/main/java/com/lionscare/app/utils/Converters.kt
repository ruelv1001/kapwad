package com.lionscare.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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

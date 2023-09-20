package com.lionscare.app.utils

import android.content.Context
import android.widget.Toast
import com.lionscare.app.R
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.StringTokenizer

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

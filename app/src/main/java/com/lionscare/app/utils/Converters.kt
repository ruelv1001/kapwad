package com.lionscare.app.utils

import android.content.Context
import android.widget.Toast
import com.lionscare.app.R
import java.text.DecimalFormat
import java.text.ParseException
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
 * Converts currencies or points into proer format with decimals
 * and comma separator
 */
fun formatCurrency(text: String): String {
    val decimalFormat = DecimalFormat("#,##0.00")
    try {
        val amount = decimalFormat.parse(text)?.toDouble() ?: 0.0
        return decimalFormat.format(amount)
    } catch (e: ParseException) {
       throw e
    }
}

/**
 * Remove commas from string
 */
fun String.removeCommas(): String {
    return this.replace(",", "")
}

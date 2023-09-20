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
 *  Function to format a number with thousand separators
 *  https://stackoverflow.com/questions/25823579/edittext-and-textview-formatted-with-thousands-separators-in-android
 */
fun getDecimalFormat(value: String): String {
    val lst = StringTokenizer(value, ".")
    var str1 = value
    var str2 = ""
    if (lst.countTokens() > 1) {
        str1 = lst.nextToken()
        str2 = lst.nextToken()
    }
    var str3 = ""
    var i = 0
    var j = str1.length - 1
    if (str1[j] == '.') {
        j--
        str3 = "."
    }
    for (k in j downTo 0) {
        if (i == 3) {
            str3 = ",$str3"
            i = 0
        }
        str3 = str1[k] + str3
        i++
    }
    if (str2.isNotEmpty()) {
        str3 = "$str3.$str2"
    }
    return str3
}

/**
 * Remove commas from string
 */
fun String.removeCommas(): String {
    return this.replace(",", "")
}

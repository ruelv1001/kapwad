package com.lionscare.app.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

fun String.capitalizeWords(): String = split(" ").map { it.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
} }.joinToString(" ")

fun String.maskedEmail(): String {
    val regex = """(?:\G(?!^)|(?<=^[^@]{2}|@))[^@](?!\.[^.]+$)""".toRegex()
    return replace(regex, "*")
}

fun removeLastSubstring(input: String, substringToRemove: String): String {
    if (input.endsWith(substringToRemove)) {
        return input.removeSuffix(substringToRemove)
    }
    return input
}

fun isPhoneNumberValid(phoneNumber: String, countryIso: String): Boolean {
    val phoneNumberUtil = PhoneNumberUtil.getInstance()
    return try {
        val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, countryIso)
        phoneNumberUtil.isValidNumber(parsedPhoneNumber)
    } catch (e: Exception) {
        false
    }
}

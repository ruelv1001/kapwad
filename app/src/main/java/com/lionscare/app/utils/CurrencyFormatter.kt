package com.lionscare.app.utils

import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText
import java.text.DecimalFormat

fun EditText.setAmountFormat(){
    this.setOnFocusChangeListener { view, _ ->
        applyDecimalInputFilter(this)
        val input = this.text.toString().replace(",","")
        if(!view.isFocused){
            if(input.isNotEmpty())
                this.setText(currencyFormat(input))
        }
    }
}


fun currencyFormat(amount: String): String? {
    if(amount.replace(",","").toDouble()==0.0){
        return "0.00"
    }
    val formatter = DecimalFormat("###,###,###,###,###.00")
    return formatter.format(amount.toBigDecimal())
}


fun applyDecimalInputFilter(editText: EditText) {
    val decimalDigits = 2

    val inputFilter = object : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val newText = StringBuilder(dest.toString())
            newText.replace(dstart, dend, source?.subSequence(start, end).toString())

            val decimalSeparator = '.'
            val decimalIndex = newText.indexOf(decimalSeparator)
            if (decimalIndex != -1 && newText.length - decimalIndex > decimalDigits + 1) {
                return ""
            }

            return null
        }
    }

    editText.filters = arrayOf(inputFilter)
}
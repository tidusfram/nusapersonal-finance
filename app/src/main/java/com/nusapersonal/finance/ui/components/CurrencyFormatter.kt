package com.nusapersonal.finance.ui.components

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    private val localeId = Locale("id", "ID")
    private val rupiahFormat = NumberFormat.getCurrencyInstance(localeId).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    fun formatRupiah(amount: Long): String {
        return rupiahFormat.format(amount)
    }

    fun formatNumber(number: Long): String {
        return NumberFormat.getNumberInstance(localeId).format(number)
    }

    fun formatDouble(number: Double): String {
        return NumberFormat.getNumberInstance(localeId).format(number)
    }
}

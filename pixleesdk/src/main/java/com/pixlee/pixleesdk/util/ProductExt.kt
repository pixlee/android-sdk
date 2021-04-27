package com.pixlee.pixleesdk.util

import android.util.Log
import com.pixlee.pixleesdk.data.PXLProduct
import java.math.BigDecimal

fun PXLProduct.getCurrencySymbol(defaultCurrency: String?): String? {
    val extendedCurrency: ExtendedCurrency? = ExtendedCurrency
            .getCurrencyByISO(currency ?: defaultCurrency ?: "")
    return extendedCurrency?.symbol ?: currency
}

fun BigDecimal.getFractionalPart(): String {
    val num = this
    val fractionalPart = (num - num.setScale(0, BigDecimal.ROUND_FLOOR)).toPlainString()
    val decimalIndex = fractionalPart.indexOf(".")
    return if (-1 < decimalIndex && decimalIndex < fractionalPart.length) {
        fractionalPart.substring(decimalIndex, fractionalPart.length)
    } else {
        ""
    }
}

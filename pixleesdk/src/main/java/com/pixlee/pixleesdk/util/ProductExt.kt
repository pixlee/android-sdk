package com.pixlee.pixleesdk.util

import com.pixlee.pixleesdk.data.PXLProduct

fun PXLProduct.getCurrencySymbol(defaultCurrency: String?): String? {
    val extendedCurrency: ExtendedCurrency? = ExtendedCurrency.getCurrencyByISO(currency?: defaultCurrency)
    return extendedCurrency?.symbol ?: currency
}
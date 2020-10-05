package com.pixlee.pixleesdk.ui.widgets

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import com.pixlee.pixleesdk.util.px

/**
 * This lets the app customize TextView with text:String
 */
open class TextViewStyle(var text: String = "") : TextStyle()

/**
 * this is for a view with price and currency
 */
open class CurrencyTextStyle(var defaultCurrency: String? = null,
                             var leftText: TextStyle = TextStyle(),
                             var rightText: TextStyle = TextStyle())

/**
 * This lets the app customize TextView
 */
open class TextStyle(var size: Float = 18.px,
                     var sizeUnit: Int = TypedValue.COMPLEX_UNIT_PX,
                     @ColorInt var color: Int = Color.WHITE,
                     var typeface: Typeface? = null)


/**
 * This is to change TextView's UI using TextViewStyle passed by the SDK user
 */
fun TextView.setTextViewStyle(style: TextViewStyle) {
    text = style.text
    setTextStyle(style)
}

/**
 * This is to change TextView's UI using TextStyle passed by the SDK user
 */
fun TextView.setTextStyle(style: TextStyle) {
    setTextSize(style.sizeUnit, style.size)
    setTextColor(style.color)
    style.typeface?.also {
        typeface = it
    }
}
package com.pixlee.pixleesdk.ui.widgets

import android.graphics.Color
import android.graphics.Typeface
import android.os.Parcelable
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import com.pixlee.pixleesdk.util.px
import kotlinx.android.parcel.Parcelize

/**
 * This lets the app customize TextView with text:String
 */
@Parcelize
open class TextViewStyle(var text: String = "", var textPadding:TextPadding = TextPadding(bottom = 14.px.toInt())) : TextStyle(), Parcelable

@Parcelize
open class TextPadding(var left: Int = 0, var top: Int = 0, var right: Int = 0, var bottom: Int = 0): Parcelable

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
                     @ColorInt var color: Int = Color.BLACK,
                     var typeface: Typeface? = null)


/**
 * This is to change TextView's UI using TextViewStyle passed by the SDK user
 */
fun TextView.setTextViewStyle(style: TextViewStyle) {
    text = style.text
    style.textPadding.also {
        setPadding(it.left, it.top, it.right, it.bottom)
    }

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
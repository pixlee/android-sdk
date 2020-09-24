package com.pixlee.pixleesdk.ui.widgets

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import com.pixlee.pixleesdk.util.px

/**
 * This lets the app customize TextView with text:String
 */
open class TextViewStyle(var text: String = "") : TextStyle()

/**
 * This lets the app customize TextView
 */
open class TextStyle(var size: Float = 18.px,
                     var sizeUnit: Int = TypedValue.COMPLEX_UNIT_PX,
                     var typeface: Typeface? = null)

/**
 * This is to change TextView's UI using TextViewStyle passed by the SDK user
 */
fun TextView.setTextViewStyle(style: TextViewStyle) {
    text = style.text
    setTextSize(style.sizeUnit, style.size)
    typeface = style.typeface
}

/**
 * This is to change TextView's UI using TextStyle passed by the SDK user
 */
fun TextView.setTextStyle(style: TextStyle) {
    setTextSize(style.sizeUnit, style.size)
    typeface = style.typeface
}
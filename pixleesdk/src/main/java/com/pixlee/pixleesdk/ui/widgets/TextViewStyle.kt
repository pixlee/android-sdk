package com.pixlee.pixleesdk.ui.widgets

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 9/21/20.
 */
open class TextViewStyle(var text: String = "") : TextStyle()

open class TextStyle(var size: Float = 18.px,
                     var sizeUnit: Int = TypedValue.COMPLEX_UNIT_PX,
                     var typeface: Typeface? = null)


fun TextView.setTextViewStyle(style: TextViewStyle) {
    text = style.text
    setTextSize(style.sizeUnit, style.size)
    typeface = style.typeface
}

fun TextView.setTextStyle(style: TextStyle) {

    setTextSize(style.sizeUnit, style.size)
    typeface = style.typeface
}